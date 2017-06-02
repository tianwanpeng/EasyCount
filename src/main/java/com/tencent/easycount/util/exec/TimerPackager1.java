package com.tencent.easycount.util.exec;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.util.status.StatusCollected;
import com.tencent.easycount.util.status.TDBankUtils;

public class TimerPackager1<T, P> extends Thread implements StatusCollected {
	private static Logger log = LoggerFactory.getLogger(TimerPackager1.class);
	final private int timeout_ms;
	final private Packager<T, P> packager;
	final private ConcurrentHashMap<String, P> key2packages;
	final private ConcurrentHashMap<String, Long> key2times;
	final private ConcurrentHashMap<String, TimerTask> key2timertasks;
	final private LinkedBlockingQueue<EmitPack> tobeEmittedKeys;
	private Timer timer;
	final private Object lock = new Object();

	@Override
	public String getStatus() {
		StringBuffer sb = new StringBuffer();
		sb.append("[key2times.size:" + key2times.size());
		sb.append(",tobeEmittedKeys.size:" + tobeEmittedKeys.size() + "]");
		return sb.toString();
	}

	public TimerPackager1(int timeout_ms, Packager<T, P> packager) {
		this.timeout_ms = timeout_ms;
		this.packager = packager;
		key2packages = new ConcurrentHashMap<String, P>();
		key2times = new ConcurrentHashMap<String, Long>();
		key2timertasks = new ConcurrentHashMap<String, TimerTask>();
		tobeEmittedKeys = new LinkedBlockingQueue<EmitPack>(10000);
		timer = new Timer();
	}

	private class EmitPack {
		final String key;
		final P p;
		final boolean full;

		public EmitPack(String key, P p, boolean full) {
			this.key = key;
			this.p = p;
			this.full = full;
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				EmitPack key = tobeEmittedKeys.poll(1, TimeUnit.SECONDS);
				if (key != null) {
					packager.emit(key.key, key.p, key.full);
				}
			} catch (Throwable e) {
			}
		}
	}

	/**
	 * thread safe
	 * 
	 * @param t
	 */
	public void putTuple(T t) {
		synchronized (lock) {
			final String key = this.packager.getKey(t);
			P p;
			if (!this.key2times.containsKey(key)) {
				p = this.packager.newPackage(t);
				final long ctime = System.currentTimeMillis();
				this.key2times.putIfAbsent(key, ctime);
				P op = this.key2packages.putIfAbsent(key, p);
				if (op != null) {
					p = op;
				}
				TimerTask ttask = new TimerTask() {
					@Override
					public void run() {
						try {
							synchronized (lock) {
								if (key2times.containsKey(key)
										&& ctime == key2times.get(key)) {
									key2times.remove(key);
									key2timertasks.remove(key);

									if (!tobeEmittedKeys.add(new EmitPack(key,
											key2packages.remove(key), false))) {
										log.warn("tobeEmittedKeys is full so drop key : ");
									}
								}
							}
						} catch (Throwable e) {
							log.error(TDBankUtils.getExceptionStack(e));
						}
					}
				};

				TimerTask oldttask = this.key2timertasks.remove(key);
				if (oldttask != null) {
					oldttask.cancel();
				}
				this.key2timertasks.putIfAbsent(key, ttask);

				while (true) {
					try {
						timer.schedule(ttask, timeout_ms);
						break;
					} catch (final Throwable e) {
						log.error(TDBankUtils.getExceptionStack(e));
						if (e instanceof IllegalStateException) {
							timer = new Timer();
						} else {
							break;
						}
					}
				}
			} else {
				p = this.key2packages.get(key);
			}
			if (this.packager.pack(key, t, p)) {
				if (!tobeEmittedKeys.add(new EmitPack(key, key2packages
						.remove(key), true))) {
					log.warn("tobeEmittedKeys is full so drop key : ");
				}

			}
		}
	}

	// private void emitPack(String key, boolean full) {
	// key2times.remove(key);
	// key2timertasks.remove(key);
	// packager.emit(key, key2packages.remove(key), full);
	// }

	public static interface Packager<T, P> {
		public String getKey(T t);

		public P newPackage(T t);

		/**
		 * return true if the package is full
		 * 
		 * @param key
		 * 
		 * @param t
		 * @param p
		 * @return
		 */
		public boolean pack(String key, T t, P p);

		public void emit(String key, P p, boolean full);
	}
}
