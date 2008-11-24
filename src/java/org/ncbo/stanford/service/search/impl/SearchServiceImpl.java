package org.ncbo.stanford.service.search.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopFieldDocs;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;
import org.ncbo.stanford.manager.search.OntologySearchManager;
import org.ncbo.stanford.manager.search.impl.OntologySearchManagerLexGridImpl;
import org.ncbo.stanford.manager.search.impl.OntologySearchManagerProtegeImpl;
import org.ncbo.stanford.service.search.SearchService;
import org.ncbo.stanford.util.cache.expiration.system.ExpirationSystem;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.wrapper.LuceneIndexWriterWrapper;

public class SearchServiceImpl implements SearchService {

	private static final int MAX_NUM_HITS = 1000;
	private static final int INDEX_MERGE_FACTOR = LogMergePolicy.DEFAULT_MERGE_FACTOR;
	private static final int INDEX_MAX_MERGE_DOCS = LogMergePolicy.DEFAULT_MAX_MERGE_DOCS;
	private Analyzer analyzer = new StandardAnalyzer();
	protected ExpirationSystem<Integer, SearchResultListBean> searchResultCache = null;

	// TODO: Throwaway code ===============================================

	public static final String PROPERTY_FILENAME = "build.properties";
	private static Properties properties = new Properties();

	private Map<String, OntologySearchManager> formatHandlerMap = null;

	static {
		try {
			properties.load(new FileInputStream(PROPERTY_FILENAME));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected SearchServiceImpl() {
		OntologySearchManager lsmProtege = new OntologySearchManagerProtegeImpl(
				properties.getProperty("protege.jdbc.url"), properties
						.getProperty("protege.jdbc.driver"), properties
						.getProperty("protege.jdbc.username"), properties
						.getProperty("protege.jdbc.password"));
		OntologySearchManager lsmLexGrid = new OntologySearchManagerLexGridImpl();

		formatHandlerMap = new HashMap<String, OntologySearchManager>();
		formatHandlerMap.put(ApplicationConstants.FORMAT_OWL, lsmProtege);
		formatHandlerMap.put(ApplicationConstants.FORMAT_OWL_DL, lsmProtege);
		formatHandlerMap.put(ApplicationConstants.FORMAT_OWL_FULL, lsmProtege);
		formatHandlerMap.put(ApplicationConstants.FORMAT_OWL_LITE, lsmProtege);
		formatHandlerMap.put(ApplicationConstants.FORMAT_PROTEGE, lsmProtege);
		formatHandlerMap.put(ApplicationConstants.FORMAT_OBO, lsmLexGrid);
		formatHandlerMap.put(ApplicationConstants.FORMAT_UMLS_RRF, lsmLexGrid);
		formatHandlerMap.put(ApplicationConstants.FORMAT_LEXGRID_XML,
				lsmLexGrid);
	}

	private static class LuceneSearchHolder {
		private static final SearchServiceImpl instance = new SearchServiceImpl();
	}

	public static SearchServiceImpl getInstance() {
		return LuceneSearchHolder.instance;
	}

	// TODO: END, Throwaway code ==========================================

	public void indexOntology(Integer ontologyId) throws Exception {
		indexOntology(ontologyId, true);
	}

	public void indexOntology(Integer ontologyId, boolean doBackup) throws Exception {
		Connection connBioPortal = connectBioPortal();
		ResultSet rs = findOntology(connBioPortal, ontologyId);

		if (rs.next()) {
			try {
				LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
						getIndexPath(), analyzer);
				writer.setMergeFactor(INDEX_MERGE_FACTOR);
				writer.setMaxMergeDocs(INDEX_MAX_MERGE_DOCS);

				indexOntology(writer, rs, doBackup);

				writer.optimize();
				writer.closeWriter();
				writer = null;
			} catch (Exception e) {
				handleException(rs, e, false);
			}
		}

		closeResultSet(rs);
		rs = null;
		closeConnection(connBioPortal);
		connBioPortal = null;
	}

	public void indexAllOntologies() throws Exception {
		long start = System.currentTimeMillis();

		Connection connBioPortal = connectBioPortal();
		ResultSet rs = findAllOntologies(connBioPortal);
		String indexPath = getIndexPath();

		LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(indexPath, analyzer);
		backupIndex(writer);
		writer.closeWriter();

		writer = new LuceneIndexWriterWrapper(indexPath, analyzer, true);
		writer.setMergeFactor(INDEX_MERGE_FACTOR);
		writer.setMaxMergeDocs(INDEX_MAX_MERGE_DOCS);

		while (rs.next()) {
			try {
				indexOntology(writer, rs, false);
			} catch (Exception e) {
				handleException(rs, e, true);
			}
		}

		writer.optimize();
		writer.closeWriter();
		writer = null;
		closeResultSet(rs);
		rs = null;
		closeConnection(connBioPortal);
		connBioPortal = null;

		long stop = System.currentTimeMillis(); // stop timing
		System.out.println("Finished indexing all ontologies in "
				+ (double) (stop - start) / 1000 / 60 / 60 + " hours.");
	}

	public void removeOntology(Integer ontologyId) throws Exception {
		Connection connBioPortal = connectBioPortal();
		ResultSet rs = findOntology(connBioPortal, ontologyId);

		if (rs.next()) {
			try {
				LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(
						getIndexPath(), analyzer);

				removeOntology(writer, rs, true);

				writer.optimize();
				writer.closeWriter();
				writer = null;
			} catch (Exception e) {
				handleException(rs, e, false);
			}
		}

		closeResultSet(rs);
		rs = null;
		closeConnection(connBioPortal);
		connBioPortal = null;
	}

	public void backupIndex() throws Exception {
		LuceneIndexWriterWrapper writer = new LuceneIndexWriterWrapper(getIndexPath(),
				analyzer);

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

	public SearchResultListBean executeQuery(Query query)
			throws IOException {
		long start = System.currentTimeMillis();

		Searcher searcher = new IndexSearcher(getIndexPath());
		TopFieldDocs docs = searcher.search(query, null, MAX_NUM_HITS,
				getSortFields());
		ScoreDoc[] hits = docs.scoreDocs;

		long stop = System.currentTimeMillis(); // stop timing
	
		List<String> uniqueDocs = new ArrayList<String>();
		SearchResultListBean searchResults = new SearchResultListBean(0);

		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document doc = searcher.doc(docId);
			String conceptId = doc.get(SearchIndexBean.CONCEPT_ID_FIELD_LABEL);
			Integer ontologyId = new Integer(doc.get(SearchIndexBean.ONTOLOGY_ID_FIELD_LABEL));

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

				DecimalFormat score = new DecimalFormat("0.00");
				System.out.println(score.format(hits[i].score) + " | "
						+ searchResult);
			}
		}

		System.out.println("Query: " + query);
		System.out.println("Hits: " + hits.length + ", Unique Hits: "
				+ uniqueDocs.size());
		System.out.println("Excecution Time: " + (double) (stop - start) / 1000
				+ " seconds.");

		return searchResults;
	}

