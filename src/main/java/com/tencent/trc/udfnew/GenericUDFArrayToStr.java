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

import java.util.List;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.Text;

/**
 * GenericUDFArrayContains.
 * 
 */
@Description(name = "array_to_str", value = "_FUNC_(array, value) - Returns TRUE if the array contains value.", extended = "Example:\n"
		+ "  > SELECT _FUNC_(array(1, 2, 3), 2) FROM src LIMIT 1;\n" + "  true")
public class GenericUDFArrayToStr extends GenericUDF {

	private static final String FUNC_NAME = "ARRAY_TO_STR"; // External Name

	private transient ConstantObjectInspector splitOI;
	private transient ListObjectInspector arrayOI;

	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException {

		// Check if two arguments were passed
		if (arguments.length != 2) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " accepts " + 2 + " arguments.");
		}

		// Check if ARRAY_IDX argument is of category LIST
		if (!(arguments[0] instanceof ListObjectInspector)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ "'s 1st param must be list , but input is : "
					+ arguments[0]);
		}

		if (!(arguments[1] instanceof ConstantObjectInspector)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ "'s 2nd param must be constant , but input is : "
					+ arguments[0]);
		}

		arrayOI = (ListObjectInspector) arguments[0];
		splitOI = (ConstantObjectInspector) arguments[1];
		return PrimitiveObjectInspectorFactory.writableStringObjectInspector;
	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {

		Object array = arguments[0].get();

		Object split = splitOI.getWritableConstantValue();

		List<?> list = arrayOI.getList(array);
		if (list == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				sb.append(list.get(i));
			} else {
				sb.append(split).append(list.get(i));
			}
		}

		return new Text(sb.toString());
		// return ObjectInspectorUtils.copyToStandardObject(
		// arrayOI.getListElement(array, idx), arrayElementOI);

	}

	@Override
	public String getDisplayString(String[] children) {
		assert (children.length == 2);
		return "array_to_str(" + children[0] + ", " + children[1] + ")";
	}
}
