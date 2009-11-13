package org.ncbo.stanford.service.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
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
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.manager.retrieval.OntologyRetrievalManager;
import org.ncbo.stanford.manager.search.OntologySearchManager;
import org.ncbo.stanford.util.cache.expiration.system.ExpirationSystem;
import org.ncbo.stanford.util.helper.StringHelper;

/**
 * Abstract class to contain functionality common to both query and indexing
 * services
 * 
 * 
 * @author Michael Dorf
 * 
 */
public abstract class AbstractSearchService {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(AbstractSearchService.class);

	/**
	 * Separates the query from the maxNumHits in the cache key
	 */
	private static final String CACHE_KEY_SEPARATOR = "@@@@@";

	protected Analyzer analyzer;
	protected String indexPath;
	protected int defMaxNumHits;
	protected ExpirationSystem<String, SearchResultListBean> searchResultCache;
	protected OntologyMetadataManager ontologyMetadataManagerProtege;
	protected Map<String, OntologyRetrievalManager> ontologyRetrievalHandlerMap = new HashMap<String, OntologyRetrievalManager>(
			0);
	protected Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>(
			0);
	protected Map<String, OntologySearchManager> ontologySearchHandlerMap = new HashMap<String, OntologySearchManager>(
			0);

	// non-injected properties
	protected IndexSearcher searcher = null;
	private Date openIndexDate;
	private Object createSearcherLock = new Object();

	/**
	 * Executes a query against the Lucene index. Does not use caching
	 * 
	 * @param query
	 * @param maxNumHits
	 * @param subtreeRootConceptId -
	 *            optional root concept id for sub-tree search
	 * @return
	 * @throws Exception
	 */
	public SearchResultListBean runQuery(Query query, Integer maxNumHits,
			String subtreeRootConceptId) throws Exception {
		// check whether the index has changed and if so, reloads the searcher
		// reloading searcher must be synchronized to avoid null searchers
		synchronized (createSearcherLock) {
			if (searcher == null || hasNewerIndexFile()) {
				reloadSearcher();
			}
		}

		TopFieldDocs docs = null;

		try {
			docs = searcher.search(query, null, getMaxNumHits(maxNumHits),
					getSortFields());
		} catch (OutOfMemoryError e) {
			throw new Exception(e);
		}

		ScoreDoc[] hits = docs.scoreDocs;
		SearchResultListBean searchResults = new SearchResultListBean(0);

		if (StringHelper.isNullOrNullString(subtreeRootConceptId)) {
			populateSearchResults(hits, searchResults);
		} else {
			populateSearchResults(hits, searchResults, subtreeRootConceptId);
		}

		if (log.isDebugEnabled()) {
			log.debug("Total All Hits: " + hits.length);
			log.debug("Total Unique Hits: " + searchResults.size());
		}

		return searchResults;
	}

	/**
	 * Empty search results cache
	 */
	public void emptySearchCache() {
		if (log.isDebugEnabled()) {
			log.debug("Emptying cache...");
		}

		searchResultCache.clear();
	}

	/**
	 * Reload search results cache by re-running all queries in it and
	 * re-populating it with new results
	 */
	public void reloadSearchCache() {
		long start = 0;
		long stop = 0;

		if (log.isDebugEnabled()) {
			log.debug("Reloading cache...");
			start = System.currentTimeMillis();
		}

		QueryParser parser = new QueryParser(
				SearchIndexBean.CONTENTS_FIELD_LABEL, analyzer);
		Set<String> keys = searchResultCache.getKeys();
		searchResultCache.clear();

		for (String fullKey : keys) {
			SearchResultListBean results = null;
			String[] splitKey = parseCacheKey(fullKey);

			try {
				results = runQuery(parser.parse(splitKey[0]), Integer
						.parseInt(splitKey[1]), splitKey[2]);
			} catch (Exception e) {
				results = null;
				e.printStackTrace();
				log.error("Error while reloading cache: " + e);
			}

			if (results != null) {
				searchResultCache.put(fullKey, results);
			}
		}

		if (log.isDebugEnabled()) {
			stop = System.currentTimeMillis(); // stop timing
			log.debug("Finished reloading cache in " + (double) (stop - start)
					/ 1000 + " seconds.");
		}
	}

	private void populateSearchResults(ScoreDoc[] hits,
			SearchResultListBean searchResults) throws Exception {
		List<String> uniqueDocs = new ArrayList<String>(0);
		long start = System.currentTimeMillis();

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
			String uniqueIdent = ontologyId + "_" + conceptId;

			if (!uniqueDocs.contains(uniqueIdent)) {
				SearchBean searchResult = new SearchBean(ontologyVersionId,
						ontologyId, ontologyDisplayLabel,
						SearchRecordTypeEnum.getFromLabel(doc
								.get(SearchIndexBean.RECORD_TYPE_FIELD_LABEL)),
						conceptId,
						doc.get(SearchIndexBean.CONCEPT_ID_SHORT_FIELD_LABEL),
						doc.get(SearchIndexBean.PREFERRED_NAME_FIELD_LABEL),
						doc.get(SearchIndexBean.CONTENTS_FIELD_LABEL));
				searchResults.add(searchResult);
				searchResults.addOntologyHit(ontologyVersionId, ontologyId,
						ontologyDisplayLabel);

				uniqueDocs.add(uniqueIdent);

				if (log.isDebugEnabled()) {
					log.debug(getHitAsString(hits[i].score, searchResult));
				}
			}
		}

