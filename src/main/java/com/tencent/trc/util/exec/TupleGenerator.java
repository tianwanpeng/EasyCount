package com.tencent.trc.util.exec;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.tencent.trc.util.status.StatusCollected;

public class TupleGenerator<T> implements StatusCollected {
	// private static Logger log =
	// LoggerFactory.getLogger(TupleGenerator.class);

	@Override
	public String getStatus() {
		StringBuffer sb = new StringBuffer();
		sb.append("[ executorsNum : " + executorsNum + " e.status : ");
		for (int i = 0; i < executorsNum; i++) {
			sb.append("( " + this.executors.get(i).getStatus() + " ), ");
		}
		sb.append("g.status : ( " + generator.getStatus() + " )");
		sb.append(" ] ");
		return sb.toString();
	}

	final private Generator<T> generator;
	final private int executorsNum;
	final private HashMap<Integer, Executor> executors;
	final private LinkedBlockingQueue<T> tuples;

	/**
	 * 
	 * @param processor
	 * @param generator
	 * @param tupleQueueSize
	 *            : less or equal 0 means unlimited queue
	 */
	public TupleGenerator(Generator<T> generator, int executorsNum,
			int tupleQueueSize) {
		this.generator = generator;
		this.executorsNum = executorsNum;
		this.executors = new HashMap<Integer, Executor>(executorsNum * 2);
		for (int i = 0; i < this.executorsNum; i++) {
			this.executors.put(i, new Executor(i));
		}
		this.tuples = tupleQueueSize > 0 ? new LinkedBlockingQueue<T>(
				tupleQueueSize) : new LinkedBlockingQueue<T>();
	}

	public void start() {
		for (Integer id : this.executors.keySet()) {
			this.executors.get(id).start();
		}
	}

	public T getTuple() {
		return this.getTuple(1, TimeUnit.SECONDS);
	}

	public T getTuple(int timeout, TimeUnit tu) {
		try {
			return this.tuples.poll(timeout, tu);
		} catch (InterruptedException e) {
		}
		return null;
	}

	private class Executor extends Thread implements StatusCollected {

		@Override
		public String getStatus() {
			return "e" + id + " : " + tuples.size();
		}

		final int id;

		public Executor(int id) {
			this.id = id;
		}

		@Override
		public void run() {
			while (true) {
				try {
					generator.generate(id);
				} catch (Throwable e) {
				}
			}
		}

	}

	/**
	 * the class implements this interface must be thread safe
	 * 
	 * @param <T>
	 */
	public static abstract class Generator<T> implements StatusCollected {
		final private TupleGenerator<T> tg;

		public Generator(TupleGenerator<T> tg) {
			this.tg = tg;
		}

		protected boolean addTuple(T t) {
			return tg.tuples.add(t);
		}

		abstract public int hash(T t);

		/**
		 * call addTuple whenever generate a new tuple, or the generated tuple
		 * won't be processed
		 * 
		 * @param executorid
		 */
		abstract public void generate(int executorid);

	}

}
