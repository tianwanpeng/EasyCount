package com.tencent.easycount.exec.io.local;

import com.tencent.easycount.conf.TrcConfiguration;
import com.tencent.easycount.exec.io.Data1Source;
import com.tencent.easycount.exec.io.Data2Sink;
import com.tencent.easycount.exec.physical.Data1Generator;
import com.tencent.easycount.metastore.MetaData;
import com.tencent.easycount.metastore.Table;
import com.tencent.easycount.metastore.Table.TableType;
import com.tencent.easycount.plan.logical.OpDesc1TS;
import com.tencent.easycount.plan.logical.OpDesc7FS;

public class LocalModeUtils {

	public static void setupTables(final MetaData md,
			final TrcConfiguration config) {
		for (final String tblalias : md.getTables().keySet()) {
			final Table tbl = md.getTables().get(tblalias);
			if ((tbl.getTableType() == TableType.stream)
					|| (tbl.getTableType() == TableType.tube)) {
				new LocalTableServerStream(tbl, config).start();
			} else if ((tbl.getTableType() != TableType.inner)
					&& (tbl.getTableType() != TableType.print)) {
				new LocalTableServerKV(tbl, config).start();
			}
		}
	}

	public static Data2Sink generateLocalDataSink(final Table tbl,
			final OpDesc7FS opDesc) {
		if (tbl.isKVTbl()) {
			return new Data2SinkLocalKV(opDesc);
		} else {
			return new Data2SinkLocalStream(opDesc);
		}
	}

	public static Data1Source generateLocalDataSource(
			final String taskId_OpTagIdx, final OpDesc1TS opDesc,
			final Data1Generator data1Generator, final TrcConfiguration hconf) {
		if (opDesc.getTable().isKVTbl()) {
			return new Data1SourceLocalKV(taskId_OpTagIdx, opDesc,
					data1Generator, hconf);
		} else {
			return new Data1SourceLocalStream(taskId_OpTagIdx, opDesc,
					data1Generator, hconf);
		}
	}
}
