/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tencent.easycount.udfnew;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils.ObjectInspectorCopyOption;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;

/**
 * GenericUDFArrayContains.
 * 
 */
@Description(name = "map_get", value = "_FUNC_(array, value) - Returns TRUE if the array contains value.", extended = "Example:\n"
		+ "  > SELECT _FUNC_(map(2, 3), 2) FROM src LIMIT 1;\n" + "  3")
public class GenericUDFMapGet extends GenericUDF {

	private static final int ARRAY_IDX = 0;
	private static final int VALUE_IDX = 1;
	private static final int ARG_COUNT = 2; // Number of arguments to this UDF
	private static final String FUNC_NAME = "map_get"; // External Name

	private transient MapObjectInspector mapOI;
	private transient ObjectInspector valueOI;
	private transient ObjectInspector mapValueElementOI;

	private boolean mapKeyIsPrimitive = true;
	private ObjectInspectorCopyOption mapKeyOption = ObjectInspectorCopyOption.WRITABLE;

	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException {

		// Check if two arguments were passed
		if (arguments.length != ARG_COUNT) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " accepts " + ARG_COUNT + " arguments.");
		}

		// Check if ARRAY_IDX argument is of category LIST
		if (!(arguments[ARRAY_IDX] instanceof MapObjectInspector)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ "'s 1st param must be map , but input is : "
					+ arguments[ARRAY_IDX]);
		}
		mapOI = (MapObjectInspector) arguments[ARRAY_IDX];
		mapValueElementOI = mapOI.getMapValueObjectInspector();
		mapKeyIsPrimitive = mapOI.getMapKeyObjectInspector().getCategory() == Category.PRIMITIVE;
		if (mapKeyIsPrimitive) {
			mapKeyOption = ((PrimitiveObjectInspector) mapOI
					.getMapKeyObjectInspector()).preferWritable() ? ObjectInspectorCopyOption.WRITABLE
					: ObjectInspectorCopyOption.JAVA;
		}

		ObjectInspector mapKeyElementOI = mapOI.getMapKeyObjectInspector();

		ObjectInspector mapKeyElementOIStandard = ObjectInspectorUtils
				.getStandardObjectInspector(mapKeyElementOI);

		valueOI = arguments[VALUE_IDX];

		if (!ObjectInspectorUtils
				.compareTypes(valueOI, mapKeyElementOIStandard)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " valueOI and mapKeyElementOI must be equal " + valueOI
					+ " : " + mapKeyElementOI);
		}

		// return mapValueElementOI;
		return ObjectInspectorUtils
				.getStandardObjectInspector(mapValueElementOI);
	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {

		Object map = arguments[ARRAY_IDX].get();
		Object key = arguments[VALUE_IDX].get();

		// System.err.println("***************" + map + "   " + key);
		Object vv = mapOI.getMapValueElement(map, ObjectInspectorUtils
				.copyToStandardObject(key, valueOI, mapKeyOption));

		return ObjectInspectorUtils.copyToStandardObject(vv, mapValueElementOI);
		// return mapOI.getMapValueElement(map, key);
	}

	@Override
	public String getDisplayString(String[] children) {
		assert (children.length == ARG_COUNT);
		return "map_get(" + children[ARRAY_IDX] + ", " + children[VALUE_IDX]
				+ ")";
	}
}
