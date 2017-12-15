package com.tencent.easycount.driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.ini4j.Ini;

import com.tencent.easycount.conf.ECConfiguration;
import com.tencent.easycount.driver.TopologyGenerator.EcTopology;
import com.tencent.easycount.exec.io.local.LocalModeUtils;
import com.tencent.easycount.udfnew.MyUDFUtils;

public class Driver {
	private static final String version = "TRC-EC-R0.8";

	public static void main(final String[] args) throws Exception {

		System.out.println("TRC EC version is : " + version);
		// do some initilize work
		MyUDFUtils.initialize();

		/**
		 * initialize conf
		 */
		final ECConfiguration config = ConfigGenerator.generateConfig(args);

		final InputStreamReader reader = new InputStreamReader(
				new FileInputStream(new File(config.get("configfile",
						"inifile/localtest.ini"))), "utf8");
		final Ini ini = new Ini(reader);

		ConfigGenerator.addSysteminfoToConfig(config, ini);

		final String sql = config.get("sql");
		if (sql == null) {
			System.err
			.println("error ::: sql clause must be set, you should specify the configfile .... ");
			System.exit(-1);
		}

		try {
			final EcTopology tt = TopologyGenerator.generateTopology(config,
					ini);

			if (config.getBoolean("check", false)) {
				System.exit(0);
				return;
			}

			if (config.getBoolean("compile", false)) {
				final boolean withRoot = config.getBoolean("with.root", false);
				GraphPrinter.graphPrint(tt.lPlan, tt.pPlan, withRoot);
				return;
			}

			/**
			 * generate topology and submit it
			 */
			final boolean localMode = config.getBoolean("localmode", false);
			if (localMode) {
				LocalModeUtils.setupTables(tt.md, config);
				config.set("moniter.send.status", "false");
			}
			TopologySubmiter.submitTopology(tt.topology,
					config.get("topologyname"), localMode, config,
					config.getInt("work.num", -1));
		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
