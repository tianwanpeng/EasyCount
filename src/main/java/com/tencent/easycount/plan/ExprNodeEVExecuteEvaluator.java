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

import com.tencent.easycount.plan.TypeCheckCtxEC.Var;

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

	public BlockNodeEvaluator getBlockEval(final ExprNodeNewBlockDesc blockDesc)
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

		public AssignNodeEvaluator(final ExprNodeNewAssignDesc blockDesc)
				throws HiveException {
			this.idx = ExprNodeEVExecuteEvaluator.this.var2idx.get(blockDesc
					.getVarStr());
			this.assignEval = ExprNodeEvaluatorFactoryNew.get(blockDesc
					.getAssignDesc());
			final ObjectInspector assignOI = this.assignEval
					.initialize(ExprNodeEVExecuteEvaluator.this.innerExecuteOI);
			this.converter = ObjectInspectorConverters.getConverter(assignOI,
					ExprNodeEVExecuteEvaluator.this.var2OI.get(blockDesc
							.getVarStr()));
		}

		@Override
		public void eval(final Object[] outputCache, final int offset)
				throws HiveException {
			outputCache[this.idx + offset] = this.converter
					.convert(this.assignEval.evaluate(outputCache));
		}
	}

	class ForNodeEvaluator extends BlockNodeEvaluator {

		ArrayList<BlockNodeEvaluator> blockEvals;
		PrimitiveObjectInspector conditionInspector;

		@SuppressWarnings("rawtypes")
		ExprNodeEvaluator condationEval;

		public ForNodeEvaluator(final ExprNodeNewForDesc blockDesc)
				throws HiveException {
			this.condationEval = ExprNodeEvaluatorFactoryNew.get(blockDesc
					.getCondationDesc());
			this.conditionInspector = (PrimitiveObjectInspector) this.condationEval
					.initialize(ExprNodeEVExecuteEvaluator.this.innerExecuteOI);
			this.blockEvals = new ArrayList<ExprNodeEVExecuteEvaluator.BlockNodeEvaluator>();
			for (int i = 0; i < blockDesc.getBlockDescs().size(); i++) {
				this.blockEvals.add(getBlockEval(blockDesc.getBlockDescs().get(
						i)));
			}
		}

		@Override
		public void eval(final Object[] outputCache, final int offset)
				throws HiveException {
			while (true) {
				final Object condition = this.condationEval
						.evaluate(outputCache);
				final Boolean ret = (Boolean) this.conditionInspector
						.getPrimitiveJavaObject(condition);
				if (Boolean.FALSE.equals(ret)) {
					break;
				}
				for (int i = 0; i < this.blockEvals.size(); i++) {
					this.blockEvals.get(i).eval(outputCache, offset);
				}
			}
		}
	}

	class IfNodeEvaluator extends BlockNodeEvaluator {

		ArrayList<BlockNodeEvaluator> blockEvals;
		PrimitiveObjectInspector conditionInspector;

		@SuppressWarnings("rawtypes")
		ExprNodeEvaluator condationEval;

		public IfNodeEvaluator(final ExprNodeNewIfDesc blockDesc)
				throws HiveException {
			this.condationEval = ExprNodeEvaluatorFactoryNew.get(blockDesc
					.getCondationDesc());
			this.conditionInspector = (PrimitiveObjectInspector) this.condationEval
					.initialize(ExprNodeEVExecuteEvaluator.this.innerExecuteOI);
			this.blockEvals = new ArrayList<ExprNodeEVExecuteEvaluator.BlockNodeEvaluator>();
			for (int i = 0; i < blockDesc.getBlockDescs().size(); i++) {
				this.blockEvals.add(getBlockEval(blockDesc.getBlockDescs().get(
						i)));
			}
		}

		@Override
		public void eval(final Object[] outputCache, final int offset)
				throws HiveException {
			final Object condition = this.condationEval.evaluate(outputCache);
			final Boolean ret = (Boolean) this.conditionInspector
					.getPrimitiveJavaObject(condition);
			if (Boolean.FALSE.equals(ret)) {
				return;
			}
			for (int i = 0; i < this.blockEvals.size(); i++) {
				this.blockEvals.get(i).eval(outputCache, offset);
			}
		}
	}

	public ExprNodeEVExecuteEvaluator(final ExprNodeNewExecuteDesc expr)
			throws Exception {
		super(expr);
		this.emitEval = ExprNodeEvaluatorFactoryNew.get(expr.getEmitDesc());
	}

	@Override
	public ObjectInspector initialize(final ObjectInspector rowInspector)
			throws HiveException {
		this.inputOI = (StructObjectInspector) rowInspector;

		final ExprNodeNewDefineDesc defineDesc = this.expr.getDefineDesc();

		final List<? extends StructField> fields = this.inputOI
				.getAllStructFieldRefs();
		final ArrayList<String> structFieldNames = new ArrayList<String>();
		final ArrayList<ObjectInspector> structFieldObjectInspectors = new ArrayList<ObjectInspector>();
		for (final StructField field : fields) {
			structFieldNames.add(field.getFieldName());
			structFieldObjectInspectors.add(field.getFieldObjectInspector());
		}
		this.vars = defineDesc.getVars();
		this.var2idx = new HashMap<String, Integer>();
		this.var2OI = new HashMap<String, ObjectInspector>();

		for (int i = 0; i < this.vars.size(); i++) {
			final Var var = this.vars.get(i);
			this.var2idx.put(var.getVarName(), i);
			structFieldNames.add(var.getVarInternalName());
			final ObjectInspector varOI = TypeInfoUtils
					.getStandardWritableObjectInspectorFromTypeInfo(var
							.getTypeInfo());
			structFieldObjectInspectors.add(varOI);
			this.var2OI.put(var.getVarName(), varOI);
		}

		this.innerExecuteOI = ObjectInspectorFactory
				.getStandardStructObjectInspector(structFieldNames,
						structFieldObjectInspectors);

		this.blockNodeEvals = new ArrayList<ExprNodeEVExecuteEvaluator.BlockNodeEvaluator>();
		final ExprNodeNewExecuteBlockDesc blockDescs = this.expr.getBlockDesc();
		for (int i = 0; i < blockDescs.getBlockDescs().size(); i++) {
			final ExprNodeNewBlockDesc blockDesc = blockDescs.getBlockDescs()
					.get(i);
			if (blockDesc instanceof ExprNodeNewAssignDesc) {
				this.blockNodeEvals.add(new AssignNodeEvaluator(
						(ExprNodeNewAssignDesc) blockDesc));
			} else if (blockDesc instanceof ExprNodeNewForDesc) {
				this.blockNodeEvals.add(new ForNodeEvaluator(
						(ExprNodeNewForDesc) blockDesc));
			} else if (blockDesc instanceof ExprNodeNewIfDesc) {
				this.blockNodeEvals.add(new IfNodeEvaluator(
						(ExprNodeNewIfDesc) blockDesc));
			} else {
			}
		}

		this.emitOI = this.emitEval.initialize(this.innerExecuteOI);
		// return outputOI = ObjectInspectorFactory
		// .getStandardListObjectInspector(emitOI);
		return this.outputOI = this.emitOI;
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
	protected Object evaluate(final Object row, final int version)
			throws HiveException {
		return _evaluate(row, version);
	}

	@Override
	protected Object _evaluate(final Object row, final int version)
			throws HiveException {

		final List<Object> rowlist = this.inputOI
				.getStructFieldsDataAsList(row);
		final Object[] outputCache = new Object[rowlist.size()
				+ this.vars.size()];
		for (int i = 0; i < rowlist.size(); i++) {
			outputCache[i] = rowlist.get(i);
		}
		for (int i = 0; i < this.vars.size(); i++) {
			outputCache[rowlist.size() + i] = this.vars.get(i).getDefaultVal();
		}

		for (int i = 0; i < this.blockNodeEvals.size(); i++) {
			this.blockNodeEvals.get(i).eval(outputCache, rowlist.size());
		}

		return this.emitEval.evaluate(outputCache);
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
	public Integer compare(final Object row) throws HiveException {
		return null;
	}
}
