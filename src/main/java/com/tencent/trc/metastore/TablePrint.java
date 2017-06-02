package com.tencent.trc.metastore;

import java.util.ArrayList;
import java.util.HashMap;

public class TablePrint extends Table {

	private static final long serialVersionUID = -8143401473875162088L;

	public TablePrint(String tableName, ArrayList<Field> fields,
			HashMap<String, String> attrs) {
		super(tableName, fields, attrs, TableType.print);
	}
}
