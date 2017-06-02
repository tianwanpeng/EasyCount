package com.tencent.trc.plan;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class ExprNodeNewGenerateDesc extends ExprNodeDesc {

	private static final long serialVersionUID = 1L;

	private ExprNodeDesc generateDesc;
	private ExprNodeDesc generateDesc1;

	private boolean generateMap = false;

	public ExprNodeNewGenerateDesc() {
	}

	public ExprNodeNewGenerateDesc(ExprNodeDesc generateDesc,
			ExprNodeDesc generateDesc1, boolean generateMap) {
		super(TypeInfoFactory.getListTypeInfo(generateDesc.getTypeInfo()));
		this.generateDesc = generateDesc;
		this.generateDesc1 = generateDesc1;
		this.generateMap = generateMap;
	}

	public ExprNodeDesc getGenerateDesc() {
		return generateDesc;
	}

	public void setGenerateDesc(ExprNodeDesc generateDesc) {
		this.generateDesc = generateDesc;
	}

	@Override
	public String toString() {
		return getExprString();
	}

	@Explain(displayName = "expr")
	@Override
	public String getExprString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(generateDesc.getExprString());
		sb.append(", ");
		sb.append(generateDesc1 == null ? "null" : generateDesc1
				.getExprString());
		sb.append(")");
		return sb.toString();
	}

	@Override
	public ExprNodeNewGenerateDesc clone() {
		ExprNodeNewGenerateDesc clone = new ExprNodeNewGenerateDesc(
				this.generateDesc.clone(),
				this.generateMap ? this.generateDesc1.clone() : null,
				this.generateMap);
		return clone;
	}

	@Override
	public boolean isSame(Object o) {
		if (!(o instanceof ExprNodeNewGenerateDesc)) {
			return false;
		}
		ExprNodeNewGenerateDesc dest = (ExprNodeNewGenerateDesc) o;
		if (!getTypeInfo().equals(dest.getTypeInfo())
				|| !this.generateDesc.isSame(dest.generateDesc)) {
			return false;
		}
		if (this.generateMap != dest.generateMap) {
			return false;
		}
		if (this.generateMap && !this.generateDesc1.isSame(dest.generateDesc1)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int superHashCode = super.hashCode();
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.appendSuper(superHashCode);
		builder.append(generateDesc);
		builder.append(generateDesc1);
		return builder.toHashCode();
	}

	public boolean isGenerateMap() {
		return generateMap;
	}

	public void setGenerateMap(boolean generateMap) {
		this.generateMap = generateMap;
	}

	public ExprNodeDesc getGenerateDesc1() {
		return generateDesc1;
	}

	public void setGenerateDesc1(ExprNodeDesc generateDesc1) {
		this.generateDesc1 = generateDesc1;
	}

}