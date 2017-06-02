package com.tencent.easycount.plan.physical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import com.tencent.easycount.plan.logical.OpDesc;
import com.tencent.easycount.util.graph.GraphWalker.Node;

public class PhysicalPlan implements Serializable {
	private static final long serialVersionUID = -1653929286053690507L;

	final private ArrayList<TaskWork1Spout> rootWorks;

	final private TreeMap<Integer, HashSet<OpDesc>> taskid2OpDescs;
	final private HashMap<OpDesc, Integer> opDesc2TaskId;
	final private TreeMap<Integer, TaskWork> taskId2Works;

	public PhysicalPlan(final TaskWork1Spout rootWork,
			final TreeMap<Integer, HashSet<OpDesc>> taskid2OpDescs,
			final HashMap<OpDesc, Integer> opDesc2TaskId,
			final TreeMap<Integer, TaskWork> taskId2Works) {
		this.rootWorks = new ArrayList<TaskWork1Spout>();
		this.rootWorks.add(rootWork);
		this.taskid2OpDescs = taskid2OpDescs;
		this.opDesc2TaskId = opDesc2TaskId;
		this.taskId2Works = taskId2Works;
	}

	public ArrayList<TaskWork1Spout> getRootWorks() {
		return this.rootWorks;
	}

	public ArrayList<Node> getRootWorksNodes() {
		final ArrayList<Node> res = new ArrayList<Node>();
		res.addAll(this.rootWorks);
		return res;
	}

	public TreeMap<Integer, HashSet<OpDesc>> getTaskid2OpDescs() {
		return this.taskid2OpDescs;
	}

	public HashMap<OpDesc, Integer> getOpDesc2TaskId() {
		return this.opDesc2TaskId;
	}

	public TreeMap<Integer, TaskWork> getTaskId2Works() {
		return this.taskId2Works;
	}

}
