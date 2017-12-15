package com.tencent.easycount.exec.io.kafka;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.lazy.LazyStruct;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazySimpleStructObjectInspector;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.conf.ECConfiguration;
import com.tencent.easycount.exec.physical.Task.SOCallBack;
import com.tencent.easycount.metastore.Table;
import com.tencent.easycount.metastore.TableUtils;
import com.tencent.easycount.mon.MonKeys;
import com.tencent.easycount.mon.MonStatusUpdater;
import com.tencent.easycount.mon.MonStatusUtils;
import com.tencent.easycount.util.status.StatusPrintable;
import com.tencent.easycount.util.status.TDBankUtils;

public class KafkaECConsumer implements Closeable, StatusPrintable,
Serializable {
	private static final long serialVersionUID = -3241711258369015166L;

	private static Logger log = LoggerFactory.getLogger(KafkaECConsumer.class);

	final private ECConfiguration hconf;

	private final KafkaConsumer<Integer, byte[]> consumer;
	private final String topic;

	private boolean monStatus = false;
	private MonStatusUpdater updater = null;
	private String wholeMsgReceivedKey = null;
	private String wholeValidMsgReceivedKey = null;
	private String wholeValidMsgReceivedIIDKey = null;

	private ConcurrentHashMap<String, Data1SourceKafka> interfaceId2Data1Source = null;
	private ConcurrentHashMap<String, ByteArrayRef> interfaceId2ByteArrayRef = null;
	private ConcurrentHashMap<String, LazyStruct> interfaceId2LazyStruct = null;

	private final HashMap<String, Byte> interfaceId2FieldSeperator;
	private final HashMap<String, Byte> interfaceId2ListSeperator;
	private final HashMap<String, Byte> interfaceId2MapSeperator;

	private final HashMap<String, AtomicLong> interfaceId2MsgReceived;
	private long wholeMsgReceived = 0;
	private long wholeValidMsgReceived = 0;

	private final AtomicLong priPrintId;
	private final String tubeMaster;
	private final int tubePort;
	final private String taskId;

	@Override
	public void printStatus(final int printId) {
		if (this.priPrintId.get() == printId) {
			return;
		}
		this.priPrintId.set(printId);
		log.info(this.taskId + " : " + this.topic + " : msgReceived : "
				+ this.interfaceId2MsgReceived);
		log.info(this.taskId + " : " + this.topic
				+ " : wholeValidMsgReceived : " + this.wholeValidMsgReceived
				+ " : wholeMsgReceived : " + this.wholeMsgReceived);
	}

	public static class KafkaProperties {
		public static final String TOPIC = "topic1";
		public static final String KAFKA_SERVER_URL = "localhost";
		public static final int KAFKA_SERVER_PORT = 9092;
		public static final int KAFKA_PRODUCER_BUFFER_SIZE = 64 * 1024;
		public static final int CONNECTION_TIMEOUT = 100000;
		public static final String TOPIC2 = "topic2";
		public static final String TOPIC3 = "topic3";
		public static final String CLIENT_ID = "SimpleConsumerDemoClient";

		private KafkaProperties() {
		}
	}

	public KafkaECConsumer(final ECConfiguration hconf,
			final String tubeMaster, final int tubePort, final String addrList,
			final String topic, final String taskId, final String execId) {
		this.hconf = hconf;
		this.topic = topic;
		this.tubeMaster = tubeMaster;
		this.tubePort = tubePort;
		this.taskId = taskId;
		final String groupname = hconf.get("consumergroup") + "_" + topic;

		final Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
				KafkaProperties.KAFKA_SERVER_URL + ":"
						+ KafkaProperties.KAFKA_SERVER_PORT);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.IntegerDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");

		this.consumer = new KafkaConsumer<>(props);

		if (hconf.getBoolean("moniter.send.status", true)) {
			this.monStatus = true;
			this.wholeMsgReceivedKey = new StringBuffer()
					.append(MonKeys.TUBEMASTER + "=" + tubeMaster)
					.append("&" + MonKeys.EXECID + "=" + execId)
					.append("&" + MonKeys.TASKID + "=" + taskId)
					.append("&" + MonKeys.TOPIC + "=" + topic)
					.append("&" + MonKeys.CONSUMERGROUP + "=" + groupname)
					.append("&" + MonKeys.MONTYPE + "=0").toString();
			this.wholeValidMsgReceivedKey = new StringBuffer()
					.append(MonKeys.TUBEMASTER + "=" + tubeMaster)
					.append("&" + MonKeys.EXECID + "=" + execId)
					.append("&" + MonKeys.TASKID + "=" + taskId)
					.append("&" + MonKeys.TOPIC + "=" + topic)
					.append("&" + MonKeys.CONSUMERGROUP + "=" + groupname)
					.append("&" + MonKeys.MONTYPE + "=1").toString();
			this.wholeValidMsgReceivedIIDKey = new StringBuffer()
					.append(MonKeys.TUBEMASTER + "=" + tubeMaster)
					.append("&" + MonKeys.EXECID + "=" + execId)
					.append("&" + MonKeys.TASKID + "=" + taskId)
					.append("&" + MonKeys.TOPIC + "=" + topic)
					.append("&" + MonKeys.CONSUMERGROUP + "=" + groupname)
					.append("&" + MonKeys.MONTYPE + "=2").toString();
		}

		this.interfaceId2Data1Source = new ConcurrentHashMap<String, Data1SourceKafka>();
		this.interfaceId2ByteArrayRef = new ConcurrentHashMap<String, ByteArrayRef>();
		this.interfaceId2LazyStruct = new ConcurrentHashMap<String, LazyStruct>();
		this.priPrintId = new AtomicLong(0);
		this.interfaceId2MsgReceived = new HashMap<String, AtomicLong>();

		this.interfaceId2FieldSeperator = new HashMap<String, Byte>();
		this.interfaceId2ListSeperator = new HashMap<String, Byte>();
		this.interfaceId2MapSeperator = new HashMap<String, Byte>();

	}

	private final AtomicBoolean startted = new AtomicBoolean(false);

	synchronized public void start() {
		if (!this.startted.get()) {
			try {
				this.consumer.subscribe(Collections.singletonList(this.topic));

				this.startted.set(true);

				new Thread() {
					@Override
					public void run() {
						while (true) {
							final ConsumerRecords<Integer, byte[]> records = KafkaECConsumer.this.consumer
									.poll(1000);
							for (final ConsumerRecord<Integer, byte[]> record : records) {
								process(record);
							}
						}
					};
				}.start();

				log.info("consumer started : " + this.tubeMaster + "-"
						+ this.tubePort);
			} catch (final Exception e) {
				e.printStackTrace();
				log.error(TDBankUtils.getExceptionStack(e));
			}
			if (this.monStatus) {
				this.updater = MonStatusUtils
						.getTubeMonStatusUpdater(this.hconf);
			}
		}
	}

	public void register(final Data1SourceKafka data1Source,
			final String tableInterfaceId, final Table tbl) {

		this.interfaceId2MsgReceived.put(tableInterfaceId, new AtomicLong());
		this.interfaceId2Data1Source.put(tableInterfaceId, data1Source);
		this.interfaceId2ByteArrayRef.put(tableInterfaceId, new ByteArrayRef());
		this.interfaceId2LazyStruct.put(
				tableInterfaceId,
				new LazyStruct((LazySimpleStructObjectInspector) data1Source
						.getObjectInspector()));
		this.interfaceId2FieldSeperator.put(tableInterfaceId,
				(byte) TableUtils.getFieldSpliter(tbl));
		this.interfaceId2ListSeperator.put(tableInterfaceId,
				(byte) TableUtils.getListSpliter(tbl));
		this.interfaceId2MapSeperator.put(tableInterfaceId,
				(byte) TableUtils.getMapSpliter(tbl));
		log.info("consumer register a interfaceId : " + this.tubeMaster + "-"
				+ this.tubePort + "-" + tableInterfaceId);
	}

	public void process(final ConsumerRecord<Integer, byte[]> rec) {
		this.wholeMsgReceived++;
		if (this.monStatus && (this.updater != null)) {
			this.updater.update(this.wholeMsgReceivedKey, 1);
		}

		if (this.interfaceId2Data1Source.containsKey(rec.key())) {
			sendData(rec);
			this.interfaceId2MsgReceived.get(rec.key()).incrementAndGet();
			this.wholeValidMsgReceived++;
			if (this.monStatus && (this.updater != null)) {
				this.updater.update(this.wholeValidMsgReceivedKey, 1);
				this.updater.update(this.wholeValidMsgReceivedIIDKey + "&"
						+ MonKeys.INTERFACEID + "=" + rec.key(), 1);
			}
		}

	}

	synchronized private void sendData(final ConsumerRecord<Integer, byte[]> rec) {
		try {
			final String iid = rec.key().toString();
			final ByteArrayRef byteref = this.interfaceId2ByteArrayRef.get(iid);
			final byte[] attrbytes = "".getBytes();// TODO
			for (int i = 0; i < attrbytes.length; i++) {
				if (attrbytes[i] == '&') {
					attrbytes[i] = this.interfaceId2ListSeperator.get(iid);
				} else if (attrbytes[i] == '=') {
					attrbytes[i] = this.interfaceId2MapSeperator.get(iid);
				}
			}
			final byte[] data = rec.value();
			byteref.setData(data);
			final LazyStruct lstruct = this.interfaceId2LazyStruct.get(iid);
			lstruct.init(byteref, 0, data.length);
			this.interfaceId2Data1Source.get(iid).emit(lstruct,
					new SOCallBack() {
				CountDownLatch cdl = new CountDownLatch(1);

				@Override
				public void finish() {
					this.cdl.countDown();
				}

				@Override
				public void await() throws InterruptedException {
					this.cdl.await();
				}
			});
		} catch (final Exception e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}
	}

	@Override
	public void close() throws IOException {
		try {
			if (this.consumer != null) {
				log.info("begin to close consumer : "
						+ (this.tubeMaster + "-" + this.tubePort + "-" + this.topic));
				this.consumer.close();
				log.info("consumer closed : "
						+ (this.tubeMaster + "-" + this.tubePort + "-" + this.topic));

			}
			this.startted.set(false);
		} catch (final Exception e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}
	}

	public void restart() {
		start();
	}

	public boolean started() {
		return this.startted.get();
	}

}
