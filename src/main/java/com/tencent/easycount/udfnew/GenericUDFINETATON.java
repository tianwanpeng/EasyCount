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
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hadoop.io.LongWritable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.util.status.TDBankUtils;

/**
 * GenericUDFArrayContains.
 * 
 */
@Description(name = "inet_aton", value = "_FUNC_(string) - Returns long ", extended = "Example:\n"
		+ "  > SELECT _FUNC_(0.0.1.1) FROM src LIMIT 1;\n" + "  257")
public class GenericUDFINETATON extends GenericUDF {
	private static Logger log = LoggerFactory
			.getLogger(GenericUDFINETATON.class);

	private static final String FUNC_NAME = "inet_aton"; // External Name

	private transient LongWritable result;

	// private transient static BytesWritable nullres = new BytesWritable();

	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException {

		// Check if two arguments were passed
		if (arguments.length != 1) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " accepts 1 arguments.");
		}

		for (int i = 0; i < arguments.length; i++) {
			if (!(arguments[i] instanceof StringObjectInspector)) {
				throw new UDFArgumentException("The function " + FUNC_NAME
						+ "'s args must be string ");
			}
		}

		return PrimitiveObjectInspectorFactory.writableLongObjectInspector;
	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {

		if (arguments.length <= 0) {
			return null;
		}

		try {
			String[] segments = arguments[0].get().toString().split("\\.");
			long ipint = (long) ((Long.parseLong(segments[0]) << 24)
					+ (Long.parseLong(segments[1]) << 16)
					+ (Long.parseLong(segments[2]) << 8) + Long
					.parseLong(segments[3]));
			result.set(ipint);
		} catch (Exception e) {
			result.set(-1);
			log.error("inet_aton final : " + TDBankUtils.getExceptionStack(e));
		}
		return result;
	}

	@Override
	public String getDisplayString(String[] children) {
		return "inet_aton()";
	}
}
