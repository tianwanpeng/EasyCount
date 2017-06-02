package com.tencent.trc.util.exec;

import java.io.Closeable;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.trc.util.status.StatusCollected;

public class TimeoutCheckerKeyOnly<T> implements StatusCollected, Closeable {
	private static Logger log = LoggerFactory
			.getLogger(TimeoutCheckerKeyOnly.class);

	@Override
	public String getStatus() {
		StringBuffer sb = new StringBuffer();
		sb.append("[key2times.size:" + key2times.size() + "]");
		return sb.toString();
	}

	final private int timeunit_seconds;
	final private int timeout_ms;
	final private CheckerProcessor<T> checker;
	final private ConcurrentHashMap<TimeTuple, Long> key2times;
	static private Timer timer = null;
	final static private Object timerlock = new Object();
	final private Object lock = new Object();

	public TimeoutCheckerKeyOnly(int time_num, TimeUnit unit, int timeout_ms,
			CheckerProcessor<T> checker, String checkerName) {
		this.timeunit_seconds = (int) unit.toSeconds(time_num);
		this.timeout_ms = timeout_ms;
		this.checker = checker;
		this.key2times = new ConcurrentHashMap<TimeTuple, Long>();
		synchronized (timerlock) {
			if (timer == null) {
				timer = new Timer("TimeoutCheckerKeyOnly");
			}
		}
	}

	public void update(T t, long tupleTime) {
		// String key = this.checker.getKey(t);
		TimeTuple ttkey = new TimeTuple(t, tupleTime, this.timeunit_seconds);
		long ctime = System.currentTimeMillis();
		synchronized (lock) {
			if (!this.key2times.containsKey(ttkey)) {
				schedule(ttkey);
			}
			this.key2times.put(ttkey, ctime);
		}
	}

	public boolean containsKey(T t, long tupleTime) {
		TimeTuple ttkey = new TimeTuple(t, tupleTime, this.timeunit_seconds);
		synchronized (lock) {
			return key2times.containsKey(ttkey);
		}
	}

	private void schedule(TimeTuple ttkey) {
		long ctime = System.currentTimeMillis();
		TimerTask ttask = new TupleTimerTask(ttkey);

		long minnextsctime = ttkey.tupleTime + timeunit_seconds * 1000;
		long actnextsctime = (key2times.containsKey(ttkey) ? key2times
				.get(ttkey) : ttkey.tupleTime) + timeout_ms;
		int timeout = (int) (Math.max(minnextsctime, actnextsctime) - ctime);
		timeout = timeout < 0 ? 0 : timeout;
		while (true) {
			try {
				timer.schedule(ttask, timeout);
				break;
			} catch (final Throwable e) {
				log.error(TDBankUtils.getExceptionStack(e));
				if (e instanceof IllegalStateException) {
					synchronized (timerlock) {
						timer = new Timer("TimeoutCheckerKeyOnly");
					}
				} else {
					break;
				}
			}
		}
	}

	private void emit(TimeTuple ttkey) {
		key2times.remove(ttkey);
		checker.process(ttkey.tupleKey, ttkey.tupleTime);
	}

	@Override
	public void close() throws IOException {
		// timer.cancel();
		for (TimeTuple ttKey : key2times.keySet()) {
			emit(ttKey);
		}
	}

	private class TimeTuple {
		final T tupleKey;
		final long tupleTime;

		public TimeTuple(T tupleKey, long tupleTime, int timeunit_seconds) {
			this.tupleKey = tupleKey;
			this.tupleTime = tupleTime / timeunit_seconds / 1000
					* timeunit_seconds * 1000;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null
					|| !(obj instanceof TimeoutCheckerKeyOnly.TimeTuple)) {
				return false;
			}
			return this.hashCode() == obj.hashCode();
			// TimeoutChecker.TimeTuple objtuple = (TimeoutChecker.TimeTuple)
			// obj;
			// return (this.tupleKey.equals(objtuple.tupleKey) && this.tupleTime
			// == objtuple.tupleTime);
		}

		@Override
		public int hashCode() {
			return (int) (tupleTime * 31 + (tupleKey == null ? 0 : tupleKey
					.hashCode()));
		}
	}

	private class TupleTimerTask extends TimerTask {
		final private TimeTuple ttkey;

		public TupleTimerTask(TimeTuple ttkey) {
			this.ttkey = ttkey;
		}

		@Override
		public void run() {
			try {
				synchronized (lock) {
					long ctime = System.currentTimeMillis();
					if (key2times.containsKey(ttkey)) {
						if (ctime - key2times.get(ttkey) > timeout_ms) {
							emit(ttkey);
						} else {
							schedule(ttkey);
						}
					}
				}
			} catch (Throwable e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
		}
	}

	public static interface CheckerProcessor<T> {
		// public String getKey(T t);

		public void process(T key, long tupleTime);
	}

	public static void main(String[] args) {
		TimeoutCheckerKeyOnly<String> tc = new TimeoutCheckerKeyOnly<String>(5,
				TimeUnit.SECONDS, 6000, new CheckerProcessor<String>() {

					@Override
					public void process(String key, long tupleTime) {
						System.out.println(System.currentTimeMillis() / 1000
								+ " " + key + " " + tupleTime / 1000);
					}

				}, "TimerTest");
		while (true) {
			tc.update(getRandString(), System.currentTimeMillis() + 4000);
			// tc.update(getRandString(), System.currentTimeMillis()+1000);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
		}
	}

	static Random r = new Random();

	private static String getRandString() {
		int num = 1;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < num; i++) {
			sb.append((char) ('a' + r.nextInt(1)));
		}

		return sb.toString();
	}

}
