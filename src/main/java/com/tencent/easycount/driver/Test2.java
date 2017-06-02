package com.tencent.easycount.driver;

import java.io.IOException;
import java.util.Random;

public class Test2 {
	public static void main(String[] args) throws IOException {
		Random r = new Random();

		for (int i = 0; i < 100; i++) {
			System.out.println(r.nextLong() % 1000);
		}

	}
}
