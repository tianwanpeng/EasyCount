package com.tencent.easycount.exec.physical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.conf.TrcConfiguration;
import com.tencent.easycount.exec.io.TaskContext;
import com.tencent.easycount.exec.logical.Operator;
import com.tencent.easycount.exec.logical.Operator1TS;
import com.tencent.easycount.exec.logical.Operator7FS;
import com.tencent.easycount.exec.logical.OperatorFactory;
import com.tencent.easycount.plan.logical.OpDesc;
import com.tencent.easycount.plan.physical.TaskWork;
import com.tencent.easycount.udfnew.MyUDFUtils;
import com.tencent.easycount.util.config.ApplicationOnlineConfig;
import com.tencent.easycount.util.config.ApplicationOnlineConfig.CmdParam;
import com.tencent.easycount.util.config.ApplicationOnlineConfig.CmdProcessor;
import com.tencent.easycount.util.graph.GraphWalker;
import com.tencent.easycount.util.graph.GraphWalker.Dispatcher;
import com.tencent.easycount.util.graph.GraphWalker.Node;
import com.tencent.easycount.util.graph.GraphWalker.WalkMode;
import com.tencent.easycount.util.status.StatusPrintable;
import com.tencent.easycount.util.status.StatusPrinter;
import com.tencent.easycount.util.status.TDBankUtils;

public abstract class Task implements Node, Serializable, StatusPrintable {
	private static final long serialVersionUID = -4546577419925310380L;
	private static Logger log = LoggerFactory.getLogger(Task.class);

	final TaskWork taskWork;
	protected TrcConfiguration hconf;
	private final Properties properties = new Properties();

	final HashMap<Integer, Task> taskId2ParentTask;
	final private HashMap<String, Task> inStreamId2Task;
	final private HashMap<Task, String> inTask2StreamId;

	final HashMap<Integer, Task> taskId2ChildTask;
	final private HashMap<String, Task> outStreamId2Task;
	final private HashMap<Task, String> outTask2StreamId;

	transient protected StatusPrinter sysPrinter;

	protected TaskContext taskContext;

	public Task(final TaskWork taskWork, final Properties properties) {
		this.taskWork = taskWork;
		this.properties.putAll(properties);

		this.taskId2ParentTask = new HashMap<Integer, Task>();
		this.inStreamId2Task = new HashMap<String, Task>();
		this.inTask2StreamId = new HashMap<Task, String>();

		this.taskId2ChildTask = new HashMap<Integer, Task>();
		this.outStreamId2Task = new HashMap<String, Task>();
		this.outTask2StreamId = new HashMap<Task, String>();

		this.taskContext = new TaskContext();
	}

	public String addChild(final Task task) {
		this.taskId2ChildTask.put(task.taskWork.getTaskId(), task);
		final String streamId = genStreamId(this, task);
		this.outStreamId2Task.put(streamId, task);
		this.outTask2StreamId.put(task, streamId);
		return streamId;
	}

	public String addParent(final Task task) {
		this.taskId2ParentTask.put(task.taskWork.getTaskId(), task);
		final String streamId = genStreamId(task, this);
		this.inStreamId2Task.put(streamId, task);
		this.inTask2StreamId.put(task, streamId);
		return streamId;
	}

	private String genStreamId(final Task task, final Task task2) {
		return task.getTaskId() + "-" + task2.getTaskId();
	}

	protected final AtomicLong incremsgid = new AtomicLong(Long.MAX_VALUE / 4);

	// data source
	transient private Data1Generator dataGenerator;

	// data sink
	transient private Data2Finalizer dataFinalizer;

	// key is taskId+"-"+opTagIdx
	// transient protected HashMap<String, InnerOp1Source> innerSrcOps = null;
	transient protected HashMap<String, Operator<? extends OpDesc>> allSrcOps = null;
	// transient protected ArrayList<Operator1TS> tsOps = null;

