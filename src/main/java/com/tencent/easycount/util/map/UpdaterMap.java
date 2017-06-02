package com.tencent.easycount.util.map;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UpdaterMap<V1, V2> {

	final private ConcurrentHashMap<String, V2> map = new ConcurrentHashMap<String, V2>();
	final private Updater<V1, V2> p;

	public UpdaterMap(Updater<V1, V2> p) {
		this.p = p;
	}

	public void add(String k, V1 v) {
		synchronized (map) {
			if (p.inplace() && map.containsKey(k)) {
				p.update(map.get(k), v);
			} else {
				map.put(k, p.update(map.get(k), v));
			}
		}
	}

	public V2 get(String k) {
		synchronized (map) {
			return map.get(k);
		}
	}

	public V2 getAndSet(String k, V1 v1) {
		synchronized (map) {
			V2 v2 = map.remove(k);
			add(k, v1);
			return v2;
		}
	}

	public V2 remove(String k) {
		synchronized (map) {
			return map.remove(k);
		}
	}

	public Set<String> keySet() {
		synchronized (map) {
			return map.keySet();
		}
	}
}
