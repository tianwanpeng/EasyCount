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
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFParameterInfo;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFResolver2;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyPrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.BinaryObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;

import com.tencent.easycount.util.bloom.BitSet;
import com.tencent.easycount.util.bloom.BloomFilter;

/**
 * This class implements the COUNT aggregation function as in SQL.
 */
@Description(name = "countd", value = "_FUNC_(*) - Returns the total number of retrieved rows, including "
		+ "rows containing NULL values.\n"

		+ "_FUNC_(expr) - Returns the number of rows for which the supplied "
		+ "expression is non-NULL.\n"

		+ "_FUNC_(DISTINCT expr[, expr...]) - Returns the number of rows for "
		+ "which the supplied expression(s) are unique and non-NULL.")
public class GenericUDAFCountDistinct implements GenericUDAFResolver2 {

	@Override
	public GenericUDAFEvaluator getEvaluator(final TypeInfo[] parameters)
			throws SemanticException {
		// This method implementation is preserved for backward compatibility.
		return new GenericUDAFCountDistinctEvaluator();
	}

	@SuppressWarnings("deprecation")
	@Override
	public GenericUDAFEvaluator getEvaluator(
			final GenericUDAFParameterInfo paramInfo) throws SemanticException {

		final TypeInfo[] parameters = paramInfo.getParameters();

		if (parameters.length == 0) {
			if (!paramInfo.isAllColumns()) {
				throw new UDFArgumentException("Argument expected");
			}
			assert !paramInfo.isDistinct() : "DISTINCT not supported with *";
		}
		return new GenericUDAFCountDistinctEvaluator()
		.setCountAllColumns(paramInfo.isAllColumns());
	}

	/**
	 * GenericUDAFCountEvaluator.
	 *
	 */
	@SuppressWarnings("deprecation")
	public static class GenericUDAFCountDistinctEvaluator extends
	GenericUDAFEvaluator {
		private boolean countAllColumns = false;
		private BinaryObjectInspector partialOI;
		private BinaryObjectInspector partialOIMerge;
		// private LongObjectInspector resultOI;
		private LongWritable result;
		private ObjectInspector[] paramOI;

		@Override
		public ObjectInspector init(final Mode m,
				final ObjectInspector[] parameters) throws HiveException {
			super.init(m, parameters);
			this.paramOI = parameters;
			this.partialOI = PrimitiveObjectInspectorFactory.writableBinaryObjectInspector;
			if ((m == Mode.PARTIAL1) || (m == Mode.COMPLETE)) {
				this.partialOIMerge = LazyPrimitiveObjectInspectorFactory.LAZY_BINARY_OBJECT_INSPECTOR;
			} else {
				this.partialOIMerge = (BinaryObjectInspector) parameters[0];
			}
			// resultOI =
			// PrimitiveObjectInspectorFactory.writableLongObjectInspector;
			this.result = new LongWritable(0);
			if ((m == Mode.PARTIAL1) || (m == Mode.PARTIAL2)) {
				return this.partialOI;
			} else {
				return PrimitiveObjectInspectorFactory.writableLongObjectInspector;
			}

		}

		private GenericUDAFCountDistinctEvaluator setCountAllColumns(
				final boolean countAllCols) {
			this.countAllColumns = countAllCols;
			return this;
		}

		/** class for storing count value. */
		@AggregationType(estimable = true)
		static class CountAgg extends AbstractAggregationBuffer {
			BloomFilter<String> value = null;

			@Override
			public int estimate() {
				return this.value == null ? 0 : this.value.size() / 8;
			}
		}

		@Override
		public AggregationBuffer getNewAggregationBuffer() throws HiveException {
			final CountAgg buffer = new CountAgg();
			reset(buffer);
			return buffer;
		}

		@Override
		public void reset(final AggregationBuffer agg) throws HiveException {
			((CountAgg) agg).value = new BloomFilter<String>(100000, 0.001);
		}

		@Override
		public void iterate(final AggregationBuffer agg,
				final Object[] parameters) throws HiveException {
			// parameters == null means the input table/split is empty
			if (parameters == null) {
				return;
			}
			if (this.countAllColumns) {
				assert parameters.length == 0;
			} else {
				assert parameters.length > 0;
				boolean countThisRow = true;
				for (final Object nextParam : parameters) {
					if (nextParam == null) {
						countThisRow = false;
						break;
					}
				}
				if (countThisRow) {
					long hashcode = 0;
					for (int i = 0; i < Math.min(this.paramOI.length,
							parameters.length); i++) {
						hashcode = (hashcode * 31)
								+ ((PrimitiveObjectInspector) this.paramOI[i])
								.getPrimitiveJavaObject(parameters[i])
								.hashCode();
					}
					((CountAgg) agg).value.add(longToByteArray(hashcode));
				}
			}
		}

		public static byte[] longToByteArray(final long dd) {
			return new byte[] { (byte) ((dd >> 56) & 0xFF),
					(byte) ((dd >> 48) & 0xFF), (byte) ((dd >> 40) & 0xFF),
					(byte) ((dd >> 32) & 0xFF), (byte) ((dd >> 24) & 0xFF),
					(byte) ((dd >> 16) & 0xFF), (byte) ((dd >> 8) & 0xFF),
					(byte) (dd & 0xFF) };
		}

		@Override
		public void merge(final AggregationBuffer agg, final Object partial)
				throws HiveException {
			if (partial != null) {
				final byte[] p = this.partialOIMerge
						.getPrimitiveJavaObject(partial);
				final BitSet bs = BitSet.valueOf(p);
				final BloomFilter<String> currBF = ((CountAgg) agg).value;
				final BloomFilter<String> bf = new BloomFilter<String>(bs,
						currBF.getM(), currBF.getK(), currBF.getHashMethod(),
						currBF.getCryptographicHashFunctionName());
				currBF.union(bf);
			}
		}

		@Override
		public Object terminate(final AggregationBuffer agg)
				throws HiveException {
			this.result.set(((CountAgg) agg).value.valueSize());
			return this.result;
		}

		@Override
		public Object terminatePartial(final AggregationBuffer agg)
				throws HiveException {
			return new BytesWritable(((CountAgg) agg).value.getBitSet()
					.toByteArray());
		}
	}
}
