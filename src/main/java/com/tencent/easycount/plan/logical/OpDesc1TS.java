package com.tencent.easycount.plan.logical;

import java.util.ArrayList;

import com.tencent.easycount.metastore.Table;

/**
 * ts 包含以下信息，读取的表描述table，需要输出的字段列表，是否是维表
 *
 * @author steven
 *
 */
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
		return this.outputColumnNames;
	}

	public void setOutputColumnNames(final ArrayList<String> outputColumnNames) {
		this.outputColumnNames = outputColumnNames;
	}

	public Table getTable() {
		return this.table;
	}

	public void setTable(final Table table) {
		this.table = table;
	}

	public boolean isDimensionTable() {
		return this.isDimensionTable;
	}

	public void setDimensionTable(final boolean isDimensionTable) {
		this.isDimensionTable = isDimensionTable;
	}

}
