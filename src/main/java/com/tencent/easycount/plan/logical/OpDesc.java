package com.tencent.easycount.plan.logical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;

import com.tencent.easycount.util.graph.GraphWalker.Node;

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
		final ArrayList<Node> res = new ArrayList<Node>();
		res.addAll(this.childrenOps);
		return res;
	}

	public void addChild(final OpDesc childOpDesc) {
		// TODO not a good method
		this.childrenOps.add(childOpDesc);
		childOpDesc.addParent(this);
	}

	private void addParent(final OpDesc opDesc) {
		this.parentOps.add(opDesc);
	}

	public LinkedHashSet<OpDesc> childrenOps() {
		return this.childrenOps;
	}

	public LinkedHashSet<OpDesc> parentOps() {
		return this.parentOps;
	}

	public TypeInfo getOutputType() {
		return this.outputType;
	}

	public void setOutputType(final TypeInfo outputType) {
		this.outputType = outputType;
	}

	public int getOpTagIdx() {
		return this.opTagIdx;
	}

	public void setOpTagIdx(final int opTagIdx) {
		this.opTagIdx = opTagIdx;
	}

	public int getTaskId() {
		return this.taskId;
	}

	public void setTaskId(final int taskId) {
		this.taskId = taskId;
	}

	public String getTaskId_OpTagIdx() {
		return this.taskId + "-" + this.opTagIdx;
	}

}
