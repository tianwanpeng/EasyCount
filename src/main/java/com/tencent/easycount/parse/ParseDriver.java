package com.tencent.easycount.parse;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenRewriteStream;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeAdaptor;

public class ParseDriver {

	public ASTNodeTRC parse(String sql) throws Exception {
		ASTNodeTRC tree = parseInternal(sql);
		return ParseUtils.findRootNonNullToken(tree);
	}

	private ASTNodeTRC parseInternal(String sql) throws Exception {
		TrcLexerX lexer = new TrcLexerX(new ANTLRNoCaseStringStream(sql));
		TokenRewriteStream tokens = new TokenRewriteStream(lexer);
		TrcParserX parser = new TrcParserX(tokens);
		parser.setTreeAdaptor(adaptor);

		return (ASTNodeTRC) parser.statement().getTree();
	}

	public class ANTLRNoCaseStringStream extends ANTLRStringStream {

		public ANTLRNoCaseStringStream(String input) {
			super(input);
		}

		@Override
		public int LA(int i) {

			int returnChar = super.LA(i);
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

		public TrcLexerX(CharStream input) {
			super(input);
		}

	}

	private static final TreeAdaptor adaptor = new CommonTreeAdaptor() {
		@Override
		public ASTNodeTRC create(Token payload) {
			return new ASTNodeTRC(payload);
		}
	};

	public class TrcParserX extends TrcParser {
		public TrcParserX(TokenStream input) {
			super(input);
		}
	}
}
