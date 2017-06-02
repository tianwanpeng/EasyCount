package com.tencent.trc.util.map;

public interface Updater<V1, V2> {
	public V2 update(V2 finalv, V1 v);

	public boolean inplace();
}
