package com.tencent.easycount.exec.physical;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinarySerDe;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.conf.ECConfiguration;
import com.tencent.easycount.exec.io.TaskContext;
import com.tencent.easycount.exec.logical.Operator;
import com.tencent.easycount.plan.logical.OpDesc;
import com.tencent.easycount.plan.logical.OpDesc5GBY;
import com.tencent.easycount.util.status.TDBankUtils;

public class InnerOp2Sink extends Operator<OpDesc> {
	private static Logger log = LoggerFactory.getLogger(InnerOp2Sink.class);

	final private CallBack callback;

	private LazyBinarySerDe serDe = null;
	private int taskId;
	private int opTagIdx;
	private int targetTaskId;
	private int targetOpTagIdx;

	private int keySize = -1;
	Random r = new Random(123456);

	@Override
	public void printInternal(final int printId) {
		final boolean print = false;
		if (print) {
			log.info(String.valueOf(printId));
		}
	}

	public InnerOp2Sink(final ECConfiguration hconf,
			final TaskContext taskContext, final OpDesc opDesc,
			final OpDesc childOpDesc, final CallBack callback) {
		super(new OpDesc() {
			private static final long serialVersionUID = -1457896484655601258L;

			@Override
			public String getName() {
				return "SINK";
			}
		}, hconf, taskContext);
		this.callback = callback;

		this.taskId = opDesc.getTaskId();
		this.opTagIdx = opDesc.getOpTagIdx();
		this.targetTaskId = childOpDesc.getTaskId();
		this.targetOpTagIdx = childOpDesc.getOpTagIdx();

		getOpDesc().setTaskId(this.taskId);

		try {
			this.serDe = new LazyBinarySerDe();
		} catch (final SerDeException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}

		if ("MGBY".equals(opDesc.getName())) {
			this.keySize = ((OpDesc5GBY) opDesc).getGroupByKeys().size() + 1;
		}
	}

	@Override
	public void processOp(final Object row, final int tag) {
		try {
			// log.info(taskId + "-" + opTagIdx + " : " + row.toString());
			this.callback.call(generateWrapperMsg(row, tag));
		} catch (final Exception e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}
	}

	private WrapperMsg generateWrapperMsg(final Object row, final int tag) {
		try {
			byte[] data = null;
			final Object obj = this.serDe.serialize(row,
					this.inputObjInspectors[tag]);
			if (obj instanceof BytesWritable) {
				final BytesWritable bw = (BytesWritable) obj;
				data = Arrays.copyOf(bw.getBytes(), bw.getLength());
			} else {
				final Text w = (Text) obj;
				data = Arrays.copyOf(w.getBytes(), w.getLength());
			}
			final int key = getKey(row, tag);
			return new WrapperMsg(this.taskId, this.opTagIdx,
					this.targetTaskId, this.targetOpTagIdx, key, data);
		} catch (final Exception e) {
			log.error(getName() + " : " + getOpDesc().getTaskId_OpTagIdx()
					+ " : row : " + row.toString());
			log.error(TDBankUtils.getExceptionStack(e));
		}
		return null;
	}

	private int getKey(final Object row, final int tag) {
		int resHashCode = 0;
		if (this.keySize > 0) {
			final List<Object> objs = ((StructObjectInspector) this.inputObjInspectors[tag])
					.getStructFieldsDataAsList(row);

			final Iterator<Object> it = objs.iterator();
			for (int i = 0; i < this.keySize; i++) {
				final Object obj = it.next();
				resHashCode = (31 * resHashCode)
						+ (obj == null ? 0 : obj.hashCode());
			}
			// Object lastObj = objs.get(objs.size() - 1);
			// resHashCode = 31 * resHashCode
			// + (lastObj == null ? 0 : lastObj.hashCode());
		} else {
			resHashCode = this.r.nextInt() % 1801;
		}
		return resHashCode % 1801;
	}

	public static interface CallBack {
		public boolean call(WrapperMsg msg);
	}

}
