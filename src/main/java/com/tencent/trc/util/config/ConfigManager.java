package com.tencent.trc.util.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.I0Itec.zkclient.ZkClient;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import com.alibaba.fastjson.JSON;
import com.tencent.trc.util.config.ApplicationOnlineConfig.CmdParam;

public class ConfigManager {

	final private String zkroot;
	final private ZkClient zkClient;

	public ConfigManager(String zkips, String zkroot) {
		this.zkroot = zkroot;
		this.zkClient = new ZkClient(zkips);
	}

	public void sendCmd(String cmd, byte[] data) {
		zkClient.writeData(zkroot + "/CMDS/" + cmd, data);
	}

	public void updateconfig(String topic, String configFile)
			throws InvalidFileFormatException, IOException {
		URL url = new File(configFile).toURI().toURL();
		Ini ini = new Ini(url);
		TopicOnlineConfig onlineConfig = new TopicOnlineConfig();
		for (String secKey : ini.keySet()) {
			Section sec = ini.get(secKey);
			if ("system".equalsIgnoreCase(secKey)) {
				for (String key : sec.keySet()) {
					onlineConfig.systemAttrs.put(key, sec.get(key));
				}
			} else if (secKey.startsWith("interface=")) {
				String interfaceName = secKey.substring(10);
				if (!onlineConfig.interfaceAttrs.containsKey(interfaceName)) {
					onlineConfig.interfaceAttrs.put(interfaceName,
							new HashMap<String, String>());
				}
				HashMap<String, String> interfaceAttrs = onlineConfig.interfaceAttrs
						.get(interfaceName);
				for (String interfaceKey : sec.keySet()) {
					interfaceAttrs.put(interfaceKey, sec.get(interfaceKey));
				}
			}
		}
		zkClient.writeData(zkroot + "/CONFIG/" + topic,
				JSON.toJSONString(onlineConfig).getBytes());
	}

	public static void main(String[] args) {

		String helpinfo = "!!!ERROR::: please input cmd: [ updateconfig | stop ]";
		try {
			if (args.length < 1) {
				throw new Exception(helpinfo);
			}
			HashMap<String, String> map = getKV(args);

			String zkips = map.remove("zkips");
			String zkroot = map.remove("zkroot");
			if (zkips == null || zkroot == null) {
				throw new RuntimeException("zkips or zkroot is null");
			}

			ConfigManager manager = new ConfigManager(zkips, zkroot);

			if (args[0].equals("stop")) {
				manager.sendCmd("stop", JSON.toJSONBytes(new CmdParam(map)));
			} else if ("updateconfig".equals(args[0])) {
				String topic = map.get("topic");
				String configFile = map.get("configfile");
				if (topic == null || configFile == null) {
					throw new RuntimeException("topic or configfile is null");
				}
				manager.updateconfig(topic, configFile);
			} else {
				throw new Exception(helpinfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("CMD_FAIL");
			System.exit(1);
		}
		System.out.println("CMD_SUCC");
		System.exit(0);
	}

	private static HashMap<String, String> getKV(String[] args) {
		HashMap<String, String> map = new HashMap<String, String>();
		for (String arg : args) {
			int pos = arg.indexOf('=');
			if (pos > 0) {
				map.put(arg.substring(0, pos), arg.substring(pos + 1));
			}
		}
		return map;
	}

}
