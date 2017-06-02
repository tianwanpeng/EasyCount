package com.tencent.trc.plan.logical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.hadoop.hive.ql.exec.FunctionRegistry;
import org.apache.hadoop.hive.ql.plan.ExprNodeColumnDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;

import com.tencent.trc.metastore.Field;
import com.tencent.trc.metastore.MetaData;
import com.tencent.trc.metastore.Table;
import com.tencent.trc.metastore.Table.TableType;
import com.tencent.trc.parse.ASTNodeTRC;
import com.tencent.trc.parse.ParseStringUtil;
import com.tencent.trc.parse.QB;
import com.tencent.trc.parse.Query;
import com.tencent.trc.parse.Query.QueryMode;
import com.tencent.trc.parse.TrcParser;
import com.tencent.trc.plan.AggregationDescWrapper;
import com.tencent.trc.plan.AggregationDescWrapper.AggrMode;
import com.tencent.trc.plan.ColumnInfoTRC;
import com.tencent.trc.plan.RowResolverTRC;
import com.tencent.trc.plan.RowSchemaTRC;
import com.tencent.trc.plan.TypeCheckCtxTRC;
import com.tencent.trc.plan.logical.JoinUtils.Condition;
import com.tencent.trc.util.constants.Constants;
import com.tencent.trc.util.graph.GraphPrinter;
import com.tencent.trc.util.graph.GraphWalker;
import com.tencent.trc.util.graph.GraphWalker.Dispatcher;
import com.tencent.trc.util.graph.GraphWalker.Node;
import com.tencent.trc.util.graph.GraphWalker.WalkMode;

public class LogicalPlanGenerator {
	@SuppressWarnings("unused")
	final private ASTNodeTRC tree;
	final private QB qb;
	final private MetaData md;

	final private HashMap<OpDesc, Query> op2Querys;
	final private HashMap<OpDesc, RowResolverTRC> op2RR;

	public LogicalPlanGenerator(ASTNodeTRC tree, QB qb, MetaData md) {
		this.tree = tree;
		this.qb = qb;
		this.md = md;

		this.op2Querys = new HashMap<OpDesc, Query>();
		this.op2RR = new HashMap<OpDesc, RowResolverTRC>();
	}

	/**
	 * the logical plan is just a op-tree, this generateLogicalPlan method aim
	 * to generate the op-tree from the query-tree
	 * 
	 * @param tree
	 * @param qb
	 * @param md
	 * @return
	 * @throws Exception
	 */
	public LogicalPlan generateLogicalPlan() throws Exception {

		/**
		 * just generate the op tree, and op2query
		 */
		HashMap<Node, QueryOpDescTree> queryToQueryTree = new GraphWalker<QueryOpDescTree>(
				new GenerateOpTreeFromQueryTreeDispatcher(),
				WalkMode.CHILD_FIRST).walk(qb.getRootQueryNodes());

		/**
		 * get the root ops, all the root must be ts opdesc
		 */
		ArrayList<OpDesc> rootOps = new ArrayList<OpDesc>();
		ArrayList<Node> rootOpNodes = new ArrayList<Node>();
		for (Node rootq : qb.getRootQueryNodes()) {
			OpDesc rootOp = queryToQueryTree.get(rootq).getRoot();
			rootOps.add(rootOp);
			rootOpNodes.add(rootOp);
		}

		GraphPrinter.print(rootOpNodes, null);

		/**
		 * build each opdesc
		 */
		new GraphWalker<RowResolverTRC>(new BuildOpDescInfoDispatcher(),
				WalkMode.ROOT_FIRST_RECURSIVE).walk(rootOpNodes);

		return new LogicalPlan(queryToQueryTree, rootOps);
	}

	static class QueryOpDescTree {
		final private OpDesc root;
		final private OpDesc dest;

		public QueryOpDescTree(OpDesc root, OpDesc dest) {
			this.root = root;
			this.dest = dest;
		}

		public OpDesc getRoot() {
			return root;
		}

		public OpDesc getDest() {
			return dest;
		}
	}

