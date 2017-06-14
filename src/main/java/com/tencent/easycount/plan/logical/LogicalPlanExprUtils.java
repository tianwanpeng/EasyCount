package com.tencent.easycount.plan.logical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.FunctionInfo;
import org.apache.hadoop.hive.ql.exec.FunctionRegistry;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.plan.ExprNodeColumnDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeColumnListDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeConstantDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeFieldDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeGenericFuncDesc;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFBaseCompare;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFOPEqual;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
import org.apache.hadoop.hive.serde2.typeinfo.ListTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.MapTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;

import com.tencent.easycount.parse.ASTNodeTRC;
import com.tencent.easycount.parse.ErrorMsg;
import com.tencent.easycount.parse.ParseStringUtil;
import com.tencent.easycount.parse.TrcParser;
import com.tencent.easycount.plan.ColumnInfoTRC;
import com.tencent.easycount.plan.ExprNodeNewAssignDesc;
import com.tencent.easycount.plan.ExprNodeNewBlockDesc;
import com.tencent.easycount.plan.ExprNodeNewColumnRefDesc;
import com.tencent.easycount.plan.ExprNodeNewColumnRefDesc.RefMode;
import com.tencent.easycount.plan.ExprNodeNewDefineDesc;
import com.tencent.easycount.plan.ExprNodeNewExecuteBlockDesc;
import com.tencent.easycount.plan.ExprNodeNewExecuteDesc;
import com.tencent.easycount.plan.ExprNodeNewForDesc;
import com.tencent.easycount.plan.ExprNodeNewForeachDesc;
import com.tencent.easycount.plan.ExprNodeNewForeachVarDesc;
import com.tencent.easycount.plan.ExprNodeNewGenerateDesc;
import com.tencent.easycount.plan.ExprNodeNewGenericFuncDesc;
import com.tencent.easycount.plan.ExprNodeNewIfDesc;
import com.tencent.easycount.plan.ExprNodeNewVarDesc;
import com.tencent.easycount.plan.RowResolverTRC;
import com.tencent.easycount.plan.TypeCheckCtxTRC;
import com.tencent.easycount.plan.TypeCheckCtxTRC.Var;
import com.tencent.easycount.util.constants.Constants;
import com.tencent.easycount.util.graph.GraphWalker;
import com.tencent.easycount.util.graph.GraphWalker.Dispatcher;
import com.tencent.easycount.util.graph.GraphWalker.Node;
import com.tencent.easycount.util.graph.GraphWalker.WalkMode;
import com.tencent.easycount.util.graph.RuleDispatcher;
import com.tencent.easycount.util.graph.RuleDispatcher.NodeProcessor;
import com.tencent.easycount.util.graph.RuleDispatcher.NodeProcessorCtx;
import com.tencent.easycount.util.graph.RuleDispatcher.Rule;
import com.tencent.easycount.util.graph.RuleDispatcher.RuleRegExp;

public class LogicalPlanExprUtils {
	public static ExprNodeDesc processGByExpr(final Node nd,
			final Object procCtx) {
		// We recursively create the exprNodeDesc. Base cases: when we encounter
		// a column ref, we convert that into an exprNodeColumnDesc; when we
		// encounter
		// a constant, we convert that into an exprNodeConstantDesc. For others
		// we
		// just
		// build the exprNodeFuncDesc with recursively built children.
		final ASTNodeTRC expr = (ASTNodeTRC) nd;
		final TypeCheckCtxTRC ctx = (TypeCheckCtxTRC) procCtx;

		final RowResolverTRC input = ctx.getInputRR();
		ExprNodeDesc desc = null;

		if ((ctx == null) || (input == null)) {
			return null;
		}

		// If the current subExpression is pre-calculated, as in Group-By etc.
		final ColumnInfoTRC colInfo = input.getExpression(expr);
		if (colInfo != null) {
			desc = new ExprNodeColumnDesc(colInfo.getType(),
					colInfo.getInternalName(), colInfo.getTabAlias(),
					colInfo.getIsVirtualCol());
			// ASTNode source = input.getExpressionSource(expr);
			// if (source != null) {
			// ctx.getUnparseTranslator().addCopyTranslation(expr, source);
			// }
			return desc;
		}
		return desc;
	}

	public static ExprNodeDesc getExprNodeDesc(final ASTNodeTRC expr,
			final NodeProcessorCtx ctx) throws Exception {

		// Create the walker, the rules dispatcher and the context.
		// create a walker which walks the tree in a DFS manner while
		// maintaining
		// the operator stack. The dispatcher
		// generates the plan from the operator tree
		final Map<Rule, NodeProcessor<ExprNodeDesc>> opRules = new LinkedHashMap<Rule, NodeProcessor<ExprNodeDesc>>();

		// TODO
		// opRules.put(new RuleRegExp("R1", TrcParser.TOK_NULL + "%"),
		// getNullExprProcessor());
		opRules.put(new RuleRegExp("R2", TrcParser.Number + "%|"
				+ TrcParser.TinyintLiteral + "%|" + TrcParser.SmallintLiteral
				+ "%|" + TrcParser.BigintLiteral + "%|"
		// + TrcParser.DecimalLiteral + "%"
				), getNumExprProcessor());
		opRules.put(new RuleRegExp("R3", TrcParser.Identifier + "%|"
				+ TrcParser.IdentifierRef + "%|" + TrcParser.StringLiteral
				+ "%|" + TrcParser.TOK_CHARSETLITERAL + "%|"
				+ TrcParser.TOK_STRINGLITERALSEQUENCE + "%|" + "%|"
				+ TrcParser.KW_IF + "%|" + TrcParser.KW_CASE + "%|"
				+ TrcParser.KW_WHEN + "%|" + TrcParser.KW_IN + "%|"
				+ TrcParser.KW_ARRAY + "%|" + TrcParser.KW_MAP + "%|"
				+ TrcParser.KW_STRUCT + "%"), getStrExprProcessor());
		opRules.put(new RuleRegExp("R4", TrcParser.KW_TRUE + "%|"
				+ TrcParser.KW_FALSE + "%"), getBoolExprProcessor());
		// opRules.put(new RuleRegExp("R5", TrcParser.TOK_DATELITERAL + "%"),
		// getDateExprProcessor());
		opRules.put(new RuleRegExp("R6", TrcParser.TOK_TABLE_OR_COL + "%"),
				getColumnExprProcessor());
		opRules.put(new RuleRegExp("R7", TrcParser.KW_AGGR_TIME + "%"),
				getTokAggrProcessor());

		// for foreach
		opRules.put(
				new RuleRegExp("R8_1", TrcParser.TOK_TABLE_OR_COL_REF + "%"),
				getColumnRefExprProcessor());
		opRules.put(new RuleRegExp("R8_2", TrcParser.TOK_OF + "%"),
				getColumnRef_OF_ExprProcessor());
		opRules.put(new RuleRegExp("R8_3", TrcParser.TOK_IN + "%"),
				getColumnRef_IN_ExprProcessor());
		opRules.put(new RuleRegExp("R8_4", TrcParser.TOK_FOREACH + "%"),
				getForeachExprProcessor());
		opRules.put(new RuleRegExp("R8_5", TrcParser.TOK_GENERATE + "%"),
				getGenerateExprProcessor());
		opRules.put(new RuleRegExp("R8_6", TrcParser.TOK_GENERATEMAP + "%"),
				getGenerateMapExprProcessor());

		// for execute
		opRules.put(new RuleRegExp("R9_1", TrcParser.TOK_EXECUTE + "%"),
				getExecuteExprProcessor());
		opRules.put(new RuleRegExp("R9_2", TrcParser.TOK_DEFINE + "%"),
				getDefineExprProcessor());
		opRules.put(new RuleRegExp("R9_3", TrcParser.TOK_VAR + "%"),
				getVarExprProcessor());
		opRules.put(new RuleRegExp("R9_4", TrcParser.TOK_EXECUTEBLOCK + "%"),
				getExecuteblockExprProcessor());
		opRules.put(new RuleRegExp("R9_5", TrcParser.TOK_ASSIGN + "%"),
				getAssignExprProcessor());
		opRules.put(new RuleRegExp("R9_6", TrcParser.TOK_FOR + "%"),
				getForExprProcessor());
		opRules.put(new RuleRegExp("R9_6", TrcParser.TOK_IF + "%"),
				getIfExprProcessor());
		opRules.put(new RuleRegExp("R9_7", TrcParser.TOK_EMIT + "%"),
				getEmitExprProcessor());

		opRules.put(new RuleRegExp("R10", TrcParser.KW_TINYINT + "%|"
				+ TrcParser.KW_SMALLINT + "%|" + TrcParser.KW_INT + "%|"
				+ TrcParser.KW_BIGINT + "%|" + TrcParser.KW_BOOLEAN + "%|"
				+ TrcParser.KW_FLOAT + "%|" + TrcParser.KW_DOUBLE + "%|"
				+ TrcParser.KW_DATE + "%|" + TrcParser.KW_DATETIME + "%|"
				+ TrcParser.KW_TIMESTAMP + "%|" + TrcParser.KW_STRING + "%|"
				+ TrcParser.KW_BINARY + "%|" + TrcParser.TOK_MAP + "%|"
				+ TrcParser.TOK_ARRAY + "%|" + TrcParser.TOK_STRUCT + "%|"
				+ TrcParser.TOK_STRUCTUNIT + "%|" + TrcParser.TOK_UNION + "%"),
				getDataTypeExprProcessor());

		// data attrs
		opRules.put(new RuleRegExp("R11", TrcParser.KW_ATTRIBUTES + "%"),
				getTokAttrsProcessor());

		// The dispatcher fires the processor corresponding to the closest
		// matching
		// rule and passes the context along
		final Dispatcher<ExprNodeDesc> disp = new RuleDispatcher<ExprNodeDesc>(
				getDefaultExprProcessor(), opRules, ctx);

		final GraphWalker<ExprNodeDesc> ogw = new GraphWalker<ExprNodeDesc>(
				disp, WalkMode.CHILD_FIRST, "getExprNodeDesc");

		// Create a list of topop nodes
		final ArrayList<Node> topNodes = new ArrayList<Node>();
		topNodes.add(expr);
		return ogw.walk(topNodes).get(expr);
	}

