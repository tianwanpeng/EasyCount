package com.tencent.easycount.udfnew;

import org.apache.hadoop.hive.ql.exec.FunctionRegistry;

public class MyUDFUtils {

	public static void initialize() {

		// final HiveConf conf = new HiveConf();
		// conf.setVar(HiveConf.ConfVars.HIVE_AUTHENTICATOR_MANAGER,
		// SessionStateUserAuthenticator.class.getName());
		//
		// final SessionState ss = new SessionState(conf, "ttt");
		// SessionState.start(ss);
		FunctionRegistry.registerTemporaryUDF("countd_bf",
				GenericUDAFCountDistinct.class);
		FunctionRegistry.registerTemporaryUDF("countd",
				GenericUDAFCountDistinctHLLP.class);
		FunctionRegistry.registerTemporaryUDF("countd_hllp",
				GenericUDAFCountDistinctHLLP.class);
		FunctionRegistry.registerTemporaryUDF("hllp", GenericUDAFHLLP.class);

		FunctionRegistry.registerTemporaryUDF("collect_set",
				GenericUDAFCollectSet2.class);
		FunctionRegistry.registerTemporaryUDF("collect_list",
				GenericUDAFCollectList1.class);

		FunctionRegistry
		.registerTemporaryUDF("mapmap", GenericUDAFMapMap.class);
		FunctionRegistry.registerTemporaryUDF("mapmapmerge",
				GenericUDAFMapMapMerge.class);
		FunctionRegistry.registerTemporaryUDF("mapmapset",
				GenericUDAFMapMapSet.class);

		FunctionRegistry.registerTemporaryUDF("hllp_merge",
				GenericUDFHLLPMerge.class);
		FunctionRegistry.registerTemporaryUDF("hllp_get",
				GenericUDFHLLPGet.class);

		FunctionRegistry.registerTemporaryUDF("array_get",
				GenericUDFArrayGet.class);
		FunctionRegistry.registerTemporaryUDF("array_getlast",
				GenericUDFArrayGetLast.class);
		FunctionRegistry.registerTemporaryUDF("array_to_str",
				GenericUDFArrayToStr.class);
		FunctionRegistry.registerTemporaryUDF("array_merge",
				GenericUDFArrayMerge.class);
		FunctionRegistry.registerTemporaryUDF("array_add",
				GenericUDFArrayAdd.class);
		FunctionRegistry.registerTemporaryUDF("array_set",
				GenericUDFArraySet.class);
		FunctionRegistry.registerTemporaryUDF("array_del",
				GenericUDFArrayDel.class);
		FunctionRegistry.registerTemporaryUDF("array_rm",
				GenericUDFArrayDel.class);

		FunctionRegistry
		.registerTemporaryUDF("map_get", GenericUDFMapGet.class);
		FunctionRegistry.registerTemporaryUDF("map_keyset",
				GenericUDFMapKeys_new.class);
		FunctionRegistry.registerTemporaryUDF("map_merge",
				GenericUDFMapMerge.class);
		// FunctionRegistry.registerTemporaryUDF("map_keyset",
		// GenericUDFMapKeyset.class);
		// FunctionRegistry.registerTemporaryUDF("map_values",
		// GenericUDFMapValues.class);
		FunctionRegistry.registerTemporaryUDF("map_struct",
				GenericUDFMapStruct.class);
		FunctionRegistry
		.registerTemporaryUDF("map_put", GenericUDFMapPut.class);
		FunctionRegistry
		.registerTemporaryUDF("map_del", GenericUDFMapDel.class);
		FunctionRegistry.registerTemporaryUDF("map_rm", GenericUDFMapDel.class);
		FunctionRegistry.registerTemporaryUDF("map_to_str",
				GenericUDFMapToStr.class);

		FunctionRegistry.registerTemporaryUDF("struct_get",
				GenericUDFStructGet.class);

		FunctionRegistry.registerTemporaryUDF("getallpath",
				GenericUDFGetAllPathInMap.class);
		FunctionRegistry.registerTemporaryUDF("getallpathwithdests",
				GenericUDFGetAllPathInMapWithDests.class);
		FunctionRegistry.registerTemporaryUDF("getallpathinlistwithdests",
				GenericUDFGetAllPathInListWithDests.class);
		FunctionRegistry.registerTemporaryUDF(
				"getallpathinlist_withsrcsanddests",
				GenericUDFGetAllPathInListWithSrcsDests.class);

		// for binary
		FunctionRegistry.registerTemporaryUDF("emptybinary",
				GenericUDFBinaryEmpty.class);
		FunctionRegistry.registerTemporaryUDF("bit_rsft",
				GenericUDFBinaryRightShift.class);
		FunctionRegistry.registerTemporaryUDF("bit_lsft",
				GenericUDFBinaryLeftShift.class);
		FunctionRegistry.registerTemporaryUDF("bit_set",
				GenericUDFBinarySet.class);
		FunctionRegistry.registerTemporaryUDF("bit_clear",
				GenericUDFBinaryClear.class);

		// bloomfilter
		FunctionRegistry.registerTemporaryUDF("bloom", GenericUDAFBloom.class);
		FunctionRegistry.registerTemporaryUDF("bloom_contains",
				GenericUDFBloomContains.class);

		// other function
		FunctionRegistry.registerTemporaryUDF("rand_i", UDFRand_i.class);
		FunctionRegistry.registerTemporaryUDF("inet_aton",
				GenericUDFINETATON.class);
		FunctionRegistry.registerTemporaryUDF("inet_ntoa",
				GenericUDFINETNTOA.class);

	}
}
