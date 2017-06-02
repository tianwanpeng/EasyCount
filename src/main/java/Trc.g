grammar Trc;

options {
  language     = Java;
  output       = AST;
  ASTLabelType = CommonTree;
  backtrack    = true;
}

tokens {
  TOK_INSERT;
  TOK_ROOT;
  TOK_ROOT_QUERY;
  TOK_QUERY;
  TOK_INSERT_QUERY;
  TOK_WITH;
  TOK_KEY;
  TOK_ATTRBUTES;
  TOK_SELECT;
  TOK_SELECTDI;
  TOK_SELEXPR;
  TOK_FROM;
  TOK_TAB;
  TOK_PARTSPEC;
  TOK_PARTVAL;
  TOK_DIR;
  TOK_LOCAL_DIR;
  TOK_TABREF;
  TOK_SUBQUERY;
  TOK_INSERT_INTO;
  TOK_DESTINATION;
  TOK_ALLCOLREF;
  TOK_TABLE_OR_COL;
  TOK_TABLE_OR_COL_REF;
  TOK_FOREACH;
  TOK_GENERATE;
  TOK_GENERATEMAP;
  TOK_OF;
  TOK_IN;
  TOK_EXECUTE;
  TOK_EXECUTEBLOCK;
  TOK_ASSIGN;
  TOK_IF;
  TOK_FOR;
  TOK_EMIT;
  TOK_DEFINE;
  TOK_VAR;
  TOK_FUNCTION;
  TOK_FUNCTIONDI;
  TOK_FUNCTIONSTAR;
  TOK_FUNCTIONACCU;
  TOK_FUNCTIONACCUDI;
  TOK_FUNCTIONSW;
  TOK_FUNCTIONUPDATE;
  TOK_FUNCTIONSWDI;
  TOK_COORDINATE;
  TOK_COORDINATE_EXPR;
  TOK_AGGR_INTERVAL;
  TOK_ACCU_INTERVAL;
  TOK_SW_INTERVAL;
  TOK_WHERE;
  TOK_OP_EQ;
  TOK_OP_NE;
  TOK_OP_LE;
  TOK_OP_LT;
  TOK_OP_GE;
  TOK_OP_GT;
  TOK_OP_DIV;
  TOK_OP_ADD;
  TOK_OP_SUB;
  TOK_OP_MUL;
  TOK_OP_MOD;
  TOK_OP_BITAND;
  TOK_OP_BITNOT;
  TOK_OP_BITOR;
  TOK_OP_BITXOR;
  TOK_OP_AND;
  TOK_OP_OR;
  TOK_OP_NOT;
  TOK_OP_LIKE;
  TOK_TRUE;
  TOK_FALSE;
  TOK_TRANSFORM;
  TOK_SERDE;
  TOK_SERDENAME;
  TOK_SERDEPROPS;
  TOK_EXPLIST;
  TOK_ALIASLIST;
  TOK_GROUPBY;
  TOK_HAVING;
  TOK_ORDERBY;
  TOK_CLUSTERBY;
  TOK_DISTRIBUTEBY;
  TOK_SORTBY;
  TOK_UNION;
  TOK_JOIN;
  TOK_LEFTOUTERJOIN;
  TOK_RIGHTOUTERJOIN;
  TOK_FULLOUTERJOIN;
  TOK_ON;
  TOK_UNIQUEJOIN;
  TOK_LOAD;
  TOK_EXPORT;
  TOK_IMPORT;
  TOK_NULL;
  TOK_ISNULL;
  TOK_ISNOTNULL;
  TOK_TINYINT;
  TOK_SMALLINT;
  TOK_INT;
  TOK_BIGINT;
  TOK_BOOLEAN;
  TOK_FLOAT;
  TOK_DOUBLE;
  TOK_DATE;
  TOK_DATETIME;
  TOK_TIMESTAMP;
  TOK_STRING;
  TOK_BINARY;
  TOK_ARRAY;
  TOK_STRUCT;
  TOK_STRUCTUNIT;
  TOK_MAP;
  TOK_UNIONTYPE;
  TOK_COLTYPELIST;
  TOK_CREATEDATABASE;
  TOK_CREATETABLE;
  TOK_CREATEINDEX;
  TOK_CREATEINDEX_INDEXTBLNAME;
  TOK_DEFERRED_REBUILDINDEX;
  TOK_DROPINDEX;
  TOK_LIKETABLE;
  TOK_DESCTABLE;
  TOK_DESCFUNCTION;
  TOK_ALTERTABLE_PARTITION;
  TOK_ALTERTABLE_RENAME;
  TOK_ALTERTABLE_ADDCOLS;
  TOK_ALTERTABLE_RENAMECOL;
  TOK_ALTERTABLE_RENAMEPART;
  TOK_ALTERTABLE_REPLACECOLS;
  TOK_ALTERTABLE_ADDPARTS;
  TOK_ALTERTABLE_DROPPARTS;
  TOK_ALTERTABLE_ALTERPARTS_PROTECTMODE;
  TOK_ALTERTABLE_TOUCH;
  TOK_ALTERTABLE_ARCHIVE;
  TOK_ALTERTABLE_UNARCHIVE;
  TOK_ALTERTABLE_SERDEPROPERTIES;
  TOK_ALTERTABLE_SERIALIZER;
  TOK_TABLE_PARTITION;
  TOK_ALTERTABLE_FILEFORMAT;
  TOK_ALTERTABLE_LOCATION;
  TOK_ALTERTABLE_PROPERTIES;
  TOK_ALTERTABLE_CHANGECOL_AFTER_POSITION;
  TOK_ALTERINDEX_REBUILD;
  TOK_ALTERINDEX_PROPERTIES;
  TOK_MSCK;
  TOK_SHOWDATABASES;
  TOK_SHOWTABLES;
  TOK_SHOWFUNCTIONS;
  TOK_SHOWPARTITIONS;
  TOK_SHOW_TABLESTATUS;
  TOK_SHOWLOCKS;
  TOK_LOCKTABLE;
  TOK_UNLOCKTABLE;
  TOK_SWITCHDATABASE;
  TOK_DROPDATABASE;
  TOK_DROPTABLE;
  TOK_DATABASECOMMENT;
  TOK_TABCOLLIST;
  TOK_TABCOL;
  TOK_TABLECOMMENT;
  TOK_TABLEPARTCOLS;
  TOK_TABLEBUCKETS;
  TOK_TABLEROWFORMAT;
  TOK_TABLEROWFORMATFIELD;
  TOK_TABLEROWFORMATCOLLITEMS;
  TOK_TABLEROWFORMATMAPKEYS;
  TOK_TABLEROWFORMATLINES;
  TOK_TBLSEQUENCEFILE;
  TOK_TBLTEXTFILE;
  TOK_TBLRCFILE;
  TOK_TABLEFILEFORMAT;
  TOK_FILEFORMAT_GENERIC;
  TOK_OFFLINE;
  TOK_ENABLE;
  TOK_DISABLE;
  TOK_READONLY;
  TOK_NO_DROP;
  TOK_STORAGEHANDLER;
  TOK_ALTERTABLE_CLUSTER_SORT;
  TOK_TABCOLNAME;
  TOK_TABLELOCATION;
  TOK_PARTITIONLOCATION;
  TOK_TABLEBUCKETSAMPLE;
  TOK_TABLESPLITSAMPLE;
  TOK_TMP_FILE;
  TOK_TABSORTCOLNAMEASC;
  TOK_TABSORTCOLNAMEDESC;
  TOK_STRINGLITERALSEQUENCE;
  TOK_CHARSETLITERAL;
  TOK_CREATEFUNCTION;
  TOK_DROPFUNCTION;
  TOK_CREATEVIEW;
  TOK_DROPVIEW;
  TOK_ALTERVIEW_PROPERTIES;
  TOK_ALTERVIEW_ADDPARTS;
  TOK_ALTERVIEW_DROPPARTS;
  TOK_ALTERVIEW_RENAME;
  TOK_VIEWPARTCOLS;
  TOK_EXPLAIN;
  TOK_TABLESERIALIZER;
  TOK_TABLEPROPERTIES;
  TOK_TABLEPROPLIST;
  TOK_INDEXPROPERTIES;
  TOK_INDEXPROPLIST;
  TOK_TABTYPE;
  TOK_LIMIT;
  TOK_TABLEPROPERTY;
  TOK_IFEXISTS;
  TOK_IFNOTEXISTS;
  TOK_ORREPLACE;
  TOK_HINTLIST;
  TOK_HINT;
  TOK_MAPJOIN;
  TOK_STREAMTABLE;
  TOK_HOLD_DDLTIME;
  TOK_HINTARGLIST;
  TOK_USERSCRIPTCOLNAMES;
  TOK_USERSCRIPTCOLSCHEMA;
  TOK_RECORDREADER;
  TOK_RECORDWRITER;
  TOK_LEFTSEMIJOIN;
  TOK_LATERAL_VIEW;
  TOK_TABALIAS;
  TOK_ANALYZE;
  TOK_CREATEROLE;
  TOK_DROPROLE;
  TOK_GRANT;
  TOK_REVOKE;
  TOK_SHOW_GRANT;
  TOK_PRIVILEGE_LIST;
  TOK_PRIVILEGE;
  TOK_PRINCIPAL_NAME;
  TOK_USER;
  TOK_GROUP;
  TOK_ROLE;
  TOK_GRANT_WITH_OPTION;
  TOK_PRIV_ALL;
  TOK_PRIV_ALTER_METADATA;
  TOK_PRIV_ALTER_DATA;
  TOK_PRIV_DROP;
  TOK_PRIV_INDEX;
  TOK_PRIV_LOCK;
  TOK_PRIV_SELECT;
  TOK_PRIV_SHOW_DATABASE;
  TOK_PRIV_CREATE;
  TOK_PRIV_OBJECT;
  TOK_PRIV_OBJECT_COL;
  TOK_GRANT_ROLE;
  TOK_REVOKE_ROLE;
  TOK_SHOW_ROLE_GRANT;
  TOK_SHOWINDEXES;
  TOK_INDEXCOMMENT;
  TOK_DESCDATABASE;
  TOK_DATABASEPROPERTIES;
  TOK_DATABASELOCATION;
  TOK_DBPROPLIST;
  TOK_ALTERDATABASE_PROPERTIES;
  TOK_ALTERTABLE_ALTERPARTS_MERGEFILES;
  TOK_TABNAME;
  TOK_TABSRC;
  TOK_RESTRICT;
  TOK_CASCADE;
}