	public static class ColumnRefExprProcessor implements
			NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {

			final TypeCheckCtxTRC ctx = (TypeCheckCtxTRC) procCtx;
			if (ctx.getError() != null) {
				return null;
			}

			final ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				return desc;
			}
			final ASTNodeTRC expr = (ASTNodeTRC) nd;

			String varname = ParseStringUtil.unescapeIdentifier(expr
					.getChild(0).getText());
			varname = varname.substring(1);
			Var var = null;
			for (int i = stack.size() - 1; i >= 0; i--) {
				// parent may be foreach or execute
				final ASTNodeTRC parent = (ASTNodeTRC) stack.get(i);
				var = ctx.getVar(parent, varname);
				if (var != null) {
					break;
				}
			}
			if (var == null) {
				System.err.println(varname);
			}

			final ExprNodeNewColumnRefDesc exprNodColDesc = new ExprNodeNewColumnRefDesc(
					var, RefMode.ref);
			return exprNodColDesc;
		}
	}

	public static ColumnRefExprProcessor getColumnRefExprProcessor() {
		return new ColumnRefExprProcessor();
	}

	public static class ColumnRef_OF_ExprProcessor implements
			NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {

			final TypeCheckCtxTRC ctx = (TypeCheckCtxTRC) procCtx;
			if (ctx.getError() != null) {
				return null;
			}

			final ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				return desc;
			}
			// ASTNodeTRC expr = (ASTNodeTRC) nd;
			// FOREACH node
			final ASTNodeTRC parent = (ASTNodeTRC) stack.get(stack.size() - 2);

			final String varname = ((ExprNodeConstantDesc) nodeOutputs.get(0))
					.getValue().toString();

			final TypeInfo tinfo = nodeOutputs.get(1).getTypeInfo();
			TypeInfo vartype = null;
			RefMode fmode = null;
			if (tinfo instanceof ListTypeInfo) {
				vartype = TypeInfoFactory.intTypeInfo;
				fmode = RefMode.oflist;
			} else if (tinfo instanceof MapTypeInfo) {
				vartype = ((MapTypeInfo) tinfo).getMapKeyTypeInfo();
				fmode = RefMode.ofmap;
			} else {
				throw new RuntimeException(
						"type info wrong, must be list or map ! "
								+ tinfo.toString());
			}

			// parent is foreach
			final Var var = ctx.generateVar(parent, varname, vartype, null);

			final ExprNodeNewForeachVarDesc exprNodColDesc = new ExprNodeNewForeachVarDesc(
					var, fmode, nodeOutputs.get(1));
			return exprNodColDesc;
		}
	}

	public static ColumnRef_OF_ExprProcessor getColumnRef_OF_ExprProcessor() {
		return new ColumnRef_OF_ExprProcessor();
	}

	public static class ColumnRef_IN_ExprProcessor implements
			NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {

			final TypeCheckCtxTRC ctx = (TypeCheckCtxTRC) procCtx;
			if (ctx.getError() != null) {
				return null;
			}

			final ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				return desc;
			}
			// ASTNodeTRC expr = (ASTNodeTRC) nd;
			final ASTNodeTRC parent = (ASTNodeTRC) stack.get(stack.size() - 2);

			final String varname = String
					.valueOf(((ExprNodeConstantDesc) nodeOutputs.get(0))
							.getValue());

			final TypeInfo tinfo = nodeOutputs.get(1).getTypeInfo();
			TypeInfo vartype = null;
			RefMode fmode = null;
			if (tinfo instanceof ListTypeInfo) {
				vartype = ((ListTypeInfo) tinfo).getListElementTypeInfo();
				fmode = RefMode.inlist;
			} else if (tinfo instanceof MapTypeInfo) {
				vartype = ((MapTypeInfo) tinfo).getMapValueTypeInfo();
				fmode = RefMode.inmap;
			} else {
				throw new RuntimeException(
						"type info wrong, must be list or map ! "
								+ tinfo.toString());
			}

			// parent is foreach
			final Var var = ctx.generateVar(parent, varname, vartype, null);

			final ExprNodeNewForeachVarDesc exprNodColDesc = new ExprNodeNewForeachVarDesc(
					var, fmode, nodeOutputs.get(1));
			return exprNodColDesc;
		}
	}

	public static ColumnRef_IN_ExprProcessor getColumnRef_IN_ExprProcessor() {
		return new ColumnRef_IN_ExprProcessor();
	}

	public static class ForeachExprProcessor implements
			NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {

			final TypeCheckCtxTRC ctx = (TypeCheckCtxTRC) procCtx;
			if (ctx.getError() != null) {
				return null;
			}

			final ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				return desc;
			}

			return new ExprNodeNewForeachDesc(nodeOutputs.get(0),
					(ExprNodeNewGenerateDesc) nodeOutputs.get(1),
					((ExprNodeNewForeachVarDesc) nodeOutputs.get(0)).getFmode());
		}
	}

	public static ForeachExprProcessor getForeachExprProcessor() {
		return new ForeachExprProcessor();
	}

	public static class GenerateExprProcessor implements
			NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {

			final TypeCheckCtxTRC ctx = (TypeCheckCtxTRC) procCtx;
			if (ctx.getError() != null) {
				return null;
			}

			final ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				return desc;
			}
			return new ExprNodeNewGenerateDesc(nodeOutputs.get(0), null, false);
		}
	}

	public static GenerateExprProcessor getGenerateExprProcessor() {
		return new GenerateExprProcessor();
	}

	public static class GenerateMapExprProcessor implements
			NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {

			final TypeCheckCtxTRC ctx = (TypeCheckCtxTRC) procCtx;
			if (ctx.getError() != null) {
				return null;
			}

			final ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				return desc;
			}
			return new ExprNodeNewGenerateDesc(nodeOutputs.get(0),
					nodeOutputs.get(1), true);
		}
	}

	public static GenerateMapExprProcessor getGenerateMapExprProcessor() {
		return new GenerateMapExprProcessor();
	}

	public static class DefineExprProcessor implements
			NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {
			final TypeCheckCtxTRC ctx = (TypeCheckCtxTRC) procCtx;
			if (ctx.getError() != null) {
				return null;
			}
			final ArrayList<Var> vars = new ArrayList<TypeCheckCtxTRC.Var>();
			for (int i = 0; i < nodeOutputs.size(); i++) {
				vars.add(((ExprNodeNewVarDesc) nodeOutputs.get(i)).getVar());
			}
			return new ExprNodeNewDefineDesc(vars);
		}
	}

	private static DefineExprProcessor getDefineExprProcessor() {
		return new DefineExprProcessor();
	}

	public static class VarExprProcessor implements NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {
			final TypeCheckCtxTRC ctx = (TypeCheckCtxTRC) procCtx;
			if (ctx.getError() != null) {
				return null;
			}

			if (stack.size() < 3) {
				throw new RuntimeException(
						"error : stack size at least 3 but is " + stack.size());
			}
			final ASTNodeTRC parent = (ASTNodeTRC) stack.get(stack.size() - 2);
			final ASTNodeTRC secondParent = (ASTNodeTRC) stack
					.get(stack.size() - 3);
			if ((parent.getToken().getType() != TrcParser.TOK_DEFINE)
					|| (secondParent.getToken().getType() != TrcParser.TOK_EXECUTE)) {
				throw new RuntimeException("must be execute -> define -> var ");
			}

			if (nodeOutputs.size() < 2) {
				throw new RuntimeException("var childs at least 2");
			}
			final String varname = (String) ((ExprNodeConstantDesc) nodeOutputs
					.get(0)).getValue();
			final String typename = (String) ((ExprNodeConstantDesc) nodeOutputs
					.get(1)).getValue();
			final TypeInfo vartype = TypeInfoUtils
					.getTypeInfoFromTypeString(typename);

			ExprNodeConstantDesc defaultExpr = null;
			if (nodeOutputs.size() >= 3) {
				defaultExpr = (ExprNodeConstantDesc) nodeOutputs.get(2);
			}

			final Var var = ctx.generateVar(secondParent, varname, vartype,
					defaultExpr);

			return new ExprNodeNewVarDesc(var);
		}
	}

	private static VarExprProcessor getVarExprProcessor() {
		return new VarExprProcessor();
	}

	public static class AssignExprProcessor implements
			NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {
			final ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				return desc;
			}

			final String varStr = ((ExprNodeConstantDesc) nodeOutputs.get(0))
					.getValue().toString();
			final ExprNodeDesc assignDesc = nodeOutputs.get(1);

			return new ExprNodeNewAssignDesc(varStr, assignDesc);
		}
	}

	public static AssignExprProcessor getAssignExprProcessor() {
		return new AssignExprProcessor();
	}

	public static class ForExprProcessor implements NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {
			final ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				return desc;
			}

			final ExprNodeDesc condationDesc = nodeOutputs.get(0);
			final ArrayList<ExprNodeNewBlockDesc> blockDescs = new ArrayList<ExprNodeNewBlockDesc>();
			for (int i = 1; i < nodeOutputs.size(); i++) {
				blockDescs.add((ExprNodeNewBlockDesc) nodeOutputs.get(i));
			}

			return new ExprNodeNewForDesc(condationDesc, blockDescs);
		}
	}

	public static ForExprProcessor getForExprProcessor() {
		return new ForExprProcessor();
	}

	public static class IfExprProcessor implements NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {
			final ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				return desc;
			}

			final ExprNodeDesc condationDesc = nodeOutputs.get(0);
			final ArrayList<ExprNodeNewBlockDesc> blockDescs = new ArrayList<ExprNodeNewBlockDesc>();
			for (int i = 1; i < nodeOutputs.size(); i++) {
				blockDescs.add((ExprNodeNewBlockDesc) nodeOutputs.get(i));
			}
			return new ExprNodeNewIfDesc(condationDesc, blockDescs);
		}
	}

	public static IfExprProcessor getIfExprProcessor() {
		return new IfExprProcessor();
	}

	public static class ExecuteblockExprProcessor implements
			NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {
			final ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				return desc;
			}

			final ArrayList<ExprNodeNewBlockDesc> blockDescs = new ArrayList<ExprNodeNewBlockDesc>();
			for (int i = 0; i < nodeOutputs.size(); i++) {
				blockDescs.add((ExprNodeNewBlockDesc) nodeOutputs.get(i));
			}

			return new ExprNodeNewExecuteBlockDesc(blockDescs);
		}
	}

	public static ExecuteblockExprProcessor getExecuteblockExprProcessor() {
		return new ExecuteblockExprProcessor();
	}

	public static class EmitExprProcessor implements
			NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {
			final ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				return desc;
			}

			return nodeOutputs.get(0);
		}
	}

	public static EmitExprProcessor getEmitExprProcessor() {
		return new EmitExprProcessor();
	}

	public static class ExecuteExprProcessor implements
			NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {
			final ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				return desc;
			}

			final ExprNodeNewDefineDesc defineDesc = (ExprNodeNewDefineDesc) nodeOutputs
					.get(0);
			final ExprNodeNewExecuteBlockDesc blockDesc = (ExprNodeNewExecuteBlockDesc) nodeOutputs
					.get(1);
			final ExprNodeDesc emitDesc = nodeOutputs.get(2);

			return new ExprNodeNewExecuteDesc(defineDesc, blockDesc, emitDesc);
		}
	}

	public static ExecuteExprProcessor getExecuteExprProcessor() {
		return new ExecuteExprProcessor();
	}

	// public static class NullExprProcessor implements
	// NodeProcessor<ExprNodeDesc> {
	//
	// @Override
	// public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
	// final NodeProcessorCtx procCtx,
	// final ArrayList<ExprNodeDesc> nodeOutputs) {
	// final ExprNodeDesc desc = processGByExpr(nd, procCtx);
	// if (desc != null) {
	// return desc;
	// }
	//
	// return new ExprNodeNullDesc();
	// }
	// }
	//
	// public static NullExprProcessor getNullExprProcessor() {
	// return new NullExprProcessor();
	// }

	public static class DataTypeExprProcessor implements
			NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {
			ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				return desc;
			}

			String str = null;
			final ASTNodeTRC expr = (ASTNodeTRC) nd;

			switch (expr.getToken().getType()) {
			case TrcParser.KW_TINYINT:
				str = serdeConstants.TINYINT_TYPE_NAME;
				break;
			case TrcParser.KW_SMALLINT:
				str = serdeConstants.SMALLINT_TYPE_NAME;
				break;
			case TrcParser.KW_INT:
				str = serdeConstants.INT_TYPE_NAME;
				break;
			case TrcParser.KW_BIGINT:
				str = serdeConstants.BIGINT_TYPE_NAME;
				break;
			case TrcParser.KW_BOOLEAN:
				str = serdeConstants.BOOLEAN_TYPE_NAME;
				break;
			case TrcParser.KW_FLOAT:
				str = serdeConstants.FLOAT_TYPE_NAME;
				break;
			case TrcParser.KW_DOUBLE:
				str = serdeConstants.DOUBLE_TYPE_NAME;
				break;
			case TrcParser.KW_DATE:
				str = serdeConstants.DATE_TYPE_NAME;
				break;
			case TrcParser.KW_DATETIME:
				str = serdeConstants.DATETIME_TYPE_NAME;
				break;
			case TrcParser.KW_TIMESTAMP:
				str = serdeConstants.TIMESTAMP_TYPE_NAME;
				break;
			case TrcParser.KW_STRING:
				str = serdeConstants.STRING_TYPE_NAME;
				break;
			case TrcParser.KW_BINARY:
				str = serdeConstants.BINARY_TYPE_NAME;
				break;
			case TrcParser.TOK_MAP:
				final ExprNodeConstantDesc keydesc = (ExprNodeConstantDesc) nodeOutputs
						.get(0);
				final ExprNodeConstantDesc valuedesc = (ExprNodeConstantDesc) nodeOutputs
						.get(1);
				str = serdeConstants.MAP_TYPE_NAME + "<" + keydesc.getValue()
						+ "," + valuedesc.getValue() + ">";
				break;
			case TrcParser.TOK_ARRAY:
				final ExprNodeConstantDesc eledesc = (ExprNodeConstantDesc) nodeOutputs
						.get(0);
				str = serdeConstants.LIST_TYPE_NAME + "<" + eledesc.getValue()
						+ ">";
				break;
			case TrcParser.TOK_UNION:
				StringBuilder sb = new StringBuilder();
				sb.append(serdeConstants.UNION_TYPE_NAME + "<");
				for (int i = 0; i < nodeOutputs.size(); i++) {
					final ExprNodeConstantDesc udesc = (ExprNodeConstantDesc) nodeOutputs
							.get(i);
					if (i > 0) {
						sb.append(",");
					}
					sb.append(udesc.getValue());
				}
				sb.append(">");
				str = sb.toString();
				break;
			case TrcParser.TOK_STRUCTUNIT:
				final ExprNodeConstantDesc namedesc = (ExprNodeConstantDesc) nodeOutputs
						.get(0);
				final ExprNodeConstantDesc typedesc = (ExprNodeConstantDesc) nodeOutputs
						.get(1);
				str = namedesc.getValue() + ":" + typedesc.getValue();
				break;
			case TrcParser.TOK_STRUCT:
				sb = new StringBuilder();
				sb.append(serdeConstants.STRUCT_TYPE_NAME + "<");
				for (int i = 0; i < nodeOutputs.size(); i++) {
					final ExprNodeConstantDesc udesc = (ExprNodeConstantDesc) nodeOutputs
							.get(i);
					if (i > 0) {
						sb.append(",");
					}
					sb.append(udesc.getValue());
				}
				sb.append(">");
				str = sb.toString();
				break;
			default:
				break;
			}

			desc = new ExprNodeConstantDesc(TypeInfoFactory.stringTypeInfo, str);

			return desc;
		}
	}

	public static DataTypeExprProcessor getDataTypeExprProcessor() {
		return new DataTypeExprProcessor();
	}

	public static class AggrTimeExprProcessor implements
			NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {
			return new ExprNodeColumnDesc(TypeInfoFactory.longTypeInfo,
					Constants.gbyAggrTupleTime, "", false, false);
		}
	}

	private static AggrTimeExprProcessor getTokAggrProcessor() {
		return new AggrTimeExprProcessor();
	}

	public static class AttrsExprProcessor implements
			NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {
			return new ExprNodeColumnDesc(TypeInfoFactory.getMapTypeInfo(
					TypeInfoFactory.stringTypeInfo,
					TypeInfoFactory.stringTypeInfo), Constants.dataAttrs, "",
					false, false);
		}
	}

	private static AttrsExprProcessor getTokAttrsProcessor() {
		return new AttrsExprProcessor();
	}

	/**
	 * Processor for processing numeric constants.
	 */
	public static class NumExprProcessor implements NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {

			final TypeCheckCtxTRC ctx = (TypeCheckCtxTRC) procCtx;
			if (ctx.getError() != null) {
				return null;
			}

			final ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				return desc;
			}

			Number v = null;
			final ASTNodeTRC expr = (ASTNodeTRC) nd;
			// The expression can be any one of Double, Long and Integer. We
			// try to parse the expression in that order to ensure that the
			// most specific type is used for conversion.
			try {
				if (expr.getText().endsWith("L")) {
					// Literal bigint.
					v = Long.valueOf(expr.getText().substring(0,
							expr.getText().length() - 1));
				} else if (expr.getText().endsWith("S")) {
					// Literal smallint.
					v = Short.valueOf(expr.getText().substring(0,
							expr.getText().length() - 1));
				} else if (expr.getText().endsWith("Y")) {
					// Literal tinyint.
					v = Byte.valueOf(expr.getText().substring(0,
							expr.getText().length() - 1));
				}
				// else if (expr.getText().endsWith("BD")) {
				// // Literal decimal
				// return new ExprNodeConstantDesc(
				// TypeInfoFactory.decimalTypeInfo, expr.getText()
				// .substring(0, expr.getText().length() - 2));
				// }

				else {
					v = Double.valueOf(expr.getText());
					v = Long.valueOf(expr.getText());
					v = Integer.valueOf(expr.getText());
				}
			} catch (final NumberFormatException e) {
				// do nothing here, we will throw an exception in the following
				// block
			}
			return new ExprNodeConstantDesc(v);
		}

	}

	/**
	 * Factory method to get NumExprProcessor.
	 *
	 * @return NumExprProcessor.
	 */
	public static NumExprProcessor getNumExprProcessor() {
		return new NumExprProcessor();
	}

	/**
	 * Processor for processing string constants.
	 */
	public static class StrExprProcessor implements NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {

			final TypeCheckCtxTRC ctx = (TypeCheckCtxTRC) procCtx;
			if (ctx.getError() != null) {
				return null;
			}

			final ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				return desc;
			}

			final ASTNodeTRC expr = (ASTNodeTRC) nd;
			String str = null;

			switch (expr.getToken().getType()) {
			case TrcParser.StringLiteral:
				str = ParseStringUtil.unescapeSQLString(expr.getText());
				break;
			case TrcParser.TOK_STRINGLITERALSEQUENCE:
				final StringBuilder sb = new StringBuilder();
				for (final Node n : expr.getChildren()) {
					sb.append(ParseStringUtil
							.unescapeSQLString(((ASTNodeTRC) n).getText()));
				}
				str = sb.toString();
				break;
			case TrcParser.TOK_CHARSETLITERAL:
				str = ParseStringUtil.charSetString(expr.getChild(0).getText(),
						expr.getChild(1).getText());
				break;
			default:
				// TrcParser.identifier | HiveParse.KW_IF | HiveParse.KW_LEFT |
				// HiveParse.KW_RIGHT
				str = ParseStringUtil.unescapeIdentifier(expr.getText());
				if (expr.getToken().getType() == TrcParser.IdentifierRef) {
					str = str.substring(1);
				}
				break;
			}
			return new ExprNodeConstantDesc(TypeInfoFactory.stringTypeInfo, str);
		}
	}

	/**
	 * Factory method to get StrExprProcessor.
	 *
	 * @return StrExprProcessor.
	 */
	public static StrExprProcessor getStrExprProcessor() {
		return new StrExprProcessor();
	}

	/**
	 * Processor for boolean constants.
	 */
	public static class BoolExprProcessor implements
			NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) {

			final TypeCheckCtxTRC ctx = (TypeCheckCtxTRC) procCtx;
			if (ctx.getError() != null) {
				return null;
			}

			final ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				return desc;
			}

			final ASTNodeTRC expr = (ASTNodeTRC) nd;
			Boolean bool = null;

			switch (expr.getToken().getType()) {
			case TrcParser.KW_TRUE:
				bool = Boolean.TRUE;
				break;
			case TrcParser.KW_FALSE:
				bool = Boolean.FALSE;
				break;
			default:
				assert false;
			}
			return new ExprNodeConstantDesc(TypeInfoFactory.booleanTypeInfo,
					bool);
		}

	}

	/**
	 * Factory method to get BoolExprProcessor.
	 *
	 * @return BoolExprProcessor.
	 */
	public static BoolExprProcessor getBoolExprProcessor() {
		return new BoolExprProcessor();
	}

	/**
	 * Processor for table columns.
	 */
	public static class ColumnExprProcessor implements
			NodeProcessor<ExprNodeDesc> {

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs)
				throws SemanticException {

			final TypeCheckCtxTRC ctx = (TypeCheckCtxTRC) procCtx;
			if (ctx.getError() != null) {
				return null;
			}

			final ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				return desc;
			}

			final ASTNodeTRC expr = (ASTNodeTRC) nd;
			final ASTNodeTRC parent = stack.size() > 1 ? (ASTNodeTRC) stack
					.get(stack.size() - 2) : null;
					final RowResolverTRC input = ctx.getInputRR();

					if (expr.getType() != TrcParser.TOK_TABLE_OR_COL) {
						ctx.setError(ErrorMsg.INVALID_COLUMN.getMsg(expr), expr);
						throw new SemanticException(
								ErrorMsg.INVALID_COLUMN.getMsg(expr));
					}

					assert (expr.getChildCount() == 1);
					final String tableOrCol = ParseStringUtil.unescapeIdentifier(expr
							.getChild(0).getText());

					final boolean isTableAlias = input.hasTableAlias(tableOrCol);
					final ColumnInfoTRC colInfo = input.get(null, tableOrCol);

					if (isTableAlias) {
						if (colInfo != null) {
							if ((parent != null) && (parent.getType() == TrcParser.DOT)) {
								// It's a table alias.
								return null;
							}
							// It's a column.
							return new ExprNodeColumnDesc(colInfo.getType(),
									colInfo.getInternalName(), colInfo.getTabAlias(),
									colInfo.getIsVirtualCol());
						} else {
							if ((parent != null) && (parent.getType() != TrcParser.DOT)) {
								throw new RuntimeException("error in process column : "
										+ tableOrCol + " can not get from input : "
										+ input.toString());
							}

							// It's a table alias.
							// We will process that later in DOT.
							return null;
						}
					} else {
						if (colInfo == null) {
							// It's not a column or a table alias.
							// column cant found from input it may be in group by key
							// expr
							if (input.getIsExprResolver()) {
								ASTNodeTRC exprNode = expr;
								if (!stack.empty()) {
									final ASTNodeTRC tmp = (ASTNodeTRC) stack.pop();
									if (!stack.empty()) {
										exprNode = (ASTNodeTRC) stack.peek();
									}
									stack.push(tmp);
								}
								ctx.setError(ErrorMsg.NON_KEY_EXPR_IN_GROUPBY
										.getMsg(exprNode), expr);
								return null;
							} else {
								final List<String> possibleColumnNames = input
										.getReferenceableColumnAliases(tableOrCol, -1);
								final String reason = String.format(
										"(possible column names are: %s)",
										StringUtils.join(possibleColumnNames, ", "));
								ctx.setError(
										ErrorMsg.INVALID_TABLE_OR_COLUMN.getMsg(
												expr.getChild(0), reason), expr);
								return null;
							}
							// if (parent != null && parent.getType() != TrcParser.DOT)
							// {
							// throw new RuntimeException("error in process column : "
							// + tableOrCol + " can not get from input : "
							// + input.toString());
							// }

						} else {
							// It's a column.
							final ExprNodeColumnDesc exprNodColDesc = new ExprNodeColumnDesc(
									colInfo.getType(), colInfo.getInternalName(),
									colInfo.getTabAlias(), colInfo.getIsVirtualCol());
							// exprNodColDesc.setSkewedCol(colInfo.isSkewedCol());
							return exprNodColDesc;
						}
					}
		}
	}

	/**
	 * Factory method to get ColumnExprProcessor.
	 *
	 * @return ColumnExprProcessor.
	 */
	public static ColumnExprProcessor getColumnExprProcessor() {
		return new ColumnExprProcessor();
	}

	/**
	 * The default processor for typechecking.
	 */
	public static class DefaultExprProcessor implements
			NodeProcessor<ExprNodeDesc> {

		static HashMap<Integer, String> specialUnaryOperatorTextHashMap;
		static HashMap<Integer, String> specialFunctionTextHashMap;
		static HashMap<Integer, String> conversionFunctionTextHashMap;
		// static HashSet<Integer> windowingTokens;
		static {
			specialUnaryOperatorTextHashMap = new HashMap<Integer, String>();
			specialUnaryOperatorTextHashMap.put(TrcParser.PLUS, "positive");
			specialUnaryOperatorTextHashMap.put(TrcParser.MINUS, "negative");
			specialFunctionTextHashMap = new HashMap<Integer, String>();
			specialFunctionTextHashMap.put(TrcParser.TOK_ISNULL, "isnull");
			specialFunctionTextHashMap
					.put(TrcParser.TOK_ISNOTNULL, "isnotnull");
			conversionFunctionTextHashMap = new HashMap<Integer, String>();
			conversionFunctionTextHashMap.put(TrcParser.TOK_BOOLEAN,
					serdeConstants.BOOLEAN_TYPE_NAME);
			conversionFunctionTextHashMap.put(TrcParser.TOK_TINYINT,
					serdeConstants.TINYINT_TYPE_NAME);
			conversionFunctionTextHashMap.put(TrcParser.TOK_SMALLINT,
					serdeConstants.SMALLINT_TYPE_NAME);
			conversionFunctionTextHashMap.put(TrcParser.TOK_INT,
					serdeConstants.INT_TYPE_NAME);
			conversionFunctionTextHashMap.put(TrcParser.TOK_BIGINT,
					serdeConstants.BIGINT_TYPE_NAME);
			conversionFunctionTextHashMap.put(TrcParser.TOK_FLOAT,
					serdeConstants.FLOAT_TYPE_NAME);
			conversionFunctionTextHashMap.put(TrcParser.TOK_DOUBLE,
					serdeConstants.DOUBLE_TYPE_NAME);
			conversionFunctionTextHashMap.put(TrcParser.TOK_STRING,
					serdeConstants.STRING_TYPE_NAME);
			// conversionFunctionTextHashMap.put(TrcParser.TOK_VARCHAR,
			// serdeConstants.VARCHAR_TYPE_NAME);
			conversionFunctionTextHashMap.put(TrcParser.TOK_BINARY,
					serdeConstants.BINARY_TYPE_NAME);
			conversionFunctionTextHashMap.put(TrcParser.TOK_DATE,
					serdeConstants.DATE_TYPE_NAME);
			conversionFunctionTextHashMap.put(TrcParser.TOK_TIMESTAMP,
					serdeConstants.TIMESTAMP_TYPE_NAME);
			// conversionFunctionTextHashMap.put(TrcParser.TOK_DECIMAL,
			// serdeConstants.DECIMAL_TYPE_NAME);

			// windowingTokens = new HashSet<Integer>();
			// windowingTokens.add(TrcParser.KW_OVER);
			// windowingTokens.add(TrcParser.TOK_PARTITIONINGSPEC);
			// windowingTokens.add(TrcParser.TOK_DISTRIBUTEBY);
			// windowingTokens.add(TrcParser.TOK_SORTBY);
			// windowingTokens.add(TrcParser.TOK_CLUSTERBY);
			// windowingTokens.add(TrcParser.TOK_WINDOWSPEC);
			// windowingTokens.add(TrcParser.TOK_WINDOWRANGE);
			// windowingTokens.add(TrcParser.TOK_WINDOWVALUES);
			// windowingTokens.add(TrcParser.KW_UNBOUNDED);
			// windowingTokens.add(TrcParser.KW_PRECEDING);
			// windowingTokens.add(TrcParser.KW_FOLLOWING);
			// windowingTokens.add(TrcParser.KW_CURRENT);
			// windowingTokens.add(TrcParser.TOK_TABSORTCOLNAMEASC);
			// windowingTokens.add(TrcParser.TOK_TABSORTCOLNAMEDESC);
		}

		private static boolean isRedundantConversionFunction(
				final ASTNodeTRC expr, final boolean isFunction,
				final ArrayList<ExprNodeDesc> children) {
			if (!isFunction) {
				return false;
			}
			// conversion functions take a single parameter
			if (children.size() != 1) {
				return false;
			}
			final String funcText = conversionFunctionTextHashMap
					.get(((ASTNodeTRC) expr.getChild(0)).getType());
			// not a conversion function
			if (funcText == null) {
				return false;
			}
			// return true when the child type and the conversion target type is
			// the
			// same
			return ((PrimitiveTypeInfo) children.get(0).getTypeInfo())
					.getTypeName().equalsIgnoreCase(funcText);
		}

		public static String getFunctionText(final ASTNodeTRC expr,
				final boolean isFunction) {
			String funcText = null;
			if (!isFunction) {
				// For operator, the function name is the operator text, unless
				// it's in
				// our special dictionary
				if (expr.getChildCount() == 1) {
					funcText = specialUnaryOperatorTextHashMap.get(expr
							.getType());
				}
				if (funcText == null) {
					funcText = expr.getText();
				}
			} else {
				// For TOK_FUNCTION, the function name is stored in the first
				// child,
				// unless it's in our
				// special dictionary.
				assert (expr.getChildCount() >= 1);
				final int funcType = ((ASTNodeTRC) expr.getChild(0)).getType();
				funcText = specialFunctionTextHashMap.get(funcType);
				if (funcText == null) {
					funcText = conversionFunctionTextHashMap.get(funcType);
				}
				if (funcText == null) {
					funcText = ((ASTNodeTRC) expr.getChild(0)).getText();
				}
			}
			return ParseStringUtil.unescapeIdentifier(funcText);
		}

		/**
		 * This function create an ExprNodeDesc for a UDF function given the
		 * children (arguments). It will insert implicit type conversion
		 * functions if necessary.
		 *
		 * @throws SemanticException
		 */
		static ExprNodeDesc getFuncExprNodeDescWithUdfData(
				final String udfName, final Object udfData,
				final ExprNodeDesc... children) throws SemanticException {

			final FunctionInfo fi = FunctionRegistry.getFunctionInfo(udfName);
			if (fi == null) {
				throw new UDFArgumentException(udfName + " not found.");
			}

			final GenericUDF genericUDF = fi.getGenericUDF();
			if (genericUDF == null) {
				throw new UDFArgumentException(udfName
						+ " is an aggregation function or a table function.");
			}

			// Add udfData to UDF if necessary
			// if (udfData != null) {
			// if (genericUDF instanceof SettableUDF) {
			// ((SettableUDF) genericUDF).setParams(udfData);
			// }
			// }

			final List<ExprNodeDesc> childrenList = new ArrayList<ExprNodeDesc>(
					children.length);
			childrenList.addAll(Arrays.asList(children));
			return ExprNodeNewGenericFuncDesc.newInstance(genericUDF,
					childrenList);
		}

		public static ExprNodeDesc getFuncExprNodeDesc(final String udfName,
				final ExprNodeDesc... children) throws SemanticException {
			return getFuncExprNodeDescWithUdfData(udfName, null, children);
		}

		static ExprNodeDesc getXpathOrFuncExprNodeDesc(final ASTNodeTRC expr,
				final boolean isFunction,
				final ArrayList<ExprNodeDesc> children,
				final TypeCheckCtxTRC ctx) throws Exception {
			// return the child directly if the conversion is redundant.
			if (isRedundantConversionFunction(expr, isFunction, children)) {
				assert (children.size() == 1);
				assert (children.get(0) != null);
				return children.get(0);
			}
			final String funcText = getFunctionText(expr, isFunction);
			ExprNodeDesc desc;
			if (funcText.equals(".")) {
				// "." : FIELD Expression
				assert (children.size() == 2);
				// Only allow constant field name for now
				assert (children.get(1) instanceof ExprNodeConstantDesc);
				final ExprNodeDesc object = children.get(0);
				final ExprNodeConstantDesc fieldName = (ExprNodeConstantDesc) children
						.get(1);
				assert (fieldName.getValue() instanceof String);

				// Calculate result TypeInfo
				final String fieldNameString = (String) fieldName.getValue();
				TypeInfo objectTypeInfo = object.getTypeInfo();

				// Allow accessing a field of list element structs directly from
				// a list
				final boolean isList = (object.getTypeInfo().getCategory() == ObjectInspector.Category.LIST);
				if (isList) {
					objectTypeInfo = ((ListTypeInfo) objectTypeInfo)
							.getListElementTypeInfo();
				}
				if (objectTypeInfo.getCategory() != Category.STRUCT) {
					// throw new SemanticException(
					// ErrorMsg.INVALID_DOT.getMsg(expr));
				}
				TypeInfo t = ((StructTypeInfo) objectTypeInfo)
						.getStructFieldTypeInfo(fieldNameString);
				if (isList) {
					t = TypeInfoFactory.getListTypeInfo(t);
				}

				desc = new ExprNodeFieldDesc(t, children.get(0),
						fieldNameString, isList);

			} else if (funcText.equals("[")) {
				// "[]" : LSQUARE/INDEX Expression
				assert (children.size() == 2);

				// Check whether this is a list or a map
				final TypeInfo myt = children.get(0).getTypeInfo();

				if (myt.getCategory() == Category.LIST) {
					// Only allow integer index for now
					if (!(children.get(1) instanceof ExprNodeConstantDesc)
							|| !(((ExprNodeConstantDesc) children.get(1))
									.getTypeInfo()
									.equals(TypeInfoFactory.intTypeInfo))) {
						// throw new SemanticException(
						// SemanticAnalyzer.generateErrorMessage(expr,
						// ErrorMsg.INVALID_ARRAYINDEX_CONSTANT
						// .getMsg()));
					}

					// Calculate TypeInfo
					final TypeInfo t = ((ListTypeInfo) myt)
							.getListElementTypeInfo();
					desc = new ExprNodeNewGenericFuncDesc(t,
							FunctionRegistry.getGenericUDFForIndex(), children);
				} else if (myt.getCategory() == Category.MAP) {
					// Only allow constant map key for now
					if (!(children.get(1) instanceof ExprNodeConstantDesc)) {
						// throw new SemanticException(
						// SemanticAnalyzer.generateErrorMessage(expr,
						// ErrorMsg.INVALID_MAPINDEX_CONSTANT
						// .getMsg()));
					}
					if (!(((ExprNodeConstantDesc) children.get(1))
							.getTypeInfo().equals(((MapTypeInfo) myt)
							.getMapKeyTypeInfo()))) {
						// throw new SemanticException(
						// ErrorMsg.INVALID_MAPINDEX_TYPE.getMsg(expr));
					}
					// Calculate TypeInfo
					final TypeInfo t = ((MapTypeInfo) myt)
							.getMapValueTypeInfo();
					desc = new ExprNodeNewGenericFuncDesc(t,
							FunctionRegistry.getGenericUDFForIndex(), children);
				} else {
					// throw new SemanticException(
					// ErrorMsg.NON_COLLECTION_TYPE.getMsg(expr,
					// myt.getTypeName()));
					desc = null;
				}
			} else {
				// other operators or functions
				final FunctionInfo fi = FunctionRegistry
						.getFunctionInfo(funcText);
				System.err.println("____" + funcText + "-"
						+ (fi == null ? "" : fi.getDisplayName()));
				if (fi == null) {
					if (isFunction) {
						// throw new SemanticException(
						// ErrorMsg.INVALID_FUNCTION
						// .getMsg((ASTNodeTRC) expr.getChild(0)));
					} else {
						// throw new SemanticException(
						// ErrorMsg.INVALID_FUNCTION.getMsg(expr));
					}
				}

				// getGenericUDF() actually clones the UDF. Just call it once
				// and reuse.
				final GenericUDF genericUDF = fi.getGenericUDF();
				System.err
						.println("#########"
								+ (genericUDF == null ? "null" : genericUDF
										.getClass()));

				// if (!fi.isNative()) {
				// ctx.getUnparseTranslator().addIdentifierTranslation(
				// (ASTNodeTRC) expr.getChild(0));
				// }

				// Handle type casts that may contain type parameters
				if (isFunction) {
					final ASTNodeTRC funcNameNode = (ASTNodeTRC) expr
							.getChild(0);
					switch (funcNameNode.getType()) {
					// case TrcParser.TOK_VARCHAR:
					// // Add type params
					// VarcharTypeParams varcharTypeParams = new
					// VarcharTypeParams();
					// varcharTypeParams.length = Integer
					// .valueOf((funcNameNode.getChild(0).getText()));
					// if (genericUDF != null) {
					// ((SettableUDF) genericUDF)
					// .setParams(varcharTypeParams);
					// }
					// break;
					default:
						// Do nothing
						break;
					}
				}

				// Detect UDTF's in nested SELECT, GROUP BY, etc as they aren't
				// supported
				if (fi.getGenericUDTF() != null) {
					// throw new SemanticException(
					// ErrorMsg.UDTF_INVALID_LOCATION.getMsg());
				}
				// UDAF in filter condition, group-by caluse, param of funtion,
				// etc.
				if (fi.getGenericUDAFResolver() != null) {
					// if (isFunction) {
					// throw new SemanticException(
					// ErrorMsg.UDAF_INVALID_LOCATION
					// .getMsg((ASTNodeTRC) expr.getChild(0)));
					// } else {
					// throw new SemanticException(
					// ErrorMsg.UDAF_INVALID_LOCATION.getMsg(expr));
					// }
				}
				if (!ctx.getAllowStatefulFunctions() && (genericUDF != null)) {
					// if (FunctionRegistry.isStateful(genericUDF)) {
					// throw new SemanticException(
					// ErrorMsg.UDF_STATEFUL_INVALID_LOCATION.getMsg());
					// }
				}

				// Try to infer the type of the constant only if there are two
				// nodes, one of them is column and the other is numeric const
				if ((genericUDF instanceof GenericUDFBaseCompare)
						&& (children.size() == 2)
						&& (((children.get(0) instanceof ExprNodeConstantDesc) && (children
								.get(1) instanceof ExprNodeColumnDesc)) || ((children
								.get(0) instanceof ExprNodeColumnDesc) && (children
								.get(1) instanceof ExprNodeConstantDesc)))) {
					final int constIdx = children.get(0) instanceof ExprNodeConstantDesc ? 0
							: 1;

					final Set<String> inferTypes = new HashSet<String>(
							Arrays.asList(serdeConstants.TINYINT_TYPE_NAME
									.toLowerCase(),
									serdeConstants.SMALLINT_TYPE_NAME
									.toLowerCase(),
									serdeConstants.INT_TYPE_NAME.toLowerCase(),
									serdeConstants.BIGINT_TYPE_NAME
									.toLowerCase(),
									serdeConstants.FLOAT_TYPE_NAME
									.toLowerCase(),
									serdeConstants.DOUBLE_TYPE_NAME
									.toLowerCase(),
									serdeConstants.STRING_TYPE_NAME
									.toLowerCase()));

					final String constType = children.get(constIdx)
							.getTypeString().toLowerCase();
					final String columnType = children.get(1 - constIdx)
							.getTypeString().toLowerCase();

					if (inferTypes.contains(constType)
							&& inferTypes.contains(columnType)
							&& !columnType.equalsIgnoreCase(constType)) {
						final Object originalValue = ((ExprNodeConstantDesc) children
								.get(constIdx)).getValue();
						final String constValue = originalValue.toString();
						boolean triedDouble = false;
						Number value = null;
						try {
							if (columnType
									.equalsIgnoreCase(serdeConstants.TINYINT_TYPE_NAME)) {
								value = new Byte(constValue);
							} else if (columnType
									.equalsIgnoreCase(serdeConstants.SMALLINT_TYPE_NAME)) {
								value = new Short(constValue);
							} else if (columnType
									.equalsIgnoreCase(serdeConstants.INT_TYPE_NAME)) {
								value = new Integer(constValue);
							} else if (columnType
									.equalsIgnoreCase(serdeConstants.BIGINT_TYPE_NAME)) {
								value = new Long(constValue);
							} else if (columnType
									.equalsIgnoreCase(serdeConstants.FLOAT_TYPE_NAME)) {
								value = new Float(constValue);
							} else if (columnType
									.equalsIgnoreCase(serdeConstants.DOUBLE_TYPE_NAME)) {
								triedDouble = true;
								value = new Double(constValue);
							} else if (columnType
									.equalsIgnoreCase(serdeConstants.STRING_TYPE_NAME)) {
								// Don't scramble the const type information if
								// comparing to a string column,
								// It's not useful to do so; as of now, there is
								// also a hack in
								// SemanticAnalyzer#genTablePlan that causes
								// every column to look like a string
								// a string down here, so number type
								// information is always lost otherwise.
								final boolean isNumber = (originalValue instanceof Number);
								triedDouble = !isNumber;
								value = isNumber ? (Number) originalValue
										: new Double(constValue);
							}
						} catch (final NumberFormatException nfe) {
							// this exception suggests the precise type
							// inference did not succeed
							// we'll try again to convert it to double
							// however, if we already tried this, or the column
							// is NUMBER type and
							// the operator is EQUAL, return false due to the
							// type mismatch
							if (triedDouble
									|| ((genericUDF instanceof GenericUDFOPEqual) && !columnType
											.equals(serdeConstants.STRING_TYPE_NAME))) {
								return new ExprNodeConstantDesc(false);
							}

							try {
								value = new Double(constValue);
							} catch (final NumberFormatException ex) {
								return new ExprNodeConstantDesc(false);
							}
						}

						if (value != null) {
							children.set(constIdx, new ExprNodeConstantDesc(
									value));
						}
					}
				}

				desc = ExprNodeNewGenericFuncDesc.newInstance(genericUDF,
						children);
			}
			// UDFOPPositive is a no-op.
			// However, we still create it, and then remove it here, to make
			// sure we
			// only allow
			// "+" for numeric types.
			if (FunctionRegistry.isOpPositive(desc)) {
				assert (desc.getChildren().size() == 1);
				desc = desc.getChildren().get(0);
			}
			assert (desc != null);
			return desc;
		}

		/**
		 * Returns true if des is a descendant of ans (ancestor)
		 */
		private boolean isDescendant(final Node ans, final Node des) {
			if (ans.getChildren() == null) {
				return false;
			}
			for (final Node c : ans.getChildren()) {
				if (c == des) {
					return true;
				}
				if (isDescendant(c, des)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public ExprNodeDesc process(final Node nd, final Stack<Node> stack,
				final NodeProcessorCtx procCtx,
				final ArrayList<ExprNodeDesc> nodeOutputs) throws Exception {

			final TypeCheckCtxTRC ctx = (TypeCheckCtxTRC) procCtx;

			final ExprNodeDesc desc = processGByExpr(nd, procCtx);
			if (desc != null) {
				// Here we know nd represents a group by expression.

				// During the DFS traversal of the AST, a descendant of nd
				// likely set an
				// error because a sub-tree of nd is unlikely to also be a group
				// by
				// expression. For example, in a query such as
				// SELECT *concat(key)* FROM src GROUP BY concat(key), 'key'
				// will be
				// processed before 'concat(key)' and since 'key' is not a group
				// by
				// expression, an error will be set in ctx by
				// ColumnExprProcessor.

				// We can clear the global error when we see that it was set in
				// a
				// descendant node of a group by expression because
				// processGByExpr() returns a ExprNodeDesc that effectively
				// ignores
				// its children. Although the error can be set multiple times by
				// descendant nodes, DFS traversal ensures that the error only
				// needs to
				// be cleared once. Also, for a case like
				// SELECT concat(value, concat(value))... the logic still works
				// as the
				// error is only set with the first 'value'; all node pocessors
				// quit
				// early if the global error is set.

				if (isDescendant(nd, ctx.getErrorSrcNode())) {
					ctx.setError(null, null);
				}
				return desc;
			}

			if (ctx.getError() != null) {
				return null;
			}

			final ASTNodeTRC expr = (ASTNodeTRC) nd;

			/*
			 * A Windowing specification get added as a child to a UDAF
			 * invocation to distinguish it from similar UDAFs but on different
			 * windows. The UDAF is translated to a WindowFunction invocation in
			 * the PTFTranslator. So here we just return null for tokens that
			 * appear in a Window Specification. When the traversal reaches up
			 * to the UDAF invocation its ExprNodeDesc is build using the
			 * ColumnInfo in the InputRR. This is similar to how UDAFs are
			 * handled in Select lists. The difference is that there is
			 * translation for Window related tokens, so we just return null;
			 */
			// if (windowingTokens.contains(expr.getType())) {
			// return null;
			// }

			if (expr.getType() == TrcParser.TOK_TABNAME) {
				return null;
			}

			if (expr.getType() == TrcParser.TOK_ALLCOLREF) {
				final RowResolverTRC input = ctx.getInputRR();
				final ExprNodeColumnListDesc columnList = new ExprNodeColumnListDesc();
				assert expr.getChildCount() <= 1;
				if (expr.getChildCount() == 1) {
					// table aliased (select a.*, for example)
					final ASTNodeTRC child = (ASTNodeTRC) expr.getChild(0);
					assert child.getType() == TrcParser.TOK_TABNAME;
					assert child.getChildCount() == 1;
					final String tableAlias = ParseStringUtil
							.unescapeIdentifier(child.getChild(0).getText());
					final HashMap<String, ColumnInfoTRC> columns = input
							.getFieldMap(tableAlias);
					if (columns == null) {
						// throw new SemanticException(
						// ErrorMsg.INVALID_TABLE_ALIAS.getMsg(child));
					}
					for (final Map.Entry<String, ColumnInfoTRC> colMap : columns
							.entrySet()) {
						final ColumnInfoTRC colInfo = colMap.getValue();
						if (!colInfo.getIsVirtualCol()) {
							columnList.addColumn(new ExprNodeColumnDesc(colInfo
									.getType(), colInfo.getInternalName(),
									colInfo.getTabAlias(), false));
						}
					}
				} else {
					// all columns (select *, for example)
					for (final ColumnInfoTRC colInfo : input.getColumnInfos()) {
						if (!colInfo.getIsVirtualCol()) {
							columnList.addColumn(new ExprNodeColumnDesc(colInfo
									.getType(), colInfo.getInternalName(),
									colInfo.getTabAlias(), false));
						}
					}
				}
				return columnList;
			}

			// If the first child is a TOK_TABLE_OR_COL, and nodeOutput[0] is
			// NULL,
			// and the operator is a DOT, then it's a table column reference.
			if ((expr.getType() == TrcParser.DOT)
					&& (expr.getChild(0).getType() == TrcParser.TOK_TABLE_OR_COL)
					&& (nodeOutputs.get(0) == null)) {
				final RowResolverTRC input = ctx.getInputRR();
				final String tableAlias = ParseStringUtil
						.unescapeIdentifier(expr.getChild(0).getChild(0)
								.getText());
				// NOTE: tableAlias must be a valid non-ambiguous table alias,
				// because we've checked that in TOK_TABLE_OR_COL's process
				// method.
				final ColumnInfoTRC colInfo = input.get(tableAlias,
						((ExprNodeConstantDesc) nodeOutputs.get(1)).getValue()
								.toString());

				if (colInfo == null) {
					ctx.setError(
							ErrorMsg.INVALID_COLUMN.getMsg(expr.getChild(1)),
							expr);
					return null;
					// throw new RuntimeException("error in process column : "
					// + tableAlias + " can not get from input : "
					// + input.toString());
				}
				return new ExprNodeColumnDesc(colInfo.getType(),
						colInfo.getInternalName(), colInfo.getTabAlias(),
						colInfo.getIsVirtualCol());
			}

			// Return nulls for conversion operators
			if (conversionFunctionTextHashMap.keySet().contains(expr.getType())
					|| specialFunctionTextHashMap.keySet().contains(
							expr.getType())
					|| (expr.getToken().getType() == TrcParser.CharSetName)
					|| (expr.getToken().getType() == TrcParser.CharSetLiteral)) {
				return null;
			}

			final boolean isFunction = ((expr.getType() == TrcParser.TOK_FUNCTION)
					|| (expr.getType() == TrcParser.TOK_FUNCTIONSTAR) || (expr
					.getType() == TrcParser.TOK_FUNCTIONDI));

			// Create all children
			final int childrenBegin = (isFunction ? 1 : 0);
			final ArrayList<ExprNodeDesc> children = new ArrayList<ExprNodeDesc>(
					expr.getChildCount() - childrenBegin);
			for (int ci = childrenBegin; ci < expr.getChildCount(); ci++) {
				if (nodeOutputs.get(ci) instanceof ExprNodeColumnListDesc) {
					children.addAll(((ExprNodeColumnListDesc) nodeOutputs
							.get(ci)).getChildren());
				} else {
					children.add(nodeOutputs.get(ci));
				}
				System.err.println("expr-child : "
						+ expr.toStringTree()
						+ " : "
						+ ci
						+ " : "
						+ nodeOutputs.get(ci)
						+ " : "
						+ (nodeOutputs.get(ci) == null ? "" : nodeOutputs.get(
								ci).getClass()));
			}

			if (expr.getType() == TrcParser.TOK_FUNCTIONSTAR) {
				final RowResolverTRC input = ctx.getInputRR();
				for (final ColumnInfoTRC colInfo : input.getColumnInfos()) {
					if (!colInfo.getIsVirtualCol()) {
						children.add(new ExprNodeColumnDesc(colInfo.getType(),
								colInfo.getInternalName(), colInfo
										.getTabAlias(), false));
					}
				}
			}

			// If any of the children contains null, then return a null
			// this is a hack for now to handle the group by case
			if (children.contains(null)) {
				// RowResolverTRC input = ctx.getInputRR();
				// List<String> possibleColumnNames = input
				// .getReferenceableColumnAliases(null, -1);
				// String reason = String.format(
				// "(possible column names are: %s)",
				// StringUtils.join(possibleColumnNames, ", "));
				// ctx.setError(ErrorMsg.INVALID_COLUMN.getMsg(expr.getChild(0),
				// reason), expr);
				return null;
			}

			// Create function desc
			// System.err.println("\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
			// + expr.toStringTree() + "\n");
			final ExprNodeDesc funcdesc = getXpathOrFuncExprNodeDesc(expr,
					isFunction, children, ctx);
			// if (funcdesc instanceof ExprNodeGenericFuncDesc
			// && !(((ExprNodeGenericFuncDesc) funcdesc)
			// .getGenericUDF() instanceof GenericUDFBridge)) {
			if (funcdesc instanceof ExprNodeGenericFuncDesc) {
				return new ExprNodeNewGenericFuncDesc(
						(ExprNodeGenericFuncDesc) funcdesc);
			}

			return funcdesc;
		}

	}

	/**
	 * Factory method to get DefaultExprProcessor.
	 *
	 * @return DefaultExprProcessor.
	 */
	public static DefaultExprProcessor getDefaultExprProcessor() {
		return new DefaultExprProcessor();
	}

	public static ExprNodeDesc generateExprNodeGenericFuncDesc(
			final ExprNodeDesc predicate) {
		if (!(predicate instanceof ExprNodeNewGenericFuncDesc)) {
			return predicate;
		}

		final ExprNodeNewGenericFuncDesc desc = (ExprNodeNewGenericFuncDesc) predicate;

		final ArrayList<ExprNodeDesc> children = new ArrayList<ExprNodeDesc>();
		for (final ExprNodeDesc child : predicate.getChildren()) {
			children.add(generateExprNodeGenericFuncDesc(child));
		}

		return new ExprNodeNewGenericFuncDesc(predicate.getTypeInfo(),
				desc.getGenericUDFClass(), desc.getGenericUDFBridge(), children);
	}
}
