package com.tencent.easycount.util.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.xerial.snappy.Snappy;

/**
 *
 * @author steventian
 *
 */
public class TDMsg1 {
	private static final int DEFAULT_CAPACITY = 4096;
	private final int CAPACITY;

	private final static byte[] MAGIC0 = { (byte) 0xf, (byte) 0x0 };
	// with timestamp
	private final static byte[] MAGIC1 = { (byte) 0xf, (byte) 0x1 };
	// with msg cnt 20130619
	private final static byte[] MAGIC2 = { (byte) 0xf, (byte) 0x2 };

	private final boolean addmode;

	static class DataBuffer {
		DataOutputBuffer out;
		int cnt;

		public DataBuffer() {
			this.out = new DataOutputBuffer();
		}

		public void write(final byte[] array, final int position, final int len)
				throws IOException {
			this.cnt++;
			this.out.writeInt(len);
			this.out.write(array, position, len);
		}
	}

	private LinkedHashMap<String, DataBuffer> attr2MsgBuffer;
	private int datalen = 0;
	private int msgcnt = 0;
	private boolean compress;

	private final Version version;
	private long timeoffset = 0;

	public void setTimeoffset(final long offset) {
		this.timeoffset = offset;
	}

	private enum Version {
		vn(-1), v0(0), v1(1), v2(2);

		private static final Map<Integer, Version> intToTypeMap = new HashMap<Integer, Version>();
		static {
			for (final Version type : Version.values()) {
				intToTypeMap.put(type.value, type);
			}
		}

		private final int value;

		private Version(final int value) {
			this.value = value;
		}

		public int intValue() {
			return this.value;
		}

		public static Version of(final int v) {
			if (!intToTypeMap.containsKey(v)) {
				return vn;
			}
			return intToTypeMap.get(v);
		}

	}

	/**
	 * capacity: 4096, compress: true, version: 1
	 *
	 * @return
	 */
	public static TDMsg1 newTDMsg() {
		return newTDMsg(true);
	}

	/**
	 * capacity: 4096, version: 1
	 *
	 * @param compress
	 * @return
	 */
	public static TDMsg1 newTDMsg(final boolean compress) {
		return newTDMsg(DEFAULT_CAPACITY, compress);
	}

	/**
	 * capacity: 4096, compress: true
	 *
	 * @param v
	 * @return
	 */
	public static TDMsg1 newTDMsg(final int v) {
		return newTDMsg(DEFAULT_CAPACITY, true, v);
	}

	/**
	 * capacity: 4096
	 *
	 * @param compress
	 * @param v
	 * @return
	 */
	public static TDMsg1 newTDMsg(final boolean compress, final int v) {
		return newTDMsg(DEFAULT_CAPACITY, compress, v);
	}

	/**
	 * version: 1
	 *
	 * @param capacity
	 * @param compress
	 * @return
	 */
	public static TDMsg1 newTDMsg(final int capacity, final boolean compress) {
		return new TDMsg1(capacity, compress, Version.v1);
	}

	/**
	 *
	 * @param capacity
	 * @param compress
	 * @param v
	 * @return
	 */
	public static TDMsg1 newTDMsg(final int capacity, final boolean compress,
			final int v) {
		return new TDMsg1(capacity, compress, Version.of(v));
	}

	// for create
	private TDMsg1(final int capacity, final boolean compress, final Version v) {
		this.version = v;
		this.addmode = true;
		this.compress = compress;
		this.CAPACITY = capacity;
		this.attr2MsgBuffer = new LinkedHashMap<String, DataBuffer>();
		this.parsedInput = null;
		reset();
	}

	public boolean addMsg(final String attr, final byte[] data) {
		return addMsg(attr, ByteBuffer.wrap(data));
	}

	/**
	 * return false means current msg is big enough, no other data should be
	 * added again, but attention: the input data has already been added, and if
	 * you add another data after return false it can also be added
	 * successfully.
	 *
	 * @param attr
	 * @param data
	 * @param offset
	 * @param len
	 * @return
	 */
	public boolean addMsg(final String attr, final byte[] data,
			final int offset, final int len) {
		return addMsg(attr, ByteBuffer.wrap(data, offset, len));
	}

