package com.tencent.trc.exec.logical;

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

import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.io.TaskContext;
import com.tencent.trc.mon.MonKeys;
import com.tencent.trc.mon.MonStatusUpdater;
import com.tencent.trc.mon.MonStatusUtils;
import com.tencent.trc.plan.logical.OpDesc;
import com.tencent.trc.util.graph.GraphWalker;
import com.tencent.trc.util.status.StatusPrintable;

public abstract class Operator<T extends OpDesc> implements GraphWalker.Node,
		StatusPrintable {
	private static Logger log = LoggerFactory.getLogger(Operator.class);

	final private T opDesc;

	public T getOpDesc() {
		return opDesc;
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

	protected TrcConfiguration configuration;

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

	public void setPrintPrefix(String printPrefixStr) {
		this.printPrefixStr = printPrefixStr;
	}

	protected String getPrintPrefix() {
		return printPrefixStr + "-" + opPrintStr() + " -> ";
	}

	public String opPrintStr() {
		return opDesc.getOpTagIdx() + "-" + getName();
	}

	final private AtomicLong rNum;
	final private AtomicLong sNum;

	@Override
	public void printStatus(int printId) {
		if (printId == priPrintId.get()) {
			return;
		}
		priPrintId.set(printId);
		int avgtime = 0;
		int maxtime = 0;
		for (int i = 0; i < 10; i++) {
			avgtime += times[i];
			maxtime = Math.max(maxtime, times[i]);
		}
		avgtime /= 10;
		log.info(getPrintPrefix() + "r&s_Num:" + rNum.get() + "-" + sNum.get()
				+ "-" + avgtime + "-" + maxtime);
		printInternal(printId);
		for (Operator<? extends OpDesc> op : childOperators) {
			op.printStatus(printId);
		}
	}

	public abstract void printInternal(int printId);

	public Operator(T opDesc, TrcConfiguration hconf, TaskContext taskContext) {
		this.opDesc = opDesc;
		this.parentOperators = new ArrayList<Operator<? extends OpDesc>>();
		this.childOperators = new ArrayList<Operator<? extends OpDesc>>();
		this.rNum = new AtomicLong(0);
		this.sNum = new AtomicLong(0);
	}

	public void initialize(TrcConfiguration hconf, TaskContext taskContext,
			ObjectInspector[] inputOIs) {
		this.configuration = hconf;
		if (!areAllParentsInitialized()) {
			return;
		}

		if (hconf.getBoolean("moniter.send.status", true)) {
			monStatus = true;
			opMsgComputeKey = new StringBuffer()
					.append(MonKeys.TOPOLOGY + "=" + hconf.get("topologyname"))
					.append("&" + MonKeys.EXECID + "=" + taskContext.execId)
					.append("&" + MonKeys.TASKID + "=" + taskContext.taskId)
					.append("&" + MonKeys.OPTAGID + "=" + opDesc.getOpTagIdx())
					.append("&" + MonKeys.OPNAME + "=" + opDesc.getName())
					.append("&" + MonKeys.MONTYPE + "=100").toString();
			updater = MonStatusUtils.getTubeMonStatusUpdater(hconf);
		}

		priPrintId = new AtomicInteger(-1);

		if (inputOIs != null) {
			inputObjInspectors = inputOIs;
		}

		if (childOperators != null) {
			childOperatorsArray = new Operator<?>[childOperators.size()];

			for (int i = 0; i < childOperatorsArray.length; i++) {
				childOperatorsArray[i] = childOperators.get(i);
			}
			childOperatorsTag = new int[childOperatorsArray.length];
			for (int i = 0; i < childOperatorsArray.length; i++) {
				List<Operator<? extends OpDesc>> parentOperators = childOperatorsArray[i]
						.getParentOperators();
				childOperatorsTag[i] = parentOperators.indexOf(this);
			}
		}

		// derived classes can set this to different object if needed
		outputObjInspector = inputObjInspectors[0];

		initializeOp(hconf, taskContext);
		String outputstr = "outputOI : " + this.getName() + " : "
				+ opDesc.getTaskId_OpTagIdx() + " : " + outputObjInspector;
		log.info(getPrintPrefix() + outputstr);
	}

	protected void initializeOp(TrcConfiguration hconf, TaskContext taskContext) {
		initializeChildren(hconf, taskContext);
	}

	protected void initializeChildren(TrcConfiguration hconf,
			TaskContext taskContext) {
		state = State.INIT;
		if (childOperators == null) {
			return;
		}
		for (int i = 0; i < childOperatorsArray.length; i++) {
			childOperatorsArray[i].initialize(hconf, taskContext,
					outputObjInspector, childOperatorsTag[i]);
		}
	}

	public void initialize(TrcConfiguration hconf, TaskContext taskContext,
			ObjectInspector inputOI, int parentId) {
		if (parentId >= inputObjInspectors.length) {
			int newLength = inputObjInspectors.length * 2;
			while (parentId >= newLength) {
				newLength *= 2;
			}
			// inputObjInspectors = Arrays.copyOf(inputObjInspectors,
			// newLength);
			ObjectInspector[] inputObjInspectors1 = new ObjectInspector[newLength];
			System.arraycopy(inputObjInspectors, 0, inputObjInspectors1, 0,
					inputObjInspectors.length);
			inputObjInspectors = inputObjInspectors1;
		}
		inputObjInspectors[parentId] = inputOI;
		// call the actual operator initialization function
		initialize(hconf, taskContext, null);
	}

	int[] times = new int[10];
	int timeidx = 0;

	public void process(Object row, int tag, boolean nop) {
		if (nop) {
			processNop();
			for (int i = 0; i < childOperatorsArray.length; i++) {
				Operator<? extends OpDesc> o = childOperatorsArray[i];
				o.process(row, childOperatorsTag[i], true);
			}
		} else {
			rNum.incrementAndGet();
			long t = System.currentTimeMillis();
			processOp(row, tag);
			times[timeidx] = (int) (System.currentTimeMillis() - t);
			timeidx = (timeidx + 1) % 10;
			if (monStatus && updater != null) {
				try {
					updater.update(opMsgComputeKey, 1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void processNop() {
	}

	public abstract void processOp(Object row, int tag);

	protected void forward(Object row) {
		sNum.incrementAndGet();
		if ((childOperatorsArray == null)) {
			return;
		}
		for (int i = 0; i < childOperatorsArray.length; i++) {
			Operator<? extends OpDesc> o = childOperatorsArray[i];
			o.process(row, childOperatorsTag[i], false);
		}
	}

	public void close(boolean abort) {

		if (state == State.CLOSE) {
			return;
		}

		// check if all parents are finished
		if (!allInitializedParentsAreClosed()) {
			return;
		}

		// set state as CLOSE as long as all parents are closed
		// state == CLOSE doesn't mean all children are also in state CLOSE
		state = State.CLOSE;

		// call the operator specific close routine
		closeOp(abort);

		if (childOperators == null) {
			return;
		}

		for (Operator<? extends OpDesc> op : childOperators) {
			op.close(abort);
		}

	}

	protected void closeOp(boolean abort) {
	}

	public void reset() {
		this.state = State.INIT;
		if (childOperators != null) {
			for (Operator<? extends OpDesc> o : childOperators) {
				o.reset();
			}
		}

	}

	private List<Operator<? extends OpDesc>> getParentOperators() {
		return this.parentOperators;
	}

	protected boolean areAllParentsInitialized() {
		if (parentOperators == null) {
			return true;
		}
		for (Operator<? extends OpDesc> parent : parentOperators) {
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
		if (parentOperators != null) {
			for (Operator<? extends OpDesc> parent : parentOperators) {
				if (parent == null) {
					continue;
				}
				if (!(parent.state == State.CLOSE || parent.state == State.UNINIT)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public List<? extends GraphWalker.Node> getChildren() {
		ArrayList<GraphWalker.Node> res = new ArrayList<GraphWalker.Node>();
		res.addAll(childOperators);
		return res;
	}

	@Override
	public String getName() {
		return opDesc.getName();
	}

	public void addChild(Operator<? extends OpDesc> operator) {
		childOperators.add(operator);
		operator.addParent(this);
	}

	private void addParent(Operator<? extends OpDesc> operator) {
		parentOperators.add(operator);
	}

	protected static StructObjectInspector initEvaluatorsAndReturnStruct(
			@SuppressWarnings("rawtypes") ExprNodeEvaluator[] evals,
			List<String> outputColName, ObjectInspector rowInspector)
			throws HiveException {
		ObjectInspector[] fieldObjectInspectors = initEvaluators(evals,
				rowInspector);
		return ObjectInspectorFactory.getStandardStructObjectInspector(
				outputColName, Arrays.asList(fieldObjectInspectors));
	}

	protected static ObjectInspector[] initEvaluators(
			@SuppressWarnings("rawtypes") ExprNodeEvaluator[] evals,
			ObjectInspector rowInspector) throws HiveException {
		ObjectInspector[] result = new ObjectInspector[evals.length];
		for (int i = 0; i < evals.length; i++) {
			result[i] = evals[i].initialize(rowInspector);
		}
		return result;
	}

}
