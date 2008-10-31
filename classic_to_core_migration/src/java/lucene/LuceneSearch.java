package lucene;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import lucene.manager.LuceneSearchManager;
import lucene.manager.impl.LuceneSearchManagerLexGridImpl;
import lucene.manager.impl.LuceneSearchManagerProtegeImpl;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.ncbo.stanford.util.constants.ApplicationConstants;

import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;

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

	public void index() throws Exception {
		Connection connBioPortal = connectBioPortal();
		ResultSet rs = findAllOntologies(connBioPortal);
		IndexWriter writer = openWriter(true);

		while (rs.next()) {
			try {
				LuceneSearchManager mgr = formatHandlerMap.get(rs
						.getString("format"));

				if (mgr != null) {
					mgr.indexOntology(writer, rs);
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
		forceWriterClose(writer);
	}

	private void forceWriterClose(IndexWriter writer) {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private IndexWriter openWriter(boolean create) throws IOException {
		return new IndexWriter(getIndexPath(), analyzer, create);
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
				+ "WHERE "
				+ "	upper(ont.format) in ('PROTEGE', 'OWL-FULL', 'OWL-DL', 'OWL-LITE') "

				// + " and id = 13578 "

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

	private static String getIndexPath() {
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
	private static Connection connect(String url, String driver,
			String username, String password) throws SQLException,
			ClassNotFoundException {
		String jdbcUrl = properties.getProperty(url);
		String jdbcDriver = properties.getProperty(driver);
		String jdbcUsername = properties.getProperty(username);
		String jdbcPassword = properties.getProperty(password);

		// Load the JDBC driver
		Class.forName(jdbcDriver);
		return DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
	}
}