@header {
package com.tencent.trc.parse;
}

@lexer::header {
package com.tencent.trc.parse;
}

statement
  :
  explainStatement EOF
  | execStatement EOF
  ;

explainStatement
  :
  KW_EXPLAIN
  (
    explainOptions=KW_EXTENDED
    | explainOptions=KW_FORMATTED
  )?
  execStatement
    ->
      ^(TOK_EXPLAIN execStatement $explainOptions?)
  ;

execStatement
  :
  execStatement1 execStatement2*
    ->
      ^(TOK_ROOT execStatement1 execStatement2*)
  ;

execStatement1
  :
  withClause? insertSelectFromWholeClause (COMMA? insertSelectFromWholeClause)*
    ->
      ^(TOK_ROOT_QUERY withClause? insertSelectFromWholeClause+)
  ;

execStatement2
  :
  withClause insertSelectFromWholeClause (COMMA? insertSelectFromWholeClause)*
    ->
      ^(TOK_ROOT_QUERY withClause insertSelectFromWholeClause+)
  ;

withClause
  :
  KW_WITH subQuerySource (COMMA? subQuerySource)*
    ->
      ^(TOK_WITH subQuerySource+)
  ;

insertSelectFromWholeClause
  :
  insertClause selectClause fromClause whereClause? groupByClause? havingClause?
    ->
      ^(
        TOK_INSERT_QUERY fromClause
        ^(TOK_INSERT insertClause selectClause whereClause? groupByClause? havingClause?)
       )
  ;

