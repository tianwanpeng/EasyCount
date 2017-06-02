package com.tencent.trc.plan.physical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import com.tencent.trc.plan.logical.OpDesc;
import com.tencent.trc.util.graph.GraphWalker.Node;

public class PhysicalPlan implements Serializable {
	private static final long serialVersionUID = -1653929286053690507L;

	final private ArrayList<TaskWork1Spout> rootWorks;

	final private TreeMap<Integer, HashSet<OpDesc>> taskid2OpDescs;
	final private HashMap<OpDesc, Integer> opDesc2TaskId;
	final private TreeMap<Integer, TaskWork> taskId2Works;

	public PhysicalPlan(TaskWork1Spout rootWork,
			TreeMap<Integer, HashSet<OpDesc>> taskid2OpDescs,
			HashMap<OpDesc, Integer> opDesc2TaskId,
			TreeMap<Integer, TaskWork> taskId2Works) {
		this.rootWorks = new ArrayList<TaskWork1Spout>();
		this.rootWorks.add(rootWork);
		this.taskid2OpDescs = taskid2OpDescs;
		this.opDesc2TaskId = opDesc2TaskId;
		this.taskId2Works = taskId2Works;
	}

	public ArrayList<TaskWork1Spout> getRootWorks() {
		return rootWorks;
	}

	public ArrayList<Node> getRootWorksNodes() {
		ArrayList<Node> res = new ArrayList<Node>();
		res.addAll(rootWorks);
		return res;
	}

	public TreeMap<Integer, HashSet<OpDesc>> getTaskid2OpDescs() {
		return taskid2OpDescs;
	}

	public HashMap<OpDesc, Integer> getOpDesc2TaskId() {
		return opDesc2TaskId;
	}

	public TreeMap<Integer, TaskWork> getTaskId2Works() {
		return taskId2Works;
	}

}
