package com.tencent.easycount.util.exec;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.util.status.StatusCollected;
import com.tencent.easycount.util.status.TDBankUtils;

public class TimeoutChecker<T> implements StatusCollected {
	private static Logger log = LoggerFactory.getLogger(TimeoutChecker.class);

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
	private Timer timer;
	final private Object lock = new Object();

	public TimeoutChecker(int time_num, TimeUnit unit, int timeout_ms,
			CheckerProcessor<T> checker) {
		this.timeunit_seconds = (int) unit.toSeconds(time_num);
		this.timeout_ms = timeout_ms;
		this.checker = checker;
		this.key2times = new ConcurrentHashMap<TimeTuple, Long>();
		this.timer = new Timer();
	}

	public void update(T t, long tupleTime) {
		String key = this.checker.getKey(t);
		TimeTuple ttkey = new TimeTuple(key, tupleTime, this.timeunit_seconds);
		long ctime = System.currentTimeMillis();
		synchronized (lock) {
			if (!this.key2times.containsKey(ttkey)) {
				schedule(ttkey);
			}
			this.key2times.put(ttkey, ctime);
		}
	}

	private void schedule(TimeTuple ttkey) {
		long ctime = System.currentTimeMillis();
		TimerTask ttask = new TupleTimerTask(ttkey);

		int minnextsctime = (int) (ttkey.tupleTime + timeunit_seconds * 1000);
		int actnextsctime = (int) ((key2times.containsKey(ttkey) ? key2times
				.get(ttkey) : ttkey.tupleTime) + timeout_ms);
		int timeout = (int) (Math.max(minnextsctime, actnextsctime) - ctime);
		timeout = timeout < 0 ? 0 : timeout;
		while (true) {
			try {
				timer.schedule(ttask, timeout);
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
	}

	private void emit(TimeTuple ttkey) {
		key2times.remove(ttkey);
		checker.process(ttkey.tupleKey, ttkey.tupleTime);
	}

	private static class TimeTuple {
		final String tupleKey;
		final long tupleTime;

		public TimeTuple(String tupleKey, long tupleTime, int timeunit_seconds) {
			this.tupleKey = tupleKey;
			this.tupleTime = tupleTime / timeunit_seconds / 1000
					* timeunit_seconds * 1000;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof TimeoutChecker.TimeTuple)) {
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
		public String getKey(T t);

		public void process(String key, long tupleTime);
	}

	public static void main(String[] args) {
		TimeoutChecker<String> tc = new TimeoutChecker<String>(10,
				TimeUnit.SECONDS, 1000, new CheckerProcessor<String>() {

					@Override
					public String getKey(String t) {
						return t;
					}

					@Override
					public void process(String key, long tupleTime) {
						System.out.println(System.currentTimeMillis() + " "
								+ key + " " + tupleTime);
					}

				});
		while (true) {
			tc.update(getRandString(), System.currentTimeMillis());
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
			sb.append((char) ('a' + r.nextInt(2)));
		}

		return sb.toString();
	}
}
