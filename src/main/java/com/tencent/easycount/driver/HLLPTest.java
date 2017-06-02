package com.tencent.easycount.driver;

import java.io.IOException;
import java.util.Random;

import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus.Builder;

public class HLLPTest {
	static HyperLogLogPlus.Builder builder = new Builder(5, 5);

	public static void main(String[] args) throws Exception {

		HyperLogLogPlus hllp = builder.build();

		for (int i = 0; i < 10; i++) {
			hllp.addAll(Builder.build(getHllp()));
		}
		System.out.println(hllp.cardinality());

	}

	static byte[] getHllp() throws IOException {
		Random r = new Random();
		HyperLogLogPlus hllp = builder.build();
		for (int i = 0; i < 5; i++) {
			int x = r.nextInt();
			// set.add(x);
			hllp.offer(x);
			if (i % 10000 == 0) {
				// System.out.println(i);
			}
		}
		return hllp.getBytes();
	}
}
