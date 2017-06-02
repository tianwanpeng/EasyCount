package com.tencent.trc.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Stack;

import org.apache.hadoop.hive.ql.exec.FunctionRegistry;

import com.tencent.trc.parse.Query.QueryMode;
import com.tencent.trc.util.graph.GraphWalker;
import com.tencent.trc.util.graph.GraphWalker.Dispatcher;
import com.tencent.trc.util.graph.GraphWalker.Node;
import com.tencent.trc.util.graph.GraphWalker.WalkMode;

public class ParseUtils {
	public final static String INNERTABLE = "__innertable_";
	public final static String PRINTTABLE = "__printtable";

	public static ASTNodeTRC findRootNonNullToken(ASTNodeTRC tree) {
		while ((tree.getToken() == null) && (tree.getChildCount() > 0)) {
			tree = (ASTNodeTRC) tree.getChild(0);
		}
		return tree;
	}

	public static QB generateQb(ASTNodeTRC tree) throws Exception {
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
				add((GraphWalker.Node) ast);
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
		 */
		@Override
		public Boolean dispatch(Node nd, Stack<Node> stack,
				ArrayList<Boolean> nodeOutputs, HashMap<Node, Boolean> retMap) {
			ASTNodeTRC n = (ASTNodeTRC) nd;

			switch (n.getToken().getType()) {
			// union query is some kind of sub-query
			case TrcParser.TOK_SUBQUERY:
				// judge if it has some alias, if not just generate one
				String alias = n.getChildCount() > 1 ? ParseStringUtil
						.unescapeIdentifier(n.getChild(1).getText())
						: ("__dummyAlise-" + (aliseIdx++));
				ASTNodeTRC queryn = (ASTNodeTRC) n.getChild(0);
				qb.updateQuery(alias, queryn);
				parseQuery(queryn, alias);
				break;
			case TrcParser.TOK_INSERT_QUERY:
				alias = "__dummyDestAlise-" + (destQueryAliseIdx++);
				qb.updateQuery(alias, n);
				parseQuery(n, alias);
				break;
			case TrcParser.TOK_TABREF:
				// table or subquery defined in with clause
				ASTNodeTRC tableTree = (ASTNodeTRC) (n.getChild(0));
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
					qb.updateQueryTableRef(INNERTABLE + ms, tblalias, tableTree);
				} else {
					// the tabIdName may be a real table or a subquery alias
					// defined in with clause
					String tabIdName = ParseStringUtil
							.getUnescapedName(tableTree);
					alias = null;
					if (n.getChildCount() == 2) {
						// here the tblname may has a new alias
						alias = ParseStringUtil.unescapeIdentifier(n
								.getChild(1).getText());
					}
					qb.updateQueryTableRef(tabIdName, alias, tableTree);
				}
				break;
			}
			return true;
		}

