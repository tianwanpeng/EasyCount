package com.tencent.easycount.exec.logical;

import com.tencent.easycount.conf.TrcConfiguration;
import com.tencent.easycount.exec.io.TaskContext;
import com.tencent.easycount.plan.logical.OpDesc1TS;

/**
 * just transfer, no need to do any other works
 * 
 * @author steventian
 * 
 */
public class Operator1TS extends Operator<OpDesc1TS> {

	@Override
	public void printInternal(int printId) {
	}

	public Operator1TS(OpDesc1TS opDesc, TrcConfiguration hconf,
			TaskContext taskContext) {
		super(opDesc, hconf, taskContext);
	}

	@Override
	public void processOp(Object row, int tag) {
		forward(row);
	}

}
