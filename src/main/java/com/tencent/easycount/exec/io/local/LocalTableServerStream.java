package com.tencent.easycount.exec.io.local;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.ipc.ProtocolSignature;

import com.tencent.easycount.conf.ECConfiguration;
import com.tencent.easycount.metastore.Table;

public class LocalTableServerStream extends LocalTableServer {

	private final LinkedBlockingQueue<Writable> queue;

	public LocalTableServerStream(final Table tbl, final ECConfiguration config) {
		super(tbl, config);
		this.queue = new LinkedBlockingQueue<Writable>();
	}

	@Override
	public boolean sendMsg(final Writable data) {
		this.queue.add(data);
		print(null, data, "NEW");
		return true;
	}

	@Override
	public Writable nextMsg() {
		final Writable data = this.queue.poll();
		if (data != null) {
			print(null, data, "GET");
		}
		return data;
	}

	@Override
	public boolean putMsg(final String key, final Writable data) {
		return false;
	}

	@Override
	public Writable getMsg(final String key) {
		return null;
	}

	@Override
	public ProtocolSignature getProtocolSignature(final String protocol,
			final long clientVersion, final int clientMethodsHash)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
