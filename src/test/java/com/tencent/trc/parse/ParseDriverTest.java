package com.tencent.easycount.parse;

import com.tencent.easycount.parse.ASTNodeTRC;

public class ParseDriverTest {
	public static void main(String[] args) throws Exception {
		ParseDriver pd = new ParseDriver();
		String sql = "INSERT INTO xxx SELECT a, sum(b) FROM (SELECT * FROM bb UNION ALL SELECT * FROM vv UNION ALL SELECT * FROM gg) dd WHERE x>100 AND y!=1000 GROUP BY a";
		ASTNodeTRC astTree = pd.parse(sql);
		System.out.println(astTree.toStringTree());
	}
}
