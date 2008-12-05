package org.ncbo.stanford.service.search.impl;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;
import org.ncbo.stanford.service.search.AbstractSearchService;
import org.ncbo.stanford.service.search.QuerySearchService;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.paginator.Paginator;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.util.paginator.impl.PaginatorImpl;

/**
 * A default implementation of the QuerySearchService
 * 
 * @author Michael Dorf
 * 
 */
public class QuerySearchServiceImpl extends AbstractSearchService implements
		QuerySearchService {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(QuerySearchServiceImpl.class);

	/**
	 * Execute a search query for a given expression and return results in a
	 * form of a single page (of specified size)
	 * 
	 * @param expr
	 * @param includeProperties
	 * @param isExactMatch
	 * @param pageSize
	 * @param pageNum
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(String expr,
			boolean includeProperties, boolean isExactMatch, Integer pageSize,
			Integer pageNum) throws Exception {
		return executeQuery(expr, null, includeProperties, isExactMatch,
				pageSize, pageNum);
	}

	/**
	 * Execute a search query for a given expression and return ALL results in a
	 * form of a single page
	 * 
	 * @param expr
	 * @param includeProperties
	 * @param isExactMatch
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(String expr,
			boolean includeProperties, boolean isExactMatch) throws Exception {
		return executeQuery(expr, null, includeProperties, isExactMatch);
	}

	/**
	 * Execute a search query for a given expression, limiting search to the
	 * specific ontologies. Return results in a form of a single page (of
	 * specified size)
	 * 
	 * @param expr
	 * @param ontologyIds
	 * @param includeProperties
	 * @param isExactMatch
	 * @param pageSize
	 * @param pageNum
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(String expr,
			Collection<Integer> ontologyIds, boolean includeProperties,
			boolean isExactMatch, Integer pageSize, Integer pageNum)
			throws Exception {
		Query query = generateLuceneSearchQuery(ontologyIds, expr,
				includeProperties, isExactMatch);

		return executeQuery(query, pageSize, pageNum);
	}

	/**
	 * Execute a search query for a given expression, limiting search to the
	 * specific ontologies. Return ALL results in a form of a single page
	 * 
	 * @param expr
	 * @param ontologyIds
	 * @param includeProperties
	 * @param isExactMatch
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(String expr,
			Collection<Integer> ontologyIds, boolean includeProperties,
			boolean isExactMatch) throws Exception {
		Query query = generateLuceneSearchQuery(ontologyIds, expr,
				includeProperties, isExactMatch);

		return executeQuery(query);
	}

	/**
	 * Execute a search from an already constructed Query object. Return results
	 * in a form of a single page (of specified size)
	 * 
	 * @param query
	 * @param pageSize
	 * @param pageNum
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(Query query) throws Exception {
		return executeQuery(query, null, null);
	}

	/**
	 * Execute a search from an already constructed Query object. Return ALL
	 * results in a form of a single page
	 * 
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(Query query, Integer pageSize,
			Integer pageNum) throws Exception {
		long start = System.currentTimeMillis();
		String resultsKey = query.toString();
		boolean fromCache = true;
		SearchResultListBean searchResults = searchResultCache.get(resultsKey);

		if (searchResults == null) {
			searchResults = runQuery(query);
			fromCache = false;
			searchResultCache.put(resultsKey, searchResults);
		}

		int resultsSize = searchResults.size();

		if (pageSize == null || pageSize <= 0) {
			pageSize = resultsSize;
		}

		Page<SearchBean> page;
		Paginator<SearchBean> p = new PaginatorImpl<SearchBean>(searchResults,
				pageSize);

		if (pageNum == null || pageNum <= 1) {
			page = p.getFirstPage();
		} else {
			page = p.getNextPage(pageNum - 1);
		}

		long stop = System.currentTimeMillis();

		if (log.isDebugEnabled()) {
			log.debug("Query: " + query);
			log.debug("Cached?: " + (fromCache ? "Yes" : "No"));
			log.debug("Number of Hits: " + searchResults.size());
			log.debug("Excecution Time: " + (double) (stop - start) / 1000
					+ " seconds.");
		}

		return page;
	}

	/**
	 * Generate a search query from the expression and optional ontology ids
	 * 
	 * @param ontologyIds
	 * @param expr
	 * @param includeProperties
	 * @param isExactMatch
	 * @return
	 * @throws IOException
	 */
	public Query generateLuceneSearchQuery(Collection<Integer> ontologyIds,
			String expr, boolean includeProperties, boolean isExactMatch)
			throws IOException {
		BooleanQuery query = new BooleanQuery();

		if (isExactMatch) {
			addContentsClauseExact(expr, query);
		} else {
			addContentsClauseContains(expr, query);
		}

		addOntologyIdsClause(ontologyIds, query);
		addPropertiesClause(includeProperties, query);

		return query;
	}

	/**
	 * Constructs the contents field clause for "exact match" searches and adds
	 * it to the main query
	 * 
	 * @param expr
	 * @param query
	 * @throws IOException
	 */
	private void addContentsClauseExact(String expr, BooleanQuery query)
			throws IOException {
		TermQuery q = new TermQuery(new Term(
				SearchIndexBean.LITERAL_CONTENTS_FIELD_LABEL, expr
						.toLowerCase()));
		query.add(q, BooleanClause.Occur.MUST);
	}

	/**
	 * Constructs the contents field clause for "regular (non-exact) match"
	 * searches and adds it to the main query
	 * 
	 * @param expr
	 * @param query
	 * @throws IOException
	 */
	private void addContentsClauseContains(String expr, BooleanQuery query)
			throws IOException {
		QueryParser parser = new QueryParser(
				SearchIndexBean.CONTENTS_FIELD_LABEL, analyzer);
		parser.setAllowLeadingWildcard(true);
		parser.setDefaultOperator(QueryParser.AND_OPERATOR);

		try {
			expr = StringHelper.escapeSpaces(expr);
			query.add(parser.parse(expr), BooleanClause.Occur.MUST);
		} catch (ParseException e) {
			IOException ioe = new IOException(e.getMessage());
			ioe.initCause(e);
			throw ioe;
		}
	}

	/**
	 * Constructs the search clause for searching ontology properties and adds
	 * it to the main query
	 * 
	 * @param includeProperties
	 * @param query
	 */
	private void addPropertiesClause(boolean includeProperties,
			BooleanQuery query) {
		if (!includeProperties) {
			Term term = new Term(SearchIndexBean.RECORD_TYPE_FIELD_LABEL,
					SearchRecordTypeEnum.RECORD_TYPE_PROPERTY.getLabel());
			query.add(new TermQuery(term), BooleanClause.Occur.MUST_NOT);
		}
	}

	/**
	 * Adds the clause that limits the search to the given ontology ids to the
	 * main query
	 * 
	 * @param ontologyIds
	 * @param query
	 */
	private void addOntologyIdsClause(Collection<Integer> ontologyIds,
			BooleanQuery query) {
		if (ontologyIds != null && !ontologyIds.isEmpty()) {
			query.add(generateOntologyIdsQuery(ontologyIds),
					BooleanClause.Occur.MUST);
		}
	}

	/**
	 * Constructs the search clause that limits the search to the given ontology
	 * ids
	 * 
	 * @param ontologyIds
	 * @return
	 */
	private Query generateOntologyIdsQuery(Collection<Integer> ontologyIds) {
		BooleanQuery query = new BooleanQuery();

		for (Integer ontologyId : ontologyIds) {
			Term term = new Term(SearchIndexBean.ONTOLOGY_ID_FIELD_LABEL,
					ontologyId.toString());
			query.add(new TermQuery(term), BooleanClause.Occur.SHOULD);
		}

		return query;
	}
}