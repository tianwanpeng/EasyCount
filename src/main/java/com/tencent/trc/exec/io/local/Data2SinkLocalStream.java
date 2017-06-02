package com.tencent.trc.exec.io.local;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.ipc.RPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.trc.exec.io.Data2Sink;
import com.tencent.trc.metastore.TableUtils;
import com.tencent.trc.plan.logical.OpDesc7FS;

public class Data2SinkLocalStream extends Data2Sink {
	private static Logger log = LoggerFactory
			.getLogger(Data2SinkLocalStream.class);
	private LocalModeProtocol tblserver;

	public Data2SinkLocalStream(OpDesc7FS opDesc) {
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
			Writable obj = serDe.serialize(row, objectInspector);
			tblserver.sendMsg(obj);
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
