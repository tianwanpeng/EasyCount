/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencent.trc.udfnew;

import java.util.Random;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.udf.UDFType;
import org.apache.hadoop.io.LongWritable;

/**
 * UDFRand.
 * 
 */
@Description(name = "rand_i", value = "_FUNC_([seed]) - Returns a pseudorandom number between 0 and 1")
@UDFType(deterministic = false)
public class UDFRand_i extends UDF {
	private Random random;

	private LongWritable result = new LongWritable();

	public UDFRand_i() {
	}

	public LongWritable evaluate() {
		if (random == null) {
			random = new Random();
		}
		result.set(random.nextLong());
		return result;
	}

	public LongWritable evaluate(LongWritable range) {
		if (random == null) {
			random = new Random();
		}
		result.set(random.nextLong() % range.get());
		return result;
	}

}
