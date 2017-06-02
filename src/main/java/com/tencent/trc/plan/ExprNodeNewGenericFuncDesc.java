package com.tencent.trc.plan;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.ErrorMsg;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.Utilities;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeGenericFuncDesc;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFBaseCompare;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFBridge;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;

public class ExprNodeNewGenericFuncDesc extends ExprNodeDesc {

	private static final long serialVersionUID = 1L;

	/**
	 * In case genericUDF is Serializable, we will serialize the object.
	 * 
	 * In case genericUDF does not implement Serializable, Java will remember
	 * the class of genericUDF and creates a new instance when deserialized.
	 * This is exactly what we want.
	 */

	// private String udfName;
	// private Class<? extends GenericUDFBridge> genericUDFBridgeClass;

	private Class<? extends GenericUDF> genericUDFClass;
	private GenericUDFBridge genericUDFBridge = null;

	public GenericUDFBridge getGenericUDFBridge() {
		return genericUDFBridge;
	}

	public Class<? extends GenericUDF> getGenericUDFClass() {
		return genericUDFClass;
	}

	public void setGenericUDFClass(Class<? extends GenericUDF> genericUDFClass) {
		this.genericUDFClass = genericUDFClass;
	}

	private List<ExprNodeDesc> childExprs;
	/**
	 * This class uses a writableObjectInspector rather than a TypeInfo to store
	 * the canonical type information for this NodeDesc.
	 */
	private transient ObjectInspector writableObjectInspector;
	// Is this an expression that should perform a comparison for sorted
	// searches
	private boolean isSortedExpr;

	public ExprNodeNewGenericFuncDesc() {
	}

	public ExprNodeNewGenericFuncDesc(TypeInfo typeInfo, GenericUDF genericUDF,
			List<ExprNodeDesc> children) {
		this(TypeInfoUtils
				.getStandardWritableObjectInspectorFromTypeInfo(typeInfo),
				genericUDF, children);
	}

	public ExprNodeNewGenericFuncDesc(ObjectInspector oi,
			GenericUDF genericUDF, List<ExprNodeDesc> children) {
		super(TypeInfoUtils.getTypeInfoFromObjectInspector(oi));
		this.writableObjectInspector = ObjectInspectorUtils
				.getWritableObjectInspector(oi);
		assert (genericUDFClass != null);

		this.genericUDFClass = genericUDF.getClass();
		this.childExprs = children;

		if (this.genericUDFClass == GenericUDFBridge.class) {
			this.genericUDFBridge = ((GenericUDFBridge) genericUDF);
		}

	}

	public ExprNodeNewGenericFuncDesc(ExprNodeGenericFuncDesc resdesc) {
		this(resdesc.getTypeInfo(), resdesc.getGenericUDF(), resdesc
				.getChildren());
		// if (resdesc.getGenericUDF().getClass() == GenericUDFBridge.class) {
		//
		// GenericUDFBridge genericUDFBridge1 = (GenericUDFBridge) resdesc
		// .getGenericUDF();
		//
		// //this.genericUDFBridgeClass = genericUDFBridge1.getClass();
		// this.genericUDFClass = genericUDFBridge1.getUdfClass();
		// }
	}

	public ExprNodeNewGenericFuncDesc(TypeInfo typeInfo,
			Class<? extends GenericUDF> genericUDFClass,
			GenericUDFBridge genericUDF, List<ExprNodeDesc> cloneCh) {
		super(typeInfo);
		this.genericUDFClass = genericUDFClass;
		this.genericUDFBridge = genericUDF;
		this.childExprs = cloneCh;
	}

	@Override
	public ObjectInspector getWritableObjectInspector() {
		return writableObjectInspector;
	}

	// public GenericUDFBridge getGenericUDF() {
	// return genericUDFBridge;
	// }
	//
	// public void setGenericUDF(GenericUDFBridge genericUDFBridge) {
	// this.genericUDFBridge = genericUDFBridge;
	// }

