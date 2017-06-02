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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.parse.SemanticException;

import com.tencent.easycount.parse.ASTNodeTRC;

/**
 * Implementation of the Row Resolver.
 * 
 */
public class RowResolverTRC implements Serializable {
	private static final long serialVersionUID = 1L;
	private RowSchemaTRC rowSchema;
	// table alias --> columnAlias --> ColumnInfo
	private HashMap<String, LinkedHashMap<String, ColumnInfoTRC>> rslvMap;

	// internalName --> [tableAlias, columnAlias]
	private HashMap<String, String[]> invRslvMap;

	// astTree --> ASTNodeTRC
	private Map<String, ASTNodeTRC> expressionMap;

	// TODO: Refactor this and do in a more object oriented manner
	private boolean isExprResolver;

	@SuppressWarnings("unused")
	private static final Log LOG = LogFactory.getLog(RowResolverTRC.class
			.getName());

	public RowResolverTRC() {
		rowSchema = new RowSchemaTRC();
		rslvMap = new HashMap<String, LinkedHashMap<String, ColumnInfoTRC>>();
		invRslvMap = new HashMap<String, String[]>();
		expressionMap = new HashMap<String, ASTNodeTRC>();
		isExprResolver = false;
	}

	/**
	 * Puts a resolver entry corresponding to a source expression which is to be
	 * used for identical expression recognition (e.g. for matching expressions
	 * in the SELECT list with the GROUP BY clause). The convention for such
	 * entries is an empty-string ("") as the table alias together with the
	 * string rendering of the ASTNodeTRC as the column alias.
	 */
	public void putExpression(ASTNodeTRC node, ColumnInfoTRC colInfo) {
		String treeAsString = node.toStringTree();
		expressionMap.put(treeAsString, node);
		put("", treeAsString, colInfo);
	}

	/**
	 * Retrieves the ColumnInfo corresponding to a source expression which
	 * exactly matches the string rendering of the given ASTNodeTRC.
	 */
	public ColumnInfoTRC getExpression(ASTNodeTRC node) {
		return get("", node.toStringTree());
	}

	/**
	 * Retrieves the source expression matching a given ASTNodeTRC's string
	 * rendering exactly.
	 */
	public ASTNodeTRC getExpressionSource(ASTNodeTRC node) {
		return expressionMap.get(node.toStringTree());
	}

	public void put(String tab_alias, String col_alias, ColumnInfoTRC colInfo) {
		if (tab_alias != null) {
			tab_alias = tab_alias.toLowerCase();
		}
		col_alias = col_alias.toLowerCase();
		if (rowSchema.getSignature() == null) {
			rowSchema.setSignature(new ArrayList<ColumnInfoTRC>());
		}

		rowSchema.getSignature().add(colInfo);

		LinkedHashMap<String, ColumnInfoTRC> f_map = rslvMap.get(tab_alias);
		if (f_map == null) {
			f_map = new LinkedHashMap<String, ColumnInfoTRC>();
			rslvMap.put(tab_alias, f_map);
		}
		f_map.put(col_alias, colInfo);

		String[] qualifiedAlias = new String[2];
		qualifiedAlias[0] = tab_alias;
		qualifiedAlias[1] = col_alias;
		invRslvMap.put(colInfo.getInternalName(), qualifiedAlias);
	}

	public boolean hasTableAlias(String tab_alias) {
		return rslvMap.get(tab_alias.toLowerCase()) != null;
	}

	/**
	 * Gets the column Info to tab_alias.col_alias type of a column reference. I
	 * the tab_alias is not provided as can be the case with an non aliased
	 * column, this function looks up the column in all the table aliases in
	 * this row resolver and returns the match. It also throws an exception if
	 * the column is found in multiple table aliases. If no match is found a
	 * null values is returned.
	 * 
	 * This allows us to interpret both select t.c1 type of references and
	 * select c1 kind of references. The later kind are what we call non aliased
	 * column references in the query.
	 * 
	 * @param tab_alias
	 *            The table alias to match (this is null if the column reference
	 *            is non aliased)
	 * @param col_alias
	 *            The column name that is being searched for
	 * @return ColumnInfo
	 * @throws SemanticException
	 */
	public ColumnInfoTRC get(String tab_alias, String col_alias) {
		col_alias = col_alias.toLowerCase();
		ColumnInfoTRC ret = null;

		if (tab_alias != null) {
			tab_alias = tab_alias.toLowerCase();
			HashMap<String, ColumnInfoTRC> f_map = rslvMap.get(tab_alias);
			if (f_map == null) {
				return null;
			}
			ret = f_map.get(col_alias);
		} else {
			boolean found = false;
			for (LinkedHashMap<String, ColumnInfoTRC> cmap : rslvMap.values()) {
				for (Map.Entry<String, ColumnInfoTRC> cmapEnt : cmap.entrySet()) {
					if (col_alias.equalsIgnoreCase(cmapEnt.getKey())) {
						if (found) {
							// throw new SemanticException(
							// "Column "
							// + col_alias
							// + " Found in more than One Tables/Subqueries");
						}
						found = true;
						ret = cmapEnt.getValue();
					}
				}
			}
		}

		return ret;
	}

