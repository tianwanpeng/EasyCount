package com.tencent.easycount.plan;

import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.ErrorMsg;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeGenericFuncDesc;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFBaseCompare;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

import com.tencent.easycount.plan.ExprNodeNewColumnRefDesc.RefMode;

public class ExprNodeNewForeachDesc extends ExprNodeDesc {

	private static final long serialVersionUID = 1L;

	private ExprNodeDesc fromListDesc;
	private ExprNodeNewGenerateDesc generateDesc;
	private RefMode fmode;

	public ExprNodeNewForeachDesc() {
	}

	public ExprNodeNewForeachDesc(ExprNodeDesc fromListDesc,
			ExprNodeNewGenerateDesc generateDesc, RefMode fmode) {
		super(TypeInfoFactory.getListTypeInfo(generateDesc.getTypeInfo()));
		this.fromListDesc = fromListDesc;
		this.generateDesc = generateDesc;
		this.fmode = fmode;
	}

	public ExprNodeDesc getFromListDesc() {
		return fromListDesc;
	}

	public void setFromListDesc(ExprNodeDesc fromListDesc) {
		this.fromListDesc = fromListDesc;
	}

	public ExprNodeNewGenerateDesc getGenerateDesc() {
		return generateDesc;
	}

	public void setGenerateDesc(ExprNodeNewGenerateDesc generateDesc) {
		this.generateDesc = generateDesc;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(fromListDesc.toString());
		sb.append(", ");
		sb.append(generateDesc.toString());
		sb.append(")");
		return sb.toString();
	}

	@Explain(displayName = "expr")
	@Override
	public String getExprString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(fromListDesc.getExprString());
		sb.append(", ");
		sb.append(generateDesc.getExprString());
		sb.append(")");
		return sb.toString();
	}

	@Override
	public ExprNodeNewForeachDesc clone() {
		ExprNodeNewForeachDesc clone = new ExprNodeNewForeachDesc(
				this.fromListDesc.clone(), this.generateDesc.clone(),
				this.fmode);
		return clone;
	}

	/**
	 * Create a exprNodeGenericFuncDesc based on the genericUDFClass and the
	 * children parameters.
	 * 
	 * @throws UDFArgumentException
	 */
	public static ExprNodeGenericFuncDesc newInstance(GenericUDF genericUDF,
			List<ExprNodeDesc> children) throws UDFArgumentException {
		ObjectInspector[] childrenOIs = new ObjectInspector[children.size()];
		for (int i = 0; i < childrenOIs.length; i++) {
			childrenOIs[i] = children.get(i).getWritableObjectInspector();
		}

		// Check if a bigint is implicitely cast to a double as part of a
		// comparison
		// Perform the check here instead of in GenericUDFBaseCompare to
		// guarantee it is only run once per operator
		if (genericUDF instanceof GenericUDFBaseCompare && children.size() == 2) {

			TypeInfo oiTypeInfo0 = children.get(0).getTypeInfo();
			TypeInfo oiTypeInfo1 = children.get(1).getTypeInfo();

			SessionState ss = SessionState.get();
			Configuration conf = (ss != null) ? ss.getConf()
					: new Configuration();

			// For now, if a bigint is going to be cast to a double throw an
			// error or warning
			if ((oiTypeInfo0.equals(TypeInfoFactory.stringTypeInfo) && oiTypeInfo1
					.equals(TypeInfoFactory.longTypeInfo))
					|| (oiTypeInfo0.equals(TypeInfoFactory.longTypeInfo) && oiTypeInfo1
							.equals(TypeInfoFactory.stringTypeInfo))) {
				if (HiveConf.getVar(conf, HiveConf.ConfVars.HIVEMAPREDMODE)
						.equalsIgnoreCase("strict")) {
					throw new UDFArgumentException(
							ErrorMsg.NO_COMPARE_BIGINT_STRING.getMsg());
				} else {
					// console.printError("WARNING: Comparing a bigint and a string may result in a loss of precision.");
				}
			} else if ((oiTypeInfo0.equals(TypeInfoFactory.doubleTypeInfo) && oiTypeInfo1
					.equals(TypeInfoFactory.longTypeInfo))
					|| (oiTypeInfo0.equals(TypeInfoFactory.longTypeInfo) && oiTypeInfo1
							.equals(TypeInfoFactory.doubleTypeInfo))) {
				if (HiveConf.getVar(conf, HiveConf.ConfVars.HIVEMAPREDMODE)
						.equalsIgnoreCase("strict")) {
					throw new UDFArgumentException(
							ErrorMsg.NO_COMPARE_BIGINT_DOUBLE.getMsg());
				} else {
					// console.printError("WARNING: Comparing a bigint and a double may result in a loss of precision.");
				}
			}
		}

		ObjectInspector oi = genericUDF.initializeAndFoldConstants(childrenOIs);

		String[] requiredJars = genericUDF.getRequiredJars();
		String[] requiredFiles = genericUDF.getRequiredFiles();
		SessionState ss = SessionState.get();

		if (requiredJars != null) {
			SessionState.ResourceType t = SessionState
					.find_resource_type("JAR");
			for (String jarPath : requiredJars) {
				ss.add_resource(t, jarPath);
			}
		}

		if (requiredFiles != null) {
			SessionState.ResourceType t = SessionState
					.find_resource_type("FILE");
			for (String filePath : requiredFiles) {
				ss.add_resource(t, filePath);
			}
		}

		return new ExprNodeGenericFuncDesc(oi, genericUDF, children);
	}

	@Override
	public boolean isSame(Object o) {
		if (!(o instanceof ExprNodeNewForeachDesc)) {
			return false;
		}
		ExprNodeNewForeachDesc dest = (ExprNodeNewForeachDesc) o;
		if (!getTypeInfo().equals(dest.getTypeInfo())
				|| !this.fromListDesc.isSame(dest.fromListDesc)
				|| !this.generateDesc.isSame(dest.generateDesc)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int superHashCode = super.hashCode();
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.appendSuper(superHashCode);
		builder.append(fromListDesc);
		builder.append(generateDesc);
		return builder.toHashCode();
	}

	public RefMode getFmode() {
		return fmode;
	}

	public void setFmode(RefMode fmode) {
		this.fmode = fmode;
	}

}