package com.tencent.easycount.mon;

import java.util.concurrent.atomic.AtomicInteger;

import com.tencent.easycount.util.map.Updater1;
import com.tencent.easycount.util.map.UpdaterMap1;

public class MonStatusUpdater {
	final private UpdaterMap1<String, Integer, AtomicInteger> updateMap;

	public MonStatusUpdater(final MsgSender sender, final int emitInterval) {
		updateMap = new UpdaterMap1<String, Integer, AtomicInteger>(
				new Updater1<String, Integer, AtomicInteger>() {

					@Override
					public boolean inplace() {
						return true;
					}

					@Override
					public AtomicInteger update(String key, Integer num,
							AtomicInteger resnum) {
						if (resnum == null) {
							resnum = new AtomicInteger(0);
						}
						resnum.addAndGet(num);
						return resnum;
					}
				});

		new Thread() {
			public void run() {
				long lastEmitTime = 0;
				while (true) {
					try {
						if (System.currentTimeMillis() - lastEmitTime > emitInterval * 1000) {
							lastEmitTime = System.currentTimeMillis();
							for (String key : updateMap.keySet()) {
								sender.send(key, updateMap.remove(key).get());
							}
						} else {
							Thread.sleep(1000);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

	}

	public void update(String key, int val) {
		updateMap.add(key, val);
	}

	public static interface MsgSender {
		public void send(String key, int val);
	}
}
