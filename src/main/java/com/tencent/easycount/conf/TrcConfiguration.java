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

package com.tencent.easycount.conf;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;

import com.tencent.easycount.util.constants.Constants;

public class TrcConfiguration extends Configuration implements Serializable {

	private static final long serialVersionUID = 3206775931810899825L;

	public static enum ConfVars {
		TOPIC_NAME("topic", ""), //
		TOPIC_NAMES("topics", ""), //

		META_ZK_ZKCONNECT("meta.zk.zkConnect", ""), //
		META_ZK_ZKSESSIONTIMEOUTMS("meta.zk.zkSessionTimeoutMs", 10000), //
		META_ZK_ZKCONNECTIONTIMEOUTMS("meta.zk.zkConnectionTimeoutMs", 30000), //
		META_ZK_ZKSYNCTIMEMS("meta.zk.zkSyncTimeMs", 5000), //
		META_ZK_ROOT("meta.zk.root", "/meta"), //

		SORT_ZK_ZKCONNECT("sort.zk.zkConnect", ""), //
		SORT_ZK_ZKSESSIONTIMEOUTMS("sort.zk.zkSessionTimeoutMs", 10000), //
		SORT_ZK_ZKCONNECTIONTIMEOUTMS("sort.zk.zkConnectionTimeoutMs", 30000), //
		SORT_ZK_ZKSYNCTIMEMS("sort.zk.zkSyncTimeMs", 5000), //
		SORT_ZK_ROOT("sort.zk.root", "/tdbank"), //

		TOPOLOGYNAME("topologyname", ""), //

		CONSUMER_GROUP_PREFIX("consumer.group.prefix", "sort"), //
		CONSUMER_GROUP_NAME("consumergroup", ""), //
		MAXDELAYFETCHTIME("MaxDelayFetchTime", 50), //
		META_FETCH_MAX_SIZE("meta.fetch.max_size", 1048576), //
		TOPOLOGY_NAME_PREFIX("topology.name.prefix", "sort"), //
		TOPOLOGY_WORKERS("topology.workers", 20), //
		TOPOLOGY_ACKERS("topology.ackers", 2), //
		MAX_SPOUT_TIMEOUT_SECONDS("topology.message.timeout.secs", 60), //
		SPOUT_ID_PREFIX("spout.id.prefix", "sort"), //
		SPOUT_PARALLEL("spout.parallel", 40), //
		BOLT_ID_PREFIX("bolt.id.prefix", "sort"), //
		BOLT_PARALLEL("bolt.parallel", 80), //
		CHECKER_ID_PREFIX("checker.id.prefix", "sort"), //
		CHECKER_PARALLEL("checker.parallel", 1), //

		COUNT_ID_PREFIX("count.id.prefix", "sort"), //
		COUNT_PARALLEL("count.parallel", 20), //

		LOCAL_STORM_TMP_DIR("local.storm.tmp.dir", "/tmp/stormtmp"), //
		DFS_DATA_DIR("dfs.data.dir", "/tmp/stormdata"), // 120MB
		MAXMSGSIZE("maxmsgsize", 1l * 1024 * 1024), // 1MB

		THREADPOOL_KEEPALIVETIME("threadpool.keepalivetime.seconds", 30), //
		LOCALWRITER_COREPOOLSIZE("localwriter.corepoolsize", 100), //
		LOCALWRITER_MAXPOOLSIZE("localwriter.maxpoolsize", 100), //

		UPLOADER_COREPOOLSIZE("uploader.corepoolsize", 100), //
		UPLOADER_MAXPOOLSIZE("uploader.maxpoolsize", 100), //
		UPLOADER_RUNNER_PARALLEL("uploader.runner.parallel", 2), //
		UPLOADER_MAXFILESIZE("uploader.maxfilesize", 125829120), // 120MB

		BUFFERPOOL_BUFFERSIZE("bufferpool.buffersize", 65536), // 64KB

		TIMEOUT_WITHOUT_CHECK_SECONDS("timeout.without.check.seconds", 3 * 60), // 3mins
		TIMEOUT_WITH_CHECK_AND_ARRIVED_SECONDS(
				"timeout.with.check.and.arrived.seconds", 60), // 60s
		TIMEOUT_WITH_CHECK_BUT_NOT_ARRIVED_MINUTES(
				"timeout.with.check.but.not.arrived.minutes", 30), // 30mins

