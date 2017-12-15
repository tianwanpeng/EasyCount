package com.tencent.easycount.plan.physical;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import prefuse.data.Graph;
import prefuse.data.io.GraphMLReader;

import com.tencent.easycount.conf.ECConfiguration;
import com.tencent.easycount.metastore.MetaData;
import com.tencent.easycount.metastore.MetaUtils;
import com.tencent.easycount.parse.ASTNodeTRC;
import com.tencent.easycount.parse.ParseDriver;
import com.tencent.easycount.parse.ParseUtils;
import com.tencent.easycount.parse.QB;
import com.tencent.easycount.plan.logical.LogicalPlan;
import com.tencent.easycount.plan.logical.LogicalPlanGenerator;
import com.tencent.easycount.plan.logical.OpDesc;
import com.tencent.easycount.util.graph.GraphDrawer;
import com.tencent.easycount.util.graph.GraphPrinter;
import com.tencent.easycount.util.graph.GraphPrinter.CallBack;
import com.tencent.easycount.util.graph.GraphWalker;
import com.tencent.easycount.util.graph.GraphWalker.Dispatcher;
import com.tencent.easycount.util.graph.GraphWalker.Node;
import com.tencent.easycount.util.graph.GraphWalker.WalkMode;
import com.tencent.easycount.util.graph.GraphXmlBuilder;

public class PhysicalPlanUtilsTest {

