package com.tencent.easycount.driver.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpTest {
	public static void main(String[] args) {
		Pattern p = Pattern.compile("^[0-9]+$");
		Matcher m = p.matcher("0109sd");
		System.out.println(m.find());

	}
}
