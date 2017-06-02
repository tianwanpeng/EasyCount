package com.tencent.easycount.exec.physical;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryFactory;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryStruct;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;

import com.tencent.easycount.conf.TrcConfiguration;
import com.tencent.easycount.exec.io.TaskContext;
import com.tencent.easycount.exec.logical.Operator;
import com.tencent.easycount.exec.utils.OIUtils;
import com.tencent.easycount.plan.logical.OpDesc;

public class InnerOp1Source extends Operator<OpDesc> {

	@SuppressWarnings("unused")
	private String srcKey;
	private ByteArrayRef byteref;
	private LazyBinaryStruct lstruct;

	@Override
	public void printInternal(final int printId) {
	}

	public InnerOp1Source(final TrcConfiguration hconf,
			final TaskContext taskContext, final String srcKey,
			final int taskId, final int opTagIdx, final TypeInfo type) {
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
	protected void initializeOp(final TrcConfiguration hconf,
			final TaskContext taskContext) {
		final StructTypeInfo type = (StructTypeInfo) getOpDesc()
				.getOutputType();

		this.outputObjInspector = OIUtils.createLazyBinaryStructInspector(type);
		this.lstruct = (LazyBinaryStruct) LazyBinaryFactory
				.createLazyBinaryObject(this.outputObjInspector);
		this.byteref = new ByteArrayRef();

		initializeChildren(hconf, taskContext);
	}

	@Override
	public void processOp(final Object row, final int tag) {

		final byte[] data = (byte[]) row;
		// // TODO tmp
		// LazyBinaryStruct lstruct = (LazyBinaryStruct) LazyBinaryFactory
		// .createLazyBinaryObject((LazyBinaryStructObjectInspector)
		// outputObjInspector);
		// ByteArrayRef byteref = new ByteArrayRef();

		this.byteref.setData(data);
		this.lstruct.init(this.byteref, 0, data.length);

		forward(this.lstruct);
	}
}