	transient AtomicInteger printId = null;
	transient ApplicationOnlineConfig onlineConfig = null;
	transient protected boolean openOnlineConfig = false;
	transient TupleProcessor tupleProcessor;

	protected void printTaskStatus() {
		for (final Operator<? extends OpDesc> op : this.allSrcOps.values()) {
			// log.info(getPrintPrifix() + "printTaskStatus from "
			// + op.opPrintStr());
			op.printStatus(this.printId.get());
		}
		this.dataGenerator.printStatus(this.printId.get());
		this.dataFinalizer.printStatus(this.printId.get());
		this.tupleProcessor.printStatus(this.printId.get());
		printStatus(this.printId.get());
		this.printId.incrementAndGet();
	}

	protected int topologyTaskId = 0;

	String printPrefixStr() {
		return getName() + "#" + this.topologyTaskId + "#" + getTaskId();
	}

	protected String getPrintPrifix() {
		return printPrefixStr() + " -> ";
	}

	public void intialize(final String execId) throws Exception {

		MyUDFUtils.initialize();
		this.printId = new AtomicInteger(0);
		this.hconf = new TrcConfiguration(this.properties);

		this.taskContext.taskId = getTaskId();
		this.taskContext.execId = execId;

		final int SPOUT_MSG_QUEUE_SIZE = 100000;
		this.tupleProcessor = new TupleProcessor(SPOUT_MSG_QUEUE_SIZE);

		generateOnlineConfig();

		generateSysPrinter();

		// 包含当前task需要读表的tsop
		final ArrayList<OpDesc> tsOpDescs = new ArrayList<OpDesc>();

		// 包含当前task输出表的op
		final ArrayList<OpDesc> fsOpDescs = new ArrayList<OpDesc>();

		// 包含当前task来自父task-op的子节点op
		final ArrayList<OpDesc> srcOpDescs = new ArrayList<OpDesc>();

		// 包含当前task所有需要流入到子task的op节点
		final ArrayList<OpDesc> sinkOpDescs = new ArrayList<OpDesc>();

		// 填充上面的op列表
		final HashMap<Node, Operator<? extends OpDesc>> opDescToOperators = generateOpDescToOpsAndGetSrcOps(
				tsOpDescs, fsOpDescs, srcOpDescs, sinkOpDescs);

		/**
		 * generate src op and connect to root ops
		 */
		generateInnerSrcOps(srcOpDescs, tsOpDescs, opDescToOperators);

		/**
		 * connect all the ops
		 *
		 * task之间进行连接的基本原理：
		 *
		 * 1、每个task的所有destop，针对所有不在本task内的子节点，添加到一个sinkop
		 *
		 * 2、每个task的所有srcop，添加一个srcop父节点
		 *
		 * 3、以上的过程相当于两个相连接的父子节点如果不在一个task内部，则在中间添加两个op， 分别是父task的sink和子task的src
		 *
		 * 4、实际运行过程中，每个sinkop会有一个唯一的srckey，每个srcop根据对应的srck接收数据并进行处理
		 *
		 */
		connectAllOps(opDescToOperators);

		/**
		 * set finalizer
		 */
		generateAndStartFinalizer(fsOpDescs, opDescToOperators);

		/**
		 * data source
		 */
		generateDataGenerator(tsOpDescs, opDescToOperators, execId);

		for (final Operator<? extends OpDesc> op : this.allSrcOps.values()) {
			if (op instanceof Operator1TS) {
				op.initialize(
						this.hconf,
						this.taskContext,
						new ObjectInspector[] { this.dataGenerator
								.getTagKey2OIs().get(
										op.getOpDesc().getTaskId_OpTagIdx()) });
			} else {
				op.initialize(this.hconf, this.taskContext, null);
			}
		}

		this.dataGenerator.start();

		this.tupleProcessor.start();
	}

