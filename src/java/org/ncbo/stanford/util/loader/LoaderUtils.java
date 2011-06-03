package org.ncbo.stanford.util.loader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.ontologyfile.OntologyDescriptorParser;
import org.ncbo.stanford.util.ontologyfile.pathhandler.AbstractFilePathHandler;

public class LoaderUtils {
	private static final Log log = LogFactory.getLog(LoaderUtils.class);

	public static boolean hasDownloadLocationBeenUpdated(String url_location,
			OntologyBean latestOntology) {
		boolean isUpdated = true;
		if (isValidDownloadLocation(url_location)) {
			String url_md5 = computeMD5(url_location);
			String latest_md5 = fetchMd5FromFile(latestOntology);
			if (url_md5 != null && url_md5.equalsIgnoreCase(latest_md5)) {
				isUpdated = false;
			}
		} else {
			isUpdated = false;
		}

		return isUpdated;
	}

	/**
	 * This method downloads the content from the download location and checks
	 * its md5 against that of the latest ontology. It returns true if the
	 * content is different from that of the latest ontology currently in the
	 * repository
	 * 
	 * @param url_location
	 * @param latestOntology
	 * @return
	 */
	public static boolean hasDownloadLocationContentBeenUpdated(
			String url_location, OntologyBean latestOntology) {
		boolean isUpdated = false;
		try {
			if (isValidDownloadLocation(url_location)) {
				getPulledContent(url_location, latestOntology);
				String pull_md5 = fetchMd5InPullPath(latestOntology);
				String latest_md5 = fetchMd5FromFile(latestOntology);
				if (pull_md5 != null && !pull_md5.equalsIgnoreCase(latest_md5)) {
					isUpdated = true;
				}
			}
		} catch (Exception ex) {
			log.error("Error while pulling ontology content for ontology: "
					+ latestOntology);
			ex.printStackTrace();
		}

		return isUpdated;
	}

	public static boolean hasRepositoryFileBeenUpdated(File repo_file,
			OntologyBean latestOntology) {
		boolean isUpdated = true;
		String repo_md5 = computeMD5(repo_file);
		String latest_md5 = fetchMd5FromFile(latestOntology);
		if (repo_md5 != null && repo_md5.equalsIgnoreCase(latest_md5)) {
			isUpdated = false;
		}
		return isUpdated;
	}

	public static long getLastModifiedDate(String url_location)
			throws Exception {
		long lastModified = 0;
		URL url = new URL(url_location);
		URLConnection uc = url.openConnection();
		lastModified = uc.getLastModified();
		return lastModified;

	}

	public static int getContentLength(String url_location) throws Exception {
		int contentLength = 0;
		URL url = new URL(url_location);
		URLConnection uc = url.openConnection();
		contentLength = uc.getContentLength();
		return contentLength;
	}

	public static boolean isValidDownloadLocation(String url_location) {
		boolean isValid = false;
		if (StringUtils.isBlank(url_location)) {
			return false;
		}
		try {
			HttpURLConnection.setFollowRedirects(true);
			// note : you may also need
			// HttpURLConnection.setInstanceFollowRedirects(false)
			URL url = new URL(url_location);
			if (url.getProtocol().toLowerCase().contains("http")) {
				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				con.setInstanceFollowRedirects(true);
				// con.setRequestMethod("HEAD");

				if (con.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
					// System.out.println("Header=\n" + con.getHeaderFields());
					isValid = true;
				}
				con.disconnect();
			} else {
				URLConnection uc = url.openConnection();
				int contentLength = uc.getContentLength();
				if (contentLength > 0) {
					isValid = true;
				}
			}
		} catch (Exception e) {
			log.error(e);
			// e.printStackTrace();

		}
		return isValid;
	}

