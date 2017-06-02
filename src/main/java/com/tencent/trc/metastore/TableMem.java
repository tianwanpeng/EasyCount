package com.tencent.trc.metastore;

import java.util.ArrayList;
import java.util.HashMap;

public class TableMem extends Table {

	private static final long serialVersionUID = 1375786160492509384L;

	private final byte[] data;

	private final Field keyField;

	public TableMem(String tableName, ArrayList<Field> fields,
			HashMap<String, String> attrs, byte[] data) throws Exception {
		super(tableName, fields, attrs, TableType.mem);
		this.data = data;

		String keyFieldName = TableUtils.getKeyFieldName(this);
		Field f = null;

		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			if (field.getColumnName().equals(keyFieldName)) {
				f = field;
				break;
			}
		}

		keyField = f;

	}

	public Field getKeyField() {
		return keyField;
	}

	public byte[] getData() {
		return data;
	}

}