	private void generateOnlineConfig() {
		this.openOnlineConfig = this.hconf.getBoolean("ec.online.config.open",
				false);

		if (this.openOnlineConfig) {
			final String zkips = this.hconf.get("ec.online.config.zkips");
			final String zkroot = this.hconf.get("ec.online.config.zkroot");
			this.onlineConfig = new ApplicationOnlineConfig(zkips, zkroot,
					new CmdProcessor() {
						@Override
						public void process(final String cmd,
						final CmdParam cmdParam) {
							if ("stop".equalsIgnoreCase(cmd)) {
								log.info(getPrintPrifix()
										+ "begin to stop the task : "
										+ getName() + " : " + getTaskId());
								Task.this.close();
								log.info(getPrintPrifix() + "task stopped : "
										+ getName() + " : " + getTaskId());
							}
						}
					});
		}
	}

	private void generateSysPrinter() {
		this.sysPrinter = new StatusPrinter(new StatusPrinter.Printer() {
			@Override
			public void printStatus() {
				printTaskStatus();
			}
		}, 10 * 1000, "SysPrinter-" + printPrefixStr());
	}

	private HashMap<Node, Operator<? extends OpDesc>> generateOpDescToOpsAndGetSrcOps(
			final ArrayList<OpDesc> tsOpDescs,
			final ArrayList<OpDesc> fsOpDescs,
			final ArrayList<OpDesc> srcOpDescs,
			final ArrayList<OpDesc> sinkOpDescs) throws Exception {
		/**
		 * generate the operators of the curr task 同时填充四类opdesc
		 */
		final HashMap<Node, Operator<? extends OpDesc>> opDescToOperators = new GraphWalker<Operator<? extends OpDesc>>(
				new Dispatcher<Operator<? extends OpDesc>>() {

					@Override
					public Operator<? extends OpDesc> dispatch(
							final Node nd,
							final Stack<Node> stack,
							final ArrayList<Operator<? extends OpDesc>> nodeOutputs,
							final HashMap<Node, Operator<? extends OpDesc>> retMap) {
						final OpDesc opDesc = (OpDesc) nd;
						// if this opDesc not belong to curr task, just return
						// null
						if (!Task.this.taskWork.getOpDescs().contains(opDesc)) {
							return null;
						}

						if ("TS".equals(opDesc.getName())) {
							tsOpDescs.add(opDesc);
						} else if (Task.this.taskWork.getRootOpDescs()
								.contains(opDesc)) {
							srcOpDescs.add(opDesc);
						}

						if ("FS".equals(opDesc.getName())) {
							fsOpDescs.add(opDesc);
						} else if (Task.this.taskWork.getDestOpDescs()
								.contains(opDesc)) {
							sinkOpDescs.add(opDesc);
						}
						final Operator<? extends OpDesc> op = OperatorFactory
								.getOperator(opDesc, Task.this.hconf,
										Task.this.taskContext);
						op.setPrintPrefix(printPrefixStr());
						return op;
					}

					@Override
					public boolean needToDispatchChildren(
							final Node nd,
							final Stack<Node> stack,
							final ArrayList<Operator<? extends OpDesc>> nodeOutputs,
							final HashMap<Node, Operator<? extends OpDesc>> retMap) {
						return true;
					}
				}, WalkMode.CHILD_FIRST).walk(this.taskWork
						.getRootOpDescsNodes());

		return opDescToOperators;
	}

