package com.tencent.easycount.metastore;

import java.util.ArrayList;
import java.util.HashMap;

public class TableTpg extends Table {

	private static final long serialVersionUID = -8143401473875162088L;

	public TableTpg(String tableName, ArrayList<Field> fields,
			HashMap<String, String> attrs) {
		super(tableName, fields, attrs, TableType.tpg);
		// TODO
	}

}
