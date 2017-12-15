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
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.hadoop.hive.ql.plan.ExprNodeColumnDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;

import com.tencent.easycount.plan.TypeCheckCtxEC.Var;

/**
 * ExprNodeColumnDesc.
 *
 */
public class ExprNodeNewColumnRefDesc extends ExprNodeDesc implements
Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The column name.
	 */
	private Var var;
	private RefMode fmode;
	private ExprNodeColumnDesc columnDesc;

	public enum RefMode {
		ref, oflist, ofmap, inlist, inmap, varDefine
	}

	public ExprNodeNewColumnRefDesc() {
	}

	public ExprNodeNewColumnRefDesc(final Var var, final RefMode fmode) {
		super(var.getTypeInfo());
		this.var = var;
		this.setFmode(fmode);
		this.columnDesc = new ExprNodeColumnDesc(var.getTypeInfo(),
				var.getVarInternalName(), "", false);
	}

	public String getColumn() {
		return this.var.getVarName();
	}

	@Override
	public String toString() {
		return "Column[" + this.var.getVarName() + "]";
	}

	@Explain(displayName = "expr")
	@Override
	public String getExprString() {
		return getColumn();
	}

	@Override
	public List<String> getCols() {
		final List<String> lst = new ArrayList<String>();
		lst.add(this.var.getVarName());
		return lst;
	}

	@Override
	public ExprNodeDesc clone() {
		return new ExprNodeNewColumnRefDesc(this.var, this.fmode);
	}

	@Override
	public boolean isSame(final Object o) {
		if (!(o instanceof ExprNodeNewColumnRefDesc)) {
			return false;
		}
		final ExprNodeNewColumnRefDesc dest = (ExprNodeNewColumnRefDesc) o;
		if (!this.var.equals(dest.var)) {
			return false;
		}
		if (!getTypeInfo().equals(dest.getTypeInfo())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int superHashCode = super.hashCode();
		final HashCodeBuilder builder = new HashCodeBuilder();
		builder.appendSuper(superHashCode);
		builder.append(this.var.hashCode());
		return builder.toHashCode();
	}

	public RefMode getFmode() {
		return this.fmode;
	}

	public void setFmode(final RefMode fmode) {
		this.fmode = fmode;
	}

	public ExprNodeColumnDesc getColumnDesc() {
		return this.columnDesc;
	}
}