	private class GenerateOpTreeFromQueryTreeDispatcher implements
			Dispatcher<QueryOpDescTree> {

		/**
		 * for each query, first generate the op-tree within the query, then
		 * connect it with their children
		 * 
		 * query-tree is just a line(list), without branch from any node, only
		 * one src node and one dest node
		 * 
		 * @param nd
		 * @param stack
		 * @param nodeOutputs
		 * @return
		 */
		@Override
		public QueryOpDescTree dispatch(Node nd, Stack<Node> stack,
				ArrayList<QueryOpDescTree> nodeOutputs,
				HashMap<Node, QueryOpDescTree> retMap) {
			Query q = (Query) nd;
			QueryOpDescTree qOpDescTree = generateOpDesc(q);
			appendToOpQuery(qOpDescTree, q);
			connect(qOpDescTree, nodeOutputs);
			return qOpDescTree;
		}

		@Override
		public boolean needToDispatchChildren(Node nd, Stack<Node> stack,
				ArrayList<QueryOpDescTree> nodeOutputs,
				HashMap<Node, QueryOpDescTree> retMap) {
			return true;
		}

		private void appendToOpQuery(QueryOpDescTree qOpDescTree, Query q) {
			OpDesc opd = qOpDescTree.root;
			do {
				op2Querys.put(opd, q);
				opd = opd.getChildren().size() > 0 ? (OpDesc) opd.getChildren()
						.get(0) : null;
			} while (opd != null);
		}

		private void connect(QueryOpDescTree qOpDescTree,
				ArrayList<QueryOpDescTree> nodeOutputs) {
			for (QueryOpDescTree subTree : nodeOutputs) {
				qOpDescTree.getDest().addChild(subTree.getRoot());
			}
		}

		private QueryOpDescTree generateOpDesc(Query q) {
			if (q.getQmode() == QueryMode.table) {
				OpDesc opdesc = new OpDesc1TS();
				return new QueryOpDescTree(opdesc, opdesc);
			}
			if (q.getQmode() == QueryMode.union) {
				OpDesc opdesc = new OpDesc4UNION();
				return new QueryOpDescTree(opdesc, opdesc);
			}

			OpDesc rootop = null;
			OpDesc destop = null;
			if (q.getJoinOnExpr() != null) {
				rootop = new OpDesc3JOIN();
				destop = rootop;
			}

			if (q.getWhereExpr() != null) {
				OpDesc op = new OpDesc2FIL(false);
				if (rootop == null) {
					rootop = op;
					destop = rootop;
				} else {
					destop.addChild(op);
					destop = op;
				}
			}

			if (q.containsAggr()) {
				OpDesc op = new OpDesc5GBY(true);
				if (rootop == null) {
					rootop = op;
					destop = rootop;
				} else {
					destop.addChild(op);
					destop = op;
				}

				OpDesc gop = new OpDesc5GBY(false);
				op.addChild(gop);

				destop = gop;
			}

			if (q.getHavingExpr() != null) {
				OpDesc op = new OpDesc2FIL(true);
				if (rootop == null) {
					rootop = op;
					destop = rootop;
				} else {
					destop.addChild(op);
					destop = op;
				}
			}

			OpDesc sop = new OpDesc6SEL();

			if (rootop == null) {
				rootop = sop;
				destop = sop;
			} else {
				destop.addChild(sop);
				destop = sop;
			}

			if (q.getQmode() == QueryMode.insertq) {
				OpDesc fop = new OpDesc7FS();
				destop.addChild(fop);
				destop = fop;
			}

			return new QueryOpDescTree(rootop, destop);
		}
	}

	private class BuildOpDescInfoDispatcher implements
			Dispatcher<RowResolverTRC> {
		final private HashMap<Class<? extends OpDesc>, OpDescBuilder> opDescBuilders;

		public BuildOpDescInfoDispatcher() {
			opDescBuilders = new HashMap<Class<? extends OpDesc>, OpDescBuilder>();
			opDescBuilders.put(OpDesc1TS.class, new OpDescBuilder1TS());
			opDescBuilders.put(OpDesc2FIL.class, new OpDescBuilder2FIL());
			opDescBuilders.put(OpDesc3JOIN.class, new OpDescBuilder3JOIN());
			opDescBuilders.put(OpDesc4UNION.class, new OpDescBuilder4UNION());
			opDescBuilders.put(OpDesc5GBY.class, new OpDescBuilder5GBY());
			opDescBuilders.put(OpDesc6SEL.class, new OpDescBuilder6SEL());
			opDescBuilders.put(OpDesc7FS.class, new OpDescBuilder7FS());
		}

		@Override
		public RowResolverTRC dispatch(Node nd, Stack<Node> stack,
				ArrayList<RowResolverTRC> nodeOutputs,
				HashMap<Node, RowResolverTRC> retMap) throws Exception {
			RowResolverTRC rr = opDescBuilders.get(nd.getClass()).process(nd,
					stack, nodeOutputs, retMap);
			if (rr != null) {
				System.out.println("nd2RR : " + nd.getName() + " " + rr);
			}
			return rr;
		}

		@Override
		public boolean needToDispatchChildren(Node nd, Stack<Node> stack,
				ArrayList<RowResolverTRC> nodeOutputs,
				HashMap<Node, RowResolverTRC> retMap) {
			return retMap.get(nd) != null;
		}
	}

	private interface OpDescBuilder {
		public RowResolverTRC process(Node nd, Stack<Node> stack,
				ArrayList<RowResolverTRC> nodeOutputs,
				HashMap<Node, RowResolverTRC> retMap) throws Exception;
	}

	private class OpDescBuilder1TS implements OpDescBuilder {

		@Override
		public RowResolverTRC process(Node nd, Stack<Node> stack,
				ArrayList<RowResolverTRC> nodeOutputs,
				HashMap<Node, RowResolverTRC> retMap) {
			OpDesc1TS opd = (OpDesc1TS) nd;

			Query query = op2Querys.get(opd);

			Table table = md.getTable(query.getTableId());
			String tableAlias = qb.getAliasByAstNode(query.getN());
			opd.setTable(table);
			opd.setDimensionTable(query.isDimensionTable());

			RowResolverTRC outputRR = new RowResolverTRC();
			ArrayList<String> outputColumnNames = new ArrayList<String>();
			ArrayList<TypeInfo> outputTypes = new ArrayList<TypeInfo>();
			if (table.getTableType() == TableType.stream
					|| table.getTableType() == TableType.tube) {
				outputColumnNames.add(Constants.dataAttrs);
				TypeInfo attrtype = TypeInfoFactory.getMapTypeInfo(
						TypeInfoFactory.stringTypeInfo,
						TypeInfoFactory.stringTypeInfo);
				outputTypes.add(attrtype);
				outputRR.put(tableAlias, Constants.dataAttrs,
						new ColumnInfoTRC(Constants.dataAttrs, attrtype,
								tableAlias, false));
			}
			for (Field field : table.getFields()) {
				ColumnInfoTRC colInfo = new ColumnInfoTRC(
						field.getColumnName(), field.getType(), tableAlias,
						false);
				outputRR.put(tableAlias, field.getColumnName(), colInfo);
				for (String alias : query.getAliases()) {
					outputRR.put(alias, field.getColumnName(), colInfo);
				}
				outputColumnNames.add(field.getColumnName());
				outputTypes.add(field.getType());
			}

			opd.setOutputColumnNames(outputColumnNames);
			opd.setOutputType((StructTypeInfo) TypeInfoFactory
					.getStructTypeInfo(outputColumnNames, outputTypes));
			op2RR.put(opd, outputRR);
			return outputRR;
		}
	}

