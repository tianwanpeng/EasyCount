package com.tencent.trc.util.graph;

import java.util.ArrayList;
import java.util.Collection;
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
		 * @param stack
		 * @param nodeOutputs
		 * @param retMap
		 * @return
		 * @throws Exception
		 */
		public T dispatch(Node nd, Stack<Node> stack, ArrayList<T> nodeOutputs,
				HashMap<Node, T> retMap) throws Exception;

		/**
		 * 
		 * @param nd
		 * @param stack
		 * @param nodeOutputs
		 * @param retMap
		 * @return
		 */
		public boolean needToDispatchChildren(Node nd, Stack<Node> stack,
				ArrayList<T> nodeOutputs, HashMap<Node, T> retMap);
	}

	public static interface Node {
		public List<? extends Node> getChildren();

		public String getName();
	}

	// inner stack save the parent path of the current node
	// parent path: from root parent to parent of current node
	final private Stack<Node> opStack;
	// walk queue
	final private List<Node> toWalk;
	final private LinkedHashMap<Node, T> retMap;
	final private Dispatcher<T> dispatcher;
	final private WalkMode mode;

	/**
	 * 
	 * @param dispatcher
	 * @param mode
	 */
	public GraphWalker(Dispatcher<T> dispatcher, WalkMode mode) {
		opStack = new Stack<Node>();
		toWalk = new ArrayList<Node>();
		retMap = new LinkedHashMap<Node, T>();
		this.dispatcher = dispatcher;
		this.mode = mode;
	}

	private void dispatch(Node nd, Stack<Node> ndStack) throws Exception {
		ArrayList<T> childNodeOutputs = getChildrenNodeOutputs(nd);
		T retVal = dispatcher.dispatch(nd, ndStack, childNodeOutputs, retMap);
		retMap.put(nd, retVal);
	}

	private ArrayList<T> getChildrenNodeOutputs(Node nd) {
		if (nd.getChildren() != null) {
			ArrayList<T> nodeOutputs = new ArrayList<T>(nd.getChildren().size());
			for (Node child : nd.getChildren()) {
				nodeOutputs.add(retMap.get(child));
			}
			return nodeOutputs;
		}
		return null;
	}

	public HashMap<Node, T> walk(Collection<Node> startNodes) throws Exception {
		toWalk.addAll(startNodes);
		while (toWalk.size() > 0) {
			Node nd = toWalk.remove(0);
			if (mode == WalkMode.CHILD_FIRST) {
				walkChild(nd);
			} else if (mode == WalkMode.ROOT_FIRST) {
				walkRoot(nd);
			} else if (mode == WalkMode.ROOT_FIRST_RECURSIVE) {
				walkRootRecursive(nd);
			} else if (mode == WalkMode.LEAF_FIRST) {
				walkLeaf(nd);
			}
		}
		return retMap;
	}

	private void walkChild(Node nd) throws Exception {
		if (opStack.empty() || nd != opStack.peek()) {
			opStack.push(nd);
		}

		if ((nd.getChildren() == null)
				|| retMap.keySet().containsAll(nd.getChildren())
				|| !dispatcher.needToDispatchChildren(nd, opStack,
						getChildrenNodeOutputs(nd), retMap)) {
			if (!retMap.keySet().contains(nd)) {
				dispatch(nd, opStack);
			}
			opStack.pop();
			return;
		}

		toWalk.add(0, nd);
		toWalk.removeAll(nd.getChildren());
		toWalk.addAll(0, nd.getChildren());
	}

	private void walkLeaf(Node nd) throws Exception {
		opStack.push(nd);

		if ((nd.getChildren() == null)
				|| retMap.keySet().containsAll(nd.getChildren())
				|| !dispatcher.needToDispatchChildren(nd, opStack,
						getChildrenNodeOutputs(nd), retMap)) {
			if (!retMap.keySet().contains(nd)) {
				dispatch(nd, opStack);
			}
			opStack.pop();
			return;
		}
		toWalk.removeAll(nd.getChildren());
		toWalk.addAll(0, nd.getChildren());
		toWalk.add(nd);
	}

	private void walkRoot(Node nd) throws Exception {
		opStack.push(nd);
		dispatch(nd, opStack);
		if (nd.getChildren() != null
				&& dispatcher.needToDispatchChildren(nd, opStack,
						getChildrenNodeOutputs(nd), retMap)) {
			for (Node n : nd.getChildren()) {
				if (!retMap.keySet().contains(n)) {
					walkRoot(n);
				}
			}
		}
		opStack.pop();
	}

	private void walkRootRecursive(Node nd) throws Exception {
		opStack.push(nd);
		dispatch(nd, opStack);
		if (nd.getChildren() != null
				&& dispatcher.needToDispatchChildren(nd, opStack,
						getChildrenNodeOutputs(nd), retMap)) {
			for (Node n : nd.getChildren()) {
				walkRootRecursive(n);
			}
		}
		opStack.pop();
	}

}
