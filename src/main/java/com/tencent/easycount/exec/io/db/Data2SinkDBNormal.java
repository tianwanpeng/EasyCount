package com.tencent.easycount.exec.io.db;

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

import com.tencent.easycount.exec.io.Data2Sink;
import com.tencent.easycount.exec.io.DataIOUtils;
import com.tencent.easycount.metastore.Table;
import com.tencent.easycount.metastore.TableUtils;
import com.tencent.easycount.plan.logical.OpDesc7FS;
import com.tencent.easycount.util.exec.TimerPackager;
import com.tencent.easycount.util.exec.TimerPackager.Packager;
import com.tencent.easycount.util.status.TDBankUtils;

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
	public void printStatus(final int printId) {
		int max = 0;
		int sum = 0;
		for (int i = 0; i < this.times.length; i++) {
			sum += this.times[i];
			max = Math.max(max, this.times[i]);
		}
		log.info("packager:" + this.packager.getStatus() + ",avgtime:"
				+ (sum / this.TIMESLEN) + ",maxtime:" + max);
	}

	public Data2SinkDBNormal(final OpDesc7FS opDesc) {
		super(opDesc);
		this.times = new int[this.TIMESLEN];
		Arrays.fill(this.times, 0);
		this.tbl = getOpDesc().getTable();
		this.insertSql = DataIOUtils.generateInsertSqlNormal(this.tbl);
		this.conn = DataIOUtils.getConnNormal(this.tbl);
		final int packsize = TableUtils.getMysqlDBPacksize(this.tbl);
		final boolean useTransaction = TableUtils
				.getMysqlDBUseTransaction(this.tbl);

		log.info("insertSql : " + this.insertSql);
		// pools = DataIOUtils.prepareDBSource(tbl);
		this.packager = new TimerPackager<ArrayList<String>, ArrayList<ArrayList<String>>>(
				1000,
				new Packager<ArrayList<String>, ArrayList<ArrayList<String>>>() {

					@Override
					public String getKey(final ArrayList<String> t) {
						return "sql";
					}

					@Override
					public ArrayList<ArrayList<String>> newPackage(
							final ArrayList<String> t) {
						return new ArrayList<ArrayList<String>>();
					}

					@Override
					public boolean pack(final String key,
							final ArrayList<String> t,
							final ArrayList<ArrayList<String>> p) {
						p.add(t);
						return p.size() > packsize;
					}

					long curremitnum = 0;

					@Override
					public void emit(final String key,
							final ArrayList<ArrayList<String>> p,
							final boolean full) {
						final long ctime = System.currentTimeMillis();
						final StringBuffer sqlsb = new StringBuffer();
						try {
							checkConn();
							if (useTransaction) {
								Data2SinkDBNormal.this.conn
										.setAutoCommit(false);
							}
							sqlsb.append(Data2SinkDBNormal.this.insertSql);

							for (int i = 0; i < p.size(); i++) {
								final StringBuffer value = new StringBuffer();
								value.append("(");
								final ArrayList<String> t = p.get(i);
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
							Data2SinkDBNormal.this.conn.createStatement()
									.execute(sqlsb.toString());
							if (useTransaction) {
								Data2SinkDBNormal.this.conn.commit();
							}
						} catch (final Exception e) {
							if (shouldprint()) {
								log.error("sql : " + sqlsb.toString());
								log.info(TDBankUtils.getExceptionStack(e));
							}
							try {
								if (useTransaction) {
									Data2SinkDBNormal.this.conn.rollback();
									Data2SinkDBNormal.this.conn
											.setAutoCommit(true);
								}
								for (int i = 0; i < p.size(); i++) {
									sqlsb.setLength(0);
									sqlsb.append(Data2SinkDBNormal.this.insertSql);
									sqlsb.append("(");
									final ArrayList<String> t = p.get(i);
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

									Data2SinkDBNormal.this.conn
											.createStatement().execute(
													sqlsb.toString());

								}
							} catch (final SQLException e1) {
								Data2SinkDBNormal.this.conn = null;
								if (shouldprint()) {
									log.error("sql : " + sqlsb.toString());
									log.info(TDBankUtils.getExceptionStack(e1));
								}
							}
						} finally {
						}
						Data2SinkDBNormal.this.times[(int) ((this.curremitnum++) % Data2SinkDBNormal.this.TIMESLEN)] = (int) (System
								.currentTimeMillis() - ctime);
					}

				});
	}

	private void checkConn() {
		if (this.conn == null) {
			this.conn = DataIOUtils.getConnNormal(this.tbl);
		}
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

			final ArrayList<String> objs = new ArrayList<String>();
			for (int i = 0; i < fields.size(); i++) {
				final ObjectInspector foi = fields.get(i)
						.getFieldObjectInspector();
				final Object f = (list == null ? null : list.get(i));
				final Object sobj = ObjectInspectorUtils
						.copyToStandardJavaObject(f, foi);
				objs.add(null == sobj ? null : sobj.toString());
			}
			this.packager.putTuple(objs);
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

}
