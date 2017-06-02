package com.tencent.trc.exec.io.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.trc.exec.io.Data2Sink;
import com.tencent.trc.exec.io.DataIOUtils;
import com.tencent.trc.metastore.Table;
import com.tencent.trc.metastore.TableUtils;
import com.tencent.trc.plan.logical.OpDesc7FS;
import com.tencent.trc.util.exec.TimerPackager;
import com.tencent.trc.util.exec.TimerPackager.Packager;

public class Data2SinkDBNormal extends Data2Sink {

	private static Logger log = LoggerFactory
			.getLogger(Data2SinkDBNormal.class);

	// private static ConnectionPool pools = null;
	// final private ArrayList<Integer> columnTypes;
	final private Table tbl;
	final private String insertSql;
	final private TimerPackager<ArrayList<String>, ArrayList<ArrayList<String>>> packager;
	final private int[] times;
	final private int TIMESLEN = 100;
	Connection conn = null;

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
	}

	public Data2SinkDBNormal(OpDesc7FS opDesc) {
		super(opDesc);
		times = new int[TIMESLEN];
		Arrays.fill(times, 0);
		tbl = getOpDesc().getTable();
		insertSql = DataIOUtils.generateInsertSqlNormal(tbl);
		conn = DataIOUtils.getConnNormal(tbl);
		final int packsize = TableUtils.getMysqlDBPacksize(tbl);
		final boolean useTransaction = TableUtils.getMysqlDBUseTransaction(tbl);

		log.info("insertSql : " + insertSql);
		// pools = DataIOUtils.prepareDBSource(tbl);
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
						return p.size() > packsize;
					}

					long curremitnum = 0;

					@Override
					public void emit(String key,
							ArrayList<ArrayList<String>> p, boolean full) {
						long ctime = System.currentTimeMillis();
						StringBuffer sqlsb = new StringBuffer();
						try {
							checkConn();
							if (useTransaction) {
								conn.setAutoCommit(false);
							}
							sqlsb.append(insertSql);

							for (int i = 0; i < p.size(); i++) {
								StringBuffer value = new StringBuffer();
								value.append("(");
								ArrayList<String> t = p.get(i);
								for (int j = 0; j < t.size(); j++) {
									if (j == 0) {
										value.append("\'").append(t.get(j))
												.append("\'");
									} else {
										value.append(",").append("\'")
												.append(t.get(j)).append("\'");
									}
								}
								value.append(")");
								if (i == 0) {
									sqlsb.append(value.toString());
								} else {
									sqlsb.append(",").append(value.toString());
								}
							}
							conn.createStatement().execute(sqlsb.toString());
							if (useTransaction) {
								conn.commit();
							}
						} catch (Exception e) {
							if (shouldprint()) {
								log.error("sql : " + sqlsb.toString());
								log.info(TDBankUtils.getExceptionStack(e));
							}
							try {
								if (useTransaction) {
									conn.rollback();
									conn.setAutoCommit(true);
								}
								for (int i = 0; i < p.size(); i++) {
									sqlsb.setLength(0);
									sqlsb.append(insertSql);
									sqlsb.append("(");
									ArrayList<String> t = p.get(i);
									for (int j = 0; j < t.size(); j++) {
										if (j == 0) {
											sqlsb.append("\'").append(t.get(j))
													.append("\'");
											;
										} else {
											sqlsb.append(",").append("\'")
													.append(t.get(j))
													.append("\'");
										}
									}
									sqlsb.append(")");

									conn.createStatement().execute(
											sqlsb.toString());

								}
							} catch (SQLException e1) {
								conn = null;
								if (shouldprint()) {
									log.error("sql : " + sqlsb.toString());
									log.info(TDBankUtils.getExceptionStack(e1));
								}
							}
						} finally {
						}
						times[(int) ((curremitnum++) % TIMESLEN)] = (int) (System
								.currentTimeMillis() - ctime);
					}

				});
	}

	private void checkConn() {
		if (conn == null) {
			conn = DataIOUtils.getConnNormal(tbl);
		}
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
				objs.add(null == sobj ? null : sobj.toString());
			}
			packager.putTuple(objs);
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
