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

import java.util.Map;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.Text;

/**
 * GenericUDFArrayContains.
 * 
 */
@Description(name = "map_to_str", value = "_FUNC_(map, split1, split2) - Returns TRUE if the array contains value.", extended = "Example:\n"
		+ "  > SELECT _FUNC_(map(1=1, 2=2, 3=3), '&', '=') FROM src LIMIT 1;\n"
		+ "  true")
public class GenericUDFMapToStr extends GenericUDF {

	private static final String FUNC_NAME = "MAP_TO_STR"; // External Name

	private transient ConstantObjectInspector splitOI1;
	private transient ConstantObjectInspector splitOI2;
	private transient MapObjectInspector mapOI;

	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException {

		// Check if three arguments were passed
		if (arguments.length != 3) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " accepts " + 3 + " arguments.");
		}

		// Check if MAP_IDX argument is of category MAP
		if (!(arguments[0] instanceof MapObjectInspector)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ "'s 1st param must be map , but input is : "
					+ arguments[0]);
		}

		if (!(arguments[1] instanceof ConstantObjectInspector)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ "'s 2nd param must be constant , but input is : "
					+ arguments[1]);
		}

		if (!(arguments[2] instanceof ConstantObjectInspector)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ "'s 3rd param must be constant , but input is : "
					+ arguments[2]);
		}

		mapOI = (MapObjectInspector) arguments[0];
		splitOI1 = (ConstantObjectInspector) arguments[1];
		splitOI2 = (ConstantObjectInspector) arguments[2];
		return PrimitiveObjectInspectorFactory.writableStringObjectInspector;
	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {

		Object mapobj = arguments[0].get();

		Object split1 = splitOI1.getWritableConstantValue();
		Object split2 = splitOI2.getWritableConstantValue();

		Map<?, ?> map = mapOI.getMap(mapobj);
		if (map == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		Object[] keyValuesPairs = map.entrySet().toArray();
		int mapsize = map.size();
		for (int i = 0; i < mapsize; i++) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) keyValuesPairs[i];
			Object key = entry.getKey();
			Object val = entry.getValue();

			if (i == 0) {
				sb.append(key).append(split2).append(val);
			} else {
				sb.append(split1).append(key).append(split2).append(val);
			}
		}

		return new Text(sb.toString());
		// return ObjectInspectorUtils.copyToStandardObject(
		// arrayOI.getListElement(array, idx), arrayElementOI);

	}

	@Override
	public String getDisplayString(String[] children) {
		assert (children.length == 3);
		return "map_to_str(" + children[0] + ", " + children[1] + ", "
				+ children[2] + ")";
	}
}