insertClause
  :
  KW_INSERT KW_INTO destTable keyExpr? attrsExpr?
    ->
      ^(TOK_DESTINATION destTable keyExpr? attrsExpr?)
  ;

destTable
  :
  tableName
  | KW_PRINTTABLE al=Identifier?
    ->
      ^(KW_PRINTTABLE $al?)
  ;

tableName
  :
  (db=Identifier DOT)? tab=Identifier
    ->
      ^(TOK_TABNAME $db? $tab)
  ;

keyExpr
  :
  KW_WITH expression KW_AS KW_KEY
    ->
      ^(TOK_KEY expression)
  ;

attrsExpr
  :
  KW_WITH expression KW_AS KW_ATTRIBUTES
    ->
      ^(TOK_ATTRBUTES expression)
  ;

selectClause
  :
  KW_SELECT (dist=KW_DISTINCT)? selectList
    -> {$dist == null}?
      ^(TOK_SELECT selectList)
    ->
      ^(TOK_SELECTDI selectList)
  ;

selectList
  :
  selectItem (COMMA selectItem)*
    -> selectItem+
  ;

selectItem
  :
  selectExpression (KW_AS? Identifier)? KW_EXPAND?
    ->
      ^(TOK_SELEXPR selectExpression Identifier? KW_EXPAND?)
  ;

selectExpression
  :
  expression
  | tableAllColumns
  ;

tableAllColumns
  :
  STAR
    ->
      ^(TOK_ALLCOLREF)
  | tableName DOT STAR
    ->
      ^(TOK_ALLCOLREF tableName)
  ;

tableOrColumn
  :
  Identifier
    ->
      ^(TOK_TABLE_OR_COL Identifier)
  | IdentifierRef
    ->
      ^(TOK_TABLE_OR_COL_REF IdentifierRef)
  ;

expression
  :
  precedenceOrExpression
  ;

precedenceOrExpression
  :
  precedenceAndExpression (precedenceOrOperator^ precedenceAndExpression)*
  ;

precedenceAndExpression
  :
  precedenceNotExpression (precedenceAndOperator^ precedenceNotExpression)*
  ;

precedenceNotExpression
  :
  (precedenceNotOperator^)* precedenceEqualExpression
  ;

precedenceEqualExpression
  :
  precedenceBitwiseOrExpression
  (
    precedenceEqualOperator^ precedenceBitwiseOrExpression
    | precedenceInNotInOperator^ expressions
  )*
  ;

precedenceBitwiseOrExpression
  :
  precedenceAmpersandExpression (precedenceBitwiseOrOperator^ precedenceAmpersandExpression)*
  ;

precedenceAmpersandExpression
  :
  precedencePlusExpression (precedenceAmpersandOperator^ precedencePlusExpression)*
  ;

precedencePlusExpression
  :
  precedenceStarExpression (precedencePlusOperator^ precedenceStarExpression)*
  ;

precedenceStarExpression
  :
  precedenceBitwiseXorExpression (precedenceStarOperator^ precedenceBitwiseXorExpression)*
  ;

precedenceBitwiseXorExpression
  :
  precedenceUnarySuffixExpression (precedenceBitwiseXorOperator^ precedenceUnarySuffixExpression)*
  ;

precedenceUnarySuffixExpression
  :
  precedenceUnaryPrefixExpression (a=KW_IS nullCondition)?
    -> {$a != null}?
      ^(TOK_FUNCTION nullCondition precedenceUnaryPrefixExpression)
    -> precedenceUnaryPrefixExpression
  ;

precedenceUnaryPrefixExpression
  :
  (precedenceUnaryOperator^)* precedenceFieldExpression
  ;

precedenceFieldExpression
  :
  atomExpression
  (
    (LSQUARE^ expression RSQUARE!)
    | (DOT^ Identifier)
  )*
  ;

atomExpression
  :
  KW_NULL
    -> TOK_NULL
  | constant
  | function
  | foreach
  | execute
  | castExpression
  | caseExpression
  | whenExpression
  | tableOrColumn
  | KW_AGGR_TIME
  | KW_ATTRIBUTES
  | LPAREN! expression RPAREN!
  ;

constant
  :
  Number
  | StringLiteral
  | stringLiteralSequence
  | BigintLiteral
  | SmallintLiteral
  | TinyintLiteral
  | charSetStringLiteral
  | booleanValue
  ;

stringLiteralSequence
  :
  StringLiteral StringLiteral+
    ->
      ^(TOK_STRINGLITERALSEQUENCE StringLiteral StringLiteral+)
  ;

charSetStringLiteral
  :
  csName=CharSetName csLiteral=CharSetLiteral
    ->
      ^(TOK_CHARSETLITERAL $csName $csLiteral)
  ;

booleanValue
  :
  KW_TRUE^
  | KW_FALSE^
  ;

function
  :
  functionName LPAREN
  (
    (star=STAR)
    // | (dist=KW_DISTINCT)? (expression (COMMA expression)*)?
    | (expression (COMMA expression)*)?
  )
  (
    (accu=KW_ACCU)
    | (sw=KW_SW)
    | (update=KW_UPDATE)
  )?
  RPAREN
    -> {$star != null}?
      ^(TOK_FUNCTIONSTAR functionName)
    -> {$accu != null}?
      ^(
        TOK_FUNCTIONACCU functionName (expression+)?
       )
    -> {$sw != null}?
      ^(
        TOK_FUNCTIONSW functionName (expression+)?
       )
    -> {$update != null}?
      ^(
        TOK_FUNCTIONUPDATE functionName (expression+)?
       )
    ->
      ^(
        TOK_FUNCTION functionName (expression+)?
       )
  ;

