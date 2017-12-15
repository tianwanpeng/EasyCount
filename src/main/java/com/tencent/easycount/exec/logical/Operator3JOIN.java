package com.tencent.easycount.exec.logical;

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

import com.tencent.easycount.conf.ECConfiguration;
import com.tencent.easycount.exec.io.Queryable;
import com.tencent.easycount.exec.io.Queryable.CallBack;
import com.tencent.easycount.exec.io.QueryableTableUtils;
import com.tencent.easycount.exec.io.TaskContext;
import com.tencent.easycount.metastore.Table;
import com.tencent.easycount.plan.ExprNodeEvaluatorFactoryNew;
import com.tencent.easycount.plan.logical.OpDesc1TS;
import com.tencent.easycount.plan.logical.OpDesc3JOIN;
import com.tencent.easycount.util.algr.Permutation;
import com.tencent.easycount.util.algr.Permutation.PermutateCallback;
import com.tencent.easycount.util.status.TDBankUtils;

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
	public void printInternal(final int printId) {
		log.info(getPrintPrefix() + "emitRowsNum:" + this.emitRowsNum.get()
				+ ",cachedObjQueue:" + this.cachedObjQueue.size());
		for (int i = 0; i < this.dimensionTable.length; i++) {
			this.dimensionTable[i].printStatus(printId);
		}
	}

	public Operator3JOIN(final OpDesc3JOIN opDesc,
			final ECConfiguration hconf, final TaskContext taskContext) {
		super(opDesc, hconf, taskContext);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void initializeOp(final ECConfiguration hconf,
			final TaskContext taskContext) {

		this.emitRowsNum = new AtomicLong(0);
		this.cachedObjQueue = new LinkedBlockingQueue<Object>();

		final HashMap<String, Table> alias2JoinTables = getOpDesc()
				.getAlias2JoinTables();
		final ArrayList<String> joinAliass = new ArrayList<String>(
				alias2JoinTables.keySet());

		this.dimensionTable = new Queryable[joinAliass.size()];
		this.dimensionTableOIs = new ObjectInspector[joinAliass.size()];
		for (int i = 0; i < joinAliass.size(); i++) {
			final Table dimtbl = alias2JoinTables.get(joinAliass.get(i));
			// if (!(dimtbl instanceof TableMem)) {
			// memDimTableOnly = false;
			// }
			this.dimensionTable[i] = QueryableTableUtils.generateQuerable(
					dimtbl, getPrintPrefix(),
					hconf.getBoolean("localmode", false));
			this.dimensionTableOIs[i] = this.dimensionTable[i]
					.getObjectInspector();
		}

		final HashMap<String, ArrayList<ExprNodeDesc>> alias2ExprDesc = getOpDesc()
				.getAlias2ExprDesc();

		final ArrayList<ObjectInspector> ois = new ArrayList<ObjectInspector>();
		this.valueEvaluators = new ExprNodeEvaluator[alias2ExprDesc.size()][];

		int streamTableTag = 0;
		for (int i = 0; i < this.parentOperators.size(); i++) {
			if (!(this.parentOperators.get(i).getOpDesc() instanceof OpDesc1TS)
					|| !((OpDesc1TS) this.parentOperators.get(i).getOpDesc())
					.isDimensionTable()) {
				streamTableTag = i;
				continue;
			}
		}
		log.info("streamTableTag : " + streamTableTag);
		this.streamTableOI = this.inputObjInspectors[streamTableTag];
		this.streamTableStandardOI = ObjectInspectorUtils
				.getStandardObjectInspector(this.streamTableOI);

		final String alias1 = getOpDesc().getStreamAlias();
		final ArrayList<ExprNodeDesc> exprs1 = alias2ExprDesc.get(alias1);
		this.valueEvaluators[0] = new ExprNodeEvaluator[exprs1.size()];
		for (int j = 0; j < exprs1.size(); j++) {
			try {
				this.valueEvaluators[0][j] = ExprNodeEvaluatorFactoryNew
						.get(exprs1.get(j));
				ois.add(this.valueEvaluators[0][j]
						.initialize(this.streamTableStandardOI));
			} catch (final HiveException e) {
				log.error(getPrintPrefix() + TDBankUtils.getExceptionStack(e));
			}
		}

		for (int i = 0; i < joinAliass.size(); i++) {
			final String alias = joinAliass.get(i);
			final ArrayList<ExprNodeDesc> exprs = alias2ExprDesc.get(alias);
			this.valueEvaluators[i + 1] = new ExprNodeEvaluator[exprs.size()];
			for (int j = 0; j < exprs.size(); j++) {
				try {
					this.valueEvaluators[i + 1][j] = ExprNodeEvaluatorFactoryNew
							.get(exprs.get(j));
					ois.add(this.valueEvaluators[i + 1][j]
							.initialize(this.dimensionTableOIs[i]));
				} catch (final HiveException e) {
					log.error(getPrintPrefix()
							+ TDBankUtils.getExceptionStack(e));
				}
			}
		}

		final ArrayList<String> fieldNames = getOpDesc().getOutputColumnNames();

		this.outputObjInspector = ObjectInspectorFactory
				.getStandardStructObjectInspector(fieldNames, ois);

		try {
			final HashMap<String, ExprNodeDesc> key2exprs = getOpDesc()
					.getAlias2ConditionKeyExprs();
			this.keyEvaluators = new ExprNodeEvaluator[joinAliass.size()];
			this.keyInspectors = new PrimitiveObjectInspector[joinAliass.size()];
			for (int i = 0; i < joinAliass.size(); i++) {
				// TODO makesure again
				// keyEvaluators[i] = ExprNodeEvaluatorFactoryNew.get(key2exprs
				// .get(alias2JoinTables.get(joinAliass.get(i))
				// .getTableName()));
				this.keyEvaluators[i] = ExprNodeEvaluatorFactoryNew
						.get(key2exprs.get(joinAliass.get(i)));
				this.keyInspectors[i] = (PrimitiveObjectInspector) this.keyEvaluators[i]
						.initialize(this.streamTableOI);
			}

			this.preConditionEvaluators = new ArrayList<ExprNodeEvaluator>();
			this.preConditionInspectors = new ArrayList<PrimitiveObjectInspector>();
			this.postConditionEvaluators = new ArrayList<ArrayList<ExprNodeEvaluator>>();
			this.postConditionInspectors = new ArrayList<ArrayList<PrimitiveObjectInspector>>();
			for (int i = 0; i < joinAliass.size(); i++) {
				this.postConditionEvaluators
						.add(new ArrayList<ExprNodeEvaluator>());
				this.postConditionInspectors
				.add(new ArrayList<PrimitiveObjectInspector>());
			}
		} catch (final HiveException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}

		this.permutation = new Permutation();

		initializeChildren(hconf, taskContext);
	}

	@Override
	public void processOp(final Object row, final int tag) {
		if (filter(row, this.preConditionEvaluators,
				this.preConditionInspectors)) {
			final ObjectWrapper objw = new ObjectWrapper(
					this.keyEvaluators.length);
			this.emitRowsNum.incrementAndGet();
			objw.forwardObjectsCache[0] = new Object[1];
			objw.forwardObjectsCache[0][0] = ObjectInspectorUtils
					.copyToStandardObject(row, this.streamTableOI);

			for (int i = 0; i < this.keyEvaluators.length; i++) {
				Object key = null;
				try {
					key = ObjectInspectorUtils.copyToStandardJavaObject(
							this.keyEvaluators[i].evaluate(row),
							this.keyInspectors[i]);
				} catch (final Throwable e) {
					objw.cdl.countDown();
					log.error(getPrintPrefix()
							+ TDBankUtils.getExceptionStack(e));
					continue;
				}
				final int ii = i;
				final String queryKey = String.valueOf(key);

				final CallBack cb = new CallBack() {
					int failnum = 0;
					boolean error = false;

					@Override
					public void callback(Object[] rows) {
						objw.cdl.countDown();
						if ((rows != null) && (rows.length > 0)) {
							for (int j = 0; j < rows.length; j++) {
								if (!filter(
										rows[j],
										Operator3JOIN.this.postConditionEvaluators
												.get(ii),
										Operator3JOIN.this.postConditionInspectors
												.get(ii))) {
									objw.filter.set(false);
								}
							}
						} else {
							// at least one row, set to null if no row been
							// joined
							rows = new Object[] { null };
						}
						objw.forwardObjectsCache[ii + 1] = rows;
						if (!this.error && (objw.cdl.getCount() == 0)
								&& objw.filter.get()) {
							forwardRow(objw);
						}
					}

					@Override
					public void fail() {
						this.failnum++;
						if (this.failnum < 100) {
							try {
								Thread.sleep(1);
							} catch (final InterruptedException e) {
								e.printStackTrace();
							}
							Operator3JOIN.this.dimensionTable[ii].get(queryKey,
									this);
						} else {
							this.error = true;
							log.warn("query key " + queryKey + " fail for "
									+ this.failnum
									+ " times so discard it ... ");
						}
					}
				};

				this.dimensionTable[ii].get(queryKey, cb);
			}
		}
		processNop();
	}

	private void forwardRow(final ObjectWrapper objw) {

		this.emitRowsNum.addAndGet(-1);

		final int[] nums = new int[objw.forwardObjectsCache.length];
		for (int i = 0; i < nums.length; i++) {
			nums[i] = objw.forwardObjectsCache[i].length;
		}

		this.permutation.permutate(nums, new PermutateCallback() {

			@Override
			public void call(final int[] idxs) {
				final ArrayList<Object> forwardObj = new ArrayList<Object>();

				for (int i = 0; i < Operator3JOIN.this.valueEvaluators.length; i++) {
					for (int j = 0; j < Operator3JOIN.this.valueEvaluators[i].length; j++) {
						try {
							final Object o = Operator3JOIN.this.valueEvaluators[i][j]
									.evaluate(objw.forwardObjectsCache[i][idxs[i]]);
							forwardObj.add(o);
						} catch (final Exception e) {
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
					while (!Operator3JOIN.this.cachedObjQueue.offer(forwardObj,
							1, TimeUnit.SECONDS)) {
						processNop();
					}
				} catch (final InterruptedException e) {
					log.error(getPrintPrefix()
							+ TDBankUtils.getExceptionStack(e));
				}

			}
		});
	}

	@Override
	protected void processNop() {
		final long starttime = System.currentTimeMillis();
		while (true) {
			if ((System.currentTimeMillis() - starttime) > 1000) {
				// use too much time, we must first give back the compute
				// resources
				log.warn(getPrintPrefix()
						+ "processNop use too much time, and remain "
						+ this.cachedObjQueue.size() + " row to be processed");
				break;
			}
			final Object obj = this.cachedObjQueue.poll();
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

		public ObjectWrapper(final int rownum) {
			this.cdl = new CountDownLatch(rownum);
			this.forwardObjectsCache = new Object[rownum + 1][];
		}
	}

	@SuppressWarnings("rawtypes")
	private boolean filter(final Object row,
			final ArrayList<ExprNodeEvaluator> conditionEvaluators,
			final ArrayList<PrimitiveObjectInspector> conditionInspectors) {
		for (int i = 0; i < conditionEvaluators.size(); i++) {
			final ExprNodeEvaluator conditionEval = conditionEvaluators.get(i);
			final PrimitiveObjectInspector conditionInspector = conditionInspectors
					.get(i);
			Object condition;
			try {
				condition = conditionEval.evaluate(row);
				final Boolean ret = (Boolean) conditionInspector
						.getPrimitiveJavaObject(condition);
				if (Boolean.FALSE.equals(ret)) {
					return false;
				}
			} catch (final HiveException e) {
				log.error(getPrintPrefix() + TDBankUtils.getExceptionStack(e));
			}
		}

		return true;
	}
}
