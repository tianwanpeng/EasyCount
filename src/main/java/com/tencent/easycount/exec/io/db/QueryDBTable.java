package com.tencent.easycount.exec.io.db;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.lazy.LazyStruct;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazySimpleStructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils.ObjectInspectorCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.exec.io.DataIOUtils;
import com.tencent.easycount.exec.utils.OIUtils;
import com.tencent.easycount.metastore.TableUtils;

public class QueryDBTable implements Queryable {

	private static Logger log = LoggerFactory.getLogger(QueryDBTable.class);

	final private LazySimpleStructObjectInspector objectInspector;
	final private String printPrefix;
	final private String tableName;
	final private AtomicLong queryNum;

	static private DbTableUpdater tableUpdater = null;

	final private AtomicReference<ConcurrentHashMap<Object, Object[]>> dbDataRef;

	@Override
	public void printStatus(int printId) {
		log.info(printPrefix + "dbDim : " + tableName + " queryNum : "
				+ queryNum.get());
	}

	public QueryDBTable(Table tbl, String printPrefix) {
		this.printPrefix = printPrefix;
		this.tableName = tbl.getTableName();
		this.queryNum = new AtomicLong(0);
		tbl.getTblAttrs().put(TableUtils.TABLE_FIELD_SPLITTER, "|");
		this.objectInspector = OIUtils.createLazyStructInspector(tbl);

		if (tableUpdater == null) {
			tableUpdater = new DbTableUpdater();
		}

		this.dbDataRef = tableUpdater.addTable(tbl, objectInspector);
	}

	@Override
	public void get(String key, CallBack cb) {
		queryNum.incrementAndGet();
		cb.callback(dbDataRef.get().get(key));
	}

	@Override
	public ObjectInspector getObjectInspector() {
		return ObjectInspectorUtils.getStandardObjectInspector(objectInspector,
				ObjectInspectorCopyOption.JAVA);
	}

	static class DbTableUpdater implements Serializable {
		private static final long serialVersionUID = -8464626017917897022L;
		final static private ConcurrentHashMap<String, AtomicReference<ConcurrentHashMap<Object, Object[]>>> table2DataRef = new ConcurrentHashMap<String, AtomicReference<ConcurrentHashMap<Object, Object[]>>>();

		AtomicBoolean started = new AtomicBoolean(false);
		static private Timer timer = null;
		Random r = new Random();

		synchronized public AtomicReference<ConcurrentHashMap<Object, Object[]>> addTable(
				Table tbl, LazySimpleStructObjectInspector objectInspector) {
			if (timer == null) {
				timer = new Timer();
			}
			if (!table2DataRef.containsKey(tbl.getTableName())) {
				AtomicReference<ConcurrentHashMap<Object, Object[]>> dataRef = new AtomicReference<ConcurrentHashMap<Object, Object[]>>();
				dataRef.set(new ConcurrentHashMap<Object, Object[]>());
				table2DataRef.put(tbl.getTableName(), dataRef);

				long updateInterval = TableUtils.getDbUpdateInterval(tbl);

				timer.scheduleAtFixedRate(new UpdateTask(tbl, dataRef,
						objectInspector), new Date(System.currentTimeMillis()
						+ r.nextInt(10000)), updateInterval * 1000);

			}
			return table2DataRef.get(tbl.getTableName());
		}
	}

	static class UpdateTask extends TimerTask {
		final private AtomicReference<ConcurrentHashMap<Object, Object[]>> dataRef;
		final private int keyidx;
		final private String selectSql;
		final private LazySimpleStructObjectInspector objectInspector;
		final private ConnectionPool pools;

		public UpdateTask(Table tbl,
				AtomicReference<ConcurrentHashMap<Object, Object[]>> dataRef,
				LazySimpleStructObjectInspector objectInspector) {
			this.dataRef = dataRef;
			this.objectInspector = objectInspector;

			String keyname = TableUtils.getDbKeyName(tbl);
			keyidx = tbl.getFieldNames().indexOf(keyname);
			pools = DataIOUtils.prepareDBSource(tbl);
			StringBuffer sb = new StringBuffer();
			sb.append("select concat(");
			for (int i = 0; i < tbl.getFieldNames().size(); i++) {
				if (i != 0) {
					sb.append(", \'|\', ");
				}
				sb.append(tbl.getFieldName(i));
			}
			sb.append(") from ").append(tbl.getTableName());

			selectSql = sb.toString();
			log.info("selectSql : " + selectSql);
		}

		@Override
		public void run() {
			initDBData();
		}

		private void initDBData() {
			Connection conn = null;
			try {
				conn = pools.getConnection();
				ResultSet rs = conn.createStatement().executeQuery(selectSql);
				ConcurrentHashMap<Object, ArrayList<Object>> dbDataTmp = new ConcurrentHashMap<Object, ArrayList<Object>>();
				ConcurrentHashMap<Object, Object[]> dbData = new ConcurrentHashMap<Object, Object[]>();
				int rownum = 0;
				int keynum = 0;
				while (rs.next()) {
					String row = rs.getString(1);
					// log.warn("testprintrow : " + row);
					if (row == null) {
						continue;
					}
					byte[] data = row.getBytes("utf-8");
					ByteArrayRef bytes = new ByteArrayRef();
					bytes.setData(data);
					LazyStruct ls = new LazyStruct(objectInspector);
					ls.init(bytes, 0, data.length);

					@SuppressWarnings("unchecked")
					ArrayList<Object> standardObjs = (ArrayList<Object>) ObjectInspectorUtils
							.copyToStandardJavaObject(ls, objectInspector);

					if (standardObjs.size() <= keyidx) {
						log.warn("error data : " + standardObjs);
						continue;
					}
					Object kobj = standardObjs.get(keyidx);
					if (kobj == null) {
						log.warn("error data key is null");
						continue;
					}
					String key = kobj.toString();

					if (!dbDataTmp.containsKey(key)) {
						keynum++;
						dbDataTmp.put(key, new ArrayList<Object>());
					}
					dbDataTmp.get(key).add(standardObjs);
					rownum++;
				}
				conn.close();
				conn = null;
				for (Object key : dbDataTmp.keySet()) {
					dbData.put(
							key,
							dbDataTmp.get(key).toArray(
									new Object[dbDataTmp.get(key).size()]));
				}
				dataRef.set(dbData);
				log.info("update dbdata from db : " + keynum + " keys and "
						+ rownum + " rows !");
			} catch (Throwable e) {
				e.printStackTrace();
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
}