		RECOVER_CONSUMER_TERMINATE_WAITING_TIME(
				"recover.consumer.terminate.waiting.time", 10 * 60 * 1000l), // 10m

		FLUSH_INTERVAL_TIME("flush.interval.time", 10 * 60 * 1000l), // 10m

		TDW_FS_DEFAULT_NAME("tdw.fs.default.name", ""), //

		CLOSESESSION_TIMEOUT_MS("close.session.timeout.ms", 30l * 1000), //
		FLUSH_MEM_TIMEOUT_MS("flush.mem.timeout.ms", 3l * 60 * 1000), //
		UPLOAD_FILE_TIMEOUT_MS("upload.file.timeout.ms", 10l * 60 * 1000), //
		CHECKER_SESSION_RUNNER_INTERFAL_TIME_MS(
				"checker.sessionrunner.intervaltime.ms", 3l * 60 * 1000), //
		CHECKER_SESSION_TIMEOUT_TIME_DAY_MS(
				"checker.sessiontimeout.timeday.ms", 6 * Constants.ANHOUR), //
		CHECKER_SESSION_TIMEOUT_TIME_HOUR_MS(
				"checker.sessiontimeout.timehour.ms", 40 * Constants.AMINUTE), //
		CHECKER_SESSION_TIMEOUT_TIME_HALFHOUR_MS(
				"checker.sessiontimeout.timehalfhour.ms",
				20 * Constants.AMINUTE), //
		CHECKER_SESSION_TIMEOUT_TIME_QUERTER_MS(
				"checker.sessiontimeout.timequerter.ms", 10 * Constants.AMINUTE), //
		CHECKER_SESSION_TIMEOUT_TIME_TENMINS_MS(
				"checker.sessiontimeout.timetenmins.ms", 10 * Constants.AMINUTE), //
		CHECKER_SESSION_TIMEOUT_TIME_FIVEMINS_MS(
				"checker.sessiontimeout.timefivemins.ms",
				10 * Constants.AMINUTE), //
		CHECKER_SESSION_TIMEOUT_TIME_MINUTE_MS(
				"checker.sessiontimeout.timeminute.ms", 10 * Constants.AMINUTE), //
		META_MAX_SLEEP_TIME("meta.max.sleep.time", 10l * 60 * 1000), //
		META_SENDNUM_PER_NUEXTUPLE("max.sendnum.per.nexttuple", 1), //

		FS_DEFAULT_NAME("fs.default.name", ""), //
		parserNum("parserNum", 100), //
		parsersQueueNum("parsersQueueNum", 1000), //
		BASEDIR("baseDir", ""), //
		CONSUME_FROM_MAX_OFFSET("consume.from.max.offset", false);

		public final String varname;
		public final String defaultVal;
		public final int defaultIntVal;
		public final long defaultLongVal;
		public final float defaultFloatVal;
		public final Class<?> valClass;
		public final boolean defaultBoolVal;

		ConfVars(final String varname, final String defaultVal) {
			this.varname = varname;
			this.valClass = String.class;
			this.defaultVal = defaultVal;
			this.defaultIntVal = parseInt(defaultVal);
			this.defaultLongVal = parseLong(defaultVal);
			this.defaultFloatVal = parseFloat(defaultVal);
			this.defaultBoolVal = parseBoolean(defaultVal);
		}

		ConfVars(final String varname, final int defaultIntVal) {
			this.varname = varname;
			this.valClass = Integer.class;
			this.defaultVal = String.valueOf(defaultIntVal);
			this.defaultIntVal = defaultIntVal;
			this.defaultLongVal = defaultIntVal;
			this.defaultFloatVal = defaultIntVal;
			this.defaultBoolVal = defaultIntVal != 0;
		}

		ConfVars(final String varname, final long defaultLongVal) {
			this.varname = varname;
			this.valClass = Long.class;
			this.defaultVal = String.valueOf(defaultLongVal);
			this.defaultIntVal = (int) defaultLongVal;
			this.defaultLongVal = defaultLongVal;
			this.defaultFloatVal = defaultLongVal;
			this.defaultBoolVal = defaultLongVal != 0;
		}

