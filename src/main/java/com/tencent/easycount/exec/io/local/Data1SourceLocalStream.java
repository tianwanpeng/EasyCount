package com.tencent.easycount.exec.io.local;

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

import com.tencent.easycount.conf.ECConfiguration;
import com.tencent.easycount.exec.io.Data1Source;
import com.tencent.easycount.exec.physical.Data1Generator;
import com.tencent.easycount.exec.physical.Task.SOCallBack;
import com.tencent.easycount.exec.utils.OIUtils;
import com.tencent.easycount.metastore.Table;
import com.tencent.easycount.metastore.TableUtils;
import com.tencent.easycount.plan.logical.OpDesc1TS;

public class Data1SourceLocalStream extends Data1Source {

	private final ObjectInspector objectInspector;
	private LocalModeProtocol tblserver;
	private final Thread msgtransfer;

	@Override
	public void printStatus(final int printId) {

	}

	public Data1SourceLocalStream(final String sourceId,
			final OpDesc1TS opDesc, final Data1Generator msgEmitter,
			final ECConfiguration hconf) {
		super(sourceId, opDesc, msgEmitter);
		final Table tbl = this.opdesc.getTable();
		this.objectInspector = OIUtils.createLazyStructInspector(tbl);
		try {
			this.tblserver = RPC.getProxy(LocalModeProtocol.class,
					LocalModeProtocol.versionID, new InetSocketAddress(
							TableUtils.getLocalTableAddr(opDesc.getTable()),
							TableUtils.getLocalTablePort(opDesc.getTable())),
					new Configuration());
		} catch (final IOException e) {
			e.printStackTrace();
		}

		final LazyStruct ls = new LazyStruct(
				(LazySimpleStructObjectInspector) this.objectInspector);
		final ByteArrayRef bytesref = new ByteArrayRef();

		this.msgtransfer = new Thread() {

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
						final Writable obj = Data1SourceLocalStream.this.tblserver
								.nextMsg();
						if (obj == null) {
							Thread.sleep(100);
							continue;
						}
						if (obj instanceof Text) {
							while (!this.sendok.get()) {
								Thread.sleep(1);
							}
							bytesref.setData(obj.toString().getBytes());
							ls.init(bytesref, 0, bytesref.getData().length);
							emit(ls, this.socb);
							this.sendok.set(false);
						} else {
							emit(obj, null);
						}
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			}
		};

	}

	@Override
	public ObjectInspector getObjectInspector() {
		return this.objectInspector;
	}

	@Override
	public void start() {
		this.msgtransfer.start();
	}

	@Override
	public void restart() throws Exception {

	}

	@Override
	public void close() throws IOException {

	}

}
