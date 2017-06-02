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
import org.apache.hadoop.hive.serde2.objectinspector.StandardStructObjectInspector;

/**
 * GenericUDFArrayContains.
 * 
 */
@Description(name = "map_struct", value = "_FUNC_(array, value) - Returns TRUE if the array contains value.", extended = "Example:\n"
		+ "  > SELECT _FUNC_(array(1, 2, 3), 2) FROM src LIMIT 1;\n" + "  true")
public class GenericUDFMapStruct extends GenericUDF {

	private static final int MAP_IDX = 0;
	private static final int ARG_COUNT = 1; // Number of arguments to this UDF
	private static final String FUNC_NAME = "map_struct"; // External Name

	private transient MapObjectInspector mapOI;
	private transient ObjectInspector mapKeyElementOI;
	private transient ObjectInspector mapValueElementOI;
	private transient StandardStructObjectInspector kvStructOI;

	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException {

		// Check if two arguments were passed
		if (arguments.length != ARG_COUNT) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " accepts " + ARG_COUNT + " arguments.");
		}

		// Check if ARRAY_IDX argument is of category LIST
		if (!(arguments[MAP_IDX] instanceof MapObjectInspector)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ "'s 1st param must be map , but input is : "
					+ arguments[MAP_IDX]);
		}
		mapOI = (MapObjectInspector) arguments[MAP_IDX];
		mapKeyElementOI = mapOI.getMapKeyObjectInspector();
		mapValueElementOI = mapOI.getMapValueObjectInspector();

		ArrayList<String> structFieldNames = new ArrayList<String>(2);
		structFieldNames.add("key");
		structFieldNames.add("value");
		ArrayList<ObjectInspector> structFieldObjectInspectors = new ArrayList<ObjectInspector>(
				2);
		structFieldObjectInspectors.add(mapKeyElementOI);
		structFieldObjectInspectors.add(mapValueElementOI);

		kvStructOI = ObjectInspectorFactory.getStandardStructObjectInspector(
				structFieldNames, structFieldObjectInspectors);

		return ObjectInspectorFactory
				.getStandardListObjectInspector(kvStructOI);
	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {

		Object map = arguments[MAP_IDX].get();

		Map<?, ?> mapobj = mapOI.getMap(map);

		ArrayList<ArrayList<Object>> resobj = new ArrayList<ArrayList<Object>>();
		for (Object key : mapobj.keySet()) {
			ArrayList<Object> entry = new ArrayList<Object>(2);
			// entry.add(ObjectInspectorUtils.copyToStandardObject(key,
			// mapKeyElementOI));
			// entry.add(ObjectInspectorUtils.copyToStandardObject(
			// mapobj.get(key), mapValueElementOI));
			entry.add(key);
			entry.add(mapobj.get(key));
			resobj.add(entry);
		}

		return resobj;

	}

	@Override
	public String getDisplayString(String[] children) {
		assert (children.length == ARG_COUNT);
		return "map_struct(map)";
	}
}