foreach
  :
  KW_FOREACH LPAREN indexExpr generateExpr RPAREN
    ->
      ^(TOK_FOREACH indexExpr generateExpr)
  ;

indexExpr
  :
  Identifier
  (
    (of=KW_OF)
    | (in=KW_IN)
  )
  list=expression
    -> {$of != null}?
      ^(TOK_OF Identifier $list)
    ->
      ^(TOK_IN Identifier $list)
  ;

generateExpr
  :
  KW_GENERATE expression
    ->
      ^(TOK_GENERATE expression)
  | KW_GENERATEMAP k=expression v=expression
    ->
      ^(TOK_GENERATEMAP $k $v)
  ;

execute
  :
  KW_EXECUTE LPAREN defineVars executeBlock emitValue RPAREN
    ->
      ^(TOK_EXECUTE defineVars executeBlock emitValue)
  ;

defineVars
  :
  KW_DEFINE var (COMMA var)*
    ->
      ^(TOK_DEFINE var+)
  ;

var
  :
  varname=Identifier KW_AS? vartype=dataType (KW_DEFAULT constant)?
    ->
      ^(TOK_VAR $varname $vartype constant?)
  ;

dataType
  :
  primitiveTypeDef
  | mapType
  | arrayType
  | structType
  | unionType
  ;

mapType
  :
  KW_MAP LESSTHAN kt=dataType COMMA vt=dataType GREATERTHAN
    ->
      ^(TOK_MAP $kt $vt)
  ;

arrayType
  :
  KW_ARRAY LESSTHAN dataType GREATERTHAN
    ->
      ^(TOK_ARRAY dataType)
  ;

structType
  :
  KW_STRUCT LESSTHAN structUnit (COMMA structUnit)* GREATERTHAN
    ->
      ^(TOK_STRUCT structUnit+)
  ;

structUnit
  :
  Identifier SEMICOLON dataType
    ->
      ^(TOK_STRUCTUNIT Identifier dataType)
  ;

unionType
  :
  KW_UNION LESSTHAN dataType (COMMA dataType)* GREATERTHAN
    ->
      ^(TOK_UNION Identifier dataType+)
  ;

executeBlock
  :
  LCURLY block (SEMICOLON block)* SEMICOLON? RCURLY
    ->
      ^(TOK_EXECUTEBLOCK block+)
  ;

assignable
  :
  v=
  (
    Identifier
    | IdentifierRef
  )
  ASSIGN expression
    ->
      ^(TOK_ASSIGN $v expression)
  ;

block
  :
  assignable
  | ifblock
  | forblock
  ;

ifblock
  :
  KW_IF LPAREN cond=searchCondition block (SEMICOLON block)* SEMICOLON? RPAREN
    ->
      ^(TOK_IF $cond block+)
  ;

forblock
  :
  KW_FOR LPAREN cond=searchCondition block (SEMICOLON block)* SEMICOLON? RPAREN
    ->
      ^(TOK_FOR $cond block+)
  ;

emitValue
  :
  KW_EMIT expression
    ->
      ^(TOK_EMIT expression)
  ;

functionName
  :
  // Keyword IF is also a function name
  Identifier
  | KW_IF
  | KW_ARRAY
  | KW_MAP
  | KW_STRUCT
  | KW_UNIONTYPE
  ;

castExpression
  :
  KW_CAST LPAREN expression KW_AS primitiveType RPAREN
    ->
      ^(TOK_FUNCTION primitiveType expression)
  ;

primitiveType
  :
  KW_TINYINT
    -> TOK_TINYINT
  | KW_SMALLINT
    -> TOK_SMALLINT
  | KW_INT
    -> TOK_INT
  | KW_BIGINT
    -> TOK_BIGINT
  | KW_BOOLEAN
    -> TOK_BOOLEAN
  | KW_FLOAT
    -> TOK_FLOAT
  | KW_DOUBLE
    -> TOK_DOUBLE
  | KW_DATE
    -> TOK_DATE
  | KW_DATETIME
    -> TOK_DATETIME
  | KW_TIMESTAMP
    -> TOK_TIMESTAMP
  | KW_STRING
    -> TOK_STRING
  | KW_BINARY
    -> TOK_BINARY
  ;

primitiveTypeDef
  :
  KW_TINYINT
  | KW_SMALLINT
  | KW_INT
  | KW_BIGINT
  | KW_BOOLEAN
  | KW_FLOAT
  | KW_DOUBLE
  | KW_DATE
  | KW_DATETIME
  | KW_TIMESTAMP
  | KW_STRING
  | KW_BINARY
  ;

caseExpression
  :
  KW_CASE expression (KW_WHEN expression KW_THEN expression)+ (KW_ELSE expression)? KW_END
    ->
      ^(TOK_FUNCTION KW_CASE expression*)
  ;

whenExpression
  :
  KW_CASE (KW_WHEN expression KW_THEN expression)+ (KW_ELSE expression)? KW_END
    ->
      ^(TOK_FUNCTION KW_WHEN expression*)
  ;

expressions
  :
  LPAREN expression (COMMA expression)* RPAREN
    -> expression*
  ;

precedenceOrOperator
  :
  KW_OR
  ;

precedenceAndOperator
  :
  KW_AND
  ;

precedenceNotOperator
  :
  KW_NOT
  ;

precedenceInNotInOperator
  :
  KW_IN
  | KW_NOT KW_IN
    -> NOTIN
  ;

