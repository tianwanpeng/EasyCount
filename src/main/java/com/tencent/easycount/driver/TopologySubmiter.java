package com.tencent.easycount.driver;

import java.util.Properties;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;

import com.tencent.easycount.conf.ECConfiguration;

public class TopologySubmiter {
	static void submitTopology(final StormTopology topology,
			final String topologyName, final boolean localMode,
			final ECConfiguration config, final int workNum) {

		final Config conf = new Config();
		final Properties pro = config.getAllProperties();

		for (final Object keyobj : pro.keySet()) {
			conf.put((String) keyobj, pro.get(keyobj));
		}

		conf.setDebug(false);
		conf.setNumWorkers(workNum <= 0 ? 1 : workNum);
		conf.setNumAckers((workNum / 3) + 1);

		if (localMode) {
			final LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(topologyName, conf, topology);
		} else {
			try {
				StormSubmitter.submitTopology(topologyName, conf, topology);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

}
