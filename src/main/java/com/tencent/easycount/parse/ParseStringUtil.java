package com.tencent.easycount.parse;

import java.io.UnsupportedEncodingException;

import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeDescUtils;

import com.tencent.easycount.plan.RowResolverTRC;

public class ParseStringUtil {
	public static String getUnescapedName(ASTNodeTRC tableNameNode) {
		if (tableNameNode.getToken().getType() == TrcParser.TOK_TABNAME) {
			if (tableNameNode.getChildCount() == 2) {
				String dbName = unescapeIdentifier(tableNameNode.getChild(0)
						.getText());
				String tableName = unescapeIdentifier(tableNameNode.getChild(1)
						.getText());
				return dbName + "." + tableName;
			} else {
				return unescapeIdentifier(tableNameNode.getChild(0).getText());
			}
		}
		return unescapeIdentifier(tableNameNode.getText());
	}

	private static final int[] multiplier = new int[] { 1000, 100, 10, 1 };

	public static String unescapeSQLString(String b) {

		Character enclosure = null;

		// Some of the strings can be passed in as unicode. For example, the
		// delimiter can be passed in as \002 - So, we first check if the
		// string is a unicode number, else go back to the old behavior
		StringBuilder sb = new StringBuilder(b.length());
		for (int i = 0; i < b.length(); i++) {

			char currentChar = b.charAt(i);
			if (enclosure == null) {
				if (currentChar == '\'' || b.charAt(i) == '\"') {
					enclosure = currentChar;
				}
				// ignore all other chars outside the enclosure
				continue;
			}

			if (enclosure.equals(currentChar)) {
				enclosure = null;
				continue;
			}

			if (currentChar == '\\' && (i + 6 < b.length())
					&& b.charAt(i + 1) == 'u') {
				int code = 0;
				int base = i + 2;
				for (int j = 0; j < 4; j++) {
					int digit = Character.digit(b.charAt(j + base), 16);
					code += digit * multiplier[j];
				}
				sb.append((char) code);
				i += 5;
				continue;
			}

			if (currentChar == '\\' && (i + 4 < b.length())) {
				char i1 = b.charAt(i + 1);
				char i2 = b.charAt(i + 2);
				char i3 = b.charAt(i + 3);
				if ((i1 >= '0' && i1 <= '1') && (i2 >= '0' && i2 <= '7')
						&& (i3 >= '0' && i3 <= '7')) {
					byte bVal = (byte) ((i3 - '0') + ((i2 - '0') * 8) + ((i1 - '0') * 8 * 8));
					byte[] bValArr = new byte[1];
					bValArr[0] = bVal;
					String tmp = new String(bValArr);
					sb.append(tmp);
					i += 3;
					continue;
				}
			}

			if (currentChar == '\\' && (i + 2 < b.length())) {
				char n = b.charAt(i + 1);
				switch (n) {
				case '0':
					sb.append("\0");
					break;
				case '\'':
					sb.append("'");
					break;
				case '"':
					sb.append("\"");
					break;
				case 'b':
					sb.append("\b");
					break;
				case 'n':
					sb.append("\n");
					break;
				case 'r':
					sb.append("\r");
					break;
				case 't':
					sb.append("\t");
					break;
				case 'Z':
					sb.append("\u001A");
					break;
				case '\\':
					sb.append("\\");
					break;
				// The following 2 lines are exactly what MySQL does
				case '%':
					sb.append("\\%");
					break;
				case '_':
					sb.append("\\_");
					break;
				default:
					sb.append(n);
				}
				i++;
			} else {
				sb.append(currentChar);
			}
		}
		return sb.toString();
	}

