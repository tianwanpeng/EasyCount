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

package com.tencent.trc.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluator;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorConverters;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorConverters.Converter;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;

import com.tencent.trc.plan.TypeCheckCtxTRC.Var;

/**
 * ExprNodeGenericFuncEvaluator.
 * 
 */
public class ExprNodeEVExecuteEvaluator extends
		ExprNodeEvaluator<ExprNodeNewExecuteDesc> {

	@SuppressWarnings("rawtypes")
	transient ExprNodeEvaluator emitEval;

	transient StructObjectInspector inputOI;

	transient ObjectInspector innerExecuteOI;
	transient ObjectInspector emitOI;
	transient ObjectInspector outputOI;

	ArrayList<Var> vars;
	HashMap<String, Integer> var2idx;
	HashMap<String, ObjectInspector> var2OI;
	ArrayList<BlockNodeEvaluator> blockNodeEvals;

	abstract class BlockNodeEvaluator {
		public abstract void eval(Object[] outputCache, int offset)
				throws HiveException;

	}

	public BlockNodeEvaluator getBlockEval(ExprNodeNewBlockDesc blockDesc)
			throws HiveException {
		if (blockDesc instanceof ExprNodeNewAssignDesc) {
			return new AssignNodeEvaluator((ExprNodeNewAssignDesc) blockDesc);
		} else if (blockDesc instanceof ExprNodeNewIfDesc) {
			return new IfNodeEvaluator((ExprNodeNewIfDesc) blockDesc);
		} else if (blockDesc instanceof ExprNodeNewForDesc) {
			return new ForNodeEvaluator((ExprNodeNewForDesc) blockDesc);
		}
		return null;
	}

	class AssignNodeEvaluator extends BlockNodeEvaluator {
		int idx = 0;
		@SuppressWarnings("rawtypes")
		ExprNodeEvaluator assignEval;
		Converter converter;

		public AssignNodeEvaluator(ExprNodeNewAssignDesc blockDesc)
				throws HiveException {
			this.idx = var2idx.get(blockDesc.getVarStr());
			assignEval = ExprNodeEvaluatorFactoryNew.get(blockDesc
					.getAssignDesc());
			ObjectInspector assignOI = assignEval.initialize(innerExecuteOI);
			converter = ObjectInspectorConverters.getConverter(assignOI,
					var2OI.get(blockDesc.getVarStr()));
		}

		@Override
		public void eval(Object[] outputCache, int offset) throws HiveException {
			outputCache[idx + offset] = converter.convert(assignEval
					.evaluate(outputCache));
		}
	}

	class ForNodeEvaluator extends BlockNodeEvaluator {

		ArrayList<BlockNodeEvaluator> blockEvals;
		PrimitiveObjectInspector conditionInspector;

		@SuppressWarnings("rawtypes")
		ExprNodeEvaluator condationEval;

		public ForNodeEvaluator(ExprNodeNewForDesc blockDesc)
				throws HiveException {
			condationEval = ExprNodeEvaluatorFactoryNew.get(blockDesc
					.getCondationDesc());
			conditionInspector = (PrimitiveObjectInspector) condationEval
					.initialize(innerExecuteOI);
			blockEvals = new ArrayList<ExprNodeEVExecuteEvaluator.BlockNodeEvaluator>();
			for (int i = 0; i < blockDesc.getBlockDescs().size(); i++) {
				blockEvals.add(getBlockEval(blockDesc.getBlockDescs().get(i)));
			}
		}

		@Override
		public void eval(Object[] outputCache, int offset) throws HiveException {
			while (true) {
				Object condition = condationEval.evaluate(outputCache);
				Boolean ret = (Boolean) conditionInspector
						.getPrimitiveJavaObject(condition);
				if (Boolean.FALSE.equals(ret)) {
					break;
				}
				for (int i = 0; i < blockEvals.size(); i++) {
					blockEvals.get(i).eval(outputCache, offset);
				}
			}
		}
	}

	class IfNodeEvaluator extends BlockNodeEvaluator {

		ArrayList<BlockNodeEvaluator> blockEvals;
		PrimitiveObjectInspector conditionInspector;

		@SuppressWarnings("rawtypes")
		ExprNodeEvaluator condationEval;

		public IfNodeEvaluator(ExprNodeNewIfDesc blockDesc)
				throws HiveException {
			condationEval = ExprNodeEvaluatorFactoryNew.get(blockDesc
					.getCondationDesc());
			conditionInspector = (PrimitiveObjectInspector) condationEval
					.initialize(innerExecuteOI);
			blockEvals = new ArrayList<ExprNodeEVExecuteEvaluator.BlockNodeEvaluator>();
			for (int i = 0; i < blockDesc.getBlockDescs().size(); i++) {
				blockEvals.add(getBlockEval(blockDesc.getBlockDescs().get(i)));
			}
		}

		@Override
		public void eval(Object[] outputCache, int offset) throws HiveException {
			Object condition = condationEval.evaluate(outputCache);
			Boolean ret = (Boolean) conditionInspector
					.getPrimitiveJavaObject(condition);
			if (Boolean.FALSE.equals(ret)) {
				return;
			}
			for (int i = 0; i < blockEvals.size(); i++) {
				blockEvals.get(i).eval(outputCache, offset);
			}
		}
	}

	public ExprNodeEVExecuteEvaluator(ExprNodeNewExecuteDesc expr)
			throws Exception {
		super(expr);
		emitEval = ExprNodeEvaluatorFactoryNew.get(expr.getEmitDesc());
	}

	@Override
	public ObjectInspector initialize(ObjectInspector rowInspector)
			throws HiveException {
		inputOI = (StructObjectInspector) rowInspector;

		ExprNodeNewDefineDesc defineDesc = expr.getDefineDesc();

		List<? extends StructField> fields = inputOI.getAllStructFieldRefs();
		ArrayList<String> structFieldNames = new ArrayList<String>();
		ArrayList<ObjectInspector> structFieldObjectInspectors = new ArrayList<ObjectInspector>();
		for (StructField field : fields) {
			structFieldNames.add(field.getFieldName());
			structFieldObjectInspectors.add(field.getFieldObjectInspector());
		}
		vars = defineDesc.getVars();
		var2idx = new HashMap<String, Integer>();
		var2OI = new HashMap<String, ObjectInspector>();

		for (int i = 0; i < vars.size(); i++) {
			Var var = vars.get(i);
			var2idx.put(var.getVarName(), i);
			structFieldNames.add(var.getVarInternalName());
			ObjectInspector varOI = TypeInfoUtils
					.getStandardWritableObjectInspectorFromTypeInfo(var
							.getTypeInfo());
			structFieldObjectInspectors.add(varOI);
			var2OI.put(var.getVarName(), varOI);
		}

		innerExecuteOI = ObjectInspectorFactory
				.getStandardStructObjectInspector(structFieldNames,
						structFieldObjectInspectors);

		blockNodeEvals = new ArrayList<ExprNodeEVExecuteEvaluator.BlockNodeEvaluator>();
		ExprNodeNewExecuteBlockDesc blockDescs = expr.getBlockDesc();
		for (int i = 0; i < blockDescs.getBlockDescs().size(); i++) {
			ExprNodeNewBlockDesc blockDesc = blockDescs.getBlockDescs().get(i);
			if (blockDesc instanceof ExprNodeNewAssignDesc) {
				blockNodeEvals.add(new AssignNodeEvaluator(
						(ExprNodeNewAssignDesc) blockDesc));
			} else if (blockDesc instanceof ExprNodeNewForDesc) {
				blockNodeEvals.add(new ForNodeEvaluator(
						(ExprNodeNewForDesc) blockDesc));
			} else if (blockDesc instanceof ExprNodeNewIfDesc) {
				blockNodeEvals.add(new IfNodeEvaluator(
						(ExprNodeNewIfDesc) blockDesc));
			} else {
			}
		}

		emitOI = emitEval.initialize(innerExecuteOI);
		// return outputOI = ObjectInspectorFactory
		// .getStandardListObjectInspector(emitOI);
		return outputOI = emitOI;
	}

	@Override
	public boolean isDeterministic() {
		return false;
	}

	@Override
	public boolean isStateful() {
		return true;
	}

	@Override
	protected Object evaluate(Object row, int version) throws HiveException {
		return _evaluate(row, version);
	}

	@Override
	protected Object _evaluate(Object row, int version) throws HiveException {

		List<Object> rowlist = inputOI.getStructFieldsDataAsList(row);
		Object[] outputCache = new Object[rowlist.size() + vars.size()];
		for (int i = 0; i < rowlist.size(); i++) {
			outputCache[i] = rowlist.get(i);
		}
		for (int i = 0; i < vars.size(); i++) {
			outputCache[rowlist.size() + i] = vars.get(i).getDefaultVal();
		}

		for (int i = 0; i < blockNodeEvals.size(); i++) {
			blockNodeEvals.get(i).eval(outputCache, rowlist.size());
		}

		return emitEval.evaluate(outputCache);
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
