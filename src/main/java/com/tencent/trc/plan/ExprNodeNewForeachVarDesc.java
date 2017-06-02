package com.tencent.trc.plan;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.hadoop.hive.ql.plan.ExprNodeColumnDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;

import com.tencent.trc.plan.ExprNodeNewColumnRefDesc.RefMode;
import com.tencent.trc.plan.TypeCheckCtxTRC.Var;

public class ExprNodeNewForeachVarDesc extends ExprNodeDesc {
	private static final long serialVersionUID = 1L;

	/**
	 * The column name.
	 */
	private Var var;
	private RefMode fmode;
	private ExprNodeColumnDesc columnDesc;
	private ExprNodeDesc listDesc;

	public ExprNodeNewForeachVarDesc(Var var, RefMode fmode,
			ExprNodeDesc listDesc) {
		super(var.getTypeInfo());
		this.var = var;
		this.setFmode(fmode);
		this.setListDesc(listDesc);
		columnDesc = new ExprNodeColumnDesc(var.getTypeInfo(),
				var.getVarInternalName(), "", false);
	}

	public Var getVar() {
		return var;
	}

	public void setVar(Var var) {
		this.var = var;
	}

	public String getColumn() {
		return var.getVarName();
	}

	@Override
	public String toString() {
		return "Column[" + var.getVarName() + "]";
	}

	@Explain(displayName = "expr")
	@Override
	public String getExprString() {
		return getColumn();
	}

	@Override
	public List<String> getCols() {
		List<String> lst = new ArrayList<String>();
		lst.add(var.getVarName());
		return lst;
	}

	@Override
	public ExprNodeDesc clone() {
		return new ExprNodeNewColumnRefDesc(var, this.fmode);
	}

	@Override
	public boolean isSame(Object o) {
		if (!(o instanceof ExprNodeNewForeachVarDesc)) {
			return false;
		}
		ExprNodeNewForeachVarDesc dest = (ExprNodeNewForeachVarDesc) o;
		if (!var.equals(dest.var)) {
			return false;
		}
		if (!getTypeInfo().equals(dest.getTypeInfo())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int superHashCode = super.hashCode();
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.appendSuper(superHashCode);
		builder.append(var.hashCode());
		return builder.toHashCode();
	}

	public RefMode getFmode() {
		return fmode;
	}

	public void setFmode(RefMode fmode) {
		this.fmode = fmode;
	}

	public ExprNodeColumnDesc getColumnDesc() {
		return columnDesc;
	}

	public ExprNodeDesc getListDesc() {
		return listDesc;
	}

	public void setListDesc(ExprNodeDesc listDesc) {
		this.listDesc = listDesc;
	}
}