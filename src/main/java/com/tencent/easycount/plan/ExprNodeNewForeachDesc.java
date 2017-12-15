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

	public ExprNodeNewForeachDesc(final ExprNodeDesc fromListDesc,
			final ExprNodeNewGenerateDesc generateDesc, final RefMode fmode) {
		super(TypeInfoFactory.getListTypeInfo(generateDesc.getTypeInfo()));
		this.fromListDesc = fromListDesc;
		this.generateDesc = generateDesc;
		this.fmode = fmode;
	}

	public ExprNodeDesc getFromListDesc() {
		return this.fromListDesc;
	}

	public void setFromListDesc(final ExprNodeDesc fromListDesc) {
		this.fromListDesc = fromListDesc;
	}

	public ExprNodeNewGenerateDesc getGenerateDesc() {
		return this.generateDesc;
	}

	public void setGenerateDesc(final ExprNodeNewGenerateDesc generateDesc) {
		this.generateDesc = generateDesc;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(this.fromListDesc.toString());
		sb.append(", ");
		sb.append(this.generateDesc.toString());
		sb.append(")");
		return sb.toString();
	}

	@Explain(displayName = "expr")
	@Override
	public String getExprString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(this.fromListDesc.getExprString());
		sb.append(", ");
		sb.append(this.generateDesc.getExprString());
		sb.append(")");
		return sb.toString();
	}

	@Override
	public ExprNodeNewForeachDesc clone() {
		final ExprNodeNewForeachDesc clone = new ExprNodeNewForeachDesc(
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
	public static ExprNodeGenericFuncDesc newInstance(
			final GenericUDF genericUDF, final List<ExprNodeDesc> children)
					throws UDFArgumentException {
		final ObjectInspector[] childrenOIs = new ObjectInspector[children
		                                                          .size()];
		for (int i = 0; i < childrenOIs.length; i++) {
			childrenOIs[i] = children.get(i).getWritableObjectInspector();
		}

		// Check if a bigint is implicitely cast to a double as part of a
		// comparison
		// Perform the check here instead of in GenericUDFBaseCompare to
		// guarantee it is only run once per operator
		if ((genericUDF instanceof GenericUDFBaseCompare)
				&& (children.size() == 2)) {

			final TypeInfo oiTypeInfo0 = children.get(0).getTypeInfo();
			final TypeInfo oiTypeInfo1 = children.get(1).getTypeInfo();

			final SessionState ss = SessionState.get();
			final Configuration conf = (ss != null) ? ss.getConf()
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

		final ObjectInspector oi = genericUDF
				.initializeAndFoldConstants(childrenOIs);

		final String[] requiredJars = genericUDF.getRequiredJars();
		final String[] requiredFiles = genericUDF.getRequiredFiles();
		final SessionState ss = SessionState.get();

		if (requiredJars != null) {
			final SessionState.ResourceType t = SessionState
					.find_resource_type("JAR");
			for (final String jarPath : requiredJars) {
				ss.add_resource(t, jarPath);
			}
		}

		if (requiredFiles != null) {
			final SessionState.ResourceType t = SessionState
					.find_resource_type("FILE");
			for (final String filePath : requiredFiles) {
				ss.add_resource(t, filePath);
			}
		}

		return new ExprNodeGenericFuncDesc(oi, genericUDF, children);
	}

	@Override
	public boolean isSame(final Object o) {
		if (!(o instanceof ExprNodeNewForeachDesc)) {
			return false;
		}
		final ExprNodeNewForeachDesc dest = (ExprNodeNewForeachDesc) o;
		if (!getTypeInfo().equals(dest.getTypeInfo())
				|| !this.fromListDesc.isSame(dest.fromListDesc)
				|| !this.generateDesc.isSame(dest.generateDesc)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		final int superHashCode = super.hashCode();
		final HashCodeBuilder builder = new HashCodeBuilder();
		builder.appendSuper(superHashCode);
		builder.append(this.fromListDesc);
		builder.append(this.generateDesc);
		return builder.toHashCode();
	}

	public RefMode getFmode() {
		return this.fmode;
	}

	public void setFmode(final RefMode fmode) {
		this.fmode = fmode;
	}

}