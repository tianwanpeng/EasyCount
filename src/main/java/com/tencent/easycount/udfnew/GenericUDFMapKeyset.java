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

import java.util.ArrayList;
import java.util.Map;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;

/**
 * GenericUDFArrayContains.
 * 
 */
@Description(name = "map_keyset", value = "_FUNC_(array, value) - Returns TRUE if the array contains value.", extended = "Example:\n"
		+ "  > SELECT _FUNC_(array(1, 2, 3), 2) FROM src LIMIT 1;\n" + "  true")
public class GenericUDFMapKeyset extends GenericUDF {

	private static final int ARRAY_IDX = 0;
	private static final int ARG_COUNT = 1; // Number of arguments to this UDF
	private static final String FUNC_NAME = "map_keyset"; // External Name

	private transient MapObjectInspector mapOI;
	private transient ObjectInspector mapKeyElementOI;

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
		mapKeyElementOI = mapOI.getMapKeyObjectInspector();

		return ObjectInspectorFactory
				.getStandardListObjectInspector(mapKeyElementOI);

	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {

		Object map = arguments[ARRAY_IDX].get();

		Map<?, ?> mapobj = mapOI.getMap(map);
		ArrayList<Object> reslist = new ArrayList<Object>(mapobj.size());
		for (Object obj : mapobj.keySet()) {
			reslist.add(ObjectInspectorUtils.copyToStandardJavaObject(obj,
					mapKeyElementOI));
		}

		return mapobj;

	}

	@Override
	public String getDisplayString(String[] children) {
		return "map_keyset()";
	}
}
