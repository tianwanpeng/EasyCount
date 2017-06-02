package com.tencent.trc.exec.utils;

import java.util.ArrayList;

import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.lazy.LazyFactory;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.LazySimpleStructObjectInspector;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryUtils;
import org.apache.hadoop.hive.serde2.lazybinary.objectinspector.LazyBinaryStructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.trc.metastore.Table;
import com.tencent.trc.metastore.TableUtils;
import com.tencent.trc.metastore.Table.TableType;

public class OIUtils {
	private static Logger log = LoggerFactory.getLogger(OIUtils.class);

	public static ObjectInspector createLazyStructInspector(StructTypeInfo type) {
		ArrayList<String> columnNames = type.getAllStructFieldNames();
		ArrayList<TypeInfo> typeInfos = type.getAllStructFieldTypeInfos();

		return createLazyStructInspector(columnNames, typeInfos,
				TableUtils.defaultSeparators);
	}

	public static LazySimpleStructObjectInspector createLazyStructInspector(
			Table tbl) {

		byte[] separators = TableUtils.generateSeparators(tbl);

		if (tbl.getTableType() == TableType.stream
				|| tbl.getTableType() == TableType.tube) {
			return createLazyStructInspector(tbl.getWholeFieldNames(),
					tbl.getWholeStructTypes(), separators);
		} else {
			return createLazyStructInspector(tbl.getFieldNames(),
					tbl.getStructTypes(), separators);
		}
	}

	public static LazySimpleStructObjectInspector createLazyStructInspector(
			ArrayList<String> columnNames, ArrayList<TypeInfo> typeInfos,
			byte[] separators) {
		Text nullSequence = new Text("\\N");
		boolean lastColumnTakesRest = false;
		boolean escaped = false;
		byte escapeChar = (byte) '\\';

		try {
			return (LazySimpleStructObjectInspector) LazyFactory
					.createLazyStructInspector(columnNames, typeInfos,
							separators, nullSequence, lastColumnTakesRest,
							escaped, escapeChar);
		} catch (SerDeException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}

		return null;
	}

	public static LazyBinaryStructObjectInspector createLazyBinaryStructInspector(
			StructTypeInfo type) {
		return (LazyBinaryStructObjectInspector) LazyBinaryUtils
				.getLazyBinaryObjectInspectorFromTypeInfo(type);
	}

	public static LazyBinaryStructObjectInspector createLazyBinaryStructInspector(
			ArrayList<String> names, ArrayList<TypeInfo> structTypes) {
		return createLazyBinaryStructInspector((StructTypeInfo) TypeInfoFactory
				.getStructTypeInfo(names, structTypes));
	}
}
