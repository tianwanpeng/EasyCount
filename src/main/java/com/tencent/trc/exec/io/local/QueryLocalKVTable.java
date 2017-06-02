package com.tencent.trc.exec.io.local;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.ipc.RPC;

import com.tencent.trc.exec.io.Queryable;
import com.tencent.trc.exec.utils.OIUtils;
import com.tencent.trc.metastore.Table;
import com.tencent.trc.metastore.TableUtils;

public class QueryLocalKVTable implements Queryable {
	private StructObjectInspector objectInspector;

	@Override
	public void printStatus(int printId) {

	}

	private LocalModeProtocol tblserver;

	public QueryLocalKVTable(Table tbl) {
		objectInspector = OIUtils.createLazyStructInspector(tbl);
		try {
			tblserver = (LocalModeProtocol) RPC.getProxy(
					LocalModeProtocol.class, LocalModeProtocol.versionID,
					new InetSocketAddress(TableUtils.getLocalTableAddr(tbl),
							TableUtils.getLocalTablePort(tbl)),
					new Configuration());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void get(String key, CallBack cb) {
		Object obj = tblserver.getMsg(key);
		cb.callback(new Object[] { obj });
	}

	@Override
	public ObjectInspector getObjectInspector() {
		return objectInspector;
	}
}
