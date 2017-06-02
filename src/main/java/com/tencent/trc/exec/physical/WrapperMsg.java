package com.tencent.trc.exec.physical;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.tencent.tdbank.msg.DataInputBuffer;
import com.tencent.tdbank.msg.DataOutputBuffer;

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

	public WrapperMsg(int taskId, int opTagIdx, int targetTaskId,
			int targetOpTagIdx, int key, byte[] data) {
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
		return taskId + "-" + targetTaskId;
	}

	public ByteBuffer wrap() {
		DataOutputBuffer output = serialize();
		return ByteBuffer.wrap(output.getData(), 0, output.getLength());
	}

	public byte[] wrapArray() {
		DataOutputBuffer output = serialize();
		return output.getData();
	}

	private DataOutputBuffer serialize() {
		int size = 28 + data.length;
		DataOutputBuffer output = new DataOutputBuffer(size);
		try {
			output.writeInt(0xff);
			output.writeInt(taskId);
			output.writeInt(opTagIdx);
			output.writeInt(targetTaskId);
			output.writeInt(targetOpTagIdx);
			output.writeInt(key);
			output.writeInt(data.length);
			output.write(data);
			// output.writeLong(msgId);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	public WrapperMsg unwrap(ByteBuffer buffer) {
		DataInputBuffer input = new DataInputBuffer();
		input.reset(buffer.array(), buffer.position(), buffer.limit());
		try {
			int magic = input.readInt();
			if (magic != 0xff) {
				return null;
			}
			this.taskId = input.readInt();
			this.opTagIdx = input.readInt();
			this.targetTaskId = input.readInt();
			this.targetOpTagIdx = input.readInt();
			this.key = input.readInt();
			int datalen = input.readInt();
			this.data = new byte[datalen];
			input.readFully(data);
			// this.msgId = input.readLong();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public WrapperMsg unwrap(byte[] bdata) {
		return unwrap(ByteBuffer.wrap(bdata));
	}
}
