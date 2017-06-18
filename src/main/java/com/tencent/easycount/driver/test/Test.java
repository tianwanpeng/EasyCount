package com.tencent.easycount.driver.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tencent.easycount.parse.TrcParser;

public class Test {
	public static void main(final String[] args) {
		System.out.println("R3" + TrcParser.Identifier + "%|"
				+ TrcParser.StringLiteral + "%|" + TrcParser.TOK_CHARSETLITERAL
				+ "%|" + TrcParser.TOK_STRINGLITERALSEQUENCE + "%|" + "%|"
				+ TrcParser.KW_IF + "%|" + TrcParser.KW_CASE + "%|"
				+ TrcParser.KW_WHEN + "%|" + TrcParser.KW_IN + "%|"
				+ TrcParser.KW_ARRAY + "%|" + TrcParser.KW_MAP + "%|"
				+ TrcParser.KW_STRUCT + "%");

		final String xxx = "123-2014-23-98afsdf";
		final Matcher mat = Pattern.compile("\\d{4}-\\d{2}-\\d{2}")
				.matcher(xxx);
		System.out.println(mat.find());
		System.out.println(mat.start());
		System.out.println(mat.end());
		System.out.println(xxx.substring(mat.start(), mat.end()));

		test();
		// test1();
		// System.out.println((byte) '0');
		// System.out.println((byte) 'a');
	}

	private static void test() {
		// TODO Auto-generated method stub
		// final DataOutputBuffer obuffer = new DataOutputBuffer();
		// try {
		// obuffer.writeUTF("a, b, 27, 107.0, 404.0,");
		// for (int i = 0; i < obuffer.getLength(); i++) {
		// System.out.println(obuffer.getData()[i]);
		// }
		// } catch (final IOException e) {
		// e.printStackTrace();
		// }
	}
}
