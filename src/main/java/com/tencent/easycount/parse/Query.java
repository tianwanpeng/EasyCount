package com.tencent.easycount.parse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.antlr.runtime.tree.Tree;

import com.tencent.easycount.util.graph.GraphWalker.Node;

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
		final StringBuffer sb = new StringBuffer();
		sb.append("{" + this.qmode)
		.append("," + this.tableId)
		.append(this.selectExpr == null ? "" : ","
				+ this.selectExpr.toStringTree()).append("}");
		return sb.toString();
	}

	public Query(final ASTNodeTRC n, final String alias, final QueryMode qmode) {
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
		final HashSet<Query> set = new HashSet<Query>();
		set.addAll(this.aliasToChildQuery.values());
		final ArrayList<Node> nodes = new ArrayList<Node>();
		nodes.addAll(set);
		return nodes;
	}

	@Override
	public String getName() {
		return this.n.getText();
	}

	public Collection<Query> children() {
		return this.aliasToChildQuery.values();
	}

	public void addAlias(final String alias) {
		this.aliases.add(alias);
	}

	public void addChildQuery(final String alias, final Query q) {
		this.aliasToChildQuery.put(alias, q);
	}

	public ASTNodeTRC getWhereExpr() {
		return this.whereExpr;
	}

	public void setWhereExpr(final ASTNodeTRC whereExpr) {
		this.whereExpr = whereExpr;
	}

	public ArrayList<ASTNodeTRC> getGroupExprs() {
		final ArrayList<ASTNodeTRC> groupByExprs = new ArrayList<ASTNodeTRC>(
				this.groupExpr == null ? 0 : this.groupExpr.getChildCount());
		if (this.groupExpr != null) {
			// the last element is coordinate info
			for (int i = 0; i < (this.groupExpr.getChildCount() - 1); i++) {
				final ASTNodeTRC grpbyExpr = (ASTNodeTRC) this.groupExpr
						.getChild(i);
				groupByExprs.add(grpbyExpr);
			}
		}
		return groupByExprs;
	}

	public void setGroupExpr(final ASTNodeTRC groupExpr) {
		this.groupExpr = groupExpr;
	}

	public ASTNodeTRC getCoordinateExpr() {
		if (this.groupExpr == null) {
			return null;
		}
		final Tree tree = this.groupExpr.getChild(this.groupExpr
				.getChildCount() - 1);
		return tree == null ? null : (ASTNodeTRC) tree;
	}

	public ASTNodeTRC getHavingExpr() {
		return this.havingExpr;
	}

	public void setHavingExpr(final ASTNodeTRC havingExpr) {
		this.havingExpr = havingExpr;
	}

	public ASTNodeTRC getN() {
		return this.n;
	}

	public ArrayList<String> getAliases() {
		return this.aliases;
	}

	public ASTNodeTRC getSelectExpr() {
		return this.selectExpr;
	}

	public void setSelectExpr(final ASTNodeTRC selectExpr) {
		this.selectExpr = selectExpr;
	}

	public ASTNodeTRC getDestExpr() {
		return this.destExpr;
	}

	public void setDestExpr(final ASTNodeTRC destExpr) {
		this.destExpr = destExpr;
	}

	public LinkedHashMap<String, ASTNodeTRC> getAggregationExprs() {
		return this.aggregationExprs;
	}

	public void setAggregationExprs(
			final LinkedHashMap<String, ASTNodeTRC> aggregationExprs) {
		if (this.aggregationExprs == null) {
			this.aggregationExprs = aggregationExprs;
		} else {
			this.aggregationExprs.putAll(aggregationExprs);
		}
	}

	public void setExprToColumnAlias(final ASTNodeTRC child,
			final String columnAlias) {
		this.exprToColumnAlias.put(child, columnAlias);
	}

	public ASTNodeTRC getJoinOnExpr() {
		return this.joinOnExpr;
	}

	public void setJoinOnExpr(final ASTNodeTRC joinOnExpr) {
		this.joinOnExpr = joinOnExpr;
	}

	public QueryMode getQmode() {
		return this.qmode;
	}

	public String getTableId() {
		return this.tableId;
	}

	public void setDestTableId(final String destTableId) {
		this.destTableId = destTableId;
	}

	public String getDestTableId() {
		return this.destTableId;
	}

	public boolean containsAggr() {
		return (this.groupExpr != null)
				|| ((this.aggregationExprs != null) && (this.aggregationExprs
						.size() > 0));
	}

	public boolean isDimensionTable() {
		return this.isDimensionTable;
	}

	public void setDimensionTable(final boolean isDimensionTable) {
		this.isDimensionTable = isDimensionTable;
	}

	public void setJoinStreamQuery(final Query joinStreamQuery,
			final String jalias) {
		this.joinStreamQuery = joinStreamQuery;
		this.setStreamQueryAlias(jalias);
	}

	public Query getJoinStreamQuery() {
		return this.joinStreamQuery;
	}

	public String getStreamQueryAlias() {
		return this.streamQueryAlias;
	}

	public void setStreamQueryAlias(final String streamQueryAlias) {
		this.streamQueryAlias = streamQueryAlias;
	}

	public void setTableId(final String tableId) {
		this.tableId = tableId;
	}

	public ArrayList<String> getJoinDTableAliass() {
		return this.joinDTableAliass;
	}

	public void setJoinDTableAliass(final ArrayList<String> joinDTableAliass) {
		this.joinDTableAliass = joinDTableAliass;
	}

	public void setDestKeyExpr(final ASTNodeTRC destKeyExpr) {
		this.destKeyExpr = destKeyExpr;
	}

	public ASTNodeTRC getDestKeyExpr() {
		return this.destKeyExpr;
	}

	public void setDestAttrsExpr(final ASTNodeTRC destAttrsExpr) {
		this.destAttrsExpr = destAttrsExpr;
	}

	public ASTNodeTRC getDestAttrsExpr() {
		return this.destAttrsExpr;
	}
}
