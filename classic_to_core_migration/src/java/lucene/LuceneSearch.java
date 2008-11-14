package lucene;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;

import lucene.bean.LuceneSearchDocument;
import lucene.enumeration.LuceneRecordTypeEnum;
import lucene.manager.LuceneSearchManager;
import lucene.manager.impl.LuceneSearchManagerLexGridImpl;
import lucene.manager.impl.LuceneSearchManagerProtegeImpl;
import lucene.wrapper.IndexWriterWrapper;

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
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;

import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;

import edu.stanford.smi.protege.model.Frame;

public class LuceneSearch {

	private static final int MAX_NUM_HITS = 1000;
	private static final int INDEX_MERGE_FACTOR = LogMergePolicy.DEFAULT_MERGE_FACTOR;
	private Analyzer analyzer = new StandardAnalyzer();

	// TODO: Throwaway code ===============================================

	public static final String PROPERTY_FILENAME = "build.properties";
	private static Properties properties = new Properties();

	private Map<String, LuceneSearchManager> formatHandlerMap = null;

	static {
		try {
			properties.load(new FileInputStream(PROPERTY_FILENAME));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected LuceneSearch() {
		LuceneSearchManager lsmProtege = new LuceneSearchManagerProtegeImpl(
				properties.getProperty("protege.jdbc.url"), properties
						.getProperty("protege.jdbc.driver"), properties
						.getProperty("protege.jdbc.username"), properties
						.getProperty("protege.jdbc.password"));
		LuceneSearchManager lsmLexGrid = new LuceneSearchManagerLexGridImpl();

		formatHandlerMap = new HashMap<String, LuceneSearchManager>();
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
		private static final LuceneSearch instance = new LuceneSearch();
	}

	public static LuceneSearch getInstance() {
		return LuceneSearchHolder.instance;
	}

	// TODO: END, Throwaway code ==========================================

	public void indexOntology(Integer ontologyId) throws Exception {
		Connection connBioPortal = connectBioPortal();
		ResultSet rs = findOntology(connBioPortal, ontologyId);

		if (rs.next()) {
			try {
				IndexWriterWrapper writer = new IndexWriterWrapper(
						getIndexPath(), analyzer);
				writer.setMergeFactor(INDEX_MERGE_FACTOR);

				indexOntology(writer, rs, true);

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

		IndexWriterWrapper writer = new IndexWriterWrapper(indexPath, analyzer);
		backupIndex(writer);
		writer.closeWriter();

		writer = new IndexWriterWrapper(indexPath, analyzer, true);
		writer.setMergeFactor(INDEX_MERGE_FACTOR);

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
				IndexWriterWrapper writer = new IndexWriterWrapper(
						getIndexPath(), analyzer);
				writer.setMergeFactor(INDEX_MERGE_FACTOR);

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
		IndexWriterWrapper writer = new IndexWriterWrapper(getIndexPath(),
				analyzer);
		writer.setMergeFactor(INDEX_MERGE_FACTOR);

		backupIndex(writer);
		writer.closeWriter();
		writer = null;
	}

	public void executeQuery(String expr, boolean includeProperties)
			throws IOException {
		executeQuery(expr, null, includeProperties);
	}

	public void executeQuery(String expr, Collection<Integer> ontologyIds,
			boolean includeProperties) throws IOException {
		Searcher searcher = null;
		Collection<Frame> results = new LinkedHashSet<Frame>();

		Query query = generateLuceneSearchQuery(ontologyIds, expr,
				includeProperties);
		searcher = new IndexSearcher(getIndexPath());

		// TopDocCollector collector = new TopDocCollector(MAX_NUM_HITS);
		// searcher.search(query, collector);
		// ScoreDoc[] hits = collector.topDocs().scoreDocs;

		SortField[] fields = { SortField.FIELD_SCORE,
				new SortField("recordType"), new SortField("preferredName") };
		TopFieldDocs docs = searcher.search(query, null, MAX_NUM_HITS,
				new Sort(fields));
		ScoreDoc[] hits = docs.scoreDocs;

		Map<String, Document> uniqueDocs = new LinkedHashMap<String, Document>();

		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			String conceptId = d.get("conceptId");

			if (!uniqueDocs.containsKey(conceptId)) {
				uniqueDocs.put(conceptId, d);

				System.out.println(hits[i].score + " | " + d.get("ontologyId")
						+ " | " + conceptId + " | " + d.get("contents") + " | "
						+ d.get("recordType") + " | " + d.get("preferredName")
						+ " | " + d.get("conceptIdShort") + " | "
						+ d.get("literalContents"));
			}
		}

		System.out.println("Query: " + query);
		System.out.println("Hits: " + hits.length + ", Unique Hits: "
				+ uniqueDocs.size());
	}

	// TODO: in BP, replace rs with OntologyBean
	public void indexOntology(IndexWriterWrapper writer, ResultSet rs,
			boolean doBackup) throws Exception {
		String format = rs.getString("format");
		LuceneSearchManager mgr = formatHandlerMap.get(format);

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

	public void backupIndex(IndexWriterWrapper writer) throws Exception {
		System.out.println("Backing up index...");
		long start = System.currentTimeMillis();
		writer.backupIndexByFileCopy(getBackupIndexPath());
		// writer.backupIndexByReading(getBackupIndexPath());

		long stop = System.currentTimeMillis(); // stop timing
		System.out.println("Finished backing up index in "
				+ (double) (stop - start) / 1000 / 60 + " minutes.");
	}

	// TODO: in BP, replace rs with OntologyBean
	public void removeOntology(IndexWriterWrapper writer, ResultSet rs,
			boolean doBackup) throws Exception {
		Integer ontologyId = rs.getInt("ontology_id");
		String displayLabel = rs.getString("display_label");

		System.out.println("Removing ontology from index: " + displayLabel
				+ " (Id: " + rs.getInt("id") + ", Ontology Id: " + ontologyId
				+ ", Format: " + rs.getString("format") + ")");
		long start = System.currentTimeMillis();

		if (doBackup) {
			backupIndex(writer);
		}

		writer.removeOntology(ontologyId);

		long stop = System.currentTimeMillis(); // stop timing
		System.out.println("Finished removing ontology: " + displayLabel
				+ " in " + (double) (stop - start) / 1000 + " seconds.");
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

	private Query generateLuceneSearchQuery(Collection<Integer> ontologyIds,
			String expr, boolean includeProperties) throws IOException {
		BooleanQuery query = new BooleanQuery();

		// addContentsClauseExact(expr, query);
		addContentsClauseContains(expr, query);
		addOntologyIdsClause(ontologyIds, query);
		addPropertiesClause(includeProperties, query);

		return query;
	}

	private void addContentsClauseExact(String expr, BooleanQuery query)
			throws IOException {
		TermQuery q = new TermQuery(new Term(
				LuceneSearchDocument.LITERAL_CONTENTS_FIELD_LABEL, expr));
		query.add(q, BooleanClause.Occur.MUST);
	}

	private void addContentsClauseContains(String expr, BooleanQuery query)
			throws IOException {
		QueryParser parser = new QueryParser(
				LuceneSearchDocument.CONTENTS_FIELD_LABEL, analyzer);
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
			q
					.add(new Term(LuceneSearchDocument.CONTENTS_FIELD_LABEL,
							words[i]));
		}
		// query.add(q, BooleanClause.Occur.MUST);

	}

	private void addPropertiesClause(boolean includeProperties,
			BooleanQuery query) {
		if (!includeProperties) {
			Term term = new Term(LuceneSearchDocument.RECORD_TYPE_FIELD_LABEL,
					LuceneRecordTypeEnum.RECORD_TYPE_PROPERTY.getLabel());
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
			Term term = new Term(LuceneSearchDocument.ONTOLOGY_ID_FIELD_LABEL,
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

	private void handleException(ResultSet rs, Exception e,
			boolean ignoreNotFound) throws Exception {
		Throwable t = e.getCause();

		if (e instanceof LBParameterException
				|| (t != null && t instanceof MySQLSyntaxErrorException)) {
			String msg = "Ontology: " + rs.getString("display_label")
					+ " (Id: " + rs.getInt("id") + ", Ontology Id: "
					+ rs.getInt("ontology_id")
					+ ") does not exist in the backend store";

			if (ignoreNotFound) {
				System.out.println(msg);
			} else {
				throw new Exception(msg);
			}
		} else {
			throw new Exception(e);
		}
	}
}