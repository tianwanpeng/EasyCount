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
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.IntObjectInspector;

/**
 * GenericUDFArrayContains.
 * 
 */
@Description(name = "array_get", value = "_FUNC_(array, value) - Returns TRUE if the array contains value.", extended = "Example:\n"
		+ "  > SELECT _FUNC_(array(1, 2, 3), 2) FROM src LIMIT 1;\n" + "  true")
public class GenericUDFArrayGet extends GenericUDF {

	private static final int ARRAY_IDX = 0;
	private static final int VALUE_IDX = 1;
	private static final int ARG_COUNT = 2; // Number of arguments to this UDF
	private static final String FUNC_NAME = "ARRAY_GET"; // External Name

	private transient IntObjectInspector valueOI;
	private transient ListObjectInspector arrayOI;
	private transient ObjectInspector arrayElementOI;

	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException {

		// Check if two arguments were passed
		if (arguments.length != ARG_COUNT) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " accepts " + ARG_COUNT + " arguments.");
		}

		// Check if the comparison is supported for this type
		if (!(arguments[VALUE_IDX] instanceof IntObjectInspector)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ "'s 2nd param must be int ");
		}

		valueOI = (IntObjectInspector) arguments[VALUE_IDX];

		// Check if ARRAY_IDX argument is of category LIST
		if (!(arguments[ARRAY_IDX] instanceof ListObjectInspector)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ "'s 1st param must be list , but input is : "
					+ arguments[ARRAY_IDX]);
		}
		arrayOI = (ListObjectInspector) arguments[ARRAY_IDX];
		arrayElementOI = ((ListObjectInspector) arrayOI)
				.getListElementObjectInspector();
		return arrayElementOI;
		// return
		// ObjectInspectorUtils.getStandardObjectInspector(arrayElementOI);

	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {

		Object array = arguments[ARRAY_IDX].get();
		Object value = arguments[VALUE_IDX].get();

		int arrayLength = arrayOI.getListLength(array);

		// Check if array is null or empty or value is null
		if (value == null || arrayLength <= 0) {
			return null;
		}

		int idx = valueOI.get(value);
		return arrayOI.getListElement(array, idx);
		// return ObjectInspectorUtils.copyToStandardObject(
		// arrayOI.getListElement(array, idx), arrayElementOI);

	}

	@Override
	public String getDisplayString(String[] children) {
		assert (children.length == ARG_COUNT);
		return "array_get(" + children[ARRAY_IDX] + ", " + children[VALUE_IDX]
				+ ")";
	}
}
