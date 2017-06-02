package com.tencent.easycount.exec.physical;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.tencent.easycount.util.io.DataInputBuffer;
import com.tencent.easycount.util.io.DataOutputBuffer;

public class WrapperMsg {
	int taskId;
	int opTagIdx;
	int targetTaskId;
	int targetOpTagIdx;
	int key;
	byte[] data;

	// long msgId;

	public WrapperMsg() {
	}

	public WrapperMsg(final int taskId, final int opTagIdx,
			final int targetTaskId, final int targetOpTagIdx, final int key,
			final byte[] data) {
		this.taskId = taskId;
		this.opTagIdx = opTagIdx;
		this.targetTaskId = targetTaskId;
		this.targetOpTagIdx = targetOpTagIdx;
		this.key = key;
		this.data = data;
	}

	// public void setMsgId(long msgId) {
	// this.msgId = msgId;
	// }

	public String getStreamId() {
		return this.taskId + "-" + this.targetTaskId;
	}

	public ByteBuffer wrap() {
		final DataOutputBuffer output = serialize();
		return ByteBuffer.wrap(output.getData(), 0, output.getLength());
	}

	public byte[] wrapArray() {
		final DataOutputBuffer output = serialize();
		return output.getData();
	}

	private DataOutputBuffer serialize() {
		final int size = 28 + this.data.length;
		final DataOutputBuffer output = new DataOutputBuffer(size);
		try {
			output.writeInt(0xff);
			output.writeInt(this.taskId);
			output.writeInt(this.opTagIdx);
			output.writeInt(this.targetTaskId);
			output.writeInt(this.targetOpTagIdx);
			output.writeInt(this.key);
			output.writeInt(this.data.length);
			output.write(this.data);
			// output.writeLong(msgId);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	public WrapperMsg unwrap(final ByteBuffer buffer) {
		final DataInputBuffer input = new DataInputBuffer();
		input.reset(buffer.array(), buffer.position(), buffer.limit());
		try {
			final int magic = input.readInt();
			if (magic != 0xff) {
				return null;
			}
			this.taskId = input.readInt();
			this.opTagIdx = input.readInt();
			this.targetTaskId = input.readInt();
			this.targetOpTagIdx = input.readInt();
			this.key = input.readInt();
			final int datalen = input.readInt();
			this.data = new byte[datalen];
			input.readFully(this.data);
			// this.msgId = input.readLong();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public WrapperMsg unwrap(final byte[] bdata) {
		return unwrap(ByteBuffer.wrap(bdata));
	}
}
