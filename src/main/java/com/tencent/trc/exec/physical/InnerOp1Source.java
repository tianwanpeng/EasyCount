package com.tencent.trc.exec.physical;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryFactory;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryStruct;
import org.apache.hadoop.hive.serde2.lazybinary.objectinspector.LazyBinaryStructObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;

import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.io.TaskContext;
import com.tencent.trc.exec.logical.Operator;
import com.tencent.trc.exec.utils.OIUtils;
import com.tencent.trc.plan.logical.OpDesc;

public class InnerOp1Source extends Operator<OpDesc> {

	@SuppressWarnings("unused")
	private String srcKey;
	private ByteArrayRef byteref;
	private LazyBinaryStruct lstruct;

	@Override
	public void printInternal(int printId) {
	}

	public InnerOp1Source(TrcConfiguration hconf, TaskContext taskContext,
			String srcKey, final int taskId, final int opTagIdx, TypeInfo type) {
		super(new OpDesc() {
			private static final long serialVersionUID = 2145312353392195025L;
			{
				setTaskId(taskId);
				setOpTagIdx(opTagIdx);
			}

			@Override
			public String getName() {
				return "SRC";
			}
		}, hconf, taskContext);
		getOpDesc().setOutputType(type);

		this.srcKey = srcKey;
	}

	@Override
	protected void initializeOp(TrcConfiguration hconf, TaskContext taskContext) {
		StructTypeInfo type = (StructTypeInfo) getOpDesc().getOutputType();

		outputObjInspector = OIUtils.createLazyBinaryStructInspector(type);
		lstruct = (LazyBinaryStruct) LazyBinaryFactory
				.createLazyBinaryObject((LazyBinaryStructObjectInspector) outputObjInspector);
		byteref = new ByteArrayRef();

		initializeChildren(hconf, taskContext);
	}

	@Override
	public void processOp(Object row, int tag) {

		byte[] data = (byte[]) row;
		// // TODO tmp
		// LazyBinaryStruct lstruct = (LazyBinaryStruct) LazyBinaryFactory
		// .createLazyBinaryObject((LazyBinaryStructObjectInspector)
		// outputObjInspector);
		// ByteArrayRef byteref = new ByteArrayRef();

		byteref.setData(data);
		lstruct.init(byteref, 0, data.length);

		forward(lstruct);
	}
}
