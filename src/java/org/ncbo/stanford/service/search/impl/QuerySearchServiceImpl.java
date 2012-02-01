package org.ncbo.stanford.service.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.search.OntologyHitBean;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.ncbo.stanford.enumeration.ConceptTypeEnum;
import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.manager.retrieval.OntologyRetrievalManager;
import org.ncbo.stanford.service.search.AbstractSearchService;
import org.ncbo.stanford.service.search.QuerySearchService;
import org.ncbo.stanford.util.helper.StringHelper;
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

	private static final Log log = LogFactory
			.getLog(QuerySearchServiceImpl.class);

	/**
	 * Separates the query from the maxNumHits in the cache key
	 */
	private static final String CACHE_KEY_SEPARATOR = "@@@@@";

	// non-injected properties
	private IndexSearcher searcher = null;
	private AtomicInteger activeSearches = new AtomicInteger(0);	
	
	
//	public static void main(String[] args) {
//		try {
//			String expr = "no";
//			String expr = "blood";
//			String expr = "Blue_Nevus-Like_Melanoma";
//			String expr = "Interferon-Alfa_Lu-177-Monoclonal-Antibody-CC49_Pa";
//			String expr = "Swiss_Albinos_City_of_Hope_Med_Ctr";
//			String expr = "Can of vul and vag";
//			String expr = "lun";
//			String expr = "algorith";
//			String expr = "posi";
//			String expr = "predominately round";
//			String expr = "monadic                    	Quality of an object*";
//			String expr = "CHEBI:16069";
//			String expr = "blood-vein";
//			String expr = "Interferon-Alfa_Lu-177-Monoclonal-Antibody-CC49";
//			String expr = "*Clarke's nu*";
//			String expr = "multiply";
//			Collection<Integer> ontologyIds = new ArrayList<Integer>(0);
//			ontologyIds.add(1649);
//			ontologyIds.add(1104);
//			ontologyIds.add(1057);
//			ontologyIds.add(1070);
//			ontologyIds.add(1107);
//			ontologyIds.add(1321); // Nemo
//
//			boolean includeProperties = false;
//			boolean isExactMatch = false;
//			Integer maxNumHits = 2250;
//
//			Collection<String> objectTypes = new ArrayList<String>(0);
//			String indexPath = "/apps/bmir.apps/bioportal_resources/searchindex";
//			QuerySearchServiceImpl ss = new QuerySearchServiceImpl();
//
//			Version ver = Version.LUCENE_24;
//			ss.setLuceneVersion(ver);
//			ss.setAnalyzer(new StandardAnalyzer(ver, Collections.emptySet()));
//			ss.setIndexPath(indexPath);
//
//			Query q = ss.generateLuceneSearchQuery(ontologyIds, objectTypes,
//					expr, includeProperties, isExactMatch);
//			System.out.println("q: " + q);
//
//			long start = System.currentTimeMillis();
//			SearchResultListBean results = ss.runQuery(q, maxNumHits,
//					ontologyIds, null, false);
//			long stop = System.currentTimeMillis();
//			System.out.println("Excecution Time: " + (double) (stop - start)
//					/ 1000 + " seconds.");
//
//			System.out.println("Num Hits: " + results.size());
//			System.out.println(Jestr.str(results));
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	// public static void main(String[] args) {
	// try {
	// IndexSearcher searcher = new IndexSearcher(
	// "/apps/bmir.apps/bioportal_resources/searchindex");
	// TermEnum terms = searcher.getIndexReader().terms(
	// new Term("conceptId", ""));
	// int numTerms = 0;
	//
	// while ("conceptId".equals(terms.term().field())) {
	// numTerms++;
	//
	// if (!terms.next())
	// break;
	// }
	//
	// terms.close();
	//
	// System.out.println("Num Concepts: " + numTerms);
	//
	// } catch (CorruptIndexException e) { // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) { // TODO Auto-generated
	// e.printStackTrace();
	// }
	// }

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
			Integer pageNum, Integer maxNumHits, Boolean includeDefinitions)
			throws Exception {
		return executeQuery(expr, null, null, includeProperties, isExactMatch,
				pageSize, pageNum, maxNumHits, null, includeDefinitions);
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
			boolean includeProperties, boolean isExactMatch,
			Integer maxNumHits, Boolean includeDefinitions) throws Exception {
		return executeQuery(expr, null, null, includeProperties, isExactMatch,
				maxNumHits, null, includeDefinitions);
	}

	/**
	 * Execute a search query for a given expression, limiting search to the
	 * specific ontologies. Return results in a form of a single page (of
	 * specified size). If maxNumHits is null, the default value from the
	 * configuation file is used.
	 * 
	 * @param expr
	 * @param ontologyIds
	 * @param objectTypes
	 * @param includeProperties
	 * @param isExactMatch
	 * @param pageSize
	 * @param pageNum
	 * @param maxNumHits
	 * @param subtreeRootConceptId
	 *            - optional root concept id for sub-tree search
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(String expr,
			Collection<Integer> ontologyIds, Collection<String> objectTypes,
			boolean includeProperties, boolean isExactMatch, Integer pageSize,
			Integer pageNum, Integer maxNumHits, String subtreeRootConceptId,
			Boolean includeDefinitions) throws Exception {
		Query query = generateLuceneSearchQuery(ontologyIds, objectTypes, expr,
				includeProperties, isExactMatch);

		return executeQuery(query, pageSize, pageNum, maxNumHits, ontologyIds,
				subtreeRootConceptId, includeDefinitions);
	}

	/**
	 * Execute a search query for a given expression, limiting search to the
	 * specific ontologies. Return ALL results in a form of a single page. If
	 * maxNumHits is null, the default value from the configuation file is used.
	 * 
	 * @param expr
	 * @param ontologyIds
	 * @param objectTypes
	 * @param includeProperties
	 * @param isExactMatch
	 * @param maxNumHits
	 * @param subtreeRootConceptId
	 *            - optional root concept id for sub-tree search
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(String expr,
			Collection<Integer> ontologyIds, Collection<String> objectTypes,
			boolean includeProperties, boolean isExactMatch,
			Integer maxNumHits, String subtreeRootConceptId,
			Boolean includeDefinitions) throws Exception {
		Query query = generateLuceneSearchQuery(ontologyIds, objectTypes, expr,
				includeProperties, isExactMatch);

		return executeQuery(query, maxNumHits, ontologyIds,
				subtreeRootConceptId, includeDefinitions);
	}

	/**
	 * Execute a search from an already constructed Query object. Return ALL
	 * results in a form of a single page. If maxNumHits is null, the default
	 * value from the configuation file is used.
	 * 
	 * @param query
	 * @param maxNumHits
	 * @param ontologyIds
	 *            - optional list of ontology ids
	 * @param subtreeRootConceptId
	 *            - optional root concept id for sub-tree search
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(Query query, Integer maxNumHits,
			Collection<Integer> ontologyIds, String subtreeRootConceptId,
			Boolean includeDefinitions) throws Exception {
		return executeQuery(query, null, null, maxNumHits, ontologyIds,
				subtreeRootConceptId, includeDefinitions);
	}

	/**
	 * Execute a search from an already constructed Query object. Return results
	 * in a form of a single page (of specified size). If maxNumHits is null,
	 * the default value from the configuation file is used.
	 * 
	 * @param query
	 * @param pageSize
	 * @param pageNum
	 * @param maxNumHits
	 * @param ontologyIds
	 *            - optional list of ontology ids
	 * @param subtreeRootConceptId
	 *            - optional root concept id for sub-tree search
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(Query query, Integer pageSize,
			Integer pageNum, Integer maxNumHits,
			Collection<Integer> ontologyIds, String subtreeRootConceptId,
			Boolean includeDefinitions) throws Exception {
		long start = System.currentTimeMillis();
		String resultsKey = composeCacheKey(query, maxNumHits,
				subtreeRootConceptId, includeDefinitions);
		boolean fromCache = true;
		SearchResultListBean searchResults = searchResultCache.get(resultsKey);

		if (searchResults == null) {
			searchResults = runQuery(query, maxNumHits, ontologyIds,
					subtreeRootConceptId, includeDefinitions);
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

		if (log.isDebugEnabled()) {
			long stop = System.currentTimeMillis();
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
	 * @param objectTypes
	 * @param expr
	 * @param includeProperties
	 * @param isExactMatch
	 * @return
	 * @throws IOException
	 */
	public Query generateLuceneSearchQuery(Collection<Integer> ontologyIds,
			Collection<String> objectTypes, String expr,
			boolean includeProperties, boolean isExactMatch) throws IOException {
		BooleanQuery query = new BooleanQuery();
		BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);

		if (isExactMatch) {
			addContentsClauseExact(expr, query);
		} else {
			addContentsClauseContains(expr, query);
		}

		addOntologyIdsClause(ontologyIds, query);
		addObjectTypesClause(objectTypes, query);
		addPropertiesClause(includeProperties, query);

		return query;
	}
	
	/**
	 * Executes a query against the Lucene index. Does not use caching
	 * 
	 * @param query
	 * @param maxNumHits
	 * @param ontologyIds
	 *            - optional list of ontology ids
	 * @param subtreeRootConceptId
	 *            - optional root concept id for sub-tree search
	 * @return
	 * @throws Exception
	 */
	public SearchResultListBean runQuery(Query query, Integer maxNumHits,
			Collection<Integer> ontologyIds, String subtreeRootConceptId,
			Boolean includeDefinitions) throws Exception {
		TopFieldDocs docs = null;
		
		try {
			reloadSearcher();
			docs = searcher.search(query, null, getMaxNumHits(maxNumHits),
					getSortFields());
		} catch (OutOfMemoryError e) {
			throw new Exception(e);
		} finally {
			decrementSearches();
		}

		ScoreDoc[] hits = docs.scoreDocs;
		SearchResultListBean searchResults = new SearchResultListBean(0);

		if (ontologyIds == null) {
			ontologyIds = new ArrayList<Integer>(0);
		}

		populateSearchResults(hits, searchResults, ontologyIds,
				subtreeRootConceptId, includeDefinitions);

		populateEmptyOntologyResults(searchResults, ontologyIds);

		if (log.isDebugEnabled()) {
			log.debug("Total All Hits: " + hits.length);
			log.debug("Total Unique Hits: " + searchResults.size());
		}

		return searchResults;
	}
	
	/**
	 * Checks whether ontology is present in the index and returns basic info
	 * such as ontologyVersionId and ontologyDisplayLabel
	 * 
	 * @param ontologyId
	 * @return
	 * @throws Exception
	 */
	public OntologyHitBean checkOntologyInIndex(Integer ontologyId)
			throws Exception {
		Integer ontologyVersionId = null;
		String ontologyDisplayLabel = null;
		BooleanQuery query = new BooleanQuery();
		query.add(new TermQuery(generateOntologyIdTerm(ontologyId)),
				BooleanClause.Occur.MUST);

		try {
			reloadSearcher();
			TopDocs docs = searcher.search(query, null, 1);
			ScoreDoc[] hits = docs.scoreDocs;

			if (hits.length > 0) {
				int docId = hits[0].doc;
				Document doc = searcher.doc(docId);
				ontologyVersionId = new Integer(doc
						.get(SearchIndexBean.ONTOLOGY_VERSION_ID_FIELD_LABEL));
				ontologyDisplayLabel = doc
						.get(SearchIndexBean.ONTOLOGY_DISPLAY_LABEL_FIELD_LABEL);
			}

		} finally {
			decrementSearches();
		}

		return new OntologyHitBean(ontologyVersionId, ontologyId,
				ontologyDisplayLabel, null);
	}
	
	private String composeCacheKey(Query query, Integer maxNumHits,
			String subtreeRootConceptId, Boolean includeDefinitions) {
		return query
				+ CACHE_KEY_SEPARATOR
				+ getMaxNumHits(maxNumHits)
				+ CACHE_KEY_SEPARATOR
				+ includeDefinitions.toString()
				+ (StringHelper.isNullOrNullString(subtreeRootConceptId) ? ""
						: CACHE_KEY_SEPARATOR + subtreeRootConceptId);
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
	
	private void populateEmptyOntologyResults(
			SearchResultListBean searchResults,
			Collection<Integer> noHitsOntologyIds) throws Exception {
		for (Integer ontologyId : noHitsOntologyIds) {
			OntologyHitBean ontologyHit = checkOntologyInIndex(ontologyId);
			searchResults.addEmptyOntologyHit(ontologyId, ontologyHit);
		}
	}
	
	private void populateSearchResults(ScoreDoc[] hits,
			SearchResultListBean searchResults,
			Collection<Integer> ontologyIds, String subtreeRootConceptId,
			Boolean includeDefinitions) throws Exception {
		List<String> uniqueDocs = new ArrayList<String>(0);
		long start = System.currentTimeMillis();
		Document doc = null;
		Integer ontologyVersionId = null;
		int docId = 0;
		OntologyBean ob = null;
		OntologyRetrievalManager mgr = null;
		boolean hasSubtreeRoot = !StringHelper
				.isNullOrNullString(subtreeRootConceptId);

		if (hits.length > 0) {
			if (hasSubtreeRoot) {
				docId = hits[0].doc;
				doc = searcher.doc(docId);
				ontologyVersionId = new Integer(doc
						.get(SearchIndexBean.ONTOLOGY_VERSION_ID_FIELD_LABEL));
				ob = ontologyMetadataManager
						.findOntologyOrViewVersionById(ontologyVersionId);

				if (ob == null) {
					throw new OntologyNotFoundException(
							OntologyNotFoundException.DEFAULT_MESSAGE
									+ " (Version Id: " + ontologyVersionId
									+ ")");
				}

				mgr = getRetrievalManager(ob);
			}

			for (int i = 0; i < hits.length; i++) {
				docId = hits[i].doc;
				doc = searcher.doc(docId);
				ontologyVersionId = new Integer(doc
						.get(SearchIndexBean.ONTOLOGY_VERSION_ID_FIELD_LABEL));
				String conceptId = doc
						.get(SearchIndexBean.CONCEPT_ID_FIELD_LABEL);
				String ontologyDisplayLabel = doc
						.get(SearchIndexBean.ONTOLOGY_DISPLAY_LABEL_FIELD_LABEL);
				Integer ontologyId = new Integer(doc
						.get(SearchIndexBean.ONTOLOGY_ID_FIELD_LABEL));
				Byte isObsolete = 0;

				try {
					isObsolete = new Byte(doc
							.get(SearchIndexBean.IS_OBSOLETE_FIELD_LABEL));
				} catch (NumberFormatException e) {
					// Do nothing
				}

				String uniqueIdent = ontologyId + "_" + conceptId;

				if (!uniqueDocs.contains(uniqueIdent)) {
					if (!hasSubtreeRoot
							|| subtreeRootConceptId.equalsIgnoreCase(conceptId)
							|| mgr.hasParent(ob, conceptId,
									subtreeRootConceptId)) {
						ontologyIds.remove(ontologyId);
						SearchBean searchResult = new SearchBean(
								ontologyVersionId,
								ontologyId,
								ontologyDisplayLabel,
								SearchRecordTypeEnum
										.getFromLabel(doc
												.get(SearchIndexBean.RECORD_TYPE_FIELD_LABEL)),
								ConceptTypeEnum
										.getFromLabel(doc
												.get(SearchIndexBean.OBJECT_TYPE_FIELD_LABEL)),
								conceptId,
								doc
										.get(SearchIndexBean.CONCEPT_ID_SHORT_FIELD_LABEL),
								doc
										.get(SearchIndexBean.PREFERRED_NAME_FIELD_LABEL),
								doc.get(SearchIndexBean.CONTENTS_FIELD_LABEL),
								null, isObsolete);

						if (includeDefinitions) {
							// Make sure that getting the definition doesn't
							// break the search call
							try {
								ClassBean concept = conceptService.findConcept(
										ontologyVersionId, conceptId, null,
										false, true, false);

								if (concept != null)
									searchResult.setDefinition(StringUtils
											.join(concept.getDefinitions(),
													". "));
							} catch (Exception e) {
								// Do nothing
							}
						}

						searchResults.add(searchResult);
						searchResults.addOntologyHit(ontologyVersionId,
								ontologyId, ontologyDisplayLabel);

						uniqueDocs.add(uniqueIdent);

						if (log.isDebugEnabled()) {
							log.debug(getHitAsString(hits[i].score,
									searchResult));
						}
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

	private OntologyRetrievalManager getRetrievalManager(OntologyBean ontology) {
		String formatHandler = ontologyFormatHandlerMap.get(ontology
				.getFormat().toUpperCase());
		return ontologyRetrievalHandlerMap.get(formatHandler);
	}

	private String getHitAsString(float score, SearchBean searchResult) {
		return score + " | " + searchResult.getContents() + ", Type: "
				+ searchResult.getRecordType() + ", OntologyId: "
				+ searchResult.getOntologyId() + ", PrefName: "
				+ searchResult.getPreferredName() + ", Ontology: "
				+ searchResult.getOntologyDisplayLabel() + ", Concept Id: "
				+ searchResult.getConceptId() + ", Concept Id Short: "
				+ searchResult.getConceptIdShort();
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
			PrefixQuery q = new PrefixQuery(luceneVersion, searcher
					.getIndexReader(), analyzer);
			q.parsePrefixQuery(SearchIndexBean.CONTENTS_FIELD_LABEL, expr);
			// q.parseStartsWithPrefixQuery((PrefixQuery.isMultiWord(expr)) ?
			// SearchIndexBean.LITERAL_CONTENTS_FIELD_LABEL :
			// SearchIndexBean.CONTENTS_FIELD_LABEL, expr);
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

	/**
	 * Adds the clause that limits the search to the given object types (i.e.
	 * class, individual, property)
	 * 
	 * @param objectTypes
	 * @param query
	 */
	private void addObjectTypesClause(Collection<String> objectTypes,
			BooleanQuery query) {
		if (objectTypes != null && !objectTypes.isEmpty()) {
			query.add(generateObjectTypesQuery(objectTypes),
					BooleanClause.Occur.MUST);
		}
	}

	private synchronized void decrementSearches() {
		activeSearches.decrementAndGet();
	}

	/**
	 * Reloads the searcher if index has changed
	 * 
	 * @throws IOException
	 */
	private synchronized void reloadSearcher() throws IOException {
		if (searcher != null && !searcher.getIndexReader().isCurrent()
				&& activeSearches.get() == 0) {
			searcher.close();
			searcher = null;
		}

		if (searcher == null) {
			searcher = new IndexSearcher(indexDir, true);
		}
		activeSearches.incrementAndGet();
	}	
	
	/**
	 * Sets the index path and creates a new instance of searcher
	 * 
	 * @param indexPath
	 *            the indexPath to set
	 */
	@Override
	public void setIndexPath(String indexPath) {
		super.setIndexPath(indexPath);
		
		try {
			searcher = new IndexSearcher(indexDir, true);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("Could not initialize IndexSearcher at startup: "
					+ e);
		}
	}
}