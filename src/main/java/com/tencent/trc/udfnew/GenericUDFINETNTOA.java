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

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.LongObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.tdbank.mc.sorter.TDBankUtils;

/**
 * GenericUDFArrayContains.
 * 
 */
@Description(name = "inet_ntoa", value = "_FUNC_(string) - Returns string ", extended = "Example:\n"
		+ "  > SELECT _FUNC_(257) FROM src LIMIT 1;\n" + "  0.0.1.1")
public class GenericUDFINETNTOA extends GenericUDF {
	private static Logger log = LoggerFactory
			.getLogger(GenericUDFINETNTOA.class);

	private static final String FUNC_NAME = "inet_ntoa"; // External Name

	// private transient LongWritable result;
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
			if (!(arguments[i] instanceof LongObjectInspector)) {
				throw new UDFArgumentException("The function " + FUNC_NAME
						+ "'s args must be bigint ");
			}
		}

		// result = new BytesWritable();
		return PrimitiveObjectInspectorFactory.writableStringObjectInspector;

	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {

		if (arguments.length <= 0) {
			return null;
		}

		try {
			long ipint = Long.parseLong(arguments[0].get().toString());
			StringBuffer sb = new StringBuffer();

			sb.append((ipint & 0xff000000) >> 24)
					.append((ipint & 0xff0000) >> 16)
					.append((ipint & 0xff00) >> 8).append(ipint & 0xff);

			return new Text(sb.toString());
		} catch (Exception e) {
			log.error("inet_ntoa final : " + TDBankUtils.getExceptionStack(e));
		}
		return null;
	}

	@Override
	public String getDisplayString(String[] children) {
		return "inet_ntoa()";
	}
}
