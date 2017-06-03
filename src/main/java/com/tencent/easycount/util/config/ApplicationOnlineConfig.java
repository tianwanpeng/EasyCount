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

import com.alibaba.fastjson.JSON;
import com.tencent.easycount.util.exec.ExpiringMap;

public class ApplicationOnlineConfig {
	private static Logger log = LoggerFactory
			.getLogger(ApplicationOnlineConfig.class);

	final private AtomicReference<ConcurrentHashMap<String, TopicOnlineConfig>> topic2OnlineConfigRef;
	final private CmdProcessor cmdProcessor;
	final private ApplicationListener listener;
	final private String zkroot;

	// final private ZkClient zkClient;
	public ApplicationOnlineConfig(final String zkips, final String zkroot,
			final CmdProcessor cmdProcessor) {
		this.topic2OnlineConfigRef = new AtomicReference<ConcurrentHashMap<String, TopicOnlineConfig>>(
				new ConcurrentHashMap<String, TopicOnlineConfig>());
		this.cmdProcessor = cmdProcessor;

		this.zkroot = zkroot;
		// this.zkClient = new ZkClient(new HashMap<String, String>(), null,
		// null);

		this.listener = new ApplicationListener();
		this.listener.listen();
	}

	public Set<String> getTopics() {
		return this.topic2OnlineConfigRef.get().keySet();
	}

	public TopicOnlineConfig getOnlineConfig(final String topic) {
		return this.topic2OnlineConfigRef.get().get(topic);
	}

	public String getAttr(final String topic, final String intf,
			final String attrname) {
		if (!this.topic2OnlineConfigRef.get().containsKey(topic)) {
			return null;
		}
		if (!this.topic2OnlineConfigRef.get().get(topic).interfaceAttrs
				.containsKey(intf)
				|| !this.topic2OnlineConfigRef.get().get(topic).interfaceAttrs
				.get(intf).containsKey(attrname)) {
			return this.topic2OnlineConfigRef.get().get(topic).systemAttrs
					.get(attrname);
		}

		return this.topic2OnlineConfigRef.get().get(topic).interfaceAttrs.get(
				intf).get(attrname);
	}

	public String getSysAttr(final String topic, final String attrname) {
		if (!this.topic2OnlineConfigRef.get().containsKey(topic)) {
			return null;
		}
		return this.topic2OnlineConfigRef.get().get(topic).systemAttrs
				.get(attrname);
	}

	private class ApplicationListener implements IZkChildListener,
			IZkDataListener {

		@SuppressWarnings("unused")
		final private String applicationCmdRoot;
		@SuppressWarnings("unused")
		final private String applicationConfigRoot;
		final private ExpiringMap<String, byte[]> cmdsmap;

		public ApplicationListener() {
			this.applicationCmdRoot = ApplicationOnlineConfig.this.zkroot
					+ "/CMDS";
			this.applicationConfigRoot = ApplicationOnlineConfig.this.zkroot
					+ "/CONFIG";
			this.cmdsmap = ExpiringMap.builder()
					.expiration(3, TimeUnit.SECONDS).build();
		}

		private void listen() {
			// zkClient.createPersistent(applicationCmdRoot, true);
			// zkClient.subscribeChildChanges(applicationCmdRoot, this);
		}

		private CmdParam getParams(final byte[] data) {
			if ((data == null) || (data.length == 0)) {
				return new CmdParam(null);
			}
			final String d = new String(data);
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
		public void handleChildChange(final String parentPath,
				final List<String> currentChilds) throws Exception {

			try {
				final HashMap<String, byte[]> cmdsTobeProcessed = new HashMap<String, byte[]>();
				for (final String cmd : currentChilds) {
					if (!this.cmdsmap.containsKey(cmd)) {
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
					for (final String cmd : cmdsTobeProcessed.keySet()) {
						final CmdParam param = getParams(cmdsTobeProcessed
								.get(cmd));
						log.info("cmd received : " + cmd + " : "
								+ param.toString());
						ApplicationOnlineConfig.this.cmdProcessor.process(cmd,
								param);
					}
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void handleDataChange(final String dataPath, final Object data)
				throws Exception {

		}

		@Override
		public void handleDataDeleted(final String dataPath) throws Exception {

		}
	}

	public static class CmdParam {
		final private HashMap<String, String> params;

		public CmdParam(final HashMap<String, String> params) {
			this.params = params == null ? new HashMap<String, String>()
					: params;
		}

		public HashMap<String, String> getParams() {
			return this.params;
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
