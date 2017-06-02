package com.tencent.trc.exec.physical;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.physical.InnerOp2Sink.CallBack;
import com.tencent.trc.plan.logical.OpDesc;
import com.tencent.trc.plan.physical.TaskWork1Spout;
import com.tencent.trc.util.exec.TimerPackager;
import com.tencent.trc.util.exec.TimerPackager.Packager;

public class Task1Spout extends Task implements IRichSpout {
	private static Logger log = LoggerFactory.getLogger(Task1Spout.class);

	private static final long serialVersionUID = 1L;

	transient private SpoutOutputCollector collector;

	// config info
	final private TaskWork1Spout workconf;
	private CallBack callBack = null;
	transient private TimerPackager<WrapperMsg, WrapperMsgPack> packager;
	protected transient LinkedBlockingQueue<WrapperMsgPack> messageQueue;
	protected transient ConcurrentHashMap<Long, WrapperMsgPack> id2TupleMap;

	private int innerPackSize = 16384;

	@Override
	public void printStatus(int printId) {
		log.info(getPrintPrifix() + "messageQueue:" + messageQueue.size()
				+ ",id2TupleMap:" + id2TupleMap.size());
	}

	public Task1Spout(TaskWork1Spout workconf, Properties properties) {
		super(workconf, properties);
		this.workconf = workconf;
		log.info("Task1Spout ....");
	}

	@Override
	public void open(@SuppressWarnings("rawtypes") Map conf,
			TopologyContext context, SpoutOutputCollector collector) {
		log.info("Task1Spout open ....");
		this.collector = collector;
		final int SPOUT_MSG_QUEUE_SIZE = 10000;
		topologyTaskId = context.getThisTaskId();
		messageQueue = new LinkedBlockingQueue<WrapperMsgPack>();
		id2TupleMap = new ConcurrentHashMap<Long, WrapperMsgPack>();

		try {
			innerPackSize = Integer
					.parseInt((String) conf.get("innerpacksize"));
		} catch (Exception e) {
			innerPackSize = 16384;
		}

		this.packager = new TimerPackager<WrapperMsg, WrapperMsgPack>(1000,
				new Packager<WrapperMsg, WrapperMsgPack>() {

					@Override
					public String getKey(WrapperMsg t) {
						return t.getStreamId() + String.valueOf(t.key);
					}

					@Override
					public WrapperMsgPack newPackage(WrapperMsg t) {
						return new WrapperMsgPack(t.key, t.getStreamId(),
								incremsgid.incrementAndGet(), innerPackSize);
					}

					@Override
					public boolean pack(String key, WrapperMsg t,
							WrapperMsgPack p) {
						return !p.tdmsg.addMsg(key, t.wrap());
					}

					@Override
					public void emit(String key, WrapperMsgPack p, boolean full) {
						try {
							int iii = 0;
							while (messageQueue.size() >= SPOUT_MSG_QUEUE_SIZE) {
								log.warn("emit pack sleep for " + (++iii)
										+ " times .... ");
								Thread.sleep(1000);
							}
							iii = 0;
							while (!messageQueue.offer(p, 1, TimeUnit.SECONDS)) {
								log.warn("emit pack fail for " + (++iii)
										+ " times .... ");
							}
							id2TupleMap.put(p.msgId, p);
						} catch (Throwable e) {
							log.error(getPrintPrifix()
									+ TDBankUtils.getExceptionStack(e));
						}
					}
				});

		callBack = new CallBack() {
			@Override
			public boolean call(WrapperMsg msg) {
				if (msg == null) {
					log.error(getPrintPrifix() + "receive a null msg ... ");
					return false;
				}

				packager.putTuple(msg);

				return true;
			}
		};
		try {
			super.intialize(getExecId());
		} catch (Exception e) {
			log.error(getPrintPrifix() + TDBankUtils.getExceptionStack(e));
		}
		sysPrinter.start();
	}

	@Override
	protected InnerOp2Sink generateSinkOp(TrcConfiguration hconf,
			OpDesc opDesc, OpDesc childOpDesc) {
		return new InnerOp2Sink(hconf, taskContext, opDesc, childOpDesc,
				callBack);
	}

	AtomicBoolean shouldwait = new AtomicBoolean(false);

	/**
	 * all the row obj should have been processed by the ops and then send to
	 * the queue, then nextTuple will emit it to next task
	 */
	@Override
	public void nextTuple() {
		try {
			if (shouldwait.get()) {
				log.warn(getPrintPrifix()
						+ "some tuple failed so wait for 30 ms");
				Thread.sleep(30);
				shouldwait.set(false);
			}

			WrapperMsgPack msg = messageQueue.poll(1, TimeUnit.SECONDS);
			if (msg != null) {
				String streamId = msg.streamId;
				collector
						.emit(streamId,
								new Values(msg.packkey, true, msg.genData()),
								msg.msgId);
			}
		} catch (Exception e) {
			log.error(getPrintPrifix() + TDBankUtils.getExceptionStack(e));
		}
	}

	@Override
	public void close() {
		super.close();
	}

	@Override
	public void activate() {
		// not do it right now, because whenever deactivate is called, then the
		// whole topology must be restart
		// super.restart();
	}

	@Override
	public void deactivate() {
		this.close();
	}

	@Override
	public void ack(Object msgId) {
		id2TupleMap.remove(msgId);
	}

	@Override
	public void fail(Object msgId) {
		try {
			shouldwait.set(true);
			WrapperMsgPack msg = id2TupleMap.remove(msgId);
			if (msg != null) {
				msg.setMsgId(incremsgid.incrementAndGet());
				int iii = 0;
				while (!messageQueue.offer(msg, 1, TimeUnit.SECONDS)) {
					log.warn("emit fail in fail m for " + (++iii) + " times");
				}
				id2TupleMap.put(msg.msgId, msg);
			}
		} catch (Throwable e) {
			log.error(getPrintPrifix() + TDBankUtils.getExceptionStack(e));
		}
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

	@Override
	protected String getExecId() {
		return String.valueOf(topologyTaskId);
	}

}
