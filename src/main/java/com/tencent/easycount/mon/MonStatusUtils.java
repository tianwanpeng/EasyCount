package com.tencent.easycount.mon;

import java.util.concurrent.ConcurrentHashMap;

import com.tencent.easycount.conf.ECConfiguration;

public class MonStatusUtils {
	transient volatile static private ConcurrentHashMap<String, MonStatusUpdater> id2updater = null;
	{
		id2updater = new ConcurrentHashMap<String, MonStatusUpdater>();
	}

	synchronized public static MonStatusUpdater getTubeMonStatusUpdater(
			final String tubeMaster, final int tubePort, final String addrList,
			final String topic, final String interfaceId,
			final String kvsplitter, final int emitInterval) {
		if (id2updater == null) {
			id2updater = new ConcurrentHashMap<String, MonStatusUpdater>();
		}
		final String id = tubeMaster + "-" + tubePort + "-" + topic + "-"
				+ interfaceId + "-" + kvsplitter;
		if (!id2updater.containsKey(id)) {
			// id2updater.put(id, new MonStatusUpdater(new MonTubeMsgSender(
			// tubeMaster, tubePort, addrList, topic, interfaceId,
			// kvsplitter), emitInterval));
		}
		return id2updater.get(id);
	}

	public static MonStatusUpdater getTubeMonStatusUpdater(
			final ECConfiguration hconf) {
		final String tubeMaster = hconf.get("moniter.tube.master",
				"tl-vip-tube-master");
		final int tubePort = hconf.getInt("moniter.tube.port", 8609);
		final String addrList = hconf.get("moniter.tube.addrlist",
				"tl-vip-tube-master:8609");
		final String topic = hconf.get("moniter.tube.topic", "teg_trc_monitor");
		final String interfaceId = hconf.get("moniter.tube.interfaceId",
				"trc_mon");
		final String kvsplitter = hconf.get("moniter.tube.kvsplitter", "&cnt=");
		final int emitInterval = hconf.getInt("moniter.tube.emit.interval", 60);
		return getTubeMonStatusUpdater(tubeMaster, tubePort, addrList, topic,
				interfaceId, kvsplitter, emitInterval);
	}
}
