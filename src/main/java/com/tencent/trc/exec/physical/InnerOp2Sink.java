package com.tencent.trc.exec.physical;

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

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.io.TaskContext;
import com.tencent.trc.exec.logical.Operator;
import com.tencent.trc.plan.logical.OpDesc;
import com.tencent.trc.plan.logical.OpDesc5GBY;

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
	public void printInternal(int printId) {
		boolean print = false;
		if (print) {
			log.info(String.valueOf(printId));
		}
	}

	public InnerOp2Sink(TrcConfiguration hconf, TaskContext taskContext,
			OpDesc opDesc, OpDesc childOpDesc, CallBack callback) {
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

		getOpDesc().setTaskId(taskId);

		try {
			this.serDe = new LazyBinarySerDe();
		} catch (SerDeException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}

		if ("MGBY".equals(opDesc.getName())) {
			keySize = ((OpDesc5GBY) opDesc).getGroupByKeys().size() + 1;
		}
	}

	@Override
	public void processOp(Object row, int tag) {
		try {
			// log.info(taskId + "-" + opTagIdx + " : " + row.toString());
			callback.call(generateWrapperMsg(row, tag));
		} catch (Exception e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}
	}

	private WrapperMsg generateWrapperMsg(Object row, int tag) {
		try {
			byte[] data = null;
			Object obj = serDe.serialize(row, inputObjInspectors[tag]);
			if (obj instanceof BytesWritable) {
				BytesWritable bw = (BytesWritable) obj;
				data = Arrays.copyOf(bw.getBytes(), bw.getLength());
			} else {
				Text w = (Text) obj;
				data = Arrays.copyOf(w.getBytes(), w.getLength());
			}
			int key = getKey(row, tag);
			return new WrapperMsg(taskId, opTagIdx, targetTaskId,
					targetOpTagIdx, key, data);
		} catch (Exception e) {
			log.error(getName() + " : " + getOpDesc().getTaskId_OpTagIdx()
					+ " : row : " + row.toString());
			log.error(TDBankUtils.getExceptionStack(e));
		}
		return null;
	}

	private int getKey(Object row, int tag) {
		int resHashCode = 0;
		if (keySize > 0) {
			List<Object> objs = ((StructObjectInspector) inputObjInspectors[tag])
					.getStructFieldsDataAsList(row);

			Iterator<Object> it = objs.iterator();
			for (int i = 0; i < keySize; i++) {
				Object obj = it.next();
				resHashCode = 31 * resHashCode
						+ (obj == null ? 0 : obj.hashCode());
			}
			// Object lastObj = objs.get(objs.size() - 1);
			// resHashCode = 31 * resHashCode
			// + (lastObj == null ? 0 : lastObj.hashCode());
		} else {
			resHashCode = r.nextInt() % 1801;
		}
		return resHashCode % 1801;
	}

	public static interface CallBack {
		public boolean call(WrapperMsg msg);
	}

}