	private class OpDescBuilder2FIL implements OpDescBuilder {

		@Override
		public RowResolverTRC process(Node nd, Stack<Node> stack,
				ArrayList<RowResolverTRC> nodeOutputs,
				HashMap<Node, RowResolverTRC> retMap) throws Exception {
			OpDesc2FIL opd = (OpDesc2FIL) nd;
			OpDesc parent = (OpDesc) stack.get(stack.size() - 2);
			RowResolverTRC outputRR = op2RR.get(parent);

			boolean having = "HAVING".equals(nd.getName());
			Query q = op2Querys.get(opd);

			ExprNodeDesc predicate = getExprNodeDescCached(
					having ? q.getHavingExpr() : q.getWhereExpr(), outputRR);

			opd.setPredicate(predicate);
			opd.setOutputType(parent.getOutputType());
			op2RR.put(opd, outputRR);
			return outputRR;
		}
	}

	private ExprNodeDesc getExprNodeDescCached(ASTNodeTRC expr,
			RowResolverTRC input) throws Exception {
		ColumnInfoTRC colInfo = input.getExpression(expr);
		if (colInfo != null) {
			return new ExprNodeColumnDesc(colInfo.getType(),
					colInfo.getInternalName(), colInfo.getTabAlias(),
					colInfo.getIsVirtualCol(), colInfo.isSkewedCol());
		}
		ExprNodeDesc res = LogicalPlanExprUtils.getExprNodeDesc(expr,
				new TypeCheckCtxTRC(input));
		if (res == null) {
			System.err.println("warned : " + expr.toStringTree()
					+ " from input : " + input + " is null");
		}
		return res;
	}

	private class OpDescBuilder3JOIN implements OpDescBuilder {
		@Override
		public RowResolverTRC process(Node nd, Stack<Node> stack,
				ArrayList<RowResolverTRC> nodeOutputs,
				HashMap<Node, RowResolverTRC> retMap) throws Exception {

			OpDesc3JOIN opd = (OpDesc3JOIN) nd;
			for (OpDesc partent : opd.parentOps()) {
				if (op2RR.get(partent) == null) {
					// if any of its parent not processed, just ignore it
					return null;
				}
			}

			Query query = op2Querys.get(nd);

			ArrayList<String> joinDTableAliass = query.getJoinDTableAliass();

			HashMap<String, Table> joinDTables = new HashMap<String, Table>();
			for (String alias : joinDTableAliass) {
				Table tbl = md.getTable(qb.getQueryByAlias(alias).getTableId());
				joinDTables.put(alias, tbl);
			}
			opd.setAlias2JoinTables(joinDTables);

			String streamAlias = qb.getAliasByAstNode(query
					.getJoinStreamQuery().getN());
			opd.setStreamAlias(streamAlias);

			// alias is the dimension table alias, and the ASTNodeTRC is the
			// expr ast of the stream table, that means use the computed result
			// of the expr on stream table as the key to query the <K,V>
			// dimension table
			HashMap<String, Condition> alias2KeyAsts = new HashMap<String, Condition>();
			JoinUtils.generateJoinTree(query.getJoinOnExpr(), streamAlias,
					joinDTableAliass, alias2KeyAsts);
			// System.out.println("alias2KeyAsts : " + alias2KeyAsts);

			HashMap<String, RowResolverTRC> alias2InputRRs = new HashMap<String, RowResolverTRC>();
			// for (OpDesc partent : opd.parentOps()) {
			// String alias = qb.getAliasByAstNode(op2Querys.get(partent)
			// .getN());
			// alias2InputRRs.put(alias, op2RR.get(partent));
			// }
			for (OpDesc partent : opd.parentOps()) {
				for (String alias : op2Querys.get(partent).getAliases()) {
					alias2InputRRs.put(alias, op2RR.get(partent));
				}
			}

			HashMap<String, ExprNodeDesc> table2KeyExprs = new HashMap<String, ExprNodeDesc>();
			RowResolverTRC streamRR = alias2InputRRs.get(streamAlias);
			for (String alias : alias2KeyAsts.keySet()) {
				Condition condition = alias2KeyAsts.get(alias);
				RowResolverTRC rr = alias2InputRRs.get(alias);
				ExprNodeDesc leftNodeDesc = getExprNodeDescCached(
						condition.leftAst, condition.leftIsStream ? streamRR
								: rr);
				ExprNodeDesc rightNodeDesc = getExprNodeDescCached(
						condition.rightAst, condition.leftIsStream ? rr
								: streamRR);
				if (leftNodeDesc.getTypeInfo() != rightNodeDesc.getTypeInfo()) {
					throw new RuntimeException(
							"type in on expr must be same : left is "
									+ leftNodeDesc.getTypeInfo()
									+ " and right is "
									+ rightNodeDesc.getTypeInfo());
				}

				table2KeyExprs.put(alias, condition.leftIsStream ? leftNodeDesc
						: rightNodeDesc);
			}
			opd.setAlias2ConditionKeyExprs(table2KeyExprs);

			RowResolverTRC outputRR = new RowResolverTRC();
			ArrayList<String> outputColumnNames = new ArrayList<String>();

			HashMap<String, ArrayList<ExprNodeDesc>> alias2ExprDesc = new HashMap<String, ArrayList<ExprNodeDesc>>();
			ArrayList<TypeInfo> outputTypes = new ArrayList<TypeInfo>();

			int outputPos = 0;
			ArrayList<ExprNodeDesc> keyDesc1 = new ArrayList<ExprNodeDesc>();
			HashMap<String, ColumnInfoTRC> colalias2ColumnInfo1 = streamRR
					.getFieldMap(streamAlias);
			for (String colalias : colalias2ColumnInfo1.keySet()) {
				ColumnInfoTRC valueInfo = colalias2ColumnInfo1.get(colalias);
				keyDesc1.add(new ExprNodeColumnDesc(valueInfo.getType(),
						valueInfo.getInternalName(), valueInfo.getTabAlias(),
						valueInfo.getIsVirtualCol()));
				String colName = "internalColumn-" + outputPos;
				outputPos++;
				outputColumnNames.add(colName);
				outputRR.put(streamAlias, colalias,
						new ColumnInfoTRC(colName, valueInfo.getType(),
								streamAlias, valueInfo.getIsVirtualCol(),
								valueInfo.isHiddenVirtualCol()));
				outputTypes.add(valueInfo.getType());
			}
			alias2ExprDesc.put(streamAlias, keyDesc1);

			for (String tabAlias : joinDTableAliass) {
				ArrayList<ExprNodeDesc> keyDesc = new ArrayList<ExprNodeDesc>();
				HashMap<String, ColumnInfoTRC> colalias2ColumnInfo = alias2InputRRs
						.get(tabAlias).getFieldMap(tabAlias);
				for (String colalias : colalias2ColumnInfo.keySet()) {
					ColumnInfoTRC valueInfo = colalias2ColumnInfo.get(colalias);
					keyDesc.add(new ExprNodeColumnDesc(valueInfo.getType(),
							valueInfo.getInternalName(), valueInfo
									.getTabAlias(), valueInfo.getIsVirtualCol()));
					String colName = "internalColumn-" + outputPos;
					outputPos++;
					outputColumnNames.add(colName);
					outputRR.put(tabAlias, colalias,
							new ColumnInfoTRC(colName, valueInfo.getType(),
									tabAlias, valueInfo.getIsVirtualCol(),
									valueInfo.isHiddenVirtualCol()));
					outputTypes.add(valueInfo.getType());
				}
				alias2ExprDesc.put(tabAlias, keyDesc);

			}

			opd.setAlias2ExprDesc(alias2ExprDesc);

			opd.setOutputColumnNames(outputColumnNames);

			opd.setOutputType((StructTypeInfo) TypeInfoFactory
					.getStructTypeInfo(outputColumnNames, outputTypes));

			op2RR.put((OpDesc) nd, outputRR);
			return outputRR;
		}
	}

