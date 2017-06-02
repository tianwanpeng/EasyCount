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

import java.io.IOException;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
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

import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import com.tencent.easycount.util.status.TDBankUtils;

/**
 * This class implements the COUNT aggregation function as in SQL.
 */
@Description(name = "hllp", value = "_FUNC_(*) - Returns the total number of retrieved rows, including "
		+ "rows containing NULL values.\n"

		+ "_FUNC_(expr) - Returns the number of rows for which the supplied "
		+ "expression is non-NULL.\n"

		+ "_FUNC_(DISTINCT expr[, expr...]) - Returns the number of rows for "
		+ "which the supplied expression(s) are unique and non-NULL.")
public class GenericUDAFHLLP implements GenericUDAFResolver2 {
	private static Logger log = LoggerFactory.getLogger(GenericUDAFHLLP.class);

	@Override
	public GenericUDAFEvaluator getEvaluator(TypeInfo[] parameters)
			throws SemanticException {
		// This method implementation is preserved for backward compatibility.
		return new GenericUDAFHLLPEvaluator();
	}

	@SuppressWarnings("deprecation")
	@Override
	public GenericUDAFEvaluator getEvaluator(GenericUDAFParameterInfo paramInfo)
			throws SemanticException {

		TypeInfo[] parameters = paramInfo.getParameters();

		if (parameters.length == 0) {
			if (!paramInfo.isAllColumns()) {
				throw new UDFArgumentException("Argument expected");
			}
			assert !paramInfo.isDistinct() : "DISTINCT not supported with *";
		}
		return new GenericUDAFHLLPEvaluator().setCountAllColumns(paramInfo
				.isAllColumns());
	}

	/**
	 * GenericUDAFCountEvaluator.
	 * 
	 */
	@SuppressWarnings("deprecation")
	public static class GenericUDAFHLLPEvaluator extends GenericUDAFEvaluator {
		private boolean countAllColumns = false;
		private BinaryObjectInspector partialOIMerge = null;
		private ObjectInspector[] paramOI;

		// private BytesWritable result;

		// private static BytesWritable nullres = new BytesWritable();

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

		private GenericUDAFHLLPEvaluator setCountAllColumns(boolean countAllCols) {
			countAllColumns = countAllCols;
			return this;
		}

		/** class for storing count value. */
		@AggregationType(estimable = true)
		static class CountAgg extends AbstractAggregationBuffer {
			HyperLogLogPlus value = null;

			@Override
			public int estimate() {
				return value == null ? 0 : value.sizeof();
			}
		}

		@Override
		public AggregationBuffer getNewAggregationBuffer() throws HiveException {
			CountAgg buffer = new CountAgg();
			reset(buffer);
			return buffer;
		}

		@Override
		public void reset(AggregationBuffer agg) throws HiveException {
			((CountAgg) agg).value = new HyperLogLogPlus(16, 24);
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
					long hashcode = 0;
					for (int i = 0; i < Math.min(this.paramOI.length,
							parameters.length); i++) {
						hashcode = hashcode
								* 31
								+ ((PrimitiveObjectInspector) paramOI[i])
										.getPrimitiveJavaObject(parameters[i])
										.hashCode();
					}
					((CountAgg) agg).value.offer(hashcode);
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
					HyperLogLogPlus chllp = ((CountAgg) agg).value;
					HyperLogLogPlus hllp = HyperLogLogPlus.Builder.build(p);
					if (chllp.sizeof() != hllp.sizeof()) {
						log.error("GenericUDAFEvaluator sizeof is not equal : "
								+ chllp.sizeof() + " : " + hllp.sizeof());
						return;
					}
					chllp.addAll(hllp);
				} catch (Exception e) {
					log.error(TDBankUtils.getExceptionStack(e));
				}
			}
		}

		@Override
		public Object terminate(AggregationBuffer agg) throws HiveException {
			try {
				// byte[] data = ((CountAgg) agg).value.getBytes();
				// result.set(data, 0, data.length);
				// return result;
				return new BytesWritable(((CountAgg) agg).value.getBytes());
			} catch (IOException e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
			return null;
		}

		@Override
		public Object terminatePartial(AggregationBuffer agg)
				throws HiveException {
			try {
				// byte[] data = ((CountAgg) agg).value.getBytes();
				// result.set(data, 0, data.length);
				// return result;
				return new BytesWritable(((CountAgg) agg).value.getBytes());
			} catch (IOException e) {
				log.error(TDBankUtils.getExceptionStack(e));
			}
			return null;
		}
	}
}
