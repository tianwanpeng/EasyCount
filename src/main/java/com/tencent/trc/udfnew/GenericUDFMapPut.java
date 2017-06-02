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
package com.tencent.trc.udfnew;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils.ObjectInspectorCopyOption;

import com.tencent.tdbank.mc.sorter.TDBankUtils;

/**
 * GenericUDFArrayContains.
 * 
 */
@Description(name = "array_get", value = "_FUNC_(array, value) - Returns TRUE if the array contains value.", extended = "Example:\n"
		+ "  > SELECT _FUNC_(array(1, 2, 3), 2) FROM src LIMIT 1;\n" + "  true")
public class GenericUDFMapPut extends GenericUDF {
	static final Log LOG = LogFactory.getLog(GenericUDFMapPut.class.getName());

	private static final int MAP_IDX = 0;
	private static final int KEY_IDX = 1;
	private static final int VALUE_IDX = 2;
	private static final int ARG_COUNT = 3; // Number of arguments to this UDF
	private static final String FUNC_NAME = "map_put"; // External Name

	private transient MapObjectInspector mapOI;
	// private transient MapObjectInspector mapJavaOI;
	private transient ObjectInspector mapKeyElementOI;
	private transient ObjectInspector mapValueElementOI;

	ObjectInspector keyOI;
	ObjectInspector valueOI;

	// private boolean inputsettable = false;

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
		// if (mapOI instanceof SettableMapObjectInspector) {
		// inputsettable = true;
		// }

		mapKeyElementOI = mapOI.getMapKeyObjectInspector();
		ObjectInspector mapKeyElementOIStandard = ObjectInspectorUtils
				.getStandardObjectInspector(mapKeyElementOI);

		keyOI = arguments[KEY_IDX];
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

		mapValueElementOI = mapOI.getMapValueObjectInspector();
		ObjectInspector mapValueElementOIStandard = ObjectInspectorUtils
				.getStandardObjectInspector(mapValueElementOI);

		valueOI = arguments[VALUE_IDX];
		ObjectInspector valueOIStandard = ObjectInspectorUtils
				.getStandardObjectInspector(valueOI);
		if (!ObjectInspectorUtils.compareTypes(mapValueElementOIStandard,
				valueOIStandard)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " valueOI and mapKeyElementOI must be equal " + valueOI
					+ " : " + mapValueElementOI);
		}

		// if (inputsettable) {
		// return mapOI;
		// } else {
		// mapJavaOI = (MapObjectInspector) ObjectInspectorUtils
		// .getStandardObjectInspector(mapOI,
		// ObjectInspectorCopyOption.JAVA);
		// mapJavaOI = ObjectInspectorFactory.getStandardMapObjectInspector(
		// ObjectInspectorUtils.getStandardObjectInspector(
		// mapKeyElementOI, ObjectInspectorCopyOption.JAVA),
		// ObjectInspectorUtils.getStandardObjectInspector(
		// mapValueElementOI, ObjectInspectorCopyOption.JAVA));
		return ObjectInspectorUtils.getStandardObjectInspector(mapOI,
				ObjectInspectorCopyOption.JAVA);
		// }

		// return ObjectInspectorUtils.getStandardObjectInspector(mapOI);

	}

	@SuppressWarnings("unchecked")
	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {

		Object map = arguments[MAP_IDX].get();
		Object key = arguments[KEY_IDX].get();
		Object value = arguments[VALUE_IDX].get();

		Map<Object, Object> mapObj = null;
		try {
			mapObj = (Map<Object, Object>) ObjectInspectorUtils
					.copyToStandardJavaObject(map, mapOI);
		} catch (Exception e) {
			LOG.warn("mapclass : " + map.getClass());
			LOG.warn("map : " + map);
			LOG.warn("evalutate fail : " + TDBankUtils.getExceptionStack(e));
		}
		if (mapObj == null) {
			mapObj = new HashMap<Object, Object>();
		}
		key = ObjectInspectorUtils.copyToStandardJavaObject(key, keyOI);
		value = ObjectInspectorUtils.copyToStandardJavaObject(value, valueOI);
		mapObj.put(key, value);
		return mapObj;

		// if (inputsettable) {
		// Object oriObj = ((SettableMapObjectInspector) mapOI).getMap(map);
		// Map<Object, Object> mapObj = null;
		// if (oriObj == null) {
		// mapObj = new HashMap<Object, Object>();
		// } else {
		// mapObj = (Map<Object, Object>) oriObj;
		// }
		// mapObj.put(ObjectInspectorUtils.copyToStandardObject(key,
		// mapKeyElementOI, ObjectInspectorCopyOption.WRITABLE),
		// ObjectInspectorUtils.copyToStandardObject(value,
		// mapValueElementOI));
		// return mapObj;
		// } else {
		// Object oriObj = ObjectInspectorUtils.copyToStandardObject(map,
		// mapOI);
		// Map<Object, Object> mapObj = null;
		// if (oriObj == null) {
		// mapObj = new HashMap<Object, Object>();
		// } else {
		// mapObj = (Map<Object, Object>) oriObj;
		// }
		// mapObj.put(ObjectInspectorUtils.copyToStandardObject(key,
		// mapKeyElementOI, ObjectInspectorCopyOption.WRITABLE),
		// ObjectInspectorUtils.copyToStandardObject(value,
		// mapValueElementOI));
		// return mapObj;
		// }
	}

	@Override
	public String getDisplayString(String[] children) {
		assert (children.length == ARG_COUNT);
		return "map_put(" + children[MAP_IDX] + ", " + children[VALUE_IDX]
				+ ")";
	}
}
