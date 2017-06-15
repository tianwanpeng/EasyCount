//package com.tencent.easycount.util.graph;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Stack;
//
//import junit.framework.Assert;
//
//import org.junit.Test;
//
//import com.tencent.easycount.util.graph.GraphWalker.Dispatcher;
//import com.tencent.easycount.util.graph.GraphWalker.Node;
//import com.tencent.easycount.util.graph.GraphWalker.WalkMode;
//
//public class GraphWalkerTest {
//
//	@Test
//	public void walkTest() throws Exception {
//		final StringBuffer sb = new StringBuffer();
//
//		final Dispatcher<Boolean> dispather = new Dispatcher<Boolean>() {
//
//			@Override
//			public Boolean dispatch(final Node nd, final Stack<Node> stack,
//					final ArrayList<Boolean> nodeOutputs,
//					final HashMap<Node, Boolean> retMap) {
//				sb.append(nd.getName()).append("%");
//				return null;
//			}
//
//			@Override
//			public boolean needToDispatchChildren(final Node nd,
//					final Stack<Node> stack,
//					final ArrayList<Boolean> nodeOutputs,
//					final HashMap<Node, Boolean> retMap) {
//				return true;
//			}
//		};
//
//		ArrayList<Node> rootNodes = generateNodes();
//
//		GraphWalker<Boolean> gw = new GraphWalker<Boolean>(dispather,
//				WalkMode.ROOT_FIRST);
//		gw.walk(rootNodes);
//		Assert.assertEquals(sb.toString(), "a%b%d%e%f%g%c%h%");
//
//		GraphWalker<Boolean> gw1 = new GraphWalker<Boolean>(dispather,
//				WalkMode.CHILD_FIRST);
//		sb.setLength(0);
//		gw1.walk(rootNodes);
//		Assert.assertEquals(sb.toString(), "d%f%g%e%b%h%c%a%");
//
//		rootNodes = generateNodes1();
//
//		gw = new GraphWalker<Boolean>(dispather, WalkMode.ROOT_FIRST);
//		sb.setLength(0);
//		gw.walk(rootNodes);
//		Assert.assertEquals(sb.toString(), "a%b%d%e%f%g%c%h%");
//
//		gw1 = new GraphWalker<Boolean>(dispather, WalkMode.CHILD_FIRST);
//		sb.setLength(0);
//		gw1.walk(rootNodes);
//		Assert.assertEquals(sb.toString(), "d%f%g%e%b%h%c%a%");
//
//		rootNodes = generateNodes2();
//
//		gw = new GraphWalker<Boolean>(dispather, WalkMode.ROOT_FIRST);
//		sb.setLength(0);
//		gw.walk(rootNodes);
//		Assert.assertEquals(sb.toString(), "a%c%d%e%g%b%f%h%");
//
//		gw1 = new GraphWalker<Boolean>(dispather, WalkMode.CHILD_FIRST);
//		sb.setLength(0);
//		gw1.walk(rootNodes);
//		Assert.assertEquals(sb.toString(), "d%g%e%c%a%h%f%b%");
//
//	}
//
//	private ArrayList<Node> generateNodes2() {
//		final ArrayList<Node> nodes = new ArrayList<GraphWalker.Node>();
//		final TestNode a = new TestNode("a");
//		final TestNode b = new TestNode("b");
//		final TestNode c = new TestNode("c");
//		final TestNode d = new TestNode("d");
//		final TestNode e = new TestNode("e");
//		final TestNode f = new TestNode("f");
//		final TestNode g = new TestNode("g");
//		final TestNode h = new TestNode("h");
//		a.addChild(c);
//		b.addChild(c);
//		b.addChild(d);
//		c.addChild(d);
//		c.addChild(e);
//		b.addChild(f);
//		f.addChild(e);
//		e.addChild(g);
//		f.addChild(h);
//		nodes.add(a);
//		nodes.add(b);
//		return nodes;
//	}
//
//	private ArrayList<Node> generateNodes1() {
//		final ArrayList<Node> nodes = new ArrayList<GraphWalker.Node>();
//		final TestNode a = new TestNode("a");
//		final TestNode b = new TestNode("b");
//		final TestNode c = new TestNode("c");
//		final TestNode d = new TestNode("d");
//		final TestNode e = new TestNode("e");
//		final TestNode f = new TestNode("f");
//		final TestNode g = new TestNode("g");
//		final TestNode h = new TestNode("h");
//		a.addChild(b);
//		a.addChild(c);
//		b.addChild(d);
//		b.addChild(e);
//		c.addChild(h);
//		c.addChild(e);
//		e.addChild(f);
//		e.addChild(g);
//		h.addChild(g);
//		nodes.add(a);
//		return nodes;
//	}
//
//	private ArrayList<Node> generateNodes() {
//		final ArrayList<Node> nodes = new ArrayList<GraphWalker.Node>();
//		final TestNode a = new TestNode("a");
//		final TestNode b = new TestNode("b");
//		final TestNode c = new TestNode("c");
//		final TestNode d = new TestNode("d");
//		final TestNode e = new TestNode("e");
//		final TestNode f = new TestNode("f");
//		final TestNode g = new TestNode("g");
//		final TestNode h = new TestNode("h");
//		a.addChild(b);
//		a.addChild(c);
//		b.addChild(d);
//		b.addChild(e);
//		c.addChild(h);
//		e.addChild(f);
//		e.addChild(g);
//		nodes.add(a);
//		return nodes;
//	}
//
//	public static void main(final String[] args) throws Exception {
//		new GraphWalkerTest().walkTest();
//	}
//
//	class TestNode implements Node {
//		final String name;
//		final ArrayList<Node> children;
//
//		public TestNode(final String name) {
//			this.name = name;
//			this.children = new ArrayList<GraphWalker.Node>();
//		}
//
//		public void addChild(final Node n) {
//			this.children.add(n);
//		}
//
//		@Override
//		public List<? extends Node> getChildren() {
//			return this.children;
//		}
//
//		@Override
//		public String getName() {
//			return this.name;
//		}
//	}
// }
