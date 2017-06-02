package com.tencent.easycount.exec.io.local;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.ipc.RPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.exec.io.Data2Sink;
import com.tencent.easycount.metastore.TableUtils;
import com.tencent.easycount.plan.logical.OpDesc7FS;
import com.tencent.easycount.util.status.TDBankUtils;

public class Data2SinkLocalKV extends Data2Sink {
	private static Logger log = LoggerFactory.getLogger(Data2SinkLocalKV.class);
	LocalModeProtocol tblserver;

	public Data2SinkLocalKV(OpDesc7FS opDesc) {
		super(opDesc);
		try {
			tblserver = (LocalModeProtocol) RPC.getProxy(
					LocalModeProtocol.class,
					LocalModeProtocol.versionID,
					new InetSocketAddress(TableUtils.getLocalTableAddr(opDesc
							.getTable()), TableUtils.getLocalTablePort(opDesc
							.getTable())), new Configuration());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean finalize(Object row, ObjectInspector objectInspector,
			ObjectInspector keyInspector, ObjectInspector attrsInspector,
			int opTagIdx) {
		try {
			Object[] rs = (Object[]) row;
			Object key = rs[rs.length - 1];
			if (attrsInspector != null) {
				key = rs[rs.length - 2];
			}
			Object stand_obj = ObjectInspectorUtils.copyToStandardObject(key,
					keyInspector);
			if (stand_obj == null) {
				log.error("key is null : " + key + " : " + row);
				return false;
			}

			Writable obj = serDe.serialize(row, objectInspector);
			tblserver.putMsg(stand_obj.toString(), obj);
		} catch (SerDeException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}

		return true;
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public void printStatus(int printId) {

	}

}
