package com.tencent.trc.exec.io.inner;

import java.io.IOException;

import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.io.BytesWritable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.trc.exec.io.Data2Sink;
import com.tencent.trc.plan.logical.OpDesc7FS;

public class Data2SinkPrint extends Data2Sink {
	private static Logger log = LoggerFactory.getLogger(Data2SinkPrint.class);

	// FileOutputStream fos;

	public Data2SinkPrint(OpDesc7FS opDesc) {
		super(opDesc);
		// try {
		// fos = new FileOutputStream(new File("testdata.dat"));
		// } catch (FileNotFoundException e) {
		// log.error(TDBankUtils.getExceptionStack(e));
		// }
	}

	@Override
	public boolean finalize(Object row, ObjectInspector objectInspector,
			ObjectInspector keyInspector, ObjectInspector attrsInspector,
			int opTagIdx) {
		try {
			Object obj = serDe.serialize(row, objectInspector);
			if (obj instanceof BytesWritable) {
				log.info("finalize : " + (opDesc.getTaskId_OpTagIdx()) + " : "
						+ new String(((BytesWritable) obj).getBytes()) + " "
						+ (System.currentTimeMillis() / 1000));
				// BytesWritable data = ((BytesWritable) obj);
				// try {
				// fos.write(data.getBytes(), 0, data.getLength());
				// fos.write('\n');
				// fos.flush();
				// } catch (IOException e) {
				// log.error(TDBankUtils.getExceptionStack(e));
				// }
			} else {
				log.info("finalize : " + (opDesc.getTaskId_OpTagIdx()) + " : "
						+ obj.toString() + " "
						+ (System.currentTimeMillis() / 1000));
				// try {
				// fos.write(obj.toString().getBytes());
				// fos.write('\n');
				// fos.flush();
				// } catch (IOException e) {
				// log.error(TDBankUtils.getExceptionStack(e));
				// }
			}
		} catch (SerDeException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}

		return true;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void printStatus(int printId) {
		// TODO Auto-generated method stub

	}

}
