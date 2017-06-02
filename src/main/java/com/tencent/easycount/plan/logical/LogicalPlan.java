package com.tencent.easycount.plan.logical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.tencent.easycount.plan.logical.LogicalPlanGenerator.QueryOpDescTree;
import com.tencent.easycount.util.graph.GraphWalker.Node;

public class LogicalPlan implements Serializable {

	private static final long serialVersionUID = -385806922789998196L;
	final private HashMap<Node, QueryOpDescTree> qOpDescTree;
	final private ArrayList<OpDesc> rootOps;

	public String printStr() {
		final StringBuffer sb = new StringBuffer();
		return sb.toString();
	}

	public LogicalPlan(final HashMap<Node, QueryOpDescTree> qOpDescTree,
			final ArrayList<OpDesc> rootOps) {
		this.qOpDescTree = qOpDescTree;
		this.rootOps = rootOps;
	}

	public HashMap<Node, QueryOpDescTree> getqOpDescTree() {
		return this.qOpDescTree;
	}

	public ArrayList<OpDesc> getRootOps() {
		return this.rootOps;
	}

	public ArrayList<Node> getRootOpNodes() {
		final ArrayList<Node> rootOpNodes = new ArrayList<Node>();
		rootOpNodes.addAll(this.rootOps);
		return rootOpNodes;
	}
}
