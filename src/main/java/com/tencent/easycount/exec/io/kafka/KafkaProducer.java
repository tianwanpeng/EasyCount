package com.tencent.easycount.exec.io.kafka;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.conf.TrcConfiguration;
import com.tencent.easycount.mon.MonKeys;
import com.tencent.easycount.mon.MonStatusUpdater;
import com.tencent.easycount.mon.MonStatusUtils;
import com.tencent.easycount.util.status.StatusPrintable;
import com.tencent.easycount.util.status.TDBankUtils;

public class KafkaProducer implements Closeable, StatusPrintable {
	private static Logger log = LoggerFactory.getLogger(KafkaProducer.class);

	final private TrcConfiguration hconf;

	private TubeMessageSessionFactory messageSessionFactory = null;
	private MessageProducer producer;
	final private String tubeMaster;
	final private int tubePort;
	final private String tubeAddrList;
	private final String topic;

	final private AtomicLong msgSend = new AtomicLong(0);
	final private AtomicLong priPrintId = new AtomicLong();

	private boolean monStatus = false;
	private MonStatusUpdater updater = null;
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

	public KafkaProducer(final TrcConfiguration hconf, final String tubeMaster,
			final int tubePort, final String tubeAddrList, final String topic,
			final String taskId, final String execId) {
		this.hconf = hconf;
		this.topic = topic;
		this.tubeMaster = tubeMaster;
		this.tubePort = tubePort;
		this.tubeAddrList = tubeAddrList;

		if (hconf.getBoolean("moniter.send.status", true)) {
			this.monStatus = true;
			this.wholeMsgSendKey = new StringBuffer()
			.append(MonKeys.TUBEMASTER + "=" + tubeMaster)
			.append("&" + MonKeys.EXECID + "=" + execId)
			.append("&" + MonKeys.TASKID + "=" + taskId)
			.append("&" + MonKeys.TOPIC + "=" + topic)
			.append("&" + MonKeys.MONTYPE + "=10").toString();
			this.wholeMsgSendSuccKey = new StringBuffer()
			.append(MonKeys.TUBEMASTER + "=" + tubeMaster)
			.append("&" + MonKeys.EXECID + "=" + execId)
			.append("&" + MonKeys.TASKID + "=" + taskId)
			.append("&" + MonKeys.TOPIC + "=" + topic)
			.append("&" + MonKeys.MONTYPE + "=11").toString();
			this.wholeMsgSendFailKey = new StringBuffer()
			.append(MonKeys.TUBEMASTER + "=" + tubeMaster)
			.append("&" + MonKeys.EXECID + "=" + execId)
			.append("&" + MonKeys.TASKID + "=" + taskId)
			.append("&" + MonKeys.TOPIC + "=" + topic)
			.append("&" + MonKeys.MONTYPE + "=12").toString();
		}

		log.info("topic : " + topic);
		log.info("tubeMaster : " + tubeMaster);
		log.info("tubePort : " + tubePort);
		log.info("new TubeProducer  ");
	}

	private final AtomicBoolean startted = new AtomicBoolean(false);

	synchronized public void start() {
		if (!this.startted.get()) {
			while (true) {
				try {
					final TubeClientConfig tubeClientConfig = new TubeClientConfig();
					if (this.tubeAddrList != null) {
						tubeClientConfig.setMasterInfo(new MasterInfo(
								this.tubeAddrList));
					} else {
						tubeClientConfig.setMasterInfo(new MasterInfo(
								this.tubeMaster, this.tubePort));
					}

					this.messageSessionFactory = new TubeMessageSessionFactory(
							tubeClientConfig);
					this.producer = this.messageSessionFactory.createProducer();
					this.producer.publish(this.topic);
					this.startted.set(true);
					break;
				} catch (final TubeClientException e) {
					log.error(TDBankUtils.getExceptionStack(e));
				}
				try {
					Thread.sleep(1000);
				} catch (final InterruptedException e) {
					log.error(TDBankUtils.getExceptionStack(e));
				}
			}
			if (this.monStatus) {
				this.updater = MonStatusUtils
						.getTubeMonStatusUpdater(this.hconf);
			}
		}
	}

	public boolean sendMsg(final byte[] data) {
		try {
			this.producer.sendMessage(new Message(this.topic, data),
					new SendMessageCallback() {
				@Override
				public void onMessageSent(
						final SendMessageCallBackResult result) {
					if (KafkaProducer.this.monStatus
									&& (KafkaProducer.this.updater != null)) {
						try {
							KafkaProducer.this.updater.update(
											KafkaProducer.this.wholeMsgSendKey,
											1);
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}
					if (result.isSuccess()) {
						if (KafkaProducer.this.monStatus
										&& (KafkaProducer.this.updater != null)) {
							try {
								KafkaProducer.this.updater
												.update(KafkaProducer.this.wholeMsgSendSuccKey,
														1);
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						if (KafkaProducer.this.monStatus
										&& (KafkaProducer.this.updater != null)) {
							try {
								KafkaProducer.this.updater
												.update(KafkaProducer.this.wholeMsgSendFailKey,
														1);
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}
					}
				}

				@Override
				public void onException(final Throwable e) {
					log.error(TDBankUtils.getExceptionStack(e));
				}
			}, 30, TimeUnit.SECONDS);
		} catch (final TubeClientException e) {
			e.printStackTrace();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		this.msgSend.incrementAndGet();
		return true;

	}

	@Override
	public void close() throws IOException {
		try {
			this.producer.shutdown();
			this.producer = null;
			this.startted.set(false);
		} catch (final TubeClientException e) {
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
