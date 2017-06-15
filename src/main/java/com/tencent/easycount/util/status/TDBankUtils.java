package com.tencent.easycount.util.status;

public class TDBankUtils {
	public static String getExceptionStack(final Throwable e) {
		e.printStackTrace();
		return e.toString();
	}
}
