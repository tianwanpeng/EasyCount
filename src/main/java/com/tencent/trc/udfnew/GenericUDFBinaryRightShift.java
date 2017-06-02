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

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.BinaryObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.IntObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.BytesWritable;

/**
 * deterministic version of UDFUnixTimeStamp. enforces argument
 */
@Description(name = "bit_rsft", value = "_FUNC_(date[, pattern]) - Returns the UNIX timestamp", extended = "Converts the specified time to number of seconds since 1970-01-01.")
public class GenericUDFBinaryRightShift extends GenericUDF {
	static final Log LOG = LogFactory.getLog(GenericUDAFMapMap.class.getName());

	private transient BinaryObjectInspector inputOI;
	private transient IntObjectInspector sftlenOI;

	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException {
		if (arguments.length < 2) {
			throw new UDFArgumentLengthException("The function bit_rsft "
					+ "requires at least 2 argument");
		}
		if (!(arguments[0] instanceof BinaryObjectInspector)) {
			throw new UDFArgumentException("The function "
					+ getName().toUpperCase() + " takes only binary types");
		}
		inputOI = (BinaryObjectInspector) arguments[0];

		if (!(arguments[1] instanceof IntObjectInspector)) {
			throw new UDFArgumentException("The function "
					+ getName().toUpperCase() + " takes only int types");
		}
		sftlenOI = (IntObjectInspector) arguments[1];

		return PrimitiveObjectInspectorFactory.writableBinaryObjectInspector;
	}

	protected String getName() {
		return "bit_rsft";
	}

	protected transient final BytesWritable retValue = new BytesWritable();

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {
		BytesWritable retValue1 = (BytesWritable) ObjectInspectorUtils
				.copyToStandardObject(arguments[0].get(), inputOI);
		int sftlen = sftlenOI.get(arguments[1].get());
		byte[] data = retValue1.getBytes();
		byte[] datares = new byte[data.length];
		Arrays.fill(datares, (byte) 0);
		if (sftlen >= 0 && sftlen < data.length * 8) {
			int bytenum = sftlen / 8;
			int bitnum = sftlen % 8;

			int tmp = 0;
			int tmp1 = 0;
			for (int i = 0; i < data.length - bytenum; i++) {
				tmp = data[i] & 0x0ff;
				data[i] = (byte) ((tmp << bitnum) | tmp1);
				tmp1 = tmp >> (8 - bitnum);
			}
			System.arraycopy(data, 0, datares, bytenum, data.length - bytenum);
		}
		retValue.set(datares, 0, datares.length);
		return retValue;
	}

	@Override
	public String getDisplayString(String[] children) {
		StringBuilder sb = new StringBuilder(32);
		sb.append(getName());
		sb.append('(');
		sb.append(StringUtils.join(children, ','));
		sb.append(')');
		return sb.toString();
	}
}
