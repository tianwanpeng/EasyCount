package com.tencent.easycount.exec.io.tube;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.lazy.LazyStruct;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazySimpleStructObjectInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.conf.TrcConfiguration;
import com.tencent.easycount.exec.physical.Task.SOCallBack;
import com.tencent.easycount.metastore.TableUtils;
import com.tencent.easycount.mon.MonKeys;
import com.tencent.easycount.mon.MonStatusUpdater;
import com.tencent.easycount.mon.MonStatusUtils;
import com.tencent.easycount.util.status.StatusPrintable;
import com.tencent.easycount.util.status.TDBankUtils;

public class TubeConsumer implements Closeable, StatusPrintable, Serializable {
	private static final long serialVersionUID = -3241711258369015166L;

	private static Logger log = LoggerFactory.getLogger(TubeConsumer.class);

	final private TrcConfiguration hconf;

	private TubeMessageSessionFactory messageSessionFactory = null;
	private MessageConsumer messageConsumer;
	private ConsumerConfig consumerConfig;
	private String topic;

	private boolean monStatus = false;
	private MonStatusUpdater updater = null;
	private String wholeMsgReceivedKey = null;
	private String wholeValidMsgReceivedKey = null;
	private String wholeValidMsgReceivedIIDKey = null;

	private OnlineConfig onlineconfig;
	private ConcurrentHashMap<String, Data1SourceTube> interfaceId2Data1Source = null;
	private ConcurrentHashMap<String, ByteArrayRef> interfaceId2ByteArrayRef = null;
	private ConcurrentHashMap<String, LazyStruct> interfaceId2LazyStruct = null;

	private HashMap<String, Byte> interfaceId2FieldSeperator;
	private HashMap<String, Byte> interfaceId2ListSeperator;
	private HashMap<String, Byte> interfaceId2MapSeperator;

	private HashMap<String, AtomicLong> interfaceId2MsgReceived;
	private long wholeMsgReceived = 0;
	private long wholeValidMsgReceived = 0;

	private AtomicLong priPrintId;
	private final String tubeMaster;
	private final int tubePort;
	final private String taskId;

	@Override
	public void printStatus(int printId) {
		if (priPrintId.get() == printId) {
			return;
		}
		priPrintId.set(printId);
		log.info(taskId + " : " + topic + " : msgReceived : "
				+ interfaceId2MsgReceived);
		log.info(taskId + " : " + topic + " : wholeValidMsgReceived : "
				+ wholeValidMsgReceived + " : wholeMsgReceived : "
				+ wholeMsgReceived);
	}

	public TubeConsumer(TrcConfiguration hconf, String tubeMaster,
			int tubePort, String addrList, String topic, String taskId,
			String execId) {
		this.hconf = hconf;
		this.topic = topic;
		this.tubeMaster = tubeMaster;
		this.tubePort = tubePort;
		this.taskId = taskId;
		String groupname = hconf.get("consumergroup") + "_" + topic;

		if (hconf.getBoolean("moniter.send.status", true)) {
			monStatus = true;
			wholeMsgReceivedKey = new StringBuffer()
					.append(MonKeys.TUBEMASTER + "=" + tubeMaster)
					.append("&" + MonKeys.EXECID + "=" + execId)
					.append("&" + MonKeys.TASKID + "=" + taskId)
					.append("&" + MonKeys.TOPIC + "=" + topic)
					.append("&" + MonKeys.CONSUMERGROUP + "=" + groupname)
					.append("&" + MonKeys.MONTYPE + "=0").toString();
			wholeValidMsgReceivedKey = new StringBuffer()
					.append(MonKeys.TUBEMASTER + "=" + tubeMaster)
					.append("&" + MonKeys.EXECID + "=" + execId)
					.append("&" + MonKeys.TASKID + "=" + taskId)
					.append("&" + MonKeys.TOPIC + "=" + topic)
					.append("&" + MonKeys.CONSUMERGROUP + "=" + groupname)
					.append("&" + MonKeys.MONTYPE + "=1").toString();
			wholeValidMsgReceivedIIDKey = new StringBuffer()
					.append(MonKeys.TUBEMASTER + "=" + tubeMaster)
					.append("&" + MonKeys.EXECID + "=" + execId)
					.append("&" + MonKeys.TASKID + "=" + taskId)
					.append("&" + MonKeys.TOPIC + "=" + topic)
					.append("&" + MonKeys.CONSUMERGROUP + "=" + groupname)
					.append("&" + MonKeys.MONTYPE + "=2").toString();
		}

		this.onlineconfig = new OnlineConfig();
		interfaceId2Data1Source = new ConcurrentHashMap<String, Data1SourceTube>();
		interfaceId2ByteArrayRef = new ConcurrentHashMap<String, ByteArrayRef>();
		interfaceId2LazyStruct = new ConcurrentHashMap<String, LazyStruct>();
		priPrintId = new AtomicLong(0);
		interfaceId2MsgReceived = new HashMap<String, AtomicLong>();

		interfaceId2FieldSeperator = new HashMap<String, Byte>();
		interfaceId2ListSeperator = new HashMap<String, Byte>();
		interfaceId2MapSeperator = new HashMap<String, Byte>();

		try {

			TubeClientConfig tubeClientConfig = new TubeClientConfig();
			if (addrList != null) {
				tubeClientConfig.setMasterInfo(new MasterInfo(addrList));
			} else {
				tubeClientConfig.setMasterInfo(new MasterInfo(tubeMaster,
						tubePort));
			}

			messageSessionFactory = new TubeMessageSessionFactory(
					tubeClientConfig);

			consumerConfig = new ConsumerConfig(groupname);
			consumerConfig.setMaxDelayFetchTimeInMills(50);
			consumerConfig.setRecoverMessageIntervalInMills(Long.MAX_VALUE);
			consumerConfig.setRecoverThreadCount(1);
			consumerConfig.setFetchRunnerCount(hconf.getInt(
					"tube.fetchrunner.count", 3));
			boolean setmax = hconf.getBoolean(
					"ConsumeFromMaxOffset".toLowerCase(), true);
			if (setmax) {
				consumerConfig.setConsumeFromMaxOffset(true);
			}

			log.info("topic : " + topic);
			log.info("tubeMaster : " + tubeMaster);
			log.info("tubePort : " + tubePort);
			log.info("groupname : " + groupname);
		} catch (Exception e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}
	}

