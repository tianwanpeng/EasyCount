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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;

/**
 * GenericUDFArrayContains.
 *
 */
@Description(name = "getallpath", value = "_FUNC_(array, value) - Returns TRUE if the array contains value.", extended = "Example:\n"
		+ "  > SELECT _FUNC_(array(1, 2, 3), 2) FROM src LIMIT 1;\n" + "  true")
public class GenericUDFGetAllPathInMap extends GenericUDF {

	private static final int ARRAY_IDX = 0;
	private static final int ARG_COUNT = 1; // Number of arguments to this UDF
	private static final String FUNC_NAME = "getallpath"; // External Name

	private transient MapObjectInspector mapOI;
	private transient ObjectInspector mapKeyElementOI;
	private transient ObjectInspector mapValueElementOI;

	@Override
	public ObjectInspector initialize(final ObjectInspector[] arguments)
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
		this.mapOI = (MapObjectInspector) arguments[ARRAY_IDX];
		this.mapKeyElementOI = this.mapOI.getMapKeyObjectInspector();
		final ObjectInspector mapKeyElementOIStandard = ObjectInspectorUtils
				.getStandardObjectInspector(this.mapKeyElementOI);

		this.mapValueElementOI = this.mapOI.getMapValueObjectInspector();

		if (!(this.mapValueElementOI instanceof ListObjectInspector)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ "'s mapValueElementOI must be list , but input is : "
					+ this.mapValueElementOI);
		}

		final ObjectInspector listElementOI = ((ListObjectInspector) this.mapValueElementOI)
				.getListElementObjectInspector();
		final ObjectInspector listElementOIStandard = ObjectInspectorUtils
				.getStandardObjectInspector(listElementOI);

		if (!listElementOIStandard.getClass().isAssignableFrom(
				mapKeyElementOIStandard.getClass())
				&& !mapKeyElementOIStandard.getClass().isAssignableFrom(
						listElementOIStandard.getClass())) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " listElementOI and mapKeyElementOI must be equal : "
					+ listElementOI + " : " + this.mapKeyElementOI);
		}

		final ObjectInspector pathOI = ObjectInspectorFactory
				.getStandardListObjectInspector(listElementOI);
		return ObjectInspectorFactory.getStandardListObjectInspector(pathOI);

	}

	@Override
	public Object evaluate(final DeferredObject[] arguments)
			throws HiveException {

		final Object map = arguments[ARRAY_IDX].get();

		final Map<?, ?> mapobj = this.mapOI.getMap(map);

		final HashMap<Object, HashSet<Object>> standardMap = new HashMap<Object, HashSet<Object>>();

		for (final Object key : mapobj.keySet()) {
			final Object value = mapobj.get(key);
			@SuppressWarnings("unchecked")
			final ArrayList<Object> standardV = (ArrayList<Object>) ObjectInspectorUtils
					.copyToStandardObject(value, this.mapValueElementOI);
			final Object standardK = ObjectInspectorUtils.copyToStandardObject(
					key, this.mapKeyElementOI);
			if (!standardMap.containsKey(standardK)) {
				standardMap.put(standardK, new HashSet<Object>());
			}
			for (final Object obj : standardV) {
				standardMap.get(standardK).add(obj);
			}
		}

		return generateAllPath(standardMap);

	}

	private Object generateAllPath(
			final HashMap<Object, HashSet<Object>> standardMap) {
		final ArrayList<ArrayList<Object>> resObj = new ArrayList<ArrayList<Object>>();

		for (final Object key : standardMap.keySet()) {
			final LinkedHashSet<Object> set = new LinkedHashSet<Object>();
			set.add(key);
			generateAllPathFrom(set, key, standardMap, resObj);
		}

		return resObj;
	}

	private void generateAllPathFrom(final LinkedHashSet<Object> set,
			final Object key,
			final HashMap<Object, HashSet<Object>> standardMap,
			final ArrayList<ArrayList<Object>> resObj) {
		final HashSet<Object> values = standardMap.get(key);
		if (values == null) {
			return;
		}

		for (final Object value : values) {
			if (set.contains(value) || (value == null)) {
				continue;
			}
			// LinkedHashSet<Object> setnew = new LinkedHashSet<Object>();
			// setnew.addAll(set);
			// setnew.add(value);
			set.add(value);

			final ArrayList<Object> path = new ArrayList<Object>();
			path.addAll(set);
			resObj.add(path);

			generateAllPathFrom(set, value, standardMap, resObj);

			set.remove(value);
		}
	}

	@Override
	public String getDisplayString(final String[] children) {
		assert (children.length == ARG_COUNT);
		return "getallpath(map)";
	}

	@SuppressWarnings("resource")
	public static void main(final String[] args) {
		final HashMap<Object, HashSet<Object>> standardMap = new HashMap<Object, HashSet<Object>>();
		standardMap.put(1, new HashSet<Object>());
		standardMap.get(1).add(2);
		standardMap.get(1).add(3);
		standardMap.put(2, new HashSet<Object>());
		standardMap.get(2).add(3);
		standardMap.get(2).add(4);
		standardMap.put(3, new HashSet<Object>());
		standardMap.get(3).add(2);
		standardMap.get(3).add(4);

		System.out.println(new GenericUDFGetAllPathInMap()
				.generateAllPath(standardMap));

	}
}
