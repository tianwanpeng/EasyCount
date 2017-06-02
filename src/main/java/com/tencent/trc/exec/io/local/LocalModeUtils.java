package com.tencent.trc.exec.io.local;

import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.io.Data1Source;
import com.tencent.trc.exec.io.Data2Sink;
import com.tencent.trc.exec.physical.Data1Generator;
import com.tencent.trc.metastore.MetaData;
import com.tencent.trc.metastore.Table;
import com.tencent.trc.metastore.Table.TableType;
import com.tencent.trc.plan.logical.OpDesc1TS;
import com.tencent.trc.plan.logical.OpDesc7FS;

public class LocalModeUtils {

	public static void setupTables(MetaData md, TrcConfiguration config) {
		for (String tblalias : md.getTables().keySet()) {
			Table tbl = md.getTables().get(tblalias);
			if (tbl.getTableType() == TableType.stream
					|| tbl.getTableType() == TableType.tube) {
				new LocalTableServerStream(tbl, config).start();
			} else if (tbl.getTableType() != TableType.inner
					&& tbl.getTableType() != TableType.print) {
				new LocalTableServerKV(tbl, config).start();
			}
		}
	}

	public static Data2Sink generateLocalDataSink(Table tbl, OpDesc7FS opDesc) {
		if (tbl.isKVTbl()) {
			return new Data2SinkLocalKV(opDesc);
		} else {
			return new Data2SinkLocalStream(opDesc);
		}
	}

	public static Data1Source generateLocalDataSource(String taskId_OpTagIdx,
			OpDesc1TS opDesc, Data1Generator data1Generator,
			TrcConfiguration hconf) {
		if (opDesc.getTable().isKVTbl()) {
			return new Data1SourceLocalKV(taskId_OpTagIdx, opDesc,
					data1Generator, hconf);
		} else {
			return new Data1SourceLocalStream(taskId_OpTagIdx, opDesc,
					data1Generator, hconf);
		}
	}
}
