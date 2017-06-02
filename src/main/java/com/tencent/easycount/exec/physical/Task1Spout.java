package com.tencent.easycount.exec.physical;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.conf.TrcConfiguration;
import com.tencent.easycount.exec.io.Queryable.CallBack;
import com.tencent.easycount.plan.logical.OpDesc;
import com.tencent.easycount.plan.physical.TaskWork1Spout;
import com.tencent.easycount.util.exec.TimerPackager;
import com.tencent.easycount.util.exec.TimerPackager.Packager;
import com.tencent.easycount.util.status.TDBankUtils;

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
	public void printStatus(final int printId) {
		log.info(getPrintPrifix() + "messageQueue:" + this.messageQueue.size()
				+ ",id2TupleMap:" + this.id2TupleMap.size());
	}

	public Task1Spout(final TaskWork1Spout workconf, final Properties properties) {
		super(workconf, properties);
		this.workconf = workconf;
		log.info("Task1Spout ....");
	}

	@Override
	public void open(@SuppressWarnings("rawtypes") final Map conf,
			final TopologyContext context, final SpoutOutputCollector collector) {
		log.info("Task1Spout open ....");
		this.collector = collector;
		final int SPOUT_MSG_QUEUE_SIZE = 10000;
		this.topologyTaskId = context.getThisTaskId();
		this.messageQueue = new LinkedBlockingQueue<WrapperMsgPack>();
		this.id2TupleMap = new ConcurrentHashMap<Long, WrapperMsgPack>();

		try {
			this.innerPackSize = Integer.parseInt((String) conf
					.get("innerpacksize"));
		} catch (final Exception e) {
			this.innerPackSize = 16384;
		}

		this.packager = new TimerPackager<WrapperMsg, WrapperMsgPack>(1000,
				new Packager<WrapperMsg, WrapperMsgPack>() {

			@Override
			public String getKey(final WrapperMsg t) {
				return t.getStreamId() + String.valueOf(t.key);
			}

			@Override
			public WrapperMsgPack newPackage(final WrapperMsg t) {
				return new WrapperMsgPack(t.key, t.getStreamId(),
						Task1Spout.this.incremsgid.incrementAndGet(),
								Task1Spout.this.innerPackSize);
			}

			@Override
			public boolean pack(final String key, final WrapperMsg t,
					final WrapperMsgPack p) {
				return !p.tdmsg.addMsg(key, t.wrap());
			}

			@Override
			public void emit(final String key, final WrapperMsgPack p,
							final boolean full) {
				try {
					int iii = 0;
					while (Task1Spout.this.messageQueue.size() >= SPOUT_MSG_QUEUE_SIZE) {
						log.warn("emit pack sleep for " + (++iii)
								+ " times .... ");
						Thread.sleep(1000);
					}
					iii = 0;
					while (!Task1Spout.this.messageQueue.offer(p, 1,
									TimeUnit.SECONDS)) {
						log.warn("emit pack fail for " + (++iii)
								+ " times .... ");
					}
					Task1Spout.this.id2TupleMap.put(p.msgId, p);
				} catch (final Throwable e) {
					log.error(getPrintPrifix()
							+ TDBankUtils.getExceptionStack(e));
				}
			}
		});

		this.callBack = new CallBack() {
			@Override
			public boolean call(final WrapperMsg msg) {
				if (msg == null) {
					log.error(getPrintPrifix() + "receive a null msg ... ");
					return false;
				}

				Task1Spout.this.packager.putTuple(msg);

				return true;
			}
		};
		try {
			super.intialize(getExecId());
		} catch (final Exception e) {
			log.error(getPrintPrifix() + TDBankUtils.getExceptionStack(e));
		}
		this.sysPrinter.start();
	}

	@Override
	protected InnerOp2Sink generateSinkOp(final TrcConfiguration hconf,
			final OpDesc opDesc, final OpDesc childOpDesc) {
		return new InnerOp2Sink(hconf, this.taskContext, opDesc, childOpDesc,
				this.callBack);
	}

	AtomicBoolean shouldwait = new AtomicBoolean(false);

	/**
	 * all the row obj should have been processed by the ops and then send to
	 * the queue, then nextTuple will emit it to next task
	 */
	@Override
	public void nextTuple() {
		try {
			if (this.shouldwait.get()) {
				log.warn(getPrintPrifix()
						+ "some tuple failed so wait for 30 ms");
				Thread.sleep(30);
				this.shouldwait.set(false);
			}

			final WrapperMsgPack msg = this.messageQueue.poll(1,
					TimeUnit.SECONDS);
			if (msg != null) {
				final String streamId = msg.streamId;
				this.collector
				.emit(streamId,
						new Values(msg.packkey, true, msg.genData()),
						msg.msgId);
			}
		} catch (final Exception e) {
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
	public void ack(final Object msgId) {
		this.id2TupleMap.remove(msgId);
	}

	@Override
	public void fail(final Object msgId) {
		try {
			this.shouldwait.set(true);
			final WrapperMsgPack msg = this.id2TupleMap.remove(msgId);
			if (msg != null) {
				msg.setMsgId(this.incremsgid.incrementAndGet());
				int iii = 0;
				while (!this.messageQueue.offer(msg, 1, TimeUnit.SECONDS)) {
					log.warn("emit fail in fail m for " + (++iii) + " times");
				}
				this.id2TupleMap.put(msg.msgId, msg);
			}
		} catch (final Throwable e) {
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

	@Override
	protected String getExecId() {
		return String.valueOf(this.topologyTaskId);
	}

}
