package com.tencent.easycount.driver;

import java.util.Random;

import org.apache.commons.codec.binary.Base64;

import com.tencent.easycount.util.bloom.BloomFilter;

public class TestBF {

	public static void main(final String[] args) {
		final BloomFilter<Integer> bf = new BloomFilter<Integer>(10000, 0.008);
		final Random r = new Random();
		for (int i = 0; i < 10000; i++) {
			bf.add(i);
		}

		final byte[] data = bf.getBitSet().toByteArray();
		final byte[] todata = Base64.encodeBase64(data);
		System.out.println(data.length);
		System.out.println(todata.length);
		System.out.println(new String(todata).length());
		System.out.println(new String(todata).getBytes().length);

		System.out.println(bf.getBitSet().length());
		System.out.println(bf.getBitSet().size());
		System.out.println(bf.getM());
		System.out.println(bf.getK());
		System.out.println(bf.valueSize());
		System.out.println(bf.getBitSet().cardinality());
		System.out.println(bf.getBitSet().toByteArray().length);

		int xx = 0;
		for (int i = 0; i < 100000000; i++) {
			final int x = r.nextInt();
			if (bf.contains(x)) {
				if ((x < 0) || (x >= 1000)) {
					xx++;
					System.out.println(i + " " + (xx / (double) i));
				}
			}
		}
	}
}
