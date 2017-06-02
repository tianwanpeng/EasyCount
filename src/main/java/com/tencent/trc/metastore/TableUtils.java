package com.tencent.trc.metastore;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;

import com.tencent.trc.metastore.Table.TableType;

public class TableUtils {

	final public static byte[] defaultSeparators = { (byte) 1, (byte) 2,
			(byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8,
			(byte) 11, (byte) 14, (byte) 15, (byte) 16, (byte) 17, (byte) 18,
			(byte) 19, (byte) 20, (byte) 21, (byte) 22, (byte) 23, (byte) 24,
			(byte) 25, (byte) 26, (byte) 28, (byte) 29 };

	private static String TABLE_TYPE = "table.type";
	private static String TABLE_NAME = "table.name";
	private static String TABLE_FIELDS = "table.fields";
	private static String TABLE_BINARY_MODE = "table.binary.mode";
	private static String TABLE_FIELD_KEY = "table.field.key";

	// splitters
	public static String TABLE_FIELD_SPLITTER = "table.field.splitter";
	public static String TABLE_LIST_SPLITTER = "table.list.splitter";
	public static String TABLE_MAP_SPLITTER = "table.map.splitter";
	public static String TABLE_SERILIAZE_SPLITTERS = "table.serialize.splitters";

	// for meta table stream table
	private static String TABLE_TOPIC = "table.topic";
	private static String TABLE_INTERFACEID = "table.interfaceId";
	private static String TABLE_ZK_SERVERS = "table.zk.servers";
	private static String TABLE_ZK_ROOT = "table.zk.root";
	private static String TABLE_TDSORT_ATTRS = "table.tdsort.attrs";

	// for tube stream table
	private static String TABLE_TUBE_MASTER = "table.tube.master";
	private static String TABLE_TUBE_ADDRLIST = "table.tube.addrlist";
	private static String TABLE_TUBE_PORT = "table.tube.port";
	private static String TABLE_TUBE_COMPRESS = "table.tube.compress";

	// for hbase
	private static String TABLE_HBASE_ZK_QUORUM = "table.hbase.zk.quorum";
	private static String TABLE_HBASE_ZK_PORT = "table.hbase.zk.port";
	private static String TABLE_HBASE_ZK_ROOT = "table.hbase.zk.root";
	private static String TABLE_HBASE_READ_ASYNC = "table.hbase.read.async";
	private static String TABLE_HBASE_QUERY_PARALLEL = "table.hbase.query.parallel";
	private static String TABLE_HBASE_QUERY_QUEUE_SIZE = "table.hbase.query.queue.size";

	// for mem table
	private static String TABLE_MEM_DATA_FILE = "table.mem.data.file";

	// for tde query
	private static String TABLE_TDE_MASTER = "table.tde.master";
	private static String TABLE_TDE_SLAVE = "table.tde.slave";
	private static String TABLE_TDE_GROUPNAME = "table.tde.groupname";
	private static String TABLE_TDE_TID = "table.tde.tid";
	private static String TABLE_TDE_TIMEOUT = "table.tde.timeout";
	private static String TABLE_TDE_EXPIRETIME = "table.tde.expiretime.seconds";
	private static String TABLE_TDE_KEYNAME = "table.tde.keyname";
	private static String TABLE_TDE_WRITE_ASYNC = "table.tde.write.async";
	private static String TABLE_TDE_READ_ASYNC = "table.tde.read.async";

	// tpg
	private static String TABLE_TPG_DB_HOST = "table.tpg.db.host";
	private static String TABLE_TPG_DB_PORT = "table.tpg.db.port";
	private static String TABLE_TPG_DB_NAME = "table.tpg.db.name";
	private static String TABLE_TPG_DB_USERNAME = "table.tpg.db.username";
	private static String TABLE_TPG_DB_PASSWD = "table.tpg.db.passwd";

	// mysql
	private static String TABLE_MYSQL_DB_HOST = "table.mysql.db.host";
	private static String TABLE_MYSQL_DB_PORT = "table.mysql.db.port";
	private static String TABLE_MYSQL_DB_NAME = "table.mysql.db.name";
	private static String TABLE_MYSQL_DB_USERNAME = "table.mysql.db.username";
	private static String TABLE_MYSQL_DB_PASSWD = "table.mysql.db.passwd";
	private static String TABLE_MYSQL_INSERT_MODE = "table.mysql.insert.mode";
	private static String TABLE_MYSQL_PACKSIZE = "table.mysql.packsize";
	private static String TABLE_MYSQL_USETRANS = "table.mysql.use.transaction";

	// db
	private static String TABLE_DB_UPDATE_INTERVAL = "table.db.update.interval";
	private static String TABLE_DB_KEYNAME = "table.db.keyname";

	// for redis query
	private static String TABLE_REDIS_HOST = "table.redis.host";
	private static String TABLE_REDIS_PORT = "table.redis.port";
	private static String TABLE_REDIS_EXPIRETIME = "table.redis.expiretime.seconds";

	// local mode
	private static String TABLE_LOCAL_ADDR = "table.localmode.addr";
	private static String TABLE_LOCAL_PORT = "table.localmode.port";

	public static Table generateTable(Set<Entry<String, String>> entrySet)
			throws Exception {
		HashMap<String, String> attrs = new HashMap<String, String>();
		for (Entry<String, String> entry : entrySet) {
			attrs.put(entry.getKey(), entry.getValue().trim());
		}

		String tableName = attrs.get(TABLE_NAME);
		ArrayList<Field> fields = new ArrayList<Field>();
		String[] columns = splitColumn(attrs.get(TABLE_FIELDS), ':', false);

		for (String column : columns) {
			String[] args = splitColumn(column, ',', true);
			Field f = new Field(TypeInfoUtils.getTypeInfoFromTypeString(args[1]
					.toLowerCase()), args[0]);
			fields.add(f);
		}

		String typeName = attrs.get(TABLE_TYPE);
		TableType type = TableType.valueOf(typeName == null ? "stream"
				: typeName);

		if (type == TableType.stream) {
			return new TableStream(tableName, fields, attrs);
		} else if (type == TableType.tube) {
			return new TableTube(tableName, fields, attrs);
		} else if (type == TableType.tpg) {
			return new TableTpg(tableName, fields, attrs);
		} else if (type == TableType.mysql) {
			return new TableMysql(tableName, fields, attrs);
		} else if (type == TableType.tde) {
			return new TableTde(tableName, fields, attrs);
		} else if (type == TableType.redis) {
			return new TableRedis(tableName, fields, attrs);
		} else if (type == TableType.hbase) {
			return new TableHbase(tableName, fields, attrs);
		} else if (type == TableType.mem) {
			byte[] data = null;
			String datafilename = attrs.get(TABLE_MEM_DATA_FILE);
			if (datafilename == null || !(new File(datafilename).exists())) {
				data = new byte[0];
			} else {
				File file = new File(datafilename);
				data = new byte[(int) file.length()];
				FileInputStream fis = new FileInputStream(file);
				fis.read(data);
				fis.close();
			}
			return new TableMem(tableName, fields, attrs, data);
		}

		return null;
	}

	private static String[] splitColumn(String column, char spliter,
			boolean deleteOtherSplash) {
		ArrayList<String> res = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		// int idx = 0;
		for (int i = 0; i < column.length(); i++) {
			if (column.charAt(i) == spliter) {
				if (i == 0 || column.charAt(i - 1) != '\\') {
					res.add(sb.toString());
					// idx++;
					sb.setLength(0);
					// if (idx >= res.size()) {
					// return (String[]) res.toArray(new String[res.size()]);
					// }
				} else {
					sb.append(column.charAt(i));
				}
			} else if (!deleteOtherSplash || column.charAt(i) != '\\') {
				sb.append(column.charAt(i));
			}
		}

		res.add(sb.toString());
		return (String[]) res.toArray(new String[res.size()]);
	}

	public static char getFieldSpliter(Table tbl) {
		return getSpliter(tbl.getTblAttrs().get(TABLE_FIELD_SPLITTER), (char) 1);
	}

	public static char getListSpliter(Table tbl) {
		return getSpliter(tbl.getTblAttrs().get(TABLE_LIST_SPLITTER), (char) 2);
	}

	public static char getMapSpliter(Table tbl) {
		return getSpliter(tbl.getTblAttrs().get(TABLE_MAP_SPLITTER), (char) 3);
	}

	private static char getSpliter(String spliterStr, char dft) {
		return spliterStr == null ? dft : (spliterStr.toLowerCase().startsWith(
				"0x") ? (char) Integer.parseInt(spliterStr.substring(2))
				: spliterStr.charAt(0));
	}

	public static String getStreamTdsortAttrs(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_TDSORT_ATTRS);
	}

