package com.tencent.trc.exec.io;

import java.util.Properties;

import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde2.AbstractSerDe;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinarySerDe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.trc.exec.logical.Operator7FS.Finalized;
import com.tencent.trc.metastore.Table;
import com.tencent.trc.metastore.TableUtils;
import com.tencent.trc.plan.logical.OpDesc7FS;

public abstract class Data2Sink implements Finalized {
	private static Logger log = LoggerFactory.getLogger(Data2Sink.class);

	protected final int opTagIdx;
	protected final OpDesc7FS opDesc;
	protected AbstractSerDe serDe = null;

	public Data2Sink(OpDesc7FS opDesc) {
		this.opDesc = opDesc;
		this.opTagIdx = opDesc.getOpTagIdx();

		try {
			boolean binaryMode = TableUtils.getBinaryMode(opDesc.getTable());
			if (binaryMode) {
				this.serDe = new LazyBinarySerDe();
			} else {
				this.serDe = new LazySimpleSerDe();
				Properties prop = new Properties();
				Table tbl = getOpDesc().getTable();
				byte[] separators = TableUtils.generateSeparators(tbl);
				char fieldSpliter = (char) separators[0];
				char listSpliter = (char) separators[1];
				char mapSpliter = (char) separators[2];

				prop.put(serdeConstants.FIELD_DELIM,
						String.valueOf(fieldSpliter));
				prop.put(serdeConstants.COLLECTION_DELIM,
						String.valueOf(listSpliter));
				prop.put(serdeConstants.MAPKEY_DELIM,
						String.valueOf(mapSpliter));
				serDe.initialize(null, prop);
			}
		} catch (SerDeException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}
	}

	public int getOpTagIdx() {
		return opTagIdx;
	}

	public OpDesc7FS getOpDesc() {
		return opDesc;
	}
}
