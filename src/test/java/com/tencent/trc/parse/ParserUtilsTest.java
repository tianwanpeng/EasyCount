package com.tencent.easycount.parse;

import junit.framework.Assert;

import org.antlr.runtime.tree.Tree;
import org.junit.Test;

import com.tencent.easycount.parse.ASTNodeTRC;

public class ParserUtilsTest {
	private ASTNodeTRC genAstTree(String cmd) throws Exception {
		return new ParseDriver().parse(cmd);
	}

	private static void makesureClass(Tree tree) {
		Assert.assertEquals("class com.tencent.trc.parse.ASTNode", tree
				.getClass().toString());
		for (int i = 0; i < tree.getChildCount(); i++) {
			makesureClass(tree.getChild(i));
		}
	}

	@Test
	public void testParser() throws Exception {
		String cmd = "INSERT INTO xxx SELECT a, sum(b) FROM (SELECT * FROM bb UNION ALL SELECT * FROM vv UNION ALL SELECT * FROM gg) dd LEFT JOIN fff ON dd.x=fff.b WHERE x>100 AND y!=1000 GROUP BY a";
		ASTNodeTRC tree = genAstTree(cmd);
		makesureClass(tree);
		QB qb = ParseUtils.generateQb(tree);
		System.out.println(qb);
		System.out.println(qb.getAllAliases());
		System.out.println(qb.getAliasToQuery());
		System.out.println(qb.getAsttreeToQuery());
		System.out.println(qb.getAstnodeToQuery());
		System.out.println(qb.getAstnodeToAlias());
		System.out.println(qb.getRootQuerys());

	}

	@Test
	public void testParser1() throws Exception {
		String cmd = "WITH (SELECT * FROM ttt1 WHERE cc=10 AND dd<1000) subt1, "
				+ "(SELECT A, B, C, COUNT(1) FROM ttt2 GROUP BY A, B, C HAVING(COUNT(1) > 100) ) subt2, "
				+ "(SELECT * FROM ttt3 UNION ALL SELECT * FROM subt1 UNION ALL SELECT * FROM subt2) subt3 "
				+ "INSERT INTO xxx SELECT a, sum(b) FROM (SELECT * FROM subt1) yy LEFT JOIN subt2 xx , subt3 zzz "
				+ "ON xx.a=yy.a AND xx.a=zzz.a WHERE x>100 AND y!=1000 GROUP BY a";
		ASTNodeTRC tree = genAstTree(cmd);
		makesureClass(tree);
	}

	public static void main(String[] args) throws Exception {
		ParserUtilsTest put = new ParserUtilsTest();
		String cmd = "INSERT INTO xxx SELECT a, sum(b) FROM (SELECT * FROM lll) yy LEFT JOIN xx ON xx.a=yy.a WHERE x>100 AND y!=1000 GROUP BY a";
		ASTNodeTRC tree = put.genAstTree(cmd);
		System.out.println(cmd);
		System.out.println(tree.toStringTree());
		makesureClass(tree);

		// QB qb = ParseUtils.generateQb(tree);
		//
		// System.out.println(qb.allAliases().size());
		// System.out.println(qb.aliasToSubq());
		// System.out.println(qb.astTreeToSubq().size());
		// for (Query q : qb.getAliasToSubq().values()) {
		// System.out.println(q);
		// for (Query qq : q.children()) {
		// System.out.println("subq-" + qq);
		// }
		// }

	}

}