		if (log.isDebugEnabled()) {
			long stop = System.currentTimeMillis();
			log.debug("Search Result Population Time (no branch): "
					+ (double) (stop - start) / 1000 + " seconds.");
		}
	}

	private void populateSearchResults(ScoreDoc[] hits,
			SearchResultListBean searchResults, String subtreeRootConceptId)
			throws Exception {
		List<String> uniqueDocs = new ArrayList<String>(0);
		long start = System.currentTimeMillis();

		if (hits.length > 0) {
			int docId = hits[0].doc;
			Document doc = searcher.doc(docId);
			Integer ontologyVersionId = new Integer(doc
					.get(SearchIndexBean.ONTOLOGY_VERSION_ID_FIELD_LABEL));
			OntologyBean ob = ontologyMetadataManagerProtege
					.findOntologyOrViewVersionById(ontologyVersionId);

			if (ob == null) {
				throw new OntologyNotFoundException(
						OntologyNotFoundException.DEFAULT_MESSAGE
								+ " (Version Id: " + ontologyVersionId + ")");
			}

			OntologyRetrievalManager mgr = getRetrievalManager(ob);

			for (int i = 0; i < hits.length; i++) {
				docId = hits[i].doc;
				doc = searcher.doc(docId);
				ontologyVersionId = new Integer(doc
						.get(SearchIndexBean.ONTOLOGY_VERSION_ID_FIELD_LABEL));
				String conceptId = doc
						.get(SearchIndexBean.CONCEPT_ID_FIELD_LABEL);
				Integer ontologyId = new Integer(doc
						.get(SearchIndexBean.ONTOLOGY_ID_FIELD_LABEL));
				String ontologyDisplayLabel = doc
						.get(SearchIndexBean.ONTOLOGY_DISPLAY_LABEL_FIELD_LABEL);
				String uniqueIdent = ontologyId + "_" + conceptId;

				if (!uniqueDocs.contains(uniqueIdent)
						&& (subtreeRootConceptId.equalsIgnoreCase(conceptId) || mgr
								.hasParent(ob, conceptId, subtreeRootConceptId))) {
					SearchBean searchResult = new SearchBean(
							ontologyVersionId,
							ontologyId,
							ontologyDisplayLabel,
							SearchRecordTypeEnum
									.getFromLabel(doc
											.get(SearchIndexBean.RECORD_TYPE_FIELD_LABEL)),
							conceptId,
							doc
									.get(SearchIndexBean.CONCEPT_ID_SHORT_FIELD_LABEL),
							doc.get(SearchIndexBean.PREFERRED_NAME_FIELD_LABEL),
							doc.get(SearchIndexBean.CONTENTS_FIELD_LABEL));
					searchResults.add(searchResult);
					searchResults.addOntologyHit(ontologyVersionId, ontologyId,
							ontologyDisplayLabel);

					uniqueDocs.add(uniqueIdent);

					if (log.isDebugEnabled()) {
						log.debug(getHitAsString(hits[i].score, searchResult));
					}
				}
			}
		}

		if (log.isDebugEnabled()) {
			long stop = System.currentTimeMillis();
			log.debug("Search Result Population Time (branch): "
					+ (double) (stop - start) / 1000 + " seconds.");
		}
	}

	private String getHitAsString(float score, SearchBean searchResult) {
		return score + " | " + searchResult.getContents() + ", Type: "
				+ searchResult.getRecordType() + ", PrefName: "
				+ searchResult.getPreferredName() + ", Ontology: "
				+ searchResult.getOntologyDisplayLabel() + ", Concept Id: "
				+ searchResult.getConceptId() + ", Concept Id Short: "
				+ searchResult.getConceptIdShort();
	}

	/**
	 * Constructs the query that limits the search to the given ontology ids
	 * 
	 * @param ontologyIds
	 * @return
	 */
	protected Query generateOntologyIdsQuery(Collection<Integer> ontologyIds) {
		BooleanQuery query = new BooleanQuery();

		for (Integer ontologyId : ontologyIds) {
			query.add(new TermQuery(generateOntologyIdTerm(ontologyId)),
					BooleanClause.Occur.SHOULD);
		}

		return query;
	}

	/**
	 * Constructs the term with the given ontology id
	 * 
	 * @param ontologyId
	 * @return
	 */
	protected Term generateOntologyIdTerm(Integer ontologyId) {
		return new Term(SearchIndexBean.ONTOLOGY_ID_FIELD_LABEL, ontologyId
				.toString());
	}

	/**
	 * Provides a display format for a list of ontologies
	 * 
	 * @param ontologies
	 * @return
	 */
	protected String getOntologyListDisplay(List<OntologyBean> ontologies) {
		StringBuffer sb = new StringBuffer(0);

		for (OntologyBean ontology : ontologies) {
			sb.append(getOntologyDisplay(ontology.getId(), ontology
					.getOntologyId(), ontology.getDisplayLabel(), ontology
					.getFormat()));
			sb.append(", ");
		}

		return sb.substring(0, sb.length() - 2);
	}

	/**
	 * Provides a display format for a single ontology
	 * 
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param displayLabel
	 * @param format
	 * @return
	 */
	protected String getOntologyDisplay(Integer ontologyVersionId,
			Integer ontologyId, String displayLabel, String format) {
		StringBuffer sb = new StringBuffer(0);

		sb.append(displayLabel);
		sb.append(" (Id: ");
		sb.append(ontologyVersionId);
		sb.append(", Ont Id: ");
		sb.append(ontologyId);
		sb.append(", Fmt: ");
		sb.append(format);
		sb.append(')');

		return sb.toString();
	}

	protected String[] parseCacheKey(String cacheKey) {
		String[] keys = cacheKey.split(CACHE_KEY_SEPARATOR);

		if (keys.length > 2 && keys[2] != null && keys[2].equals("null")) {
			keys[2] = null;
		}

		return keys;
	}

	protected String composeCacheKey(Query query, Integer maxNumHits,
			String subtreeRootConceptId) {
		return query + CACHE_KEY_SEPARATOR + getMaxNumHits(maxNumHits)
				+ CACHE_KEY_SEPARATOR + subtreeRootConceptId;
	}

	/**
	 * Sets the index path and creates a new instance of searcher
	 * 
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
	 * @param defMaxNumHits
	 *            the defMaxNumHits to set
	 */
	public void setDefMaxNumHits(int defMaxNumHits) {
		this.defMaxNumHits = defMaxNumHits;
	}

	/**
	 * @param searchResultCache
	 *            the searchResultCache to set
	 */
	public void setSearchResultCache(
			ExpirationSystem<String, SearchResultListBean> searchResultCache) {
		this.searchResultCache = searchResultCache;
	}

	/**
	 * Reloads the searcher, disposes of the old searcher
	 * 
	 * @throws IOException
	 */
	private void reloadSearcher() throws IOException {
		if (log.isDebugEnabled()) {
			log.debug("Index file has changed. Reloading searcher...");
		}

		if (searcher != null) {
			searcher.close();
		}

		createSearcher();
	}

	/**
	 * Method that always returns a valid maxNumHits
	 * 
	 * @param maxNumHits
	 * @return
	 */
	private Integer getMaxNumHits(Integer maxNumHits) {
		return (maxNumHits != null && maxNumHits > 0) ? maxNumHits
				: defMaxNumHits;
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
				new SortField(SearchIndexBean.PREFERRED_NAME_LC_FIELD_LABEL,
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

	private OntologyRetrievalManager getRetrievalManager(OntologyBean ontology) {
		String formatHandler = ontologyFormatHandlerMap.get(ontology
				.getFormat().toUpperCase());
		return ontologyRetrievalHandlerMap.get(formatHandler);
	}

	/**
	 * @param ontologyMetadataManagerProtege
	 *            the ontologyMetadataManagerProtege to set
	 */
	public void setOntologyMetadataManagerProtege(
			OntologyMetadataManager ontologyMetadataManagerProtege) {
		this.ontologyMetadataManagerProtege = ontologyMetadataManagerProtege;
	}

	/**
	 * @param ontologyRetrievalHandlerMap
	 *            the ontologyRetrievalHandlerMap to set
	 */
	public void setOntologyRetrievalHandlerMap(
			Map<String, OntologyRetrievalManager> ontologyRetrievalHandlerMap) {
		this.ontologyRetrievalHandlerMap = ontologyRetrievalHandlerMap;
	}

	/**
	 * @param ontologyFormatHandlerMap
	 *            the ontologyFormatHandlerMap to set
	 */
	public void setOntologyFormatHandlerMap(
			Map<String, String> ontologyFormatHandlerMap) {
		this.ontologyFormatHandlerMap = ontologyFormatHandlerMap;
	}

	/**
	 * @param ontologySearchHandlerMap
	 *            the ontologySearchHandlerMap to set
	 */
	public void setOntologySearchHandlerMap(
			Map<String, OntologySearchManager> ontologySearchHandlerMap) {
		this.ontologySearchHandlerMap = ontologySearchHandlerMap;
	}
}
