package com.tencent.trc.util.map;

import java.util.concurrent.atomic.AtomicLong;

public class UpdaterMapFactory {
	public static UpdaterMap<Integer, AtomicLong> getIntegerUpdater() {
		return new UpdaterMap<Integer, AtomicLong>(
				new Updater<Integer, AtomicLong>() {

					@Override
					public AtomicLong update(AtomicLong finalv, Integer v) {
						if (finalv == null) {
							return new AtomicLong(v);
						}
						finalv.addAndGet(v);
						return finalv;
					}

					@Override
					public boolean inplace() {
						return true;
					}
				});
	}

}
