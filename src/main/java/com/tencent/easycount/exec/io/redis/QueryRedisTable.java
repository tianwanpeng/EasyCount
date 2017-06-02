package com.tencent.easycount.exec.io.redis;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.lazy.LazyStruct;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazySimpleStructObjectInspector;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryFactory;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryStruct;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;

import redis.clients.jedis.BinaryJedis;

import com.tencent.easycount.exec.io.Queryable;
import com.tencent.easycount.exec.utils.OIUtils;
import com.tencent.easycount.metastore.TableRedis;
import com.tencent.easycount.metastore.TableUtils;

public class QueryRedisTable implements Queryable {

	private StructObjectInspector objectInspector;
	final private TableRedis tbl;
	private final BinaryJedis client;
	boolean binaryMode = false;

	@Override
	public void printStatus(final int printId) {

	}

	public QueryRedisTable(final TableRedis tbl) {
		this.tbl = tbl;

		this.binaryMode = TableUtils.getBinaryMode(tbl);
		if (this.binaryMode) {
			this.objectInspector = OIUtils.createLazyBinaryStructInspector(
					tbl.getFieldNames(), tbl.getStructTypes());
		} else {
			this.objectInspector = OIUtils.createLazyStructInspector(tbl);
		}

		final String hostIp = TableUtils.getRedisHost(this.tbl);
		final int port = Integer.parseInt(TableUtils.getRedisPort(this.tbl));
		this.client = new BinaryJedis(hostIp, port);
	}

	@Override
	public void get(final String key, final CallBack cb) {
		final byte[] data = this.client.get(key.getBytes());

		if (data == null) {
			cb.callback(null);
			return;
		}
		if (this.binaryMode) {
			final ByteArrayRef bytesref = new ByteArrayRef();
			final StringBuffer sb = new StringBuffer();
			sb.append(key).append(" : ");
			for (int i = 0; i < data.length; i++) {
				final String xx = Integer.toHexString(data[i] & 0x0ff);
				sb.append((xx.length() == 1 ? ("0" + xx) : xx) + " ");
			}
			System.err.println(sb.toString());

			bytesref.setData(data);
			final LazyBinaryStruct lbs = (LazyBinaryStruct) LazyBinaryFactory
					.createLazyBinaryObject(this.objectInspector);
			lbs.init(bytesref, 0, bytesref.getData().length);
			System.err.println("QueryRedisTable : " + key + " : "
					+ lbs.getFieldsAsList());
			cb.callback(new Object[] { lbs });
		} else {
			final ByteArrayRef bytesref = new ByteArrayRef();
			bytesref.setData(data);
			final LazyStruct ls = new LazyStruct(
					(LazySimpleStructObjectInspector) this.objectInspector);
			ls.init(bytesref, 0, bytesref.getData().length);
			cb.callback(new Object[] { ls });
		}

	}

	@Override
	public ObjectInspector getObjectInspector() {
		return this.objectInspector;
	}

}
