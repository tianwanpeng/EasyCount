package com.tencent.easycount.plan.logical;

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

import com.tencent.easycount.metastore.Field;
import com.tencent.easycount.metastore.MetaData;
import com.tencent.easycount.metastore.Table;
import com.tencent.easycount.metastore.Table.TableType;
import com.tencent.easycount.parse.ASTNodeTRC;
import com.tencent.easycount.parse.ParseStringUtil;
import com.tencent.easycount.parse.QB;
import com.tencent.easycount.parse.Query;
import com.tencent.easycount.parse.Query.QueryMode;
import com.tencent.easycount.parse.TrcParser;
import com.tencent.easycount.plan.AggregationDescWrapper;
import com.tencent.easycount.plan.AggregationDescWrapper.AggrMode;
import com.tencent.easycount.plan.ColumnInfoEC;
import com.tencent.easycount.plan.RowResolverEC;
import com.tencent.easycount.plan.RowSchemaEC;
import com.tencent.easycount.plan.TypeCheckCtxEC;
import com.tencent.easycount.plan.logical.JoinUtils.Condition;
import com.tencent.easycount.util.constants.Constants;
import com.tencent.easycount.util.graph.GraphPrinter;
import com.tencent.easycount.util.graph.GraphWalker;
import com.tencent.easycount.util.graph.GraphWalker.Dispatcher;
import com.tencent.easycount.util.graph.GraphWalker.Node;
import com.tencent.easycount.util.graph.GraphWalker.WalkMode;

public class LogicalPlanGenerator {
	@SuppressWarnings("unused")
	final private ASTNodeTRC tree;
	final private QB qb;
	final private MetaData md;

	final private HashMap<OpDesc, Query> op2Querys;
	final private HashMap<OpDesc, RowResolverEC> op2RR;

	public LogicalPlanGenerator(final ASTNodeTRC tree, final QB qb,
			final MetaData md) {
		this.tree = tree;
		this.qb = qb;
		this.md = md;

		this.op2Querys = new HashMap<OpDesc, Query>();
		this.op2RR = new HashMap<OpDesc, RowResolverEC>();
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
		 * just generate the op tree from query tree in qb, and op2query
		 */

		final HashMap<Node, QueryOpDescTree> queryToQueryTree = new GraphWalker<QueryOpDescTree>(
				new GenerateOpTreeFromQueryTreeDispatcher(),
				WalkMode.CHILD_FIRST, "generateQBTree").walk(this.qb
				.getRootQueryNodes());

		/**
		 * get the root ops, all the root must be ts opdesc
		 */
		final ArrayList<OpDesc> rootOps = new ArrayList<OpDesc>();
		final ArrayList<Node> rootOpNodes = new ArrayList<Node>();
		for (final Node rootq : this.qb.getRootQueryNodes()) {
			final OpDesc rootOp = queryToQueryTree.get(rootq).getRoot();
			rootOps.add(rootOp);
			rootOpNodes.add(rootOp);
		}

		GraphPrinter.print(rootOpNodes, null);

		/**
		 * build each opdesc
		 *
		 * 通过上一步walk已经得到了optree，但是内容是空的，这一步的主要目的就是对每个opdesc进行填充。
		 * 这要从root节点（通常都是ts）开始对所有的子节点进行递归处理和填充
		 *
		 */
		new GraphWalker<RowResolverEC>(new BuildOpDescInfoDispatcher(),
				WalkMode.ROOT_FIRST_RECURSIVE, "generateLogicalPlan")
				.walk(rootOpNodes);
		final ArrayList<Node> rootOpsp = new ArrayList<>();
		for (final Node n : rootOps) {
			rootOpsp.add(n);
		}
		GraphPrinter.print(rootOpsp, null);

		return new LogicalPlan(queryToQueryTree, rootOps);
	}

	/**
	 * a query contains a opdesc link list from root to dest
	 *
	 * @author steven
	 *
	 */
	static class QueryOpDescTree {
		final private OpDesc root;
		final private OpDesc dest;

		public QueryOpDescTree(final OpDesc root, final OpDesc dest) {
			this.root = root;
			this.dest = dest;
		}

		public OpDesc getRoot() {
			return this.root;
		}

		public OpDesc getDest() {
			return this.dest;
		}
	}

	/**
	 * this dispatcher do two things
	 *
	 * 1. generate the opdesctree for every query, and connect all the opdesc as
	 * a optree
	 *
	 * 2. generate op2Querys map for next stage
	 *
	 * @author steven
	 *
	 */
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
		public QueryOpDescTree dispatch(final Node nd, final Stack<Node> stack,
				final ArrayList<QueryOpDescTree> nodeOutputs,
				final HashMap<Node, QueryOpDescTree> retMap) {
			final Query q = (Query) nd;
			// 对每个query生成opdesctree
			final QueryOpDescTree qOpDescTree = generateOpDesc(q);
			// 填充op2Querys
			appendToOpQuery(qOpDescTree, q);
			// 把所有的子节点query的optree和父节点optree进行连接
			connect(qOpDescTree, nodeOutputs);
			return qOpDescTree;
		}

