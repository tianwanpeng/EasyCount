package com.tencent.easycount.util.exec;

import java.io.Closeable;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.util.status.StatusCollected;
import com.tencent.easycount.util.status.TDBankUtils;

public class TimeoutCheckerKeyOnly<T> implements StatusCollected, Closeable {
	private static Logger log = LoggerFactory
			.getLogger(TimeoutCheckerKeyOnly.class);

	@Override
	public String getStatus() {
		final StringBuffer sb = new StringBuffer();
		sb.append("[key2times.size:" + this.key2times.size() + "]");
		return sb.toString();
	}

	final private int timeunit_seconds;
	final private int timeout_ms;
	final private CheckerProcessor<T> checker;
	final private ConcurrentHashMap<TimeTuple, Long> key2times;
	static private Timer timer = null;
	final static private Object timerlock = new Object();
	final private Object lock = new Object();

	public TimeoutCheckerKeyOnly(final int time_num, final TimeUnit unit,
			final int timeout_ms, final CheckerProcessor<T> checker,
			final String checkerName) {
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

	public void update(final T t, final long tupleTime) {
		// String key = this.checker.getKey(t);
		final TimeTuple ttkey = new TimeTuple(t, tupleTime,
				this.timeunit_seconds);
		final long ctime = System.currentTimeMillis();
		synchronized (this.lock) {
			if (!this.key2times.containsKey(ttkey)) {
				schedule(ttkey);
			}
			this.key2times.put(ttkey, ctime);
		}
	}

	public boolean containsKey(final T t, final long tupleTime) {
		final TimeTuple ttkey = new TimeTuple(t, tupleTime,
				this.timeunit_seconds);
		synchronized (this.lock) {
			return this.key2times.containsKey(ttkey);
		}
	}

	private void schedule(final TimeTuple ttkey) {
		final long ctime = System.currentTimeMillis();
		final TimerTask ttask = new TupleTimerTask(ttkey);

		final long minnextsctime = ttkey.tupleTime
				+ (this.timeunit_seconds * 1000);
		final long actnextsctime = (this.key2times.containsKey(ttkey) ? this.key2times
				.get(ttkey) : ttkey.tupleTime)
				+ this.timeout_ms;
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

	private void emit(final TimeTuple ttkey) {
		this.key2times.remove(ttkey);
		this.checker.process(ttkey.tupleKey, ttkey.tupleTime);
	}

	@Override
	public void close() throws IOException {
		// timer.cancel();
		for (final TimeTuple ttKey : this.key2times.keySet()) {
			emit(ttKey);
		}
	}

	private class TimeTuple {
		final T tupleKey;
		final long tupleTime;

		public TimeTuple(final T tupleKey, final long tupleTime,
				final int timeunit_seconds) {
			this.tupleKey = tupleKey;
			this.tupleTime = (tupleTime / timeunit_seconds / 1000)
					* timeunit_seconds * 1000;
		}

		@Override
		public boolean equals(final Object obj) {
			if ((obj == null)
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
			return (int) ((this.tupleTime * 31) + (this.tupleKey == null ? 0
					: this.tupleKey.hashCode()));
		}
	}

	private class TupleTimerTask extends TimerTask {
		final private TimeTuple ttkey;

		public TupleTimerTask(final TimeTuple ttkey) {
			this.ttkey = ttkey;
		}

		@Override
		public void run() {
			try {
				synchronized (TimeoutCheckerKeyOnly.this.lock) {
					final long ctime = System.currentTimeMillis();
					if (TimeoutCheckerKeyOnly.this.key2times
							.containsKey(this.ttkey)) {
						if ((ctime - TimeoutCheckerKeyOnly.this.key2times
								.get(this.ttkey)) > TimeoutCheckerKeyOnly.this.timeout_ms) {
							emit(this.ttkey);
						} else {
							schedule(this.ttkey);
						}
					}
				}
			} catch (final Throwable e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
		}
	}

	public static interface CheckerProcessor<T> {
		// public String getKey(T t);

		public void process(T key, long tupleTime);
	}

	public static void main(final String[] args) {
		// TimeoutCheckerKeyOnly<String> tc = new
		// TimeoutCheckerKeyOnly<String>(5,
		// TimeUnit.SECONDS, 6000, new CheckerProcessor<String>() {
		//
		// @Override
		// public void process(String key, long tupleTime) {
		// System.out.println(System.currentTimeMillis() / 1000
		// + " " + key + " " + tupleTime / 1000);
		// }
		//
		// }, "TimerTest");
		// while (true) {
		// tc.update(getRandString(), System.currentTimeMillis() + 4000);
		// // tc.update(getRandString(), System.currentTimeMillis()+1000);
		// try {
		// Thread.sleep(1);
		// } catch (InterruptedException e) {
		// log.error(TDBankUtils.getExceptionStack(e));
		// }
		// }
	}
	//
	// static Random r = new Random();
	//
	// private static String getRandString() {
	// final int num = 1;
	// final StringBuffer sb = new StringBuffer();
	// for (int i = 0; i < num; i++) {
	// sb.append((char) ('a' + r.nextInt(1)));
	// }
	//
	// return sb.toString();
	// }

}
