package com.tencent.trc.exec.io.redis;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.BinaryJedis;

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.trc.exec.io.Data2Sink;
import com.tencent.trc.metastore.TableRedis;
import com.tencent.trc.metastore.TableUtils;
import com.tencent.trc.plan.logical.OpDesc7FS;

public class Data2SinkRedis extends Data2Sink {

	private static Logger log = LoggerFactory.getLogger(Data2SinkRedis.class);

	final private TableRedis tbl;
	private BinaryJedis client;
	final private int expireTime;

	public Data2SinkRedis(OpDesc7FS opDesc) {
		super(opDesc);

		this.tbl = (TableRedis) opDesc.getTable();
		this.expireTime = TableUtils.getRedisExpiretime(this.tbl);
		init();
		log.info("new Data2SinkRedis()");
	}

	private void init() {
		String hostIp = TableUtils.getRedisHost(this.tbl);
		int port = Integer.parseInt(TableUtils.getRedisPort(this.tbl));
		client = new BinaryJedis(hostIp, port);
	}

	@Override
	public boolean finalize(Object row, ObjectInspector objectInspector,
			ObjectInspector keyInspector, ObjectInspector attrsInspector,
			int opTagIdx) {

		if (keyInspector == null) {
			log.error("keyEvaluator is null");
			return false;
		}

		try {
			Object[] rs = (Object[]) row;
			Object key = rs[rs.length - 1];
			if (attrsInspector != null) {
				key = rs[rs.length - 2];
			}
			String keystr = String.valueOf(ObjectInspectorUtils
					.copyToStandardObject(key, keyInspector));
			byte[] keydata = keystr.getBytes();

			byte[] valuedata = null;
			Object obj = serDe.serialize(row, objectInspector);
			if (obj instanceof BytesWritable) {
				BytesWritable bw = (BytesWritable) obj;
				valuedata = Arrays.copyOf(bw.getBytes(), bw.getLength());
			} else {
				Text w = (Text) obj;
				valuedata = Arrays.copyOf(w.getBytes(), w.getLength());
			}
			System.err.println("redisSink : "
					+ getOpDesc().getTaskId_OpTagIdx() + " : " + keystr + " : "
					+ (valuedata == null ? "" : valuedata.length) + " : "
					+ String.valueOf(obj));
			client.setex(keydata, expireTime, valuedata);
			return true;
		} catch (SerDeException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}

		return false;
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
