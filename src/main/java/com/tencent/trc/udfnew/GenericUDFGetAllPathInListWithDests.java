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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;

/**
 * GenericUDFArrayContains.
 * 
 */
@Description(name = "getallpathwithdests", value = "_FUNC_(array, value) - Returns TRUE if the array contains value.", extended = "Example:\n"
		+ "  > SELECT _FUNC_(array(1, 2, 3), 2) FROM src LIMIT 1;\n" + "  true")
public class GenericUDFGetAllPathInListWithDests extends GenericUDF {

	private static final int LIST_IDX = 0;
	private static final int DESTS_IDX = 1;
	private static final int ARG_COUNT = 2; // Number of arguments to this UDF
	private static final String FUNC_NAME = "getallpathwithdests";

	private transient ListObjectInspector listOI;
	private transient ListObjectInspector elementOI;
	private transient ObjectInspector objOI;

	private transient ListObjectInspector destsOI;
	private transient ObjectInspector destElementOI;

	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException {

		// Check if two arguments were passed
		if (arguments.length != ARG_COUNT) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " accepts " + ARG_COUNT + " arguments.");
		}

		// Check if ARRAY_IDX argument is of category LIST
		if (!(arguments[LIST_IDX] instanceof ListObjectInspector)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ "'s 1st param must be list , but input is : "
					+ arguments[LIST_IDX].getClass());
		}
		listOI = (ListObjectInspector) arguments[LIST_IDX];

		ObjectInspector eleOI = listOI.getListElementObjectInspector();
		if (!(eleOI instanceof ListObjectInspector)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ "'s 1st param element must be list , but input is : "
					+ eleOI.getClass());
		}
		elementOI = (ListObjectInspector) eleOI;

		objOI = elementOI.getListElementObjectInspector();
		ObjectInspector objOIStandard = ObjectInspectorUtils
				.getStandardObjectInspector(objOI);

		// Check if ARRAY_IDX argument is of category LIST
		if (!(arguments[DESTS_IDX] instanceof ListObjectInspector)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ "'s 2nd param must be list , but input is : "
					+ arguments[DESTS_IDX]);
		}

		destsOI = (ListObjectInspector) arguments[DESTS_IDX];
		destElementOI = destsOI.getListElementObjectInspector();
		ObjectInspector destElementOIStandard = ObjectInspectorUtils
				.getStandardObjectInspector(destElementOI);

		if (!destElementOIStandard.getClass().isAssignableFrom(
				objOIStandard.getClass())
				&& !objOIStandard.getClass().isAssignableFrom(
						destElementOIStandard.getClass())) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " listElementOI and mapKeyElementOI must be equal : "
					+ destElementOI + " : " + objOI);
		}

		ObjectInspector pathOI = ObjectInspectorFactory
				.getStandardListObjectInspector(objOI);
		return ObjectInspectorFactory.getStandardListObjectInspector(pathOI);

	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {

		List<?> lists = listOI.getList(arguments[LIST_IDX].get());

		HashMap<Object, HashSet<Object>> standardMap = new HashMap<Object, HashSet<Object>>();
		for (Object list : lists) {
			List<?> objs = elementOI.getList(list);
			if (objs == null || objs.size() < 2) {
				continue;
			}
			Object key = objs.get(0);
			Object standardK = ObjectInspectorUtils.copyToStandardObject(key,
					objOI);

			if (!standardMap.containsKey(standardK)) {
				standardMap.put(standardK, new HashSet<Object>());
			}

			for (int i = 1; i < objs.size(); i++) {
				standardMap.get(standardK).add(
						ObjectInspectorUtils.copyToStandardObject(objs.get(i),
								objOI));
			}
		}

		List<?> list = destsOI.getList(arguments[DESTS_IDX].get());
		HashSet<Object> destsSet = new HashSet<Object>();
		for (Object obj : list) {
			Object sobj = ObjectInspectorUtils.copyToStandardObject(obj,
					destElementOI);
			destsSet.add(sobj);
		}

		return generateAllPath(standardMap, destsSet);

	}

	private Object generateAllPath(
			HashMap<Object, HashSet<Object>> standardMap,
			HashSet<Object> destsSet) {
		ArrayList<ArrayList<Object>> resObj = new ArrayList<ArrayList<Object>>();

		for (Object key : standardMap.keySet()) {
			LinkedHashSet<Object> set = new LinkedHashSet<Object>();
			set.add(key);
			generateAllPathFrom(set, key, standardMap, resObj, destsSet);
		}

		return resObj;
	}

	private void generateAllPathFrom(LinkedHashSet<Object> set, Object key,
			HashMap<Object, HashSet<Object>> standardMap,
			ArrayList<ArrayList<Object>> resObj, HashSet<Object> destsSet) {
		HashSet<Object> values = standardMap.get(key);
		if (values == null) {
			return;
		}

		for (Object value : values) {
			if (set.contains(value) || value == null) {
				continue;
			}
			// LinkedHashSet<Object> setnew = new LinkedHashSet<Object>();
			// setnew.addAll(set);
			// setnew.add(value);
			set.add(value);

			if (destsSet.contains(value)) {
				ArrayList<Object> path = new ArrayList<Object>();
				path.addAll(set);
				resObj.add(path);
			}

			generateAllPathFrom(set, value, standardMap, resObj, destsSet);

			set.remove(value);
		}
	}

	@Override
	public String getDisplayString(String[] children) {
		assert (children.length == ARG_COUNT);
		return "getallpath(map)";
	}

	public static void main(String[] args) {
		HashMap<Object, HashSet<Object>> standardMap = new HashMap<Object, HashSet<Object>>();
		standardMap.put(1, new HashSet<Object>());
		standardMap.get(1).add(2);
		standardMap.get(1).add(3);
		standardMap.put(2, new HashSet<Object>());
		standardMap.get(2).add(3);
		standardMap.get(2).add(4);
		standardMap.put(3, new HashSet<Object>());
		standardMap.get(3).add(2);
		standardMap.get(3).add(4);

		System.out.println(new GenericUDFGetAllPathInListWithDests()
				.generateAllPath(standardMap, new HashSet<Object>() {
					private static final long serialVersionUID = -317186176436086804L;

					{
						add(2);
					}
				}));

	}
}
