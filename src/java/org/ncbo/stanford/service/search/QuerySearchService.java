package org.ncbo.stanford.service.search;

import java.io.IOException;
import java.util.Collection;

import org.apache.lucene.search.Query;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.ncbo.stanford.util.paginator.impl.Page;

/**
 * Service responsible for querying the search index
 * 
 * @author Michael Dorf
 * 
 */
public interface QuerySearchService {

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
			Integer pageNum, Integer maxNumHits) throws Exception;

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
			throws Exception;

	/**
	 * Execute a search query for a given expression, limiting search to the
	 * specific ontologies. Return results in a form of a single page (of
	 * specified size). If maxNumHits is null, the default value from the
	 * configuration file is used.
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
			Integer pageNum, Integer maxNumHits, String subtreeRootConceptId)
			throws Exception;

	/**
	 * Execute a search query for a given expression, limiting search to the
	 * specific ontologies. Return ALL results in a form of a single page. If
	 * maxNumHits is null, the default value from the configuration file is
	 * used.
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
			Integer maxNumHits, String subtreeRootConceptId) throws Exception;

	/**
	 * Execute a search from an already constructed Query object. Return ALL
	 * results in a form of a single page. If maxNumHits is null, the default
	 * value from the configuration file is used.
	 * 
	 * @param query
	 * @param pageSize
	 * @param pageNum
	 * @param maxNumHits
	 * @param subtreeRootConceptId
	 *            - optional root concept id for sub-tree search
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(Query query, Integer pageSize,
			Integer pageNum, Integer maxNumHits, String subtreeRootConceptId)
			throws Exception;

	/**
	 * Execute a search from an already constructed Query object. Return results
	 * in a form of a single page (of specified size). If maxNumHits is null,
	 * the default value from the configuration file is used.
	 * 
	 * @param query
	 * @param maxNumHits
	 * @param subtreeRootConceptId
	 *            - optional root concept id for sub-tree search
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(Query query, Integer maxNumHits,
			String subtreeRootConceptId) throws Exception;

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
			boolean includeProperties, boolean isExactMatch) throws IOException;

	/**
	 * Executes a query against the Lucene index. Does not use caching
	 * 
	 * @param query
	 * @param maxNumHits
	 * @param subtreeRootConceptId
	 *            - optional root concept id for sub-tree search
	 * @return
	 * @throws Exception
	 */
	public SearchResultListBean runQuery(Query query, Integer maxNumHits,
			String subtreeRootConceptId) throws Exception;

	/**
	 * Empty search results cache
	 */
	public void emptySearchCache();

	/**
	 * Reload search results cache by re-running all queries in it and
	 * re-populating it with new results
	 */
	public void reloadSearchCache();
}
