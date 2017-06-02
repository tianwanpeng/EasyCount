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

/**
 * RowSchema Implementation.
 */
public class RowSchemaTRC implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<ColumnInfoTRC> signature;

	public RowSchemaTRC() {
	}

	@SuppressWarnings("unchecked")
	public RowSchemaTRC(RowSchemaTRC that) {
		this.signature = (ArrayList<ColumnInfoTRC>) that.signature.clone();
	}

	public RowSchemaTRC(ArrayList<ColumnInfoTRC> signature) {
		this.signature = signature;
	}

	public void setSignature(ArrayList<ColumnInfoTRC> signature) {
		this.signature = signature;
	}

	public ArrayList<ColumnInfoTRC> getSignature() {
		return signature;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder('(');
		for (ColumnInfoTRC col : signature) {
			sb.append(col.toString());
		}
		sb.append(')');
		return sb.toString();
	}
}
