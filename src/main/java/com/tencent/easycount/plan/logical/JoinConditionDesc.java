package com.tencent.easycount.plan.logical;

import java.io.Serializable;
import java.util.ArrayList;

import com.tencent.easycount.parse.ASTNodeTRC;

public class JoinConditionDesc implements Serializable {
	private static final long serialVersionUID = 5672972494225028083L;
	private String leftAlias;
	private String[] rightAliases;
	// join conditions
	private ArrayList<ArrayList<ASTNodeTRC>> expressions;
	// filters
	private ArrayList<ArrayList<ASTNodeTRC>> filters;
	// filters for pushing
	private ArrayList<ArrayList<ASTNodeTRC>> filtersForPushing;

	/**
	 * constructor.
	 */
	public JoinConditionDesc() {
	}

	/**
	 * returns left alias if any - this is used for merging later on.
	 * 
	 * @return left alias if any
	 */
	public String getLeftAlias() {
		return leftAlias;
	}

	/**
	 * set left alias for the join expression.
	 * 
	 * @param leftAlias
	 *            String
	 */
	public void setLeftAlias(String leftAlias) {
		this.leftAlias = leftAlias;
	}

	public String[] getRightAliases() {
		return rightAliases;
	}

	public void setRightAliases(String[] rightAliases) {
		this.rightAliases = rightAliases;
	}

	public ArrayList<ArrayList<ASTNodeTRC>> getExpressions() {
		return expressions;
	}

	public void setExpressions(ArrayList<ArrayList<ASTNodeTRC>> expressions) {
		this.expressions = expressions;
	}

	/**
	 * @return the filters
	 */
	public ArrayList<ArrayList<ASTNodeTRC>> getFilters() {
		return filters;
	}

	/**
	 * @param filters
	 *            the filters to set
	 */
	public void setFilters(ArrayList<ArrayList<ASTNodeTRC>> filters) {
		this.filters = filters;
	}

	/**
	 * @return the filters for pushing
	 */
	public ArrayList<ArrayList<ASTNodeTRC>> getFiltersForPushing() {
		return filtersForPushing;
	}

	/**
	 * @param filters
	 *            for pushing the filters to set
	 */
	public void setFiltersForPushing(ArrayList<ArrayList<ASTNodeTRC>> filters) {
		this.filtersForPushing = filters;
	}

}
