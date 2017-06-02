package com.tencent.easycount.metastore;

import java.util.ArrayList;
import java.util.HashMap;

public class TableTube extends Table {

	private static final long serialVersionUID = 1375786160492509384L;

	public TableTube(String tableName, ArrayList<Field> fields,
			HashMap<String, String> attrs) {
		super(tableName, fields, attrs, TableType.tube);
	}
}
