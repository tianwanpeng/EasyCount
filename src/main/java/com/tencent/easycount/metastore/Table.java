package com.tencent.easycount.metastore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

import com.tencent.easycount.util.constants.Constants;

public class Table implements Serializable {
	private static final long serialVersionUID = -7653402102450702811L;
	final private String tableName;
	final private ArrayList<Field> fields;
	final private ArrayList<String> fieldNames;
	final private ArrayList<TypeInfo> fieldTypes;
	final private ArrayList<String> wholeFieldNames;
	final private ArrayList<TypeInfo> wholeFieldTypes;
	final private HashMap<String, String> tblAttrs;

	public HashMap<String, String> getTblAttrs() {
		return this.tblAttrs;
	}

	final private TableType tableType;

	public static enum TableType {
		stream, kafka, mem, tpg, mysql, tde, leveldb, redis, hbase, hdfs, inner, print
	}

	public String printstr() {
		final StringBuffer sb = new StringBuffer();
		sb.append(this.tableName).append("\n");
		sb.append("\t").append(this.fields.toString()).append("\n");
		sb.append("\t").append(this.tblAttrs.toString()).append("\n");
		return sb.toString();
	}

	public Table(final String tableName, final ArrayList<Field> fields,
			final HashMap<String, String> attrs, final TableType tableType) {
		this.tableName = tableName;
		this.fields = fields;
		this.tableType = tableType;
		this.tblAttrs = attrs;
		this.fieldNames = new ArrayList<String>();
		this.fieldTypes = new ArrayList<TypeInfo>();
		for (final Field field : fields) {
			this.fieldNames.add(field.getColumnName());
			this.fieldTypes.add(field.getType());
		}
		this.wholeFieldNames = new ArrayList<String>();
		this.wholeFieldNames.add(Constants.dataAttrs);
		this.wholeFieldNames.addAll(this.fieldNames);
		this.wholeFieldTypes = new ArrayList<TypeInfo>();
		this.wholeFieldTypes
		.add(TypeInfoFactory.getMapTypeInfo(
				TypeInfoFactory.stringTypeInfo,
				TypeInfoFactory.stringTypeInfo));
		this.wholeFieldTypes.addAll(this.fieldTypes);
	}

	public ArrayList<Field> getFields() {
		return this.fields;
	}

	public String getTableName() {
		return this.tableName;
	}

	@Override
	public String toString() {
		return this.tableName + " : " + this.fields.toString() + " : "
				+ this.tblAttrs.toString();
	}

	public TableType getTableType() {
		return this.tableType;
	}

	public ArrayList<String> getFieldNames() {
		return this.fieldNames;
	}

	public String getFieldName(final int i) {
		if (i < this.fieldNames.size()) {
			return this.fieldNames.get(i);
		}
		return null;
	}

	public ArrayList<TypeInfo> getStructTypes() {
		return this.fieldTypes;
	}

	public ArrayList<String> getWholeFieldNames() {
		return this.wholeFieldNames;
	}

	public ArrayList<TypeInfo> getWholeStructTypes() {
		return this.wholeFieldTypes;
	}

	public static void main(final String[] args) {
	}

	public boolean isKVTbl() {
		return (this.tableType == TableType.hbase)
				|| (this.tableType == TableType.mem)
				|| (this.tableType == TableType.redis)
				|| (this.tableType == TableType.tde);
	}
}
