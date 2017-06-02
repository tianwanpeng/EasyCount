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
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableConstantIntObjectInspector;

/**
 * GenericUDFArrayContains.
 * 
 */
@Description(name = "struct_get", value = "_FUNC_(struct, pos) - Returns TRUE if the array contains value.", extended = "Example:\n"
		+ "  > SELECT _FUNC_(struct(1, 2, 3), 2) FROM src LIMIT 1;\n"
		+ "  true")
public class GenericUDFStructGet extends GenericUDF {

	private static final int STRUCT_IDX = 0;
	private static final int VALUE_IDX = 1;
	private static final int ARG_COUNT = 2; // Number of arguments to this UDF
	private static final String FUNC_NAME = "STRUCT_GET"; // External Name

	private transient StructObjectInspector structOI;
	private transient StructField fieldRef;
	private int idx = 0;

	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException {

		// Check if two arguments were passed
		if (arguments.length != ARG_COUNT) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " accepts " + ARG_COUNT + " arguments.");
		}

		if (!(arguments[STRUCT_IDX] instanceof StructObjectInspector)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ "'s 1st param must be struct, but input is : "
					+ arguments[STRUCT_IDX]);
		}
		// Check if the comparison is supported for this type
		if (!(arguments[VALUE_IDX] instanceof WritableConstantIntObjectInspector)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ "'s 2nd param must be int constant ");
		}

		idx = ((WritableConstantIntObjectInspector) arguments[VALUE_IDX])
				.getWritableConstantValue().get();

		structOI = (StructObjectInspector) arguments[STRUCT_IDX];

		fieldRef = structOI.getAllStructFieldRefs().get(idx);

		return fieldRef.getFieldObjectInspector();
	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {
		Object struct = arguments[STRUCT_IDX].get();
		if (struct == null) {
			return null;
		}
		// List<Object> listObj = structOI.getStructFieldsDataAsList(struct);
		// if (listObj.size() <= idx) {
		// return null;
		// }
		return structOI.getStructFieldData(struct, fieldRef);

		// return listObj.get(idx);
	}

	@Override
	public String getDisplayString(String[] children) {
		assert (children.length == ARG_COUNT);
		return "struct_get(" + children[STRUCT_IDX] + ", "
				+ children[VALUE_IDX] + ")";
	}
}
