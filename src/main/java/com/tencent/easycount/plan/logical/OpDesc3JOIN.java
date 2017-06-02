package com.tencent.easycount.plan.logical;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;

import com.tencent.easycount.metastore.Table;

public class OpDesc3JOIN extends OpDesc {

	private static final long serialVersionUID = 6290504314698557385L;

	private String streamAlias;
	private HashMap<String, Table> alias2JoinTables;

	private HashMap<String, ExprNodeDesc> alias2ConditionKeyExprs;

	private HashMap<String, ArrayList<ExprNodeDesc>> alias2ExprDesc;
	private ArrayList<String> outputColumnNames;

	public HashMap<String, Table> getAlias2JoinTables() {
		return this.alias2JoinTables;
	}

	public void setAlias2JoinTables(
			final HashMap<String, Table> alias2JoinTables) {
		this.alias2JoinTables = alias2JoinTables;
	}

	public OpDesc3JOIN() {
		super();
	}

	@Override
	public String getName() {
		return "JOIN";
	}

	public String getStreamAlias() {
		return this.streamAlias;
	}

	public void setStreamAlias(final String streamAlias) {
		this.streamAlias = streamAlias;
	}

	public ArrayList<String> getOutputColumnNames() {
		return this.outputColumnNames;
	}

	public void setOutputColumnNames(final ArrayList<String> outputColumnNames) {
		this.outputColumnNames = outputColumnNames;
	}

	public HashMap<String, ExprNodeDesc> getAlias2ConditionKeyExprs() {
		return this.alias2ConditionKeyExprs;
	}

	public void setAlias2ConditionKeyExprs(
			final HashMap<String, ExprNodeDesc> alias2ConditionKeyExprs) {
		this.alias2ConditionKeyExprs = alias2ConditionKeyExprs;
	}

	public HashMap<String, ArrayList<ExprNodeDesc>> getAlias2ExprDesc() {
		return this.alias2ExprDesc;
	}

	public void setAlias2ExprDesc(
			final HashMap<String, ArrayList<ExprNodeDesc>> alias2ExprDesc) {
		this.alias2ExprDesc = alias2ExprDesc;
	}

}
