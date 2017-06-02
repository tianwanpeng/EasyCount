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
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.BinaryObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.BytesWritable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import com.tencent.tdbank.mc.sorter.TDBankUtils;

/**
 * GenericUDFArrayContains.
 * 
 */
@Description(name = "hllp_merge", value = "_FUNC_(array, value) - Returns TRUE if the array contains value.", extended = "Example:\n"
		+ "  > SELECT _FUNC_(array(1, 2, 3), 2) FROM src LIMIT 1;\n" + "  true")
public class GenericUDFHLLPMerge extends GenericUDF {
	private static Logger log = LoggerFactory
			.getLogger(GenericUDFHLLPMerge.class);

	private static final String FUNC_NAME = "hllp_merge"; // External Name
	private transient ObjectInspector[] valueOI;

	// private transient BytesWritable result;
	// private transient static BytesWritable nullres = new BytesWritable();

	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException {

		// Check if two arguments were passed
		if (arguments.length <= 0) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " accepts at least 1 arguments.");
		}

		for (int i = 0; i < arguments.length; i++) {
			if (!(arguments[i] instanceof BinaryObjectInspector)) {
				throw new UDFArgumentException("The function " + FUNC_NAME
						+ "'s args must be binary ");
			}
		}

		valueOI = arguments;
		// result = new BytesWritable();
		return PrimitiveObjectInspectorFactory.writableBinaryObjectInspector;

	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {

		if (arguments.length <= 0) {
			return null;
		}

		try {
			HyperLogLogPlus hllp_res = new HyperLogLogPlus(16, 24);

			for (int i = 0; i < arguments.length; i++) {
				byte[] data = (byte[]) ObjectInspectorUtils
						.copyToStandardJavaObject(arguments[i].get(),
								valueOI[i]);
				if (data == null || data.length == 0) {
					continue;
				}
				try {
					HyperLogLogPlus hllp1 = HyperLogLogPlus.Builder.build(data);
					if (hllp_res.sizeof() != hllp1.sizeof()) {
						log.error("sizeof is not equal : " + i + " : "
								+ hllp1.sizeof() + " : " + hllp_res.sizeof()
								+ " : " + data.length);
						continue;
					}
					hllp_res.addAll(hllp1);
				} catch (Exception e) {
					log.error("hllpmerge : " + i + " : " + data.length + " : "
							+ hllp_res.sizeof());
					log.error(TDBankUtils.getExceptionStack(e));
				}
			}
			return new BytesWritable(hllp_res.getBytes());
		} catch (Exception e) {
			log.error("hllpmerge final : " + TDBankUtils.getExceptionStack(e));
		}
		return null;

	}

	@Override
	public String getDisplayString(String[] children) {
		return "hllp_merge()";
	}
}