	private void connectAllOps(
			final HashMap<Node, Operator<? extends OpDesc>> opDescToOperators)
			throws Exception {
		new GraphWalker<Operator<? extends OpDesc>>(
				new Dispatcher<Operator<? extends OpDesc>>() {
					@Override
					public Operator<? extends OpDesc> dispatch(
							final Node nd,
							final Stack<Node> stack,
							final ArrayList<Operator<? extends OpDesc>> nodeOutputs,
							final HashMap<Node, Operator<? extends OpDesc>> retMap) {
						final OpDesc opDesc = (OpDesc) nd;
						// if this opDesc not belong to curr task, just return
						// null
						if (!Task.this.taskWork.getOpDescs().contains(opDesc)) {
							return null;
						}

						final Operator<? extends OpDesc> op = opDescToOperators
								.get(opDesc);

						for (final OpDesc childOpDesc : opDesc.childrenOps()) {
							final Operator<? extends OpDesc> operator = retMap
									.get(childOpDesc);
							if (operator != null) {
								op.addChild(operator);
							} else {
								// if childOp is null that means the child is
								// not belong to the curr task so link to sinkOp
								op.addChild(generateSinkOp(Task.this.hconf,
										opDesc, childOpDesc));
							}
						}

						return op;
					}

					@Override
					public boolean needToDispatchChildren(
							final Node nd,
							final Stack<Node> stack,
							final ArrayList<Operator<? extends OpDesc>> nodeOutputs,
							final HashMap<Node, Operator<? extends OpDesc>> retMap) {
						return true;
					}
				}, WalkMode.CHILD_FIRST).walk(this.taskWork
						.getRootOpDescsNodes());

	}

	private void generateAndStartFinalizer(final ArrayList<OpDesc> fsOpDescs,
			final HashMap<Node, Operator<? extends OpDesc>> opDescToOperators) {
		final ArrayList<Operator<? extends OpDesc>> fsOps = new ArrayList<Operator<? extends OpDesc>>();
		for (final OpDesc opDesc : fsOpDescs) {
			fsOps.add(opDescToOperators.get(opDesc));
		}

		this.dataFinalizer = new Data2Finalizer(this.hconf, getTaskId(),
				getExecId());
		for (final Operator<? extends OpDesc> fsop : fsOps) {
			this.dataFinalizer.addFsOp((Operator7FS) fsop);
		}
		this.dataFinalizer.start();
	}

	private void generateInnerSrcOps(final ArrayList<OpDesc> srcOpDescs,
			final ArrayList<OpDesc> tsOpDescs,
			final HashMap<Node, Operator<? extends OpDesc>> opDescToOperators) {

		this.allSrcOps = new HashMap<String, Operator<? extends OpDesc>>();

		for (final OpDesc sdesc : srcOpDescs) {
			for (final OpDesc pdesc : sdesc.parentOps()) {
				if (sdesc.getTaskId() != pdesc.getTaskId()) {
					final String srcKey = pdesc.getTaskId() + "-"
							+ pdesc.getOpTagIdx();
					if (!this.allSrcOps.containsKey(srcKey)) {
						this.allSrcOps.put(
								srcKey,
								new InnerOp1Source(this.hconf,
										this.taskContext, srcKey, sdesc
										.getTaskId(), sdesc
										.getOpTagIdx() + 1000, pdesc
												.getOutputType()));
					}
					this.allSrcOps.get(srcKey).addChild(
							opDescToOperators.get(sdesc));
					this.allSrcOps.get(srcKey).getOpDesc()
							.setTaskId(sdesc.getTaskId());
				}
			}
		}
		final HashMap<String, Operator<? extends OpDesc>> tsSrcOps = new HashMap<String, Operator<? extends OpDesc>>();
		for (final OpDesc opDesc : tsOpDescs) {
			tsSrcOps.put(opDesc.getTaskId_OpTagIdx(),
					opDescToOperators.get(opDesc));
		}
		this.allSrcOps.putAll(tsSrcOps);

	}

	private void generateDataGenerator(final ArrayList<OpDesc> tsOpDescs,
			final HashMap<Node, Operator<? extends OpDesc>> opDescToOperators,
			final String execId) {
		this.dataGenerator = new Data1Generator(this.hconf, tsOpDescs,
				getName(), getTaskId(), execId, this.tupleProcessor);
	}

