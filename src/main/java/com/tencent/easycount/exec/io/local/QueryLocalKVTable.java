package com.tencent.easycount.exec.io.local;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.ipc.RPC;

import com.tencent.easycount.exec.io.Queryable;
import com.tencent.easycount.exec.utils.OIUtils;
import com.tencent.easycount.metastore.Table;
import com.tencent.easycount.metastore.TableUtils;

public class QueryLocalKVTable implements Queryable {
	private final StructObjectInspector objectInspector;

	@Override
	public void printStatus(final int printId) {

	}

	private LocalModeProtocol tblserver;

	public QueryLocalKVTable(final Table tbl) {
		this.objectInspector = OIUtils.createLazyStructInspector(tbl);
		try {
			this.tblserver = RPC.getProxy(LocalModeProtocol.class,
					LocalModeProtocol.versionID,
					new InetSocketAddress(TableUtils.getLocalTableAddr(tbl),
							TableUtils.getLocalTablePort(tbl)),
							new Configuration());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void get(final String key, final CallBack cb) {
		final Object obj = this.tblserver.getMsg(key);
		cb.callback(new Object[] { obj });
	}

	@Override
	public ObjectInspector getObjectInspector() {
		return this.objectInspector;
	}
}
