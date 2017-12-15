package com.tencent.easycount.driver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import com.tencent.easycount.conf.ECConfiguration;

public class ConfigGenerator {
	static ECConfiguration generateConfig(final String[] args)
			throws IOException {
		final HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < args.length; i++) {
			final int pos = args[i].indexOf('=');
			if (pos <= 0) {
				continue;
			}
			final String[] strs = new String[2];
			strs[0] = args[i].substring(0, pos);
			strs[1] = args[i].substring(pos + 1);
			map.put(strs[0].toLowerCase().trim(), strs[1]);
		}

		final ECConfiguration config = new ECConfiguration();
		final String sysconfigfile = map.get("sysconfig");
		if (sysconfigfile != null) {
			final URL url = new File(sysconfigfile).toURI().toURL();
			final Ini ini = new Ini(url);
			for (final String seckey : ini.keySet()) {
				final Section section = ini.get(seckey);
				for (final String key : section.keySet()) {
					final String value = section.get(key);
					config.set(key, value);
				}
			}
		}

		for (final String key : map.keySet()) {
			config.set(key, map.get(key));
		}

		final String topologyName = config.get("topologyname");
		if (null == topologyName) {
			System.err.println("error ::: topologyname must be set .... ");
			System.exit(-1);
		}

		final String groupname = config.get("consumergroup");
		if (null == groupname) {
			config.set("consumergroup", topologyName + "_consumergroup");
		}

		if (config.getBoolean("printversion", false)) {
			System.exit(-1);
		}
		return config;
	}

	static void addSysteminfoToConfig(final ECConfiguration config,
			final Ini ini) {
		for (final String seckey : ini.keySet()) {
			final Section section = ini.get(seckey);
			for (final String key : section.keySet()) {
				if (config.get(key) != null) {
					continue;
				}
				final String value = section.get(key);
				config.set(seckey.equals("system") ? key : seckey + "::" + key,
						value);
			}
		}
	}

}
