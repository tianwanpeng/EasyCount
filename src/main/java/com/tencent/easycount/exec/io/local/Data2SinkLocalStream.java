package com.tencent.easycount.exec.io.local;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.ipc.RPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.exec.io.Data2Sink;
import com.tencent.easycount.metastore.TableUtils;
import com.tencent.easycount.plan.logical.OpDesc7FS;
import com.tencent.easycount.util.status.TDBankUtils;

public class Data2SinkLocalStream extends Data2Sink {
	private static Logger log = LoggerFactory
			.getLogger(Data2SinkLocalStream.class);
	private LocalModeProtocol tblserver;

	public Data2SinkLocalStream(final OpDesc7FS opDesc) {
		super(opDesc);
		try {
			this.tblserver = RPC.getProxy(LocalModeProtocol.class,
					LocalModeProtocol.versionID, new InetSocketAddress(
							TableUtils.getLocalTableAddr(opDesc.getTable()),
							TableUtils.getLocalTablePort(opDesc.getTable())),
							new Configuration());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean finalize(final Object row,
			final ObjectInspector objectInspector,
			final ObjectInspector keyInspector,
			final ObjectInspector attrsInspector, final int opTagIdx) {
		try {
			final Writable obj = this.serDe.serialize(row, objectInspector);
			this.tblserver.sendMsg(obj);
		} catch (final SerDeException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}

		return true;
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public void printStatus(final int printId) {

	}

}
