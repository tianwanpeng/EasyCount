package com.tencent.easycount.util.map;

import java.util.ArrayList;
import java.util.HashMap;

public class AggrsFactory {

	public static class AggrKey {
		final private ArrayList<String> gbkvs;
		final private long time;
		final private static ArrayList<String> nullobj = new ArrayList<String>();
		static {
			nullobj.add("null");
		}
		private long lastcommittime;

		public AggrKey(ArrayList<String> gbkvs, long time) {
			this.gbkvs = gbkvs;
			this.time = time;
			this.setLastcommittime(System.currentTimeMillis());
		}

		public ArrayList<String> getGbks() {
			if (gbkvs.size() == 0) {
				return nullobj;
			}
			return gbkvs;
		}

		public long getTime() {
			return time;
		}

		public long getLastcommittime() {
			return lastcommittime;
		}

		public void setLastcommittime(long lastcommittime) {
			this.lastcommittime = lastcommittime;
		}

		@Override
		public int hashCode() {
			int hc = 1;
			for (String s : gbkvs) {
				hc = (hc * 31) ^ s.hashCode();
			}
			hc = (int) ((hc * 31) ^ (time & 0x0ff));
			hc = (int) ((hc * 31) ^ ((time >> 32) & 0x0ff));
			return hc;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AggrKey)) {
				return false;
			}
			AggrKey ak = (AggrKey) obj;
			if (ak.gbkvs.equals(this.gbkvs) && ak.time == this.time) {
				return true;
			}
			return false;
		}

		public String printStr() {
			return "{ " + gbkvs.toString() + ", " + time + ", "
					+ lastcommittime + " }";
		}
	}

	public static class AggrValue {
		final private String aggrv;

		public AggrValue(String aggrv) {
			this.aggrv = aggrv;
		}

		public String getAggrvs() {
			return aggrv;
		}
	}

	public static abstract class AggrResult {
		private long lasttime = System.currentTimeMillis();

		public abstract String value();

		protected void updateLastTime() {
			lasttime = System.currentTimeMillis();
		}

		public long lastUpdateTime() {
			return lasttime;
		}
	}

	public static class AggrResultLong extends AggrResult {
		private long value;

		public long getValue() {
			return value;
		}

		public AggrResultLong(long v) {
			value = v;
		}

		@Override
		public String value() {
			return String.valueOf(value);
		}

	}

	public static class AggrResultAvg extends AggrResult {
		private long sum;
		private long cnt;

		public AggrResultAvg(long sum, long cnt) {
			this.sum = sum;
			this.cnt = cnt;
		}

		@Override
		public String value() {
			return String.valueOf(cnt == 0 ? 0 : sum / cnt);
		}

	}

	public static UpdaterMap1<AggrKey, AggrValue, AggrResult> getAggrProcessor(
			String funcname) {
		return new UpdaterMap1<AggrKey, AggrValue, AggrResult>(
				getUpdater(funcname));
	}

	public static UpdaterMap1<AggrKey, ArrayList<AggrValue>, ArrayList<AggrResult>> getAggrProcessor(
			final ArrayList<String> funcnames) {

		return new UpdaterMap1<AggrKey, ArrayList<AggrValue>, ArrayList<AggrResult>>(
				new Updater1<AggrKey, ArrayList<AggrValue>, ArrayList<AggrResult>>() {

					@Override
					public ArrayList<AggrResult> update(AggrKey k,
							ArrayList<AggrValue> v, ArrayList<AggrResult> finalv) {
						if (finalv == null || funcnames.size() != finalv.size()) {
							ArrayList<AggrResult> resfinalv = new ArrayList<AggrResult>(
									funcnames.size());
							for (int i = 0; i < funcnames.size(); i++) {
								resfinalv.add(getUpdater(funcnames.get(i))
										.update(k, v.get(i), null));
							}
							return resfinalv;
						}
						for (int i = 0; i < funcnames.size(); i++) {
							getUpdater(funcnames.get(i)).update(k, v.get(i),
									finalv.get(i));
						}
						return finalv;
					}

					@Override
					public boolean inplace() {
						return true;
					}
				});

	}

	final static private HashMap<String, Updater1<AggrKey, AggrValue, AggrResult>> func2Updaters = new HashMap<String, Updater1<AggrKey, AggrValue, AggrResult>>();
	static {
		func2Updaters.put("sum",
				new Updater1<AggrKey, AggrValue, AggrResult>() {

					@Override
					public AggrResult update(AggrKey k, AggrValue v,
							AggrResult finalv) {
						long vl = (v == null || v.aggrv == null) ? 0 : Long
								.parseLong(v.aggrv);
						if (finalv == null) {
							return new AggrResultLong(vl);
						}
						((AggrResultLong) finalv).value += vl;
						finalv.updateLastTime();
						return finalv;
					}

					@Override
					public boolean inplace() {
						return true;
					}
				});

		func2Updaters.put("count",
				new Updater1<AggrKey, AggrValue, AggrResult>() {

					@Override
					public AggrResult update(AggrKey k, AggrValue v,
							AggrResult finalv) {
						int num = (v == null || v.aggrv == null) ? 0 : 1;
						if (finalv == null) {
							return new AggrResultLong(num);
						}
						((AggrResultLong) finalv).value += num;
						finalv.updateLastTime();
						return finalv;
					}

					@Override
					public boolean inplace() {
						return true;
					}
				});
		func2Updaters.put("max",
				new Updater1<AggrKey, AggrValue, AggrResult>() {

					@Override
					public AggrResult update(AggrKey k, AggrValue v,
							AggrResult finalv) {
						long vl = (v == null || v.aggrv == null) ? Long.MIN_VALUE
								: Long.parseLong(v.aggrv);
						if (finalv == null) {
							return new AggrResultLong(vl);
						}
						long orivalue = ((AggrResultLong) finalv).value;
						if (vl > orivalue) {
							((AggrResultLong) finalv).value = vl;
						}
						finalv.updateLastTime();
						return finalv;
					}

					@Override
					public boolean inplace() {
						return true;
					}
				});
		func2Updaters.put("min",
				new Updater1<AggrKey, AggrValue, AggrResult>() {

					@Override
					public AggrResult update(AggrKey k, AggrValue v,
							AggrResult finalv) {
						long vl = (v == null || v.aggrv == null) ? Long.MAX_VALUE
								: Long.parseLong(v.aggrv);
						if (finalv == null) {
							return new AggrResultLong(vl);
						}
						long orivalue = ((AggrResultLong) finalv).value;
						if (vl < orivalue) {
							((AggrResultLong) finalv).value = vl;
						}
						finalv.updateLastTime();
						return finalv;
					}

					@Override
					public boolean inplace() {
						return true;
					}
				});
		func2Updaters.put("avg",
				new Updater1<AggrKey, AggrValue, AggrResult>() {

					@Override
					public AggrResult update(AggrKey k, AggrValue v,
							AggrResult finalv) {
						boolean isnull = (v == null || v.aggrv == null);
						int num = isnull ? 0 : 1;
						long vl = isnull ? 0 : Long.parseLong(v.aggrv);
						if (finalv == null) {
							return new AggrResultAvg(vl, num);
						}
						((AggrResultAvg) finalv).sum += vl;
						((AggrResultAvg) finalv).cnt += num;
						finalv.updateLastTime();
						return finalv;
					}

					@Override
					public boolean inplace() {
						return true;
					}
				});
	}

	private static Updater1<AggrKey, AggrValue, AggrResult> getUpdater(
			String funcname) {

		if ("sum".equalsIgnoreCase(funcname)) {
			return func2Updaters.get("sum");
		}

		if ("count".equalsIgnoreCase(funcname)) {
			return func2Updaters.get("count");
		}

		if ("max".equalsIgnoreCase(funcname)) {
			return func2Updaters.get("max");
		}

		if ("min".equalsIgnoreCase(funcname)) {
			return func2Updaters.get("min");
		}
		if ("avg".equalsIgnoreCase(funcname)) {
			return func2Updaters.get("avg");
		}

		return null;
	}

	@SuppressWarnings("serial")
	public static void main(String[] args) {
		ArrayList<String> funcnames = new ArrayList<String>();
		funcnames.add("count");
		funcnames.add("sum");
		UpdaterMap1<AggrKey, ArrayList<AggrValue>, ArrayList<AggrResult>> map_5s = getAggrProcessor(funcnames);
		ArrayList<String> gbks = new ArrayList<String>(13);
		gbks.add("a");
		gbks.add("b");
		gbks.add("c");
		gbks.add("d");

		long ctime = System.currentTimeMillis();
		long time5s = ctime / 5000 * 5000;

		map_5s.add(new AggrKey(gbks, time5s), new ArrayList<AggrValue>() {
			{
				new AggrValue("100");
				new AggrValue("100");
			}
		});
		map_5s.add(new AggrKey(gbks, time5s), new ArrayList<AggrValue>() {
			{
				new AggrValue("200");
				new AggrValue("200");
			}
		});
		map_5s.add(new AggrKey(gbks, time5s), new ArrayList<AggrValue>() {
			{
				new AggrValue("300");
				new AggrValue("300");
			}
		});
		map_5s.add(new AggrKey(gbks, time5s), new ArrayList<AggrValue>() {
			{
				new AggrValue("400");
				new AggrValue("400");
			}
		});
		System.out
				.println(map_5s.get(new AggrKey(gbks, time5s)).get(0).value());
		System.out
				.println(map_5s.get(new AggrKey(gbks, time5s)).get(1).value());
	}
}
