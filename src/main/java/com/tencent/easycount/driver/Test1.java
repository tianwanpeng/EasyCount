package com.tencent.easycount.driver;


public class Test1 {
	public static void main(String[] args) {
		System.out.println(BloomFilter.optimalM(10000000, 0.001) / 8 / 1000000d
				+ "M");
		BloomFilter<String> bf = new BloomFilter<String>(10000000, 0.001);
		System.out.println(bf.toString());
		System.out.println(bf.getBitSet().size() / 8l);

		for (int i = 0; i < 10000000; i++) {
			bf.add(String.valueOf(i).getBytes());
		}
		long time = System.currentTimeMillis();
		System.out.println(bf.valueSize());
		System.out.println(System.currentTimeMillis() - time);
	}
}