package com.tencent.easycount.exec.utils;

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

import com.tencent.easycount.metastore.Table;
import com.tencent.easycount.metastore.Table.TableType;
import com.tencent.easycount.metastore.TableUtils;
import com.tencent.easycount.util.status.TDBankUtils;

public class OIUtils {
	private static Logger log = LoggerFactory.getLogger(OIUtils.class);

	public static ObjectInspector createLazyStructInspector(
			final StructTypeInfo type) {
		final ArrayList<String> columnNames = type.getAllStructFieldNames();
		final ArrayList<TypeInfo> typeInfos = type.getAllStructFieldTypeInfos();

		return createLazyStructInspector(columnNames, typeInfos,
				TableUtils.defaultSeparators);
	}

	public static LazySimpleStructObjectInspector createLazyStructInspector(
			final Table tbl) {

		final byte[] separators = TableUtils.generateSeparators(tbl);

		if ((tbl.getTableType() == TableType.stream)
				|| (tbl.getTableType() == TableType.tube)) {
			return createLazyStructInspector(tbl.getWholeFieldNames(),
					tbl.getWholeStructTypes(), separators);
		} else {
			return createLazyStructInspector(tbl.getFieldNames(),
					tbl.getStructTypes(), separators);
		}
	}

	public static LazySimpleStructObjectInspector createLazyStructInspector(
			final ArrayList<String> columnNames,
			final ArrayList<TypeInfo> typeInfos, final byte[] separators) {
		final Text nullSequence = new Text("\\N");
		final boolean lastColumnTakesRest = false;
		final boolean escaped = false;
		final byte escapeChar = (byte) '\\';

		try {
			return (LazySimpleStructObjectInspector) LazyFactory
					.createLazyStructInspector(columnNames, typeInfos,
							separators, nullSequence, lastColumnTakesRest,
							escaped, escapeChar);
		} catch (final SerDeException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}

		return null;
	}

	public static LazyBinaryStructObjectInspector createLazyBinaryStructInspector(
			final StructTypeInfo type) {
		return (LazyBinaryStructObjectInspector) LazyBinaryUtils
				.getLazyBinaryObjectInspectorFromTypeInfo(type);
	}

	public static LazyBinaryStructObjectInspector createLazyBinaryStructInspector(
			final ArrayList<String> names, final ArrayList<TypeInfo> structTypes) {
		return createLazyBinaryStructInspector((StructTypeInfo) TypeInfoFactory
				.getStructTypeInfo(names, structTypes));
	}
}
