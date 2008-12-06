package org.ncbo.stanford.service.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopFieldDocs;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;
import org.ncbo.stanford.util.cache.expiration.system.ExpirationSystem;

/**
 * Abstract class to contain functionality common to both query and indexing
 * services
 * 
 * 
 * @author Michael Dorf
 * 
 */
public class AbstractSearchService {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(AbstractSearchService.class);

	protected Analyzer analyzer;
	protected String indexPath;
	protected int maxNumHits;
	protected ExpirationSystem<String, SearchResultListBean> searchResultCache;

	// non-injected properties
	private IndexSearcher searcher = null;
	private Date openIndexDate;
	protected Object createSearcherLock = new Object();

	/**
	 * Executes a query against the Lucene index
	 * 
	 * @param query
	 * @return
	 * @throws Exception
	 */
	protected SearchResultListBean runQuery(Query query) throws Exception {
		// check whether the index has changed and if so, reloads the searcher
		// reloading searcher must be synchronized to avoid null searchers
		synchronized (createSearcherLock) {
			if (searcher == null || hasNewerIndexFile()) {
				reloadSearcher();
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("Executing Query: " + query);
		}

		TopFieldDocs docs = null;

		try {
			docs = searcher.search(query, null, maxNumHits, getSortFields());
		} catch (OutOfMemoryError e) {
			throw new Exception(e);
		}

		ScoreDoc[] hits = docs.scoreDocs;

		List<String> uniqueDocs = new ArrayList<String>();
		SearchResultListBean searchResults = new SearchResultListBean(0);

		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document doc = searcher.doc(docId);
			String conceptId = doc.get(SearchIndexBean.CONCEPT_ID_FIELD_LABEL);
			Integer ontologyVersionId = new Integer(doc
					.get(SearchIndexBean.ONTOLOGY_VERSION_ID_FIELD_LABEL));
			Integer ontologyId = new Integer(doc
					.get(SearchIndexBean.ONTOLOGY_ID_FIELD_LABEL));
			String ontologyDisplayLabel = doc
					.get(SearchIndexBean.ONTOLOGY_DISPLAY_LABEL_FIELD_LABEL);

			if (!uniqueDocs.contains(conceptId)) {
				SearchBean searchResult = new SearchBean(ontologyVersionId,
						ontologyId, ontologyDisplayLabel,
						SearchRecordTypeEnum.getFromLabel(doc
								.get(SearchIndexBean.RECORD_TYPE_FIELD_LABEL)),
						conceptId,
						doc.get(SearchIndexBean.CONCEPT_ID_SHORT_FIELD_LABEL),
						doc.get(SearchIndexBean.PREFERRED_NAME_FIELD_LABEL),
						doc.get(SearchIndexBean.CONTENTS_FIELD_LABEL), null);
				searchResults.add(searchResult);
				searchResults.addOntologyHit(ontologyVersionId, ontologyId,
						ontologyDisplayLabel);

				uniqueDocs.add(conceptId);
			}
		}

		return searchResults;
	}

	/**
	 * Reloads the searcher, disposes of the old searcher
	 * 
	 * @throws IOException
	 */
	protected void reloadSearcher() throws IOException {
		if (log.isDebugEnabled()) {
			log.debug("Index file has changed. Reloading searcher...");
		}

		if (searcher != null) {
			searcher.close();
		}

		createSearcher();
	}

	/**
	 * Returns the sort fields for the query
	 * 
	 * @return
	 */
	private Sort getSortFields() {
		SortField[] fields = {
				SortField.FIELD_SCORE,
				new SortField(SearchIndexBean.RECORD_TYPE_FIELD_LABEL,
						SortField.STRING),
				new SortField(SearchIndexBean.PREFERRED_NAME_FIELD_LABEL,
						SortField.STRING) };

		return new Sort(fields);
	}

	/**
	 * Creates a new instance of the searcher
	 * 
	 * @throws IOException
	 */
	private void createSearcher() throws IOException {
		searcher = new IndexSearcher(indexPath);
		openIndexDate = getCurrentIndexDate();
	}

	/**
	 * Determines whether the index file has changed
	 * 
	 * @return
	 */
	private boolean hasNewerIndexFile() {
		try {
			if (getCurrentIndexDate().after(openIndexDate)) {
				return true;
			}
		} catch (Exception e) { // no index file found
		}

		return false;
	}

	/**
	 * @return creation date of current used search index
	 */
	private Date getCurrentIndexDate() throws IOException {
		return new Date(IndexReader.getCurrentVersion(indexPath));
	}

	/**
	 * @param indexPath
	 *            the indexPath to set
	 */
	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;

		try {
			createSearcher();
		} catch (IOException e) {
			e.printStackTrace();
			log.error("Could not create IndexSearcher at startup: " + e);
		}
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
	 * @param maxNumHits
	 *            the maxNumHits to set
	 */
	public void setMaxNumHits(Integer maxNumHits) {
		this.maxNumHits = maxNumHits;
	}

	/**
	 * @param searchResultCache
	 *            the searchResultCache to set
	 */
	public void setSearchResultCache(
			ExpirationSystem<String, SearchResultListBean> searchResultCache) {
		this.searchResultCache = searchResultCache;
	}
}
