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

public class QuerySearchServiceImpl extends AbstractSearchService implements
		QuerySearchService {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(QuerySearchServiceImpl.class);

	public Page<SearchBean> executeQuery(String expr,
			boolean includeProperties, boolean isExactMatch, Integer pageSize,
			Integer pageNum) throws Exception {
		return executeQuery(expr, null, includeProperties, isExactMatch,
				pageSize, pageNum);
	}

	public Page<SearchBean> executeQuery(String expr,
			boolean includeProperties, boolean isExactMatch) throws Exception {
		return executeQuery(expr, null, includeProperties, isExactMatch);
	}

	public Page<SearchBean> executeQuery(String expr,
			Collection<Integer> ontologyIds, boolean includeProperties,
			boolean isExactMatch, Integer pageSize, Integer pageNum)
			throws Exception {
		Query query = generateLuceneSearchQuery(ontologyIds, expr,
				includeProperties, isExactMatch);

		return executeQuery(query, pageSize, pageNum);
	}

	public Page<SearchBean> executeQuery(String expr,
			Collection<Integer> ontologyIds, boolean includeProperties,
			boolean isExactMatch) throws Exception {
		Query query = generateLuceneSearchQuery(ontologyIds, expr,
				includeProperties, isExactMatch);

		return executeQuery(query);
	}

	public Page<SearchBean> executeQuery(Query query) throws Exception {
		return executeQuery(query, null, null);
	}

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

	private void addContentsClauseExact(String expr, BooleanQuery query)
			throws IOException {
		TermQuery q = new TermQuery(new Term(
				SearchIndexBean.LITERAL_CONTENTS_FIELD_LABEL, expr
						.toLowerCase()));
		query.add(q, BooleanClause.Occur.MUST);
	}

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

	private void addPropertiesClause(boolean includeProperties,
			BooleanQuery query) {
		if (!includeProperties) {
			Term term = new Term(SearchIndexBean.RECORD_TYPE_FIELD_LABEL,
					SearchRecordTypeEnum.RECORD_TYPE_PROPERTY.getLabel());
			query.add(new TermQuery(term), BooleanClause.Occur.MUST_NOT);
		}
	}

	private void addOntologyIdsClause(Collection<Integer> ontologyIds,
			BooleanQuery query) {
		if (ontologyIds != null && !ontologyIds.isEmpty()) {
			query.add(generateOntologyIdsQuery(ontologyIds),
					BooleanClause.Occur.MUST);
		}
	}

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