	private class OpDescBuilder4UNION implements OpDescBuilder {

		@Override
		public RowResolverTRC process(Node nd, Stack<Node> stack,
				ArrayList<RowResolverTRC> nodeOutputs,
				HashMap<Node, RowResolverTRC> retMap) {

			OpDesc4UNION opd = (OpDesc4UNION) nd;

			for (OpDesc partent : opd.parentOps()) {
				if (op2RR.get(partent) == null) {
					// if any of its parent not processed, just ignore it
					return null;
				}
			}

			Query q = op2Querys.get(nd);
			String qAlias = qb.getAliasByAstNode(q.getN());
			RowResolverTRC outputRR = new RowResolverTRC();
			ArrayList<String> outputColumnNames = new ArrayList<String>();
			ArrayList<TypeInfo> outputTypes = new ArrayList<TypeInfo>();

			ArrayList<RowSchemaTRC> parentSchemas = new ArrayList<RowSchemaTRC>();
			for (OpDesc partent : opd.parentOps()) {
				RowResolverTRC rr = op2RR.get(partent);
				parentSchemas.add(rr.getRowSchema());
			}

			int size = 0;
			for (int i = 0; i < parentSchemas.size(); i++) {
				if (i == 0) {
					size = parentSchemas.get(i).getSignature().size();
				} else {
					if (parentSchemas.get(i).getSignature().size() != size) {
						throw new RuntimeException(
								"union size is not equal ... ");
					}
				}
			}

			OpDesc fistParent = opd.parentOps().iterator().next();
			RowResolverTRC firstParentRR = op2RR.get(fistParent);
			String firstAlias = qb.getAliasByAstNode(op2Querys.get(fistParent)
					.getN());
			int pos = 0;
			for (Map.Entry<String, ColumnInfoTRC> lEntry : firstParentRR
					.getFieldMap(firstAlias).entrySet()) {
				ColumnInfoTRC unionColInfo = new ColumnInfoTRC(
						lEntry.getValue());

				ArrayList<TypeInfo> columnInfoTRCs = new ArrayList<TypeInfo>();
				for (RowSchemaTRC rs : parentSchemas) {
					columnInfoTRCs.add(rs.getSignature().get(pos).getType());
				}
				TypeInfo type = getCommonTypeInfoTRC(columnInfoTRCs);
				if (type == null) {
					System.err.println("error ::: "
							+ firstParentRR.getFieldMap(firstAlias).keySet());
					System.err.println("error ::: " + lEntry.getKey() + " : "
							+ columnInfoTRCs);
					throw new RuntimeException(
							"union type should be equal ... ");
				}
				unionColInfo.setType(type);

				outputRR.put(qAlias, lEntry.getKey(), unionColInfo);
				outputTypes.add(type);
				outputColumnNames.add(unionColInfo.getInternalName());
				pos++;
			}

			// opd.setOutputColumnNames(outputColumnNames);

			opd.setOutputType((StructTypeInfo) TypeInfoFactory
					.getStructTypeInfo(outputColumnNames, outputTypes));

			op2RR.put(opd, outputRR);

			return outputRR;

		}

		private TypeInfo getCommonTypeInfoTRC(ArrayList<TypeInfo> types) {
			TypeInfo leftType = types.get(0);
			for (int i = 1; i < types.size(); i++) {
				// TODO all the type must be same
				if (!leftType.equals(types.get(i))) {
					System.err
							.println("all the type in union clause should be same : "
									+ leftType + " " + types.get(i));
					return null;
				}
				leftType = FunctionRegistry.getCommonClassForUnionAll(leftType,
						types.get(i));
			}
			return leftType;
		}
	}

