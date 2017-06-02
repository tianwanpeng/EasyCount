package com.tencent.trc.exec.io;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import snaq.db.ConnectionPool;

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.trc.exec.physical.Data1Generator;
import com.tencent.trc.exec.utils.OIUtils;
import com.tencent.trc.metastore.Table;
import com.tencent.trc.metastore.Table.TableType;
import com.tencent.trc.metastore.TableHbase;
import com.tencent.trc.metastore.TableUtils;
import com.tencent.trc.plan.logical.OpDesc1TS;

@SuppressWarnings("deprecation")
public class DataIOUtils {
	private static Logger log = LoggerFactory.getLogger(DataIOUtils.class);

	public static Data1Source generateDimDataSource(String sourceId,
			final OpDesc1TS opDesc1TS, Data1Generator emitter) {
		return new Data1Source(sourceId, opDesc1TS, emitter) {

			@Override
			public void start() {

			}

			@Override
			public ObjectInspector getObjectInspector() {
				return OIUtils.createLazyStructInspector(opDesc1TS.getTable());
			}

			@Override
			public void close() throws IOException {

			}

			@Override
			public void restart() throws Exception {

			}

			@Override
			public void printStatus(int printId) {

			}
		};
	}

	public static String generateInsertSql(Table tbl) {
		StringBuffer sb = new StringBuffer();
		sb.append("insert into " + tbl.getTableName() + " ( ");
		for (int i = 0; i < tbl.getFieldNames().size(); i++) {
			if (i == 0) {
				sb.append(tbl.getFieldNames().get(i));
			} else {
				sb.append(", ").append(tbl.getFieldNames().get(i));
			}
		}
		sb.append(" ) values (");
		for (int i = 0; i < tbl.getFieldNames().size(); i++) {
			if (i == 0) {
				sb.append("?");
			} else {
				sb.append(", ?");
			}
		}
		sb.append(")");

		return sb.toString();

	}

	public static String generateInsertSqlNormal(Table tbl) {
		StringBuffer sb = new StringBuffer();
		sb.append("insert into " + tbl.getTableName() + " ( ");
		for (int i = 0; i < tbl.getFieldNames().size(); i++) {
			if (i == 0) {
				sb.append(tbl.getFieldNames().get(i));
			} else {
				sb.append(", ").append(tbl.getFieldNames().get(i));
			}
		}
		sb.append(" ) values ");

		return sb.toString();
	}

	static private HashMap<String, ConnectionPool> connectionPools = null;

	synchronized public static ConnectionPool prepareDBSource(Table tbl) {

		boolean istpgtable = tbl.getTableType() == TableType.tpg;

		String dpIp = istpgtable ? TableUtils.getTpgDBHost(tbl) : TableUtils
				.getMysqlDBHost(tbl);
		String dbPort = istpgtable ? TableUtils.getTpgDBPort(tbl) : TableUtils
				.getMysqlDBPort(tbl);
		String dbName = istpgtable ? TableUtils.getTpgDBName(tbl) : TableUtils
				.getMysqlDBName(tbl);
		String dbUserName = istpgtable ? TableUtils.getTpgDBUserName(tbl)
				: TableUtils.getMysqlDBUserName(tbl);
		String passWd = istpgtable ? TableUtils.getTpgDBPassWD(tbl)
				: TableUtils.getMysqlDBPassWD(tbl);

		String DB_URL = (istpgtable ? "jdbc:postgresql://" : "jdbc:mysql://")
				+ dpIp + ":" + dbPort + "/" + dbName
				+ "?useUnicode=true&characterEncoding=UTF-8";
		if (connectionPools == null) {
			connectionPools = new HashMap<String, ConnectionPool>();
		}

		if (!connectionPools.containsKey(DB_URL)
				|| connectionPools.get(DB_URL) == null) {
			try {
				if (istpgtable) {
					Class<?> c = Class.forName("org.postgresql.Driver");
					Driver driver = (Driver) c.newInstance();
					DriverManager.registerDriver(driver);
				} else {
					Class<?> c = Class.forName("com.mysql.jdbc.Driver");
					Driver driver = (Driver) c.newInstance();
					DriverManager.registerDriver(driver);
				}
			} catch (Exception e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
			connectionPools.put(DB_URL, new ConnectionPool("trcdbconnection",
					3, 30, 50, 90, DB_URL, dbUserName, passWd));
		}
		return connectionPools.get(DB_URL);

	}

	public static Connection getConnNormal(Table tbl) {
		boolean istpgtable = tbl.getTableType() == TableType.tpg;

		try {
			if (istpgtable) {
				Class<?> c = Class.forName("org.postgresql.Driver");
				Driver driver = (Driver) c.newInstance();
				DriverManager.registerDriver(driver);
			} else {
				Class<?> c = Class.forName("com.mysql.jdbc.Driver");
				Driver driver = (Driver) c.newInstance();
				DriverManager.registerDriver(driver);
			}

			String dpIp = istpgtable ? TableUtils.getTpgDBHost(tbl)
					: TableUtils.getMysqlDBHost(tbl);
			String dbPort = istpgtable ? TableUtils.getTpgDBPort(tbl)
					: TableUtils.getMysqlDBPort(tbl);
			String dbName = istpgtable ? TableUtils.getTpgDBName(tbl)
					: TableUtils.getMysqlDBName(tbl);
			String dbUserName = istpgtable ? TableUtils.getTpgDBUserName(tbl)
					: TableUtils.getMysqlDBUserName(tbl);
			String passWd = istpgtable ? TableUtils.getTpgDBPassWD(tbl)
					: TableUtils.getMysqlDBPassWD(tbl);
			String DB_URL = (istpgtable ? "jdbc:postgresql://"
					: "jdbc:mysql://")
					+ dpIp
					+ ":"
					+ dbPort
					+ "/"
					+ dbName
					+ "?useUnicode=true&characterEncoding=UTF-8";
			return DriverManager.getConnection(DB_URL, dbUserName, passWd);
		} catch (Exception e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}
		return null;
	}

	public static ArrayList<Integer> processMetaData(ConnectionPool pools,
			Table tbl) {

		ArrayList<Integer> columnTypes = new ArrayList<Integer>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSetMetaData metadata = null;
		ResultSet rs = null;

		while (true) {
			try {
				columnTypes.clear();
				conn = pools.getConnection();
				// TODO need change for table
				String metaSql = prepareMetaSql(tbl);
				ps = conn.prepareStatement(metaSql);
				rs = ps.executeQuery();
				metadata = ps.getMetaData();

				for (int i = 0; i < metadata.getColumnCount(); i++) {
					columnTypes.add(metadata.getColumnType(i + 1));
				}
				break;
			} catch (Exception e) {
				log.error(TDBankUtils.getExceptionStack(e));
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (ps != null) {
						ps.close();
					}
					if (conn != null) {
						conn.close();
					}
				} catch (Exception e) {
					log.error(TDBankUtils.getExceptionStack(e));
				}
			}
		}

		return columnTypes;
	}

