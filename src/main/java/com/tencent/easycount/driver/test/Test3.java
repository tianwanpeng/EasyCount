package com.tencent.easycount.driver.test;

public class Test3 {
	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		long xx = 0;
		for (int i = 0; i < 1000000000; i++) {
			xx += (i << 5) - i;
		}
		long time1 = System.currentTimeMillis();
		long yy = 0;
		for (int i = 0; i < 1000000000; i++) {
			yy += i * 31;
		}
		long time2 = System.currentTimeMillis();
		System.out.println(xx);
		System.out.println(yy);

		System.out.println(time1 - time);
		System.out.println(time2 - time1);

	}
}
