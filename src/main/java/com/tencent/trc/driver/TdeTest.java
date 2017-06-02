package com.tencent.trc.driver;

import com.tencent.tde.client.Result;
import com.tencent.tde.client.TairClient.TairOption;
import com.tencent.tde.client.impl.MutiThreadCallbackClient;

public class TdeTest {
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			throw new Exception("args length is 0");
		}
		MutiThreadCallbackClient tdeClient = new MutiThreadCallbackClient();
		tdeClient.setMaster("10.208.146.154:5198");
		tdeClient.setSlave("10.208.146.172:5198");
		tdeClient.setMaxNotifyQueueSize(5000);
		tdeClient.setGroup("comm_gk_tdengine");
		tdeClient.setWorkerThreadCount(10);
		TairOption opt = new TairOption(5000);

		tdeClient.init();
		Result<byte[]> result = tdeClient.get((short) 1001, args[0].getBytes(),
				opt);
		System.out.println(new String(result.getResult()));
		tdeClient.close();
	}
}
