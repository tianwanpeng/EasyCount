package com.tencent.easycount.exec.logical;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluator;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils.ObjectInspectorCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.conf.TrcConfiguration;
import com.tencent.easycount.exec.io.TaskContext;
import com.tencent.easycount.plan.ExprNodeEvaluatorFactoryNew;
import com.tencent.easycount.plan.logical.OpDesc6SEL;
import com.tencent.easycount.util.status.TDBankUtils;

public class Operator6SEL extends Operator<OpDesc6SEL> {
	private static Logger log = LoggerFactory.getLogger(Operator6SEL.class);

	@SuppressWarnings("rawtypes")
	protected transient ExprNodeEvaluator[] eval;

	private ListObjectInspector expandColumnOI = null;
	private ObjectInspector expandColumnFieldOI = null;

	@Override
	public void printInternal(int printId) {
	}

	public Operator6SEL(OpDesc6SEL opDesc, TrcConfiguration hconf,
			TaskContext taskContext) {
		super(opDesc, hconf, taskContext);
	}

	// private transient Object[] output;

	private int expandIdx = -1;

	@Override
	protected void initializeOp(TrcConfiguration hconf, TaskContext taskContext) {
		// Just forward the row as is
		if (getOpDesc().isSelStarNoCompute()) {
			// initializeChildren();
			return;
		}

		expandIdx = getOpDesc().getExpandIdx();

		ExprNodeDesc keyExpr = getOpDesc().getKeyExpr();
		ExprNodeDesc attrsExpr = getOpDesc().getAttrsExpr();

		int extendCols = 0;
		if (keyExpr != null) {
			extendCols++;
		}
		if (attrsExpr != null) {
			extendCols++;
		}
		ArrayList<ExprNodeDesc> colList = getOpDesc().getColList();
		eval = new ExprNodeEvaluator[colList.size() + extendCols];
		// output = new Object[eval.length];
		try {
			ArrayList<String> outputColumnNames = new ArrayList<String>();
			outputColumnNames.addAll(getOpDesc().getOutputColumnNames());
			for (int i = 0; i < colList.size(); i++) {
				eval[i] = ExprNodeEvaluatorFactoryNew.get(colList.get(i));
			}

			if (keyExpr != null) {
				outputColumnNames.add("__key");
				eval[colList.size()] = ExprNodeEvaluatorFactoryNew.get(keyExpr);
				if (attrsExpr != null) {
					outputColumnNames.add("__attrs");
					eval[colList.size() + 1] = ExprNodeEvaluatorFactoryNew
							.get(attrsExpr);
				}
			} else if (attrsExpr != null) {
				outputColumnNames.add("__attrs");
				eval[colList.size()] = ExprNodeEvaluatorFactoryNew
						.get(attrsExpr);
			}

			if (expandIdx < 0) {
				outputObjInspector = initEvaluatorsAndReturnStruct(eval,
						outputColumnNames, inputObjInspectors[0]);
			} else {
				ArrayList<ObjectInspector> filedsObjectInspector = new ArrayList<ObjectInspector>(
						eval.length);

				for (int i = 0; i < eval.length; i++) {
					if (i == expandIdx) {
						expandColumnOI = (ListObjectInspector) eval[i]
								.initialize(inputObjInspectors[0]);
						expandColumnFieldOI = expandColumnOI
								.getListElementObjectInspector();
						filedsObjectInspector.add(expandColumnFieldOI);
					} else {
						filedsObjectInspector.add(eval[i]
								.initialize(inputObjInspectors[0]));
					}
				}
				outputObjInspector = ObjectInspectorFactory
						.getStandardStructObjectInspector(outputColumnNames,
								filedsObjectInspector);
			}
			initializeChildren(hconf, taskContext);
		} catch (HiveException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}
	}

	@Override
	public void processOp(Object row, int tag) {
		// Just forward the row as is
		if (getOpDesc().isSelStarNoCompute()) {
			forward(row);
			return;
		}
		Object[] output = new Object[eval.length];
		for (int i = 0; i < eval.length; i++) {
			try {
				output[i] = eval[i].evaluate(row);
			} catch (Exception e) {
				output[i] = null;
				log.error(getPrintPrefix() + TDBankUtils.getExceptionStack(e));
			}
		}
		if (expandIdx < 0) {
			forward(output);
		} else {
			// @SuppressWarnings("unchecked")
			// ArrayList<Object> expandObjs = (ArrayList<Object>)
			// ObjectInspectorUtils
			// .copyToStandardObject(output[expandIdx], expandColumnOI,
			// ObjectInspectorCopyOption.WRITABLE);
			List<?> list = expandColumnOI.getList(output[expandIdx]);
			if (list == null) {
				output[expandIdx] = null;
				forward(output);
			} else {
				for (Object eobj : list) {
					output[expandIdx] = ObjectInspectorUtils
							.copyToStandardObject(eobj, expandColumnFieldOI,
									ObjectInspectorCopyOption.WRITABLE);
					forward(output);
				}
			}
		}
	}
}
