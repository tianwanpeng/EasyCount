package com.tencent.trc.exec.io.local;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.io.Writable;

import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.metastore.Table;

public class LocalTableServerKV extends LocalTableServer {

	private ConcurrentHashMap<String, Writable> map;

	public LocalTableServerKV(Table tbl, TrcConfiguration config) {
		super(tbl, config);
		this.map = new ConcurrentHashMap<String, Writable>();
	}

	@Override
	public boolean putMsg(String key, Writable data) {
		this.map.put(key, data);
		print(key, data, "NEW");
		return true;
	}

	@Override
	public Writable getMsg(String key) {
		Writable data = map.get(key);
		if (data != null) {
			print(key, data, "GET");
		}
		return data;
	}

	@Override
	public boolean sendMsg(Writable data) {
		return false;
	}

	@Override
	public Writable nextMsg() {
		return null;
	}

}