	abstract protected InnerOp2Sink generateSinkOp(TrcConfiguration hconf,
			OpDesc opDesc, OpDesc childOpDesc);

	@Override
	public List<? extends Node> getChildren() {
		final ArrayList<Node> res = new ArrayList<Node>();
		for (final Task t : this.taskId2ChildTask.values()) {
			res.add(t);
		}
		return res;
	}

	public String getTaskId() {
		return String.valueOf(this.taskWork.getTaskId());
	}

	abstract protected String getExecId();

	public HashMap<String, Task> getOutStreamIds2Task() {
		return this.outStreamId2Task;
	}

	public HashMap<Task, String> getOutTask2StreamId() {
		return this.outTask2StreamId;
	}

	public HashMap<String, Task> getInStreamId2Task() {
		return this.inStreamId2Task;
	}

	public HashMap<Task, String> getInTask2StreamId() {
		return this.inTask2StreamId;
	}

	public void close() {
		this.dataGenerator.close();
		for (final Operator<? extends OpDesc> sop : this.allSrcOps.values()) {
			sop.close(true);
		}
	}

	public void restart() {
		this.dataGenerator.restart();
	}

	protected static class SrcObject {
		final private String tagKey;
		final private Object obj;
		final public SOCallBack socb;

		public SrcObject(final String tagKey, final Object obj,
				final SOCallBack socb) {
			this.tagKey = tagKey;
			this.obj = obj;
			this.socb = socb;
		}

		@Override
		public String toString() {
			return this.tagKey + ":" + this.obj.toString();
		}

		public void finish() {
			if (this.socb != null) {
				this.socb.finish();
			}
		}

		public void await() throws InterruptedException {
			if (this.socb != null) {
				this.socb.await();
			}
		}
	}

	public static interface SOCallBack {
		public void finish();

		public void await() throws InterruptedException;
	}

	protected class TupleProcessor extends Thread implements StatusPrintable {

		@Override
		public void printStatus(final int printId) {
			log.info(getPrintPrifix() + "TupleProcessorObjQueue:"
					+ this.objQueue.size());
		}

		final private LinkedBlockingQueue<SrcObject> objQueue;

		public TupleProcessor(final int queueNum) {
			super("TupleProcessor-" + printPrefixStr());
			this.objQueue = new LinkedBlockingQueue<Task.SrcObject>(100000);
		}

		public boolean offer(final SrcObject so) {
			try {
				return this.objQueue.offer(so, 1, TimeUnit.SECONDS);
			} catch (final Throwable e) {
				log.error(getPrintPrifix() + TDBankUtils.getExceptionStack(e));
			}
			return false;
		}

		@Override
		public synchronized void start() {
			super.start();
		}

		@Override
		public void run() {
			long lastnoptime = System.currentTimeMillis();
			long lastheartbeattime = System.currentTimeMillis();
			while (true) {
				try {
					if ((System.currentTimeMillis() - lastnoptime) > 1000) {
						// send nop
						for (final Operator<? extends OpDesc> iop : Task.this.allSrcOps
								.values()) {
							iop.process(null, 0, true);
						}
						lastnoptime = System.currentTimeMillis();
						if ((System.currentTimeMillis() - lastheartbeattime) > 100000) {
							log.info(getPrintPrifix() + "processNop heartbeat");
							lastheartbeattime = System.currentTimeMillis();
						}
					}
					final SrcObject so = this.objQueue
							.poll(1, TimeUnit.SECONDS);
					if (so != null) {
						try {
							Task.this.allSrcOps.get(so.tagKey).process(so.obj,
									0, false);
						} catch (final Throwable e) {
							log.error(getPrintPrifix()
									+ TDBankUtils.getExceptionStack(e));
						}
						so.finish();
					}
				} catch (final Throwable e) {
					log.error(getPrintPrifix()
							+ TDBankUtils.getExceptionStack(e));
				}
			}
		}
	}
}
