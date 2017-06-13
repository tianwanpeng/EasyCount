package com.tencent.easycount.exec.io.kafka;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.conf.TrcConfiguration;
import com.tencent.easycount.exec.io.kafka.KafkaECConsumer.KafkaProperties;
import com.tencent.easycount.mon.MonKeys;
import com.tencent.easycount.mon.MonStatusUpdater;
import com.tencent.easycount.mon.MonStatusUtils;
import com.tencent.easycount.util.status.StatusPrintable;
import com.tencent.easycount.util.status.TDBankUtils;

public class KafkaECProducer implements Closeable, StatusPrintable {
	private static Logger log = LoggerFactory.getLogger(KafkaProducer.class);

	final private TrcConfiguration hconf;

	private KafkaProducer<String, byte[]> producer;
	private final String topic;

	final private AtomicLong msgSend = new AtomicLong(0);
	final private AtomicLong priPrintId = new AtomicLong();

	private final boolean isAsync;

	private boolean monStatus = false;
	private final MonStatusUpdater updater;
	private String wholeMsgSendKey = null;
	private String wholeMsgSendSuccKey = null;
	private String wholeMsgSendFailKey = null;

	@Override
	public void printStatus(final int printId) {
		if (this.priPrintId.get() == printId) {
			return;
		}
		this.priPrintId.set(printId);
		log.info(this.topic + " : msgSend : " + this.msgSend);
	}

	public KafkaECProducer(final TrcConfiguration hconf, final String topic,
			final String taskId, final String execId) {
		this.hconf = hconf;
		this.topic = topic;
		this.isAsync = hconf.getBoolean("kafka.send.isAsync", true);

		if (hconf.getBoolean("moniter.send.status", true)) {
			this.monStatus = true;
			this.wholeMsgSendKey = new StringBuffer()
			.append(MonKeys.EXECID + "=" + execId)
			.append("&" + MonKeys.TASKID + "=" + taskId)
			.append("&" + MonKeys.TOPIC + "=" + topic)
			.append("&" + MonKeys.MONTYPE + "=10").toString();
			this.wholeMsgSendSuccKey = new StringBuffer()
			.append(MonKeys.EXECID + "=" + execId)
			.append("&" + MonKeys.TASKID + "=" + taskId)
			.append("&" + MonKeys.TOPIC + "=" + topic)
			.append("&" + MonKeys.MONTYPE + "=11").toString();
			this.wholeMsgSendFailKey = new StringBuffer()
			.append(MonKeys.EXECID + "=" + execId)
			.append("&" + MonKeys.TASKID + "=" + taskId)
			.append("&" + MonKeys.TOPIC + "=" + topic)
			.append("&" + MonKeys.MONTYPE + "=12").toString();
		}

		if (this.monStatus) {
			this.updater = MonStatusUtils.getTubeMonStatusUpdater(this.hconf);
		} else {
			this.updater = null;
		}

		log.info("topic : " + topic);
		log.info("new TubeProducer  ");
	}

	private final AtomicBoolean startted = new AtomicBoolean(false);

	synchronized public void start() {

		final Properties props = new Properties();
		props.put("bootstrap.servers", KafkaProperties.KAFKA_SERVER_URL + ":"
				+ KafkaProperties.KAFKA_SERVER_PORT);
		props.put("client.id", "DemoProducer");
		props.put("key.serializer",
				"org.apache.kafka.common.serialization.IntegerSerializer");
		props.put("value.serializer",
				"org.apache.kafka.common.serialization.StringSerializer");
		this.producer = new KafkaProducer<>(props);

	}

	public boolean sendMsg(final String iid, final byte[] data) {

		final ProducerRecord<String, byte[]> srec = new ProducerRecord<>(
				this.topic, iid, data);

		if (this.isAsync) {
			this.producer.send(srec,
					new SendCallBack(System.currentTimeMillis(), iid, data));
		} else {
			try {
				this.producer.send(srec).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		this.msgSend.incrementAndGet();
		return true;

	}

	class SendCallBack implements Callback {

		@SuppressWarnings("unused")
		private final long startTime;
		@SuppressWarnings("unused")
		private final String key;
		@SuppressWarnings("unused")
		private final byte[] message;

		public SendCallBack(final long startTime, final String key,
				final byte[] message) {
			this.startTime = startTime;
			this.key = key;
			this.message = message;
		}

		/**
		 * A callback method the user can implement to provide asynchronous
		 * handling of request completion. This method will be called when the
		 * record sent to the server has been acknowledged. Exactly one of the
		 * arguments will be non-null.
		 *
		 * @param metadata
		 *            The metadata for the record that was sent (i.e. the
		 *            partition and offset). Null if an error occurred.
		 * @param exception
		 *            The exception thrown during processing of this record.
		 *            Null if no error occurred.
		 */
		@Override
		public void onCompletion(final RecordMetadata metadata,
				final Exception exception) {
			try {
				if (KafkaECProducer.this.monStatus
						&& (KafkaECProducer.this.updater != null)) {
					KafkaECProducer.this.updater.update(
							KafkaECProducer.this.wholeMsgSendKey, 1);
					if (exception == null) {
						KafkaECProducer.this.updater.update(
								KafkaECProducer.this.wholeMsgSendSuccKey, 1);
					} else {
						KafkaECProducer.this.updater.update(
								KafkaECProducer.this.wholeMsgSendFailKey, 1);
					}
				}

			} catch (final Exception e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void close() throws IOException {
		try {
			this.producer.close();
			this.producer = null;
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
