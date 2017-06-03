package com.tencent.easycount.util.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.I0Itec.zkclient.ZkClient;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import com.alibaba.fastjson.JSON;
import com.tencent.easycount.util.config.ApplicationOnlineConfig.CmdParam;

public class ConfigManager {

	final private String zkroot;
	final private ZkClient zkClient;

	public ConfigManager(final String zkips, final String zkroot) {
		this.zkroot = zkroot;
		this.zkClient = new ZkClient(zkips);
	}

	public void sendCmd(final String cmd, final byte[] data) {
		this.zkClient.writeData(this.zkroot + "/CMDS/" + cmd, data);
	}

	public void updateconfig(final String topic, final String configFile)
			throws InvalidFileFormatException, IOException {
		final URL url = new File(configFile).toURI().toURL();
		final Ini ini = new Ini(url);
		final TopicOnlineConfig onlineConfig = new TopicOnlineConfig();
		for (final String secKey : ini.keySet()) {
			final Section sec = ini.get(secKey);
			if ("system".equalsIgnoreCase(secKey)) {
				for (final String key : sec.keySet()) {
					onlineConfig.systemAttrs.put(key, sec.get(key));
				}
			} else if (secKey.startsWith("interface=")) {
				final String interfaceName = secKey.substring(10);
				if (!onlineConfig.interfaceAttrs.containsKey(interfaceName)) {
					onlineConfig.interfaceAttrs.put(interfaceName,
							new HashMap<String, String>());
				}
				final HashMap<String, String> interfaceAttrs = onlineConfig.interfaceAttrs
						.get(interfaceName);
				for (final String interfaceKey : sec.keySet()) {
					interfaceAttrs.put(interfaceKey, sec.get(interfaceKey));
				}
			}
		}
		this.zkClient.writeData(this.zkroot + "/CONFIG/" + topic, JSON
				.toJSONString(onlineConfig).getBytes());
	}

	public static void main(final String[] args) {

		final String helpinfo = "!!!ERROR::: please input cmd: [ updateconfig | stop ]";
		try {
			if (args.length < 1) {
				throw new Exception(helpinfo);
			}
			final HashMap<String, String> map = getKV(args);

			final String zkips = map.remove("zkips");
			final String zkroot = map.remove("zkroot");
			if ((zkips == null) || (zkroot == null)) {
				throw new RuntimeException("zkips or zkroot is null");
			}

			final ConfigManager manager = new ConfigManager(zkips, zkroot);

			if (args[0].equals("stop")) {
				manager.sendCmd("stop", JSON.toJSONBytes(new CmdParam(map)));
			} else if ("updateconfig".equals(args[0])) {
				final String topic = map.get("topic");
				final String configFile = map.get("configfile");
				if ((topic == null) || (configFile == null)) {
					throw new RuntimeException("topic or configfile is null");
				}
				manager.updateconfig(topic, configFile);
			} else {
				throw new Exception(helpinfo);
			}
		} catch (final Exception e) {
			e.printStackTrace();
			System.out.println("CMD_FAIL");
			System.exit(1);
		}
		System.out.println("CMD_SUCC");
		System.exit(0);
	}

	private static HashMap<String, String> getKV(final String[] args) {
		final HashMap<String, String> map = new HashMap<String, String>();
		for (final String arg : args) {
			final int pos = arg.indexOf('=');
			if (pos > 0) {
				map.put(arg.substring(0, pos), arg.substring(pos + 1));
			}
		}
		return map;
	}

}
