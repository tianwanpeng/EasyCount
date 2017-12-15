package com.tencent.easycount.metastore;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import com.tencent.easycount.conf.ECConfiguration;
import com.tencent.easycount.metastore.Table.TableType;
import com.tencent.easycount.parse.ParseUtils;
import com.tencent.easycount.parse.QB;
import com.tencent.easycount.parse.Query;

public class MetaUtils {

	public static MetaData getMetaData(final QB qb,
			final ECConfiguration config, final Ini ini) throws Exception {

		final MetaData md = new MetaData();
		// boolean testmode = config.getBoolean("testmode", false);
		final boolean testmode = false;

		if (testmode) {
			for (final Query q : qb.getRootQuerys()) {
				final String tableId = q.getTableId();
				final Table table = generateTestTable(tableId);
				if (table == null) {
					throw new RuntimeException("src table " + tableId
							+ " not exist .... ");
				}
				md.addTable(tableId, table);
			}
			for (final Query q : qb.getDestQuerys()) {
				final String tableId = q.getDestTableId();
				final Table table = generateTestTable(tableId);
				if (table == null) {
					throw new RuntimeException("src table " + tableId
							+ " not exist .... ");
				}
				md.addTable(tableId, table);
			}

		} else {
			final HashMap<String, Table> map = new HashMap<String, Table>();

			for (final String key : ini.keySet()) {
				if (key.startsWith("tabledesc")) {
					final Section sec = ini.get(key);
					final Table tti = TableUtils.generateTable(sec.entrySet());
					map.put(tti.getTableName(), tti);
				}
			}

			for (final Query q : qb.getRootQuerys()) {
				final String tableId = q.getTableId();
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

			for (final Query q : qb.getDestQuerys()) {
				final String tableId = q.getDestTableId();
				if (tableId.startsWith(ParseUtils.PRINTTABLE)) {
					final HashMap<String, String> attrs = new HashMap<String, String>();
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

	private static Table generateTestTable(final String tableId) {
		if (!testTables.containsKey(tableId)) {
			throw new RuntimeException("table not exist ! " + tableId);
		}
		return testTables.get(tableId);
	}

	public static MetaData getTestMetaData(final QB qb) {
		final MetaData md = new MetaData();

		for (final Query q : qb.getRootQuerys()) {
			final String tableId = q.getTableId();
			final Table table = generateTestTable(tableId);
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