	private static String prepareMetaSql(Table tbl) {
		StringBuffer sb = new StringBuffer();
		sb.append("select ");
		for (int i = 0; i < tbl.getFieldNames().size(); i++) {
			if (i == 0) {
				sb.append(tbl.getFieldNames().get(i));
			} else {
				sb.append(", ").append(tbl.getFieldNames().get(i));
			}
		}
		sb.append(" from ");
		sb.append(tbl.getTableName());
		sb.append(" where 1=2");
		return sb.toString();
	}

	public static HTable getHbaseConn(Table tbl) {

		Configuration config = HBaseConfiguration.create();
		config.set("hbase.zookeeper.quorum", TableUtils.getHbaseZkQuorum(tbl));
		config.set("hbase.zookeeper.property.clientPort",
				TableUtils.getHbaseZkPort(tbl));
		config.set("zookeeper.znode.parent", TableUtils.getHbaseZkRoot(tbl));

		while (true) {
			try {
				HTable newTable = new HTable(config, tbl.getTableName());
				return newTable;
			} catch (Exception e) {
				log.info(TDBankUtils.getExceptionStack(e));
			}
		}
	}

	public static LazySimpleSerDe generateHbaseValueSerDe(byte[] separators,
			String valueName, TypeInfo valueType) {
		try {
			LazySimpleSerDe valueSerDe = new LazySimpleSerDe();
			Properties prop = new Properties();
			char fieldSpliter = (char) separators[0];
			char listSpliter = (char) separators[1];
			char mapSpliter = (char) separators[2];

			prop.put(serdeConstants.FIELD_DELIM, String.valueOf(fieldSpliter));
			prop.put(serdeConstants.COLLECTION_DELIM,
					String.valueOf(listSpliter));
			prop.put(serdeConstants.MAPKEY_DELIM, String.valueOf(mapSpliter));

			if (valueName != null || valueType != null) {
				prop.put(serdeConstants.LIST_COLUMNS, valueName);
				prop.put(serdeConstants.LIST_COLUMN_TYPES,
						valueType.getTypeName());
			}
			valueSerDe.initialize(null, prop);
			return valueSerDe;
		} catch (SerDeException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static HTablePool getHbaseTablePool(TableHbase tbl) {

		Configuration config = HBaseConfiguration.create();
		config.set("hbase.zookeeper.quorum", TableUtils.getHbaseZkQuorum(tbl));
		config.set("hbase.zookeeper.property.clientPort",
				TableUtils.getHbaseZkPort(tbl));
		config.set("zookeeper.znode.parent", TableUtils.getHbaseZkRoot(tbl));

		while (true) {
			try {
				return new HTablePool(config, 1000);
			} catch (Exception e) {
				log.info(TDBankUtils.getExceptionStack(e));
			}
		}
	}
}