	public boolean addMsg(final String attr, final ByteBuffer data) {
		checkMode(true);
		DataBuffer outputBuffer = this.attr2MsgBuffer.get(attr);
		if (outputBuffer == null) {
			outputBuffer = new DataBuffer();
			this.attr2MsgBuffer.put(attr, outputBuffer);
			// attrlen + utflen + meglen + compress
			this.datalen += attr.length() + 2 + 4 + 1;
		}
		final int len = data.remaining();
		try {
			outputBuffer.write(data.array(), data.position(), len);
			this.datalen += len + 4;
			if (this.version.intValue() >= Version.v2.intValue()) {
				this.datalen += 4;
			}
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
		this.msgcnt++;
		return checkLen(attr, len);
	}

	public boolean addMsgs(final String attr, final ByteBuffer data) {
		boolean res = true;
		final Iterator<ByteBuffer> it = getIteratorBuffer(data);
		while (it.hasNext()) {
			res = this.addMsg(attr, it.next());
		}
		return res;
	}

	private boolean checkLen(final String attr, final int len) {
		return this.datalen < this.CAPACITY;
	}

	public boolean isfull() {
		checkMode(true);
		if (this.datalen >= this.CAPACITY) {
			return true;
		}
		return false;
	}

	public ByteBuffer build() {
		return build(System.currentTimeMillis() + this.timeoffset);
	}

	public ByteBuffer build(final long createtime) {
		checkMode(true);
		try {
			this.createtime = createtime;
			final DataOutputBuffer out = new DataOutputBuffer(this.CAPACITY);

			writeHeader(out);
			out.writeInt(this.attr2MsgBuffer.size());

			if (this.compress) {
				for (final String attr : this.attr2MsgBuffer.keySet()) {
					out.writeUTF(attr);
					final DataBuffer data = this.attr2MsgBuffer.get(attr);
					if (this.version.intValue() >= Version.v2.intValue()) {
						out.writeInt(data.cnt);
					}
					final int guessLen = Snappy.maxCompressedLength(data.out
							.getLength());
					final byte[] tmpData = new byte[guessLen];
					final int len = Snappy.compress(data.out.getData(), 0,
							data.out.getLength(), tmpData, 0);
					out.writeInt(len + 1);
					out.writeBoolean(this.compress);
					out.write(tmpData, 0, len);
				}
			} else {
				for (final String attr : this.attr2MsgBuffer.keySet()) {
					out.writeUTF(attr);
					final DataBuffer data = this.attr2MsgBuffer.get(attr);
					if (this.version.intValue() >= Version.v2.intValue()) {
						out.writeInt(data.cnt);
					}
					out.writeInt(data.out.getLength() + 1);
					out.writeBoolean(this.compress);
					out.write(data.out.getData(), 0, data.out.getLength());
				}
			}
			writeMagic(out);
			out.close();
			return ByteBuffer.wrap(out.getData(), 0, out.getLength());
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void writeHeader(final DataOutputBuffer out) throws IOException {
		writeMagic(out);
		if (this.version.intValue() >= Version.v1.intValue()) {
			// createtime = System.currentTimeMillis() + timeoffset;
			out.writeLong(this.createtime);
		}
		if (this.version.intValue() >= Version.v2.intValue()) {
			out.writeInt(this.getMsgCnt());
		}
	}

	private void writeMagic(final DataOutputBuffer out) throws IOException {
		if (this.version == Version.v1) {
			out.write(MAGIC1[0]);
			out.write(MAGIC1[1]);
		} else if (this.version == Version.v2) {
			out.write(MAGIC2[0]);
			out.write(MAGIC2[1]);
		} else {
			throw new IOException("wrong version : " + this.version.intValue());
		}
	}

	public byte[] buildArray() {
		return buildArray(System.currentTimeMillis() + this.timeoffset);
	}

	public byte[] buildArray(final long createtime) {
		final ByteBuffer buffer = this.build(createtime);
		if (buffer == null) {
			return null;
		}
		final byte[] res = new byte[buffer.remaining()];
		System.arraycopy(buffer.array(), buffer.position(), res, 0, res.length);
		return res;
	}

	public void reset() {
		checkMode(true);
		this.attr2MsgBuffer.clear();
		this.datalen = getHeaderLen();
		this.msgcnt = 0;
	}

	private int getHeaderLen() {
		int len = 4;// magic
		if (this.version.intValue() >= Version.v1.intValue()) {
			len += 8;// create time
		}
		if (this.version.intValue() >= Version.v2.intValue()) {
			len += 4;// msgcnt
		}

		return len + 4;// attrcnt
	}

	// for both mode
	public int getMsgCnt() {
		return this.msgcnt;
	}

	public int getMsgCnt(final String attr) {
		if (this.addmode) {
			return this.attr2MsgBuffer.get(attr).cnt;
		} else {
			return this.attr2Rawdata.get(attr).cnt;
		}
	}

	private void checkMode(final boolean add) {
		if (this.addmode != add) {
			throw new RuntimeException(
					this.addmode ? "illegal operation in add mode !!!"
							: "illegal operation in parse mode !!!");
		}
	}

	private int attrcnt = -1;

	// private LinkedHashMap<String, ByteBuffer> attr2Rawdata = null;
	static class DataByteBuffer {
		final ByteBuffer buffer;
		final int cnt;

		public DataByteBuffer(final int cnt, final ByteBuffer buffer) {
			this.cnt = cnt;
			this.buffer = buffer;
		}
	}

	private LinkedHashMap<String, DataByteBuffer> attr2Rawdata = null;

	// not used right now
	// private LinkedHashMap<String, Integer> attr2index = null;
	private long createtime = -1;
	private boolean parsed = false;
	final private DataInputBuffer parsedInput;

	// for parsed
	private TDMsg1(final ByteBuffer buffer, final Version magic)
			throws IOException {
		this.version = magic;
		this.addmode = false;
		this.CAPACITY = 0;
		this.parsedInput = new DataInputBuffer();
		this.parsedInput.reset(buffer.array(), buffer.position() + 2,
				buffer.remaining());
		if (this.version.intValue() >= Version.v1.intValue()) {
			this.createtime = this.parsedInput.readLong();
		}

		if (this.version.intValue() >= Version.v2.intValue()) {
			this.msgcnt = this.parsedInput.readInt();
		}

		this.attrcnt = this.parsedInput.readInt();
	}

	private void parse() throws IOException {
		if (this.parsed) {
			return;
		}
		this.attr2Rawdata = new LinkedHashMap<String, DataByteBuffer>(
				(this.attrcnt * 10) / 7);
		for (int i = 0; i < this.attrcnt; i++) {
			final String attr = this.parsedInput.readUTF();
			int cnt = 0;
			if (this.version.intValue() >= Version.v2.intValue()) {
				cnt = this.parsedInput.readInt();
			}
			final int len = this.parsedInput.readInt();
			final int pos = this.parsedInput.getPosition();
			this.attr2Rawdata.put(
					attr,
					new DataByteBuffer(cnt, ByteBuffer.wrap(
							this.parsedInput.getData(), pos, len)));
			this.parsedInput.skip(len);
		}
		this.parsed = true;
	}

	private static Version getMagic(final ByteBuffer buffer) {
		final byte[] array = buffer.array();
		if (buffer.remaining() < 4) {
			return Version.vn;
		}
		final int pos = buffer.position();
		final int rem = buffer.remaining();
		if ((array[pos] == MAGIC2[0]) && (array[pos + 1] == MAGIC2[1])
				&& (array[(pos + rem) - 2] == MAGIC2[0])
				&& (array[(pos + rem) - 1] == MAGIC2[1])) {
			return Version.v2;
		}
		if ((array[pos] == MAGIC1[0]) && (array[pos + 1] == MAGIC1[1])
				&& (array[(pos + rem) - 2] == MAGIC1[0])
				&& (array[(pos + rem) - 1] == MAGIC1[1])) {
			return Version.v1;
		}
		if ((array[pos] == MAGIC0[0]) && (array[pos + 1] == MAGIC0[1])
				&& (array[(pos + rem) - 2] == MAGIC0[0])
				&& (array[(pos + rem) - 1] == MAGIC0[1])) {
			return Version.v0;
		}
		return Version.vn;
	}

	public static TDMsg1 parseFrom(final byte[] data) {
		return parseFrom(ByteBuffer.wrap(data));
	}

	public static TDMsg1 parseFrom(final ByteBuffer buffer) {
		final Version magic = getMagic(buffer);
		if (magic == Version.vn) {
			return null;
		}

		try {
			return new TDMsg1(buffer, magic);
		} catch (final IOException e) {
			return null;
		}
	}

	private void makeSureParsed() {
		if (!this.parsed) {
			try {
				parse();
			} catch (final IOException e) {
			}
		}
	}

	public Set<String> getAttrs() {
		checkMode(false);
		makeSureParsed();
		return this.attr2Rawdata.keySet();
	}

	public byte[] getRawData(final String attr) {
		checkMode(false);
		makeSureParsed();
		final ByteBuffer buffer = getRawDataBuffer(attr);
		final byte[] data = new byte[buffer.remaining()];
		System.arraycopy(buffer.array(), buffer.position(), data, 0,
				buffer.remaining());
		return data;
	}

	public ByteBuffer getRawDataBuffer(final String attr) {
		checkMode(false);
		makeSureParsed();
		return this.attr2Rawdata.get(attr).buffer;
	}

	public Iterator<byte[]> getIterator(final String attr) {
		checkMode(false);
		makeSureParsed();
		return getIterator(this.attr2Rawdata.get(attr).buffer);
	}

	public Iterator<ByteBuffer> getIteratorBuffer(final String attr) {
		checkMode(false);
		makeSureParsed();
		return getIteratorBuffer(this.attr2Rawdata.get(attr).buffer);
	}

	public static Iterator<byte[]> getIterator(final byte[] rawdata) {
		return getIterator(ByteBuffer.wrap(rawdata));
	}

	public static Iterator<byte[]> getIterator(final ByteBuffer rawdata) {
		try {
			@SuppressWarnings("resource")
			final DataInputBuffer input = new DataInputBuffer();
			final byte[] array = rawdata.array();
			final int pos = rawdata.position();
			final int rem = rawdata.remaining() - 1;
			final int compress = array[pos];

			if (compress == 1) {
				final byte[] uncompressdata = new byte[Snappy
						.uncompressedLength(array, pos + 1, rem)];
				final int len = Snappy.uncompress(array, pos + 1, rem,
						uncompressdata, 0);
				input.reset(uncompressdata, len);
			} else {
				input.reset(array, pos + 1, rem);
			}

			return new Iterator<byte[]>() {

				@Override
				public boolean hasNext() {
					try {
						return input.available() > 0;
					} catch (final IOException e) {
						e.printStackTrace();
					}
					return false;
				}

				@Override
				public byte[] next() {
					try {
						int len;
						len = input.readInt();
						final byte[] res = new byte[len];
						input.read(res);
						return res;
					} catch (final IOException e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				public void remove() {
					this.next();
				}
			};
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static Iterator<ByteBuffer> getIteratorBuffer(final byte[] rawdata) {
		return getIteratorBuffer(ByteBuffer.wrap(rawdata));
	}

	public static Iterator<ByteBuffer> getIteratorBuffer(
			final ByteBuffer rawdata) {

		try {
			@SuppressWarnings("resource")
			final DataInputBuffer input = new DataInputBuffer();
			final byte[] array = rawdata.array();
			final int pos = rawdata.position();
			final int rem = rawdata.remaining() - 1;
			final int compress = array[pos];

			if (compress == 1) {
				final byte[] uncompressdata = new byte[Snappy
						.uncompressedLength(array, pos + 1, rem)];
				final int len = Snappy.uncompress(array, pos + 1, rem,
						uncompressdata, 0);
				input.reset(uncompressdata, len);
			} else {
				input.reset(array, pos + 1, rem);
			}

			final byte[] uncompressdata = input.getData();

			return new Iterator<ByteBuffer>() {

				@Override
				public boolean hasNext() {
					try {
						return input.available() > 0;
					} catch (final IOException e) {
						e.printStackTrace();
					}
					return false;
				}

				@Override
				public ByteBuffer next() {
					try {
						final int len = input.readInt();
						final int pos = input.getPosition();
						input.skip(len);
						return ByteBuffer.wrap(uncompressdata, pos, len);
					} catch (final IOException e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				public void remove() {
					this.next();
				}
			};
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	public long getCreatetime() {
		return this.createtime;
	}

	public int getAttrCount() {
		checkMode(false);
		return this.attrcnt;
	}

}
