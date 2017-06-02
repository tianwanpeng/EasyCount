package com.tencent.easycount.plan.logical;

import java.util.ArrayList;

import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;

import com.tencent.easycount.plan.Explain;

public class OpDesc6SEL extends OpDesc {

	private static final long serialVersionUID = 1L;
	private boolean selectStar;
	private boolean selStarNoCompute = false;
	private ArrayList<ExprNodeDesc> colList;
	private ArrayList<String> outputColumnNames;
	private int expandIdx;

	private ExprNodeDesc keyExpr = null;
	private ExprNodeDesc attrsExpr = null;

	public int getExpandIdx() {
		return this.expandIdx;
	}

	public OpDesc6SEL() {
		super();
	}

	@Explain(displayName = "SELECT * ")
	public String explainNoCompute() {
		if (isSelStarNoCompute()) {
			return "(no compute)";
		} else {
			return null;
		}
	}

	/**
	 * @return the selectStar
	 */
	public boolean isSelectStar() {
		return this.selectStar;
	}

	/**
	 * @param selectStar
	 *            the selectStar to set
	 */
	public void setSelectStar(final boolean selectStar) {
		this.selectStar = selectStar;
	}

	/**
	 * @return the selStarNoCompute
	 */
	public boolean isSelStarNoCompute() {
		return this.selStarNoCompute;
	}

	/**
	 * @param selStarNoCompute
	 *            the selStarNoCompute to set
	 */
	public void setSelStarNoCompute(final boolean selStarNoCompute) {
		this.selStarNoCompute = selStarNoCompute;
	}

	// @Override
	// public Object clone() {
	// OpDesc9SEL ret = new OpDesc9SEL(getColList() == null ? null
	// : new ArrayList<ExprNodeDesc>(getColList()),
	// getOutputColumnNames() == null ? null : new ArrayList<String>(
	// getOutputColumnNames()));
	// ret.setSelectStar(selectStar);
	// ret.setSelStarNoCompute(selStarNoCompute);
	// return ret;
	// }

	@Override
	public String getName() {
		return "SEL";
	}

	public void setColList(final ArrayList<ExprNodeDesc> colList) {
		this.colList = colList;
	}

	public void setOutputColumnNames(final ArrayList<String> outputColumnNames) {
		this.outputColumnNames = outputColumnNames;
	}

	public ArrayList<ExprNodeDesc> getColList() {
		return this.colList;
	}

	public ArrayList<String> getOutputColumnNames() {
		return this.outputColumnNames;
	}

	public void setExpandIdx(final int expandIdx) {
		this.expandIdx = expandIdx;
	}

	public ExprNodeDesc getKeyExpr() {
		return this.keyExpr;
	}

	public void setKeyExpr(final ExprNodeDesc keyExpr) {
		this.keyExpr = keyExpr;
	}

	public void setAttrsExpr(final ExprNodeDesc attrsExpr) {
		this.attrsExpr = attrsExpr;
	}

	public ExprNodeDesc getAttrsExpr() {
		return this.attrsExpr;
	}

}
