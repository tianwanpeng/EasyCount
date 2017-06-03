package com.tencent.easycount.exec.io.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.MapTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.exec.io.Data2Sink;
import com.tencent.easycount.exec.io.DataIOUtils;
import com.tencent.easycount.metastore.Table;
import com.tencent.easycount.metastore.TableUtils;
import com.tencent.easycount.plan.logical.OpDesc7FS;
import com.tencent.easycount.util.exec.TimerPackager;
import com.tencent.easycount.util.exec.TimerPackager.Packager;
import com.tencent.easycount.util.status.TDBankUtils;

public class Data2SinkHbase extends Data2Sink {

	private static Logger log = LoggerFactory.getLogger(Data2SinkHbase.class);

	final private Table tbl;
	final AtomicReference<HTable> htableRef;
	final private TimerPackager<HbaseRow, ArrayList<HbaseRow>> packager;
	private final AtomicLong emitMsgNum;
	private final AtomicLong emitPackNum;
	private LazySimpleSerDe valueSerDe;
	ArrayList<ObjectInspector> valueOIS;

	@Override
	public void printStatus(final int printId) {
		log.info(this.opDesc.getTaskId_OpTagIdx() + " : emitMsgNum : "
				+ this.emitMsgNum.get() + " : emitPackNum : "
				+ this.emitPackNum.get());
	}

	public Data2SinkHbase(final OpDesc7FS opDesc) {
		super(opDesc);
		this.tbl = getOpDesc().getTable();
		this.emitMsgNum = new AtomicLong(0);
		this.emitPackNum = new AtomicLong(0);
		final byte[] separators = TableUtils.generateSeparators(this.tbl);
		this.valueSerDe = DataIOUtils.generateHbaseValueSerDe(separators, null,
				null);
		this.valueOIS = new ArrayList<ObjectInspector>();
		final ArrayList<TypeInfo> structTypes = this.tbl.getStructTypes();
		for (int i = 0; i < structTypes.size(); i++) {
			final TypeInfo typeinfo = structTypes.get(i);
			if (i == 0) {
				this.valueOIS
						.add(TypeInfoUtils
								.getStandardWritableObjectInspectorFromTypeInfo(typeinfo));
			} else {
				final TypeInfo valueTypeInfo = ((MapTypeInfo) typeinfo)
						.getMapValueTypeInfo();
				final ObjectInspector oi = ObjectInspectorFactory
						.getStandardStructObjectInspector(
								new ArrayList<String>() {
									private static final long serialVersionUID = 6475415283850411978L;
									{
										add("v");
									}
								}, new ArrayList<ObjectInspector>() {
									private static final long serialVersionUID = 4129516970809780765L;
									{
										add(TypeInfoUtils
												.getStandardWritableObjectInspectorFromTypeInfo(valueTypeInfo));
									}
								});
				this.valueOIS.add(oi);
			}
		}

		this.htableRef = new AtomicReference<HTable>();
		reConnectHTable();
		this.packager = new TimerPackager<HbaseRow, ArrayList<HbaseRow>>(3000,
				new Packager<HbaseRow, ArrayList<HbaseRow>>() {

					@Override
					public String getKey(final HbaseRow t) {
						return "hbase";
					}

					@Override
					public ArrayList<HbaseRow> newPackage(final HbaseRow t) {
						return new ArrayList<HbaseRow>();
					}

					@Override
					public boolean pack(final String key, final HbaseRow t,
							final ArrayList<HbaseRow> p) {
						p.add(t);
						return p.size() > 100;
					}

					@Override
					public void emit(final String key,
							final ArrayList<HbaseRow> p, final boolean full) {
						final ArrayList<Put> putList = new ArrayList<Put>();
						for (final HbaseRow hr : p) {
							final Put hp = new Put(Bytes.toBytes(hr.rowkey
									.toString()));
							for (int i = 0; i < hr.values.size(); i++) {
								final Map<String, byte[]> valueMap = hr.values
										.get(i);
								final String family = Data2SinkHbase.this.tbl
										.getFieldName(i + 1);
								final byte[] fbytes = Bytes.toBytes(family);
								final long tt = System.currentTimeMillis();
								for (final String colName : valueMap.keySet()) {
									hp.add(fbytes, Bytes.toBytes(colName), tt,
											valueMap.get(colName));
								}
							}
							putList.add(hp);
						}
						while (true) {
							try {
								final HTable table = getHTable();
								table.put(putList);
								Data2SinkHbase.this.emitPackNum
										.incrementAndGet();
								break;
							} catch (final IOException e) {
								reConnectHTable();
								log.info(TDBankUtils.getExceptionStack(e));
							}
						}
					}
				});
	}

	protected void reConnectHTable() {
		this.htableRef.set(DataIOUtils.getHbaseConn(this.tbl));
		log.info("hbase sink connected : " + this.tbl.toString());
	}

	protected HTable getHTable() {
		return this.htableRef.get();
	}

	@Override
	public boolean finalize(final Object row,
			final ObjectInspector objectInspector,
			final ObjectInspector keyInspector,
			final ObjectInspector attrsInspector, final int opTagIdx) {
		try {
			final StructObjectInspector soi = (StructObjectInspector) objectInspector;
			final List<? extends StructField> fields = soi
					.getAllStructFieldRefs();
			final List<Object> list = soi.getStructFieldsDataAsList(row);

			String keyobj = null;
			final ArrayList<Map<String, byte[]>> objs = new ArrayList<Map<String, byte[]>>();
			for (int i = 0; i < fields.size(); i++) {
				final ObjectInspector foi = fields.get(i)
						.getFieldObjectInspector();
				final Object f = (list == null ? null : list.get(i));
				final Object sobj = ObjectInspectorUtils.copyToStandardObject(
						f, foi);
				if (i == 0) {
					keyobj = String.valueOf(sobj);
				} else {
					final HashMap<String, byte[]> valueMap = new HashMap<String, byte[]>();
					if (null != sobj) {
						final Map<?, ?> sobjMap = (Map<?, ?>) sobj;
						for (final Object key : sobjMap.keySet()) {
							final ObjectInspector valueOI = this.valueOIS
									.get(i);
							final Text valueText = (Text) this.valueSerDe
									.serialize(new ArrayList<Object>() {
										private static final long serialVersionUID = 938921474785972741L;
										{
											add(sobjMap.get(key));
										}
									}, valueOI);
							valueMap.put(String.valueOf(key), valueText
									.toString().getBytes());
						}
					}
					objs.add(valueMap);
				}
			}
			this.emitMsgNum.incrementAndGet();
			this.packager.putTuple(new HbaseRow(keyobj, objs));
		} catch (final Exception e) {
			if (shouldprint()) {
				log.info(TDBankUtils.getExceptionStack(e));
			}
			return false;
		}
		return true;
	}

	long ctime = System.currentTimeMillis();

	private boolean shouldprint() {
		if ((System.currentTimeMillis() - this.ctime) > 3000) {
			this.ctime = System.currentTimeMillis();
			return true;
		}
		return false;
	}

	@Override
	public void close() throws IOException {

	}

	private class HbaseRow {
		Object rowkey;
		ArrayList<Map<String, byte[]>> values;

		public HbaseRow(final Object rowkey,
				final ArrayList<Map<String, byte[]>> values) {
			this.rowkey = rowkey;
			this.values = values;
		}
	}
}
