package com.tencent.trc.metastore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

import com.tencent.trc.util.constants.Constants;

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
		return tblAttrs;
	}

	final private TableType tableType;

	public static enum TableType {
		stream, tube, mem, tpg, mysql, tde, leveldb, redis, hbase, hdfs, inner, print
	}

	public String printstr() {
		StringBuffer sb = new StringBuffer();
		sb.append(tableName).append("\n");
		sb.append("\t").append(fields.toString()).append("\n");
		sb.append("\t").append(tblAttrs.toString()).append("\n");
		return sb.toString();
	}

	public Table(String tableName, ArrayList<Field> fields,
			HashMap<String, String> attrs, TableType tableType) {
		this.tableName = tableName;
		this.fields = fields;
		this.tableType = tableType;
		this.tblAttrs = attrs;
		this.fieldNames = new ArrayList<String>();
		this.fieldTypes = new ArrayList<TypeInfo>();
		for (Field field : fields) {
			fieldNames.add(field.getColumnName());
			fieldTypes.add(field.getType());
		}
		this.wholeFieldNames = new ArrayList<String>();
		this.wholeFieldNames.add(Constants.dataAttrs);
		this.wholeFieldNames.addAll(fieldNames);
		this.wholeFieldTypes = new ArrayList<TypeInfo>();
		this.wholeFieldTypes
				.add(TypeInfoFactory.getMapTypeInfo(
						TypeInfoFactory.stringTypeInfo,
						TypeInfoFactory.stringTypeInfo));
		this.wholeFieldTypes.addAll(fieldTypes);
	}

	public ArrayList<Field> getFields() {
		return fields;
	}

	public String getTableName() {
		return tableName;
	}

	@Override
	public String toString() {
		return tableName + " : " + fields.toString() + " : "
				+ tblAttrs.toString();
	}

	public TableType getTableType() {
		return tableType;
	}

	public ArrayList<String> getFieldNames() {
		return fieldNames;
	}

	public String getFieldName(int i) {
		if (i < fieldNames.size()) {
			return fieldNames.get(i);
		}
		return null;
	}

	public ArrayList<TypeInfo> getStructTypes() {
		return fieldTypes;
	}

	public ArrayList<String> getWholeFieldNames() {
		return this.wholeFieldNames;
	}

	public ArrayList<TypeInfo> getWholeStructTypes() {
		return this.wholeFieldTypes;
	}

	public static void main(String[] args) {
	}

	public boolean isKVTbl() {
		return this.tableType == TableType.hbase
				|| this.tableType == TableType.mem
				|| this.tableType == TableType.redis
				|| this.tableType == TableType.tde;
	}
}