	private class OpDescBuilder5GBY implements OpDescBuilder {

		@Override
		public RowResolverTRC process(Node nd, Stack<Node> stack,
				ArrayList<RowResolverTRC> nodeOutputs,
				HashMap<Node, RowResolverTRC> retMap) throws Exception {
			OpDesc5GBY opd = (OpDesc5GBY) nd;
			boolean mapMode = "MGBY".equals(opd.getName());

			OpDesc parent = (OpDesc) stack.get(stack.size() - 2);
			RowResolverTRC inputRR = op2RR.get(parent);

			// System.err.println(op2Querys);
			ASTNodeTRC coordinateAst = (ASTNodeTRC) op2Querys.get(opd)
					.getCoordinateExpr();
			if (coordinateAst != null && coordinateAst.getChildCount() > 0) {
				for (int i = 0; i < coordinateAst.getChildCount(); i++) {
					ASTNodeTRC astInfo = (ASTNodeTRC) coordinateAst.getChild(i);
					if (astInfo.getType() == TrcParser.TOK_AGGR_INTERVAL) {
						opd.setAggrInterval(Integer.parseInt(ParseStringUtil
								.unescapeIdentifier(astInfo.getChild(0)
										.getText())));
					} else if (astInfo.getType() == TrcParser.TOK_ACCU_INTERVAL) {
						opd.setAccuInterval(Integer.parseInt(ParseStringUtil
								.unescapeIdentifier(astInfo.getChild(0)
										.getText())));
					} else if (astInfo.getType() == TrcParser.TOK_SW_INTERVAL) {
						opd.setSwInterval(Integer.parseInt(ParseStringUtil
								.unescapeIdentifier(astInfo.getChild(0)
										.getText())));
					} else if (astInfo.getType() == TrcParser.KW_ACCUGLOBAL) {
						opd.setAccuGlobal(true);
					} else if (astInfo.getType() == TrcParser.TOK_COORDINATE_EXPR) {
						if (mapMode) {
							opd.setCoordinateExpr(getExprNodeDescCached(
									(ASTNodeTRC) astInfo.getChild(0), inputRR));
						} else {
							opd.setCoordinateExpr(new ExprNodeColumnDesc(
									TypeInfoFactory.longTypeInfo,
									Constants.gbyAggrTupleTime, "", false));
						}
					}
				}
			}

			if (mapMode) {
				return processMGBY(nd, stack, nodeOutputs, retMap);
			} else {
				return processRGBY(nd, stack, nodeOutputs, retMap);
			}

		}

		private RowResolverTRC processMGBY(Node nd, Stack<Node> stack,
				ArrayList<RowResolverTRC> nodeOutputs,
				HashMap<Node, RowResolverTRC> retMap) throws Exception {
			OpDesc5GBY opd = (OpDesc5GBY) nd;
			OpDesc parent = (OpDesc) stack.get(stack.size() - 2);
			RowResolverTRC inputRR = op2RR.get(parent);
			RowResolverTRC outputRR = new RowResolverTRC();
			outputRR.setIsExprResolver(true);

			Query query = op2Querys.get(opd);

			boolean isAccuGlobal = opd.isAccuGlobal();

			ArrayList<ASTNodeTRC> grpByExprs = query.getGroupExprs();

			ArrayList<ExprNodeDesc> groupByKeys = new ArrayList<ExprNodeDesc>();

			ArrayList<String> outputColumnNames = new ArrayList<String>();

			ArrayList<TypeInfo> outputTypes = new ArrayList<TypeInfo>();

			ArrayList<AggregationDescWrapper> aggregations = new ArrayList<AggregationDescWrapper>();

			int idx = 0;
			for (int i = 0; i < grpByExprs.size(); ++i) {
				ASTNodeTRC grpbyExpr = grpByExprs.get(i);
				ExprNodeDesc grpByExprNode = getExprNodeDescCached(grpbyExpr,
						inputRR);

				groupByKeys.add(grpByExprNode);

				String columnName = "internalColumn-" + (idx++);
				TypeInfo type = grpByExprNode.getTypeInfo();

				outputColumnNames.add(columnName);
				outputTypes.add(type);

				outputRR.putExpression(grpbyExpr, new ColumnInfoTRC(columnName,
						type, "", false));
			}

			outputColumnNames.add(Constants.gbyAggrTupleTime);
			outputTypes.add(TypeInfoFactory.longTypeInfo);
			outputRR.put("", Constants.gbyAggrTupleTime, new ColumnInfoTRC(
					Constants.gbyAggrTupleTime, TypeInfoFactory.longTypeInfo,
					"", false));

			HashMap<String, ASTNodeTRC> aggregationTrees = query
					.getAggregationExprs();
			for (Map.Entry<String, ASTNodeTRC> entry : aggregationTrees
					.entrySet()) {
				ASTNodeTRC value = entry.getValue();
				String aggName = ParseStringUtil.unescapeIdentifier(value
						.getChild(0).getText());
				ArrayList<ExprNodeDesc> aggParameters = new ArrayList<ExprNodeDesc>();
				// 0 is the function name
				for (int i = 1; i < value.getChildCount(); i++) {
					ASTNodeTRC paraExpr = (ASTNodeTRC) value.getChild(i);
					ExprNodeDesc paraExprNode = LogicalPlanExprUtils
							.getExprNodeDesc(paraExpr, new TypeCheckCtxTRC(
									inputRR));
					aggParameters.add(paraExprNode);
				}

				boolean isAllColumns = value.getType() == TrcParser.TOK_FUNCTIONSTAR;

				System.err.println("processMGBY : " + aggName + " : "
						+ aggParameters + " : " + value.toStringTree());
				GenericUDAFEvaluator genericUDAFEvaluator = getGenericUDAFEvaluator(
						aggName, aggParameters, value, false, isAllColumns);
				GenericUDAFInfo udaf = getGenericUDAFInfo(genericUDAFEvaluator,
						GenericUDAFEvaluator.Mode.PARTIAL1, aggParameters);

				AggrMode aggrMode = getAggrMode(value);
				if (isAccuGlobal) {
					if (aggrMode != AggrMode.AGGR) {
						throw new RuntimeException(
								"only aggr mode aggr func is support in ACCUGLOBAL mode, while in ACCUGLOBAL mode all the aggr func is set to ACCU mode as default...");
					}
				}
				aggregations.add(new AggregationDescWrapper(aggName
						.toLowerCase(), udaf.genericUDAFEvaluator.getClass(),
						udaf.convertedParameters, false,
						GenericUDAFEvaluator.Mode.PARTIAL1, aggrMode));
				String columnName = "internalColumn-" + (idx++);
				TypeInfo type = udaf.returnType;

				outputColumnNames.add(columnName);
				outputTypes.add(type);

				outputRR.putExpression(value, new ColumnInfoTRC(columnName,
						type, "", false));
			}

			opd.setGroupByKeys(groupByKeys);

			opd.setAggregations(aggregations);

			opd.setOutputColumnNames(outputColumnNames);

			opd.setOutputType((StructTypeInfo) TypeInfoFactory
					.getStructTypeInfo(outputColumnNames, outputTypes));

			op2RR.put(opd, outputRR);

			return outputRR;

		}

