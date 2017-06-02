package com.tencent.trc.driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Stack;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import prefuse.data.Graph;
import prefuse.data.io.GraphMLReader;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;

import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.io.local.LocalModeUtils;
import com.tencent.trc.exec.physical.PhysicalExecGenerator;
import com.tencent.trc.metastore.MetaData;
import com.tencent.trc.metastore.MetaUtils;
import com.tencent.trc.parse.ASTNodeTRC;
import com.tencent.trc.parse.ParseDriver;
import com.tencent.trc.parse.ParseUtils;
import com.tencent.trc.parse.QB;
import com.tencent.trc.plan.logical.LogicalPlan;
import com.tencent.trc.plan.logical.LogicalPlanGenerator;
import com.tencent.trc.plan.logical.OpDesc;
import com.tencent.trc.plan.physical.PhysicalPlan;
import com.tencent.trc.plan.physical.PhysicalPlanGenerator;
import com.tencent.trc.udfnew.MyUDFUtils;
import com.tencent.trc.util.graph.GraphDrawer;
import com.tencent.trc.util.graph.GraphWalker;
import com.tencent.trc.util.graph.GraphWalker.Dispatcher;
import com.tencent.trc.util.graph.GraphWalker.Node;
import com.tencent.trc.util.graph.GraphWalker.WalkMode;
import com.tencent.trc.util.graph.GraphXmlBuilder;

public class Driver {
	private static final String version = "TRC-EC-R0.8";

	public static void main(String[] args) throws Exception {

		System.out.println("TRC EC version is : " + version);
		// do some initilize work
		MyUDFUtils.initialize();

		/**
		 * initialize conf
		 */
		TrcConfiguration config = generateConfig(args);

		// URL url = new File(config.get("configfile",
		// "testconfig.ini")).toURI()
		// .toURL();
		InputStreamReader reader = new InputStreamReader(new FileInputStream(
				new File(config.get("configfile", "testconfig.ini"))), "utf8");
		Ini ini = new Ini(reader);
		// Ini ini = new Ini(url);
		for (String seckey : ini.keySet()) {
			Section section = ini.get(seckey);
			for (String key : section.keySet()) {
				if (config.get(key) != null) {
					continue;
				}
				String value = section.get(key);
				config.set(seckey.equals("system") ? key : seckey + "::" + key,
						value);
			}
		}

		String sql = config.get("sql");
		if (sql == null) {
			System.err
					.println("error ::: sql clause must be set, you should specify the configfile .... ");
			System.exit(-1);
		}

		try {
			TrcTopology tt = generateTopology(config, ini);

			if (config.getBoolean("check", false)) {
				System.exit(0);
				return;
			}

			if (config.getBoolean("compile", false)) {
				boolean withRoot = config.getBoolean("with.root", false);
				graphPrint(tt.lPlan, tt.pPlan, withRoot);
				return;
			}

			/**
			 * generate topology and submit it
			 */
			boolean localMode = config.getBoolean("localmode", false);
			if (localMode) {
				LocalModeUtils.setupTables(tt.md, config);
				config.set("moniter.send.status", "false");
			}
			submitTopology(tt.topology, config.get("topologyname"), localMode,
					config, config.getInt("work.num", -1));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	static class TrcTopology {
		ASTNodeTRC tree;
		QB qb;
		MetaData md;
		LogicalPlan lPlan;
		PhysicalPlan pPlan;
		StormTopology topology;

	}

	private static TrcTopology generateTopology(TrcConfiguration config, Ini ini)
			throws Exception {

		TrcTopology tt = new TrcTopology();
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
		int workNum = config.getInt("work.num", -1);
		tt.topology = new PhysicalExecGenerator(tt.pPlan, config)
				.generateExecTopology(workNum);

		return tt;

	}

	private static TrcConfiguration generateConfig(String[] args)
			throws IOException {
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < args.length; i++) {
			int pos = args[i].indexOf('=');
			if (pos <= 0) {
				continue;
			}
			String[] strs = new String[2];
			strs[0] = args[i].substring(0, pos);
			strs[1] = args[i].substring(pos + 1);
			map.put(strs[0].toLowerCase().trim(), strs[1]);
		}

		TrcConfiguration config = new TrcConfiguration();
		String sysconfigfile = map.get("sysconfig");
		if (sysconfigfile != null) {
			URL url = new File(sysconfigfile).toURI().toURL();
			Ini ini = new Ini(url);
			for (String seckey : ini.keySet()) {
				Section section = ini.get(seckey);
				for (String key : section.keySet()) {
					String value = section.get(key);
					config.set(key, value);
				}
			}
		}

		for (String key : map.keySet()) {
			config.set(key, map.get(key));
		}

		String topologyName = config.get("topologyname");
		if (null == topologyName) {
			System.err.println("error ::: topologyname must be set .... ");
			System.exit(-1);
		}

		String groupname = config.get("consumergroup");
		if (null == groupname) {
			config.set("consumergroup", topologyName + "_consumergroup");
		}

		if (config.getBoolean("printversion", false)) {
			System.exit(-1);
		}
		return config;
	}

	private static void submitTopology(StormTopology topology,
			String topologyName, boolean localMode, TrcConfiguration config,
			int workNum) {

		Config conf = new Config();
		Properties pro = config.getAllProperties();

		for (Object keyobj : pro.keySet()) {
			conf.put((String) keyobj, pro.get(keyobj));
		}

		conf.setDebug(false);
		conf.setNumWorkers(workNum <= 0 ? 1 : workNum);
		conf.setNumAckers(workNum / 3 + 1);

		if (localMode) {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(topologyName, conf, topology);
		} else {
			try {
				StormSubmitter.submitTopology(topologyName, conf, topology);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void graphPrint(LogicalPlan lPlan, PhysicalPlan pPlan,
			boolean withRoot) throws Exception {
		ArrayList<Node> rootOpDescs = new ArrayList<Node>();
		if (withRoot) {
			OpDesc rootOp = new OpDesc() {
				private static final long serialVersionUID = -1298273294286915472L;

				@Override
				public String getName() {
					return "ROOT";
				}
			};
			for (OpDesc op : lPlan.getRootOps()) {
				rootOp.addChild(op);
			}
			rootOpDescs.add(rootOp);
		} else {
			for (OpDesc op : lPlan.getRootOps()) {
				rootOpDescs.add(op);
			}
		}

		final GraphXmlBuilder builder = new GraphXmlBuilder();
		final HashMap<OpDesc, Integer> opDesc2TaskId = pPlan.getOpDesc2TaskId();

		GraphWalker<String> walker = new GraphWalker<String>(
				new Dispatcher<String>() {

					@Override
					public String dispatch(Node nd, Stack<Node> stack,
							ArrayList<String> nodeOutputs,
							HashMap<Node, String> retMap) {
						builder.addNode(nd.toString(), nd.getName(),
								String.valueOf(opDesc2TaskId.get(nd)));

						for (Node node : nd.getChildren()) {
							builder.addEdge(nd.toString(), node.toString());
						}
						return "";
					}

					@Override
					public boolean needToDispatchChildren(Node nd,
							Stack<Node> stack, ArrayList<String> nodeOutputs,
							HashMap<Node, String> retMap) {
						return true;
					}
				}, WalkMode.CHILD_FIRST);
		walker.walk(rootOpDescs);

		String xml = builder.build();
		System.out.println(xml);

		final ByteBuffer buffer = ByteBuffer.wrap(xml.getBytes());

		InputStream is = new InputStream() {
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
	}
}
