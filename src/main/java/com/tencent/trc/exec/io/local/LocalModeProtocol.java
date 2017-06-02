package com.tencent.trc.exec.io.local;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.ipc.VersionedProtocol;

public interface LocalModeProtocol extends VersionedProtocol {
	public static final long versionID = 3L;

	public boolean sendMsg(Writable data);

	public Writable nextMsg();

	public boolean putMsg(String key, Writable data);

	public Writable getMsg(String key);
}
