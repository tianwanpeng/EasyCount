package com.tencent.easycount.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Stack;

import org.apache.hadoop.hive.ql.exec.FunctionRegistry;
import org.apache.hadoop.hive.ql.parse.SemanticException;

import com.tencent.easycount.parse.Query.QueryMode;
import com.tencent.easycount.util.graph.GraphWalker;
import com.tencent.easycount.util.graph.GraphWalker.Dispatcher;
import com.tencent.easycount.util.graph.GraphWalker.Node;
import com.tencent.easycount.util.graph.GraphWalker.WalkMode;

public class ParseUtils {
	public final static String INNERTABLE = "__innertable_";
	public final static String PRINTTABLE = "__printtable";

	public static ASTNodeTRC findRootNonNullToken(ASTNodeTRC tree) {
		while ((tree.getToken() == null) && (tree.getChildCount() > 0)) {
			tree = (ASTNodeTRC) tree.getChild(0);
		}
		return tree;
	}

	public static QB generateQb(final ASTNodeTRC tree) throws Exception {
		return parseQb(tree);
	}

	private static QB parseQb(final ASTNodeTRC ast) throws Exception {
		final QB qb = new QB();
		/**
		 * walk the ast tree and parse the info into qb
		 */
		new GraphWalker<Boolean>(new ASTParseDispatcher(qb),
				WalkMode.CHILD_FIRST).walk(new ArrayList<Node>() {
			private static final long serialVersionUID = 1L;
			{
				add(ast);
			}
		});
		return qb;
	}

	private static class ASTParseDispatcher implements Dispatcher<Boolean> {

		int aliseIdx = 0;
		int destQueryAliseIdx = 0;
		final private QB qb;

		public ASTParseDispatcher(final QB qb) {
			this.qb = qb;
		}

		/**
		 * walk the asttree with child first mode to find all sub-querys, the
		 * sub-query includes sub_query, union query, insert query and table ref
		 *
		 * first generate the query-tree whose nodes are querys
		 *
		 * @param nd
		 * @param stack
		 * @param nodeOutputs
		 * @return
		 * @throws SemanticException
		 */
		@Override
		public Boolean dispatch(final Node nd, final Stack<Node> stack,
				final ArrayList<Boolean> nodeOutputs,
				final HashMap<Node, Boolean> retMap) throws SemanticException {
			final ASTNodeTRC n = (ASTNodeTRC) nd;

			switch (n.getToken().getType()) {
			// union query is some kind of sub-query
			case TrcParser.TOK_SUBQUERY:
				// judge if it has some alias, if not just generate one
				String alias = n.getChildCount() > 1 ? ParseStringUtil
						.unescapeIdentifier(n.getChild(1).getText())
						: ("__dummyAlise-" + (this.aliseIdx++));
				final ASTNodeTRC queryn = (ASTNodeTRC) n.getChild(0);
				this.qb.updateQuery(alias, queryn);
				parseQuery(queryn, alias);
				break;
			case TrcParser.TOK_INSERT_QUERY:
				alias = "__dummyDestAlise-" + (this.destQueryAliseIdx++);
				this.qb.updateQuery(alias, n);
				parseQuery(n, alias);
				break;
			case TrcParser.TOK_TABREF:
				// table or subquery defined in with clause
				final ASTNodeTRC tableTree = (ASTNodeTRC) (n.getChild(0));
				if (tableTree.getToken().getType() == TrcParser.KW_INNERTABLE) {
					// inner table for test ??? TODO
					int ms = 1000;
					String tblalias = null;
					if (tableTree.getChildCount() > 0) {
						ms = Integer.parseInt(((ASTNodeTRC) tableTree
								.getChild(0)).getToken().getText().toString());
					}
					if (n.getChildCount() > 1) {
						tblalias = ParseStringUtil
								.unescapeIdentifier(((ASTNodeTRC) n.getChild(0))
										.getToken().getText().toString());
					}
					this.qb.updateQueryTableRef(INNERTABLE + ms, tblalias,
							tableTree);
				} else {
					// the tabIdName may be a real table or a subquery alias
					// defined in with clause
					final String tabIdName = ParseStringUtil
							.getUnescapedName(tableTree);
					alias = null;
					if (n.getChildCount() == 2) {
						// here the tblname may has a new alias
						alias = ParseStringUtil.unescapeIdentifier(n
								.getChild(1).getText());
					}
					this.qb.updateQueryTableRef(tabIdName, alias, tableTree);
				}
				break;
			}
			return true;
		}

		@Override
		public boolean needToDispatchChildren(final Node nd,
				final Stack<Node> stack, final ArrayList<Boolean> nodeOutputs,
				final HashMap<Node, Boolean> retMap) {
			final ASTNodeTRC n = (ASTNodeTRC) nd;

			switch (n.getToken().getType()) {
			// only walk those node that contains sub-query
			case TrcParser.TOK_INSERT:
				return false;
			}
			return true;
		}

