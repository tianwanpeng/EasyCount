package com.tencent.trc.exec.io.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import snaq.db.ConnectionPool;

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.trc.exec.io.Data2Sink;
import com.tencent.trc.exec.io.DataIOUtils;
import com.tencent.trc.metastore.Table;
import com.tencent.trc.plan.logical.OpDesc7FS;
import com.tencent.trc.util.exec.TimerPackager;
import com.tencent.trc.util.exec.TimerPackager.Packager;

public class Data2SinkDB extends Data2Sink {

	private static Logger log = LoggerFactory.getLogger(Data2SinkDB.class);

	private static ConnectionPool pools = null;
	final private ArrayList<Integer> columnTypes;
	final private Table tbl;
	final private String insertSql;
	final private TimerPackager<ArrayList<String>, ArrayList<ArrayList<String>>> packager;
	final private int[] times;
	final private int TIMESLEN = 100;
	private final AtomicLong emitMsgNum;
	private final AtomicLong emitPackNum;

	@Override
	public void printStatus(int printId) {
		int max = 0;
		int sum = 0;
		for (int i = 0; i < times.length; i++) {
			sum += times[i];
			max = Math.max(max, times[i]);
		}
		log.info("packager:" + packager.getStatus() + ",avgtime:"
				+ (sum / TIMESLEN) + ",maxtime:" + max);
		log.info(opDesc.getTaskId_OpTagIdx() + " : " + tbl.getTableName()
				+ " : emitMsgNum : " + emitMsgNum.get() + " : emitPackNum : "
				+ emitPackNum.get());
	}

	public Data2SinkDB(OpDesc7FS opDesc) {
		super(opDesc);
		this.emitMsgNum = new AtomicLong();
		this.emitPackNum = new AtomicLong();

		times = new int[TIMESLEN];
		Arrays.fill(times, 0);
		tbl = getOpDesc().getTable();
		insertSql = DataIOUtils.generateInsertSql(tbl);
		pools = DataIOUtils.prepareDBSource(tbl);
		columnTypes = DataIOUtils.processMetaData(pools, tbl);
		log.info("columnTypes : " + columnTypes);
		packager = new TimerPackager<ArrayList<String>, ArrayList<ArrayList<String>>>(
				1000,
				new Packager<ArrayList<String>, ArrayList<ArrayList<String>>>() {

					@Override
					public String getKey(ArrayList<String> t) {
						return "sql";
					}

					@Override
					public ArrayList<ArrayList<String>> newPackage(
							ArrayList<String> t) {
						return new ArrayList<ArrayList<String>>();
					}

					@Override
					public boolean pack(String key, ArrayList<String> t,
							ArrayList<ArrayList<String>> p) {
						p.add(t);
						return p.size() > 100;
					}

					long curremitnum = 0;

					@Override
					public void emit(String key,
							ArrayList<ArrayList<String>> p, boolean full) {
						PreparedStatement ps = null;
						Connection conn = null;
						long ctime = System.currentTimeMillis();
						try {
							conn = pools.getConnection();
							conn.setAutoCommit(false);
							ps = conn.prepareStatement(insertSql);
							for (int i = 0; i < p.size(); i++) {
								ArrayList<String> t = p.get(i);
								for (int j = 0; j < t.size(); j++) {
									ps.setObject(j + 1, t.get(j),
											columnTypes.get(j));
								}
								ps.addBatch();
							}
							ps.executeBatch();
							conn.commit();
							emitPackNum.incrementAndGet();
						} catch (Exception e) {
							if (shouldprint()) {
								log.info(TDBankUtils.getExceptionStack(e));
							}
							try {
								conn.rollback();
								conn.setAutoCommit(true);
								for (int i = 0; i < p.size(); i++) {
									ps.clearParameters();
									ArrayList<String> t = p.get(i);
									for (int j = 0; j < t.size(); j++) {
										ps.setObject(j + 1, t.get(j),
												columnTypes.get(j));
									}
									try {
										ps.execute();
									} catch (Exception e2) {
										log.info(t
												+ " : "
												+ TDBankUtils
														.getExceptionStack(e2));
									}
								}
							} catch (SQLException e1) {
								if (shouldprint()) {
									log.info(TDBankUtils.getExceptionStack(e1));
								}
							}
						} finally {
							try {
								if (conn != null) {
									conn.close();
								}
								if (ps != null) {
									ps.close();
								}
							} catch (SQLException e) {
								if (shouldprint()) {
									log.info(TDBankUtils.getExceptionStack(e));
								}
							}
						}
						times[(int) ((curremitnum++) % TIMESLEN)] = (int) (System
								.currentTimeMillis() - ctime);
					}
				});
	}

	@Override
	public boolean finalize(Object row, ObjectInspector objectInspector,
			ObjectInspector keyInspector, ObjectInspector attrsInspector,
			int opTagIdx) {
		try {
			StructObjectInspector soi = (StructObjectInspector) objectInspector;
			List<? extends StructField> fields = soi.getAllStructFieldRefs();
			List<Object> list = soi.getStructFieldsDataAsList(row);

			ArrayList<String> objs = new ArrayList<String>();
			for (int i = 0; i < fields.size(); i++) {
				ObjectInspector foi = fields.get(i).getFieldObjectInspector();
				Object f = (list == null ? null : list.get(i));
				Object sobj = ObjectInspectorUtils.copyToStandardJavaObject(f,
						foi);
				if (sobj == null) {
					objs.add(null);
				} else if (sobj instanceof byte[]) {
					objs.add(new String(Base64.encodeBase64((byte[]) sobj)));
				} else {
					objs.add(sobj.toString());
				}
			}
			if (objs.size() > 0) {
				packager.putTuple(objs);
				emitMsgNum.incrementAndGet();
			} else {
				log.warn("finalize obj in db is null : " + fields);
			}
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

}
