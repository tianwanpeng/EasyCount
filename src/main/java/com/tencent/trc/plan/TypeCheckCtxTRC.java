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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.hive.ql.plan.ExprNodeConstantDesc;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;

import com.tencent.trc.parse.ASTNodeTRC;
import com.tencent.trc.util.graph.RuleDispatcher.NodeProcessorCtx;

/**
 * This class implements the context information that is used for typechecking
 * phase in query compilation.
 */
public class TypeCheckCtxTRC implements NodeProcessorCtx {

	public static class Var implements Serializable {
		private static final long serialVersionUID = -2059121710700943209L;
		final private String varName;
		final private String varInternalName;
		final private TypeInfo typeInfo;
		final private ExprNodeConstantDesc defaultExpr;

		// final Object defaultVal;

		public Var(String varName, String varInternalName, TypeInfo typeInfo,
				ExprNodeConstantDesc defaultExpr) {
			this.varName = varName;
			this.varInternalName = varInternalName;
			this.typeInfo = typeInfo;
			this.defaultExpr = defaultExpr;
			// defaultExpr.getValue();
		}

		public String getVarName() {
			return varName;
		}

		public String getVarInternalName() {
			return varInternalName;
		}

		public TypeInfo getTypeInfo() {
			return typeInfo;
		}

		@Override
		public int hashCode() {
			int hashcode = varInternalName.hashCode();
			hashcode = hashcode * 31 + varName.hashCode();
			hashcode = hashcode * 31 + typeInfo.hashCode();
			hashcode = hashcode * 31 + defaultExpr.hashCode();
			return hashcode;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Var)) {
				return false;
			}
			Var v = (Var) obj;
			return this.varName.equals(v.varName)
					&& this.varInternalName.equals(v.varInternalName)
					&& this.typeInfo.equals(v.typeInfo)
					&& ((this.defaultExpr == null && v.defaultExpr == null) || this.defaultExpr
							.equals(v.defaultExpr));
		}

		public Object getDefaultVal() {
			return defaultExpr == null ? null : ObjectInspectorUtils
					.getWritableConstantValue(defaultExpr
							.getWritableObjectInspector());
		}

	}

	/**
	 * The row resolver of the previous operator. This field is used to generate
	 * expression descriptors from the expression ASTs.
	 */
	private RowResolverTRC inputRR;

	private HashMap<ASTNodeTRC, HashMap<String, Var>> astNode2VarInfos;
	private HashMap<ASTNodeTRC, Integer> astNode2LambdaIdx;
	private ArrayList<ASTNodeTRC> lambdaExprs;

	/**
	 * Potential typecheck error reason.
	 */
	private String error;

	/**
	 * The node that generated the potential typecheck error
	 */
	private ASTNodeTRC errorSrcNode;

	/**
	 * Whether to allow stateful UDF invocations.
	 */
	private boolean allowStatefulFunctions;

	/**
	 * Constructor.
	 * 
	 * @param inputRR
	 *            The input row resolver of the previous operator.
	 */
	public TypeCheckCtxTRC(RowResolverTRC inputRR) {
		setInputRR(inputRR);
		error = null;
		allowStatefulFunctions = false;
		this.astNode2VarInfos = new HashMap<ASTNodeTRC, HashMap<String, Var>>();
		this.astNode2LambdaIdx = new HashMap<ASTNodeTRC, Integer>();
		this.lambdaExprs = new ArrayList<ASTNodeTRC>();
	}

	/**
	 * @param inputRR
	 *            the inputRR to set
	 */
	public void setInputRR(RowResolverTRC inputRR) {
		this.inputRR = inputRR;
	}

	/**
	 * @return the inputRR
	 */
	public RowResolverTRC getInputRR() {
		return inputRR;
	}

	/**
	 * @param allowStatefulFunctions
	 *            whether to allow stateful UDF invocations
	 */
	public void setAllowStatefulFunctions(boolean allowStatefulFunctions) {
		this.allowStatefulFunctions = allowStatefulFunctions;
	}

	/**
	 * @return whether to allow stateful UDF invocations
	 */
	public boolean getAllowStatefulFunctions() {
		return allowStatefulFunctions;
	}

	/**
	 * @param error
	 *            the error to set
	 * 
	 */
	public void setError(String error, ASTNodeTRC errorSrcNode) {
		this.error = error;
		this.errorSrcNode = errorSrcNode;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	public ASTNodeTRC getErrorSrcNode() {
		return errorSrcNode;
	}

	public Var generateVar(ASTNodeTRC parent, String varname, TypeInfo vartype,
			ExprNodeConstantDesc defaultExpr) {
		if (!astNode2LambdaIdx.containsKey(parent)) {
			this.lambdaExprs.add(parent);
			this.astNode2LambdaIdx.put(parent, this.lambdaExprs.size());
			this.astNode2VarInfos.put(parent,
					new HashMap<String, TypeCheckCtxTRC.Var>());
		}
		String varInternalName = "var-" + this.astNode2LambdaIdx.get(parent)
				+ "-" + varname;
		Var var = new Var(varname, varInternalName, vartype, defaultExpr);
		this.astNode2VarInfos.get(parent).put(varname, var);
		return var;
	}

	public Var getVar(ASTNodeTRC parent, String varname) {
		if (!this.astNode2VarInfos.containsKey(parent)) {
			return null;
		}
		if (!this.astNode2VarInfos.get(parent).containsKey(varname)) {
			return null;
		}
		return this.astNode2VarInfos.get(parent).get(varname);
	}

}
