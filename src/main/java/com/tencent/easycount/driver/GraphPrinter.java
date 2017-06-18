package com.tencent.easycount.driver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import prefuse.data.Graph;
import prefuse.data.io.GraphMLReader;

import com.tencent.easycount.plan.logical.LogicalPlan;
import com.tencent.easycount.plan.logical.OpDesc;
import com.tencent.easycount.plan.physical.PhysicalPlan;
import com.tencent.easycount.util.graph.GraphDrawer;
import com.tencent.easycount.util.graph.GraphWalker;
import com.tencent.easycount.util.graph.GraphWalker.Dispatcher;
import com.tencent.easycount.util.graph.GraphWalker.Node;
import com.tencent.easycount.util.graph.GraphWalker.WalkMode;
import com.tencent.easycount.util.graph.GraphXmlBuilder;

public class GraphPrinter {
	static void graphPrint(final LogicalPlan lPlan, final PhysicalPlan pPlan,
			final boolean withRoot) throws Exception {
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
				}, WalkMode.CHILD_FIRST, "driverGraphPrint");
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
