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

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.SettableMapObjectInspector;

/**
 * GenericUDFArrayContains.
 * 
 */
@Description(name = "map_del/map_rm", value = "_FUNC_(map, key) - Returns the map which have removed the key.", extended = "Example:\n"
		+ "  > SELECT _FUNC_(map(1, 2), 1) FROM src LIMIT 1;\n" + "  ")
public class GenericUDFMapDel extends GenericUDF {

	private static final int MAP_IDX = 0;
	private static final int KEY_IDX = 1;
	private static final int ARG_COUNT = 2; // Number of arguments to this UDF
	private static final String FUNC_NAME = "map_del"; // External Name

	private transient MapObjectInspector mapOI;
	private transient ObjectInspector mapKeyElementOI;

	private boolean inputsettable = false;

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
		if (mapOI instanceof SettableMapObjectInspector) {
			inputsettable = true;
		}

		mapKeyElementOI = mapOI.getMapKeyObjectInspector();
		ObjectInspector mapKeyElementOIStandard = ObjectInspectorUtils
				.getStandardObjectInspector(mapKeyElementOI);

		ObjectInspector keyOI = arguments[KEY_IDX];
		ObjectInspector keyOIStandard = ObjectInspectorUtils
				.getStandardObjectInspector(keyOI);
		if (!keyOIStandard.getClass().isAssignableFrom(
				mapKeyElementOIStandard.getClass())
				&& !mapKeyElementOIStandard.getClass().isAssignableFrom(
						keyOIStandard.getClass())) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " keyOI and mapKeyElementOI must be equal " + keyOI
					+ " : " + mapKeyElementOI);
		}

		if (inputsettable) {
			return mapOI;
		} else {
			return ObjectInspectorUtils.getStandardObjectInspector(mapOI);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {

		Object map = arguments[MAP_IDX].get();
		Object key = arguments[KEY_IDX].get();

		if (inputsettable) {
			Object oriObj = ((SettableMapObjectInspector) mapOI).getMap(map);
			Map<Object, Object> mapObj = null;
			if (oriObj == null) {
				mapObj = new HashMap<Object, Object>();
			} else {
				mapObj = (Map<Object, Object>) oriObj;
			}
			mapObj.remove(ObjectInspectorUtils.copyToStandardObject(key,
					mapKeyElementOI));
			return mapObj;
		} else {
			Object oriObj = ObjectInspectorUtils.copyToStandardObject(map,
					mapOI);
			Map<Object, Object> mapObj = null;
			if (oriObj == null) {
				mapObj = new HashMap<Object, Object>();
			} else {
				mapObj = (Map<Object, Object>) oriObj;
			}
			mapObj.remove(ObjectInspectorUtils.copyToStandardObject(key,
					mapKeyElementOI));
			return mapObj;
		}
	}

	@Override
	public String getDisplayString(String[] children) {
		assert (children.length == ARG_COUNT);
		return "map_del(" + children[MAP_IDX] + ", " + children[KEY_IDX] + ")";
	}
}
