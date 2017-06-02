package com.tencent.easycount.exec.io;

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
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.okhttp.ConnectionPool;
import com.tencent.easycount.exec.physical.Data1Generator;
import com.tencent.easycount.exec.utils.OIUtils;
import com.tencent.easycount.metastore.Table;
import com.tencent.easycount.metastore.Table.TableType;
import com.tencent.easycount.metastore.TableHbase;
import com.tencent.easycount.metastore.TableUtils;
import com.tencent.easycount.plan.logical.OpDesc1TS;
import com.tencent.easycount.util.status.TDBankUtils;

@SuppressWarnings("deprecation")
public class DataIOUtils {
	private static Logger log = LoggerFactory.getLogger(DataIOUtils.class);

	public static Data1Source generateDimDataSource(final String sourceId,
			final OpDesc1TS opDesc1TS, final Data1Generator emitter) {
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
			public void printStatus(final int printId) {

			}
		};
	}

	public static String generateInsertSql(final Table tbl) {
		final StringBuffer sb = new StringBuffer();
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

	public static String generateInsertSqlNormal(final Table tbl) {
		final StringBuffer sb = new StringBuffer();
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

	synchronized public static ConnectionPool prepareDBSource(final Table tbl) {

		final boolean istpgtable = tbl.getTableType() == TableType.tpg;

		final String dpIp = istpgtable ? TableUtils.getTpgDBHost(tbl)
				: TableUtils.getMysqlDBHost(tbl);
		final String dbPort = istpgtable ? TableUtils.getTpgDBPort(tbl)
				: TableUtils.getMysqlDBPort(tbl);
		final String dbName = istpgtable ? TableUtils.getTpgDBName(tbl)
				: TableUtils.getMysqlDBName(tbl);
		final String dbUserName = istpgtable ? TableUtils.getTpgDBUserName(tbl)
				: TableUtils.getMysqlDBUserName(tbl);
		final String passWd = istpgtable ? TableUtils.getTpgDBPassWD(tbl)
				: TableUtils.getMysqlDBPassWD(tbl);

		final String DB_URL = (istpgtable ? "jdbc:postgresql://"
				: "jdbc:mysql://")
				+ dpIp
				+ ":"
				+ dbPort
				+ "/"
				+ dbName
				+ "?useUnicode=true&characterEncoding=UTF-8";
		if (connectionPools == null) {
			connectionPools = new HashMap<String, ConnectionPool>();
		}

		if (!connectionPools.containsKey(DB_URL)
				|| (connectionPools.get(DB_URL) == null)) {
			try {
				if (istpgtable) {
					final Class<?> c = Class.forName("org.postgresql.Driver");
					final Driver driver = (Driver) c.newInstance();
					DriverManager.registerDriver(driver);
				} else {
					final Class<?> c = Class.forName("com.mysql.jdbc.Driver");
					final Driver driver = (Driver) c.newInstance();
					DriverManager.registerDriver(driver);
				}
			} catch (final Exception e) {
				// log.error(TDBankUtils.getExceptionStack(e));
			}
			connectionPools.put(DB_URL, new ConnectionPool("trcdbconnection",
					3, 30, 50, 90, DB_URL, dbUserName, passWd));
		}
		return connectionPools.get(DB_URL);

	}

	public static Connection getConnNormal(final Table tbl) {
		final boolean istpgtable = tbl.getTableType() == TableType.tpg;

		try {
			if (istpgtable) {
				final Class<?> c = Class.forName("org.postgresql.Driver");
				final Driver driver = (Driver) c.newInstance();
				DriverManager.registerDriver(driver);
			} else {
				final Class<?> c = Class.forName("com.mysql.jdbc.Driver");
				final Driver driver = (Driver) c.newInstance();
				DriverManager.registerDriver(driver);
			}

			final String dpIp = istpgtable ? TableUtils.getTpgDBHost(tbl)
					: TableUtils.getMysqlDBHost(tbl);
			final String dbPort = istpgtable ? TableUtils.getTpgDBPort(tbl)
					: TableUtils.getMysqlDBPort(tbl);
			final String dbName = istpgtable ? TableUtils.getTpgDBName(tbl)
					: TableUtils.getMysqlDBName(tbl);
			final String dbUserName = istpgtable ? TableUtils
					.getTpgDBUserName(tbl) : TableUtils.getMysqlDBUserName(tbl);
					final String passWd = istpgtable ? TableUtils.getTpgDBPassWD(tbl)
							: TableUtils.getMysqlDBPassWD(tbl);
					final String DB_URL = (istpgtable ? "jdbc:postgresql://"
							: "jdbc:mysql://")
							+ dpIp
							+ ":"
							+ dbPort
							+ "/"
							+ dbName
							+ "?useUnicode=true&characterEncoding=UTF-8";
					return DriverManager.getConnection(DB_URL, dbUserName, passWd);
		} catch (final Exception e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}
		return null;
	}

	public static ArrayList<Integer> processMetaData(
			final ConnectionPool pools, final Table tbl) {

		final ArrayList<Integer> columnTypes = new ArrayList<Integer>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSetMetaData metadata = null;
		ResultSet rs = null;

		while (true) {
			try {
				columnTypes.clear();
				conn = pools.getConnection();
				// TODO need change for table
				final String metaSql = prepareMetaSql(tbl);
				ps = conn.prepareStatement(metaSql);
				rs = ps.executeQuery();
				metadata = ps.getMetaData();

				for (int i = 0; i < metadata.getColumnCount(); i++) {
					columnTypes.add(metadata.getColumnType(i + 1));
				}
				break;
			} catch (final Exception e) {
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
				} catch (final Exception e) {
					log.error(TDBankUtils.getExceptionStack(e));
				}
			}
		}

		return columnTypes;
	}

	private static String prepareMetaSql(final Table tbl) {
		final StringBuffer sb = new StringBuffer();
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

	public static HTable getHbaseConn(final Table tbl) {

		final Configuration config = HBaseConfiguration.create();
		config.set("hbase.zookeeper.quorum", TableUtils.getHbaseZkQuorum(tbl));
		config.set("hbase.zookeeper.property.clientPort",
				TableUtils.getHbaseZkPort(tbl));
		config.set("zookeeper.znode.parent", TableUtils.getHbaseZkRoot(tbl));

		while (true) {
			try {
				final HTable newTable = new HTable(config, tbl.getTableName());
				return newTable;
			} catch (final Exception e) {
				log.info(TDBankUtils.getExceptionStack(e));
			}
		}
	}

	public static LazySimpleSerDe generateHbaseValueSerDe(
			final byte[] separators, final String valueName,
			final TypeInfo valueType) {
		try {
			final LazySimpleSerDe valueSerDe = new LazySimpleSerDe();
			final Properties prop = new Properties();
			final char fieldSpliter = (char) separators[0];
			final char listSpliter = (char) separators[1];
			final char mapSpliter = (char) separators[2];

			prop.put(serdeConstants.FIELD_DELIM, String.valueOf(fieldSpliter));
			prop.put(serdeConstants.COLLECTION_DELIM,
					String.valueOf(listSpliter));
			prop.put(serdeConstants.MAPKEY_DELIM, String.valueOf(mapSpliter));

			if ((valueName != null) || (valueType != null)) {
				prop.put(serdeConstants.LIST_COLUMNS, valueName);
				prop.put(serdeConstants.LIST_COLUMN_TYPES,
						valueType.getTypeName());
			}
			valueSerDe.initialize(null, prop);
			return valueSerDe;
		} catch (final SerDeException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static HTablePool getHbaseTablePool(final TableHbase tbl) {

		final Configuration config = HBaseConfiguration.create();
		config.set("hbase.zookeeper.quorum", TableUtils.getHbaseZkQuorum(tbl));
		config.set("hbase.zookeeper.property.clientPort",
				TableUtils.getHbaseZkPort(tbl));
		config.set("zookeeper.znode.parent", TableUtils.getHbaseZkRoot(tbl));

		while (true) {
			try {
				return new HTablePool(config, 1000);
			} catch (final Exception e) {
				log.info(TDBankUtils.getExceptionStack(e));
			}
		}
	}
}
