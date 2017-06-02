package com.tencent.easycount.plan.logical;

import java.util.ArrayList;

import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;

import com.tencent.easycount.plan.AggregationDescWrapper;

public class OpDesc5GBY extends OpDesc {

	private static final long serialVersionUID = -5693877331953958573L;

	private ArrayList<ExprNodeDesc> groupByKeys;
	private ExprNodeDesc coordinateExpr;
	private ArrayList<AggregationDescWrapper> aggregations;
	private ArrayList<String> outputColumnNames;

	private int aggrInterval = 3;
	private int accuInterval = 60;
	private int swInterval = 60;
	private boolean accuGlobal = false;

	private final String opName;

	public OpDesc5GBY(boolean map) {
		super();
		opName = map ? "MGBY" : "RGBY";
	}

	@Override
	public String getName() {
		return opName;
	}

	public void setGroupByKeys(ArrayList<ExprNodeDesc> groupByKeys) {
		this.groupByKeys = groupByKeys;
	}

	public ArrayList<ExprNodeDesc> getGroupByKeys() {
		return groupByKeys;
	}

	public void setCoordinateExpr(ExprNodeDesc coordinateExpr) {
		this.coordinateExpr = coordinateExpr;
	}

	public ExprNodeDesc getCoordinateExpr() {
		return coordinateExpr;
	}

	public void setAggregations(ArrayList<AggregationDescWrapper> aggregations) {
		this.aggregations = aggregations;
	}

	public ArrayList<AggregationDescWrapper> getAggregations() {
		return aggregations;
	}

	public void setOutputColumnNames(ArrayList<String> outputColumnNames) {
		this.outputColumnNames = outputColumnNames;
	}

	public ArrayList<String> getOutputColumnNames() {
		return outputColumnNames;
	}

	public void setAggrInterval(int aggrInterval) {
		this.aggrInterval = aggrInterval;
	}

	public void setAccuInterval(int accuInterval) {
		this.accuInterval = accuInterval;
	}

	public void setSwInterval(int swInterval) {
		this.swInterval = swInterval;
	}

	public int getAggrInterval() {
		return aggrInterval;
	}

	public int getAccuInterval() {
		return accuInterval;
	}

	public int getSwInterval() {
		return swInterval;
	}

	public void setAccuGlobal(boolean accuGlobal) {

	}

	public boolean isAccuGlobal() {
		return accuGlobal;
	}

}
