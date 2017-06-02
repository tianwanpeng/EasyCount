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
import java.util.Arrays;
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
@Description(name = "array_merge", value = "_FUNC_(array, value) - Returns TRUE if the array contains value.", extended = "Example:\n"
		+ "  > SELECT _FUNC_(array(1, 2, 3), 2) FROM src LIMIT 1;\n" + "  true")
public class GenericUDFArrayMerge extends GenericUDF {

	private static final int ARRAY_IDX = 0;
	private static final int VALUE_IDX = 1;
	private static final int ARG_COUNT = 2; // Number of arguments to this UDF
	private static final String FUNC_NAME = "ARRAY_MERGE"; // External Name

	private transient boolean[] islist;
	private transient ObjectInspector[] inputOIs;

	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException {

		// Check if two arguments were passed
		if (arguments.length < 2) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " accepts at least " + 2 + " arguments.");
		}

		islist = new boolean[arguments.length];
		Arrays.fill(islist, true);
		ObjectInspector elementOI = null;
		ObjectInspector elementOIStandard = null;
		for (int i = 0; i < arguments.length; i++) {
			if (i == 0) {
				if (arguments[i] instanceof ListObjectInspector) {
					elementOI = ((ListObjectInspector) arguments[i])
							.getListElementObjectInspector();
					islist[i] = true;
				} else {
					elementOI = arguments[i];
					islist[i] = false;
				}
				elementOIStandard = ObjectInspectorUtils
						.getStandardObjectInspector(elementOI);
			} else {
				ObjectInspector elementOI1 = null;
				if (arguments[i] instanceof ListObjectInspector) {
					elementOI1 = ((ListObjectInspector) arguments[i])
							.getListElementObjectInspector();
					islist[i] = true;
				} else {
					elementOI1 = arguments[i];
					islist[i] = false;
				}
				ObjectInspector elementOI1Standard = ObjectInspectorUtils
						.getStandardObjectInspector(elementOI1);
				if (!elementOI1Standard.getClass().isAssignableFrom(
						elementOIStandard.getClass())
						&& !elementOIStandard.getClass().isAssignableFrom(
								elementOI1Standard.getClass())) {
					throw new UDFArgumentException("The function " + FUNC_NAME
							+ "'s all param must be same but : "
							+ elementOI.toString() + " , and "
							+ elementOI1.toString() + " received ...");
				}
			}
		}
		inputOIs = arguments;

		return ObjectInspectorFactory.getStandardListObjectInspector(elementOI);

	}

	@SuppressWarnings("unchecked")
	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {
		ArrayList<Object> outputObj = new ArrayList<Object>();
		for (int i = 0; i < arguments.length; i++) {
			if (islist[i]) {
				List<Object> listObj = (List<Object>) ObjectInspectorUtils
						.copyToStandardObject(arguments[i].get(), inputOIs[i]);
				if (listObj != null) {
					outputObj.addAll(listObj);
				}
			} else {
				outputObj.add(ObjectInspectorUtils.copyToStandardObject(
						arguments[i].get(), inputOIs[i]));
			}
		}
		return outputObj;
	}

	@Override
	public String getDisplayString(String[] children) {
		assert (children.length == ARG_COUNT);
		return "array_merge(" + children[ARRAY_IDX] + ", "
				+ children[VALUE_IDX] + ")";
	}
}
