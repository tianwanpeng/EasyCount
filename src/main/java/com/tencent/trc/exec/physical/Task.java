package com.tencent.trc.exec.physical;

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

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.io.TaskContext;
import com.tencent.trc.exec.logical.Operator;
import com.tencent.trc.exec.logical.Operator1TS;
import com.tencent.trc.exec.logical.Operator7FS;
import com.tencent.trc.exec.logical.OperatorFactory;
import com.tencent.trc.plan.logical.OpDesc;
import com.tencent.trc.plan.physical.TaskWork;
import com.tencent.trc.udfnew.MyUDFUtils;
import com.tencent.trc.util.config.ApplicationOnlineConfig;
import com.tencent.trc.util.config.ApplicationOnlineConfig.CmdParam;
import com.tencent.trc.util.config.ApplicationOnlineConfig.CmdProcessor;
import com.tencent.trc.util.graph.GraphWalker;
import com.tencent.trc.util.graph.GraphWalker.Dispatcher;
import com.tencent.trc.util.graph.GraphWalker.Node;
import com.tencent.trc.util.graph.GraphWalker.WalkMode;
import com.tencent.trc.util.status.StatusPrintable;
import com.tencent.trc.util.status.StatusPrinter;

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

	public Task(TaskWork taskWork, Properties properties) {
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

	public String addChild(Task task) {
		this.taskId2ChildTask.put(task.taskWork.getTaskId(), task);
		String streamId = genStreamId(this, task);
		this.outStreamId2Task.put(streamId, task);
		this.outTask2StreamId.put(task, streamId);
		return streamId;
	}

	public String addParent(Task task) {
		this.taskId2ParentTask.put(task.taskWork.getTaskId(), task);
		String streamId = genStreamId(task, this);
		this.inStreamId2Task.put(streamId, task);
		this.inTask2StreamId.put(task, streamId);
		return streamId;
	}

	private String genStreamId(Task task, Task task2) {
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
		for (Operator<? extends OpDesc> op : allSrcOps.values()) {
			// log.info(getPrintPrifix() + "printTaskStatus from "
			// + op.opPrintStr());
			op.printStatus(printId.get());
		}
		dataGenerator.printStatus(printId.get());
		dataFinalizer.printStatus(printId.get());
		tupleProcessor.printStatus(printId.get());
		printStatus(printId.get());
		printId.incrementAndGet();
	}

	protected int topologyTaskId = 0;

	String printPrefixStr() {
		return getName() + "#" + topologyTaskId + "#" + getTaskId();
	}

	protected String getPrintPrifix() {
		return printPrefixStr() + " -> ";
	}

	public void intialize(String execId) throws Exception {

		MyUDFUtils.initialize();
		printId = new AtomicInteger(0);
		hconf = new TrcConfiguration(properties);

		taskContext.taskId = getTaskId();
		taskContext.execId = execId;

		int SPOUT_MSG_QUEUE_SIZE = 100000;
		tupleProcessor = new TupleProcessor(SPOUT_MSG_QUEUE_SIZE);

		generateOnlineConfig();

		generateSysPrinter();

		final ArrayList<OpDesc> tsOpDescs = new ArrayList<OpDesc>();
		final ArrayList<OpDesc> fsOpDescs = new ArrayList<OpDesc>();
		final ArrayList<OpDesc> srcOpDescs = new ArrayList<OpDesc>();
		final ArrayList<OpDesc> sinkOpDescs = new ArrayList<OpDesc>();

		final HashMap<Node, Operator<? extends OpDesc>> opDescToOperators = generateOpDescToOpsAndGetSrcOps(
				tsOpDescs, fsOpDescs, srcOpDescs, sinkOpDescs);

		/**
		 * generate src op and connect to root ops
		 */
		generateInnerSrcOps(srcOpDescs, tsOpDescs, opDescToOperators);

		/**
		 * connect all the ops
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

		for (Operator<? extends OpDesc> op : allSrcOps.values()) {
			if (op instanceof Operator1TS) {
				op.initialize(hconf, taskContext,
						new ObjectInspector[] { dataGenerator.getTagKey2OIs()
								.get(op.getOpDesc().getTaskId_OpTagIdx()) });
			} else {
				op.initialize(hconf, taskContext, null);
			}
		}

		dataGenerator.start();

		tupleProcessor.start();
	}

	private void generateOnlineConfig() {
		openOnlineConfig = hconf.getBoolean("ec.online.config.open", false);

		if (openOnlineConfig) {
			String zkips = hconf.get("ec.online.config.zkips");
			String zkroot = hconf.get("ec.online.config.zkroot");
			onlineConfig = new ApplicationOnlineConfig(zkips, zkroot,
					new CmdProcessor() {
						@Override
						public void process(String cmd, CmdParam cmdParam) {
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
		sysPrinter = new StatusPrinter(new StatusPrinter.Printer() {
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
		 * generate the operators of the curr task
		 */
		final HashMap<Node, Operator<? extends OpDesc>> opDescToOperators = new GraphWalker<Operator<? extends OpDesc>>(
				new Dispatcher<Operator<? extends OpDesc>>() {

					@Override
					public Operator<? extends OpDesc> dispatch(Node nd,
							Stack<Node> stack,
							ArrayList<Operator<? extends OpDesc>> nodeOutputs,
							HashMap<Node, Operator<? extends OpDesc>> retMap) {
						OpDesc opDesc = (OpDesc) nd;
						// if this opDesc not belong to curr task, just return
						// null
						if (!taskWork.getOpDescs().contains(opDesc)) {
							return null;
						}

						if ("TS".equals(opDesc.getName())) {
							tsOpDescs.add(opDesc);
						} else if (taskWork.getRootOpDescs().contains(opDesc)) {
							srcOpDescs.add(opDesc);
						}

						if ("FS".equals(opDesc.getName())) {
							fsOpDescs.add(opDesc);
						} else if (taskWork.getDestOpDescs().contains(opDesc)) {
							sinkOpDescs.add(opDesc);
						}
						Operator<? extends OpDesc> op = OperatorFactory.getOperator(
								opDesc, hconf, taskContext);
						op.setPrintPrefix(printPrefixStr());
						return op;
					}

					@Override
					public boolean needToDispatchChildren(Node nd,
							Stack<Node> stack,
							ArrayList<Operator<? extends OpDesc>> nodeOutputs,
							HashMap<Node, Operator<? extends OpDesc>> retMap) {
						return true;
					}
				}, WalkMode.CHILD_FIRST).walk(taskWork.getRootOpDescsNodes());

		return opDescToOperators;
	}

	private void connectAllOps(
			final HashMap<Node, Operator<? extends OpDesc>> opDescToOperators)
			throws Exception {
		new GraphWalker<Operator<? extends OpDesc>>(
				new Dispatcher<Operator<? extends OpDesc>>() {
					@Override
					public Operator<? extends OpDesc> dispatch(Node nd,
							Stack<Node> stack,
							ArrayList<Operator<? extends OpDesc>> nodeOutputs,
							HashMap<Node, Operator<? extends OpDesc>> retMap) {
						OpDesc opDesc = (OpDesc) nd;
						// if this opDesc not belong to curr task, just return
						// null
						if (!taskWork.getOpDescs().contains(opDesc)) {
							return null;
						}

						Operator<? extends OpDesc> op = opDescToOperators.get(opDesc);

						for (OpDesc childOpDesc : opDesc.childrenOps()) {
							Operator<? extends OpDesc> operator = retMap
									.get(childOpDesc);
							if (operator != null) {
								op.addChild(operator);
							} else {
								// if childOp is null that means the child is
								// not belong to the curr task so link to sinkOp
								op.addChild(generateSinkOp(hconf, opDesc,
										childOpDesc));
							}
						}

						return op;
					}

					@Override
					public boolean needToDispatchChildren(Node nd,
							Stack<Node> stack,
							ArrayList<Operator<? extends OpDesc>> nodeOutputs,
							HashMap<Node, Operator<? extends OpDesc>> retMap) {
						return true;
					}
				}, WalkMode.CHILD_FIRST).walk(taskWork.getRootOpDescsNodes());

	}

	private void generateAndStartFinalizer(ArrayList<OpDesc> fsOpDescs,
			HashMap<Node, Operator<? extends OpDesc>> opDescToOperators) {
		final ArrayList<Operator<? extends OpDesc>> fsOps = new ArrayList<Operator<? extends OpDesc>>();
		for (OpDesc opDesc : fsOpDescs) {
			fsOps.add(opDescToOperators.get(opDesc));
		}

		dataFinalizer = new Data2Finalizer(hconf, getTaskId(), getExecId());
		for (Operator<? extends OpDesc> fsop : fsOps) {
			dataFinalizer.addFsOp((Operator7FS) fsop);
		}
		dataFinalizer.start();
	}

	private void generateInnerSrcOps(ArrayList<OpDesc> srcOpDescs,
			ArrayList<OpDesc> tsOpDescs,
			HashMap<Node, Operator<? extends OpDesc>> opDescToOperators) {

		allSrcOps = new HashMap<String, Operator<? extends OpDesc>>();

		for (OpDesc sdesc : srcOpDescs) {
			for (OpDesc pdesc : sdesc.parentOps()) {
				if (sdesc.getTaskId() != pdesc.getTaskId()) {
					String srcKey = pdesc.getTaskId() + "-"
							+ pdesc.getOpTagIdx();
					if (!allSrcOps.containsKey(srcKey)) {
						allSrcOps.put(
								srcKey,
								new InnerOp1Source(hconf, taskContext, srcKey,
										sdesc.getTaskId(),
										sdesc.getOpTagIdx() + 1000, pdesc
												.getOutputType()));
					}
					allSrcOps.get(srcKey)
							.addChild(opDescToOperators.get(sdesc));
					allSrcOps.get(srcKey).getOpDesc()
							.setTaskId(sdesc.getTaskId());
				}
			}
		}
		HashMap<String, Operator<? extends OpDesc>> tsSrcOps = new HashMap<String, Operator<? extends OpDesc>>();
		for (OpDesc opDesc : tsOpDescs) {
			tsSrcOps.put(opDesc.getTaskId_OpTagIdx(),
					opDescToOperators.get(opDesc));
		}
		allSrcOps.putAll(tsSrcOps);

	}

	private void generateDataGenerator(ArrayList<OpDesc> tsOpDescs,
			HashMap<Node, Operator<? extends OpDesc>> opDescToOperators,
			String execId) {
		dataGenerator = new Data1Generator(hconf, tsOpDescs, getName(),
				getTaskId(), execId, tupleProcessor);
	}

	abstract protected InnerOp2Sink generateSinkOp(TrcConfiguration hconf,
			OpDesc opDesc, OpDesc childOpDesc);

	@Override
	public List<? extends Node> getChildren() {
		ArrayList<Node> res = new ArrayList<Node>();
		for (Task t : taskId2ChildTask.values()) {
			res.add(t);
		}
		return res;
	}

	public String getTaskId() {
		return String.valueOf(this.taskWork.getTaskId());
	}

	abstract protected String getExecId();

	public HashMap<String, Task> getOutStreamIds2Task() {
		return outStreamId2Task;
	}

	public HashMap<Task, String> getOutTask2StreamId() {
		return outTask2StreamId;
	}

	public HashMap<String, Task> getInStreamId2Task() {
		return inStreamId2Task;
	}

	public HashMap<Task, String> getInTask2StreamId() {
		return inTask2StreamId;
	}

	public void close() {
		dataGenerator.close();
		for (Operator<? extends OpDesc> sop : allSrcOps.values()) {
			sop.close(true);
		}
	}

	public void restart() {
		dataGenerator.restart();
	}

	protected static class SrcObject {
		final private String tagKey;
		final private Object obj;
		final public SOCallBack socb;

		public SrcObject(String tagKey, Object obj, SOCallBack socb) {
			this.tagKey = tagKey;
			this.obj = obj;
			this.socb = socb;
		}

		@Override
		public String toString() {
			return tagKey + ":" + obj.toString();
		}

		public void finish() {
			if (socb != null) {
				socb.finish();
			}
		}

		public void await() throws InterruptedException {
			if (socb != null) {
				socb.await();
			}
		}
	}

	public static interface SOCallBack {
		public void finish();

		public void await() throws InterruptedException;
	}

	protected class TupleProcessor extends Thread implements StatusPrintable {

		@Override
		public void printStatus(int printId) {
			log.info(getPrintPrifix() + "TupleProcessorObjQueue:"
					+ objQueue.size());
		}

		final private LinkedBlockingQueue<SrcObject> objQueue;

		public TupleProcessor(int queueNum) {
			super("TupleProcessor-" + printPrefixStr());
			this.objQueue = new LinkedBlockingQueue<Task.SrcObject>(100000);
		}

		public boolean offer(SrcObject so) {
			try {
				return objQueue.offer(so, 1, TimeUnit.SECONDS);
			} catch (Throwable e) {
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
					if (System.currentTimeMillis() - lastnoptime > 1000) {
						// send nop
						for (Operator<? extends OpDesc> iop : allSrcOps
								.values()) {
							iop.process(null, 0, true);
						}
						lastnoptime = System.currentTimeMillis();
						if (System.currentTimeMillis() - lastheartbeattime > 100000) {
							log.info(getPrintPrifix() + "processNop heartbeat");
							lastheartbeattime = System.currentTimeMillis();
						}
					}
					SrcObject so = objQueue.poll(1, TimeUnit.SECONDS);
					if (so != null) {
						try {
							allSrcOps.get(so.tagKey).process(so.obj, 0, false);
						} catch (Throwable e) {
							log.error(getPrintPrifix()
									+ TDBankUtils.getExceptionStack(e));
						}
						so.finish();
					}
				} catch (Throwable e) {
					log.error(getPrintPrifix()
							+ TDBankUtils.getExceptionStack(e));
				}
			}
		}
	}
}
