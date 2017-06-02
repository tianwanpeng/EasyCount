package com.tencent.easycount.plan.logical;

import com.tencent.easycount.metastore.Table;

public class OpDesc7FS extends OpDesc {
	private static final long serialVersionUID = -3478737359649515222L;

	private String tableId = null;
	private Table table = null;
	private boolean containsKeyExpr = false;
	private boolean containsAttrsExpr = false;

	// private ExprNodeDesc keyExpr = null;
	// private ExprNodeDesc attrsExpr = null;

	public OpDesc7FS() {
		super();
	}

	@Override
	public String getName() {
		return "FS";
	}

	public void setTableId(final String tableId) {
		this.tableId = tableId;
	}

	public String getTableId() {
		return this.tableId;
	}

	public void setTable(final Table table) {
		this.table = table;
	}

	public Table getTable() {
		return this.table;
	}

	public void setContainsKeyExpr(final boolean containsKeyExpr) {
		this.containsKeyExpr = containsKeyExpr;
	}

	public void setContainsAttrsExpr(final boolean containsAttrsExpr) {
		this.containsAttrsExpr = containsAttrsExpr;
	}

	public boolean containsKeyExpr() {
		return this.containsKeyExpr;
	}

	public boolean containsAttrsExpr() {
		return this.containsAttrsExpr;
	}

	// public ExprNodeDesc getKeyExpr() {
	// return keyExpr;
	// }
	//
	// public void setKeyExpr(ExprNodeDesc keyExpr) {
	// this.keyExpr = keyExpr;
	// }
	//
	// public void setAttrsExpr(ExprNodeDesc attrsExpr) {
	// this.attrsExpr = attrsExpr;
	// }
	//
	// public ExprNodeDesc getAttrsExpr() {
	// return attrsExpr;
	// }

}
