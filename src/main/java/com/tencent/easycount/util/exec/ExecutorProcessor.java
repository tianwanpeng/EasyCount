package com.tencent.easycount.util.exec;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.util.status.StatusCollected;
import com.tencent.easycount.util.status.TDBankUtils;

public class ExecutorProcessor<T> implements StatusCollected {
	private static Logger log = LoggerFactory
			.getLogger(ExecutorProcessor.class);

	@Override
	public String getStatus() {
		StringBuffer sb = new StringBuffer();
		sb.append("[executorsNum:" + executorsNum + ",e.status:");
		for (int i = 0; i < executorsNum; i++) {
			sb.append("(" + this.executors.get(i).getStatus() + "),");
		}
		sb.append("p.status:(" + processor.getStatus() + ")");
		sb.append("]");
		return sb.toString();
	}

	final private Processor<T> processor;
	final private int executorsNum;
	final private HashMap<Integer, Executor> executors;

	/**
	 * 
	 * @param processor
	 * @param executorsNum
	 * @param tupleQueueSize
	 *            : less or equal 0 means unlimited queue
	 */
	public ExecutorProcessor(Processor<T> processor, int executorsNum,
			int tupleQueueSize, String execName) {
		this.processor = processor;
		this.executorsNum = executorsNum;
		this.executors = new HashMap<Integer, Executor>(executorsNum * 2);
		for (int i = 0; i < this.executorsNum; i++) {
			this.executors.put(i, new Executor(i, tupleQueueSize, execName));
		}
	}

	public void start() {
		for (Integer id : this.executors.keySet()) {
			this.executors.get(id).start();
		}
	}

	public boolean addTuple(T t) {
		int id = Math.abs(this.processor.hash(t) % this.executorsNum);
		return this.executors.get(id).addTuple(t);
	}

	private class Executor extends Thread implements StatusCollected {

		@Override
		public String getStatus() {
			return "e" + id + ":" + tuples.size();
		}

		final private LinkedBlockingQueue<T> tuples;
		final int id;

		public Executor(int id, int tupleQueueSize, String execName) {
			super(execName + "-" + id);
			this.id = id;
			tuples = tupleQueueSize > 0 ? new LinkedBlockingQueue<T>(
					tupleQueueSize) : new LinkedBlockingQueue<T>();
		}

		@Override
		public void run() {
			while (true) {
				try {
					T t = tuples.poll(1, TimeUnit.SECONDS);
					if (t == null) {
						continue;
					}
					processor.process(t, this.id);
				} catch (Throwable e) {
				}
			}
		}

		private boolean addTuple(T t) {
			return tuples.offer(t);
		}
	}

	/**
	 * the class implements this interface must be thread safe
	 * 
	 * @param <T>
	 */
	public static interface Processor<T> extends StatusCollected {

		public int hash(T t);

		public void process(T t, int executorid);

	}

	public static void main(String[] args) {
		ExecutorProcessor<Integer> ep = new ExecutorProcessor<Integer>(
				new Processor<Integer>() {

					@Override
					public String getStatus() {
						return null;
					}

					@Override
					public void process(Integer t, int id) {
						System.out.println(t);
					}

					@Override
					public int hash(Integer t) {
						return t;
					}
				}, 10, 0, "ThreadTest");

		ep.start();
		for (int i = 0; i < 1000; i++) {
			ep.addTuple(i);
			System.out.println(ep.getStatus());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
		}
	}
}
