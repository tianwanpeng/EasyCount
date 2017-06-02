package com.tencent.trc.exec.io.tde;

import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.lazy.LazyStruct;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazySimpleStructObjectInspector;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryFactory;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryStruct;
import org.apache.hadoop.hive.serde2.lazybinary.objectinspector.LazyBinaryStructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.tde.client.Result;
import com.tencent.tde.client.TairClient.TairOption;
import com.tencent.tde.client.impl.MutiThreadCallbackClient;
import com.tencent.tde.client.impl.MutiThreadCallbackClient.MutiClientCallBack;
import com.tencent.trc.exec.io.Queryable;
import com.tencent.trc.exec.utils.OIUtils;
import com.tencent.trc.metastore.TableTde;
import com.tencent.trc.metastore.TableUtils;

public class QueryTdeTable implements Queryable {

	private static Logger log = LoggerFactory.getLogger(QueryTdeTable.class);
	final static byte[] nullbyte0 = new byte[0];
	final static byte[] nullbyte1 = new byte[] { 0 };

	private StructObjectInspector objectInspector;
	final private TableTde tbl;

	private static final int ASYNC_QUERY_SIZE = 100000;
	transient private MutiThreadCallbackClient tdeClient = null;
	private final short tableId;
	private TairOption opt = null;
	transient private Semaphore sem = null;

	boolean binaryMode = false;
	final private boolean asyncMode;

	@Override
	public void printStatus(int printId) {
		log.info("dimtable : " + tbl.getTableName()
				+ " sem.availablePermits() : " + sem.availablePermits());
	}

	public QueryTdeTable(TableTde tbl, boolean asyncMode) {
		this.tbl = tbl;
		this.asyncMode = asyncMode;
		tableId = TableUtils.getTdeTableId(this.tbl);
		init();
		binaryMode = TableUtils.getBinaryMode(tbl);
		if (binaryMode) {
			objectInspector = (StructObjectInspector) OIUtils
					.createLazyBinaryStructInspector(tbl.getFieldNames(),
							tbl.getStructTypes());
		} else {
			objectInspector = OIUtils.createLazyStructInspector(tbl);
		}
	}

	synchronized private void init() {
		int xxx = 0;
		while (true) {
			try {
				// try {
				// Thread.sleep(1000);
				// } catch (InterruptedException e1) {
				// }
				String master = TableUtils.getTdeMaster(this.tbl);
				String slaver = TableUtils.getTdeSlave(this.tbl);
				String groupName = TableUtils.getTdeGroupName(this.tbl);
				long timeOut = TableUtils.getTdeTimeOut(this.tbl);

				if (tdeClient != null) {
					return;
				}

				tdeClient = new MutiThreadCallbackClient();
				tdeClient.setMaster(master);
				if (slaver != null && slaver.trim().length() > 0) {
					tdeClient.setSlave(slaver);
				}
				tdeClient.setMaxNotifyQueueSize(ASYNC_QUERY_SIZE);
				tdeClient.setGroup(groupName);
				tdeClient.setWorkerThreadCount(10);
				opt = new TairOption(timeOut);
				sem = new Semaphore(ASYNC_QUERY_SIZE);

				tdeClient.init();
				break;
			} catch (Exception e) {
				xxx++;
				tdeClient = null;
				log.warn("error when connect to tde and retry for " + xxx
						+ " times ....");
			}
		}
	}

	@Override
	public void get(String key, CallBack cb) {
		if (!asyncMode) {
			try {
				Result<byte[]> result = tdeClient.get(tableId, key.getBytes(),
						opt);
				if (result.getCode().errno() == 0) {
					byte[] data = result.getResult();
					processData(data, cb);
				} else if (result.getCode().errno() == 1) {
					cb.callback(null);
					if (shouldprint()) {
						log.warn("key : " + key + " not exists in tde ");
					}
				} else {
					cb.fail();
					log.warn("some error occored when get from tde : " + key
							+ " : " + result.getCode().errno());
				}
				if (shouldprint()) {
					// log.info("tdedata : key : " + key + ", value : "
					// + new String(result.getResult()));
				}
			} catch (Exception e) {
				log.error(TDBankUtils.getExceptionStack(e));
				// init();
				cb.fail();
			}
		} else {
			Future<Result<byte[]>> future2;
			try {
				sem.acquire();
				future2 = tdeClient.getAsync(tableId, key.getBytes(), opt);
				tdeClient.notifyFuture(future2, new TdeGetCallBack(), cb);
			} catch (Exception e) {
				log.error(TDBankUtils.getExceptionStack(e));
				sem.release();
				init();
				cb.fail();
			}
		}
	}

	long lasttime = System.currentTimeMillis();

	private boolean shouldprint() {
		long ctime = System.currentTimeMillis();
		if (ctime - lasttime > 30000) {
			lasttime = ctime;
			return true;
		}
		return false;
	}

	private void processData(byte[] data, CallBack cb) {
		ByteArrayRef bytesref = new ByteArrayRef();
		bytesref.setData(data);
		if (binaryMode) {
			LazyBinaryStruct lbs = (LazyBinaryStruct) LazyBinaryFactory
					.createLazyBinaryObject((LazyBinaryStructObjectInspector) objectInspector);
			lbs.init(bytesref, 0, bytesref.getData().length);
			cb.callback(new Object[] { lbs });
		} else {
			LazyStruct ls = new LazyStruct(
					(LazySimpleStructObjectInspector) objectInspector);
			ls.init(bytesref, 0, bytesref.getData().length);
			cb.callback(new Object[] { ls });
		}
	}

	class TdeGetCallBack implements MutiClientCallBack {
		@SuppressWarnings("unchecked")
		public void handle(Future<?> future, Object context) {
			sem.release();
			Future<Result<byte[]>> afuture = (Future<Result<byte[]>>) future;
			CallBack outterCallback = (CallBack) context;
			try {
				Result<byte[]> result = afuture.get();

				if (result.getCode().errno() == 0) {
					byte[] data = result.getResult();
					processData(data, outterCallback);
				} else if (result.getCode().errno() == 1) {
					outterCallback.callback(null);
				} else {
					log.warn("some error occored when get from tde : "
							+ result.getCode().errno());
					outterCallback.fail();
				}
			} catch (Exception e) {
				log.error(TDBankUtils.getExceptionStack(e));
				// init();
				outterCallback.fail();
			}
		}
	}

	@Override
	public ObjectInspector getObjectInspector() {
		return objectInspector;
	}

}
