package com.tencent.trc.exec.io.tde;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.tde.client.Result;
import com.tencent.tde.client.TairClient.TairOption;
import com.tencent.tde.client.error.TairException;
import com.tencent.tde.client.impl.MutiThreadCallbackClient;
import com.tencent.tde.client.impl.MutiThreadCallbackClient.MutiClientCallBack;
import com.tencent.trc.exec.io.Data2Sink;
import com.tencent.trc.metastore.TableTde;
import com.tencent.trc.metastore.TableUtils;
import com.tencent.trc.plan.logical.OpDesc7FS;

public class Data2SinkTde extends Data2Sink {

	private static Logger log = LoggerFactory.getLogger(Data2SinkTde.class);

	final private TableTde tbl;
	private static final int ASYNC_QUERY_SIZE = 100000;
	transient private MutiThreadCallbackClient tdeClient = null;
	private final short tableId;
	private TairOption opt = null;
	private Semaphore sem = null;
	private final boolean asyncMode;

	@Override
	public void printStatus(int printId) {

	}

	public Data2SinkTde(OpDesc7FS opDesc, boolean asyncMode) {
		super(opDesc);
		this.tbl = (TableTde) opDesc.getTable();
		this.tableId = TableUtils.getTdeTableId(this.tbl);
		this.asyncMode = asyncMode;
		try {
			init();
		} catch (TairException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}
	}

	private void init() throws TairException {

		String master = TableUtils.getTdeMaster(this.tbl);
		String slaver = TableUtils.getTdeSlave(this.tbl);
		String groupName = TableUtils.getTdeGroupName(this.tbl);
		long timeOut = TableUtils.getTdeTimeOut(this.tbl);
		int expireTime = TableUtils.getTdeExpireTime(this.tbl);

		tdeClient = new MutiThreadCallbackClient();
		tdeClient.setMaster(master);
		if (slaver != null && slaver.trim().length() > 0) {
			tdeClient.setSlave(slaver);
		}
		tdeClient.setMaxNotifyQueueSize(ASYNC_QUERY_SIZE);
		tdeClient.setGroup(groupName);
		tdeClient.setWorkerThreadCount(10);
		opt = new TairOption(timeOut);
		opt.setExpireTime(expireTime);
		sem = new Semaphore(ASYNC_QUERY_SIZE);

		tdeClient.init();
	}

	@Override
	public boolean finalize(Object row, ObjectInspector objectInspector,
			ObjectInspector keyInspector, ObjectInspector attrsInspector,
			int opTagIdx) {

		if (keyInspector == null) {
			return false;
		}
		byte[] keydata = null;
		byte[] valuedata = null;
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

			keydata = stand_obj.toString().getBytes();

			Object obj = serDe.serialize(row, objectInspector);
			if (obj instanceof BytesWritable) {
				BytesWritable bw = (BytesWritable) obj;
				valuedata = Arrays.copyOf(bw.getBytes(), bw.getLength());
			} else {
				Text w = (Text) obj;
				valuedata = Arrays.copyOf(w.getBytes(), w.getLength());
			}
		} catch (Exception e) {
			log.error(TDBankUtils.getExceptionStack(e));
			return false;
		}
		try {
			if (asyncMode) {
				putToTdeAsync(keydata, valuedata, 0);
			} else {
				putToTde(keydata, valuedata);
			}
		} catch (Exception e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}

		return true;
	}

	private void putToTde(byte[] keydata, byte[] valuedata) throws Exception {
		for (int i = 0; i < 100; i++) {
			Result<Void> result = tdeClient.put(tableId, keydata, valuedata,
					opt);
			if (result.getCode().errno() == 0) {
				break;
			}
			log.info("put to tde fail for " + i + " times, and retry : "
					+ new String(keydata));
			try {
				Thread.sleep(10);
			} catch (Exception e) {
			}
		}
	}

	private void putToTdeAsync(byte[] keydata, byte[] valuedata, int num)
			throws Exception {
		if (num >= 30) {
			throw new Exception("put to tde fail for " + num + " times "
					+ new String(keydata) + " so discard it ... ");
		}
		try {
			sem.acquire();
			Future<Result<Void>> putFure = tdeClient.putAsync(tableId, keydata,
					valuedata, opt);
			tdeClient.notifyFuture(putFure, new TdePutCallback(keydata,
					valuedata, num), null);
		} catch (Exception e) {
			log.error(TDBankUtils.getExceptionStack(e));
			sem.release();
		}
	}

	private class TdePutCallback implements MutiClientCallBack {
		final byte[] keydata;
		final byte[] valuedata;
		final int num;

		public TdePutCallback(byte[] keydata, byte[] valuedata, int num) {
			this.keydata = keydata;
			this.valuedata = valuedata;
			this.num = num;
		}

		@SuppressWarnings("unchecked")
		public void handle(Future<?> future, Object context) {
			try {
				sem.release();
				Result<Void> result = (Result<Void>) future.get();

				if (result.getCode().errno() != 0) {
					log.error("Put Message Error to TDE, KEY => "
							+ new String(keydata) + " ERROR_CODE : "
							+ result.getCode().errno() + " for " + num
							+ " times and retry ...");
					putToTdeAsync(keydata, valuedata, num + 1);
				}
			} catch (Exception e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
		}
	}

	@Override
	public void close() throws IOException {

	}

}
