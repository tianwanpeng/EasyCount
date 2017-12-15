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

import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;

import com.tencent.easycount.plan.TypeCheckCtxEC.Var;

/**
 * ExprNodeNullDesc.
 *
 */
public class ExprNodeNewVarDesc extends ExprNodeDesc implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -6666493843346372405L;

	final private Var var;

	public ExprNodeNewVarDesc(final Var var) {
		super(var.getTypeInfo());
		this.var = var;
	}

	@Explain(displayName = "expr")
	@Override
	public String getExprString() {
		return "var";
	}

	@Override
	public ExprNodeNewVarDesc clone() {
		return new ExprNodeNewVarDesc(this.var);
	}

	@Override
	public boolean isSame(final Object o) {

		if (!(o instanceof ExprNodeNewVarDesc)) {
			return false;
		}
		final ExprNodeNewVarDesc o1 = (ExprNodeNewVarDesc) o;
		if (!getTypeInfo().equals(o1.getTypeInfo())) {
			return false;
		}

		if (this.var.equals(o1.var)) {
			return false;
		}

		return true;
	}

	public Var getVar() {
		return this.var;
	}
}
