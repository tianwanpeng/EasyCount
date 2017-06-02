package com.tencent.trc.plan.logical;

import java.util.ArrayList;

import com.tencent.trc.metastore.Table;

public class OpDesc1TS extends OpDesc {
	private static final long serialVersionUID = -4244342859231259475L;

	private Table table;
	private ArrayList<String> outputColumnNames;
	private boolean isDimensionTable = false;

	public OpDesc1TS() {
		super();
	}

	@Override
	public String getName() {
		return "TS";
	}

	public ArrayList<String> getOutputColumnNames() {
		return outputColumnNames;
	}

	public void setOutputColumnNames(ArrayList<String> outputColumnNames) {
		this.outputColumnNames = outputColumnNames;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public boolean isDimensionTable() {
		return isDimensionTable;
	}

	public void setDimensionTable(boolean isDimensionTable) {
		this.isDimensionTable = isDimensionTable;
	}

}
