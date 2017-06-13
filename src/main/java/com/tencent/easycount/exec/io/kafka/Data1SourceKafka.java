package com.tencent.easycount.exec.io.kafka;

import java.io.IOException;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.conf.TrcConfiguration;
import com.tencent.easycount.exec.io.Data1Source;
import com.tencent.easycount.exec.physical.Data1Generator;
import com.tencent.easycount.exec.utils.OIUtils;
import com.tencent.easycount.metastore.Table;
import com.tencent.easycount.metastore.TableUtils;
import com.tencent.easycount.plan.logical.OpDesc1TS;

public class Data1SourceKafka extends Data1Source {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(Data1SourceKafka.class);

	private final ObjectInspector objectInspector;

	private final KafkaECConsumer consumer;

	@Override
	public void printStatus(final int printId) {
		this.consumer.printStatus(printId);
	}

	public Data1SourceKafka(final String sourceId, final OpDesc1TS opdesc,
			final Data1Generator msgEmitter, final TrcConfiguration hconf,
			final KafkaECConsumer consumer) {
		super(sourceId, opdesc, msgEmitter);
		final Table tbl = opdesc.getTable();
		this.objectInspector = OIUtils.createLazyStructInspector(tbl);
		this.consumer = consumer;

		final String tableInterfaceId = TableUtils.getTableInterfaceId(opdesc
				.getTable());
		this.consumer.register(this, tableInterfaceId, tbl);
	}

	@Override
	public void start() {
		if (!this.consumer.started()) {
			this.consumer.start();
		}
	}

	long msgReceived = 0;

	@Override
	public ObjectInspector getObjectInspector() {
		return this.objectInspector;
	}

	@Override
	public void close() throws IOException {
		this.consumer.close();
	}

	@Override
	public void restart() throws Exception {
		this.consumer.start();
	}

}
