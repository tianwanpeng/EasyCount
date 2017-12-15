package com.tencent.easycount.exec.io.kafka;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.conf.ECConfiguration;
import com.tencent.easycount.exec.io.Data2Sink;
import com.tencent.easycount.metastore.Table;
import com.tencent.easycount.metastore.TableUtils;
import com.tencent.easycount.plan.logical.OpDesc7FS;
import com.tencent.easycount.util.exec.TimerPackager;
import com.tencent.easycount.util.exec.TimerPackager.Packager;
import com.tencent.easycount.util.io.TDMsg1;
import com.tencent.easycount.util.status.TDBankUtils;

public class Data2SinkKafka extends Data2Sink {
	private static Logger log = LoggerFactory.getLogger(Data2SinkKafka.class);

	private final String iid;
	private final String tdsortAttrs;

	private final AtomicLong emitMsgNum;
	private final AtomicLong emitPackNum;

	final private TimerPackager<RowAttrs, TDMsg1> timerPackager;

	@Override
	public void printStatus(final int printId) {
		log.info(this.opDesc.getTaskId_OpTagIdx() + " : " + this.iid
				+ " : emitMsgNum : " + this.emitMsgNum.get()
				+ " : emitPackNum : " + this.emitPackNum.get());
	}

	public Data2SinkKafka(final ECConfiguration hconf, final OpDesc7FS opDesc,
			final KafkaECProducer producer) {
		super(opDesc);
		// this.producer = producer;
		final Table tbl = opDesc.getTable();
		final String topic = TableUtils.getTableTopic(tbl);
		final String interfaceId = TableUtils.getTableInterfaceId(tbl);
		this.iid = topic + "-" + interfaceId;
		this.tdsortAttrs = TableUtils.getStreamTdsortAttrs(tbl);

		this.emitMsgNum = new AtomicLong(0);
		this.emitPackNum = new AtomicLong(0);

		final int timeout_seconds = hconf
				.getInt("tube.emit.timeout.seconds", 3);
		final int packsize = hconf.getInt("tube.emit.package.size", 4096);
		final boolean compress = TableUtils.getTubeTableCompress(tbl,
				hconf.getBoolean("tube.emit.package.compress", false));

		this.timerPackager = new TimerPackager<RowAttrs, TDMsg1>(
				timeout_seconds * 1000, new Packager<RowAttrs, TDMsg1>() {

					@Override
					public String getKey(final RowAttrs t) {
						return Data2SinkKafka.this.iid;
					}

					@Override
					public TDMsg1 newPackage(final RowAttrs t) {
						return TDMsg1.newTDMsg(packsize, compress);
					}

					@Override
					public boolean pack(final String key, final RowAttrs ra,
							final TDMsg1 p) {
						final byte[] data = Arrays.copyOf(ra.w.getBytes(),
								ra.w.getLength());
						final StringBuffer sb = new StringBuffer();
						sb.append("m=0&iname=" + interfaceId);
						if (Data2SinkKafka.this.tdsortAttrs != null) {
							sb.append("&" + Data2SinkKafka.this.tdsortAttrs);
						}
						boolean containsDT = false;
						if (ra.attrs != null) {
							for (final Object k : ra.attrs.keySet()) {
								final String kstr = String.valueOf(k);
								if (kstr.equals("m") || kstr.equals("iname")
										|| kstr.equals("id")) {
									continue;
								}
								if (kstr.equals("dt")) {
									containsDT = true;
								}
								final String vstr = String.valueOf(ra.attrs
										.get(k));
								if (kstr.equals("t") && vstr.startsWith("1")) {
									// TODO its a hack just leave it here until
									// 2033
									continue;
								}
								sb.append("&").append(kstr).append("=")
										.append(vstr);
							}
						}
						if (!containsDT) {
							sb.append("&dt=" + System.currentTimeMillis());
						}
						return !p.addMsg(sb.toString(), data);
					}

					@Override
					public void emit(final String key, final TDMsg1 p,
							final boolean full) {
						producer.sendMsg(Data2SinkKafka.this.iid,
								p.buildArray());
						Data2SinkKafka.this.emitPackNum.incrementAndGet();
					}
				});
	}

	// @SuppressWarnings({ "rawtypes", "unchecked" })
	// @Override
	// public boolean finalize(Object row, ObjectInspector objectInspector,
	// ExprNodeEvaluator keyEvaluator, ObjectInspector keyInspector,
	// ExprNodeEvaluator attrsEvaluator, ObjectInspector attrsInspector,
	// int opTagIdx) {
	// try {
	// Map<Object, Object> attrs = null;
	// if (attrsEvaluator != null) {
	// attrs = (Map<Object, Object>) ObjectInspectorUtils
	// .copyToStandardObject(attrsEvaluator.evaluate(row),
	// attrsInspector);
	// }
	// Text w = (Text) serDe.serialize(row, objectInspector);
	// timerPackager.putTuple(new RowAttrs(w, attrs));
	// emitMsgNum.incrementAndGet();
	// } catch (SerDeException e) {
	// log.error(TDBankUtils.getExceptionStack(e));
	// } catch (HiveException e) {
	// log.error(TDBankUtils.getExceptionStack(e));
	// }
	//
	// return true;
	// }

	@SuppressWarnings("unchecked")
	@Override
	public boolean finalize(final Object row,
			final ObjectInspector objectInspector,
			final ObjectInspector keyInspector,
			final ObjectInspector attrsInspector, final int opTagIdx) {
		try {
			Map<Object, Object> attrs = null;
			if (attrsInspector != null) {
				final Object[] rs = (Object[]) row;
				attrs = (Map<Object, Object>) ObjectInspectorUtils
						.copyToStandardObject(rs[rs.length - 1], attrsInspector);
			}
			final Text w = (Text) this.serDe.serialize(row, objectInspector);
			this.timerPackager.putTuple(new RowAttrs(w, attrs));
			this.emitMsgNum.incrementAndGet();
		} catch (final SerDeException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}

		return true;
	}

	class RowAttrs {
		Text w;
		Map<Object, Object> attrs;

		public RowAttrs(final Text w, final Map<Object, Object> attrs) {
			this.w = w;
			this.attrs = attrs;
		}
	}

	@Override
	public void close() throws IOException {

	}
}
