package com.tencent.easycount.exec.logical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluator;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFCount.GenericUDAFCountEvaluator;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator.AggregationBuffer;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectsEqualComparer;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils.ObjectInspectorCopyOption;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.conf.ECConfiguration;
import com.tencent.easycount.exec.io.TaskContext;
import com.tencent.easycount.plan.AggregationDescWrapper;
import com.tencent.easycount.plan.AggregationDescWrapper.AggrMode;
import com.tencent.easycount.plan.ExprNodeEvaluatorFactoryNew;
import com.tencent.easycount.plan.logical.OpDesc5GBY;
import com.tencent.easycount.util.constants.Constants;
import com.tencent.easycount.util.exec.TimeoutCheckerKeyOnly;
import com.tencent.easycount.util.map.Updater1;
import com.tencent.easycount.util.map.UpdaterMap1;
import com.tencent.easycount.util.status.TDBankUtils;

@SuppressWarnings("deprecation")
public class Operator5GBY extends Operator<OpDesc5GBY> {
	private static Logger log = LoggerFactory.getLogger(Operator5GBY.class);

	protected transient ExprNodeEvaluator<? extends ExprNodeDesc>[] keyFields;
	private transient ExprNodeEvaluator<? extends ExprNodeDesc> coordinateExprEval = null;
	private transient ObjectInspector coordinateFieldOI;
	protected transient ObjectInspector[] keyObjectInspectors;
	protected transient ExprNodeEvaluator<? extends ExprNodeDesc>[][] aggregationParameterFields;
	// hive have a bug.....
	protected transient boolean[] aggrIsCount;
	protected transient ObjectInspector[][] aggregationParameterObjectInspectors;
	protected transient ObjectInspector[][] aggregationParameterStandardObjectInspectors;
	protected transient Object[][] aggregationParameterObjects;
	transient GenericUDAFEvaluator[] aggregationEvaluators;
	protected transient AggrMode[] aggrModes;
	protected transient Object[][] nullObjs;

	// current Key ObjectInspectors are standard ObjectInspectors
	protected transient ObjectInspector[] currentKeyObjectInspectors;
	// new Key ObjectInspectors are objectInspectors from the parent
	transient StructObjectInspector newKeyObjectInspector;
	transient StructObjectInspector currentKeyObjectInspector;

	protected transient ArrayList<ObjectInspector> objectInspectors;
	transient ArrayList<String> fieldNames;

	protected transient AggregationBuffer[] aggregations;
	protected transient Object[][] aggregationsParametersLastInvoke;

	transient private ListObjectsEqualComparer currentStructEqualComparer;
	// transient private ListObjectsEqualComparer newKeyStructEqualComparer;

	transient private TimeoutCheckerKeyOnly<ListKeyWrapper> timeoutChecker;
	transient private UpdaterMap1<ListKeyWrapper, Object[][], AggregationBuffer[]> updateMap;
	transient private int aggrInterval_ms = 60 * 1000;
	transient private int accuInterval_ms = 1800 * 1000;
	transient private int swInterval_ms = 1800 * 1000;
	transient private boolean containsWindowFunc = false;
	transient private int windowsNumSW = 30;
	// transient private int windowsNumMaxACCU = 30;

	transient private int timeout_ms = 1000;
	transient private boolean mapMode = true;

	transient private boolean isAccuGlobal = false;
	// transient private LinkedBlockingQueue<Object> cachedObjQueue = null;
	transient private LinkedBlockingQueue<ListKeyWrapper> cachedForwardKeys = null;

	transient private boolean mapfast = false;

	@Override
	public void printInternal(final int printId) {
		log.info(getPrintPrefix() + "updateMapSize:" + this.updateMap.size()
				+ ",cachedForwardKeys:" + this.cachedForwardKeys.size()
				+ ",timeoutChecker:" + this.timeoutChecker.getStatus());
	}

