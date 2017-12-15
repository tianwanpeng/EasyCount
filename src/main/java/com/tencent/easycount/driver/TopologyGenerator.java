package com.tencent.easycount.driver;

import org.ini4j.Ini;

import com.tencent.easycount.conf.ECConfiguration;
import com.tencent.easycount.exec.physical.PhysicalExecGenerator;
import com.tencent.easycount.metastore.MetaData;
import com.tencent.easycount.metastore.MetaUtils;
import com.tencent.easycount.parse.ASTNodeTRC;
import com.tencent.easycount.parse.ParseDriver;
import com.tencent.easycount.parse.ParseUtils;
import com.tencent.easycount.parse.QB;
import com.tencent.easycount.plan.logical.LogicalPlan;
import com.tencent.easycount.plan.logical.LogicalPlanGenerator;
import com.tencent.easycount.plan.physical.PhysicalPlan;
import com.tencent.easycount.plan.physical.PhysicalPlanGenerator;

public class TopologyGenerator {

	static class EcTopology {
		ASTNodeTRC tree;
		QB qb;
		MetaData md;
		LogicalPlan lPlan;
		PhysicalPlan pPlan;
		org.apache.storm.generated.StormTopology topology;

	}

	static EcTopology generateTopology(final ECConfiguration config,
			final Ini ini) throws Exception {

		final EcTopology tt = new EcTopology();
		/**
		 * parse sql to asttree
		 */
		tt.tree = new ParseDriver().parse(config.get("sql"));

		System.err.println(tt.tree.toStringTree());

		/**
		 * generate Query parse info
		 */
		tt.qb = ParseUtils.generateQb(tt.tree);

		System.err.println(tt.qb.printStr());

		/**
		 * check metadata
		 */
		tt.md = MetaUtils.getMetaData(tt.qb, config, ini);

		System.err.println(tt.md.printStr());

		/**
		 * generate logical plan --- opTree
		 */
		tt.lPlan = new LogicalPlanGenerator(tt.tree, tt.qb, tt.md)
		.generateLogicalPlan();

		System.err.println(tt.lPlan.printStr());

		/**
		 * generate physical plan --- taskTree
		 *
		 */
		tt.pPlan = new PhysicalPlanGenerator(tt.qb, tt.md, tt.lPlan)
		.generatePhysicalPlan();

		/**
		 * generate topology
		 */
		final int workNum = config.getInt("work.num", -1);
		tt.topology = new PhysicalExecGenerator(tt.pPlan, config)
		.generateExecTopology(workNum);

		return tt;

	}

}
