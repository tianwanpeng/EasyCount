package com.tencent.trc.exec.physical;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.tdbank.msg.TDMsg1;
import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.physical.InnerOp2Sink.CallBack;
import com.tencent.trc.plan.logical.OpDesc;
import com.tencent.trc.plan.physical.TaskWork2Bolt;
import com.tencent.trc.util.exec.ExecutorProcessor;
import com.tencent.trc.util.exec.ExecutorProcessor.Processor;
import com.tencent.trc.util.exec.SingleThreadAckor;

public class Task2Bolt extends Task implements IRichBolt {
	private static Logger log = LoggerFactory.getLogger(Task2Bolt.class);

	private static final long serialVersionUID = -3446969306828033189L;

	transient private ExecutorProcessor<Tuple> boltReceiverProcessor;

	transient private SingleThreadAckor<Tuple, WrapperMsg> singleAckor;

	transient private CallBack callback;

	final private TaskWork2Bolt workconf;

	@Override
	public void printStatus(int printId) {
		log.info(getPrintPrifix() + "boltReceiverProcessor:"
				+ boltReceiverProcessor.getStatus());
	}

	public Task2Bolt(TaskWork2Bolt workconf, Properties properties) {
		super(workconf, properties);
		this.workconf = workconf;
		log.info(getPrintPrifix() + "Task2Bolt start ....");
	}

	@Override
	public void prepare(@SuppressWarnings("rawtypes") Map stormConf,
			TopologyContext context, final OutputCollector collector) {
		log.info(getPrintPrifix() + "Task2Bolt prepare ....");
		topologyTaskId = context.getThisTaskId();
		singleAckor = new SingleThreadAckor<Tuple, WrapperMsg>(
				new SingleThreadAckor.Ackor<Tuple, WrapperMsg>() {

					@Override
					public void ack(Tuple t) {
						collector.ack(t);
					}

					@Override
					public void fail(Tuple t) {
						collector.fail(t);
					}

					@Override
					public void emit(WrapperMsg msg) {
						String streamId = taskWork.getTaskId() + "-"
								+ msg.targetTaskId;
						// collector.emit(streamId, new Values(msg.key,
						// msg.taskId, msg.opTagIdx, msg.targetTaskId,
						// msg.targetOpTagIdx, msg.data));
						collector.emit(streamId,
								new Values(msg.key, false, msg.wrapArray()));
					}
				}, "TupleAcker-" + getTaskId());

		callback = new CallBack() {
			@Override
			public boolean call(WrapperMsg msg) {
				// msg.setMsgId(incremsgid.incrementAndGet());
				singleAckor.emit(msg);
				return true;
			}
		};

		try {
			super.intialize(getExecId());
		} catch (Exception e) {
			log.error(getPrintPrifix() + TDBankUtils.getExceptionStack(e));
		}

		int processorNum = 1;
		int processorQueueNum = 10000;
		boltReceiverProcessor = new ExecutorProcessor<Tuple>(
				new BoltReceiverProcessorP(), processorNum, processorQueueNum,
				"BoltReceiverProcessor-" + printPrefixStr());
		boltReceiverProcessor.start();
		singleAckor.start();

		sysPrinter.start();

	}

	@Override
	protected InnerOp2Sink generateSinkOp(TrcConfiguration hconf,
			OpDesc opDesc, OpDesc childOpDesc) {
		return new InnerOp2Sink(hconf, taskContext, opDesc, childOpDesc,
				callback);
	}

	// private long msgReceived = -1;

	@Override
	public void execute(Tuple input) {
		// msgReceived++;
		if (boltReceiverProcessor.addTuple(input)) {
			singleAckor.ack(input);
		} else {
			singleAckor.fail(input);
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
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		for (String streamId : super.getOutStreamIds2Task().keySet()) {
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
			return "processedTuples:" + processedTuples.get();
		}

		@Override
		public int hash(Tuple t) {
			return t.getInteger(0);
		}

		@Override
		public void process(Tuple t, int executorid) {
			if (t.getBoolean(1)) {
				TDMsg1 tdmsg = TDMsg1.parseFrom(t.getBinary(2));
				for (String attr : tdmsg.getAttrs()) {
					Iterator<ByteBuffer> it = tdmsg.getIteratorBuffer(attr);
					while (it.hasNext()) {
						WrapperMsg wmsg = new WrapperMsg().unwrap(it.next());
						emitMsg(wmsg);
					}
				}
			} else {
				WrapperMsg wmsg = new WrapperMsg().unwrap(t.getBinary(2));
				emitMsg(wmsg);
			}
		}

		private void emitMsg(WrapperMsg wmsg) {
			if (wmsg == null) {
				return;
			}
			String srcKey = wmsg.taskId + "-" + wmsg.opTagIdx;
			SrcObject so = new SrcObject(srcKey, wmsg.data, null);
			try {
				int xxx = 0;
				while (!tupleProcessor.offer(so)) {
					log.warn(getPrintPrifix() + "srcObject offer fail : "
							+ so.toString() + " for " + (xxx++) + " times");
				}
				// return after the obj been processed
				so.await();
				processedTuples.incrementAndGet();
			} catch (Exception e) {
				log.error(getPrintPrifix() + TDBankUtils.getExceptionStack(e));
			}
		}
	}

	@Override
	protected String getExecId() {
		return String.valueOf(topologyTaskId);
	}
}
