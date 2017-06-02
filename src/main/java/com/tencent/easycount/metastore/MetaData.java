package com.tencent.easycount.metastore;

import java.util.HashMap;

public class MetaData {
	final private HashMap<String, Table> tables;

	public String printStr() {

		StringBuffer sb = new StringBuffer();
		sb.append(tables);
		return sb.toString();

	}

	public MetaData() {
		this.tables = new HashMap<String, Table>();
	}

	public Table getTable(String tableId) {
		return this.tables.get(tableId);
	}

	public void addTable(String talbeId, Table table) {
		this.tables.put(talbeId, table);
	}

	public HashMap<String, Table> getTables() {
		return tables;
	}

}
