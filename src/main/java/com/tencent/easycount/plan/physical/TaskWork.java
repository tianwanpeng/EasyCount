package com.tencent.easycount.plan.physical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.tencent.easycount.plan.logical.OpDesc;
import com.tencent.easycount.util.graph.GraphWalker.Node;

public abstract class TaskWork implements Serializable, Node {
	private static final long serialVersionUID = 295990859324564905L;
	final private HashSet<TaskWork> children;
	// opDescs contains those ops that should be executed in this task.
	final private HashSet<OpDesc> opDescs;
	// 包含当前task的所有输入的opdesc
	final private HashSet<OpDesc> rootOpDescs;
	// 包含当前task的所有输出的opdesc
	final private HashSet<OpDesc> destOpDescs;
	final private Integer taskId;
	final private HashMap<OpDesc, Integer> opDesc2TaskId;

	public TaskWork(final Integer taskId, final HashSet<OpDesc> opDescs,
			final HashMap<OpDesc, Integer> opDesc2TaskId) {
		this.taskId = taskId;
		this.opDescs = opDescs;
		this.opDesc2TaskId = opDesc2TaskId;
		this.children = new HashSet<TaskWork>();
		this.rootOpDescs = new HashSet<OpDesc>();
		this.destOpDescs = new HashSet<OpDesc>();
	}

	public void addChild(final TaskWork child) {
		if (this != child) {
			this.children.add(child);
		}
	}

	public void addRootOpDescs(final ArrayList<OpDesc> rootOps) {
		this.rootOpDescs.addAll(rootOps);
	}

	public void addDestOpDescs(final OpDesc opDesc) {
		this.destOpDescs.add(opDesc);
	}

	public void addRootOpDesc(final OpDesc opDesc) {
		this.rootOpDescs.add(opDesc);
	}

	@Override
	public List<? extends Node> getChildren() {
		final ArrayList<Node> res = new ArrayList<Node>();
		res.addAll(this.children);
		return res;
	}

	public HashSet<OpDesc> getOpDescs() {
		return this.opDescs;
	}

	public HashSet<OpDesc> getRootOpDescs() {
		return this.rootOpDescs;
	}

	public ArrayList<Node> getRootOpDescsNodes() {
		final HashSet<Node> res = new HashSet<Node>();
		final ArrayList<Node> res1 = new ArrayList<Node>();
		res.addAll(this.rootOpDescs);
		res1.addAll(res);
		return res1;
	}

	public HashSet<OpDesc> getDestOpDescs() {
		return this.destOpDescs;
	}

	public Integer getTaskId() {
		return this.taskId;
	}

	public HashMap<OpDesc, Integer> getOpDesc2TaskId() {
		return this.opDesc2TaskId;
	}

}
