package com.tencent.easycount.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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

public class GraphWalkerBak<T> {

	public static enum WalkMode {
		// 父节点优先遍历，先处理父节点，然后依次处理子节点
		ROOT_FIRST,

		// 父节点优先递归遍历，对于子节点的处理是递归进行的，某个子节点如果有多个父节点，
		// 因此可能在第一个父节点处理的时候就已经被处理过了，这个时候第二个父节点处理以后这子节点仍然要处理
		// 但是在ROOT_FIRST状态下，如果某个子节点已经被处理过了，就不会在处理。
		ROOT_FIRST_RECURSIVE,

		// 子节点优先遍历，在处理某个节点时，首先递归将该节点的所有子节点处理完，然后才能处理这个节点
		CHILD_FIRST,

		// 叶子节点优先遍历，
		LEAF_FIRST //
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

	public static interface Node {
		public List<? extends Node> getChildren();

		public String getName();
	}

	// inner stack save the parent path of the current node
	// parent path: from root parent to parent of current node
	final private Stack<Node> parentNodeStack;
	// walk queue
	final private List<Node> toWalkQueue;
	final private LinkedHashMap<Node, T> retMap;
	final private Dispatcher<T> dispatcher;
	final private WalkMode mode;

	/**
	 *
	 * @param dispatcher
	 * @param mode
	 */
	public GraphWalkerBak(final Dispatcher<T> dispatcher, final WalkMode mode) {
		this.parentNodeStack = new Stack<Node>();
		this.toWalkQueue = new LinkedList<Node>();
		this.retMap = new LinkedHashMap<Node, T>();
		this.dispatcher = dispatcher;
		this.mode = mode;
	}

	private void dispatch(final Node nd, final Stack<Node> ndStack)
			throws Exception {
		final ArrayList<T> childNodeOutputs = getChildrenNodeOutputs(nd);
		final T retVal = this.dispatcher.dispatch(nd, ndStack,
				childNodeOutputs, this.retMap);
		this.retMap.put(nd, retVal);
	}

	private ArrayList<T> getChildrenNodeOutputs(final Node nd) {
		if (nd.getChildren() != null) {
			final ArrayList<T> nodeOutputs = new ArrayList<T>(nd.getChildren()
					.size());
			for (final Node child : nd.getChildren()) {
				nodeOutputs.add(this.retMap.get(child));
			}
			return nodeOutputs;
		}
		return null;
	}

	public HashMap<Node, T> walk(final Collection<Node> startNodes)
			throws Exception {
		this.toWalkQueue.addAll(startNodes);
		while (this.toWalkQueue.size() > 0) {
			final Node nd = this.toWalkQueue.remove(0);
			if (this.mode == WalkMode.CHILD_FIRST) {
				walkChild(nd);
			} else if (this.mode == WalkMode.ROOT_FIRST) {
				walkRoot(nd);
			} else if (this.mode == WalkMode.ROOT_FIRST_RECURSIVE) {
				walkRootRecursive(nd);
			} else if (this.mode == WalkMode.LEAF_FIRST) {
				walkLeaf(nd);
			}
		}
		return this.retMap;
	}

	private void walkChild(final Node nd) throws Exception {
		// 父栈为空，表示起点，那么当前节点先加入父栈
		// 某些情况下，当前节点的父节点已经在栈中？？ when？？ TODO
		if (this.parentNodeStack.empty() || (nd != this.parentNodeStack.peek())) {
			this.parentNodeStack.push(nd);
		}

		// 以下几种情况下：
		// 1、没有子节点
		// 2、所有子节点都已经处理完毕
		// 3、dispatcher判断子节点需要处理。
		// 对当前节点进行处理
		if ((nd.getChildren() == null)
				|| this.retMap.keySet().containsAll(nd.getChildren())
				|| !this.dispatcher.needToDispatchChildren(nd,
						this.parentNodeStack, getChildrenNodeOutputs(nd),
						this.retMap)) {
			// 如果当前节点已经被处理过了，那么不用再进行处理
			if (!this.retMap.keySet().contains(nd)) {
				dispatch(nd, this.parentNodeStack);
			}

			// 当前节点已经处理完成，从父栈将节点退出
			this.parentNodeStack.pop();
			return;
		} else {
			// 当前节点有子节点需要处理，那么执行以下操作：
			// 1、当前节点插入walk队列头部
			// 2、删除可能已经被放入到队列中的所有子节点
			// 例如：a->b, a->c, b->c
			// a入队列，bc加入队列，
			this.toWalkQueue.add(0, nd);
			this.toWalkQueue.removeAll(nd.getChildren());
			this.toWalkQueue.addAll(0, nd.getChildren());
		}
	}

	private void walkLeaf(final Node nd) throws Exception {
		this.parentNodeStack.push(nd);

		if ((nd.getChildren() == null)
				|| this.retMap.keySet().containsAll(nd.getChildren())
				|| !this.dispatcher.needToDispatchChildren(nd,
						this.parentNodeStack, getChildrenNodeOutputs(nd),
						this.retMap)) {
			if (!this.retMap.keySet().contains(nd)) {
				dispatch(nd, this.parentNodeStack);
			}
			this.parentNodeStack.pop();
			return;
		}
		this.toWalkQueue.removeAll(nd.getChildren());
		this.toWalkQueue.addAll(0, nd.getChildren());
		this.toWalkQueue.add(nd);
	}

	private void walkRoot(final Node nd) throws Exception {
		this.parentNodeStack.push(nd);
		dispatch(nd, this.parentNodeStack);
		if ((nd.getChildren() != null)
				&& this.dispatcher.needToDispatchChildren(nd,
						this.parentNodeStack, getChildrenNodeOutputs(nd),
						this.retMap)) {
			for (final Node n : nd.getChildren()) {
				if (!this.retMap.keySet().contains(n)) {
					walkRoot(n);
				}
			}
		}
		this.parentNodeStack.pop();
	}

	private void walkRootRecursive(final Node nd) throws Exception {
		this.parentNodeStack.push(nd);
		dispatch(nd, this.parentNodeStack);
		if ((nd.getChildren() != null)
				&& this.dispatcher.needToDispatchChildren(nd,
						this.parentNodeStack, getChildrenNodeOutputs(nd),
						this.retMap)) {
			for (final Node n : nd.getChildren()) {
				walkRootRecursive(n);
			}
		}
		this.parentNodeStack.pop();
	}

}
