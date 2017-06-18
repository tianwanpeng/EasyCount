package com.tencent.easycount.driver.test;

import java.io.File;
import java.io.IOException;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

public class TestIni {
	public static void main(String[] args) throws InvalidFileFormatException,
			IOException {
		Ini ini = new Ini();
		Config config = new Config();
		config.setMultiOption(true);
		config.setEmptyOption(false);
		config.setGlobalSection(true);
		ini.setConfig(config);
		ini.load(new File("testconfig.ini"));

		for (String seckey : ini.keySet()) {
			Section section = ini.get(seckey);
			for (String key : section.keySet()) {
				System.out.println(seckey + " " + key + " " + section.get(key));
			}
		}
	}
}
