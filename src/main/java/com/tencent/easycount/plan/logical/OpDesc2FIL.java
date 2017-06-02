package com.tencent.easycount.plan.logical;

import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;

import com.tencent.easycount.plan.Explain;

public class OpDesc2FIL extends OpDesc {
	private static final long serialVersionUID = 5350458578233988790L;
	private final String opName;

	@Override
	public String getName() {
		return this.opName;
	}

	private ExprNodeDesc predicate;

	public OpDesc2FIL(final boolean having) {
		super();
		this.opName = having ? "HAVING" : "FIL";
	}

	@Explain(displayName = "predicate")
	public ExprNodeDesc getPredicate() {
		return this.predicate;
	}

	public void setPredicate(final ExprNodeDesc predicate) {
		this.predicate = predicate;
	}

	// @Override
	// public Object clone() {
	// OpDesc2FIL filterDesc = new OpDesc2FIL(getPredicate().clone(),
	// getColList() == null ? null : new ArrayList<ExprNodeDesc>(
	// getColList()), getOutputColumnNames() == null ? null
	// : new ArrayList<String>(getOutputColumnNames()));
	// return filterDesc;
	// }

}
