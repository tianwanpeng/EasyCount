// $ANTLR 3.5 D:\\svn\\EasyCount\\src\\main\\java\\Trc.g 2014-12-09 13:19:10

package com.tencent.easycount.parse;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.RewriteEarlyExitException;
import org.antlr.runtime.tree.RewriteRuleSubtreeStream;
import org.antlr.runtime.tree.RewriteRuleTokenStream;
import org.antlr.runtime.tree.TreeAdaptor;

@SuppressWarnings("all")
public class TrcParser extends Parser {
	public static final String[] tokenNames = new String[] { "<invalid>",
			"<EOR>", "<DOWN>", "<UP>", "AMPERSAND", "ASSIGN", "BITWISEOR",
			"BITWISEXOR", "BigintLiteral", "COLON", "COMMA", "COMMENT",
			"CharSetLiteral", "CharSetName", "DIV", "DIVIDE", "DOLLAR", "DOT",
			"Digit", "EQUAL", "Exponent", "GREATERTHAN",
			"GREATERTHANOREQUALTO", "HexDigit", "Identifier", "IdentifierRef",
			"KW_ACCU", "KW_ACCUGLOBAL", "KW_ADD", "KW_AFTER", "KW_AGGR",
			"KW_AGGR_TIME", "KW_ALL", "KW_ALTER", "KW_ANALYZE", "KW_AND",
			"KW_ARCHIVE", "KW_ARRAY", "KW_AS", "KW_ASC", "KW_ATTRIBUTES",
			"KW_BEFORE", "KW_BETWEEN", "KW_BIGINT", "KW_BINARY", "KW_BOOLEAN",
			"KW_BOTH", "KW_BUCKET", "KW_BUCKETS", "KW_BY", "KW_CASCADE",
			"KW_CASE", "KW_CAST", "KW_CHANGE", "KW_CLUSTER", "KW_CLUSTERED",
			"KW_CLUSTERSTATUS", "KW_COLLECTION", "KW_COLUMN", "KW_COLUMNS",
			"KW_COMMENT", "KW_COMPUTE", "KW_CONCATENATE", "KW_CONTINUE",
			"KW_COORDINATE", "KW_CREATE", "KW_CROSS", "KW_CURSOR", "KW_DATA",
			"KW_DATABASE", "KW_DATABASES", "KW_DATE", "KW_DATETIME",
			"KW_DBPROPERTIES", "KW_DEFAULT", "KW_DEFERRED", "KW_DEFINE",
			"KW_DELETE", "KW_DELIMITED", "KW_DESC", "KW_DESCRIBE",
			"KW_DIRECTORY", "KW_DISABLE", "KW_DISTINCT", "KW_DISTRIBUTE",
			"KW_DOUBLE", "KW_DROP", "KW_ELEM_TYPE", "KW_ELSE", "KW_EMIT",
			"KW_ENABLE", "KW_END", "KW_ESCAPED", "KW_EXCLUSIVE", "KW_EXECUTE",
			"KW_EXISTS", "KW_EXPAND", "KW_EXPLAIN", "KW_EXPORT", "KW_EXTENDED",
			"KW_EXTERNAL", "KW_FALSE", "KW_FETCH", "KW_FIELDS",
			"KW_FILEFORMAT", "KW_FIRST", "KW_FLOAT", "KW_FOR", "KW_FOREACH",
			"KW_FORMAT", "KW_FORMATTED", "KW_FROM", "KW_FULL", "KW_FUNCTION",
			"KW_FUNCTIONS", "KW_GENERATE", "KW_GENERATEMAP", "KW_GRANT",
			"KW_GROUP", "KW_HAVING", "KW_HOLD_DDLTIME", "KW_IDXPROPERTIES",
			"KW_IF", "KW_IMPORT", "KW_IN", "KW_INDEX", "KW_INDEXES",
			"KW_INNERTABLE", "KW_INPATH", "KW_INPUTDRIVER", "KW_INPUTFORMAT",
			"KW_INSERT", "KW_INT", "KW_INTERSECT", "KW_INTERVAL", "KW_INTO",
			"KW_IS", "KW_ITEMS", "KW_JOIN", "KW_KEY", "KW_KEYS", "KW_KEY_TYPE",
			"KW_LATERAL", "KW_LEFT", "KW_LIKE", "KW_LIMIT", "KW_LINES",
			"KW_LOAD", "KW_LOCAL", "KW_LOCATION", "KW_LOCK", "KW_LOCKS",
			"KW_LONG", "KW_MAP", "KW_MAPJOIN", "KW_MATERIALIZED", "KW_MINUS",
			"KW_MSCK", "KW_NOT", "KW_NO_DROP", "KW_NULL", "KW_OF",
			"KW_OFFLINE", "KW_ON", "KW_OPTION", "KW_OR", "KW_ORDER", "KW_OUT",
			"KW_OUTER", "KW_OUTPUTDRIVER", "KW_OUTPUTFORMAT", "KW_OVERWRITE",
			"KW_PARTITION", "KW_PARTITIONED", "KW_PARTITIONS", "KW_PERCENT",
			"KW_PLUS", "KW_PRESERVE", "KW_PRINTTABLE", "KW_PROCEDURE",
			"KW_PURGE", "KW_RANGE", "KW_RCFILE", "KW_READ", "KW_READONLY",
			"KW_READS", "KW_REBUILD", "KW_RECORDREADER", "KW_RECORDWRITER",
			"KW_REDUCE", "KW_REGEXP", "KW_RENAME", "KW_REPAIR", "KW_REPLACE",
			"KW_RESTRICT", "KW_REVOKE", "KW_RIGHT", "KW_RLIKE", "KW_ROW",
			"KW_SCHEMA", "KW_SCHEMAS", "KW_SECONDS", "KW_SELECT", "KW_SEMI",
			"KW_SEQUENCEFILE", "KW_SERDE", "KW_SERDEPROPERTIES", "KW_SET",
			"KW_SHARED", "KW_SHOW", "KW_SHOW_DATABASE", "KW_SMALLINT",
			"KW_SORT", "KW_SORTED", "KW_SSL", "KW_STATISTICS", "KW_STORED",
			"KW_STREAMTABLE", "KW_STRING", "KW_STRUCT", "KW_SW", "KW_TABLE",
			"KW_TABLES", "KW_TABLESAMPLE", "KW_TBLPROPERTIES", "KW_TEMPORARY",
			"KW_TERMINATED", "KW_TEXTFILE", "KW_THEN", "KW_TIMESTAMP",
			"KW_TINYINT", "KW_TO", "KW_TOUCH", "KW_TRANSFORM", "KW_TRIGGER",
			"KW_TRUE", "KW_UNARCHIVE", "KW_UNDO", "KW_UNION", "KW_UNIONTYPE",
			"KW_UNIQUEJOIN", "KW_UNLOCK", "KW_UNSIGNED", "KW_UPDATE", "KW_USE",
			"KW_USING", "KW_UTC", "KW_UTCTIMESTAMP", "KW_VALUE_TYPE",
			"KW_VIEW", "KW_WHEN", "KW_WHERE", "KW_WHILE", "KW_WITH", "LCURLY",
			"LESSTHAN", "LESSTHANOREQUALTO", "LPAREN", "LSQUARE", "Letter",
			"MINUS", "MOD", "NOTEQUAL", "NOTIN", "NOTLIKE", "NOTREGEXP",
			"NOTRLIKE", "Number", "PLUS", "QUESTION", "RCURLY", "RPAREN",
			"RSQUARE", "RegexComponent", "SEMICOLON", "STAR",
			"SmallintLiteral", "StringLiteral", "TILDE", "TOK_ACCU_INTERVAL",
			"TOK_AGGR_INTERVAL", "TOK_ALIASLIST", "TOK_ALLCOLREF",
			"TOK_ALTERDATABASE_PROPERTIES", "TOK_ALTERINDEX_PROPERTIES",
			"TOK_ALTERINDEX_REBUILD", "TOK_ALTERTABLE_ADDCOLS",
			"TOK_ALTERTABLE_ADDPARTS", "TOK_ALTERTABLE_ALTERPARTS_MERGEFILES",
			"TOK_ALTERTABLE_ALTERPARTS_PROTECTMODE", "TOK_ALTERTABLE_ARCHIVE",
			"TOK_ALTERTABLE_CHANGECOL_AFTER_POSITION",
			"TOK_ALTERTABLE_CLUSTER_SORT", "TOK_ALTERTABLE_DROPPARTS",
			"TOK_ALTERTABLE_FILEFORMAT", "TOK_ALTERTABLE_LOCATION",
			"TOK_ALTERTABLE_PARTITION", "TOK_ALTERTABLE_PROPERTIES",
			"TOK_ALTERTABLE_RENAME", "TOK_ALTERTABLE_RENAMECOL",
			"TOK_ALTERTABLE_RENAMEPART", "TOK_ALTERTABLE_REPLACECOLS",
			"TOK_ALTERTABLE_SERDEPROPERTIES", "TOK_ALTERTABLE_SERIALIZER",
			"TOK_ALTERTABLE_TOUCH", "TOK_ALTERTABLE_UNARCHIVE",
			"TOK_ALTERVIEW_ADDPARTS", "TOK_ALTERVIEW_DROPPARTS",
			"TOK_ALTERVIEW_PROPERTIES", "TOK_ALTERVIEW_RENAME", "TOK_ANALYZE",
			"TOK_ARRAY", "TOK_ASSIGN", "TOK_ATTRBUTES", "TOK_BIGINT",
			"TOK_BINARY", "TOK_BOOLEAN", "TOK_CASCADE", "TOK_CHARSETLITERAL",
			"TOK_CLUSTERBY", "TOK_COLTYPELIST", "TOK_COORDINATE",
			"TOK_COORDINATE_EXPR", "TOK_CREATEDATABASE", "TOK_CREATEFUNCTION",
			"TOK_CREATEINDEX", "TOK_CREATEINDEX_INDEXTBLNAME",
			"TOK_CREATEROLE", "TOK_CREATETABLE", "TOK_CREATEVIEW",
			"TOK_DATABASECOMMENT", "TOK_DATABASELOCATION",
			"TOK_DATABASEPROPERTIES", "TOK_DATE", "TOK_DATETIME",
			"TOK_DBPROPLIST", "TOK_DEFERRED_REBUILDINDEX", "TOK_DEFINE",
			"TOK_DESCDATABASE", "TOK_DESCFUNCTION", "TOK_DESCTABLE",
			"TOK_DESTINATION", "TOK_DIR", "TOK_DISABLE", "TOK_DISTRIBUTEBY",
			"TOK_DOUBLE", "TOK_DROPDATABASE", "TOK_DROPFUNCTION",
			"TOK_DROPINDEX", "TOK_DROPROLE", "TOK_DROPTABLE", "TOK_DROPVIEW",
			"TOK_EMIT", "TOK_ENABLE", "TOK_EXECUTE", "TOK_EXECUTEBLOCK",
			"TOK_EXPLAIN", "TOK_EXPLIST", "TOK_EXPORT", "TOK_FALSE",
			"TOK_FILEFORMAT_GENERIC", "TOK_FLOAT", "TOK_FOR", "TOK_FOREACH",
			"TOK_FROM", "TOK_FULLOUTERJOIN", "TOK_FUNCTION",
			"TOK_FUNCTIONACCU", "TOK_FUNCTIONACCUDI", "TOK_FUNCTIONDI",
			"TOK_FUNCTIONSTAR", "TOK_FUNCTIONSW", "TOK_FUNCTIONSWDI",
			"TOK_FUNCTIONUPDATE", "TOK_GENERATE", "TOK_GENERATEMAP",
			"TOK_GRANT", "TOK_GRANT_ROLE", "TOK_GRANT_WITH_OPTION",
			"TOK_GROUP", "TOK_GROUPBY", "TOK_HAVING", "TOK_HINT",
			"TOK_HINTARGLIST", "TOK_HINTLIST", "TOK_HOLD_DDLTIME", "TOK_IF",
			"TOK_IFEXISTS", "TOK_IFNOTEXISTS", "TOK_IMPORT", "TOK_IN",
			"TOK_INDEXCOMMENT", "TOK_INDEXPROPERTIES", "TOK_INDEXPROPLIST",
			"TOK_INSERT", "TOK_INSERT_INTO", "TOK_INSERT_QUERY", "TOK_INT",
			"TOK_ISNOTNULL", "TOK_ISNULL", "TOK_JOIN", "TOK_KEY",
			"TOK_LATERAL_VIEW", "TOK_LEFTOUTERJOIN", "TOK_LEFTSEMIJOIN",
			"TOK_LIKETABLE", "TOK_LIMIT", "TOK_LOAD", "TOK_LOCAL_DIR",
			"TOK_LOCKTABLE", "TOK_MAP", "TOK_MAPJOIN", "TOK_MSCK",
			"TOK_NO_DROP", "TOK_NULL", "TOK_OF", "TOK_OFFLINE", "TOK_ON",
			"TOK_OP_ADD", "TOK_OP_AND", "TOK_OP_BITAND", "TOK_OP_BITNOT",
			"TOK_OP_BITOR", "TOK_OP_BITXOR", "TOK_OP_DIV", "TOK_OP_EQ",
			"TOK_OP_GE", "TOK_OP_GT", "TOK_OP_LE", "TOK_OP_LIKE", "TOK_OP_LT",
			"TOK_OP_MOD", "TOK_OP_MUL", "TOK_OP_NE", "TOK_OP_NOT", "TOK_OP_OR",
			"TOK_OP_SUB", "TOK_ORDERBY", "TOK_ORREPLACE",
			"TOK_PARTITIONLOCATION", "TOK_PARTSPEC", "TOK_PARTVAL",
			"TOK_PRINCIPAL_NAME", "TOK_PRIVILEGE", "TOK_PRIVILEGE_LIST",
			"TOK_PRIV_ALL", "TOK_PRIV_ALTER_DATA", "TOK_PRIV_ALTER_METADATA",
			"TOK_PRIV_CREATE", "TOK_PRIV_DROP", "TOK_PRIV_INDEX",
			"TOK_PRIV_LOCK", "TOK_PRIV_OBJECT", "TOK_PRIV_OBJECT_COL",
			"TOK_PRIV_SELECT", "TOK_PRIV_SHOW_DATABASE", "TOK_QUERY",
			"TOK_READONLY", "TOK_RECORDREADER", "TOK_RECORDWRITER",
			"TOK_RESTRICT", "TOK_REVOKE", "TOK_REVOKE_ROLE",
			"TOK_RIGHTOUTERJOIN", "TOK_ROLE", "TOK_ROOT", "TOK_ROOT_QUERY",
			"TOK_SELECT", "TOK_SELECTDI", "TOK_SELEXPR", "TOK_SERDE",
			"TOK_SERDENAME", "TOK_SERDEPROPS", "TOK_SHOWDATABASES",
			"TOK_SHOWFUNCTIONS", "TOK_SHOWINDEXES", "TOK_SHOWLOCKS",
			"TOK_SHOWPARTITIONS", "TOK_SHOWTABLES", "TOK_SHOW_GRANT",
			"TOK_SHOW_ROLE_GRANT", "TOK_SHOW_TABLESTATUS", "TOK_SMALLINT",
			"TOK_SORTBY", "TOK_STORAGEHANDLER", "TOK_STREAMTABLE",
			"TOK_STRING", "TOK_STRINGLITERALSEQUENCE", "TOK_STRUCT",
			"TOK_STRUCTUNIT", "TOK_SUBQUERY", "TOK_SWITCHDATABASE",
			"TOK_SW_INTERVAL", "TOK_TAB", "TOK_TABALIAS", "TOK_TABCOL",
			"TOK_TABCOLLIST", "TOK_TABCOLNAME", "TOK_TABLEBUCKETS",
			"TOK_TABLEBUCKETSAMPLE", "TOK_TABLECOMMENT", "TOK_TABLEFILEFORMAT",
			"TOK_TABLELOCATION", "TOK_TABLEPARTCOLS", "TOK_TABLEPROPERTIES",
			"TOK_TABLEPROPERTY", "TOK_TABLEPROPLIST", "TOK_TABLEROWFORMAT",
			"TOK_TABLEROWFORMATCOLLITEMS", "TOK_TABLEROWFORMATFIELD",
			"TOK_TABLEROWFORMATLINES", "TOK_TABLEROWFORMATMAPKEYS",
			"TOK_TABLESERIALIZER", "TOK_TABLESPLITSAMPLE", "TOK_TABLE_OR_COL",
			"TOK_TABLE_OR_COL_REF", "TOK_TABLE_PARTITION", "TOK_TABNAME",
			"TOK_TABREF", "TOK_TABSORTCOLNAMEASC", "TOK_TABSORTCOLNAMEDESC",
			"TOK_TABSRC", "TOK_TABTYPE", "TOK_TBLRCFILE",
			"TOK_TBLSEQUENCEFILE", "TOK_TBLTEXTFILE", "TOK_TIMESTAMP",
			"TOK_TINYINT", "TOK_TMP_FILE", "TOK_TRANSFORM", "TOK_TRUE",
			"TOK_UNION", "TOK_UNIONTYPE", "TOK_UNIQUEJOIN", "TOK_UNLOCKTABLE",
			"TOK_USER", "TOK_USERSCRIPTCOLNAMES", "TOK_USERSCRIPTCOLSCHEMA",
			"TOK_VAR", "TOK_VIEWPARTCOLS", "TOK_WHERE", "TOK_WITH",
			"TinyintLiteral", "WS" };
	public static final int EOF = -1;
	public static final int AMPERSAND = 4;
	public static final int ASSIGN = 5;
	public static final int BITWISEOR = 6;
	public static final int BITWISEXOR = 7;
	public static final int BigintLiteral = 8;
	public static final int COLON = 9;
	public static final int COMMA = 10;
	public static final int COMMENT = 11;
	public static final int CharSetLiteral = 12;
	public static final int CharSetName = 13;
	public static final int DIV = 14;
	public static final int DIVIDE = 15;
	public static final int DOLLAR = 16;
	public static final int DOT = 17;
	public static final int Digit = 18;
	public static final int EQUAL = 19;
	public static final int Exponent = 20;
	public static final int GREATERTHAN = 21;
	public static final int GREATERTHANOREQUALTO = 22;
	public static final int HexDigit = 23;
	public static final int Identifier = 24;
	public static final int IdentifierRef = 25;
	public static final int KW_ACCU = 26;
	public static final int KW_ACCUGLOBAL = 27;
	public static final int KW_ADD = 28;
	public static final int KW_AFTER = 29;
	public static final int KW_AGGR = 30;
	public static final int KW_AGGR_TIME = 31;
	public static final int KW_ALL = 32;
	public static final int KW_ALTER = 33;
	public static final int KW_ANALYZE = 34;
	public static final int KW_AND = 35;
	public static final int KW_ARCHIVE = 36;
	public static final int KW_ARRAY = 37;
	public static final int KW_AS = 38;
	public static final int KW_ASC = 39;
	public static final int KW_ATTRIBUTES = 40;
	public static final int KW_BEFORE = 41;
	public static final int KW_BETWEEN = 42;
	public static final int KW_BIGINT = 43;
	public static final int KW_BINARY = 44;
	public static final int KW_BOOLEAN = 45;
	public static final int KW_BOTH = 46;
	public static final int KW_BUCKET = 47;
	public static final int KW_BUCKETS = 48;
	public static final int KW_BY = 49;
	public static final int KW_CASCADE = 50;
	public static final int KW_CASE = 51;
	public static final int KW_CAST = 52;
	public static final int KW_CHANGE = 53;
	public static final int KW_CLUSTER = 54;
	public static final int KW_CLUSTERED = 55;
	public static final int KW_CLUSTERSTATUS = 56;
	public static final int KW_COLLECTION = 57;
	public static final int KW_COLUMN = 58;
	public static final int KW_COLUMNS = 59;
	public static final int KW_COMMENT = 60;
	public static final int KW_COMPUTE = 61;
	public static final int KW_CONCATENATE = 62;
	public static final int KW_CONTINUE = 63;
	public static final int KW_COORDINATE = 64;
	public static final int KW_CREATE = 65;
	public static final int KW_CROSS = 66;
	public static final int KW_CURSOR = 67;
	public static final int KW_DATA = 68;
	public static final int KW_DATABASE = 69;
	public static final int KW_DATABASES = 70;
	public static final int KW_DATE = 71;
	public static final int KW_DATETIME = 72;
	public static final int KW_DBPROPERTIES = 73;
	public static final int KW_DEFAULT = 74;
	public static final int KW_DEFERRED = 75;
	public static final int KW_DEFINE = 76;
	public static final int KW_DELETE = 77;
	public static final int KW_DELIMITED = 78;
	public static final int KW_DESC = 79;
	public static final int KW_DESCRIBE = 80;
	public static final int KW_DIRECTORY = 81;
	public static final int KW_DISABLE = 82;
	public static final int KW_DISTINCT = 83;
	public static final int KW_DISTRIBUTE = 84;
	public static final int KW_DOUBLE = 85;
	public static final int KW_DROP = 86;
	public static final int KW_ELEM_TYPE = 87;
	public static final int KW_ELSE = 88;
	public static final int KW_EMIT = 89;
	public static final int KW_ENABLE = 90;
	public static final int KW_END = 91;
	public static final int KW_ESCAPED = 92;
	public static final int KW_EXCLUSIVE = 93;
	public static final int KW_EXECUTE = 94;
	public static final int KW_EXISTS = 95;
	public static final int KW_EXPAND = 96;
	public static final int KW_EXPLAIN = 97;
	public static final int KW_EXPORT = 98;
	public static final int KW_EXTENDED = 99;
	public static final int KW_EXTERNAL = 100;
	public static final int KW_FALSE = 101;
	public static final int KW_FETCH = 102;
	public static final int KW_FIELDS = 103;
	public static final int KW_FILEFORMAT = 104;
	public static final int KW_FIRST = 105;
	public static final int KW_FLOAT = 106;
	public static final int KW_FOR = 107;
	public static final int KW_FOREACH = 108;
	public static final int KW_FORMAT = 109;
	public static final int KW_FORMATTED = 110;
	public static final int KW_FROM = 111;
	public static final int KW_FULL = 112;
	public static final int KW_FUNCTION = 113;
	public static final int KW_FUNCTIONS = 114;
	public static final int KW_GENERATE = 115;
	public static final int KW_GENERATEMAP = 116;
	public static final int KW_GRANT = 117;
	public static final int KW_GROUP = 118;
	public static final int KW_HAVING = 119;
	public static final int KW_HOLD_DDLTIME = 120;
	public static final int KW_IDXPROPERTIES = 121;
	public static final int KW_IF = 122;
	public static final int KW_IMPORT = 123;
	public static final int KW_IN = 124;
	public static final int KW_INDEX = 125;
	public static final int KW_INDEXES = 126;
	public static final int KW_INNERTABLE = 127;
	public static final int KW_INPATH = 128;
	public static final int KW_INPUTDRIVER = 129;
	public static final int KW_INPUTFORMAT = 130;
	public static final int KW_INSERT = 131;
	public static final int KW_INT = 132;
	public static final int KW_INTERSECT = 133;
	public static final int KW_INTERVAL = 134;
	public static final int KW_INTO = 135;
	public static final int KW_IS = 136;
	public static final int KW_ITEMS = 137;
	public static final int KW_JOIN = 138;
	public static final int KW_KEY = 139;
	public static final int KW_KEYS = 140;
	public static final int KW_KEY_TYPE = 141;
	public static final int KW_LATERAL = 142;
	public static final int KW_LEFT = 143;
	public static final int KW_LIKE = 144;
	public static final int KW_LIMIT = 145;
	public static final int KW_LINES = 146;
	public static final int KW_LOAD = 147;
	public static final int KW_LOCAL = 148;
	public static final int KW_LOCATION = 149;
	public static final int KW_LOCK = 150;
	public static final int KW_LOCKS = 151;
	public static final int KW_LONG = 152;
	public static final int KW_MAP = 153;
	public static final int KW_MAPJOIN = 154;
	public static final int KW_MATERIALIZED = 155;
	public static final int KW_MINUS = 156;
	public static final int KW_MSCK = 157;
	public static final int KW_NOT = 158;
	public static final int KW_NO_DROP = 159;
	public static final int KW_NULL = 160;
	public static final int KW_OF = 161;
	public static final int KW_OFFLINE = 162;
	public static final int KW_ON = 163;
	public static final int KW_OPTION = 164;
	public static final int KW_OR = 165;
	public static final int KW_ORDER = 166;
	public static final int KW_OUT = 167;
	public static final int KW_OUTER = 168;
	public static final int KW_OUTPUTDRIVER = 169;
	public static final int KW_OUTPUTFORMAT = 170;
	public static final int KW_OVERWRITE = 171;
	public static final int KW_PARTITION = 172;
	public static final int KW_PARTITIONED = 173;
	public static final int KW_PARTITIONS = 174;
	public static final int KW_PERCENT = 175;
	public static final int KW_PLUS = 176;
	public static final int KW_PRESERVE = 177;
	public static final int KW_PRINTTABLE = 178;
	public static final int KW_PROCEDURE = 179;
	public static final int KW_PURGE = 180;
	public static final int KW_RANGE = 181;
	public static final int KW_RCFILE = 182;
	public static final int KW_READ = 183;
	public static final int KW_READONLY = 184;
	public static final int KW_READS = 185;
	public static final int KW_REBUILD = 186;
	public static final int KW_RECORDREADER = 187;
	public static final int KW_RECORDWRITER = 188;
	public static final int KW_REDUCE = 189;
	public static final int KW_REGEXP = 190;
	public static final int KW_RENAME = 191;
	public static final int KW_REPAIR = 192;
	public static final int KW_REPLACE = 193;
	public static final int KW_RESTRICT = 194;
	public static final int KW_REVOKE = 195;
	public static final int KW_RIGHT = 196;
	public static final int KW_RLIKE = 197;
	public static final int KW_ROW = 198;
	public static final int KW_SCHEMA = 199;
	public static final int KW_SCHEMAS = 200;
	public static final int KW_SECONDS = 201;
	public static final int KW_SELECT = 202;
	public static final int KW_SEMI = 203;
	public static final int KW_SEQUENCEFILE = 204;
	public static final int KW_SERDE = 205;
	public static final int KW_SERDEPROPERTIES = 206;
	public static final int KW_SET = 207;
	public static final int KW_SHARED = 208;
	public static final int KW_SHOW = 209;
	public static final int KW_SHOW_DATABASE = 210;
	public static final int KW_SMALLINT = 211;
	public static final int KW_SORT = 212;
	public static final int KW_SORTED = 213;
	public static final int KW_SSL = 214;
	public static final int KW_STATISTICS = 215;
	public static final int KW_STORED = 216;
	public static final int KW_STREAMTABLE = 217;
	public static final int KW_STRING = 218;
	public static final int KW_STRUCT = 219;
	public static final int KW_SW = 220;
	public static final int KW_TABLE = 221;
	public static final int KW_TABLES = 222;
	public static final int KW_TABLESAMPLE = 223;
	public static final int KW_TBLPROPERTIES = 224;
	public static final int KW_TEMPORARY = 225;
	public static final int KW_TERMINATED = 226;
	public static final int KW_TEXTFILE = 227;
	public static final int KW_THEN = 228;
	public static final int KW_TIMESTAMP = 229;
	public static final int KW_TINYINT = 230;
	public static final int KW_TO = 231;
	public static final int KW_TOUCH = 232;
	public static final int KW_TRANSFORM = 233;
	public static final int KW_TRIGGER = 234;
	public static final int KW_TRUE = 235;
	public static final int KW_UNARCHIVE = 236;
	public static final int KW_UNDO = 237;
	public static final int KW_UNION = 238;
	public static final int KW_UNIONTYPE = 239;
	public static final int KW_UNIQUEJOIN = 240;
	public static final int KW_UNLOCK = 241;
	public static final int KW_UNSIGNED = 242;
	public static final int KW_UPDATE = 243;
	public static final int KW_USE = 244;
	public static final int KW_USING = 245;
	public static final int KW_UTC = 246;
	public static final int KW_UTCTIMESTAMP = 247;
	public static final int KW_VALUE_TYPE = 248;
	public static final int KW_VIEW = 249;
	public static final int KW_WHEN = 250;
	public static final int KW_WHERE = 251;
	public static final int KW_WHILE = 252;
	public static final int KW_WITH = 253;
	public static final int LCURLY = 254;
	public static final int LESSTHAN = 255;
	public static final int LESSTHANOREQUALTO = 256;
	public static final int LPAREN = 257;
	public static final int LSQUARE = 258;
	public static final int Letter = 259;
	public static final int MINUS = 260;
	public static final int MOD = 261;
	public static final int NOTEQUAL = 262;
	public static final int NOTIN = 263;
	public static final int NOTLIKE = 264;
	public static final int NOTREGEXP = 265;
	public static final int NOTRLIKE = 266;
	public static final int Number = 267;
	public static final int PLUS = 268;
	public static final int QUESTION = 269;
	public static final int RCURLY = 270;
	public static final int RPAREN = 271;
	public static final int RSQUARE = 272;
	public static final int RegexComponent = 273;
	public static final int SEMICOLON = 274;
	public static final int STAR = 275;
	public static final int SmallintLiteral = 276;
	public static final int StringLiteral = 277;
	public static final int TILDE = 278;
	public static final int TOK_ACCU_INTERVAL = 279;
	public static final int TOK_AGGR_INTERVAL = 280;
	public static final int TOK_ALIASLIST = 281;
	public static final int TOK_ALLCOLREF = 282;
	public static final int TOK_ALTERDATABASE_PROPERTIES = 283;
	public static final int TOK_ALTERINDEX_PROPERTIES = 284;
	public static final int TOK_ALTERINDEX_REBUILD = 285;
	public static final int TOK_ALTERTABLE_ADDCOLS = 286;
	public static final int TOK_ALTERTABLE_ADDPARTS = 287;
	public static final int TOK_ALTERTABLE_ALTERPARTS_MERGEFILES = 288;
	public static final int TOK_ALTERTABLE_ALTERPARTS_PROTECTMODE = 289;
	public static final int TOK_ALTERTABLE_ARCHIVE = 290;
	public static final int TOK_ALTERTABLE_CHANGECOL_AFTER_POSITION = 291;
	public static final int TOK_ALTERTABLE_CLUSTER_SORT = 292;
	public static final int TOK_ALTERTABLE_DROPPARTS = 293;
	public static final int TOK_ALTERTABLE_FILEFORMAT = 294;
	public static final int TOK_ALTERTABLE_LOCATION = 295;
	public static final int TOK_ALTERTABLE_PARTITION = 296;
	public static final int TOK_ALTERTABLE_PROPERTIES = 297;
	public static final int TOK_ALTERTABLE_RENAME = 298;
	public static final int TOK_ALTERTABLE_RENAMECOL = 299;
	public static final int TOK_ALTERTABLE_RENAMEPART = 300;
	public static final int TOK_ALTERTABLE_REPLACECOLS = 301;
	public static final int TOK_ALTERTABLE_SERDEPROPERTIES = 302;
	public static final int TOK_ALTERTABLE_SERIALIZER = 303;
	public static final int TOK_ALTERTABLE_TOUCH = 304;
	public static final int TOK_ALTERTABLE_UNARCHIVE = 305;
	public static final int TOK_ALTERVIEW_ADDPARTS = 306;
	public static final int TOK_ALTERVIEW_DROPPARTS = 307;
	public static final int TOK_ALTERVIEW_PROPERTIES = 308;
	public static final int TOK_ALTERVIEW_RENAME = 309;
	public static final int TOK_ANALYZE = 310;
	public static final int TOK_ARRAY = 311;
	public static final int TOK_ASSIGN = 312;
	public static final int TOK_ATTRBUTES = 313;
	public static final int TOK_BIGINT = 314;
	public static final int TOK_BINARY = 315;
	public static final int TOK_BOOLEAN = 316;
	public static final int TOK_CASCADE = 317;
	public static final int TOK_CHARSETLITERAL = 318;
	public static final int TOK_CLUSTERBY = 319;
	public static final int TOK_COLTYPELIST = 320;
	public static final int TOK_COORDINATE = 321;
	public static final int TOK_COORDINATE_EXPR = 322;
	public static final int TOK_CREATEDATABASE = 323;
	public static final int TOK_CREATEFUNCTION = 324;
	public static final int TOK_CREATEINDEX = 325;
	public static final int TOK_CREATEINDEX_INDEXTBLNAME = 326;
	public static final int TOK_CREATEROLE = 327;
	public static final int TOK_CREATETABLE = 328;
	public static final int TOK_CREATEVIEW = 329;
	public static final int TOK_DATABASECOMMENT = 330;
	public static final int TOK_DATABASELOCATION = 331;
	public static final int TOK_DATABASEPROPERTIES = 332;
	public static final int TOK_DATE = 333;
	public static final int TOK_DATETIME = 334;
	public static final int TOK_DBPROPLIST = 335;
	public static final int TOK_DEFERRED_REBUILDINDEX = 336;
	public static final int TOK_DEFINE = 337;
	public static final int TOK_DESCDATABASE = 338;
	public static final int TOK_DESCFUNCTION = 339;
	public static final int TOK_DESCTABLE = 340;
	public static final int TOK_DESTINATION = 341;
	public static final int TOK_DIR = 342;
	public static final int TOK_DISABLE = 343;
	public static final int TOK_DISTRIBUTEBY = 344;
	public static final int TOK_DOUBLE = 345;
	public static final int TOK_DROPDATABASE = 346;
	public static final int TOK_DROPFUNCTION = 347;
	public static final int TOK_DROPINDEX = 348;
	public static final int TOK_DROPROLE = 349;
	public static final int TOK_DROPTABLE = 350;
	public static final int TOK_DROPVIEW = 351;
	public static final int TOK_EMIT = 352;
	public static final int TOK_ENABLE = 353;
	public static final int TOK_EXECUTE = 354;
	public static final int TOK_EXECUTEBLOCK = 355;
	public static final int TOK_EXPLAIN = 356;
	public static final int TOK_EXPLIST = 357;
	public static final int TOK_EXPORT = 358;
	public static final int TOK_FALSE = 359;
	public static final int TOK_FILEFORMAT_GENERIC = 360;
	public static final int TOK_FLOAT = 361;
	public static final int TOK_FOR = 362;
	public static final int TOK_FOREACH = 363;
	public static final int TOK_FROM = 364;
	public static final int TOK_FULLOUTERJOIN = 365;
	public static final int TOK_FUNCTION = 366;
	public static final int TOK_FUNCTIONACCU = 367;
	public static final int TOK_FUNCTIONACCUDI = 368;
	public static final int TOK_FUNCTIONDI = 369;
	public static final int TOK_FUNCTIONSTAR = 370;
	public static final int TOK_FUNCTIONSW = 371;
	public static final int TOK_FUNCTIONSWDI = 372;
	public static final int TOK_FUNCTIONUPDATE = 373;
	public static final int TOK_GENERATE = 374;
	public static final int TOK_GENERATEMAP = 375;
	public static final int TOK_GRANT = 376;
	public static final int TOK_GRANT_ROLE = 377;
	public static final int TOK_GRANT_WITH_OPTION = 378;
	public static final int TOK_GROUP = 379;
	public static final int TOK_GROUPBY = 380;
	public static final int TOK_HAVING = 381;
	public static final int TOK_HINT = 382;
	public static final int TOK_HINTARGLIST = 383;
	public static final int TOK_HINTLIST = 384;
	public static final int TOK_HOLD_DDLTIME = 385;
	public static final int TOK_IF = 386;
	public static final int TOK_IFEXISTS = 387;
	public static final int TOK_IFNOTEXISTS = 388;
	public static final int TOK_IMPORT = 389;
	public static final int TOK_IN = 390;
	public static final int TOK_INDEXCOMMENT = 391;
	public static final int TOK_INDEXPROPERTIES = 392;
	public static final int TOK_INDEXPROPLIST = 393;
	public static final int TOK_INSERT = 394;
	public static final int TOK_INSERT_INTO = 395;
	public static final int TOK_INSERT_QUERY = 396;
	public static final int TOK_INT = 397;
	public static final int TOK_ISNOTNULL = 398;
	public static final int TOK_ISNULL = 399;
	public static final int TOK_JOIN = 400;
	public static final int TOK_KEY = 401;
	public static final int TOK_LATERAL_VIEW = 402;
	public static final int TOK_LEFTOUTERJOIN = 403;
	public static final int TOK_LEFTSEMIJOIN = 404;
	public static final int TOK_LIKETABLE = 405;
	public static final int TOK_LIMIT = 406;
	public static final int TOK_LOAD = 407;
	public static final int TOK_LOCAL_DIR = 408;
	public static final int TOK_LOCKTABLE = 409;
	public static final int TOK_MAP = 410;
	public static final int TOK_MAPJOIN = 411;
	public static final int TOK_MSCK = 412;
	public static final int TOK_NO_DROP = 413;
	public static final int TOK_NULL = 414;
	public static final int TOK_OF = 415;
	public static final int TOK_OFFLINE = 416;
	public static final int TOK_ON = 417;
	public static final int TOK_OP_ADD = 418;
	public static final int TOK_OP_AND = 419;
	public static final int TOK_OP_BITAND = 420;
	public static final int TOK_OP_BITNOT = 421;
	public static final int TOK_OP_BITOR = 422;
	public static final int TOK_OP_BITXOR = 423;
	public static final int TOK_OP_DIV = 424;
	public static final int TOK_OP_EQ = 425;
	public static final int TOK_OP_GE = 426;
	public static final int TOK_OP_GT = 427;
	public static final int TOK_OP_LE = 428;
	public static final int TOK_OP_LIKE = 429;
	public static final int TOK_OP_LT = 430;
	public static final int TOK_OP_MOD = 431;
	public static final int TOK_OP_MUL = 432;
	public static final int TOK_OP_NE = 433;
	public static final int TOK_OP_NOT = 434;
	public static final int TOK_OP_OR = 435;
	public static final int TOK_OP_SUB = 436;
	public static final int TOK_ORDERBY = 437;
	public static final int TOK_ORREPLACE = 438;
	public static final int TOK_PARTITIONLOCATION = 439;
	public static final int TOK_PARTSPEC = 440;
	public static final int TOK_PARTVAL = 441;
	public static final int TOK_PRINCIPAL_NAME = 442;
	public static final int TOK_PRIVILEGE = 443;
	public static final int TOK_PRIVILEGE_LIST = 444;
	public static final int TOK_PRIV_ALL = 445;
	public static final int TOK_PRIV_ALTER_DATA = 446;
	public static final int TOK_PRIV_ALTER_METADATA = 447;
	public static final int TOK_PRIV_CREATE = 448;
	public static final int TOK_PRIV_DROP = 449;
	public static final int TOK_PRIV_INDEX = 450;
	public static final int TOK_PRIV_LOCK = 451;
	public static final int TOK_PRIV_OBJECT = 452;
	public static final int TOK_PRIV_OBJECT_COL = 453;
	public static final int TOK_PRIV_SELECT = 454;
	public static final int TOK_PRIV_SHOW_DATABASE = 455;
	public static final int TOK_QUERY = 456;
	public static final int TOK_READONLY = 457;
	public static final int TOK_RECORDREADER = 458;
	public static final int TOK_RECORDWRITER = 459;
	public static final int TOK_RESTRICT = 460;
	public static final int TOK_REVOKE = 461;
	public static final int TOK_REVOKE_ROLE = 462;
	public static final int TOK_RIGHTOUTERJOIN = 463;
	public static final int TOK_ROLE = 464;
	public static final int TOK_ROOT = 465;
	public static final int TOK_ROOT_QUERY = 466;
	public static final int TOK_SELECT = 467;
	public static final int TOK_SELECTDI = 468;
	public static final int TOK_SELEXPR = 469;
	public static final int TOK_SERDE = 470;
	public static final int TOK_SERDENAME = 471;
	public static final int TOK_SERDEPROPS = 472;
	public static final int TOK_SHOWDATABASES = 473;
	public static final int TOK_SHOWFUNCTIONS = 474;
	public static final int TOK_SHOWINDEXES = 475;
	public static final int TOK_SHOWLOCKS = 476;
	public static final int TOK_SHOWPARTITIONS = 477;
	public static final int TOK_SHOWTABLES = 478;
	public static final int TOK_SHOW_GRANT = 479;
	public static final int TOK_SHOW_ROLE_GRANT = 480;
	public static final int TOK_SHOW_TABLESTATUS = 481;
	public static final int TOK_SMALLINT = 482;
	public static final int TOK_SORTBY = 483;
	public static final int TOK_STORAGEHANDLER = 484;
	public static final int TOK_STREAMTABLE = 485;
	public static final int TOK_STRING = 486;
	public static final int TOK_STRINGLITERALSEQUENCE = 487;
	public static final int TOK_STRUCT = 488;
	public static final int TOK_STRUCTUNIT = 489;
	public static final int TOK_SUBQUERY = 490;
	public static final int TOK_SWITCHDATABASE = 491;
	public static final int TOK_SW_INTERVAL = 492;
	public static final int TOK_TAB = 493;
	public static final int TOK_TABALIAS = 494;
	public static final int TOK_TABCOL = 495;
	public static final int TOK_TABCOLLIST = 496;
	public static final int TOK_TABCOLNAME = 497;
	public static final int TOK_TABLEBUCKETS = 498;
	public static final int TOK_TABLEBUCKETSAMPLE = 499;
	public static final int TOK_TABLECOMMENT = 500;
	public static final int TOK_TABLEFILEFORMAT = 501;
	public static final int TOK_TABLELOCATION = 502;
	public static final int TOK_TABLEPARTCOLS = 503;
	public static final int TOK_TABLEPROPERTIES = 504;
	public static final int TOK_TABLEPROPERTY = 505;
	public static final int TOK_TABLEPROPLIST = 506;
	public static final int TOK_TABLEROWFORMAT = 507;
	public static final int TOK_TABLEROWFORMATCOLLITEMS = 508;
	public static final int TOK_TABLEROWFORMATFIELD = 509;
	public static final int TOK_TABLEROWFORMATLINES = 510;
	public static final int TOK_TABLEROWFORMATMAPKEYS = 511;
	public static final int TOK_TABLESERIALIZER = 512;
	public static final int TOK_TABLESPLITSAMPLE = 513;
	public static final int TOK_TABLE_OR_COL = 514;
	public static final int TOK_TABLE_OR_COL_REF = 515;
	public static final int TOK_TABLE_PARTITION = 516;
	public static final int TOK_TABNAME = 517;
	public static final int TOK_TABREF = 518;
	public static final int TOK_TABSORTCOLNAMEASC = 519;
	public static final int TOK_TABSORTCOLNAMEDESC = 520;
	public static final int TOK_TABSRC = 521;
	public static final int TOK_TABTYPE = 522;
	public static final int TOK_TBLRCFILE = 523;
	public static final int TOK_TBLSEQUENCEFILE = 524;
	public static final int TOK_TBLTEXTFILE = 525;
	public static final int TOK_TIMESTAMP = 526;
	public static final int TOK_TINYINT = 527;
	public static final int TOK_TMP_FILE = 528;
	public static final int TOK_TRANSFORM = 529;
	public static final int TOK_TRUE = 530;
	public static final int TOK_UNION = 531;
	public static final int TOK_UNIONTYPE = 532;
	public static final int TOK_UNIQUEJOIN = 533;
	public static final int TOK_UNLOCKTABLE = 534;
	public static final int TOK_USER = 535;
	public static final int TOK_USERSCRIPTCOLNAMES = 536;
	public static final int TOK_USERSCRIPTCOLSCHEMA = 537;
	public static final int TOK_VAR = 538;
	public static final int TOK_VIEWPARTCOLS = 539;
	public static final int TOK_WHERE = 540;
	public static final int TOK_WITH = 541;
	public static final int TinyintLiteral = 542;
	public static final int WS = 543;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators

	public TrcParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}

	public TrcParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}

	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}

	@Override
	public String[] getTokenNames() {
		return TrcParser.tokenNames;
	}

	@Override
	public String getGrammarFileName() {
		return "D:\\svn\\EasyCount\\src\\main\\java\\Trc.g";
	}

	public static class statement_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "statement"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:284:1: statement : (
	// explainStatement EOF | execStatement EOF );
	public final TrcParser.statement_return statement()
			throws RecognitionException {
		TrcParser.statement_return retval = new TrcParser.statement_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token EOF2 = null;
		Token EOF4 = null;
		ParserRuleReturnScope explainStatement1 = null;
		ParserRuleReturnScope execStatement3 = null;

		CommonTree EOF2_tree = null;
		CommonTree EOF4_tree = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:285:3: (
			// explainStatement EOF | execStatement EOF )
			int alt1 = 2;
			int LA1_0 = input.LA(1);
			if ((LA1_0 == KW_EXPLAIN)) {
				alt1 = 1;
			} else if ((LA1_0 == KW_INSERT || LA1_0 == KW_WITH)) {
				alt1 = 2;
			}

			else {
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 1, 0,
						input);
				throw nvae;
			}

			switch (alt1) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:286:3:
			// explainStatement EOF
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_explainStatement_in_statement1403);
				explainStatement1 = explainStatement();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, explainStatement1.getTree());

				EOF2 = (Token) match(input, EOF, FOLLOW_EOF_in_statement1405);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					EOF2_tree = (CommonTree) adaptor.create(EOF2);
					adaptor.addChild(root_0, EOF2_tree);
				}

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:287:5: execStatement
			// EOF
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_execStatement_in_statement1411);
				execStatement3 = execStatement();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, execStatement3.getTree());

				EOF4 = (Token) match(input, EOF, FOLLOW_EOF_in_statement1413);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					EOF4_tree = (CommonTree) adaptor.create(EOF4);
					adaptor.addChild(root_0, EOF4_tree);
				}

			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "statement"

	public static class explainStatement_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "explainStatement"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:290:1: explainStatement :
	// KW_EXPLAIN (explainOptions= KW_EXTENDED |explainOptions= KW_FORMATTED )?
	// execStatement -> ^( TOK_EXPLAIN execStatement ( $explainOptions)? ) ;
	public final TrcParser.explainStatement_return explainStatement()
			throws RecognitionException {
		TrcParser.explainStatement_return retval = new TrcParser.explainStatement_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token explainOptions = null;
		Token KW_EXPLAIN5 = null;
		ParserRuleReturnScope execStatement6 = null;

		CommonTree explainOptions_tree = null;
		CommonTree KW_EXPLAIN5_tree = null;
		RewriteRuleTokenStream stream_KW_FORMATTED = new RewriteRuleTokenStream(
				adaptor, "token KW_FORMATTED");
		RewriteRuleTokenStream stream_KW_EXTENDED = new RewriteRuleTokenStream(
				adaptor, "token KW_EXTENDED");
		RewriteRuleTokenStream stream_KW_EXPLAIN = new RewriteRuleTokenStream(
				adaptor, "token KW_EXPLAIN");
		RewriteRuleSubtreeStream stream_execStatement = new RewriteRuleSubtreeStream(
				adaptor, "rule execStatement");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:291:3: ( KW_EXPLAIN
			// (explainOptions= KW_EXTENDED |explainOptions= KW_FORMATTED )?
			// execStatement -> ^( TOK_EXPLAIN execStatement ( $explainOptions)?
			// ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:292:3: KW_EXPLAIN
			// (explainOptions= KW_EXTENDED |explainOptions= KW_FORMATTED )?
			// execStatement
			{
				KW_EXPLAIN5 = (Token) match(input, KW_EXPLAIN,
						FOLLOW_KW_EXPLAIN_in_explainStatement1428);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_EXPLAIN.add(KW_EXPLAIN5);

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:293:3:
				// (explainOptions= KW_EXTENDED |explainOptions= KW_FORMATTED )?
				int alt2 = 3;
				int LA2_0 = input.LA(1);
				if ((LA2_0 == KW_EXTENDED)) {
					alt2 = 1;
				} else if ((LA2_0 == KW_FORMATTED)) {
					alt2 = 2;
				}
				switch (alt2) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:294:5:
				// explainOptions= KW_EXTENDED
				{
					explainOptions = (Token) match(input, KW_EXTENDED,
							FOLLOW_KW_EXTENDED_in_explainStatement1440);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_KW_EXTENDED.add(explainOptions);

				}
					break;
				case 2:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:295:7:
				// explainOptions= KW_FORMATTED
				{
					explainOptions = (Token) match(input, KW_FORMATTED,
							FOLLOW_KW_FORMATTED_in_explainStatement1450);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_KW_FORMATTED.add(explainOptions);

				}
					break;

				}

				pushFollow(FOLLOW_execStatement_in_explainStatement1459);
				execStatement6 = execStatement();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_execStatement.add(execStatement6.getTree());
				// AST REWRITE
				// elements: execStatement, explainOptions
				// token labels: explainOptions
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_explainOptions = new RewriteRuleTokenStream(
							adaptor, "token explainOptions", explainOptions);
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 298:5: -> ^( TOK_EXPLAIN execStatement (
					// $explainOptions)? )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:299:7: ^(
						// TOK_EXPLAIN execStatement ( $explainOptions)? )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_EXPLAIN,
											"TOK_EXPLAIN"), root_1);
							adaptor.addChild(root_1,
									stream_execStatement.nextTree());
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:299:36:
							// ( $explainOptions)?
							if (stream_explainOptions.hasNext()) {
								adaptor.addChild(root_1,
										stream_explainOptions.nextNode());
							}
							stream_explainOptions.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "explainStatement"

	public static class execStatement_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "execStatement"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:302:1: execStatement :
	// execStatement1 ( execStatement2 )* -> ^( TOK_ROOT execStatement1 (
	// execStatement2 )* ) ;
	public final TrcParser.execStatement_return execStatement()
			throws RecognitionException {
		TrcParser.execStatement_return retval = new TrcParser.execStatement_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope execStatement17 = null;
		ParserRuleReturnScope execStatement28 = null;

		RewriteRuleSubtreeStream stream_execStatement1 = new RewriteRuleSubtreeStream(
				adaptor, "rule execStatement1");
		RewriteRuleSubtreeStream stream_execStatement2 = new RewriteRuleSubtreeStream(
				adaptor, "rule execStatement2");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:303:3: (
			// execStatement1 ( execStatement2 )* -> ^( TOK_ROOT execStatement1
			// ( execStatement2 )* ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:304:3: execStatement1
			// ( execStatement2 )*
			{
				pushFollow(FOLLOW_execStatement1_in_execStatement1496);
				execStatement17 = execStatement1();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_execStatement1.add(execStatement17.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:304:18: (
				// execStatement2 )*
				loop3: while (true) {
					int alt3 = 2;
					int LA3_0 = input.LA(1);
					if ((LA3_0 == KW_WITH)) {
						alt3 = 1;
					}

					switch (alt3) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:304:18:
					// execStatement2
					{
						pushFollow(FOLLOW_execStatement2_in_execStatement1498);
						execStatement28 = execStatement2();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_execStatement2
									.add(execStatement28.getTree());
					}
						break;

					default:
						break loop3;
					}
				}

				// AST REWRITE
				// elements: execStatement2, execStatement1
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 305:5: -> ^( TOK_ROOT execStatement1 ( execStatement2 )*
					// )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:306:7: ^(
						// TOK_ROOT execStatement1 ( execStatement2 )* )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_ROOT,
											"TOK_ROOT"), root_1);
							adaptor.addChild(root_1,
									stream_execStatement1.nextTree());
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:306:33:
							// ( execStatement2 )*
							while (stream_execStatement2.hasNext()) {
								adaptor.addChild(root_1,
										stream_execStatement2.nextTree());
							}
							stream_execStatement2.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "execStatement"

	public static class execStatement1_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "execStatement1"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:309:1: execStatement1 : (
	// withClause )? insertSelectFromWholeClause ( ( COMMA )?
	// insertSelectFromWholeClause )* -> ^( TOK_ROOT_QUERY ( withClause )? (
	// insertSelectFromWholeClause )+ ) ;
	public final TrcParser.execStatement1_return execStatement1()
			throws RecognitionException {
		TrcParser.execStatement1_return retval = new TrcParser.execStatement1_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token COMMA11 = null;
		ParserRuleReturnScope withClause9 = null;
		ParserRuleReturnScope insertSelectFromWholeClause10 = null;
		ParserRuleReturnScope insertSelectFromWholeClause12 = null;

		CommonTree COMMA11_tree = null;
		RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(
				adaptor, "token COMMA");
		RewriteRuleSubtreeStream stream_insertSelectFromWholeClause = new RewriteRuleSubtreeStream(
				adaptor, "rule insertSelectFromWholeClause");
		RewriteRuleSubtreeStream stream_withClause = new RewriteRuleSubtreeStream(
				adaptor, "rule withClause");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:310:3: ( ( withClause
			// )? insertSelectFromWholeClause ( ( COMMA )?
			// insertSelectFromWholeClause )* -> ^( TOK_ROOT_QUERY ( withClause
			// )? ( insertSelectFromWholeClause )+ ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:311:3: ( withClause )?
			// insertSelectFromWholeClause ( ( COMMA )?
			// insertSelectFromWholeClause )*
			{
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:311:3: (
				// withClause )?
				int alt4 = 2;
				int LA4_0 = input.LA(1);
				if ((LA4_0 == KW_WITH)) {
					alt4 = 1;
				}
				switch (alt4) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:311:3: withClause
				{
					pushFollow(FOLLOW_withClause_in_execStatement11535);
					withClause9 = withClause();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_withClause.add(withClause9.getTree());
				}
					break;

				}

				pushFollow(FOLLOW_insertSelectFromWholeClause_in_execStatement11538);
				insertSelectFromWholeClause10 = insertSelectFromWholeClause();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_insertSelectFromWholeClause
							.add(insertSelectFromWholeClause10.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:311:43: ( ( COMMA
				// )? insertSelectFromWholeClause )*
				loop6: while (true) {
					int alt6 = 2;
					int LA6_0 = input.LA(1);
					if ((LA6_0 == COMMA || LA6_0 == KW_INSERT)) {
						alt6 = 1;
					}

					switch (alt6) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:311:44: (
					// COMMA )? insertSelectFromWholeClause
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:311:44: (
						// COMMA )?
						int alt5 = 2;
						int LA5_0 = input.LA(1);
						if ((LA5_0 == COMMA)) {
							alt5 = 1;
						}
						switch (alt5) {
						case 1:
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:311:44:
						// COMMA
						{
							COMMA11 = (Token) match(input, COMMA,
									FOLLOW_COMMA_in_execStatement11541);
							if (state.failed)
								return retval;
							if (state.backtracking == 0)
								stream_COMMA.add(COMMA11);

						}
							break;

						}

						pushFollow(FOLLOW_insertSelectFromWholeClause_in_execStatement11544);
						insertSelectFromWholeClause12 = insertSelectFromWholeClause();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_insertSelectFromWholeClause
									.add(insertSelectFromWholeClause12
											.getTree());
					}
						break;

					default:
						break loop6;
					}
				}

				// AST REWRITE
				// elements: withClause, insertSelectFromWholeClause
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 312:5: -> ^( TOK_ROOT_QUERY ( withClause )? (
					// insertSelectFromWholeClause )+ )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:313:7: ^(
						// TOK_ROOT_QUERY ( withClause )? (
						// insertSelectFromWholeClause )+ )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_ROOT_QUERY,
											"TOK_ROOT_QUERY"), root_1);
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:313:24:
							// ( withClause )?
							if (stream_withClause.hasNext()) {
								adaptor.addChild(root_1,
										stream_withClause.nextTree());
							}
							stream_withClause.reset();

							if (!(stream_insertSelectFromWholeClause.hasNext())) {
								throw new RewriteEarlyExitException();
							}
							while (stream_insertSelectFromWholeClause.hasNext()) {
								adaptor.addChild(root_1,
										stream_insertSelectFromWholeClause
												.nextTree());
							}
							stream_insertSelectFromWholeClause.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "execStatement1"

	public static class execStatement2_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "execStatement2"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:316:1: execStatement2 :
	// withClause insertSelectFromWholeClause ( ( COMMA )?
	// insertSelectFromWholeClause )* -> ^( TOK_ROOT_QUERY withClause (
	// insertSelectFromWholeClause )+ ) ;
	public final TrcParser.execStatement2_return execStatement2()
			throws RecognitionException {
		TrcParser.execStatement2_return retval = new TrcParser.execStatement2_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token COMMA15 = null;
		ParserRuleReturnScope withClause13 = null;
		ParserRuleReturnScope insertSelectFromWholeClause14 = null;
		ParserRuleReturnScope insertSelectFromWholeClause16 = null;

		CommonTree COMMA15_tree = null;
		RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(
				adaptor, "token COMMA");
		RewriteRuleSubtreeStream stream_insertSelectFromWholeClause = new RewriteRuleSubtreeStream(
				adaptor, "rule insertSelectFromWholeClause");
		RewriteRuleSubtreeStream stream_withClause = new RewriteRuleSubtreeStream(
				adaptor, "rule withClause");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:317:3: ( withClause
			// insertSelectFromWholeClause ( ( COMMA )?
			// insertSelectFromWholeClause )* -> ^( TOK_ROOT_QUERY withClause (
			// insertSelectFromWholeClause )+ ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:318:3: withClause
			// insertSelectFromWholeClause ( ( COMMA )?
			// insertSelectFromWholeClause )*
			{
				pushFollow(FOLLOW_withClause_in_execStatement21583);
				withClause13 = withClause();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_withClause.add(withClause13.getTree());
				pushFollow(FOLLOW_insertSelectFromWholeClause_in_execStatement21585);
				insertSelectFromWholeClause14 = insertSelectFromWholeClause();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_insertSelectFromWholeClause
							.add(insertSelectFromWholeClause14.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:318:42: ( ( COMMA
				// )? insertSelectFromWholeClause )*
				loop8: while (true) {
					int alt8 = 2;
					int LA8_0 = input.LA(1);
					if ((LA8_0 == COMMA || LA8_0 == KW_INSERT)) {
						alt8 = 1;
					}

					switch (alt8) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:318:43: (
					// COMMA )? insertSelectFromWholeClause
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:318:43: (
						// COMMA )?
						int alt7 = 2;
						int LA7_0 = input.LA(1);
						if ((LA7_0 == COMMA)) {
							alt7 = 1;
						}
						switch (alt7) {
						case 1:
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:318:43:
						// COMMA
						{
							COMMA15 = (Token) match(input, COMMA,
									FOLLOW_COMMA_in_execStatement21588);
							if (state.failed)
								return retval;
							if (state.backtracking == 0)
								stream_COMMA.add(COMMA15);

						}
							break;

						}

						pushFollow(FOLLOW_insertSelectFromWholeClause_in_execStatement21591);
						insertSelectFromWholeClause16 = insertSelectFromWholeClause();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_insertSelectFromWholeClause
									.add(insertSelectFromWholeClause16
											.getTree());
					}
						break;

					default:
						break loop8;
					}
				}

				// AST REWRITE
				// elements: insertSelectFromWholeClause, withClause
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 319:5: -> ^( TOK_ROOT_QUERY withClause (
					// insertSelectFromWholeClause )+ )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:320:7: ^(
						// TOK_ROOT_QUERY withClause (
						// insertSelectFromWholeClause )+ )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_ROOT_QUERY,
											"TOK_ROOT_QUERY"), root_1);
							adaptor.addChild(root_1,
									stream_withClause.nextTree());
							if (!(stream_insertSelectFromWholeClause.hasNext())) {
								throw new RewriteEarlyExitException();
							}
							while (stream_insertSelectFromWholeClause.hasNext()) {
								adaptor.addChild(root_1,
										stream_insertSelectFromWholeClause
												.nextTree());
							}
							stream_insertSelectFromWholeClause.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "execStatement2"

	public static class withClause_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "withClause"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:323:1: withClause : KW_WITH
	// subQuerySource ( ( COMMA )? subQuerySource )* -> ^( TOK_WITH (
	// subQuerySource )+ ) ;
	public final TrcParser.withClause_return withClause()
			throws RecognitionException {
		TrcParser.withClause_return retval = new TrcParser.withClause_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_WITH17 = null;
		Token COMMA19 = null;
		ParserRuleReturnScope subQuerySource18 = null;
		ParserRuleReturnScope subQuerySource20 = null;

		CommonTree KW_WITH17_tree = null;
		CommonTree COMMA19_tree = null;
		RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(
				adaptor, "token COMMA");
		RewriteRuleTokenStream stream_KW_WITH = new RewriteRuleTokenStream(
				adaptor, "token KW_WITH");
		RewriteRuleSubtreeStream stream_subQuerySource = new RewriteRuleSubtreeStream(
				adaptor, "rule subQuerySource");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:324:3: ( KW_WITH
			// subQuerySource ( ( COMMA )? subQuerySource )* -> ^( TOK_WITH (
			// subQuerySource )+ ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:325:3: KW_WITH
			// subQuerySource ( ( COMMA )? subQuerySource )*
			{
				KW_WITH17 = (Token) match(input, KW_WITH,
						FOLLOW_KW_WITH_in_withClause1629);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_WITH.add(KW_WITH17);

				pushFollow(FOLLOW_subQuerySource_in_withClause1631);
				subQuerySource18 = subQuerySource();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_subQuerySource.add(subQuerySource18.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:325:26: ( ( COMMA
				// )? subQuerySource )*
				loop10: while (true) {
					int alt10 = 2;
					int LA10_0 = input.LA(1);
					if ((LA10_0 == COMMA || LA10_0 == LPAREN)) {
						alt10 = 1;
					}

					switch (alt10) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:325:27: (
					// COMMA )? subQuerySource
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:325:27: (
						// COMMA )?
						int alt9 = 2;
						int LA9_0 = input.LA(1);
						if ((LA9_0 == COMMA)) {
							alt9 = 1;
						}
						switch (alt9) {
						case 1:
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:325:27:
						// COMMA
						{
							COMMA19 = (Token) match(input, COMMA,
									FOLLOW_COMMA_in_withClause1634);
							if (state.failed)
								return retval;
							if (state.backtracking == 0)
								stream_COMMA.add(COMMA19);

						}
							break;

						}

						pushFollow(FOLLOW_subQuerySource_in_withClause1637);
						subQuerySource20 = subQuerySource();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_subQuerySource.add(subQuerySource20
									.getTree());
					}
						break;

					default:
						break loop10;
					}
				}

				// AST REWRITE
				// elements: subQuerySource
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 326:5: -> ^( TOK_WITH ( subQuerySource )+ )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:327:7: ^(
						// TOK_WITH ( subQuerySource )+ )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_WITH,
											"TOK_WITH"), root_1);
							if (!(stream_subQuerySource.hasNext())) {
								throw new RewriteEarlyExitException();
							}
							while (stream_subQuerySource.hasNext()) {
								adaptor.addChild(root_1,
										stream_subQuerySource.nextTree());
							}
							stream_subQuerySource.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "withClause"

	public static class insertSelectFromWholeClause_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "insertSelectFromWholeClause"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:330:1:
	// insertSelectFromWholeClause : insertClause selectClause fromClause (
	// whereClause )? ( groupByClause )? ( havingClause )? -> ^(
	// TOK_INSERT_QUERY fromClause ^( TOK_INSERT insertClause selectClause (
	// whereClause )? ( groupByClause )? ( havingClause )? ) ) ;
	public final TrcParser.insertSelectFromWholeClause_return insertSelectFromWholeClause()
			throws RecognitionException {
		TrcParser.insertSelectFromWholeClause_return retval = new TrcParser.insertSelectFromWholeClause_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope insertClause21 = null;
		ParserRuleReturnScope selectClause22 = null;
		ParserRuleReturnScope fromClause23 = null;
		ParserRuleReturnScope whereClause24 = null;
		ParserRuleReturnScope groupByClause25 = null;
		ParserRuleReturnScope havingClause26 = null;

		RewriteRuleSubtreeStream stream_whereClause = new RewriteRuleSubtreeStream(
				adaptor, "rule whereClause");
		RewriteRuleSubtreeStream stream_insertClause = new RewriteRuleSubtreeStream(
				adaptor, "rule insertClause");
		RewriteRuleSubtreeStream stream_groupByClause = new RewriteRuleSubtreeStream(
				adaptor, "rule groupByClause");
		RewriteRuleSubtreeStream stream_havingClause = new RewriteRuleSubtreeStream(
				adaptor, "rule havingClause");
		RewriteRuleSubtreeStream stream_selectClause = new RewriteRuleSubtreeStream(
				adaptor, "rule selectClause");
		RewriteRuleSubtreeStream stream_fromClause = new RewriteRuleSubtreeStream(
				adaptor, "rule fromClause");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:331:3: ( insertClause
			// selectClause fromClause ( whereClause )? ( groupByClause )? (
			// havingClause )? -> ^( TOK_INSERT_QUERY fromClause ^( TOK_INSERT
			// insertClause selectClause ( whereClause )? ( groupByClause )? (
			// havingClause )? ) ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:332:3: insertClause
			// selectClause fromClause ( whereClause )? ( groupByClause )? (
			// havingClause )?
			{
				pushFollow(FOLLOW_insertClause_in_insertSelectFromWholeClause1673);
				insertClause21 = insertClause();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_insertClause.add(insertClause21.getTree());
				pushFollow(FOLLOW_selectClause_in_insertSelectFromWholeClause1675);
				selectClause22 = selectClause();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_selectClause.add(selectClause22.getTree());
				pushFollow(FOLLOW_fromClause_in_insertSelectFromWholeClause1677);
				fromClause23 = fromClause();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_fromClause.add(fromClause23.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:332:40: (
				// whereClause )?
				int alt11 = 2;
				int LA11_0 = input.LA(1);
				if ((LA11_0 == KW_WHERE)) {
					alt11 = 1;
				}
				switch (alt11) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:332:40:
				// whereClause
				{
					pushFollow(FOLLOW_whereClause_in_insertSelectFromWholeClause1679);
					whereClause24 = whereClause();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_whereClause.add(whereClause24.getTree());
				}
					break;

				}

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:332:53: (
				// groupByClause )?
				int alt12 = 2;
				int LA12_0 = input.LA(1);
				if ((LA12_0 == KW_GROUP)) {
					alt12 = 1;
				}
				switch (alt12) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:332:53:
				// groupByClause
				{
					pushFollow(FOLLOW_groupByClause_in_insertSelectFromWholeClause1682);
					groupByClause25 = groupByClause();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_groupByClause.add(groupByClause25.getTree());
				}
					break;

				}

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:332:68: (
				// havingClause )?
				int alt13 = 2;
				int LA13_0 = input.LA(1);
				if ((LA13_0 == KW_HAVING)) {
					alt13 = 1;
				}
				switch (alt13) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:332:68:
				// havingClause
				{
					pushFollow(FOLLOW_havingClause_in_insertSelectFromWholeClause1685);
					havingClause26 = havingClause();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_havingClause.add(havingClause26.getTree());
				}
					break;

				}

				// AST REWRITE
				// elements: insertClause, fromClause, selectClause,
				// groupByClause, whereClause, havingClause
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 333:5: -> ^( TOK_INSERT_QUERY fromClause ^( TOK_INSERT
					// insertClause selectClause ( whereClause )? (
					// groupByClause )? ( havingClause )? ) )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:334:7: ^(
						// TOK_INSERT_QUERY fromClause ^( TOK_INSERT
						// insertClause selectClause ( whereClause )? (
						// groupByClause )? ( havingClause )? ) )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(
											TOK_INSERT_QUERY,
											"TOK_INSERT_QUERY"), root_1);
							adaptor.addChild(root_1,
									stream_fromClause.nextTree());
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:336:9:
							// ^( TOK_INSERT insertClause selectClause (
							// whereClause )? ( groupByClause )? ( havingClause
							// )? )
							{
								CommonTree root_2 = (CommonTree) adaptor.nil();
								root_2 = (CommonTree) adaptor.becomeRoot(
										(CommonTree) adaptor.create(TOK_INSERT,
												"TOK_INSERT"), root_2);
								adaptor.addChild(root_2,
										stream_insertClause.nextTree());
								adaptor.addChild(root_2,
										stream_selectClause.nextTree());
								// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:336:48:
								// ( whereClause )?
								if (stream_whereClause.hasNext()) {
									adaptor.addChild(root_2,
											stream_whereClause.nextTree());
								}
								stream_whereClause.reset();

								// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:336:61:
								// ( groupByClause )?
								if (stream_groupByClause.hasNext()) {
									adaptor.addChild(root_2,
											stream_groupByClause.nextTree());
								}
								stream_groupByClause.reset();

								// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:336:76:
								// ( havingClause )?
								if (stream_havingClause.hasNext()) {
									adaptor.addChild(root_2,
											stream_havingClause.nextTree());
								}
								stream_havingClause.reset();

								adaptor.addChild(root_1, root_2);
							}

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "insertSelectFromWholeClause"

	public static class insertClause_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "insertClause"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:340:1: insertClause :
	// KW_INSERT KW_INTO destTable ( keyExpr )? ( attrsExpr )? -> ^(
	// TOK_DESTINATION destTable ( keyExpr )? ( attrsExpr )? ) ;
	public final TrcParser.insertClause_return insertClause()
			throws RecognitionException {
		TrcParser.insertClause_return retval = new TrcParser.insertClause_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_INSERT27 = null;
		Token KW_INTO28 = null;
		ParserRuleReturnScope destTable29 = null;
		ParserRuleReturnScope keyExpr30 = null;
		ParserRuleReturnScope attrsExpr31 = null;

		CommonTree KW_INSERT27_tree = null;
		CommonTree KW_INTO28_tree = null;
		RewriteRuleTokenStream stream_KW_INTO = new RewriteRuleTokenStream(
				adaptor, "token KW_INTO");
		RewriteRuleTokenStream stream_KW_INSERT = new RewriteRuleTokenStream(
				adaptor, "token KW_INSERT");
		RewriteRuleSubtreeStream stream_attrsExpr = new RewriteRuleSubtreeStream(
				adaptor, "rule attrsExpr");
		RewriteRuleSubtreeStream stream_destTable = new RewriteRuleSubtreeStream(
				adaptor, "rule destTable");
		RewriteRuleSubtreeStream stream_keyExpr = new RewriteRuleSubtreeStream(
				adaptor, "rule keyExpr");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:341:3: ( KW_INSERT
			// KW_INTO destTable ( keyExpr )? ( attrsExpr )? -> ^(
			// TOK_DESTINATION destTable ( keyExpr )? ( attrsExpr )? ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:342:3: KW_INSERT
			// KW_INTO destTable ( keyExpr )? ( attrsExpr )?
			{
				KW_INSERT27 = (Token) match(input, KW_INSERT,
						FOLLOW_KW_INSERT_in_insertClause1761);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_INSERT.add(KW_INSERT27);

				KW_INTO28 = (Token) match(input, KW_INTO,
						FOLLOW_KW_INTO_in_insertClause1763);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_INTO.add(KW_INTO28);

				pushFollow(FOLLOW_destTable_in_insertClause1765);
				destTable29 = destTable();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_destTable.add(destTable29.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:342:31: ( keyExpr
				// )?
				int alt14 = 2;
				int LA14_0 = input.LA(1);
				if ((LA14_0 == KW_WITH)) {
					int LA14_1 = input.LA(2);
					if ((synpred15_Trc())) {
						alt14 = 1;
					}
				}
				switch (alt14) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:342:31: keyExpr
				{
					pushFollow(FOLLOW_keyExpr_in_insertClause1767);
					keyExpr30 = keyExpr();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_keyExpr.add(keyExpr30.getTree());
				}
					break;

				}

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:342:40: (
				// attrsExpr )?
				int alt15 = 2;
				int LA15_0 = input.LA(1);
				if ((LA15_0 == KW_WITH)) {
					alt15 = 1;
				}
				switch (alt15) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:342:40: attrsExpr
				{
					pushFollow(FOLLOW_attrsExpr_in_insertClause1770);
					attrsExpr31 = attrsExpr();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_attrsExpr.add(attrsExpr31.getTree());
				}
					break;

				}

				// AST REWRITE
				// elements: keyExpr, attrsExpr, destTable
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 343:5: -> ^( TOK_DESTINATION destTable ( keyExpr )? (
					// attrsExpr )? )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:344:7: ^(
						// TOK_DESTINATION destTable ( keyExpr )? ( attrsExpr )?
						// )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor
									.becomeRoot(
											(CommonTree) adaptor.create(
													TOK_DESTINATION,
													"TOK_DESTINATION"), root_1);
							adaptor.addChild(root_1,
									stream_destTable.nextTree());
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:344:35:
							// ( keyExpr )?
							if (stream_keyExpr.hasNext()) {
								adaptor.addChild(root_1,
										stream_keyExpr.nextTree());
							}
							stream_keyExpr.reset();

							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:344:44:
							// ( attrsExpr )?
							if (stream_attrsExpr.hasNext()) {
								adaptor.addChild(root_1,
										stream_attrsExpr.nextTree());
							}
							stream_attrsExpr.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "insertClause"

	public static class destTable_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "destTable"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:347:1: destTable : ( tableName
	// | KW_PRINTTABLE (al= Identifier )? -> ^( KW_PRINTTABLE ( $al)? ) );
	public final TrcParser.destTable_return destTable()
			throws RecognitionException {
		TrcParser.destTable_return retval = new TrcParser.destTable_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token al = null;
		Token KW_PRINTTABLE33 = null;
		ParserRuleReturnScope tableName32 = null;

		CommonTree al_tree = null;
		CommonTree KW_PRINTTABLE33_tree = null;
		RewriteRuleTokenStream stream_KW_PRINTTABLE = new RewriteRuleTokenStream(
				adaptor, "token KW_PRINTTABLE");
		RewriteRuleTokenStream stream_Identifier = new RewriteRuleTokenStream(
				adaptor, "token Identifier");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:348:3: ( tableName |
			// KW_PRINTTABLE (al= Identifier )? -> ^( KW_PRINTTABLE ( $al)? ) )
			int alt17 = 2;
			int LA17_0 = input.LA(1);
			if ((LA17_0 == Identifier)) {
				alt17 = 1;
			} else if ((LA17_0 == KW_PRINTTABLE)) {
				alt17 = 2;
			}

			else {
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 17, 0,
						input);
				throw nvae;
			}

			switch (alt17) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:349:3: tableName
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_tableName_in_destTable1810);
				tableName32 = tableName();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, tableName32.getTree());

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:350:5: KW_PRINTTABLE
			// (al= Identifier )?
			{
				KW_PRINTTABLE33 = (Token) match(input, KW_PRINTTABLE,
						FOLLOW_KW_PRINTTABLE_in_destTable1816);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_PRINTTABLE.add(KW_PRINTTABLE33);

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:350:21: (al=
				// Identifier )?
				int alt16 = 2;
				int LA16_0 = input.LA(1);
				if ((LA16_0 == Identifier)) {
					alt16 = 1;
				}
				switch (alt16) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:350:21: al=
				// Identifier
				{
					al = (Token) match(input, Identifier,
							FOLLOW_Identifier_in_destTable1820);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_Identifier.add(al);

				}
					break;

				}

				// AST REWRITE
				// elements: KW_PRINTTABLE, al
				// token labels: al
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_al = new RewriteRuleTokenStream(
							adaptor, "token al", al);
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 351:5: -> ^( KW_PRINTTABLE ( $al)? )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:352:7: ^(
						// KW_PRINTTABLE ( $al)? )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									stream_KW_PRINTTABLE.nextNode(), root_1);
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:352:24:
							// ( $al)?
							if (stream_al.hasNext()) {
								adaptor.addChild(root_1, stream_al.nextNode());
							}
							stream_al.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "destTable"

	public static class tableName_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "tableName"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:355:1: tableName : (db=
	// Identifier DOT )? tab= Identifier -> ^( TOK_TABNAME ( $db)? $tab) ;
	public final TrcParser.tableName_return tableName()
			throws RecognitionException {
		TrcParser.tableName_return retval = new TrcParser.tableName_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token db = null;
		Token tab = null;
		Token DOT34 = null;

		CommonTree db_tree = null;
		CommonTree tab_tree = null;
		CommonTree DOT34_tree = null;
		RewriteRuleTokenStream stream_DOT = new RewriteRuleTokenStream(adaptor,
				"token DOT");
		RewriteRuleTokenStream stream_Identifier = new RewriteRuleTokenStream(
				adaptor, "token Identifier");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:356:3: ( (db=
			// Identifier DOT )? tab= Identifier -> ^( TOK_TABNAME ( $db)? $tab)
			// )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:357:3: (db= Identifier
			// DOT )? tab= Identifier
			{
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:357:3: (db=
				// Identifier DOT )?
				int alt18 = 2;
				int LA18_0 = input.LA(1);
				if ((LA18_0 == Identifier)) {
					int LA18_1 = input.LA(2);
					if ((LA18_1 == DOT)) {
						int LA18_2 = input.LA(3);
						if ((LA18_2 == Identifier)) {
							alt18 = 1;
						}
					}
				}
				switch (alt18) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:357:4: db=
				// Identifier DOT
				{
					db = (Token) match(input, Identifier,
							FOLLOW_Identifier_in_tableName1859);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_Identifier.add(db);

					DOT34 = (Token) match(input, DOT,
							FOLLOW_DOT_in_tableName1861);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_DOT.add(DOT34);

				}
					break;

				}

				tab = (Token) match(input, Identifier,
						FOLLOW_Identifier_in_tableName1867);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_Identifier.add(tab);

				// AST REWRITE
				// elements: db, tab
				// token labels: db, tab
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_db = new RewriteRuleTokenStream(
							adaptor, "token db", db);
					RewriteRuleTokenStream stream_tab = new RewriteRuleTokenStream(
							adaptor, "token tab", tab);
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 358:5: -> ^( TOK_TABNAME ( $db)? $tab)
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:359:7: ^(
						// TOK_TABNAME ( $db)? $tab)
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_TABNAME,
											"TOK_TABNAME"), root_1);
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:359:22:
							// ( $db)?
							if (stream_db.hasNext()) {
								adaptor.addChild(root_1, stream_db.nextNode());
							}
							stream_db.reset();

							adaptor.addChild(root_1, stream_tab.nextNode());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "tableName"

	public static class keyExpr_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "keyExpr"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:362:1: keyExpr : KW_WITH
	// expression KW_AS KW_KEY -> ^( TOK_KEY expression ) ;
	public final TrcParser.keyExpr_return keyExpr() throws RecognitionException {
		TrcParser.keyExpr_return retval = new TrcParser.keyExpr_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_WITH35 = null;
		Token KW_AS37 = null;
		Token KW_KEY38 = null;
		ParserRuleReturnScope expression36 = null;

		CommonTree KW_WITH35_tree = null;
		CommonTree KW_AS37_tree = null;
		CommonTree KW_KEY38_tree = null;
		RewriteRuleTokenStream stream_KW_AS = new RewriteRuleTokenStream(
				adaptor, "token KW_AS");
		RewriteRuleTokenStream stream_KW_KEY = new RewriteRuleTokenStream(
				adaptor, "token KW_KEY");
		RewriteRuleTokenStream stream_KW_WITH = new RewriteRuleTokenStream(
				adaptor, "token KW_WITH");
		RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(
				adaptor, "rule expression");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:363:3: ( KW_WITH
			// expression KW_AS KW_KEY -> ^( TOK_KEY expression ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:364:3: KW_WITH
			// expression KW_AS KW_KEY
			{
				KW_WITH35 = (Token) match(input, KW_WITH,
						FOLLOW_KW_WITH_in_keyExpr1905);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_WITH.add(KW_WITH35);

				pushFollow(FOLLOW_expression_in_keyExpr1907);
				expression36 = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_expression.add(expression36.getTree());
				KW_AS37 = (Token) match(input, KW_AS,
						FOLLOW_KW_AS_in_keyExpr1909);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_AS.add(KW_AS37);

				KW_KEY38 = (Token) match(input, KW_KEY,
						FOLLOW_KW_KEY_in_keyExpr1911);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_KEY.add(KW_KEY38);

				// AST REWRITE
				// elements: expression
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 365:5: -> ^( TOK_KEY expression )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:366:7: ^(
						// TOK_KEY expression )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_KEY,
											"TOK_KEY"), root_1);
							adaptor.addChild(root_1,
									stream_expression.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "keyExpr"

	public static class attrsExpr_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "attrsExpr"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:369:1: attrsExpr : KW_WITH
	// expression KW_AS KW_ATTRIBUTES -> ^( TOK_ATTRBUTES expression ) ;
	public final TrcParser.attrsExpr_return attrsExpr()
			throws RecognitionException {
		TrcParser.attrsExpr_return retval = new TrcParser.attrsExpr_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_WITH39 = null;
		Token KW_AS41 = null;
		Token KW_ATTRIBUTES42 = null;
		ParserRuleReturnScope expression40 = null;

		CommonTree KW_WITH39_tree = null;
		CommonTree KW_AS41_tree = null;
		CommonTree KW_ATTRIBUTES42_tree = null;
		RewriteRuleTokenStream stream_KW_AS = new RewriteRuleTokenStream(
				adaptor, "token KW_AS");
		RewriteRuleTokenStream stream_KW_WITH = new RewriteRuleTokenStream(
				adaptor, "token KW_WITH");
		RewriteRuleTokenStream stream_KW_ATTRIBUTES = new RewriteRuleTokenStream(
				adaptor, "token KW_ATTRIBUTES");
		RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(
				adaptor, "rule expression");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:370:3: ( KW_WITH
			// expression KW_AS KW_ATTRIBUTES -> ^( TOK_ATTRBUTES expression ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:371:3: KW_WITH
			// expression KW_AS KW_ATTRIBUTES
			{
				KW_WITH39 = (Token) match(input, KW_WITH,
						FOLLOW_KW_WITH_in_attrsExpr1944);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_WITH.add(KW_WITH39);

				pushFollow(FOLLOW_expression_in_attrsExpr1946);
				expression40 = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_expression.add(expression40.getTree());
				KW_AS41 = (Token) match(input, KW_AS,
						FOLLOW_KW_AS_in_attrsExpr1948);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_AS.add(KW_AS41);

				KW_ATTRIBUTES42 = (Token) match(input, KW_ATTRIBUTES,
						FOLLOW_KW_ATTRIBUTES_in_attrsExpr1950);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_ATTRIBUTES.add(KW_ATTRIBUTES42);

				// AST REWRITE
				// elements: expression
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 372:5: -> ^( TOK_ATTRBUTES expression )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:373:7: ^(
						// TOK_ATTRBUTES expression )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_ATTRBUTES,
											"TOK_ATTRBUTES"), root_1);
							adaptor.addChild(root_1,
									stream_expression.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "attrsExpr"

	public static class selectClause_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "selectClause"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:376:1: selectClause :
	// KW_SELECT (dist= KW_DISTINCT )? selectList -> {$dist == null}? ^(
	// TOK_SELECT selectList ) -> ^( TOK_SELECTDI selectList ) ;
	public final TrcParser.selectClause_return selectClause()
			throws RecognitionException {
		TrcParser.selectClause_return retval = new TrcParser.selectClause_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token dist = null;
		Token KW_SELECT43 = null;
		ParserRuleReturnScope selectList44 = null;

		CommonTree dist_tree = null;
		CommonTree KW_SELECT43_tree = null;
		RewriteRuleTokenStream stream_KW_SELECT = new RewriteRuleTokenStream(
				adaptor, "token KW_SELECT");
		RewriteRuleTokenStream stream_KW_DISTINCT = new RewriteRuleTokenStream(
				adaptor, "token KW_DISTINCT");
		RewriteRuleSubtreeStream stream_selectList = new RewriteRuleSubtreeStream(
				adaptor, "rule selectList");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:377:3: ( KW_SELECT
			// (dist= KW_DISTINCT )? selectList -> {$dist == null}? ^(
			// TOK_SELECT selectList ) -> ^( TOK_SELECTDI selectList ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:378:3: KW_SELECT
			// (dist= KW_DISTINCT )? selectList
			{
				KW_SELECT43 = (Token) match(input, KW_SELECT,
						FOLLOW_KW_SELECT_in_selectClause1983);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_SELECT.add(KW_SELECT43);

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:378:13: (dist=
				// KW_DISTINCT )?
				int alt19 = 2;
				int LA19_0 = input.LA(1);
				if ((LA19_0 == KW_DISTINCT)) {
					alt19 = 1;
				}
				switch (alt19) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:378:14: dist=
				// KW_DISTINCT
				{
					dist = (Token) match(input, KW_DISTINCT,
							FOLLOW_KW_DISTINCT_in_selectClause1988);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_KW_DISTINCT.add(dist);

				}
					break;

				}

				pushFollow(FOLLOW_selectList_in_selectClause1992);
				selectList44 = selectList();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_selectList.add(selectList44.getTree());
				// AST REWRITE
				// elements: selectList, selectList
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 379:5: -> {$dist == null}? ^( TOK_SELECT selectList )
					if (dist == null) {
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:380:7: ^(
						// TOK_SELECT selectList )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_SELECT,
											"TOK_SELECT"), root_1);
							adaptor.addChild(root_1,
									stream_selectList.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					else // 381:5: -> ^( TOK_SELECTDI selectList )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:382:7: ^(
						// TOK_SELECTDI selectList )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_SELECTDI,
											"TOK_SELECTDI"), root_1);
							adaptor.addChild(root_1,
									stream_selectList.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "selectClause"

	public static class selectList_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "selectList"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:385:1: selectList : selectItem
	// ( COMMA selectItem )* -> ( selectItem )+ ;
	public final TrcParser.selectList_return selectList()
			throws RecognitionException {
		TrcParser.selectList_return retval = new TrcParser.selectList_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token COMMA46 = null;
		ParserRuleReturnScope selectItem45 = null;
		ParserRuleReturnScope selectItem47 = null;

		CommonTree COMMA46_tree = null;
		RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(
				adaptor, "token COMMA");
		RewriteRuleSubtreeStream stream_selectItem = new RewriteRuleSubtreeStream(
				adaptor, "rule selectItem");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:386:3: ( selectItem (
			// COMMA selectItem )* -> ( selectItem )+ )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:387:3: selectItem (
			// COMMA selectItem )*
			{
				pushFollow(FOLLOW_selectItem_in_selectList2045);
				selectItem45 = selectItem();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_selectItem.add(selectItem45.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:387:14: ( COMMA
				// selectItem )*
				loop20: while (true) {
					int alt20 = 2;
					int LA20_0 = input.LA(1);
					if ((LA20_0 == COMMA)) {
						alt20 = 1;
					}

					switch (alt20) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:387:15: COMMA
					// selectItem
					{
						COMMA46 = (Token) match(input, COMMA,
								FOLLOW_COMMA_in_selectList2048);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_COMMA.add(COMMA46);

						pushFollow(FOLLOW_selectItem_in_selectList2050);
						selectItem47 = selectItem();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_selectItem.add(selectItem47.getTree());
					}
						break;

					default:
						break loop20;
					}
				}

				// AST REWRITE
				// elements: selectItem
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 388:5: -> ( selectItem )+
					{
						if (!(stream_selectItem.hasNext())) {
							throw new RewriteEarlyExitException();
						}
						while (stream_selectItem.hasNext()) {
							adaptor.addChild(root_0,
									stream_selectItem.nextTree());
						}
						stream_selectItem.reset();

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "selectList"

	public static class selectItem_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "selectItem"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:391:1: selectItem :
	// selectExpression ( ( KW_AS )? Identifier )? ( KW_EXPAND )? -> ^(
	// TOK_SELEXPR selectExpression ( Identifier )? ( KW_EXPAND )? ) ;
	public final TrcParser.selectItem_return selectItem()
			throws RecognitionException {
		TrcParser.selectItem_return retval = new TrcParser.selectItem_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_AS49 = null;
		Token Identifier50 = null;
		Token KW_EXPAND51 = null;
		ParserRuleReturnScope selectExpression48 = null;

		CommonTree KW_AS49_tree = null;
		CommonTree Identifier50_tree = null;
		CommonTree KW_EXPAND51_tree = null;
		RewriteRuleTokenStream stream_KW_AS = new RewriteRuleTokenStream(
				adaptor, "token KW_AS");
		RewriteRuleTokenStream stream_KW_EXPAND = new RewriteRuleTokenStream(
				adaptor, "token KW_EXPAND");
		RewriteRuleTokenStream stream_Identifier = new RewriteRuleTokenStream(
				adaptor, "token Identifier");
		RewriteRuleSubtreeStream stream_selectExpression = new RewriteRuleSubtreeStream(
				adaptor, "rule selectExpression");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:392:3: (
			// selectExpression ( ( KW_AS )? Identifier )? ( KW_EXPAND )? -> ^(
			// TOK_SELEXPR selectExpression ( Identifier )? ( KW_EXPAND )? ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:393:3:
			// selectExpression ( ( KW_AS )? Identifier )? ( KW_EXPAND )?
			{
				pushFollow(FOLLOW_selectExpression_in_selectItem2076);
				selectExpression48 = selectExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_selectExpression.add(selectExpression48.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:393:20: ( ( KW_AS
				// )? Identifier )?
				int alt22 = 2;
				int LA22_0 = input.LA(1);
				if ((LA22_0 == Identifier || LA22_0 == KW_AS)) {
					alt22 = 1;
				}
				switch (alt22) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:393:21: ( KW_AS )?
				// Identifier
				{
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:393:21: (
					// KW_AS )?
					int alt21 = 2;
					int LA21_0 = input.LA(1);
					if ((LA21_0 == KW_AS)) {
						alt21 = 1;
					}
					switch (alt21) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:393:21: KW_AS
					{
						KW_AS49 = (Token) match(input, KW_AS,
								FOLLOW_KW_AS_in_selectItem2079);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_KW_AS.add(KW_AS49);

					}
						break;

					}

					Identifier50 = (Token) match(input, Identifier,
							FOLLOW_Identifier_in_selectItem2082);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_Identifier.add(Identifier50);

				}
					break;

				}

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:393:41: (
				// KW_EXPAND )?
				int alt23 = 2;
				int LA23_0 = input.LA(1);
				if ((LA23_0 == KW_EXPAND)) {
					alt23 = 1;
				}
				switch (alt23) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:393:41: KW_EXPAND
				{
					KW_EXPAND51 = (Token) match(input, KW_EXPAND,
							FOLLOW_KW_EXPAND_in_selectItem2086);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_KW_EXPAND.add(KW_EXPAND51);

				}
					break;

				}

				// AST REWRITE
				// elements: Identifier, selectExpression, KW_EXPAND
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 394:5: -> ^( TOK_SELEXPR selectExpression ( Identifier )?
					// ( KW_EXPAND )? )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:395:7: ^(
						// TOK_SELEXPR selectExpression ( Identifier )? (
						// KW_EXPAND )? )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_SELEXPR,
											"TOK_SELEXPR"), root_1);
							adaptor.addChild(root_1,
									stream_selectExpression.nextTree());
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:395:38:
							// ( Identifier )?
							if (stream_Identifier.hasNext()) {
								adaptor.addChild(root_1,
										stream_Identifier.nextNode());
							}
							stream_Identifier.reset();

							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:395:50:
							// ( KW_EXPAND )?
							if (stream_KW_EXPAND.hasNext()) {
								adaptor.addChild(root_1,
										stream_KW_EXPAND.nextNode());
							}
							stream_KW_EXPAND.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "selectItem"

	public static class selectExpression_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "selectExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:398:1: selectExpression : (
	// expression | tableAllColumns );
	public final TrcParser.selectExpression_return selectExpression()
			throws RecognitionException {
		TrcParser.selectExpression_return retval = new TrcParser.selectExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope expression52 = null;
		ParserRuleReturnScope tableAllColumns53 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:399:3: ( expression |
			// tableAllColumns )
			int alt24 = 2;
			switch (input.LA(1)) {
			case BigintLiteral:
			case CharSetName:
			case IdentifierRef:
			case KW_AGGR_TIME:
			case KW_ARRAY:
			case KW_ATTRIBUTES:
			case KW_CASE:
			case KW_CAST:
			case KW_EXECUTE:
			case KW_FALSE:
			case KW_FOREACH:
			case KW_IF:
			case KW_MAP:
			case KW_NOT:
			case KW_NULL:
			case KW_STRUCT:
			case KW_TRUE:
			case KW_UNIONTYPE:
			case LPAREN:
			case MINUS:
			case Number:
			case PLUS:
			case SmallintLiteral:
			case StringLiteral:
			case TILDE:
			case TinyintLiteral: {
				alt24 = 1;
			}
				break;
			case Identifier: {
				int LA24_2 = input.LA(2);
				if ((LA24_2 == DOT)) {
					int LA24_4 = input.LA(3);
					if ((LA24_4 == Identifier)) {
						int LA24_5 = input.LA(4);
						if ((LA24_5 == EOF
								|| LA24_5 == AMPERSAND
								|| (LA24_5 >= BITWISEOR && LA24_5 <= BITWISEXOR)
								|| LA24_5 == COMMA
								|| (LA24_5 >= DIV && LA24_5 <= DIVIDE)
								|| LA24_5 == EQUAL
								|| (LA24_5 >= GREATERTHAN && LA24_5 <= GREATERTHANOREQUALTO)
								|| LA24_5 == Identifier
								|| LA24_5 == KW_AND
								|| LA24_5 == KW_AS
								|| LA24_5 == KW_EXPAND
								|| LA24_5 == KW_FROM
								|| LA24_5 == KW_IN
								|| LA24_5 == KW_IS
								|| LA24_5 == KW_LIKE
								|| LA24_5 == KW_NOT
								|| LA24_5 == KW_OR
								|| LA24_5 == KW_REGEXP
								|| LA24_5 == KW_RLIKE
								|| (LA24_5 >= LESSTHAN && LA24_5 <= LESSTHANOREQUALTO)
								|| LA24_5 == LSQUARE
								|| (LA24_5 >= MINUS && LA24_5 <= NOTEQUAL)
								|| LA24_5 == PLUS || LA24_5 == STAR)) {
							alt24 = 1;
						} else if ((LA24_5 == DOT)) {
							int LA24_6 = input.LA(5);
							if ((LA24_6 == Identifier)) {
								alt24 = 1;
							} else if ((LA24_6 == STAR)) {
								alt24 = 2;
							}

							else {
								if (state.backtracking > 0) {
									state.failed = true;
									return retval;
								}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae = new NoViableAltException(
											"", 24, 6, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}

						else {
							if (state.backtracking > 0) {
								state.failed = true;
								return retval;
							}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae = new NoViableAltException(
										"", 24, 5, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					} else if ((LA24_4 == STAR)) {
						alt24 = 2;
					}

					else {
						if (state.backtracking > 0) {
							state.failed = true;
							return retval;
						}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae = new NoViableAltException(
									"", 24, 4, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				} else if ((LA24_2 == EOF
						|| LA24_2 == AMPERSAND
						|| (LA24_2 >= BITWISEOR && LA24_2 <= BITWISEXOR)
						|| LA24_2 == COMMA
						|| (LA24_2 >= DIV && LA24_2 <= DIVIDE)
						|| LA24_2 == EQUAL
						|| (LA24_2 >= GREATERTHAN && LA24_2 <= GREATERTHANOREQUALTO)
						|| LA24_2 == Identifier || LA24_2 == KW_AND
						|| LA24_2 == KW_AS || LA24_2 == KW_EXPAND
						|| LA24_2 == KW_FROM || LA24_2 == KW_IN
						|| LA24_2 == KW_IS || LA24_2 == KW_LIKE
						|| LA24_2 == KW_NOT || LA24_2 == KW_OR
						|| LA24_2 == KW_REGEXP || LA24_2 == KW_RLIKE
						|| (LA24_2 >= LESSTHAN && LA24_2 <= LSQUARE)
						|| (LA24_2 >= MINUS && LA24_2 <= NOTEQUAL)
						|| LA24_2 == PLUS || LA24_2 == STAR)) {
					alt24 = 1;
				}

				else {
					if (state.backtracking > 0) {
						state.failed = true;
						return retval;
					}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae = new NoViableAltException(
								"", 24, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
				break;
			case STAR: {
				alt24 = 2;
			}
				break;
			default:
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 24, 0,
						input);
				throw nvae;
			}
			switch (alt24) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:400:3: expression
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_expression_in_selectExpression2126);
				expression52 = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, expression52.getTree());

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:401:5: tableAllColumns
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_tableAllColumns_in_selectExpression2132);
				tableAllColumns53 = tableAllColumns();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, tableAllColumns53.getTree());

			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "selectExpression"

	public static class tableAllColumns_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "tableAllColumns"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:404:1: tableAllColumns : (
	// STAR -> ^( TOK_ALLCOLREF ) | tableName DOT STAR -> ^( TOK_ALLCOLREF
	// tableName ) );
	public final TrcParser.tableAllColumns_return tableAllColumns()
			throws RecognitionException {
		TrcParser.tableAllColumns_return retval = new TrcParser.tableAllColumns_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token STAR54 = null;
		Token DOT56 = null;
		Token STAR57 = null;
		ParserRuleReturnScope tableName55 = null;

		CommonTree STAR54_tree = null;
		CommonTree DOT56_tree = null;
		CommonTree STAR57_tree = null;
		RewriteRuleTokenStream stream_STAR = new RewriteRuleTokenStream(
				adaptor, "token STAR");
		RewriteRuleTokenStream stream_DOT = new RewriteRuleTokenStream(adaptor,
				"token DOT");
		RewriteRuleSubtreeStream stream_tableName = new RewriteRuleSubtreeStream(
				adaptor, "rule tableName");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:405:3: ( STAR -> ^(
			// TOK_ALLCOLREF ) | tableName DOT STAR -> ^( TOK_ALLCOLREF
			// tableName ) )
			int alt25 = 2;
			int LA25_0 = input.LA(1);
			if ((LA25_0 == STAR)) {
				alt25 = 1;
			} else if ((LA25_0 == Identifier)) {
				alt25 = 2;
			}

			else {
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 25, 0,
						input);
				throw nvae;
			}

			switch (alt25) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:406:3: STAR
			{
				STAR54 = (Token) match(input, STAR,
						FOLLOW_STAR_in_tableAllColumns2147);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_STAR.add(STAR54);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 407:5: -> ^( TOK_ALLCOLREF )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:408:7: ^(
						// TOK_ALLCOLREF )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_ALLCOLREF,
											"TOK_ALLCOLREF"), root_1);
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:409:5: tableName DOT
			// STAR
			{
				pushFollow(FOLLOW_tableName_in_tableAllColumns2169);
				tableName55 = tableName();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_tableName.add(tableName55.getTree());
				DOT56 = (Token) match(input, DOT,
						FOLLOW_DOT_in_tableAllColumns2171);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_DOT.add(DOT56);

				STAR57 = (Token) match(input, STAR,
						FOLLOW_STAR_in_tableAllColumns2173);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_STAR.add(STAR57);

				// AST REWRITE
				// elements: tableName
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 410:5: -> ^( TOK_ALLCOLREF tableName )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:411:7: ^(
						// TOK_ALLCOLREF tableName )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_ALLCOLREF,
											"TOK_ALLCOLREF"), root_1);
							adaptor.addChild(root_1,
									stream_tableName.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "tableAllColumns"

	public static class tableOrColumn_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "tableOrColumn"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:414:1: tableOrColumn : (
	// Identifier -> ^( TOK_TABLE_OR_COL Identifier ) | IdentifierRef -> ^(
	// TOK_TABLE_OR_COL_REF IdentifierRef ) );
	public final TrcParser.tableOrColumn_return tableOrColumn()
			throws RecognitionException {
		TrcParser.tableOrColumn_return retval = new TrcParser.tableOrColumn_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token Identifier58 = null;
		Token IdentifierRef59 = null;

		CommonTree Identifier58_tree = null;
		CommonTree IdentifierRef59_tree = null;
		RewriteRuleTokenStream stream_IdentifierRef = new RewriteRuleTokenStream(
				adaptor, "token IdentifierRef");
		RewriteRuleTokenStream stream_Identifier = new RewriteRuleTokenStream(
				adaptor, "token Identifier");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:415:3: ( Identifier ->
			// ^( TOK_TABLE_OR_COL Identifier ) | IdentifierRef -> ^(
			// TOK_TABLE_OR_COL_REF IdentifierRef ) )
			int alt26 = 2;
			int LA26_0 = input.LA(1);
			if ((LA26_0 == Identifier)) {
				alt26 = 1;
			} else if ((LA26_0 == IdentifierRef)) {
				alt26 = 2;
			}

			else {
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 26, 0,
						input);
				throw nvae;
			}

			switch (alt26) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:416:3: Identifier
			{
				Identifier58 = (Token) match(input, Identifier,
						FOLLOW_Identifier_in_tableOrColumn2206);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_Identifier.add(Identifier58);

				// AST REWRITE
				// elements: Identifier
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 417:5: -> ^( TOK_TABLE_OR_COL Identifier )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:418:7: ^(
						// TOK_TABLE_OR_COL Identifier )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(
											TOK_TABLE_OR_COL,
											"TOK_TABLE_OR_COL"), root_1);
							adaptor.addChild(root_1,
									stream_Identifier.nextNode());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:419:5: IdentifierRef
			{
				IdentifierRef59 = (Token) match(input, IdentifierRef,
						FOLLOW_IdentifierRef_in_tableOrColumn2230);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_IdentifierRef.add(IdentifierRef59);

				// AST REWRITE
				// elements: IdentifierRef
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 420:5: -> ^( TOK_TABLE_OR_COL_REF IdentifierRef )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:421:7: ^(
						// TOK_TABLE_OR_COL_REF IdentifierRef )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(
											TOK_TABLE_OR_COL_REF,
											"TOK_TABLE_OR_COL_REF"), root_1);
							adaptor.addChild(root_1,
									stream_IdentifierRef.nextNode());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "tableOrColumn"

	public static class expression_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "expression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:424:1: expression :
	// precedenceOrExpression ;
	public final TrcParser.expression_return expression()
			throws RecognitionException {
		TrcParser.expression_return retval = new TrcParser.expression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope precedenceOrExpression60 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:425:3: (
			// precedenceOrExpression )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:426:3:
			// precedenceOrExpression
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_precedenceOrExpression_in_expression2263);
				precedenceOrExpression60 = precedenceOrExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, precedenceOrExpression60.getTree());

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "expression"

	public static class precedenceOrExpression_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceOrExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:429:1: precedenceOrExpression
	// : precedenceAndExpression ( precedenceOrOperator ^
	// precedenceAndExpression )* ;
	public final TrcParser.precedenceOrExpression_return precedenceOrExpression()
			throws RecognitionException {
		TrcParser.precedenceOrExpression_return retval = new TrcParser.precedenceOrExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope precedenceAndExpression61 = null;
		ParserRuleReturnScope precedenceOrOperator62 = null;
		ParserRuleReturnScope precedenceAndExpression63 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:430:3: (
			// precedenceAndExpression ( precedenceOrOperator ^
			// precedenceAndExpression )* )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:431:3:
			// precedenceAndExpression ( precedenceOrOperator ^
			// precedenceAndExpression )*
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_precedenceAndExpression_in_precedenceOrExpression2278);
				precedenceAndExpression61 = precedenceAndExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0,
							precedenceAndExpression61.getTree());

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:431:27: (
				// precedenceOrOperator ^ precedenceAndExpression )*
				loop27: while (true) {
					int alt27 = 2;
					int LA27_0 = input.LA(1);
					if ((LA27_0 == KW_OR)) {
						alt27 = 1;
					}

					switch (alt27) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:431:28:
					// precedenceOrOperator ^ precedenceAndExpression
					{
						pushFollow(FOLLOW_precedenceOrOperator_in_precedenceOrExpression2281);
						precedenceOrOperator62 = precedenceOrOperator();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							root_0 = (CommonTree) adaptor.becomeRoot(
									precedenceOrOperator62.getTree(), root_0);
						pushFollow(FOLLOW_precedenceAndExpression_in_precedenceOrExpression2284);
						precedenceAndExpression63 = precedenceAndExpression();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							adaptor.addChild(root_0,
									precedenceAndExpression63.getTree());

					}
						break;

					default:
						break loop27;
					}
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceOrExpression"

	public static class precedenceAndExpression_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceAndExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:434:1: precedenceAndExpression
	// : precedenceNotExpression ( precedenceAndOperator ^
	// precedenceNotExpression )* ;
	public final TrcParser.precedenceAndExpression_return precedenceAndExpression()
			throws RecognitionException {
		TrcParser.precedenceAndExpression_return retval = new TrcParser.precedenceAndExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope precedenceNotExpression64 = null;
		ParserRuleReturnScope precedenceAndOperator65 = null;
		ParserRuleReturnScope precedenceNotExpression66 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:435:3: (
			// precedenceNotExpression ( precedenceAndOperator ^
			// precedenceNotExpression )* )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:436:3:
			// precedenceNotExpression ( precedenceAndOperator ^
			// precedenceNotExpression )*
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_precedenceNotExpression_in_precedenceAndExpression2301);
				precedenceNotExpression64 = precedenceNotExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0,
							precedenceNotExpression64.getTree());

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:436:27: (
				// precedenceAndOperator ^ precedenceNotExpression )*
				loop28: while (true) {
					int alt28 = 2;
					int LA28_0 = input.LA(1);
					if ((LA28_0 == KW_AND)) {
						alt28 = 1;
					}

					switch (alt28) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:436:28:
					// precedenceAndOperator ^ precedenceNotExpression
					{
						pushFollow(FOLLOW_precedenceAndOperator_in_precedenceAndExpression2304);
						precedenceAndOperator65 = precedenceAndOperator();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							root_0 = (CommonTree) adaptor.becomeRoot(
									precedenceAndOperator65.getTree(), root_0);
						pushFollow(FOLLOW_precedenceNotExpression_in_precedenceAndExpression2307);
						precedenceNotExpression66 = precedenceNotExpression();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							adaptor.addChild(root_0,
									precedenceNotExpression66.getTree());

					}
						break;

					default:
						break loop28;
					}
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceAndExpression"

	public static class precedenceNotExpression_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceNotExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:439:1: precedenceNotExpression
	// : ( precedenceNotOperator ^)* precedenceEqualExpression ;
	public final TrcParser.precedenceNotExpression_return precedenceNotExpression()
			throws RecognitionException {
		TrcParser.precedenceNotExpression_return retval = new TrcParser.precedenceNotExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope precedenceNotOperator67 = null;
		ParserRuleReturnScope precedenceEqualExpression68 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:440:3: ( (
			// precedenceNotOperator ^)* precedenceEqualExpression )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:441:3: (
			// precedenceNotOperator ^)* precedenceEqualExpression
			{
				root_0 = (CommonTree) adaptor.nil();

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:441:3: (
				// precedenceNotOperator ^)*
				loop29: while (true) {
					int alt29 = 2;
					int LA29_0 = input.LA(1);
					if ((LA29_0 == KW_NOT)) {
						alt29 = 1;
					}

					switch (alt29) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:441:4:
					// precedenceNotOperator ^
					{
						pushFollow(FOLLOW_precedenceNotOperator_in_precedenceNotExpression2325);
						precedenceNotOperator67 = precedenceNotOperator();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							root_0 = (CommonTree) adaptor.becomeRoot(
									precedenceNotOperator67.getTree(), root_0);
					}
						break;

					default:
						break loop29;
					}
				}

				pushFollow(FOLLOW_precedenceEqualExpression_in_precedenceNotExpression2330);
				precedenceEqualExpression68 = precedenceEqualExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0,
							precedenceEqualExpression68.getTree());

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceNotExpression"

	public static class precedenceEqualExpression_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceEqualExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:444:1:
	// precedenceEqualExpression : precedenceBitwiseOrExpression (
	// precedenceEqualOperator ^ precedenceBitwiseOrExpression |
	// precedenceInNotInOperator ^ expressions )* ;
	public final TrcParser.precedenceEqualExpression_return precedenceEqualExpression()
			throws RecognitionException {
		TrcParser.precedenceEqualExpression_return retval = new TrcParser.precedenceEqualExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope precedenceBitwiseOrExpression69 = null;
		ParserRuleReturnScope precedenceEqualOperator70 = null;
		ParserRuleReturnScope precedenceBitwiseOrExpression71 = null;
		ParserRuleReturnScope precedenceInNotInOperator72 = null;
		ParserRuleReturnScope expressions73 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:445:3: (
			// precedenceBitwiseOrExpression ( precedenceEqualOperator ^
			// precedenceBitwiseOrExpression | precedenceInNotInOperator ^
			// expressions )* )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:446:3:
			// precedenceBitwiseOrExpression ( precedenceEqualOperator ^
			// precedenceBitwiseOrExpression | precedenceInNotInOperator ^
			// expressions )*
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_precedenceBitwiseOrExpression_in_precedenceEqualExpression2345);
				precedenceBitwiseOrExpression69 = precedenceBitwiseOrExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0,
							precedenceBitwiseOrExpression69.getTree());

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:447:3: (
				// precedenceEqualOperator ^ precedenceBitwiseOrExpression |
				// precedenceInNotInOperator ^ expressions )*
				loop30: while (true) {
					int alt30 = 3;
					switch (input.LA(1)) {
					case KW_NOT: {
						int LA30_2 = input.LA(2);
						if ((LA30_2 == KW_LIKE || LA30_2 == KW_REGEXP || LA30_2 == KW_RLIKE)) {
							alt30 = 1;
						} else if ((LA30_2 == KW_IN)) {
							alt30 = 2;
						}

					}
						break;
					case EQUAL:
					case GREATERTHAN:
					case GREATERTHANOREQUALTO:
					case KW_LIKE:
					case KW_REGEXP:
					case KW_RLIKE:
					case LESSTHAN:
					case LESSTHANOREQUALTO:
					case NOTEQUAL: {
						alt30 = 1;
					}
						break;
					case KW_IN: {
						alt30 = 2;
					}
						break;
					}
					switch (alt30) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:448:5:
					// precedenceEqualOperator ^ precedenceBitwiseOrExpression
					{
						pushFollow(FOLLOW_precedenceEqualOperator_in_precedenceEqualExpression2355);
						precedenceEqualOperator70 = precedenceEqualOperator();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							root_0 = (CommonTree) adaptor
									.becomeRoot(
											precedenceEqualOperator70.getTree(),
											root_0);
						pushFollow(FOLLOW_precedenceBitwiseOrExpression_in_precedenceEqualExpression2358);
						precedenceBitwiseOrExpression71 = precedenceBitwiseOrExpression();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							adaptor.addChild(root_0,
									precedenceBitwiseOrExpression71.getTree());

					}
						break;
					case 2:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:449:7:
					// precedenceInNotInOperator ^ expressions
					{
						pushFollow(FOLLOW_precedenceInNotInOperator_in_precedenceEqualExpression2366);
						precedenceInNotInOperator72 = precedenceInNotInOperator();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							root_0 = (CommonTree) adaptor.becomeRoot(
									precedenceInNotInOperator72.getTree(),
									root_0);
						pushFollow(FOLLOW_expressions_in_precedenceEqualExpression2369);
						expressions73 = expressions();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							adaptor.addChild(root_0, expressions73.getTree());

					}
						break;

					default:
						break loop30;
					}
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceEqualExpression"

	public static class precedenceBitwiseOrExpression_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceBitwiseOrExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:453:1:
	// precedenceBitwiseOrExpression : precedenceAmpersandExpression (
	// precedenceBitwiseOrOperator ^ precedenceAmpersandExpression )* ;
	public final TrcParser.precedenceBitwiseOrExpression_return precedenceBitwiseOrExpression()
			throws RecognitionException {
		TrcParser.precedenceBitwiseOrExpression_return retval = new TrcParser.precedenceBitwiseOrExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope precedenceAmpersandExpression74 = null;
		ParserRuleReturnScope precedenceBitwiseOrOperator75 = null;
		ParserRuleReturnScope precedenceAmpersandExpression76 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:454:3: (
			// precedenceAmpersandExpression ( precedenceBitwiseOrOperator ^
			// precedenceAmpersandExpression )* )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:455:3:
			// precedenceAmpersandExpression ( precedenceBitwiseOrOperator ^
			// precedenceAmpersandExpression )*
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_precedenceAmpersandExpression_in_precedenceBitwiseOrExpression2389);
				precedenceAmpersandExpression74 = precedenceAmpersandExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0,
							precedenceAmpersandExpression74.getTree());

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:455:33: (
				// precedenceBitwiseOrOperator ^ precedenceAmpersandExpression
				// )*
				loop31: while (true) {
					int alt31 = 2;
					int LA31_0 = input.LA(1);
					if ((LA31_0 == BITWISEOR)) {
						alt31 = 1;
					}

					switch (alt31) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:455:34:
					// precedenceBitwiseOrOperator ^
					// precedenceAmpersandExpression
					{
						pushFollow(FOLLOW_precedenceBitwiseOrOperator_in_precedenceBitwiseOrExpression2392);
						precedenceBitwiseOrOperator75 = precedenceBitwiseOrOperator();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							root_0 = (CommonTree) adaptor.becomeRoot(
									precedenceBitwiseOrOperator75.getTree(),
									root_0);
						pushFollow(FOLLOW_precedenceAmpersandExpression_in_precedenceBitwiseOrExpression2395);
						precedenceAmpersandExpression76 = precedenceAmpersandExpression();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							adaptor.addChild(root_0,
									precedenceAmpersandExpression76.getTree());

					}
						break;

					default:
						break loop31;
					}
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceBitwiseOrExpression"

	public static class precedenceAmpersandExpression_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceAmpersandExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:458:1:
	// precedenceAmpersandExpression : precedencePlusExpression (
	// precedenceAmpersandOperator ^ precedencePlusExpression )* ;
	public final TrcParser.precedenceAmpersandExpression_return precedenceAmpersandExpression()
			throws RecognitionException {
		TrcParser.precedenceAmpersandExpression_return retval = new TrcParser.precedenceAmpersandExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope precedencePlusExpression77 = null;
		ParserRuleReturnScope precedenceAmpersandOperator78 = null;
		ParserRuleReturnScope precedencePlusExpression79 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:459:3: (
			// precedencePlusExpression ( precedenceAmpersandOperator ^
			// precedencePlusExpression )* )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:460:3:
			// precedencePlusExpression ( precedenceAmpersandOperator ^
			// precedencePlusExpression )*
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_precedencePlusExpression_in_precedenceAmpersandExpression2412);
				precedencePlusExpression77 = precedencePlusExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0,
							precedencePlusExpression77.getTree());

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:460:28: (
				// precedenceAmpersandOperator ^ precedencePlusExpression )*
				loop32: while (true) {
					int alt32 = 2;
					int LA32_0 = input.LA(1);
					if ((LA32_0 == AMPERSAND)) {
						alt32 = 1;
					}

					switch (alt32) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:460:29:
					// precedenceAmpersandOperator ^ precedencePlusExpression
					{
						pushFollow(FOLLOW_precedenceAmpersandOperator_in_precedenceAmpersandExpression2415);
						precedenceAmpersandOperator78 = precedenceAmpersandOperator();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							root_0 = (CommonTree) adaptor.becomeRoot(
									precedenceAmpersandOperator78.getTree(),
									root_0);
						pushFollow(FOLLOW_precedencePlusExpression_in_precedenceAmpersandExpression2418);
						precedencePlusExpression79 = precedencePlusExpression();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							adaptor.addChild(root_0,
									precedencePlusExpression79.getTree());

					}
						break;

					default:
						break loop32;
					}
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceAmpersandExpression"

	public static class precedencePlusExpression_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedencePlusExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:463:1:
	// precedencePlusExpression : precedenceStarExpression (
	// precedencePlusOperator ^ precedenceStarExpression )* ;
	public final TrcParser.precedencePlusExpression_return precedencePlusExpression()
			throws RecognitionException {
		TrcParser.precedencePlusExpression_return retval = new TrcParser.precedencePlusExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope precedenceStarExpression80 = null;
		ParserRuleReturnScope precedencePlusOperator81 = null;
		ParserRuleReturnScope precedenceStarExpression82 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:464:3: (
			// precedenceStarExpression ( precedencePlusOperator ^
			// precedenceStarExpression )* )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:465:3:
			// precedenceStarExpression ( precedencePlusOperator ^
			// precedenceStarExpression )*
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_precedenceStarExpression_in_precedencePlusExpression2435);
				precedenceStarExpression80 = precedenceStarExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0,
							precedenceStarExpression80.getTree());

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:465:28: (
				// precedencePlusOperator ^ precedenceStarExpression )*
				loop33: while (true) {
					int alt33 = 2;
					alt33 = dfa33.predict(input);
					switch (alt33) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:465:29:
					// precedencePlusOperator ^ precedenceStarExpression
					{
						pushFollow(FOLLOW_precedencePlusOperator_in_precedencePlusExpression2438);
						precedencePlusOperator81 = precedencePlusOperator();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							root_0 = (CommonTree) adaptor.becomeRoot(
									precedencePlusOperator81.getTree(), root_0);
						pushFollow(FOLLOW_precedenceStarExpression_in_precedencePlusExpression2441);
						precedenceStarExpression82 = precedenceStarExpression();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							adaptor.addChild(root_0,
									precedenceStarExpression82.getTree());

					}
						break;

					default:
						break loop33;
					}
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedencePlusExpression"

	public static class precedenceStarExpression_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceStarExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:468:1:
	// precedenceStarExpression : precedenceBitwiseXorExpression (
	// precedenceStarOperator ^ precedenceBitwiseXorExpression )* ;
	public final TrcParser.precedenceStarExpression_return precedenceStarExpression()
			throws RecognitionException {
		TrcParser.precedenceStarExpression_return retval = new TrcParser.precedenceStarExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope precedenceBitwiseXorExpression83 = null;
		ParserRuleReturnScope precedenceStarOperator84 = null;
		ParserRuleReturnScope precedenceBitwiseXorExpression85 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:469:3: (
			// precedenceBitwiseXorExpression ( precedenceStarOperator ^
			// precedenceBitwiseXorExpression )* )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:470:3:
			// precedenceBitwiseXorExpression ( precedenceStarOperator ^
			// precedenceBitwiseXorExpression )*
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_precedenceBitwiseXorExpression_in_precedenceStarExpression2458);
				precedenceBitwiseXorExpression83 = precedenceBitwiseXorExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0,
							precedenceBitwiseXorExpression83.getTree());

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:470:34: (
				// precedenceStarOperator ^ precedenceBitwiseXorExpression )*
				loop34: while (true) {
					int alt34 = 2;
					int LA34_0 = input.LA(1);
					if (((LA34_0 >= DIV && LA34_0 <= DIVIDE) || LA34_0 == MOD || LA34_0 == STAR)) {
						alt34 = 1;
					}

					switch (alt34) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:470:35:
					// precedenceStarOperator ^ precedenceBitwiseXorExpression
					{
						pushFollow(FOLLOW_precedenceStarOperator_in_precedenceStarExpression2461);
						precedenceStarOperator84 = precedenceStarOperator();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							root_0 = (CommonTree) adaptor.becomeRoot(
									precedenceStarOperator84.getTree(), root_0);
						pushFollow(FOLLOW_precedenceBitwiseXorExpression_in_precedenceStarExpression2464);
						precedenceBitwiseXorExpression85 = precedenceBitwiseXorExpression();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							adaptor.addChild(root_0,
									precedenceBitwiseXorExpression85.getTree());

					}
						break;

					default:
						break loop34;
					}
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceStarExpression"

	public static class precedenceBitwiseXorExpression_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceBitwiseXorExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:473:1:
	// precedenceBitwiseXorExpression : precedenceUnarySuffixExpression (
	// precedenceBitwiseXorOperator ^ precedenceUnarySuffixExpression )* ;
	public final TrcParser.precedenceBitwiseXorExpression_return precedenceBitwiseXorExpression()
			throws RecognitionException {
		TrcParser.precedenceBitwiseXorExpression_return retval = new TrcParser.precedenceBitwiseXorExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope precedenceUnarySuffixExpression86 = null;
		ParserRuleReturnScope precedenceBitwiseXorOperator87 = null;
		ParserRuleReturnScope precedenceUnarySuffixExpression88 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:474:3: (
			// precedenceUnarySuffixExpression ( precedenceBitwiseXorOperator ^
			// precedenceUnarySuffixExpression )* )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:475:3:
			// precedenceUnarySuffixExpression ( precedenceBitwiseXorOperator ^
			// precedenceUnarySuffixExpression )*
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_precedenceUnarySuffixExpression_in_precedenceBitwiseXorExpression2481);
				precedenceUnarySuffixExpression86 = precedenceUnarySuffixExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0,
							precedenceUnarySuffixExpression86.getTree());

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:475:35: (
				// precedenceBitwiseXorOperator ^
				// precedenceUnarySuffixExpression )*
				loop35: while (true) {
					int alt35 = 2;
					int LA35_0 = input.LA(1);
					if ((LA35_0 == BITWISEXOR)) {
						alt35 = 1;
					}

					switch (alt35) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:475:36:
					// precedenceBitwiseXorOperator ^
					// precedenceUnarySuffixExpression
					{
						pushFollow(FOLLOW_precedenceBitwiseXorOperator_in_precedenceBitwiseXorExpression2484);
						precedenceBitwiseXorOperator87 = precedenceBitwiseXorOperator();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							root_0 = (CommonTree) adaptor.becomeRoot(
									precedenceBitwiseXorOperator87.getTree(),
									root_0);
						pushFollow(FOLLOW_precedenceUnarySuffixExpression_in_precedenceBitwiseXorExpression2487);
						precedenceUnarySuffixExpression88 = precedenceUnarySuffixExpression();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							adaptor.addChild(root_0,
									precedenceUnarySuffixExpression88.getTree());

					}
						break;

					default:
						break loop35;
					}
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceBitwiseXorExpression"

	public static class precedenceUnarySuffixExpression_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceUnarySuffixExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:478:1:
	// precedenceUnarySuffixExpression : precedenceUnaryPrefixExpression (a=
	// KW_IS nullCondition )? -> {$a != null}? ^( TOK_FUNCTION nullCondition
	// precedenceUnaryPrefixExpression ) -> precedenceUnaryPrefixExpression ;
	public final TrcParser.precedenceUnarySuffixExpression_return precedenceUnarySuffixExpression()
			throws RecognitionException {
		TrcParser.precedenceUnarySuffixExpression_return retval = new TrcParser.precedenceUnarySuffixExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token a = null;
		ParserRuleReturnScope precedenceUnaryPrefixExpression89 = null;
		ParserRuleReturnScope nullCondition90 = null;

		CommonTree a_tree = null;
		RewriteRuleTokenStream stream_KW_IS = new RewriteRuleTokenStream(
				adaptor, "token KW_IS");
		RewriteRuleSubtreeStream stream_precedenceUnaryPrefixExpression = new RewriteRuleSubtreeStream(
				adaptor, "rule precedenceUnaryPrefixExpression");
		RewriteRuleSubtreeStream stream_nullCondition = new RewriteRuleSubtreeStream(
				adaptor, "rule nullCondition");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:479:3: (
			// precedenceUnaryPrefixExpression (a= KW_IS nullCondition )? -> {$a
			// != null}? ^( TOK_FUNCTION nullCondition
			// precedenceUnaryPrefixExpression ) ->
			// precedenceUnaryPrefixExpression )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:480:3:
			// precedenceUnaryPrefixExpression (a= KW_IS nullCondition )?
			{
				pushFollow(FOLLOW_precedenceUnaryPrefixExpression_in_precedenceUnarySuffixExpression2504);
				precedenceUnaryPrefixExpression89 = precedenceUnaryPrefixExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_precedenceUnaryPrefixExpression
							.add(precedenceUnaryPrefixExpression89.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:480:35: (a= KW_IS
				// nullCondition )?
				int alt36 = 2;
				int LA36_0 = input.LA(1);
				if ((LA36_0 == KW_IS)) {
					alt36 = 1;
				}
				switch (alt36) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:480:36: a= KW_IS
				// nullCondition
				{
					a = (Token) match(input, KW_IS,
							FOLLOW_KW_IS_in_precedenceUnarySuffixExpression2509);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_KW_IS.add(a);

					pushFollow(FOLLOW_nullCondition_in_precedenceUnarySuffixExpression2511);
					nullCondition90 = nullCondition();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_nullCondition.add(nullCondition90.getTree());
				}
					break;

				}

				// AST REWRITE
				// elements: precedenceUnaryPrefixExpression, nullCondition,
				// precedenceUnaryPrefixExpression
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 481:5: -> {$a != null}? ^( TOK_FUNCTION nullCondition
					// precedenceUnaryPrefixExpression )
					if (a != null) {
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:482:7: ^(
						// TOK_FUNCTION nullCondition
						// precedenceUnaryPrefixExpression )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_FUNCTION,
											"TOK_FUNCTION"), root_1);
							adaptor.addChild(root_1,
									stream_nullCondition.nextTree());
							adaptor.addChild(root_1,
									stream_precedenceUnaryPrefixExpression
											.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					else // 483:5: -> precedenceUnaryPrefixExpression
					{
						adaptor.addChild(root_0,
								stream_precedenceUnaryPrefixExpression
										.nextTree());
					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceUnarySuffixExpression"

	public static class precedenceUnaryPrefixExpression_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceUnaryPrefixExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:486:1:
	// precedenceUnaryPrefixExpression : ( precedenceUnaryOperator ^)*
	// precedenceFieldExpression ;
	public final TrcParser.precedenceUnaryPrefixExpression_return precedenceUnaryPrefixExpression()
			throws RecognitionException {
		TrcParser.precedenceUnaryPrefixExpression_return retval = new TrcParser.precedenceUnaryPrefixExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope precedenceUnaryOperator91 = null;
		ParserRuleReturnScope precedenceFieldExpression92 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:487:3: ( (
			// precedenceUnaryOperator ^)* precedenceFieldExpression )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:488:3: (
			// precedenceUnaryOperator ^)* precedenceFieldExpression
			{
				root_0 = (CommonTree) adaptor.nil();

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:488:3: (
				// precedenceUnaryOperator ^)*
				loop37: while (true) {
					int alt37 = 2;
					int LA37_0 = input.LA(1);
					if ((LA37_0 == MINUS || LA37_0 == PLUS || LA37_0 == TILDE)) {
						alt37 = 1;
					}

					switch (alt37) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:488:4:
					// precedenceUnaryOperator ^
					{
						pushFollow(FOLLOW_precedenceUnaryOperator_in_precedenceUnaryPrefixExpression2559);
						precedenceUnaryOperator91 = precedenceUnaryOperator();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							root_0 = (CommonTree) adaptor
									.becomeRoot(
											precedenceUnaryOperator91.getTree(),
											root_0);
					}
						break;

					default:
						break loop37;
					}
				}

				pushFollow(FOLLOW_precedenceFieldExpression_in_precedenceUnaryPrefixExpression2564);
				precedenceFieldExpression92 = precedenceFieldExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0,
							precedenceFieldExpression92.getTree());

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceUnaryPrefixExpression"

	public static class precedenceFieldExpression_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceFieldExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:491:1:
	// precedenceFieldExpression : atomExpression ( ( LSQUARE ^ expression
	// RSQUARE !) | ( DOT ^ Identifier ) )* ;
	public final TrcParser.precedenceFieldExpression_return precedenceFieldExpression()
			throws RecognitionException {
		TrcParser.precedenceFieldExpression_return retval = new TrcParser.precedenceFieldExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token LSQUARE94 = null;
		Token RSQUARE96 = null;
		Token DOT97 = null;
		Token Identifier98 = null;
		ParserRuleReturnScope atomExpression93 = null;
		ParserRuleReturnScope expression95 = null;

		CommonTree LSQUARE94_tree = null;
		CommonTree RSQUARE96_tree = null;
		CommonTree DOT97_tree = null;
		CommonTree Identifier98_tree = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:492:3: (
			// atomExpression ( ( LSQUARE ^ expression RSQUARE !) | ( DOT ^
			// Identifier ) )* )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:493:3: atomExpression
			// ( ( LSQUARE ^ expression RSQUARE !) | ( DOT ^ Identifier ) )*
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_atomExpression_in_precedenceFieldExpression2579);
				atomExpression93 = atomExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, atomExpression93.getTree());

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:494:3: ( ( LSQUARE
				// ^ expression RSQUARE !) | ( DOT ^ Identifier ) )*
				loop38: while (true) {
					int alt38 = 3;
					int LA38_0 = input.LA(1);
					if ((LA38_0 == LSQUARE)) {
						alt38 = 1;
					} else if ((LA38_0 == DOT)) {
						alt38 = 2;
					}

					switch (alt38) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:495:5: (
					// LSQUARE ^ expression RSQUARE !)
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:495:5: (
						// LSQUARE ^ expression RSQUARE !)
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:495:6:
						// LSQUARE ^ expression RSQUARE !
						{
							LSQUARE94 = (Token) match(input, LSQUARE,
									FOLLOW_LSQUARE_in_precedenceFieldExpression2590);
							if (state.failed)
								return retval;
							if (state.backtracking == 0) {
								LSQUARE94_tree = (CommonTree) adaptor
										.create(LSQUARE94);
								root_0 = (CommonTree) adaptor.becomeRoot(
										LSQUARE94_tree, root_0);
							}

							pushFollow(FOLLOW_expression_in_precedenceFieldExpression2593);
							expression95 = expression();
							state._fsp--;
							if (state.failed)
								return retval;
							if (state.backtracking == 0)
								adaptor.addChild(root_0, expression95.getTree());

							RSQUARE96 = (Token) match(input, RSQUARE,
									FOLLOW_RSQUARE_in_precedenceFieldExpression2595);
							if (state.failed)
								return retval;
						}

					}
						break;
					case 2:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:496:7: ( DOT ^
					// Identifier )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:496:7: (
						// DOT ^ Identifier )
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:496:8: DOT
						// ^ Identifier
						{
							DOT97 = (Token) match(input, DOT,
									FOLLOW_DOT_in_precedenceFieldExpression2606);
							if (state.failed)
								return retval;
							if (state.backtracking == 0) {
								DOT97_tree = (CommonTree) adaptor.create(DOT97);
								root_0 = (CommonTree) adaptor.becomeRoot(
										DOT97_tree, root_0);
							}

							Identifier98 = (Token) match(input, Identifier,
									FOLLOW_Identifier_in_precedenceFieldExpression2609);
							if (state.failed)
								return retval;
							if (state.backtracking == 0) {
								Identifier98_tree = (CommonTree) adaptor
										.create(Identifier98);
								adaptor.addChild(root_0, Identifier98_tree);
							}

						}

					}
						break;

					default:
						break loop38;
					}
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceFieldExpression"

	public static class atomExpression_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "atomExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:500:1: atomExpression : (
	// KW_NULL -> TOK_NULL | constant | function | foreach | execute |
	// castExpression | caseExpression | whenExpression | tableOrColumn |
	// KW_AGGR_TIME | KW_ATTRIBUTES | LPAREN ! expression RPAREN !);
	public final TrcParser.atomExpression_return atomExpression()
			throws RecognitionException {
		TrcParser.atomExpression_return retval = new TrcParser.atomExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_NULL99 = null;
		Token KW_AGGR_TIME108 = null;
		Token KW_ATTRIBUTES109 = null;
		Token LPAREN110 = null;
		Token RPAREN112 = null;
		ParserRuleReturnScope constant100 = null;
		ParserRuleReturnScope function101 = null;
		ParserRuleReturnScope foreach102 = null;
		ParserRuleReturnScope execute103 = null;
		ParserRuleReturnScope castExpression104 = null;
		ParserRuleReturnScope caseExpression105 = null;
		ParserRuleReturnScope whenExpression106 = null;
		ParserRuleReturnScope tableOrColumn107 = null;
		ParserRuleReturnScope expression111 = null;

		CommonTree KW_NULL99_tree = null;
		CommonTree KW_AGGR_TIME108_tree = null;
		CommonTree KW_ATTRIBUTES109_tree = null;
		CommonTree LPAREN110_tree = null;
		CommonTree RPAREN112_tree = null;
		RewriteRuleTokenStream stream_KW_NULL = new RewriteRuleTokenStream(
				adaptor, "token KW_NULL");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:501:3: ( KW_NULL ->
			// TOK_NULL | constant | function | foreach | execute |
			// castExpression | caseExpression | whenExpression | tableOrColumn
			// | KW_AGGR_TIME | KW_ATTRIBUTES | LPAREN ! expression RPAREN !)
			int alt39 = 12;
			switch (input.LA(1)) {
			case KW_NULL: {
				alt39 = 1;
			}
				break;
			case BigintLiteral:
			case CharSetName:
			case KW_FALSE:
			case KW_TRUE:
			case Number:
			case SmallintLiteral:
			case StringLiteral:
			case TinyintLiteral: {
				alt39 = 2;
			}
				break;
			case Identifier: {
				int LA39_10 = input.LA(2);
				if ((synpred44_Trc())) {
					alt39 = 3;
				} else if ((synpred50_Trc())) {
					alt39 = 9;
				}

				else {
					if (state.backtracking > 0) {
						state.failed = true;
						return retval;
					}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae = new NoViableAltException(
								"", 39, 10, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
				break;
			case KW_FOREACH: {
				alt39 = 4;
			}
				break;
			case KW_EXECUTE: {
				alt39 = 5;
			}
				break;
			case KW_CAST: {
				alt39 = 6;
			}
				break;
			case KW_CASE: {
				int LA39_14 = input.LA(2);
				if ((synpred48_Trc())) {
					alt39 = 7;
				} else if ((synpred49_Trc())) {
					alt39 = 8;
				}

				else {
					if (state.backtracking > 0) {
						state.failed = true;
						return retval;
					}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae = new NoViableAltException(
								"", 39, 14, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
				break;
			case KW_ARRAY:
			case KW_IF:
			case KW_MAP:
			case KW_STRUCT:
			case KW_UNIONTYPE: {
				alt39 = 3;
			}
				break;
			case IdentifierRef: {
				alt39 = 9;
			}
				break;
			case KW_AGGR_TIME: {
				alt39 = 10;
			}
				break;
			case KW_ATTRIBUTES: {
				alt39 = 11;
			}
				break;
			case LPAREN: {
				alt39 = 12;
			}
				break;
			default:
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 39, 0,
						input);
				throw nvae;
			}
			switch (alt39) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:502:3: KW_NULL
			{
				KW_NULL99 = (Token) match(input, KW_NULL,
						FOLLOW_KW_NULL_in_atomExpression2630);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_NULL.add(KW_NULL99);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 503:5: -> TOK_NULL
					{
						adaptor.addChild(root_0, (CommonTree) adaptor.create(
								TOK_NULL, "TOK_NULL"));
					}

					retval.tree = root_0;
				}

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:504:5: constant
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_constant_in_atomExpression2644);
				constant100 = constant();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, constant100.getTree());

			}
				break;
			case 3:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:505:5: function
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_function_in_atomExpression2650);
				function101 = function();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, function101.getTree());

			}
				break;
			case 4:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:506:5: foreach
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_foreach_in_atomExpression2656);
				foreach102 = foreach();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, foreach102.getTree());

			}
				break;
			case 5:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:507:5: execute
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_execute_in_atomExpression2662);
				execute103 = execute();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, execute103.getTree());

			}
				break;
			case 6:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:508:5: castExpression
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_castExpression_in_atomExpression2668);
				castExpression104 = castExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, castExpression104.getTree());

			}
				break;
			case 7:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:509:5: caseExpression
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_caseExpression_in_atomExpression2674);
				caseExpression105 = caseExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, caseExpression105.getTree());

			}
				break;
			case 8:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:510:5: whenExpression
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_whenExpression_in_atomExpression2680);
				whenExpression106 = whenExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, whenExpression106.getTree());

			}
				break;
			case 9:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:511:5: tableOrColumn
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_tableOrColumn_in_atomExpression2686);
				tableOrColumn107 = tableOrColumn();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, tableOrColumn107.getTree());

			}
				break;
			case 10:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:512:5: KW_AGGR_TIME
			{
				root_0 = (CommonTree) adaptor.nil();

				KW_AGGR_TIME108 = (Token) match(input, KW_AGGR_TIME,
						FOLLOW_KW_AGGR_TIME_in_atomExpression2692);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					KW_AGGR_TIME108_tree = (CommonTree) adaptor
							.create(KW_AGGR_TIME108);
					adaptor.addChild(root_0, KW_AGGR_TIME108_tree);
				}

			}
				break;
			case 11:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:513:5: KW_ATTRIBUTES
			{
				root_0 = (CommonTree) adaptor.nil();

				KW_ATTRIBUTES109 = (Token) match(input, KW_ATTRIBUTES,
						FOLLOW_KW_ATTRIBUTES_in_atomExpression2698);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					KW_ATTRIBUTES109_tree = (CommonTree) adaptor
							.create(KW_ATTRIBUTES109);
					adaptor.addChild(root_0, KW_ATTRIBUTES109_tree);
				}

			}
				break;
			case 12:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:514:5: LPAREN !
			// expression RPAREN !
			{
				root_0 = (CommonTree) adaptor.nil();

				LPAREN110 = (Token) match(input, LPAREN,
						FOLLOW_LPAREN_in_atomExpression2704);
				if (state.failed)
					return retval;
				pushFollow(FOLLOW_expression_in_atomExpression2707);
				expression111 = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, expression111.getTree());

				RPAREN112 = (Token) match(input, RPAREN,
						FOLLOW_RPAREN_in_atomExpression2709);
				if (state.failed)
					return retval;
			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "atomExpression"

	public static class constant_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "constant"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:517:1: constant : ( Number |
	// StringLiteral | stringLiteralSequence | BigintLiteral | SmallintLiteral |
	// TinyintLiteral | charSetStringLiteral | booleanValue );
	public final TrcParser.constant_return constant()
			throws RecognitionException {
		TrcParser.constant_return retval = new TrcParser.constant_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token Number113 = null;
		Token StringLiteral114 = null;
		Token BigintLiteral116 = null;
		Token SmallintLiteral117 = null;
		Token TinyintLiteral118 = null;
		ParserRuleReturnScope stringLiteralSequence115 = null;
		ParserRuleReturnScope charSetStringLiteral119 = null;
		ParserRuleReturnScope booleanValue120 = null;

		CommonTree Number113_tree = null;
		CommonTree StringLiteral114_tree = null;
		CommonTree BigintLiteral116_tree = null;
		CommonTree SmallintLiteral117_tree = null;
		CommonTree TinyintLiteral118_tree = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:518:3: ( Number |
			// StringLiteral | stringLiteralSequence | BigintLiteral |
			// SmallintLiteral | TinyintLiteral | charSetStringLiteral |
			// booleanValue )
			int alt40 = 8;
			switch (input.LA(1)) {
			case Number: {
				alt40 = 1;
			}
				break;
			case StringLiteral: {
				int LA40_2 = input.LA(2);
				if ((LA40_2 == EOF
						|| LA40_2 == AMPERSAND
						|| (LA40_2 >= BITWISEOR && LA40_2 <= BigintLiteral)
						|| LA40_2 == COMMA
						|| (LA40_2 >= CharSetName && LA40_2 <= DIVIDE)
						|| LA40_2 == DOT
						|| LA40_2 == EQUAL
						|| (LA40_2 >= GREATERTHAN && LA40_2 <= GREATERTHANOREQUALTO)
						|| (LA40_2 >= Identifier && LA40_2 <= KW_ACCUGLOBAL)
						|| LA40_2 == KW_AGGR_TIME || LA40_2 == KW_AND
						|| (LA40_2 >= KW_ARRAY && LA40_2 <= KW_AS)
						|| LA40_2 == KW_ATTRIBUTES
						|| (LA40_2 >= KW_CASE && LA40_2 <= KW_CAST)
						|| LA40_2 == KW_COORDINATE || LA40_2 == KW_ELSE
						|| LA40_2 == KW_END || LA40_2 == KW_EXECUTE
						|| LA40_2 == KW_EXPAND || LA40_2 == KW_FALSE
						|| (LA40_2 >= KW_FOR && LA40_2 <= KW_FOREACH)
						|| LA40_2 == KW_FROM
						|| (LA40_2 >= KW_GENERATE && LA40_2 <= KW_GENERATEMAP)
						|| (LA40_2 >= KW_GROUP && LA40_2 <= KW_HAVING)
						|| LA40_2 == KW_IF || LA40_2 == KW_IN
						|| LA40_2 == KW_INSERT || LA40_2 == KW_IS
						|| LA40_2 == KW_LIKE || LA40_2 == KW_MAP
						|| LA40_2 == KW_NOT || LA40_2 == KW_NULL
						|| LA40_2 == KW_OR || LA40_2 == KW_REGEXP
						|| LA40_2 == KW_RLIKE
						|| (LA40_2 >= KW_STRUCT && LA40_2 <= KW_SW)
						|| LA40_2 == KW_THEN || LA40_2 == KW_TRUE
						|| (LA40_2 >= KW_UNION && LA40_2 <= KW_UNIONTYPE)
						|| LA40_2 == KW_UPDATE
						|| (LA40_2 >= KW_WHEN && LA40_2 <= KW_WHERE)
						|| (LA40_2 >= KW_WITH && LA40_2 <= LSQUARE)
						|| (LA40_2 >= MINUS && LA40_2 <= NOTEQUAL)
						|| (LA40_2 >= Number && LA40_2 <= PLUS)
						|| (LA40_2 >= RCURLY && LA40_2 <= RSQUARE)
						|| (LA40_2 >= SEMICOLON && LA40_2 <= SmallintLiteral)
						|| LA40_2 == TILDE || LA40_2 == TinyintLiteral)) {
					alt40 = 2;
				} else if ((LA40_2 == StringLiteral)) {
					int LA40_9 = input.LA(3);
					if ((synpred54_Trc())) {
						alt40 = 2;
					} else if ((synpred55_Trc())) {
						alt40 = 3;
					}

					else {
						if (state.backtracking > 0) {
							state.failed = true;
							return retval;
						}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae = new NoViableAltException(
									"", 40, 9, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					if (state.backtracking > 0) {
						state.failed = true;
						return retval;
					}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae = new NoViableAltException(
								"", 40, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
				break;
			case BigintLiteral: {
				alt40 = 4;
			}
				break;
			case SmallintLiteral: {
				alt40 = 5;
			}
				break;
			case TinyintLiteral: {
				alt40 = 6;
			}
				break;
			case CharSetName: {
				alt40 = 7;
			}
				break;
			case KW_FALSE:
			case KW_TRUE: {
				alt40 = 8;
			}
				break;
			default:
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 40, 0,
						input);
				throw nvae;
			}
			switch (alt40) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:519:3: Number
			{
				root_0 = (CommonTree) adaptor.nil();

				Number113 = (Token) match(input, Number,
						FOLLOW_Number_in_constant2725);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					Number113_tree = (CommonTree) adaptor.create(Number113);
					adaptor.addChild(root_0, Number113_tree);
				}

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:520:5: StringLiteral
			{
				root_0 = (CommonTree) adaptor.nil();

				StringLiteral114 = (Token) match(input, StringLiteral,
						FOLLOW_StringLiteral_in_constant2731);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					StringLiteral114_tree = (CommonTree) adaptor
							.create(StringLiteral114);
					adaptor.addChild(root_0, StringLiteral114_tree);
				}

			}
				break;
			case 3:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:521:5:
			// stringLiteralSequence
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_stringLiteralSequence_in_constant2737);
				stringLiteralSequence115 = stringLiteralSequence();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, stringLiteralSequence115.getTree());

			}
				break;
			case 4:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:522:5: BigintLiteral
			{
				root_0 = (CommonTree) adaptor.nil();

				BigintLiteral116 = (Token) match(input, BigintLiteral,
						FOLLOW_BigintLiteral_in_constant2743);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					BigintLiteral116_tree = (CommonTree) adaptor
							.create(BigintLiteral116);
					adaptor.addChild(root_0, BigintLiteral116_tree);
				}

			}
				break;
			case 5:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:523:5: SmallintLiteral
			{
				root_0 = (CommonTree) adaptor.nil();

				SmallintLiteral117 = (Token) match(input, SmallintLiteral,
						FOLLOW_SmallintLiteral_in_constant2749);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					SmallintLiteral117_tree = (CommonTree) adaptor
							.create(SmallintLiteral117);
					adaptor.addChild(root_0, SmallintLiteral117_tree);
				}

			}
				break;
			case 6:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:524:5: TinyintLiteral
			{
				root_0 = (CommonTree) adaptor.nil();

				TinyintLiteral118 = (Token) match(input, TinyintLiteral,
						FOLLOW_TinyintLiteral_in_constant2755);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					TinyintLiteral118_tree = (CommonTree) adaptor
							.create(TinyintLiteral118);
					adaptor.addChild(root_0, TinyintLiteral118_tree);
				}

			}
				break;
			case 7:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:525:5:
			// charSetStringLiteral
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_charSetStringLiteral_in_constant2761);
				charSetStringLiteral119 = charSetStringLiteral();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, charSetStringLiteral119.getTree());

			}
				break;
			case 8:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:526:5: booleanValue
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_booleanValue_in_constant2767);
				booleanValue120 = booleanValue();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, booleanValue120.getTree());

			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "constant"

	public static class stringLiteralSequence_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "stringLiteralSequence"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:529:1: stringLiteralSequence :
	// StringLiteral ( StringLiteral )+ -> ^( TOK_STRINGLITERALSEQUENCE
	// StringLiteral ( StringLiteral )+ ) ;
	public final TrcParser.stringLiteralSequence_return stringLiteralSequence()
			throws RecognitionException {
		TrcParser.stringLiteralSequence_return retval = new TrcParser.stringLiteralSequence_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token StringLiteral121 = null;
		Token StringLiteral122 = null;

		CommonTree StringLiteral121_tree = null;
		CommonTree StringLiteral122_tree = null;
		RewriteRuleTokenStream stream_StringLiteral = new RewriteRuleTokenStream(
				adaptor, "token StringLiteral");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:530:3: ( StringLiteral
			// ( StringLiteral )+ -> ^( TOK_STRINGLITERALSEQUENCE StringLiteral
			// ( StringLiteral )+ ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:531:3: StringLiteral (
			// StringLiteral )+
			{
				StringLiteral121 = (Token) match(input, StringLiteral,
						FOLLOW_StringLiteral_in_stringLiteralSequence2782);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_StringLiteral.add(StringLiteral121);

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:531:17: (
				// StringLiteral )+
				int cnt41 = 0;
				loop41: while (true) {
					int alt41 = 2;
					int LA41_0 = input.LA(1);
					if ((LA41_0 == StringLiteral)) {
						int LA41_2 = input.LA(2);
						if ((synpred60_Trc())) {
							alt41 = 1;
						}

					}

					switch (alt41) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:531:17:
					// StringLiteral
					{
						StringLiteral122 = (Token) match(input, StringLiteral,
								FOLLOW_StringLiteral_in_stringLiteralSequence2784);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_StringLiteral.add(StringLiteral122);

					}
						break;

					default:
						if (cnt41 >= 1)
							break loop41;
						if (state.backtracking > 0) {
							state.failed = true;
							return retval;
						}
						EarlyExitException eee = new EarlyExitException(41,
								input);
						throw eee;
					}
					cnt41++;
				}

				// AST REWRITE
				// elements: StringLiteral, StringLiteral
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 532:5: -> ^( TOK_STRINGLITERALSEQUENCE StringLiteral (
					// StringLiteral )+ )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:533:7: ^(
						// TOK_STRINGLITERALSEQUENCE StringLiteral (
						// StringLiteral )+ )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(
											TOK_STRINGLITERALSEQUENCE,
											"TOK_STRINGLITERALSEQUENCE"),
									root_1);
							adaptor.addChild(root_1,
									stream_StringLiteral.nextNode());
							if (!(stream_StringLiteral.hasNext())) {
								throw new RewriteEarlyExitException();
							}
							while (stream_StringLiteral.hasNext()) {
								adaptor.addChild(root_1,
										stream_StringLiteral.nextNode());
							}
							stream_StringLiteral.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "stringLiteralSequence"

	public static class charSetStringLiteral_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "charSetStringLiteral"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:536:1: charSetStringLiteral :
	// csName= CharSetName csLiteral= CharSetLiteral -> ^( TOK_CHARSETLITERAL
	// $csName $csLiteral) ;
	public final TrcParser.charSetStringLiteral_return charSetStringLiteral()
			throws RecognitionException {
		TrcParser.charSetStringLiteral_return retval = new TrcParser.charSetStringLiteral_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token csName = null;
		Token csLiteral = null;

		CommonTree csName_tree = null;
		CommonTree csLiteral_tree = null;
		RewriteRuleTokenStream stream_CharSetLiteral = new RewriteRuleTokenStream(
				adaptor, "token CharSetLiteral");
		RewriteRuleTokenStream stream_CharSetName = new RewriteRuleTokenStream(
				adaptor, "token CharSetName");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:537:3: (csName=
			// CharSetName csLiteral= CharSetLiteral -> ^( TOK_CHARSETLITERAL
			// $csName $csLiteral) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:538:3: csName=
			// CharSetName csLiteral= CharSetLiteral
			{
				csName = (Token) match(input, CharSetName,
						FOLLOW_CharSetName_in_charSetStringLiteral2823);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_CharSetName.add(csName);

				csLiteral = (Token) match(input, CharSetLiteral,
						FOLLOW_CharSetLiteral_in_charSetStringLiteral2827);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_CharSetLiteral.add(csLiteral);

				// AST REWRITE
				// elements: csName, csLiteral
				// token labels: csName, csLiteral
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_csName = new RewriteRuleTokenStream(
							adaptor, "token csName", csName);
					RewriteRuleTokenStream stream_csLiteral = new RewriteRuleTokenStream(
							adaptor, "token csLiteral", csLiteral);
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 539:5: -> ^( TOK_CHARSETLITERAL $csName $csLiteral)
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:540:7: ^(
						// TOK_CHARSETLITERAL $csName $csLiteral)
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(
											TOK_CHARSETLITERAL,
											"TOK_CHARSETLITERAL"), root_1);
							adaptor.addChild(root_1, stream_csName.nextNode());
							adaptor.addChild(root_1,
									stream_csLiteral.nextNode());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "charSetStringLiteral"

	public static class booleanValue_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "booleanValue"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:543:1: booleanValue : (
	// KW_TRUE ^| KW_FALSE ^);
	public final TrcParser.booleanValue_return booleanValue()
			throws RecognitionException {
		TrcParser.booleanValue_return retval = new TrcParser.booleanValue_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_TRUE123 = null;
		Token KW_FALSE124 = null;

		CommonTree KW_TRUE123_tree = null;
		CommonTree KW_FALSE124_tree = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:544:3: ( KW_TRUE ^|
			// KW_FALSE ^)
			int alt42 = 2;
			int LA42_0 = input.LA(1);
			if ((LA42_0 == KW_TRUE)) {
				alt42 = 1;
			} else if ((LA42_0 == KW_FALSE)) {
				alt42 = 2;
			}

			else {
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 42, 0,
						input);
				throw nvae;
			}

			switch (alt42) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:545:3: KW_TRUE ^
			{
				root_0 = (CommonTree) adaptor.nil();

				KW_TRUE123 = (Token) match(input, KW_TRUE,
						FOLLOW_KW_TRUE_in_booleanValue2864);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					KW_TRUE123_tree = (CommonTree) adaptor.create(KW_TRUE123);
					root_0 = (CommonTree) adaptor.becomeRoot(KW_TRUE123_tree,
							root_0);
				}

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:546:5: KW_FALSE ^
			{
				root_0 = (CommonTree) adaptor.nil();

				KW_FALSE124 = (Token) match(input, KW_FALSE,
						FOLLOW_KW_FALSE_in_booleanValue2871);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					KW_FALSE124_tree = (CommonTree) adaptor.create(KW_FALSE124);
					root_0 = (CommonTree) adaptor.becomeRoot(KW_FALSE124_tree,
							root_0);
				}

			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "booleanValue"

	public static class function_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "function"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:549:1: function : functionName
	// LPAREN ( (star= STAR ) | ( expression ( COMMA expression )* )? ) ( (accu=
	// KW_ACCU ) | (sw= KW_SW ) | (update= KW_UPDATE ) )? RPAREN -> {$star !=
	// null}? ^( TOK_FUNCTIONSTAR functionName ) -> {$accu != null}? ^(
	// TOK_FUNCTIONACCU functionName ( ( expression )+ )? ) -> {$sw != null}? ^(
	// TOK_FUNCTIONSW functionName ( ( expression )+ )? ) -> {$update != null}?
	// ^( TOK_FUNCTIONUPDATE functionName ( ( expression )+ )? ) -> ^(
	// TOK_FUNCTION functionName ( ( expression )+ )? ) ;
	public final TrcParser.function_return function()
			throws RecognitionException {
		TrcParser.function_return retval = new TrcParser.function_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token star = null;
		Token accu = null;
		Token sw = null;
		Token update = null;
		Token LPAREN126 = null;
		Token COMMA128 = null;
		Token RPAREN130 = null;
		ParserRuleReturnScope functionName125 = null;
		ParserRuleReturnScope expression127 = null;
		ParserRuleReturnScope expression129 = null;

		CommonTree star_tree = null;
		CommonTree accu_tree = null;
		CommonTree sw_tree = null;
		CommonTree update_tree = null;
		CommonTree LPAREN126_tree = null;
		CommonTree COMMA128_tree = null;
		CommonTree RPAREN130_tree = null;
		RewriteRuleTokenStream stream_KW_ACCU = new RewriteRuleTokenStream(
				adaptor, "token KW_ACCU");
		RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(
				adaptor, "token RPAREN");
		RewriteRuleTokenStream stream_STAR = new RewriteRuleTokenStream(
				adaptor, "token STAR");
		RewriteRuleTokenStream stream_KW_UPDATE = new RewriteRuleTokenStream(
				adaptor, "token KW_UPDATE");
		RewriteRuleTokenStream stream_KW_SW = new RewriteRuleTokenStream(
				adaptor, "token KW_SW");
		RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(
				adaptor, "token COMMA");
		RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(
				adaptor, "token LPAREN");
		RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(
				adaptor, "rule expression");
		RewriteRuleSubtreeStream stream_functionName = new RewriteRuleSubtreeStream(
				adaptor, "rule functionName");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:550:3: ( functionName
			// LPAREN ( (star= STAR ) | ( expression ( COMMA expression )* )? )
			// ( (accu= KW_ACCU ) | (sw= KW_SW ) | (update= KW_UPDATE ) )?
			// RPAREN -> {$star != null}? ^( TOK_FUNCTIONSTAR functionName ) ->
			// {$accu != null}? ^( TOK_FUNCTIONACCU functionName ( ( expression
			// )+ )? ) -> {$sw != null}? ^( TOK_FUNCTIONSW functionName ( (
			// expression )+ )? ) -> {$update != null}? ^( TOK_FUNCTIONUPDATE
			// functionName ( ( expression )+ )? ) -> ^( TOK_FUNCTION
			// functionName ( ( expression )+ )? ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:551:3: functionName
			// LPAREN ( (star= STAR ) | ( expression ( COMMA expression )* )? )
			// ( (accu= KW_ACCU ) | (sw= KW_SW ) | (update= KW_UPDATE ) )?
			// RPAREN
			{
				pushFollow(FOLLOW_functionName_in_function2887);
				functionName125 = functionName();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_functionName.add(functionName125.getTree());
				LPAREN126 = (Token) match(input, LPAREN,
						FOLLOW_LPAREN_in_function2889);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_LPAREN.add(LPAREN126);

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:552:3: ( (star=
				// STAR ) | ( expression ( COMMA expression )* )? )
				int alt45 = 2;
				int LA45_0 = input.LA(1);
				if ((LA45_0 == STAR)) {
					alt45 = 1;
				} else if ((LA45_0 == BigintLiteral || LA45_0 == CharSetName
						|| (LA45_0 >= Identifier && LA45_0 <= KW_ACCU)
						|| LA45_0 == KW_AGGR_TIME || LA45_0 == KW_ARRAY
						|| LA45_0 == KW_ATTRIBUTES
						|| (LA45_0 >= KW_CASE && LA45_0 <= KW_CAST)
						|| LA45_0 == KW_EXECUTE || LA45_0 == KW_FALSE
						|| LA45_0 == KW_FOREACH || LA45_0 == KW_IF
						|| LA45_0 == KW_MAP || LA45_0 == KW_NOT
						|| LA45_0 == KW_NULL
						|| (LA45_0 >= KW_STRUCT && LA45_0 <= KW_SW)
						|| LA45_0 == KW_TRUE || LA45_0 == KW_UNIONTYPE
						|| LA45_0 == KW_UPDATE || LA45_0 == LPAREN
						|| LA45_0 == MINUS
						|| (LA45_0 >= Number && LA45_0 <= PLUS)
						|| LA45_0 == RPAREN
						|| (LA45_0 >= SmallintLiteral && LA45_0 <= TILDE) || LA45_0 == TinyintLiteral)) {
					alt45 = 2;
				}

				else {
					if (state.backtracking > 0) {
						state.failed = true;
						return retval;
					}
					NoViableAltException nvae = new NoViableAltException("",
							45, 0, input);
					throw nvae;
				}

				switch (alt45) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:553:5: (star= STAR
				// )
				{
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:553:5: (star=
					// STAR )
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:553:6: star=
					// STAR
					{
						star = (Token) match(input, STAR,
								FOLLOW_STAR_in_function2902);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_STAR.add(star);

					}

				}
					break;
				case 2:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:555:7: (
				// expression ( COMMA expression )* )?
				{
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:555:7: (
					// expression ( COMMA expression )* )?
					int alt44 = 2;
					int LA44_0 = input.LA(1);
					if ((LA44_0 == BigintLiteral
							|| LA44_0 == CharSetName
							|| (LA44_0 >= Identifier && LA44_0 <= IdentifierRef)
							|| LA44_0 == KW_AGGR_TIME || LA44_0 == KW_ARRAY
							|| LA44_0 == KW_ATTRIBUTES
							|| (LA44_0 >= KW_CASE && LA44_0 <= KW_CAST)
							|| LA44_0 == KW_EXECUTE || LA44_0 == KW_FALSE
							|| LA44_0 == KW_FOREACH || LA44_0 == KW_IF
							|| LA44_0 == KW_MAP || LA44_0 == KW_NOT
							|| LA44_0 == KW_NULL || LA44_0 == KW_STRUCT
							|| LA44_0 == KW_TRUE || LA44_0 == KW_UNIONTYPE
							|| LA44_0 == LPAREN || LA44_0 == MINUS
							|| (LA44_0 >= Number && LA44_0 <= PLUS)
							|| (LA44_0 >= SmallintLiteral && LA44_0 <= TILDE) || LA44_0 == TinyintLiteral)) {
						alt44 = 1;
					}
					switch (alt44) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:555:8:
					// expression ( COMMA expression )*
					{
						pushFollow(FOLLOW_expression_in_function2917);
						expression127 = expression();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_expression.add(expression127.getTree());
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:555:19: (
						// COMMA expression )*
						loop43: while (true) {
							int alt43 = 2;
							int LA43_0 = input.LA(1);
							if ((LA43_0 == COMMA)) {
								alt43 = 1;
							}

							switch (alt43) {
							case 1:
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:555:20:
							// COMMA expression
							{
								COMMA128 = (Token) match(input, COMMA,
										FOLLOW_COMMA_in_function2920);
								if (state.failed)
									return retval;
								if (state.backtracking == 0)
									stream_COMMA.add(COMMA128);

								pushFollow(FOLLOW_expression_in_function2922);
								expression129 = expression();
								state._fsp--;
								if (state.failed)
									return retval;
								if (state.backtracking == 0)
									stream_expression.add(expression129
											.getTree());
							}
								break;

							default:
								break loop43;
							}
						}

					}
						break;

					}

				}
					break;

				}

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:557:3: ( (accu=
				// KW_ACCU ) | (sw= KW_SW ) | (update= KW_UPDATE ) )?
				int alt46 = 4;
				switch (input.LA(1)) {
				case KW_ACCU: {
					alt46 = 1;
				}
					break;
				case KW_SW: {
					alt46 = 2;
				}
					break;
				case KW_UPDATE: {
					alt46 = 3;
				}
					break;
				}
				switch (alt46) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:558:5: (accu=
				// KW_ACCU )
				{
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:558:5: (accu=
					// KW_ACCU )
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:558:6: accu=
					// KW_ACCU
					{
						accu = (Token) match(input, KW_ACCU,
								FOLLOW_KW_ACCU_in_function2943);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_KW_ACCU.add(accu);

					}

				}
					break;
				case 2:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:559:7: (sw= KW_SW
				// )
				{
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:559:7: (sw=
					// KW_SW )
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:559:8: sw=
					// KW_SW
					{
						sw = (Token) match(input, KW_SW,
								FOLLOW_KW_SW_in_function2955);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_KW_SW.add(sw);

					}

				}
					break;
				case 3:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:560:7: (update=
				// KW_UPDATE )
				{
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:560:7:
					// (update= KW_UPDATE )
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:560:8: update=
					// KW_UPDATE
					{
						update = (Token) match(input, KW_UPDATE,
								FOLLOW_KW_UPDATE_in_function2967);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_KW_UPDATE.add(update);

					}

				}
					break;

				}

				RPAREN130 = (Token) match(input, RPAREN,
						FOLLOW_RPAREN_in_function2977);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_RPAREN.add(RPAREN130);

				// AST REWRITE
				// elements: expression, expression, functionName, functionName,
				// functionName, expression, functionName, functionName,
				// expression
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 563:5: -> {$star != null}? ^( TOK_FUNCTIONSTAR
					// functionName )
					if (star != null) {
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:564:7: ^(
						// TOK_FUNCTIONSTAR functionName )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(
											TOK_FUNCTIONSTAR,
											"TOK_FUNCTIONSTAR"), root_1);
							adaptor.addChild(root_1,
									stream_functionName.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					else // 565:5: -> {$accu != null}? ^( TOK_FUNCTIONACCU
							// functionName ( ( expression )+ )? )
					if (accu != null) {
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:566:7: ^(
						// TOK_FUNCTIONACCU functionName ( ( expression )+ )? )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(
											TOK_FUNCTIONACCU,
											"TOK_FUNCTIONACCU"), root_1);
							adaptor.addChild(root_1,
									stream_functionName.nextTree());
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:567:39:
							// ( ( expression )+ )?
							if (stream_expression.hasNext()) {
								if (!(stream_expression.hasNext())) {
									throw new RewriteEarlyExitException();
								}
								while (stream_expression.hasNext()) {
									adaptor.addChild(root_1,
											stream_expression.nextTree());
								}
								stream_expression.reset();

							}
							stream_expression.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					else // 569:5: -> {$sw != null}? ^( TOK_FUNCTIONSW
							// functionName ( ( expression )+ )? )
					if (sw != null) {
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:570:7: ^(
						// TOK_FUNCTIONSW functionName ( ( expression )+ )? )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_FUNCTIONSW,
											"TOK_FUNCTIONSW"), root_1);
							adaptor.addChild(root_1,
									stream_functionName.nextTree());
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:571:37:
							// ( ( expression )+ )?
							if (stream_expression.hasNext()) {
								if (!(stream_expression.hasNext())) {
									throw new RewriteEarlyExitException();
								}
								while (stream_expression.hasNext()) {
									adaptor.addChild(root_1,
											stream_expression.nextTree());
								}
								stream_expression.reset();

							}
							stream_expression.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					else // 573:5: -> {$update != null}? ^( TOK_FUNCTIONUPDATE
							// functionName ( ( expression )+ )? )
					if (update != null) {
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:574:7: ^(
						// TOK_FUNCTIONUPDATE functionName ( ( expression )+ )?
						// )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(
											TOK_FUNCTIONUPDATE,
											"TOK_FUNCTIONUPDATE"), root_1);
							adaptor.addChild(root_1,
									stream_functionName.nextTree());
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:575:41:
							// ( ( expression )+ )?
							if (stream_expression.hasNext()) {
								if (!(stream_expression.hasNext())) {
									throw new RewriteEarlyExitException();
								}
								while (stream_expression.hasNext()) {
									adaptor.addChild(root_1,
											stream_expression.nextTree());
								}
								stream_expression.reset();

							}
							stream_expression.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					else // 577:5: -> ^( TOK_FUNCTION functionName ( (
							// expression )+ )? )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:578:7: ^(
						// TOK_FUNCTION functionName ( ( expression )+ )? )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_FUNCTION,
											"TOK_FUNCTION"), root_1);
							adaptor.addChild(root_1,
									stream_functionName.nextTree());
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:579:35:
							// ( ( expression )+ )?
							if (stream_expression.hasNext()) {
								if (!(stream_expression.hasNext())) {
									throw new RewriteEarlyExitException();
								}
								while (stream_expression.hasNext()) {
									adaptor.addChild(root_1,
											stream_expression.nextTree());
								}
								stream_expression.reset();

							}
							stream_expression.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "function"

	public static class foreach_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "foreach"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:583:1: foreach : KW_FOREACH
	// LPAREN indexExpr generateExpr RPAREN -> ^( TOK_FOREACH indexExpr
	// generateExpr ) ;
	public final TrcParser.foreach_return foreach() throws RecognitionException {
		TrcParser.foreach_return retval = new TrcParser.foreach_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_FOREACH131 = null;
		Token LPAREN132 = null;
		Token RPAREN135 = null;
		ParserRuleReturnScope indexExpr133 = null;
		ParserRuleReturnScope generateExpr134 = null;

		CommonTree KW_FOREACH131_tree = null;
		CommonTree LPAREN132_tree = null;
		CommonTree RPAREN135_tree = null;
		RewriteRuleTokenStream stream_KW_FOREACH = new RewriteRuleTokenStream(
				adaptor, "token KW_FOREACH");
		RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(
				adaptor, "token RPAREN");
		RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(
				adaptor, "token LPAREN");
		RewriteRuleSubtreeStream stream_generateExpr = new RewriteRuleSubtreeStream(
				adaptor, "rule generateExpr");
		RewriteRuleSubtreeStream stream_indexExpr = new RewriteRuleSubtreeStream(
				adaptor, "rule indexExpr");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:584:3: ( KW_FOREACH
			// LPAREN indexExpr generateExpr RPAREN -> ^( TOK_FOREACH indexExpr
			// generateExpr ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:585:3: KW_FOREACH
			// LPAREN indexExpr generateExpr RPAREN
			{
				KW_FOREACH131 = (Token) match(input, KW_FOREACH,
						FOLLOW_KW_FOREACH_in_foreach3182);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_FOREACH.add(KW_FOREACH131);

				LPAREN132 = (Token) match(input, LPAREN,
						FOLLOW_LPAREN_in_foreach3184);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_LPAREN.add(LPAREN132);

				pushFollow(FOLLOW_indexExpr_in_foreach3186);
				indexExpr133 = indexExpr();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_indexExpr.add(indexExpr133.getTree());
				pushFollow(FOLLOW_generateExpr_in_foreach3188);
				generateExpr134 = generateExpr();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_generateExpr.add(generateExpr134.getTree());
				RPAREN135 = (Token) match(input, RPAREN,
						FOLLOW_RPAREN_in_foreach3190);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_RPAREN.add(RPAREN135);

				// AST REWRITE
				// elements: indexExpr, generateExpr
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 586:5: -> ^( TOK_FOREACH indexExpr generateExpr )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:587:7: ^(
						// TOK_FOREACH indexExpr generateExpr )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_FOREACH,
											"TOK_FOREACH"), root_1);
							adaptor.addChild(root_1,
									stream_indexExpr.nextTree());
							adaptor.addChild(root_1,
									stream_generateExpr.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "foreach"

	public static class indexExpr_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "indexExpr"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:590:1: indexExpr : Identifier
	// ( (of= KW_OF ) | (in= KW_IN ) ) list= expression -> {$of != null}? ^(
	// TOK_OF Identifier $list) -> ^( TOK_IN Identifier $list) ;
	public final TrcParser.indexExpr_return indexExpr()
			throws RecognitionException {
		TrcParser.indexExpr_return retval = new TrcParser.indexExpr_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token of = null;
		Token in = null;
		Token Identifier136 = null;
		ParserRuleReturnScope list = null;

		CommonTree of_tree = null;
		CommonTree in_tree = null;
		CommonTree Identifier136_tree = null;
		RewriteRuleTokenStream stream_KW_IN = new RewriteRuleTokenStream(
				adaptor, "token KW_IN");
		RewriteRuleTokenStream stream_KW_OF = new RewriteRuleTokenStream(
				adaptor, "token KW_OF");
		RewriteRuleTokenStream stream_Identifier = new RewriteRuleTokenStream(
				adaptor, "token Identifier");
		RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(
				adaptor, "rule expression");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:591:3: ( Identifier (
			// (of= KW_OF ) | (in= KW_IN ) ) list= expression -> {$of != null}?
			// ^( TOK_OF Identifier $list) -> ^( TOK_IN Identifier $list) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:592:3: Identifier (
			// (of= KW_OF ) | (in= KW_IN ) ) list= expression
			{
				Identifier136 = (Token) match(input, Identifier,
						FOLLOW_Identifier_in_indexExpr3225);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_Identifier.add(Identifier136);

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:593:3: ( (of=
				// KW_OF ) | (in= KW_IN ) )
				int alt47 = 2;
				int LA47_0 = input.LA(1);
				if ((LA47_0 == KW_OF)) {
					alt47 = 1;
				} else if ((LA47_0 == KW_IN)) {
					alt47 = 2;
				}

				else {
					if (state.backtracking > 0) {
						state.failed = true;
						return retval;
					}
					NoViableAltException nvae = new NoViableAltException("",
							47, 0, input);
					throw nvae;
				}

				switch (alt47) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:594:5: (of= KW_OF
				// )
				{
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:594:5: (of=
					// KW_OF )
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:594:6: of=
					// KW_OF
					{
						of = (Token) match(input, KW_OF,
								FOLLOW_KW_OF_in_indexExpr3238);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_KW_OF.add(of);

					}

				}
					break;
				case 2:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:595:7: (in= KW_IN
				// )
				{
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:595:7: (in=
					// KW_IN )
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:595:8: in=
					// KW_IN
					{
						in = (Token) match(input, KW_IN,
								FOLLOW_KW_IN_in_indexExpr3250);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_KW_IN.add(in);

					}

				}
					break;

				}

				pushFollow(FOLLOW_expression_in_indexExpr3261);
				list = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_expression.add(list.getTree());
				// AST REWRITE
				// elements: list, Identifier, Identifier, list
				// token labels:
				// rule labels: retval, list
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);
					RewriteRuleSubtreeStream stream_list = new RewriteRuleSubtreeStream(
							adaptor, "rule list", list != null ? list.getTree()
									: null);

					root_0 = (CommonTree) adaptor.nil();
					// 598:5: -> {$of != null}? ^( TOK_OF Identifier $list)
					if (of != null) {
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:599:7: ^(
						// TOK_OF Identifier $list)
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_OF,
											"TOK_OF"), root_1);
							adaptor.addChild(root_1,
									stream_Identifier.nextNode());
							adaptor.addChild(root_1, stream_list.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					else // 600:5: -> ^( TOK_IN Identifier $list)
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:601:7: ^(
						// TOK_IN Identifier $list)
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_IN,
											"TOK_IN"), root_1);
							adaptor.addChild(root_1,
									stream_Identifier.nextNode());
							adaptor.addChild(root_1, stream_list.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "indexExpr"

	public static class generateExpr_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "generateExpr"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:604:1: generateExpr : (
	// KW_GENERATE expression -> ^( TOK_GENERATE expression ) | KW_GENERATEMAP
	// k= expression v= expression -> ^( TOK_GENERATEMAP $k $v) );
	public final TrcParser.generateExpr_return generateExpr()
			throws RecognitionException {
		TrcParser.generateExpr_return retval = new TrcParser.generateExpr_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_GENERATE137 = null;
		Token KW_GENERATEMAP139 = null;
		ParserRuleReturnScope k = null;
		ParserRuleReturnScope v = null;
		ParserRuleReturnScope expression138 = null;

		CommonTree KW_GENERATE137_tree = null;
		CommonTree KW_GENERATEMAP139_tree = null;
		RewriteRuleTokenStream stream_KW_GENERATE = new RewriteRuleTokenStream(
				adaptor, "token KW_GENERATE");
		RewriteRuleTokenStream stream_KW_GENERATEMAP = new RewriteRuleTokenStream(
				adaptor, "token KW_GENERATEMAP");
		RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(
				adaptor, "rule expression");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:605:3: ( KW_GENERATE
			// expression -> ^( TOK_GENERATE expression ) | KW_GENERATEMAP k=
			// expression v= expression -> ^( TOK_GENERATEMAP $k $v) )
			int alt48 = 2;
			int LA48_0 = input.LA(1);
			if ((LA48_0 == KW_GENERATE)) {
				alt48 = 1;
			} else if ((LA48_0 == KW_GENERATEMAP)) {
				alt48 = 2;
			}

			else {
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 48, 0,
						input);
				throw nvae;
			}

			switch (alt48) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:606:3: KW_GENERATE
			// expression
			{
				KW_GENERATE137 = (Token) match(input, KW_GENERATE,
						FOLLOW_KW_GENERATE_in_generateExpr3320);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_GENERATE.add(KW_GENERATE137);

				pushFollow(FOLLOW_expression_in_generateExpr3322);
				expression138 = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_expression.add(expression138.getTree());
				// AST REWRITE
				// elements: expression
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 607:5: -> ^( TOK_GENERATE expression )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:608:7: ^(
						// TOK_GENERATE expression )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_GENERATE,
											"TOK_GENERATE"), root_1);
							adaptor.addChild(root_1,
									stream_expression.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:609:5: KW_GENERATEMAP
			// k= expression v= expression
			{
				KW_GENERATEMAP139 = (Token) match(input, KW_GENERATEMAP,
						FOLLOW_KW_GENERATEMAP_in_generateExpr3346);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_GENERATEMAP.add(KW_GENERATEMAP139);

				pushFollow(FOLLOW_expression_in_generateExpr3350);
				k = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_expression.add(k.getTree());
				pushFollow(FOLLOW_expression_in_generateExpr3354);
				v = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_expression.add(v.getTree());
				// AST REWRITE
				// elements: k, v
				// token labels:
				// rule labels: v, retval, k
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_v = new RewriteRuleSubtreeStream(
							adaptor, "rule v", v != null ? v.getTree() : null);
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);
					RewriteRuleSubtreeStream stream_k = new RewriteRuleSubtreeStream(
							adaptor, "rule k", k != null ? k.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 610:5: -> ^( TOK_GENERATEMAP $k $v)
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:611:7: ^(
						// TOK_GENERATEMAP $k $v)
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor
									.becomeRoot(
											(CommonTree) adaptor.create(
													TOK_GENERATEMAP,
													"TOK_GENERATEMAP"), root_1);
							adaptor.addChild(root_1, stream_k.nextTree());
							adaptor.addChild(root_1, stream_v.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "generateExpr"

	public static class execute_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "execute"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:614:1: execute : KW_EXECUTE
	// LPAREN defineVars executeBlock emitValue RPAREN -> ^( TOK_EXECUTE
	// defineVars executeBlock emitValue ) ;
	public final TrcParser.execute_return execute() throws RecognitionException {
		TrcParser.execute_return retval = new TrcParser.execute_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_EXECUTE140 = null;
		Token LPAREN141 = null;
		Token RPAREN145 = null;
		ParserRuleReturnScope defineVars142 = null;
		ParserRuleReturnScope executeBlock143 = null;
		ParserRuleReturnScope emitValue144 = null;

		CommonTree KW_EXECUTE140_tree = null;
		CommonTree LPAREN141_tree = null;
		CommonTree RPAREN145_tree = null;
		RewriteRuleTokenStream stream_KW_EXECUTE = new RewriteRuleTokenStream(
				adaptor, "token KW_EXECUTE");
		RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(
				adaptor, "token RPAREN");
		RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(
				adaptor, "token LPAREN");
		RewriteRuleSubtreeStream stream_emitValue = new RewriteRuleSubtreeStream(
				adaptor, "rule emitValue");
		RewriteRuleSubtreeStream stream_defineVars = new RewriteRuleSubtreeStream(
				adaptor, "rule defineVars");
		RewriteRuleSubtreeStream stream_executeBlock = new RewriteRuleSubtreeStream(
				adaptor, "rule executeBlock");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:615:3: ( KW_EXECUTE
			// LPAREN defineVars executeBlock emitValue RPAREN -> ^( TOK_EXECUTE
			// defineVars executeBlock emitValue ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:616:3: KW_EXECUTE
			// LPAREN defineVars executeBlock emitValue RPAREN
			{
				KW_EXECUTE140 = (Token) match(input, KW_EXECUTE,
						FOLLOW_KW_EXECUTE_in_execute3391);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_EXECUTE.add(KW_EXECUTE140);

				LPAREN141 = (Token) match(input, LPAREN,
						FOLLOW_LPAREN_in_execute3393);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_LPAREN.add(LPAREN141);

				pushFollow(FOLLOW_defineVars_in_execute3395);
				defineVars142 = defineVars();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_defineVars.add(defineVars142.getTree());
				pushFollow(FOLLOW_executeBlock_in_execute3397);
				executeBlock143 = executeBlock();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_executeBlock.add(executeBlock143.getTree());
				pushFollow(FOLLOW_emitValue_in_execute3399);
				emitValue144 = emitValue();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_emitValue.add(emitValue144.getTree());
				RPAREN145 = (Token) match(input, RPAREN,
						FOLLOW_RPAREN_in_execute3401);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_RPAREN.add(RPAREN145);

				// AST REWRITE
				// elements: executeBlock, defineVars, emitValue
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 617:5: -> ^( TOK_EXECUTE defineVars executeBlock
					// emitValue )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:618:7: ^(
						// TOK_EXECUTE defineVars executeBlock emitValue )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_EXECUTE,
											"TOK_EXECUTE"), root_1);
							adaptor.addChild(root_1,
									stream_defineVars.nextTree());
							adaptor.addChild(root_1,
									stream_executeBlock.nextTree());
							adaptor.addChild(root_1,
									stream_emitValue.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "execute"

	public static class defineVars_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "defineVars"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:621:1: defineVars : KW_DEFINE
	// var ( COMMA var )* -> ^( TOK_DEFINE ( var )+ ) ;
	public final TrcParser.defineVars_return defineVars()
			throws RecognitionException {
		TrcParser.defineVars_return retval = new TrcParser.defineVars_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_DEFINE146 = null;
		Token COMMA148 = null;
		ParserRuleReturnScope var147 = null;
		ParserRuleReturnScope var149 = null;

		CommonTree KW_DEFINE146_tree = null;
		CommonTree COMMA148_tree = null;
		RewriteRuleTokenStream stream_KW_DEFINE = new RewriteRuleTokenStream(
				adaptor, "token KW_DEFINE");
		RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(
				adaptor, "token COMMA");
		RewriteRuleSubtreeStream stream_var = new RewriteRuleSubtreeStream(
				adaptor, "rule var");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:622:3: ( KW_DEFINE var
			// ( COMMA var )* -> ^( TOK_DEFINE ( var )+ ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:623:3: KW_DEFINE var (
			// COMMA var )*
			{
				KW_DEFINE146 = (Token) match(input, KW_DEFINE,
						FOLLOW_KW_DEFINE_in_defineVars3438);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_DEFINE.add(KW_DEFINE146);

				pushFollow(FOLLOW_var_in_defineVars3440);
				var147 = var();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_var.add(var147.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:623:17: ( COMMA
				// var )*
				loop49: while (true) {
					int alt49 = 2;
					int LA49_0 = input.LA(1);
					if ((LA49_0 == COMMA)) {
						alt49 = 1;
					}

					switch (alt49) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:623:18: COMMA
					// var
					{
						COMMA148 = (Token) match(input, COMMA,
								FOLLOW_COMMA_in_defineVars3443);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_COMMA.add(COMMA148);

						pushFollow(FOLLOW_var_in_defineVars3445);
						var149 = var();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_var.add(var149.getTree());
					}
						break;

					default:
						break loop49;
					}
				}

				// AST REWRITE
				// elements: var
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 624:5: -> ^( TOK_DEFINE ( var )+ )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:625:7: ^(
						// TOK_DEFINE ( var )+ )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_DEFINE,
											"TOK_DEFINE"), root_1);
							if (!(stream_var.hasNext())) {
								throw new RewriteEarlyExitException();
							}
							while (stream_var.hasNext()) {
								adaptor.addChild(root_1, stream_var.nextTree());
							}
							stream_var.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "defineVars"

	public static class var_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "var"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:628:1: var : varname=
	// Identifier ( KW_AS )? vartype= dataType ( KW_DEFAULT constant )? -> ^(
	// TOK_VAR $varname $vartype ( constant )? ) ;
	public final TrcParser.var_return var() throws RecognitionException {
		TrcParser.var_return retval = new TrcParser.var_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token varname = null;
		Token KW_AS150 = null;
		Token KW_DEFAULT151 = null;
		ParserRuleReturnScope vartype = null;
		ParserRuleReturnScope constant152 = null;

		CommonTree varname_tree = null;
		CommonTree KW_AS150_tree = null;
		CommonTree KW_DEFAULT151_tree = null;
		RewriteRuleTokenStream stream_KW_DEFAULT = new RewriteRuleTokenStream(
				adaptor, "token KW_DEFAULT");
		RewriteRuleTokenStream stream_KW_AS = new RewriteRuleTokenStream(
				adaptor, "token KW_AS");
		RewriteRuleTokenStream stream_Identifier = new RewriteRuleTokenStream(
				adaptor, "token Identifier");
		RewriteRuleSubtreeStream stream_dataType = new RewriteRuleSubtreeStream(
				adaptor, "rule dataType");
		RewriteRuleSubtreeStream stream_constant = new RewriteRuleSubtreeStream(
				adaptor, "rule constant");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:629:3: (varname=
			// Identifier ( KW_AS )? vartype= dataType ( KW_DEFAULT constant )?
			// -> ^( TOK_VAR $varname $vartype ( constant )? ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:630:3: varname=
			// Identifier ( KW_AS )? vartype= dataType ( KW_DEFAULT constant )?
			{
				varname = (Token) match(input, Identifier,
						FOLLOW_Identifier_in_var3483);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_Identifier.add(varname);

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:630:22: ( KW_AS )?
				int alt50 = 2;
				int LA50_0 = input.LA(1);
				if ((LA50_0 == KW_AS)) {
					alt50 = 1;
				}
				switch (alt50) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:630:22: KW_AS
				{
					KW_AS150 = (Token) match(input, KW_AS,
							FOLLOW_KW_AS_in_var3485);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_KW_AS.add(KW_AS150);

				}
					break;

				}

				pushFollow(FOLLOW_dataType_in_var3490);
				vartype = dataType();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_dataType.add(vartype.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:630:46: (
				// KW_DEFAULT constant )?
				int alt51 = 2;
				int LA51_0 = input.LA(1);
				if ((LA51_0 == KW_DEFAULT)) {
					alt51 = 1;
				}
				switch (alt51) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:630:47: KW_DEFAULT
				// constant
				{
					KW_DEFAULT151 = (Token) match(input, KW_DEFAULT,
							FOLLOW_KW_DEFAULT_in_var3493);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_KW_DEFAULT.add(KW_DEFAULT151);

					pushFollow(FOLLOW_constant_in_var3495);
					constant152 = constant();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_constant.add(constant152.getTree());
				}
					break;

				}

				// AST REWRITE
				// elements: vartype, varname, constant
				// token labels: varname
				// rule labels: retval, vartype
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_varname = new RewriteRuleTokenStream(
							adaptor, "token varname", varname);
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);
					RewriteRuleSubtreeStream stream_vartype = new RewriteRuleSubtreeStream(
							adaptor, "rule vartype",
							vartype != null ? vartype.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 631:5: -> ^( TOK_VAR $varname $vartype ( constant )? )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:632:7: ^(
						// TOK_VAR $varname $vartype ( constant )? )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_VAR,
											"TOK_VAR"), root_1);
							adaptor.addChild(root_1, stream_varname.nextNode());
							adaptor.addChild(root_1, stream_vartype.nextTree());
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:632:35:
							// ( constant )?
							if (stream_constant.hasNext()) {
								adaptor.addChild(root_1,
										stream_constant.nextTree());
							}
							stream_constant.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "var"

	public static class dataType_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "dataType"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:635:1: dataType : (
	// primitiveTypeDef | mapType | arrayType | structType | unionType );
	public final TrcParser.dataType_return dataType()
			throws RecognitionException {
		TrcParser.dataType_return retval = new TrcParser.dataType_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope primitiveTypeDef153 = null;
		ParserRuleReturnScope mapType154 = null;
		ParserRuleReturnScope arrayType155 = null;
		ParserRuleReturnScope structType156 = null;
		ParserRuleReturnScope unionType157 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:636:3: (
			// primitiveTypeDef | mapType | arrayType | structType | unionType )
			int alt52 = 5;
			switch (input.LA(1)) {
			case KW_BIGINT:
			case KW_BINARY:
			case KW_BOOLEAN:
			case KW_DATE:
			case KW_DATETIME:
			case KW_DOUBLE:
			case KW_FLOAT:
			case KW_INT:
			case KW_SMALLINT:
			case KW_STRING:
			case KW_TIMESTAMP:
			case KW_TINYINT: {
				alt52 = 1;
			}
				break;
			case KW_MAP: {
				alt52 = 2;
			}
				break;
			case KW_ARRAY: {
				alt52 = 3;
			}
				break;
			case KW_STRUCT: {
				alt52 = 4;
			}
				break;
			case KW_UNION: {
				alt52 = 5;
			}
				break;
			default:
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 52, 0,
						input);
				throw nvae;
			}
			switch (alt52) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:637:3:
			// primitiveTypeDef
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_primitiveTypeDef_in_dataType3537);
				primitiveTypeDef153 = primitiveTypeDef();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, primitiveTypeDef153.getTree());

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:638:5: mapType
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_mapType_in_dataType3543);
				mapType154 = mapType();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, mapType154.getTree());

			}
				break;
			case 3:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:639:5: arrayType
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_arrayType_in_dataType3549);
				arrayType155 = arrayType();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, arrayType155.getTree());

			}
				break;
			case 4:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:640:5: structType
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_structType_in_dataType3555);
				structType156 = structType();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, structType156.getTree());

			}
				break;
			case 5:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:641:5: unionType
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_unionType_in_dataType3561);
				unionType157 = unionType();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, unionType157.getTree());

			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "dataType"

	public static class mapType_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "mapType"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:644:1: mapType : KW_MAP
	// LESSTHAN kt= dataType COMMA vt= dataType GREATERTHAN -> ^( TOK_MAP $kt
	// $vt) ;
	public final TrcParser.mapType_return mapType() throws RecognitionException {
		TrcParser.mapType_return retval = new TrcParser.mapType_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_MAP158 = null;
		Token LESSTHAN159 = null;
		Token COMMA160 = null;
		Token GREATERTHAN161 = null;
		ParserRuleReturnScope kt = null;
		ParserRuleReturnScope vt = null;

		CommonTree KW_MAP158_tree = null;
		CommonTree LESSTHAN159_tree = null;
		CommonTree COMMA160_tree = null;
		CommonTree GREATERTHAN161_tree = null;
		RewriteRuleTokenStream stream_LESSTHAN = new RewriteRuleTokenStream(
				adaptor, "token LESSTHAN");
		RewriteRuleTokenStream stream_KW_MAP = new RewriteRuleTokenStream(
				adaptor, "token KW_MAP");
		RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(
				adaptor, "token COMMA");
		RewriteRuleTokenStream stream_GREATERTHAN = new RewriteRuleTokenStream(
				adaptor, "token GREATERTHAN");
		RewriteRuleSubtreeStream stream_dataType = new RewriteRuleSubtreeStream(
				adaptor, "rule dataType");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:645:3: ( KW_MAP
			// LESSTHAN kt= dataType COMMA vt= dataType GREATERTHAN -> ^(
			// TOK_MAP $kt $vt) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:646:3: KW_MAP LESSTHAN
			// kt= dataType COMMA vt= dataType GREATERTHAN
			{
				KW_MAP158 = (Token) match(input, KW_MAP,
						FOLLOW_KW_MAP_in_mapType3576);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_MAP.add(KW_MAP158);

				LESSTHAN159 = (Token) match(input, LESSTHAN,
						FOLLOW_LESSTHAN_in_mapType3578);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_LESSTHAN.add(LESSTHAN159);

				pushFollow(FOLLOW_dataType_in_mapType3582);
				kt = dataType();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_dataType.add(kt.getTree());
				COMMA160 = (Token) match(input, COMMA,
						FOLLOW_COMMA_in_mapType3584);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_COMMA.add(COMMA160);

				pushFollow(FOLLOW_dataType_in_mapType3588);
				vt = dataType();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_dataType.add(vt.getTree());
				GREATERTHAN161 = (Token) match(input, GREATERTHAN,
						FOLLOW_GREATERTHAN_in_mapType3590);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_GREATERTHAN.add(GREATERTHAN161);

				// AST REWRITE
				// elements: vt, kt
				// token labels:
				// rule labels: retval, kt, vt
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);
					RewriteRuleSubtreeStream stream_kt = new RewriteRuleSubtreeStream(
							adaptor, "rule kt", kt != null ? kt.getTree()
									: null);
					RewriteRuleSubtreeStream stream_vt = new RewriteRuleSubtreeStream(
							adaptor, "rule vt", vt != null ? vt.getTree()
									: null);

					root_0 = (CommonTree) adaptor.nil();
					// 647:5: -> ^( TOK_MAP $kt $vt)
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:648:7: ^(
						// TOK_MAP $kt $vt)
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_MAP,
											"TOK_MAP"), root_1);
							adaptor.addChild(root_1, stream_kt.nextTree());
							adaptor.addChild(root_1, stream_vt.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "mapType"

	public static class arrayType_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "arrayType"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:651:1: arrayType : KW_ARRAY
	// LESSTHAN dataType GREATERTHAN -> ^( TOK_ARRAY dataType ) ;
	public final TrcParser.arrayType_return arrayType()
			throws RecognitionException {
		TrcParser.arrayType_return retval = new TrcParser.arrayType_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_ARRAY162 = null;
		Token LESSTHAN163 = null;
		Token GREATERTHAN165 = null;
		ParserRuleReturnScope dataType164 = null;

		CommonTree KW_ARRAY162_tree = null;
		CommonTree LESSTHAN163_tree = null;
		CommonTree GREATERTHAN165_tree = null;
		RewriteRuleTokenStream stream_LESSTHAN = new RewriteRuleTokenStream(
				adaptor, "token LESSTHAN");
		RewriteRuleTokenStream stream_KW_ARRAY = new RewriteRuleTokenStream(
				adaptor, "token KW_ARRAY");
		RewriteRuleTokenStream stream_GREATERTHAN = new RewriteRuleTokenStream(
				adaptor, "token GREATERTHAN");
		RewriteRuleSubtreeStream stream_dataType = new RewriteRuleSubtreeStream(
				adaptor, "rule dataType");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:652:3: ( KW_ARRAY
			// LESSTHAN dataType GREATERTHAN -> ^( TOK_ARRAY dataType ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:653:3: KW_ARRAY
			// LESSTHAN dataType GREATERTHAN
			{
				KW_ARRAY162 = (Token) match(input, KW_ARRAY,
						FOLLOW_KW_ARRAY_in_arrayType3627);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_ARRAY.add(KW_ARRAY162);

				LESSTHAN163 = (Token) match(input, LESSTHAN,
						FOLLOW_LESSTHAN_in_arrayType3629);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_LESSTHAN.add(LESSTHAN163);

				pushFollow(FOLLOW_dataType_in_arrayType3631);
				dataType164 = dataType();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_dataType.add(dataType164.getTree());
				GREATERTHAN165 = (Token) match(input, GREATERTHAN,
						FOLLOW_GREATERTHAN_in_arrayType3633);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_GREATERTHAN.add(GREATERTHAN165);

				// AST REWRITE
				// elements: dataType
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 654:5: -> ^( TOK_ARRAY dataType )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:655:7: ^(
						// TOK_ARRAY dataType )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_ARRAY,
											"TOK_ARRAY"), root_1);
							adaptor.addChild(root_1, stream_dataType.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "arrayType"

	public static class structType_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "structType"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:658:1: structType : KW_STRUCT
	// LESSTHAN structUnit ( COMMA structUnit )* GREATERTHAN -> ^( TOK_STRUCT (
	// structUnit )+ ) ;
	public final TrcParser.structType_return structType()
			throws RecognitionException {
		TrcParser.structType_return retval = new TrcParser.structType_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_STRUCT166 = null;
		Token LESSTHAN167 = null;
		Token COMMA169 = null;
		Token GREATERTHAN171 = null;
		ParserRuleReturnScope structUnit168 = null;
		ParserRuleReturnScope structUnit170 = null;

		CommonTree KW_STRUCT166_tree = null;
		CommonTree LESSTHAN167_tree = null;
		CommonTree COMMA169_tree = null;
		CommonTree GREATERTHAN171_tree = null;
		RewriteRuleTokenStream stream_LESSTHAN = new RewriteRuleTokenStream(
				adaptor, "token LESSTHAN");
		RewriteRuleTokenStream stream_KW_STRUCT = new RewriteRuleTokenStream(
				adaptor, "token KW_STRUCT");
		RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(
				adaptor, "token COMMA");
		RewriteRuleTokenStream stream_GREATERTHAN = new RewriteRuleTokenStream(
				adaptor, "token GREATERTHAN");
		RewriteRuleSubtreeStream stream_structUnit = new RewriteRuleSubtreeStream(
				adaptor, "rule structUnit");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:659:3: ( KW_STRUCT
			// LESSTHAN structUnit ( COMMA structUnit )* GREATERTHAN -> ^(
			// TOK_STRUCT ( structUnit )+ ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:660:3: KW_STRUCT
			// LESSTHAN structUnit ( COMMA structUnit )* GREATERTHAN
			{
				KW_STRUCT166 = (Token) match(input, KW_STRUCT,
						FOLLOW_KW_STRUCT_in_structType3666);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_STRUCT.add(KW_STRUCT166);

				LESSTHAN167 = (Token) match(input, LESSTHAN,
						FOLLOW_LESSTHAN_in_structType3668);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_LESSTHAN.add(LESSTHAN167);

				pushFollow(FOLLOW_structUnit_in_structType3670);
				structUnit168 = structUnit();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_structUnit.add(structUnit168.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:660:33: ( COMMA
				// structUnit )*
				loop53: while (true) {
					int alt53 = 2;
					int LA53_0 = input.LA(1);
					if ((LA53_0 == COMMA)) {
						alt53 = 1;
					}

					switch (alt53) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:660:34: COMMA
					// structUnit
					{
						COMMA169 = (Token) match(input, COMMA,
								FOLLOW_COMMA_in_structType3673);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_COMMA.add(COMMA169);

						pushFollow(FOLLOW_structUnit_in_structType3675);
						structUnit170 = structUnit();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_structUnit.add(structUnit170.getTree());
					}
						break;

					default:
						break loop53;
					}
				}

				GREATERTHAN171 = (Token) match(input, GREATERTHAN,
						FOLLOW_GREATERTHAN_in_structType3679);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_GREATERTHAN.add(GREATERTHAN171);

				// AST REWRITE
				// elements: structUnit
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 661:5: -> ^( TOK_STRUCT ( structUnit )+ )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:662:7: ^(
						// TOK_STRUCT ( structUnit )+ )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_STRUCT,
											"TOK_STRUCT"), root_1);
							if (!(stream_structUnit.hasNext())) {
								throw new RewriteEarlyExitException();
							}
							while (stream_structUnit.hasNext()) {
								adaptor.addChild(root_1,
										stream_structUnit.nextTree());
							}
							stream_structUnit.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "structType"

	public static class structUnit_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "structUnit"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:665:1: structUnit : Identifier
	// SEMICOLON dataType -> ^( TOK_STRUCTUNIT Identifier dataType ) ;
	public final TrcParser.structUnit_return structUnit()
			throws RecognitionException {
		TrcParser.structUnit_return retval = new TrcParser.structUnit_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token Identifier172 = null;
		Token SEMICOLON173 = null;
		ParserRuleReturnScope dataType174 = null;

		CommonTree Identifier172_tree = null;
		CommonTree SEMICOLON173_tree = null;
		RewriteRuleTokenStream stream_SEMICOLON = new RewriteRuleTokenStream(
				adaptor, "token SEMICOLON");
		RewriteRuleTokenStream stream_Identifier = new RewriteRuleTokenStream(
				adaptor, "token Identifier");
		RewriteRuleSubtreeStream stream_dataType = new RewriteRuleSubtreeStream(
				adaptor, "rule dataType");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:666:3: ( Identifier
			// SEMICOLON dataType -> ^( TOK_STRUCTUNIT Identifier dataType ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:667:3: Identifier
			// SEMICOLON dataType
			{
				Identifier172 = (Token) match(input, Identifier,
						FOLLOW_Identifier_in_structUnit3713);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_Identifier.add(Identifier172);

				SEMICOLON173 = (Token) match(input, SEMICOLON,
						FOLLOW_SEMICOLON_in_structUnit3715);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_SEMICOLON.add(SEMICOLON173);

				pushFollow(FOLLOW_dataType_in_structUnit3717);
				dataType174 = dataType();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_dataType.add(dataType174.getTree());
				// AST REWRITE
				// elements: Identifier, dataType
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 668:5: -> ^( TOK_STRUCTUNIT Identifier dataType )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:669:7: ^(
						// TOK_STRUCTUNIT Identifier dataType )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_STRUCTUNIT,
											"TOK_STRUCTUNIT"), root_1);
							adaptor.addChild(root_1,
									stream_Identifier.nextNode());
							adaptor.addChild(root_1, stream_dataType.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "structUnit"

	public static class unionType_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "unionType"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:672:1: unionType : KW_UNION
	// LESSTHAN dataType ( COMMA dataType )* GREATERTHAN -> ^( TOK_UNION
	// Identifier ( dataType )+ ) ;
	public final TrcParser.unionType_return unionType()
			throws RecognitionException {
		TrcParser.unionType_return retval = new TrcParser.unionType_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_UNION175 = null;
		Token LESSTHAN176 = null;
		Token COMMA178 = null;
		Token GREATERTHAN180 = null;
		ParserRuleReturnScope dataType177 = null;
		ParserRuleReturnScope dataType179 = null;

		CommonTree KW_UNION175_tree = null;
		CommonTree LESSTHAN176_tree = null;
		CommonTree COMMA178_tree = null;
		CommonTree GREATERTHAN180_tree = null;
		RewriteRuleTokenStream stream_LESSTHAN = new RewriteRuleTokenStream(
				adaptor, "token LESSTHAN");
		RewriteRuleTokenStream stream_KW_UNION = new RewriteRuleTokenStream(
				adaptor, "token KW_UNION");
		RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(
				adaptor, "token COMMA");
		RewriteRuleTokenStream stream_GREATERTHAN = new RewriteRuleTokenStream(
				adaptor, "token GREATERTHAN");
		RewriteRuleSubtreeStream stream_dataType = new RewriteRuleSubtreeStream(
				adaptor, "rule dataType");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:673:3: ( KW_UNION
			// LESSTHAN dataType ( COMMA dataType )* GREATERTHAN -> ^( TOK_UNION
			// Identifier ( dataType )+ ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:674:3: KW_UNION
			// LESSTHAN dataType ( COMMA dataType )* GREATERTHAN
			{
				KW_UNION175 = (Token) match(input, KW_UNION,
						FOLLOW_KW_UNION_in_unionType3752);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_UNION.add(KW_UNION175);

				LESSTHAN176 = (Token) match(input, LESSTHAN,
						FOLLOW_LESSTHAN_in_unionType3754);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_LESSTHAN.add(LESSTHAN176);

				pushFollow(FOLLOW_dataType_in_unionType3756);
				dataType177 = dataType();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_dataType.add(dataType177.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:674:30: ( COMMA
				// dataType )*
				loop54: while (true) {
					int alt54 = 2;
					int LA54_0 = input.LA(1);
					if ((LA54_0 == COMMA)) {
						alt54 = 1;
					}

					switch (alt54) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:674:31: COMMA
					// dataType
					{
						COMMA178 = (Token) match(input, COMMA,
								FOLLOW_COMMA_in_unionType3759);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_COMMA.add(COMMA178);

						pushFollow(FOLLOW_dataType_in_unionType3761);
						dataType179 = dataType();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_dataType.add(dataType179.getTree());
					}
						break;

					default:
						break loop54;
					}
				}

				GREATERTHAN180 = (Token) match(input, GREATERTHAN,
						FOLLOW_GREATERTHAN_in_unionType3765);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_GREATERTHAN.add(GREATERTHAN180);

				// AST REWRITE
				// elements: dataType
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 675:5: -> ^( TOK_UNION Identifier ( dataType )+ )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:676:7: ^(
						// TOK_UNION Identifier ( dataType )+ )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_UNION,
											"TOK_UNION"), root_1);
							adaptor.addChild(root_1, (CommonTree) adaptor
									.create(Identifier, "Identifier"));
							if (!(stream_dataType.hasNext())) {
								throw new RewriteEarlyExitException();
							}
							while (stream_dataType.hasNext()) {
								adaptor.addChild(root_1,
										stream_dataType.nextTree());
							}
							stream_dataType.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "unionType"

	public static class executeBlock_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "executeBlock"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:679:1: executeBlock : LCURLY
	// block ( SEMICOLON block )* ( SEMICOLON )? RCURLY -> ^( TOK_EXECUTEBLOCK (
	// block )+ ) ;
	public final TrcParser.executeBlock_return executeBlock()
			throws RecognitionException {
		TrcParser.executeBlock_return retval = new TrcParser.executeBlock_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token LCURLY181 = null;
		Token SEMICOLON183 = null;
		Token SEMICOLON185 = null;
		Token RCURLY186 = null;
		ParserRuleReturnScope block182 = null;
		ParserRuleReturnScope block184 = null;

		CommonTree LCURLY181_tree = null;
		CommonTree SEMICOLON183_tree = null;
		CommonTree SEMICOLON185_tree = null;
		CommonTree RCURLY186_tree = null;
		RewriteRuleTokenStream stream_LCURLY = new RewriteRuleTokenStream(
				adaptor, "token LCURLY");
		RewriteRuleTokenStream stream_SEMICOLON = new RewriteRuleTokenStream(
				adaptor, "token SEMICOLON");
		RewriteRuleTokenStream stream_RCURLY = new RewriteRuleTokenStream(
				adaptor, "token RCURLY");
		RewriteRuleSubtreeStream stream_block = new RewriteRuleSubtreeStream(
				adaptor, "rule block");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:680:3: ( LCURLY block
			// ( SEMICOLON block )* ( SEMICOLON )? RCURLY -> ^( TOK_EXECUTEBLOCK
			// ( block )+ ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:681:3: LCURLY block (
			// SEMICOLON block )* ( SEMICOLON )? RCURLY
			{
				LCURLY181 = (Token) match(input, LCURLY,
						FOLLOW_LCURLY_in_executeBlock3801);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_LCURLY.add(LCURLY181);

				pushFollow(FOLLOW_block_in_executeBlock3803);
				block182 = block();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_block.add(block182.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:681:16: (
				// SEMICOLON block )*
				loop55: while (true) {
					int alt55 = 2;
					int LA55_0 = input.LA(1);
					if ((LA55_0 == SEMICOLON)) {
						int LA55_1 = input.LA(2);
						if (((LA55_1 >= Identifier && LA55_1 <= IdentifierRef)
								|| LA55_1 == KW_FOR || LA55_1 == KW_IF)) {
							alt55 = 1;
						}

					}

					switch (alt55) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:681:17:
					// SEMICOLON block
					{
						SEMICOLON183 = (Token) match(input, SEMICOLON,
								FOLLOW_SEMICOLON_in_executeBlock3806);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_SEMICOLON.add(SEMICOLON183);

						pushFollow(FOLLOW_block_in_executeBlock3808);
						block184 = block();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_block.add(block184.getTree());
					}
						break;

					default:
						break loop55;
					}
				}

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:681:35: (
				// SEMICOLON )?
				int alt56 = 2;
				int LA56_0 = input.LA(1);
				if ((LA56_0 == SEMICOLON)) {
					alt56 = 1;
				}
				switch (alt56) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:681:35: SEMICOLON
				{
					SEMICOLON185 = (Token) match(input, SEMICOLON,
							FOLLOW_SEMICOLON_in_executeBlock3812);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_SEMICOLON.add(SEMICOLON185);

				}
					break;

				}

				RCURLY186 = (Token) match(input, RCURLY,
						FOLLOW_RCURLY_in_executeBlock3815);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_RCURLY.add(RCURLY186);

				// AST REWRITE
				// elements: block
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 682:5: -> ^( TOK_EXECUTEBLOCK ( block )+ )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:683:7: ^(
						// TOK_EXECUTEBLOCK ( block )+ )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(
											TOK_EXECUTEBLOCK,
											"TOK_EXECUTEBLOCK"), root_1);
							if (!(stream_block.hasNext())) {
								throw new RewriteEarlyExitException();
							}
							while (stream_block.hasNext()) {
								adaptor.addChild(root_1,
										stream_block.nextTree());
							}
							stream_block.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "executeBlock"

	public static class assignable_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "assignable"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:686:1: assignable : v= (
	// Identifier | IdentifierRef ) ASSIGN expression -> ^( TOK_ASSIGN $v
	// expression ) ;
	public final TrcParser.assignable_return assignable()
			throws RecognitionException {
		TrcParser.assignable_return retval = new TrcParser.assignable_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token v = null;
		Token ASSIGN187 = null;
		ParserRuleReturnScope expression188 = null;

		CommonTree v_tree = null;
		CommonTree ASSIGN187_tree = null;
		RewriteRuleTokenStream stream_IdentifierRef = new RewriteRuleTokenStream(
				adaptor, "token IdentifierRef");
		RewriteRuleTokenStream stream_Identifier = new RewriteRuleTokenStream(
				adaptor, "token Identifier");
		RewriteRuleTokenStream stream_ASSIGN = new RewriteRuleTokenStream(
				adaptor, "token ASSIGN");
		RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(
				adaptor, "rule expression");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:687:3: (v= (
			// Identifier | IdentifierRef ) ASSIGN expression -> ^( TOK_ASSIGN
			// $v expression ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:688:3: v= ( Identifier
			// | IdentifierRef ) ASSIGN expression
			{
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:689:3: (
				// Identifier | IdentifierRef )
				int alt57 = 2;
				int LA57_0 = input.LA(1);
				if ((LA57_0 == Identifier)) {
					alt57 = 1;
				} else if ((LA57_0 == IdentifierRef)) {
					alt57 = 2;
				}

				else {
					if (state.backtracking > 0) {
						state.failed = true;
						return retval;
					}
					NoViableAltException nvae = new NoViableAltException("",
							57, 0, input);
					throw nvae;
				}

				switch (alt57) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:690:5: Identifier
				{
					v = (Token) match(input, Identifier,
							FOLLOW_Identifier_in_assignable3860);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_Identifier.add(v);

				}
					break;
				case 2:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:691:7:
				// IdentifierRef
				{
					v = (Token) match(input, IdentifierRef,
							FOLLOW_IdentifierRef_in_assignable3868);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_IdentifierRef.add(v);

				}
					break;

				}

				ASSIGN187 = (Token) match(input, ASSIGN,
						FOLLOW_ASSIGN_in_assignable3876);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_ASSIGN.add(ASSIGN187);

				pushFollow(FOLLOW_expression_in_assignable3878);
				expression188 = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_expression.add(expression188.getTree());
				// AST REWRITE
				// elements: expression, v
				// token labels: v
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_v = new RewriteRuleTokenStream(
							adaptor, "token v", v);
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 694:5: -> ^( TOK_ASSIGN $v expression )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:695:7: ^(
						// TOK_ASSIGN $v expression )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_ASSIGN,
											"TOK_ASSIGN"), root_1);
							adaptor.addChild(root_1, stream_v.nextNode());
							adaptor.addChild(root_1,
									stream_expression.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "assignable"

	public static class block_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "block"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:698:1: block : ( assignable |
	// ifblock | forblock );
	public final TrcParser.block_return block() throws RecognitionException {
		TrcParser.block_return retval = new TrcParser.block_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope assignable189 = null;
		ParserRuleReturnScope ifblock190 = null;
		ParserRuleReturnScope forblock191 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:699:3: ( assignable |
			// ifblock | forblock )
			int alt58 = 3;
			switch (input.LA(1)) {
			case Identifier:
			case IdentifierRef: {
				alt58 = 1;
			}
				break;
			case KW_IF: {
				alt58 = 2;
			}
				break;
			case KW_FOR: {
				alt58 = 3;
			}
				break;
			default:
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 58, 0,
						input);
				throw nvae;
			}
			switch (alt58) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:700:3: assignable
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_assignable_in_block3914);
				assignable189 = assignable();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, assignable189.getTree());

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:701:5: ifblock
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_ifblock_in_block3920);
				ifblock190 = ifblock();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, ifblock190.getTree());

			}
				break;
			case 3:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:702:5: forblock
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_forblock_in_block3926);
				forblock191 = forblock();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, forblock191.getTree());

			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "block"

	public static class ifblock_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "ifblock"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:705:1: ifblock : KW_IF LPAREN
	// cond= searchCondition block ( SEMICOLON block )* ( SEMICOLON )? RPAREN ->
	// ^( TOK_IF $cond ( block )+ ) ;
	public final TrcParser.ifblock_return ifblock() throws RecognitionException {
		TrcParser.ifblock_return retval = new TrcParser.ifblock_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_IF192 = null;
		Token LPAREN193 = null;
		Token SEMICOLON195 = null;
		Token SEMICOLON197 = null;
		Token RPAREN198 = null;
		ParserRuleReturnScope cond = null;
		ParserRuleReturnScope block194 = null;
		ParserRuleReturnScope block196 = null;

		CommonTree KW_IF192_tree = null;
		CommonTree LPAREN193_tree = null;
		CommonTree SEMICOLON195_tree = null;
		CommonTree SEMICOLON197_tree = null;
		CommonTree RPAREN198_tree = null;
		RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(
				adaptor, "token RPAREN");
		RewriteRuleTokenStream stream_SEMICOLON = new RewriteRuleTokenStream(
				adaptor, "token SEMICOLON");
		RewriteRuleTokenStream stream_KW_IF = new RewriteRuleTokenStream(
				adaptor, "token KW_IF");
		RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(
				adaptor, "token LPAREN");
		RewriteRuleSubtreeStream stream_searchCondition = new RewriteRuleSubtreeStream(
				adaptor, "rule searchCondition");
		RewriteRuleSubtreeStream stream_block = new RewriteRuleSubtreeStream(
				adaptor, "rule block");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:706:3: ( KW_IF LPAREN
			// cond= searchCondition block ( SEMICOLON block )* ( SEMICOLON )?
			// RPAREN -> ^( TOK_IF $cond ( block )+ ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:707:3: KW_IF LPAREN
			// cond= searchCondition block ( SEMICOLON block )* ( SEMICOLON )?
			// RPAREN
			{
				KW_IF192 = (Token) match(input, KW_IF,
						FOLLOW_KW_IF_in_ifblock3941);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_IF.add(KW_IF192);

				LPAREN193 = (Token) match(input, LPAREN,
						FOLLOW_LPAREN_in_ifblock3943);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_LPAREN.add(LPAREN193);

				pushFollow(FOLLOW_searchCondition_in_ifblock3947);
				cond = searchCondition();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_searchCondition.add(cond.getTree());
				pushFollow(FOLLOW_block_in_ifblock3949);
				block194 = block();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_block.add(block194.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:707:43: (
				// SEMICOLON block )*
				loop59: while (true) {
					int alt59 = 2;
					int LA59_0 = input.LA(1);
					if ((LA59_0 == SEMICOLON)) {
						int LA59_1 = input.LA(2);
						if (((LA59_1 >= Identifier && LA59_1 <= IdentifierRef)
								|| LA59_1 == KW_FOR || LA59_1 == KW_IF)) {
							alt59 = 1;
						}

					}

					switch (alt59) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:707:44:
					// SEMICOLON block
					{
						SEMICOLON195 = (Token) match(input, SEMICOLON,
								FOLLOW_SEMICOLON_in_ifblock3952);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_SEMICOLON.add(SEMICOLON195);

						pushFollow(FOLLOW_block_in_ifblock3954);
						block196 = block();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_block.add(block196.getTree());
					}
						break;

					default:
						break loop59;
					}
				}

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:707:62: (
				// SEMICOLON )?
				int alt60 = 2;
				int LA60_0 = input.LA(1);
				if ((LA60_0 == SEMICOLON)) {
					alt60 = 1;
				}
				switch (alt60) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:707:62: SEMICOLON
				{
					SEMICOLON197 = (Token) match(input, SEMICOLON,
							FOLLOW_SEMICOLON_in_ifblock3958);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_SEMICOLON.add(SEMICOLON197);

				}
					break;

				}

				RPAREN198 = (Token) match(input, RPAREN,
						FOLLOW_RPAREN_in_ifblock3961);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_RPAREN.add(RPAREN198);

				// AST REWRITE
				// elements: block, cond
				// token labels:
				// rule labels: retval, cond
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);
					RewriteRuleSubtreeStream stream_cond = new RewriteRuleSubtreeStream(
							adaptor, "rule cond", cond != null ? cond.getTree()
									: null);

					root_0 = (CommonTree) adaptor.nil();
					// 708:5: -> ^( TOK_IF $cond ( block )+ )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:709:7: ^(
						// TOK_IF $cond ( block )+ )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_IF,
											"TOK_IF"), root_1);
							adaptor.addChild(root_1, stream_cond.nextTree());
							if (!(stream_block.hasNext())) {
								throw new RewriteEarlyExitException();
							}
							while (stream_block.hasNext()) {
								adaptor.addChild(root_1,
										stream_block.nextTree());
							}
							stream_block.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "ifblock"

	public static class forblock_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "forblock"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:712:1: forblock : KW_FOR
	// LPAREN cond= searchCondition block ( SEMICOLON block )* ( SEMICOLON )?
	// RPAREN -> ^( TOK_FOR $cond ( block )+ ) ;
	public final TrcParser.forblock_return forblock()
			throws RecognitionException {
		TrcParser.forblock_return retval = new TrcParser.forblock_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_FOR199 = null;
		Token LPAREN200 = null;
		Token SEMICOLON202 = null;
		Token SEMICOLON204 = null;
		Token RPAREN205 = null;
		ParserRuleReturnScope cond = null;
		ParserRuleReturnScope block201 = null;
		ParserRuleReturnScope block203 = null;

		CommonTree KW_FOR199_tree = null;
		CommonTree LPAREN200_tree = null;
		CommonTree SEMICOLON202_tree = null;
		CommonTree SEMICOLON204_tree = null;
		CommonTree RPAREN205_tree = null;
		RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(
				adaptor, "token RPAREN");
		RewriteRuleTokenStream stream_SEMICOLON = new RewriteRuleTokenStream(
				adaptor, "token SEMICOLON");
		RewriteRuleTokenStream stream_KW_FOR = new RewriteRuleTokenStream(
				adaptor, "token KW_FOR");
		RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(
				adaptor, "token LPAREN");
		RewriteRuleSubtreeStream stream_searchCondition = new RewriteRuleSubtreeStream(
				adaptor, "rule searchCondition");
		RewriteRuleSubtreeStream stream_block = new RewriteRuleSubtreeStream(
				adaptor, "rule block");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:713:3: ( KW_FOR LPAREN
			// cond= searchCondition block ( SEMICOLON block )* ( SEMICOLON )?
			// RPAREN -> ^( TOK_FOR $cond ( block )+ ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:714:3: KW_FOR LPAREN
			// cond= searchCondition block ( SEMICOLON block )* ( SEMICOLON )?
			// RPAREN
			{
				KW_FOR199 = (Token) match(input, KW_FOR,
						FOLLOW_KW_FOR_in_forblock3998);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_FOR.add(KW_FOR199);

				LPAREN200 = (Token) match(input, LPAREN,
						FOLLOW_LPAREN_in_forblock4000);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_LPAREN.add(LPAREN200);

				pushFollow(FOLLOW_searchCondition_in_forblock4004);
				cond = searchCondition();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_searchCondition.add(cond.getTree());
				pushFollow(FOLLOW_block_in_forblock4006);
				block201 = block();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_block.add(block201.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:714:44: (
				// SEMICOLON block )*
				loop61: while (true) {
					int alt61 = 2;
					int LA61_0 = input.LA(1);
					if ((LA61_0 == SEMICOLON)) {
						int LA61_1 = input.LA(2);
						if (((LA61_1 >= Identifier && LA61_1 <= IdentifierRef)
								|| LA61_1 == KW_FOR || LA61_1 == KW_IF)) {
							alt61 = 1;
						}

					}

					switch (alt61) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:714:45:
					// SEMICOLON block
					{
						SEMICOLON202 = (Token) match(input, SEMICOLON,
								FOLLOW_SEMICOLON_in_forblock4009);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_SEMICOLON.add(SEMICOLON202);

						pushFollow(FOLLOW_block_in_forblock4011);
						block203 = block();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_block.add(block203.getTree());
					}
						break;

					default:
						break loop61;
					}
				}

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:714:63: (
				// SEMICOLON )?
				int alt62 = 2;
				int LA62_0 = input.LA(1);
				if ((LA62_0 == SEMICOLON)) {
					alt62 = 1;
				}
				switch (alt62) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:714:63: SEMICOLON
				{
					SEMICOLON204 = (Token) match(input, SEMICOLON,
							FOLLOW_SEMICOLON_in_forblock4015);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_SEMICOLON.add(SEMICOLON204);

				}
					break;

				}

				RPAREN205 = (Token) match(input, RPAREN,
						FOLLOW_RPAREN_in_forblock4018);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_RPAREN.add(RPAREN205);

				// AST REWRITE
				// elements: cond, block
				// token labels:
				// rule labels: retval, cond
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);
					RewriteRuleSubtreeStream stream_cond = new RewriteRuleSubtreeStream(
							adaptor, "rule cond", cond != null ? cond.getTree()
									: null);

					root_0 = (CommonTree) adaptor.nil();
					// 715:5: -> ^( TOK_FOR $cond ( block )+ )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:716:7: ^(
						// TOK_FOR $cond ( block )+ )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_FOR,
											"TOK_FOR"), root_1);
							adaptor.addChild(root_1, stream_cond.nextTree());
							if (!(stream_block.hasNext())) {
								throw new RewriteEarlyExitException();
							}
							while (stream_block.hasNext()) {
								adaptor.addChild(root_1,
										stream_block.nextTree());
							}
							stream_block.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "forblock"

	public static class emitValue_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "emitValue"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:719:1: emitValue : KW_EMIT
	// expression -> ^( TOK_EMIT expression ) ;
	public final TrcParser.emitValue_return emitValue()
			throws RecognitionException {
		TrcParser.emitValue_return retval = new TrcParser.emitValue_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_EMIT206 = null;
		ParserRuleReturnScope expression207 = null;

		CommonTree KW_EMIT206_tree = null;
		RewriteRuleTokenStream stream_KW_EMIT = new RewriteRuleTokenStream(
				adaptor, "token KW_EMIT");
		RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(
				adaptor, "rule expression");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:720:3: ( KW_EMIT
			// expression -> ^( TOK_EMIT expression ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:721:3: KW_EMIT
			// expression
			{
				KW_EMIT206 = (Token) match(input, KW_EMIT,
						FOLLOW_KW_EMIT_in_emitValue4055);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_EMIT.add(KW_EMIT206);

				pushFollow(FOLLOW_expression_in_emitValue4057);
				expression207 = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_expression.add(expression207.getTree());
				// AST REWRITE
				// elements: expression
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 722:5: -> ^( TOK_EMIT expression )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:723:7: ^(
						// TOK_EMIT expression )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_EMIT,
											"TOK_EMIT"), root_1);
							adaptor.addChild(root_1,
									stream_expression.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "emitValue"

	public static class functionName_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "functionName"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:726:1: functionName : (
	// Identifier | KW_IF | KW_ARRAY | KW_MAP | KW_STRUCT | KW_UNIONTYPE );
	public final TrcParser.functionName_return functionName()
			throws RecognitionException {
		TrcParser.functionName_return retval = new TrcParser.functionName_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token set208 = null;

		CommonTree set208_tree = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:727:3: ( Identifier |
			// KW_IF | KW_ARRAY | KW_MAP | KW_STRUCT | KW_UNIONTYPE )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:
			{
				root_0 = (CommonTree) adaptor.nil();

				set208 = input.LT(1);
				if (input.LA(1) == Identifier || input.LA(1) == KW_ARRAY
						|| input.LA(1) == KW_IF || input.LA(1) == KW_MAP
						|| input.LA(1) == KW_STRUCT
						|| input.LA(1) == KW_UNIONTYPE) {
					input.consume();
					if (state.backtracking == 0)
						adaptor.addChild(root_0,
								(CommonTree) adaptor.create(set208));
					state.errorRecovery = false;
					state.failed = false;
				} else {
					if (state.backtracking > 0) {
						state.failed = true;
						return retval;
					}
					MismatchedSetException mse = new MismatchedSetException(
							null, input);
					throw mse;
				}
			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "functionName"

	public static class castExpression_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "castExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:737:1: castExpression :
	// KW_CAST LPAREN expression KW_AS primitiveType RPAREN -> ^( TOK_FUNCTION
	// primitiveType expression ) ;
	public final TrcParser.castExpression_return castExpression()
			throws RecognitionException {
		TrcParser.castExpression_return retval = new TrcParser.castExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_CAST209 = null;
		Token LPAREN210 = null;
		Token KW_AS212 = null;
		Token RPAREN214 = null;
		ParserRuleReturnScope expression211 = null;
		ParserRuleReturnScope primitiveType213 = null;

		CommonTree KW_CAST209_tree = null;
		CommonTree LPAREN210_tree = null;
		CommonTree KW_AS212_tree = null;
		CommonTree RPAREN214_tree = null;
		RewriteRuleTokenStream stream_KW_AS = new RewriteRuleTokenStream(
				adaptor, "token KW_AS");
		RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(
				adaptor, "token RPAREN");
		RewriteRuleTokenStream stream_KW_CAST = new RewriteRuleTokenStream(
				adaptor, "token KW_CAST");
		RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(
				adaptor, "token LPAREN");
		RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(
				adaptor, "rule expression");
		RewriteRuleSubtreeStream stream_primitiveType = new RewriteRuleSubtreeStream(
				adaptor, "rule primitiveType");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:738:3: ( KW_CAST
			// LPAREN expression KW_AS primitiveType RPAREN -> ^( TOK_FUNCTION
			// primitiveType expression ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:739:3: KW_CAST LPAREN
			// expression KW_AS primitiveType RPAREN
			{
				KW_CAST209 = (Token) match(input, KW_CAST,
						FOLLOW_KW_CAST_in_castExpression4138);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_CAST.add(KW_CAST209);

				LPAREN210 = (Token) match(input, LPAREN,
						FOLLOW_LPAREN_in_castExpression4140);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_LPAREN.add(LPAREN210);

				pushFollow(FOLLOW_expression_in_castExpression4142);
				expression211 = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_expression.add(expression211.getTree());
				KW_AS212 = (Token) match(input, KW_AS,
						FOLLOW_KW_AS_in_castExpression4144);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_AS.add(KW_AS212);

				pushFollow(FOLLOW_primitiveType_in_castExpression4146);
				primitiveType213 = primitiveType();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_primitiveType.add(primitiveType213.getTree());
				RPAREN214 = (Token) match(input, RPAREN,
						FOLLOW_RPAREN_in_castExpression4148);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_RPAREN.add(RPAREN214);

				// AST REWRITE
				// elements: primitiveType, expression
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 740:5: -> ^( TOK_FUNCTION primitiveType expression )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:741:7: ^(
						// TOK_FUNCTION primitiveType expression )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_FUNCTION,
											"TOK_FUNCTION"), root_1);
							adaptor.addChild(root_1,
									stream_primitiveType.nextTree());
							adaptor.addChild(root_1,
									stream_expression.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "castExpression"

	public static class primitiveType_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "primitiveType"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:744:1: primitiveType : (
	// KW_TINYINT -> TOK_TINYINT | KW_SMALLINT -> TOK_SMALLINT | KW_INT ->
	// TOK_INT | KW_BIGINT -> TOK_BIGINT | KW_BOOLEAN -> TOK_BOOLEAN | KW_FLOAT
	// -> TOK_FLOAT | KW_DOUBLE -> TOK_DOUBLE | KW_DATE -> TOK_DATE |
	// KW_DATETIME -> TOK_DATETIME | KW_TIMESTAMP -> TOK_TIMESTAMP | KW_STRING
	// -> TOK_STRING | KW_BINARY -> TOK_BINARY );
	public final TrcParser.primitiveType_return primitiveType()
			throws RecognitionException {
		TrcParser.primitiveType_return retval = new TrcParser.primitiveType_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_TINYINT215 = null;
		Token KW_SMALLINT216 = null;
		Token KW_INT217 = null;
		Token KW_BIGINT218 = null;
		Token KW_BOOLEAN219 = null;
		Token KW_FLOAT220 = null;
		Token KW_DOUBLE221 = null;
		Token KW_DATE222 = null;
		Token KW_DATETIME223 = null;
		Token KW_TIMESTAMP224 = null;
		Token KW_STRING225 = null;
		Token KW_BINARY226 = null;

		CommonTree KW_TINYINT215_tree = null;
		CommonTree KW_SMALLINT216_tree = null;
		CommonTree KW_INT217_tree = null;
		CommonTree KW_BIGINT218_tree = null;
		CommonTree KW_BOOLEAN219_tree = null;
		CommonTree KW_FLOAT220_tree = null;
		CommonTree KW_DOUBLE221_tree = null;
		CommonTree KW_DATE222_tree = null;
		CommonTree KW_DATETIME223_tree = null;
		CommonTree KW_TIMESTAMP224_tree = null;
		CommonTree KW_STRING225_tree = null;
		CommonTree KW_BINARY226_tree = null;
		RewriteRuleTokenStream stream_KW_DATETIME = new RewriteRuleTokenStream(
				adaptor, "token KW_DATETIME");
		RewriteRuleTokenStream stream_KW_STRING = new RewriteRuleTokenStream(
				adaptor, "token KW_STRING");
		RewriteRuleTokenStream stream_KW_TIMESTAMP = new RewriteRuleTokenStream(
				adaptor, "token KW_TIMESTAMP");
		RewriteRuleTokenStream stream_KW_DATE = new RewriteRuleTokenStream(
				adaptor, "token KW_DATE");
		RewriteRuleTokenStream stream_KW_FLOAT = new RewriteRuleTokenStream(
				adaptor, "token KW_FLOAT");
		RewriteRuleTokenStream stream_KW_BINARY = new RewriteRuleTokenStream(
				adaptor, "token KW_BINARY");
		RewriteRuleTokenStream stream_KW_INT = new RewriteRuleTokenStream(
				adaptor, "token KW_INT");
		RewriteRuleTokenStream stream_KW_SMALLINT = new RewriteRuleTokenStream(
				adaptor, "token KW_SMALLINT");
		RewriteRuleTokenStream stream_KW_DOUBLE = new RewriteRuleTokenStream(
				adaptor, "token KW_DOUBLE");
		RewriteRuleTokenStream stream_KW_BIGINT = new RewriteRuleTokenStream(
				adaptor, "token KW_BIGINT");
		RewriteRuleTokenStream stream_KW_TINYINT = new RewriteRuleTokenStream(
				adaptor, "token KW_TINYINT");
		RewriteRuleTokenStream stream_KW_BOOLEAN = new RewriteRuleTokenStream(
				adaptor, "token KW_BOOLEAN");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:745:3: ( KW_TINYINT ->
			// TOK_TINYINT | KW_SMALLINT -> TOK_SMALLINT | KW_INT -> TOK_INT |
			// KW_BIGINT -> TOK_BIGINT | KW_BOOLEAN -> TOK_BOOLEAN | KW_FLOAT ->
			// TOK_FLOAT | KW_DOUBLE -> TOK_DOUBLE | KW_DATE -> TOK_DATE |
			// KW_DATETIME -> TOK_DATETIME | KW_TIMESTAMP -> TOK_TIMESTAMP |
			// KW_STRING -> TOK_STRING | KW_BINARY -> TOK_BINARY )
			int alt63 = 12;
			switch (input.LA(1)) {
			case KW_TINYINT: {
				alt63 = 1;
			}
				break;
			case KW_SMALLINT: {
				alt63 = 2;
			}
				break;
			case KW_INT: {
				alt63 = 3;
			}
				break;
			case KW_BIGINT: {
				alt63 = 4;
			}
				break;
			case KW_BOOLEAN: {
				alt63 = 5;
			}
				break;
			case KW_FLOAT: {
				alt63 = 6;
			}
				break;
			case KW_DOUBLE: {
				alt63 = 7;
			}
				break;
			case KW_DATE: {
				alt63 = 8;
			}
				break;
			case KW_DATETIME: {
				alt63 = 9;
			}
				break;
			case KW_TIMESTAMP: {
				alt63 = 10;
			}
				break;
			case KW_STRING: {
				alt63 = 11;
			}
				break;
			case KW_BINARY: {
				alt63 = 12;
			}
				break;
			default:
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 63, 0,
						input);
				throw nvae;
			}
			switch (alt63) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:746:3: KW_TINYINT
			{
				KW_TINYINT215 = (Token) match(input, KW_TINYINT,
						FOLLOW_KW_TINYINT_in_primitiveType4183);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_TINYINT.add(KW_TINYINT215);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 747:5: -> TOK_TINYINT
					{
						adaptor.addChild(root_0, (CommonTree) adaptor.create(
								TOK_TINYINT, "TOK_TINYINT"));
					}

					retval.tree = root_0;
				}

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:748:5: KW_SMALLINT
			{
				KW_SMALLINT216 = (Token) match(input, KW_SMALLINT,
						FOLLOW_KW_SMALLINT_in_primitiveType4197);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_SMALLINT.add(KW_SMALLINT216);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 749:5: -> TOK_SMALLINT
					{
						adaptor.addChild(root_0, (CommonTree) adaptor.create(
								TOK_SMALLINT, "TOK_SMALLINT"));
					}

					retval.tree = root_0;
				}

			}
				break;
			case 3:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:750:5: KW_INT
			{
				KW_INT217 = (Token) match(input, KW_INT,
						FOLLOW_KW_INT_in_primitiveType4211);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_INT.add(KW_INT217);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 751:5: -> TOK_INT
					{
						adaptor.addChild(root_0,
								(CommonTree) adaptor.create(TOK_INT, "TOK_INT"));
					}

					retval.tree = root_0;
				}

			}
				break;
			case 4:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:752:5: KW_BIGINT
			{
				KW_BIGINT218 = (Token) match(input, KW_BIGINT,
						FOLLOW_KW_BIGINT_in_primitiveType4225);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_BIGINT.add(KW_BIGINT218);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 753:5: -> TOK_BIGINT
					{
						adaptor.addChild(root_0, (CommonTree) adaptor.create(
								TOK_BIGINT, "TOK_BIGINT"));
					}

					retval.tree = root_0;
				}

			}
				break;
			case 5:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:754:5: KW_BOOLEAN
			{
				KW_BOOLEAN219 = (Token) match(input, KW_BOOLEAN,
						FOLLOW_KW_BOOLEAN_in_primitiveType4239);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_BOOLEAN.add(KW_BOOLEAN219);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 755:5: -> TOK_BOOLEAN
					{
						adaptor.addChild(root_0, (CommonTree) adaptor.create(
								TOK_BOOLEAN, "TOK_BOOLEAN"));
					}

					retval.tree = root_0;
				}

			}
				break;
			case 6:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:756:5: KW_FLOAT
			{
				KW_FLOAT220 = (Token) match(input, KW_FLOAT,
						FOLLOW_KW_FLOAT_in_primitiveType4253);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_FLOAT.add(KW_FLOAT220);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 757:5: -> TOK_FLOAT
					{
						adaptor.addChild(root_0, (CommonTree) adaptor.create(
								TOK_FLOAT, "TOK_FLOAT"));
					}

					retval.tree = root_0;
				}

			}
				break;
			case 7:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:758:5: KW_DOUBLE
			{
				KW_DOUBLE221 = (Token) match(input, KW_DOUBLE,
						FOLLOW_KW_DOUBLE_in_primitiveType4267);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_DOUBLE.add(KW_DOUBLE221);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 759:5: -> TOK_DOUBLE
					{
						adaptor.addChild(root_0, (CommonTree) adaptor.create(
								TOK_DOUBLE, "TOK_DOUBLE"));
					}

					retval.tree = root_0;
				}

			}
				break;
			case 8:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:760:5: KW_DATE
			{
				KW_DATE222 = (Token) match(input, KW_DATE,
						FOLLOW_KW_DATE_in_primitiveType4281);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_DATE.add(KW_DATE222);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 761:5: -> TOK_DATE
					{
						adaptor.addChild(root_0, (CommonTree) adaptor.create(
								TOK_DATE, "TOK_DATE"));
					}

					retval.tree = root_0;
				}

			}
				break;
			case 9:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:762:5: KW_DATETIME
			{
				KW_DATETIME223 = (Token) match(input, KW_DATETIME,
						FOLLOW_KW_DATETIME_in_primitiveType4295);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_DATETIME.add(KW_DATETIME223);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 763:5: -> TOK_DATETIME
					{
						adaptor.addChild(root_0, (CommonTree) adaptor.create(
								TOK_DATETIME, "TOK_DATETIME"));
					}

					retval.tree = root_0;
				}

			}
				break;
			case 10:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:764:5: KW_TIMESTAMP
			{
				KW_TIMESTAMP224 = (Token) match(input, KW_TIMESTAMP,
						FOLLOW_KW_TIMESTAMP_in_primitiveType4309);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_TIMESTAMP.add(KW_TIMESTAMP224);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 765:5: -> TOK_TIMESTAMP
					{
						adaptor.addChild(root_0, (CommonTree) adaptor.create(
								TOK_TIMESTAMP, "TOK_TIMESTAMP"));
					}

					retval.tree = root_0;
				}

			}
				break;
			case 11:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:766:5: KW_STRING
			{
				KW_STRING225 = (Token) match(input, KW_STRING,
						FOLLOW_KW_STRING_in_primitiveType4323);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_STRING.add(KW_STRING225);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 767:5: -> TOK_STRING
					{
						adaptor.addChild(root_0, (CommonTree) adaptor.create(
								TOK_STRING, "TOK_STRING"));
					}

					retval.tree = root_0;
				}

			}
				break;
			case 12:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:768:5: KW_BINARY
			{
				KW_BINARY226 = (Token) match(input, KW_BINARY,
						FOLLOW_KW_BINARY_in_primitiveType4337);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_BINARY.add(KW_BINARY226);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 769:5: -> TOK_BINARY
					{
						adaptor.addChild(root_0, (CommonTree) adaptor.create(
								TOK_BINARY, "TOK_BINARY"));
					}

					retval.tree = root_0;
				}

			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "primitiveType"

	public static class primitiveTypeDef_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "primitiveTypeDef"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:772:1: primitiveTypeDef : (
	// KW_TINYINT | KW_SMALLINT | KW_INT | KW_BIGINT | KW_BOOLEAN | KW_FLOAT |
	// KW_DOUBLE | KW_DATE | KW_DATETIME | KW_TIMESTAMP | KW_STRING | KW_BINARY
	// );
	public final TrcParser.primitiveTypeDef_return primitiveTypeDef()
			throws RecognitionException {
		TrcParser.primitiveTypeDef_return retval = new TrcParser.primitiveTypeDef_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token set227 = null;

		CommonTree set227_tree = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:773:3: ( KW_TINYINT |
			// KW_SMALLINT | KW_INT | KW_BIGINT | KW_BOOLEAN | KW_FLOAT |
			// KW_DOUBLE | KW_DATE | KW_DATETIME | KW_TIMESTAMP | KW_STRING |
			// KW_BINARY )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:
			{
				root_0 = (CommonTree) adaptor.nil();

				set227 = input.LT(1);
				if ((input.LA(1) >= KW_BIGINT && input.LA(1) <= KW_BOOLEAN)
						|| (input.LA(1) >= KW_DATE && input.LA(1) <= KW_DATETIME)
						|| input.LA(1) == KW_DOUBLE
						|| input.LA(1) == KW_FLOAT
						|| input.LA(1) == KW_INT
						|| input.LA(1) == KW_SMALLINT
						|| input.LA(1) == KW_STRING
						|| (input.LA(1) >= KW_TIMESTAMP && input.LA(1) <= KW_TINYINT)) {
					input.consume();
					if (state.backtracking == 0)
						adaptor.addChild(root_0,
								(CommonTree) adaptor.create(set227));
					state.errorRecovery = false;
					state.failed = false;
				} else {
					if (state.backtracking > 0) {
						state.failed = true;
						return retval;
					}
					MismatchedSetException mse = new MismatchedSetException(
							null, input);
					throw mse;
				}
			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "primitiveTypeDef"

	public static class caseExpression_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "caseExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:788:1: caseExpression :
	// KW_CASE expression ( KW_WHEN expression KW_THEN expression )+ ( KW_ELSE
	// expression )? KW_END -> ^( TOK_FUNCTION KW_CASE ( expression )* ) ;
	public final TrcParser.caseExpression_return caseExpression()
			throws RecognitionException {
		TrcParser.caseExpression_return retval = new TrcParser.caseExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_CASE228 = null;
		Token KW_WHEN230 = null;
		Token KW_THEN232 = null;
		Token KW_ELSE234 = null;
		Token KW_END236 = null;
		ParserRuleReturnScope expression229 = null;
		ParserRuleReturnScope expression231 = null;
		ParserRuleReturnScope expression233 = null;
		ParserRuleReturnScope expression235 = null;

		CommonTree KW_CASE228_tree = null;
		CommonTree KW_WHEN230_tree = null;
		CommonTree KW_THEN232_tree = null;
		CommonTree KW_ELSE234_tree = null;
		CommonTree KW_END236_tree = null;
		RewriteRuleTokenStream stream_KW_THEN = new RewriteRuleTokenStream(
				adaptor, "token KW_THEN");
		RewriteRuleTokenStream stream_KW_CASE = new RewriteRuleTokenStream(
				adaptor, "token KW_CASE");
		RewriteRuleTokenStream stream_KW_WHEN = new RewriteRuleTokenStream(
				adaptor, "token KW_WHEN");
		RewriteRuleTokenStream stream_KW_END = new RewriteRuleTokenStream(
				adaptor, "token KW_END");
		RewriteRuleTokenStream stream_KW_ELSE = new RewriteRuleTokenStream(
				adaptor, "token KW_ELSE");
		RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(
				adaptor, "rule expression");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:789:3: ( KW_CASE
			// expression ( KW_WHEN expression KW_THEN expression )+ ( KW_ELSE
			// expression )? KW_END -> ^( TOK_FUNCTION KW_CASE ( expression )* )
			// )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:790:3: KW_CASE
			// expression ( KW_WHEN expression KW_THEN expression )+ ( KW_ELSE
			// expression )? KW_END
			{
				KW_CASE228 = (Token) match(input, KW_CASE,
						FOLLOW_KW_CASE_in_caseExpression4441);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_CASE.add(KW_CASE228);

				pushFollow(FOLLOW_expression_in_caseExpression4443);
				expression229 = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_expression.add(expression229.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:790:22: ( KW_WHEN
				// expression KW_THEN expression )+
				int cnt64 = 0;
				loop64: while (true) {
					int alt64 = 2;
					int LA64_0 = input.LA(1);
					if ((LA64_0 == KW_WHEN)) {
						alt64 = 1;
					}

					switch (alt64) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:790:23:
					// KW_WHEN expression KW_THEN expression
					{
						KW_WHEN230 = (Token) match(input, KW_WHEN,
								FOLLOW_KW_WHEN_in_caseExpression4446);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_KW_WHEN.add(KW_WHEN230);

						pushFollow(FOLLOW_expression_in_caseExpression4448);
						expression231 = expression();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_expression.add(expression231.getTree());
						KW_THEN232 = (Token) match(input, KW_THEN,
								FOLLOW_KW_THEN_in_caseExpression4450);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_KW_THEN.add(KW_THEN232);

						pushFollow(FOLLOW_expression_in_caseExpression4452);
						expression233 = expression();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_expression.add(expression233.getTree());
					}
						break;

					default:
						if (cnt64 >= 1)
							break loop64;
						if (state.backtracking > 0) {
							state.failed = true;
							return retval;
						}
						EarlyExitException eee = new EarlyExitException(64,
								input);
						throw eee;
					}
					cnt64++;
				}

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:790:63: ( KW_ELSE
				// expression )?
				int alt65 = 2;
				int LA65_0 = input.LA(1);
				if ((LA65_0 == KW_ELSE)) {
					alt65 = 1;
				}
				switch (alt65) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:790:64: KW_ELSE
				// expression
				{
					KW_ELSE234 = (Token) match(input, KW_ELSE,
							FOLLOW_KW_ELSE_in_caseExpression4457);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_KW_ELSE.add(KW_ELSE234);

					pushFollow(FOLLOW_expression_in_caseExpression4459);
					expression235 = expression();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_expression.add(expression235.getTree());
				}
					break;

				}

				KW_END236 = (Token) match(input, KW_END,
						FOLLOW_KW_END_in_caseExpression4463);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_END.add(KW_END236);

				// AST REWRITE
				// elements: KW_CASE, expression
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 791:5: -> ^( TOK_FUNCTION KW_CASE ( expression )* )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:792:7: ^(
						// TOK_FUNCTION KW_CASE ( expression )* )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_FUNCTION,
											"TOK_FUNCTION"), root_1);
							adaptor.addChild(root_1, stream_KW_CASE.nextNode());
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:792:30:
							// ( expression )*
							while (stream_expression.hasNext()) {
								adaptor.addChild(root_1,
										stream_expression.nextTree());
							}
							stream_expression.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "caseExpression"

	public static class whenExpression_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "whenExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:795:1: whenExpression :
	// KW_CASE ( KW_WHEN expression KW_THEN expression )+ ( KW_ELSE expression
	// )? KW_END -> ^( TOK_FUNCTION KW_WHEN ( expression )* ) ;
	public final TrcParser.whenExpression_return whenExpression()
			throws RecognitionException {
		TrcParser.whenExpression_return retval = new TrcParser.whenExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_CASE237 = null;
		Token KW_WHEN238 = null;
		Token KW_THEN240 = null;
		Token KW_ELSE242 = null;
		Token KW_END244 = null;
		ParserRuleReturnScope expression239 = null;
		ParserRuleReturnScope expression241 = null;
		ParserRuleReturnScope expression243 = null;

		CommonTree KW_CASE237_tree = null;
		CommonTree KW_WHEN238_tree = null;
		CommonTree KW_THEN240_tree = null;
		CommonTree KW_ELSE242_tree = null;
		CommonTree KW_END244_tree = null;
		RewriteRuleTokenStream stream_KW_THEN = new RewriteRuleTokenStream(
				adaptor, "token KW_THEN");
		RewriteRuleTokenStream stream_KW_CASE = new RewriteRuleTokenStream(
				adaptor, "token KW_CASE");
		RewriteRuleTokenStream stream_KW_WHEN = new RewriteRuleTokenStream(
				adaptor, "token KW_WHEN");
		RewriteRuleTokenStream stream_KW_END = new RewriteRuleTokenStream(
				adaptor, "token KW_END");
		RewriteRuleTokenStream stream_KW_ELSE = new RewriteRuleTokenStream(
				adaptor, "token KW_ELSE");
		RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(
				adaptor, "rule expression");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:796:3: ( KW_CASE (
			// KW_WHEN expression KW_THEN expression )+ ( KW_ELSE expression )?
			// KW_END -> ^( TOK_FUNCTION KW_WHEN ( expression )* ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:797:3: KW_CASE (
			// KW_WHEN expression KW_THEN expression )+ ( KW_ELSE expression )?
			// KW_END
			{
				KW_CASE237 = (Token) match(input, KW_CASE,
						FOLLOW_KW_CASE_in_whenExpression4499);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_CASE.add(KW_CASE237);

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:797:11: ( KW_WHEN
				// expression KW_THEN expression )+
				int cnt66 = 0;
				loop66: while (true) {
					int alt66 = 2;
					int LA66_0 = input.LA(1);
					if ((LA66_0 == KW_WHEN)) {
						alt66 = 1;
					}

					switch (alt66) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:797:12:
					// KW_WHEN expression KW_THEN expression
					{
						KW_WHEN238 = (Token) match(input, KW_WHEN,
								FOLLOW_KW_WHEN_in_whenExpression4502);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_KW_WHEN.add(KW_WHEN238);

						pushFollow(FOLLOW_expression_in_whenExpression4504);
						expression239 = expression();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_expression.add(expression239.getTree());
						KW_THEN240 = (Token) match(input, KW_THEN,
								FOLLOW_KW_THEN_in_whenExpression4506);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_KW_THEN.add(KW_THEN240);

						pushFollow(FOLLOW_expression_in_whenExpression4508);
						expression241 = expression();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_expression.add(expression241.getTree());
					}
						break;

					default:
						if (cnt66 >= 1)
							break loop66;
						if (state.backtracking > 0) {
							state.failed = true;
							return retval;
						}
						EarlyExitException eee = new EarlyExitException(66,
								input);
						throw eee;
					}
					cnt66++;
				}

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:797:52: ( KW_ELSE
				// expression )?
				int alt67 = 2;
				int LA67_0 = input.LA(1);
				if ((LA67_0 == KW_ELSE)) {
					alt67 = 1;
				}
				switch (alt67) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:797:53: KW_ELSE
				// expression
				{
					KW_ELSE242 = (Token) match(input, KW_ELSE,
							FOLLOW_KW_ELSE_in_whenExpression4513);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_KW_ELSE.add(KW_ELSE242);

					pushFollow(FOLLOW_expression_in_whenExpression4515);
					expression243 = expression();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_expression.add(expression243.getTree());
				}
					break;

				}

				KW_END244 = (Token) match(input, KW_END,
						FOLLOW_KW_END_in_whenExpression4519);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_END.add(KW_END244);

				// AST REWRITE
				// elements: KW_WHEN, expression
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 798:5: -> ^( TOK_FUNCTION KW_WHEN ( expression )* )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:799:7: ^(
						// TOK_FUNCTION KW_WHEN ( expression )* )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_FUNCTION,
											"TOK_FUNCTION"), root_1);
							adaptor.addChild(root_1, stream_KW_WHEN.nextNode());
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:799:30:
							// ( expression )*
							while (stream_expression.hasNext()) {
								adaptor.addChild(root_1,
										stream_expression.nextTree());
							}
							stream_expression.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "whenExpression"

	public static class expressions_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "expressions"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:802:1: expressions : LPAREN
	// expression ( COMMA expression )* RPAREN -> ( expression )* ;
	public final TrcParser.expressions_return expressions()
			throws RecognitionException {
		TrcParser.expressions_return retval = new TrcParser.expressions_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token LPAREN245 = null;
		Token COMMA247 = null;
		Token RPAREN249 = null;
		ParserRuleReturnScope expression246 = null;
		ParserRuleReturnScope expression248 = null;

		CommonTree LPAREN245_tree = null;
		CommonTree COMMA247_tree = null;
		CommonTree RPAREN249_tree = null;
		RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(
				adaptor, "token RPAREN");
		RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(
				adaptor, "token COMMA");
		RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(
				adaptor, "token LPAREN");
		RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(
				adaptor, "rule expression");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:803:3: ( LPAREN
			// expression ( COMMA expression )* RPAREN -> ( expression )* )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:804:3: LPAREN
			// expression ( COMMA expression )* RPAREN
			{
				LPAREN245 = (Token) match(input, LPAREN,
						FOLLOW_LPAREN_in_expressions4555);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_LPAREN.add(LPAREN245);

				pushFollow(FOLLOW_expression_in_expressions4557);
				expression246 = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_expression.add(expression246.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:804:21: ( COMMA
				// expression )*
				loop68: while (true) {
					int alt68 = 2;
					int LA68_0 = input.LA(1);
					if ((LA68_0 == COMMA)) {
						alt68 = 1;
					}

					switch (alt68) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:804:22: COMMA
					// expression
					{
						COMMA247 = (Token) match(input, COMMA,
								FOLLOW_COMMA_in_expressions4560);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_COMMA.add(COMMA247);

						pushFollow(FOLLOW_expression_in_expressions4562);
						expression248 = expression();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_expression.add(expression248.getTree());
					}
						break;

					default:
						break loop68;
					}
				}

				RPAREN249 = (Token) match(input, RPAREN,
						FOLLOW_RPAREN_in_expressions4566);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_RPAREN.add(RPAREN249);

				// AST REWRITE
				// elements: expression
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 805:5: -> ( expression )*
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:805:8: (
						// expression )*
						while (stream_expression.hasNext()) {
							adaptor.addChild(root_0,
									stream_expression.nextTree());
						}
						stream_expression.reset();

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "expressions"

	public static class precedenceOrOperator_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceOrOperator"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:808:1: precedenceOrOperator :
	// KW_OR ;
	public final TrcParser.precedenceOrOperator_return precedenceOrOperator()
			throws RecognitionException {
		TrcParser.precedenceOrOperator_return retval = new TrcParser.precedenceOrOperator_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_OR250 = null;

		CommonTree KW_OR250_tree = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:809:3: ( KW_OR )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:810:3: KW_OR
			{
				root_0 = (CommonTree) adaptor.nil();

				KW_OR250 = (Token) match(input, KW_OR,
						FOLLOW_KW_OR_in_precedenceOrOperator4590);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					KW_OR250_tree = (CommonTree) adaptor.create(KW_OR250);
					adaptor.addChild(root_0, KW_OR250_tree);
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceOrOperator"

	public static class precedenceAndOperator_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceAndOperator"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:813:1: precedenceAndOperator :
	// KW_AND ;
	public final TrcParser.precedenceAndOperator_return precedenceAndOperator()
			throws RecognitionException {
		TrcParser.precedenceAndOperator_return retval = new TrcParser.precedenceAndOperator_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_AND251 = null;

		CommonTree KW_AND251_tree = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:814:3: ( KW_AND )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:815:3: KW_AND
			{
				root_0 = (CommonTree) adaptor.nil();

				KW_AND251 = (Token) match(input, KW_AND,
						FOLLOW_KW_AND_in_precedenceAndOperator4605);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					KW_AND251_tree = (CommonTree) adaptor.create(KW_AND251);
					adaptor.addChild(root_0, KW_AND251_tree);
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceAndOperator"

	public static class precedenceNotOperator_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceNotOperator"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:818:1: precedenceNotOperator :
	// KW_NOT ;
	public final TrcParser.precedenceNotOperator_return precedenceNotOperator()
			throws RecognitionException {
		TrcParser.precedenceNotOperator_return retval = new TrcParser.precedenceNotOperator_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_NOT252 = null;

		CommonTree KW_NOT252_tree = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:819:3: ( KW_NOT )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:820:3: KW_NOT
			{
				root_0 = (CommonTree) adaptor.nil();

				KW_NOT252 = (Token) match(input, KW_NOT,
						FOLLOW_KW_NOT_in_precedenceNotOperator4620);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					KW_NOT252_tree = (CommonTree) adaptor.create(KW_NOT252);
					adaptor.addChild(root_0, KW_NOT252_tree);
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceNotOperator"

	public static class precedenceInNotInOperator_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceInNotInOperator"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:823:1:
	// precedenceInNotInOperator : ( KW_IN | KW_NOT KW_IN -> NOTIN );
	public final TrcParser.precedenceInNotInOperator_return precedenceInNotInOperator()
			throws RecognitionException {
		TrcParser.precedenceInNotInOperator_return retval = new TrcParser.precedenceInNotInOperator_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_IN253 = null;
		Token KW_NOT254 = null;
		Token KW_IN255 = null;

		CommonTree KW_IN253_tree = null;
		CommonTree KW_NOT254_tree = null;
		CommonTree KW_IN255_tree = null;
		RewriteRuleTokenStream stream_KW_IN = new RewriteRuleTokenStream(
				adaptor, "token KW_IN");
		RewriteRuleTokenStream stream_KW_NOT = new RewriteRuleTokenStream(
				adaptor, "token KW_NOT");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:824:3: ( KW_IN |
			// KW_NOT KW_IN -> NOTIN )
			int alt69 = 2;
			int LA69_0 = input.LA(1);
			if ((LA69_0 == KW_IN)) {
				alt69 = 1;
			} else if ((LA69_0 == KW_NOT)) {
				alt69 = 2;
			}

			else {
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 69, 0,
						input);
				throw nvae;
			}

			switch (alt69) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:825:3: KW_IN
			{
				root_0 = (CommonTree) adaptor.nil();

				KW_IN253 = (Token) match(input, KW_IN,
						FOLLOW_KW_IN_in_precedenceInNotInOperator4635);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					KW_IN253_tree = (CommonTree) adaptor.create(KW_IN253);
					adaptor.addChild(root_0, KW_IN253_tree);
				}

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:826:5: KW_NOT KW_IN
			{
				KW_NOT254 = (Token) match(input, KW_NOT,
						FOLLOW_KW_NOT_in_precedenceInNotInOperator4641);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_NOT.add(KW_NOT254);

				KW_IN255 = (Token) match(input, KW_IN,
						FOLLOW_KW_IN_in_precedenceInNotInOperator4643);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_IN.add(KW_IN255);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 827:5: -> NOTIN
					{
						adaptor.addChild(root_0,
								(CommonTree) adaptor.create(NOTIN, "NOTIN"));
					}

					retval.tree = root_0;
				}

			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceInNotInOperator"

	public static class precedenceEqualOperator_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceEqualOperator"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:830:1: precedenceEqualOperator
	// : ( EQUAL | NOTEQUAL | LESSTHANOREQUALTO | LESSTHAN |
	// GREATERTHANOREQUALTO | GREATERTHAN | KW_LIKE | KW_RLIKE | KW_REGEXP |
	// KW_NOT KW_LIKE -> NOTLIKE | KW_NOT KW_RLIKE -> NOTRLIKE | KW_NOT
	// KW_REGEXP -> NOTREGEXP );
	public final TrcParser.precedenceEqualOperator_return precedenceEqualOperator()
			throws RecognitionException {
		TrcParser.precedenceEqualOperator_return retval = new TrcParser.precedenceEqualOperator_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token EQUAL256 = null;
		Token NOTEQUAL257 = null;
		Token LESSTHANOREQUALTO258 = null;
		Token LESSTHAN259 = null;
		Token GREATERTHANOREQUALTO260 = null;
		Token GREATERTHAN261 = null;
		Token KW_LIKE262 = null;
		Token KW_RLIKE263 = null;
		Token KW_REGEXP264 = null;
		Token KW_NOT265 = null;
		Token KW_LIKE266 = null;
		Token KW_NOT267 = null;
		Token KW_RLIKE268 = null;
		Token KW_NOT269 = null;
		Token KW_REGEXP270 = null;

		CommonTree EQUAL256_tree = null;
		CommonTree NOTEQUAL257_tree = null;
		CommonTree LESSTHANOREQUALTO258_tree = null;
		CommonTree LESSTHAN259_tree = null;
		CommonTree GREATERTHANOREQUALTO260_tree = null;
		CommonTree GREATERTHAN261_tree = null;
		CommonTree KW_LIKE262_tree = null;
		CommonTree KW_RLIKE263_tree = null;
		CommonTree KW_REGEXP264_tree = null;
		CommonTree KW_NOT265_tree = null;
		CommonTree KW_LIKE266_tree = null;
		CommonTree KW_NOT267_tree = null;
		CommonTree KW_RLIKE268_tree = null;
		CommonTree KW_NOT269_tree = null;
		CommonTree KW_REGEXP270_tree = null;
		RewriteRuleTokenStream stream_KW_LIKE = new RewriteRuleTokenStream(
				adaptor, "token KW_LIKE");
		RewriteRuleTokenStream stream_KW_REGEXP = new RewriteRuleTokenStream(
				adaptor, "token KW_REGEXP");
		RewriteRuleTokenStream stream_KW_RLIKE = new RewriteRuleTokenStream(
				adaptor, "token KW_RLIKE");
		RewriteRuleTokenStream stream_KW_NOT = new RewriteRuleTokenStream(
				adaptor, "token KW_NOT");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:831:3: ( EQUAL |
			// NOTEQUAL | LESSTHANOREQUALTO | LESSTHAN | GREATERTHANOREQUALTO |
			// GREATERTHAN | KW_LIKE | KW_RLIKE | KW_REGEXP | KW_NOT KW_LIKE ->
			// NOTLIKE | KW_NOT KW_RLIKE -> NOTRLIKE | KW_NOT KW_REGEXP ->
			// NOTREGEXP )
			int alt70 = 12;
			switch (input.LA(1)) {
			case EQUAL: {
				alt70 = 1;
			}
				break;
			case NOTEQUAL: {
				alt70 = 2;
			}
				break;
			case LESSTHANOREQUALTO: {
				alt70 = 3;
			}
				break;
			case LESSTHAN: {
				alt70 = 4;
			}
				break;
			case GREATERTHANOREQUALTO: {
				alt70 = 5;
			}
				break;
			case GREATERTHAN: {
				alt70 = 6;
			}
				break;
			case KW_LIKE: {
				alt70 = 7;
			}
				break;
			case KW_RLIKE: {
				alt70 = 8;
			}
				break;
			case KW_REGEXP: {
				alt70 = 9;
			}
				break;
			case KW_NOT: {
				switch (input.LA(2)) {
				case KW_LIKE: {
					alt70 = 10;
				}
					break;
				case KW_RLIKE: {
					alt70 = 11;
				}
					break;
				case KW_REGEXP: {
					alt70 = 12;
				}
					break;
				default:
					if (state.backtracking > 0) {
						state.failed = true;
						return retval;
					}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae = new NoViableAltException(
								"", 70, 10, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
			}
				break;
			default:
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 70, 0,
						input);
				throw nvae;
			}
			switch (alt70) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:832:3: EQUAL
			{
				root_0 = (CommonTree) adaptor.nil();

				EQUAL256 = (Token) match(input, EQUAL,
						FOLLOW_EQUAL_in_precedenceEqualOperator4666);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					EQUAL256_tree = (CommonTree) adaptor.create(EQUAL256);
					adaptor.addChild(root_0, EQUAL256_tree);
				}

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:833:5: NOTEQUAL
			{
				root_0 = (CommonTree) adaptor.nil();

				NOTEQUAL257 = (Token) match(input, NOTEQUAL,
						FOLLOW_NOTEQUAL_in_precedenceEqualOperator4672);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					NOTEQUAL257_tree = (CommonTree) adaptor.create(NOTEQUAL257);
					adaptor.addChild(root_0, NOTEQUAL257_tree);
				}

			}
				break;
			case 3:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:834:5:
			// LESSTHANOREQUALTO
			{
				root_0 = (CommonTree) adaptor.nil();

				LESSTHANOREQUALTO258 = (Token) match(input, LESSTHANOREQUALTO,
						FOLLOW_LESSTHANOREQUALTO_in_precedenceEqualOperator4678);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					LESSTHANOREQUALTO258_tree = (CommonTree) adaptor
							.create(LESSTHANOREQUALTO258);
					adaptor.addChild(root_0, LESSTHANOREQUALTO258_tree);
				}

			}
				break;
			case 4:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:835:5: LESSTHAN
			{
				root_0 = (CommonTree) adaptor.nil();

				LESSTHAN259 = (Token) match(input, LESSTHAN,
						FOLLOW_LESSTHAN_in_precedenceEqualOperator4684);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					LESSTHAN259_tree = (CommonTree) adaptor.create(LESSTHAN259);
					adaptor.addChild(root_0, LESSTHAN259_tree);
				}

			}
				break;
			case 5:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:836:5:
			// GREATERTHANOREQUALTO
			{
				root_0 = (CommonTree) adaptor.nil();

				GREATERTHANOREQUALTO260 = (Token) match(input,
						GREATERTHANOREQUALTO,
						FOLLOW_GREATERTHANOREQUALTO_in_precedenceEqualOperator4690);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					GREATERTHANOREQUALTO260_tree = (CommonTree) adaptor
							.create(GREATERTHANOREQUALTO260);
					adaptor.addChild(root_0, GREATERTHANOREQUALTO260_tree);
				}

			}
				break;
			case 6:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:837:5: GREATERTHAN
			{
				root_0 = (CommonTree) adaptor.nil();

				GREATERTHAN261 = (Token) match(input, GREATERTHAN,
						FOLLOW_GREATERTHAN_in_precedenceEqualOperator4696);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					GREATERTHAN261_tree = (CommonTree) adaptor
							.create(GREATERTHAN261);
					adaptor.addChild(root_0, GREATERTHAN261_tree);
				}

			}
				break;
			case 7:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:838:5: KW_LIKE
			{
				root_0 = (CommonTree) adaptor.nil();

				KW_LIKE262 = (Token) match(input, KW_LIKE,
						FOLLOW_KW_LIKE_in_precedenceEqualOperator4702);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					KW_LIKE262_tree = (CommonTree) adaptor.create(KW_LIKE262);
					adaptor.addChild(root_0, KW_LIKE262_tree);
				}

			}
				break;
			case 8:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:839:5: KW_RLIKE
			{
				root_0 = (CommonTree) adaptor.nil();

				KW_RLIKE263 = (Token) match(input, KW_RLIKE,
						FOLLOW_KW_RLIKE_in_precedenceEqualOperator4708);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					KW_RLIKE263_tree = (CommonTree) adaptor.create(KW_RLIKE263);
					adaptor.addChild(root_0, KW_RLIKE263_tree);
				}

			}
				break;
			case 9:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:840:5: KW_REGEXP
			{
				root_0 = (CommonTree) adaptor.nil();

				KW_REGEXP264 = (Token) match(input, KW_REGEXP,
						FOLLOW_KW_REGEXP_in_precedenceEqualOperator4714);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					KW_REGEXP264_tree = (CommonTree) adaptor
							.create(KW_REGEXP264);
					adaptor.addChild(root_0, KW_REGEXP264_tree);
				}

			}
				break;
			case 10:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:841:5: KW_NOT KW_LIKE
			{
				KW_NOT265 = (Token) match(input, KW_NOT,
						FOLLOW_KW_NOT_in_precedenceEqualOperator4720);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_NOT.add(KW_NOT265);

				KW_LIKE266 = (Token) match(input, KW_LIKE,
						FOLLOW_KW_LIKE_in_precedenceEqualOperator4722);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_LIKE.add(KW_LIKE266);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 842:5: -> NOTLIKE
					{
						adaptor.addChild(root_0,
								(CommonTree) adaptor.create(NOTLIKE, "NOTLIKE"));
					}

					retval.tree = root_0;
				}

			}
				break;
			case 11:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:843:5: KW_NOT KW_RLIKE
			{
				KW_NOT267 = (Token) match(input, KW_NOT,
						FOLLOW_KW_NOT_in_precedenceEqualOperator4736);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_NOT.add(KW_NOT267);

				KW_RLIKE268 = (Token) match(input, KW_RLIKE,
						FOLLOW_KW_RLIKE_in_precedenceEqualOperator4738);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_RLIKE.add(KW_RLIKE268);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 844:5: -> NOTRLIKE
					{
						adaptor.addChild(root_0, (CommonTree) adaptor.create(
								NOTRLIKE, "NOTRLIKE"));
					}

					retval.tree = root_0;
				}

			}
				break;
			case 12:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:845:5: KW_NOT
			// KW_REGEXP
			{
				KW_NOT269 = (Token) match(input, KW_NOT,
						FOLLOW_KW_NOT_in_precedenceEqualOperator4752);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_NOT.add(KW_NOT269);

				KW_REGEXP270 = (Token) match(input, KW_REGEXP,
						FOLLOW_KW_REGEXP_in_precedenceEqualOperator4754);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_REGEXP.add(KW_REGEXP270);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 846:5: -> NOTREGEXP
					{
						adaptor.addChild(root_0, (CommonTree) adaptor.create(
								NOTREGEXP, "NOTREGEXP"));
					}

					retval.tree = root_0;
				}

			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceEqualOperator"

	public static class precedenceBitwiseOrOperator_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceBitwiseOrOperator"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:849:1:
	// precedenceBitwiseOrOperator : BITWISEOR ;
	public final TrcParser.precedenceBitwiseOrOperator_return precedenceBitwiseOrOperator()
			throws RecognitionException {
		TrcParser.precedenceBitwiseOrOperator_return retval = new TrcParser.precedenceBitwiseOrOperator_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token BITWISEOR271 = null;

		CommonTree BITWISEOR271_tree = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:850:3: ( BITWISEOR )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:851:3: BITWISEOR
			{
				root_0 = (CommonTree) adaptor.nil();

				BITWISEOR271 = (Token) match(input, BITWISEOR,
						FOLLOW_BITWISEOR_in_precedenceBitwiseOrOperator4777);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					BITWISEOR271_tree = (CommonTree) adaptor
							.create(BITWISEOR271);
					adaptor.addChild(root_0, BITWISEOR271_tree);
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceBitwiseOrOperator"

	public static class precedenceAmpersandOperator_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceAmpersandOperator"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:854:1:
	// precedenceAmpersandOperator : AMPERSAND ;
	public final TrcParser.precedenceAmpersandOperator_return precedenceAmpersandOperator()
			throws RecognitionException {
		TrcParser.precedenceAmpersandOperator_return retval = new TrcParser.precedenceAmpersandOperator_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token AMPERSAND272 = null;

		CommonTree AMPERSAND272_tree = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:855:3: ( AMPERSAND )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:856:3: AMPERSAND
			{
				root_0 = (CommonTree) adaptor.nil();

				AMPERSAND272 = (Token) match(input, AMPERSAND,
						FOLLOW_AMPERSAND_in_precedenceAmpersandOperator4792);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					AMPERSAND272_tree = (CommonTree) adaptor
							.create(AMPERSAND272);
					adaptor.addChild(root_0, AMPERSAND272_tree);
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceAmpersandOperator"

	public static class precedencePlusOperator_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedencePlusOperator"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:859:1: precedencePlusOperator
	// : ( PLUS | MINUS );
	public final TrcParser.precedencePlusOperator_return precedencePlusOperator()
			throws RecognitionException {
		TrcParser.precedencePlusOperator_return retval = new TrcParser.precedencePlusOperator_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token set273 = null;

		CommonTree set273_tree = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:860:3: ( PLUS | MINUS
			// )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:
			{
				root_0 = (CommonTree) adaptor.nil();

				set273 = input.LT(1);
				if (input.LA(1) == MINUS || input.LA(1) == PLUS) {
					input.consume();
					if (state.backtracking == 0)
						adaptor.addChild(root_0,
								(CommonTree) adaptor.create(set273));
					state.errorRecovery = false;
					state.failed = false;
				} else {
					if (state.backtracking > 0) {
						state.failed = true;
						return retval;
					}
					MismatchedSetException mse = new MismatchedSetException(
							null, input);
					throw mse;
				}
			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedencePlusOperator"

	public static class precedenceStarOperator_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceStarOperator"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:865:1: precedenceStarOperator
	// : ( STAR | DIVIDE | MOD | DIV );
	public final TrcParser.precedenceStarOperator_return precedenceStarOperator()
			throws RecognitionException {
		TrcParser.precedenceStarOperator_return retval = new TrcParser.precedenceStarOperator_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token set274 = null;

		CommonTree set274_tree = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:866:3: ( STAR | DIVIDE
			// | MOD | DIV )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:
			{
				root_0 = (CommonTree) adaptor.nil();

				set274 = input.LT(1);
				if ((input.LA(1) >= DIV && input.LA(1) <= DIVIDE)
						|| input.LA(1) == MOD || input.LA(1) == STAR) {
					input.consume();
					if (state.backtracking == 0)
						adaptor.addChild(root_0,
								(CommonTree) adaptor.create(set274));
					state.errorRecovery = false;
					state.failed = false;
				} else {
					if (state.backtracking > 0) {
						state.failed = true;
						return retval;
					}
					MismatchedSetException mse = new MismatchedSetException(
							null, input);
					throw mse;
				}
			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceStarOperator"

	public static class precedenceBitwiseXorOperator_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceBitwiseXorOperator"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:873:1:
	// precedenceBitwiseXorOperator : BITWISEXOR ;
	public final TrcParser.precedenceBitwiseXorOperator_return precedenceBitwiseXorOperator()
			throws RecognitionException {
		TrcParser.precedenceBitwiseXorOperator_return retval = new TrcParser.precedenceBitwiseXorOperator_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token BITWISEXOR275 = null;

		CommonTree BITWISEXOR275_tree = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:874:3: ( BITWISEXOR )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:875:3: BITWISEXOR
			{
				root_0 = (CommonTree) adaptor.nil();

				BITWISEXOR275 = (Token) match(input, BITWISEXOR,
						FOLLOW_BITWISEXOR_in_precedenceBitwiseXorOperator4861);
				if (state.failed)
					return retval;
				if (state.backtracking == 0) {
					BITWISEXOR275_tree = (CommonTree) adaptor
							.create(BITWISEXOR275);
					adaptor.addChild(root_0, BITWISEXOR275_tree);
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceBitwiseXorOperator"

	public static class precedenceUnaryOperator_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "precedenceUnaryOperator"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:878:1: precedenceUnaryOperator
	// : ( PLUS | MINUS | TILDE );
	public final TrcParser.precedenceUnaryOperator_return precedenceUnaryOperator()
			throws RecognitionException {
		TrcParser.precedenceUnaryOperator_return retval = new TrcParser.precedenceUnaryOperator_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token set276 = null;

		CommonTree set276_tree = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:879:3: ( PLUS | MINUS
			// | TILDE )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:
			{
				root_0 = (CommonTree) adaptor.nil();

				set276 = input.LT(1);
				if (input.LA(1) == MINUS || input.LA(1) == PLUS
						|| input.LA(1) == TILDE) {
					input.consume();
					if (state.backtracking == 0)
						adaptor.addChild(root_0,
								(CommonTree) adaptor.create(set276));
					state.errorRecovery = false;
					state.failed = false;
				} else {
					if (state.backtracking > 0) {
						state.failed = true;
						return retval;
					}
					MismatchedSetException mse = new MismatchedSetException(
							null, input);
					throw mse;
				}
			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "precedenceUnaryOperator"

	public static class nullCondition_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "nullCondition"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:885:1: nullCondition : (
	// KW_NULL -> ^( TOK_ISNULL ) | KW_NOT KW_NULL -> ^( TOK_ISNOTNULL ) );
	public final TrcParser.nullCondition_return nullCondition()
			throws RecognitionException {
		TrcParser.nullCondition_return retval = new TrcParser.nullCondition_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_NULL277 = null;
		Token KW_NOT278 = null;
		Token KW_NULL279 = null;

		CommonTree KW_NULL277_tree = null;
		CommonTree KW_NOT278_tree = null;
		CommonTree KW_NULL279_tree = null;
		RewriteRuleTokenStream stream_KW_NULL = new RewriteRuleTokenStream(
				adaptor, "token KW_NULL");
		RewriteRuleTokenStream stream_KW_NOT = new RewriteRuleTokenStream(
				adaptor, "token KW_NOT");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:886:3: ( KW_NULL -> ^(
			// TOK_ISNULL ) | KW_NOT KW_NULL -> ^( TOK_ISNOTNULL ) )
			int alt71 = 2;
			int LA71_0 = input.LA(1);
			if ((LA71_0 == KW_NULL)) {
				alt71 = 1;
			} else if ((LA71_0 == KW_NOT)) {
				alt71 = 2;
			}

			else {
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 71, 0,
						input);
				throw nvae;
			}

			switch (alt71) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:887:3: KW_NULL
			{
				KW_NULL277 = (Token) match(input, KW_NULL,
						FOLLOW_KW_NULL_in_nullCondition4903);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_NULL.add(KW_NULL277);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 888:5: -> ^( TOK_ISNULL )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:889:7: ^(
						// TOK_ISNULL )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_ISNULL,
											"TOK_ISNULL"), root_1);
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:890:5: KW_NOT KW_NULL
			{
				KW_NOT278 = (Token) match(input, KW_NOT,
						FOLLOW_KW_NOT_in_nullCondition4925);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_NOT.add(KW_NOT278);

				KW_NULL279 = (Token) match(input, KW_NULL,
						FOLLOW_KW_NULL_in_nullCondition4927);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_NULL.add(KW_NULL279);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 891:5: -> ^( TOK_ISNOTNULL )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:892:7: ^(
						// TOK_ISNOTNULL )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_ISNOTNULL,
											"TOK_ISNOTNULL"), root_1);
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "nullCondition"

	public static class fromClause_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "fromClause"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:895:1: fromClause : KW_FROM
	// joinSource -> ^( TOK_FROM joinSource ) ;
	public final TrcParser.fromClause_return fromClause()
			throws RecognitionException {
		TrcParser.fromClause_return retval = new TrcParser.fromClause_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_FROM280 = null;
		ParserRuleReturnScope joinSource281 = null;

		CommonTree KW_FROM280_tree = null;
		RewriteRuleTokenStream stream_KW_FROM = new RewriteRuleTokenStream(
				adaptor, "token KW_FROM");
		RewriteRuleSubtreeStream stream_joinSource = new RewriteRuleSubtreeStream(
				adaptor, "rule joinSource");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:896:3: ( KW_FROM
			// joinSource -> ^( TOK_FROM joinSource ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:897:3: KW_FROM
			// joinSource
			{
				KW_FROM280 = (Token) match(input, KW_FROM,
						FOLLOW_KW_FROM_in_fromClause4958);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_FROM.add(KW_FROM280);

				pushFollow(FOLLOW_joinSource_in_fromClause4960);
				joinSource281 = joinSource();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_joinSource.add(joinSource281.getTree());
				// AST REWRITE
				// elements: joinSource
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 898:5: -> ^( TOK_FROM joinSource )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:899:7: ^(
						// TOK_FROM joinSource )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_FROM,
											"TOK_FROM"), root_1);
							adaptor.addChild(root_1,
									stream_joinSource.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "fromClause"

	public static class unionOperator_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "unionOperator"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:902:1: unionOperator :
	// KW_UNION KW_ALL -> ^( TOK_UNION ) ;
	public final TrcParser.unionOperator_return unionOperator()
			throws RecognitionException {
		TrcParser.unionOperator_return retval = new TrcParser.unionOperator_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_UNION282 = null;
		Token KW_ALL283 = null;

		CommonTree KW_UNION282_tree = null;
		CommonTree KW_ALL283_tree = null;
		RewriteRuleTokenStream stream_KW_ALL = new RewriteRuleTokenStream(
				adaptor, "token KW_ALL");
		RewriteRuleTokenStream stream_KW_UNION = new RewriteRuleTokenStream(
				adaptor, "token KW_UNION");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:903:3: ( KW_UNION
			// KW_ALL -> ^( TOK_UNION ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:904:3: KW_UNION KW_ALL
			{
				KW_UNION282 = (Token) match(input, KW_UNION,
						FOLLOW_KW_UNION_in_unionOperator4993);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_UNION.add(KW_UNION282);

				KW_ALL283 = (Token) match(input, KW_ALL,
						FOLLOW_KW_ALL_in_unionOperator4995);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_ALL.add(KW_ALL283);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 905:5: -> ^( TOK_UNION )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:906:7: ^(
						// TOK_UNION )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_UNION,
											"TOK_UNION"), root_1);
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "unionOperator"

	public static class joinSource_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "joinSource"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:909:1: joinSource : fromSource
	// ( joinToken ^ joinRithtSource onClause )? ;
	public final TrcParser.joinSource_return joinSource()
			throws RecognitionException {
		TrcParser.joinSource_return retval = new TrcParser.joinSource_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope fromSource284 = null;
		ParserRuleReturnScope joinToken285 = null;
		ParserRuleReturnScope joinRithtSource286 = null;
		ParserRuleReturnScope onClause287 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:910:3: ( fromSource (
			// joinToken ^ joinRithtSource onClause )? )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:911:3: fromSource (
			// joinToken ^ joinRithtSource onClause )?
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_fromSource_in_joinSource5026);
				fromSource284 = fromSource();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, fromSource284.getTree());

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:911:14: (
				// joinToken ^ joinRithtSource onClause )?
				int alt72 = 2;
				int LA72_0 = input.LA(1);
				if ((LA72_0 == KW_LEFT)) {
					alt72 = 1;
				}
				switch (alt72) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:911:15: joinToken
				// ^ joinRithtSource onClause
				{
					pushFollow(FOLLOW_joinToken_in_joinSource5029);
					joinToken285 = joinToken();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						root_0 = (CommonTree) adaptor.becomeRoot(
								joinToken285.getTree(), root_0);
					pushFollow(FOLLOW_joinRithtSource_in_joinSource5032);
					joinRithtSource286 = joinRithtSource();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						adaptor.addChild(root_0, joinRithtSource286.getTree());

					pushFollow(FOLLOW_onClause_in_joinSource5034);
					onClause287 = onClause();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						adaptor.addChild(root_0, onClause287.getTree());

				}
					break;

				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "joinSource"

	public static class onClause_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "onClause"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:917:1: onClause : KW_ON
	// expression -> ^( TOK_ON expression ) ;
	public final TrcParser.onClause_return onClause()
			throws RecognitionException {
		TrcParser.onClause_return retval = new TrcParser.onClause_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_ON288 = null;
		ParserRuleReturnScope expression289 = null;

		CommonTree KW_ON288_tree = null;
		RewriteRuleTokenStream stream_KW_ON = new RewriteRuleTokenStream(
				adaptor, "token KW_ON");
		RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(
				adaptor, "rule expression");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:918:3: ( KW_ON
			// expression -> ^( TOK_ON expression ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:919:3: KW_ON
			// expression
			{
				KW_ON288 = (Token) match(input, KW_ON,
						FOLLOW_KW_ON_in_onClause5060);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_ON.add(KW_ON288);

				pushFollow(FOLLOW_expression_in_onClause5062);
				expression289 = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_expression.add(expression289.getTree());
				// AST REWRITE
				// elements: expression
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 920:5: -> ^( TOK_ON expression )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:921:7: ^(
						// TOK_ON expression )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_ON,
											"TOK_ON"), root_1);
							adaptor.addChild(root_1,
									stream_expression.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "onClause"

	public static class joinRithtSource_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "joinRithtSource"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:924:1: joinRithtSource :
	// tableSource ( COMMA tableSource )* -> ( tableSource )+ ;
	public final TrcParser.joinRithtSource_return joinRithtSource()
			throws RecognitionException {
		TrcParser.joinRithtSource_return retval = new TrcParser.joinRithtSource_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token COMMA291 = null;
		ParserRuleReturnScope tableSource290 = null;
		ParserRuleReturnScope tableSource292 = null;

		CommonTree COMMA291_tree = null;
		RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(
				adaptor, "token COMMA");
		RewriteRuleSubtreeStream stream_tableSource = new RewriteRuleSubtreeStream(
				adaptor, "rule tableSource");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:925:3: ( tableSource (
			// COMMA tableSource )* -> ( tableSource )+ )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:926:3: tableSource (
			// COMMA tableSource )*
			{
				pushFollow(FOLLOW_tableSource_in_joinRithtSource5095);
				tableSource290 = tableSource();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_tableSource.add(tableSource290.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:926:15: ( COMMA
				// tableSource )*
				loop73: while (true) {
					int alt73 = 2;
					int LA73_0 = input.LA(1);
					if ((LA73_0 == COMMA)) {
						alt73 = 1;
					}

					switch (alt73) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:926:16: COMMA
					// tableSource
					{
						COMMA291 = (Token) match(input, COMMA,
								FOLLOW_COMMA_in_joinRithtSource5098);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_COMMA.add(COMMA291);

						pushFollow(FOLLOW_tableSource_in_joinRithtSource5100);
						tableSource292 = tableSource();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_tableSource.add(tableSource292.getTree());
					}
						break;

					default:
						break loop73;
					}
				}

				// AST REWRITE
				// elements: tableSource
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 927:5: -> ( tableSource )+
					{
						if (!(stream_tableSource.hasNext())) {
							throw new RewriteEarlyExitException();
						}
						while (stream_tableSource.hasNext()) {
							adaptor.addChild(root_0,
									stream_tableSource.nextTree());
						}
						stream_tableSource.reset();

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "joinRithtSource"

	public static class joinToken_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "joinToken"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:930:1: joinToken : KW_LEFT
	// KW_JOIN -> TOK_LEFTOUTERJOIN ;
	public final TrcParser.joinToken_return joinToken()
			throws RecognitionException {
		TrcParser.joinToken_return retval = new TrcParser.joinToken_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_LEFT293 = null;
		Token KW_JOIN294 = null;

		CommonTree KW_LEFT293_tree = null;
		CommonTree KW_JOIN294_tree = null;
		RewriteRuleTokenStream stream_KW_JOIN = new RewriteRuleTokenStream(
				adaptor, "token KW_JOIN");
		RewriteRuleTokenStream stream_KW_LEFT = new RewriteRuleTokenStream(
				adaptor, "token KW_LEFT");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:931:3: ( KW_LEFT
			// KW_JOIN -> TOK_LEFTOUTERJOIN )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:932:3: KW_LEFT KW_JOIN
			{
				KW_LEFT293 = (Token) match(input, KW_LEFT,
						FOLLOW_KW_LEFT_in_joinToken5126);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_LEFT.add(KW_LEFT293);

				KW_JOIN294 = (Token) match(input, KW_JOIN,
						FOLLOW_KW_JOIN_in_joinToken5128);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_JOIN.add(KW_JOIN294);

				// AST REWRITE
				// elements:
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 933:5: -> TOK_LEFTOUTERJOIN
					{
						adaptor.addChild(root_0, (CommonTree) adaptor.create(
								TOK_LEFTOUTERJOIN, "TOK_LEFTOUTERJOIN"));
					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "joinToken"

	public static class fromSource_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "fromSource"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:936:1: fromSource : (
	// tableSource | subQuerySource );
	public final TrcParser.fromSource_return fromSource()
			throws RecognitionException {
		TrcParser.fromSource_return retval = new TrcParser.fromSource_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope tableSource295 = null;
		ParserRuleReturnScope subQuerySource296 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:937:3: ( tableSource |
			// subQuerySource )
			int alt74 = 2;
			int LA74_0 = input.LA(1);
			if ((LA74_0 == Identifier || LA74_0 == KW_INNERTABLE)) {
				alt74 = 1;
			} else if ((LA74_0 == LPAREN)) {
				alt74 = 2;
			}

			else {
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 74, 0,
						input);
				throw nvae;
			}

			switch (alt74) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:938:3: tableSource
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_tableSource_in_fromSource5151);
				tableSource295 = tableSource();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, tableSource295.getTree());

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:939:5: subQuerySource
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_subQuerySource_in_fromSource5157);
				subQuerySource296 = subQuerySource();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, subQuerySource296.getTree());

			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "fromSource"

	public static class subQuerySource_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "subQuerySource"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:942:1: subQuerySource : LPAREN
	// subQueryStatement RPAREN Identifier -> ^( TOK_SUBQUERY subQueryStatement
	// Identifier ) ;
	public final TrcParser.subQuerySource_return subQuerySource()
			throws RecognitionException {
		TrcParser.subQuerySource_return retval = new TrcParser.subQuerySource_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token LPAREN297 = null;
		Token RPAREN299 = null;
		Token Identifier300 = null;
		ParserRuleReturnScope subQueryStatement298 = null;

		CommonTree LPAREN297_tree = null;
		CommonTree RPAREN299_tree = null;
		CommonTree Identifier300_tree = null;
		RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(
				adaptor, "token RPAREN");
		RewriteRuleTokenStream stream_Identifier = new RewriteRuleTokenStream(
				adaptor, "token Identifier");
		RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(
				adaptor, "token LPAREN");
		RewriteRuleSubtreeStream stream_subQueryStatement = new RewriteRuleSubtreeStream(
				adaptor, "rule subQueryStatement");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:943:3: ( LPAREN
			// subQueryStatement RPAREN Identifier -> ^( TOK_SUBQUERY
			// subQueryStatement Identifier ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:944:3: LPAREN
			// subQueryStatement RPAREN Identifier
			{
				LPAREN297 = (Token) match(input, LPAREN,
						FOLLOW_LPAREN_in_subQuerySource5172);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_LPAREN.add(LPAREN297);

				pushFollow(FOLLOW_subQueryStatement_in_subQuerySource5174);
				subQueryStatement298 = subQueryStatement();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_subQueryStatement
							.add(subQueryStatement298.getTree());
				RPAREN299 = (Token) match(input, RPAREN,
						FOLLOW_RPAREN_in_subQuerySource5176);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_RPAREN.add(RPAREN299);

				Identifier300 = (Token) match(input, Identifier,
						FOLLOW_Identifier_in_subQuerySource5178);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_Identifier.add(Identifier300);

				// AST REWRITE
				// elements: subQueryStatement, Identifier
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 945:5: -> ^( TOK_SUBQUERY subQueryStatement Identifier )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:946:7: ^(
						// TOK_SUBQUERY subQueryStatement Identifier )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_SUBQUERY,
											"TOK_SUBQUERY"), root_1);
							adaptor.addChild(root_1,
									stream_subQueryStatement.nextTree());
							adaptor.addChild(root_1,
									stream_Identifier.nextNode());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "subQuerySource"

	public static class subQueryStatement_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "subQueryStatement"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:949:1: subQueryStatement : (
	// selectFromWholeClause ( unionOperator selectFromWholeClause )+ -> ^(
	// TOK_UNION ( ^( TOK_SUBQUERY selectFromWholeClause ) )+ ) |
	// selectFromWholeClause );
	public final TrcParser.subQueryStatement_return subQueryStatement()
			throws RecognitionException {
		TrcParser.subQueryStatement_return retval = new TrcParser.subQueryStatement_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope selectFromWholeClause301 = null;
		ParserRuleReturnScope unionOperator302 = null;
		ParserRuleReturnScope selectFromWholeClause303 = null;
		ParserRuleReturnScope selectFromWholeClause304 = null;

		RewriteRuleSubtreeStream stream_unionOperator = new RewriteRuleSubtreeStream(
				adaptor, "rule unionOperator");
		RewriteRuleSubtreeStream stream_selectFromWholeClause = new RewriteRuleSubtreeStream(
				adaptor, "rule selectFromWholeClause");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:950:3: (
			// selectFromWholeClause ( unionOperator selectFromWholeClause )+ ->
			// ^( TOK_UNION ( ^( TOK_SUBQUERY selectFromWholeClause ) )+ ) |
			// selectFromWholeClause )
			int alt76 = 2;
			int LA76_0 = input.LA(1);
			if ((LA76_0 == KW_SELECT)) {
				int LA76_1 = input.LA(2);
				if ((synpred143_Trc())) {
					alt76 = 1;
				} else if ((true)) {
					alt76 = 2;
				}

			}

			else {
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 76, 0,
						input);
				throw nvae;
			}

			switch (alt76) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:951:3:
			// selectFromWholeClause ( unionOperator selectFromWholeClause )+
			{
				pushFollow(FOLLOW_selectFromWholeClause_in_subQueryStatement5213);
				selectFromWholeClause301 = selectFromWholeClause();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_selectFromWholeClause.add(selectFromWholeClause301
							.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:951:25: (
				// unionOperator selectFromWholeClause )+
				int cnt75 = 0;
				loop75: while (true) {
					int alt75 = 2;
					int LA75_0 = input.LA(1);
					if ((LA75_0 == KW_UNION)) {
						alt75 = 1;
					}

					switch (alt75) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:951:26:
					// unionOperator selectFromWholeClause
					{
						pushFollow(FOLLOW_unionOperator_in_subQueryStatement5216);
						unionOperator302 = unionOperator();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_unionOperator
									.add(unionOperator302.getTree());
						pushFollow(FOLLOW_selectFromWholeClause_in_subQueryStatement5218);
						selectFromWholeClause303 = selectFromWholeClause();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_selectFromWholeClause
									.add(selectFromWholeClause303.getTree());
					}
						break;

					default:
						if (cnt75 >= 1)
							break loop75;
						if (state.backtracking > 0) {
							state.failed = true;
							return retval;
						}
						EarlyExitException eee = new EarlyExitException(75,
								input);
						throw eee;
					}
					cnt75++;
				}

				// AST REWRITE
				// elements: selectFromWholeClause
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 952:5: -> ^( TOK_UNION ( ^( TOK_SUBQUERY
					// selectFromWholeClause ) )+ )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:953:7: ^(
						// TOK_UNION ( ^( TOK_SUBQUERY selectFromWholeClause )
						// )+ )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_UNION,
											"TOK_UNION"), root_1);
							if (!(stream_selectFromWholeClause.hasNext())) {
								throw new RewriteEarlyExitException();
							}
							while (stream_selectFromWholeClause.hasNext()) {
								// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:955:9:
								// ^( TOK_SUBQUERY selectFromWholeClause )
								{
									CommonTree root_2 = (CommonTree) adaptor
											.nil();
									root_2 = (CommonTree) adaptor.becomeRoot(
											(CommonTree) adaptor.create(
													TOK_SUBQUERY,
													"TOK_SUBQUERY"), root_2);
									adaptor.addChild(root_2,
											stream_selectFromWholeClause
													.nextTree());
									adaptor.addChild(root_1, root_2);
								}

							}
							stream_selectFromWholeClause.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:957:5:
			// selectFromWholeClause
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_selectFromWholeClause_in_subQueryStatement5274);
				selectFromWholeClause304 = selectFromWholeClause();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, selectFromWholeClause304.getTree());

			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "subQueryStatement"

	public static class selectFromWholeClause_return extends
			ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "selectFromWholeClause"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:960:1: selectFromWholeClause :
	// selectClause fromClause ( whereClause )? ( groupByClause )? (
	// havingClause )? -> ^( TOK_QUERY fromClause ^( TOK_INSERT ^(
	// TOK_DESTINATION ^( TOK_DIR TOK_TMP_FILE ) ) selectClause ( whereClause )?
	// ( groupByClause )? ( havingClause )? ) ) ;
	public final TrcParser.selectFromWholeClause_return selectFromWholeClause()
			throws RecognitionException {
		TrcParser.selectFromWholeClause_return retval = new TrcParser.selectFromWholeClause_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope selectClause305 = null;
		ParserRuleReturnScope fromClause306 = null;
		ParserRuleReturnScope whereClause307 = null;
		ParserRuleReturnScope groupByClause308 = null;
		ParserRuleReturnScope havingClause309 = null;

		RewriteRuleSubtreeStream stream_whereClause = new RewriteRuleSubtreeStream(
				adaptor, "rule whereClause");
		RewriteRuleSubtreeStream stream_groupByClause = new RewriteRuleSubtreeStream(
				adaptor, "rule groupByClause");
		RewriteRuleSubtreeStream stream_havingClause = new RewriteRuleSubtreeStream(
				adaptor, "rule havingClause");
		RewriteRuleSubtreeStream stream_selectClause = new RewriteRuleSubtreeStream(
				adaptor, "rule selectClause");
		RewriteRuleSubtreeStream stream_fromClause = new RewriteRuleSubtreeStream(
				adaptor, "rule fromClause");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:961:3: ( selectClause
			// fromClause ( whereClause )? ( groupByClause )? ( havingClause )?
			// -> ^( TOK_QUERY fromClause ^( TOK_INSERT ^( TOK_DESTINATION ^(
			// TOK_DIR TOK_TMP_FILE ) ) selectClause ( whereClause )? (
			// groupByClause )? ( havingClause )? ) ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:962:3: selectClause
			// fromClause ( whereClause )? ( groupByClause )? ( havingClause )?
			{
				pushFollow(FOLLOW_selectClause_in_selectFromWholeClause5289);
				selectClause305 = selectClause();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_selectClause.add(selectClause305.getTree());
				pushFollow(FOLLOW_fromClause_in_selectFromWholeClause5291);
				fromClause306 = fromClause();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_fromClause.add(fromClause306.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:962:27: (
				// whereClause )?
				int alt77 = 2;
				int LA77_0 = input.LA(1);
				if ((LA77_0 == KW_WHERE)) {
					alt77 = 1;
				}
				switch (alt77) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:962:27:
				// whereClause
				{
					pushFollow(FOLLOW_whereClause_in_selectFromWholeClause5293);
					whereClause307 = whereClause();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_whereClause.add(whereClause307.getTree());
				}
					break;

				}

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:962:40: (
				// groupByClause )?
				int alt78 = 2;
				int LA78_0 = input.LA(1);
				if ((LA78_0 == KW_GROUP)) {
					alt78 = 1;
				}
				switch (alt78) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:962:40:
				// groupByClause
				{
					pushFollow(FOLLOW_groupByClause_in_selectFromWholeClause5296);
					groupByClause308 = groupByClause();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_groupByClause.add(groupByClause308.getTree());
				}
					break;

				}

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:962:55: (
				// havingClause )?
				int alt79 = 2;
				int LA79_0 = input.LA(1);
				if ((LA79_0 == KW_HAVING)) {
					alt79 = 1;
				}
				switch (alt79) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:962:55:
				// havingClause
				{
					pushFollow(FOLLOW_havingClause_in_selectFromWholeClause5299);
					havingClause309 = havingClause();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_havingClause.add(havingClause309.getTree());
				}
					break;

				}

				// AST REWRITE
				// elements: fromClause, havingClause, groupByClause,
				// whereClause, selectClause
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 963:5: -> ^( TOK_QUERY fromClause ^( TOK_INSERT ^(
					// TOK_DESTINATION ^( TOK_DIR TOK_TMP_FILE ) ) selectClause
					// ( whereClause )? ( groupByClause )? ( havingClause )? ) )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:964:7: ^(
						// TOK_QUERY fromClause ^( TOK_INSERT ^( TOK_DESTINATION
						// ^( TOK_DIR TOK_TMP_FILE ) ) selectClause (
						// whereClause )? ( groupByClause )? ( havingClause )? )
						// )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_QUERY,
											"TOK_QUERY"), root_1);
							adaptor.addChild(root_1,
									stream_fromClause.nextTree());
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:966:9:
							// ^( TOK_INSERT ^( TOK_DESTINATION ^( TOK_DIR
							// TOK_TMP_FILE ) ) selectClause ( whereClause )? (
							// groupByClause )? ( havingClause )? )
							{
								CommonTree root_2 = (CommonTree) adaptor.nil();
								root_2 = (CommonTree) adaptor.becomeRoot(
										(CommonTree) adaptor.create(TOK_INSERT,
												"TOK_INSERT"), root_2);
								// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:968:11:
								// ^( TOK_DESTINATION ^( TOK_DIR TOK_TMP_FILE )
								// )
								{
									CommonTree root_3 = (CommonTree) adaptor
											.nil();
									root_3 = (CommonTree) adaptor.becomeRoot(
											(CommonTree) adaptor.create(
													TOK_DESTINATION,
													"TOK_DESTINATION"), root_3);
									// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:970:13:
									// ^( TOK_DIR TOK_TMP_FILE )
									{
										CommonTree root_4 = (CommonTree) adaptor
												.nil();
										root_4 = (CommonTree) adaptor
												.becomeRoot(
														(CommonTree) adaptor
																.create(TOK_DIR,
																		"TOK_DIR"),
														root_4);
										adaptor.addChild(root_4,
												(CommonTree) adaptor.create(
														TOK_TMP_FILE,
														"TOK_TMP_FILE"));
										adaptor.addChild(root_3, root_4);
									}

									adaptor.addChild(root_2, root_3);
								}

								adaptor.addChild(root_2,
										stream_selectClause.nextTree());
								// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:972:24:
								// ( whereClause )?
								if (stream_whereClause.hasNext()) {
									adaptor.addChild(root_2,
											stream_whereClause.nextTree());
								}
								stream_whereClause.reset();

								// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:972:37:
								// ( groupByClause )?
								if (stream_groupByClause.hasNext()) {
									adaptor.addChild(root_2,
											stream_groupByClause.nextTree());
								}
								stream_groupByClause.reset();

								// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:972:52:
								// ( havingClause )?
								if (stream_havingClause.hasNext()) {
									adaptor.addChild(root_2,
											stream_havingClause.nextTree());
								}
								stream_havingClause.reset();

								adaptor.addChild(root_1, root_2);
							}

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "selectFromWholeClause"

	public static class tableSource_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "tableSource"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:977:1: tableSource : (
	// KW_INNERTABLE ( LPAREN num= Number RPAREN )? (al= Identifier )? -> ^(
	// TOK_TABREF ^( KW_INNERTABLE ( $num)? ) ( $al)? ) |tabname= tableName
	// (alias= Identifier )? -> ^( TOK_TABREF $tabname ( $alias)? ) );
	public final TrcParser.tableSource_return tableSource()
			throws RecognitionException {
		TrcParser.tableSource_return retval = new TrcParser.tableSource_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token num = null;
		Token al = null;
		Token alias = null;
		Token KW_INNERTABLE310 = null;
		Token LPAREN311 = null;
		Token RPAREN312 = null;
		ParserRuleReturnScope tabname = null;

		CommonTree num_tree = null;
		CommonTree al_tree = null;
		CommonTree alias_tree = null;
		CommonTree KW_INNERTABLE310_tree = null;
		CommonTree LPAREN311_tree = null;
		CommonTree RPAREN312_tree = null;
		RewriteRuleTokenStream stream_RPAREN = new RewriteRuleTokenStream(
				adaptor, "token RPAREN");
		RewriteRuleTokenStream stream_Number = new RewriteRuleTokenStream(
				adaptor, "token Number");
		RewriteRuleTokenStream stream_Identifier = new RewriteRuleTokenStream(
				adaptor, "token Identifier");
		RewriteRuleTokenStream stream_LPAREN = new RewriteRuleTokenStream(
				adaptor, "token LPAREN");
		RewriteRuleTokenStream stream_KW_INNERTABLE = new RewriteRuleTokenStream(
				adaptor, "token KW_INNERTABLE");
		RewriteRuleSubtreeStream stream_tableName = new RewriteRuleSubtreeStream(
				adaptor, "rule tableName");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:978:3: ( KW_INNERTABLE
			// ( LPAREN num= Number RPAREN )? (al= Identifier )? -> ^(
			// TOK_TABREF ^( KW_INNERTABLE ( $num)? ) ( $al)? ) |tabname=
			// tableName (alias= Identifier )? -> ^( TOK_TABREF $tabname (
			// $alias)? ) )
			int alt83 = 2;
			int LA83_0 = input.LA(1);
			if ((LA83_0 == KW_INNERTABLE)) {
				alt83 = 1;
			} else if ((LA83_0 == Identifier)) {
				alt83 = 2;
			}

			else {
				if (state.backtracking > 0) {
					state.failed = true;
					return retval;
				}
				NoViableAltException nvae = new NoViableAltException("", 83, 0,
						input);
				throw nvae;
			}

			switch (alt83) {
			case 1:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:979:3: KW_INNERTABLE (
			// LPAREN num= Number RPAREN )? (al= Identifier )?
			{
				KW_INNERTABLE310 = (Token) match(input, KW_INNERTABLE,
						FOLLOW_KW_INNERTABLE_in_tableSource5461);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_INNERTABLE.add(KW_INNERTABLE310);

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:979:17: ( LPAREN
				// num= Number RPAREN )?
				int alt80 = 2;
				int LA80_0 = input.LA(1);
				if ((LA80_0 == LPAREN)) {
					alt80 = 1;
				}
				switch (alt80) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:979:18: LPAREN
				// num= Number RPAREN
				{
					LPAREN311 = (Token) match(input, LPAREN,
							FOLLOW_LPAREN_in_tableSource5464);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_LPAREN.add(LPAREN311);

					num = (Token) match(input, Number,
							FOLLOW_Number_in_tableSource5468);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_Number.add(num);

					RPAREN312 = (Token) match(input, RPAREN,
							FOLLOW_RPAREN_in_tableSource5470);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_RPAREN.add(RPAREN312);

				}
					break;

				}

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:979:47: (al=
				// Identifier )?
				int alt81 = 2;
				int LA81_0 = input.LA(1);
				if ((LA81_0 == Identifier)) {
					alt81 = 1;
				}
				switch (alt81) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:979:47: al=
				// Identifier
				{
					al = (Token) match(input, Identifier,
							FOLLOW_Identifier_in_tableSource5476);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_Identifier.add(al);

				}
					break;

				}

				// AST REWRITE
				// elements: num, KW_INNERTABLE, al
				// token labels: al, num
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_al = new RewriteRuleTokenStream(
							adaptor, "token al", al);
					RewriteRuleTokenStream stream_num = new RewriteRuleTokenStream(
							adaptor, "token num", num);
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 980:5: -> ^( TOK_TABREF ^( KW_INNERTABLE ( $num)? ) (
					// $al)? )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:981:7: ^(
						// TOK_TABREF ^( KW_INNERTABLE ( $num)? ) ( $al)? )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_TABREF,
											"TOK_TABREF"), root_1);
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:983:9:
							// ^( KW_INNERTABLE ( $num)? )
							{
								CommonTree root_2 = (CommonTree) adaptor.nil();
								root_2 = (CommonTree) adaptor
										.becomeRoot(
												stream_KW_INNERTABLE.nextNode(),
												root_2);
								// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:983:26:
								// ( $num)?
								if (stream_num.hasNext()) {
									adaptor.addChild(root_2,
											stream_num.nextNode());
								}
								stream_num.reset();

								adaptor.addChild(root_1, root_2);
							}

							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:984:10:
							// ( $al)?
							if (stream_al.hasNext()) {
								adaptor.addChild(root_1, stream_al.nextNode());
							}
							stream_al.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}
				break;
			case 2:
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:986:5: tabname=
			// tableName (alias= Identifier )?
			{
				pushFollow(FOLLOW_tableName_in_tableSource5546);
				tabname = tableName();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_tableName.add(tabname.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:986:23: (alias=
				// Identifier )?
				int alt82 = 2;
				int LA82_0 = input.LA(1);
				if ((LA82_0 == Identifier)) {
					alt82 = 1;
				}
				switch (alt82) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:986:24: alias=
				// Identifier
				{
					alias = (Token) match(input, Identifier,
							FOLLOW_Identifier_in_tableSource5551);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_Identifier.add(alias);

				}
					break;

				}

				// AST REWRITE
				// elements: tabname, alias
				// token labels: alias
				// rule labels: retval, tabname
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_alias = new RewriteRuleTokenStream(
							adaptor, "token alias", alias);
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);
					RewriteRuleSubtreeStream stream_tabname = new RewriteRuleSubtreeStream(
							adaptor, "rule tabname",
							tabname != null ? tabname.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 987:5: -> ^( TOK_TABREF $tabname ( $alias)? )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:988:7: ^(
						// TOK_TABREF $tabname ( $alias)? )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_TABREF,
											"TOK_TABREF"), root_1);
							adaptor.addChild(root_1, stream_tabname.nextTree());
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:988:30:
							// ( $alias)?
							if (stream_alias.hasNext()) {
								adaptor.addChild(root_1,
										stream_alias.nextNode());
							}
							stream_alias.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}
				break;

			}
			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "tableSource"

	public static class whereClause_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "whereClause"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:991:1: whereClause : KW_WHERE
	// searchCondition -> ^( TOK_WHERE searchCondition ) ;
	public final TrcParser.whereClause_return whereClause()
			throws RecognitionException {
		TrcParser.whereClause_return retval = new TrcParser.whereClause_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_WHERE313 = null;
		ParserRuleReturnScope searchCondition314 = null;

		CommonTree KW_WHERE313_tree = null;
		RewriteRuleTokenStream stream_KW_WHERE = new RewriteRuleTokenStream(
				adaptor, "token KW_WHERE");
		RewriteRuleSubtreeStream stream_searchCondition = new RewriteRuleSubtreeStream(
				adaptor, "rule searchCondition");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:992:3: ( KW_WHERE
			// searchCondition -> ^( TOK_WHERE searchCondition ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:993:3: KW_WHERE
			// searchCondition
			{
				KW_WHERE313 = (Token) match(input, KW_WHERE,
						FOLLOW_KW_WHERE_in_whereClause5591);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_WHERE.add(KW_WHERE313);

				pushFollow(FOLLOW_searchCondition_in_whereClause5593);
				searchCondition314 = searchCondition();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_searchCondition.add(searchCondition314.getTree());
				// AST REWRITE
				// elements: searchCondition
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 994:5: -> ^( TOK_WHERE searchCondition )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:995:7: ^(
						// TOK_WHERE searchCondition )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_WHERE,
											"TOK_WHERE"), root_1);
							adaptor.addChild(root_1,
									stream_searchCondition.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "whereClause"

	public static class searchCondition_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "searchCondition"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:998:1: searchCondition :
	// expression ;
	public final TrcParser.searchCondition_return searchCondition()
			throws RecognitionException {
		TrcParser.searchCondition_return retval = new TrcParser.searchCondition_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope expression315 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:999:3: ( expression )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1000:3: expression
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_expression_in_searchCondition5626);
				expression315 = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, expression315.getTree());

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "searchCondition"

	public static class groupByClause_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "groupByClause"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1003:1: groupByClause :
	// KW_GROUP KW_BY groupByExpression ( COMMA groupByExpression )*
	// coordinateInfo -> ^( TOK_GROUPBY ( groupByExpression )+ coordinateInfo )
	// ;
	public final TrcParser.groupByClause_return groupByClause()
			throws RecognitionException {
		TrcParser.groupByClause_return retval = new TrcParser.groupByClause_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_GROUP316 = null;
		Token KW_BY317 = null;
		Token COMMA319 = null;
		ParserRuleReturnScope groupByExpression318 = null;
		ParserRuleReturnScope groupByExpression320 = null;
		ParserRuleReturnScope coordinateInfo321 = null;

		CommonTree KW_GROUP316_tree = null;
		CommonTree KW_BY317_tree = null;
		CommonTree COMMA319_tree = null;
		RewriteRuleTokenStream stream_COMMA = new RewriteRuleTokenStream(
				adaptor, "token COMMA");
		RewriteRuleTokenStream stream_KW_GROUP = new RewriteRuleTokenStream(
				adaptor, "token KW_GROUP");
		RewriteRuleTokenStream stream_KW_BY = new RewriteRuleTokenStream(
				adaptor, "token KW_BY");
		RewriteRuleSubtreeStream stream_coordinateInfo = new RewriteRuleSubtreeStream(
				adaptor, "rule coordinateInfo");
		RewriteRuleSubtreeStream stream_groupByExpression = new RewriteRuleSubtreeStream(
				adaptor, "rule groupByExpression");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1004:3: ( KW_GROUP
			// KW_BY groupByExpression ( COMMA groupByExpression )*
			// coordinateInfo -> ^( TOK_GROUPBY ( groupByExpression )+
			// coordinateInfo ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1005:3: KW_GROUP KW_BY
			// groupByExpression ( COMMA groupByExpression )* coordinateInfo
			{
				KW_GROUP316 = (Token) match(input, KW_GROUP,
						FOLLOW_KW_GROUP_in_groupByClause5641);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_GROUP.add(KW_GROUP316);

				KW_BY317 = (Token) match(input, KW_BY,
						FOLLOW_KW_BY_in_groupByClause5643);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_BY.add(KW_BY317);

				pushFollow(FOLLOW_groupByExpression_in_groupByClause5645);
				groupByExpression318 = groupByExpression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_groupByExpression
							.add(groupByExpression318.getTree());
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1005:36: ( COMMA
				// groupByExpression )*
				loop84: while (true) {
					int alt84 = 2;
					int LA84_0 = input.LA(1);
					if ((LA84_0 == COMMA)) {
						int LA84_2 = input.LA(2);
						if ((LA84_2 == BigintLiteral
								|| LA84_2 == CharSetName
								|| (LA84_2 >= Identifier && LA84_2 <= IdentifierRef)
								|| LA84_2 == KW_AGGR_TIME
								|| LA84_2 == KW_ARRAY
								|| LA84_2 == KW_ATTRIBUTES
								|| (LA84_2 >= KW_CASE && LA84_2 <= KW_CAST)
								|| LA84_2 == KW_EXECUTE
								|| LA84_2 == KW_FALSE
								|| LA84_2 == KW_FOREACH
								|| LA84_2 == KW_IF
								|| LA84_2 == KW_MAP
								|| LA84_2 == KW_NOT
								|| LA84_2 == KW_NULL
								|| LA84_2 == KW_STRUCT
								|| LA84_2 == KW_TRUE
								|| LA84_2 == KW_UNIONTYPE
								|| LA84_2 == LPAREN
								|| LA84_2 == MINUS
								|| (LA84_2 >= Number && LA84_2 <= PLUS)
								|| (LA84_2 >= SmallintLiteral && LA84_2 <= TILDE) || LA84_2 == TinyintLiteral)) {
							alt84 = 1;
						}

					}

					switch (alt84) {
					case 1:
					// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1005:37: COMMA
					// groupByExpression
					{
						COMMA319 = (Token) match(input, COMMA,
								FOLLOW_COMMA_in_groupByClause5648);
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_COMMA.add(COMMA319);

						pushFollow(FOLLOW_groupByExpression_in_groupByClause5650);
						groupByExpression320 = groupByExpression();
						state._fsp--;
						if (state.failed)
							return retval;
						if (state.backtracking == 0)
							stream_groupByExpression.add(groupByExpression320
									.getTree());
					}
						break;

					default:
						break loop84;
					}
				}

				pushFollow(FOLLOW_coordinateInfo_in_groupByClause5654);
				coordinateInfo321 = coordinateInfo();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_coordinateInfo.add(coordinateInfo321.getTree());
				// AST REWRITE
				// elements: coordinateInfo, groupByExpression
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 1006:5: -> ^( TOK_GROUPBY ( groupByExpression )+
					// coordinateInfo )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1007:7: ^(
						// TOK_GROUPBY ( groupByExpression )+ coordinateInfo )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_GROUPBY,
											"TOK_GROUPBY"), root_1);
							if (!(stream_groupByExpression.hasNext())) {
								throw new RewriteEarlyExitException();
							}
							while (stream_groupByExpression.hasNext()) {
								adaptor.addChild(root_1,
										stream_groupByExpression.nextTree());
							}
							stream_groupByExpression.reset();

							adaptor.addChild(root_1,
									stream_coordinateInfo.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "groupByClause"

	public static class coordinateInfo_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "coordinateInfo"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1010:1: coordinateInfo : (
	// KW_COORDINATE KW_BY coordinateExpr )? ( aggrInterval )? ( accuInterval )?
	// ( swInterval )? ( KW_ACCUGLOBAL )? -> ^( TOK_COORDINATE ( coordinateExpr
	// )? ( aggrInterval )? ( accuInterval )? ( swInterval )? ( KW_ACCUGLOBAL )?
	// ) ;
	public final TrcParser.coordinateInfo_return coordinateInfo()
			throws RecognitionException {
		TrcParser.coordinateInfo_return retval = new TrcParser.coordinateInfo_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_COORDINATE322 = null;
		Token KW_BY323 = null;
		Token KW_ACCUGLOBAL328 = null;
		ParserRuleReturnScope coordinateExpr324 = null;
		ParserRuleReturnScope aggrInterval325 = null;
		ParserRuleReturnScope accuInterval326 = null;
		ParserRuleReturnScope swInterval327 = null;

		CommonTree KW_COORDINATE322_tree = null;
		CommonTree KW_BY323_tree = null;
		CommonTree KW_ACCUGLOBAL328_tree = null;
		RewriteRuleTokenStream stream_KW_COORDINATE = new RewriteRuleTokenStream(
				adaptor, "token KW_COORDINATE");
		RewriteRuleTokenStream stream_KW_ACCUGLOBAL = new RewriteRuleTokenStream(
				adaptor, "token KW_ACCUGLOBAL");
		RewriteRuleTokenStream stream_KW_BY = new RewriteRuleTokenStream(
				adaptor, "token KW_BY");
		RewriteRuleSubtreeStream stream_coordinateExpr = new RewriteRuleSubtreeStream(
				adaptor, "rule coordinateExpr");
		RewriteRuleSubtreeStream stream_aggrInterval = new RewriteRuleSubtreeStream(
				adaptor, "rule aggrInterval");
		RewriteRuleSubtreeStream stream_accuInterval = new RewriteRuleSubtreeStream(
				adaptor, "rule accuInterval");
		RewriteRuleSubtreeStream stream_swInterval = new RewriteRuleSubtreeStream(
				adaptor, "rule swInterval");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1011:3: ( (
			// KW_COORDINATE KW_BY coordinateExpr )? ( aggrInterval )? (
			// accuInterval )? ( swInterval )? ( KW_ACCUGLOBAL )? -> ^(
			// TOK_COORDINATE ( coordinateExpr )? ( aggrInterval )? (
			// accuInterval )? ( swInterval )? ( KW_ACCUGLOBAL )? ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1012:3: (
			// KW_COORDINATE KW_BY coordinateExpr )? ( aggrInterval )? (
			// accuInterval )? ( swInterval )? ( KW_ACCUGLOBAL )?
			{
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1012:3: (
				// KW_COORDINATE KW_BY coordinateExpr )?
				int alt85 = 2;
				int LA85_0 = input.LA(1);
				if ((LA85_0 == KW_COORDINATE)) {
					alt85 = 1;
				}
				switch (alt85) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1012:4:
				// KW_COORDINATE KW_BY coordinateExpr
				{
					KW_COORDINATE322 = (Token) match(input, KW_COORDINATE,
							FOLLOW_KW_COORDINATE_in_coordinateInfo5691);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_KW_COORDINATE.add(KW_COORDINATE322);

					KW_BY323 = (Token) match(input, KW_BY,
							FOLLOW_KW_BY_in_coordinateInfo5693);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_KW_BY.add(KW_BY323);

					pushFollow(FOLLOW_coordinateExpr_in_coordinateInfo5695);
					coordinateExpr324 = coordinateExpr();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_coordinateExpr.add(coordinateExpr324.getTree());
				}
					break;

				}

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1012:41: (
				// aggrInterval )?
				int alt86 = 2;
				int LA86_0 = input.LA(1);
				if ((LA86_0 == KW_WITH)) {
					int LA86_1 = input.LA(2);
					if ((LA86_1 == KW_AGGR)) {
						alt86 = 1;
					}
				}
				switch (alt86) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1012:42:
				// aggrInterval
				{
					pushFollow(FOLLOW_aggrInterval_in_coordinateInfo5700);
					aggrInterval325 = aggrInterval();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_aggrInterval.add(aggrInterval325.getTree());
				}
					break;

				}

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1012:57: (
				// accuInterval )?
				int alt87 = 2;
				int LA87_0 = input.LA(1);
				if ((LA87_0 == KW_WITH)) {
					int LA87_1 = input.LA(2);
					if ((LA87_1 == KW_ACCU)) {
						alt87 = 1;
					}
				}
				switch (alt87) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1012:58:
				// accuInterval
				{
					pushFollow(FOLLOW_accuInterval_in_coordinateInfo5705);
					accuInterval326 = accuInterval();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_accuInterval.add(accuInterval326.getTree());
				}
					break;

				}

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1012:73: (
				// swInterval )?
				int alt88 = 2;
				int LA88_0 = input.LA(1);
				if ((LA88_0 == KW_WITH)) {
					int LA88_1 = input.LA(2);
					if ((LA88_1 == KW_SW)) {
						alt88 = 1;
					}
				}
				switch (alt88) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1012:74:
				// swInterval
				{
					pushFollow(FOLLOW_swInterval_in_coordinateInfo5710);
					swInterval327 = swInterval();
					state._fsp--;
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_swInterval.add(swInterval327.getTree());
				}
					break;

				}

				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1012:87: (
				// KW_ACCUGLOBAL )?
				int alt89 = 2;
				int LA89_0 = input.LA(1);
				if ((LA89_0 == KW_ACCUGLOBAL)) {
					alt89 = 1;
				}
				switch (alt89) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1012:87:
				// KW_ACCUGLOBAL
				{
					KW_ACCUGLOBAL328 = (Token) match(input, KW_ACCUGLOBAL,
							FOLLOW_KW_ACCUGLOBAL_in_coordinateInfo5714);
					if (state.failed)
						return retval;
					if (state.backtracking == 0)
						stream_KW_ACCUGLOBAL.add(KW_ACCUGLOBAL328);

				}
					break;

				}

				// AST REWRITE
				// elements: swInterval, coordinateExpr, aggrInterval,
				// accuInterval, KW_ACCUGLOBAL
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 1013:5: -> ^( TOK_COORDINATE ( coordinateExpr )? (
					// aggrInterval )? ( accuInterval )? ( swInterval )? (
					// KW_ACCUGLOBAL )? )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1014:7: ^(
						// TOK_COORDINATE ( coordinateExpr )? ( aggrInterval )?
						// ( accuInterval )? ( swInterval )? ( KW_ACCUGLOBAL )?
						// )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_COORDINATE,
											"TOK_COORDINATE"), root_1);
							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1015:24:
							// ( coordinateExpr )?
							if (stream_coordinateExpr.hasNext()) {
								adaptor.addChild(root_1,
										stream_coordinateExpr.nextTree());
							}
							stream_coordinateExpr.reset();

							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1015:40:
							// ( aggrInterval )?
							if (stream_aggrInterval.hasNext()) {
								adaptor.addChild(root_1,
										stream_aggrInterval.nextTree());
							}
							stream_aggrInterval.reset();

							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1015:56:
							// ( accuInterval )?
							if (stream_accuInterval.hasNext()) {
								adaptor.addChild(root_1,
										stream_accuInterval.nextTree());
							}
							stream_accuInterval.reset();

							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1015:72:
							// ( swInterval )?
							if (stream_swInterval.hasNext()) {
								adaptor.addChild(root_1,
										stream_swInterval.nextTree());
							}
							stream_swInterval.reset();

							// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1015:86:
							// ( KW_ACCUGLOBAL )?
							if (stream_KW_ACCUGLOBAL.hasNext()) {
								adaptor.addChild(root_1,
										stream_KW_ACCUGLOBAL.nextNode());
							}
							stream_KW_ACCUGLOBAL.reset();

							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "coordinateInfo"

	public static class coordinateExpr_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "coordinateExpr"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1019:1: coordinateExpr :
	// expression -> ^( TOK_COORDINATE_EXPR expression ) ;
	public final TrcParser.coordinateExpr_return coordinateExpr()
			throws RecognitionException {
		TrcParser.coordinateExpr_return retval = new TrcParser.coordinateExpr_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope expression329 = null;

		RewriteRuleSubtreeStream stream_expression = new RewriteRuleSubtreeStream(
				adaptor, "rule expression");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1020:3: ( expression
			// -> ^( TOK_COORDINATE_EXPR expression ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1021:3: expression
			{
				pushFollow(FOLLOW_expression_in_coordinateExpr5784);
				expression329 = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_expression.add(expression329.getTree());
				// AST REWRITE
				// elements: expression
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 1022:5: -> ^( TOK_COORDINATE_EXPR expression )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1023:7: ^(
						// TOK_COORDINATE_EXPR expression )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(
											TOK_COORDINATE_EXPR,
											"TOK_COORDINATE_EXPR"), root_1);
							adaptor.addChild(root_1,
									stream_expression.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "coordinateExpr"

	public static class aggrInterval_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "aggrInterval"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1026:1: aggrInterval : KW_WITH
	// KW_AGGR KW_INTERVAL Number KW_SECONDS -> ^( TOK_AGGR_INTERVAL Number ) ;
	public final TrcParser.aggrInterval_return aggrInterval()
			throws RecognitionException {
		TrcParser.aggrInterval_return retval = new TrcParser.aggrInterval_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_WITH330 = null;
		Token KW_AGGR331 = null;
		Token KW_INTERVAL332 = null;
		Token Number333 = null;
		Token KW_SECONDS334 = null;

		CommonTree KW_WITH330_tree = null;
		CommonTree KW_AGGR331_tree = null;
		CommonTree KW_INTERVAL332_tree = null;
		CommonTree Number333_tree = null;
		CommonTree KW_SECONDS334_tree = null;
		RewriteRuleTokenStream stream_Number = new RewriteRuleTokenStream(
				adaptor, "token Number");
		RewriteRuleTokenStream stream_KW_SECONDS = new RewriteRuleTokenStream(
				adaptor, "token KW_SECONDS");
		RewriteRuleTokenStream stream_KW_AGGR = new RewriteRuleTokenStream(
				adaptor, "token KW_AGGR");
		RewriteRuleTokenStream stream_KW_WITH = new RewriteRuleTokenStream(
				adaptor, "token KW_WITH");
		RewriteRuleTokenStream stream_KW_INTERVAL = new RewriteRuleTokenStream(
				adaptor, "token KW_INTERVAL");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1027:3: ( KW_WITH
			// KW_AGGR KW_INTERVAL Number KW_SECONDS -> ^( TOK_AGGR_INTERVAL
			// Number ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1028:3: KW_WITH
			// KW_AGGR KW_INTERVAL Number KW_SECONDS
			{
				KW_WITH330 = (Token) match(input, KW_WITH,
						FOLLOW_KW_WITH_in_aggrInterval5817);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_WITH.add(KW_WITH330);

				KW_AGGR331 = (Token) match(input, KW_AGGR,
						FOLLOW_KW_AGGR_in_aggrInterval5819);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_AGGR.add(KW_AGGR331);

				KW_INTERVAL332 = (Token) match(input, KW_INTERVAL,
						FOLLOW_KW_INTERVAL_in_aggrInterval5821);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_INTERVAL.add(KW_INTERVAL332);

				Number333 = (Token) match(input, Number,
						FOLLOW_Number_in_aggrInterval5823);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_Number.add(Number333);

				KW_SECONDS334 = (Token) match(input, KW_SECONDS,
						FOLLOW_KW_SECONDS_in_aggrInterval5825);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_SECONDS.add(KW_SECONDS334);

				// AST REWRITE
				// elements: Number
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 1029:5: -> ^( TOK_AGGR_INTERVAL Number )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1030:7: ^(
						// TOK_AGGR_INTERVAL Number )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(
											TOK_AGGR_INTERVAL,
											"TOK_AGGR_INTERVAL"), root_1);
							adaptor.addChild(root_1, stream_Number.nextNode());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "aggrInterval"

	public static class accuInterval_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "accuInterval"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1033:1: accuInterval : KW_WITH
	// KW_ACCU KW_INTERVAL Number KW_SECONDS -> ^( TOK_ACCU_INTERVAL Number ) ;
	public final TrcParser.accuInterval_return accuInterval()
			throws RecognitionException {
		TrcParser.accuInterval_return retval = new TrcParser.accuInterval_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_WITH335 = null;
		Token KW_ACCU336 = null;
		Token KW_INTERVAL337 = null;
		Token Number338 = null;
		Token KW_SECONDS339 = null;

		CommonTree KW_WITH335_tree = null;
		CommonTree KW_ACCU336_tree = null;
		CommonTree KW_INTERVAL337_tree = null;
		CommonTree Number338_tree = null;
		CommonTree KW_SECONDS339_tree = null;
		RewriteRuleTokenStream stream_KW_ACCU = new RewriteRuleTokenStream(
				adaptor, "token KW_ACCU");
		RewriteRuleTokenStream stream_Number = new RewriteRuleTokenStream(
				adaptor, "token Number");
		RewriteRuleTokenStream stream_KW_SECONDS = new RewriteRuleTokenStream(
				adaptor, "token KW_SECONDS");
		RewriteRuleTokenStream stream_KW_WITH = new RewriteRuleTokenStream(
				adaptor, "token KW_WITH");
		RewriteRuleTokenStream stream_KW_INTERVAL = new RewriteRuleTokenStream(
				adaptor, "token KW_INTERVAL");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1034:3: ( KW_WITH
			// KW_ACCU KW_INTERVAL Number KW_SECONDS -> ^( TOK_ACCU_INTERVAL
			// Number ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1035:3: KW_WITH
			// KW_ACCU KW_INTERVAL Number KW_SECONDS
			{
				KW_WITH335 = (Token) match(input, KW_WITH,
						FOLLOW_KW_WITH_in_accuInterval5858);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_WITH.add(KW_WITH335);

				KW_ACCU336 = (Token) match(input, KW_ACCU,
						FOLLOW_KW_ACCU_in_accuInterval5860);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_ACCU.add(KW_ACCU336);

				KW_INTERVAL337 = (Token) match(input, KW_INTERVAL,
						FOLLOW_KW_INTERVAL_in_accuInterval5862);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_INTERVAL.add(KW_INTERVAL337);

				Number338 = (Token) match(input, Number,
						FOLLOW_Number_in_accuInterval5864);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_Number.add(Number338);

				KW_SECONDS339 = (Token) match(input, KW_SECONDS,
						FOLLOW_KW_SECONDS_in_accuInterval5866);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_SECONDS.add(KW_SECONDS339);

				// AST REWRITE
				// elements: Number
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 1036:5: -> ^( TOK_ACCU_INTERVAL Number )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1037:7: ^(
						// TOK_ACCU_INTERVAL Number )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(
											TOK_ACCU_INTERVAL,
											"TOK_ACCU_INTERVAL"), root_1);
							adaptor.addChild(root_1, stream_Number.nextNode());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "accuInterval"

	public static class swInterval_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "swInterval"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1040:1: swInterval : KW_WITH
	// KW_SW KW_INTERVAL Number KW_SECONDS -> ^( TOK_SW_INTERVAL Number ) ;
	public final TrcParser.swInterval_return swInterval()
			throws RecognitionException {
		TrcParser.swInterval_return retval = new TrcParser.swInterval_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_WITH340 = null;
		Token KW_SW341 = null;
		Token KW_INTERVAL342 = null;
		Token Number343 = null;
		Token KW_SECONDS344 = null;

		CommonTree KW_WITH340_tree = null;
		CommonTree KW_SW341_tree = null;
		CommonTree KW_INTERVAL342_tree = null;
		CommonTree Number343_tree = null;
		CommonTree KW_SECONDS344_tree = null;
		RewriteRuleTokenStream stream_Number = new RewriteRuleTokenStream(
				adaptor, "token Number");
		RewriteRuleTokenStream stream_KW_SECONDS = new RewriteRuleTokenStream(
				adaptor, "token KW_SECONDS");
		RewriteRuleTokenStream stream_KW_SW = new RewriteRuleTokenStream(
				adaptor, "token KW_SW");
		RewriteRuleTokenStream stream_KW_WITH = new RewriteRuleTokenStream(
				adaptor, "token KW_WITH");
		RewriteRuleTokenStream stream_KW_INTERVAL = new RewriteRuleTokenStream(
				adaptor, "token KW_INTERVAL");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1041:3: ( KW_WITH
			// KW_SW KW_INTERVAL Number KW_SECONDS -> ^( TOK_SW_INTERVAL Number
			// ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1042:3: KW_WITH KW_SW
			// KW_INTERVAL Number KW_SECONDS
			{
				KW_WITH340 = (Token) match(input, KW_WITH,
						FOLLOW_KW_WITH_in_swInterval5899);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_WITH.add(KW_WITH340);

				KW_SW341 = (Token) match(input, KW_SW,
						FOLLOW_KW_SW_in_swInterval5901);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_SW.add(KW_SW341);

				KW_INTERVAL342 = (Token) match(input, KW_INTERVAL,
						FOLLOW_KW_INTERVAL_in_swInterval5903);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_INTERVAL.add(KW_INTERVAL342);

				Number343 = (Token) match(input, Number,
						FOLLOW_Number_in_swInterval5905);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_Number.add(Number343);

				KW_SECONDS344 = (Token) match(input, KW_SECONDS,
						FOLLOW_KW_SECONDS_in_swInterval5907);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_SECONDS.add(KW_SECONDS344);

				// AST REWRITE
				// elements: Number
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 1043:5: -> ^( TOK_SW_INTERVAL Number )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1044:7: ^(
						// TOK_SW_INTERVAL Number )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor
									.becomeRoot(
											(CommonTree) adaptor.create(
													TOK_SW_INTERVAL,
													"TOK_SW_INTERVAL"), root_1);
							adaptor.addChild(root_1, stream_Number.nextNode());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "swInterval"

	public static class groupByExpression_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "groupByExpression"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1047:1: groupByExpression :
	// expression ;
	public final TrcParser.groupByExpression_return groupByExpression()
			throws RecognitionException {
		TrcParser.groupByExpression_return retval = new TrcParser.groupByExpression_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope expression345 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1048:3: ( expression )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1049:3: expression
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_expression_in_groupByExpression5940);
				expression345 = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, expression345.getTree());

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "groupByExpression"

	public static class havingClause_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "havingClause"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1052:1: havingClause :
	// KW_HAVING havingCondition -> ^( TOK_HAVING havingCondition ) ;
	public final TrcParser.havingClause_return havingClause()
			throws RecognitionException {
		TrcParser.havingClause_return retval = new TrcParser.havingClause_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token KW_HAVING346 = null;
		ParserRuleReturnScope havingCondition347 = null;

		CommonTree KW_HAVING346_tree = null;
		RewriteRuleTokenStream stream_KW_HAVING = new RewriteRuleTokenStream(
				adaptor, "token KW_HAVING");
		RewriteRuleSubtreeStream stream_havingCondition = new RewriteRuleSubtreeStream(
				adaptor, "rule havingCondition");

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1053:3: ( KW_HAVING
			// havingCondition -> ^( TOK_HAVING havingCondition ) )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1054:3: KW_HAVING
			// havingCondition
			{
				KW_HAVING346 = (Token) match(input, KW_HAVING,
						FOLLOW_KW_HAVING_in_havingClause5955);
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_KW_HAVING.add(KW_HAVING346);

				pushFollow(FOLLOW_havingCondition_in_havingClause5957);
				havingCondition347 = havingCondition();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					stream_havingCondition.add(havingCondition347.getTree());
				// AST REWRITE
				// elements: havingCondition
				// token labels:
				// rule labels: retval
				// token list labels:
				// rule list labels:
				// wildcard labels:
				if (state.backtracking == 0) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(
							adaptor, "rule retval",
							retval != null ? retval.getTree() : null);

					root_0 = (CommonTree) adaptor.nil();
					// 1055:5: -> ^( TOK_HAVING havingCondition )
					{
						// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1056:7: ^(
						// TOK_HAVING havingCondition )
						{
							CommonTree root_1 = (CommonTree) adaptor.nil();
							root_1 = (CommonTree) adaptor.becomeRoot(
									(CommonTree) adaptor.create(TOK_HAVING,
											"TOK_HAVING"), root_1);
							adaptor.addChild(root_1,
									stream_havingCondition.nextTree());
							adaptor.addChild(root_0, root_1);
						}

					}

					retval.tree = root_0;
				}

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "havingClause"

	public static class havingCondition_return extends ParserRuleReturnScope {
		CommonTree tree;

		@Override
		public CommonTree getTree() {
			return tree;
		}
	};

	// $ANTLR start "havingCondition"
	// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1059:1: havingCondition :
	// expression ;
	public final TrcParser.havingCondition_return havingCondition()
			throws RecognitionException {
		TrcParser.havingCondition_return retval = new TrcParser.havingCondition_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope expression348 = null;

		try {
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1060:3: ( expression )
			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:1061:3: expression
			{
				root_0 = (CommonTree) adaptor.nil();

				pushFollow(FOLLOW_expression_in_havingCondition5990);
				expression348 = expression();
				state._fsp--;
				if (state.failed)
					return retval;
				if (state.backtracking == 0)
					adaptor.addChild(root_0, expression348.getTree());

			}

			retval.stop = input.LT(-1);

			if (state.backtracking == 0) {
				retval.tree = (CommonTree) adaptor.rulePostProcessing(root_0);
				adaptor.setTokenBoundaries(retval.tree, retval.start,
						retval.stop);
			}
		} catch (RecognitionException re) {
			reportError(re);
			recover(input, re);
			retval.tree = (CommonTree) adaptor.errorNode(input, retval.start,
					input.LT(-1), re);
		} finally {
			// do for sure before leaving
		}
		return retval;
	}

	// $ANTLR end "havingCondition"

	// $ANTLR start synpred15_Trc
	public final void synpred15_Trc_fragment() throws RecognitionException {
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:342:31: ( keyExpr )
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:342:31: keyExpr
		{
			pushFollow(FOLLOW_keyExpr_in_synpred15_Trc1767);
			keyExpr();
			state._fsp--;
			if (state.failed)
				return;

		}

	}

	// $ANTLR end synpred15_Trc

	// $ANTLR start synpred35_Trc
	public final void synpred35_Trc_fragment() throws RecognitionException {
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:465:29: (
		// precedencePlusOperator precedenceStarExpression )
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:465:29:
		// precedencePlusOperator precedenceStarExpression
		{
			pushFollow(FOLLOW_precedencePlusOperator_in_synpred35_Trc2438);
			precedencePlusOperator();
			state._fsp--;
			if (state.failed)
				return;

			pushFollow(FOLLOW_precedenceStarExpression_in_synpred35_Trc2441);
			precedenceStarExpression();
			state._fsp--;
			if (state.failed)
				return;

		}

	}

	// $ANTLR end synpred35_Trc

	// $ANTLR start synpred44_Trc
	public final void synpred44_Trc_fragment() throws RecognitionException {
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:505:5: ( function )
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:505:5: function
		{
			pushFollow(FOLLOW_function_in_synpred44_Trc2650);
			function();
			state._fsp--;
			if (state.failed)
				return;

		}

	}

	// $ANTLR end synpred44_Trc

	// $ANTLR start synpred48_Trc
	public final void synpred48_Trc_fragment() throws RecognitionException {
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:509:5: ( caseExpression )
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:509:5: caseExpression
		{
			pushFollow(FOLLOW_caseExpression_in_synpred48_Trc2674);
			caseExpression();
			state._fsp--;
			if (state.failed)
				return;

		}

	}

	// $ANTLR end synpred48_Trc

	// $ANTLR start synpred49_Trc
	public final void synpred49_Trc_fragment() throws RecognitionException {
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:510:5: ( whenExpression )
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:510:5: whenExpression
		{
			pushFollow(FOLLOW_whenExpression_in_synpred49_Trc2680);
			whenExpression();
			state._fsp--;
			if (state.failed)
				return;

		}

	}

	// $ANTLR end synpred49_Trc

	// $ANTLR start synpred50_Trc
	public final void synpred50_Trc_fragment() throws RecognitionException {
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:511:5: ( tableOrColumn )
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:511:5: tableOrColumn
		{
			pushFollow(FOLLOW_tableOrColumn_in_synpred50_Trc2686);
			tableOrColumn();
			state._fsp--;
			if (state.failed)
				return;

		}

	}

	// $ANTLR end synpred50_Trc

	// $ANTLR start synpred54_Trc
	public final void synpred54_Trc_fragment() throws RecognitionException {
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:520:5: ( StringLiteral )
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:520:5: StringLiteral
		{
			match(input, StringLiteral,
					FOLLOW_StringLiteral_in_synpred54_Trc2731);
			if (state.failed)
				return;

		}

	}

	// $ANTLR end synpred54_Trc

	// $ANTLR start synpred55_Trc
	public final void synpred55_Trc_fragment() throws RecognitionException {
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:521:5: (
		// stringLiteralSequence )
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:521:5:
		// stringLiteralSequence
		{
			pushFollow(FOLLOW_stringLiteralSequence_in_synpred55_Trc2737);
			stringLiteralSequence();
			state._fsp--;
			if (state.failed)
				return;

		}

	}

	// $ANTLR end synpred55_Trc

	// $ANTLR start synpred60_Trc
	public final void synpred60_Trc_fragment() throws RecognitionException {
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:531:17: ( StringLiteral )
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:531:17: StringLiteral
		{
			match(input, StringLiteral,
					FOLLOW_StringLiteral_in_synpred60_Trc2784);
			if (state.failed)
				return;

		}

	}

	// $ANTLR end synpred60_Trc

	// $ANTLR start synpred143_Trc
	public final void synpred143_Trc_fragment() throws RecognitionException {
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:951:3: (
		// selectFromWholeClause ( unionOperator selectFromWholeClause )+ )
		// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:951:3:
		// selectFromWholeClause ( unionOperator selectFromWholeClause )+
		{
			pushFollow(FOLLOW_selectFromWholeClause_in_synpred143_Trc5213);
			selectFromWholeClause();
			state._fsp--;
			if (state.failed)
				return;

			// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:951:25: (
			// unionOperator selectFromWholeClause )+
			int cnt95 = 0;
			loop95: while (true) {
				int alt95 = 2;
				int LA95_0 = input.LA(1);
				if ((LA95_0 == KW_UNION)) {
					alt95 = 1;
				}

				switch (alt95) {
				case 1:
				// D:\\svn\\EasyCount\\src\\main\\java\\Trc.g:951:26:
				// unionOperator selectFromWholeClause
				{
					pushFollow(FOLLOW_unionOperator_in_synpred143_Trc5216);
					unionOperator();
					state._fsp--;
					if (state.failed)
						return;

					pushFollow(FOLLOW_selectFromWholeClause_in_synpred143_Trc5218);
					selectFromWholeClause();
					state._fsp--;
					if (state.failed)
						return;

				}
					break;

				default:
					if (cnt95 >= 1)
						break loop95;
					if (state.backtracking > 0) {
						state.failed = true;
						return;
					}
					EarlyExitException eee = new EarlyExitException(95, input);
					throw eee;
				}
				cnt95++;
			}

		}

	}

	// $ANTLR end synpred143_Trc

	// Delegated rules

	public final boolean synpred55_Trc() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred55_Trc_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: " + re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed = false;
		return success;
	}

	public final boolean synpred49_Trc() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred49_Trc_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: " + re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed = false;
		return success;
	}

	public final boolean synpred15_Trc() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred15_Trc_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: " + re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed = false;
		return success;
	}

	public final boolean synpred44_Trc() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred44_Trc_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: " + re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed = false;
		return success;
	}

	public final boolean synpred60_Trc() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred60_Trc_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: " + re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed = false;
		return success;
	}

	public final boolean synpred143_Trc() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred143_Trc_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: " + re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed = false;
		return success;
	}

	public final boolean synpred35_Trc() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred35_Trc_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: " + re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed = false;
		return success;
	}

	public final boolean synpred54_Trc() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred54_Trc_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: " + re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed = false;
		return success;
	}

	public final boolean synpred50_Trc() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred50_Trc_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: " + re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed = false;
		return success;
	}

	public final boolean synpred48_Trc() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred48_Trc_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: " + re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed = false;
		return success;
	}

	protected DFA33 dfa33 = new DFA33(this);
	static final String DFA33_eotS = "\102\uffff";
	static final String DFA33_eofS = "\1\1\101\uffff";
	static final String DFA33_minS = "\1\4\34\uffff\1\0\44\uffff";
	static final String DFA33_maxS = "\1\u021e\34\uffff\1\0\44\uffff";
	static final String DFA33_acceptS = "\1\uffff\1\2\77\uffff\1\1";
	static final String DFA33_specialS = "\35\uffff\1\0\44\uffff}>";
	static final String[] DFA33_transitionS = {
			"\1\1\1\uffff\1\1\1\uffff\1\1\1\uffff\1\1\2\uffff\1\1\5\uffff\1\1\1\uffff"
					+ "\2\1\1\uffff\4\1\3\uffff\1\1\3\uffff\1\1\1\uffff\2\1\1\uffff\1\1\12\uffff"
					+ "\2\1\13\uffff\1\1\27\uffff\1\1\2\uffff\1\1\2\uffff\1\1\1\uffff\1\1\4"
					+ "\uffff\1\1\5\uffff\2\1\2\uffff\1\1\3\uffff\2\1\1\uffff\2\1\2\uffff\1"
					+ "\1\1\uffff\1\1\6\uffff\1\1\14\uffff\1\1\10\uffff\1\1\4\uffff\1\1\1\uffff"
					+ "\1\1\4\uffff\1\1\30\uffff\1\1\6\uffff\1\1\25\uffff\2\1\7\uffff\1\1\6"
					+ "\uffff\1\1\2\uffff\2\1\3\uffff\1\1\6\uffff\2\1\1\uffff\1\1\1\uffff\3"
					+ "\1\2\uffff\1\35\1\uffff\1\1\4\uffff\1\1\1\35\1\uffff\3\1\1\uffff\1\1"
					+ "\1\uffff\3\1\u0107\uffff\1\1", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "\1\uffff", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "" };

	static final short[] DFA33_eot = DFA.unpackEncodedString(DFA33_eotS);
	static final short[] DFA33_eof = DFA.unpackEncodedString(DFA33_eofS);
	static final char[] DFA33_min = DFA
			.unpackEncodedStringToUnsignedChars(DFA33_minS);
	static final char[] DFA33_max = DFA
			.unpackEncodedStringToUnsignedChars(DFA33_maxS);
	static final short[] DFA33_accept = DFA.unpackEncodedString(DFA33_acceptS);
	static final short[] DFA33_special = DFA
			.unpackEncodedString(DFA33_specialS);
	static final short[][] DFA33_transition;

	static {
		int numStates = DFA33_transitionS.length;
		DFA33_transition = new short[numStates][];
		for (int i = 0; i < numStates; i++) {
			DFA33_transition[i] = DFA.unpackEncodedString(DFA33_transitionS[i]);
		}
	}

	protected class DFA33 extends DFA {

		public DFA33(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 33;
			this.eot = DFA33_eot;
			this.eof = DFA33_eof;
			this.min = DFA33_min;
			this.max = DFA33_max;
			this.accept = DFA33_accept;
			this.special = DFA33_special;
			this.transition = DFA33_transition;
		}

		@Override
		public String getDescription() {
			return "()* loopback of 465:28: ( precedencePlusOperator ^ precedenceStarExpression )*";
		}

		@Override
		public int specialStateTransition(int s, IntStream _input)
				throws NoViableAltException {
			TokenStream input = (TokenStream) _input;
			int _s = s;
			switch (s) {
			case 0:
				int LA33_29 = input.LA(1);

				int index33_29 = input.index();
				input.rewind();
				s = -1;
				if ((synpred35_Trc())) {
					s = 65;
				} else if ((true)) {
					s = 1;
				}

				input.seek(index33_29);
				if (s >= 0)
					return s;
				break;
			}
			if (state.backtracking > 0) {
				state.failed = true;
				return -1;
			}
			NoViableAltException nvae = new NoViableAltException(
					getDescription(), 33, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	public static final BitSet FOLLOW_explainStatement_in_statement1403 = new BitSet(
			new long[] { 0x0000000000000000L });
	public static final BitSet FOLLOW_EOF_in_statement1405 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_execStatement_in_statement1411 = new BitSet(
			new long[] { 0x0000000000000000L });
	public static final BitSet FOLLOW_EOF_in_statement1413 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_EXPLAIN_in_explainStatement1428 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000400800000000L,
					0x0000000000000008L, 0x2000000000000000L });
	public static final BitSet FOLLOW_KW_EXTENDED_in_explainStatement1440 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000008L, 0x2000000000000000L });
	public static final BitSet FOLLOW_KW_FORMATTED_in_explainStatement1450 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000008L, 0x2000000000000000L });
	public static final BitSet FOLLOW_execStatement_in_explainStatement1459 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_execStatement1_in_execStatement1496 = new BitSet(
			new long[] { 0x0000000000000002L, 0x0000000000000000L,
					0x0000000000000000L, 0x2000000000000000L });
	public static final BitSet FOLLOW_execStatement2_in_execStatement1498 = new BitSet(
			new long[] { 0x0000000000000002L, 0x0000000000000000L,
					0x0000000000000000L, 0x2000000000000000L });
	public static final BitSet FOLLOW_withClause_in_execStatement11535 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000008L });
	public static final BitSet FOLLOW_insertSelectFromWholeClause_in_execStatement11538 = new BitSet(
			new long[] { 0x0000000000000402L, 0x0000000000000000L,
					0x0000000000000008L });
	public static final BitSet FOLLOW_COMMA_in_execStatement11541 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000008L });
	public static final BitSet FOLLOW_insertSelectFromWholeClause_in_execStatement11544 = new BitSet(
			new long[] { 0x0000000000000402L, 0x0000000000000000L,
					0x0000000000000008L });
	public static final BitSet FOLLOW_withClause_in_execStatement21583 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000008L });
	public static final BitSet FOLLOW_insertSelectFromWholeClause_in_execStatement21585 = new BitSet(
			new long[] { 0x0000000000000402L, 0x0000000000000000L,
					0x0000000000000008L });
	public static final BitSet FOLLOW_COMMA_in_execStatement21588 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000008L });
	public static final BitSet FOLLOW_insertSelectFromWholeClause_in_execStatement21591 = new BitSet(
			new long[] { 0x0000000000000402L, 0x0000000000000000L,
					0x0000000000000008L });
	public static final BitSet FOLLOW_KW_WITH_in_withClause1629 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000002L });
	public static final BitSet FOLLOW_subQuerySource_in_withClause1631 = new BitSet(
			new long[] { 0x0000000000000402L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000002L });
	public static final BitSet FOLLOW_COMMA_in_withClause1634 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000002L });
	public static final BitSet FOLLOW_subQuerySource_in_withClause1637 = new BitSet(
			new long[] { 0x0000000000000402L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000002L });
	public static final BitSet FOLLOW_insertClause_in_insertSelectFromWholeClause1673 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000400L });
	public static final BitSet FOLLOW_selectClause_in_insertSelectFromWholeClause1675 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000800000000000L });
	public static final BitSet FOLLOW_fromClause_in_insertSelectFromWholeClause1677 = new BitSet(
			new long[] { 0x0000000000000002L, 0x00C0000000000000L,
					0x0000000000000000L, 0x0800000000000000L });
	public static final BitSet FOLLOW_whereClause_in_insertSelectFromWholeClause1679 = new BitSet(
			new long[] { 0x0000000000000002L, 0x00C0000000000000L });
	public static final BitSet FOLLOW_groupByClause_in_insertSelectFromWholeClause1682 = new BitSet(
			new long[] { 0x0000000000000002L, 0x0080000000000000L });
	public static final BitSet FOLLOW_havingClause_in_insertSelectFromWholeClause1685 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_INSERT_in_insertClause1761 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000080L });
	public static final BitSet FOLLOW_KW_INTO_in_insertClause1763 = new BitSet(
			new long[] { 0x0000000001000000L, 0x0000000000000000L,
					0x0004000000000000L });
	public static final BitSet FOLLOW_destTable_in_insertClause1765 = new BitSet(
			new long[] { 0x0000000000000002L, 0x0000000000000000L,
					0x0000000000000000L, 0x2000000000000000L });
	public static final BitSet FOLLOW_keyExpr_in_insertClause1767 = new BitSet(
			new long[] { 0x0000000000000002L, 0x0000000000000000L,
					0x0000000000000000L, 0x2000000000000000L });
	public static final BitSet FOLLOW_attrsExpr_in_insertClause1770 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_tableName_in_destTable1810 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_PRINTTABLE_in_destTable1816 = new BitSet(
			new long[] { 0x0000000001000002L });
	public static final BitSet FOLLOW_Identifier_in_destTable1820 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_Identifier_in_tableName1859 = new BitSet(
			new long[] { 0x0000000000020000L });
	public static final BitSet FOLLOW_DOT_in_tableName1861 = new BitSet(
			new long[] { 0x0000000001000000L });
	public static final BitSet FOLLOW_Identifier_in_tableName1867 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_WITH_in_keyExpr1905 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_keyExpr1907 = new BitSet(
			new long[] { 0x0000004000000000L });
	public static final BitSet FOLLOW_KW_AS_in_keyExpr1909 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000800L });
	public static final BitSet FOLLOW_KW_KEY_in_keyExpr1911 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_WITH_in_attrsExpr1944 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_attrsExpr1946 = new BitSet(
			new long[] { 0x0000004000000000L });
	public static final BitSet FOLLOW_KW_AS_in_attrsExpr1948 = new BitSet(
			new long[] { 0x0000010000000000L });
	public static final BitSet FOLLOW_KW_ATTRIBUTES_in_attrsExpr1950 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_SELECT_in_selectClause1983 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040080000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000781812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_KW_DISTINCT_in_selectClause1988 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000781812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_selectList_in_selectClause1992 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_selectItem_in_selectList2045 = new BitSet(
			new long[] { 0x0000000000000402L });
	public static final BitSet FOLLOW_COMMA_in_selectList2048 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000781812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_selectItem_in_selectList2050 = new BitSet(
			new long[] { 0x0000000000000402L });
	public static final BitSet FOLLOW_selectExpression_in_selectItem2076 = new BitSet(
			new long[] { 0x0000004001000002L, 0x0000000100000000L });
	public static final BitSet FOLLOW_KW_AS_in_selectItem2079 = new BitSet(
			new long[] { 0x0000000001000000L });
	public static final BitSet FOLLOW_Identifier_in_selectItem2082 = new BitSet(
			new long[] { 0x0000000000000002L, 0x0000000100000000L });
	public static final BitSet FOLLOW_KW_EXPAND_in_selectItem2086 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_expression_in_selectExpression2126 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_tableAllColumns_in_selectExpression2132 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_STAR_in_tableAllColumns2147 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_tableName_in_tableAllColumns2169 = new BitSet(
			new long[] { 0x0000000000020000L });
	public static final BitSet FOLLOW_DOT_in_tableAllColumns2171 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000080000L });
	public static final BitSet FOLLOW_STAR_in_tableAllColumns2173 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_Identifier_in_tableOrColumn2206 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_IdentifierRef_in_tableOrColumn2230 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_precedenceOrExpression_in_expression2263 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_precedenceAndExpression_in_precedenceOrExpression2278 = new BitSet(
			new long[] { 0x0000000000000002L, 0x0000000000000000L,
					0x0000002000000000L });
	public static final BitSet FOLLOW_precedenceOrOperator_in_precedenceOrExpression2281 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_precedenceAndExpression_in_precedenceOrExpression2284 = new BitSet(
			new long[] { 0x0000000000000002L, 0x0000000000000000L,
					0x0000002000000000L });
	public static final BitSet FOLLOW_precedenceNotExpression_in_precedenceAndExpression2301 = new BitSet(
			new long[] { 0x0000000800000002L });
	public static final BitSet FOLLOW_precedenceAndOperator_in_precedenceAndExpression2304 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_precedenceNotExpression_in_precedenceAndExpression2307 = new BitSet(
			new long[] { 0x0000000800000002L });
	public static final BitSet FOLLOW_precedenceNotOperator_in_precedenceNotExpression2325 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_precedenceEqualExpression_in_precedenceNotExpression2330 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_precedenceBitwiseOrExpression_in_precedenceEqualExpression2345 = new BitSet(
			new long[] { 0x0000000000680002L, 0x1000000000000000L,
					0x4000000040010000L, 0x8000000000000020L,
					0x0000000000000041L });
	public static final BitSet FOLLOW_precedenceEqualOperator_in_precedenceEqualExpression2355 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000102000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_precedenceBitwiseOrExpression_in_precedenceEqualExpression2358 = new BitSet(
			new long[] { 0x0000000000680002L, 0x1000000000000000L,
					0x4000000040010000L, 0x8000000000000020L,
					0x0000000000000041L });
	public static final BitSet FOLLOW_precedenceInNotInOperator_in_precedenceEqualExpression2366 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000002L });
	public static final BitSet FOLLOW_expressions_in_precedenceEqualExpression2369 = new BitSet(
			new long[] { 0x0000000000680002L, 0x1000000000000000L,
					0x4000000040010000L, 0x8000000000000020L,
					0x0000000000000041L });
	public static final BitSet FOLLOW_precedenceAmpersandExpression_in_precedenceBitwiseOrExpression2389 = new BitSet(
			new long[] { 0x0000000000000042L });
	public static final BitSet FOLLOW_precedenceBitwiseOrOperator_in_precedenceBitwiseOrExpression2392 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000102000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_precedenceAmpersandExpression_in_precedenceBitwiseOrExpression2395 = new BitSet(
			new long[] { 0x0000000000000042L });
	public static final BitSet FOLLOW_precedencePlusExpression_in_precedenceAmpersandExpression2412 = new BitSet(
			new long[] { 0x0000000000000012L });
	public static final BitSet FOLLOW_precedenceAmpersandOperator_in_precedenceAmpersandExpression2415 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000102000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_precedencePlusExpression_in_precedenceAmpersandExpression2418 = new BitSet(
			new long[] { 0x0000000000000012L });
	public static final BitSet FOLLOW_precedenceStarExpression_in_precedencePlusExpression2435 = new BitSet(
			new long[] { 0x0000000000000002L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000001010L });
	public static final BitSet FOLLOW_precedencePlusOperator_in_precedencePlusExpression2438 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000102000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_precedenceStarExpression_in_precedencePlusExpression2441 = new BitSet(
			new long[] { 0x0000000000000002L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000001010L });
	public static final BitSet FOLLOW_precedenceBitwiseXorExpression_in_precedenceStarExpression2458 = new BitSet(
			new long[] { 0x000000000000C002L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000080020L });
	public static final BitSet FOLLOW_precedenceStarOperator_in_precedenceStarExpression2461 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000102000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_precedenceBitwiseXorExpression_in_precedenceStarExpression2464 = new BitSet(
			new long[] { 0x000000000000C002L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000080020L });
	public static final BitSet FOLLOW_precedenceUnarySuffixExpression_in_precedenceBitwiseXorExpression2481 = new BitSet(
			new long[] { 0x0000000000000082L });
	public static final BitSet FOLLOW_precedenceBitwiseXorOperator_in_precedenceBitwiseXorExpression2484 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000102000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_precedenceUnarySuffixExpression_in_precedenceBitwiseXorExpression2487 = new BitSet(
			new long[] { 0x0000000000000082L });
	public static final BitSet FOLLOW_precedenceUnaryPrefixExpression_in_precedenceUnarySuffixExpression2504 = new BitSet(
			new long[] { 0x0000000000000002L, 0x0000000000000000L,
					0x0000000000000100L });
	public static final BitSet FOLLOW_KW_IS_in_precedenceUnarySuffixExpression2509 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000140000000L });
	public static final BitSet FOLLOW_nullCondition_in_precedenceUnarySuffixExpression2511 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_precedenceUnaryOperator_in_precedenceUnaryPrefixExpression2559 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000102000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_precedenceFieldExpression_in_precedenceUnaryPrefixExpression2564 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_atomExpression_in_precedenceFieldExpression2579 = new BitSet(
			new long[] { 0x0000000000020002L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000004L });
	public static final BitSet FOLLOW_LSQUARE_in_precedenceFieldExpression2590 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_precedenceFieldExpression2593 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000010000L });
	public static final BitSet FOLLOW_RSQUARE_in_precedenceFieldExpression2595 = new BitSet(
			new long[] { 0x0000000000020002L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000004L });
	public static final BitSet FOLLOW_DOT_in_precedenceFieldExpression2606 = new BitSet(
			new long[] { 0x0000000001000000L });
	public static final BitSet FOLLOW_Identifier_in_precedenceFieldExpression2609 = new BitSet(
			new long[] { 0x0000000000020002L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000004L });
	public static final BitSet FOLLOW_KW_NULL_in_atomExpression2630 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_constant_in_atomExpression2644 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_function_in_atomExpression2650 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_foreach_in_atomExpression2656 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_execute_in_atomExpression2662 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_castExpression_in_atomExpression2668 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_caseExpression_in_atomExpression2674 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_whenExpression_in_atomExpression2680 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_tableOrColumn_in_atomExpression2686 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_AGGR_TIME_in_atomExpression2692 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_ATTRIBUTES_in_atomExpression2698 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_LPAREN_in_atomExpression2704 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_atomExpression2707 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000008000L });
	public static final BitSet FOLLOW_RPAREN_in_atomExpression2709 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_Number_in_constant2725 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_StringLiteral_in_constant2731 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_stringLiteralSequence_in_constant2737 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_BigintLiteral_in_constant2743 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_SmallintLiteral_in_constant2749 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_TinyintLiteral_in_constant2755 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_charSetStringLiteral_in_constant2761 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_booleanValue_in_constant2767 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_StringLiteral_in_stringLiteralSequence2782 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000200000L });
	public static final BitSet FOLLOW_StringLiteral_in_stringLiteralSequence2784 = new BitSet(
			new long[] { 0x0000000000000002L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000200000L });
	public static final BitSet FOLLOW_CharSetName_in_charSetStringLiteral2823 = new BitSet(
			new long[] { 0x0000000000001000L });
	public static final BitSet FOLLOW_CharSetLiteral_in_charSetStringLiteral2827 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_TRUE_in_booleanValue2864 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_FALSE_in_booleanValue2871 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_functionName_in_function2887 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000002L });
	public static final BitSet FOLLOW_LPAREN_in_function2889 = new BitSet(
			new long[] { 0x0018012087002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0008880018000000L,
					0x0000000000789812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_STAR_in_function2902 = new BitSet(
			new long[] { 0x0000000004000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0008000010000000L,
					0x0000000000008000L });
	public static final BitSet FOLLOW_expression_in_function2917 = new BitSet(
			new long[] { 0x0000000004000400L, 0x0000000000000000L,
					0x0000000000000000L, 0x0008000010000000L,
					0x0000000000008000L });
	public static final BitSet FOLLOW_COMMA_in_function2920 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_function2922 = new BitSet(
			new long[] { 0x0000000004000400L, 0x0000000000000000L,
					0x0000000000000000L, 0x0008000010000000L,
					0x0000000000008000L });
	public static final BitSet FOLLOW_KW_ACCU_in_function2943 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000008000L });
	public static final BitSet FOLLOW_KW_SW_in_function2955 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000008000L });
	public static final BitSet FOLLOW_KW_UPDATE_in_function2967 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000008000L });
	public static final BitSet FOLLOW_RPAREN_in_function2977 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_FOREACH_in_foreach3182 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000002L });
	public static final BitSet FOLLOW_LPAREN_in_foreach3184 = new BitSet(
			new long[] { 0x0000000001000000L });
	public static final BitSet FOLLOW_indexExpr_in_foreach3186 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0018000000000000L });
	public static final BitSet FOLLOW_generateExpr_in_foreach3188 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000008000L });
	public static final BitSet FOLLOW_RPAREN_in_foreach3190 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_Identifier_in_indexExpr3225 = new BitSet(
			new long[] { 0x0000000000000000L, 0x1000000000000000L,
					0x0000000200000000L });
	public static final BitSet FOLLOW_KW_OF_in_indexExpr3238 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_KW_IN_in_indexExpr3250 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_indexExpr3261 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_GENERATE_in_generateExpr3320 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_generateExpr3322 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_GENERATEMAP_in_generateExpr3346 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_generateExpr3350 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_generateExpr3354 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_EXECUTE_in_execute3391 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000002L });
	public static final BitSet FOLLOW_LPAREN_in_execute3393 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000001000L });
	public static final BitSet FOLLOW_defineVars_in_execute3395 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x4000000000000000L });
	public static final BitSet FOLLOW_executeBlock_in_execute3397 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000002000000L });
	public static final BitSet FOLLOW_emitValue_in_execute3399 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000008000L });
	public static final BitSet FOLLOW_RPAREN_in_execute3401 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_DEFINE_in_defineVars3438 = new BitSet(
			new long[] { 0x0000000001000000L });
	public static final BitSet FOLLOW_var_in_defineVars3440 = new BitSet(
			new long[] { 0x0000000000000402L });
	public static final BitSet FOLLOW_COMMA_in_defineVars3443 = new BitSet(
			new long[] { 0x0000000001000000L });
	public static final BitSet FOLLOW_var_in_defineVars3445 = new BitSet(
			new long[] { 0x0000000000000402L });
	public static final BitSet FOLLOW_Identifier_in_var3483 = new BitSet(
			new long[] { 0x0000386000000000L, 0x0000040000200180L,
					0x0000000002000010L, 0x000040600C080000L });
	public static final BitSet FOLLOW_KW_AS_in_var3485 = new BitSet(new long[] {
			0x0000382000000000L, 0x0000040000200180L, 0x0000000002000010L,
			0x000040600C080000L });
	public static final BitSet FOLLOW_dataType_in_var3490 = new BitSet(
			new long[] { 0x0000000000000002L, 0x0000000000000400L });
	public static final BitSet FOLLOW_KW_DEFAULT_in_var3493 = new BitSet(
			new long[] { 0x0000000000002100L, 0x0000002000000000L,
					0x0000000000000000L, 0x0000080000000000L,
					0x0000000000300800L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_constant_in_var3495 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_primitiveTypeDef_in_dataType3537 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_mapType_in_dataType3543 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_arrayType_in_dataType3549 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_structType_in_dataType3555 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_unionType_in_dataType3561 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_MAP_in_mapType3576 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x8000000000000000L });
	public static final BitSet FOLLOW_LESSTHAN_in_mapType3578 = new BitSet(
			new long[] { 0x0000382000000000L, 0x0000040000200180L,
					0x0000000002000010L, 0x000040600C080000L });
	public static final BitSet FOLLOW_dataType_in_mapType3582 = new BitSet(
			new long[] { 0x0000000000000400L });
	public static final BitSet FOLLOW_COMMA_in_mapType3584 = new BitSet(
			new long[] { 0x0000382000000000L, 0x0000040000200180L,
					0x0000000002000010L, 0x000040600C080000L });
	public static final BitSet FOLLOW_dataType_in_mapType3588 = new BitSet(
			new long[] { 0x0000000000200000L });
	public static final BitSet FOLLOW_GREATERTHAN_in_mapType3590 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_ARRAY_in_arrayType3627 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x8000000000000000L });
	public static final BitSet FOLLOW_LESSTHAN_in_arrayType3629 = new BitSet(
			new long[] { 0x0000382000000000L, 0x0000040000200180L,
					0x0000000002000010L, 0x000040600C080000L });
	public static final BitSet FOLLOW_dataType_in_arrayType3631 = new BitSet(
			new long[] { 0x0000000000200000L });
	public static final BitSet FOLLOW_GREATERTHAN_in_arrayType3633 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_STRUCT_in_structType3666 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x8000000000000000L });
	public static final BitSet FOLLOW_LESSTHAN_in_structType3668 = new BitSet(
			new long[] { 0x0000000001000000L });
	public static final BitSet FOLLOW_structUnit_in_structType3670 = new BitSet(
			new long[] { 0x0000000000200400L });
	public static final BitSet FOLLOW_COMMA_in_structType3673 = new BitSet(
			new long[] { 0x0000000001000000L });
	public static final BitSet FOLLOW_structUnit_in_structType3675 = new BitSet(
			new long[] { 0x0000000000200400L });
	public static final BitSet FOLLOW_GREATERTHAN_in_structType3679 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_Identifier_in_structUnit3713 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000040000L });
	public static final BitSet FOLLOW_SEMICOLON_in_structUnit3715 = new BitSet(
			new long[] { 0x0000382000000000L, 0x0000040000200180L,
					0x0000000002000010L, 0x000040600C080000L });
	public static final BitSet FOLLOW_dataType_in_structUnit3717 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_UNION_in_unionType3752 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x8000000000000000L });
	public static final BitSet FOLLOW_LESSTHAN_in_unionType3754 = new BitSet(
			new long[] { 0x0000382000000000L, 0x0000040000200180L,
					0x0000000002000010L, 0x000040600C080000L });
	public static final BitSet FOLLOW_dataType_in_unionType3756 = new BitSet(
			new long[] { 0x0000000000200400L });
	public static final BitSet FOLLOW_COMMA_in_unionType3759 = new BitSet(
			new long[] { 0x0000382000000000L, 0x0000040000200180L,
					0x0000000002000010L, 0x000040600C080000L });
	public static final BitSet FOLLOW_dataType_in_unionType3761 = new BitSet(
			new long[] { 0x0000000000200400L });
	public static final BitSet FOLLOW_GREATERTHAN_in_unionType3765 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_LCURLY_in_executeBlock3801 = new BitSet(
			new long[] { 0x0000000003000000L, 0x0400080000000000L });
	public static final BitSet FOLLOW_block_in_executeBlock3803 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000044000L });
	public static final BitSet FOLLOW_SEMICOLON_in_executeBlock3806 = new BitSet(
			new long[] { 0x0000000003000000L, 0x0400080000000000L });
	public static final BitSet FOLLOW_block_in_executeBlock3808 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000044000L });
	public static final BitSet FOLLOW_SEMICOLON_in_executeBlock3812 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000004000L });
	public static final BitSet FOLLOW_RCURLY_in_executeBlock3815 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_Identifier_in_assignable3860 = new BitSet(
			new long[] { 0x0000000000000020L });
	public static final BitSet FOLLOW_IdentifierRef_in_assignable3868 = new BitSet(
			new long[] { 0x0000000000000020L });
	public static final BitSet FOLLOW_ASSIGN_in_assignable3876 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_assignable3878 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_assignable_in_block3914 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_ifblock_in_block3920 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_forblock_in_block3926 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_IF_in_ifblock3941 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000002L });
	public static final BitSet FOLLOW_LPAREN_in_ifblock3943 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_searchCondition_in_ifblock3947 = new BitSet(
			new long[] { 0x0000000003000000L, 0x0400080000000000L });
	public static final BitSet FOLLOW_block_in_ifblock3949 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000048000L });
	public static final BitSet FOLLOW_SEMICOLON_in_ifblock3952 = new BitSet(
			new long[] { 0x0000000003000000L, 0x0400080000000000L });
	public static final BitSet FOLLOW_block_in_ifblock3954 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000048000L });
	public static final BitSet FOLLOW_SEMICOLON_in_ifblock3958 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000008000L });
	public static final BitSet FOLLOW_RPAREN_in_ifblock3961 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_FOR_in_forblock3998 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000002L });
	public static final BitSet FOLLOW_LPAREN_in_forblock4000 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_searchCondition_in_forblock4004 = new BitSet(
			new long[] { 0x0000000003000000L, 0x0400080000000000L });
	public static final BitSet FOLLOW_block_in_forblock4006 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000048000L });
	public static final BitSet FOLLOW_SEMICOLON_in_forblock4009 = new BitSet(
			new long[] { 0x0000000003000000L, 0x0400080000000000L });
	public static final BitSet FOLLOW_block_in_forblock4011 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000048000L });
	public static final BitSet FOLLOW_SEMICOLON_in_forblock4015 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000008000L });
	public static final BitSet FOLLOW_RPAREN_in_forblock4018 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_EMIT_in_emitValue4055 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_emitValue4057 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_CAST_in_castExpression4138 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000002L });
	public static final BitSet FOLLOW_LPAREN_in_castExpression4140 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_castExpression4142 = new BitSet(
			new long[] { 0x0000004000000000L });
	public static final BitSet FOLLOW_KW_AS_in_castExpression4144 = new BitSet(
			new long[] { 0x0000380000000000L, 0x0000040000200180L,
					0x0000000000000010L, 0x0000006004080000L });
	public static final BitSet FOLLOW_primitiveType_in_castExpression4146 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000008000L });
	public static final BitSet FOLLOW_RPAREN_in_castExpression4148 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_TINYINT_in_primitiveType4183 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_SMALLINT_in_primitiveType4197 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_INT_in_primitiveType4211 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_BIGINT_in_primitiveType4225 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_BOOLEAN_in_primitiveType4239 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_FLOAT_in_primitiveType4253 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_DOUBLE_in_primitiveType4267 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_DATE_in_primitiveType4281 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_DATETIME_in_primitiveType4295 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_TIMESTAMP_in_primitiveType4309 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_STRING_in_primitiveType4323 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_BINARY_in_primitiveType4337 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_CASE_in_caseExpression4441 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_caseExpression4443 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0400000000000000L });
	public static final BitSet FOLLOW_KW_WHEN_in_caseExpression4446 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_caseExpression4448 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000001000000000L });
	public static final BitSet FOLLOW_KW_THEN_in_caseExpression4450 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_caseExpression4452 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000009000000L,
					0x0000000000000000L, 0x0400000000000000L });
	public static final BitSet FOLLOW_KW_ELSE_in_caseExpression4457 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_caseExpression4459 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000008000000L });
	public static final BitSet FOLLOW_KW_END_in_caseExpression4463 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_CASE_in_whenExpression4499 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0400000000000000L });
	public static final BitSet FOLLOW_KW_WHEN_in_whenExpression4502 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_whenExpression4504 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000001000000000L });
	public static final BitSet FOLLOW_KW_THEN_in_whenExpression4506 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_whenExpression4508 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000009000000L,
					0x0000000000000000L, 0x0400000000000000L });
	public static final BitSet FOLLOW_KW_ELSE_in_whenExpression4513 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_whenExpression4515 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000008000000L });
	public static final BitSet FOLLOW_KW_END_in_whenExpression4519 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_LPAREN_in_expressions4555 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_expressions4557 = new BitSet(
			new long[] { 0x0000000000000400L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000008000L });
	public static final BitSet FOLLOW_COMMA_in_expressions4560 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_expressions4562 = new BitSet(
			new long[] { 0x0000000000000400L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000008000L });
	public static final BitSet FOLLOW_RPAREN_in_expressions4566 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_OR_in_precedenceOrOperator4590 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_AND_in_precedenceAndOperator4605 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_NOT_in_precedenceNotOperator4620 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_IN_in_precedenceInNotInOperator4635 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_NOT_in_precedenceInNotInOperator4641 = new BitSet(
			new long[] { 0x0000000000000000L, 0x1000000000000000L });
	public static final BitSet FOLLOW_KW_IN_in_precedenceInNotInOperator4643 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_EQUAL_in_precedenceEqualOperator4666 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_NOTEQUAL_in_precedenceEqualOperator4672 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_LESSTHANOREQUALTO_in_precedenceEqualOperator4678 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_LESSTHAN_in_precedenceEqualOperator4684 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_GREATERTHANOREQUALTO_in_precedenceEqualOperator4690 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_GREATERTHAN_in_precedenceEqualOperator4696 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_LIKE_in_precedenceEqualOperator4702 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_RLIKE_in_precedenceEqualOperator4708 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_REGEXP_in_precedenceEqualOperator4714 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_NOT_in_precedenceEqualOperator4720 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000010000L });
	public static final BitSet FOLLOW_KW_LIKE_in_precedenceEqualOperator4722 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_NOT_in_precedenceEqualOperator4736 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000020L });
	public static final BitSet FOLLOW_KW_RLIKE_in_precedenceEqualOperator4738 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_NOT_in_precedenceEqualOperator4752 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x4000000000000000L });
	public static final BitSet FOLLOW_KW_REGEXP_in_precedenceEqualOperator4754 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_BITWISEOR_in_precedenceBitwiseOrOperator4777 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_AMPERSAND_in_precedenceAmpersandOperator4792 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_BITWISEXOR_in_precedenceBitwiseXorOperator4861 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_NULL_in_nullCondition4903 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_NOT_in_nullCondition4925 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000100000000L });
	public static final BitSet FOLLOW_KW_NULL_in_nullCondition4927 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_FROM_in_fromClause4958 = new BitSet(
			new long[] { 0x0000000001000000L, 0x8000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000002L });
	public static final BitSet FOLLOW_joinSource_in_fromClause4960 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_UNION_in_unionOperator4993 = new BitSet(
			new long[] { 0x0000000100000000L });
	public static final BitSet FOLLOW_KW_ALL_in_unionOperator4995 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_fromSource_in_joinSource5026 = new BitSet(
			new long[] { 0x0000000000000002L, 0x0000000000000000L,
					0x0000000000008000L });
	public static final BitSet FOLLOW_joinToken_in_joinSource5029 = new BitSet(
			new long[] { 0x0000000001000000L, 0x8000000000000000L });
	public static final BitSet FOLLOW_joinRithtSource_in_joinSource5032 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000800000000L });
	public static final BitSet FOLLOW_onClause_in_joinSource5034 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_ON_in_onClause5060 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_expression_in_onClause5062 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_tableSource_in_joinRithtSource5095 = new BitSet(
			new long[] { 0x0000000000000402L });
	public static final BitSet FOLLOW_COMMA_in_joinRithtSource5098 = new BitSet(
			new long[] { 0x0000000001000000L, 0x8000000000000000L });
	public static final BitSet FOLLOW_tableSource_in_joinRithtSource5100 = new BitSet(
			new long[] { 0x0000000000000402L });
	public static final BitSet FOLLOW_KW_LEFT_in_joinToken5126 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000400L });
	public static final BitSet FOLLOW_KW_JOIN_in_joinToken5128 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_tableSource_in_fromSource5151 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_subQuerySource_in_fromSource5157 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_LPAREN_in_subQuerySource5172 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000400L });
	public static final BitSet FOLLOW_subQueryStatement_in_subQuerySource5174 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000008000L });
	public static final BitSet FOLLOW_RPAREN_in_subQuerySource5176 = new BitSet(
			new long[] { 0x0000000001000000L });
	public static final BitSet FOLLOW_Identifier_in_subQuerySource5178 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_selectFromWholeClause_in_subQueryStatement5213 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000400000000000L });
	public static final BitSet FOLLOW_unionOperator_in_subQueryStatement5216 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000400L });
	public static final BitSet FOLLOW_selectFromWholeClause_in_subQueryStatement5218 = new BitSet(
			new long[] { 0x0000000000000002L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000400000000000L });
	public static final BitSet FOLLOW_selectFromWholeClause_in_subQueryStatement5274 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_selectClause_in_selectFromWholeClause5289 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000800000000000L });
	public static final BitSet FOLLOW_fromClause_in_selectFromWholeClause5291 = new BitSet(
			new long[] { 0x0000000000000002L, 0x00C0000000000000L,
					0x0000000000000000L, 0x0800000000000000L });
	public static final BitSet FOLLOW_whereClause_in_selectFromWholeClause5293 = new BitSet(
			new long[] { 0x0000000000000002L, 0x00C0000000000000L });
	public static final BitSet FOLLOW_groupByClause_in_selectFromWholeClause5296 = new BitSet(
			new long[] { 0x0000000000000002L, 0x0080000000000000L });
	public static final BitSet FOLLOW_havingClause_in_selectFromWholeClause5299 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_INNERTABLE_in_tableSource5461 = new BitSet(
			new long[] { 0x0000000001000002L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000002L });
	public static final BitSet FOLLOW_LPAREN_in_tableSource5464 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000800L });
	public static final BitSet FOLLOW_Number_in_tableSource5468 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000008000L });
	public static final BitSet FOLLOW_RPAREN_in_tableSource5470 = new BitSet(
			new long[] { 0x0000000001000002L });
	public static final BitSet FOLLOW_Identifier_in_tableSource5476 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_tableName_in_tableSource5546 = new BitSet(
			new long[] { 0x0000000001000002L });
	public static final BitSet FOLLOW_Identifier_in_tableSource5551 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_WHERE_in_whereClause5591 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_searchCondition_in_whereClause5593 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_expression_in_searchCondition5626 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_GROUP_in_groupByClause5641 = new BitSet(
			new long[] { 0x0002000000000000L });
	public static final BitSet FOLLOW_KW_BY_in_groupByClause5643 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_groupByExpression_in_groupByClause5645 = new BitSet(
			new long[] { 0x0000000008000400L, 0x0000000000000001L,
					0x0000000000000000L, 0x2000000000000000L });
	public static final BitSet FOLLOW_COMMA_in_groupByClause5648 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_groupByExpression_in_groupByClause5650 = new BitSet(
			new long[] { 0x0000000008000400L, 0x0000000000000001L,
					0x0000000000000000L, 0x2000000000000000L });
	public static final BitSet FOLLOW_coordinateInfo_in_groupByClause5654 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_COORDINATE_in_coordinateInfo5691 = new BitSet(
			new long[] { 0x0002000000000000L });
	public static final BitSet FOLLOW_KW_BY_in_coordinateInfo5693 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_coordinateExpr_in_coordinateInfo5695 = new BitSet(
			new long[] { 0x0000000008000002L, 0x0000000000000000L,
					0x0000000000000000L, 0x2000000000000000L });
	public static final BitSet FOLLOW_aggrInterval_in_coordinateInfo5700 = new BitSet(
			new long[] { 0x0000000008000002L, 0x0000000000000000L,
					0x0000000000000000L, 0x2000000000000000L });
	public static final BitSet FOLLOW_accuInterval_in_coordinateInfo5705 = new BitSet(
			new long[] { 0x0000000008000002L, 0x0000000000000000L,
					0x0000000000000000L, 0x2000000000000000L });
	public static final BitSet FOLLOW_swInterval_in_coordinateInfo5710 = new BitSet(
			new long[] { 0x0000000008000002L });
	public static final BitSet FOLLOW_KW_ACCUGLOBAL_in_coordinateInfo5714 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_expression_in_coordinateExpr5784 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_WITH_in_aggrInterval5817 = new BitSet(
			new long[] { 0x0000000040000000L });
	public static final BitSet FOLLOW_KW_AGGR_in_aggrInterval5819 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000040L });
	public static final BitSet FOLLOW_KW_INTERVAL_in_aggrInterval5821 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000800L });
	public static final BitSet FOLLOW_Number_in_aggrInterval5823 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000200L });
	public static final BitSet FOLLOW_KW_SECONDS_in_aggrInterval5825 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_WITH_in_accuInterval5858 = new BitSet(
			new long[] { 0x0000000004000000L });
	public static final BitSet FOLLOW_KW_ACCU_in_accuInterval5860 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000040L });
	public static final BitSet FOLLOW_KW_INTERVAL_in_accuInterval5862 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000800L });
	public static final BitSet FOLLOW_Number_in_accuInterval5864 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000200L });
	public static final BitSet FOLLOW_KW_SECONDS_in_accuInterval5866 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_WITH_in_swInterval5899 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000010000000L });
	public static final BitSet FOLLOW_KW_SW_in_swInterval5901 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000040L });
	public static final BitSet FOLLOW_KW_INTERVAL_in_swInterval5903 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000800L });
	public static final BitSet FOLLOW_Number_in_swInterval5905 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000200L });
	public static final BitSet FOLLOW_KW_SECONDS_in_swInterval5907 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_expression_in_groupByExpression5940 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_KW_HAVING_in_havingClause5955 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000142000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_havingCondition_in_havingClause5957 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_expression_in_havingCondition5990 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_keyExpr_in_synpred15_Trc1767 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_precedencePlusOperator_in_synpred35_Trc2438 = new BitSet(
			new long[] { 0x0018012083002100L, 0x0400102040000000L,
					0x0000000102000000L, 0x0000880008000000L,
					0x0000000000701812L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000000L,
					0x0000000040000000L });
	public static final BitSet FOLLOW_precedenceStarExpression_in_synpred35_Trc2441 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_function_in_synpred44_Trc2650 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_caseExpression_in_synpred48_Trc2674 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_whenExpression_in_synpred49_Trc2680 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_tableOrColumn_in_synpred50_Trc2686 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_StringLiteral_in_synpred54_Trc2731 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_stringLiteralSequence_in_synpred55_Trc2737 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_StringLiteral_in_synpred60_Trc2784 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_selectFromWholeClause_in_synpred143_Trc5213 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000400000000000L });
	public static final BitSet FOLLOW_unionOperator_in_synpred143_Trc5216 = new BitSet(
			new long[] { 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000400L });
	public static final BitSet FOLLOW_selectFromWholeClause_in_synpred143_Trc5218 = new BitSet(
			new long[] { 0x0000000000000002L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000400000000000L });
}
