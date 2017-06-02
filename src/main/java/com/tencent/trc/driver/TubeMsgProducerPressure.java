package com.tencent.trc.driver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.tencent.tdbank.msg.TDMsg1;
import com.tencent.tube.Message;
import com.tencent.tube.client.TubeClientConfig;
import com.tencent.tube.client.TubeMessageSessionFactory;
import com.tencent.tube.client.producer.MessageProducer;
import com.tencent.tube.client.producer.SendMessageCallBackResult;
import com.tencent.tube.client.producer.SendMessageCallback;
import com.tencent.tube.cluster.MasterInfo;
import com.tencent.tube.exception.TubeClientException;

public class TubeMsgProducerPressure {

	static AtomicLong receivenum = new AtomicLong();
	static AtomicLong sendnum = new AtomicLong();
	static AtomicLong success = new AtomicLong();
	static AtomicLong fail = new AtomicLong();
	static AtomicLong except = new AtomicLong();
	static AtomicLong time = new AtomicLong();

	static long msgcnt = 0;

	static String topic = "test";
	static boolean rand = true;
	static int msglen = 200;
	static long sleeptime = 0;
	static boolean async = true;
	static long timeout = 30;
	static int runnum = 100;
	static boolean multiproducer = false;
	static String tubemaster = "";
	static int tubeport = 8609;
	static long sendnumber = Long.MAX_VALUE;
	static long daynum = 30;
	static long interfacenum = 1;
	static String startid = "10000";
	static String iid = "10000";
	static int msgnum = 10;
	static boolean compress = true;
	static boolean random = true;
	static boolean sendcheck = false;
	static String mode = null;
	static boolean tdmsg = true;
	static boolean printlog = false;
	static long sendrate = 100000;
	static long unittime_s = 1 * 60;
	static long maxdiff = 99995;
	static String timemode = "hour";
	static String ntpip = "172.23.32.142";
	static String appendattr = null;
	static String metaattr = null;
	static String splitter = "|";
	static int gnum = 100;
	static int gstart = 0;
	static int num1 = 100;
	static int num2 = 100;
	static int num3 = 100;
	static int num4 = 100;
	static int num5 = 10;
	static int num6 = 10;
	static int num7 = 10;