		@Override
		public boolean needToDispatchChildren(final Node nd,
				final Stack<Node> stack,
				final ArrayList<QueryOpDescTree> nodeOutputs,
				final HashMap<Node, QueryOpDescTree> retMap) {
			return true;
		}

		private void appendToOpQuery(final QueryOpDescTree qOpDescTree,
				final Query q) {
			OpDesc opd = qOpDescTree.root;
			do {
				LogicalPlanGenerator.this.op2Querys.put(opd, q);
				opd = opd.getChildren().size() > 0 ? (OpDesc) opd.getChildren()
						.get(0) : null;
			} while (opd != null);
		}

		private void connect(final QueryOpDescTree qOpDescTree,
				final ArrayList<QueryOpDescTree> nodeOutputs) {
			for (final QueryOpDescTree subTree : nodeOutputs) {
				qOpDescTree.getDest().addChild(subTree.getRoot());
			}
		}

		/**
		 * 这里仅生成每个query对应的算子树opdesctree，对于每个opdesc，这里并不深入填充，需要在下一个stage进行
		 *
		 * @param q
		 * @return
		 */
		private QueryOpDescTree generateOpDesc(final Query q) {
			// 对于table和union两种情况来说，只是简单的生成对应的opdesc即可
			// 这两种查询对应的opdesctree，只有一个op，既是root又是dest
			// 但是对于其他复杂的查询情况来说就必须考虑aggr，where等op了
			if (q.getQmode() == QueryMode.table) {
				final OpDesc opdesc = new OpDesc1TS();
				return new QueryOpDescTree(opdesc, opdesc);
			}
			if (q.getQmode() == QueryMode.union) {
				final OpDesc opdesc = new OpDesc4UNION();
				return new QueryOpDescTree(opdesc, opdesc);
			}

			// 对于一般的query，首先应该考虑join，因为这里只支持left join，是一种查询的方式
			// 但是对于有的时候where条件作用于stream table时，where应该前置的，是否需要进一步优化？？？？ TODO 待确认
			OpDesc rootop = null;
			OpDesc destop = null;
			if (q.getJoinOnExpr() != null) {
				rootop = new OpDesc3JOIN();
				destop = rootop;
			}

			// 除了join以外，所有的过滤条件应该优先考虑
			if (q.getWhereExpr() != null) {
				final OpDesc op = new OpDesc2FIL(false);
				if (rootop == null) {
					rootop = op;
					destop = rootop;
				} else {
					destop.addChild(op);
					destop = op;
				}
			}

			// 接着考虑聚合计算，这里会包括map-gby和reduce-gby两个算子。
			if (q.containsAggr()) {
				final OpDesc op = new OpDesc5GBY(true);
				if (rootop == null) {
					rootop = op;
					destop = rootop;
				} else {
					destop.addChild(op);
					destop = op;
				}

				final OpDesc gop = new OpDesc5GBY(false);
				op.addChild(gop);

				destop = gop;
			}

			// 考虑having过滤算子
			if (q.getHavingExpr() != null) {
				final OpDesc op = new OpDesc2FIL(true);
				if (rootop == null) {
					rootop = op;
					destop = rootop;
				} else {
					destop.addChild(op);
					destop = op;
				}
			}

			// 倒数第二考虑select算子
			final OpDesc sop = new OpDesc6SEL();

			if (rootop == null) {
				rootop = sop;
				destop = sop;
			} else {
				destop.addChild(sop);
				destop = sop;
			}

			// 最后考虑insert算子
			// 一般的子查询和tablescan是不会包含这个算子的
			if (q.getQmode() == QueryMode.insertq) {
				final OpDesc fop = new OpDesc7FS();
				destop.addChild(fop);
				destop = fop;
			}

			return new QueryOpDescTree(rootop, destop);
		}
	}

	/**
	 * 主要是为了填充每个opdesc，op2RR的作用是一个中间变量
	 *
	 * @author steven
	 *
	 */
	private class BuildOpDescInfoDispatcher implements
	Dispatcher<RowResolverEC> {
		final private HashMap<Class<? extends OpDesc>, OpDescBuilder> opDescBuilders;

		public BuildOpDescInfoDispatcher() {
			this.opDescBuilders = new HashMap<Class<? extends OpDesc>, OpDescBuilder>();
			this.opDescBuilders.put(OpDesc1TS.class, new OpDescBuilder1TS());
			this.opDescBuilders.put(OpDesc2FIL.class, new OpDescBuilder2FIL());
			this.opDescBuilders
					.put(OpDesc3JOIN.class, new OpDescBuilder3JOIN());
			this.opDescBuilders.put(OpDesc4UNION.class,
					new OpDescBuilder4UNION());
			this.opDescBuilders.put(OpDesc5GBY.class, new OpDescBuilder5GBY());
			this.opDescBuilders.put(OpDesc6SEL.class, new OpDescBuilder6SEL());
			this.opDescBuilders.put(OpDesc7FS.class, new OpDescBuilder7FS());
		}

		/**
		 * dispatcher 的结果是一个op到rowresolver的map
		 */
		@Override
		public RowResolverEC dispatch(final Node nd, final Stack<Node> stack,
				final ArrayList<RowResolverEC> nodeOutputs,
				final HashMap<Node, RowResolverEC> retMap) throws Exception {
			final RowResolverEC rr = this.opDescBuilders.get(nd.getClass())
					.process(nd, stack, nodeOutputs, retMap);
			if (rr != null) {
				System.out.println("nd2RR : " + nd.getName() + " " + rr);
			}
			return rr;
		}

		@Override
		public boolean needToDispatchChildren(final Node nd,
				final Stack<Node> stack,
				final ArrayList<RowResolverEC> nodeOutputs,
				final HashMap<Node, RowResolverEC> retMap) {
			return retMap.get(nd) != null;
		}
	}

	private interface OpDescBuilder {
		public RowResolverEC process(Node nd, Stack<Node> stack,
				ArrayList<RowResolverEC> nodeOutputs,
				HashMap<Node, RowResolverEC> retMap) throws Exception;
	}

	/**
	 * ts 一般都是第一个被处理的
	 *
	 * @author steven
	 *
	 */
	private class OpDescBuilder1TS implements OpDescBuilder {

		@Override
		public RowResolverEC process(final Node nd, final Stack<Node> stack,
				final ArrayList<RowResolverEC> nodeOutputs,
				final HashMap<Node, RowResolverEC> retMap) {
			final OpDesc1TS opd = (OpDesc1TS) nd;

			final Query query = LogicalPlanGenerator.this.op2Querys.get(opd);

			// 表名只能从op对应的query中获取
			// 一个query最多只能有一个输入表
			final Table table = LogicalPlanGenerator.this.md.getTable(query
					.getTableId());
			// 得到正确的别名alias
			final String tableAlias = LogicalPlanGenerator.this.qb
					.getAliasByAstNode(query.getN());
			opd.setTable(table);
			opd.setDimensionTable(query.isDimensionTable());

			final RowResolverEC outputRR = new RowResolverEC();
			final ArrayList<String> outputColumnNames = new ArrayList<String>();
			final ArrayList<TypeInfo> outputTypes = new ArrayList<TypeInfo>();
			if ((table.getTableType() == TableType.stream)
					|| (table.getTableType() == TableType.kafka)) {
				// 对于流水表，数据组成除了记录本身以外可能还包含一些数据描述信息，
				// 这些信息以map的形式放在了attr里面，也可以看做是一些输出字段
				outputColumnNames.add(Constants.dataAttrs);
				final TypeInfo attrtype = TypeInfoFactory.getMapTypeInfo(
						TypeInfoFactory.stringTypeInfo,
						TypeInfoFactory.stringTypeInfo);
				outputTypes.add(attrtype);
				outputRR.put(tableAlias, Constants.dataAttrs,
						new ColumnInfoEC(Constants.dataAttrs, attrtype,
								tableAlias, false));
			}
			for (final Field field : table.getFields()) {
				final ColumnInfoEC colInfo = new ColumnInfoEC(
						field.getColumnName(), field.getType(), tableAlias,
						false);
				outputRR.put(tableAlias, field.getColumnName(), colInfo);
				for (final String alias : query.getAliases()) {
					outputRR.put(alias, field.getColumnName(), colInfo);
				}
				outputColumnNames.add(field.getColumnName());
				outputTypes.add(field.getType());
			}

			opd.setOutputColumnNames(outputColumnNames);
			opd.setOutputType(TypeInfoFactory.getStructTypeInfo(
					outputColumnNames, outputTypes));

			// 最后将当前op的输出rr填充到op2RR，供子节点op使用
			LogicalPlanGenerator.this.op2RR.put(opd, outputRR);
			return outputRR;
		}
	}

	private class OpDescBuilder2FIL implements OpDescBuilder {

		@Override
		public RowResolverEC process(final Node nd, final Stack<Node> stack,
				final ArrayList<RowResolverEC> nodeOutputs,
				final HashMap<Node, RowResolverEC> retMap) throws Exception {
			final OpDesc2FIL opd = (OpDesc2FIL) nd;
			final OpDesc parent = (OpDesc) stack.get(stack.size() - 2);
			final RowResolverEC outputRR = LogicalPlanGenerator.this.op2RR
					.get(parent);

			final boolean having = "HAVING".equals(nd.getName());
			final Query q = LogicalPlanGenerator.this.op2Querys.get(opd);

			final ExprNodeDesc predicate = getExprNodeDescCached(
					having ? q.getHavingExpr() : q.getWhereExpr(), outputRR);

			opd.setPredicate(predicate);
			opd.setOutputType(parent.getOutputType());
			LogicalPlanGenerator.this.op2RR.put(opd, outputRR);
			return outputRR;
		}
	}

	private ExprNodeDesc getExprNodeDescCached(final ASTNodeTRC expr,
			final RowResolverEC input) throws Exception {
		final ColumnInfoEC colInfo = input.getExpression(expr);
		if (colInfo != null) {
			return new ExprNodeColumnDesc(colInfo.getType(),
					colInfo.getInternalName(), colInfo.getTabAlias(),
					colInfo.getIsVirtualCol(), colInfo.isSkewedCol());
		}
		final ExprNodeDesc res = LogicalPlanExprUtils.getExprNodeDesc(expr,
				new TypeCheckCtxEC(input));
		if (res == null) {
			System.err.println("warned : " + expr.toStringTree()
					+ " from input : " + input + " is null");
		}
		return res;
	}

	private class OpDescBuilder3JOIN implements OpDescBuilder {
		@Override
		public RowResolverEC process(final Node nd, final Stack<Node> stack,
				final ArrayList<RowResolverEC> nodeOutputs,
				final HashMap<Node, RowResolverEC> retMap) throws Exception {

			final OpDesc3JOIN opd = (OpDesc3JOIN) nd;
			for (final OpDesc partent : opd.parentOps()) {
				if (LogicalPlanGenerator.this.op2RR.get(partent) == null) {
					// if any of its parent not processed, just ignore it
					return null;
				}
			}

			final Query query = LogicalPlanGenerator.this.op2Querys.get(nd);

			final ArrayList<String> joinDTableAliass = query
					.getJoinDTableAliass();

			final HashMap<String, Table> joinDTables = new HashMap<String, Table>();
			for (final String alias : joinDTableAliass) {
				final Table tbl = LogicalPlanGenerator.this.md
						.getTable(LogicalPlanGenerator.this.qb.getQueryByAlias(
								alias).getTableId());
				joinDTables.put(alias, tbl);
			}
			opd.setAlias2JoinTables(joinDTables);

			final String streamAlias = LogicalPlanGenerator.this.qb
					.getAliasByAstNode(query.getJoinStreamQuery().getN());
			opd.setStreamAlias(streamAlias);

			// alias is the dimension table alias, and the ASTNodeTRC is the
			// expr ast of the stream table, that means use the computed result
			// of the expr on stream table as the key to query the <K,V>
			// dimension table
			final HashMap<String, Condition> alias2KeyAsts = new HashMap<String, Condition>();
			JoinUtils.generateJoinTree(query.getJoinOnExpr(), streamAlias,
					joinDTableAliass, alias2KeyAsts);
			// System.out.println("alias2KeyAsts : " + alias2KeyAsts);

			final HashMap<String, RowResolverEC> alias2InputRRs = new HashMap<String, RowResolverEC>();
			// for (OpDesc partent : opd.parentOps()) {
			// String alias = qb.getAliasByAstNode(op2Querys.get(partent)
			// .getN());
			// alias2InputRRs.put(alias, op2RR.get(partent));
			// }
			for (final OpDesc partent : opd.parentOps()) {
				for (final String alias : LogicalPlanGenerator.this.op2Querys
						.get(partent).getAliases()) {
					alias2InputRRs.put(alias,
							LogicalPlanGenerator.this.op2RR.get(partent));
				}
			}

			final HashMap<String, ExprNodeDesc> table2KeyExprs = new HashMap<String, ExprNodeDesc>();
			final RowResolverEC streamRR = alias2InputRRs.get(streamAlias);
			for (final String alias : alias2KeyAsts.keySet()) {
				final Condition condition = alias2KeyAsts.get(alias);
				final RowResolverEC rr = alias2InputRRs.get(alias);
				final ExprNodeDesc leftNodeDesc = getExprNodeDescCached(
						condition.leftAst, condition.leftIsStream ? streamRR
								: rr);
				final ExprNodeDesc rightNodeDesc = getExprNodeDescCached(
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

			final RowResolverEC outputRR = new RowResolverEC();
			final ArrayList<String> outputColumnNames = new ArrayList<String>();

			final HashMap<String, ArrayList<ExprNodeDesc>> alias2ExprDesc = new HashMap<String, ArrayList<ExprNodeDesc>>();
			final ArrayList<TypeInfo> outputTypes = new ArrayList<TypeInfo>();

			int outputPos = 0;
			final ArrayList<ExprNodeDesc> keyDesc1 = new ArrayList<ExprNodeDesc>();
			final HashMap<String, ColumnInfoEC> colalias2ColumnInfo1 = streamRR
					.getFieldMap(streamAlias);
			for (final String colalias : colalias2ColumnInfo1.keySet()) {
				final ColumnInfoEC valueInfo = colalias2ColumnInfo1
						.get(colalias);
				keyDesc1.add(new ExprNodeColumnDesc(valueInfo.getType(),
						valueInfo.getInternalName(), valueInfo.getTabAlias(),
						valueInfo.getIsVirtualCol()));
				final String colName = "internalColumn-" + outputPos;
				outputPos++;
				outputColumnNames.add(colName);
				outputRR.put(streamAlias, colalias,
						new ColumnInfoEC(colName, valueInfo.getType(),
								streamAlias, valueInfo.getIsVirtualCol(),
								valueInfo.isHiddenVirtualCol()));
				outputTypes.add(valueInfo.getType());
			}
			alias2ExprDesc.put(streamAlias, keyDesc1);

			for (final String tabAlias : joinDTableAliass) {
				final ArrayList<ExprNodeDesc> keyDesc = new ArrayList<ExprNodeDesc>();
				final HashMap<String, ColumnInfoEC> colalias2ColumnInfo = alias2InputRRs
						.get(tabAlias).getFieldMap(tabAlias);
				for (final String colalias : colalias2ColumnInfo.keySet()) {
					final ColumnInfoEC valueInfo = colalias2ColumnInfo
							.get(colalias);
					keyDesc.add(new ExprNodeColumnDesc(valueInfo.getType(),
							valueInfo.getInternalName(), valueInfo
							.getTabAlias(), valueInfo.getIsVirtualCol()));
					final String colName = "internalColumn-" + outputPos;
					outputPos++;
					outputColumnNames.add(colName);
					outputRR.put(tabAlias, colalias,
							new ColumnInfoEC(colName, valueInfo.getType(),
									tabAlias, valueInfo.getIsVirtualCol(),
									valueInfo.isHiddenVirtualCol()));
					outputTypes.add(valueInfo.getType());
				}
				alias2ExprDesc.put(tabAlias, keyDesc);

			}

			opd.setAlias2ExprDesc(alias2ExprDesc);

			opd.setOutputColumnNames(outputColumnNames);

			opd.setOutputType(TypeInfoFactory.getStructTypeInfo(
					outputColumnNames, outputTypes));

			LogicalPlanGenerator.this.op2RR.put((OpDesc) nd, outputRR);
			return outputRR;
		}
	}

	private class OpDescBuilder4UNION implements OpDescBuilder {

		@Override
		public RowResolverEC process(final Node nd, final Stack<Node> stack,
				final ArrayList<RowResolverEC> nodeOutputs,
				final HashMap<Node, RowResolverEC> retMap) {

			final OpDesc4UNION opd = (OpDesc4UNION) nd;

			for (final OpDesc partent : opd.parentOps()) {
				if (LogicalPlanGenerator.this.op2RR.get(partent) == null) {
					// if any of its parent not processed, just ignore it
					return null;
				}
			}

			final Query q = LogicalPlanGenerator.this.op2Querys.get(nd);
			final String qAlias = LogicalPlanGenerator.this.qb
					.getAliasByAstNode(q.getN());
			final RowResolverEC outputRR = new RowResolverEC();
			final ArrayList<String> outputColumnNames = new ArrayList<String>();
			final ArrayList<TypeInfo> outputTypes = new ArrayList<TypeInfo>();

			final ArrayList<RowSchemaEC> parentSchemas = new ArrayList<RowSchemaEC>();
			for (final OpDesc partent : opd.parentOps()) {
				final RowResolverEC rr = LogicalPlanGenerator.this.op2RR
						.get(partent);
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

			final OpDesc fistParent = opd.parentOps().iterator().next();
			final RowResolverEC firstParentRR = LogicalPlanGenerator.this.op2RR
					.get(fistParent);
			final String firstAlias = LogicalPlanGenerator.this.qb
					.getAliasByAstNode(LogicalPlanGenerator.this.op2Querys.get(
							fistParent).getN());
			int pos = 0;
			for (final Map.Entry<String, ColumnInfoEC> lEntry : firstParentRR
					.getFieldMap(firstAlias).entrySet()) {
				final ColumnInfoEC unionColInfo = new ColumnInfoEC(
						lEntry.getValue());

				final ArrayList<TypeInfo> columnInfoTRCs = new ArrayList<TypeInfo>();
				for (final RowSchemaEC rs : parentSchemas) {
					columnInfoTRCs.add(rs.getSignature().get(pos).getType());
				}
				final TypeInfo type = getCommonTypeInfoTRC(columnInfoTRCs);
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

			opd.setOutputType(TypeInfoFactory.getStructTypeInfo(
					outputColumnNames, outputTypes));

			LogicalPlanGenerator.this.op2RR.put(opd, outputRR);

			return outputRR;

		}

		private TypeInfo getCommonTypeInfoTRC(final ArrayList<TypeInfo> types) {
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
		public RowResolverEC process(final Node nd, final Stack<Node> stack,
				final ArrayList<RowResolverEC> nodeOutputs,
				final HashMap<Node, RowResolverEC> retMap) throws Exception {
			final OpDesc5GBY opd = (OpDesc5GBY) nd;
			final boolean mapMode = "MGBY".equals(opd.getName());

			final OpDesc parent = (OpDesc) stack.get(stack.size() - 2);
			final RowResolverEC inputRR = LogicalPlanGenerator.this.op2RR
					.get(parent);

			// System.err.println(op2Querys);
			final ASTNodeTRC coordinateAst = LogicalPlanGenerator.this.op2Querys
					.get(opd).getCoordinateExpr();
			if ((coordinateAst != null) && (coordinateAst.getChildCount() > 0)) {
				for (int i = 0; i < coordinateAst.getChildCount(); i++) {
					final ASTNodeTRC astInfo = (ASTNodeTRC) coordinateAst
							.getChild(i);
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

		private RowResolverEC processMGBY(final Node nd,
				final Stack<Node> stack,
				final ArrayList<RowResolverEC> nodeOutputs,
				final HashMap<Node, RowResolverEC> retMap) throws Exception {
			final OpDesc5GBY opd = (OpDesc5GBY) nd;
			final OpDesc parent = (OpDesc) stack.get(stack.size() - 2);
			final RowResolverEC inputRR = LogicalPlanGenerator.this.op2RR
					.get(parent);
			final RowResolverEC outputRR = new RowResolverEC();
			outputRR.setIsExprResolver(true);

			final Query query = LogicalPlanGenerator.this.op2Querys.get(opd);

			final boolean isAccuGlobal = opd.isAccuGlobal();

			final ArrayList<ASTNodeTRC> grpByExprs = query.getGroupExprs();

			final ArrayList<ExprNodeDesc> groupByKeys = new ArrayList<ExprNodeDesc>();

			final ArrayList<String> outputColumnNames = new ArrayList<String>();

			final ArrayList<TypeInfo> outputTypes = new ArrayList<TypeInfo>();

			final ArrayList<AggregationDescWrapper> aggregations = new ArrayList<AggregationDescWrapper>();

			int idx = 0;
			for (int i = 0; i < grpByExprs.size(); ++i) {
				final ASTNodeTRC grpbyExpr = grpByExprs.get(i);
				final ExprNodeDesc grpByExprNode = getExprNodeDescCached(
						grpbyExpr, inputRR);

				groupByKeys.add(grpByExprNode);

				final String columnName = "internalColumn-" + (idx++);
				final TypeInfo type = grpByExprNode.getTypeInfo();

				outputColumnNames.add(columnName);
				outputTypes.add(type);

				outputRR.putExpression(grpbyExpr, new ColumnInfoEC(columnName,
						type, "", false));
			}

			outputColumnNames.add(Constants.gbyAggrTupleTime);
			outputTypes.add(TypeInfoFactory.longTypeInfo);
			outputRR.put("", Constants.gbyAggrTupleTime, new ColumnInfoEC(
					Constants.gbyAggrTupleTime, TypeInfoFactory.longTypeInfo,
					"", false));

			final HashMap<String, ASTNodeTRC> aggregationTrees = query
					.getAggregationExprs();
			for (final Map.Entry<String, ASTNodeTRC> entry : aggregationTrees
					.entrySet()) {
				final ASTNodeTRC value = entry.getValue();
				final String aggName = ParseStringUtil.unescapeIdentifier(value
						.getChild(0).getText());
				final ArrayList<ExprNodeDesc> aggParameters = new ArrayList<ExprNodeDesc>();
				// 0 is the function name
				for (int i = 1; i < value.getChildCount(); i++) {
					final ASTNodeTRC paraExpr = (ASTNodeTRC) value.getChild(i);
					final ExprNodeDesc paraExprNode = LogicalPlanExprUtils
							.getExprNodeDesc(paraExpr, new TypeCheckCtxEC(
									inputRR));
					aggParameters.add(paraExprNode);
				}

				final boolean isAllColumns = value.getType() == TrcParser.TOK_FUNCTIONSTAR;

				System.err.println("processMGBY : " + aggName + " : "
						+ aggParameters + " : " + value.toStringTree());
				final GenericUDAFEvaluator genericUDAFEvaluator = getGenericUDAFEvaluator(
						aggName, aggParameters, value, false, isAllColumns);
				final GenericUDAFInfo udaf = getGenericUDAFInfo(
						genericUDAFEvaluator,
						GenericUDAFEvaluator.Mode.PARTIAL1, aggParameters);

				final AggrMode aggrMode = getAggrMode(value);
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
				final String columnName = "internalColumn-" + (idx++);
				final TypeInfo type = udaf.returnType;

				outputColumnNames.add(columnName);
				outputTypes.add(type);

				outputRR.putExpression(value, new ColumnInfoEC(columnName,
						type, "", false));
			}

			opd.setGroupByKeys(groupByKeys);

			opd.setAggregations(aggregations);

			opd.setOutputColumnNames(outputColumnNames);

			opd.setOutputType(TypeInfoFactory.getStructTypeInfo(
					outputColumnNames, outputTypes));

			LogicalPlanGenerator.this.op2RR.put(opd, outputRR);

			return outputRR;

		}

		private AggrMode getAggrMode(final ASTNodeTRC value) {
			final int funcType = value.getToken().getType();
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

		private RowResolverEC processRGBY(final Node nd,
				final Stack<Node> stack,
				final ArrayList<RowResolverEC> nodeOutputs,
				final HashMap<Node, RowResolverEC> retMap) {
			final OpDesc5GBY opd = (OpDesc5GBY) nd;
			final OpDesc parent = (OpDesc) stack.get(stack.size() - 2);
			final RowResolverEC inputRR = LogicalPlanGenerator.this.op2RR
					.get(parent);
			final RowResolverEC outputRR = new RowResolverEC();
			outputRR.setIsExprResolver(true);

			final Query query = LogicalPlanGenerator.this.op2Querys.get(opd);

			final ArrayList<ASTNodeTRC> grpByExprs = query.getGroupExprs();

			final ArrayList<ExprNodeDesc> groupByKeys = new ArrayList<ExprNodeDesc>();

			final ArrayList<String> outputColumnNames = new ArrayList<String>();

			final ArrayList<TypeInfo> outputTypes = new ArrayList<TypeInfo>();

			int idx = 0;
			for (int i = 0; i < grpByExprs.size(); ++i) {
				final ASTNodeTRC grpbyExpr = grpByExprs.get(i);
				final ColumnInfoEC exprInfo = inputRR.getExpression(grpbyExpr);

				groupByKeys.add(new ExprNodeColumnDesc(exprInfo.getType(),
						exprInfo.getInternalName(), exprInfo.getTabAlias(),
						exprInfo.getIsVirtualCol()));
				final String columnName = "internalColumn-" + (idx++);
				final TypeInfo type = exprInfo.getType();

				outputColumnNames.add(columnName);
				outputTypes.add(type);

				outputRR.putExpression(grpbyExpr, new ColumnInfoEC(columnName,
						type, "", false));
			}

			outputColumnNames.add(Constants.gbyAggrTupleTime);
			outputTypes.add(TypeInfoFactory.longTypeInfo);
			outputRR.put("", Constants.gbyAggrTupleTime, new ColumnInfoEC(
					Constants.gbyAggrTupleTime, TypeInfoFactory.longTypeInfo,
					"", false));

			final HashMap<String, ASTNodeTRC> aggregationTrees = query
					.getAggregationExprs();
			final ArrayList<AggregationDescWrapper> aggregations = new ArrayList<AggregationDescWrapper>();

			final ArrayList<AggregationDescWrapper> parentAggregations = ((OpDesc5GBY) parent)
					.getAggregations();

			int aggrIdx = 0;
			for (final Map.Entry<String, ASTNodeTRC> entry : aggregationTrees
					.entrySet()) {
				final ASTNodeTRC value = entry.getValue();
				final String aggName = ParseStringUtil.unescapeIdentifier(value
						.getChild(0).getText());
				final ArrayList<ExprNodeDesc> aggParameters = new ArrayList<ExprNodeDesc>();

				final ColumnInfoEC paraExprInfo = inputRR.getExpression(value);

				final String paraExpression = paraExprInfo.getInternalName();
				aggParameters
				.add(new ExprNodeColumnDesc(paraExprInfo.getType(),
						paraExpression, paraExprInfo.getTabAlias(),
						paraExprInfo.getIsVirtualCol()));

				GenericUDAFEvaluator genericUDAFEvaluator;
				try {
					genericUDAFEvaluator = parentAggregations.get(aggrIdx)
							.getGenericUDAFEvaluatorClass().newInstance();

					final GenericUDAFInfo udaf = getGenericUDAFInfo(
							genericUDAFEvaluator,
							GenericUDAFEvaluator.Mode.FINAL, aggParameters);

					final AggrMode aggrMode = getAggrMode(value);
					aggregations.add(new AggregationDescWrapper(aggName
							.toLowerCase(), udaf.genericUDAFEvaluator
							.getClass(), udaf.convertedParameters, false,
							GenericUDAFEvaluator.Mode.FINAL, aggrMode));

					final String columnName = "internalColumn-" + (idx++);
					final TypeInfo type = udaf.returnType;

					outputColumnNames.add(columnName);
					outputTypes.add(type);

					outputRR.putExpression(value, new ColumnInfoEC(columnName,
							type, "", false));
				} catch (final Exception e) {
					e.printStackTrace();
				}
				aggrIdx++;

			}

			opd.setGroupByKeys(groupByKeys);

			opd.setAggregations(aggregations);

			opd.setOutputColumnNames(outputColumnNames);

			opd.setOutputType(TypeInfoFactory.getStructTypeInfo(
					outputColumnNames, outputTypes));
			LogicalPlanGenerator.this.op2RR.put(opd, outputRR);
			return outputRR;
		}
	}

	static class GenericUDAFInfo {
		ArrayList<ExprNodeDesc> convertedParameters;
		GenericUDAFEvaluator genericUDAFEvaluator;
		TypeInfo returnType;
	}

	static GenericUDAFInfo getGenericUDAFInfo(
			final GenericUDAFEvaluator evaluator,
			final GenericUDAFEvaluator.Mode mode,
			final ArrayList<ExprNodeDesc> aggParameters) throws Exception {

		final GenericUDAFInfo r = new GenericUDAFInfo();

		// set r.genericUDAFEvaluator
		r.genericUDAFEvaluator = evaluator;

		// set r.returnType
		ObjectInspector returnOI = null;
		final ArrayList<ObjectInspector> aggOIs = getWritableObjectInspector(aggParameters);
		final ObjectInspector[] aggOIArray = new ObjectInspector[aggOIs.size()];
		for (int ii = 0; ii < aggOIs.size(); ++ii) {
			aggOIArray[ii] = aggOIs.get(ii);
		}
		returnOI = r.genericUDAFEvaluator.init(mode, aggOIArray);
		r.returnType = TypeInfoUtils.getTypeInfoFromObjectInspector(returnOI);
		r.convertedParameters = aggParameters;

		return r;
	}

	static GenericUDAFEvaluator getGenericUDAFEvaluator(final String aggName,
			final ArrayList<ExprNodeDesc> aggParameters,
			final ASTNodeTRC aggTree, final boolean isDistinct,
			final boolean isAllColumns) throws Exception {
		final ArrayList<ObjectInspector> originalParameterTypeInfos = getWritableObjectInspector(aggParameters);
		GenericUDAFEvaluator result = null;
		result = FunctionRegistry.getGenericUDAFEvaluator(aggName,
				originalParameterTypeInfos, isDistinct, isAllColumns);
		return result;
	}

	static ArrayList<ObjectInspector> getWritableObjectInspector(
			final ArrayList<ExprNodeDesc> exprs) {
		final ArrayList<ObjectInspector> result = new ArrayList<ObjectInspector>();
		for (final ExprNodeDesc expr : exprs) {
			result.add(expr.getWritableObjectInspector());
		}
		return result;
	}

	private class OpDescBuilder6SEL implements OpDescBuilder {
		@Override
		public RowResolverEC process(final Node nd, final Stack<Node> stack,
				final ArrayList<RowResolverEC> nodeOutputs,
				final HashMap<Node, RowResolverEC> retMap) throws Exception {
			final OpDesc6SEL opd = (OpDesc6SEL) nd;
			final RowResolverEC inputRR = LogicalPlanGenerator.this.op2RR
					.get(stack.get(stack.size() - 2));
			final Query q = LogicalPlanGenerator.this.op2Querys.get(opd);
			String qAlias = LogicalPlanGenerator.this.qb.getAliasByAstNode(q
					.getN());
			final ASTNodeTRC selExpr = q.getSelectExpr();

			final RowResolverEC outputRR = new RowResolverEC();

			final ArrayList<ExprNodeDesc> col_list = new ArrayList<ExprNodeDesc>();
			final ArrayList<String> outputColumnNames = new ArrayList<String>();
			final ArrayList<TypeInfo> outputTypes = new ArrayList<TypeInfo>();

			int pos = 0;
			int expandIdx = -1;
			for (int i = 0; i < selExpr.getChildCount(); i++) {
				boolean isExpand = false;

				// child can be EXPR AS ALIAS, or EXPR.
				final ASTNodeTRC child = (ASTNodeTRC) selExpr.getChild(i);
				// boolean hasAsClause = child.getChildCount() == 2;
				// The real expression
				final ASTNodeTRC expr = (ASTNodeTRC) child.getChild(0);

				// Case when this is an expression
				// TypeCheckCtxTRC tcCtx = new TypeCheckCtxTRC(inputRR);
				// ExprNodeDesc exp = LogicalPlanExprUtils.getExprNodeDesc(expr,
				// tcCtx);

				final ExprNodeDesc exp = getExprNodeDescCached(expr, inputRR);
				col_list.add(exp);
				if (exp == null) {
					throw new RuntimeException("expr : " + expr.toStringTree()
							+ " can not be resolved !");
				}

				final String[] colRef = ParseStringUtil.getColAlias(child, "",
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

				final String columnName = "internalColumn-" + (pos++);
				outputColumnNames.add(columnName);

				ObjectInspector selOI = exp.getWritableObjectInspector();
				if (isExpand) {
					selOI = ((ListObjectInspector) selOI)
							.getListElementObjectInspector();
				}
				final ColumnInfoEC outputColumnInfo = new ColumnInfoEC(
						columnName, selOI, qAlias, false);
				outputRR.put(qAlias, colAlias, outputColumnInfo);

				outputTypes.add(outputColumnInfo.getType());

			}

			// process for FS child key or attrs
			if (opd.getChildren().size() == 1) {
				final OpDesc childdesc = (OpDesc) opd.getChildren().get(0);
				if (childdesc.getName().equals("FS")) {
					if (q.getDestKeyExpr() != null) {
						final ExprNodeDesc keyExpr = getExprNodeDescCached(
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
						final ExprNodeDesc attrsExpr = getExprNodeDescCached(
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
			opd.setOutputType(TypeInfoFactory.getStructTypeInfo(
					outputColumnNames, outputTypes));
			opd.setColList(col_list);
			LogicalPlanGenerator.this.op2RR.put(opd, outputRR);
			return outputRR;
		}
	}

	private class OpDescBuilder7FS implements OpDescBuilder {
		@Override
		public RowResolverEC process(final Node nd, final Stack<Node> stack,
				final ArrayList<RowResolverEC> nodeOutputs,
				final HashMap<Node, RowResolverEC> retMap) throws Exception {
			final OpDesc7FS opd = (OpDesc7FS) nd;

			// parent of FS must be SEL
			final OpDesc6SEL parent = (OpDesc6SEL) stack.get(stack.size() - 2);
			final RowResolverEC inputRR = LogicalPlanGenerator.this.op2RR
					.get(parent);

			final Query query = LogicalPlanGenerator.this.op2Querys.get(opd);
			final String tableId = query.getDestTableId();
			final Table table = LogicalPlanGenerator.this.md.getTable(tableId);
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

			final TypeInfo fsType = parent.getOutputType();
			if (table.getTableType() == TableType.tpg) {
				if (!checkFieldType(table, table.getStructTypes(),
						(StructTypeInfo) fsType)) {
					throw new RuntimeException("FS type wrong !");
				}
			}

			opd.setOutputType(fsType);
			LogicalPlanGenerator.this.op2RR.put(opd, inputRR);
			return inputRR;
		}

		private boolean checkFieldType(final Table table,
				final ArrayList<TypeInfo> structTypes,
				final StructTypeInfo fsType) {
			final ArrayList<TypeInfo> thistypes = fsType
					.getAllStructFieldTypeInfos();
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
