package org.ncbo.stanford.service.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
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
import org.ncbo.stanford.util.lucene.PrefixQuery;
import org.ncbo.stanford.util.paginator.Paginator;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.util.paginator.impl.PaginatorImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * A default implementation of the QuerySearchService
 * 
 * @author Michael Dorf
 * 
 */
@Transactional
public class QuerySearchServiceImpl extends AbstractSearchService implements
		QuerySearchService {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(QuerySearchServiceImpl.class);

//	public static void main(String[] args) {
//		try {
//			String expr = "Melanoma";
//			Collection<Integer> ontologyIds = new ArrayList<Integer>(0);
//			 ontologyIds.add(1009);
			// ontologyIds.add(1070);
			// ontologyIds.add(1107);

//			boolean includeProperties = false;
//			boolean isExactMatch = true;
//			Integer maxNumHits = 250;
//
//			String indexPath = "/apps/bmir.apps/bioportal_resources/searchindex";
//			QuerySearchServiceImpl ss = new QuerySearchServiceImpl();
//			ss.setAnalyzer(new StandardAnalyzer());
//			ss.setIndexPath(indexPath);
//
//			Query q = ss.generateLuceneSearchQuery(ontologyIds, expr,
//					includeProperties, isExactMatch);
//			System.out.println("q: " + q);
//
//			long start = System.currentTimeMillis();
//			SearchResultListBean results = ss.runQuery(q, maxNumHits);
//			long stop = System.currentTimeMillis();
//
//			System.out.println("Excecution Time: " + (double) (stop - start)
//					/ 1000 + " seconds.");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	public static void main(String[] args) {
//		try {
//			IndexSearcher searcher = new IndexSearcher(
//					"/apps/bmir.apps/bioportal_resources/searchindex");
//			TermEnum terms = searcher.getIndexReader().terms(
//					new Term("conceptId", ""));
//			int numTerms = 0;
//
//			while ("conceptId".equals(terms.term().field())) {
//				numTerms++;
//
//				if (!terms.next())
//					break;
//			}
//
//			terms.close();
//
//			System.out.println("Num Concepts: " + numTerms);
//
//		} catch (CorruptIndexException e) { // TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) { // TODO Auto-generated
//			e.printStackTrace();
//		}
//	}

	/**
	 * Execute a search query for a given expression and return results in a
	 * form of a single page (of specified size). If maxNumHits is null, the
	 * default value from the configuration file is used.
	 * 
	 * @param expr
	 * @param includeProperties
	 * @param isExactMatch
	 * @param pageSize
	 * @param pageNum
	 * @param maxNumHits
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(String expr,
			boolean includeProperties, boolean isExactMatch, Integer pageSize,
			Integer pageNum, Integer maxNumHits) throws Exception {
		return executeQuery(expr, null, includeProperties, isExactMatch,
				pageSize, pageNum, maxNumHits);
	}

	/**
	 * Execute a search query for a given expression and return ALL results in a
	 * form of a single page. If maxNumHits is null, the default value from the
	 * configuration file is used.
	 * 
	 * @param expr
	 * @param includeProperties
	 * @param isExactMatch
	 * @param maxNumHits
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(String expr,
			boolean includeProperties, boolean isExactMatch, Integer maxNumHits)
			throws Exception {
		return executeQuery(expr, null, includeProperties, isExactMatch,
				maxNumHits);
	}

	/**
	 * Execute a search query for a given expression, limiting search to the
	 * specific ontologies. Return results in a form of a single page (of
	 * specified size). If maxNumHits is null, the default value from the
	 * configuation file is used.
	 * 
	 * @param expr
	 * @param ontologyIds
	 * @param includeProperties
	 * @param isExactMatch
	 * @param pageSize
	 * @param pageNum
	 * @param maxNumHits
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(String expr,
			Collection<Integer> ontologyIds, boolean includeProperties,
			boolean isExactMatch, Integer pageSize, Integer pageNum,
			Integer maxNumHits) throws Exception {
		Query query = generateLuceneSearchQuery(ontologyIds, expr,
				includeProperties, isExactMatch);

		return executeQuery(query, pageSize, pageNum, maxNumHits);
	}

	/**
	 * Execute a search query for a given expression, limiting search to the
	 * specific ontologies. Return ALL results in a form of a single page. If
	 * maxNumHits is null, the default value from the configuation file is used.
	 * 
	 * @param expr
	 * @param ontologyIds
	 * @param includeProperties
	 * @param isExactMatch
	 * @param maxNumHits
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(String expr,
			Collection<Integer> ontologyIds, boolean includeProperties,
			boolean isExactMatch, Integer maxNumHits) throws Exception {
		Query query = generateLuceneSearchQuery(ontologyIds, expr,
				includeProperties, isExactMatch);

		return executeQuery(query, maxNumHits);
	}

	/**
	 * Execute a search from an already constructed Query object. Return results
	 * in a form of a single page (of specified size). If maxNumHits is null,
	 * the default value from the configuation file is used.
	 * 
	 * @param query
	 * @param maxNumHits
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(Query query, Integer maxNumHits)
			throws Exception {
		return executeQuery(query, null, null, maxNumHits);
	}

	/**
	 * Execute a search from an already constructed Query object. Return ALL
	 * results in a form of a single page. If maxNumHits is null, the default
	 * value from the configuation file is used.
	 * 
	 * @param query
	 * @param pageSize
	 * @param pageNum
	 * @param maxNumHits
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(Query query, Integer pageSize,
			Integer pageNum, Integer maxNumHits) throws Exception {
		long start = System.currentTimeMillis();
		String resultsKey = composeCacheKey(query, maxNumHits);
		boolean fromCache = true;
		SearchResultListBean searchResults = searchResultCache.get(resultsKey);

		if (searchResults == null) {
			searchResults = runQuery(query, maxNumHits);
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
		BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);

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
	 * Constructs the contents field clause for "regular (non-exact) match"
	 * searches and adds it to the main query
	 * 
	 * @param expr
	 * @param query
	 * @throws IOException
	 */
	private void addContentsClauseContains(String expr, BooleanQuery query)
			throws IOException {
		try {
			PrefixQuery q = new PrefixQuery(searcher.getIndexReader());
			q.parsePrefixQuery(SearchIndexBean.CONTENTS_FIELD_LABEL, expr);
//			 q.parseStartsWithPrefixQuery((PrefixQuery.isMultiWord(expr)) ?
//			 SearchIndexBean.LITERAL_CONTENTS_FIELD_LABEL :
//			 SearchIndexBean.CONTENTS_FIELD_LABEL, expr);
			query.add(q, BooleanClause.Occur.MUST);
		} catch (Exception e) {
			IOException ioe = new IOException(e.getMessage());
			ioe.initCause(e);
			throw ioe;
		}
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
}