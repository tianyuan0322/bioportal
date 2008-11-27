package org.ncbo.stanford.service.search.impl;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopFieldDocs;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;
import org.ncbo.stanford.service.search.QueryService;
import org.ncbo.stanford.util.cache.expiration.system.ExpirationSystem;
import org.ncbo.stanford.util.helper.StringHelper;

public class QueryServiceImpl implements QueryService {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(QueryServiceImpl.class);
	private Analyzer analyzer;
	private String indexPath;
	private int maxNumHits;
	private ExpirationSystem<Integer, SearchResultListBean> searchResultCache;

	// non-injected properties
	private IndexSearcher searcher = null;
	private Date openIndexDate;
	private Object createSearcherLock = new Object();

	public SearchResultListBean executeQuery(String expr,
			boolean includeProperties, boolean isExactMatch) throws IOException {
		return executeQuery(expr, null, includeProperties, isExactMatch);
	}

	public SearchResultListBean executeQuery(String expr,
			Collection<Integer> ontologyIds, boolean includeProperties,
			boolean isExactMatch) throws IOException {
		Query query = generateLuceneSearchQuery(ontologyIds, expr,
				includeProperties, isExactMatch);

		return executeQuery(query);
	}

	public SearchResultListBean executeQuery(Query query) throws IOException {
		long start = System.currentTimeMillis();

		synchronized (createSearcherLock) {
			if (searcher == null) {
				createSearcher();
			} else if (hasNewerIndexFile()) {
				reloadSearcher();
			}
		}

		TopFieldDocs docs = searcher.search(query, null, maxNumHits,
				getSortFields());
		ScoreDoc[] hits = docs.scoreDocs;

		long stop = System.currentTimeMillis();

		List<String> uniqueDocs = new ArrayList<String>();
		SearchResultListBean searchResults = new SearchResultListBean(0);

		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document doc = searcher.doc(docId);
			String conceptId = doc.get(SearchIndexBean.CONCEPT_ID_FIELD_LABEL);
			Integer ontologyId = new Integer(doc
					.get(SearchIndexBean.ONTOLOGY_ID_FIELD_LABEL));

			if (!uniqueDocs.contains(conceptId)) {
				SearchBean searchResult = new SearchBean(
						new Integer(
								doc
										.get(SearchIndexBean.ONTOLOGY_VERSION_ID_FIELD_LABEL)),
						ontologyId,
						doc
								.get(SearchIndexBean.ONTOLOGY_DISPLAY_LABEL_FIELD_LABEL),
						SearchRecordTypeEnum.getFromLabel(doc
								.get(SearchIndexBean.RECORD_TYPE_FIELD_LABEL)),
						conceptId,
						doc.get(SearchIndexBean.CONCEPT_ID_SHORT_FIELD_LABEL),
						doc.get(SearchIndexBean.PREFERRED_NAME_FIELD_LABEL),
						doc.get(SearchIndexBean.CONTENTS_FIELD_LABEL),
						doc.get(SearchIndexBean.LITERAL_CONTENTS_FIELD_LABEL));
				searchResults.add(searchResult);
				searchResults.addOntologyHit(ontologyId);

				uniqueDocs.add(conceptId);

				if (log.isDebugEnabled()) {
					DecimalFormat score = new DecimalFormat("0.00");
					log.debug("\n" + score.format(hits[i].score) + " | "
							+ searchResult);
				}
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("Query: " + query);
			log.debug("Hits: " + hits.length + ", Unique Hits: "
					+ uniqueDocs.size());
			log.debug("Excecution Time: " + (double) (stop - start) / 1000
					+ " seconds.");
		}

		return searchResults;
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

	private Sort getSortFields() {
		SortField[] fields = { SortField.FIELD_SCORE,
				new SortField(SearchIndexBean.RECORD_TYPE_FIELD_LABEL),
				new SortField(SearchIndexBean.PREFERRED_NAME_FIELD_LABEL) };

		return new Sort(fields);
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

	private void createSearcher() throws IOException {
		searcher = new IndexSearcher(indexPath);
		openIndexDate = getCurrentIndexDate();
	}

	private void reloadSearcher() throws IOException {
		if (log.isDebugEnabled()) {
			log.debug("Index file has changed. Reloading searcher...");
		}
		
		searcher.close();
		createSearcher();
	}

	private boolean hasNewerIndexFile() {
		try {
			if (getCurrentIndexDate().after(openIndexDate)) {
				return true;
			}
		} catch (IOException e) { // no index file exist
		}

		return false;
	}

	/**
	 * @return Creation date of current used search index.
	 */
	private Date getCurrentIndexDate() throws IOException {
		return new Date(IndexReader.getCurrentVersion(indexPath));
	}

	/**
	 * @return the analyzer
	 */
	public Analyzer getAnalyzer() {
		return analyzer;
	}

	/**
	 * @param analyzer
	 *            the analyzer to set
	 */
	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	/**
	 * @param indexPath
	 *            the indexPath to set
	 */
	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}

	/**
	 * @param searchResultCache
	 *            the searchResultCache to set
	 */
	public void setSearchResultCache(
			ExpirationSystem<Integer, SearchResultListBean> searchResultCache) {
		this.searchResultCache = searchResultCache;
	}

	/**
	 * @param maxNumHits
	 *            the maxNumHits to set
	 */
	public void setMaxNumHits(Integer maxNumHits) {
		this.maxNumHits = maxNumHits;
	}
}