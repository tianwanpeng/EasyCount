package com.tencent.trc.plan.physical;

import java.util.HashMap;
import java.util.HashSet;

import com.tencent.trc.plan.logical.OpDesc;

public class TaskWork2Bolt extends TaskWork {

	public TaskWork2Bolt(Integer taskId, HashSet<OpDesc> opDescs,
			HashMap<OpDesc, Integer> opDesc2TaskId) {
		super(taskId, opDescs, opDesc2TaskId);
	}

	private static final long serialVersionUID = 8038036604355044706L;

	@Override
	public String getName() {
		return "BOLT";
	}

}
