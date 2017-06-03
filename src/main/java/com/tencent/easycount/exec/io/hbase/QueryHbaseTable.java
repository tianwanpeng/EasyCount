package com.tencent.easycount.exec.io.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
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
import com.tencent.easycount.exec.io.Queryable;
import com.tencent.easycount.metastore.TableHbase;
import com.tencent.easycount.metastore.TableUtils;
import com.tencent.easycount.util.exec.ExecutorProcessor;
import com.tencent.easycount.util.exec.ExecutorProcessor.Processor;
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
	public void printStatus(final int printId) {
		int avgtime = 0;
		for (int i = 0; i < 10; i++) {
			avgtime += this.times[i];
		}
		avgtime /= 10;
		log.info(this.printPrefix + "hbaseDim : " + this.tbl.getTableName()
				+ " queryNum : " + this.queryNum.get() + "-" + avgtime);
		if (this.asyncMode) {
			log.info(this.printPrefix + "hbaseDim : " + this.eps.getStatus());
		}
	}

	class GetInfo {
		String key;
		CallBack cb;

		public GetInfo(final String key, final CallBack cb) {
			this.key = key;
			this.cb = cb;
		}
	}

	public QueryHbaseTable(final TableHbase tbl, final boolean asyncMode,
			final String printPrefix) {
		this.tbl = tbl;
		this.tableName = tbl.getTableName();
		this.printPrefix = printPrefix;
		this.queryNum = new AtomicLong(0);
		this.asyncMode = asyncMode;
		final byte[] separators = TableUtils.generateSeparators(tbl);
		this.valueSerDes = new ArrayList<LazySimpleSerDe>();

		this.htablePool = DataIOUtils.getHbaseTablePool(tbl);

		final ArrayList<ObjectInspector> ois = new ArrayList<ObjectInspector>();
		ois.add(PrimitiveObjectInspectorFactory.writableStringObjectInspector);

		final ArrayList<TypeInfo> hbaseStructType = tbl.getStructTypes();
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
				final LazySimpleSerDe serde = DataIOUtils
						.generateHbaseValueSerDe(separators, "hbase_value",
								valuetype);
				this.valueSerDes.add(serde);
				ois.add(TypeInfoUtils
						.getStandardWritableObjectInspectorFromTypeInfo(hbaseStructType
								.get(i)));
			}
		}

		final int hbaseQueryParallel = TableUtils.getHbaseQueryParallel(tbl);
		final int hbaseQueryQueueSize = TableUtils.getHbaseQueryQueueSize(tbl);

		this.objectInspector = ObjectInspectorFactory
				.getStandardStructObjectInspector(tbl.getFieldNames(), ois);

		this.eps = (!asyncMode) ? null
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
							public int hash(final GetInfo t) {
								return t.key.hashCode() % 499;
							}

							@Override
							public void process(final GetInfo t,
									final int executorid) {
								processGet(t.key, t.cb);
							}
						}, hbaseQueryParallel, hbaseQueryQueueSize,
						"HbaseQueryEPS");
		if (asyncMode) {
			this.eps.start();
		}
	}

	@Override
	public void get(final String key, final CallBack cb) {
		if (this.asyncMode) {
			int xxx = 0;
			while (!this.eps.addTuple(new GetInfo(key, cb))) {
				xxx++;
				log.warn("hbase query queue full so sleep for 30 ms ... for "
						+ xxx + " times");
				try {
					Thread.sleep(30);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			processGet(key, cb);
		}
	}

	private final int[] times = new int[10];
	private int timeidx = 0;

	private void processGet(final String key, final CallBack cb) {
		try {
			this.queryNum.incrementAndGet();
			final byte[] startKeys = key.getBytes();
			final byte[] endKeys = key.getBytes();
			if (key.endsWith("$")) {
				startKeys[startKeys.length - 1] = 0;
				endKeys[startKeys.length - 1] = -1;
			}
			final long t = System.currentTimeMillis();
			final HTableInterface hti = this.htablePool
					.getTable(this.tableName);
			final ResultScanner resScan = hti.getScanner(new Scan(startKeys,
					endKeys));
			hti.close();
			// Result result = hti.get(new Get(key.getBytes()));
			this.times[this.timeidx] = (int) (System.currentTimeMillis() - t);
			this.timeidx = (this.timeidx + 1) % 10;
			final Iterator<Result> it = resScan.iterator();
			final ArrayList<Object[]> resobjs = new ArrayList<Object[]>();
			while (it.hasNext()) {
				resobjs.add(processGetResult1(it.next()));
			}
			cb.callback(resobjs.toArray(new Object[resobjs.size()]));
		} catch (final IOException e) {
			log.warn(TDBankUtils.getExceptionStack(e));
			cb.fail();
		}
	}

	private Object[] processGetResult1(final Result result) {

		final NavigableMap<byte[], NavigableMap<byte[], byte[]>> map = result
				.getNoVersionMap();
		if (map == null) {
			return null;
		}
		if (result.getRow() == null) {
			log.warn("key is null");
			return null;
		}

		final Object[] resobj = new Object[this.tbl.getFieldNames().size()];
		resobj[0] = new Text(result.getRow());

		int i = 0;
		for (final byte[] family : map.keySet()) {
			final NavigableMap<byte[], byte[]> fcolumnmap = map.get(family);
			final HashMap<Object, Object> familyColumnStandard = new HashMap<Object, Object>();
			for (final byte[] column : fcolumnmap.keySet()) {
				final Text columnKey = new Text(column);
				LazyStruct valueObject;
				try {
					// Text vvv = new Text(fcolumnmap.get(column));
					final BytesWritable vvv = new BytesWritable(
							fcolumnmap.get(column));
					valueObject = (LazyStruct) this.valueSerDes.get(i)
							.deserialize(vvv);
					@SuppressWarnings("unchecked")
					final ArrayList<Object> valueObjectStandard = (ArrayList<Object>) ObjectInspectorUtils
					.copyToStandardObject(valueObject, this.valueSerDes
									.get(i).getObjectInspector());
					// System.out.println("valueObjectStandard_testtt: "
					// + new String(vvv.getBytes()) + "   "
					// + valueObjectStandard);

					familyColumnStandard
							.put(columnKey,
									((valueObjectStandard == null) || (valueObjectStandard
											.size() == 0)) ? null
											: valueObjectStandard.get(0));
				} catch (final SerDeException e) {
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
		return this.objectInspector;
	}
}
