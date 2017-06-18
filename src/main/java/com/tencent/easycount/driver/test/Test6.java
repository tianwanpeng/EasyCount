package com.tencent.easycount.driver.test;

public class Test6 {
	public static void main(String[] args) {
		long ip = 2090913977L;
		System.out.println((ip >> 24) & 0xff);
		System.out.println((ip >> 16) & 0xff);
		System.out.println((ip >> 8) & 0xff);
		System.out.println(ip - (ip & 0xff));
	}
}
