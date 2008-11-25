package org.ncbo.stanford.service.search.impl;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopFieldDocs;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;
import org.ncbo.stanford.manager.search.OntologySearchManager;
import org.ncbo.stanford.service.search.SearchService;
import org.ncbo.stanford.util.cache.expiration.system.ExpirationSystem;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.wrapper.LuceneIndexWriterWrapper;

public class SearchServiceImpl implements SearchService {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(SearchServiceImpl.class);
	private String indexPath;
	private String indexBackupPath;
	private int maxNumHits;
	private int indexMergeFactor;
	private int indexMaxMergeDocs;
	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	private Analyzer analyzer;
	private IndexSearcher searcher;
	private ExpirationSystem<Integer, SearchResultListBean> searchResultCache;
	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>(
			0);
	private Map<String, OntologySearchManager> ontologySearchHandlerMap = new HashMap<String, OntologySearchManager>(
			0);

	public void indexOntology(Integer ontologyId) throws Exception {
		indexOntology(ontologyId, true);
	}

	public void indexOntology(Integer ontologyId, boolean doBackup)
			throws Exception {
		VNcboOntology ontology = ncboOntologyVersionDAO
				.findLatestActiveOntologyVersion(ontologyId);

		if (ontology != null) {
			try {
				LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
						indexPath, analyzer);
				writer.setMergeFactor(indexMergeFactor);
				writer.setMaxMergeDocs(indexMaxMergeDocs);

				indexOntology(writer, ontology, doBackup);

				writer.optimize();
				writer.closeWriter();
				writer = null;
				reloadSearcher();
			} catch (Exception e) {
				handleException(ontology, e, false);
			}
		}
	}

	public void indexAllOntologies() throws Exception {
		long start = System.currentTimeMillis();
		List<VNcboOntology> ontologies = ncboOntologyVersionDAO
				.findLatestActiveOntologyVersions();

		LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
				indexPath, analyzer);
		backupIndex(writer);
		writer.closeWriter();

		writer = new LuceneIndexWriterWrapper(indexPath, analyzer, true);
		writer.setMergeFactor(indexMergeFactor);
		writer.setMaxMergeDocs(indexMaxMergeDocs);

		for (VNcboOntology ontology : ontologies) {
			try {
				indexOntology(writer, ontology, false);
			} catch (Exception e) {
				handleException(ontology, e, true);
			}
		}

		writer.optimize();
		writer.closeWriter();
		writer = null;
		reloadSearcher();

		if (log.isDebugEnabled()) {
			long stop = System.currentTimeMillis(); // stop timing
			log.debug("Finished indexing all ontologies in "
					+ (double) (stop - start) / 1000 / 60 / 60 + " hours.");
		}
	}

	public void removeOntology(Integer ontologyId) throws Exception {
		VNcboOntology ontology = ncboOntologyVersionDAO
				.findLatestOntologyVersion(ontologyId);

		if (ontology != null) {
			try {
				LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
						indexPath, analyzer);
				removeOntology(writer, ontology, true);
				writer.optimize();
				writer.closeWriter();
				writer = null;
				reloadSearcher();
			} catch (Exception e) {
				handleException(ontology, e, false);
			}
		}
	}

	public void backupIndex() throws Exception {
		LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
				indexPath, analyzer);
		backupIndex(writer);
		writer.closeWriter();
		writer = null;
	}

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
					log.debug(score.format(hits[i].score) + " | "
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

	private void indexOntology(LuceneIndexWriterWrapper writer,
			VNcboOntology ontology, boolean doBackup) throws Exception {
		Integer ontologyVersionId = ontology.getId();
		Integer ontologyId = ontology.getOntologyId();
		String format = ontology.getFormat();
		String displayLabel = ontology.getDisplayLabel();
		OntologySearchManager mgr = getOntologySearchManager(format);
		long start = 0;
		long stop = 0;

		if (mgr != null) {
			removeOntology(writer, ontology, doBackup);

			if (log.isDebugEnabled()) {
				log.debug("Adding ontology to index: " + displayLabel
						+ " (Id: " + ontologyVersionId + ", Ontology Id: "
						+ ontologyId + ", Format: " + format + ")");
				start = System.currentTimeMillis();
			}

			mgr.indexOntology(writer, ontology);

			if (log.isDebugEnabled()) {
				stop = System.currentTimeMillis(); // stop timing
				log.debug("Finished indexing ontology: " + displayLabel
						+ " in " + (double) (stop - start) / 1000 / 60
						+ " minutes.\n");
			}
		} else {
			throw new Exception("No hanlder was found for ontology: "
					+ ontology.getDisplayLabel() + " (Id: " + ontologyVersionId
					+ ", Ontology Id: " + ontologyId + ", Format: " + format
					+ ")");
		}
	}

	private void backupIndex(LuceneIndexWriterWrapper writer) throws Exception {
		long start = 0;
		long stop = 0;

		if (log.isDebugEnabled()) {
			log.debug("Backing up index...");
			start = System.currentTimeMillis();
		}

		writer.backupIndexByFileCopy(indexBackupPath);
		// writer.backupIndexByReading(getBackupIndexPath());

		if (log.isDebugEnabled()) {
			stop = System.currentTimeMillis(); // stop timing
			log.debug("Finished backing up index in " + (double) (stop - start)
					/ 1000 + " seconds.");
		}
	}

	private void removeOntology(LuceneIndexWriterWrapper writer,
			VNcboOntology ontology, boolean doBackup) throws Exception {
		Integer ontologyId = ontology.getOntologyId();
		String displayLabel = ontology.getDisplayLabel();

		if (doBackup) {
			backupIndex(writer);
		}

		writer.removeOntology(ontologyId);

		if (log.isDebugEnabled()) {
			log.debug("Removed ontology from index: " + displayLabel + " (Id: "
					+ ontology.getId() + ", Ontology Id: " + ontologyId
					+ ", Format: " + ontology.getFormat() + ")");
		}
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

		// parser.setLowercaseExpandedTerms(false);

		try {
			// expr = doubleQuoteString(expr);
			expr = StringHelper.escapeSpaces(expr);
			query.add(parser.parse(expr), BooleanClause.Occur.MUST);
		} catch (ParseException e) {
			IOException ioe = new IOException(e.getMessage());
			ioe.initCause(e);
			throw ioe;
		}

		PhraseQuery q = new PhraseQuery();
		expr = expr.trim().toLowerCase().replaceAll("[\\t|\\s]+", " ");
		String[] words = expr.split(" ");

		for (int i = 0; i < words.length; i++) {
			q.add(new Term(SearchIndexBean.CONTENTS_FIELD_LABEL, words[i]));
		}
		// query.add(q, BooleanClause.Occur.MUST);

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

	private void reloadSearcher() throws IOException {
		IndexSearcher oldSearcher = searcher;
		searcher = new IndexSearcher(indexPath);

		if (oldSearcher != null) {
			oldSearcher.close();
			oldSearcher = null;
		}
	}

	private void handleException(VNcboOntology ontology, Exception e,
			boolean ignoreErrors) throws Exception {
		Throwable t = e.getCause();
		String msg = null;
		String className = (t == null) ? "" : t.getClass().getName();

		if (e instanceof LBParameterException
				|| (t != null && (className
						.equals("com.mysql.jdbc.exceptions.MySQLSyntaxErrorException") || className
						.equals("com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException")))) {
			msg = "Ontology " + ontology.getDisplayLabel() + " (Id: "
					+ ontology.getId() + ", Ontology Id: "
					+ ontology.getOntologyId()
					+ ") does not exist in the backend store";
		}

		if (ignoreErrors && msg != null) {
			log.error(msg + "\n");
		} else if (ignoreErrors) {
			log.error(e);
			e.printStackTrace();
		} else if (msg != null) {
			throw new Exception(msg);
		} else {
			throw e;
		}
	}

	private OntologySearchManager getOntologySearchManager(String format) {
		return ontologyFormatHandlerMap.containsKey(format) ? ontologySearchHandlerMap
				.get(ontologyFormatHandlerMap.get(format))
				: null;
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

	/**
	 * @param indexMergeFactor
	 *            the indexMergeFactor to set
	 */
	public void setIndexMergeFactor(int indexMergeFactor) {
		this.indexMergeFactor = indexMergeFactor;
	}

	/**
	 * @param indexMaxMergeDocs
	 *            the indexMaxMergeDocs to set
	 */
	public void setIndexMaxMergeDocs(int indexMaxMergeDocs) {
		this.indexMaxMergeDocs = indexMaxMergeDocs;
	}

	/**
	 * @param indexPath
	 *            the indexPath to set
	 */
	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}

	/**
	 * @param indexBackupPath
	 *            the indexBackupPath to set
	 */
	public void setIndexBackupPath(String indexBackupPath) {
		this.indexBackupPath = indexBackupPath;
	}

	/**
	 * @param searcher
	 *            the searcher to set
	 */
	public void setSearcher(IndexSearcher searcher) {
		this.searcher = searcher;
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
	 * @param ncboOntologyVersionDAO
	 *            the ncboOntologyVersionDAO to set
	 */
	public void setNcboOntologyVersionDAO(
			CustomNcboOntologyVersionDAO ncboOntologyVersionDAO) {
		this.ncboOntologyVersionDAO = ncboOntologyVersionDAO;
	}
}