	// TODO: in BP, replace rs with OntologyBean
	public void indexOntology(LuceneIndexWriterWrapper writer, ResultSet rs,
			boolean doBackup) throws Exception {
		String format = rs.getString("format");
		OntologySearchManager mgr = formatHandlerMap.get(format);

		if (mgr != null) {
			removeOntology(writer, rs, doBackup);
			mgr.indexOntology(writer, rs);
		} else {
			System.out.println("No hanlder was found for ontology: "
					+ rs.getString("display_label") + " (Id: "
					+ rs.getInt("id") + ", Ontology Id: "
					+ rs.getInt("ontology_id") + ", Format: " + format + ")");
		}
	}

	public void backupIndex(LuceneIndexWriterWrapper writer) throws Exception {
		System.out.println("Backing up index...");
		long start = System.currentTimeMillis();
		writer.backupIndexByFileCopy(getBackupIndexPath());
		// writer.backupIndexByReading(getBackupIndexPath());

		long stop = System.currentTimeMillis(); // stop timing
		System.out.println("Finished backing up index in "
				+ (double) (stop - start) / 1000 + " seconds.");
	}

	// TODO: in BP, replace rs with OntologyBean
	public void removeOntology(LuceneIndexWriterWrapper writer, ResultSet rs,
			boolean doBackup) throws Exception {
		Integer ontologyId = rs.getInt("ontology_id");
		String displayLabel = rs.getString("display_label");

		if (doBackup) {
			backupIndex(writer);
		}

		writer.removeOntology(ontologyId);

		System.out.println("Removed ontology from index: " + displayLabel
				+ " (Id: " + rs.getInt("id") + ", Ontology Id: " + ontologyId
				+ ", Format: " + rs.getString("format") + ")");
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

	private void closeConnection(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
				SearchIndexBean.LITERAL_CONTENTS_FIELD_LABEL, expr.toLowerCase()));
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

