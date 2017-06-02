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
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFParameterInfo;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFResolver2;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.BinaryObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.io.BytesWritable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.tdbank.mc.sorter.TDBankUtils;
import com.tencent.trc.util.bloom.BitSet;
import com.tencent.trc.util.bloom.BloomFilter;

/**
 * This class implements the COUNT aggregation function as in SQL.
 */
@Description(name = "bloom", value = "_FUNC_(*) - Returns the total number of retrieved rows, including "
		+ "rows containing NULL values.\n"

		+ "_FUNC_(expr) - Returns the number of rows for which the supplied "
		+ "expression is non-NULL.\n"

		+ "_FUNC_(DISTINCT expr[, expr...]) - Returns the number of rows for "
		+ "which the supplied expression(s) are unique and non-NULL.")
public class GenericUDAFBloom implements GenericUDAFResolver2 {
	private static Logger log = LoggerFactory.getLogger(GenericUDAFBloom.class);
	final static private long n = 10000;
	final static private double p = 0.005;

	@Override
	public GenericUDAFEvaluator getEvaluator(TypeInfo[] parameters)
			throws SemanticException {
		// This method implementation is preserved for backward compatibility.
		return new GenericUDAFBloomEvaluator();
	}

	@Override
	public GenericUDAFEvaluator getEvaluator(GenericUDAFParameterInfo paramInfo)
			throws SemanticException {
		return new GenericUDAFBloomEvaluator();
	}

	/**
	 * GenericUDAFCountEvaluator.
	 * 
	 */
	@SuppressWarnings("deprecation")
	public static class GenericUDAFBloomEvaluator extends GenericUDAFEvaluator {
		private boolean countAllColumns = false;
		private BinaryObjectInspector partialOIMerge = null;
		private ObjectInspector[] paramOI;

		@Override
		public ObjectInspector init(Mode m, ObjectInspector[] parameters)
				throws HiveException {
			super.init(m, parameters);
			// result = new BytesWritable();
			paramOI = parameters;
			if (m == Mode.PARTIAL1 || m == Mode.COMPLETE) {
			} else {
				partialOIMerge = (BinaryObjectInspector) parameters[0];
			}
			return PrimitiveObjectInspectorFactory.writableBinaryObjectInspector;
		}

		/** class for storing count value. */
		static class BloomAgg extends AbstractAggregationBuffer {
			BloomFilter<Integer> value = null;

			@Override
			public int estimate() {
				return value == null ? 0 : value.size();
			}
		}

		@Override
		public AggregationBuffer getNewAggregationBuffer() throws HiveException {
			BloomAgg buffer = new BloomAgg();
			reset(buffer);
			return buffer;
		}

		@Override
		public void reset(AggregationBuffer agg) throws HiveException {
			((BloomAgg) agg).value = new BloomFilter<Integer>(n, p);
		}

		@Override
		public void iterate(AggregationBuffer agg, Object[] parameters)
				throws HiveException {
			// parameters == null means the input table/split is empty
			if (parameters == null) {
				return;
			}
			if (countAllColumns) {
				assert parameters.length == 0;
			} else {
				assert parameters.length > 0;
				boolean countThisRow = true;
				for (Object nextParam : parameters) {
					if (nextParam == null) {
						countThisRow = false;
						break;
					}
				}
				if (countThisRow) {
					int hashcode = ((PrimitiveObjectInspector) paramOI[0])
							.getPrimitiveJavaObject(parameters[0]).hashCode();
					((BloomAgg) agg).value.add(hashcode);
				}
			}
		}

		@Override
		public void merge(AggregationBuffer agg, Object partial)
				throws HiveException {
			if (partial != null) {
				try {
					byte[] p = partialOIMerge.getPrimitiveJavaObject(partial);
					if (p == null || p.length == 0) {
						log.warn("partial is null");
						return;
					}
					BitSet bs = BitSet.valueOf(p);
					BloomFilter<Integer> currBF = ((BloomAgg) agg).value;
					BloomFilter<Integer> bf = new BloomFilter<Integer>(bs,
							currBF.getM(), currBF.getK(),
							currBF.getHashMethod(),
							currBF.getCryptographicHashFunctionName());
					currBF.union(bf);
				} catch (Exception e) {
					log.error(TDBankUtils.getExceptionStack(e));
				}
			}
		}

		@Override
		public Object terminate(AggregationBuffer agg) throws HiveException {
			return terminatePartial(agg);
		}

		@Override
		public Object terminatePartial(AggregationBuffer agg)
				throws HiveException {
			return new BytesWritable(((BloomAgg) agg).value.getBitSet()
					.toByteArray());
		}
	}
}
