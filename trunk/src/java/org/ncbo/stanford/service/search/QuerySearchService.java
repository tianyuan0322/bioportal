package org.ncbo.stanford.service.search;

import java.io.IOException;
import java.util.Collection;

import org.apache.lucene.search.Query;
import org.ncbo.stanford.bean.search.SearchBean;
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
			Integer pageNum) throws Exception;

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
			boolean includeProperties, boolean isExactMatch) throws Exception;

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
			throws Exception;

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
			boolean isExactMatch) throws Exception;

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
	public Page<SearchBean> executeQuery(Query query, Integer pageSize,
			Integer pageNum) throws Exception;

	/**
	 * Execute a search from an already constructed Query object. Return ALL
	 * results in a form of a single page
	 * 
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(Query query) throws Exception;

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
			throws IOException;
}