	public List<ExprNodeDesc> getChildExprs() {
		return childExprs;
	}

	public void setChildExprs(List<ExprNodeDesc> children) {
		childExprs = children;
	}

	@Override
	public List<ExprNodeDesc> getChildren() {
		return childExprs;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(genericUDFClass);
		sb.append("(");
		for (int i = 0; i < childExprs.size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(childExprs.get(i).toString());
		}
		sb.append("(");
		sb.append(")");
		return sb.toString();
	}

	@Explain(displayName = "expr")
	@Override
	public String getExprString() {
		// Get the children expr strings
		String[] childrenExprStrings = new String[childExprs.size()];
		for (int i = 0; i < childrenExprStrings.length; i++) {
			childrenExprStrings[i] = childExprs.get(i).getExprString();
		}

		// return genericUDFClass.getDisplayString(childrenExprStrings);

		StringBuilder sb = new StringBuilder();
		sb.append(genericUDFClass.getName());
		sb.append("(");
		for (int i = 0; i < childrenExprStrings.length; i++) {
			sb.append(childrenExprStrings[i]);
			if (i + 1 < childrenExprStrings.length) {
				sb.append(", ");
			}
		}
		sb.append(")");
		return sb.toString();

	}

	@Override
	public List<String> getCols() {
		List<String> colList = new ArrayList<String>();
		if (childExprs != null) {
			int pos = 0;
			while (pos < childExprs.size()) {
				List<String> colCh = childExprs.get(pos).getCols();
				colList = Utilities.mergeUniqElems(colList, colCh);
				pos++;
			}
		}

		return colList;
	}

	@Override
	public ExprNodeNewGenericFuncDesc clone() {
		List<ExprNodeDesc> cloneCh = new ArrayList<ExprNodeDesc>(
				childExprs.size());
		for (ExprNodeDesc ch : childExprs) {
			cloneCh.add(ch.clone());
		}
		ExprNodeNewGenericFuncDesc clone = new ExprNodeNewGenericFuncDesc(
				getTypeInfo(), this.genericUDFClass, this.genericUDFBridge,
				cloneCh);
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
		if (!(o instanceof ExprNodeNewGenericFuncDesc)) {
			return false;
		}
		ExprNodeNewGenericFuncDesc dest = (ExprNodeNewGenericFuncDesc) o;
		if (!getTypeInfo().equals(dest.getTypeInfo())
				|| !genericUDFClass.equals(dest.getGenericUDFClass())) {
			return false;
		}

		// if (genericUDFClass == GenericUDFBridge.class) {
		// GenericUDFBridge bridge = (GenericUDFBridge) genericUDFBridge;
		// GenericUDFBridge bridge2 = (GenericUDFBridge) dest.getGenericUDF();
		// if (!bridge.getUdfClass().equals(bridge2.getUdfClass())
		// || !bridge.getUdfName().equals(bridge2.getUdfName())
		// || bridge.isOperator() != bridge2.isOperator()) {
		// return false;
		// }
		// }

		if (childExprs.size() != dest.getChildExprs().size()) {
			return false;
		}

		for (int pos = 0; pos < childExprs.size(); pos++) {
			if (!childExprs.get(pos).isSame(dest.getChildExprs().get(pos))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		int superHashCode = super.hashCode();
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.appendSuper(superHashCode);
		builder.append(childExprs);
		return builder.toHashCode();
	}

	public boolean isSortedExpr() {
		return isSortedExpr;
	}

	public void setSortedExpr(boolean isSortedExpr) {
		this.isSortedExpr = isSortedExpr;
	}

	// public Class<? extends GenericUDFBridge> getGenericUDFBridgeClass() {
	// return genericUDFBridgeClass;
	// }
	//
	// public void setGenericUDFBridgeClass(
	// Class<? extends GenericUDFBridge> genericUDFBridgeClass) {
	// this.genericUDFBridgeClass = genericUDFBridgeClass;
	// }
}
