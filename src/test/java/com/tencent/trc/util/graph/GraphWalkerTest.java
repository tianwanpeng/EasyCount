package com.tencent.trc.util.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import junit.framework.Assert;

import org.junit.Test;

import com.tencent.trc.util.graph.GraphWalker.Dispatcher;
import com.tencent.trc.util.graph.GraphWalker.Node;
import com.tencent.trc.util.graph.GraphWalker.WalkMode;

public class GraphWalkerTest {

	@Test
	public void walkTest() throws Exception {
		final StringBuffer sb = new StringBuffer();

		Dispatcher<Boolean> dispather = new Dispatcher<Boolean>() {

			@Override
			public Boolean dispatch(Node nd, Stack<Node> stack,
					ArrayList<Boolean> nodeOutputs,
					HashMap<Node, Boolean> retMap) {
				sb.append(nd.getName()).append("%");
				return null;
			}

			@Override
			public boolean needToDispatchChildren(Node nd, Stack<Node> stack,
					ArrayList<Boolean> nodeOutputs,
					HashMap<Node, Boolean> retMap) {
				return true;
			}
		};

		ArrayList<Node> rootNodes = generateNodes();

		GraphWalker<Boolean> gw = new GraphWalker<Boolean>(dispather,
				WalkMode.ROOT_FIRST);
		gw.walk(rootNodes);
		Assert.assertEquals(sb.toString(), "a%b%d%e%f%g%c%h%");

		GraphWalker<Boolean> gw1 = new GraphWalker<Boolean>(dispather,
				WalkMode.CHILD_FIRST);
		sb.setLength(0);
		gw1.walk(rootNodes);
		Assert.assertEquals(sb.toString(), "d%f%g%e%b%h%c%a%");

		GraphWalker<Boolean> gw2 = new GraphWalker<Boolean>(dispather,
				WalkMode.LEAF_FIRST);
		sb.setLength(0);
		gw2.walk(rootNodes);
		Assert.assertEquals(sb.toString(), "d%f%g%h%e%c%b%a%");

		rootNodes = generateNodes1();

		gw = new GraphWalker<Boolean>(dispather, WalkMode.ROOT_FIRST);
		sb.setLength(0);
		gw.walk(rootNodes);
		Assert.assertEquals(sb.toString(), "a%b%d%e%f%g%c%h%");

		gw1 = new GraphWalker<Boolean>(dispather, WalkMode.CHILD_FIRST);
		sb.setLength(0);
		gw1.walk(rootNodes);
		Assert.assertEquals(sb.toString(), "d%f%g%e%b%h%c%a%");

		gw2 = new GraphWalker<Boolean>(dispather, WalkMode.LEAF_FIRST);
		sb.setLength(0);
		gw2.walk(rootNodes);
		Assert.assertEquals(sb.toString(), "d%f%g%h%e%b%c%a%");

		rootNodes = generateNodes2();

		gw = new GraphWalker<Boolean>(dispather, WalkMode.ROOT_FIRST);
		sb.setLength(0);
		gw.walk(rootNodes);
		Assert.assertEquals(sb.toString(), "a%c%d%e%g%b%f%h%");

		gw1 = new GraphWalker<Boolean>(dispather, WalkMode.CHILD_FIRST);
		sb.setLength(0);
		gw1.walk(rootNodes);
		Assert.assertEquals(sb.toString(), "d%g%e%c%a%h%f%b%");

		gw2 = new GraphWalker<Boolean>(dispather, WalkMode.LEAF_FIRST);
		sb.setLength(0);
		gw2.walk(rootNodes);
		Assert.assertEquals(sb.toString(), "d%g%e%h%c%f%a%b%");

	}

	private ArrayList<Node> generateNodes2() {
		ArrayList<Node> nodes = new ArrayList<GraphWalker.Node>();
		TestNode a = new TestNode("a");
		TestNode b = new TestNode("b");
		TestNode c = new TestNode("c");
		TestNode d = new TestNode("d");
		TestNode e = new TestNode("e");
		TestNode f = new TestNode("f");
		TestNode g = new TestNode("g");
		TestNode h = new TestNode("h");
		a.addChild(c);
		b.addChild(c);
		b.addChild(d);
		c.addChild(d);
		c.addChild(e);
		b.addChild(f);
		f.addChild(e);
		e.addChild(g);
		f.addChild(h);
		nodes.add(a);
		nodes.add(b);
		return nodes;
	}

	private ArrayList<Node> generateNodes1() {
		ArrayList<Node> nodes = new ArrayList<GraphWalker.Node>();
		TestNode a = new TestNode("a");
		TestNode b = new TestNode("b");
		TestNode c = new TestNode("c");
		TestNode d = new TestNode("d");
		TestNode e = new TestNode("e");
		TestNode f = new TestNode("f");
		TestNode g = new TestNode("g");
		TestNode h = new TestNode("h");
		a.addChild(b);
		a.addChild(c);
		b.addChild(d);
		b.addChild(e);
		c.addChild(h);
		c.addChild(e);
		e.addChild(f);
		e.addChild(g);
		h.addChild(g);
		nodes.add(a);
		return nodes;
	}

	private ArrayList<Node> generateNodes() {
		ArrayList<Node> nodes = new ArrayList<GraphWalker.Node>();
		TestNode a = new TestNode("a");
		TestNode b = new TestNode("b");
		TestNode c = new TestNode("c");
		TestNode d = new TestNode("d");
		TestNode e = new TestNode("e");
		TestNode f = new TestNode("f");
		TestNode g = new TestNode("g");
		TestNode h = new TestNode("h");
		a.addChild(b);
		a.addChild(c);
		b.addChild(d);
		b.addChild(e);
		c.addChild(h);
		e.addChild(f);
		e.addChild(g);
		nodes.add(a);
		return nodes;
	}

	public static void main(String[] args) throws Exception {
		new GraphWalkerTest().walkTest();
	}

	class TestNode implements Node {
		final String name;
		final ArrayList<Node> children;

		public TestNode(String name) {
			this.name = name;
			this.children = new ArrayList<GraphWalker.Node>();
		}

		public void addChild(Node n) {
			this.children.add(n);
		}

		@Override
		public List<? extends Node> getChildren() {
			return children;
		}

		@Override
		public String getName() {
			return name;
		}
	}
}
