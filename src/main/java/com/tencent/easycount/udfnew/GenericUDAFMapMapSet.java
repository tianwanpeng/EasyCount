package com.tencent.easycount.udfnew;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.AbstractGenericUDAFResolver;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.StandardMapObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;

/**
 * by steventian this class override the GenericUDAFCollectSet class in hive
 * source, because GenericUDAFCollectSet have some bug
 */
@Description(name = "mapmapset", value = "_FUNC_(x) - Returns a set of objects with duplicate elements eliminated")
public class GenericUDAFMapMapSet extends AbstractGenericUDAFResolver {

	static final Log LOG = LogFactory.getLog(GenericUDAFMapMapSet.class
			.getName());

	public GenericUDAFMapMapSet() {
	}

	@Override
	public GenericUDAFEvaluator getEvaluator(TypeInfo[] parameters)
			throws SemanticException {

		if (parameters.length != 2) {
			throw new UDFArgumentTypeException(parameters.length - 1,
					"Exactly 2 argument is expected.");
		}

		// if (parameters[0].getCategory() !=
		// ObjectInspector.Category.PRIMITIVE) {
		// throw new UDFArgumentTypeException(0,
		// "Only primitive type arguments are accepted but "
		// + parameters[0].getTypeName()
		// + " was passed as parameter 1.");
		// }

		return new GenericUDAFMapMapSetEvaluator();
	}

	public static class GenericUDAFMapMapSetEvaluator extends
			GenericUDAFEvaluator {

		// For PARTIAL1 and COMPLETE: ObjectInspectors for original data
		private ObjectInspector inputKeyOI;
		private ObjectInspector inputValueOI;
		// For PARTIAL2 and FINAL: ObjectInspectors for partial aggregations
		// (list of objs)
		// private transient StandardMapObjectInspector moi;

		// private transient StandardListObjectInspector internalMergeOI;
		private transient MapObjectInspector internalMergeOI1;

		@Override
		public ObjectInspector init(Mode m, ObjectInspector[] parameters)
				throws HiveException {
			super.init(m, parameters);
			// init output object inspectors
			// The output of a partial aggregation is a list
			if (m == Mode.PARTIAL1) {
				inputKeyOI = parameters[0];
				inputValueOI = parameters[1];
				return ObjectInspectorFactory
						.getStandardMapObjectInspector(
								ObjectInspectorUtils
										.getStandardObjectInspector(inputKeyOI),
								ObjectInspectorFactory
										.getStandardListObjectInspector(ObjectInspectorUtils
												.getStandardObjectInspector(inputValueOI)));
			} else {
				if (!(parameters[0] instanceof MapObjectInspector)) {
					throw new HiveException(
							"input must be MapObjectInspector but is : "
									+ parameters[0].getClass());
				} else {
					internalMergeOI1 = (MapObjectInspector) parameters[0];

					inputKeyOI = internalMergeOI1.getMapKeyObjectInspector();
					ListObjectInspector mergeValueOI = (ListObjectInspector) internalMergeOI1
							.getMapValueObjectInspector();
					inputValueOI = mergeValueOI.getListElementObjectInspector();

					return (StandardMapObjectInspector) ObjectInspectorUtils
							.getStandardObjectInspector(internalMergeOI1);
				}
			}
		}

		static class MapMapAggregationBuffer extends AbstractAggregationBuffer {
			Map<Object, Set<Object>> container;
		}

		@Override
		public void reset(@SuppressWarnings("deprecation") AggregationBuffer agg)
				throws HiveException {
			((MapMapAggregationBuffer) agg).container = new HashMap<Object, Set<Object>>();
		}

		@SuppressWarnings("deprecation")
		@Override
		public AggregationBuffer getNewAggregationBuffer() throws HiveException {
			MapMapAggregationBuffer ret = new MapMapAggregationBuffer();
			reset(ret);
			return ret;
		}

		// mapside
		@Override
		public void iterate(
				@SuppressWarnings("deprecation") AggregationBuffer agg,
				Object[] parameters) throws HiveException {
			Object p = parameters[0];
			Object v = parameters[1];

			if (p != null) {
				MapMapAggregationBuffer myagg = (MapMapAggregationBuffer) agg;
				putIntoSet(p, v, myagg);
			}
		}

		// mapside
		@Override
		public Object terminatePartial(
				@SuppressWarnings("deprecation") AggregationBuffer agg)
				throws HiveException {
			return terminate(agg);
		}

		@Override
		public void merge(
				@SuppressWarnings("deprecation") AggregationBuffer agg,
				Object partial) throws HiveException {
			MapMapAggregationBuffer myagg = (MapMapAggregationBuffer) agg;
			@SuppressWarnings("unchecked")
			HashMap<Object, Set<Object>> partialResult = (HashMap<Object, Set<Object>>) internalMergeOI1
					.getMap(partial);
			for (Object i : partialResult.keySet()) {
				for (Object j : partialResult.keySet()) {
					putIntoSet(i, j, myagg);
				}
			}
		}

		@Override
		public Object terminate(
				@SuppressWarnings("deprecation") AggregationBuffer agg)
				throws HiveException {
			final MapMapAggregationBuffer myagg = (MapMapAggregationBuffer) agg;
			HashMap<Object, List<Object>> ret = new HashMap<Object, List<Object>>(
					myagg.container.size());
			for (final Object key : myagg.container.keySet()) {
				ret.put(key, new ArrayList<Object>() {
					private static final long serialVersionUID = -7181199072198688078L;
					{
						addAll(myagg.container.get(key));
					}
				});
			}
			return ret;
		}

		private void putIntoSet(Object p, Object v,
				MapMapAggregationBuffer myagg) {
			Object pCopy = ObjectInspectorUtils.copyToStandardObject(p,
					this.inputKeyOI);
			Object vCopy = ObjectInspectorUtils.copyToStandardObject(v,
					this.inputValueOI);
			if (!myagg.container.containsKey(pCopy)) {
				myagg.container.put(pCopy, new HashSet<Object>());
			}
			myagg.container.get(pCopy).add(vCopy);
		}
	}
}
