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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.udf.generic.AbstractGenericUDAFResolver;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;

/**
 * by steventian this class override the GenericUDAFCollectSet class in hive
 * source, because GenericUDAFCollectSet have some bug
 */
@Description(name = "mapmapmerge", value = "_FUNC_(x) - Returns a set of objects with duplicate elements eliminated")
public class GenericUDAFMapMapMerge extends AbstractGenericUDAFResolver {

	static final Log LOG = LogFactory.getLog(GenericUDAFMapMapMerge.class
			.getName());

	public GenericUDAFMapMapMerge() {
	}

	@Override
	public GenericUDAFEvaluator getEvaluator(TypeInfo[] parameters)
			throws SemanticException {

		if (parameters.length != 1) {
			throw new UDFArgumentTypeException(parameters.length - 1,
					"Exactly 1 argument is expected.");
		}

		if (parameters[0].getCategory() != ObjectInspector.Category.MAP) {
			throw new UDFArgumentTypeException(0,
					"Only MAP type arguments are accepted but "
							+ parameters[0].getTypeName()
							+ " was passed as parameter 1.");
		}

		return new GenericUDAFMapMapMergeEvaluator();
	}

	public static class GenericUDAFMapMapMergeEvaluator extends
			GenericUDAFEvaluator {

		private MapObjectInspector inputOI;

		@Override
		public ObjectInspector init(Mode m, ObjectInspector[] parameters)
				throws HiveException {
			super.init(m, parameters);
			inputOI = (MapObjectInspector) parameters[0];
			return ObjectInspectorUtils
					.getStandardObjectInspector(parameters[0]);
		}

		static class MapMapAggregationBuffer extends AbstractAggregationBuffer {
			Map<Object, Object> container;
		}

		@Override
		public void reset(@SuppressWarnings("deprecation") AggregationBuffer agg)
				throws HiveException {
			((MapMapAggregationBuffer) agg).container = new HashMap<Object, Object>();
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
			Map<?, ?> map = (Map<?, ?>) ObjectInspectorUtils
					.copyToStandardObject(parameters[0], inputOI);

			if (map != null) {
				MapMapAggregationBuffer myagg = (MapMapAggregationBuffer) agg;
				for (Object key : map.keySet()) {
					myagg.container.put(key, map.get(key));
				}
			}
		}

		// mapside
		@Override
		public Object terminatePartial(
				@SuppressWarnings("deprecation") AggregationBuffer agg)
				throws HiveException {
			MapMapAggregationBuffer myagg = (MapMapAggregationBuffer) agg;
			HashMap<Object, Object> ret = new HashMap<Object, Object>(
					myagg.container.size());
			ret.putAll(myagg.container);
			return ret;
		}

		@Override
		public void merge(
				@SuppressWarnings("deprecation") AggregationBuffer agg,
				Object partial) throws HiveException {
			Map<?, ?> map = (Map<?, ?>) ObjectInspectorUtils
					.copyToStandardObject(partial, inputOI);
			if (map != null) {
				MapMapAggregationBuffer myagg = (MapMapAggregationBuffer) agg;
				for (Object key : map.keySet()) {
					myagg.container.put(key, map.get(key));
				}
			}
		}

		@Override
		public Object terminate(
				@SuppressWarnings("deprecation") AggregationBuffer agg)
				throws HiveException {
			MapMapAggregationBuffer myagg = (MapMapAggregationBuffer) agg;
			HashMap<Object, Object> ret = new HashMap<Object, Object>(
					myagg.container.size());
			ret.putAll(myagg.container);
			return ret;
		}

	}
}
