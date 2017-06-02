package com.tencent.easycount.util.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hive.ql.parse.SemanticException;

import com.tencent.easycount.util.graph.GraphWalker.Dispatcher;
import com.tencent.easycount.util.graph.GraphWalker.Node;

//TODO use generic type for NodeProcessorCtx ???
public class RuleDispatcher<T> implements Dispatcher<T> {

	private final Map<Rule, NodeProcessor<T>> procRules;
	private final NodeProcessorCtx procCtx;
	private final NodeProcessor<T> defaultProc;

	public RuleDispatcher(final NodeProcessor<T> defaultProc,
			final Map<Rule, NodeProcessor<T>> rules,
			final NodeProcessorCtx procCtx) {
		this.defaultProc = defaultProc;
		this.procRules = rules;
		this.procCtx = procCtx;
	}

	@Override
	public T dispatch(final Node nd, final Stack<Node> ndStack,
			final ArrayList<T> nodeOutputs, final HashMap<Node, T> retMap)
			throws Exception {

		Rule rule = null;
		int minCost = Integer.MAX_VALUE;
		for (final Rule r : this.procRules.keySet()) {
			final int cost = r.cost(ndStack);
			if ((cost >= 0) && (cost <= minCost)) {
				minCost = cost;
				rule = r;
			}
		}

		NodeProcessor<T> proc;

		if (rule == null) {
			proc = this.defaultProc;
		} else {
			proc = this.procRules.get(rule);
		}

		if (proc != null) {
			return proc.process(nd, ndStack, this.procCtx, nodeOutputs);
		} else {
			return null;
		}
	}

	@Override
	public boolean needToDispatchChildren(final Node nd,
			final Stack<Node> ndStack, final ArrayList<T> nodeOutputs,
			final HashMap<Node, T> retMap) {
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
		public RuleRegExp(final String ruleName, final String regExp) {
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
		@Override
		public int cost(final Stack<Node> stack) {
			final int numElems = (stack != null ? stack.size() : 0);
			String name = new String();
			for (int pos = numElems - 1; pos >= 0; pos--) {
				name = stack.get(pos).getName() + "%" + name;
				final Matcher m = this.pattern.matcher(name);
				if (m.matches()) {
					return m.group().length();
				}
			}

			return -1;
		}

		/**
		 * @return the name of the Node
		 **/
		@Override
		public String getName() {
			return this.ruleName;
		}
	}
}
