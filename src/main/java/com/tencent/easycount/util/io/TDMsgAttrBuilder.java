package com.tencent.easycount.util.io;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author steventian
 *
 */

public class TDMsgAttrBuilder {

	public static enum PartitionUnit {
		DAY("d"), HOUR("h"), HALFHOUR("n"), QUARTER("q"), TENMINS("t"), FIVEMINS(
				"f");
		private static final Map<String, PartitionUnit> stringToTypeMap = new HashMap<String, PartitionUnit>();
		static {
			for (final PartitionUnit type : PartitionUnit.values()) {
				stringToTypeMap.put(type.value, type);
			}
		}

		private final String value;

		private PartitionUnit(final String value) {
			this.value = value;
		}

		public static PartitionUnit of(final String p) {
			final PartitionUnit type = stringToTypeMap.get(p);
			if (type == null) {
				return PartitionUnit.HOUR;
			}
			return type;
		}

		@Override
		public String toString() {
			return this.value;
		}

	}

	public static enum TimeType {
		MS("#ms"), S("#s"), STANDARD("#")/* yyyy-MM-dd HH:mm:ss */, NORMAL("#n")/* yyyyMMddHH */;
		private static final Map<String, TimeType> stringToTypeMap = new HashMap<String, TimeType>();
		static {
			for (final TimeType type : TimeType.values()) {
				stringToTypeMap.put(type.value, type);
			}
		}

		private final String value;

		private TimeType(final String value) {
			this.value = value;
		}

		public static TimeType of(final String tt) {
			final TimeType type = stringToTypeMap.get(tt);
			if (type == null) {
				return TimeType.STANDARD;
			}
			return type;
		}

		@Override
		public String toString() {
			return this.value;
		}

	}

	public static class MsgAttrProtocol_m0 {
		final private StringBuffer attrBuffer;
		private String id = null;
		private String t = null;
		private TimeType tt = TimeType.NORMAL;
		private PartitionUnit p = PartitionUnit.HOUR;

		public MsgAttrProtocol_m0() {
			this.attrBuffer = new StringBuffer();
			this.attrBuffer.append("m=0");
		}

		public MsgAttrProtocol_m0 setId(final String id) {
			this.id = id;
			return this;
		}

		public MsgAttrProtocol_m0 setSpliter(final String s) {
			this.attrBuffer.append("&s=").append(s);
			return this;
		}

		public MsgAttrProtocol_m0 setTime(final String t) {
			this.t = t;
			return this;
		}

		public MsgAttrProtocol_m0 setTime(final long t) {
			return this.setTime(String.valueOf(t));
		}

		public MsgAttrProtocol_m0 setTimeType(final String tt) {
			this.tt = TimeType.of(tt);
			return this;
		}

		public MsgAttrProtocol_m0 setTimeType(final TimeType tt) {
			this.tt = tt;
			return this;
		}

		public MsgAttrProtocol_m0 setPartitionUnit(final String p) {
			this.p = PartitionUnit.of(p);
			return this;
		}

		public MsgAttrProtocol_m0 setPartitionUnit(final PartitionUnit p) {
			this.p = p;
			return this;
		}

		public String buildAttr() throws Exception {
			if (this.id == null) {
				throw new Exception("id is null");
			}

			if (this.t == null) {
				throw new Exception("t is null");
			}

			this.attrBuffer.append("&iname=").append(this.id);

			Date d = null;
			if (this.tt == TimeType.MS) {
				d = new Date(Long.valueOf(this.t));
			} else if (this.tt == TimeType.S) {
				d = new Date(Long.valueOf(this.t) * 1000);
			} else if (this.tt == TimeType.STANDARD) {
				final SimpleDateFormat f = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				d = f.parse(this.t);
			} else if (this.tt == TimeType.NORMAL) {
				final SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHH");
				d = f.parse(this.t);
			}

			String tstr = null;
			if (this.p == PartitionUnit.DAY) {
				final SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
				tstr = f.format(d);
			} else if (this.p == PartitionUnit.HOUR) {
				final SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHH");
				tstr = f.format(d);
			} else if (this.p == PartitionUnit.QUARTER) {
				final int idx = (int) ((d.getTime() % (60l * 60 * 1000)) / (15l * 60 * 1000));
				final SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHH");
				tstr = f.format(d) + "q" + idx;
			}

			return this.attrBuffer.append("&t=").append(tstr).toString();
		}
	}

