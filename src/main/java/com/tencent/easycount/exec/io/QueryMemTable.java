package com.tencent.easycount.exec.io;

import java.util.HashMap;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.lazy.LazyStruct;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazySimpleStructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

import com.tencent.easycount.exec.utils.OIUtils;
import com.tencent.easycount.metastore.TableMem;

public class QueryMemTable implements Queryable {

	final byte[] data;
	final private LazySimpleStructObjectInspector objectInspector;
	final private HashMap<Object, LazyStruct> memData;
	final private TableMem tbl;

	@Override
	public void printStatus(int printId) {
		// TODO Auto-generated method stub

	}

	public QueryMemTable(TableMem tbl) {
		this.tbl = tbl;
		data = tbl.getData();

		objectInspector = OIUtils.createLazyStructInspector(tbl);

		memData = new HashMap<Object, LazyStruct>();
		initializeMemData();
	}

	private void initializeMemData() {
		if (data == null || data.length == 0) {
			return;
		}
		ByteArrayRef bytesref = new ByteArrayRef();
		bytesref.setData(data);
		int pos = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] == '\n') {
				generateLazyStruct(bytesref, pos, i - pos - 1);
				pos = i + 1;
			}
		}
		generateLazyStruct(bytesref, pos, data.length - pos - 1);
	}

	private void generateLazyStruct(ByteArrayRef bytesref, int pos, int length) {
		LazyStruct ls = new LazyStruct(objectInspector);
		ls.init(bytesref, pos, length);

		Object keyobj = objectInspector.getStructFieldData(ls, objectInspector
				.getStructFieldRef(tbl.getKeyField().getColumnName()));

		memData.put(keyobj.toString(), ls);
	}

	@Override
	public void get(String key, CallBack cb) {
		cb.callback(new Object[] { memData.get(key) });
	}

	@Override
	public ObjectInspector getObjectInspector() {
		return objectInspector;
	}

}
