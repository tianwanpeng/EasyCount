package com.tencent.trc.exec.logical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluator;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.io.Queryable;
import com.tencent.trc.exec.io.QueryableTableUtils;
import com.tencent.trc.exec.io.Queryable.CallBack;
import com.tencent.trc.exec.io.TaskContext;
import com.tencent.trc.metastore.Table;
import com.tencent.trc.plan.ExprNodeEvaluatorFactoryNew;
import com.tencent.trc.plan.logical.OpDesc1TS;
import com.tencent.trc.plan.logical.OpDesc3JOIN;
import com.tencent.trc.util.algr.Permutation;
import com.tencent.trc.util.algr.Permutation.PermutateCallback;

public class Operator3JOIN extends Operator<OpDesc3JOIN> {
	private static Logger log = LoggerFactory.getLogger(Operator3JOIN.class);

	@SuppressWarnings("rawtypes")
	// contains only join dim talbes
	private transient ExprNodeEvaluator[] keyEvaluators;
	private transient PrimitiveObjectInspector[] keyInspectors;

	private transient Queryable[] dimensionTable;
	private transient ObjectInspector[] dimensionTableOIs;
	@SuppressWarnings("rawtypes")
	// table : column
	private transient ExprNodeEvaluator[][] valueEvaluators;
	private transient ObjectInspector streamTableOI;
	private transient ObjectInspector streamTableStandardOI;

	// TODO not support right now
	@SuppressWarnings("rawtypes")
	private transient ArrayList<ExprNodeEvaluator> preConditionEvaluators;
	private transient ArrayList<PrimitiveObjectInspector> preConditionInspectors;
	@SuppressWarnings("rawtypes")
	private transient ArrayList<ArrayList<ExprNodeEvaluator>> postConditionEvaluators;
	private transient ArrayList<ArrayList<PrimitiveObjectInspector>> postConditionInspectors;

	// private transient SynchronousQueue<Object> forwardQueue;
	// private boolean asyncMode = false;
	// if only memtable just use synchronize method
	// private boolean memDimTableOnly = true;
	private AtomicLong emitRowsNum;

	transient private LinkedBlockingQueue<Object> cachedObjQueue = null;
	private Permutation permutation;

	@Override
	public void printInternal(int printId) {
		log.info(getPrintPrefix() + "emitRowsNum:" + emitRowsNum.get()
				+ ",cachedObjQueue:" + cachedObjQueue.size());
		for (int i = 0; i < dimensionTable.length; i++) {
			dimensionTable[i].printStatus(printId);
		}
	}

