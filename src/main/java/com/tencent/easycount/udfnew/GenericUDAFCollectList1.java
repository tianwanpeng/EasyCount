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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.AbstractGenericUDAFResolver;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StandardListObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;

import com.tencent.easycount.udfnew.GenericUDAFCollectList1.GenericUDAFMkCollectionEvaluator.BufferType;

@Description(name = "collect_list", value = "_FUNC_(x) - Returns a list of objects with duplicates")
public class GenericUDAFCollectList1 extends AbstractGenericUDAFResolver {

	static final Log LOG = LogFactory.getLog(GenericUDAFCollectList1.class
			.getName());

	public GenericUDAFCollectList1() {
	}

	@Override
	public GenericUDAFEvaluator getEvaluator(TypeInfo[] parameters)
			throws SemanticException {
		if (parameters.length != 1) {
			throw new UDFArgumentTypeException(parameters.length - 1,
					"Exactly one argument is expected.");
		}
		return new GenericUDAFMkCollectionEvaluator(BufferType.LIST);
	}

	public static class GenericUDAFMkCollectionEvaluator extends
			GenericUDAFEvaluator implements Serializable {

		private static final long serialVersionUID = 1l;

		enum BufferType {
			SET, LIST
		}

		// For PARTIAL1 and COMPLETE: ObjectInspectors for original data
		private transient ObjectInspector inputOI;
		// For PARTIAL2 and FINAL: ObjectInspectors for partial aggregations
		// (list of objs)
		private transient StandardListObjectInspector loi;

		// private transient StandardListObjectInspector internalMergeOI;
		private transient ListObjectInspector internalMergeOI1;

		private BufferType bufferType;

		// needed by kyro
		public GenericUDAFMkCollectionEvaluator() {
		}

		public GenericUDAFMkCollectionEvaluator(BufferType bufferType) {
			this.bufferType = bufferType;
		}

		@Override
		public ObjectInspector init(Mode m, ObjectInspector[] parameters)
				throws HiveException {

			super.init(m, parameters);
			// init output object inspectors
			// The output of a partial aggregation is a list
			if (m == Mode.PARTIAL1) {
				inputOI = parameters[0];
				return ObjectInspectorFactory
						.getStandardListObjectInspector(ObjectInspectorUtils
								.getStandardObjectInspector(inputOI));
			} else {
				if (!(parameters[0] instanceof ListObjectInspector)) {
					// no map aggregation.
					inputOI = (PrimitiveObjectInspector) ObjectInspectorUtils
							.getStandardObjectInspector(parameters[0]);
					return (StandardListObjectInspector) ObjectInspectorFactory
							.getStandardListObjectInspector(inputOI);
				} else {
					internalMergeOI1 = (ListObjectInspector) parameters[0];
					inputOI = internalMergeOI1.getListElementObjectInspector();
					loi = (StandardListObjectInspector) ObjectInspectorUtils
							.getStandardObjectInspector(internalMergeOI1);
					return loi;
				}
			}

		}

		class MkArrayAggregationBuffer extends AbstractAggregationBuffer {

			private Collection<Object> container;

			public MkArrayAggregationBuffer() {
				if (bufferType == BufferType.LIST) {
					container = new ArrayList<Object>();
				} else if (bufferType == BufferType.SET) {
					container = new HashSet<Object>();
				} else {
					throw new RuntimeException("Buffer type unknown");
				}
			}
		}

		@SuppressWarnings("deprecation")
		@Override
		public void reset(AggregationBuffer agg) throws HiveException {
			((MkArrayAggregationBuffer) agg).container.clear();
		}

		@SuppressWarnings("deprecation")
		@Override
		public AggregationBuffer getNewAggregationBuffer() throws HiveException {
			MkArrayAggregationBuffer ret = new MkArrayAggregationBuffer();
			return ret;
		}

		// mapside
		@SuppressWarnings("deprecation")
		@Override
		public void iterate(AggregationBuffer agg, Object[] parameters)
				throws HiveException {
			assert (parameters.length == 1);
			Object p = parameters[0];

			if (p != null) {
				MkArrayAggregationBuffer myagg = (MkArrayAggregationBuffer) agg;
				putIntoCollection(p, myagg);
			}
		}

		// mapside
		@SuppressWarnings("deprecation")
		@Override
		public Object terminatePartial(AggregationBuffer agg)
				throws HiveException {
			MkArrayAggregationBuffer myagg = (MkArrayAggregationBuffer) agg;
			List<Object> ret = new ArrayList<Object>(myagg.container.size());
			ret.addAll(myagg.container);
			return ret;
		}

		@SuppressWarnings({ "deprecation", "unchecked" })
		@Override
		public void merge(AggregationBuffer agg, Object partial)
				throws HiveException {
			MkArrayAggregationBuffer myagg = (MkArrayAggregationBuffer) agg;
			List<Object> partialResult = (ArrayList<Object>) internalMergeOI1
					.getList(partial);
			for (Object i : partialResult) {
				putIntoCollection(i, myagg);
			}
		}

		@SuppressWarnings("deprecation")
		@Override
		public Object terminate(AggregationBuffer agg) throws HiveException {
			MkArrayAggregationBuffer myagg = (MkArrayAggregationBuffer) agg;
			List<Object> ret = new ArrayList<Object>(myagg.container.size());
			ret.addAll(myagg.container);
			return ret;
		}

		private void putIntoCollection(Object p, MkArrayAggregationBuffer myagg) {
			Object pCopy = ObjectInspectorUtils.copyToStandardObject(p,
					this.inputOI);
			myagg.container.add(pCopy);
		}

		public BufferType getBufferType() {
			return bufferType;
		}

		public void setBufferType(BufferType bufferType) {
			this.bufferType = bufferType;
		}
	}
}