package com.tencent.trc.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;

import com.tencent.trc.util.graph.GraphWalker.Dispatcher;
import com.tencent.trc.util.graph.GraphWalker.Node;
import com.tencent.trc.util.graph.GraphWalker.WalkMode;

public class GraphPrinter {
	public static void print(Collection<Node> nodes, final CallBack cb)
			throws Exception {
		GraphWalker<String> walker = new GraphWalker<String>(
				new Dispatcher<String>() {

					@Override
					public String dispatch(Node nd, Stack<Node> stack,
							ArrayList<String> nodeOutputs,
							HashMap<Node, String> retMap) {
						String name = nd.getName() + ", " + nd.toString();
						System.out.println(name);
						for (String sn : nodeOutputs) {
							System.out.println("  --> " + sn);
						}
						if (cb != null) {
							cb.call(nd);
						}
						return name;
					}

					@Override
					public boolean needToDispatchChildren(Node nd,
							Stack<Node> stack, ArrayList<String> nodeOutputs,
							HashMap<Node, String> retMap) {
						return true;
					}
				}, WalkMode.CHILD_FIRST);

		walker.walk(nodes);
	}

	public static interface CallBack {
		public void call(Node nd);
	}
}
