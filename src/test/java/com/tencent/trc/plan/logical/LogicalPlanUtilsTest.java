package com.tencent.easycount.plan.logical;

import java.util.ArrayList;

import com.tencent.easycount.metastore.MetaUtils;
import com.tencent.easycount.parse.ASTNodeTRC;
import com.tencent.easycount.plan.logical.LogicalPlan;
import com.tencent.easycount.plan.logical.LogicalPlanGenerator;
import com.tencent.easycount.plan.logical.OpDesc;
import com.tencent.easycount.util.graph.GraphPrinter;

public class LogicalPlanUtilsTest {
	public static void main(String[] args) throws Exception {

		// String sql1 =
		// "INSERT INTO xxx SELECT a, sum(b) FROM (SELECT * FROM lll) yy LEFT JOIN xx ON xx.a=yy.a WHERE x>100 AND y!=1000 GROUP BY a";
		// String sql =
		// "WITH (SELECT * FROM ttt1 WHERE cc=10 AND dd<1000) subt1, "
		// +
		// "(SELECT A, B, C, COUNT(1) FROM ttt2 GROUP BY A, B, C HAVING(COUNT(1) > 100) ) subt2, "
		// +
		// "(SELECT * FROM ttt3 UNION ALL SELECT * FROM subt1 UNION ALL SELECT * FROM subt2) subt3 "
		// +
		// "INSERT INTO xxx SELECT a, sum(b) FROM (SELECT * FROM subt1) yy LEFT JOIN subt2 xx , subt3 zzz "
		// + "ON xx.a=yy.a AND xx.a=zzz.a WHERE x>100 AND y!=1000 GROUP BY a";

		String sql = "INSERT INTO yy SELECT a, b FROM xx";

		ParseDriver pd = new ParseDriver();
		ASTNodeTRC tree = pd.parse(sql);
		System.out.println(tree.toStringTree());
		QB qb = ParseUtils.generateQb(tree);

		for (Node n : qb.getRootQueryNodes()) {
			System.out.println(n + " " + ((Query) n).getN().toStringTree());
		}

		MetaData md = MetaUtils.getTestMetaData(qb);
		LogicalPlan lPlan = new LogicalPlanGenerator(tree, qb, md)
				.generateLogicalPlan();

		ArrayList<Node> rootOpDescs = new ArrayList<Node>();
		for (OpDesc op : lPlan.getRootOps()) {
			rootOpDescs.add(op);
			System.out.println(op.getName());
			for (Node node : op.getChildren()) {
				System.out.println(node.getName());
			}
		}

		GraphPrinter.print(qb.getRootQueryNodes(), null);

		GraphPrinter.print(rootOpDescs, null);

	}
}
