package com.tencent.easycount.exec.io.local;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.ipc.ProtocolSignature;

import com.tencent.easycount.conf.ECConfiguration;
import com.tencent.easycount.metastore.Table;

public class LocalTableServerKV extends LocalTableServer {

	private final ConcurrentHashMap<String, Writable> map;

	public LocalTableServerKV(final Table tbl, final ECConfiguration config) {
		super(tbl, config);
		this.map = new ConcurrentHashMap<String, Writable>();
	}

	@Override
	public boolean putMsg(final String key, final Writable data) {
		this.map.put(key, data);
		print(key, data, "NEW");
		return true;
	}

	@Override
	public Writable getMsg(final String key) {
		final Writable data = this.map.get(key);
		if (data != null) {
			print(key, data, "GET");
		}
		return data;
	}

	@Override
	public boolean sendMsg(final Writable data) {
		return false;
	}

	@Override
	public Writable nextMsg() {
		return null;
	}

	@Override
	public ProtocolSignature getProtocolSignature(final String protocol,
			final long clientVersion, final int clientMethodsHash)
					throws IOException {
		return null;
	}

}