	public static String getTableZkRoot(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_ZK_ROOT);
	}

	public static String getTableZkServers(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_ZK_SERVERS);
	}

	public static String getTableTopic(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_TOPIC);
	}

	public static String getTableInterfaceId(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_INTERFACEID);
	}

	public static String getKeyFieldName(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_FIELD_KEY);
	}

	public static String getTdeMaster(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_TDE_MASTER);
	}

	public static String getTdeSlave(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_TDE_SLAVE);
	}

	// tde
	public static String getTdeGroupName(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_TDE_GROUPNAME);
	}

	public static short getTdeTableId(Table tbl) {
		return Short.valueOf(tbl.getTblAttrs().get(TABLE_TDE_TID));
	}

	public static long getTdeTimeOut(Table tbl) {
		String timeout = tbl.getTblAttrs().get(TABLE_TDE_TIMEOUT);
		return timeout == null ? 5000 : Long.valueOf(timeout);
	}

	public static String getTdeKeyName(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_TDE_KEYNAME);
	}

	public static boolean getTdeWriteAsync(Table tbl) {
		// default is false
		return "true".equalsIgnoreCase(tbl.getTblAttrs().get(
				TABLE_TDE_WRITE_ASYNC));
	}

	public static boolean getTdeReadAsync(Table tbl) {
		// default is false
		return "true".equalsIgnoreCase(tbl.getTblAttrs().get(
				TABLE_TDE_READ_ASYNC));
	}

	// tpg
	public static String getTpgDBHost(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_TPG_DB_HOST);
	}

	public static String getTpgDBPort(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_TPG_DB_PORT);
	}

	public static String getTpgDBName(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_TPG_DB_NAME);
	}

	public static String getTpgDBUserName(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_TPG_DB_USERNAME);
	}

	public static String getTpgDBPassWD(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_TPG_DB_PASSWD);
	}

	// mysql
	public static String getMysqlDBHost(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_MYSQL_DB_HOST);
	}

	public static String getMysqlDBPort(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_MYSQL_DB_PORT);
	}

	public static String getMysqlDBName(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_MYSQL_DB_NAME);
	}

	public static String getMysqlDBUserName(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_MYSQL_DB_USERNAME);
	}

	public static String getMysqlDBPassWD(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_MYSQL_DB_PASSWD);
	}

	public static int getMysqlDBPacksize(Table tbl) {
		String packsize = tbl.getTblAttrs().get(TABLE_MYSQL_PACKSIZE);
		return packsize == null ? 100 : Integer.valueOf(packsize);
	}

	public static boolean getMysqlDBUseTransaction(Table tbl) {
		String usetrans = tbl.getTblAttrs().get(TABLE_MYSQL_USETRANS);
		return usetrans == null ? true : "true".equalsIgnoreCase(usetrans);
	}

	public static int getDbUpdateInterval(Table tbl) {
		String dbInterval = tbl.getTblAttrs().get(TABLE_DB_UPDATE_INTERVAL);
		return dbInterval == null ? 300 : Integer.valueOf(dbInterval);
	}

	public static boolean getMysqlInsertMode(Table tbl) {
		return "true".equalsIgnoreCase(tbl.getTblAttrs().get(
				TABLE_MYSQL_INSERT_MODE));
	}

	public static boolean getBinaryMode(Table tbl) {
		return "true"
				.equalsIgnoreCase(tbl.getTblAttrs().get(TABLE_BINARY_MODE));
	}

	public static String getRedisHost(TableRedis tbl) {
		return tbl.getTblAttrs().get(TABLE_REDIS_HOST);
	}

	public static String getRedisPort(TableRedis tbl) {
		return tbl.getTblAttrs().get(TABLE_REDIS_PORT);
	}

	public static int getTdeExpireTime(TableTde tbl) {
		String expireTime = tbl.getTblAttrs().get(TABLE_TDE_EXPIRETIME);
		return expireTime == null ? 2 * 24 * 60 * 60 : Integer
				.valueOf(expireTime);
	}

	public static int getRedisExpiretime(TableRedis tbl) {
		String expireTime = tbl.getTblAttrs().get(TABLE_REDIS_EXPIRETIME);
		return expireTime == null ? 2 * 24 * 60 * 60 : Integer
				.valueOf(expireTime);
	}

	public static byte[] generateSeparators(Table tbl) {
		char fieldSpliter = TableUtils.getFieldSpliter(tbl);
		char listSpliter = TableUtils.getListSpliter(tbl);
		char mapSpliter = TableUtils.getMapSpliter(tbl);

		byte[] separators = newSeparators(fieldSpliter, listSpliter,
				mapSpliter, tbl);

		return separators;
	}

	public static byte[] newSeparators(char fieldSpliter, char listSpliter,
			char mapSpliter, Table tbl) {
		byte[] seps = Arrays.copyOf(TableUtils.defaultSeparators,
				TableUtils.defaultSeparators.length);
		seps[0] = (byte) fieldSpliter;
		seps[1] = (byte) listSpliter;
		seps[2] = (byte) mapSpliter;

		String attrstr = tbl.getTblAttrs().get(TABLE_SERILIAZE_SPLITTERS);
		if (attrstr != null) {
			String[] attrs = attrstr.split("->");
			if (attrs.length > 0) {
				int i = 0;
				for (String attr : attrs) {
					seps[i] = (byte) getSpliter(attr, (char) seps[i]);
					i++;
				}
			}
		}
		return seps;
	}

	public static String getHbaseZkQuorum(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_HBASE_ZK_QUORUM);
	}

	public static String getHbaseZkPort(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_HBASE_ZK_PORT);
	}

	public static String getHbaseZkRoot(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_HBASE_ZK_ROOT);
	}

	public static boolean getHbaseReadAsync(Table tbl) {
		return "true".equalsIgnoreCase(tbl.getTblAttrs().get(
				TABLE_HBASE_READ_ASYNC));
	}

	public static int getHbaseQueryParallel(Table tbl) {
		String parallel = tbl.getTblAttrs().get(TABLE_HBASE_QUERY_PARALLEL);
		if (parallel == null) {
			return 10;
		}
		try {
			return Integer.parseInt(parallel);
		} catch (Exception e) {
		}
		return 10;
	}

	public static int getHbaseQueryQueueSize(TableHbase tbl) {
		String queuesize = tbl.getTblAttrs().get(TABLE_HBASE_QUERY_QUEUE_SIZE);
		if (queuesize == null) {
			return 1000;
		}
		try {
			return Integer.parseInt(queuesize);
		} catch (Exception e) {
		}
		return 1000;
	}

	// for local mode
	public static String getLocalTableAddr(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_LOCAL_ADDR);
	}

	public static int getLocalTablePort(Table tbl) {
		return Integer.valueOf(tbl.getTblAttrs().get(TABLE_LOCAL_PORT));
	}

	// for tube
	public static String getTableTubeMaster(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_TUBE_MASTER);
	}

	public static String getTableTubeAddrList(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_TUBE_ADDRLIST);
	}

	public static int getTableTubePort(Table tbl) {
		String port = tbl.getTblAttrs().get(TABLE_TUBE_PORT);
		return port == null ? 8609 : Integer.valueOf(port);
	}

	public static boolean getTubeTableCompress(Table tbl, boolean compress) {
		String cps = tbl.getTblAttrs().get(TABLE_TUBE_COMPRESS);
		return (cps == null) ? compress : ("true".equalsIgnoreCase(cps) ? true
				: ("false".equalsIgnoreCase(cps) ? false : compress));
	}

	public static String getDbKeyName(Table tbl) {
		return tbl.getTblAttrs().get(TABLE_DB_KEYNAME);
	}

}