		private AggrMode getAggrMode(ASTNodeTRC value) {
			int funcType = value.getToken().getType();
			if (funcType == TrcParser.TOK_FUNCTION) {
				return AggrMode.AGGR;
			}
			if (funcType == TrcParser.TOK_FUNCTIONACCU) {
				return AggrMode.ACCU;
			}
			if (funcType == TrcParser.TOK_FUNCTIONSW) {
				return AggrMode.SW;
			}
			return null;
		}

		private RowResolverTRC processRGBY(Node nd, Stack<Node> stack,
				ArrayList<RowResolverTRC> nodeOutputs,
				HashMap<Node, RowResolverTRC> retMap) {
			OpDesc5GBY opd = (OpDesc5GBY) nd;
			OpDesc parent = (OpDesc) stack.get(stack.size() - 2);
			RowResolverTRC inputRR = op2RR.get(parent);
			RowResolverTRC outputRR = new RowResolverTRC();
			outputRR.setIsExprResolver(true);

			Query query = op2Querys.get(opd);

			ArrayList<ASTNodeTRC> grpByExprs = query.getGroupExprs();

			ArrayList<ExprNodeDesc> groupByKeys = new ArrayList<ExprNodeDesc>();

			ArrayList<String> outputColumnNames = new ArrayList<String>();

			ArrayList<TypeInfo> outputTypes = new ArrayList<TypeInfo>();

			int idx = 0;
			for (int i = 0; i < grpByExprs.size(); ++i) {
				ASTNodeTRC grpbyExpr = grpByExprs.get(i);
				ColumnInfoTRC exprInfo = inputRR.getExpression(grpbyExpr);

				groupByKeys.add(new ExprNodeColumnDesc(exprInfo.getType(),
						exprInfo.getInternalName(), exprInfo.getTabAlias(),
						exprInfo.getIsVirtualCol()));
				String columnName = "internalColumn-" + (idx++);
				TypeInfo type = exprInfo.getType();

				outputColumnNames.add(columnName);
				outputTypes.add(type);

				outputRR.putExpression(grpbyExpr, new ColumnInfoTRC(columnName,
						type, "", false));
			}

			outputColumnNames.add(Constants.gbyAggrTupleTime);
			outputTypes.add(TypeInfoFactory.longTypeInfo);
			outputRR.put("", Constants.gbyAggrTupleTime, new ColumnInfoTRC(
					Constants.gbyAggrTupleTime, TypeInfoFactory.longTypeInfo,
					"", false));

			HashMap<String, ASTNodeTRC> aggregationTrees = query
					.getAggregationExprs();
			ArrayList<AggregationDescWrapper> aggregations = new ArrayList<AggregationDescWrapper>();

			ArrayList<AggregationDescWrapper> parentAggregations = ((OpDesc5GBY) parent)
					.getAggregations();

			int aggrIdx = 0;
			for (Map.Entry<String, ASTNodeTRC> entry : aggregationTrees
					.entrySet()) {
				ASTNodeTRC value = entry.getValue();
				String aggName = ParseStringUtil.unescapeIdentifier(value
						.getChild(0).getText());
				ArrayList<ExprNodeDesc> aggParameters = new ArrayList<ExprNodeDesc>();

				ColumnInfoTRC paraExprInfo = inputRR.getExpression(value);

				String paraExpression = paraExprInfo.getInternalName();
				aggParameters
						.add(new ExprNodeColumnDesc(paraExprInfo.getType(),
								paraExpression, paraExprInfo.getTabAlias(),
								paraExprInfo.getIsVirtualCol()));

				GenericUDAFEvaluator genericUDAFEvaluator;
				try {
					genericUDAFEvaluator = parentAggregations.get(aggrIdx)
							.getGenericUDAFEvaluatorClass().newInstance();

					GenericUDAFInfo udaf = getGenericUDAFInfo(
							genericUDAFEvaluator,
							GenericUDAFEvaluator.Mode.FINAL, aggParameters);

					AggrMode aggrMode = getAggrMode(value);
					aggregations.add(new AggregationDescWrapper(aggName
							.toLowerCase(), udaf.genericUDAFEvaluator
							.getClass(), udaf.convertedParameters, false,
							GenericUDAFEvaluator.Mode.FINAL, aggrMode));

					String columnName = "internalColumn-" + (idx++);
					TypeInfo type = udaf.returnType;

					outputColumnNames.add(columnName);
					outputTypes.add(type);

					outputRR.putExpression(value, new ColumnInfoTRC(columnName,
							type, "", false));
				} catch (Exception e) {
					e.printStackTrace();
				}
				aggrIdx++;

			}

			opd.setGroupByKeys(groupByKeys);

			opd.setAggregations(aggregations);

			opd.setOutputColumnNames(outputColumnNames);

			opd.setOutputType((StructTypeInfo) TypeInfoFactory
					.getStructTypeInfo(outputColumnNames, outputTypes));
			op2RR.put(opd, outputRR);
			return outputRR;
		}
	}

