package com.tencent.easycount.parse;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenRewriteStream;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeAdaptor;

import com.tencent.easycount.util.graph.GraphPrinter;

public class ParseDriver {

	public ASTNodeTRC parse(final String sql) throws Exception {
		final ASTNodeTRC tree = parseInternal(sql);
		System.out.println(tree.toStringTree());

		final ASTNodeTRC res = ParseUtils.findRootNonNullToken(tree);
		GraphPrinter.print1(res, null);
		return res;
	}

	private ASTNodeTRC parseInternal(final String sql) throws Exception {
		final TrcLexerX lexer = new TrcLexerX(new ANTLRNoCaseStringStream(sql));
		final TokenRewriteStream tokens = new TokenRewriteStream(lexer);
		final TrcParserX parser = new TrcParserX(tokens);
		parser.setTreeAdaptor(adaptor);

		return (ASTNodeTRC) parser.statement().getTree();
	}

	public class ANTLRNoCaseStringStream extends ANTLRStringStream {

		public ANTLRNoCaseStringStream(final String input) {
			super(input);
		}

		@Override
		public int LA(final int i) {

			final int returnChar = super.LA(i);
			if (returnChar == CharStream.EOF) {
				return returnChar;
			} else if (returnChar == 0) {
				return returnChar;
			}

			return Character.toUpperCase((char) returnChar);
		}
	}

	public class TrcLexerX extends TrcLexer {

		public TrcLexerX() {
			super();
		}

		public TrcLexerX(final CharStream input) {
			super(input);
		}

	}

	private static final TreeAdaptor adaptor = new CommonTreeAdaptor() {
		@Override
		public ASTNodeTRC create(final Token payload) {
			if (payload == null) {
				// System.out.println("payload is null");
				// throw new RuntimeException("payload should not be null...");
			}
			return new ASTNodeTRC(payload);
		}
	};

	public class TrcParserX extends TrcParser {
		public TrcParserX(final TokenStream input) {
			super(input);
		}
	}
}
