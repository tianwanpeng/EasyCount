package com.tencent.trc.udfnew;

import org.apache.hadoop.hive.ql.exec.FunctionRegistry;

public class MyUDFUtils {
	public static void initialize() {
		FunctionRegistry.registerTemporaryFunction("countd_bf",
				GenericUDAFCountDistinct.class);
		FunctionRegistry.registerTemporaryFunction("countd",
				GenericUDAFCountDistinctHLLP.class);
		FunctionRegistry.registerTemporaryFunction("countd_hllp",
				GenericUDAFCountDistinctHLLP.class);
		FunctionRegistry.registerTemporaryFunction("hllp",
				GenericUDAFHLLP.class);

		FunctionRegistry.registerTemporaryFunction("collect_set",
				GenericUDAFCollectSet2.class);
		FunctionRegistry.registerTemporaryFunction("collect_list",
				GenericUDAFCollectList1.class);

		FunctionRegistry.registerTemporaryFunction("mapmap",
				GenericUDAFMapMap.class);
		FunctionRegistry.registerTemporaryFunction("mapmapmerge",
				GenericUDAFMapMapMerge.class);
		FunctionRegistry.registerTemporaryFunction("mapmapset",
				GenericUDAFMapMapSet.class);

		FunctionRegistry.registerTemporaryFunction("hllp_merge",
				GenericUDFHLLPMerge.class);
		FunctionRegistry.registerTemporaryFunction("hllp_get",
				GenericUDFHLLPGet.class);

		FunctionRegistry.registerTemporaryFunction("array_get",
				GenericUDFArrayGet.class);
		FunctionRegistry.registerTemporaryFunction("array_getlast",
				GenericUDFArrayGetLast.class);
		FunctionRegistry.registerTemporaryFunction("array_to_str",
				GenericUDFArrayToStr.class);
		FunctionRegistry.registerTemporaryFunction("array_merge",
				GenericUDFArrayMerge.class);
		FunctionRegistry.registerTemporaryFunction("array_add",
				GenericUDFArrayAdd.class);
		FunctionRegistry.registerTemporaryFunction("array_set",
				GenericUDFArraySet.class);
		FunctionRegistry.registerTemporaryFunction("array_del",
				GenericUDFArrayDel.class);
		FunctionRegistry.registerTemporaryFunction("array_rm",
				GenericUDFArrayDel.class);

		FunctionRegistry.registerTemporaryFunction("map_get",
				GenericUDFMapGet.class);
		FunctionRegistry.registerTemporaryFunction("map_keyset",
				GenericUDFMapKeys_new.class);
		FunctionRegistry.registerTemporaryFunction("map_merge",
				GenericUDFMapMerge.class);
		// FunctionRegistry.registerTemporaryFunction("map_keyset",
		// GenericUDFMapKeyset.class);
		// FunctionRegistry.registerTemporaryFunction("map_values",
		// GenericUDFMapValues.class);
		FunctionRegistry.registerTemporaryFunction("map_struct",
				GenericUDFMapStruct.class);
		FunctionRegistry.registerTemporaryFunction("map_put",
				GenericUDFMapPut.class);
		FunctionRegistry.registerTemporaryFunction("map_del",
				GenericUDFMapDel.class);
		FunctionRegistry.registerTemporaryFunction("map_rm",
				GenericUDFMapDel.class);
		FunctionRegistry.registerTemporaryFunction("map_to_str",
				GenericUDFMapToStr.class);

		FunctionRegistry.registerTemporaryFunction("struct_get",
				GenericUDFStructGet.class);

		FunctionRegistry.registerTemporaryFunction("getallpath",
				GenericUDFGetAllPathInMap.class);
		FunctionRegistry.registerTemporaryFunction("getallpathwithdests",
				GenericUDFGetAllPathInMapWithDests.class);
		FunctionRegistry.registerTemporaryFunction("getallpathinlistwithdests",
				GenericUDFGetAllPathInListWithDests.class);
		FunctionRegistry.registerTemporaryFunction(
				"getallpathinlist_withsrcsanddests",
				GenericUDFGetAllPathInListWithSrcsDests.class);

		// for binary
		FunctionRegistry.registerTemporaryFunction("emptybinary",
				GenericUDFBinaryEmpty.class);
		FunctionRegistry.registerTemporaryFunction("bit_rsft",
				GenericUDFBinaryRightShift.class);
		FunctionRegistry.registerTemporaryFunction("bit_lsft",
				GenericUDFBinaryLeftShift.class);
		FunctionRegistry.registerTemporaryFunction("bit_set",
				GenericUDFBinarySet.class);
		FunctionRegistry.registerTemporaryFunction("bit_clear",
				GenericUDFBinaryClear.class);

		// bloomfilter
		FunctionRegistry.registerTemporaryFunction("bloom",
				GenericUDAFBloom.class);
		FunctionRegistry.registerTemporaryFunction("bloom_contains",
				GenericUDFBloomContains.class);

		// other function
		FunctionRegistry.registerTemporaryFunction("rand_i", UDFRand_i.class);
		FunctionRegistry.registerTemporaryFunction("inet_aton",
				GenericUDFINETATON.class);
		FunctionRegistry.registerTemporaryFunction("inet_ntoa",
				GenericUDFINETNTOA.class);

	}
}
