package com.tencent.easycount.exec.io.local;

import java.io.IOException;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

import com.tencent.easycount.conf.TrcConfiguration;
import com.tencent.easycount.exec.io.Data1Source;
import com.tencent.easycount.exec.physical.Data1Generator;
import com.tencent.easycount.plan.logical.OpDesc1TS;

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