	public static void main(String[] args) throws TubeClientException,
			InterruptedException {

		for (int i = 0; i < args.length; i++) {
			int pos = args[i].indexOf('=');
			String[] strs = new String[2];
			strs[0] = args[i].substring(0, pos);
			strs[1] = args[i].substring(pos + 1);
			if (strs[0].equals("topic")) {
				topic = strs[1];
			} else if (strs[0].equals("iid")) {
				iid = strs[1];
			} else if (strs[0].equals("rand")) {
				rand = strs[1].equals("true") ? true : false;
			} else if (strs[0].equals("msglen")) {
				msglen = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("sleeptime")) {
				sleeptime = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("async")) {
				async = strs[1].equals("true");
			} else if (strs[0].equals("timeout")) {
				timeout = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("tubemaster")) {
				tubemaster = strs[1];
			} else if (strs[0].equals("tubeport")) {
				tubeport = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("runnum")) {
				runnum = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("daynum")) {
				daynum = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("gnum")) {
				gnum = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("gstart")) {
				gstart = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("num1")) {
				num1 = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("interfacenum")) {
				interfacenum = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("startid")) {
				startid = strs[1];
			} else if (strs[0].equals("sendrate")) {
				sendrate = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("sendnum")) {
				sendnumber = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("msgnum")) {
				msgnum = Integer.parseInt(strs[1]);
			} else if (strs[0].equals("compress")) {
				compress = strs[1].equals("true");
			} else if (strs[0].equals("unittime_s")) {
				unittime_s = Long.parseLong(strs[1]);
			} else if (strs[0].equals("maxdiff")) {
				maxdiff = Long.parseLong(strs[1]);
			} else if (strs[0].equals("random")) {
				random = strs[1].equals("true");
			} else if (strs[0].equals("sendcheck")) {
				sendcheck = strs[1].equals("true");
			} else if (strs[0].equals("mode")) {
				mode = strs[1];
			} else if (strs[0].equals("timemode")) {
				timemode = strs[1];
			} else if (strs[0].equals("appendattr")) {
				appendattr = strs[1];
			} else if (strs[0].equals("ntpip")) {
				ntpip = strs[1];
			} else if (strs[0].equals("metaattr")) {
				metaattr = strs[1];
			} else if (strs[0].equals("splitter")) {
				splitter = strs[1];
			} else if (strs[0].equals("tdmsg")) {
				tdmsg = Boolean.valueOf(strs[1]);
			} else if (strs[0].equals("printlog")) {
				printlog = Boolean.valueOf(strs[1]);
			} else if (strs[0].equals("multiproducer")) {
				multiproducer = strs[1].equals("true") ? true : false;
			}
		}

		TubeMessageSessionFactory messageSessionFactory = null;
		MessageProducer producer;

		TubeClientConfig tubeClientConfig = new TubeClientConfig();
		tubeClientConfig.setMasterInfo(new MasterInfo(tubemaster, tubeport));

		messageSessionFactory = new TubeMessageSessionFactory(tubeClientConfig);
		producer = messageSessionFactory.createProducer();
		producer.publish(topic);
		new Thread() {
			public void run() {
				long lastsend = 0;
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					long sendIn1s = sendnum.get() - lastsend;
					lastsend = sendnum.get();
					System.out.println("send : " + sendnum.get()
							+ " sendIn1s : " + sendIn1s + " receive : "
							+ receivenum.get() + " fail : " + fail.get()
							+ " except : " + except.get());
				}
			};
		}.start();
		long waitns = 1000000000l / sendrate;
		long lastns = System.nanoTime();
		for (long i = 0; i < sendnumber; i++) {
			int sleep = 0;
			long ctime = System.currentTimeMillis();
			while (sendnum.get() - receivenum.get() > maxdiff) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				sleep++;
			}
			if (sleep != 0) {
				System.out.println("sleep for " + sleep + "ms "
						+ (System.currentTimeMillis() - ctime));
			}
			while (System.nanoTime() - lastns < waitns) {
			}
			lastns = System.nanoTime();
			byte[] data = tdmsg ? getMsg1() : getMsg();
			Message msg = metaattr == null ? new Message(topic, data)
					: new Message(topic, data, metaattr);
			sendnum.incrementAndGet();
			ctime = System.currentTimeMillis();
			producer.sendMessage(msg, new SendMessageCallback() {
				public void onMessageSent(final SendMessageCallBackResult result) {
					receivenum.incrementAndGet();
					if (result.isSuccess()) {
						success.incrementAndGet();
					} else {
						fail.incrementAndGet();
						if (printlog) {
							System.out.println(result.getErrorMessage());
						}
					}
				}

				public void onException(final Throwable e) {
					receivenum.incrementAndGet();
					except.incrementAndGet();
					if (printlog) {
						e.printStackTrace();
					}
					// e.printStackTrace();
				}

			}, timeout, TimeUnit.SECONDS);
			long tt = System.currentTimeMillis() - ctime;
			if (tt > 3000) {
				System.out.println("send time : " + tt);
			}
		}

	}

	static long nnn = 0;
	static String defaultmsg200 = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqr";
	static String largemsg2000 = defaultmsg200 + defaultmsg200 + defaultmsg200
			+ defaultmsg200 + defaultmsg200 + defaultmsg200 + defaultmsg200
			+ defaultmsg200 + defaultmsg200 + defaultmsg200;

	static String largemsg4000 = largemsg2000 + largemsg2000;

	static byte[] defaultdata = largemsg4000.getBytes();
	static byte[] senddata = null;
	static byte[] senddata1 = largemsg4000
			.substring(0, (int) (msglen * msgnum)).getBytes();

	private static byte[] getMsg() {
		return largemsg4000.substring(0, (int) (msglen * msgnum)).getBytes();
	}

	private static byte[] getMsg1() {
		TDMsg1 msg = TDMsg1.newTDMsg(compress);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHH");
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmmss");
		for (int i = 0; i < msgnum; i++) {
			long currtime = System.currentTimeMillis();
			Date d = new Date(currtime);
			String time = null;
			if (timemode.equals("day")) {
				time = formatter1.format(d);
			} else if (timemode.equals("hour")) {
				time = formatter.format(d);
			} else {
				long tt = unittime_s * 1000;
				long ct = currtime / tt * tt;
				time = formatter2.format(new Date(ct));
			}
			nnn = (++nnn) % interfacenum;
			// long x = nnn;
			String attr = "m=0&iname=" + iid + "&t=" + (time);
			if (appendattr != null) {
				attr = attr + "&" + appendattr;
			}

			if (random) {
				byte[] sd = generateRow().getBytes();
				msg.addMsg(attr, sd);
			} else {
				byte[] sd = largemsg4000.substring(0, msglen).getBytes();
				msg.addMsg(attr, sd);
			}
		}
		return msg.buildArray();
	}

	// private static String genRandStr(int msglen) {
	// StringBuffer sb = new StringBuffer();
	// for (long i = 0; i < msglen; i++) {
	// sb.append((char) (48 + r.nextInt(75)));
	// }
	// return sb.toString();
	// }

	private static String generateRow() {
		StringBuffer sb = new StringBuffer();
		sb.append(r.nextInt(gnum) + gstart);
		sb.append(splitter).append(String.valueOf(r.nextInt(num1)));
		sb.append(splitter).append(String.valueOf(r.nextInt(num2)));
		sb.append(splitter).append(System.currentTimeMillis());
		return sb.toString();
	};

	static Random r = new Random(12345);
}
