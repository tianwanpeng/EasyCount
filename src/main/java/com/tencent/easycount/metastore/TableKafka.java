package com.tencent.easycount.metastore;

import java.util.ArrayList;
import java.util.HashMap;

public class TableKafka extends Table {

	private static final long serialVersionUID = 1375786160492509384L;

	public TableKafka(final String tableName, final ArrayList<Field> fields,
			final HashMap<String, String> attrs) {
		super(tableName, fields, attrs, TableType.kafka);
	}
}
