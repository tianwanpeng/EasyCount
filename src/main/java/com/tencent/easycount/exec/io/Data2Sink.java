package com.tencent.easycount.exec.io;

import java.util.Properties;

import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde2.AbstractSerDe;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinarySerDe;

import com.tencent.easycount.exec.logical.Operator7FS.Finalized;
import com.tencent.easycount.metastore.Table;
import com.tencent.easycount.metastore.TableUtils;
import com.tencent.easycount.plan.logical.OpDesc7FS;

public abstract class Data2Sink implements Finalized {
	// private static Logger log = LoggerFactory.getLogger(Data2Sink.class);

	protected final int opTagIdx;
	protected final OpDesc7FS opDesc;
	protected AbstractSerDe serDe = null;

	@SuppressWarnings("deprecation")
	public Data2Sink(final OpDesc7FS opDesc) {
		this.opDesc = opDesc;
		this.opTagIdx = opDesc.getOpTagIdx();

		try {
			final boolean binaryMode = TableUtils.getBinaryMode(opDesc
					.getTable());
			if (binaryMode) {
				this.serDe = new LazyBinarySerDe();
			} else {
				this.serDe = new LazySimpleSerDe();
				final Properties prop = new Properties();
				final Table tbl = getOpDesc().getTable();
				final byte[] separators = TableUtils.generateSeparators(tbl);
				final char fieldSpliter = (char) separators[0];
				final char listSpliter = (char) separators[1];
				final char mapSpliter = (char) separators[2];

				prop.put(serdeConstants.FIELD_DELIM,
						String.valueOf(fieldSpliter));
				prop.put(serdeConstants.COLLECTION_DELIM,
						String.valueOf(listSpliter));
				prop.put(serdeConstants.MAPKEY_DELIM,
						String.valueOf(mapSpliter));
				this.serDe.initialize(null, prop);
			}
		} catch (final SerDeException e) {
			e.printStackTrace();
			// log.error(TDBankUtils.getExceptionStack(e));
		}
	}

	public int getOpTagIdx() {
		return this.opTagIdx;
	}

	public OpDesc7FS getOpDesc() {
		return this.opDesc;
	}
}
