package com.tencent.trc.plan.physical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.tencent.trc.plan.logical.OpDesc;
import com.tencent.trc.util.graph.GraphWalker.Node;

public abstract class TaskWork implements Serializable, Node {
	private static final long serialVersionUID = 295990859324564905L;
	final private HashSet<TaskWork> children;
	// opDescs contains those ops that should be executed in this task.
	final private HashSet<OpDesc> opDescs;
	final private HashSet<OpDesc> rootOpDescs;
	final private HashSet<OpDesc> destOpDescs;
	final private Integer taskId;
	final private HashMap<OpDesc, Integer> opDesc2TaskId;

	public TaskWork(Integer taskId, HashSet<OpDesc> opDescs,
			HashMap<OpDesc, Integer> opDesc2TaskId) {
		this.taskId = taskId;
		this.opDescs = opDescs;
		this.opDesc2TaskId = opDesc2TaskId;
		this.children = new HashSet<TaskWork>();
		this.rootOpDescs = new HashSet<OpDesc>();
		this.destOpDescs = new HashSet<OpDesc>();
	}

	public void addChild(TaskWork child) {
		if (this != child) {
			children.add(child);
		}
	}

	public void addRootOpDescs(ArrayList<OpDesc> rootOps) {
		this.rootOpDescs.addAll(rootOps);
	}

	public void addDestOpDescs(OpDesc opDesc) {
		this.destOpDescs.add(opDesc);
	}

	public void addRootOpDesc(OpDesc opDesc) {
		this.rootOpDescs.add(opDesc);
	}

	@Override
	public List<? extends Node> getChildren() {
		ArrayList<Node> res = new ArrayList<Node>();
		res.addAll(children);
		return res;
	}

	public HashSet<OpDesc> getOpDescs() {
		return opDescs;
	}

	public HashSet<OpDesc> getRootOpDescs() {
		return rootOpDescs;
	}

	public HashSet<Node> getRootOpDescsNodes() {
		HashSet<Node> res = new HashSet<Node>();
		res.addAll(rootOpDescs);
		return res;
	}

	public HashSet<OpDesc> getDestOpDescs() {
		return destOpDescs;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public HashMap<OpDesc, Integer> getOpDesc2TaskId() {
		return opDesc2TaskId;
	}

}