	public static class MsgAttrProtocol_m100 {

		final private StringBuffer attrBuffer;
		private String id = null;
		private String t = null;

		private int idp = -1;
		private int tp = -1;
		private TimeType tt = null;
		private PartitionUnit p = null;

		public MsgAttrProtocol_m100() {
			this.attrBuffer = new StringBuffer();
			this.attrBuffer.append("m=100");
		}

		public MsgAttrProtocol_m100 setSpliter(final String s) {
			this.attrBuffer.append("&s=").append(s);
			return this;
		}

		public MsgAttrProtocol_m100 setId(final String id) {
			this.id = id;
			return this;
		}

		public MsgAttrProtocol_m100 setTime(final String t) {
			this.t = t;
			return this;
		}

		public MsgAttrProtocol_m100 setTime(final long t) {
			return this.setTime(String.valueOf(t));
		}

		public MsgAttrProtocol_m100 setIdPos(final int idp) {
			this.idp = idp;
			return this;
		}

		public MsgAttrProtocol_m100 setTimePos(final int tp) {
			this.tp = tp;
			return this;
		}

		public MsgAttrProtocol_m100 setTimeType(final String tt) {
			return this.setTimeType(TimeType.of(tt));
		}

		public MsgAttrProtocol_m100 setTimeType(final TimeType tt) {
			this.tt = tt;
			return this;
		}

		public MsgAttrProtocol_m100 setPartitionUnit(final String p) {
			return this.setPartitionUnit(PartitionUnit.of(p));
		}

		public MsgAttrProtocol_m100 setPartitionUnit(final PartitionUnit p) {
			this.p = p;
			return this;
		}

		public String buildAttr() throws Exception {
			if (this.id != null) {
				this.attrBuffer.append("&iname=").append(this.id);
			} else if (this.idp >= 0) {
				this.attrBuffer.append("&idp=").append(this.idp);
			}
			if (this.t != null) {
				String tstr = null;
				if ((this.tt != null) && (this.tt == TimeType.NORMAL)) {
					if (makeSureTimeNormal(this.t)) {
						tstr = this.t;
					}
				} else {
					if (this.p == null) {
						this.p = PartitionUnit.HOUR;
					}
					Date d = null;
					if ((this.tt == null) || (this.tt == TimeType.MS)) {
						d = new Date(Long.valueOf(this.t));
					} else if (this.tt == TimeType.S) {
						d = new Date(Long.valueOf(this.t) * 1000);
					} else if (this.tt == TimeType.STANDARD) {
						final SimpleDateFormat f = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						d = f.parse(this.t);
					} else if (this.tt == TimeType.NORMAL) {
						final SimpleDateFormat f = new SimpleDateFormat(
								"yyyyMMddHH");
						d = f.parse(this.t);
					}

					if (this.p == PartitionUnit.DAY) {
						final SimpleDateFormat f = new SimpleDateFormat(
								"yyyyMMdd");
						tstr = f.format(d);
					} else if (this.p == PartitionUnit.HOUR) {
						final SimpleDateFormat f = new SimpleDateFormat(
								"yyyyMMddHH");
						tstr = f.format(d);
					} else if (this.p == PartitionUnit.HALFHOUR) {
						final int idx = (int) ((d.getTime() % (60l * 60 * 1000)) / (30l * 60 * 1000));
						final SimpleDateFormat f = new SimpleDateFormat(
								"yyyyMMddHH");
						tstr = f.format(d) + "n" + idx;
					} else if (this.p == PartitionUnit.QUARTER) {
						final int idx = (int) ((d.getTime() % (60l * 60 * 1000)) / (15l * 60 * 1000));
						final SimpleDateFormat f = new SimpleDateFormat(
								"yyyyMMddHH");
						tstr = f.format(d) + "q" + idx;
					} else if (this.p == PartitionUnit.TENMINS) {
						final int idx = (int) ((d.getTime() % (60l * 60 * 1000)) / (10l * 60 * 1000));
						final SimpleDateFormat f = new SimpleDateFormat(
								"yyyyMMddHH");
						tstr = f.format(d) + "t" + idx;
					} else if (this.p == PartitionUnit.FIVEMINS) {
						final int idx = (int) ((d.getTime() % (60l * 60 * 1000)) / (5l * 60 * 1000));
						final SimpleDateFormat f = new SimpleDateFormat(
								"yyyyMMddHH");
						tstr = f.format(d) + "f" + idx;
					}
				}

				if (tstr != null) {
					this.attrBuffer.append("&t=").append(tstr);
				}

			} else if (this.tp >= 0) {
				this.attrBuffer.append("&tp=").append(this.tp);
				if (this.tt != null) {
					this.attrBuffer.append("&tt=").append(this.tt);
				}
				if (this.p != null) {
					this.attrBuffer.append("&p=").append(this.p.toString());
				}
			}
			return this.attrBuffer.toString();
		}

