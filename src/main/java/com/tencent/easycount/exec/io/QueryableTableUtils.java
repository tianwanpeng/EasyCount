package com.tencent.easycount.exec.io;

import com.tencent.easycount.exec.io.db.QueryDBTable;
import com.tencent.easycount.exec.io.hbase.QueryHbaseTable;
import com.tencent.easycount.exec.io.local.QueryLocalKVTable;
import com.tencent.easycount.exec.io.redis.QueryRedisTable;
import com.tencent.easycount.exec.io.tde.QueryTdeTable;
import com.tencent.easycount.metastore.Table;
import com.tencent.easycount.metastore.Table.TableType;
import com.tencent.easycount.metastore.TableHbase;
import com.tencent.easycount.metastore.TableMem;
import com.tencent.easycount.metastore.TableMysql;
import com.tencent.easycount.metastore.TableRedis;
import com.tencent.easycount.metastore.TableTde;
import com.tencent.easycount.metastore.TableTpg;
import com.tencent.easycount.metastore.TableUtils;

public class QueryableTableUtils {

	public static Queryable generateQuerable(final Table table,
			final String printPrefix, final boolean localmode) {
		if (table.isKVTbl() && localmode) {
			return new QueryLocalKVTable(table);
		}
		if (table.getTableType() == TableType.mem) {
			return new QueryMemTable((TableMem) table);
		} else if (table.getTableType() == TableType.tde) {
			if (TableUtils.getTdeReadAsync(table)) {
				return new QueryTdeTable((TableTde) table, true);
			} else {
				return new QueryTdeTable((TableTde) table, false);
			}
		} else if (table.getTableType() == TableType.redis) {
			return new QueryRedisTable((TableRedis) table);
		} else if (table.getTableType() == TableType.mysql) {
			return new QueryDBTable((TableMysql) table, printPrefix);
		} else if (table.getTableType() == TableType.tpg) {
			return new QueryDBTable((TableTpg) table, printPrefix);
		} else if (table.getTableType() == TableType.hbase) {
			if (TableUtils.getHbaseReadAsync(table)) {
				return new QueryHbaseTable((TableHbase) table, true,
						printPrefix);
			} else {
				return new QueryHbaseTable((TableHbase) table, false,
						printPrefix);
			}
		}
		return null;
	}
}
