package com.tencent.easycount.exec.io.local;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.hadoop.io.Writable;

import com.tencent.easycount.conf.TrcConfiguration;
import com.tencent.easycount.metastore.Table;

public class LocalTableServerStream extends LocalTableServer {

	private final LinkedBlockingQueue<Writable> queue;

	public LocalTableServerStream(final Table tbl, final TrcConfiguration config) {
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

}