	public static void main(final String[] args) throws Exception {

		// String sql =
		// "INSERT INTO xxx SELECT a, sum(b) FROM (SELECT * FROM lll) yy LEFT JOIN xx ON xx.a=yy.a WHERE x>100 AND y!=1000 GROUP BY a";
		// String sql =
		// "WITH (SELECT * FROM xx WHERE cc=10 AND dd<1000) subt1, "
		// +
		// "(SELECT A, B, C, COUNT(1) FROM yy GROUP BY A, B, C HAVING(COUNT(1) > 100) ) subt2, "
		// +
		// "(SELECT * FROM zz UNION ALL SELECT * FROM subt1 UNION ALL SELECT * FROM subt2) subt3 "
		// +
		// "INSERT INTO xxx SELECT a, sum(b) FROM (SELECT * FROM subt1) yyy LEFT JOIN subt2 xxx , subt3 zzz "
		// +
		// "ON xxx.a=yyy.a AND xxx.a=zzz.a WHERE x>100 AND y!=1000 GROUP BY a";

		// String sql = "INSERT INTO yy SELECT a,b FROM xx WHERE c > 100";
		String sql = "INSERT INTO yy SELECT a, count(b), sum(x), sum(y), sum(z), sum(p) FROM (SELECT a, b, sum(c) x, sum(d) y, sum(c) + sum(d) z, count(1) p FROM xx WHERE c + d >= 0 GROUP BY a, b) tmp GROUP BY a";
		sql = "INSERT INTO ieg_bd_dl_result SELECT from_unixtime(AGGRTIME DIV 1000, 'yyyyMMddHHmm'),custom_id,count(1),sum(complete),avg(total_filesize / total_duration),collect_set(CASE complete WHEN '1' THEN uin ELSE null END),collect_set(CASE complete WHEN '0' THEN uin ELSE null END) FROM ieg_bd_download GROUP BY custom_id COORDINATE BY unix_timestamp(time_t, 'yyyyMMddHHmmss')*1000 WITH AGGR INTERVAL 60 SECONDS, INSERT INTO ieg_bd_dl_result1 SELECT from_unixtime(AGGRTIME DIV 1000, 'yyyyMMddHHmm'),province,isp,custom_id,count(1),sum(complete),avg(total_filesize / total_duration),collect_set(CASE complete WHEN '1' THEN uin ELSE null END),collect_set(CASE complete WHEN '0' THEN uin ELSE null END) FROM ieg_bd_download GROUP BY custom_id,province,isp COORDINATE BY unix_timestamp(time_t, 'yyyyMMddHHmmss')*1000 WITH AGGR INTERVAL 60 SECONDS";

		final ParseDriver pd = new ParseDriver();
		final ASTNodeTRC tree = pd.parse(sql);
		System.out.println(tree.toStringTree());
		final QB qb = ParseUtils.generateQb(tree);

		// for (Node n : qb.getRootQueryNodes()) {
		// System.out.println(n + " " + ((Query) n).getN().toStringTree());
		// }
		final ECConfiguration config = new ECConfiguration();
		final URL url = new File(config.get("configfile", "lanzuan.ini"))
		.toURI().toURL();
		final Ini ini = new Ini(url);
		int secIdx = 0;
		for (final String seckey : ini.keySet()) {
			final Section section = ini.get(seckey);
			for (final String key : section.keySet()) {
				if (config.get(key) != null) {
					continue;
				}
				final String value = section.get(key);
				config.set(seckey.equals("system") ? key : "sec-" + secIdx
						+ "::" + key, value);
			}
			secIdx++;
		}

		final MetaData md = MetaUtils.getMetaData(qb, config, ini);
		final LogicalPlan lPlan = new LogicalPlanGenerator(tree, qb, md)
				.generateLogicalPlan();

		final ArrayList<Node> rootOpDescs = new ArrayList<Node>();
		for (final OpDesc op : lPlan.getRootOps()) {
			rootOpDescs.add(op);
			// System.out.println(op.getName());
			// for (Node node : op.getChildren()) {
			// System.out.println(node.getName());
			// }
		}

		final PhysicalPlan pPlan = new PhysicalPlanGenerator(qb, md, lPlan)
				.generatePhysicalPlan();

		final HashMap<OpDesc, Integer> opDesc2TaskId = pPlan.getOpDesc2TaskId();

		GraphPrinter.print(qb.getRootQueryNodes(), null);

		final GraphXmlBuilder builder = new GraphXmlBuilder();

		final GraphWalker<String> walker = new GraphWalker<String>(
				new Dispatcher<String>() {

					@Override
					public String dispatch(final Node nd,
							final Stack<Node> stack,
							final ArrayList<String> nodeOutputs,
							final HashMap<Node, String> retMap) {
						builder.addNode(nd.toString(), nd.getName(),
								String.valueOf(opDesc2TaskId.get(nd)));

						for (final Node node : nd.getChildren()) {
							builder.addEdge(nd.toString(), node.toString());
						}
						return "";
					}

					@Override
					public boolean needToDispatchChildren(final Node nd,
							final Stack<Node> stack,
							final ArrayList<String> nodeOutputs,
							final HashMap<Node, String> retMap) {
						return true;
					}
				}, WalkMode.CHILD_FIRST, "GraphXmlBuilder");
		walker.walk(rootOpDescs);

		final String xml = builder.build();
		System.out.println(xml);

		final ByteBuffer buffer = ByteBuffer.wrap(xml.getBytes());

		final InputStream is = new InputStream() {

			@Override
			public int read() throws IOException {
				if (!buffer.hasRemaining()) {
					return -1;
				}
				return buffer.get();
			}
		};
		final Graph graph = new GraphMLReader().readGraph(is);

		GraphDrawer.draw(graph);

		// final GraphJungBuilder gjbuilder = new GraphJungBuilder();
		// GraphWalker<Vertex> walker1 = new GraphWalker<Vertex>(
		// new Dispatcher<Vertex>() {
		//
		// @Override
		// public Vertex dispatch(Node nd, Stack<Node> stack,
		// ArrayList<Vertex> nodeOutputs,
		// HashMap<Node, Vertex> retMap) {
		// Vertex r = gjbuilder.addVertex(nd.getName());
		// for (Node node : nd.getChildren()) {
		// gjbuilder.addEdge(r, retMap.get(node));
		// }
		// return r;
		// }
		//
		// @Override
		// public boolean needToDispatchChildren(Node nd,
		// Stack<Node> stack, ArrayList<Vertex> nodeOutputs) {
		// return true;
		// }
		// }, WalkMode.CHILD_FIRST);
		// walker1.walk(rootOpDescs);
		// gjbuilder.draw();

		GraphPrinter.print(rootOpDescs, null);
		// System.out.println(pPlan.getRootWorksNodes());
		// System.out.println(pPlan.getRootWorksNodes().get(0).getChildren());
		GraphPrinter.print(pPlan.getRootWorksNodes(), new CallBack() {

			@Override
			public void call(final Node nd) {
				final TaskWork tw = (TaskWork) nd;
				System.out.println("allOp : " + tw.getOpDescs());
				System.out.println("rootOp : " + tw.getRootOpDescs());
				System.out.println("destOp : " + tw.getDestOpDescs());
			}
		});
	}
}