	private final AtomicBoolean startted = new AtomicBoolean(false);

	synchronized public void start() {
		if (!startted.get()) {
			try {
				messageConsumer = messageSessionFactory
						.createConsumer(consumerConfig);
				messageConsumer.subscribe(topic, new TDBankMsgListener())
						.completeSubscribe();
				startted.set(true);
				log.info("consumer started : " + tubeMaster + "-" + tubePort);
			} catch (TubeClientException e) {
				e.printStackTrace();
				log.error(TDBankUtils.getExceptionStack(e));
			}
			if (monStatus) {
				updater = MonStatusUtils.getTubeMonStatusUpdater(hconf);
			}
		}
	}

	public void register(Data1SourceTube data1Source, String tableInterfaceId,
			Table tbl) {
		// rigister interface info first
		InterfaceInfo interfaceInfo = new InterfaceInfo(tableInterfaceId,
				tableInterfaceId, false);
		ArrayList<Field> fields = data1Source.getOpDesc().getTable()
				.getFields();

		for (int j = 0; j < fields.size(); j++) {
			Field field = fields.get(j);
			interfaceInfo.fields
					.add(new com.tencent.tdbank.mc.sorter.onlineconfig.ISorterConfig.Fields(
							field.getColumnName(), field.getType()
									.getTypeName(), field.getColumnName()));
			interfaceInfo.fieldsName2index.put(field.getColumnName(), j);
		}
		onlineconfig.configAttrs.putAll(data1Source.getOpDesc().getTable()
				.getTblAttrs());
		onlineconfig.interfaceId2Info.put(tableInterfaceId, interfaceInfo);

		interfaceId2MsgReceived.put(tableInterfaceId, new AtomicLong());
		interfaceId2Data1Source.put(tableInterfaceId, data1Source);
		interfaceId2ByteArrayRef.put(tableInterfaceId, new ByteArrayRef());
		interfaceId2LazyStruct.put(
				tableInterfaceId,
				new LazyStruct((LazySimpleStructObjectInspector) data1Source
						.getObjectInspector()));
		interfaceId2FieldSeperator.put(tableInterfaceId,
				(byte) TableUtils.getFieldSpliter(tbl));
		interfaceId2ListSeperator.put(tableInterfaceId,
				(byte) TableUtils.getListSpliter(tbl));
		interfaceId2MapSeperator.put(tableInterfaceId,
				(byte) TableUtils.getMapSpliter(tbl));
		log.info("consumer register a interfaceId : " + tubeMaster + "-"
				+ tubePort + "-" + tableInterfaceId);
	}

	private class TDBankMsgListener implements MessageListener {

