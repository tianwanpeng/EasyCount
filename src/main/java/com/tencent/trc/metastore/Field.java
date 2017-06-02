package com.tencent.trc.metastore;

import java.io.Serializable;

import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;

public class Field implements Serializable {
	private static final long serialVersionUID = 2404407183353904444L;
	final private TypeInfo type;
	final private String columnName;

	@Override
	public String toString() {
		return columnName + ":" + type;
	}

	public Field(TypeInfo type, String columnName) {
		this.type = type;
		this.columnName = columnName.toLowerCase();
	}

	public TypeInfo getType() {
		return type;
	}

	public String getColumnName() {
		return columnName;
	}
}
