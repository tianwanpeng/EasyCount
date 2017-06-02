package com.tencent.trc.plan.logical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.tencent.trc.parse.ASTNodeTRC;
import com.tencent.trc.parse.ParseStringUtil;
import com.tencent.trc.parse.TrcParser;

public class JoinUtils {

	public static void generateJoinTree(ASTNodeTRC joinOnExpr,
			String streamAlias, final ArrayList<String> joinDTableAliass,
			HashMap<String, Condition> table2KeyAst) {

		ArrayList<Condition> conditions = new ArrayList<Condition>();

		parseAliass(joinOnExpr, conditions);

		for (Condition condition : conditions) {
			if (!condition.leftIsTable && !condition.rightIsTable) {
				throw new RuntimeException("demision table must be a table ");
			}
			if (leftIsStreamTable(condition, streamAlias)) {
				if (rightIsDimentionTable(condition, joinDTableAliass)) {
					String alias = condition.rightAliass.iterator().next();
					if (!table2KeyAst.containsKey(alias)) {
						condition.leftIsStream = true;
						table2KeyAst.put(alias, condition);
					} else {
						throw new RuntimeException("alias " + alias
								+ " exist in more than 1 condition");
					}
				} else {
					throw new RuntimeException("wrong dimension table : "
							+ condition + " : " + joinDTableAliass);
				}
			} else if (rightIsStreamTable(condition, streamAlias)) {
				if (leftIsDimentionTable(condition, joinDTableAliass)) {
					String alias = condition.leftAliass.iterator().next();
					if (!table2KeyAst.containsKey(alias)) {
						condition.leftIsStream = false;
						table2KeyAst.put(alias, condition);
					} else {
						throw new RuntimeException("alias " + alias
								+ " exist in more than 1 condition");
					}
				} else {
					throw new RuntimeException("wrong dimension table");
				}
			} else {
				throw new RuntimeException("no stream table : " + streamAlias
						+ " : " + condition);
			}
		}
	}

	private static boolean leftIsDimentionTable(Condition condition,
			ArrayList<String> jTables) {
		return condition.leftIsTable && condition.leftAliass.size() == 1
				&& jTables.containsAll(condition.leftAliass);
	}

	private static boolean rightIsDimentionTable(Condition condition,
			ArrayList<String> jTables) {
		return condition.rightIsTable && condition.rightAliass.size() == 1
				&& jTables.containsAll(condition.rightAliass);
	}

	private static boolean leftIsStreamTable(Condition condition,
			String streamTable) {
		return condition.leftAliass.size() == 1
				&& condition.leftAliass.contains(streamTable);
	}

	private static boolean rightIsStreamTable(Condition condition,
			String streamTable) {
		return condition.rightAliass.size() == 1
				&& condition.rightAliass.contains(streamTable);
	}

	private static void parseAliass(ASTNodeTRC exprNode,
			ArrayList<Condition> conditions) {

		switch (exprNode.getToken().getType()) {
		case TrcParser.KW_AND:
			parseAliass((ASTNodeTRC) exprNode.getChild(0), conditions);
			parseAliass((ASTNodeTRC) exprNode.getChild(1), conditions);
			break;
		case TrcParser.EQUAL:
			ASTNodeTRC leftAst = (ASTNodeTRC) exprNode.getChild(0);
			ASTNodeTRC rightAst = (ASTNodeTRC) exprNode.getChild(1);
			Condition condition = new Condition();
			condition.leftAst = leftAst;
			condition.rightAst = rightAst;
			boolean leftIsTable = parseAliass1(leftAst, condition.leftAliass,
					true);
			boolean rightIsTable = parseAliass1(rightAst,
					condition.rightAliass, true);
			condition.leftIsTable = leftIsTable;
			condition.rightIsTable = rightIsTable;
			conditions.add(condition);
			break;

		default:
			throw new RuntimeException(
					"only 'and' and 'equal' is allowed in on condation expr ... ");
		}
	}

	private static boolean parseAliass1(ASTNodeTRC exprNode,
			HashSet<String> aliass, boolean root) {
		switch (exprNode.getToken().getType()) {
		// case TrcParser.TOK_TABLE_OR_COL:
		// String tableOrCol = ParseStringUtil.unescapeIdentifier(exprNode
		// .getChild(0).getText().toLowerCase());
		//
		// aliass.add(tableOrCol);
		// if (root) {
		// return true;
		// }
		case TrcParser.DOT:
			ASTNodeTRC child = (ASTNodeTRC) exprNode.getChild(0);
			if (child.getToken().getType() == TrcParser.TOK_TABLE_OR_COL) {
				String tableOrCol = ParseStringUtil.unescapeIdentifier(child
						.getChild(0).getText());

				aliass.add(tableOrCol);
				if (root) {
					return true;
				}
				break;
			} else {
				parseAliass1((ASTNodeTRC) exprNode.getChild(0), aliass, false);
				parseAliass1((ASTNodeTRC) exprNode.getChild(1), aliass, false);
			}
		case TrcParser.Identifier:
		case TrcParser.Number:
		case TrcParser.StringLiteral:
		case TrcParser.BigintLiteral:
		case TrcParser.SmallintLiteral:
		case TrcParser.TinyintLiteral:
			// case TrcParser.DecimalLiteral:
		case TrcParser.TOK_STRINGLITERALSEQUENCE:
		case TrcParser.TOK_CHARSETLITERAL:
		case TrcParser.KW_TRUE:
		case TrcParser.KW_FALSE:
			break;

		case TrcParser.TOK_FUNCTION:
			// check all the arguments
			for (int i = 1; i < exprNode.getChildCount(); i++) {
				parseAliass1((ASTNodeTRC) exprNode.getChild(i), aliass, false);
			}
			break;

		default:
			// This is an operator - so check whether it is unary or binary
			// operator
			if (exprNode.getChildCount() == 1) {
				parseAliass1((ASTNodeTRC) exprNode.getChild(0), aliass, false);
			} else if (exprNode.getChildCount() == 2) {
				parseAliass1((ASTNodeTRC) exprNode.getChild(0), aliass, false);
				parseAliass1((ASTNodeTRC) exprNode.getChild(1), aliass, false);
			} else {
				throw new RuntimeException(exprNode.toStringTree()
						+ " encountered with " + exprNode.getChildCount()
						+ " children");
			}
			break;
		}
		return false;
	}

	public static class Condition {
		boolean leftIsStream = true;
		ASTNodeTRC leftAst;
		ASTNodeTRC rightAst;
		boolean leftIsTable = true;
		boolean rightIsTable = true;
		HashSet<String> leftAliass = new HashSet<String>();
		HashSet<String> rightAliass = new HashSet<String>();

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("leftIsStream:" + leftIsStream + ", leftAst:"
					+ leftAst.toStringTree() + ", rightAst:"
					+ rightAst.toStringTree() + ", leftIsTable:" + leftIsTable
					+ ", rightIsTable:" + rightIsTable + ", leftAliass:"
					+ leftAliass + ", rightAliass:" + rightAliass);
			return sb.toString();
		}
	}
}
