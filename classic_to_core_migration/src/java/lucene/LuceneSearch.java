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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;

import lucene.bean.LuceneSearchDocument;
import lucene.manager.LuceneSearchManager;
import lucene.manager.impl.LuceneSearchManagerLexGridImpl;
import lucene.manager.impl.LuceneSearchManagerProtegeImpl;
import lucene.wrapper.IndexWriterWrapper;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.ncbo.stanford.util.constants.ApplicationConstants;

import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;

import edu.stanford.smi.protege.model.Frame;

public class LuceneSearch {

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

	public void executeQuery(String expr) throws IOException {
		executeQuery(expr, null);
	}

	public void executeQuery(String expr, Collection<Integer> ontologyIds)
			throws IOException {
		Searcher searcher = null;
		Collection<Frame> results = new LinkedHashSet<Frame>();

		Query q = generateLuceneQuery(ontologyIds, expr);

		searcher = new IndexSearcher(getIndexPath());
		Hits hits = searcher.search(q);

		System.out.println("Query:" + q);
		System.out.println("Hits:" + hits.length());
	}

	public void index() throws Exception {
		Connection connBioPortal = connectBioPortal();
		ResultSet rs = findAllOntologies(connBioPortal);
		IndexWriterWrapper writer = new IndexWriterWrapper(getIndexPath(),
				analyzer, true);

		while (rs.next()) {
			try {
				String format = rs.getString("format");
				LuceneSearchManager mgr = formatHandlerMap.get(format);

				if (mgr != null) {
					String displayLabel = rs.getString("display_label");

					System.out.println("Indexing ontology: " + displayLabel
							+ " (Id: " + rs.getInt("id") + ", Ontology Id: "
							+ rs.getInt("ontology_id") + ", Format: " + format
							+ ")");
					long start = System.currentTimeMillis();

					mgr.indexOntology(writer, rs);

					long stop = System.currentTimeMillis(); // stop timing
					System.out.println("Finished indexing ontology: "
							+ displayLabel + " in " + (double) (stop - start) / 1000
							+ " milliseconds.");

				} else {
					System.out.println("No hanlder was found for ontology: "
							+ rs.getString("display_label") + " (Id: "
							+ rs.getInt("id") + ", Ontology Id: "
							+ rs.getInt("ontology_id") + ", Format: " + format
							+ ")");
				}
			} catch (RuntimeException e) {
				Throwable t = e.getCause();

				if (!(t instanceof MySQLSyntaxErrorException)) {
					throw new Exception(t);
				} else {
					System.out.println("Ontology: "
							+ rs.getString("display_label") + " (Id: "
							+ rs.getInt("id") + ", Ontology Id: "
							+ rs.getInt("ontology_id")
							+ ") does not exist in Protege");
				}
			}
		}

		writer.optimize();
		writer.closeWriter();
		writer = null;
		closeResultSet(rs);
		rs = null;
		closeConnection(connBioPortal);
		connBioPortal = null;
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

	private Query generateLuceneQuery(Collection<Integer> ontologyIds,
			String expr) throws IOException {
		BooleanQuery query = new BooleanQuery();
		QueryParser parser = new QueryParser(
				LuceneSearchDocument.CONTENTS_FIELD_LABEL, analyzer);
		parser.setAllowLeadingWildcard(true);

		try {
			query.add(parser.parse(expr), BooleanClause.Occur.MUST);
		} catch (ParseException e) {
			IOException ioe = new IOException(e.getMessage());
			ioe.initCause(e);
			throw ioe;
		}

		if (ontologyIds != null && !ontologyIds.isEmpty()) {
			BooleanQuery ontologyIdQuery = new BooleanQuery();

			for (Integer ontologyId : ontologyIds) {
				Term term = new Term(
						LuceneSearchDocument.ONTOLOGY_ID_FIELD_LABEL,
						ontologyId.toString());
				ontologyIdQuery.add(new TermQuery(term),
						BooleanClause.Occur.SHOULD);
			}

			query.add(ontologyIdQuery, BooleanClause.Occur.MUST);
		}

		return query;
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
				+ "AND UPPER(ont.format) IN ('PROTEGE', 'OWL-FULL', 'OWL-DL', 'OWL-LITE') "

				// + "AND id = 13578 "
				+ "AND id = 29684 "

				+ "ORDER BY " + "ont.display_label" + " LIMIT 10";

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
}