	/**
	 * check if column name is already exist in RR
	 */
	public void checkColumn(String tableAlias, String columnAlias) {
		ColumnInfoTRC prev = get(null, columnAlias);
		if (prev != null
				&& (tableAlias == null || !tableAlias.equalsIgnoreCase(prev
						.getTabAlias()))) {
			// throw new SemanticException(
			// ErrorMsg.AMBIGUOUS_COLUMN.getMsg(columnAlias));
		}
	}

	public ArrayList<ColumnInfoTRC> getColumnInfos() {
		return rowSchema.getSignature();
	}

	/**
	 * Get a list of aliases for non-hidden columns
	 * 
	 * @param max
	 *            the maximum number of columns to return
	 * @return a list of non-hidden column names no greater in size than max
	 */
	public List<String> getReferenceableColumnAliases(String tableAlias, int max) {
		int count = 0;
		Set<String> columnNames = new LinkedHashSet<String>();

		int tables = rslvMap.size();

		Map<String, ColumnInfoTRC> mapping = rslvMap.get(tableAlias);
		if (mapping != null) {
			for (Map.Entry<String, ColumnInfoTRC> entry : mapping.entrySet()) {
				if (max > 0 && count >= max) {
					break;
				}
				ColumnInfoTRC columnInfo = entry.getValue();
				if (!columnInfo.isHiddenVirtualCol()) {
					columnNames.add(entry.getKey());
					count++;
				}
			}
		} else {
			for (ColumnInfoTRC columnInfo : getColumnInfos()) {
				if (max > 0 && count >= max) {
					break;
				}
				if (!columnInfo.isHiddenVirtualCol()) {
					String[] inverse = !isExprResolver ? reverseLookup(columnInfo
							.getInternalName()) : null;
					if (inverse != null) {
						columnNames
								.add(inverse[0] == null || tables <= 1 ? inverse[1]
										: inverse[0] + "." + inverse[1]);
					} else {
						columnNames.add(columnInfo.getAlias());
					}
					count++;
				}
			}
		}
		return new ArrayList<String>(columnNames);
	}

	public HashMap<String, ColumnInfoTRC> getFieldMap(String tabAlias) {
		if (tabAlias == null) {
			return rslvMap.get(null);
		} else {
			return rslvMap.get(tabAlias.toLowerCase());
		}
	}

	public int getPosition(String internalName) {
		int pos = -1;

		for (ColumnInfoTRC var : rowSchema.getSignature()) {
			++pos;
			if (var.getInternalName().equals(internalName)) {
				return pos;
			}
		}

		return -1;
	}

	public Set<String> getTableNames() {
		return rslvMap.keySet();
	}

	public String[] reverseLookup(String internalName) {
		return invRslvMap.get(internalName);
	}

	public void setIsExprResolver(boolean isExprResolver) {
		this.isExprResolver = isExprResolver;
	}

	public boolean getIsExprResolver() {
		return isExprResolver;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, LinkedHashMap<String, ColumnInfoTRC>> e : rslvMap
				.entrySet()) {
			String tab = e.getKey();
			sb.append(tab + "{");
			HashMap<String, ColumnInfoTRC> f_map = e.getValue();
			if (f_map != null) {
				for (Map.Entry<String, ColumnInfoTRC> entry : f_map.entrySet()) {
					sb.append("(" + entry.getKey() + ","
							+ entry.getValue().toString() + ")");
				}
			}
			sb.append("} ");
		}
		return sb.toString();
	}

	public RowSchemaTRC getRowSchema() {
		return rowSchema;
	}

	public HashMap<String, LinkedHashMap<String, ColumnInfoTRC>> getRslvMap() {
		return rslvMap;
	}

	public HashMap<String, String[]> getInvRslvMap() {
		return invRslvMap;
	}

	public Map<String, ASTNodeTRC> getExpressionMap() {
		return expressionMap;
	}

	public void setExprResolver(boolean isExprResolver) {
		this.isExprResolver = isExprResolver;
	}

	public void setRowSchema(RowSchemaTRC rowSchema) {
		this.rowSchema = rowSchema;
	}

	public void setRslvMap(
			HashMap<String, LinkedHashMap<String, ColumnInfoTRC>> rslvMap) {
		this.rslvMap = rslvMap;
	}

	public void setInvRslvMap(HashMap<String, String[]> invRslvMap) {
		this.invRslvMap = invRslvMap;
	}

	public void setExpressionMap(Map<String, ASTNodeTRC> expressionMap) {
		this.expressionMap = expressionMap;
	}

	public String[] toColumnDesc() {
		StringBuilder cols = new StringBuilder();
		StringBuilder colTypes = new StringBuilder();

		for (ColumnInfoTRC colInfo : getColumnInfos()) {
			if (cols.length() > 0) {
				cols.append(',');
				colTypes.append(':');
			}
			cols.append(colInfo.getInternalName());
			colTypes.append(colInfo.getType().getTypeName());
		}
		return new String[] { cols.toString(), colTypes.toString() };
	}
}
