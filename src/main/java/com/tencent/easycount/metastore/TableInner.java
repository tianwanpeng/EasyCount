package com.tencent.easycount.metastore;

import java.util.ArrayList;
import java.util.HashMap;

import com.tencent.easycount.parse.ParseUtils;

public class TableInner extends Table {

	private static final long serialVersionUID = -8143401473875162088L;
	private final int ms;

	public TableInner(final String tableName, final ArrayList<Field> fields,
			final HashMap<String, String> attrs) {
		super(tableName, fields, attrs, TableType.inner);
		this.ms = Integer.parseInt(tableName.substring(ParseUtils.INNERTABLE
				.length()));
	}

	public int getMs() {
		return this.ms;
	}

}