		ConfVars(final String varname, final float defaultFloatVal) {
			this.varname = varname;
			this.valClass = Float.class;
			this.defaultVal = String.valueOf(defaultFloatVal);
			this.defaultIntVal = (int) defaultFloatVal;
			this.defaultLongVal = (long) defaultFloatVal;
			this.defaultFloatVal = defaultFloatVal;
			this.defaultBoolVal = defaultFloatVal != 0;
		}

		ConfVars(final String varname, final boolean defaultBoolVal) {
			this.varname = varname;
			this.valClass = Boolean.class;
			this.defaultVal = String.valueOf(defaultBoolVal);
			this.defaultIntVal = defaultBoolVal ? 1 : 0;
			this.defaultLongVal = defaultBoolVal ? 1 : 0;
			this.defaultFloatVal = defaultBoolVal ? 1 : 0;
			this.defaultBoolVal = defaultBoolVal;
		}

		private boolean parseBoolean(final String defaultVal2) {
			try {
				return Boolean.getBoolean(defaultVal2);
			} catch (final Exception e) {
				return false;
			}
		}

		private float parseFloat(final String defaultVal2) {
			try {
				return Float.parseFloat(defaultVal2);
			} catch (final Exception e) {
				return -1;
			}
		}

		private long parseLong(final String defaultVal2) {
			try {
				return Long.parseLong(defaultVal2);
			} catch (final Exception e) {
				return -1;
			}
		}

		private int parseInt(final String defaultVal2) {
			try {
				return Integer.parseInt(defaultVal2);
			} catch (final Exception e) {
				return -1;
			}
		}

		@Override
		public String toString() {
			return this.varname;
		}
	}

	public static int getIntVar(final Configuration conf, final ConfVars var) {
		ConfVars.values();
		assert (var.valClass == Integer.class);
		return conf.getInt(var.varname, var.defaultIntVal);
	}

	public int getIntVar(final ConfVars var) {
		return getIntVar(this, var);
	}

	public static long getLongVar(final Configuration conf, final ConfVars var) {
		assert (var.valClass == Long.class);
		return conf.getLong(var.varname, var.defaultLongVal);
	}

	public long getLongVar(final ConfVars var) {
		return getLongVar(this, var);
	}

	public static float getFloatVar(final Configuration conf, final ConfVars var) {
		assert (var.valClass == Float.class);
		return conf.getFloat(var.varname, var.defaultFloatVal);
	}

	public float getFloatVar(final ConfVars var) {
		return getFloatVar(this, var);
	}

	public static boolean getBoolVar(final Configuration conf,
			final ConfVars var) {
		assert (var.valClass == Boolean.class);
		return conf.getBoolean(var.varname, var.defaultBoolVal);
	}

	public boolean getBoolVar(final ConfVars var) {
		return getBoolVar(this, var);
	}

	public static String getVar(final Configuration conf, final ConfVars var) {
		assert (var.valClass == String.class);
		return conf.get(var.varname, var.defaultVal);
	}

	public static void setVar(final Configuration conf, final ConfVars var,
			final String val) {
		assert (var.valClass == String.class);
		conf.set(var.varname, val);
	}

	public String getVar(final ConfVars var) {
		return getVar(this, var);
	}

	public void setVar(final ConfVars var, final String val) {
		setVar(this, var, val);
	}

	public void logVars(final PrintStream ps) {
		for (final ConfVars one : ConfVars.values()) {
			ps.println(one.varname + "="
					+ ((get(one.varname) != null) ? get(one.varname) : ""));
		}
	}

	public TrcConfiguration() {
		super();
	}

	public TrcConfiguration(final Properties properties) {
		this();
		this.reload(properties);
	}

	private Properties getUnderlyingProps() {
		final Iterator<Map.Entry<String, String>> iter = this.iterator();
		final Properties p = new Properties();
		while (iter.hasNext()) {
			final Map.Entry<String, String> e = iter.next();
			p.setProperty(e.getKey(), e.getValue());
		}
		return p;
	}

	public Properties getAllProperties() {
		return getUnderlyingProps();
	}

	private void reload(final Properties properties) {
		for (final Object key : properties.keySet()) {
			this.set(key.toString(), properties.get(key).toString());
		}
	}
}