	public static String charSetString(String charSetName, String charSetString) {
		try {
			// The character set name starts with a _, so strip that
			charSetName = charSetName.substring(1);
			if (charSetString.charAt(0) == '\'') {
				return new String(unescapeSQLString(charSetString).getBytes(),
						charSetName);
			} else // hex input is also supported
			{
				assert charSetString.charAt(0) == '0';
				assert charSetString.charAt(1) == 'x';
				charSetString = charSetString.substring(2);

				byte[] bArray = new byte[charSetString.length() / 2];
				int j = 0;
				for (int i = 0; i < charSetString.length(); i += 2) {
					int val = Character.digit(charSetString.charAt(i), 16) * 16
							+ Character.digit(charSetString.charAt(i + 1), 16);
					if (val > 127) {
						val = val - 256;
					}
					bArray[j++] = (byte) val;
				}

				String res = new String(bArray, charSetName);
				return res;
			}
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	/**
	 * Remove the encapsulating "`" pair from the identifier. We allow users to
	 * use "`" to escape identifier for table names, column names and aliases,
	 * in case that coincide with Hive language keywords.
	 */
	public static String unescapeIdentifier(String val) {
		if (val == null) {
			return null;
		}
		if (val.charAt(0) == '`' && val.charAt(val.length() - 1) == '`') {
			val = val.substring(1, val.length() - 1);
		}
		return val;
	}

	public static String stripQuotes(String val) {
		if ((val.charAt(0) == '\'' && val.charAt(val.length() - 1) == '\'')
				|| (val.charAt(0) == '\"' && val.charAt(val.length() - 1) == '\"')) {
			val = val.substring(1, val.length() - 1);
		}
		return val;
	}

	public static String[] getColAlias(ASTNodeTRC selExpr, String defaultName,
			RowResolverTRC inputRR, boolean includeFuncName, int colNum) {
		String colAlias = null;
		String tabAlias = null;
		String[] colRef = new String[2];

		// for queries with a windowing expressions, the selexpr may have a
		// third child
		// if (selExpr.getChildCount() == 2
		// || (selExpr.getChildCount() == 3 && selExpr.getChild(2)
		// .getType() == TrcParser.TOK_WINDOWSPEC)) {
		// // return zz for "xx + yy AS zz"
		// colAlias = unescapeIdentifier(selExpr.getChild(1).getText());
		// colRef[0] = tabAlias;
		// colRef[1] = colAlias;
		// return colRef;
		// }

		ASTNodeTRC root = (ASTNodeTRC) selExpr.getChild(0);
		if (root.getType() == TrcParser.TOK_TABLE_OR_COL) {
			colAlias = unescapeIdentifier(root.getChild(0).getText());
			colRef[0] = tabAlias;
			colRef[1] = colAlias;
			return colRef;
		}

		if (root.getType() == TrcParser.DOT) {
			ASTNodeTRC tab = (ASTNodeTRC) root.getChild(0);
			if (tab.getType() == TrcParser.TOK_TABLE_OR_COL) {
				String t = unescapeIdentifier(tab.getChild(0).getText());
				if (inputRR.hasTableAlias(t)) {
					tabAlias = t;
				}
			}

			// Return zz for "xx.zz" and "xx.yy.zz"
			ASTNodeTRC col = (ASTNodeTRC) root.getChild(1);
			if (col.getType() == TrcParser.Identifier) {
				colAlias = unescapeIdentifier(col.getText());
			}
		}

		// if specified generate alias using func name
		if (includeFuncName && (root.getType() == TrcParser.TOK_FUNCTION)) {

			String expr_flattened = root.toStringTree();

			// remove all TOK tokens
			String expr_no_tok = expr_flattened.replaceAll("TOK_\\S+", "");

			// remove all non alphanumeric letters, replace whitespace spans
			// with underscore
			String expr_formatted = expr_no_tok.replaceAll("\\W", " ").trim()
					.replaceAll("\\s+", "_");

			// limit length to 20 chars
			// if (expr_formatted.length() > AUTOGEN_COLALIAS_PRFX_MAXLENGTH) {
			// expr_formatted = expr_formatted.substring(0,
			// AUTOGEN_COLALIAS_PRFX_MAXLENGTH);
			// }

			// append colnum to make it unique
			colAlias = expr_formatted.concat("_" + colNum);
		}

		if (colAlias == null) {
			// Return defaultName if selExpr is not a simple xx.yy.zz
			colAlias = defaultName + colNum;
		}

		colRef[0] = tabAlias;
		colRef[1] = colAlias;
		return colRef;
	}

	public static boolean isRegex(String pattern) {
		for (int i = 0; i < pattern.length(); i++) {
			if (!Character.isLetterOrDigit(pattern.charAt(i))
					&& pattern.charAt(i) != '_') {
				return true;
			}
		}
		return false;
	}

	public static String recommendName(ExprNodeDesc exp, String colAlias) {
		// if (!colAlias.startsWith(autogenColAliasPrfxLbl)) {
		// return null;
		// }
		String column = ExprNodeDescUtils.recommendInputName(exp);
		// if (column != null && !column.startsWith(autogenColAliasPrfxLbl)) {
		// return column;
		// }
		return column;
	}

}