precedenceEqualOperator
  :
  EQUAL
  | NOTEQUAL
  | LESSTHANOREQUALTO
  | LESSTHAN
  | GREATERTHANOREQUALTO
  | GREATERTHAN
  | KW_LIKE
  | KW_RLIKE
  | KW_REGEXP
  | KW_NOT KW_LIKE
    -> NOTLIKE
  | KW_NOT KW_RLIKE
    -> NOTRLIKE
  | KW_NOT KW_REGEXP
    -> NOTREGEXP
  ;

precedenceBitwiseOrOperator
  :
  BITWISEOR
  ;

precedenceAmpersandOperator
  :
  AMPERSAND
  ;

precedencePlusOperator
  :
  PLUS
  | MINUS
  ;

precedenceStarOperator
  :
  STAR
  | DIVIDE
  | MOD
  | DIV
  ;

precedenceBitwiseXorOperator
  :
  BITWISEXOR
  ;

precedenceUnaryOperator
  :
  PLUS
  | MINUS
  | TILDE
  ;

nullCondition
  :
  KW_NULL
    ->
      ^(TOK_ISNULL)
  | KW_NOT KW_NULL
    ->
      ^(TOK_ISNOTNULL)
  ;

fromClause
  :
  KW_FROM joinSource
    ->
      ^(TOK_FROM joinSource)
  ;

unionOperator
  :
  KW_UNION KW_ALL
    ->
      ^(TOK_UNION)
  ;

joinSource
  :
  fromSource (joinToken^ joinRithtSource onClause)?
  //    ->
  //      ^(joinToken fromSource tableSource+ ^(KW_ON expression))
  //  | fromSource
  ;

onClause
  :
  KW_ON expression
    ->
      ^(TOK_ON expression)
  ;

joinRithtSource
  :
  tableSource (COMMA tableSource)*
    -> tableSource+
  ;

joinToken
  :
  KW_LEFT KW_JOIN
    -> TOK_LEFTOUTERJOIN
  ;

fromSource
  :
  tableSource
  | subQuerySource
  ;

subQuerySource
  :
  LPAREN subQueryStatement RPAREN Identifier
    ->
      ^(TOK_SUBQUERY subQueryStatement Identifier)
  ;

subQueryStatement
  :
  selectFromWholeClause (unionOperator selectFromWholeClause)+
    ->
      ^(
        TOK_UNION
        ^(TOK_SUBQUERY selectFromWholeClause)+
       )
  | selectFromWholeClause
  ;

selectFromWholeClause
  :
  selectClause fromClause whereClause? groupByClause? havingClause?
    ->
      ^(
        TOK_QUERY fromClause
        ^(
          TOK_INSERT
          ^(
            TOK_DESTINATION
            ^(TOK_DIR TOK_TMP_FILE)
           )
          selectClause whereClause? groupByClause? havingClause?
         )
       )
  ;

tableSource
  :
  KW_INNERTABLE (LPAREN num=Number RPAREN)? al=Identifier?
    ->
      ^(
        TOK_TABREF
        ^(KW_INNERTABLE $num?)
        $al?
       )
  | tabname=tableName (alias=Identifier)?
    ->
      ^(TOK_TABREF $tabname $alias?)
  ;

whereClause
  :
  KW_WHERE searchCondition
    ->
      ^(TOK_WHERE searchCondition)
  ;

searchCondition
  :
  expression
  ;

groupByClause
  :
  KW_GROUP KW_BY groupByExpression (COMMA groupByExpression)* coordinateInfo
    ->
      ^(TOK_GROUPBY groupByExpression+ coordinateInfo)
  ;

coordinateInfo
  :
  (KW_COORDINATE KW_BY coordinateExpr)? (aggrInterval)? (accuInterval)? (swInterval)? KW_ACCUGLOBAL?
    ->
      ^(
        TOK_COORDINATE coordinateExpr? (aggrInterval)? (accuInterval)? (swInterval)? KW_ACCUGLOBAL?
       )
  ;

coordinateExpr
  :
  expression
    ->
      ^(TOK_COORDINATE_EXPR expression)
  ;

aggrInterval
  :
  KW_WITH KW_AGGR KW_INTERVAL Number KW_SECONDS
    ->
      ^(TOK_AGGR_INTERVAL Number)
  ;

accuInterval
  :
  KW_WITH KW_ACCU KW_INTERVAL Number KW_SECONDS
    ->
      ^(TOK_ACCU_INTERVAL Number)
  ;

swInterval
  :
  KW_WITH KW_SW KW_INTERVAL Number KW_SECONDS
    ->
      ^(TOK_SW_INTERVAL Number)
  ;

groupByExpression
  :
  expression
  ;

havingClause
  :
  KW_HAVING havingCondition
    ->
      ^(TOK_HAVING havingCondition)
  ;

havingCondition
  :
  expression
  ;

// kew words

KW_FOREACH
  :
  'FOREACH'
  ;

KW_EXECUTE
  :
  'EXECUTE'
  ;

KW_DEFINE
  :
  'DEFINE'
  ;

KW_DEFAULT
  :
  'DEFAULT'
  ;

KW_EMIT
  :
  'EMIT'
  ;

KW_FOR
  :
  'FOR'
  ;

KW_GENERATE
  :
  'GENERATE'
  ;

KW_GENERATEMAP
  :
  'GENERATEMAP'
  ;

KW_INNERTABLE
  :
  '__INNERTABLE'
  ;

KW_PRINTTABLE
  :
  '__PRINTTABLE'
  ;

KW_TRUE
  :
  'TRUE'
  ;

KW_FALSE
  :
  'FALSE'
  ;

KW_ALL
  :
  'ALL'
  ;

KW_AND
  :
  'AND'
  ;

KW_OR
  :
  'OR'
  ;

KW_NOT
  :
  'NOT'
  | '!'
  ;

KW_LIKE
  :
  'LIKE'
  ;

NOTLIKE
  :
  'NOTLIKE'
  ;

KW_IF
  :
  'IF'
  ;