	private ResultSet findOntology(Connection conn, Integer ontologyId)
			throws SQLException {
		String sqlSelect = "SELECT ont.* "
				+ "FROM "
				+ "	v_ncbo_ontology ont "
				+ "    INNER JOIN ( "
				+ "		SELECT ontology_id, MAX(internal_version_number) AS internal_version_number "
				+ "		FROM "
				+ "			ncbo_ontology_version "
				+ "		WHERE status_id = ? "
				+ "		GROUP BY ontology_id "
				+ "	) a ON ont.ontology_id = a.ontology_id AND ont.internal_version_number = a.internal_version_number "
				+ "WHERE ont.ontology_id = ?";

		PreparedStatement stmt = conn.prepareStatement(sqlSelect);
		stmt.setInt(1, 3);
		stmt.setInt(2, ontologyId);

		return stmt.executeQuery();
	}

	private ResultSet findAllOntologies(Connection conn) throws SQLException {
		String sqlSelect = "SELECT ont.* "
				+ "FROM "
				+ "	v_ncbo_ontology ont "
				+ "    INNER JOIN ( "
				+ "		SELECT ontology_id, MAX(internal_version_number) AS internal_version_number "
				+ "		FROM "
				+ "			ncbo_ontology_version "
				+ "		WHERE status_id = ? "
				+ "		GROUP BY ontology_id "
				+ "	) a ON ont.ontology_id = a.ontology_id AND ont.internal_version_number = a.internal_version_number "
				+ "WHERE 1 = 1 "
				// + "AND UPPER(ont.format) IN ('PROTEGE', 'OWL-FULL', 'OWL-DL',
				// 'OWL-LITE') "
				// + "AND ont.ontology_id IN (" + "1032, " + "1070 "
				// + "AND ont.ontology_id IN (" + "1058, 1070 "
				// + "AND ont.ontology_id = 1049 "
				// + ") " +
				+ "ORDER BY " + "ont.display_label";
		// " LIMIT 10";

		PreparedStatement stmt = conn.prepareStatement(sqlSelect);
		stmt.setInt(1, 3);

		return stmt.executeQuery();
	}

	/**
	 * Connect to BioPortal db
	 * 
	 * @return Connection
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Connection connectBioPortal() throws SQLException,
			ClassNotFoundException {
		return connect("bioportal.jdbc.url", "bioportal.jdbc.driver",
				"bioportal.jdbc.username", "bioportal.jdbc.password");
	}

	private String getIndexPath() {
		return properties.getProperty("bioportal.resource.path") + "/lucene";
	}

	private String getBackupIndexPath() {
		return getIndexPath() + "/backup";
	}

	/**
	 * Connect to a db
	 * 
	 * @return Connection
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Connection connect(String url, String driver, String username,
			String password) throws SQLException, ClassNotFoundException {
		String jdbcUrl = properties.getProperty(url);
		String jdbcDriver = properties.getProperty(driver);
		String jdbcUsername = properties.getProperty(username);
		String jdbcPassword = properties.getProperty(password);

		// Load the JDBC driver
		Class.forName(jdbcDriver);
		return DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
	}

	private void handleException(ResultSet rs, Exception e, boolean ignoreErrors)
			throws Exception {
		Throwable t = e.getCause();
		String msg = null;
		String className = (t == null) ? "" : t.getClass().getName();

		if (e instanceof LBParameterException
				|| (t != null && (className
						.equals("com.mysql.jdbc.exceptions.MySQLSyntaxErrorException") || className
						.equals("com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException")))) {
			msg = "Ontology " + rs.getString("display_label") + " (Id: "
					+ rs.getInt("id") + ", Ontology Id: "
					+ rs.getInt("ontology_id")
					+ ") does not exist in the backend store";
		}

		if (ignoreErrors && msg != null) {
			System.out.println(msg + "\n");
		} else if (ignoreErrors) {
			e.printStackTrace();
		} else if (msg != null) {
			throw new Exception(msg);
		} else {
			throw e;
		}
	}

	/**
	 * @return the analyzer
	 */
	public Analyzer getAnalyzer() {
		return analyzer;
	}

	/**
	 * @param analyzer the analyzer to set
	 */
	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	/**
	 * @param searchResultCache the searchResultCache to set
	 */
	public void setSearchResultCache(
			ExpirationSystem<Integer, SearchResultListBean> searchResultCache) {
		this.searchResultCache = searchResultCache;
	}
}