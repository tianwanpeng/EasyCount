package com.tencent.trc.plan.physical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.tencent.trc.metastore.MetaData;
import com.tencent.trc.parse.QB;
import com.tencent.trc.plan.logical.LogicalPlan;
import com.tencent.trc.plan.logical.OpDesc;
import com.tencent.trc.util.graph.GraphWalker;
import com.tencent.trc.util.graph.GraphWalker.Dispatcher;
import com.tencent.trc.util.graph.GraphWalker.Node;
import com.tencent.trc.util.graph.GraphWalker.WalkMode;

public class PhysicalPlanGenerator {
	@SuppressWarnings("unused")
	final private QB qb;
	@SuppressWarnings("unused")
	final private MetaData md;
	final private LogicalPlan lPlan;

	public PhysicalPlanGenerator(QB qb, MetaData md, LogicalPlan lPlan) {
		this.qb = qb;
		this.md = md;
		this.lPlan = lPlan;
	}

	public PhysicalPlan generatePhysicalPlan() throws Exception {
		/**
		 * generate taskid2OpDescs and opDesc2TaskId
		 */
		PhysicalPlanDispatcher pDispatcher = new PhysicalPlanDispatcher();
		GraphWalker<AtomicInteger> walker = new GraphWalker<AtomicInteger>(
				pDispatcher, WalkMode.ROOT_FIRST_RECURSIVE);
		walker.walk(lPlan.getRootOpNodes());

		TreeMap<Integer, HashSet<OpDesc>> taskid2OpDescs = pDispatcher
				.getTaskid2OpDescs();
		/**
		 * initialize the tagIdx and taskId of all the opDescs
		 */
		for (Integer taskId : taskid2OpDescs.keySet()) {
			int opTagIdx = 0;
			for (OpDesc opd : taskid2OpDescs.get(taskId)) {
				opd.setTaskId(taskId);
				opd.setOpTagIdx(opTagIdx++);
			}
		}

		HashMap<OpDesc, Integer> opDesc2TaskId = pDispatcher.getOpDesc2TaskId();

		/**
		 * generate all the tasks and assign a taskid to each task
		 */
		TreeMap<Integer, TaskWork> taskId2Works = new TreeMap<Integer, TaskWork>();

		TaskWork1Spout rootWork = null;
		for (Integer taskId : taskid2OpDescs.keySet()) {
			if (!taskId2Works.containsKey(taskId)) {
				if (taskId == 0) {
					rootWork = new TaskWork1Spout(taskId,
							taskid2OpDescs.get(taskId), opDesc2TaskId);
					rootWork.addRootOpDescs(lPlan.getRootOps());
					taskId2Works.put(taskId, rootWork);
				} else {
					taskId2Works.put(taskId, new TaskWork2Bolt(taskId,
							taskid2OpDescs.get(taskId), opDesc2TaskId));
				}
			}
			TaskWork work = taskId2Works.get(taskId);
			for (OpDesc opDesc : taskid2OpDescs.get(taskId)) {
				for (OpDesc childOp : opDesc.childrenOps()) {
					int childTaskId = opDesc2TaskId.get(childOp);
					if (!taskId2Works.containsKey(childTaskId)) {
						taskId2Works.put(childTaskId, new TaskWork2Bolt(
								childTaskId, taskid2OpDescs.get(childTaskId),
								opDesc2TaskId));
					}

					if (taskId != childTaskId) {
						TaskWork childWork = taskId2Works.get(childTaskId);
						work.addChild(childWork);
						work.addDestOpDescs(opDesc);
						childWork.addRootOpDesc(childOp);
					}
				}
			}
		}

		return new PhysicalPlan(rootWork, taskid2OpDescs, opDesc2TaskId,
				taskId2Works);
	}

	public static class PhysicalPlanDispatcher implements
			Dispatcher<AtomicInteger> {

		private TreeMap<Integer, HashSet<OpDesc>> taskid2OpDescs = new TreeMap<Integer, HashSet<OpDesc>>();
		private HashMap<OpDesc, Integer> opDesc2TaskId = new HashMap<OpDesc, Integer>();

		@Override
		public AtomicInteger dispatch(Node nd, Stack<Node> stack,
				ArrayList<AtomicInteger> nodeOutputs,
				HashMap<Node, AtomicInteger> retMap) {
			AtomicInteger currId = new AtomicInteger(0);
			// stack size bigger than 2 means this is not root node
			// so first get parent id, and if parent op is MGBY then this id
			// increment 1, otherwise set this id same to parent id
			if (stack.size() >= 2) {
				Node parent = stack.get(stack.size() - 2);
				int parentId = retMap.get(parent).get();
				int incre = 0;
				if ("MGBY".equals(parent.getName())) {
					incre = 1;
				}
				int taskId = parentId + incre;
				currId.set(taskId);
			}
			// if curr node id has already been generated, compare the old id
			// and the curr id, and choose the bigger one
			if (retMap.containsKey(nd)) {
				int oldId = retMap.get(nd).get();
				if (oldId < currId.get()) {
					// clear old info
					taskid2OpDescs.get(oldId).remove(nd);
					opDesc2TaskId.remove(nd);
				} else {
					currId.set(oldId);
				}
			}
			// save the info to taskid2OpDescs and opDesc2TaskId
			opDesc2TaskId.put((OpDesc) nd, currId.get());
			if (!taskid2OpDescs.containsKey(currId.get())) {
				taskid2OpDescs.put(currId.get(), new LinkedHashSet<OpDesc>());
			}
			taskid2OpDescs.get(currId.get()).add((OpDesc) nd);
			return currId;
		}

		@Override
		public boolean needToDispatchChildren(Node nd, Stack<Node> stack,
				ArrayList<AtomicInteger> nodeOutputs,
				HashMap<Node, AtomicInteger> retMap) {
			return true;
		}

		public TreeMap<Integer, HashSet<OpDesc>> getTaskid2OpDescs() {
			return taskid2OpDescs;
		}

		public void setTaskid2OpDescs(
				TreeMap<Integer, HashSet<OpDesc>> taskid2OpDescs) {
			this.taskid2OpDescs = taskid2OpDescs;
		}

		public HashMap<OpDesc, Integer> getOpDesc2TaskId() {
			return opDesc2TaskId;
		}

		public void setOpDesc2TaskId(HashMap<OpDesc, Integer> opDesc2TaskId) {
			this.opDesc2TaskId = opDesc2TaskId;
		}
	}
}
