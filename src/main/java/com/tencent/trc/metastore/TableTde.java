package com.tencent.trc.metastore;

import java.util.ArrayList;
import java.util.HashMap;

public class TableTde extends Table {

	private static final long serialVersionUID = -8143401473875162088L;

	public TableTde(String tableName, ArrayList<Field> fields,
			HashMap<String, String> attrs) throws Exception {
		super(tableName, fields, attrs, TableType.tde);
	}
}
