package com.tencent.trc.exec.logical;

import com.tencent.trc.conf.TrcConfiguration;
import com.tencent.trc.exec.io.TaskContext;
import com.tencent.trc.plan.logical.OpDesc;
import com.tencent.trc.plan.logical.OpDesc1TS;
import com.tencent.trc.plan.logical.OpDesc2FIL;
import com.tencent.trc.plan.logical.OpDesc3JOIN;
import com.tencent.trc.plan.logical.OpDesc4UNION;
import com.tencent.trc.plan.logical.OpDesc5GBY;
import com.tencent.trc.plan.logical.OpDesc6SEL;
import com.tencent.trc.plan.logical.OpDesc7FS;

public class OperatorFactory {

	public static Operator<? extends OpDesc> getOperator(OpDesc opdesc,
			TrcConfiguration hconf, TaskContext taskContext) {
		if (opdesc instanceof OpDesc1TS) {
			return new Operator1TS((OpDesc1TS) opdesc, hconf, taskContext);
		}
		if (opdesc instanceof OpDesc2FIL) {
			return new Operator2FIL((OpDesc2FIL) opdesc, hconf, taskContext);
		}
		if (opdesc instanceof OpDesc3JOIN) {
			return new Operator3JOIN((OpDesc3JOIN) opdesc, hconf, taskContext);
		}
		if (opdesc instanceof OpDesc4UNION) {
			return new Operator4UNION((OpDesc4UNION) opdesc, hconf, taskContext);
		}
		if (opdesc instanceof OpDesc5GBY) {
			return new Operator5GBY((OpDesc5GBY) opdesc, hconf, taskContext);
		}
		if (opdesc instanceof OpDesc6SEL) {
			return new Operator6SEL((OpDesc6SEL) opdesc, hconf, taskContext);
		}
		if (opdesc instanceof OpDesc7FS) {
			return new Operator7FS((OpDesc7FS) opdesc, hconf, taskContext);
		}
		return null;
	}

}
