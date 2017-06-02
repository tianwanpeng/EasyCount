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

import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.io.NullWritable;

import com.tencent.trc.plan.TypeCheckCtxTRC.Var;

public class ExprNodeNewDefineDesc extends ExprNodeDesc implements Serializable {

	private static final long serialVersionUID = 6836750826437074112L;

	final private ArrayList<Var> vars;

	public ExprNodeNewDefineDesc(ArrayList<Var> vars) {
		super(TypeInfoFactory
				.getPrimitiveTypeInfoFromPrimitiveWritable(NullWritable.class));
		this.vars = vars;
	}

	@Explain(displayName = "expr")
	@Override
	public String getExprString() {
		return "define";
	}

	@Override
	public ExprNodeNewDefineDesc clone() {
		return new ExprNodeNewDefineDesc(this.vars);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isSame(Object o) {
		if (!(o instanceof ExprNodeNewDefineDesc)) {
			return false;
		}

		ArrayList<Var> vars1 = (ArrayList<Var>) o;
		if (vars.size() != vars1.size()) {
			return false;
		}
		for (int i = 0; i < vars.size(); i++) {
			if (!vars.get(i).equals(vars1.get(i))) {
				return false;
			}
		}
		return true;
	}

	public ArrayList<Var> getVars() {
		return vars;
	}
}
