package com.tencent.trc.metastore;

import java.util.ArrayList;
import java.util.HashMap;

import com.tencent.trc.parse.ParseUtils;

public class TableInner extends Table {

	private static final long serialVersionUID = -8143401473875162088L;
	private final int ms;

	public TableInner(String tableName, ArrayList<Field> fields,
			HashMap<String, String> attrs) {
		super(tableName, fields, attrs, TableType.inner);
		this.ms = Integer.parseInt(tableName.substring(ParseUtils.INNERTABLE
				.length()));
	}

	public int getMs() {
		return ms;
	}

}
