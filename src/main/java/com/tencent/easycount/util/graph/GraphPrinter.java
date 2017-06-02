package com.tencent.easycount.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;

import com.tencent.easycount.util.graph.GraphWalker.Dispatcher;
import com.tencent.easycount.util.graph.GraphWalker.Node;
import com.tencent.easycount.util.graph.GraphWalker.WalkMode;

public class GraphPrinter {
	public static void print(final Collection<Node> nodes, final CallBack cb)
			throws Exception {
		final GraphWalker<String> walker = new GraphWalker<String>(
				new Dispatcher<String>() {

					@Override
					public String dispatch(final Node nd,
							final Stack<Node> stack,
							final ArrayList<String> nodeOutputs,
							final HashMap<Node, String> retMap) {
						final String name = nd.getName() + ", " + nd.toString();
						System.out.println(name);
						for (final String sn : nodeOutputs) {
							System.out.println("  --> " + sn);
						}
						if (cb != null) {
							cb.call(nd);
						}
						return name;
					}

					@Override
					public boolean needToDispatchChildren(final Node nd,
							final Stack<Node> stack,
							final ArrayList<String> nodeOutputs,
							final HashMap<Node, String> retMap) {
						return true;
					}
				}, WalkMode.CHILD_FIRST);

		walker.walk(nodes);
	}

	public static interface CallBack {
		public void call(Node nd);
	}
}
