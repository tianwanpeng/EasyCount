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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.hive.ql.plan.ExprNodeConstantDesc;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;

import com.tencent.easycount.parse.ASTNodeTRC;
import com.tencent.easycount.util.graph.RuleDispatcher.NodeProcessorCtx;

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

		public Var(final String varName, final String varInternalName,
				final TypeInfo typeInfo, final ExprNodeConstantDesc defaultExpr) {
			this.varName = varName;
			this.varInternalName = varInternalName;
			this.typeInfo = typeInfo;
			this.defaultExpr = defaultExpr;
			// defaultExpr.getValue();
		}

		public String getVarName() {
			return this.varName;
		}

		public String getVarInternalName() {
			return this.varInternalName;
		}

		public TypeInfo getTypeInfo() {
			return this.typeInfo;
		}

		@Override
		public int hashCode() {
			int hashcode = this.varInternalName.hashCode();
			hashcode = (hashcode * 31) + this.varName.hashCode();
			hashcode = (hashcode * 31) + this.typeInfo.hashCode();
			hashcode = (hashcode * 31) + this.defaultExpr.hashCode();
			return hashcode;
		}

		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof Var)) {
				return false;
			}
			final Var v = (Var) obj;
			return this.varName.equals(v.varName)
					&& this.varInternalName.equals(v.varInternalName)
					&& this.typeInfo.equals(v.typeInfo)
					&& (((this.defaultExpr == null) && (v.defaultExpr == null)) || this.defaultExpr
							.equals(v.defaultExpr));
		}

		public Object getDefaultVal() {
			return this.defaultExpr == null ? null : ObjectInspectorUtils
					.getWritableConstantValue(this.defaultExpr
							.getWritableObjectInspector());
		}

	}

	/**
	 * The row resolver of the previous operator. This field is used to generate
	 * expression descriptors from the expression ASTs.
	 */
	private RowResolverTRC inputRR;

	private final HashMap<ASTNodeTRC, HashMap<String, Var>> astNode2VarInfos;
	private final HashMap<ASTNodeTRC, Integer> astNode2LambdaIdx;
	private final ArrayList<ASTNodeTRC> lambdaExprs;

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
	public TypeCheckCtxTRC(final RowResolverTRC inputRR) {
		setInputRR(inputRR);
		this.error = null;
		this.allowStatefulFunctions = false;
		this.astNode2VarInfos = new HashMap<ASTNodeTRC, HashMap<String, Var>>();
		this.astNode2LambdaIdx = new HashMap<ASTNodeTRC, Integer>();
		this.lambdaExprs = new ArrayList<ASTNodeTRC>();
	}

	/**
	 * @param inputRR
	 *            the inputRR to set
	 */
	public void setInputRR(final RowResolverTRC inputRR) {
		this.inputRR = inputRR;
	}

	/**
	 * @return the inputRR
	 */
	public RowResolverTRC getInputRR() {
		return this.inputRR;
	}

	/**
	 * @param allowStatefulFunctions
	 *            whether to allow stateful UDF invocations
	 */
	public void setAllowStatefulFunctions(final boolean allowStatefulFunctions) {
		this.allowStatefulFunctions = allowStatefulFunctions;
	}

	/**
	 * @return whether to allow stateful UDF invocations
	 */
	public boolean getAllowStatefulFunctions() {
		return this.allowStatefulFunctions;
	}

	/**
	 * @param error
	 *            the error to set
	 *
	 */
	public void setError(final String error, final ASTNodeTRC errorSrcNode) {
		this.error = error;
		this.errorSrcNode = errorSrcNode;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return this.error;
	}

	public ASTNodeTRC getErrorSrcNode() {
		return this.errorSrcNode;
	}

	public Var generateVar(final ASTNodeTRC parent, final String varname,
			final TypeInfo vartype, final ExprNodeConstantDesc defaultExpr) {
		if (!this.astNode2LambdaIdx.containsKey(parent)) {
			this.lambdaExprs.add(parent);
			this.astNode2LambdaIdx.put(parent, this.lambdaExprs.size());
			this.astNode2VarInfos.put(parent,
					new HashMap<String, TypeCheckCtxTRC.Var>());
		}
		final String varInternalName = "var-"
				+ this.astNode2LambdaIdx.get(parent) + "-" + varname;
		final Var var = new Var(varname, varInternalName, vartype, defaultExpr);
		this.astNode2VarInfos.get(parent).put(varname, var);
		return var;
	}

	public Var getVar(final ASTNodeTRC parent, final String varname) {
		if (!this.astNode2VarInfos.containsKey(parent)) {
			return null;
		}
		if (!this.astNode2VarInfos.get(parent).containsKey(varname)) {
			return null;
		}
		return this.astNode2VarInfos.get(parent).get(varname);
	}

}
