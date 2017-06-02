package com.tencent.trc.plan;

import org.apache.hadoop.hive.ql.plan.Explain;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator;

public class AggregationDescWrapper implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String genericUDAFName;

	private Class<? extends GenericUDAFEvaluator> genericUDAFEvaluatorClass;
	private java.util.ArrayList<ExprNodeDesc> parameters;
	private boolean distinct;
	private GenericUDAFEvaluator.Mode mode;
	private AggrMode aggrMode;

	public enum AggrMode {
		AGGR, ACCU, SW
	}

	public AggregationDescWrapper() {
	}

	public AggregationDescWrapper(
			final String genericUDAFName,
			final Class<? extends GenericUDAFEvaluator> genericUDAFEvaluatorClass,
			final java.util.ArrayList<ExprNodeDesc> parameters,
			final boolean distinct, final GenericUDAFEvaluator.Mode mode,
			AggrMode aggrMode) {
		this.genericUDAFName = genericUDAFName;
		this.genericUDAFEvaluatorClass = genericUDAFEvaluatorClass;
		this.parameters = parameters;
		this.distinct = distinct;
		this.mode = mode;
		this.aggrMode = aggrMode;
	}

	public void setGenericUDAFName(final String genericUDAFName) {
		this.genericUDAFName = genericUDAFName;
	}

	public String getGenericUDAFName() {
		return genericUDAFName;
	}

	public void setGenericUDAFEvaluatorClass(
			final Class<? extends GenericUDAFEvaluator> genericUDAFEvaluatorClass) {
		this.genericUDAFEvaluatorClass = genericUDAFEvaluatorClass;
	}

	public Class<? extends GenericUDAFEvaluator> getGenericUDAFEvaluatorClass() {
		return genericUDAFEvaluatorClass;
	}

	public java.util.ArrayList<ExprNodeDesc> getParameters() {
		return parameters;
	}

	public void setParameters(final java.util.ArrayList<ExprNodeDesc> parameters) {
		this.parameters = parameters;
	}

	public boolean getDistinct() {
		return distinct;
	}

	public void setDistinct(final boolean distinct) {
		this.distinct = distinct;
	}

	public void setMode(final GenericUDAFEvaluator.Mode mode) {
		this.mode = mode;
	}

	public GenericUDAFEvaluator.Mode getMode() {
		return mode;
	}

	@Explain(displayName = "expr")
	public String getExprString() {
		StringBuilder sb = new StringBuilder();
		sb.append(genericUDAFName);
		sb.append("(");
		if (distinct) {
			sb.append("DISTINCT ");
		}
		boolean first = true;
		for (ExprNodeDesc exp : parameters) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(exp.getExprString());
		}
		sb.append(")");
		return sb.toString();
	}

	public AggrMode getAggrMode() {
		return aggrMode;
	}

	public void setAggrMode(AggrMode aggrMode) {
		this.aggrMode = aggrMode;
	}

}
