package com.tencent.trc.metastore;

import java.util.ArrayList;
import java.util.HashMap;

public class TableStream extends Table {

	private static final long serialVersionUID = 1375786160492509384L;

	public TableStream(String tableName, ArrayList<Field> fields,
			HashMap<String, String> attrs) {
		super(tableName, fields, attrs, TableType.stream);
	}
}
