package com.tencent.easycount.util.exec;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.util.status.StatusCollected;
import com.tencent.easycount.util.status.TDBankUtils;

public class SingleThreadAckor<T, P> extends Thread implements StatusCollected {
	private static Logger log = LoggerFactory
			.getLogger(SingleThreadAckor.class);

	@Override
	public String getStatus() {
		return "[tuples.size=" + tuples.size() + ";failtuples.size="
				+ failtuples.size() + ";emitvalues.size=" + emitvalues.size()
				+ "]";
	}

	final private Ackor<T, P> ackor;
	final private LinkedBlockingQueue<T> tuples;
	final private LinkedBlockingQueue<T> failtuples;
	final private LinkedBlockingQueue<P> emitvalues;

	public SingleThreadAckor(Ackor<T, P> ackor, String ackName) {
		super(ackName);
		this.ackor = ackor;
		tuples = new LinkedBlockingQueue<T>();
		failtuples = new LinkedBlockingQueue<T>();
		emitvalues = new LinkedBlockingQueue<P>();
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (!failtuples.isEmpty()) {
					ackor.fail(failtuples.poll());
				} else if (!emitvalues.isEmpty()) {
					ackor.emit(emitvalues.poll());
				} else {
					try {
						T t = tuples.poll(1, TimeUnit.SECONDS);
						if (t != null) {
							ackor.ack(t);
						}
					} catch (InterruptedException e) {
					}
				}
			} catch (Throwable e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
		}
	}

	public void ack(T t) {
		this.tuples.add(t);
	}

	public void fail(T t) {
		this.failtuples.add(t);
	}

	public void emit(P t) {
		this.emitvalues.add(t);
	}

	public static interface Ackor<T, P> {
		public void ack(T t);

		public void fail(T t);

		public void emit(P p);
	}
}
