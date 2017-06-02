package com.tencent.easycount.util.constants;

import java.util.HashMap;
import java.util.Map;

public class Constants {

	public static final long TEN_SECONDS = 10000;
	public final static long ADAY = 24 * 60l * 60 * 1000;
	public final static long ANHOUR = 60l * 60 * 1000;
	public final static long HALFHOUR = 30l * 60 * 1000;
	public final static long AQUARTER = 15l * 60 * 1000;
	public final static long TENMINUTES = 10l * 60 * 1000;
	public final static long FIVEMINUTES = 5l * 60 * 1000;
	public final static long ONEMINUTES = 1l * 60 * 1000;
	public final static long TWOMINUTES = 2l * 60 * 1000;
	public final static long THREEMINUTES = 3l * 60 * 1000;
	public final static long AMINUTE = 1l * 60 * 1000;

	public final static long AMILLION = 1000000L;

	public final static String gbyAggrTupleTime = "gbyAggrTupleTime";
	public final static String dataAttrs = "dataAttrs";

	// public final static String dataAttributes = "dataAttributes";

	public static enum TimeMode {
		d(0), h(1), n(2), q(3), t(4), f(5), m(6);

		private static final Map<Integer, TimeMode> intToTypeMap = new HashMap<Integer, TimeMode>();
		static {
			for (TimeMode type : TimeMode.values()) {
				intToTypeMap.put(type.value, type);
			}
		}

		private final int value;

		private TimeMode(int value) {
			this.value = value;
		}

		public static TimeMode of(int i) {
			TimeMode type = intToTypeMap.get(i);
			if (type == null) {
				return TimeMode.h;
			}
			return type;
		}

		public int intValue() {
			return value;
		}

	}

	public static void main(String[] args) {

	}
}
