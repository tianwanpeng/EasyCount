package com.tencent.easycount.exec.io.tube;

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

public class TubeProducer implements Closeable, StatusPrintable {
	private static Logger log = LoggerFactory.getLogger(TubeProducer.class);

	final private TrcConfiguration hconf;

	private TubeMessageSessionFactory messageSessionFactory = null;
	private MessageProducer producer;
	final private String tubeMaster;
	final private int tubePort;
	final private String tubeAddrList;
	private String topic;

	final private AtomicLong msgSend = new AtomicLong(0);
	final private AtomicLong priPrintId = new AtomicLong();

	private boolean monStatus = false;
	private MonStatusUpdater updater = null;
	private String wholeMsgSendKey = null;
	private String wholeMsgSendSuccKey = null;
	private String wholeMsgSendFailKey = null;

	@Override
	public void printStatus(int printId) {
		if (priPrintId.get() == printId) {
			return;
		}
		priPrintId.set(printId);
		log.info(topic + " : msgSend : " + msgSend);
	}

	public TubeProducer(TrcConfiguration hconf, String tubeMaster,
			int tubePort, String tubeAddrList, String topic, String taskId,
			String execId) {
		this.hconf = hconf;
		this.topic = topic;
		this.tubeMaster = tubeMaster;
		this.tubePort = tubePort;
		this.tubeAddrList = tubeAddrList;

		if (hconf.getBoolean("moniter.send.status", true)) {
			monStatus = true;
			wholeMsgSendKey = new StringBuffer()
					.append(MonKeys.TUBEMASTER + "=" + tubeMaster)
					.append("&" + MonKeys.EXECID + "=" + execId)
					.append("&" + MonKeys.TASKID + "=" + taskId)
					.append("&" + MonKeys.TOPIC + "=" + topic)
					.append("&" + MonKeys.MONTYPE + "=10").toString();
			wholeMsgSendSuccKey = new StringBuffer()
					.append(MonKeys.TUBEMASTER + "=" + tubeMaster)
					.append("&" + MonKeys.EXECID + "=" + execId)
					.append("&" + MonKeys.TASKID + "=" + taskId)
					.append("&" + MonKeys.TOPIC + "=" + topic)
					.append("&" + MonKeys.MONTYPE + "=11").toString();
			wholeMsgSendFailKey = new StringBuffer()
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
		if (!startted.get()) {
			while (true) {
				try {
					TubeClientConfig tubeClientConfig = new TubeClientConfig();
					if (tubeAddrList != null) {
						tubeClientConfig.setMasterInfo(new MasterInfo(
								tubeAddrList));
					} else {
						tubeClientConfig.setMasterInfo(new MasterInfo(
								tubeMaster, tubePort));
					}

					messageSessionFactory = new TubeMessageSessionFactory(
							tubeClientConfig);
					producer = messageSessionFactory.createProducer();
					producer.publish(topic);
					startted.set(true);
					break;
				} catch (TubeClientException e) {
					log.error(TDBankUtils.getExceptionStack(e));
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.error(TDBankUtils.getExceptionStack(e));
				}
			}
			if (monStatus) {
				updater = MonStatusUtils.getTubeMonStatusUpdater(hconf);
			}
		}
	}

	public boolean sendMsg(byte[] data) {
		try {
			producer.sendMessage(new Message(topic, data),
					new SendMessageCallback() {
						@Override
						public void onMessageSent(
								SendMessageCallBackResult result) {
							if (monStatus && updater != null) {
								try {
									updater.update(wholeMsgSendKey, 1);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							if (result.isSuccess()) {
								if (monStatus && updater != null) {
									try {
										updater.update(wholeMsgSendSuccKey, 1);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								if (monStatus && updater != null) {
									try {
										updater.update(wholeMsgSendFailKey, 1);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}

						@Override
						public void onException(Throwable e) {
							log.error(TDBankUtils.getExceptionStack(e));
						}
					}, 30, TimeUnit.SECONDS);
		} catch (TubeClientException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		msgSend.incrementAndGet();
		return true;

	}

	@Override
	public void close() throws IOException {
		try {
			producer.shutdown();
			producer = null;
			startted.set(false);
		} catch (TubeClientException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}
	}

	public void restart() {
		start();
	}

	public boolean started() {
		return startted.get();
	}

}
