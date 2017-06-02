package com.tencent.easycount.util.map;

public interface Updater1<K, V1, V2> {
	public V2 update(K k, V1 v, V2 finalv);

	public boolean inplace();
}
