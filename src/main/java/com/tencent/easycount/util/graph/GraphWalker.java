package com.tencent.easycount.util.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

/**
 * walk a tree and process
 *
 * @author steven
 *
 * @param <T>
 *            T is the result type of any node process result
 */

public class GraphWalker<T> {

	private boolean printinfo = true;

	public boolean isPrintinfo() {
		return this.printinfo;
	}

	public void setPrintinfo(final boolean print) {
		this.printinfo = print;
	}

	public static enum WalkMode {
		// 父节点优先遍历，先处理父节点，然后依次处理子节点
		ROOT_FIRST,

		// 父节点优先递归遍历，对于子节点的处理是递归进行的，某个子节点如果有多个父节点，
		// 因此可能在第一个父节点处理的时候就已经被处理过了，这个时候第二个父节点处理以后这子节点仍然要处理
		// 但是在ROOT_FIRST状态下，如果某个子节点已经被处理过了，就不会在处理。
		ROOT_FIRST_RECURSIVE,

		// 子节点优先遍历，在处理某个节点时，首先递归将该节点的所有子节点处理完，然后才能处理这个节点
		CHILD_FIRST
	}

	public static interface Node {
		public List<? extends Node> getChildren();

		public String getName();
	}

	public static interface Dispatcher<T> {
		/**
		 *
		 * @param nd
		 * @param parentStack
		 * @param childOutputs
		 * @param retMap
		 * @return
		 * @throws Exception
		 */
		public T dispatch(Node nd, Stack<Node> parentStack,
				ArrayList<T> childOutputs, HashMap<Node, T> retMap)
						throws Exception;

		/**
		 *
		 * @param nd
		 * @param parentStack
		 * @param childOutputs
		 * @param retMap
		 * @return
		 */
		public boolean needToDispatchChildren(Node nd, Stack<Node> parentStack,
				ArrayList<T> childOutputs, HashMap<Node, T> retMap);
	}

	// inner stack save the parent path of the current node
	// parent path: from root parent to parent of current node
	final private Stack<Node> parentNodeStack;
	final private LinkedHashMap<Node, T> retMap;
	final private Dispatcher<T> dispatcher;
	final private WalkMode mode;
	final private String wname;

	/**
	 *
	 * @param dispatcher
	 * @param mode
	 */
	public GraphWalker(final Dispatcher<T> dispatcher, final WalkMode mode,
			final String wname) {
		this.parentNodeStack = new Stack<Node>();
		this.retMap = new LinkedHashMap<Node, T>();
		this.dispatcher = dispatcher;
		this.mode = mode;
		this.wname = wname;
	}

	public HashMap<Node, T> walk(final ArrayList<Node> startNodes)
			throws Exception {
		// walk from start nodes, walk every node
		for (final Node node : startNodes) {
			System.out.println(this.wname + ":::start:::" + node);
		}
		for (final Node node : startNodes) {
			walk(node);
		}
		return this.retMap;
	}

	private void walk(final Node nd) throws Exception {
		if (nd == null) {
			System.out.println("nd is null....");
			throw new Exception("nd is null....");
		}
		if (this.printinfo) {
			System.out.println(this.wname + "::::::" + nd);
		}

		// first put nd to stack
		this.parentNodeStack.add(nd);

		if (this.mode == WalkMode.ROOT_FIRST_RECURSIVE) {
			dispatch(nd);
			walkChildren(nd);
		} else {
			if (!checkNodeProcessed(nd)) {
				if (this.mode == WalkMode.CHILD_FIRST) {
					walkChildren(nd);
					dispatch(nd);
				} else if (this.mode == WalkMode.ROOT_FIRST) {
					dispatch(nd);
					walkChildren(nd);
				}
			}
		}

		// at the end pop the node
		this.parentNodeStack.pop();
	}

	private void walkChildren(final Node nd) throws Exception {
		if (checkNeedToDispatchChildren(nd)) {
			for (final Node child : nd.getChildren()) {
				walk(child);
			}
		}
	}

	private void dispatch(final Node nd) throws Exception {
		final ArrayList<T> childNodeOutputs = getChildrenNodeOutputs(nd);
		final T retVal = this.dispatcher.dispatch(nd, this.parentNodeStack,
				childNodeOutputs, this.retMap);
		this.retMap.put(nd, retVal);
	}

	private ArrayList<T> getChildrenNodeOutputs(final Node nd) {
		final ArrayList<T> nodeOutputs = new ArrayList<T>();
		if (nd.getChildren() != null) {
			for (final Node child : nd.getChildren()) {
				nodeOutputs.add(this.retMap.get(child));
			}
		}
		return nodeOutputs;
	}

	private boolean checkNodeProcessed(final Node nd) {
		return this.retMap.keySet().contains(nd);
	}

	private boolean checkNeedToDispatchChildren(final Node nd) {
		return (nd.getChildren() != null)
				&& this.dispatcher.needToDispatchChildren(nd,
						this.parentNodeStack, getChildrenNodeOutputs(nd),
						this.retMap);
	}

	public static void main(final String[] args) {

	}

}
