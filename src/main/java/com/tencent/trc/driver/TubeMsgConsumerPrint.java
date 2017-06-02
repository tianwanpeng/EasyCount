package com.tencent.trc.driver;

import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.tencent.tdbank.msg.TDMsg1;
import com.tencent.tube.Message;
import com.tencent.tube.client.TubeClientConfig;
import com.tencent.tube.client.TubeMessageSessionFactory;
import com.tencent.tube.client.consumer.ConsumerConfig;
import com.tencent.tube.client.consumer.MessageConsumer;
import com.tencent.tube.client.consumer.MessageListener;
import com.tencent.tube.cluster.MasterInfo;
import com.tencent.tube.exception.TubeClientException;

public class TubeMsgConsumerPrint {
	static AtomicInteger receivenum = new AtomicInteger(0);
	static AtomicInteger sendnum = new AtomicInteger();
	static AtomicInteger success = new AtomicInteger();
	static AtomicInteger fail = new AtomicInteger();
	static AtomicInteger except = new AtomicInteger();
	static AtomicLong time = new AtomicLong();
	static double starttime;
	static int[] cnt;
	volatile static int count = 0;
	static AtomicBoolean newfail = new AtomicBoolean(false);

	static int msgcnt = 0;

	static String topic = "test2";
	static boolean rand = true;
	static int msglen = 20;
	static int sleeptime = 0;
	static boolean async = true;
	static long timeout = 500;
	static String zkroot = "/meta";
	static int runnum = 100;
	static boolean multiproducer = false;
	static String zkip = "tl-zk-test1:2181,tl-zk-test2:2181,tl-zk-test3:2181,tl-zk-test4:2181,tl-zk-test5:2181";
	static int sendnumber = 1000000;
	static int daynum = 30;
	static int interfacenum = 1;
	static int startid = 10000;
	static boolean compress = true;
	static boolean printattr = true;
	static boolean dayonly = true;
	static String groupname = "testgroup";
	static String checkid = "10009";
	static String checktime = "20120919";
	static long offset = -1;
	static long timediff_s = 3600;
	static boolean setmax = false;
	static long printnum = 10000;
	static String tubeMaster = "";
	static int tubePort = 8609;

	static protected long lasttime = System.currentTimeMillis();

	public static void main(String[] args) throws TubeClientException {

		for (int i = 0; i < args.length; i++) {
			String[] strs = args[i].split("=");
			if (strs[0].equals("topic")) {
				topic = strs[1];
			} else if (strs[0].equals("rand")) {
				rand = strs[1].equals("true") ? true : false;
			} else if (strs[0].equals("msglen")) {
				msglen = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("sleeptime")) {
				sleeptime = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("async")) {
				async = strs[1].equals("true");
			} else if (strs[0].equals("setmax")) {
				setmax = strs[1].equals("true");
			} else if (strs[0].equals("timeout")) {
				timeout = Long.parseLong(strs[1]);
			} else if (strs[0].equals("zkroot")) {
				zkroot = strs[1];
			} else if (strs[0].equals("zkip")) {
				zkip = strs[1];
			} else if (strs[0].equals("tubeMaster")) {
				tubeMaster = strs[1];
			} else if (strs[0].equals("tubePort")) {
				tubePort = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("groupname")) {
				groupname = strs[1];
			} else if (strs[0].equals("checkid")) {
				checkid = strs[1];
			} else if (strs[0].equals("checktime")) {
				checktime = strs[1];
			} else if (strs[0].equals("offset")) {
				offset = Long.parseLong(strs[1]);
			} else if (strs[0].equals("runnum")) {
				runnum = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("daynum")) {
				daynum = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("interfacenum")) {
				interfacenum = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("startid")) {
				startid = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("sendnum")) {
				sendnumber = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("compress")) {
				compress = strs[1].equals("true");
			} else if (strs[0].equals("printattr")) {
				printattr = strs[1].equals("true");
			} else if (strs[0].equals("dayonly")) {
				dayonly = strs[1].equals("true");
			} else if (strs[0].equals("multiproducer")) {
				multiproducer = strs[1].equals("true") ? true : false;
			} else if (strs[0].equals("timediff_s")) {
				timediff_s = Long.parseLong(strs[1]);
			} else if (strs[0].equals("printnum")) {
				printnum = Long.parseLong(strs[1]);
			}
		}

		TubeMessageSessionFactory messageSessionFactory = null;
		MessageConsumer messageConsumer;
		ConsumerConfig consumerConfig;

		TubeClientConfig tubeClientConfig = new TubeClientConfig();
		tubeClientConfig.setMasterInfo(new MasterInfo(tubeMaster, tubePort));

		messageSessionFactory = new TubeMessageSessionFactory(tubeClientConfig);

		consumerConfig = new ConsumerConfig(groupname);
		consumerConfig.setMaxDelayFetchTimeInMills(50);
		consumerConfig.setRecoverMessageIntervalInMills(Long.MAX_VALUE);
		consumerConfig.setRecoverThreadCount(1);
		consumerConfig.setFetchRunnerCount(3);
		if (setmax) {
			consumerConfig.setConsumeFromMaxOffset(true);
		}

		System.out.println("start to subcribe ..... ");

		messageConsumer = messageSessionFactory.createConsumer(consumerConfig);
		messageConsumer.subscribe(topic, new MessageListener() {
			// int num = 0;

			public Executor getExecutor() {
				return null;
			}

			public void stop() {
				// TODO Auto-generated method stub

			}

			@Override
			public void recieveMessages(Message message)
					throws InterruptedException {

				TDMsg1 tdmsg = TDMsg1.parseFrom(message.getData());
				if (tdmsg != null) {
					for (String attr : tdmsg.getAttrs()) {
						Iterator<byte[]> it = tdmsg.getIterator(attr);
						if (it != null) {
							while (it.hasNext()) {
								if (printattr) {
									System.out.println(attr + " : "
											+ new String(it.next()));
								} else {
									System.out.println(new String(it.next()));
								}
								// num++;
								// if (num > printnum) {
								// System.exit(1);
								// }
							}
						}
					}
				}

			}
		}).completeSubscribe();

		System.out.println("end subcribe ..... ");

	}
}
