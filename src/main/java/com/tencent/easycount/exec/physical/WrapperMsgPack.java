package com.tencent.easycount.exec.physical;

import com.tencent.easycount.util.io.TDMsg1;

public class WrapperMsgPack {
	final int packkey;
	final String streamId;
	long msgId;
	final TDMsg1 tdmsg;
	boolean generated = false;
	byte[] data = null;

	public WrapperMsgPack(final int packkey, final String streamId,
			final long msgId, final int innerPackSize) {
		this.packkey = packkey;
		this.streamId = streamId;
		this.msgId = msgId;
		this.tdmsg = TDMsg1.newTDMsg(innerPackSize, true);
	}

	public byte[] genData() {
		if (!this.generated) {
			this.data = this.tdmsg.buildArray();
			this.generated = true;
		}
		return this.data;
	}

	public void setMsgId(final long msgId) {
		this.msgId = msgId;
	}
}
