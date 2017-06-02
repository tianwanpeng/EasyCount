package com.tencent.trc.exec.io.local;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.hadoop.io.Writable;

import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.metastore.Table;

public class LocalTableServerStream extends LocalTableServer {

	private LinkedBlockingQueue<Writable> queue;

	public LocalTableServerStream(Table tbl, TrcConfiguration config) {
		super(tbl, config);
		this.queue = new LinkedBlockingQueue<Writable>();
	}

	@Override
	public boolean sendMsg(Writable data) {
		queue.add(data);
		print(null, data, "NEW");
		return true;
	}

	@Override
	public Writable nextMsg() {
		Writable data = queue.poll();
		if (data != null) {
			print(null, data, "GET");
		}
		return data;
	}

	@Override
	public boolean putMsg(String key, Writable data) {
		return false;
	}

	@Override
	public Writable getMsg(String key) {
		return null;
	}

}