		@Override
		public boolean needToDispatchChildren(Node nd, Stack<Node> stack,
				ArrayList<Boolean> nodeOutputs, HashMap<Node, Boolean> retMap) {
			ASTNodeTRC n = (ASTNodeTRC) nd;

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
		 */
		private void parseQuery(final ASTNodeTRC queryn, String alias) {
			Query query = qb.getQueryByAlias(alias);

			if (queryn.getToken().getType() == TrcParser.TOK_UNION) {
				for (int j = 0; j < queryn.getChildCount(); j++) {
					// just connect all sub query as the union query's parent
					ASTNodeTRC subqn = (ASTNodeTRC) queryn.getChild(j)
							.getChild(0);
					qb.getQueryByAstNode(subqn).addChildQuery(alias, query);
				}
			} else {
				for (int i = 0; i < queryn.getChildCount(); i++) {
					ASTNodeTRC childast = (ASTNodeTRC) queryn.getChild(i);
					switch (childast.getToken().getType()) {
					case TrcParser.TOK_FROM:
						ASTNodeTRC frm = (ASTNodeTRC) childast.getChild(0);
						if (frm.getToken().getType() == TrcParser.TOK_TABREF) {
							ASTNodeTRC subqn = (ASTNodeTRC) frm.getChild(0);
							qb.getQueryByAstNode(subqn).addChildQuery(alias,
									query);
						} else if (frm.getToken().getType() == TrcParser.TOK_SUBQUERY) {
							ASTNodeTRC subqn = (ASTNodeTRC) frm.getChild(0);
							qb.getQueryByAstNode(subqn).addChildQuery(alias,
									query);
						} else if (isJoinToken(frm)) {
							// dimension table aliases
							ArrayList<String> joinDTableAliass = new ArrayList<String>();
							for (int j = 0; j < frm.getChildCount() - 1; j++) {
								ASTNodeTRC subqn = (ASTNodeTRC) frm.getChild(j)
										.getChild(0);
								String jalias = qb.getAliasByAstNode(subqn);
								if (frm.getChild(j).getChildCount() > 1) {
									// here if we found it has new alias, we
									// must use it
									jalias = ParseStringUtil
											.unescapeIdentifier(((ASTNodeTRC) frm
													.getChild(j).getChild(1))
													.getToken().getText()
													.toString());
								}

								Query pQuery = qb.getQueryByAstNode(subqn);
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
									joinDTableAliass.add(qb
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

		private boolean isJoinToken(ASTNodeTRC node) {
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

		private void processInsertQuery(ASTNodeTRC nd, String alias) {
			// continue to complete other info in query, for every subquery
			for (int i = 0; i < nd.getChildCount(); i++) {
				ASTNodeTRC child = (ASTNodeTRC) nd.getChild(i);
				switch (child.getToken().getType()) {
				case TrcParser.TOK_DESTINATION:
					Query qq = qb.getQueryByAlias(alias);
					qq.setDestExpr(child);
					ASTNodeTRC cchild = (ASTNodeTRC) child.getChild(0);
					if (cchild.getToken().getType() == TrcParser.TOK_TABNAME) {
						qq.setDestTableId(ParseStringUtil
								.getUnescapedName(cchild));

						for (int j = 1; j < child.getChildCount(); j++) {
							ASTNodeTRC cc = (ASTNodeTRC) child.getChild(j);
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
					qb.getQueryByAlias(alias).setSelectExpr(child);
					doPhase1GetColumnAliasesFromSelect(qb, child, alias);
					// process aggregations
					LinkedHashMap<String, ASTNodeTRC> aggregations = doPhase1GetAggregationsFromSelect(child);
					qb.getQueryByAlias(alias).setAggregationExprs(aggregations);
					break;
				case TrcParser.TOK_WHERE:
					qb.getQueryByAlias(alias).setWhereExpr(
							(ASTNodeTRC) child.getChild(0));
					break;
				case TrcParser.TOK_GROUPBY:
					qb.getQueryByAlias(alias).setGroupExpr(child);
					break;
				case TrcParser.TOK_HAVING:
					qb.getQueryByAlias(alias).setHavingExpr(
							(ASTNodeTRC) child.getChild(0));
					aggregations = doPhase1GetAggregationsFromHaving(child);
					qb.getQueryByAlias(alias).setAggregationExprs(aggregations);
					break;
				}
			}
		}

		private void doPhase1GetColumnAliasesFromSelect(QB qb,
				ASTNodeTRC selectExpr, String alias) {
			for (int i = 0; i < selectExpr.getChildCount(); ++i) {
				ASTNodeTRC selExpr = (ASTNodeTRC) selectExpr.getChild(i);
				if ((selExpr.getToken().getType() == TrcParser.TOK_SELEXPR)
						&& (selExpr.getChildCount() == 2)) {
					String columnAlias = ParseStringUtil
							.unescapeIdentifier(selExpr.getChild(1).getText());
					qb.getQueryByAlias(alias).setExprToColumnAlias(
							(ASTNodeTRC) selExpr.getChild(0), columnAlias);
				}
			}
		}

		private LinkedHashMap<String, ASTNodeTRC> doPhase1GetAggregationsFromSelect(
				ASTNodeTRC selExpr) {
			// Iterate over the selects search for aggregation Trees.
			// Use String as keys to eliminate duplicate trees.
			LinkedHashMap<String, ASTNodeTRC> aggregationTrees = new LinkedHashMap<String, ASTNodeTRC>();
			for (int i = 0; i < selExpr.getChildCount(); ++i) {
				ASTNodeTRC sel = (ASTNodeTRC) selExpr.getChild(i).getChild(0);
				doPhase1GetAllAggregations(sel, aggregationTrees);
			}
			return aggregationTrees;
		}

		private LinkedHashMap<String, ASTNodeTRC> doPhase1GetAggregationsFromHaving(
				ASTNodeTRC havingExpr) {
			// Iterate over the selects search for aggregation Trees.
			// Use String as keys to eliminate duplicate trees.
			LinkedHashMap<String, ASTNodeTRC> aggregationTrees = new LinkedHashMap<String, ASTNodeTRC>();
			for (int i = 0; i < havingExpr.getChildCount(); ++i) {
				ASTNodeTRC sel = (ASTNodeTRC) havingExpr.getChild(i);
				doPhase1GetAllAggregations(sel, aggregationTrees);
			}
			return aggregationTrees;
		}

		private void doPhase1GetAllAggregations(ASTNodeTRC expressionTree,
				LinkedHashMap<String, ASTNodeTRC> aggregations) {
			int exprTokenType = expressionTree.getToken().getType();
			if (exprTokenType == TrcParser.TOK_FUNCTION
					|| exprTokenType == TrcParser.TOK_FUNCTIONDI
					|| exprTokenType == TrcParser.TOK_FUNCTIONACCU
					|| exprTokenType == TrcParser.TOK_FUNCTIONACCUDI
					|| exprTokenType == TrcParser.TOK_FUNCTIONSW
					|| exprTokenType == TrcParser.TOK_FUNCTIONSWDI
					|| exprTokenType == TrcParser.TOK_FUNCTIONSTAR) {
				assert (expressionTree.getChildCount() != 0);
				if (expressionTree.getChild(0).getType() == TrcParser.Identifier) {
					String functionName = ParseStringUtil
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
