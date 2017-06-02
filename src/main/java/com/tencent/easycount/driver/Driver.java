package com.tencent.easycount.driver;

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

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import prefuse.data.Graph;
import prefuse.data.io.GraphMLReader;

import com.tencent.easycount.conf.TrcConfiguration;
import com.tencent.easycount.exec.io.local.LocalModeUtils;
import com.tencent.easycount.exec.physical.PhysicalExecGenerator;
import com.tencent.easycount.metastore.MetaData;
import com.tencent.easycount.metastore.MetaUtils;
import com.tencent.easycount.parse.ASTNodeTRC;
import com.tencent.easycount.parse.ParseDriver;
import com.tencent.easycount.parse.ParseUtils;
import com.tencent.easycount.parse.QB;
import com.tencent.easycount.plan.logical.LogicalPlan;
import com.tencent.easycount.plan.logical.LogicalPlanGenerator;
import com.tencent.easycount.plan.logical.OpDesc;
import com.tencent.easycount.plan.physical.PhysicalPlan;
import com.tencent.easycount.plan.physical.PhysicalPlanGenerator;
import com.tencent.easycount.udfnew.MyUDFUtils;
import com.tencent.easycount.util.graph.GraphDrawer;
import com.tencent.easycount.util.graph.GraphWalker;
import com.tencent.easycount.util.graph.GraphWalker.Dispatcher;
import com.tencent.easycount.util.graph.GraphWalker.Node;
import com.tencent.easycount.util.graph.GraphWalker.WalkMode;
import com.tencent.easycount.util.graph.GraphXmlBuilder;

public class Driver {
	private static final String version = "TRC-EC-R0.8";

	public static void main(final String[] args) throws Exception {

		System.out.println("TRC EC version is : " + version);
		// do some initilize work
		MyUDFUtils.initialize();

		/**
		 * initialize conf
		 */
		final TrcConfiguration config = generateConfig(args);

		// URL url = new File(config.get("configfile",
		// "testconfig.ini")).toURI()
		// .toURL();
		final InputStreamReader reader = new InputStreamReader(
				new FileInputStream(new File(config.get("configfile",
						"testconfig.ini"))), "utf8");
		final Ini ini = new Ini(reader);
		// Ini ini = new Ini(url);
		for (final String seckey : ini.keySet()) {
			final Section section = ini.get(seckey);
			for (final String key : section.keySet()) {
				if (config.get(key) != null) {
					continue;
				}
				final String value = section.get(key);
				config.set(seckey.equals("system") ? key : seckey + "::" + key,
						value);
			}
		}

		final String sql = config.get("sql");
		if (sql == null) {
			System.err
			.println("error ::: sql clause must be set, you should specify the configfile .... ");
			System.exit(-1);
		}

		try {
			final TrcTopology tt = generateTopology(config, ini);

			if (config.getBoolean("check", false)) {
				System.exit(0);
				return;
			}

			if (config.getBoolean("compile", false)) {
				final boolean withRoot = config.getBoolean("with.root", false);
				graphPrint(tt.lPlan, tt.pPlan, withRoot);
				return;
			}

			/**
			 * generate topology and submit it
			 */
			final boolean localMode = config.getBoolean("localmode", false);
			if (localMode) {
				LocalModeUtils.setupTables(tt.md, config);
				config.set("moniter.send.status", "false");
			}
			submitTopology(tt.topology, config.get("topologyname"), localMode,
					config, config.getInt("work.num", -1));
		} catch (final Exception e) {
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
		org.apache.storm.generated.StormTopology topology;

	}

	private static TrcTopology generateTopology(final TrcConfiguration config,
			final Ini ini) throws Exception {

		final TrcTopology tt = new TrcTopology();
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

	private static TrcConfiguration generateConfig(final String[] args)
			throws IOException {
		final HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < args.length; i++) {
			final int pos = args[i].indexOf('=');
			if (pos <= 0) {
				continue;
			}
			final String[] strs = new String[2];
			strs[0] = args[i].substring(0, pos);
			strs[1] = args[i].substring(pos + 1);
			map.put(strs[0].toLowerCase().trim(), strs[1]);
		}

		final TrcConfiguration config = new TrcConfiguration();
		final String sysconfigfile = map.get("sysconfig");
		if (sysconfigfile != null) {
			final URL url = new File(sysconfigfile).toURI().toURL();
			final Ini ini = new Ini(url);
			for (final String seckey : ini.keySet()) {
				final Section section = ini.get(seckey);
				for (final String key : section.keySet()) {
					final String value = section.get(key);
					config.set(key, value);
				}
			}
		}

		for (final String key : map.keySet()) {
			config.set(key, map.get(key));
		}

		final String topologyName = config.get("topologyname");
		if (null == topologyName) {
			System.err.println("error ::: topologyname must be set .... ");
			System.exit(-1);
		}

		final String groupname = config.get("consumergroup");
		if (null == groupname) {
			config.set("consumergroup", topologyName + "_consumergroup");
		}

		if (config.getBoolean("printversion", false)) {
			System.exit(-1);
		}
		return config;
	}

	private static void submitTopology(final StormTopology topology,
			final String topologyName, final boolean localMode,
			final TrcConfiguration config, final int workNum) {

		final Config conf = new Config();
		final Properties pro = config.getAllProperties();

		for (final Object keyobj : pro.keySet()) {
			conf.put((String) keyobj, pro.get(keyobj));
		}

		conf.setDebug(false);
		conf.setNumWorkers(workNum <= 0 ? 1 : workNum);
		conf.setNumAckers((workNum / 3) + 1);

		if (localMode) {
			final LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(topologyName, conf, topology);
		} else {
			try {
				StormSubmitter.submitTopology(topologyName, conf, topology);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void graphPrint(final LogicalPlan lPlan,
			final PhysicalPlan pPlan, final boolean withRoot) throws Exception {
		final ArrayList<Node> rootOpDescs = new ArrayList<Node>();
		if (withRoot) {
			final OpDesc rootOp = new OpDesc() {
				private static final long serialVersionUID = -1298273294286915472L;

				@Override
				public String getName() {
					return "ROOT";
				}
			};
			for (final OpDesc op : lPlan.getRootOps()) {
				rootOp.addChild(op);
			}
			rootOpDescs.add(rootOp);
		} else {
			for (final OpDesc op : lPlan.getRootOps()) {
				rootOpDescs.add(op);
			}
		}

		final GraphXmlBuilder builder = new GraphXmlBuilder();
		final HashMap<OpDesc, Integer> opDesc2TaskId = pPlan.getOpDesc2TaskId();

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
				}, WalkMode.CHILD_FIRST);
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
	}
}