	public Operator5GBY(final OpDesc5GBY opDesc, final ECConfiguration hconf,
			final TaskContext taskContext) {
		super(opDesc, hconf, taskContext);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	protected void initializeOp(final ECConfiguration hconf,
			final TaskContext taskContext) {

		this.mapfast = hconf.getBoolean("gby.mapfast", false);

		final ObjectInspector rowInspector = this.inputObjInspectors[0];

		// cachedObjQueue = new LinkedBlockingQueue<Object>();
		this.cachedForwardKeys = new LinkedBlockingQueue<ListKeyWrapper>();
		// init keyFields
		final int numKeys = getOpDesc().getGroupByKeys().size();

		this.mapMode = "MGBY".equals(getOpDesc().getName());

		this.isAccuGlobal = getOpDesc().isAccuGlobal();

		try {
			final ExprNodeDesc expr = getOpDesc().getCoordinateExpr();
			if (expr != null) {
				this.coordinateExprEval = ExprNodeEvaluatorFactoryNew.get(expr);
				this.coordinateFieldOI = this.coordinateExprEval
						.initialize(rowInspector);
			}
		} catch (final HiveException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}
		this.keyFields = new ExprNodeEvaluator<?>[numKeys];
		this.keyObjectInspectors = new ObjectInspector[numKeys];
		this.currentKeyObjectInspectors = new ObjectInspector[numKeys];
		for (int i = 0; i < numKeys; i++) {
			try {
				this.keyFields[i] = ExprNodeEvaluatorFactoryNew.get(getOpDesc()
						.getGroupByKeys().get(i));
				this.keyObjectInspectors[i] = this.keyFields[i]
						.initialize(rowInspector);
				this.currentKeyObjectInspectors[i] = ObjectInspectorUtils
						.getStandardObjectInspector(
								this.keyObjectInspectors[i],
								ObjectInspectorCopyOption.WRITABLE);
			} catch (final Exception e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
		}

		// init aggregationParameterFields
		final ArrayList<AggregationDescWrapper> aggrs = getOpDesc()
				.getAggregations();
		this.aggregationParameterFields = new ExprNodeEvaluator[aggrs.size()][];
		this.aggrIsCount = new boolean[aggrs.size()];
		this.aggregationParameterObjectInspectors = new ObjectInspector[aggrs
				.size()][];
		this.aggregationParameterStandardObjectInspectors = new ObjectInspector[aggrs
		                                                                        .size()][];
		this.aggregationParameterObjects = new Object[aggrs.size()][];
		this.aggrModes = new AggrMode[aggrs.size()];
		this.nullObjs = new Object[aggrs.size()][];
		for (int i = 0; i < aggrs.size(); i++) {
			final AggregationDescWrapper aggr = aggrs.get(i);
			this.aggrIsCount[i] = aggr.getGenericUDAFEvaluatorClass() == GenericUDAFCountEvaluator.class;
			this.aggrModes[i] = aggr.getAggrMode();
			if ((this.aggrModes[i] == AggrMode.ACCU)
					|| (this.aggrModes[i] == AggrMode.SW)) {
				this.containsWindowFunc = true;
			}
			final ArrayList<ExprNodeDesc> parameters = aggr.getParameters();
			this.aggregationParameterFields[i] = new ExprNodeEvaluator[parameters
			                                                           .size()];
			this.aggregationParameterObjectInspectors[i] = new ObjectInspector[parameters
			                                                                   .size()];
			this.aggregationParameterStandardObjectInspectors[i] = new ObjectInspector[parameters
			                                                                           .size()];
			this.aggregationParameterObjects[i] = new Object[parameters.size()];
			this.nullObjs[i] = new Object[parameters.size()];
			for (int j = 0; j < parameters.size(); j++) {
				this.nullObjs[i][j] = null;
				try {
					this.aggregationParameterFields[i][j] = ExprNodeEvaluatorFactoryNew
							.get(parameters.get(j));
					this.aggregationParameterObjectInspectors[i][j] = this.aggregationParameterFields[i][j]
							.initialize(rowInspector);
					this.aggregationParameterStandardObjectInspectors[i][j] = ObjectInspectorUtils
							.getStandardObjectInspector(
									this.aggregationParameterObjectInspectors[i][j],
									ObjectInspectorCopyOption.WRITABLE);
					this.aggregationParameterObjects[i][j] = null;
				} catch (final HiveException e) {
					log.error(TDBankUtils.getExceptionStack(e));
				}
			}
		}

		// init aggregationClasses
		this.aggregationEvaluators = new GenericUDAFEvaluator[getOpDesc()
		                                                      .getAggregations().size()];
		for (int i = 0; i < this.aggregationEvaluators.length; i++) {
			final AggregationDescWrapper agg = getOpDesc().getAggregations()
					.get(i);
			try {
				this.aggregationEvaluators[i] = agg
						.getGenericUDAFEvaluatorClass().newInstance();
			} catch (final Exception e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
		}

		// init objectInspectors
		final int totalFields = this.keyFields.length
				+ this.aggregationEvaluators.length + 1;
		this.objectInspectors = new ArrayList<ObjectInspector>(totalFields);
		for (int i = 0; i < this.keyFields.length; i++) {
			this.objectInspectors.add(this.currentKeyObjectInspectors[i]);
		}
		this.objectInspectors
		.add(PrimitiveObjectInspectorFactory.javaLongObjectInspector);
		for (int i = 0; i < this.aggregationEvaluators.length; i++) {
			ObjectInspector roi = null;
			try {
				roi = this.aggregationEvaluators[i].init(getOpDesc()
						.getAggregations().get(i).getMode(),
						this.aggregationParameterObjectInspectors[i]);
			} catch (final HiveException e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
			this.objectInspectors.add(roi);
		}

		this.fieldNames = getOpDesc().getOutputColumnNames();

		// Generate key names
		final ArrayList<String> keyNames = new ArrayList<String>(
				this.keyFields.length);
		for (int i = 0; i < this.keyFields.length; i++) {
			keyNames.add(this.fieldNames.get(i));
		}
		this.newKeyObjectInspector = ObjectInspectorFactory
				.getStandardStructObjectInspector(keyNames,
						Arrays.asList(this.keyObjectInspectors));
		this.currentKeyObjectInspector = ObjectInspectorFactory
				.getStandardStructObjectInspector(keyNames,
						Arrays.asList(this.currentKeyObjectInspectors));

		this.outputObjInspector = ObjectInspectorFactory
				.getStandardStructObjectInspector(this.fieldNames,
						this.objectInspectors);

		this.currentStructEqualComparer = new ListObjectsEqualComparer(
				this.currentKeyObjectInspectors,
				this.currentKeyObjectInspectors);
		// newKeyStructEqualComparer = new ListObjectsEqualComparer(
		// currentKeyObjectInspectors, keyObjectInspectors);

		this.updateMap = new UpdaterMap1<ListKeyWrapper, Object[][], GenericUDAFEvaluator.AggregationBuffer[]>(
				new Updater1<ListKeyWrapper, Object[][], GenericUDAFEvaluator.AggregationBuffer[]>() {

					@Override
					public AggregationBuffer[] update(final ListKeyWrapper k,
							final Object[][] v, AggregationBuffer[] aggs) {
						if (aggs == null) {
							try {
								aggs = newAggregations();
							} catch (final HiveException e) {
								log.error(getPrintPrefix()
										+ TDBankUtils.getExceptionStack(e));
							}
						}
						for (int ai = 0; ai < aggs.length; ai++) {
							try {
								Operator5GBY.this.aggregationEvaluators[ai]
										.aggregate(aggs[ai], v[ai]);
							} catch (final HiveException e) {
								log.error(getPrintPrefix()
										+ TDBankUtils.getExceptionStack(e));
							}
						}

						return aggs;
					}

					@Override
					public boolean inplace() {
						return true;
					}
				});

		this.aggrInterval_ms = getOpDesc().getAggrInterval() * 1000;
		this.accuInterval_ms = getOpDesc().getAccuInterval() * 1000;
		this.swInterval_ms = getOpDesc().getSwInterval() * 1000;

		this.windowsNumSW = this.swInterval_ms / this.aggrInterval_ms;

		final long configsettimeout = hconf.getInt("gby.max.timeout",
				(int) Constants.AMINUTE / 2 / 1000) * 1000;
		this.timeout_ms = (int) Math.min(this.aggrInterval_ms * 0.6,
				configsettimeout);
		if (!this.mapMode) {
			if (this.containsWindowFunc) {
				this.timeout_ms = (int) (this.aggrInterval_ms + configsettimeout);
			} else {
				this.timeout_ms *= 2.1;
			}
		}

		this.timeoutChecker = new TimeoutCheckerKeyOnly<ListKeyWrapper>(
				this.aggrInterval_ms, TimeUnit.MILLISECONDS, this.timeout_ms,
				new TimeoutCheckerKeyOnly.CheckerProcessor<ListKeyWrapper>() {

					@Override
					public void process(final ListKeyWrapper key,
							final long tupleTime) {
						Operator5GBY.this.cachedForwardKeys.offer(key);
					}
				}, "GBY-Checker-" + getOpDesc().getTaskId_OpTagIdx());
		initializeChildren(hconf, taskContext);
	}

	@Override
	public void processOp(final Object row, final int tag) {
		try {
			long tupleTime = System.currentTimeMillis();
			if (this.coordinateExprEval != null) {
				final Object o = this.coordinateExprEval.evaluate(row);
				if (o != null) {
					tupleTime = PrimitiveObjectInspectorUtils.getLong(o,
							(PrimitiveObjectInspector) this.coordinateFieldOI);
				}
			}

			if (shouldprint()) {
				log.info(getPrintPrefix() + "gby-test-tupletime:" + tupleTime);
			}

			// Update the aggs
			updateAggregations(row, tupleTime);
		} catch (final Exception e) {
			log.error(getPrintPrefix() + TDBankUtils.getExceptionStack(e));
		}
		processNop();
	}

	long lasttime = System.currentTimeMillis();

	private boolean shouldprint() {
		final long ctime = System.currentTimeMillis();
		if ((ctime - this.lasttime) > 3000) {
			this.lasttime = ctime;
			return true;
		}
		return false;
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
						+ this.cachedForwardKeys.size()
						+ " row to be processed");
				break;
			}
			final ListKeyWrapper key = this.cachedForwardKeys.poll();
			if (key == null) {
				break;
			}
			if (this.timeoutChecker.containsKey(key, key.timeTag)) {
				// if curr key is exist in timeoutChecker, just continue, wait
				// for next schedule
				log.warn("curr key ( "
						+ key
						+ " ) is exist in timeoutChecker so just continue to wait for next schedule .... ");
				continue;
			}
			try {
				if (this.isAccuGlobal) {
					final long timetag = key.timeTag;

					if (((timetag + this.aggrInterval_ms) % this.accuInterval_ms) == 0) {
						forward(key.getKeyArray(), timetag,
								this.updateMap.remove(key));
					} else {
						forward(key.getKeyArray(), timetag,
								this.updateMap.get(key));
					}
				} else {
					forward(key.getKeyArray(), key.timeTag,
							this.updateMap.remove(key));
				}
			} catch (final HiveException e) {
				log.error(getPrintPrefix() + TDBankUtils.getExceptionStack(e));
			}
		}
	}

	protected AggregationBuffer[] newAggregations() throws HiveException {
		final AggregationBuffer[] aggs = new AggregationBuffer[this.aggregationEvaluators.length];
		for (int i = 0; i < this.aggregationEvaluators.length; i++) {
			aggs[i] = this.aggregationEvaluators[i].getNewAggregationBuffer();
		}
		return aggs;
	}

	protected void updateAggregations(final Object row, final long tupleTime)
			throws HiveException {
		final Object[][] objs = new Object[this.aggregationParameterFields.length][];
		for (int ai = 0; ai < this.aggregationParameterFields.length; ai++) {
			final Object[] o = new Object[this.aggregationParameterFields[ai].length];
			for (int pi = 0; pi < this.aggregationParameterFields[ai].length; pi++) {
				o[pi] = this.aggregationParameterFields[ai][pi].evaluate(row);
				if (this.aggrIsCount[ai]) {
					o[pi] = ObjectInspectorUtils.copyToStandardObject(o[pi],
							this.aggregationParameterObjectInspectors[ai][pi]);
				}
			}
			objs[ai] = o;
		}

		final long tupleAggrTime = (tupleTime / this.aggrInterval_ms)
				* this.aggrInterval_ms;

		final ListKeyWrapper keys1 = new ListKeyWrapper(row, tupleAggrTime);
		if (!this.isAccuGlobal) {
			this.updateMap.add(keys1, objs);
		} else {
			final long currAccuTime = (tupleAggrTime / this.accuInterval_ms)
					* this.accuInterval_ms;
			final ListKeyWrapper keys = new ListKeyWrapper(row, currAccuTime);
			this.updateMap.add(keys, objs);

			// set the last aggr check timer in accu so that to call the remove
			// of the updateMap
			final ListKeyWrapper keys2 = new ListKeyWrapper(row,
					(currAccuTime + this.accuInterval_ms)
							- this.aggrInterval_ms);
			if (this.mapfast && this.mapMode) {
				this.cachedForwardKeys.offer(keys2);
			} else {
				this.timeoutChecker.update(keys2,
						(currAccuTime + this.accuInterval_ms) - 1);
			}
		}
		if (this.mapfast && this.mapMode) {
			this.cachedForwardKeys.offer(keys1);
		} else {
			this.timeoutChecker.update(keys1,
					(tupleAggrTime + this.aggrInterval_ms) - 1);
		}

		if (this.mapMode && this.containsWindowFunc && !this.isAccuGlobal) {
			final long currAccuTime = ((tupleAggrTime / this.accuInterval_ms) * this.accuInterval_ms)
					+ this.accuInterval_ms;
			final int accuNum = (int) ((currAccuTime - tupleAggrTime) / this.aggrInterval_ms);

			final Object[][] objsMap = new Object[objs.length][];
			final long tupleCheckTime = (tupleAggrTime + this.aggrInterval_ms) - 1;
			for (int i = 1; i < Math.max(accuNum, this.windowsNumSW); i++) {
				final long targetTupleTime = tupleAggrTime
						+ (i * this.aggrInterval_ms);
				final long targetCheckTime = tupleCheckTime
						+ (i * this.aggrInterval_ms);
				for (int j = 0; j < objsMap.length; j++) {
					if (this.aggrModes[j] == AggrMode.ACCU) {
						if (i < accuNum) {
							objsMap[j] = objs[j];
						} else {
							objsMap[j] = this.nullObjs[j];
						}
					} else if (this.aggrModes[j] == AggrMode.SW) {
						if (i < this.windowsNumSW) {
							objsMap[j] = objs[j];
						} else {
							objsMap[j] = this.nullObjs[j];
						}
					} else {
						objsMap[j] = this.nullObjs[j];
					}
				}

				final ListKeyWrapper keysNew = new ListKeyWrapper(row,
						targetTupleTime);

				this.updateMap.add(keysNew, objsMap);
				if (this.mapfast && this.mapMode) {
					this.cachedForwardKeys.offer(keysNew);
				} else {
					this.timeoutChecker.update(keysNew, targetCheckTime);
				}
			}
		}
	}

	protected void forward(final Object[] keys, final long timeTag,
			final AggregationBuffer[] aggs) throws HiveException {

		if ((keys == null) || (aggs == null)) {
			if (keys == null) {
				log.warn("keys is null .... ");
			}
			if (aggs == null) {
				log.warn("aggrs is null .... ");
			}
			return;
		}

		final int totalFields = keys.length + aggs.length + 1;
		final Object[] forwardCache = new Object[totalFields];

		for (int i = 0; i < keys.length; i++) {
			forwardCache[i] = keys[i];
		}
		forwardCache[keys.length] = timeTag;
		for (int i = 0; i < aggs.length; i++) {
			forwardCache[keys.length + i + 1] = this.aggregationEvaluators[i]
					.evaluate(aggs[i]);
		}
		forward(forwardCache);
	}

	@Override
	protected void closeOp(final boolean abort) {
		try {
			log.info(getPrintPrefix() + "begin to close : "
					+ getOpDesc().getTaskId_OpTagIdx());
			this.timeoutChecker.close();
			log.info(getPrintPrefix() + "op closed : "
					+ getOpDesc().getTaskId_OpTagIdx());
		} catch (final IOException e) {
			log.info(getPrintPrefix() + TDBankUtils.getExceptionStack(e));
		}
	}

	class ListKeyWrapper {

		final Object[] keys;
		final long timeTag;

		public ListKeyWrapper(final Object row, final long timeTag)
				throws HiveException {
			this.timeTag = timeTag;
			this.keys = new Object[Operator5GBY.this.keyFields.length];
			for (int i = 0; i < Operator5GBY.this.keyFields.length; i++) {
				this.keys[i] = ObjectInspectorUtils.copyToStandardObject(
						Operator5GBY.this.keyFields[i].evaluate(row),
						Operator5GBY.this.keyObjectInspectors[i],
						ObjectInspectorCopyOption.WRITABLE);
			}
		}

		@Override
		public int hashCode() {
			return (int) ((Arrays.hashCode(this.keys) * 31) + this.timeTag);
		}

		@Override
		public boolean equals(final Object obj) {
			final ListKeyWrapper tobj = (ListKeyWrapper) obj;
			if (tobj.timeTag != this.timeTag) {
				return false;
			}
			final Object[] copied_in_hashmap = tobj.keys;
			return Operator5GBY.this.currentStructEqualComparer.areEqual(
					copied_in_hashmap, this.keys);

		}

		public Object[] getKeyArray() {
			return this.keys;
		}
	}
}
