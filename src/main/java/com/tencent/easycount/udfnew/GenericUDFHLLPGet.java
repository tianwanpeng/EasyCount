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
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.BinaryObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.LongWritable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import com.tencent.easycount.util.status.TDBankUtils;

/**
 * GenericUDFArrayContains.
 * 
 */
@Description(name = "hllp_get", value = "_FUNC_(array, value) - Returns TRUE if the array contains value.", extended = "Example:\n"
		+ "  > SELECT _FUNC_(array(1, 2, 3), 2) FROM src LIMIT 1;\n" + "  true")
public class GenericUDFHLLPGet extends GenericUDF {
	private static Logger log = LoggerFactory
			.getLogger(GenericUDFHLLPGet.class);
	private static final String FUNC_NAME = "hllp_get"; // External Name

	private transient ObjectInspector valueOI;

	// private transient LongWritable result;

	// private static transient LongWritable nullres = new LongWritable(0);

	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException {

		// Check if two arguments were passed
		if (arguments.length != 1) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " accepts 1 arguments.");
		}

		if (!(arguments[0] instanceof BinaryObjectInspector)) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ "'s args must be binary ");
		}

		valueOI = arguments[0];
		// result = new LongWritable(0);
		return PrimitiveObjectInspectorFactory.writableLongObjectInspector;

	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {

		if (arguments.length <= 0) {
			return null;
		}

		byte[] hllpdata = (byte[]) ObjectInspectorUtils
				.copyToStandardJavaObject(arguments[0].get(), valueOI);
		try {
			if (hllpdata == null || hllpdata.length == 0) {
				return null;
			}
			HyperLogLogPlus hllp = HyperLogLogPlus.Builder.build(hllpdata);
			// result.set(hllp.cardinality());
			// return result;
			return new LongWritable(hllp.cardinality());
		} catch (Exception e) {
			log.error(hllpdata.length + " : "
					+ TDBankUtils.getExceptionStack(e));
		}
		// return nullres;
		return null;

	}

	@Override
	public String getDisplayString(String[] children) {
		return "hllp_get()";
	}
}
