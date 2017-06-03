package com.tencent.easycount.exec.io.db;

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

import com.tencent.easycount.exec.io.Data2Sink;
import com.tencent.easycount.exec.io.DataIOUtils;
import com.tencent.easycount.metastore.Table;
import com.tencent.easycount.plan.logical.OpDesc7FS;
import com.tencent.easycount.util.exec.TimerPackager;
import com.tencent.easycount.util.exec.TimerPackager.Packager;
import com.tencent.easycount.util.status.TDBankUtils;

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
	public void printStatus(final int printId) {
		int max = 0;
		int sum = 0;
		for (int i = 0; i < this.times.length; i++) {
			sum += this.times[i];
			max = Math.max(max, this.times[i]);
		}
		log.info("packager:" + this.packager.getStatus() + ",avgtime:"
				+ (sum / this.TIMESLEN) + ",maxtime:" + max);
		log.info(this.opDesc.getTaskId_OpTagIdx() + " : "
				+ this.tbl.getTableName() + " : emitMsgNum : "
				+ this.emitMsgNum.get() + " : emitPackNum : "
				+ this.emitPackNum.get());
	}

	public Data2SinkDB(final OpDesc7FS opDesc) {
		super(opDesc);
		this.emitMsgNum = new AtomicLong();
		this.emitPackNum = new AtomicLong();

		this.times = new int[this.TIMESLEN];
		Arrays.fill(this.times, 0);
		this.tbl = getOpDesc().getTable();
		this.insertSql = DataIOUtils.generateInsertSql(this.tbl);
		pools = DataIOUtils.prepareDBSource(this.tbl);
		this.columnTypes = DataIOUtils.processMetaData(pools, this.tbl);
		log.info("columnTypes : " + this.columnTypes);
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
						return p.size() > 100;
					}

					long curremitnum = 0;

					@Override
					public void emit(final String key,
							final ArrayList<ArrayList<String>> p,
							final boolean full) {
						PreparedStatement ps = null;
						Connection conn = null;
						final long ctime = System.currentTimeMillis();
						try {
							conn = pools.getConnection();
							conn.setAutoCommit(false);
							ps = conn
									.prepareStatement(Data2SinkDB.this.insertSql);
							for (int i = 0; i < p.size(); i++) {
								final ArrayList<String> t = p.get(i);
								for (int j = 0; j < t.size(); j++) {
									ps.setObject(j + 1, t.get(j),
											Data2SinkDB.this.columnTypes.get(j));
								}
								ps.addBatch();
							}
							ps.executeBatch();
							conn.commit();
							Data2SinkDB.this.emitPackNum.incrementAndGet();
						} catch (final Exception e) {
							if (shouldprint()) {
								log.info(TDBankUtils.getExceptionStack(e));
							}
							try {
								conn.rollback();
								conn.setAutoCommit(true);
								for (int i = 0; i < p.size(); i++) {
									ps.clearParameters();
									final ArrayList<String> t = p.get(i);
									for (int j = 0; j < t.size(); j++) {
										ps.setObject(j + 1, t.get(j),
												Data2SinkDB.this.columnTypes
														.get(j));
									}
									try {
										ps.execute();
									} catch (final Exception e2) {
										log.info(t
												+ " : "
												+ TDBankUtils
												.getExceptionStack(e2));
									}
								}
							} catch (final SQLException e1) {
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
							} catch (final SQLException e) {
								if (shouldprint()) {
									log.info(TDBankUtils.getExceptionStack(e));
								}
							}
						}
						Data2SinkDB.this.times[(int) ((this.curremitnum++) % Data2SinkDB.this.TIMESLEN)] = (int) (System
								.currentTimeMillis() - ctime);
					}
				});
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
				if (sobj == null) {
					objs.add(null);
				} else if (sobj instanceof byte[]) {
					objs.add(new String(Base64.encodeBase64((byte[]) sobj)));
				} else {
					objs.add(sobj.toString());
				}
			}
			if (objs.size() > 0) {
				this.packager.putTuple(objs);
				this.emitMsgNum.incrementAndGet();
			} else {
				log.warn("finalize obj in db is null : " + fields);
			}
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
