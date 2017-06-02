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

/**
 * ExprNodeNullDesc.
 * 
 */
public class ExprNodeNewIfDesc extends ExprNodeNewBlockDesc implements
		Serializable {

	private static final long serialVersionUID = 1L;
	final private ExprNodeDesc condationDesc;
	final private ArrayList<ExprNodeNewBlockDesc> blockDescs;

	public ExprNodeNewIfDesc(ExprNodeDesc condationDesc,
			ArrayList<ExprNodeNewBlockDesc> assignDescs) {
		super(TypeInfoFactory
				.getPrimitiveTypeInfoFromPrimitiveWritable(NullWritable.class));
		this.condationDesc = condationDesc;
		this.blockDescs = assignDescs;
	}

	@Explain(displayName = "expr")
	@Override
	public String getExprString() {
		return "for";
	}

	@Override
	public ExprNodeNewIfDesc clone() {
		return new ExprNodeNewIfDesc(this.condationDesc, this.blockDescs);
	}

	@Override
	public boolean isSame(Object o) {
		if (!(o instanceof ExprNodeNewIfDesc)) {
			return false;
		}
		ExprNodeNewIfDesc o1 = (ExprNodeNewIfDesc) o;
		if (!getTypeInfo().equals(o1.getTypeInfo())) {
			return false;
		}

		if (!this.condationDesc.isSame(o1.condationDesc)) {
			return false;
		}
		if (blockDescs.size() != o1.blockDescs.size()) {
			return false;
		}
		for (int i = 0; i < blockDescs.size(); i++) {
			if (!blockDescs.get(i).isSame(o1.blockDescs.get(i))) {
				return false;
			}
		}

		return true;
	}

	public ExprNodeDesc getCondationDesc() {
		return condationDesc;
	}

	public ArrayList<ExprNodeNewBlockDesc> getBlockDescs() {
		return blockDescs;
	}

}
