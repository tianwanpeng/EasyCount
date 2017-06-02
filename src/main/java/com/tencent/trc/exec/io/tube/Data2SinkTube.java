package com.tencent.trc.exec.io.tube;

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

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.tdbank.msg.TDMsg1;
import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.io.Data2Sink;
import com.tencent.trc.metastore.Table;
import com.tencent.trc.metastore.TableUtils;
import com.tencent.trc.plan.logical.OpDesc7FS;
import com.tencent.trc.util.exec.TimerPackager;
import com.tencent.trc.util.exec.TimerPackager.Packager;

public class Data2SinkTube extends Data2Sink {
	private static Logger log = LoggerFactory.getLogger(Data2SinkTube.class);

	private final String iid;
	private final String tdsortAttrs;

	private final AtomicLong emitMsgNum;
	private final AtomicLong emitPackNum;

	// final private TDStationProducer producer;
	final private TimerPackager<RowAttrs, TDMsg1> timerPackager;

	@Override
	public void printStatus(int printId) {
		log.info(opDesc.getTaskId_OpTagIdx() + " : " + iid + " : emitMsgNum : "
				+ emitMsgNum.get() + " : emitPackNum : " + emitPackNum.get());
	}

	public Data2SinkTube(TrcConfiguration hconf, OpDesc7FS opDesc,
			final TubeProducer producer) {
		super(opDesc);
		// this.producer = producer;
		Table tbl = opDesc.getTable();
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

		timerPackager = new TimerPackager<RowAttrs, TDMsg1>(
				timeout_seconds * 1000, new Packager<RowAttrs, TDMsg1>() {

					@Override
					public String getKey(RowAttrs t) {
						return iid;
					}

					@Override
					public TDMsg1 newPackage(RowAttrs t) {
						return TDMsg1.newTDMsg(packsize, compress);
					}

					@Override
					public boolean pack(String key, RowAttrs ra, TDMsg1 p) {
						byte[] data = Arrays.copyOf(ra.w.getBytes(),
								ra.w.getLength());
						StringBuffer sb = new StringBuffer();
						sb.append("m=0&iname=" + interfaceId);
						if (tdsortAttrs != null) {
							sb.append("&" + tdsortAttrs);
						}
						boolean containsDT = false;
						if (ra.attrs != null) {
							for (Object k : ra.attrs.keySet()) {
								String kstr = String.valueOf(k);
								if (kstr.equals("m") || kstr.equals("iname")
										|| kstr.equals("id")) {
									continue;
								}
								if (kstr.equals("dt")) {
									containsDT = true;
								}
								String vstr = String.valueOf(ra.attrs.get(k));
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
					public void emit(String key, TDMsg1 p, boolean full) {
						producer.sendMsg(p.buildArray());
						emitPackNum.incrementAndGet();
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
	public boolean finalize(Object row, ObjectInspector objectInspector,
			ObjectInspector keyInspector, ObjectInspector attrsInspector,
			int opTagIdx) {
		try {
			Map<Object, Object> attrs = null;
			if (attrsInspector != null) {
				Object[] rs = (Object[]) row;
				attrs = (Map<Object, Object>) ObjectInspectorUtils
						.copyToStandardObject(rs[rs.length - 1], attrsInspector);
			}
			Text w = (Text) serDe.serialize(row, objectInspector);
			timerPackager.putTuple(new RowAttrs(w, attrs));
			emitMsgNum.incrementAndGet();
		} catch (SerDeException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}

		return true;
	}

	class RowAttrs {
		Text w;
		Map<Object, Object> attrs;

		public RowAttrs(Text w, Map<Object, Object> attrs) {
			this.w = w;
			this.attrs = attrs;
		}
	}

	@Override
	public void close() throws IOException {

	}
}
