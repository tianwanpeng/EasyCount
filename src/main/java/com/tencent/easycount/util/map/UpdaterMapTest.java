package com.tencent.easycount.util.map;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class UpdaterMapTest {
	public static void main(String[] args) {
		final UpdaterMap<Integer, AtomicLong> map = UpdaterMapFactory
				.getIntegerUpdater();
		// final CommonMap<Integer, AtomicLong> map = UpdaterMapFactory
		// .getIntegerUpdater_c();

		new Thread() {
			public void run() {
				HashMap<Integer, AtomicLong> mm = new HashMap<Integer, AtomicLong>();
				for (int i = 0; i < 10; i++) {
					mm.put(i, new AtomicLong());
				}
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < 10; i++) {
						mm.get(i)
								.addAndGet(map.remove(String.valueOf(i)).get());
						System.out.println(i + "  " + mm.get(i).get());
					}
					// System.out.println(map.queue.size());
				}
			};
		}.start();

		long t = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			map.add(String.valueOf(i % 10), 1);
		}
		System.out.println(System.currentTimeMillis() - t);
		// t = System.currentTimeMillis();
		// for (int i = 0; i < 1000000; i++) {
		// cmap.add(String.valueOf(i % 10), 1);
		// }
		// System.out.println(System.currentTimeMillis() - t);

		// ConcurrentHashMap<String, AtomicLong> mm = new
		// ConcurrentHashMap<String, AtomicLong>();
		// t = System.currentTimeMillis();
		// for (int i = 0; i < 10; i++) {
		// mm.put(String.valueOf(i), new AtomicLong());
		// }
		// for (int i = 0; i < 1000000; i++) {
		// synchronized (mm) {
		// mm.get(String.valueOf(i % 10)).addAndGet(1);
		// }
		//
		// }
		//
		// System.out.println(System.currentTimeMillis() - t);
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// log.error(TDBankUtils.getExceptionStack(e));
		// }

	}

}
