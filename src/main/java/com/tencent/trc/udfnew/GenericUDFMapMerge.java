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

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;

/**
 * GenericUDFArrayContains.
 * 
 */
@Description(name = "map_merge", value = "_FUNC_(array, value) - Returns TRUE if the array contains value.", extended = "Example:\n"
		+ "  > SELECT _FUNC_(array(1, 2, 3), 2) FROM src LIMIT 1;\n" + "  true")
public class GenericUDFMapMerge extends GenericUDF {

	private static final String FUNC_NAME = "map_merge"; // External Name

	private transient MapObjectInspector mapOI;

	private HashMap<Object, Object> resMap;

	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException {

		// Check if two arguments were passed
		if (arguments.length <= 0) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " accepts at least 1 arguments.");
		}

		resMap = new HashMap<Object, Object>();
		for (ObjectInspector objoi : arguments) {
			if (!(objoi instanceof MapObjectInspector)) {
				throw new UDFArgumentException("The function " + FUNC_NAME
						+ "'s all param must be map , but input is : " + objoi);
			}
		}
		mapOI = (MapObjectInspector) arguments[0];
		return ObjectInspectorUtils.getStandardObjectInspector(arguments[0]);

	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {

		resMap.clear();
		for (DeferredObject dobj : arguments) {
			Map<?, ?> mapObj = (Map<?, ?>) ObjectInspectorUtils
					.copyToStandardObject(dobj.get(), mapOI);
			resMap.putAll(mapObj);
		}

		return resMap;
	}

	@Override
	public String getDisplayString(String[] children) {
		return "map_merge()";
	}
}
