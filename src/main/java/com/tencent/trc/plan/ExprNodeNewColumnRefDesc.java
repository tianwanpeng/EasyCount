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
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.hadoop.hive.ql.plan.ExprNodeColumnDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;

import com.tencent.trc.plan.TypeCheckCtxTRC.Var;

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

	public ExprNodeNewColumnRefDesc(Var var, RefMode fmode) {
		super(var.getTypeInfo());
		this.var = var;
		this.setFmode(fmode);
		columnDesc = new ExprNodeColumnDesc(var.getTypeInfo(),
				var.getVarInternalName(), "", false);
	}

	public String getColumn() {
		return var.getVarName();
	}

	@Override
	public String toString() {
		return "Column[" + var.getVarName() + "]";
	}

	@Explain(displayName = "expr")
	@Override
	public String getExprString() {
		return getColumn();
	}

	@Override
	public List<String> getCols() {
		List<String> lst = new ArrayList<String>();
		lst.add(var.getVarName());
		return lst;
	}

	@Override
	public ExprNodeDesc clone() {
		return new ExprNodeNewColumnRefDesc(var, this.fmode);
	}

	@Override
	public boolean isSame(Object o) {
		if (!(o instanceof ExprNodeNewColumnRefDesc)) {
			return false;
		}
		ExprNodeNewColumnRefDesc dest = (ExprNodeNewColumnRefDesc) o;
		if (!var.equals(dest.var)) {
			return false;
		}
		if (!getTypeInfo().equals(dest.getTypeInfo())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int superHashCode = super.hashCode();
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.appendSuper(superHashCode);
		builder.append(var.hashCode());
		return builder.toHashCode();
	}

	public RefMode getFmode() {
		return fmode;
	}

	public void setFmode(RefMode fmode) {
		this.fmode = fmode;
	}

	public ExprNodeColumnDesc getColumnDesc() {
		return columnDesc;
	}
}
