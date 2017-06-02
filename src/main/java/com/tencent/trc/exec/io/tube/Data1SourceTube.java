package com.tencent.trc.exec.io.tube;

import java.io.IOException;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.io.Data1Source;
import com.tencent.trc.exec.physical.Data1Generator;
import com.tencent.trc.exec.utils.OIUtils;
import com.tencent.trc.metastore.Table;
import com.tencent.trc.metastore.TableUtils;
import com.tencent.trc.plan.logical.OpDesc1TS;

public class Data1SourceTube extends Data1Source {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(Data1SourceTube.class);

	private ObjectInspector objectInspector;

	private final TubeConsumer consumer;

	@Override
	public void printStatus(int printId) {
		consumer.printStatus(printId);
	}

	public Data1SourceTube(String sourceId, OpDesc1TS opdesc,
			Data1Generator msgEmitter, TrcConfiguration hconf,
			TubeConsumer consumer) {
		super(sourceId, opdesc, msgEmitter);
		Table tbl = opdesc.getTable();
		objectInspector = OIUtils.createLazyStructInspector(tbl);
		this.consumer = consumer;

		String tableInterfaceId = TableUtils.getTableInterfaceId(opdesc
				.getTable());
		this.consumer.register(this, tableInterfaceId, tbl);
	}

	@Override
	public void start() {
		if (!consumer.started()) {
			consumer.start();
		}
	}

	long msgReceived = 0;

	@Override
	public ObjectInspector getObjectInspector() {
		return objectInspector;
	}

	@Override
	public void close() throws IOException {
		consumer.close();
	}

	@Override
	public void restart() throws Exception {
		consumer.start();
	}

}
