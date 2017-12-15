package com.tencent.easycount.exec.logical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluator;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.conf.ECConfiguration;
import com.tencent.easycount.exec.io.TaskContext;
import com.tencent.easycount.mon.MonKeys;
import com.tencent.easycount.mon.MonStatusUpdater;
import com.tencent.easycount.mon.MonStatusUtils;
import com.tencent.easycount.plan.logical.OpDesc;
import com.tencent.easycount.util.graph.GraphWalker;
import com.tencent.easycount.util.status.StatusPrintable;

public abstract class Operator<T extends OpDesc> implements GraphWalker.Node,
StatusPrintable {
	private static Logger log = LoggerFactory.getLogger(Operator.class);

	final private T opDesc;

	public T getOpDesc() {
		return this.opDesc;
	}

	private boolean monStatus = false;
	private MonStatusUpdater updater = null;
	private String opMsgComputeKey = null;

	protected ObjectInspector[] inputObjInspectors = new ObjectInspector[1];
	protected ObjectInspector outputObjInspector;

	final protected List<Operator<? extends OpDesc>> childOperators;
	final protected List<Operator<? extends OpDesc>> parentOperators;
	protected String operatorId;

	protected Operator<? extends OpDesc>[] childOperatorsArray = null;
	protected int[] childOperatorsTag;

	protected ECConfiguration configuration;

	public static enum State {
		UNINIT, // initialize() has not been called
		INIT, // initialize() has been called and close() has not been called,
		// or close() has been called but one of its parent is not closed.
		CLOSE
		// all its parents operators are in state CLOSE and called close()
		// to children. Note: close() being called and its state being CLOSE is
		// difference since close() could be called but state is not CLOSE if
		// one of its parent is not in state CLOSE..
	};

	protected transient State state = State.UNINIT;

	transient AtomicInteger priPrintId = null;
	transient protected String printPrefixStr = null;

	public void setPrintPrefix(final String printPrefixStr) {
		this.printPrefixStr = printPrefixStr;
	}

	protected String getPrintPrefix() {
		return this.printPrefixStr + "-" + opPrintStr() + " -> ";
	}

	public String opPrintStr() {
		return this.opDesc.getOpTagIdx() + "-" + getName();
	}

	final private AtomicLong rNum;
	final private AtomicLong sNum;

	@Override
	public void printStatus(final int printId) {
		if (printId == this.priPrintId.get()) {
			return;
		}
		this.priPrintId.set(printId);
		int avgtime = 0;
		int maxtime = 0;
		for (int i = 0; i < 10; i++) {
			avgtime += this.times[i];
			maxtime = Math.max(maxtime, this.times[i]);
		}
		avgtime /= 10;
		log.info(getPrintPrefix() + "r&s_Num:" + this.rNum.get() + "-"
				+ this.sNum.get() + "-" + avgtime + "-" + maxtime);
		printInternal(printId);
		for (final Operator<? extends OpDesc> op : this.childOperators) {
			op.printStatus(printId);
		}
	}

	public abstract void printInternal(int printId);

	public Operator(final T opDesc, final ECConfiguration hconf,
			final TaskContext taskContext) {
		this.opDesc = opDesc;
		this.parentOperators = new ArrayList<Operator<? extends OpDesc>>();
		this.childOperators = new ArrayList<Operator<? extends OpDesc>>();
		this.rNum = new AtomicLong(0);
		this.sNum = new AtomicLong(0);
	}

	public void initialize(final ECConfiguration hconf,
			final TaskContext taskContext, final ObjectInspector[] inputOIs) {
		this.configuration = hconf;
		if (!areAllParentsInitialized()) {
			return;
		}

		if (hconf.getBoolean("moniter.send.status", true)) {
			this.monStatus = true;
			this.opMsgComputeKey = new StringBuffer()
			.append(MonKeys.TOPOLOGY + "=" + hconf.get("topologyname"))
			.append("&" + MonKeys.EXECID + "=" + taskContext.execId)
			.append("&" + MonKeys.TASKID + "=" + taskContext.taskId)
			.append("&" + MonKeys.OPTAGID + "="
							+ this.opDesc.getOpTagIdx())
			.append("&" + MonKeys.OPNAME + "=" + this.opDesc.getName())
			.append("&" + MonKeys.MONTYPE + "=100").toString();
			this.updater = MonStatusUtils.getTubeMonStatusUpdater(hconf);
		}

		this.priPrintId = new AtomicInteger(-1);

		if (inputOIs != null) {
			this.inputObjInspectors = inputOIs;
		}

		if (this.childOperators != null) {
			this.childOperatorsArray = new Operator<?>[this.childOperators
					.size()];

			for (int i = 0; i < this.childOperatorsArray.length; i++) {
				this.childOperatorsArray[i] = this.childOperators.get(i);
			}
			this.childOperatorsTag = new int[this.childOperatorsArray.length];
			for (int i = 0; i < this.childOperatorsArray.length; i++) {
				final List<Operator<? extends OpDesc>> parentOperators = this.childOperatorsArray[i]
						.getParentOperators();
				this.childOperatorsTag[i] = parentOperators.indexOf(this);
			}
		}

		// derived classes can set this to different object if needed
		this.outputObjInspector = this.inputObjInspectors[0];

		initializeOp(hconf, taskContext);
		final String outputstr = "outputOI : " + this.getName() + " : "
				+ this.opDesc.getTaskId_OpTagIdx() + " : "
				+ this.outputObjInspector;
		log.info(getPrintPrefix() + outputstr);
	}

	protected void initializeOp(final ECConfiguration hconf,
			final TaskContext taskContext) {
		initializeChildren(hconf, taskContext);
	}

	protected void initializeChildren(final ECConfiguration hconf,
			final TaskContext taskContext) {
		this.state = State.INIT;
		if (this.childOperators == null) {
			return;
		}
		for (int i = 0; i < this.childOperatorsArray.length; i++) {
			this.childOperatorsArray[i].initialize(hconf, taskContext,
					this.outputObjInspector, this.childOperatorsTag[i]);
		}
	}

	public void initialize(final ECConfiguration hconf,
			final TaskContext taskContext, final ObjectInspector inputOI,
			final int parentId) {
		if (parentId >= this.inputObjInspectors.length) {
			int newLength = this.inputObjInspectors.length * 2;
			while (parentId >= newLength) {
				newLength *= 2;
			}
			// inputObjInspectors = Arrays.copyOf(inputObjInspectors,
			// newLength);
			final ObjectInspector[] inputObjInspectors1 = new ObjectInspector[newLength];
			System.arraycopy(this.inputObjInspectors, 0, inputObjInspectors1,
					0, this.inputObjInspectors.length);
			this.inputObjInspectors = inputObjInspectors1;
		}
		this.inputObjInspectors[parentId] = inputOI;
		// call the actual operator initialization function
		initialize(hconf, taskContext, null);
	}

	int[] times = new int[10];
	int timeidx = 0;

	public void process(final Object row, final int tag, final boolean nop) {
		if (nop) {
			processNop();
			for (int i = 0; i < this.childOperatorsArray.length; i++) {
				final Operator<? extends OpDesc> o = this.childOperatorsArray[i];
				o.process(row, this.childOperatorsTag[i], true);
			}
		} else {
			this.rNum.incrementAndGet();
			final long t = System.currentTimeMillis();
			processOp(row, tag);
			this.times[this.timeidx] = (int) (System.currentTimeMillis() - t);
			this.timeidx = (this.timeidx + 1) % 10;
			if (this.monStatus && (this.updater != null)) {
				try {
					this.updater.update(this.opMsgComputeKey, 1);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void processNop() {
	}

	public abstract void processOp(Object row, int tag);

	protected void forward(final Object row) {
		this.sNum.incrementAndGet();
		if ((this.childOperatorsArray == null)) {
			return;
		}
		for (int i = 0; i < this.childOperatorsArray.length; i++) {
			final Operator<? extends OpDesc> o = this.childOperatorsArray[i];
			o.process(row, this.childOperatorsTag[i], false);
		}
	}

	public void close(final boolean abort) {

		if (this.state == State.CLOSE) {
			return;
		}

		// check if all parents are finished
		if (!allInitializedParentsAreClosed()) {
			return;
		}

		// set state as CLOSE as long as all parents are closed
		// state == CLOSE doesn't mean all children are also in state CLOSE
		this.state = State.CLOSE;

		// call the operator specific close routine
		closeOp(abort);

		if (this.childOperators == null) {
			return;
		}

		for (final Operator<? extends OpDesc> op : this.childOperators) {
			op.close(abort);
		}

	}

	protected void closeOp(final boolean abort) {
	}

	public void reset() {
		this.state = State.INIT;
		if (this.childOperators != null) {
			for (final Operator<? extends OpDesc> o : this.childOperators) {
				o.reset();
			}
		}

	}

	private List<Operator<? extends OpDesc>> getParentOperators() {
		return this.parentOperators;
	}

	protected boolean areAllParentsInitialized() {
		if (this.parentOperators == null) {
			return true;
		}
		for (final Operator<? extends OpDesc> parent : this.parentOperators) {
			if (parent == null) {
				continue;
			}
			if (parent.state != State.INIT) {
				return false;
			}
		}
		return true;
	}

	protected boolean allInitializedParentsAreClosed() {
		if (this.parentOperators != null) {
			for (final Operator<? extends OpDesc> parent : this.parentOperators) {
				if (parent == null) {
					continue;
				}
				if (!((parent.state == State.CLOSE) || (parent.state == State.UNINIT))) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public List<? extends GraphWalker.Node> getChildren() {
		final ArrayList<GraphWalker.Node> res = new ArrayList<GraphWalker.Node>();
		res.addAll(this.childOperators);
		return res;
	}

	@Override
	public String getName() {
		return this.opDesc.getName();
	}

	public void addChild(final Operator<? extends OpDesc> operator) {
		this.childOperators.add(operator);
		operator.addParent(this);
	}

	private void addParent(final Operator<? extends OpDesc> operator) {
		this.parentOperators.add(operator);
	}

	protected static StructObjectInspector initEvaluatorsAndReturnStruct(
			@SuppressWarnings("rawtypes") final ExprNodeEvaluator[] evals,
			final List<String> outputColName, final ObjectInspector rowInspector)
					throws HiveException {
		final ObjectInspector[] fieldObjectInspectors = initEvaluators(evals,
				rowInspector);
		return ObjectInspectorFactory.getStandardStructObjectInspector(
				outputColName, Arrays.asList(fieldObjectInspectors));
	}

	protected static ObjectInspector[] initEvaluators(
			@SuppressWarnings("rawtypes") final ExprNodeEvaluator[] evals,
			final ObjectInspector rowInspector) throws HiveException {
		final ObjectInspector[] result = new ObjectInspector[evals.length];
		for (int i = 0; i < evals.length; i++) {
			result[i] = evals[i].initialize(rowInspector);
		}
		return result;
	}

}
