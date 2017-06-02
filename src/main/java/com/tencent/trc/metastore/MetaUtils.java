package com.tencent.trc.metastore;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.metastore.Table.TableType;
import com.tencent.trc.parse.ParseUtils;
import com.tencent.trc.parse.QB;
import com.tencent.trc.parse.Query;

public class MetaUtils {

	public static MetaData getMetaData(QB qb, TrcConfiguration config, Ini ini)
			throws Exception {

		MetaData md = new MetaData();
		// boolean testmode = config.getBoolean("testmode", false);
		boolean testmode = false;

		if (testmode) {
			for (Query q : qb.getRootQuerys()) {
				String tableId = q.getTableId();
				Table table = generateTestTable(tableId);
				if (table == null) {
					throw new RuntimeException("src table " + tableId
							+ " not exist .... ");
				}
				md.addTable(tableId, table);
			}
			for (Query q : qb.getDestQuerys()) {
				String tableId = q.getDestTableId();
				Table table = generateTestTable(tableId);
				if (table == null) {
					throw new RuntimeException("src table " + tableId
							+ " not exist .... ");
				}
				md.addTable(tableId, table);
			}

		} else {
			HashMap<String, Table> map = new HashMap<String, Table>();

			for (String key : ini.keySet()) {
				if (key.startsWith("tabledesc")) {
					Section sec = ini.get(key);
					Table tti = TableUtils.generateTable(sec.entrySet());
					map.put(tti.getTableName(), tti);
				}
			}

			for (Query q : qb.getRootQuerys()) {
				String tableId = q.getTableId();
				if (tableId.startsWith(ParseUtils.INNERTABLE)) {
					map.put(tableId, new TableInner(tableId,
							new ArrayList<Field>(),
							new HashMap<String, String>()));
				} else if (!map.containsKey(tableId)) {
					throw new RuntimeException("src table " + tableId
							+ " not exist .... ");
				}
				md.addTable(tableId, map.get(tableId));
			}

			for (Query q : qb.getDestQuerys()) {
				String tableId = q.getDestTableId();
				if (tableId.startsWith(ParseUtils.PRINTTABLE)) {
					HashMap<String, String> attrs = new HashMap<String, String>();
					attrs.put(TableUtils.TABLE_FIELD_SPLITTER, ",");
					attrs.put(TableUtils.TABLE_LIST_SPLITTER, ";");
					attrs.put(TableUtils.TABLE_MAP_SPLITTER, ":");
					map.put(tableId, new TablePrint(tableId,
							new ArrayList<Field>(), attrs));
				} else if (!map.containsKey(tableId)) {
					throw new RuntimeException("dest table " + tableId
							+ " not exist .... " + map.keySet());
				}
				md.addTable(tableId, map.get(tableId));
			}
		}
		return md;
	}

	private static Table generateTestTable(String tableId) {
		if (!testTables.containsKey(tableId)) {
			throw new RuntimeException("table not exist ! " + tableId);
		}
		return testTables.get(tableId);
	}

	public static MetaData getTestMetaData(QB qb) {
		MetaData md = new MetaData();

		for (Query q : qb.getRootQuerys()) {
			String tableId = q.getTableId();
			Table table = generateTestTable(tableId);
			md.addTable(tableId, table);
		}
		return md;
	}

	final static HashMap<String, Table> testTables = new HashMap<String, Table>();

	static {
		testTables.put("test", new Table("test", new ArrayList<Field>() {
			private static final long serialVersionUID = -8796779844855082706L;
			{
				add(new Field(TypeInfoFactory.stringTypeInfo, "a"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "b"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "c"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "d"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "e"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "f"));
			}
		}, new HashMap<String, String>(), TableType.stream));
		testTables.put("src", new Table("src", new ArrayList<Field>() {
			private static final long serialVersionUID = -8796779844855082706L;
			{
				add(new Field(TypeInfoFactory.stringTypeInfo, "a"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "b"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "c"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "d"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "e"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "f"));
			}
		}, new HashMap<String, String>(), TableType.stream));
		testTables.put("xx", new Table("xx", new ArrayList<Field>() {
			private static final long serialVersionUID = -8796779844855082706L;
			{
				add(new Field(TypeInfoFactory.stringTypeInfo, "a"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "b"));
				add(new Field(TypeInfoFactory.intTypeInfo, "c"));
				add(new Field(TypeInfoFactory.intTypeInfo, "d"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "e"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "f"));
			}
		}, new HashMap<String, String>(), TableType.stream));
		testTables.put("xxx", new Table("xxx", new ArrayList<Field>() {
			private static final long serialVersionUID = -8796779844855082706L;
			{
				add(new Field(TypeInfoFactory.stringTypeInfo, "a"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "b"));
				add(new Field(TypeInfoFactory.intTypeInfo, "c"));
				add(new Field(TypeInfoFactory.intTypeInfo, "d"));
				add(new Field(TypeInfoFactory.longTypeInfo, "e"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "f"));
			}
		}, new HashMap<String, String>(), TableType.stream));
		testTables.put("yy", new Table("yy", new ArrayList<Field>() {
			private static final long serialVersionUID = -8796779844855082706L;
			{
				add(new Field(TypeInfoFactory.stringTypeInfo, "a"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "b"));
				add(new Field(TypeInfoFactory.doubleTypeInfo, "c"));
				add(new Field(TypeInfoFactory.intTypeInfo, "d"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "e"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "f"));
			}
		}, new HashMap<String, String>(), TableType.stream));
		testTables.put("zz", new Table("zz", new ArrayList<Field>() {
			private static final long serialVersionUID = -8796779844855082706L;
			{
				add(new Field(TypeInfoFactory.stringTypeInfo, "a"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "b"));
				add(new Field(TypeInfoFactory.doubleTypeInfo, "c"));
				add(new Field(TypeInfoFactory.longTypeInfo, "d"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "e"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "f"));
			}
		}, new HashMap<String, String>(), TableType.stream));
		testTables.put("dest", new Table("dest", new ArrayList<Field>() {
			private static final long serialVersionUID = -8796779844855082706L;
			{
				add(new Field(TypeInfoFactory.stringTypeInfo, "a"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "b"));
				add(new Field(TypeInfoFactory.doubleTypeInfo, "c"));
				add(new Field(TypeInfoFactory.longTypeInfo, "d"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "e"));
				add(new Field(TypeInfoFactory.stringTypeInfo, "f"));
			}
		}, new HashMap<String, String>(), TableType.stream));
	}

}
