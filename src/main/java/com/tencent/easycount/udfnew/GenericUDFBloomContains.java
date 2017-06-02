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
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.BinaryObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.IntObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.BooleanWritable;

import com.tencent.easycount.util.bloom.BitSet;
import com.tencent.easycount.util.bloom.BloomFilter;

/**
 * GenericUDFArrayContains.
 *
 */
@Description(name = "bloom_contains", value = "_FUNC_(array, value) - Returns TRUE if the array contains value.", extended = "Example:\n"
		+ "  > SELECT _FUNC_(array(1, 2, 3), 2) FROM src LIMIT 1;\n" + "  true")
public class GenericUDFBloomContains extends GenericUDF {

	private static final int ARRAY_IDX = 0;
	private static final int VALUE_IDX = 1;
	private static final int ARG_COUNT = 2; // Number of arguments to this UDF
	private static final String FUNC_NAME = "BLOOM_CONTAINS"; // External Name
	final static private long n = 10000;
	final static private double p = 0.005;

	private transient IntObjectInspector valueOI;
	// private transient ListObjectInspector arrayOI;
	private transient BinaryObjectInspector binaryOI;
	// private transient ObjectInspector arrayElementOI;
	private BooleanWritable result;

	@Override
	public ObjectInspector initialize(final ObjectInspector[] arguments)
			throws UDFArgumentException {

		// Check if two arguments were passed
		if (arguments.length != ARG_COUNT) {
			throw new UDFArgumentException("The function " + FUNC_NAME
					+ " accepts " + ARG_COUNT + " arguments.");
		}

		// Check if ARRAY_IDX argument is of category LIST
		if (!(arguments[ARRAY_IDX] instanceof BinaryObjectInspector)) {
			throw new UDFArgumentTypeException(ARRAY_IDX, "\"" + "binary"
					+ "\" " + "expected at function BLOOM_CONTAINS, but "
					+ "\"" + arguments[ARRAY_IDX].getTypeName() + "\" "
					+ "is found");
		}

		// Check if ARRAY_IDX argument is of category LIST
		if (!(arguments[VALUE_IDX] instanceof IntObjectInspector)) {
			throw new UDFArgumentTypeException(VALUE_IDX, "\"" + "int" + "\" "
					+ "expected at function BLOOM_CONTAINS, but " + "\""
					+ arguments[VALUE_IDX].getTypeName() + "\" " + "is found");
		}

		this.binaryOI = (BinaryObjectInspector) arguments[ARRAY_IDX];
		// arrayElementOI = arrayOI.getListElementObjectInspector();

		this.valueOI = (IntObjectInspector) arguments[VALUE_IDX];

		this.result = new BooleanWritable(false);

		return PrimitiveObjectInspectorFactory.writableBooleanObjectInspector;
	}

	@Override
	public Object evaluate(final DeferredObject[] arguments)
			throws HiveException {

		this.result.set(false);

		final Object bin = arguments[ARRAY_IDX].get();
		final Object value = arguments[VALUE_IDX].get();

		if ((bin == null) || (value == null)) {
			return this.result;
		}

		final byte[] bindata = this.binaryOI.getPrimitiveJavaObject(bin);
		final BitSet bs = BitSet.valueOf(bindata);
		final BloomFilter<Integer> bf = new BloomFilter<Integer>(bs,
				BloomFilter.optimalM(n, p), BloomFilter.optimalK(n,
						BloomFilter.optimalM(n, p)));
		this.result.set(bf.contains((Integer) (this.valueOI
				.getPrimitiveJavaObject(value))));
		return this.result;
	}

	@Override
	public String getDisplayString(final String[] children) {
		assert (children.length == ARG_COUNT);
		return "array_contains(" + children[ARRAY_IDX] + ", "
		+ children[VALUE_IDX] + ")";
	}
}