		@Override
		public void recieveMessages(Message message) {
			TDMsg1 tdmsg = TDMsg1.parseFrom(message.getData());
			if (tdmsg != null) {
				for (String attr : tdmsg.getAttrs()) {
					Map<String, String> attrs = TDBankUtils.parseAttr(attr);

					String m = attrs.get("m");
					if (m == null) {
						continue;
					}
					int magic = Integer.valueOf(m);

					MsgType msgtype = getMsgType(attrs);
					if (magic == 0 && msgtype != MsgType.PACKED
							&& msgtype != MsgType.PBPACKED) {
						continue;
					}

					IdTimeParserInterface parser = PluginUtils.idtimeparsers
							.get(magic);
					if (null == parser) {
						continue;
					}

					Iterator<byte[]> it = tdmsg.getIterator(attr);
					if (it != null) {
						while (it.hasNext()) {
							byte[] data = it.next();

							ArrayList<Integer> poss = new ArrayList<Integer>(20);

							poss.add(-1);
							for (int i = 0; i < data.length; i++) {
								if (data[i] == '\n') {
									poss.add(i);
								}
							}
							poss.add(data.length);

							for (int i = 1; i < poss.size(); i++) {
								byte[] data1 = Arrays.copyOfRange(data,
										poss.get(i - 1) + 1, poss.get(i));
								IdTimeData itd = parser.parse(attrs, data1,
										onlineconfig);
								wholeMsgReceived++;
								if (monStatus && updater != null) {
									try {
										updater.update(wholeMsgReceivedKey, 1);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								if (itd.getId() != null
										&& interfaceId2Data1Source
												.containsKey(itd.getId())) {
									sendData(itd, attr);
									interfaceId2MsgReceived.get(itd.getId())
											.incrementAndGet();
									wholeValidMsgReceived++;
									if (monStatus && updater != null) {
										try {
											updater.update(
													wholeValidMsgReceivedKey, 1);
											updater.update(
													wholeValidMsgReceivedIIDKey
															+ "&"
															+ MonKeys.INTERFACEID
															+ "=" + itd.getId(),
													1);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
				}
			}
		}

		synchronized private void sendData(IdTimeData itd, String attr) {
			try {
				String iid = itd.getId();
				ByteArrayRef byteref = interfaceId2ByteArrayRef.get(iid);
				ByteBuffer bf = itd.getData();
				byte[] attrbytes = attr.getBytes();
				for (int i = 0; i < attrbytes.length; i++) {
					if (attrbytes[i] == '&') {
						attrbytes[i] = interfaceId2ListSeperator.get(iid);
					} else if (attrbytes[i] == '=') {
						attrbytes[i] = interfaceId2MapSeperator.get(iid);
					}
				}
				byte[] data = new byte[bf.remaining() + 1 + attrbytes.length];
				System.arraycopy(attrbytes, 0, data, 0, attrbytes.length);
				data[attrbytes.length] = interfaceId2FieldSeperator.get(iid);
				System.arraycopy(bf.array(), bf.position(), data,
						attrbytes.length + 1, bf.remaining());
				byteref.setData(data);
				LazyStruct lstruct = interfaceId2LazyStruct.get(iid);
				lstruct.init(byteref, 0, data.length);
				interfaceId2Data1Source.get(iid).emit(lstruct,
						new SOCallBack() {
							CountDownLatch cdl = new CountDownLatch(1);

							@Override
							public void finish() {
								cdl.countDown();
							}

							@Override
							public void await() throws InterruptedException {
								cdl.await();
							}
						});
			} catch (Exception e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
		}

		private MsgType getMsgType(Map<String, String> attrs) {
			String ck = attrs.get("ck");
			if (ck == null) {
				if ("pb".equals(attrs.get("mt"))) {
					return MsgType.PBPACKED;
				}
				return MsgType.PACKED;
			} else if ("0".equals(ck)) {
				return MsgType.CHECK;
			} else if ("1".equals(ck)) {
				return MsgType.STOPPED;
			}
			return MsgType.PACKED;
		}

		@Override
		public Executor getExecutor() {
			// there is a bug not used right now .... TODO
			return null;
		}

		@Override
		public void stop() {

		}

	}

	@Override
	public void close() throws IOException {
		try {
			if (messageConsumer != null) {
				log.info("begin to close consumer : "
						+ (tubeMaster + "-" + tubePort + "-" + topic));
				messageConsumer.shutdown();
				messageConsumer = null;
				log.info("consumer closed : "
						+ (tubeMaster + "-" + tubePort + "-" + topic));

			}
			startted.set(false);
		} catch (Exception e) {
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
