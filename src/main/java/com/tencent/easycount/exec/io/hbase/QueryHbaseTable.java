package com.tencent.easycount.exec.io.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe;
import org.apache.hadoop.hive.serde2.lazy.LazyStruct;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.MapTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.exec.io.DataIOUtils;
import com.tencent.easycount.metastore.TableHbase;
import com.tencent.easycount.metastore.TableUtils;
import com.tencent.easycount.util.exec.ExecutorProcessor;
import com.tencent.easycount.util.status.TDBankUtils;

@SuppressWarnings("deprecation")
public class QueryHbaseTable implements Queryable {
	private static Logger log = LoggerFactory.getLogger(QueryHbaseTable.class);

	private StructObjectInspector objectInspector;
	final private TableHbase tbl;
	final private boolean asyncMode;
	final private AtomicLong queryNum;
	final private ArrayList<LazySimpleSerDe> valueSerDes;
	final private ExecutorProcessor<GetInfo> eps;
	final private String tableName;
	final private String printPrefix;

	final HTablePool htablePool;

	@Override
	public void printStatus(int printId) {
		int avgtime = 0;
		for (int i = 0; i < 10; i++) {
			avgtime += times[i];
		}
		avgtime /= 10;
		log.info(printPrefix + "hbaseDim : " + tbl.getTableName()
				+ " queryNum : " + queryNum.get() + "-" + avgtime);
		if (asyncMode) {
			log.info(printPrefix + "hbaseDim : " + eps.getStatus());
		}
	}

	class GetInfo {
		String key;
		CallBack cb;

		public GetInfo(String key, CallBack cb) {
			this.key = key;
			this.cb = cb;
		}
	}

	public QueryHbaseTable(final TableHbase tbl, boolean asyncMode,
			String printPrefix) {
		this.tbl = tbl;
		this.tableName = tbl.getTableName();
		this.printPrefix = printPrefix;
		this.queryNum = new AtomicLong(0);
		this.asyncMode = asyncMode;
		byte[] separators = TableUtils.generateSeparators(tbl);
		this.valueSerDes = new ArrayList<LazySimpleSerDe>();

		htablePool = DataIOUtils.getHbaseTablePool(tbl);

		ArrayList<ObjectInspector> ois = new ArrayList<ObjectInspector>();
		ois.add(PrimitiveObjectInspectorFactory.writableStringObjectInspector);

		ArrayList<TypeInfo> hbaseStructType = tbl.getStructTypes();
		for (int i = 0; i < hbaseStructType.size(); i++) {
			if (i != 0) {
				final TypeInfo valuetype = ((MapTypeInfo) hbaseStructType
						.get(i)).getMapValueTypeInfo();
				// TypeInfo structValueType = TypeInfoFactory.getStructTypeInfo(
				// new ArrayList<String>() {
				// private static final long serialVersionUID =
				// 8070545140343772L;
				// {
				// add("v");
				// }
				// }, new ArrayList<TypeInfo>() {
				// private static final long serialVersionUID =
				// 5653982648339811098L;
				// {
				// add(valuetype);
				// }
				// });
				LazySimpleSerDe serde = DataIOUtils.generateHbaseValueSerDe(
						separators, "hbase_value", valuetype);
				valueSerDes.add(serde);
				ois.add(TypeInfoUtils
						.getStandardWritableObjectInspectorFromTypeInfo(hbaseStructType
								.get(i)));
			}
		}

		int hbaseQueryParallel = TableUtils.getHbaseQueryParallel(tbl);
		int hbaseQueryQueueSize = TableUtils.getHbaseQueryQueueSize(tbl);

		objectInspector = ObjectInspectorFactory
				.getStandardStructObjectInspector(tbl.getFieldNames(), ois);

		eps = (!asyncMode) ? null
				: new ExecutorProcessor<QueryHbaseTable.GetInfo>(
						new Processor<QueryHbaseTable.GetInfo>() {
							{
								log.info("hbase query connected : "
										+ tbl.toString());
							}

							@Override
							public String getStatus() {
								return null;
							}

							@Override
							public int hash(GetInfo t) {
								return t.key.hashCode() % 499;
							}

							@Override
							public void process(GetInfo t, int executorid) {
								processGet(t.key, t.cb);
							}
						}, hbaseQueryParallel, hbaseQueryQueueSize,
						"HbaseQueryEPS");
		if (asyncMode) {
			eps.start();
		}
	}

