package com.tencent.easycount.plan;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.hadoop.hive.ql.plan.ExprNodeColumnDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;

import com.tencent.easycount.plan.ExprNodeNewColumnRefDesc.RefMode;
import com.tencent.easycount.plan.TypeCheckCtxTRC.Var;

public class ExprNodeNewForeachVarDesc extends ExprNodeDesc {
	private static final long serialVersionUID = 1L;

	/**
	 * The column name.
	 */
	private Var var;
	private RefMode fmode;
	private final ExprNodeColumnDesc columnDesc;
	private ExprNodeDesc listDesc;

	public ExprNodeNewForeachVarDesc(final Var var, final RefMode fmode,
			final ExprNodeDesc listDesc) {
		super(var.getTypeInfo());
		this.var = var;
		this.setFmode(fmode);
		this.setListDesc(listDesc);
		this.columnDesc = new ExprNodeColumnDesc(var.getTypeInfo(),
				var.getVarInternalName(), "", false);
	}

	public Var getVar() {
		return this.var;
	}

	public void setVar(final Var var) {
		this.var = var;
	}

	public String getColumn() {
		return this.var.getVarName();
	}

	@Override
	public String toString() {
		return "Column[" + this.var.getVarName() + "]";
	}

	@Explain(displayName = "expr")
	@Override
	public String getExprString() {
		return getColumn();
	}

	@Override
	public List<String> getCols() {
		final List<String> lst = new ArrayList<String>();
		lst.add(this.var.getVarName());
		return lst;
	}

	@Override
	public ExprNodeDesc clone() {
		return new ExprNodeNewColumnRefDesc(this.var, this.fmode);
	}

	@Override
	public boolean isSame(final Object o) {
		if (!(o instanceof ExprNodeNewForeachVarDesc)) {
			return false;
		}
		final ExprNodeNewForeachVarDesc dest = (ExprNodeNewForeachVarDesc) o;
		if (!this.var.equals(dest.var)) {
			return false;
		}
		if (!getTypeInfo().equals(dest.getTypeInfo())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int superHashCode = super.hashCode();
		final HashCodeBuilder builder = new HashCodeBuilder();
		builder.appendSuper(superHashCode);
		builder.append(this.var.hashCode());
		return builder.toHashCode();
	}

	public RefMode getFmode() {
		return this.fmode;
	}

	public void setFmode(final RefMode fmode) {
		this.fmode = fmode;
	}

	public ExprNodeColumnDesc getColumnDesc() {
		return this.columnDesc;
	}

	public ExprNodeDesc getListDesc() {
		return this.listDesc;
	}

	public void setListDesc(final ExprNodeDesc listDesc) {
		this.listDesc = listDesc;
	}
}