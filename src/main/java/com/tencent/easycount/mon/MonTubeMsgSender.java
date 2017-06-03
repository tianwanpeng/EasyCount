//package com.tencent.easycount.mon;
//
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.tencent.easycount.mon.MonStatusUpdater.MsgSender;
//import com.tencent.easycount.util.exec.TimerPackager;
//import com.tencent.easycount.util.status.TDBankUtils;
//
//public class MonTubeMsgSender implements MsgSender {
//	private static Logger log = LoggerFactory.getLogger(MonTubeMsgSender.class);
//
//	private TubeMessageSessionFactory messageSessionFactory = null;
//	private MessageProducer producer;
//	final private String tubeMaster;
//	final private int tubePort;
//	final private String addrList;
//	final private String topic;
//	final private String kvsplitter;
//
//	final private TimerPackager<byte[], TDMsg1> timerPackager;
//
//	public MonTubeMsgSender(String tubeMaster, int tubePort, String addrList,
//			final String topic, final String interfaceId, String kvsplitter) {
//		this.topic = topic;
//		this.tubeMaster = tubeMaster;
//		this.tubePort = tubePort;
//		this.addrList = addrList;
//		this.kvsplitter = kvsplitter;
//
//		timerPackager = new TimerPackager<byte[], TDMsg1>(1000,
//				new Packager<byte[], TDMsg1>() {
//
//					@Override
//					public String getKey(byte[] t) {
//						return "mon";
//					}
//
//					@Override
//					public TDMsg1 newPackage(byte[] t) {
//						return TDMsg1.newTDMsg();
//					}
//
//					@Override
//					public boolean pack(String key, byte[] t, TDMsg1 p) {
//						StringBuffer sb = new StringBuffer();
//						sb.append("m=0&iname=" + interfaceId).append("&dt=")
//								.append(System.currentTimeMillis());
//						return p.addMsg(sb.toString(), t);
//					}
//
//					@Override
//					public void emit(String key, TDMsg1 p, boolean full) {
//						try {
//							producer.sendMessage(
//									new Message(topic, p.buildArray()),
//									new SendMessageCallback() {
//										@Override
//										public void onMessageSent(
//												SendMessageCallBackResult result) {
//											if (result.isSuccess()) {
//											} else {
//												System.out.println(result
//														.getErrorMessage());
//											}
//										}
//
//										@Override
//										public void onException(Throwable e) {
//											log.error(TDBankUtils
//													.getExceptionStack(e));
//										}
//									}, 30, TimeUnit.SECONDS);
//						} catch (TubeClientException e) {
//							e.printStackTrace();
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//
//					}
//				});
//		this.start();
//
//		log.info("topic : " + topic);
//		log.info("tubeMaster : " + tubeMaster);
//		log.info("tubePort : " + tubePort);
//		log.info("new TubeProducer  ");
//	}
//
//	private final AtomicBoolean startted = new AtomicBoolean(false);
//
//	synchronized public void start() {
//		if (!startted.get()) {
//			while (true) {
//				try {
//					TubeClientConfig tubeClientConfig = new TubeClientConfig();
//					if (addrList != null) {
//						tubeClientConfig
//								.setMasterInfo(new MasterInfo(addrList));
//					} else {
//						tubeClientConfig.setMasterInfo(new MasterInfo(
//								tubeMaster, tubePort));
//					}
//
//					messageSessionFactory = new TubeMessageSessionFactory(
//							tubeClientConfig);
//					producer = messageSessionFactory.createProducer();
//					producer.publish(topic);
//					startted.set(true);
//					break;
//				} catch (TubeClientException e) {
//					log.error(TDBankUtils.getExceptionStack(e));
//				}
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					log.error(TDBankUtils.getExceptionStack(e));
//				}
//			}
//		}
//	}
//
//	public void restart() {
//		start();
//	}
//
//	public boolean started() {
//		return startted.get();
//	}
//
//	@Override
//	public void send(String key, int val) {
//		byte[] data = (key + kvsplitter + val).getBytes();
//		timerPackager.putTuple(data);
//	}
//
// }