		private boolean makeSureTimeNormal(final String time) {
			final int len = time.length();
			return (len == 8) || (len == 10)
					|| ((len == 12) && (time.charAt(10) == 'p'));
		}

	}

	public static MsgAttrProtocol_m0 getProtolol_m0() {
		return new MsgAttrProtocol_m0();
	}

	public static MsgAttrProtocol_m100 getProtolol_m100() {
		return new MsgAttrProtocol_m100();
	}

	public static void main(final String[] args) throws Exception {

		final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final SimpleDateFormat f1 = new SimpleDateFormat("yyyyMMddHH");

		System.out.println(TDMsgAttrBuilder.getProtolol_m0()
				.setId("interfaceid").setTimeType(TimeType.S)
				.setTime(String.valueOf(System.currentTimeMillis() / 1000))
				.buildAttr());
		System.out.println(TDMsgAttrBuilder.getProtolol_m0()
				.setId("interfaceid").setTime(System.currentTimeMillis())
				.setTimeType(TimeType.MS).buildAttr());
		System.out.println(TDMsgAttrBuilder.getProtolol_m0()
				.setId("interfaceid")
				.setTime(f1.format(new Date(System.currentTimeMillis())))
				.buildAttr());
		System.out.println(TDMsgAttrBuilder.getProtolol_m0()
				.setId("interfaceid")
				.setTime(f.format(new Date(System.currentTimeMillis())))
				.setTimeType(TimeType.STANDARD).buildAttr());
		System.out.println(TDMsgAttrBuilder.getProtolol_m0()
				.setId("interfaceid")
				.setTime(f1.format(new Date(System.currentTimeMillis())))
				.setTimeType(TimeType.NORMAL).buildAttr());
		System.out.println(TDMsgAttrBuilder.getProtolol_m0()
				.setId("interfaceid").setTimeType(TimeType.S)
				.setTime(String.valueOf(System.currentTimeMillis() / 1000))
				.setPartitionUnit(PartitionUnit.DAY).buildAttr());
		System.out.println(TDMsgAttrBuilder.getProtolol_m0()
				.setId("interfaceid").setTimeType(TimeType.S)
				.setTime(String.valueOf(System.currentTimeMillis() / 1000))
				.setPartitionUnit(PartitionUnit.HOUR).buildAttr());
		System.out.println(TDMsgAttrBuilder.getProtolol_m0()
				.setId("interfaceid").setTimeType(TimeType.S)
				.setTime(String.valueOf(System.currentTimeMillis() / 1000))
				.setPartitionUnit(PartitionUnit.QUARTER).buildAttr());
		System.out.println();

		System.out.print(TDMsgAttrBuilder.getProtolol_m100().buildAttr());
		System.out
		.println("\t\t\t\t\t\t// ---- all the param is default : s=\\t, idp=0, tp=1, tt=#ms, p=h ");

		System.out.print(TDMsgAttrBuilder.getProtolol_m100().setSpliter(",")
				.buildAttr());
		System.out.println("\t\t\t\t\t// ---- : idp=0, tp=1, tt=#ms, p=h ");

		System.out.print(TDMsgAttrBuilder.getProtolol_m100().setSpliter(",")
				.setIdPos(0).buildAttr());
		System.out.println("\t\t\t\t\t// ---- : tp=1, tt=#ms, p=h ");

		System.out.print(TDMsgAttrBuilder.getProtolol_m100().setSpliter(",")
				.setTimePos(1).buildAttr());
		System.out.println("\t\t\t\t\t// ---- : idp=0, tt=#ms, p=h ");

		System.out.print(TDMsgAttrBuilder.getProtolol_m100().setSpliter(",")
				.setIdPos(0).setTimePos(1).buildAttr());
		System.out.println("\t\t\t\t// ---- : tt=#ms, p=h ");

		System.out.print(TDMsgAttrBuilder.getProtolol_m100().setSpliter(",")
				.setIdPos(0).setTimePos(1).setTimeType(TimeType.S).buildAttr());
		System.out.println("\t\t\t// ---- : p=h ");

		System.out.print(TDMsgAttrBuilder.getProtolol_m100().setSpliter(",")
				.setIdPos(0).setTimePos(1)
				.setPartitionUnit(PartitionUnit.QUARTER).buildAttr());
		System.out.println("\t\t\t// ---- : tt=#s ");

		System.out.print(TDMsgAttrBuilder.getProtolol_m100().setSpliter(",")
				.setIdPos(0).setTimePos(1).setTimeType(TimeType.MS)
				.setPartitionUnit(PartitionUnit.QUARTER).buildAttr());
		System.out.println("\t\t\t// ---- : all ");

		System.out.print(TDMsgAttrBuilder.getProtolol_m100()
				.setId("interfaceid").setSpliter(",").setIdPos(0).setTimePos(1)
				.setTimeType(TimeType.MS)
				.setPartitionUnit(PartitionUnit.QUARTER).buildAttr());
		System.out.println("\t// ---- : id is set so idp is ignored ");

		System.out.print(TDMsgAttrBuilder.getProtolol_m100().setSpliter(",")
				.setIdPos(0).setTimePos(1).setTime(System.currentTimeMillis())
				.setTimeType(TimeType.MS)
				.setPartitionUnit(PartitionUnit.QUARTER).buildAttr());
		System.out.println("\t\t\t// ---- : t is set so tp is ignored ");

		System.out.print(TDMsgAttrBuilder.getProtolol_m100()
				.setId("interfaceid").setSpliter(",").setIdPos(0).setTimePos(1)
				.setTime(System.currentTimeMillis()).setTimeType(TimeType.MS)
				.setPartitionUnit(PartitionUnit.QUARTER).buildAttr());
		System.out
		.println("\t\t// ---- : id and t are all set so idpos and tp are all ignored ");

		System.out.print(TDMsgAttrBuilder.getProtolol_m100()
				.setId("interfaceid").setSpliter(",").setIdPos(0).setTimePos(1)
				.setTime(f1.format(new Date(System.currentTimeMillis())))
				.setTimeType(TimeType.NORMAL)
				.setPartitionUnit(PartitionUnit.QUARTER).buildAttr());
		System.out.println("\t\t// ---- : TimeType.NORMAL ");

		System.out
		.println("\nAttention !!!! m=0 is contained by m=100, so just use m=100");

		// long time = System.currentTimeMillis();
		// byte[] data0 = ("id," + time + ",other,data").getBytes();
		// String attr = TDMsgProtocolFactory.getProtolol_m100().setSpliter(",")
		// .setIdPos(0).setTimePos(1).setTimeType(TimeType.MS)
		// .setPartitionUnit(PartitionUnit.QUARTER).buildAttr();
		// TDMsg1 tdmsg = TDMsg1.newTDMsg();
		// tdmsg.addMsg(attr, data0);
		// byte[] result = tdmsg.buildArray();
		// System.out.println(new String(result));
		// attr = TDMsgProtocolFactory.getProtolol_m0().setSpliter(",")
		// .setTime(System.currentTimeMillis()).setTimeType(TimeType.MS)
		// .setPartitionUnit(PartitionUnit.QUARTER).buildAttr();
		// tdmsg = TDMsg1.newTDMsg();
		// tdmsg.addMsg(attr, data0);
		// result = tdmsg.buildArray();
		// System.out.println(new String(result));
		System.out.print(TDMsgAttrBuilder.getProtolol_m100().setSpliter(",")
				.setIdPos(0).setTimePos(1)

				.setTimeType(TimeType.STANDARD)
				.setPartitionUnit(PartitionUnit.QUARTER).buildAttr());

	}
}
