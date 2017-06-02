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

package com.tencent.easycount.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluator;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.IntWritable;

import com.tencent.easycount.plan.ExprNodeNewColumnRefDesc.RefMode;

/**
 * ExprNodeGenericFuncEvaluator.
 * 
 */
public class ExprNodeEVForeachEvaluator extends
		ExprNodeEvaluator<ExprNodeNewForeachDesc> {

	@SuppressWarnings("rawtypes")
	transient ExprNodeEvaluator fromListEval;
	@SuppressWarnings("rawtypes")
	transient ExprNodeEvaluator generateEval;
	@SuppressWarnings("rawtypes")
	transient ExprNodeEvaluator generateEval1 = null;

	transient StructObjectInspector inputOI;

	transient ObjectInspector fromElementOI;
	transient ObjectInspector fromListOI;
	transient ObjectInspector generateExprOI;
	transient ObjectInspector generateExprOI1 = null;

	RefMode fmode;
	boolean generateMap = false;

	public ExprNodeEVForeachEvaluator(ExprNodeNewForeachDesc expr)
			throws Exception {
		super(expr);
		fromListEval = ExprNodeEvaluatorFactoryNew
				.get(((ExprNodeNewForeachVarDesc) expr.getFromListDesc())
						.getListDesc());
		generateMap = expr.getGenerateDesc().isGenerateMap();
		generateEval = ExprNodeEvaluatorFactoryNew.get(expr.getGenerateDesc()
				.getGenerateDesc());
		if (generateMap) {
			generateEval = ExprNodeEvaluatorFactoryNew.get(expr
					.getGenerateDesc().getGenerateDesc1());
		}
	}

	@Override
	public ObjectInspector initialize(ObjectInspector rowInspector)
			throws HiveException {
		inputOI = (StructObjectInspector) rowInspector;
		fromListOI = fromListEval.initialize(inputOI);
		fmode = expr.getFmode();
		List<? extends StructField> fields = inputOI.getAllStructFieldRefs();
		ArrayList<String> structFieldNames = new ArrayList<String>();
		ArrayList<ObjectInspector> structFieldObjectInspectors = new ArrayList<ObjectInspector>();
		for (StructField field : fields) {
			structFieldNames.add(field.getFieldName());
			structFieldObjectInspectors.add(field.getFieldObjectInspector());
		}
		structFieldNames.add(((ExprNodeNewForeachVarDesc) expr
				.getFromListDesc()).getVar().getVarInternalName());

		if (fmode == RefMode.oflist) {
			structFieldObjectInspectors
					.add(PrimitiveObjectInspectorFactory.writableIntObjectInspector);
		} else if (fmode == RefMode.inlist) {
			structFieldObjectInspectors
					.add(ObjectInspectorUtils
							.getStandardObjectInspector(((ListObjectInspector) fromListOI)
									.getListElementObjectInspector()));
		} else if (fmode == RefMode.ofmap) {
			structFieldObjectInspectors
					.add(ObjectInspectorUtils
							.getStandardObjectInspector(((MapObjectInspector) fromListOI)
									.getMapKeyObjectInspector()));
		} else if (fmode == RefMode.inmap) {
			structFieldObjectInspectors
					.add(ObjectInspectorUtils
							.getStandardObjectInspector(((MapObjectInspector) fromListOI)
									.getMapValueObjectInspector()));
		}

		ObjectInspector oiForGenerate = ObjectInspectorFactory
				.getStandardStructObjectInspector(structFieldNames,
						structFieldObjectInspectors);
		generateExprOI = generateEval.initialize(oiForGenerate);
		if (generateMap) {
			generateExprOI1 = generateEval1.initialize(oiForGenerate);
		}
		// return outputOI = ObjectInspectorFactory
		// .getStandardListObjectInspector(ObjectInspectorUtils
		// .getStandardObjectInspector(generateExprOI));
		if (generateMap) {
			return outputOI = ObjectInspectorFactory
					.getStandardMapObjectInspector(generateExprOI,
							generateExprOI1);
		}
		return outputOI = ObjectInspectorFactory
				.getStandardListObjectInspector(generateExprOI);
	}

	@Override
	public boolean isDeterministic() {
		return false;
	}

	@Override
	public boolean isStateful() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object _evaluate(Object row, int version) throws HiveException {
		Object list = fromListEval.evaluate(row);
		List<Object> rowlist1 = inputOI.getStructFieldsDataAsList(row);
		ArrayList<Object> rowlist = new ArrayList<Object>();
		rowlist.addAll(rowlist1);
		rowlist.add(null);
		if (generateMap) {
			HashMap<Object, Object> outputObj = new HashMap<Object, Object>();
			if (fmode == RefMode.oflist) {
				int objnum = ((ListObjectInspector) fromListOI)
						.getListLength(list);
				IntWritable iw = new IntWritable();
				for (int i = 0; i < objnum; i++) {
					iw.set(i);
					rowlist.set(rowlist.size() - 1, iw);
					Object sobj = ObjectInspectorUtils.copyToStandardObject(
							generateEval.evaluate(rowlist), generateExprOI);
					Object sobj1 = ObjectInspectorUtils.copyToStandardObject(
							generateEval1.evaluate(rowlist), generateExprOI1);
					outputObj.put(sobj, sobj1);
				}
			} else if (fmode == RefMode.inlist) {
				List<Object> standardList = (List<Object>) ObjectInspectorUtils
						.copyToStandardObject(list, fromListOI);
				for (int i = 0; i < standardList.size(); i++) {
					rowlist.set(rowlist.size() - 1, standardList.get(i));
					Object sobj = ObjectInspectorUtils.copyToStandardObject(
							generateEval.evaluate(rowlist), generateExprOI);
					Object sobj1 = ObjectInspectorUtils.copyToStandardObject(
							generateEval1.evaluate(rowlist), generateExprOI1);
					outputObj.put(sobj, sobj1);
				}
			} else if (fmode == RefMode.ofmap) {
				Map<Object, Object> standardList = (Map<Object, Object>) ObjectInspectorUtils
						.copyToStandardObject(list, fromListOI);
				for (Object key : standardList.keySet()) {
					rowlist.set(rowlist.size() - 1, key);
					Object sobj = ObjectInspectorUtils.copyToStandardObject(
							generateEval.evaluate(rowlist), generateExprOI);
					Object sobj1 = ObjectInspectorUtils.copyToStandardObject(
							generateEval1.evaluate(rowlist), generateExprOI1);
					outputObj.put(sobj, sobj1);
				}
			} else if (fmode == RefMode.inmap) {
				Map<Object, Object> standardList = (Map<Object, Object>) ObjectInspectorUtils
						.copyToStandardObject(list, fromListOI);
				for (Object key : standardList.values()) {
					rowlist.set(rowlist.size() - 1, key);
					Object sobj = ObjectInspectorUtils.copyToStandardObject(
							generateEval.evaluate(rowlist), generateExprOI);
					Object sobj1 = ObjectInspectorUtils.copyToStandardObject(
							generateEval1.evaluate(rowlist), generateExprOI1);
					outputObj.put(sobj, sobj1);
				}
			}
			return outputObj;
		} else {
			ArrayList<Object> outputObj = new ArrayList<Object>();
			if (fmode == RefMode.oflist) {
				int objnum = ((ListObjectInspector) fromListOI)
						.getListLength(list);
				IntWritable iw = new IntWritable();
				for (int i = 0; i < objnum; i++) {
					iw.set(i);
					rowlist.set(rowlist.size() - 1, iw);
					Object sobj = ObjectInspectorUtils.copyToStandardObject(
							generateEval.evaluate(rowlist), generateExprOI);
					outputObj.add(sobj);
				}
			} else if (fmode == RefMode.inlist) {
				List<Object> standardList = (List<Object>) ObjectInspectorUtils
						.copyToStandardObject(list, fromListOI);
				for (int i = 0; i < standardList.size(); i++) {
					rowlist.set(rowlist.size() - 1, standardList.get(i));
					Object sobj = ObjectInspectorUtils.copyToStandardObject(
							generateEval.evaluate(rowlist), generateExprOI);
					outputObj.add(sobj);
				}
			} else if (fmode == RefMode.ofmap) {
				Map<Object, Object> standardList = (Map<Object, Object>) ObjectInspectorUtils
						.copyToStandardObject(list, fromListOI);
				for (Object key : standardList.keySet()) {
					rowlist.set(rowlist.size() - 1, key);
					Object sobj = ObjectInspectorUtils.copyToStandardObject(
							generateEval.evaluate(rowlist), generateExprOI);
					outputObj.add(sobj);
				}
			} else if (fmode == RefMode.inmap) {
				Map<Object, Object> standardList = (Map<Object, Object>) ObjectInspectorUtils
						.copyToStandardObject(list, fromListOI);
				for (Object key : standardList.values()) {
					rowlist.set(rowlist.size() - 1, key);
					Object sobj = ObjectInspectorUtils.copyToStandardObject(
							generateEval.evaluate(rowlist), generateExprOI);
					outputObj.add(sobj);
				}
			}
			return outputObj;
		}
	}

	/**
	 * If the genericUDF is a base comparison, it returns an integer based on
	 * the result of comparing the two sides of the UDF, like the compareTo
	 * method in Comparable.
	 * 
	 * If the genericUDF is not a base comparison, or there is an error
	 * executing the comparison, it returns null.
	 * 
	 * @param row
	 * @return the compare results
	 * @throws HiveException
	 */
	public Integer compare(Object row) throws HiveException {
		return null;
	}
}
