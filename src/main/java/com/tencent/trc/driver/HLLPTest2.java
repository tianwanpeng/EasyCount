package com.tencent.trc.driver;

import java.io.IOException;
import java.util.ArrayList;

import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;

public class HLLPTest2 {

	public static void main(String[] args) throws Exception {

		int[] ps = { 8, 10, 12, 14, 16, 18, 20, 22 };
		int[] sps = { 16, 18, 20, 22, 24, 26, 28, 30, 32 };
		int[] num = { 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000,
				10000, 20000, 30000, 40000, 50000, 60000, 70000, 80000, 90000,
				100000, 1000000, 2000000, 3000000, 4000000, 5000000 };
		ArrayList<String> ress = new ArrayList<String>();
		for (int i = 0; i < ps.length; i++) {
			for (int j = 0; j < sps.length; j++) {
				if (ps[i] > sps[j]) {
					continue;
				}
				for (int j2 = 0; j2 < num.length; j2++) {
					ress.add(test(ps[i], sps[j], num[j2]));
				}
			}
		}

		System.out.println(ress);

	}

	static String test(int p, int sp, int num) throws IOException {
		HyperLogLogPlus hllp_res = new HyperLogLogPlus(p, sp);
		for (int i = 1; i < num; i++) {
			hllp_res.offer(i);
			// if (i % 1000000 == 0) {
			// System.out.println(p + " " + sp + " " + num + " " + i);
			// }
		}
		String res = p + "\t" + sp + "\t" + num + "\t" + hllp_res.cardinality()
				+ "\t" + hllp_res.sizeof() + "\t" + hllp_res.getBytes().length;
		System.out.println(res);
		return res;
	}

}
