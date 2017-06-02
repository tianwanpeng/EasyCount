package com.tencent.trc.plan.logical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;

import com.tencent.trc.util.graph.GraphWalker.Node;

public abstract class OpDesc implements Node, Serializable {
	private static final long serialVersionUID = 870998859037156169L;

	private int opTagIdx = -1;
	private int taskId = -1;

	final private LinkedHashSet<OpDesc> childrenOps;
	final private LinkedHashSet<OpDesc> parentOps;
	private TypeInfo outputType = null;

	public OpDesc() {
		this.childrenOps = new LinkedHashSet<OpDesc>();
		this.parentOps = new LinkedHashSet<OpDesc>();
	}

	@Override
	public ArrayList<? extends Node> getChildren() {
		ArrayList<Node> res = new ArrayList<Node>();
		res.addAll(childrenOps);
		return res;
	}

	public void addChild(OpDesc childOpDesc) {
		// TODO not a good method
		childrenOps.add(childOpDesc);
		childOpDesc.addParent(this);
	}

	private void addParent(OpDesc opDesc) {
		this.parentOps.add(opDesc);
	}

	public LinkedHashSet<OpDesc> childrenOps() {
		return childrenOps;
	}

	public LinkedHashSet<OpDesc> parentOps() {
		return parentOps;
	}

	public TypeInfo getOutputType() {
		return outputType;
	}

	public void setOutputType(TypeInfo outputType) {
		this.outputType = outputType;
	}

	public int getOpTagIdx() {
		return opTagIdx;
	}

	public void setOpTagIdx(int opTagIdx) {
		this.opTagIdx = opTagIdx;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getTaskId_OpTagIdx() {
		return this.taskId + "-" + this.opTagIdx;
	}

}