	static class GenericUDAFInfo {
		ArrayList<ExprNodeDesc> convertedParameters;
		GenericUDAFEvaluator genericUDAFEvaluator;
		TypeInfo returnType;
	}

	static GenericUDAFInfo getGenericUDAFInfo(GenericUDAFEvaluator evaluator,
			GenericUDAFEvaluator.Mode mode,
			ArrayList<ExprNodeDesc> aggParameters) throws Exception {

		GenericUDAFInfo r = new GenericUDAFInfo();

		// set r.genericUDAFEvaluator
		r.genericUDAFEvaluator = evaluator;

		// set r.returnType
		ObjectInspector returnOI = null;
		ArrayList<ObjectInspector> aggOIs = getWritableObjectInspector(aggParameters);
		ObjectInspector[] aggOIArray = new ObjectInspector[aggOIs.size()];
		for (int ii = 0; ii < aggOIs.size(); ++ii) {
			aggOIArray[ii] = aggOIs.get(ii);
		}
		returnOI = r.genericUDAFEvaluator.init(mode, aggOIArray);
		r.returnType = TypeInfoUtils.getTypeInfoFromObjectInspector(returnOI);
		r.convertedParameters = aggParameters;

		return r;
	}

	static GenericUDAFEvaluator getGenericUDAFEvaluator(String aggName,
			ArrayList<ExprNodeDesc> aggParameters, ASTNodeTRC aggTree,
			boolean isDistinct, boolean isAllColumns) throws Exception {
		ArrayList<ObjectInspector> originalParameterTypeInfos = getWritableObjectInspector(aggParameters);
		GenericUDAFEvaluator result = null;
		result = FunctionRegistry.getGenericUDAFEvaluator(aggName,
				originalParameterTypeInfos, isDistinct, isAllColumns);
		return result;
	}

	static ArrayList<ObjectInspector> getWritableObjectInspector(
			ArrayList<ExprNodeDesc> exprs) {
		ArrayList<ObjectInspector> result = new ArrayList<ObjectInspector>();
		for (ExprNodeDesc expr : exprs) {
			result.add(expr.getWritableObjectInspector());
		}
		return result;
	}

	private class OpDescBuilder6SEL implements OpDescBuilder {
		@Override
		public RowResolverTRC process(Node nd, Stack<Node> stack,
				ArrayList<RowResolverTRC> nodeOutputs,
				HashMap<Node, RowResolverTRC> retMap) throws Exception {
			OpDesc6SEL opd = (OpDesc6SEL) nd;
			RowResolverTRC inputRR = op2RR.get(stack.get(stack.size() - 2));
			Query q = op2Querys.get(opd);
			String qAlias = qb.getAliasByAstNode(q.getN());
			ASTNodeTRC selExpr = q.getSelectExpr();

			RowResolverTRC outputRR = new RowResolverTRC();

			ArrayList<ExprNodeDesc> col_list = new ArrayList<ExprNodeDesc>();
			ArrayList<String> outputColumnNames = new ArrayList<String>();
			ArrayList<TypeInfo> outputTypes = new ArrayList<TypeInfo>();

			int pos = 0;
			int expandIdx = -1;
			for (int i = 0; i < selExpr.getChildCount(); i++) {
				boolean isExpand = false;

				// child can be EXPR AS ALIAS, or EXPR.
				ASTNodeTRC child = (ASTNodeTRC) selExpr.getChild(i);
				// boolean hasAsClause = child.getChildCount() == 2;
				// The real expression
				ASTNodeTRC expr = (ASTNodeTRC) child.getChild(0);

				// Case when this is an expression
				// TypeCheckCtxTRC tcCtx = new TypeCheckCtxTRC(inputRR);
				// ExprNodeDesc exp = LogicalPlanExprUtils.getExprNodeDesc(expr,
				// tcCtx);

				ExprNodeDesc exp = getExprNodeDescCached(expr, inputRR);
				col_list.add(exp);
				if (exp == null) {
					throw new RuntimeException("expr : " + expr.toStringTree()
							+ " can not be resolved !");
				}

				String[] colRef = ParseStringUtil.getColAlias(child, "",
						inputRR, true, i);

				String colAlias = colRef[1];

				if (child.getChildCount() == 2) {
					if (((ASTNodeTRC) child.getChild(1)).getToken().getType() == TrcParser.KW_EXPAND) {
						if (expandIdx >= 0) {
							throw new RuntimeException(
									"there should be no more than 1 expand expr in select subclause : "
											+ child.toStringTree());
						} else if (exp.getTypeInfo().getCategory() != Category.LIST) {
							throw new RuntimeException(
									"only list type column can be expand : "
											+ child.toStringTree());
						} else {
							isExpand = true;
							expandIdx = i;
						}
					} else {
						colAlias = ParseStringUtil.unescapeIdentifier(child
								.getChild(1).getText());
					}
				} else if (child.getChildCount() == 3) {
					colAlias = ParseStringUtil.unescapeIdentifier(child
							.getChild(1).getText());
					if (((ASTNodeTRC) child.getChild(2)).getToken().getType() == TrcParser.KW_EXPAND) {
						if (expandIdx >= 0) {
							throw new RuntimeException(
									"there should be no more than 1 expand expr in select subclause : "
											+ child.toStringTree());
						} else if (exp.getTypeInfo().getCategory() != Category.LIST) {
							throw new RuntimeException(
									"only list type column can be expand : "
											+ child.toStringTree());
						} else {
							isExpand = true;
							expandIdx = i;
						}
					}
				}

				qAlias = qAlias == null ? "" : qAlias;

				String columnName = "internalColumn-" + (pos++);
				outputColumnNames.add(columnName);

				ObjectInspector selOI = exp.getWritableObjectInspector();
				if (isExpand) {
					selOI = ((ListObjectInspector) selOI)
							.getListElementObjectInspector();
				}
				ColumnInfoTRC outputColumnInfo = new ColumnInfoTRC(columnName,
						selOI, qAlias, false);
				outputRR.put(qAlias, colAlias, outputColumnInfo);

				outputTypes.add(outputColumnInfo.getType());

			}

			// process for FS child key or attrs
			if (opd.getChildren().size() == 1) {
				OpDesc childdesc = (OpDesc) opd.getChildren().get(0);
				if (childdesc.getName().equals("FS")) {
					if (q.getDestKeyExpr() != null) {
						ExprNodeDesc keyExpr = getExprNodeDescCached(
								q.getDestKeyExpr(), inputRR);
						if (keyExpr == null) {
							throw new RuntimeException(
									"can not get key Desc for : "
											+ q.getDestKeyExpr().toStringTree()
											+ " : " + inputRR);
						}
						opd.setKeyExpr(keyExpr);
					}

					if (q.getDestAttrsExpr() != null) {
						ExprNodeDesc attrsExpr = getExprNodeDescCached(
								q.getDestAttrsExpr(), inputRR);
						if (attrsExpr == null) {
							throw new RuntimeException(
									"can not get attrs Desc for : "
											+ q.getDestAttrsExpr()
													.toStringTree());
						}
						opd.setAttrsExpr(attrsExpr);
					}
				}
			}

			opd.setExpandIdx(expandIdx);
			opd.setOutputColumnNames(outputColumnNames);
			opd.setOutputType((StructTypeInfo) TypeInfoFactory
					.getStructTypeInfo(outputColumnNames, outputTypes));
			opd.setColList(col_list);
			op2RR.put(opd, outputRR);
			return outputRR;
		}
	}

