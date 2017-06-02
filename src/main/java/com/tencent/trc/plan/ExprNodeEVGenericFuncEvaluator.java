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

import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluator;
import org.apache.hadoop.hive.ql.exec.ExprNodeGenericFuncEvaluator;
import org.apache.hadoop.hive.ql.exec.FunctionRegistry;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFBaseCompare;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFCase;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFWhen;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;

/**
 * ExprNodeGenericFuncEvaluator.
 * 
 */
public class ExprNodeEVGenericFuncEvaluator extends
		ExprNodeEvaluator<ExprNodeNewGenericFuncDesc> {

	transient GenericUDF genericUDF;
	transient Object rowObject;
	@SuppressWarnings("rawtypes")
	transient ExprNodeEvaluator[] children;
	transient GenericUDF.DeferredObject[] deferredChildren;
	transient boolean isEager;

	/**
	 * Class to allow deferred evaluation for GenericUDF.
	 */
	class DeferredExprObject implements GenericUDF.DeferredObject {

		private final boolean eager;
		@SuppressWarnings("rawtypes")
		private final ExprNodeEvaluator eval;

		private transient boolean evaluated;
		@SuppressWarnings("unused")
		private transient int version;
		private transient Object obj;

		@SuppressWarnings("rawtypes")
		DeferredExprObject(ExprNodeEvaluator eval, boolean eager) {
			this.eval = eval;
			this.eager = eager;
		}

		@Override
		public void prepare(int version) throws HiveException {
			this.version = version;
			this.evaluated = false;
			if (eager) {
				get();
			}
		}

		public Object get() throws HiveException {
			if (!evaluated) {
				obj = eval.evaluate(rowObject);
				evaluated = true;
			}
			return obj;
		}
	}

	@SuppressWarnings("rawtypes")
	public ExprNodeEVGenericFuncEvaluator(ExprNodeNewGenericFuncDesc expr)
			throws Exception {
		super(expr);
		children = new ExprNodeEvaluator[expr.getChildExprs().size()];
		isEager = false;
		for (int i = 0; i < children.length; i++) {
			ExprNodeDesc child = expr.getChildExprs().get(i);
			ExprNodeEvaluator nodeEvaluator = ExprNodeEvaluatorFactoryNew
					.get(child);
			children[i] = nodeEvaluator;
			// If we have eager evaluators anywhere below us, then we are eager
			// too.
			if (nodeEvaluator instanceof ExprNodeEVGenericFuncEvaluator) {
				if (((ExprNodeEVGenericFuncEvaluator) nodeEvaluator).isEager) {
					isEager = true;
				}
				// Base case: we are eager if a child is stateful
				// GenericUDF childUDF;
				//
				// if (child instanceof ExprNodeGenericFuncDesc) {
				//
				// }
				//
				// try {
				// childUDF = ((ExprNodeGenericFuncDescWrapper) child)
				// .getGenericUDFClass().newInstance();
				// if (FunctionRegistry.isStateful(childUDF)) {
				// isEager = true;
				// }
				// } catch (InstantiationException e) {
				// e.printStackTrace();
				// } catch (IllegalAccessException e) {
				// e.printStackTrace();
				// }
			}
		}
		if (expr.getGenericUDFBridge() != null) {
			genericUDF = expr.getGenericUDFBridge();
		} else {
			genericUDF = expr.getGenericUDFClass().newInstance();
		}
		if (isEager
				&& (genericUDF instanceof GenericUDFCase || genericUDF instanceof GenericUDFWhen)) {
			throw new HiveException(
					"Stateful expressions cannot be used inside of CASE");
		}
	}

	@Override
	public ObjectInspector initialize(ObjectInspector rowInspector)
			throws HiveException {
		deferredChildren = new GenericUDF.DeferredObject[children.length];
		for (int i = 0; i < deferredChildren.length; i++) {
			deferredChildren[i] = new DeferredExprObject(children[i], isEager);
		}
		// Initialize all children first
		ObjectInspector[] childrenOIs = new ObjectInspector[children.length];
		for (int i = 0; i < children.length; i++) {
			childrenOIs[i] = children[i].initialize(rowInspector);
		}
		// MapredContext context = MapredContext.get();
		// if (context != null) {
		// context.setup(genericUDF);
		// }
		return outputOI = genericUDF.initializeAndFoldConstants(childrenOIs);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isDeterministic() {
		boolean result = FunctionRegistry.isDeterministic(genericUDF);
		for (ExprNodeEvaluator child : children) {
			result = result && child.isDeterministic();
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ExprNodeEvaluator[] getChildren() {
		return children;
	}

	@Override
	public boolean isStateful() {
		boolean result = FunctionRegistry.isStateful(genericUDF);
		for (@SuppressWarnings("rawtypes")
		ExprNodeEvaluator child : children) {
			if (result = result || child.isStateful()) {
				return result;
			}
		}
		return result;
	}

	@Override
	protected Object _evaluate(Object row, int version) throws HiveException {
		rowObject = row;
		if (ObjectInspectorUtils.isConstantObjectInspector(outputOI)
				&& isDeterministic()) {
			// The output of this UDF is constant, so don't even bother
			// evaluating.
			return ((ConstantObjectInspector) outputOI)
					.getWritableConstantValue();
		}
		for (int i = 0; i < deferredChildren.length; i++) {
			deferredChildren[i].prepare(version);
		}
		return genericUDF.evaluate(deferredChildren);
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
		if (!expr.isSortedExpr()
				|| !(genericUDF instanceof GenericUDFBaseCompare)) {
			for (@SuppressWarnings("rawtypes")
			ExprNodeEvaluator evaluator : children) {
				if (evaluator instanceof ExprNodeGenericFuncEvaluator) {
					Integer comparison = ((ExprNodeGenericFuncEvaluator) evaluator)
							.compare(row);
					if (comparison != null) {
						return comparison;
					}
				}
			}
			return null;
		}

		rowObject = row;
		for (int i = 0; i < deferredChildren.length; i++) {
			deferredChildren[i].prepare(-1);
		}
		return ((GenericUDFBaseCompare) genericUDF).compare(deferredChildren);
	}
}
