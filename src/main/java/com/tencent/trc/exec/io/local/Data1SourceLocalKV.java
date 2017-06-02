package com.tencent.trc.exec.io.local;

import java.io.IOException;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.io.Data1Source;
import com.tencent.trc.exec.physical.Data1Generator;
import com.tencent.trc.plan.logical.OpDesc1TS;

public class Data1SourceLocalKV extends Data1Source {

	public Data1SourceLocalKV(String taskId_OpTagIdx, OpDesc1TS opDesc,
			Data1Generator msgEmitter, TrcConfiguration hconf) {
		super(taskId_OpTagIdx, opDesc, msgEmitter);
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public void printStatus(int printId) {

	}

	@Override
	public ObjectInspector getObjectInspector() {
		return null;
	}

	@Override
	public void start() {

	}

	@Override
	public void restart() throws Exception {

	}

}
