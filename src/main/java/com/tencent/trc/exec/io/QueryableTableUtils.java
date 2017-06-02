package com.tencent.trc.exec.io;

import com.tencent.trc.exec.io.db.QueryDBTable;
import com.tencent.trc.exec.io.hbase.QueryHbaseTable;
import com.tencent.trc.exec.io.local.QueryLocalKVTable;
import com.tencent.trc.exec.io.redis.QueryRedisTable;
import com.tencent.trc.exec.io.tde.QueryTdeTable;
import com.tencent.trc.metastore.Table;
import com.tencent.trc.metastore.Table.TableType;
import com.tencent.trc.metastore.TableHbase;
import com.tencent.trc.metastore.TableMem;
import com.tencent.trc.metastore.TableMysql;
import com.tencent.trc.metastore.TableRedis;
import com.tencent.trc.metastore.TableTde;
import com.tencent.trc.metastore.TableTpg;
import com.tencent.trc.metastore.TableUtils;

public class QueryableTableUtils {

	public static Queryable generateQuerable(Table table, String printPrefix,
			boolean localmode) {
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
