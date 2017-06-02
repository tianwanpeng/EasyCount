package com.tencent.easycount.util.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class TopicOnlineConfig implements Serializable {

	private static final long serialVersionUID = -4545416833156833134L;
	public final ConcurrentHashMap<String, String> systemAttrs;
	public final ConcurrentHashMap<String, HashMap<String, String>> interfaceAttrs;

	public TopicOnlineConfig() {
		systemAttrs = new ConcurrentHashMap<String, String>();
		interfaceAttrs = new ConcurrentHashMap<String, HashMap<String, String>>();
	}

	public ConcurrentHashMap<String, String> getSystemAttrs() {
		return systemAttrs;
	}

	public ConcurrentHashMap<String, HashMap<String, String>> getInterfaceAttrs() {
		return interfaceAttrs;
	}

}
