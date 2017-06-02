package com.tencent.easycount.exec.io;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

import com.tencent.easycount.util.status.StatusPrintable;

public interface Queryable extends StatusPrintable {
	public void get(String key, CallBack cb);

	public ObjectInspector getObjectInspector();

	public static interface CallBack {
		public void callback(Object[] rows);

		public void fail();
	}
}
