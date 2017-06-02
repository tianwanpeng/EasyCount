package com.tencent.easycount.exec.io.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.hadoop.fs.shell.CopyCommands.Put;
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
import com.tencent.easycount.metastore.TableUtils;
import com.tencent.easycount.plan.logical.OpDesc7FS;
import com.tencent.easycount.util.exec.TimerPackager;
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
	public void printStatus(int printId) {
		log.info(opDesc.getTaskId_OpTagIdx() + " : emitMsgNum : "
				+ emitMsgNum.get() + " : emitPackNum : " + emitPackNum.get());
	}

	public Data2SinkHbase(OpDesc7FS opDesc) {
		super(opDesc);
		tbl = getOpDesc().getTable();
		this.emitMsgNum = new AtomicLong(0);
		this.emitPackNum = new AtomicLong(0);
		byte[] separators = TableUtils.generateSeparators(tbl);
		this.valueSerDe = DataIOUtils.generateHbaseValueSerDe(separators, null,
				null);
		this.valueOIS = new ArrayList<ObjectInspector>();
		ArrayList<TypeInfo> structTypes = tbl.getStructTypes();
		for (int i = 0; i < structTypes.size(); i++) {
			TypeInfo typeinfo = structTypes.get(i);
			if (i == 0) {
				this.valueOIS
						.add(TypeInfoUtils
								.getStandardWritableObjectInspectorFromTypeInfo(typeinfo));
			} else {
				final TypeInfo valueTypeInfo = ((MapTypeInfo) typeinfo)
						.getMapValueTypeInfo();
				ObjectInspector oi = ObjectInspectorFactory
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

		htableRef = new AtomicReference<HTable>();
		reConnectHTable();
		packager = new TimerPackager<HbaseRow, ArrayList<HbaseRow>>(3000,
				new Packager<HbaseRow, ArrayList<HbaseRow>>() {

					@Override
					public String getKey(HbaseRow t) {
						return "hbase";
					}

					@Override
					public ArrayList<HbaseRow> newPackage(HbaseRow t) {
						return new ArrayList<HbaseRow>();
					}

					@Override
					public boolean pack(String key, HbaseRow t,
							ArrayList<HbaseRow> p) {
						p.add(t);
						return p.size() > 100;
					}

					@Override
					public void emit(String key, ArrayList<HbaseRow> p,
							boolean full) {
						ArrayList<Put> putList = new ArrayList<Put>();
						for (HbaseRow hr : p) {
							Put hp = new Put(
									Bytes.toBytes(hr.rowkey.toString()));
							for (int i = 0; i < hr.values.size(); i++) {
								Map<String, byte[]> valueMap = hr.values.get(i);
								String family = tbl.getFieldName(i + 1);
								byte[] fbytes = Bytes.toBytes(family);
								long tt = System.currentTimeMillis();
								for (String colName : valueMap.keySet()) {
									hp.add(fbytes, Bytes.toBytes(colName), tt,
											valueMap.get(colName));
								}
							}
							putList.add(hp);
						}
						while (true) {
							try {
								HTable table = getHTable();
								table.put(putList);
								emitPackNum.incrementAndGet();
								break;
							} catch (IOException e) {
								reConnectHTable();
								log.info(TDBankUtils.getExceptionStack(e));
							}
						}
					}
				});
	}

	protected void reConnectHTable() {
		htableRef.set(DataIOUtils.getHbaseConn(tbl));
		log.info("hbase sink connected : " + tbl.toString());
	}

	protected HTable getHTable() {
		return htableRef.get();
	}

	@Override
	public boolean finalize(Object row, ObjectInspector objectInspector,
			ObjectInspector keyInspector, ObjectInspector attrsInspector,
			int opTagIdx) {
		try {
			StructObjectInspector soi = (StructObjectInspector) objectInspector;
			List<? extends StructField> fields = soi.getAllStructFieldRefs();
			List<Object> list = soi.getStructFieldsDataAsList(row);

			String keyobj = null;
			ArrayList<Map<String, byte[]>> objs = new ArrayList<Map<String, byte[]>>();
			for (int i = 0; i < fields.size(); i++) {
				ObjectInspector foi = fields.get(i).getFieldObjectInspector();
				Object f = (list == null ? null : list.get(i));
				Object sobj = ObjectInspectorUtils.copyToStandardObject(f, foi);
				if (i == 0) {
					keyobj = String.valueOf(sobj);
				} else {
					HashMap<String, byte[]> valueMap = new HashMap<String, byte[]>();
					if (null != sobj) {
						final Map<?, ?> sobjMap = (Map<?, ?>) sobj;
						for (final Object key : sobjMap.keySet()) {
							ObjectInspector valueOI = valueOIS.get(i);
							Text valueText = (Text) this.valueSerDe.serialize(
									new ArrayList<Object>() {
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
			emitMsgNum.incrementAndGet();
			packager.putTuple(new HbaseRow(keyobj, objs));
		} catch (Exception e) {
			if (shouldprint()) {
				log.info(TDBankUtils.getExceptionStack(e));
			}
			return false;
		}
		return true;
	}

	long ctime = System.currentTimeMillis();

	private boolean shouldprint() {
		if (System.currentTimeMillis() - ctime > 3000) {
			ctime = System.currentTimeMillis();
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

		public HbaseRow(Object rowkey, ArrayList<Map<String, byte[]>> values) {
			this.rowkey = rowkey;
			this.values = values;
		}
	}
}
