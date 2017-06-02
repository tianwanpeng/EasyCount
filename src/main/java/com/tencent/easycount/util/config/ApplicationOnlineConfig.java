package com.tencent.easycount.util.config;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.easycount.util.exec.ExpiringMap;

public class ApplicationOnlineConfig {
	private static Logger log = LoggerFactory
			.getLogger(ApplicationOnlineConfig.class);

	final private AtomicReference<ConcurrentHashMap<String, TopicOnlineConfig>> topic2OnlineConfigRef;
	final private CmdProcessor cmdProcessor;
	final private ApplicationListener listener;
	final private String zkroot;

	// final private ZkClient zkClient;
	public ApplicationOnlineConfig(String zkips, String zkroot,
			CmdProcessor cmdProcessor) {
		topic2OnlineConfigRef = new AtomicReference<ConcurrentHashMap<String, TopicOnlineConfig>>(
				new ConcurrentHashMap<String, TopicOnlineConfig>());
		this.cmdProcessor = cmdProcessor;

		this.zkroot = zkroot;
		// this.zkClient = new ZkClient(new HashMap<String, String>(), null,
		// null);

		this.listener = new ApplicationListener();
		this.listener.listen();
	}

	public Set<String> getTopics() {
		return topic2OnlineConfigRef.get().keySet();
	}

	public TopicOnlineConfig getOnlineConfig(String topic) {
		return topic2OnlineConfigRef.get().get(topic);
	}

	public String getAttr(String topic, String intf, String attrname) {
		if (!topic2OnlineConfigRef.get().containsKey(topic)) {
			return null;
		}
		if (!topic2OnlineConfigRef.get().get(topic).interfaceAttrs
				.containsKey(intf)
				|| !topic2OnlineConfigRef.get().get(topic).interfaceAttrs.get(
						intf).containsKey(attrname)) {
			return topic2OnlineConfigRef.get().get(topic).systemAttrs
					.get(attrname);
		}

		return topic2OnlineConfigRef.get().get(topic).interfaceAttrs.get(intf)
				.get(attrname);
	}

	public String getSysAttr(String topic, String attrname) {
		if (!topic2OnlineConfigRef.get().containsKey(topic)) {
			return null;
		}
		return topic2OnlineConfigRef.get().get(topic).systemAttrs.get(attrname);
	}

	private class ApplicationListener implements IZkChildListener,
			IZkDataListener {

		@SuppressWarnings("unused")
		final private String applicationCmdRoot;
		@SuppressWarnings("unused")
		final private String applicationConfigRoot;
		final private ExpiringMap<String, byte[]> cmdsmap;

		public ApplicationListener() {
			this.applicationCmdRoot = zkroot + "/CMDS";
			this.applicationConfigRoot = zkroot + "/CONFIG";
			this.cmdsmap = ExpiringMap.builder()
					.expiration(3, TimeUnit.SECONDS).build();
		}

		private void listen() {
			// zkClient.createPersistent(applicationCmdRoot, true);
			// zkClient.subscribeChildChanges(applicationCmdRoot, this);
		}

		private CmdParam getParams(byte[] data) {
			if (data == null || data.length == 0) {
				return new CmdParam(null);
			}
			String d = new String(data);
			return JSON.parseObject(d, CmdParam.class);
		}

		private void updateconfig() {
			// List<String> topics =
			// zkClient.getChildren(applicationConfigRoot);
			// ConcurrentHashMap<String, TopicOnlineConfig> topic2OnlineConfig =
			// new ConcurrentHashMap<String, TopicOnlineConfig>();
			// if (topics != null) {
			// for (String topic : topics) {
			// byte[] data = zkClient.readData(applicationConfigRoot + "/"
			// + topic);
			// String config = new String(data);
			// TopicOnlineConfig toc = JSON.parseObject(config,
			// TopicOnlineConfig.class);
			// topic2OnlineConfig.put(topic, toc);
			// }
			// }
			// topic2OnlineConfigRef.set(topic2OnlineConfig);
		}

		@Override
		public void handleChildChange(String parentPath,
				List<String> currentChilds) throws Exception {

			try {
				HashMap<String, byte[]> cmdsTobeProcessed = new HashMap<String, byte[]>();
				for (String cmd : currentChilds) {
					if (!cmdsmap.containsKey(cmd)) {
						if ("updateconfig".equalsIgnoreCase(cmd)) {
							log.info("cmd received : updateconfig");
							updateconfig();
						} else {
							// byte[] data =
							// zkClient.readData(applicationCmdRoot
							// + "/" + cmd);
							// cmdsmap.put(cmd, data);
							// cmdsTobeProcessed.put(cmd, data);
						}
					}
				}
				if (cmdsTobeProcessed.size() > 0) {
					for (String cmd : cmdsTobeProcessed.keySet()) {
						CmdParam param = getParams(cmdsTobeProcessed.get(cmd));
						log.info("cmd received : " + cmd + " : "
								+ param.toString());
						cmdProcessor.process(cmd, param);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void handleDataChange(String dataPath, Object data)
				throws Exception {

		}

		@Override
		public void handleDataDeleted(String dataPath) throws Exception {

		}
	}

	public static class CmdParam {
		final private HashMap<String, String> params;

		public CmdParam(HashMap<String, String> params) {
			this.params = params == null ? new HashMap<String, String>()
					: params;
		}

		public HashMap<String, String> getParams() {
			return params;
		}

		@Override
		public String toString() {
			return this.params.toString();
		}
	}

	public static interface CmdProcessor {
		public void process(String cmd, CmdParam cmdParam);
	}

}