	/**
	 * This method is being added to deal with the case where the http response
	 * to a request is a HTTP_MOVED_PERM or HTTP_MOVED_TEMP. If the move is from
	 * http to https, the HttpURLConnection's followRedirects doesn't work.
	 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4620571
	 * 
	 * @param url_location
	 * @return InputStream
	 */
	public static InputStream getInputStream(String url_location)
			throws IOException {
		URL url = new URL(url_location);
		URLConnection uc = url.openConnection();
		InputStream in = uc.getInputStream();
		if (uc instanceof HttpURLConnection) {
			HttpURLConnection http_uc = (HttpURLConnection) uc;
			if (http_uc.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
					|| http_uc.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
				String location = http_uc.getHeaderField("Location");
				if (StringUtils.isNotBlank(location)) {
					return getInputStream(location);

				}
			}
		}
		return in;
	}

	/**
	 * Pull the content from the url_location and store it as a file in the
	 * ontology's pullPath. The md5 value is also computed and stored in the
	 * ontology pull path.
	 * 
	 * @param url_location
	 * @param ontologyBean
	 * @throws IOException
	 */
	public static void getPulledContent(String url_location,
			OntologyBean ontologyBean) throws IOException {
		// get fileName and filePath
		String filePath = AbstractFilePathHandler
				.getOntologyPullPath(ontologyBean);
		String fileName = OntologyDescriptorParser.getFileName(url_location);

		// continue only if there is input file
		if (filePath != null && fileName != null) {
			// now create pull directory
			File pullDirectories = new File(filePath);
			pullDirectories.mkdirs();

			File outputFile = new File(filePath, fileName);
			LoaderUtils.getContent(url_location, outputFile);
			storeMd5InPullPath(ontologyBean, outputFile);
		}
	}

	/**
	 * * Pull the content from the url_location and store it in the
	 * distinationFile
	 * 
	 * @param source_url
	 * @param distinationFile
	 * @throws IOException
	 */
	public static void getContent(String source_url, File distinationFile)
			throws IOException {
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(distinationFile));
		BufferedInputStream in = new BufferedInputStream(LoaderUtils
				.getInputStream(source_url));

		byte data[] = new byte[ApplicationConstants.BUFFER_SIZE];
		int count;

		while ((count = in.read(data, 0, ApplicationConstants.BUFFER_SIZE)) != -1) {
			out.write(data, 0, count);
		}