		/**
		 * for every query compute the basic information and connect querys to
		 * generate the query-tree
		 *
		 * we must first notice that the walking is child-first, so anytime we
		 * parse query, the subquery has already been updated and added to QB
		 *
		 * @param queryn
		 * @param alias
		 * @throws SemanticException
		 */
		private void parseQuery(final ASTNodeTRC queryn, final String alias)
				throws SemanticException {
			final Query query = this.qb.getQueryByAlias(alias);

			if (queryn.getToken().getType() == TrcParser.TOK_UNION) {
				for (int j = 0; j < queryn.getChildCount(); j++) {
					// just connect all sub query as the union query's parent
					final ASTNodeTRC subqn = (ASTNodeTRC) queryn.getChild(j)
							.getChild(0);
					this.qb.getQueryByAstNode(subqn)
					.addChildQuery(alias, query);
				}
			} else {
				for (int i = 0; i < queryn.getChildCount(); i++) {
					final ASTNodeTRC childast = (ASTNodeTRC) queryn.getChild(i);
					switch (childast.getToken().getType()) {
					case TrcParser.TOK_FROM:
						final ASTNodeTRC frm = (ASTNodeTRC) childast
						.getChild(0);
						if (frm.getToken().getType() == TrcParser.TOK_TABREF) {
							final ASTNodeTRC subqn = (ASTNodeTRC) frm
									.getChild(0);
							this.qb.getQueryByAstNode(subqn).addChildQuery(
									alias, query);
						} else if (frm.getToken().getType() == TrcParser.TOK_SUBQUERY) {
							final ASTNodeTRC subqn = (ASTNodeTRC) frm
									.getChild(0);
							this.qb.getQueryByAstNode(subqn).addChildQuery(
									alias, query);
						} else if (isJoinToken(frm)) {
							// dimension table aliases
							final ArrayList<String> joinDTableAliass = new ArrayList<String>();
							for (int j = 0; j < (frm.getChildCount() - 1); j++) {
								final ASTNodeTRC subqn = (ASTNodeTRC) frm
										.getChild(j).getChild(0);
								String jalias = this.qb
										.getAliasByAstNode(subqn);
								if (frm.getChild(j).getChildCount() > 1) {
									// here if we found it has new alias, we
									// must use it
									jalias = ParseStringUtil
											.unescapeIdentifier(((ASTNodeTRC) frm
													.getChild(j).getChild(1))
													.getToken().getText()
													.toString());
								}

								final Query pQuery = this.qb
										.getQueryByAstNode(subqn);
								if (j == 0) {
									// when join, the first table is stream
									// table, and others are dimension tables
									query.setJoinStreamQuery(pQuery, jalias);
								} else {
									if (pQuery.getQmode() != QueryMode.table) {
										throw new RuntimeException(
												"ERROR only dimension table can be joined ....");
									}
									pQuery.setDimensionTable(true);
									joinDTableAliass.add(this.qb
											.getAliasByAstNode(subqn));
								}
								pQuery.addChildQuery(alias, query);
							}
							query.setJoinDTableAliass(joinDTableAliass);
							query.setJoinOnExpr((ASTNodeTRC) frm.getChild(
									frm.getChildCount() - 1).getChild(0));
						}
						break;
					case TrcParser.TOK_INSERT:
						// every subquery is a insert, final insert dest to
						// table, suquery insert dest to tmp table
						processInsertQuery(childast, alias);
						break;
					}
				}
			}
		}

		private boolean isJoinToken(final ASTNodeTRC node) {
			if ((node.getToken().getType() == TrcParser.TOK_JOIN)
					|| (node.getToken().getType() == TrcParser.TOK_LEFTOUTERJOIN)
					|| (node.getToken().getType() == TrcParser.TOK_RIGHTOUTERJOIN)
					|| (node.getToken().getType() == TrcParser.TOK_FULLOUTERJOIN)
					|| (node.getToken().getType() == TrcParser.TOK_LEFTSEMIJOIN)
					|| (node.getToken().getType() == TrcParser.TOK_UNIQUEJOIN)) {
				return true;
			}

			return false;
		}

