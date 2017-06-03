package com.tencent.easycount.exec.physical;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.conf.TrcConfiguration;
import com.tencent.easycount.exec.physical.InnerOp2Sink.CallBack;
import com.tencent.easycount.plan.logical.OpDesc;
import com.tencent.easycount.plan.physical.TaskWork2Bolt;
import com.tencent.easycount.util.exec.ExecutorProcessor;
import com.tencent.easycount.util.exec.ExecutorProcessor.Processor;
import com.tencent.easycount.util.exec.SingleThreadAckor;
import com.tencent.easycount.util.io.TDMsg1;
import com.tencent.easycount.util.status.TDBankUtils;

public class Task2Bolt extends Task implements IRichBolt {
	private static Logger log = LoggerFactory.getLogger(Task2Bolt.class);

	private static final long serialVersionUID = -3446969306828033189L;

	transient private ExecutorProcessor<Tuple> boltReceiverProcessor;

	transient private SingleThreadAckor<Tuple, WrapperMsg> singleAckor;

	transient private CallBack callback;

	final private TaskWork2Bolt workconf;

	@Override
	public void printStatus(final int printId) {
		log.info(getPrintPrifix() + "boltReceiverProcessor:"
				+ this.boltReceiverProcessor.getStatus());
	}

	public Task2Bolt(final TaskWork2Bolt workconf, final Properties properties) {
		super(workconf, properties);
		this.workconf = workconf;
		log.info(getPrintPrifix() + "Task2Bolt start ....");
	}

	@Override
	public void prepare(@SuppressWarnings("rawtypes") final Map stormConf,
			final TopologyContext context, final OutputCollector collector) {
		log.info(getPrintPrifix() + "Task2Bolt prepare ....");
		this.topologyTaskId = context.getThisTaskId();
		this.singleAckor = new SingleThreadAckor<Tuple, WrapperMsg>(
				new SingleThreadAckor.Ackor<Tuple, WrapperMsg>() {

					@Override
					public void ack(final Tuple t) {
						collector.ack(t);
					}

					@Override
					public void fail(final Tuple t) {
						collector.fail(t);
					}

					@Override
					public void emit(final WrapperMsg msg) {
						final String streamId = Task2Bolt.this.taskWork.getTaskId()
								+ "-" + msg.targetTaskId;
						// collector.emit(streamId, new Values(msg.key,
						// msg.taskId, msg.opTagIdx, msg.targetTaskId,
						// msg.targetOpTagIdx, msg.data));
						collector.emit(streamId,
								new Values(msg.key, false, msg.wrapArray()));
					}
				}, "TupleAcker-" + getTaskId());

		this.callback = new CallBack() {
			@Override
			public boolean call(final WrapperMsg msg) {
				// msg.setMsgId(incremsgid.incrementAndGet());
				Task2Bolt.this.singleAckor.emit(msg);
				return true;
			}
		};

		try {
			super.intialize(getExecId());
		} catch (final Exception e) {
			log.error(getPrintPrifix() + TDBankUtils.getExceptionStack(e));
		}

		final int processorNum = 1;
		final int processorQueueNum = 10000;
		this.boltReceiverProcessor = new ExecutorProcessor<Tuple>(
				new BoltReceiverProcessorP(), processorNum, processorQueueNum,
				"BoltReceiverProcessor-" + printPrefixStr());
		this.boltReceiverProcessor.start();
		this.singleAckor.start();

		this.sysPrinter.start();

	}

	@Override
	protected InnerOp2Sink generateSinkOp(final TrcConfiguration hconf,
			final OpDesc opDesc, final OpDesc childOpDesc) {
		return new InnerOp2Sink(hconf, this.taskContext, opDesc, childOpDesc,
				this.callback);
	}

	// private long msgReceived = -1;

	@Override
	public void execute(final Tuple input) {
		// msgReceived++;
		if (this.boltReceiverProcessor.addTuple(input)) {
			this.singleAckor.ack(input);
		} else {
			this.singleAckor.fail(input);
		}
	}

	@Override
	public void cleanup() {
		super.close();
	}

	// @Override
	// public void declareOutputFields(OutputFieldsDeclarer declarer) {
	// for (String streamId : super.getOutStreamIds2Task().keySet()) {
	// declarer.declareStream(streamId, new Fields("reduceKey", "taskId",
	// "opTagIdx", "targetTaskId", "targetOpTagIdx", "data"));
	// }
	// }
	@Override
	public void declareOutputFields(final OutputFieldsDeclarer declarer) {
		for (final String streamId : super.getOutStreamIds2Task().keySet()) {
			declarer.declareStream(streamId, new Fields("reduceKey",
					"packaged", "data"));
		}
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

	@Override
	public String getName() {
		return this.workconf.getName();
	}

	private class BoltReceiverProcessorP implements Processor<Tuple> {

		AtomicLong processedTuples = new AtomicLong(0);

		@Override
		public String getStatus() {
			return "processedTuples:" + this.processedTuples.get();
		}

		@Override
		public int hash(final Tuple t) {
			return t.getInteger(0);
		}

		@Override
		public void process(final Tuple t, final int executorid) {
			if (t.getBoolean(1)) {
				final TDMsg1 tdmsg = TDMsg1.parseFrom(t.getBinary(2));
				for (final String attr : tdmsg.getAttrs()) {
					final Iterator<ByteBuffer> it = tdmsg
							.getIteratorBuffer(attr);
					while (it.hasNext()) {
						final WrapperMsg wmsg = new WrapperMsg().unwrap(it
								.next());
						emitMsg(wmsg);
					}
				}
			} else {
				final WrapperMsg wmsg = new WrapperMsg().unwrap(t.getBinary(2));
				emitMsg(wmsg);
			}
		}

		private void emitMsg(final WrapperMsg wmsg) {
			if (wmsg == null) {
				return;
			}
			final String srcKey = wmsg.taskId + "-" + wmsg.opTagIdx;
			final SrcObject so = new SrcObject(srcKey, wmsg.data, null);
			try {
				int xxx = 0;
				while (!Task2Bolt.this.tupleProcessor.offer(so)) {
					log.warn(getPrintPrifix() + "srcObject offer fail : "
							+ so.toString() + " for " + (xxx++) + " times");
				}
				// return after the obj been processed
				so.await();
				this.processedTuples.incrementAndGet();
			} catch (final Exception e) {
				log.error(getPrintPrifix() + TDBankUtils.getExceptionStack(e));
			}
		}
	}

	@Override
	protected String getExecId() {
		return String.valueOf(this.topologyTaskId);
	}
}
