package com.tencent.trc.util.map;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UpdaterMap1<K, V1, V2> {

	final private ConcurrentHashMap<K, V2> map = new ConcurrentHashMap<K, V2>();
	final private Updater1<K, V1, V2> p;

	// final private AtomicInteger size = new AtomicInteger(0);

	public UpdaterMap1(Updater1<K, V1, V2> p) {
		this.p = p;
	}

	public void add(K k, V1 v) {
		synchronized (map) {
			if (p.inplace() && map.containsKey(k)) {
				p.update(k, v, map.get(k));
			} else {
				// if (!map.containsKey(k)) {
				// size.addAndGet(1);
				// }
				map.put(k, p.update(k, v, map.get(k)));
			}
		}
	}

	public V2 get(K k) {
		synchronized (map) {
			return map.get(k);
		}
	}

	public V2 getAndSet(K k, V1 v1) {
		synchronized (map) {
			V2 v2 = map.remove(k);
			add(k, v1);
			return v2;
		}
	}

	public V2 remove(K k) {
		synchronized (map) {
			// size.addAndGet(-1);
			V2 v2 = map.remove(k);
			// if (v2 == null) {
			// size.incrementAndGet();
			// }
			return v2;
		}
	}

	public Set<K> keySet() {
		synchronized (map) {
			return map.keySet();
		}
	}

	public Set<Map.Entry<K, V2>> entrySet() {
		synchronized (map) {
			return map.entrySet();
		}
	}

	public int size() {
		// return size.get();
		return map.size();
	}
}
