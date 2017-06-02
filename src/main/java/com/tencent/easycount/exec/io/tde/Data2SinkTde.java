package com.tencent.easycount.exec.io.tde;

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

import com.tencent.easycount.exec.io.Data2Sink;
import com.tencent.easycount.metastore.TableTde;
import com.tencent.easycount.metastore.TableUtils;
import com.tencent.easycount.plan.logical.OpDesc7FS;
import com.tencent.easycount.util.status.TDBankUtils;

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
	public void printStatus(final int printId) {

	}

	public Data2SinkTde(final OpDesc7FS opDesc, final boolean asyncMode) {
		super(opDesc);
		this.tbl = (TableTde) opDesc.getTable();
		this.tableId = TableUtils.getTdeTableId(this.tbl);
		this.asyncMode = asyncMode;
		try {
			init();
		} catch (final TairException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}
	}

	private void init() throws TairException {

		final String master = TableUtils.getTdeMaster(this.tbl);
		final String slaver = TableUtils.getTdeSlave(this.tbl);
		final String groupName = TableUtils.getTdeGroupName(this.tbl);
		final long timeOut = TableUtils.getTdeTimeOut(this.tbl);
		final int expireTime = TableUtils.getTdeExpireTime(this.tbl);

		this.tdeClient = new MutiThreadCallbackClient();
		this.tdeClient.setMaster(master);
		if ((slaver != null) && (slaver.trim().length() > 0)) {
			this.tdeClient.setSlave(slaver);
		}
		this.tdeClient.setMaxNotifyQueueSize(ASYNC_QUERY_SIZE);
		this.tdeClient.setGroup(groupName);
		this.tdeClient.setWorkerThreadCount(10);
		this.opt = new TairOption(timeOut);
		this.opt.setExpireTime(expireTime);
		this.sem = new Semaphore(ASYNC_QUERY_SIZE);

		this.tdeClient.init();
	}

	@Override
	public boolean finalize(final Object row,
			final ObjectInspector objectInspector,
			final ObjectInspector keyInspector,
			final ObjectInspector attrsInspector, final int opTagIdx) {

		if (keyInspector == null) {
			return false;
		}
		byte[] keydata = null;
		byte[] valuedata = null;
		try {
			final Object[] rs = (Object[]) row;
			Object key = rs[rs.length - 1];
			if (attrsInspector != null) {
				key = rs[rs.length - 2];
			}

			final Object stand_obj = ObjectInspectorUtils.copyToStandardObject(
					key, keyInspector);
			if (stand_obj == null) {
				log.error("key is null : " + key + " : " + row);
				return false;
			}

			keydata = stand_obj.toString().getBytes();

			final Object obj = this.serDe.serialize(row, objectInspector);
			if (obj instanceof BytesWritable) {
				final BytesWritable bw = (BytesWritable) obj;
				valuedata = Arrays.copyOf(bw.getBytes(), bw.getLength());
			} else {
				final Text w = (Text) obj;
				valuedata = Arrays.copyOf(w.getBytes(), w.getLength());
			}
		} catch (final Exception e) {
			log.error(TDBankUtils.getExceptionStack(e));
			return false;
		}
		try {
			if (this.asyncMode) {
				putToTdeAsync(keydata, valuedata, 0);
			} else {
				putToTde(keydata, valuedata);
			}
		} catch (final Exception e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}

		return true;
	}

	private void putToTde(final byte[] keydata, final byte[] valuedata)
			throws Exception {
		for (int i = 0; i < 100; i++) {
			final Result<Void> result = this.tdeClient.put(this.tableId,
					keydata, valuedata, this.opt);
			if (result.getCode().errno() == 0) {
				break;
			}
			log.info("put to tde fail for " + i + " times, and retry : "
					+ new String(keydata));
			try {
				Thread.sleep(10);
			} catch (final Exception e) {
			}
		}
	}

	private void putToTdeAsync(final byte[] keydata, final byte[] valuedata,
			final int num) throws Exception {
		if (num >= 30) {
			throw new Exception("put to tde fail for " + num + " times "
					+ new String(keydata) + " so discard it ... ");
		}
		try {
			this.sem.acquire();
			final Future<Result<Void>> putFure = this.tdeClient.putAsync(
					this.tableId, keydata, valuedata, this.opt);
			this.tdeClient.notifyFuture(putFure, new TdePutCallback(keydata,
					valuedata, num), null);
		} catch (final Exception e) {
			log.error(TDBankUtils.getExceptionStack(e));
			this.sem.release();
		}
	}

	private class TdePutCallback implements MutiClientCallBack {
		final byte[] keydata;
		final byte[] valuedata;
		final int num;

		public TdePutCallback(final byte[] keydata, final byte[] valuedata,
				final int num) {
			this.keydata = keydata;
			this.valuedata = valuedata;
			this.num = num;
		}

		@SuppressWarnings("unchecked")
		public void handle(final Future<?> future, final Object context) {
			try {
				Data2SinkTde.this.sem.release();
				final Result<Void> result = (Result<Void>) future.get();

				if (result.getCode().errno() != 0) {
					log.error("Put Message Error to TDE, KEY => "
							+ new String(this.keydata) + " ERROR_CODE : "
							+ result.getCode().errno() + " for " + this.num
							+ " times and retry ...");
					putToTdeAsync(this.keydata, this.valuedata, this.num + 1);
				}
			} catch (final Exception e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
		}
	}

	@Override
	public void close() throws IOException {

	}

}
