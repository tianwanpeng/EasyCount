package com.tencent.easycount.util.algr;

import java.util.Arrays;

public class Permutation {

	public void permutate(int[] nums, PermutateCallback cb) {
		int NUM = nums.length;
		int[] idxs = new int[NUM];
		Arrays.fill(idxs, 0);
		for (int i = 0; i < nums[0]; i++) {
			idxs[0] = i;
			innerPermutate(nums, idxs, 1, cb);
		}
	}

	private void innerPermutate(int[] nums, int[] idxs, int numidx,
			PermutateCallback cb) {
		if (numidx >= nums.length) {
			cb.call(idxs);
			return;
		}
		for (int i = 0; i < nums[numidx]; i++) {
			idxs[numidx] = i;
			innerPermutate(nums, idxs, numidx + 1, cb);
		}
	}

	private static void print(int[] idxs) {
		for (int i = 0; i < idxs.length; i++) {
			System.out.print(idxs[i] + ", ");
		}
		System.out.println();
	}

	public static interface PermutateCallback {
		public void call(int[] idxs);
	}

	public static void main(String[] args) {
		int[] nums = new int[3];
		nums[0] = 2;
		nums[1] = 3;
		nums[2] = 4;
		new Permutation().permutate(nums, new PermutateCallback() {
			@Override
			public void call(int[] idxs) {
				print(idxs);
			}
		});
	}
}
