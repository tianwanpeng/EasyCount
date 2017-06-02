package com.tencent.trc.exec.physical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import backtype.storm.generated.StormTopology;
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

import com.google.common.util.concurrent.AtomicDouble;
import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.plan.physical.PhysicalPlan;
import com.tencent.trc.plan.physical.TaskWork1Spout;
import com.tencent.trc.plan.physical.TaskWork2Bolt;
import com.tencent.trc.util.graph.GraphWalker;
import com.tencent.trc.util.graph.GraphWalker.Dispatcher;
import com.tencent.trc.util.graph.GraphWalker.Node;
import com.tencent.trc.util.graph.GraphWalker.WalkMode;

public class PhysicalExecGenerator {
	final private PhysicalPlan pPlan;
	final private TrcConfiguration hconf;

	public PhysicalExecGenerator(PhysicalPlan pPlan,
			final TrcConfiguration hconf) {
		this.pPlan = pPlan;
		this.hconf = hconf;
	}

	public StormTopology generateExecTopology(final int workNum1)
			throws Exception {

		final ArrayList<Node> spoutWorks = pPlan.getRootWorksNodes();

		/**
		 * first generate the task tree
		 */
		HashMap<Node, Task> nodeToTasks = new GraphWalker<Task>(
				new Dispatcher<Task>() {

					@Override
					public Task dispatch(Node nd, Stack<Node> stack,
							ArrayList<Task> nodeOutputs,
							HashMap<Node, Task> retMap) {
						Task task = null;
						if (nd instanceof TaskWork1Spout) {
							task = new Task1Spout((TaskWork1Spout) nd,
									hconf.getAllProperties());
						} else {
							task = new Task2Bolt((TaskWork2Bolt) nd,
									hconf.getAllProperties());
						}
						for (Task ctask : nodeOutputs) {
							task.addChild(ctask);
							ctask.addParent(task);
						}
						return task;
					}

					@Override
					public boolean needToDispatchChildren(Node nd,
							Stack<Node> stack, ArrayList<Task> nodeOutputs,
							HashMap<Node, Task> retMap) {
						return true;
					}
				}, WalkMode.CHILD_FIRST).walk(spoutWorks);

		ArrayList<Node> rootTaskNodes = new ArrayList<Node>();
		for (Node node : spoutWorks) {
			rootTaskNodes.add(nodeToTasks.get(node));
		}

		// final double decreaseFactor = hconf.getFloat(
		// "work.tasknum.decrease.factor", (float) 0.6);
		final String dfStr = hconf.get("work.tasknum.decrease.factor", "1.0");
		String[] dfs = dfStr.split(",");
		final double[] factors = new double[dfs.length];
		for (int i = 0; i < factors.length; i++) {
			factors[i] = Float.parseFloat(dfs[i]);
		}

		final AtomicDouble wholeRatio = new AtomicDouble(0.0);
		final AtomicInteger wholeTaskNum = new AtomicInteger(0);

		final HashMap<Node, Double> task2ratio = new GraphWalker<Double>(
				new Dispatcher<Double>() {

					@Override
					public Double dispatch(Node nd, Stack<Node> stack,
							ArrayList<Double> nodeOutputs,
							HashMap<Node, Double> retMap) {
						double ratio = 1.0;
						// if (stack.size() > 1) {
						// ratio = retMap.get(stack.get(stack.size() - 2))
						// * decreaseFactor;
						// }
						ratio = factors[Math.min(factors.length - 1,
								stack.size() - 1)];
						wholeRatio.addAndGet(ratio);
						wholeTaskNum.incrementAndGet();
						return ratio;
					}

					@Override
					public boolean needToDispatchChildren(Node nd,
							Stack<Node> stack, ArrayList<Double> nodeOutputs,
							HashMap<Node, Double> retMap) {
						return true;
					}
				}, WalkMode.ROOT_FIRST).walk(rootTaskNodes);

		int actWorkNum = workNum1;
		if (workNum1 < 0) {
			actWorkNum = wholeTaskNum.get();
		}
		if (wholeTaskNum.get() > actWorkNum) {
			throw new RuntimeException("!!!! ERROR " + wholeTaskNum.get()
					+ " workers at least should be specified .... "
					+ actWorkNum);
		}

		final HashMap<Node, Integer> task2num = new HashMap<GraphWalker.Node, Integer>();

		int resTaskNum = actWorkNum - wholeTaskNum.get();
		int pos = 0;
		double posratio = 0.0;
		for (Node node : task2ratio.keySet()) {
			posratio += task2ratio.get(node) / wholeRatio.get();
			int cpos = (int) Math.round(posratio * resTaskNum);
			task2num.put(node, cpos - pos + 1);
			// System.err.println(pos + " " + cpos + " " + posratio + " "
			// + (posratio * resTaskNum) + " " + workNum);
			pos = cpos;
		}

		System.err.println("wholeRatio : " + wholeRatio.get());
		System.err.println("task2ratio : " + task2ratio);
		System.err.println("task2num : " + task2num);

		/**
		 * then generate the topology
		 */
		final TopologyBuilder topologyBuilder = new TopologyBuilder();
		new GraphWalker<String>(new Dispatcher<String>() {

			@Override
			public String dispatch(Node nd, Stack<Node> stack,
					ArrayList<String> nodeOutputs, HashMap<Node, String> retMap) {
				int taskNum = task2num.get(nd);
				if (taskNum <= 0) {
					taskNum = 1;
				}
				if (nd instanceof Task1Spout) {
					Task task = (Task) nd;
					topologyBuilder.setSpout(task.getTaskId(),
							(IRichSpout) task, taskNum);
					return task.getTaskId();
				} else {
					Task task = (Task) nd;
					BoltDeclarer boltDeclarer = topologyBuilder.setBolt(
							task.getTaskId(), (IRichBolt) task, taskNum);
					for (String streamId : task.getInStreamId2Task().keySet()) {
						boltDeclarer.fieldsGrouping(task.getInStreamId2Task()
								.get(streamId).getTaskId(), streamId,
								new Fields("reduceKey"));
					}
					return task.getTaskId();
				}

			}

			@Override
			public boolean needToDispatchChildren(Node nd, Stack<Node> stack,
					ArrayList<String> nodeOutputs, HashMap<Node, String> retMap) {
				return true;
			}
			// walk a node more time if it has two or more parents, so
			// use recursive mode
		}, WalkMode.ROOT_FIRST).walk(rootTaskNodes);

		return topologyBuilder.createTopology();

	}
}
