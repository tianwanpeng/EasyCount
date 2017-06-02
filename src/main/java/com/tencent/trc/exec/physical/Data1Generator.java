package com.tencent.trc.exec.physical;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.io.Data1Source;
import com.tencent.trc.exec.io.DataIOUtils;
import com.tencent.trc.exec.io.inner.Data1SourceInner;
import com.tencent.trc.exec.io.local.LocalModeUtils;
import com.tencent.trc.exec.io.tube.Data1SourceTube;
import com.tencent.trc.exec.io.tube.TubeConsumer;
import com.tencent.trc.exec.physical.Task.SOCallBack;
import com.tencent.trc.exec.physical.Task.SrcObject;
import com.tencent.trc.exec.physical.Task.TupleProcessor;
import com.tencent.trc.metastore.Table.TableType;
import com.tencent.trc.metastore.TableUtils;
import com.tencent.trc.plan.logical.OpDesc;
import com.tencent.trc.plan.logical.OpDesc1TS;
import com.tencent.trc.util.status.StatusPrintable;

public class Data1Generator implements Closeable, StatusPrintable {
	private static Logger log = LoggerFactory.getLogger(Data1Generator.class);
	final private ConcurrentHashMap<String, TubeConsumer> tubeConsumers;
	// final private ArrayList<Operator1TS> tsOps;
	final private ArrayList<Data1Source> opsources;
	final private HashMap<String, ObjectInspector> tagKey2OIs;

	// final private SynchronousQueue<SourceObject> objQueue;

	final private String taskName;
	final private String taskId;
	final private String execId;

	final TupleProcessor tupleProcessor;

	@Override
	public void printStatus(int printId) {
		for (Data1Source opsrc : opsources) {
			opsrc.printStatus(printId);
		}
	}

	public Data1Generator(TrcConfiguration hconf, ArrayList<OpDesc> tsOpDescs,
			String taskName, String taskId, String execId,
			TupleProcessor tupleProcessor) {
		// this.hconf = hconf;
		this.taskName = taskName;
		this.taskId = taskId;
		this.execId = execId;
		// this.objQueue = new SynchronousQueue<SourceObject>();
		// this.tsOps = tsOps;
		this.opsources = new ArrayList<Data1Source>();
		this.tagKey2OIs = new HashMap<String, ObjectInspector>();
		this.tubeConsumers = new ConcurrentHashMap<String, TubeConsumer>();
		this.tupleProcessor = tupleProcessor;

		boolean localmode = hconf.getBoolean("localmode", false);

		for (int i = 0; i < tsOpDescs.size(); i++) {
			OpDesc1TS opDesc = (OpDesc1TS) tsOpDescs.get(i);

			Data1Source data1Source = null;
			if (localmode
					&& opDesc.getTable().getTableType() != TableType.inner) {
				data1Source = LocalModeUtils.generateLocalDataSource(
						opDesc.getTaskId_OpTagIdx(), opDesc, this, hconf);
			} else {
				if (opDesc.isDimensionTable()) {
					data1Source = DataIOUtils.generateDimDataSource(
							opDesc.getTaskId_OpTagIdx(), opDesc, this);
					// } else if (hconf.getBoolean("testmode", false)) {
					// data1Source = new
					// Data1SourceTest(opDesc.getTaskId_OpTagIdx(),
					// opDesc, this, hconf);
				} else if (opDesc.getTable().getTableType() == TableType.inner) {
					data1Source = new Data1SourceInner(
							opDesc.getTaskId_OpTagIdx(), opDesc, this, hconf);
				} else if (opDesc.getTable().getTableType() == TableType.tube) {

					String tubeMaster = TableUtils.getTableTubeMaster(opDesc
							.getTable());
					int tubePort = TableUtils.getTableTubePort(opDesc
							.getTable());
					String tubeAddrList = TableUtils
							.getTableTubeAddrList(opDesc.getTable());

					String topic = TableUtils.getTableTopic(opDesc.getTable());

					String zkid = tubeMaster + "-" + tubePort + "-" + topic;
					if (!tubeConsumers.containsKey(zkid)) {
						tubeConsumers.put(zkid, new TubeConsumer(hconf,
								tubeMaster, tubePort, tubeAddrList, topic,
								taskId, execId));
						log.info("create a new consumer : " + zkid);
					}

					data1Source = new Data1SourceTube(
							opDesc.getTaskId_OpTagIdx(), opDesc, this, hconf,
							tubeConsumers.get(zkid));
				}
			}

			this.opsources.add(data1Source);
			this.tagKey2OIs.put(opDesc.getTaskId_OpTagIdx(),
					data1Source.getObjectInspector());

			log.info("generate datasource : " + i + " : "
					+ opDesc.getTable().toString());
		}
	}

	public synchronized void start() {
		for (Data1Source opsrc : opsources) {
			opsrc.start();
		}
	}

	/**
	 * there may be multi thread emit obj, but only one(may be) thread process
	 * them
	 * 
	 * @param attrs
	 */
	public boolean emit(String tagKey, Object obj, SOCallBack socb) {
		SrcObject so = new SrcObject(tagKey, obj, socb);
		try {
			int xxx = 0;
			while (!tupleProcessor.offer(so)) {
				log.warn(taskName + " : " + taskId + "-" + execId
						+ " : srcObject offer fail : " + so.toString()
						+ " for " + (xxx++) + " times");
			}
			// return after the obj been processed
			so.await();
		} catch (Throwable e) {
			log.error(TDBankUtils.getExceptionStack(e));
			return false;
		}
		return true;
	}

	@Override
	public void close() {
		log.info("begin to stop datagenerator : " + taskName + " : " + taskId
				+ "-" + execId);
		for (Data1Source opsrc : opsources) {
			try {
				opsrc.close();
			} catch (IOException e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
		}
		log.info("datagenerator stopped : " + taskName + " : " + taskId);

	}

	public void restart() {
		for (Data1Source opsrc : opsources) {
			try {
				opsrc.restart();
			} catch (Exception e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
		}
	}

	public HashMap<String, ObjectInspector> getTagKey2OIs() {
		return tagKey2OIs;
	}
}