	@Override
	public void get(String key, final CallBack cb) {
		if (asyncMode) {
			int xxx = 0;
			while (!eps.addTuple(new GetInfo(key, cb))) {
				xxx++;
				log.warn("hbase query queue full so sleep for 30 ms ... for "
						+ xxx + " times");
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			processGet(key, cb);
		}
	}

	private int[] times = new int[10];
	private int timeidx = 0;

	private void processGet(String key, final CallBack cb) {
		try {
			queryNum.incrementAndGet();
			byte[] startKeys = key.getBytes();
			byte[] endKeys = key.getBytes();
			if (key.endsWith("$")) {
				startKeys[startKeys.length - 1] = 0;
				endKeys[startKeys.length - 1] = -1;
			}
			long t = System.currentTimeMillis();
			HTableInterface hti = htablePool.getTable(tableName);
			ResultScanner resScan = hti
					.getScanner(new Scan(startKeys, endKeys));
			hti.close();
			// Result result = hti.get(new Get(key.getBytes()));
			times[timeidx] = (int) (System.currentTimeMillis() - t);
			timeidx = (timeidx + 1) % 10;
			Iterator<Result> it = resScan.iterator();
			ArrayList<Object[]> resobjs = new ArrayList<Object[]>();
			while (it.hasNext()) {
				resobjs.add(processGetResult1(it.next()));
			}
			cb.callback(resobjs.toArray(new Object[resobjs.size()]));
		} catch (IOException e) {
			log.warn(TDBankUtils.getExceptionStack(e));
			cb.fail();
		}
	}

	private Object[] processGetResult1(Result result) {

		NavigableMap<byte[], NavigableMap<byte[], byte[]>> map = result
				.getNoVersionMap();
		if (map == null) {
			return null;
		}
		if (result.getRow() == null) {
			log.warn("key is null");
			return null;
		}

		Object[] resobj = new Object[tbl.getFieldNames().size()];
		resobj[0] = new Text(result.getRow());

		int i = 0;
		for (byte[] family : map.keySet()) {
			NavigableMap<byte[], byte[]> fcolumnmap = map.get(family);
			HashMap<Object, Object> familyColumnStandard = new HashMap<Object, Object>();
			for (byte[] column : fcolumnmap.keySet()) {
				Text columnKey = new Text(column);
				LazyStruct valueObject;
				try {
					// Text vvv = new Text(fcolumnmap.get(column));
					BytesWritable vvv = new BytesWritable(
							fcolumnmap.get(column));
					valueObject = (LazyStruct) valueSerDes.get(i).deserialize(
							vvv);
					@SuppressWarnings("unchecked")
					ArrayList<Object> valueObjectStandard = (ArrayList<Object>) ObjectInspectorUtils
							.copyToStandardObject(valueObject,
									valueSerDes.get(i).getObjectInspector());
					// System.out.println("valueObjectStandard_testtt: "
					// + new String(vvv.getBytes()) + "   "
					// + valueObjectStandard);

					familyColumnStandard.put(
							columnKey,
							(valueObjectStandard == null || valueObjectStandard
									.size() == 0) ? null : valueObjectStandard
									.get(0));
				} catch (SerDeException e) {
					e.printStackTrace();
					familyColumnStandard.put(columnKey, null);
				}
			}
			i++;
			resobj[i] = familyColumnStandard;
		}
		// System.out.println("QueryHbaseTable_testtt: " + resobj[0] + "   "
		// + resobj[1]);

		return resobj;
	}

	@Override
	public ObjectInspector getObjectInspector() {
		return objectInspector;
	}
}
