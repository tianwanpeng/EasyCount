package com.tencent.trc.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.tencent.trc.parse.Query.QueryMode;
import com.tencent.trc.util.graph.GraphWalker.Node;

/**
 * each ec-sql has only one QB(query block)
 * 
 * all subquery should has a alias, and every alias can not appear more than one
 * times, so all alias is unique globally
 * 
 * QB maintains all the map relations of alias to query, astnode to query, and
 * asttree to query, etc...
 * 
 * ----------------------------------
 * 
 * relations between alias, query, asttree, astnode
 * 
 * an astnode is a subquery reference
 * 
 * astnode 1::1 alias \ astnode n::1 asttree \ astnode n::1 query \ alias n::1
 * query \ asttree 1::1 query \
 * 
 * notice that when from subquery alias has new alias, then the astnode has 2 alias
 * 
 * @author steven
 *
 */
public class QB {

	/**
	 * alias, query, asttree, astnode
	 */

	final private HashSet<String> allAliases;
	final private HashMap<String, Query> aliasToQuery;
	final private HashMap<String, Query> asttreeToQuery;
	final private HashMap<ASTNodeTRC, Query> astnodeToQuery;
	final private HashMap<ASTNodeTRC, String> astnodeToAlias;

	// rootQuerys must be src tables
	final private HashSet<Query> rootQuerys;
	final private HashSet<Query> destQuerys;

	public String printStr() {
		StringBuffer sb = new StringBuffer();
		sb.append("allAliases : ").append(this.allAliases)
				.append("\n----------------------\n");
		sb.append("aliasToQuery : ").append(this.aliasToQuery)
				.append("\n----------------------\n");
		return sb.toString();
	}

	public QB() {
		this.allAliases = new HashSet<String>();
		this.aliasToQuery = new HashMap<String, Query>();
		this.asttreeToQuery = new HashMap<String, Query>();
		this.astnodeToQuery = new HashMap<ASTNodeTRC, Query>();
		this.astnodeToAlias = new HashMap<ASTNodeTRC, String>();
		this.rootQuerys = new HashSet<Query>();
		this.destQuerys = new HashSet<Query>();
	}

	/**
	 * whenever met a subquery in walking asttree, we should update or add the
	 * query info into QB.
	 * 
	 * note that sometimes same query may have different alias, in order by
	 * optimize exec plan, here we must find this kind of query and process it
	 * 
	 * for the same query, once we found it , we just use the same Query object
	 * to express it
	 * 
	 * @param alias
	 * @param queryn
	 * @param aliasType
	 */
	public Query updateQuery(String alias, ASTNodeTRC queryn) {

		if (allAliases.contains(alias)) {
			// alias should unique globally
			throw new RuntimeException("alise as a query allready exist : "
					+ alias);
		}

		String asttree = queryn.toStringTree();
		Query q = getQuery(alias, queryn, asttree);

		this.allAliases.add(alias);
		this.astnodeToAlias.put(queryn, alias);
		this.aliasToQuery.put(alias, q);
		this.asttreeToQuery.put(asttree, q);
		this.astnodeToQuery.put(queryn, q);

		if (q.getQmode() == QueryMode.insertq) {
			destQuerys.add(q);
		}
		return q;
	}

	public void updateQueryTableRef(String tableId, String alias,
			ASTNodeTRC queryn) {

		// if alias exist it must not in aliases, unique globally
		if (alias != null && allAliases.contains(alias)) {
			throw new RuntimeException("alise as a query allready exist : "
					+ alias);
		}
		Query q = null;
		if (allAliases.contains(tableId)) {
			// tableid exist in allAliases means the tableId is a alias of some
			// subquery
			String asttree = queryn.toStringTree();
			q = getQuery(tableId, queryn, asttree);

			this.allAliases.add(tableId);
			this.astnodeToAlias.put(queryn, tableId);
			this.aliasToQuery.put(tableId, q);
			this.asttreeToQuery.put(asttree, q);
			this.astnodeToQuery.put(queryn, q);
		} else {
			// here means this query is a table
			q = updateQuery(tableId, queryn);
		}

		if (alias != null && alias != tableId) {
			// if alias and tableid both exist, update alias too
			q = updateQuery(alias, queryn);
		}

		if (q.getQmode() == QueryMode.table) {
			// table mode, should add the tableid, and add to rootquerys
			// table scan are all root querys
			q.setTableId(tableId);
			rootQuerys.add(q);
		} else if (rootQuerys.contains(q)) {
			// sometimes from sub clause may appear before with clause, then the
			// subquery alias may be viewed as a table, and the query beed added
			// to the rootquerys, here just delete it.
			// actually it is impossible to happen here, but just be cautious
			rootQuerys.remove(q);
		}
	}

	private Query getQuery(String alias, ASTNodeTRC queryn, String asttree) {
		// if we find the alias has already exists then just return it
		if (aliasToQuery.containsKey(alias)) {
			return aliasToQuery.get(alias);
		}

		// sometimes alias is not exist, but we found the asttree has already
		// exists, so we just return it and add the new alias into the alis set
		// this means same sub-query appear more than once but has different
		// alias
		if (asttreeToQuery.containsKey(asttree)) {
			Query q = asttreeToQuery.get(asttree);
			q.addAlias(alias);
			return q;
		}

		// otherwise we must new a Query object
		int qtype = queryn.getToken().getType();
		QueryMode qmode = (qtype == TrcParser.TOK_INSERT_QUERY) ? QueryMode.insertq
				: ((qtype == TrcParser.TOK_QUERY) ? QueryMode.subquery
						: ((qtype == TrcParser.TOK_UNION) ? QueryMode.union
								: QueryMode.table));
		return new Query(queryn, alias, qmode);
	}

	public Query getQueryByAstTree(String astTree) {
		return this.asttreeToQuery.get(astTree);
	}

	public Query getQueryByAlias(String alias) {
		return this.aliasToQuery.get(alias);
	}

	public String getAliasByAstNode(ASTNodeTRC ast) {
		return this.astnodeToAlias.get(ast);
	}

	public Query getQueryByAstNode(ASTNodeTRC ast) {
		return this.astnodeToQuery.get(ast);
	}

	public HashSet<String> getAllAliases() {
		return allAliases;
	}

	public HashMap<String, Query> getAliasToQuery() {
		return aliasToQuery;
	}

	public HashMap<String, Query> getAsttreeToQuery() {
		return asttreeToQuery;
	}

	public HashMap<ASTNodeTRC, Query> getAstnodeToQuery() {
		return astnodeToQuery;
	}

	public HashMap<ASTNodeTRC, String> getAstnodeToAlias() {
		return astnodeToAlias;
	}

	public HashSet<Query> getRootQuerys() {
		return rootQuerys;
	}

	public ArrayList<Node> getRootQueryNodes() {
		ArrayList<Node> res = new ArrayList<Node>();
		res.addAll(rootQuerys);
		return res;
	}

	public HashSet<Query> getDestQuerys() {
		return destQuerys;
	}

}
