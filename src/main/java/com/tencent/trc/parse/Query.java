package com.tencent.trc.parse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.antlr.runtime.tree.Tree;

import com.tencent.trc.util.graph.GraphWalker.Node;

/**
 * every subquery has a Query object which contains all kinds of exprs such as
 * where, join, groupby etc..
 * 
 * @author steven
 *
 */
public class Query implements Node {

	private final ASTNodeTRC n;
	private final ArrayList<String> aliases;
	// table name only for table query
	private String tableId = null;
	private String destTableId = null;
	private final HashMap<String, Query> aliasToChildQuery;

	private ASTNodeTRC joinOnExpr = null;
	private ASTNodeTRC destExpr = null;
	
	// when insert into kv dest we should set a key expr 
	private ASTNodeTRC destKeyExpr = null;
	// in stream dest, sometimes every record has some attrs
	private ASTNodeTRC destAttrsExpr = null;
	private ASTNodeTRC selectExpr = null;
	private ASTNodeTRC whereExpr = null;
	private ASTNodeTRC groupExpr = null;
	private ASTNodeTRC havingExpr = null;

	private Query joinStreamQuery;
	private String streamQueryAlias;

	private LinkedHashMap<String, ASTNodeTRC> aggregationExprs = null;
	// private List<ASTNodeTRC> distinctFuncExprs = null;
	private final HashMap<ASTNodeTRC, String> exprToColumnAlias;
	private boolean isDimensionTable = false;

	public static enum QueryMode {
		table, union, subquery, insertq
	}

	private final QueryMode qmode;
	// only contains dim table alias
	private ArrayList<String> joinDTableAliass;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{" + qmode)
				.append("," + tableId)
				.append(selectExpr == null ? "" : ","
						+ selectExpr.toStringTree()).append("}");
		return sb.toString();
	}

	public Query(ASTNodeTRC n, final String alias, QueryMode qmode) {
		this.n = n;
		// this.tableId = alias;
		this.qmode = qmode;
		this.aliases = new ArrayList<String>() {
			private static final long serialVersionUID = 6139375578775301382L;
			{
				add(alias);
			}
		};
		this.aliasToChildQuery = new HashMap<String, Query>();
		this.exprToColumnAlias = new HashMap<ASTNodeTRC, String>();
	}

	@Override
	public List<? extends Node> getChildren() {
		HashSet<Query> set = new HashSet<Query>();
		set.addAll(aliasToChildQuery.values());
		ArrayList<Node> nodes = new ArrayList<Node>();
		nodes.addAll(set);
		return nodes;
	}

	@Override
	public String getName() {
		return n.getText();
	}

	public Collection<Query> children() {
		return aliasToChildQuery.values();
	}

	public void addAlias(String alias) {
		this.aliases.add(alias);
	}

	public void addChildQuery(String alias, Query q) {
		aliasToChildQuery.put(alias, q);
	}

	public ASTNodeTRC getWhereExpr() {
		return whereExpr;
	}

	public void setWhereExpr(ASTNodeTRC whereExpr) {
		this.whereExpr = whereExpr;
	}

	public ArrayList<ASTNodeTRC> getGroupExprs() {
		ArrayList<ASTNodeTRC> groupByExprs = new ArrayList<ASTNodeTRC>(
				groupExpr == null ? 0 : groupExpr.getChildCount());
		if (groupExpr != null) {
			// the last element is coordinate info
			for (int i = 0; i < groupExpr.getChildCount() - 1; i++) {
				ASTNodeTRC grpbyExpr = (ASTNodeTRC) groupExpr.getChild(i);
				groupByExprs.add(grpbyExpr);
			}
		}
		return groupByExprs;
	}

	public void setGroupExpr(ASTNodeTRC groupExpr) {
		this.groupExpr = groupExpr;
	}

	public ASTNodeTRC getCoordinateExpr() {
		if (groupExpr == null) {
			return null;
		}
		Tree tree = groupExpr.getChild(groupExpr.getChildCount() - 1);
		return tree == null ? null : (ASTNodeTRC) tree;
	}

	public ASTNodeTRC getHavingExpr() {
		return havingExpr;
	}

	public void setHavingExpr(ASTNodeTRC havingExpr) {
		this.havingExpr = havingExpr;
	}

	public ASTNodeTRC getN() {
		return n;
	}

	public ArrayList<String> getAliases() {
		return aliases;
	}

	public ASTNodeTRC getSelectExpr() {
		return selectExpr;
	}

	public void setSelectExpr(ASTNodeTRC selectExpr) {
		this.selectExpr = selectExpr;
	}

	public ASTNodeTRC getDestExpr() {
		return destExpr;
	}

	public void setDestExpr(ASTNodeTRC destExpr) {
		this.destExpr = destExpr;
	}

	public LinkedHashMap<String, ASTNodeTRC> getAggregationExprs() {
		return aggregationExprs;
	}

	public void setAggregationExprs(
			LinkedHashMap<String, ASTNodeTRC> aggregationExprs) {
		if (this.aggregationExprs == null) {
			this.aggregationExprs = aggregationExprs;
		} else {
			this.aggregationExprs.putAll(aggregationExprs);
		}
	}

	public void setExprToColumnAlias(ASTNodeTRC child, String columnAlias) {
		this.exprToColumnAlias.put(child, columnAlias);
	}

	public ASTNodeTRC getJoinOnExpr() {
		return joinOnExpr;
	}

	public void setJoinOnExpr(ASTNodeTRC joinOnExpr) {
		this.joinOnExpr = joinOnExpr;
	}

	public QueryMode getQmode() {
		return qmode;
	}

	public String getTableId() {
		return this.tableId;
	}

	public void setDestTableId(String destTableId) {
		this.destTableId = destTableId;
	}

	public String getDestTableId() {
		return this.destTableId;
	}

	public boolean containsAggr() {
		return groupExpr != null
				|| (aggregationExprs != null && (aggregationExprs.size() > 0));
	}

	public boolean isDimensionTable() {
		return isDimensionTable;
	}

	public void setDimensionTable(boolean isDimensionTable) {
		this.isDimensionTable = isDimensionTable;
	}

	public void setJoinStreamQuery(Query joinStreamQuery, String jalias) {
		this.joinStreamQuery = joinStreamQuery;
		this.setStreamQueryAlias(jalias);
	}

	public Query getJoinStreamQuery() {
		return joinStreamQuery;
	}

	public String getStreamQueryAlias() {
		return streamQueryAlias;
	}

	public void setStreamQueryAlias(String streamQueryAlias) {
		this.streamQueryAlias = streamQueryAlias;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public ArrayList<String> getJoinDTableAliass() {
		return joinDTableAliass;
	}

	public void setJoinDTableAliass(ArrayList<String> joinDTableAliass) {
		this.joinDTableAliass = joinDTableAliass;
	}

	public void setDestKeyExpr(ASTNodeTRC destKeyExpr) {
		this.destKeyExpr = destKeyExpr;
	}

	public ASTNodeTRC getDestKeyExpr() {
		return destKeyExpr;
	}

	public void setDestAttrsExpr(ASTNodeTRC destAttrsExpr) {
		this.destAttrsExpr = destAttrsExpr;
	}

	public ASTNodeTRC getDestAttrsExpr() {
		return destAttrsExpr;
	}
}
