package com.tencent.trc.exec.io.inner;

import java.io.IOException;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.lazy.LazyStruct;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazySimpleStructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.io.Data1Source;
import com.tencent.trc.exec.physical.Data1Generator;
import com.tencent.trc.exec.utils.OIUtils;
import com.tencent.trc.metastore.TableInner;
import com.tencent.trc.plan.logical.OpDesc1TS;

public class Data1SourceInner extends Data1Source {
	private static Logger log = LoggerFactory.getLogger(Data1SourceInner.class);

	private ObjectInspector objectInspector;

	@Override
	public void printStatus(int printId) {

	}

	private final TableInner table;
	private final LazyStruct lstruct;
	private int ms;

	public Data1SourceInner(String sourceId, OpDesc1TS opdesc,
			Data1Generator msgEmitter, TrcConfiguration hconf) {
		super(sourceId, opdesc, msgEmitter);
		objectInspector = OIUtils.createLazyStructInspector(opdesc.getTable());
		table = (TableInner) opdesc.getTable();

		ByteArrayRef byteref = new ByteArrayRef();
		lstruct = new LazyStruct(
				(LazySimpleStructObjectInspector) objectInspector);

		byteref.setData("".getBytes());
		lstruct.init(byteref, 0, 0);
		ms = table.getMs();
		if (ms <= 0) {
			ms = 1000;
		}

	}

	@Override
	public ObjectInspector getObjectInspector() {
		return objectInspector;
	}

	@Override
	public void start() {
		new Thread("Data1SourceInner-" + table.getTableName()) {

			// AtomicBoolean sendok = new AtomicBoolean(true);
			// SOCallBack socb = new SOCallBack() {
			// @Override
			// public void finish() {
			// sendok.set(true);
			// }
			//
			// @Override
			// public void await() {
			//
			// }
			// };

			public void run() {
				long yyy = 0;
				while (true) {
					try {
						emit(lstruct, null);
						// sendok.set(false);
						yyy++;
						if (shouldprint()) {
							log.info(table.getTableName() + " : " + yyy + " : "
									+ ms);
						}
						Thread.sleep(ms);
						// while (!sendok.get()) {
						// Thread.sleep(1);
						// }
					} catch (Throwable e) {
						log.warn(TDBankUtils.getExceptionStack(e));
					}
				}
			}

			long lasttime = System.currentTimeMillis();

			private boolean shouldprint() {
				long ctime = System.currentTimeMillis();
				if (ctime - lasttime > 3000) {
					lasttime = ctime;
					return true;
				}
				return false;
			}
		}.start();
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public void restart() throws Exception {

	}
}
