package com.tencent.easycount.exec.io.inner;

import java.io.IOException;

import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.io.BytesWritable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.exec.io.Data2Sink;
import com.tencent.easycount.plan.logical.OpDesc7FS;
import com.tencent.easycount.util.status.TDBankUtils;

public class Data2SinkPrint extends Data2Sink {
	private static Logger log = LoggerFactory.getLogger(Data2SinkPrint.class);

	// FileOutputStream fos;

	public Data2SinkPrint(final OpDesc7FS opDesc) {
		super(opDesc);
		// try {
		// fos = new FileOutputStream(new File("testdata.dat"));
		// } catch (FileNotFoundException e) {
		// log.error(TDBankUtils.getExceptionStack(e));
		// }
	}

	@Override
	public boolean finalize(final Object row,
			final ObjectInspector objectInspector,
			final ObjectInspector keyInspector,
			final ObjectInspector attrsInspector, final int opTagIdx) {
		try {
			final Object obj = this.serDe.serialize(row, objectInspector);
			if (obj instanceof BytesWritable) {
				log.error("finalize : " + (this.opDesc.getTaskId_OpTagIdx())
						+ " : " + new String(((BytesWritable) obj).getBytes())
						+ " " + (System.currentTimeMillis() / 1000));
				// BytesWritable data = ((BytesWritable) obj);
				// try {
				// fos.write(data.getBytes(), 0, data.getLength());
				// fos.write('\n');
				// fos.flush();
				// } catch (IOException e) {
				// log.error(TDBankUtils.getExceptionStack(e));
				// }
			} else {
				log.error("finalize : " + (this.opDesc.getTaskId_OpTagIdx())
						+ " : " + obj.toString() + " "
						+ (System.currentTimeMillis() / 1000));
				// try {
				// fos.write(obj.toString().getBytes());
				// fos.write('\n');
				// fos.flush();
				// } catch (IOException e) {
				// log.error(TDBankUtils.getExceptionStack(e));
				// }
			}
		} catch (final SerDeException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}

		return true;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void printStatus(final int printId) {
		// TODO Auto-generated method stub

	}

}