		in.close();
		out.flush();
		out.close();
	}

	public static String computeMD5(File file) {
		String md5sum = null;
		try {
			// System.out.println("Time=" + new Date());
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(file));
			md5sum = DigestUtils.md5Hex(in);
			// System.out.println(md5sum);
			in.close();
			// System.out.println("Time=" + new Date());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return md5sum;

	}

	public static String computeMD5(String downLoadLocation) {
		String md5sum = null;
		try {
			// System.out.println("Time=" + new Date());
			BufferedInputStream in = new BufferedInputStream(
					getInputStream(downLoadLocation));
			md5sum = DigestUtils.md5Hex(in);
			// System.out.println(md5sum);
			in.close();
			// System.out.println("Time=" + new Date());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return md5sum;

	}

	public static String computeMD5(OntologyBean ontologyBean) {
		return computeMD5(ontologyBean.getDownloadLocation());
	}

	/**
	 * Store the computed md5 of the ontologyBean's downloadLocation in the
	 * fullOntologyDirPath
	 * 
	 * @param ontologyBean
	 * @throws IOException
	 */
	public static void storeMd5ToFile(OntologyBean ontologyBean)
			throws IOException {
		String filePath = AbstractFilePathHandler
				.getFullOntologyDirPath(ontologyBean);
		String fileName = getMD5FileName(ontologyBean);
		String md5 = computeMD5(ontologyBean);
		writeMd5ToFile(filePath, fileName, md5);
	}

	/**
	 * Store the computed md5 of the file in the fullOntologyDirPath of the
	 * ontologyBean
	 * 
	 * @param ontologyBean
	 * @param file
	 * @throws IOException
	 */
	public static void storeMd5ToFile(OntologyBean ontologyBean, File file)
			throws IOException {
		String filePath = AbstractFilePathHandler
				.getFullOntologyDirPath(ontologyBean);
		String fileName = getMD5FileName(ontologyBean);
		String md5 = computeMD5(file);
		writeMd5ToFile(filePath, fileName, md5);
	}

	/**
	 * Store the computed md5 of the file in the pullPath of the ontologyBean
	 * 
	 * @param ontologyBean
	 * @param file
	 * @throws IOException
	 */
	public static void storeMd5InPullPath(OntologyBean ontologyBean, File file)
			throws IOException {
		String filePath = AbstractFilePathHandler
				.getOntologyPullPath(ontologyBean);
		String fileName = getMD5FileName(ontologyBean);
		String md5 = computeMD5(file);
		writeMd5ToFile(filePath, fileName, md5);
	}

	private static void writeMd5ToFile(String filePath, String fileName,
			String md5) throws IOException {
		// continue only if there is input file
		if (filePath != null && fileName != null && md5 != null) {
			// now create output file
			File outputDirectories = new File(filePath);
			outputDirectories.mkdirs();

			File outputFile = new File(filePath, fileName);

			BufferedWriter outputStream = new BufferedWriter(new FileWriter(
					outputFile));
			outputStream.write(md5);
			outputStream.flush();
			outputStream.close();
		}

	}

	private static String readMd5FromFile(String filePath, String fileName) {
		String md5 = null;
		try {
			// continue only if there is input file
			if (filePath != null && fileName != null) {
				File inputFile = new File(filePath, fileName);
				BufferedReader inputStream = new BufferedReader(new FileReader(
						inputFile));
				md5 = inputStream.readLine();
				inputStream.close();
			}
		} catch (Exception ex) {

		}
		return md5;
	}

	/**
	 * Read the md5 value from the md5 file that is in the fullOntologyDirPath
	 * of the ontologyBean
	 * 
	 * @param ontologyBean
	 * @return
	 */
	public static String fetchMd5FromFile(OntologyBean ontologyBean) {

		String filePath = AbstractFilePathHandler
				.getFullOntologyDirPath(ontologyBean);
		String fileName = getMD5FileName(ontologyBean);
		String md5 = readMd5FromFile(filePath, fileName);
		return md5;
	}

	/**
	 * Read the md5 value from the md5 file that is in the pullPath of the
	 * ontologyBean
	 * 
	 * @param ontologyBean
	 * @return
	 */
	public static String fetchMd5InPullPath(OntologyBean ontologyBean) {

		String filePath = AbstractFilePathHandler
				.getOntologyPullPath(ontologyBean);
		String fileName = getMD5FileName(ontologyBean);
		String md5 = readMd5FromFile(filePath, fileName);
		return md5;
	}

	public static String getMD5FileName(OntologyBean ontologyBean) {
		String md5FileName = null;

		if (StringUtils.isNotBlank(ontologyBean.getDownloadLocation())) {
			String fileName = OntologyDescriptorParser.getFileName(ontologyBean
					.getDownloadLocation());
			int indexOfFileNameWithoutExt = fileName.toString()
					.lastIndexOf(".");

			// the whole filename
			if (indexOfFileNameWithoutExt == -1) {
				indexOfFileNameWithoutExt = fileName.toString().length();
			}

			String fileNameWithoutExt = fileName.substring(0,
					indexOfFileNameWithoutExt);
			if (StringUtils.isNotBlank(fileNameWithoutExt)) {
				md5FileName = fileNameWithoutExt + ".md5";
			}

		}
		return md5FileName;

	}

	// "http://github.com/cmungall/uberon/raw/master/uberon_edit.obo");
	// URI uri = new
	// URI("http://efo.svn.sourceforge.net/viewvc/efo/trunk/src/softwareontologyinowl/softwareontology.owl");
	// URI uri = new URI(
	// "file:///C:/apps/bmir.apps/bioportal_resources/uploads/6000/1/uberon_edit.obo");
}
