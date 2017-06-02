package com.tencent.trc.exec.io.inner;

import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.lazy.LazyStruct;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazySimpleStructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.io.Data1Source;
import com.tencent.trc.exec.physical.Data1Generator;
import com.tencent.trc.exec.utils.OIUtils;
import com.tencent.trc.plan.logical.OpDesc1TS;

public class Data1SourceTest extends Data1Source {

	private ObjectInspector objectInspector;
	private char splitter = '\t';

	@Override
	public void printStatus(int printId) {

	}

	public Data1SourceTest(String sourceId, OpDesc1TS opdesc,
			Data1Generator msgEmitter, TrcConfiguration hconf) {
		super(sourceId, opdesc, msgEmitter);

		objectInspector = OIUtils.createLazyStructInspector(opdesc.getTable());
	}

	@Override
	public ObjectInspector getObjectInspector() {
		return objectInspector;
	}

	@Override
	public void start() {
		new Thread("Data1SourceTest") {
			public void run() {
				long xxx = 0;
				long yyy = 0;
				while (true) {
					try {
						String row = generateRowPath();
						byte[] data = row.getBytes();
						ByteArrayRef byteref = new ByteArrayRef();
						LazyStruct lstruct = new LazyStruct(
								(LazySimpleStructObjectInspector) objectInspector);

						byteref.setData(row.getBytes());
						lstruct.init(byteref, 0, data.length);
						emit(lstruct, null);
						xxx++;
						if (xxx % 1 == 0) {
							sleep(10);
							if ((++yyy) % 100 == 0) {
								System.err.println("generate : " + row);
							}
						}

					} catch (Exception e) {
					}
				}
			}

			Random r = new Random();

			@SuppressWarnings("unused")
			private String generateRow() {
				StringBuffer sb = new StringBuffer();
				// sb.append(r.nextBoolean() ? "" : String
				// .valueOf(TestDataSource.this.getSourceId())
				// + (char) (r.nextInt(1) + 'a'));
				sb.append(r.nextInt(5));
				int x = r.nextInt(5) + 10;
				sb.append(splitter).append(x);
				sb.append(splitter).append(x % 3);
				int y = r.nextInt(5) + 13;
				sb.append(splitter).append(y);
				sb.append(splitter).append(y % 3);
				sb.append(splitter).append(System.currentTimeMillis());
				// sb.append(splitter).append("test");
				return sb.toString();
			}

			@SuppressWarnings("unused")
			private String generateRowRedis() {
				StringBuffer sb = new StringBuffer();
				sb.append(r.nextInt(10));
				sb.append(splitter)
						.append(String.valueOf(r.nextInt(100000000)));
				sb.append(splitter).append(String.valueOf(r.nextInt(3) + 1000));
				sb.append(splitter).append(String.valueOf(r.nextInt(3) + 2000));
				sb.append(splitter).append(String.valueOf(r.nextInt(20)));// country
				sb.append(splitter).append(String.valueOf(r.nextInt(2)));// gender
				sb.append(splitter).append(String.valueOf(r.nextInt(10) + 10));// age
				sb.append(splitter).append(String.valueOf(r.nextInt(5)));// scene
				sb.append(splitter).append(System.currentTimeMillis() / 1000);
				return sb.toString();
			}

			private String generateRowPath() {
				StringBuffer sb = new StringBuffer();
				sb.append(System.currentTimeMillis() / 1000);

				int x = r.nextInt(100) + 1000;
				int y = r.nextInt(100) + 1000;
				int xtype = x % 100 <= 10 ? 3 : 0;
				int ytype = y % 100 <= 10 ? 3 : 0;

				sb.append(splitter).append(String.valueOf(x));
				sb.append(splitter).append(String.valueOf(y));
				sb.append(splitter).append(String.valueOf(xtype));
				sb.append(splitter).append(String.valueOf(ytype));
				sb.append(splitter).append(
						String.valueOf(r.nextInt(1000) + 5000000));
				sb.append(splitter).append(r.nextInt(10) - 3);

				return sb.toString();
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
