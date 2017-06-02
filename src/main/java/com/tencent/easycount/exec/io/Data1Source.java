package com.tencent.easycount.exec.io;

import java.io.Closeable;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

import com.tencent.easycount.exec.physical.Data1Generator;
import com.tencent.easycount.exec.physical.Task.SOCallBack;
import com.tencent.easycount.plan.logical.OpDesc1TS;
import com.tencent.easycount.util.status.StatusPrintable;

public abstract class Data1Source implements Closeable, StatusPrintable {

	final private String sourceId;
	final protected OpDesc1TS opdesc;
	final private Data1Generator msgEmitter;

	public Data1Source(String sourceId, OpDesc1TS opdesc,
			Data1Generator msgEmitter) {
		this.sourceId = sourceId;
		this.opdesc = opdesc;
		this.msgEmitter = msgEmitter;
	}

	/**
	 * this method must be called by sub class to emit the object
	 * 
	 * @param obj
	 * @param attrs
	 * @return
	 */
	public boolean emit(Object obj, SOCallBack socb) {
		return msgEmitter.emit(sourceId, obj, socb);
	}

	public abstract ObjectInspector getObjectInspector();

	public abstract void start();

	public String getSourceId() {
		return sourceId;
	}

	public String getTableId() {
		return opdesc.getTable().getTableName();
	}

	public Data1Generator getMsgEmitter() {
		return msgEmitter;
	}

	public OpDesc1TS getOpDesc() {
		return opdesc;
	}

	public abstract void restart() throws Exception;
}
