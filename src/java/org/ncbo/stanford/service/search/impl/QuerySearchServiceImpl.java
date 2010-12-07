package org.ncbo.stanford.service.search.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javassist.Modifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.bean.search.SearchField;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;
import org.ncbo.stanford.service.search.AbstractSearchService;
import org.ncbo.stanford.service.search.QuerySearchService;
import org.ncbo.stanford.util.helper.reflection.ReflectionHelper;
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

	public static void main(String[] args) {
		try {
			// String expr = "DOID:1909";
			// String expr = "blood";
			// String expr = "Blue_Nevus-Like_Melanoma";
			// String expr =
			// "Interferon-Alfa_Lu-177-Monoclonal-Antibody-CC49_Pa";
			// String expr = "Swiss_Albinos_City_of_Hope_Med_Ctr";
			// String expr = "Can of vul and vag";
			// String expr = "lun";
			// String expr = "algorith";
			String expr = "posi";
			// String expr = "predominately round";
			// String expr =
			// "monadic                    	Quality of an object*";
			// String expr = "CHEBI:16069";
			// String expr = "blood-vein";
			// String expr = "Interferon-Alfa_Lu-177-Monoclonal-Antibody-CC49";
			// String expr = "*Clarke's nu*";
			// String expr = "multiply";
			Collection<Integer> ontologyIds = new ArrayList<Integer>(0);
			// ontologyIds.add(1353);
			// ontologyIds.add(1104);
			// ontologyIds.add(1057);
			// ontologyIds.add(1070);
			// ontologyIds.add(1107);
			// ontologyIds.add(1321); //Nemo

			
			SearchIndexBean doc = new SearchIndexBean();

			
			List<Field> result = ReflectionHelper.getAllNonStaticFields(doc);
			
			for (Field field : result) {

				field.setAccessible(true);
				SearchField value = (SearchField)field.get(doc);
				
				
				System.out.println("Field: " + field.getName() + " | Value: " + value);
				
				
			}

			
			
			
			
			
			Collection<String> objectTypes = new ArrayList<String>(0);
//			
//			boolean includeProperties = false;
//			boolean isExactMatch = false;
//			Integer maxNumHits = 2250;
//
//			String indexPath = "/apps/bmir.apps/bioportal_resources/searchindex_lucene_2.4.1";
//			QuerySearchServiceImpl ss = new QuerySearchServiceImpl();
//
//			Version ver = Version.LUCENE_24;
//			ss.setLuceneVersion(ver);
//			ss.setAnalyzer(new StandardAnalyzer(ver));
//			ss.setIndexPath(indexPath);
//
//			Query q = ss.generateLuceneSearchQuery(ontologyIds, objectTypes, expr,
//					includeProperties, isExactMatch);
//			System.out.println("q: " + q);
//
//			long start = System.currentTimeMillis();
//			SearchResultListBean results = ss.runQuery(q, maxNumHits, null);
//			long stop = System.currentTimeMillis();
//			System.out.println("Excecution Time: " + (double) (stop - start)
//					/ 1000 + " seconds.");
//
//			System.out.println("Num Hits: " + results.size());
//			System.out.println(Jestr.str(results));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
			Integer pageNum, Integer maxNumHits) throws Exception {
		return executeQuery(expr, null, null, includeProperties, isExactMatch,
				pageSize, pageNum, maxNumHits, null);
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
		return executeQuery(expr, null, null, includeProperties, isExactMatch,
				maxNumHits, null);
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
			Integer pageNum, Integer maxNumHits, String subtreeRootConceptId)
			throws Exception {
		Query query = generateLuceneSearchQuery(ontologyIds, objectTypes, expr,
				includeProperties, isExactMatch);

		return executeQuery(query, pageSize, pageNum, maxNumHits,
				subtreeRootConceptId);
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
			Integer maxNumHits, String subtreeRootConceptId) throws Exception {
		Query query = generateLuceneSearchQuery(ontologyIds, objectTypes, expr,
				includeProperties, isExactMatch);

		return executeQuery(query, maxNumHits, subtreeRootConceptId);
	}

	/**
	 * Execute a search from an already constructed Query object. Return ALL
	 * results in a form of a single page. If maxNumHits is null, the default
	 * value from the configuation file is used.
	 * 
	 * @param query
	 * @param maxNumHits
	 * @param subtreeRootConceptId
	 *            - optional root concept id for sub-tree search
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(Query query, Integer maxNumHits,
			String subtreeRootConceptId) throws Exception {
		return executeQuery(query, null, null, maxNumHits, subtreeRootConceptId);
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
	 * @param subtreeRootConceptId
	 *            - optional root concept id for sub-tree search
	 * @return
	 * @throws Exception
	 */
	public Page<SearchBean> executeQuery(Query query, Integer pageSize,
			Integer pageNum, Integer maxNumHits, String subtreeRootConceptId)
			throws Exception {
		long start = System.currentTimeMillis();
		String resultsKey = composeCacheKey(query, maxNumHits,
				subtreeRootConceptId);
		boolean fromCache = true;
		SearchResultListBean searchResults = searchResultCache.get(resultsKey);

		if (searchResults == null) {
			searchResults = runQuery(query, maxNumHits, subtreeRootConceptId);
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
}