package com.tencent.easycount.exec.physical;

public class WrapperMsgPack {
	final int packkey;
	final String streamId;
	long msgId;
	final TDMsg1 tdmsg;
	boolean generated = false;
	byte[] data = null;

	public WrapperMsgPack(int packkey, String streamId, long msgId,
			int innerPackSize) {
		this.packkey = packkey;
		this.streamId = streamId;
		this.msgId = msgId;
		this.tdmsg = TDMsg1.newTDMsg(innerPackSize, true);
	}

	public byte[] genData() {
		if (!generated) {
			data = tdmsg.buildArray();
			generated = true;
		}
		return data;
	}

	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
}