KW_EXISTS
  :
  'EXISTS'
  ;

KW_ASC
  :
  'ASC'
  ;

KW_DESC
  :
  'DESC'
  ;

KW_ORDER
  :
  'ORDER'
  ;

KW_GROUP
  :
  'GROUP'
  ;

KW_COORDINATE
  :
  'COORDINATE'
  ;

KW_BY
  :
  'BY'
  ;

KW_HAVING
  :
  'HAVING'
  ;

KW_WHERE
  :
  'WHERE'
  ;

KW_FROM
  :
  'FROM'
  ;

KW_AS
  :
  'AS'
  ;

KW_SELECT
  :
  'SELECT'
  ;

KW_EXPAND
  :
  'EXPAND'
  ;

KW_DISTINCT
  :
  'DISTINCT'
  ;

KW_ACCU
  :
  'ACCU'
  ;

KW_ACCUGLOBAL
  :
  'ACCUGLOBAL'
  ;

KW_AGGR
  :
  'AGGR'
  ;

KW_AGGR_TIME
  :
  'AGGRTIME'
  ;

KW_ATTRIBUTES
  :
  'ATTRIBUTES'
  | 'ATTRS'
  ;

KW_INTERVAL
  :
  'INTERVAL'
  ;

KW_SECONDS
  :
  'SECONDS'
  ;

KW_SW
  :
  'SW'
  ;

KW_INSERT
  :
  'INSERT'
  ;

KW_OVERWRITE
  :
  'OVERWRITE'
  ;

KW_OUTER
  :
  'OUTER'
  ;

KW_UNIQUEJOIN
  :
  'UNIQUEJOIN'
  ;

KW_PRESERVE
  :
  'PRESERVE'
  ;

KW_JOIN
  :
  'JOIN'
  ;

KW_LEFT
  :
  'LEFT'
  ;

KW_RIGHT
  :
  'RIGHT'
  ;

KW_FULL
  :
  'FULL'
  ;

KW_ON
  :
  'ON'
  ;

KW_PARTITION
  :
  'PARTITION'
  ;

KW_PARTITIONS
  :
  'PARTITIONS'
  ;

KW_TABLE
  :
  'TABLE'
  ;

KW_TABLES
  :
  'TABLES'
  ;

KW_INDEX
  :
  'INDEX'
  ;

KW_INDEXES
  :
  'INDEXES'
  ;

KW_REBUILD
  :
  'REBUILD'
  ;

KW_FUNCTIONS
  :
  'FUNCTIONS'
  ;

KW_SHOW
  :
  'SHOW'
  ;

KW_MSCK
  :
  'MSCK'
  ;

KW_REPAIR
  :
  'REPAIR'
  ;

KW_DIRECTORY
  :
  'DIRECTORY'
  ;

KW_LOCAL
  :
  'LOCAL'
  ;

KW_TRANSFORM
  :
  'TRANSFORM'
  ;

KW_USING
  :
  'USING'
  ;

KW_CLUSTER
  :
  'CLUSTER'
  ;

KW_DISTRIBUTE
  :
  'DISTRIBUTE'
  ;

KW_SORT
  :
  'SORT'
  ;

KW_UNION
  :
  'UNION'
  ;

KW_LOAD
  :
  'LOAD'
  ;

KW_EXPORT
  :
  'EXPORT'
  ;

KW_IMPORT
  :
  'IMPORT'
  ;

KW_DATA
  :
  'DATA'
  ;

KW_INPATH
  :
  'INPATH'
  ;

KW_IS
  :
  'IS'
  ;

KW_NULL
  :
  'NULL'
  ;

KW_CREATE
  :
  'CREATE'
  ;

KW_EXTERNAL
  :
  'EXTERNAL'
  ;

KW_ALTER
  :
  'ALTER'
  ;

KW_CHANGE
  :
  'CHANGE'
  ;

KW_COLUMN
  :
  'COLUMN'
  ;

KW_FIRST
  :
  'FIRST'
  ;

KW_AFTER
  :
  'AFTER'
  ;

KW_DESCRIBE
  :
  'DESCRIBE'
  ;

KW_DROP
  :
  'DROP'
  ;

KW_RENAME
  :
  'RENAME'
  ;

KW_TO
  :
  'TO'
  ;

KW_COMMENT
  :
  'COMMENT'
  ;

KW_BOOLEAN
  :
  'BOOLEAN'
  ;

KW_TINYINT
  :
  'TINYINT'
  ;

KW_SMALLINT
  :
  'SMALLINT'
  ;

KW_INT
  :
  'INT'
  ;

KW_BIGINT
  :
  'BIGINT'
  ;

KW_FLOAT
  :
  'FLOAT'
  ;

KW_DOUBLE
  :
  'DOUBLE'
  ;

KW_DATE
  :
  'DATE'
  ;

KW_DATETIME
  :
  'DATETIME'
  ;

KW_TIMESTAMP
  :
  'TIMESTAMP'
  ;

KW_STRING
  :
  'STRING'
  ;

KW_ARRAY
  :
  'ARRAY'
  ;

KW_STRUCT
  :
  'STRUCT'
  ;

KW_MAP
  :
  'MAP'
  ;

KW_UNIONTYPE
  :
  'UNIONTYPE'
  ;

KW_REDUCE
  :
  'REDUCE'
  ;

KW_PARTITIONED
  :
  'PARTITIONED'
  ;

KW_CLUSTERED
  :
  'CLUSTERED'
  ;

KW_SORTED
  :
  'SORTED'
  ;

KW_INTO
  :
  'INTO'
  ;

KW_BUCKETS
  :
  'BUCKETS'
  ;

KW_ROW
  :
  'ROW'
  ;

KW_FORMAT
  :
  'FORMAT'
  ;

KW_DELIMITED
  :
  'DELIMITED'
  ;

