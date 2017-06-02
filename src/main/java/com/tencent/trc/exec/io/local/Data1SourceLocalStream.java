package com.tencent.trc.exec.io.local;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.lazy.LazyStruct;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazySimpleStructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.ipc.RPC;

import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.io.Data1Source;
import com.tencent.trc.exec.physical.Data1Generator;
import com.tencent.trc.exec.physical.Task.SOCallBack;
import com.tencent.trc.exec.utils.OIUtils;
import com.tencent.trc.metastore.Table;
import com.tencent.trc.metastore.TableUtils;
import com.tencent.trc.plan.logical.OpDesc1TS;

public class Data1SourceLocalStream extends Data1Source {

	private ObjectInspector objectInspector;
	private LocalModeProtocol tblserver;
	private Thread msgtransfer;

	@Override
	public void printStatus(int printId) {

	}

	public Data1SourceLocalStream(String sourceId, OpDesc1TS opDesc,
			Data1Generator msgEmitter, TrcConfiguration hconf) {
		super(sourceId, opDesc, msgEmitter);
		Table tbl = opdesc.getTable();
		objectInspector = OIUtils.createLazyStructInspector(tbl);
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

		final LazyStruct ls = new LazyStruct(
				(LazySimpleStructObjectInspector) objectInspector);
		final ByteArrayRef bytesref = new ByteArrayRef();

		msgtransfer = new Thread() {

			AtomicBoolean sendok = new AtomicBoolean(true);
			SOCallBack socb = new SOCallBack() {
				@Override
				public void finish() {
					sendok.set(true);
				}

				@Override
				public void await() {
				}
			};

			@Override
			public void run() {
				while (true) {
					try {
						Writable obj = tblserver.nextMsg();
						if (obj == null) {
							Thread.sleep(100);
							continue;
						}
						if (obj instanceof Text) {
							while (!sendok.get()) {
								Thread.sleep(1);
							}
							bytesref.setData(obj.toString().getBytes());
							ls.init(bytesref, 0, bytesref.getData().length);
							emit(ls, socb);
							sendok.set(false);
						} else {
							emit(obj, null);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};

	}

	@Override
	public ObjectInspector getObjectInspector() {
		return objectInspector;
	}

	@Override
	public void start() {
		msgtransfer.start();
	}

	@Override
	public void restart() throws Exception {

	}

	@Override
	public void close() throws IOException {

	}

}
