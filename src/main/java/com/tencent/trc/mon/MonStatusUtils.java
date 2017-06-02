package com.tencent.trc.mon;

import java.util.concurrent.ConcurrentHashMap;

import com.tencent.trc.conf.TrcConfiguration;

public class MonStatusUtils {
	transient volatile static private ConcurrentHashMap<String, MonStatusUpdater> id2updater = null;
	{
		id2updater = new ConcurrentHashMap<String, MonStatusUpdater>();
	}

	synchronized public static MonStatusUpdater getTubeMonStatusUpdater(
			String tubeMaster, int tubePort, String addrList,
			final String topic, final String interfaceId, String kvsplitter,
			final int emitInterval) {
		if (id2updater == null) {
			id2updater = new ConcurrentHashMap<String, MonStatusUpdater>();
		}
		String id = tubeMaster + "-" + tubePort + "-" + topic + "-"
				+ interfaceId + "-" + kvsplitter;
		if (!id2updater.containsKey(id)) {
			id2updater.put(id, new MonStatusUpdater(new MonTubeMsgSender(
					tubeMaster, tubePort, addrList, topic, interfaceId,
					kvsplitter), emitInterval));
		}
		return id2updater.get(id);
	}

	public static MonStatusUpdater getTubeMonStatusUpdater(
			TrcConfiguration hconf) {
		String tubeMaster = hconf.get("moniter.tube.master",
				"tl-vip-tube-master");
		int tubePort = hconf.getInt("moniter.tube.port", 8609);
		String addrList = hconf.get("moniter.tube.addrlist",
				"tl-vip-tube-master:8609");
		String topic = hconf.get("moniter.tube.topic", "teg_trc_monitor");
		String interfaceId = hconf.get("moniter.tube.interfaceId", "trc_mon");
		String kvsplitter = hconf.get("moniter.tube.kvsplitter", "&cnt=");
		int emitInterval = hconf.getInt("moniter.tube.emit.interval", 60);
		return getTubeMonStatusUpdater(tubeMaster, tubePort, addrList, topic,
				interfaceId, kvsplitter, emitInterval);
	}
}