KW_FIELDS
  :
  'FIELDS'
  ;

KW_TERMINATED
  :
  'TERMINATED'
  ;

KW_ESCAPED
  :
  'ESCAPED'
  ;

KW_COLLECTION
  :
  'COLLECTION'
  ;

KW_ITEMS
  :
  'ITEMS'
  ;

KW_KEYS
  :
  'KEYS'
  ;

KW_KEY_TYPE
  :
  '$KEY$'
  ;

KW_LINES
  :
  'LINES'
  ;

KW_STORED
  :
  'STORED'
  ;

KW_FILEFORMAT
  :
  'FILEFORMAT'
  ;

KW_SEQUENCEFILE
  :
  'SEQUENCEFILE'
  ;

KW_TEXTFILE
  :
  'TEXTFILE'
  ;

KW_RCFILE
  :
  'RCFILE'
  ;

KW_INPUTFORMAT
  :
  'INPUTFORMAT'
  ;

KW_OUTPUTFORMAT
  :
  'OUTPUTFORMAT'
  ;

KW_INPUTDRIVER
  :
  'INPUTDRIVER'
  ;

KW_OUTPUTDRIVER
  :
  'OUTPUTDRIVER'
  ;

KW_OFFLINE
  :
  'OFFLINE'
  ;

KW_ENABLE
  :
  'ENABLE'
  ;

KW_DISABLE
  :
  'DISABLE'
  ;

KW_READONLY
  :
  'READONLY'
  ;

KW_NO_DROP
  :
  'NO_DROP'
  ;

KW_LOCATION
  :
  'LOCATION'
  ;

KW_TABLESAMPLE
  :
  'TABLESAMPLE'
  ;

KW_BUCKET
  :
  'BUCKET'
  ;

KW_OUT
  :
  'OUT'
  ;

KW_OF
  :
  'OF'
  ;

KW_PERCENT
  :
  'PERCENT'
  ;

KW_CAST
  :
  'CAST'
  ;

KW_ADD
  :
  'ADD'
  ;

KW_REPLACE
  :
  'REPLACE'
  ;

KW_COLUMNS
  :
  'COLUMNS'
  ;

KW_RLIKE
  :
  'RLIKE'
  ;

NOTRLIKE
  :
  'NOTRLIKE'
  ;

KW_REGEXP
  :
  'REGEXP'
  ;

NOTREGEXP
  :
  'NOTREGEXP'
  ;

KW_TEMPORARY
  :
  'TEMPORARY'
  ;

KW_FUNCTION
  :
  'FUNCTION'
  ;

KW_EXPLAIN
  :
  'EXPLAIN'
  ;

KW_EXTENDED
  :
  'EXTENDED'
  ;

KW_FORMATTED
  :
  'FORMATTED'
  ;

KW_SERDE
  :
  'SERDE'
  ;

KW_WITH
  :
  'WITH'
  ;

KW_KEY
  :
  'KEY'
  ;

KW_DEFERRED
  :
  'DEFERRED'
  ;

KW_SERDEPROPERTIES
  :
  'SERDEPROPERTIES'
  ;

KW_DBPROPERTIES
  :
  'DBPROPERTIES'
  ;

KW_LIMIT
  :
  'LIMIT'
  ;

KW_SET
  :
  'SET'
  ;

KW_TBLPROPERTIES
  :
  'TBLPROPERTIES'
  ;

KW_IDXPROPERTIES
  :
  'IDXPROPERTIES'
  ;

KW_VALUE_TYPE
  :
  '$VALUE$'
  ;

KW_ELEM_TYPE
  :
  '$ELEM$'
  ;

KW_CASE
  :
  'CASE'
  ;

KW_WHEN
  :
  'WHEN'
  ;

KW_THEN
  :
  'THEN'
  ;

KW_ELSE
  :
  'ELSE'
  ;

KW_END
  :
  'END'
  ;

KW_MAPJOIN
  :
  'MAPJOIN'
  ;

KW_STREAMTABLE
  :
  'STREAMTABLE'
  ;

KW_HOLD_DDLTIME
  :
  'HOLD_DDLTIME'
  ;

KW_CLUSTERSTATUS
  :
  'CLUSTERSTATUS'
  ;

KW_UTC
  :
  'UTC'
  ;

KW_UTCTIMESTAMP
  :
  'UTC_TMESTAMP'
  ;

KW_LONG
  :
  'LONG'
  ;

KW_DELETE
  :
  'DELETE'
  ;

KW_PLUS
  :
  'PLUS'
  ;

KW_MINUS
  :
  'MINUS'
  ;

KW_FETCH
  :
  'FETCH'
  ;

KW_INTERSECT
  :
  'INTERSECT'
  ;

KW_VIEW
  :
  'VIEW'
  ;

KW_IN
  :
  'IN'
  ;

NOTIN
  :
  'NOTIN'
  ;

KW_DATABASE
  :
  'DATABASE'
  ;

KW_DATABASES
  :
  'DATABASES'
  ;

KW_MATERIALIZED
  :
  'MATERIALIZED'
  ;

KW_SCHEMA
  :
  'SCHEMA'
  ;

KW_SCHEMAS
  :
  'SCHEMAS'
  ;

KW_GRANT
  :
  'GRANT'
  ;

KW_REVOKE
  :
  'REVOKE'
  ;

KW_SSL
  :
  'SSL'
  ;

KW_UNDO
  :
  'UNDO'
  ;

KW_LOCK
  :
  'LOCK'
  ;

KW_LOCKS
  :
  'LOCKS'
  ;

KW_UNLOCK
  :
  'UNLOCK'
  ;

KW_SHARED
  :
  'SHARED'
  ;

KW_EXCLUSIVE
  :
  'EXCLUSIVE'
  ;

KW_PROCEDURE
  :
  'PROCEDURE'
  ;

