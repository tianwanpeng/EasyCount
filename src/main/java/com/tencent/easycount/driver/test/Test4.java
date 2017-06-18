package com.tencent.easycount.driver.test;

import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus.Builder;

public class Test4 {

	public static void main(String[] args) throws Exception {

		HyperLogLogPlus hllp_res = new HyperLogLogPlus(10, 20);
		for (int i = 0; i < 1000000000; i++) {
			hllp_res.offer(i);
			if (i % 1000000 == 0) {
				System.out.println(i);
			}
		}
		System.out.println(hllp_res.getBytes().length + " : "
				+ hllp_res.sizeof() + " : " + hllp_res.cardinality() + " : ");

		// for (int i = 0; i < 1000; i++) {
		// HyperLogLogPlus hllp_res = new HyperLogLogPlus(16, 20);
		//
		// for (int j = 0; j < i * 100 + 100; j++) {
		// hllp_res.offer(j);
		// }
		// System.out.println(hllp_res.getBytes().length + " : "
		// + hllp_res.sizeof() + " : " + hllp_res.cardinality()
		// + " : " + i);
		//
		// }

		// HyperLogLogPlus hllp_res = new HyperLogLogPlus(16, 20);
		// Random r = new Random();
		// while (true) {
		// HyperLogLogPlus.Builder builder = new Builder(16, 24);
		//
		// HyperLogLogPlus hllp1 = builder.build();
		//
		// for (int i = 0; i < 10000; i++) {
		// hllp1.offer(r.nextInt(1000000));
		// }
		//
		// hllp_res.addAll(HyperLogLogPlus.Builder.build(hllp1.getBytes()));
		//
		// System.out.println(hllp_res.sizeof() + " : "
		// + hllp_res.cardinality());
		// Thread.sleep(1000);
		// }

	}

	static void test() throws Exception {

		HyperLogLogPlus hllp_res = new HyperLogLogPlus(16, 24);

		HyperLogLogPlus.Builder builder = new Builder(16, 24);
		HyperLogLogPlus hllp = builder.build();

		for (int i = 0; i < 1000; i++) {
			hllp.offer(i);
		}

		hllp_res.addAll(hllp);

		System.out.println(hllp.getBytes().length + " : " + hllp.cardinality());
		System.out.println(hllp_res.getBytes().length + " : "
				+ hllp_res.cardinality());

		HyperLogLogPlus hllp1 = builder.build();

		for (int i = 0; i < 20000000; i++) {
			hllp1.offer(i);
		}
		System.out.println(hllp1.getBytes().length + " : "
				+ hllp1.cardinality());

		hllp1.addAll(hllp);
		System.out.println(hllp1.getBytes().length + " : "
				+ hllp1.cardinality());

		hllp_res.addAll(hllp1);

		System.out.println(hllp_res.getBytes().length + " : "
				+ hllp_res.cardinality());

	}

}
