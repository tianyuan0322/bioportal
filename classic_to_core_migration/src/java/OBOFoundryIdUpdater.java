import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.ncbo.stanford.bean.MetadataFileBean;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.ontologyfile.OntologyDescriptorParser;

/**
 * A utility class that updates obo foundry ontology ids for existing ontologies
 * 
 * @author Michael Dorf
 * 
 */
public class OBOFoundryIdUpdater {

	public static final String PROPERTY_FILENAME = "build.properties";
	private static final int ERROR_CODE = -1;
	private static String descriptorFile = null;

	private static Properties properties = new Properties();

	public static void main(String[] args) {
		Connection conn = null;

		try {
			conn = connect();
			OntologyDescriptorParser odp = new OntologyDescriptorParser(
					descriptorFile);
			List<MetadataFileBean> ontologyList = odp.parseOntologyFile();

			for (MetadataFileBean mfb : ontologyList) {
				String title = StringHelper.isNullOrNullString(mfb.getTitle()) ? "slkfjslkeswe"
						: mfb.getTitle();
				int statusCode = updateFoundryId(conn, mfb.getId(), title);
				System.out.println("Update status code: " + statusCode + ".");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	static {
		try {
			properties.load(new FileInputStream(PROPERTY_FILENAME));
			descriptorFile = properties
					.getProperty("obo.sourceforge.cvs.descriptor.file")
					.replace(
							"${obo.sourceforge.cvs.checkoutdir}",
							properties
									.getProperty(
											"obo.sourceforge.cvs.checkoutdir")
									.replace(
											"${bioportal.resource.path}",
											properties
													.getProperty("bioportal.resource.path")));
			System.out.println("Descriptor File: " + descriptorFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int updateFoundryId(Connection conn, String oboFoundryId,
			String displayLabel) throws SQLException, IOException,
			ClassNotFoundException {
		int result = ERROR_CODE;

		if (!StringHelper.isNullOrNullString(oboFoundryId)
				&& !StringHelper.isNullOrNullString(displayLabel)) {
			String sqlSelect = "SELECT DISTINCT ov.ontology_id ontology_id "
					+ "FROM ncbo_ontology_version ov "
					+ "INNER JOIN ncbo_ontology_version_metadata ovm "
					+ "on ov.id = ovm.ontology_version_id AND LOWER(display_label) = ?";

			PreparedStatement stmt = conn.prepareStatement(sqlSelect);
			stmt.setString(1, displayLabel.toLowerCase());
			ResultSet rs = stmt.executeQuery();

			PreparedStatement stmt1 = null;

			if (rs.next()) {
				Integer ontologyId = rs.getInt("ontology_id");
				System.out.println("Updating obo foundry id to: '"
						+ oboFoundryId + "' for ontology: '" + displayLabel
						+ "' (Id: " + ontologyId + ").");

				String sqlUpdate = "UPDATE ncbo_ontology SET obo_foundry_id = ? WHERE id = ?";
				stmt1 = conn.prepareStatement(sqlUpdate);
				stmt1.setString(1, oboFoundryId);
				stmt1.setInt(2, ontologyId);

				result = stmt1.executeUpdate();
			} else {
				System.out.println("No ontology found for display label: '" + displayLabel
						+ "' (Obo Foundry id: " + oboFoundryId + ").");				
			}
			
			rs.close();
			rs = null;			
			stmt.close();
			stmt = null;
			
			if (stmt1 != null) {
				stmt1.close();
				stmt1 = null;
			}
		}

		return result;
	}

	/**
	 * Connect to Bioportal db
	 * 
	 * @return Connection
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static Connection connect() throws SQLException, IOException,
			ClassNotFoundException {
		String jdbcUrl = properties.getProperty("bioportal.jdbc.url");
		String jdbcDriver = properties.getProperty("bioportal.jdbc.driver");
		String jdbcUsername = properties.getProperty("bioportal.jdbc.username");
		String jdbcPassword = properties.getProperty("bioportal.jdbc.password");

		// Load the JDBC driver
		Class.forName(jdbcDriver);
		return DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
	}
}