	public Operator3JOIN(OpDesc3JOIN opDesc, TrcConfiguration hconf,
			TaskContext taskContext) {
		super(opDesc, hconf, taskContext);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void initializeOp(TrcConfiguration hconf, TaskContext taskContext) {

		emitRowsNum = new AtomicLong(0);
		cachedObjQueue = new LinkedBlockingQueue<Object>();

		HashMap<String, Table> alias2JoinTables = getOpDesc()
				.getAlias2JoinTables();
		ArrayList<String> joinAliass = new ArrayList<String>(
				alias2JoinTables.keySet());

		dimensionTable = new Queryable[joinAliass.size()];
		dimensionTableOIs = new ObjectInspector[joinAliass.size()];
		for (int i = 0; i < joinAliass.size(); i++) {
			Table dimtbl = alias2JoinTables.get(joinAliass.get(i));
			// if (!(dimtbl instanceof TableMem)) {
			// memDimTableOnly = false;
			// }
			dimensionTable[i] = QueryableTableUtils.generateQuerable(dimtbl,
					getPrintPrefix(), hconf.getBoolean("localmode", false));
			dimensionTableOIs[i] = dimensionTable[i].getObjectInspector();
		}

		HashMap<String, ArrayList<ExprNodeDesc>> alias2ExprDesc = getOpDesc()
				.getAlias2ExprDesc();

		ArrayList<ObjectInspector> ois = new ArrayList<ObjectInspector>();
		valueEvaluators = new ExprNodeEvaluator[alias2ExprDesc.size()][];

		int streamTableTag = 0;
		for (int i = 0; i < parentOperators.size(); i++) {
			if (!(parentOperators.get(i).getOpDesc() instanceof OpDesc1TS)
					|| !((OpDesc1TS) parentOperators.get(i).getOpDesc())
							.isDimensionTable()) {
				streamTableTag = i;
				continue;
			}
		}
		log.info("streamTableTag : " + streamTableTag);
		streamTableOI = inputObjInspectors[streamTableTag];
		streamTableStandardOI = ObjectInspectorUtils
				.getStandardObjectInspector(streamTableOI);

		String alias1 = getOpDesc().getStreamAlias();
		ArrayList<ExprNodeDesc> exprs1 = alias2ExprDesc.get(alias1);
		valueEvaluators[0] = new ExprNodeEvaluator[exprs1.size()];
		for (int j = 0; j < exprs1.size(); j++) {
			try {
				valueEvaluators[0][j] = ExprNodeEvaluatorFactoryNew.get(exprs1
						.get(j));
				ois.add(valueEvaluators[0][j].initialize(streamTableStandardOI));
			} catch (HiveException e) {
				log.error(getPrintPrefix() + TDBankUtils.getExceptionStack(e));
			}
		}

		for (int i = 0; i < joinAliass.size(); i++) {
			String alias = joinAliass.get(i);
			ArrayList<ExprNodeDesc> exprs = alias2ExprDesc.get(alias);
			valueEvaluators[i + 1] = new ExprNodeEvaluator[exprs.size()];
			for (int j = 0; j < exprs.size(); j++) {
				try {
					valueEvaluators[i + 1][j] = ExprNodeEvaluatorFactoryNew
							.get(exprs.get(j));
					ois.add(valueEvaluators[i + 1][j]
							.initialize(dimensionTableOIs[i]));
				} catch (HiveException e) {
					log.error(getPrintPrefix()
							+ TDBankUtils.getExceptionStack(e));
				}
			}
		}

		ArrayList<String> fieldNames = getOpDesc().getOutputColumnNames();

		outputObjInspector = ObjectInspectorFactory
				.getStandardStructObjectInspector(fieldNames, ois);

		try {
			HashMap<String, ExprNodeDesc> key2exprs = getOpDesc()
					.getAlias2ConditionKeyExprs();
			keyEvaluators = new ExprNodeEvaluator[joinAliass.size()];
			keyInspectors = new PrimitiveObjectInspector[joinAliass.size()];
			for (int i = 0; i < joinAliass.size(); i++) {
				// TODO makesure again
				// keyEvaluators[i] = ExprNodeEvaluatorFactoryNew.get(key2exprs
				// .get(alias2JoinTables.get(joinAliass.get(i))
				// .getTableName()));
				keyEvaluators[i] = ExprNodeEvaluatorFactoryNew.get(key2exprs
						.get(joinAliass.get(i)));
				keyInspectors[i] = (PrimitiveObjectInspector) keyEvaluators[i]
						.initialize(streamTableOI);
			}

			preConditionEvaluators = new ArrayList<ExprNodeEvaluator>();
			preConditionInspectors = new ArrayList<PrimitiveObjectInspector>();
			postConditionEvaluators = new ArrayList<ArrayList<ExprNodeEvaluator>>();
			postConditionInspectors = new ArrayList<ArrayList<PrimitiveObjectInspector>>();
			for (int i = 0; i < joinAliass.size(); i++) {
				postConditionEvaluators.add(new ArrayList<ExprNodeEvaluator>());
				postConditionInspectors
						.add(new ArrayList<PrimitiveObjectInspector>());
			}
		} catch (HiveException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}

		permutation = new Permutation();

		initializeChildren(hconf, taskContext);
	}

	@Override
	public void processOp(final Object row, int tag) {
		if (filter(row, preConditionEvaluators, preConditionInspectors)) {
			final ObjectWrapper objw = new ObjectWrapper(keyEvaluators.length);
			emitRowsNum.incrementAndGet();
			objw.forwardObjectsCache[0] = new Object[1];
			objw.forwardObjectsCache[0][0] = ObjectInspectorUtils
					.copyToStandardObject(row, streamTableOI);

			for (int i = 0; i < keyEvaluators.length; i++) {
				Object key = null;
				try {
					key = ObjectInspectorUtils.copyToStandardJavaObject(
							keyEvaluators[i].evaluate(row), keyInspectors[i]);
				} catch (Throwable e) {
					objw.cdl.countDown();
					log.error(getPrintPrefix()
							+ TDBankUtils.getExceptionStack(e));
					continue;
				}
				final int ii = i;
				final String queryKey = String.valueOf(key);

				CallBack cb = new CallBack() {
					int failnum = 0;
					boolean error = false;

					@Override
					public void callback(Object[] rows) {
						objw.cdl.countDown();
						if (rows != null && rows.length > 0) {
							for (int j = 0; j < rows.length; j++) {
								if (!filter(rows[j],
										postConditionEvaluators.get(ii),
										postConditionInspectors.get(ii))) {
									objw.filter.set(false);
								}
							}
						} else {
							// at least one row, set to null if no row been
							// joined
							rows = new Object[] { null };
						}
						objw.forwardObjectsCache[ii + 1] = rows;
						if (!error && objw.cdl.getCount() == 0
								&& objw.filter.get()) {
							forwardRow(objw);
						}
					}

					@Override
					public void fail() {
						failnum++;
						if (failnum < 100) {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							dimensionTable[ii].get(queryKey, this);
						} else {
							error = true;
							log.warn("query key " + queryKey + " fail for "
									+ failnum + " times so discard it ... ");
						}
					}
				};

				dimensionTable[ii].get(queryKey, cb);
			}
		}
		processNop();
	}

	private void forwardRow(final ObjectWrapper objw) {

		emitRowsNum.addAndGet(-1);

		int[] nums = new int[objw.forwardObjectsCache.length];
		for (int i = 0; i < nums.length; i++) {
			nums[i] = objw.forwardObjectsCache[i].length;
		}

		permutation.permutate(nums, new PermutateCallback() {

			@Override
			public void call(int[] idxs) {
				ArrayList<Object> forwardObj = new ArrayList<Object>();

				for (int i = 0; i < valueEvaluators.length; i++) {
					for (int j = 0; j < valueEvaluators[i].length; j++) {
						try {
							Object o = valueEvaluators[i][j]
									.evaluate(objw.forwardObjectsCache[i][idxs[i]]);
							forwardObj.add(o);
						} catch (Exception e) {
							log.error(getPrintPrefix() + " forwardRow error : "
									+ TDBankUtils.getExceptionStack(e));
							forwardObj.add(null);
							log.error(getPrintPrefix() + " forwardRow join : "
									+ i + " " + j + " "
									+ objw.forwardObjectsCache[i][idxs[i]]);
						}
					}
				}

				try {
					while (!cachedObjQueue.offer(forwardObj, 1,
							TimeUnit.SECONDS)) {
						processNop();
					}
				} catch (InterruptedException e) {
					log.error(getPrintPrefix()
							+ TDBankUtils.getExceptionStack(e));
				}

			}
		});
	}

	@Override
	protected void processNop() {
		long starttime = System.currentTimeMillis();
		while (true) {
			if (System.currentTimeMillis() - starttime > 1000) {
				// use too much time, we must first give back the compute
				// resources
				log.warn(getPrintPrefix()
						+ "processNop use too much time, and remain "
						+ cachedObjQueue.size() + " row to be processed");
				break;
			}
			Object obj = cachedObjQueue.poll();
			if (obj == null) {
				return;
			}
			forward(obj);
		}
	}

	public static class ObjectWrapper {
		final CountDownLatch cdl;
		final AtomicBoolean filter = new AtomicBoolean(true);
		final Object[][] forwardObjectsCache;

		public ObjectWrapper(int rownum) {
			cdl = new CountDownLatch(rownum);
			forwardObjectsCache = new Object[rownum + 1][];
		}
	}

	@SuppressWarnings("rawtypes")
	private boolean filter(Object row,
			ArrayList<ExprNodeEvaluator> conditionEvaluators,
			ArrayList<PrimitiveObjectInspector> conditionInspectors) {
		for (int i = 0; i < conditionEvaluators.size(); i++) {
			ExprNodeEvaluator conditionEval = conditionEvaluators.get(i);
			PrimitiveObjectInspector conditionInspector = conditionInspectors
					.get(i);
			Object condition;
			try {
				condition = conditionEval.evaluate(row);
				Boolean ret = (Boolean) conditionInspector
						.getPrimitiveJavaObject(condition);
				if (Boolean.FALSE.equals(ret)) {
					return false;
				}
			} catch (HiveException e) {
				log.error(getPrintPrefix() + TDBankUtils.getExceptionStack(e));
			}
		}

		return true;
	}
}
