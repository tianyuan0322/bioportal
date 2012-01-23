package org.ncbo.stanford.service.search;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.manager.retrieval.OntologyRetrievalManager;
import org.ncbo.stanford.manager.search.OntologySearchManager;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.util.cache.expiration.system.ExpirationSystem;

/**
 * Abstract class to contain functionality common to both query and indexing
 * services
 * 
 * 
 * @author Michael Dorf
 * 
 */
public abstract class AbstractSearchService {

	private static final Log log = LogFactory
			.getLog(AbstractSearchService.class);

	protected Analyzer analyzer;
	protected int defMaxNumHits;
	protected ExpirationSystem<String, SearchResultListBean> searchResultCache;
	protected OntologyMetadataManager ontologyMetadataManager;
	protected ConceptService conceptService;
	protected Map<String, OntologyRetrievalManager> ontologyRetrievalHandlerMap = new HashMap<String, OntologyRetrievalManager>(
			0);
	protected Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>(
			0);
	protected Map<String, OntologySearchManager> ontologySearchHandlerMap = new HashMap<String, OntologySearchManager>(
			0);
	protected Version luceneVersion = Version.LUCENE_29; // default to 2.9

	// non-injected properties
	protected FSDirectory indexDir;


	

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
	 * Empty search results cache
	 */
	public void emptySearchCache() {
		if (log.isDebugEnabled()) {
			log.debug("Emptying cache...");
		}

		searchResultCache.clear();
	}

	/**
	 * Constructs the query that limits the search to the given object types
	 * (i.e. class, individual, property)
	 * 
	 * @param objectTypes
	 * @return
	 */
	protected Query generateObjectTypesQuery(Collection<String> objectTypes) {
		BooleanQuery query = new BooleanQuery();

		for (String objectType : objectTypes) {
			query.add(new TermQuery(generateObjectTypeTerm(objectType)),
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
	 * Constructs the term with the given object type
	 * 
	 * @param objectType
	 * @return
	 */
	protected Term generateObjectTypeTerm(String objectType) {
		return new Term(SearchIndexBean.OBJECT_TYPE_FIELD_LABEL, objectType);
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


	/**
	 * Sets the index path and creates a new instance of searcher
	 * 
	 * @param indexPath
	 *            the indexPath to set
	 */
	public void setIndexPath(String indexPath) {
		try {
			indexDir = NIOFSDirectory.open(new File(indexPath));
		} catch (IOException e) {
			e.printStackTrace();
			log.error("Could not initialize Index NIOFSDirectory at startup: "
					+ e);
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
	 * @param ontologyMetadataManager
	 *            the ontologyMetadataManager to set
	 */
	public void setOntologyMetadataManager(
			OntologyMetadataManager ontologyMetadataManager) {
		this.ontologyMetadataManager = ontologyMetadataManager;
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

	/**
	 * @param luceneVersion
	 *            the luceneVersion to set
	 */
	public void setLuceneVersion(Version luceneVersion) {
		this.luceneVersion = luceneVersion;
	}

	/**
	 * @param conceptService
	 *            the conceptService to set
	 */
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}
}
