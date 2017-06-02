package com.tencent.trc.exec.io.redis;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.lazy.LazyStruct;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazySimpleStructObjectInspector;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryFactory;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryStruct;
import org.apache.hadoop.hive.serde2.lazybinary.objectinspector.LazyBinaryStructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;

import redis.clients.jedis.BinaryJedis;

import com.tencent.trc.exec.io.Queryable;
import com.tencent.trc.exec.utils.OIUtils;
import com.tencent.trc.metastore.TableRedis;
import com.tencent.trc.metastore.TableUtils;

public class QueryRedisTable implements Queryable {

	private StructObjectInspector objectInspector;
	final private TableRedis tbl;
	private BinaryJedis client;
	boolean binaryMode = false;

	@Override
	public void printStatus(int printId) {

	}

	public QueryRedisTable(TableRedis tbl) {
		this.tbl = tbl;

		binaryMode = TableUtils.getBinaryMode(tbl);
		if (binaryMode) {
			objectInspector = (StructObjectInspector) OIUtils
					.createLazyBinaryStructInspector(tbl.getFieldNames(),
							tbl.getStructTypes());
		} else {
			objectInspector = OIUtils.createLazyStructInspector(tbl);
		}

		String hostIp = TableUtils.getRedisHost(this.tbl);
		int port = Integer.parseInt(TableUtils.getRedisPort(this.tbl));
		client = new BinaryJedis(hostIp, port);
	}

	@Override
	public void get(String key, CallBack cb) {
		byte[] data = client.get(key.getBytes());

		if (data == null) {
			cb.callback(null);
			return;
		}
		if (binaryMode) {
			ByteArrayRef bytesref = new ByteArrayRef();
			StringBuffer sb = new StringBuffer();
			sb.append(key).append(" : ");
			for (int i = 0; i < data.length; i++) {
				String xx = Integer.toHexString(data[i] & 0x0ff);
				sb.append((xx.length() == 1 ? ("0" + xx) : xx) + " ");
			}
			System.err.println(sb.toString());

			bytesref.setData(data);
			LazyBinaryStruct lbs = (LazyBinaryStruct) LazyBinaryFactory
					.createLazyBinaryObject((LazyBinaryStructObjectInspector) objectInspector);
			lbs.init(bytesref, 0, bytesref.getData().length);
			System.err.println("QueryRedisTable : " + key + " : "
					+ lbs.getFieldsAsList());
			cb.callback(new Object[] { lbs });
		} else {
			ByteArrayRef bytesref = new ByteArrayRef();
			bytesref.setData(data);
			LazyStruct ls = new LazyStruct(
					(LazySimpleStructObjectInspector) objectInspector);
			ls.init(bytesref, 0, bytesref.getData().length);
			cb.callback(new Object[] { ls });
		}

	}

	@Override
	public ObjectInspector getObjectInspector() {
		return objectInspector;
	}

}
