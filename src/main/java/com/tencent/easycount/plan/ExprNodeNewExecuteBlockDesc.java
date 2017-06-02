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

import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.io.NullWritable;

/**
 * ExprNodeNullDesc.
 * 
 */
public class ExprNodeNewExecuteBlockDesc extends ExprNodeDesc implements
		Serializable {

	private static final long serialVersionUID = 1L;

	final private ArrayList<ExprNodeNewBlockDesc> blockDescs;

	public ExprNodeNewExecuteBlockDesc(
			ArrayList<ExprNodeNewBlockDesc> blockDescs) {
		super(TypeInfoFactory
				.getPrimitiveTypeInfoFromPrimitiveWritable(NullWritable.class));
		this.blockDescs = blockDescs;
	}

	@Explain(displayName = "expr")
	@Override
	public String getExprString() {
		return "executeblock";
	}

	@Override
	public ExprNodeNewExecuteBlockDesc clone() {
		return new ExprNodeNewExecuteBlockDesc(this.blockDescs);
	}

	@Override
	public boolean isSame(Object o) {
		if (!(o instanceof ExprNodeNewExecuteBlockDesc)) {
			return false;
		}
		if (!getTypeInfo().equals(
				((ExprNodeNewExecuteBlockDesc) o).getTypeInfo())) {
			return false;
		}
		ExprNodeNewExecuteBlockDesc o1 = (ExprNodeNewExecuteBlockDesc) o;
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

	public ArrayList<ExprNodeNewBlockDesc> getBlockDescs() {
		return blockDescs;
	}
}
