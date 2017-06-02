package com.tencent.trc.plan.physical;

import java.util.HashMap;
import java.util.HashSet;

import com.tencent.trc.plan.logical.OpDesc;

public class TaskWork1Spout extends TaskWork {

	private static final long serialVersionUID = -8097634060545352056L;

	public TaskWork1Spout(Integer taskId, HashSet<OpDesc> opDescs,
			HashMap<OpDesc, Integer> opDesc2TaskId) {
		super(taskId, opDescs, opDesc2TaskId);
	}

	@Override
	public String getName() {
		return "SPOUT";
	}

}
