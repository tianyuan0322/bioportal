import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * A utility class that migrates files from BioPortal Classic to BioPortal Core
 * 
 * @author Michael Dorf
 * 
 */
public class OntologyFileCopier {

	public static final String PROPERTY_FILENAME = "build.properties";
	public static final String DEFAULT_BIOPORTAL_CLASSIC_FILE_PATH = "/apps/smi.apps/bioportal/files";

	private static Properties properties = new Properties();

	public static void main(String[] args) {
		Connection conn = null;

		try {
			ResultSet rs = getOntologies(conn);

			String resourcePath = properties
					.getProperty("bioportal.resource.path");
			String oldFilePathPrefix = (args.length > 0) ? args[0]
					: DEFAULT_BIOPORTAL_CLASSIC_FILE_PATH;
			String newFilePathPrefix = properties.getProperty(
					"bioportal.ontology.filepath").replace(
					"${bioportal.resource.path}", resourcePath);

			while (rs.next()) {
				String oldFilePath = oldFilePathPrefix + "/"
						+ rs.getString("id");
				String newFilePath = newFilePathPrefix
						+ rs.getString("file_path");

				File oldFile = new File(oldFilePath);
				File newFile = new File(newFilePath);

				if (oldFile.exists()) {
					System.out.println(oldFilePath + " ... " + newFilePath);

					try {
						copyDirectory(oldFile, newFile);
					} catch (RuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// nothing to copy but make the new file path
					newFile.mkdirs();
				}
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static ResultSet getOntologies(Connection conn) throws SQLException,
			ClassNotFoundException, IOException {
		conn = connect();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT id, file_path "
				+ "FROM ncbo_ontology_version WHERE is_remote = 0 "
				+ "ORDER BY ontology_id, internal_version_number");
		
		return rs;
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

	/**
	 * Copies all files under srcDir to dstDir. If dstDir does not exist, it
	 * will be created.
	 * 
	 * @param srcDir
	 * @param dstDir
	 * @throws IOException
	 */
	public static void copyDirectory(File srcDir, File dstDir)
			throws IOException {
		if (srcDir.isDirectory()) {
			if (!dstDir.exists()) {
				dstDir.mkdirs();
			}

			String[] children = srcDir.list();

			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(srcDir, children[i]), new File(dstDir,
						children[i]));
			}
		} else {
			copyFile(srcDir, dstDir);
		}
	}

	/**
	 * Copies src file to dst file. If the dst file does not exist, it is
	 * created
	 * 
	 * @param src
	 * @param dst
	 */
	public static void copyFile(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len = 0;

		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}

		in.close();
		out.close();
	}
}