	private class OpDescBuilder7FS implements OpDescBuilder {
		@Override
		public RowResolverTRC process(Node nd, Stack<Node> stack,
				ArrayList<RowResolverTRC> nodeOutputs,
				HashMap<Node, RowResolverTRC> retMap) throws Exception {
			OpDesc7FS opd = (OpDesc7FS) nd;

			// parent of FS must be SEL
			OpDesc6SEL parent = (OpDesc6SEL) stack.get(stack.size() - 2);
			RowResolverTRC inputRR = op2RR.get(parent);

			Query query = op2Querys.get(opd);
			String tableId = query.getDestTableId();
			Table table = md.getTable(tableId);
			opd.setTable(table);

			// if (query.getDestKeyExpr() != null) {
			// ExprNodeDesc keyExpr = getExprNodeDescCached(
			// query.getDestKeyExpr(), inputRR);
			// if (keyExpr == null) {
			// throw new RuntimeException("can not get key Desc for : "
			// + query.getDestKeyExpr().toStringTree() + " : "
			// + inputRR);
			// }
			// opd.setKeyExpr(keyExpr);
			// } else {
			// if (table.getTableType() == TableType.tde
			// || table.getTableType() == TableType.redis
			// /* key not used for hbase table right now TODO */
			// // || table.getTableType() == TableType.hbase
			// ) {
			// throw new RuntimeException(
			// "there must contations key expr after tde or redis table "
			// + tableId);
			// }
			// }
			//
			// if (query.getDestAttrsExpr() != null) {
			// ExprNodeDesc attrsExpr = getExprNodeDescCached(
			// query.getDestAttrsExpr(), inputRR);
			// if (attrsExpr == null) {
			// throw new RuntimeException("can not get attrs Desc for : "
			// + query.getDestAttrsExpr().toStringTree());
			// }
			// opd.setAttrsExpr(attrsExpr);
			// }

			if (query.getDestKeyExpr() != null) {
				opd.setContainsKeyExpr(true);
			}

			if (query.getDestAttrsExpr() != null) {
				opd.setContainsAttrsExpr(true);
			}

			TypeInfo fsType = parent.getOutputType();
			if (table.getTableType() == TableType.tpg) {
				if (!checkFieldType(table, table.getStructTypes(),
						(StructTypeInfo) fsType)) {
					throw new RuntimeException("FS type wrong !");
				}
			}

			opd.setOutputType(fsType);
			op2RR.put(opd, inputRR);
			return inputRR;
		}

		private boolean checkFieldType(Table table,
				ArrayList<TypeInfo> structTypes, StructTypeInfo fsType) {
			ArrayList<TypeInfo> thistypes = fsType.getAllStructFieldTypeInfos();
			if (structTypes.size() == thistypes.size()) {
				for (int i = 0; i < structTypes.size(); i++) {
					if (!structTypes.get(i).equals(thistypes.get(i))) {
						System.err
								.println("error ::: FS type must equal to table defined type : "
										+ structTypes.get(i)
										+ " "
										+ thistypes.get(i) + " " + table);
						return false;
					}
				}
			} else {
				throw new RuntimeException(
						"FS type size must equal to table defined type size : "
								+ structTypes.size() + " " + thistypes.size());
			}
			return true;
		}
	}
}
