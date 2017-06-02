package com.tencent.trc.exec.logical;

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

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.io.TaskContext;
import com.tencent.trc.plan.AggregationDescWrapper;
import com.tencent.trc.plan.AggregationDescWrapper.AggrMode;
import com.tencent.trc.plan.ExprNodeEvaluatorFactoryNew;
import com.tencent.trc.plan.logical.OpDesc5GBY;
import com.tencent.trc.util.constants.Constants;
import com.tencent.trc.util.exec.TimeoutCheckerKeyOnly;
import com.tencent.trc.util.map.Updater1;
import com.tencent.trc.util.map.UpdaterMap1;

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
	public void printInternal(int printId) {
		log.info(getPrintPrefix() + "updateMapSize:" + updateMap.size()
				+ ",cachedForwardKeys:" + cachedForwardKeys.size()
				+ ",timeoutChecker:" + timeoutChecker.getStatus());
	}

	public Operator5GBY(OpDesc5GBY opDesc, TrcConfiguration hconf,
			TaskContext taskContext) {
		super(opDesc, hconf, taskContext);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	protected void initializeOp(TrcConfiguration hconf, TaskContext taskContext) {

		mapfast = hconf.getBoolean("gby.mapfast", false);

		ObjectInspector rowInspector = inputObjInspectors[0];

		// cachedObjQueue = new LinkedBlockingQueue<Object>();
		cachedForwardKeys = new LinkedBlockingQueue<ListKeyWrapper>();
		// init keyFields
		int numKeys = getOpDesc().getGroupByKeys().size();

		mapMode = "MGBY".equals(getOpDesc().getName());

		isAccuGlobal = getOpDesc().isAccuGlobal();

		try {
			ExprNodeDesc expr = getOpDesc().getCoordinateExpr();
			if (expr != null) {
				coordinateExprEval = ExprNodeEvaluatorFactoryNew.get(expr);
				coordinateFieldOI = coordinateExprEval.initialize(rowInspector);
			}
		} catch (HiveException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}
		keyFields = new ExprNodeEvaluator<?>[numKeys];
		keyObjectInspectors = new ObjectInspector[numKeys];
		currentKeyObjectInspectors = new ObjectInspector[numKeys];
		for (int i = 0; i < numKeys; i++) {
			try {
				keyFields[i] = ExprNodeEvaluatorFactoryNew.get(getOpDesc()
						.getGroupByKeys().get(i));
				keyObjectInspectors[i] = keyFields[i].initialize(rowInspector);
				currentKeyObjectInspectors[i] = ObjectInspectorUtils
						.getStandardObjectInspector(keyObjectInspectors[i],
								ObjectInspectorCopyOption.WRITABLE);
			} catch (Exception e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
		}

		// init aggregationParameterFields
		ArrayList<AggregationDescWrapper> aggrs = getOpDesc().getAggregations();
		aggregationParameterFields = new ExprNodeEvaluator[aggrs.size()][];
		aggrIsCount = new boolean[aggrs.size()];
		aggregationParameterObjectInspectors = new ObjectInspector[aggrs.size()][];
		aggregationParameterStandardObjectInspectors = new ObjectInspector[aggrs
				.size()][];
		aggregationParameterObjects = new Object[aggrs.size()][];
		aggrModes = new AggrMode[aggrs.size()];
		nullObjs = new Object[aggrs.size()][];
		for (int i = 0; i < aggrs.size(); i++) {
			AggregationDescWrapper aggr = aggrs.get(i);
			aggrIsCount[i] = aggr.getGenericUDAFEvaluatorClass() == GenericUDAFCountEvaluator.class;
			aggrModes[i] = aggr.getAggrMode();
			if (aggrModes[i] == AggrMode.ACCU || aggrModes[i] == AggrMode.SW) {
				containsWindowFunc = true;
			}
			ArrayList<ExprNodeDesc> parameters = aggr.getParameters();
			aggregationParameterFields[i] = new ExprNodeEvaluator[parameters
					.size()];
			aggregationParameterObjectInspectors[i] = new ObjectInspector[parameters
					.size()];
			aggregationParameterStandardObjectInspectors[i] = new ObjectInspector[parameters
					.size()];
			aggregationParameterObjects[i] = new Object[parameters.size()];
			nullObjs[i] = new Object[parameters.size()];
			for (int j = 0; j < parameters.size(); j++) {
				nullObjs[i][j] = null;
				try {
					aggregationParameterFields[i][j] = ExprNodeEvaluatorFactoryNew
							.get(parameters.get(j));
					aggregationParameterObjectInspectors[i][j] = aggregationParameterFields[i][j]
							.initialize(rowInspector);
					aggregationParameterStandardObjectInspectors[i][j] = ObjectInspectorUtils
							.getStandardObjectInspector(
									aggregationParameterObjectInspectors[i][j],
									ObjectInspectorCopyOption.WRITABLE);
					aggregationParameterObjects[i][j] = null;
				} catch (HiveException e) {
					log.error(TDBankUtils.getExceptionStack(e));
				}
			}
		}

		// init aggregationClasses
		aggregationEvaluators = new GenericUDAFEvaluator[getOpDesc()
				.getAggregations().size()];
		for (int i = 0; i < aggregationEvaluators.length; i++) {
			AggregationDescWrapper agg = getOpDesc().getAggregations().get(i);
			try {
				aggregationEvaluators[i] = agg.getGenericUDAFEvaluatorClass()
						.newInstance();
			} catch (Exception e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
		}

		// init objectInspectors
		int totalFields = keyFields.length + aggregationEvaluators.length + 1;
		objectInspectors = new ArrayList<ObjectInspector>(totalFields);
		for (int i = 0; i < keyFields.length; i++) {
			objectInspectors.add(currentKeyObjectInspectors[i]);
		}
		objectInspectors
				.add(PrimitiveObjectInspectorFactory.javaLongObjectInspector);
		for (int i = 0; i < aggregationEvaluators.length; i++) {
			ObjectInspector roi = null;
			try {
				roi = aggregationEvaluators[i].init(getOpDesc()
						.getAggregations().get(i).getMode(),
						aggregationParameterObjectInspectors[i]);
			} catch (HiveException e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
			objectInspectors.add(roi);
		}

		fieldNames = getOpDesc().getOutputColumnNames();

		// Generate key names
		ArrayList<String> keyNames = new ArrayList<String>(keyFields.length);
		for (int i = 0; i < keyFields.length; i++) {
			keyNames.add(fieldNames.get(i));
		}
		newKeyObjectInspector = ObjectInspectorFactory
				.getStandardStructObjectInspector(keyNames,
						Arrays.asList(keyObjectInspectors));
		currentKeyObjectInspector = ObjectInspectorFactory
				.getStandardStructObjectInspector(keyNames,
						Arrays.asList(currentKeyObjectInspectors));

		outputObjInspector = ObjectInspectorFactory
				.getStandardStructObjectInspector(fieldNames, objectInspectors);

		currentStructEqualComparer = new ListObjectsEqualComparer(
				currentKeyObjectInspectors, currentKeyObjectInspectors);
		// newKeyStructEqualComparer = new ListObjectsEqualComparer(
		// currentKeyObjectInspectors, keyObjectInspectors);

		updateMap = new UpdaterMap1<ListKeyWrapper, Object[][], GenericUDAFEvaluator.AggregationBuffer[]>(
				new Updater1<ListKeyWrapper, Object[][], GenericUDAFEvaluator.AggregationBuffer[]>() {

					@Override
					public AggregationBuffer[] update(ListKeyWrapper k,
							Object[][] v, AggregationBuffer[] aggs) {
						if (aggs == null) {
							try {
								aggs = newAggregations();
							} catch (HiveException e) {
								log.error(getPrintPrefix()
										+ TDBankUtils.getExceptionStack(e));
							}
						}
						for (int ai = 0; ai < aggs.length; ai++) {
							try {
								aggregationEvaluators[ai].aggregate(aggs[ai],
										v[ai]);
							} catch (HiveException e) {
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

		aggrInterval_ms = getOpDesc().getAggrInterval() * 1000;
		accuInterval_ms = getOpDesc().getAccuInterval() * 1000;
		swInterval_ms = getOpDesc().getSwInterval() * 1000;

		windowsNumSW = swInterval_ms / aggrInterval_ms;

		long configsettimeout = hconf.getInt("gby.max.timeout",
				(int) Constants.AMINUTE / 2 / 1000) * 1000;
		timeout_ms = (int) Math.min(aggrInterval_ms * 0.6, configsettimeout);
		if (!mapMode) {
			if (containsWindowFunc) {
				timeout_ms = (int) (aggrInterval_ms + configsettimeout);
			} else {
				timeout_ms *= 2.1;
			}
		}

		timeoutChecker = new TimeoutCheckerKeyOnly<ListKeyWrapper>(
				aggrInterval_ms, TimeUnit.MILLISECONDS, timeout_ms,
				new TimeoutCheckerKeyOnly.CheckerProcessor<ListKeyWrapper>() {

					@Override
					public void process(ListKeyWrapper key, long tupleTime) {
						cachedForwardKeys.offer(key);
					}
				}, "GBY-Checker-" + getOpDesc().getTaskId_OpTagIdx());
		initializeChildren(hconf, taskContext);
	}

	@Override
	public void processOp(Object row, int tag) {
		try {
			long tupleTime = System.currentTimeMillis();
			if (coordinateExprEval != null) {
				Object o = coordinateExprEval.evaluate(row);
				if (o != null) {
					tupleTime = PrimitiveObjectInspectorUtils.getLong(o,
							(PrimitiveObjectInspector) coordinateFieldOI);
				}
			}

			if (shouldprint()) {
				log.info(getPrintPrefix() + "gby-test-tupletime:" + tupleTime);
			}

			// Update the aggs
			updateAggregations(row, tupleTime);
		} catch (Exception e) {
			log.error(getPrintPrefix() + TDBankUtils.getExceptionStack(e));
		}
		processNop();
	}

	long lasttime = System.currentTimeMillis();

	private boolean shouldprint() {
		long ctime = System.currentTimeMillis();
		if (ctime - lasttime > 3000) {
			lasttime = ctime;
			return true;
		}
		return false;
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
						+ cachedForwardKeys.size() + " row to be processed");
				break;
			}
			ListKeyWrapper key = cachedForwardKeys.poll();
			if (key == null) {
				break;
			}
			if (timeoutChecker.containsKey(key, key.timeTag)) {
				// if curr key is exist in timeoutChecker, just continue, wait
				// for next schedule
				log.warn("curr key ( "
						+ key
						+ " ) is exist in timeoutChecker so just continue to wait for next schedule .... ");
				continue;
			}
			try {
				if (isAccuGlobal) {
					long timetag = key.timeTag;

					if ((timetag + aggrInterval_ms) % accuInterval_ms == 0) {
						forward(key.getKeyArray(), timetag,
								updateMap.remove(key));
					} else {
						forward(key.getKeyArray(), timetag, updateMap.get(key));
					}
				} else {
					forward(key.getKeyArray(), key.timeTag,
							updateMap.remove(key));
				}
			} catch (HiveException e) {
				log.error(getPrintPrefix() + TDBankUtils.getExceptionStack(e));
			}
		}
	}

	protected AggregationBuffer[] newAggregations() throws HiveException {
		AggregationBuffer[] aggs = new AggregationBuffer[aggregationEvaluators.length];
		for (int i = 0; i < aggregationEvaluators.length; i++) {
			aggs[i] = aggregationEvaluators[i].getNewAggregationBuffer();
		}
		return aggs;
	}

	protected void updateAggregations(Object row, long tupleTime)
			throws HiveException {
		Object[][] objs = new Object[aggregationParameterFields.length][];
		for (int ai = 0; ai < aggregationParameterFields.length; ai++) {
			Object[] o = new Object[aggregationParameterFields[ai].length];
			for (int pi = 0; pi < aggregationParameterFields[ai].length; pi++) {
				o[pi] = aggregationParameterFields[ai][pi].evaluate(row);
				if (aggrIsCount[ai]) {
					o[pi] = ObjectInspectorUtils.copyToStandardObject(o[pi],
							aggregationParameterObjectInspectors[ai][pi]);
				}
			}
			objs[ai] = o;
		}

		long tupleAggrTime = tupleTime / aggrInterval_ms * aggrInterval_ms;

		ListKeyWrapper keys1 = new ListKeyWrapper(row, tupleAggrTime);
		if (!isAccuGlobal) {
			updateMap.add(keys1, objs);
		} else {
			long currAccuTime = tupleAggrTime / accuInterval_ms
					* accuInterval_ms;
			ListKeyWrapper keys = new ListKeyWrapper(row, currAccuTime);
			updateMap.add(keys, objs);

			// set the last aggr check timer in accu so that to call the remove
			// of the updateMap
			ListKeyWrapper keys2 = new ListKeyWrapper(row, currAccuTime
					+ accuInterval_ms - aggrInterval_ms);
			if (mapfast && mapMode) {
				cachedForwardKeys.offer(keys2);
			} else {
				timeoutChecker
						.update(keys2, currAccuTime + accuInterval_ms - 1);
			}
		}
		if (mapfast && mapMode) {
			cachedForwardKeys.offer(keys1);
		} else {
			timeoutChecker.update(keys1, tupleAggrTime + aggrInterval_ms - 1);
		}

		if (mapMode && containsWindowFunc && !isAccuGlobal) {
			long currAccuTime = tupleAggrTime / accuInterval_ms
					* accuInterval_ms + accuInterval_ms;
			int accuNum = (int) ((currAccuTime - tupleAggrTime) / aggrInterval_ms);

			Object[][] objsMap = new Object[objs.length][];
			long tupleCheckTime = tupleAggrTime + aggrInterval_ms - 1;
			for (int i = 1; i < Math.max(accuNum, windowsNumSW); i++) {
				long targetTupleTime = tupleAggrTime + i * aggrInterval_ms;
				long targetCheckTime = tupleCheckTime + i * aggrInterval_ms;
				for (int j = 0; j < objsMap.length; j++) {
					if (aggrModes[j] == AggrMode.ACCU) {
						if (i < accuNum) {
							objsMap[j] = objs[j];
						} else {
							objsMap[j] = nullObjs[j];
						}
					} else if (aggrModes[j] == AggrMode.SW) {
						if (i < windowsNumSW) {
							objsMap[j] = objs[j];
						} else {
							objsMap[j] = nullObjs[j];
						}
					} else {
						objsMap[j] = nullObjs[j];
					}
				}

				ListKeyWrapper keysNew = new ListKeyWrapper(row,
						targetTupleTime);

				updateMap.add(keysNew, objsMap);
				if (mapfast && mapMode) {
					cachedForwardKeys.offer(keysNew);
				} else {
					timeoutChecker.update(keysNew, targetCheckTime);
				}
			}
		}
	}

	protected void forward(Object[] keys, long timeTag, AggregationBuffer[] aggs)
			throws HiveException {

		if (keys == null || aggs == null) {
			if (keys == null) {
				log.warn("keys is null .... ");
			}
			if (aggs == null) {
				log.warn("aggrs is null .... ");
			}
			return;
		}

		int totalFields = keys.length + aggs.length + 1;
		Object[] forwardCache = new Object[totalFields];

		for (int i = 0; i < keys.length; i++) {
			forwardCache[i] = keys[i];
		}
		forwardCache[keys.length] = timeTag;
		for (int i = 0; i < aggs.length; i++) {
			forwardCache[keys.length + i + 1] = aggregationEvaluators[i]
					.evaluate(aggs[i]);
		}
		forward(forwardCache);
	}

	@Override
	protected void closeOp(boolean abort) {
		try {
			log.info(getPrintPrefix() + "begin to close : "
					+ getOpDesc().getTaskId_OpTagIdx());
			timeoutChecker.close();
			log.info(getPrintPrefix() + "op closed : "
					+ getOpDesc().getTaskId_OpTagIdx());
		} catch (IOException e) {
			log.info(getPrintPrefix() + TDBankUtils.getExceptionStack(e));
		}
	}

	class ListKeyWrapper {

		final Object[] keys;
		final long timeTag;

		public ListKeyWrapper(Object row, long timeTag) throws HiveException {
			this.timeTag = timeTag;
			keys = new Object[keyFields.length];
			for (int i = 0; i < keyFields.length; i++) {
				keys[i] = ObjectInspectorUtils.copyToStandardObject(
						keyFields[i].evaluate(row), keyObjectInspectors[i],
						ObjectInspectorCopyOption.WRITABLE);
			}
		}

		@Override
		public int hashCode() {
			return (int) (Arrays.hashCode(keys) * 31 + timeTag);
		}

		@Override
		public boolean equals(Object obj) {
			ListKeyWrapper tobj = (ListKeyWrapper) obj;
			if (tobj.timeTag != this.timeTag) {
				return false;
			}
			Object[] copied_in_hashmap = tobj.keys;
			return currentStructEqualComparer.areEqual(copied_in_hashmap, keys);

		}

		public Object[] getKeyArray() {
			return keys;
		}
	}
}