		private void processInsertQuery(final ASTNodeTRC nd, final String alias)
				throws SemanticException {
			// continue to complete other info in query, for every subquery
			for (int i = 0; i < nd.getChildCount(); i++) {
				final ASTNodeTRC child = (ASTNodeTRC) nd.getChild(i);
				switch (child.getToken().getType()) {
				case TrcParser.TOK_DESTINATION:
					final Query qq = this.qb.getQueryByAlias(alias);
					qq.setDestExpr(child);
					final ASTNodeTRC cchild = (ASTNodeTRC) child.getChild(0);
					if (cchild.getToken().getType() == TrcParser.TOK_TABNAME) {
						qq.setDestTableId(ParseStringUtil
								.getUnescapedName(cchild));

						for (int j = 1; j < child.getChildCount(); j++) {
							final ASTNodeTRC cc = (ASTNodeTRC) child
									.getChild(j);
							if (cc.getToken().getType() == TrcParser.TOK_KEY) {
								// kv key
								qq.setDestKeyExpr((ASTNodeTRC) cc.getChild(0));
							} else if (cc.getToken().getType() == TrcParser.TOK_ATTRBUTES) {
								// stream attributes
								qq.setDestAttrsExpr((ASTNodeTRC) cc.getChild(0));
							}
						}
					} else if (cchild.getToken().getType() == TrcParser.KW_PRINTTABLE) {
						// just for print test ??? TODO
						qq.setDestTableId(PRINTTABLE);
					}
					break;
				case TrcParser.TOK_SELECT:
					this.qb.getQueryByAlias(alias).setSelectExpr(child);
					doPhase1GetColumnAliasesFromSelect(this.qb, child, alias);
					// process aggregations
					LinkedHashMap<String, ASTNodeTRC> aggregations = doPhase1GetAggregationsFromSelect(child);
					this.qb.getQueryByAlias(alias).setAggregationExprs(
							aggregations);
					break;
				case TrcParser.TOK_WHERE:
					this.qb.getQueryByAlias(alias).setWhereExpr(
							(ASTNodeTRC) child.getChild(0));
					break;
				case TrcParser.TOK_GROUPBY:
					this.qb.getQueryByAlias(alias).setGroupExpr(child);
					break;
				case TrcParser.TOK_HAVING:
					this.qb.getQueryByAlias(alias).setHavingExpr(
							(ASTNodeTRC) child.getChild(0));
					aggregations = doPhase1GetAggregationsFromHaving(child);
					this.qb.getQueryByAlias(alias).setAggregationExprs(
							aggregations);
					break;
				}
			}
		}

		private void doPhase1GetColumnAliasesFromSelect(final QB qb,
				final ASTNodeTRC selectExpr, final String alias) {
			for (int i = 0; i < selectExpr.getChildCount(); ++i) {
				final ASTNodeTRC selExpr = (ASTNodeTRC) selectExpr.getChild(i);
				if ((selExpr.getToken().getType() == TrcParser.TOK_SELEXPR)
						&& (selExpr.getChildCount() == 2)) {
					final String columnAlias = ParseStringUtil
							.unescapeIdentifier(selExpr.getChild(1).getText());
					qb.getQueryByAlias(alias).setExprToColumnAlias(
							(ASTNodeTRC) selExpr.getChild(0), columnAlias);
				}
			}
		}

		private LinkedHashMap<String, ASTNodeTRC> doPhase1GetAggregationsFromSelect(
				final ASTNodeTRC selExpr) throws SemanticException {
			// Iterate over the selects search for aggregation Trees.
			// Use String as keys to eliminate duplicate trees.
			final LinkedHashMap<String, ASTNodeTRC> aggregationTrees = new LinkedHashMap<String, ASTNodeTRC>();
			for (int i = 0; i < selExpr.getChildCount(); ++i) {
				final ASTNodeTRC sel = (ASTNodeTRC) selExpr.getChild(i)
						.getChild(0);
				doPhase1GetAllAggregations(sel, aggregationTrees);
			}
			return aggregationTrees;
		}

		private LinkedHashMap<String, ASTNodeTRC> doPhase1GetAggregationsFromHaving(
				final ASTNodeTRC havingExpr) throws SemanticException {
			// Iterate over the selects search for aggregation Trees.
			// Use String as keys to eliminate duplicate trees.
			final LinkedHashMap<String, ASTNodeTRC> aggregationTrees = new LinkedHashMap<String, ASTNodeTRC>();
			for (int i = 0; i < havingExpr.getChildCount(); ++i) {
				final ASTNodeTRC sel = (ASTNodeTRC) havingExpr.getChild(i);
				doPhase1GetAllAggregations(sel, aggregationTrees);
			}
			return aggregationTrees;
		}

		private void doPhase1GetAllAggregations(
				final ASTNodeTRC expressionTree,
				final LinkedHashMap<String, ASTNodeTRC> aggregations)
						throws SemanticException {
			final int exprTokenType = expressionTree.getToken().getType();
			if ((exprTokenType == TrcParser.TOK_FUNCTION)
					|| (exprTokenType == TrcParser.TOK_FUNCTIONDI)
					|| (exprTokenType == TrcParser.TOK_FUNCTIONACCU)
					|| (exprTokenType == TrcParser.TOK_FUNCTIONACCUDI)
					|| (exprTokenType == TrcParser.TOK_FUNCTIONSW)
					|| (exprTokenType == TrcParser.TOK_FUNCTIONSWDI)
					|| (exprTokenType == TrcParser.TOK_FUNCTIONSTAR)) {
				assert (expressionTree.getChildCount() != 0);
				if (expressionTree.getChild(0).getType() == TrcParser.Identifier) {
					final String functionName = ParseStringUtil
							.unescapeIdentifier(expressionTree.getChild(0)
									.getText());
					if (FunctionRegistry.getGenericUDAFResolver(functionName) != null) {
						aggregations.put(expressionTree.toStringTree(),
								expressionTree);
						return;
					}
				}
			}
			for (int i = 0; i < expressionTree.getChildCount(); i++) {
				doPhase1GetAllAggregations(
						(ASTNodeTRC) expressionTree.getChild(i), aggregations);
			}
		}
	}

}
