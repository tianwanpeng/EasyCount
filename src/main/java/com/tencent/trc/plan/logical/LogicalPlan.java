package com.tencent.trc.plan.logical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.tencent.trc.plan.logical.LogicalPlanGenerator.QueryOpDescTree;
import com.tencent.trc.util.graph.GraphWalker.Node;

public class LogicalPlan implements Serializable {

	private static final long serialVersionUID = -385806922789998196L;
	final private HashMap<Node, QueryOpDescTree> qOpDescTree;
	final private ArrayList<OpDesc> rootOps;

	public String printStr() {
		StringBuffer sb = new StringBuffer();
		return sb.toString();
	}

	public LogicalPlan(HashMap<Node, QueryOpDescTree> qOpDescTree,
			ArrayList<OpDesc> rootOps) {
		this.qOpDescTree = qOpDescTree;
		this.rootOps = rootOps;
	}

	public HashMap<Node, QueryOpDescTree> getqOpDescTree() {
		return qOpDescTree;
	}

	public ArrayList<OpDesc> getRootOps() {
		return rootOps;
	}

	public ArrayList<Node> getRootOpNodes() {
		ArrayList<Node> rootOpNodes = new ArrayList<Node>();
		rootOpNodes.addAll(rootOps);
		return rootOpNodes;
	}
}
