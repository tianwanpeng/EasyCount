package com.tencent.trc.util.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hive.ql.parse.SemanticException;

import com.tencent.trc.util.graph.GraphWalker.Dispatcher;
import com.tencent.trc.util.graph.GraphWalker.Node;

//TODO use generic type for NodeProcessorCtx ???
public class RuleDispatcher<T> implements Dispatcher<T> {

	private final Map<Rule, NodeProcessor<T>> procRules;
	private final NodeProcessorCtx procCtx;
	private final NodeProcessor<T> defaultProc;

	public RuleDispatcher(NodeProcessor<T> defaultProc,
			Map<Rule, NodeProcessor<T>> rules, NodeProcessorCtx procCtx) {
		this.defaultProc = defaultProc;
		this.procRules = rules;
		this.procCtx = procCtx;
	}

	@Override
	public T dispatch(Node nd, Stack<Node> ndStack, ArrayList<T> nodeOutputs,
			HashMap<Node, T> retMap) throws Exception {

		Rule rule = null;
		int minCost = Integer.MAX_VALUE;
		for (Rule r : procRules.keySet()) {
			int cost = r.cost(ndStack);
			if ((cost >= 0) && (cost <= minCost)) {
				minCost = cost;
				rule = r;
			}
		}

		NodeProcessor<T> proc;

		if (rule == null) {
			proc = defaultProc;
		} else {
			proc = procRules.get(rule);
		}

		if (proc != null) {
			return proc.process(nd, ndStack, procCtx, nodeOutputs);
		} else {
			return null;
		}
	}

	@Override
	public boolean needToDispatchChildren(Node nd, Stack<Node> ndStack,
			ArrayList<T> nodeOutputs, HashMap<Node, T> retMap) {
		return true;
	}

	public static interface Rule {
		public int cost(Stack<Node> stack);

		public String getName();
	}

	public static interface NodeProcessor<T> {
		public T process(Node nd, Stack<Node> stack, NodeProcessorCtx procCtx,
				ArrayList<T> nodeOutputs) throws Exception;
	}

	public interface NodeProcessorCtx {
	}

	/**
	 * Rule interface for Nodes Used in Node dispatching to dispatch
	 * process/visitor functions for Nodes.
	 */
	public static class RuleRegExp implements Rule {

		private final String ruleName;
		private final Pattern pattern;

		/**
		 * The rule specified by the regular expression. Note that, the regular
		 * expression is specified in terms of Node name. For eg: TS.*RS ->
		 * means TableScan Node followed by anything any number of times
		 * followed by ReduceSink
		 * 
		 * @param ruleName
		 *            name of the rule
		 * @param regExp
		 *            regular expression for the rule
		 **/
		public RuleRegExp(String ruleName, String regExp) {
			this.ruleName = ruleName;
			this.pattern = Pattern.compile(regExp);
		}

		/**
		 * This function returns the cost of the rule for the specified stack.
		 * Lower the cost, the better the rule is matched
		 * 
		 * @param stack
		 *            Node stack encountered so far
		 * @return cost of the function
		 * @throws SemanticException
		 */
		public int cost(Stack<Node> stack) {
			int numElems = (stack != null ? stack.size() : 0);
			String name = new String();
			for (int pos = numElems - 1; pos >= 0; pos--) {
				name = stack.get(pos).getName() + "%" + name;
				Matcher m = pattern.matcher(name);
				if (m.matches()) {
					return m.group().length();
				}
			}

			return -1;
		}

		/**
		 * @return the name of the Node
		 **/
		public String getName() {
			return ruleName;
		}
	}
}
