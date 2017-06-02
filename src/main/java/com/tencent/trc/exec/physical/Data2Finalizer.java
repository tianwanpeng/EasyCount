package com.tencent.trc.exec.physical;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.io.Data2Sink;
import com.tencent.trc.exec.io.db.Data2SinkDB;
import com.tencent.trc.exec.io.db.Data2SinkDBNormal;
import com.tencent.trc.exec.io.hbase.Data2SinkHbase;
import com.tencent.trc.exec.io.inner.Data2SinkPrint;
import com.tencent.trc.exec.io.local.LocalModeUtils;
import com.tencent.trc.exec.io.redis.Data2SinkRedis;
import com.tencent.trc.exec.io.tde.Data2SinkTde;
import com.tencent.trc.exec.io.tube.Data2SinkTube;
import com.tencent.trc.exec.io.tube.TubeProducer;
import com.tencent.trc.exec.logical.Operator7FS;
import com.tencent.trc.exec.logical.Operator7FS.Finalized;
import com.tencent.trc.metastore.Table;
import com.tencent.trc.metastore.Table.TableType;
import com.tencent.trc.metastore.TableUtils;

public class Data2Finalizer implements Finalized {
	// private static Logger log =
	// LoggerFactory.getLogger(Data2Finalizer.class);

	final private HashMap<Integer, Data2Sink> tagId2Sinks;
	private TrcConfiguration hconf;
	final private ConcurrentHashMap<String, TubeProducer> tubeProducers = new ConcurrentHashMap<String, TubeProducer>();
	final private String taskId;
	final private String execId;

	@Override
	public void printStatus(int printId) {
		for (Data2Sink sink : tagId2Sinks.values()) {
			sink.printStatus(printId);
		}
	}

	public Data2Finalizer(TrcConfiguration hconf, String taskId, String execId) {
		this.hconf = hconf;
		this.taskId = taskId;
		this.execId = execId;
		this.tagId2Sinks = new HashMap<Integer, Data2Sink>();
	}

	public void start() {
		for (TubeProducer producer : tubeProducers.values()) {
			producer.start();
		}
	}

	public void addFsOp(Operator7FS fsop) {
		// if (hconf.getBoolean("localmode", false)
		// && fsop.getOpDesc().getTable().getTableType() != TableType.redis) {
		// this.tagId2Sinks.put(fsop.getOpDesc().getOpTagIdx(),
		// new Data2SinkTest(fsop.getOpDesc()));
		Table tbl = fsop.getOpDesc().getTable();
		if (tbl.getTableType() != TableType.print
				&& hconf.getBoolean("localmode", false)) {
			this.tagId2Sinks
					.put(fsop.getOpDesc().getOpTagIdx(),
							LocalModeUtils.generateLocalDataSink(tbl,
									fsop.getOpDesc()));
		} else {
			Data2Sink dataSink = null;
			if (tbl.getTableType() == TableType.print) {
				dataSink = new Data2SinkPrint(fsop.getOpDesc());
			} else if (tbl.getTableType() == TableType.tpg
					|| tbl.getTableType() == TableType.mysql) {
				boolean mysqlInsertModeNormal = TableUtils
						.getMysqlInsertMode(tbl);
				if (mysqlInsertModeNormal) {
					dataSink = new Data2SinkDBNormal(fsop.getOpDesc());
				} else {
					dataSink = new Data2SinkDB(fsop.getOpDesc());
				}
			} else if (tbl.getTableType() == TableType.tde) {
				if (TableUtils.getTdeWriteAsync(tbl)) {
					dataSink = new Data2SinkTde(fsop.getOpDesc(), true);
				} else {
					dataSink = new Data2SinkTde(fsop.getOpDesc(), false);
				}
			} else if (tbl.getTableType() == TableType.redis) {
				dataSink = new Data2SinkRedis(fsop.getOpDesc());
			} else if (tbl.getTableType() == TableType.hbase) {
				dataSink = new Data2SinkHbase(fsop.getOpDesc());
			} else if (tbl.getTableType() == TableType.tube) {
				String tubeMaster = TableUtils.getTableTubeMaster(fsop
						.getOpDesc().getTable());
				int tubePort = TableUtils.getTableTubePort(fsop.getOpDesc()
						.getTable());
				String tubeAddrList = TableUtils.getTableTubeAddrList(fsop
						.getOpDesc().getTable());

				String topic = TableUtils.getTableTopic(fsop.getOpDesc()
						.getTable());

				String producerid = tubeMaster + "-" + tubePort + "-" + topic;
				if (!tubeProducers.containsKey(producerid)) {
					tubeProducers.put(producerid, new TubeProducer(hconf,
							tubeMaster, tubePort, tubeAddrList, topic, taskId,
							execId));
				}

				dataSink = new Data2SinkTube(hconf, fsop.getOpDesc(),
						tubeProducers.get(producerid));
			}

			this.tagId2Sinks.put(fsop.getOpDesc().getOpTagIdx(), dataSink);
		}
		fsop.setFinalizer(this);
	}

	@Override
	public boolean finalize(Object row, ObjectInspector objectInspector,
			ObjectInspector keyInspector, ObjectInspector attrsInspector,
			int opTagIdx) {
		this.tagId2Sinks.get(opTagIdx).finalize(row, objectInspector,
				keyInspector, attrsInspector, opTagIdx);
		return true;
	}

	// @SuppressWarnings("rawtypes")
	// @Override
	// public boolean finalize(Object row, ObjectInspector objectInspector,
	// ExprNodeEvaluator keyEvaluator, ObjectInspector keyInspector,
	// ExprNodeEvaluator attrsEvaluator, ObjectInspector attrsInspector,
	// int opTagIdx) {
	// try {
	// this.tagId2Sinks.get(opTagIdx).finalize(row, objectInspector,
	// keyEvaluator, keyInspector, attrsEvaluator, attrsInspector,
	// opTagIdx);
	// } catch (Throwable e) {
	// log.warn(row + " : " + TDBankUtils.getExceptionStack(e));
	// return false;
	// }
	// return true;
	// }

	@Override
	public void close() throws IOException {
		for (Data2Sink sink : tagId2Sinks.values()) {
			sink.close();
		}
	}
}