KW_UNSIGNED
  :
  'UNSIGNED'
  ;

KW_WHILE
  :
  'WHILE'
  ;

KW_READ
  :
  'READ'
  ;

KW_READS
  :
  'READS'
  ;

KW_PURGE
  :
  'PURGE'
  ;

KW_RANGE
  :
  'RANGE'
  ;

KW_ANALYZE
  :
  'ANALYZE'
  ;

KW_BEFORE
  :
  'BEFORE'
  ;

KW_BETWEEN
  :
  'BETWEEN'
  ;

KW_BOTH
  :
  'BOTH'
  ;

KW_BINARY
  :
  'BINARY'
  ;

KW_CROSS
  :
  'CROSS'
  ;

KW_CONTINUE
  :
  'CONTINUE'
  ;

KW_CURSOR
  :
  'CURSOR'
  ;

KW_TRIGGER
  :
  'TRIGGER'
  ;

KW_RECORDREADER
  :
  'RECORDREADER'
  ;

KW_RECORDWRITER
  :
  'RECORDWRITER'
  ;

KW_SEMI
  :
  'SEMI'
  ;

KW_LATERAL
  :
  'LATERAL'
  ;

KW_TOUCH
  :
  'TOUCH'
  ;

KW_ARCHIVE
  :
  'ARCHIVE'
  ;

KW_UNARCHIVE
  :
  'UNARCHIVE'
  ;

KW_COMPUTE
  :
  'COMPUTE'
  ;

KW_STATISTICS
  :
  'STATISTICS'
  ;

KW_USE
  :
  'USE'
  ;

KW_OPTION
  :
  'OPTION'
  ;

KW_CONCATENATE
  :
  'CONCATENATE'
  ;

KW_SHOW_DATABASE
  :
  'SHOW_DATABASE'
  ;

KW_UPDATE
  :
  'UPDATE'
  ;

KW_RESTRICT
  :
  'RESTRICT'
  ;

KW_CASCADE
  :
  'CASCADE'
  ;

DOT
  :
  '.'
  ; // generated as a part of Number rule

COLON
  :
  ':'
  ;

ASSIGN
  :
  ':='
  ;

COMMA
  :
  ','
  ;

SEMICOLON
  :
  ';'
  ;

LPAREN
  :
  '('
  ;

RPAREN
  :
  ')'
  ;

LSQUARE
  :
  '['
  ;

RSQUARE
  :
  ']'
  ;

LCURLY
  :
  '{'
  ;

RCURLY
  :
  '}'
  ;

EQUAL
  :
  '='
  | '=='
  ;

NOTEQUAL
  :
  '<>'
  | '!='
  ;

LESSTHANOREQUALTO
  :
  '<='
  ;

LESSTHAN
  :
  '<'
  ;

GREATERTHANOREQUALTO
  :
  '>='
  ;

GREATERTHAN
  :
  '>'
  ;

DIVIDE
  :
  '/'
  ;

PLUS
  :
  '+'
  ;

MINUS
  :
  '-'
  ;

STAR
  :
  '*'
  ;

MOD
  :
  '%'
  ;

DIV
  :
  'DIV'
  ;

AMPERSAND
  :
  '&'
  ;

TILDE
  :
  '~'
  ;

BITWISEOR
  :
  '|'
  ;

BITWISEXOR
  :
  '^'
  ;

QUESTION
  :
  '?'
  ;

DOLLAR
  :
  '$'
  ;

// LITERALS

fragment
Letter
  :
  'a'..'z'
  | 'A'..'Z'
  ;

fragment
HexDigit
  :
  'a'..'f'
  | 'A'..'F'
  ;

fragment
Digit
  :
  '0'..'9'
  ;

fragment
Exponent
  :
  (
    'e'
    | 'E'
  )
  (
    PLUS
    | MINUS
  )?
  (Digit)+
  ;

fragment
RegexComponent
  :
  'a'..'z'
  | 'A'..'Z'
  | '0'..'9'
  | '_'
  | PLUS
  | STAR
  | QUESTION
  | MINUS
  | DOT
  | LPAREN
  | RPAREN
  | LSQUARE
  | RSQUARE
  | LCURLY
  | RCURLY
  | BITWISEXOR
  | BITWISEOR
  | DOLLAR
  ;

StringLiteral
  :
  (
    '\''
    (
      ~(
        '\''
        | '\\'
       )
      | ('\\' .)
    )*
    '\''
    | '\"'
    (
      ~(
        '\"'
        | '\\'
       )
      | ('\\' .)
    )*
    '\"'
  )+
  ;

CharSetLiteral
  :
  StringLiteral
  | '0' 'X'
  (
    HexDigit
    | Digit
  )+
  ;

BigintLiteral
  :
  (Digit)+ 'L'
  ;

SmallintLiteral
  :
  (Digit)+ 'S'
  ;

TinyintLiteral
  :
  (Digit)+ 'Y'
  ;

Number
  :
  (Digit)+
  (
    DOT (Digit)* (Exponent)?
    | Exponent
  )?
  ;

Identifier
  :
  (
    Letter
    | Digit
  )
  (
    Letter
    | Digit
    | '_'
  )*
  DOLLAR?
  | '`' RegexComponent+ '`'
  ;

IdentifierRef
  :
  DOLLAR
  (
    Letter
    | Digit
    | '_'
  )+
  | '`' DOLLAR RegexComponent+ '`'
  ;

CharSetName
  :
  '_'
  (
    Letter
    | Digit
    | '_'
    | '-'
    | '.'
    | ':'
  )+
  ;

WS
  :
  (
    ' '
    | '\r'
    | '\t'
    | '\n'
  )
  
   {
    $channel = HIDDEN;
   }
  ;

COMMENT
  :
  '--'
  (
    ~(
      '\n'
      | '\r'
     )
  )*
  
   {
    $channel = HIDDEN;
   }
  ;
