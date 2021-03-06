package com.tencent.easycount.exec.logical;

import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluator;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.conf.ECConfiguration;
import com.tencent.easycount.exec.io.TaskContext;
import com.tencent.easycount.plan.ExprNodeEvaluatorFactoryNew;
import com.tencent.easycount.plan.logical.OpDesc2FIL;
import com.tencent.easycount.util.status.TDBankUtils;

public class Operator2FIL extends Operator<OpDesc2FIL> {
	private static Logger log = LoggerFactory.getLogger(Operator2FIL.class);

	@SuppressWarnings("rawtypes")
	private transient ExprNodeEvaluator conditionEvaluator;
	private transient PrimitiveObjectInspector conditionInspector;

	@Override
	public void printInternal(int printId) {
	}

	public Operator2FIL(OpDesc2FIL opDesc, ECConfiguration hconf,
			TaskContext taskContext) {
		super(opDesc, hconf, taskContext);
	}

	@Override
	protected void initializeOp(ECConfiguration hconf, TaskContext taskContext) {
		try {
			ExprNodeDesc predicate = getOpDesc().getPredicate();
			conditionEvaluator = ExprNodeEvaluatorFactoryNew.get(predicate);
			conditionInspector = (PrimitiveObjectInspector) conditionEvaluator
					.initialize(inputObjInspectors[0]);
		} catch (Exception e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}
		initializeChildren(hconf, taskContext);
	}

	@Override
	public void processOp(Object row, int tag) {
		try {
			Object condition = conditionEvaluator.evaluate(row);
			Boolean ret = (Boolean) conditionInspector
					.getPrimitiveJavaObject(condition);
			if (Boolean.TRUE.equals(ret)) {
				forward(row);
			}
		} catch (HiveException e) {
			log.error(TDBankUtils.getExceptionStack(e));
		}
	}
}
