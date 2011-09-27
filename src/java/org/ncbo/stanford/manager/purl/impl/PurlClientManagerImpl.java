package org.ncbo.stanford.manager.purl.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.manager.purl.PurlClientManager;

public class PurlClientManagerImpl implements PurlClientManager {
	protected PurlClientRestHarness client = new PurlClientRestHarness();
	// Set the host and port for all subsequent URLs.
	private String host;
	private String port;
	private String userName;
	private String password;
	private String maintainers;
	private String targetUrlPrefix;
	private boolean configured;

	public PurlClientManagerImpl() {

	}

	public PurlClientManagerImpl(String host, String port, String userName,
			String password, String maintainers) {
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.maintainers = maintainers;
	}

	private static final Log log = LogFactory.getLog(PurlClientManagerImpl.class);

	// Log in a registered user.
	public boolean loginUser(String user, String password) {
		boolean success = true;
		try {
			String url = "http://" + host + ":" + port
					+ "/admin/login/login-submit.bsh";

			Map<String, String> formParameters = new HashMap<String, String>();
			formParameters.put("id", user);
			formParameters.put("passwd", password);
			formParameters.put("referrer", "/docs/index.html");

			// String errMsg = "Cannot login " + user + ": ";
			String control = "";
			String test = client.login(url, formParameters);

			// Textual response, so use assertEquals.
			success = assertEquals(control, test);

		} catch (Exception e) {
			success = false;
			log.error("Failed to login user: " + user, e);
		}
		return success;
	}

	public boolean logoutUser() {
		boolean success = false;
		try {
			String url = "http://" + host + ":" + port + "/admin/logout";

			// String errMsg = "Cannot logout user: ";
			String control = "";
			String test = client.logout(url);

			// Textual response, so use assertEquals.
			success = assertEquals(control, test);
			isLoggedOut();

		} catch (Exception e) {
			log.error("Failed to logout user: ", e);
		}
		return success;
	}

	protected boolean isLoggedIn(String user) {

		String url = "http://" + host + ":" + port + "/admin/loginstatus";

		PurlResponseWithStatus rs = client.loginstatus(url);
		if (rs.getStatus().isSuccess() && rs.getResponseTxt().contains(user))
			return true;
		else
			return false;

	}

	protected boolean isLoggedOut() {

		String url = "http://" + host + ":" + port + "/admin/loginstatus";

		PurlResponseWithStatus rs = client.loginstatus(url);
		String status = "logged out";
		if (rs.getStatus().isSuccess() && rs.getResponseTxt().contains(status))
			return true;
		else
			return false;
	}

	protected boolean isLoggedIn() {
		String url = "http://" + host + ":" + port + "/admin/loginstatus";

		String status = "logged in";
		PurlResponseWithStatus rs = client.loginstatus(url);
		if (rs.getStatus().isSuccess() && rs.getResponseTxt().contains(status))
			return true;
		else
			return false;
	}

	protected String getTestDataFile(String filename) {
		String separator = System.getProperty("file.separator");
		String userDir = System.getProperty("user.dir");

		StringBuffer sb = new StringBuffer(System.getProperty("user.dir"));
		sb.append(separator);

		if (!userDir.endsWith("test")) {
			sb.append("test");
			sb.append(separator);
		}

		sb.append("testdata");
		sb.append(separator);
		sb.append(filename);
		return sb.toString();
	}

	/**
	 * Read in the contents of a file and return them.
	 * 
	 * @param filename
	 *            The name of a file to read.
	 * @return The contents of the file.
	 */
	protected static String readFile(String filename)
			throws FileNotFoundException, IOException {

		File file = new File(filename);
		String content = "";
		FileInputStream fis = null;
		BufferedInputStream bis = null;

		fis = new FileInputStream(file);
		bis = new BufferedInputStream(fis);
		BufferedReader br = new BufferedReader(new InputStreamReader(bis));
		String in;
		while ((in = br.readLine()) != null) {
			content += in;
		}

		fis.close();
		bis.close();
		br.close();

		return content;
	}

	/**
	 * Delete a PURL via HTTP DELETE.
	 * 
	 * @param path
	 *            A PURL path or id (starting with a '/' and containing its
	 *            domains and name, e.g. /testdomain/subdomain/purlName).
	 * @param expectSuccess
	 *            Whether the test should expect to succeed. If false, it will
	 *            expect to fail.
	 */
	public boolean deletePurl(String path, boolean expectSuccess) {
		boolean success = false;
		String url = "http://" + host + ":" + port + "/admin/purl" + path;
		try {

			String purlName = path;// .substring(path.lastIndexOf('/') + 1,
									// path.length() );

			// String errMsg = "Cannot delete PURL.";
			String control = "Deleted resource: " + purlName;
			PurlResponseWithStatus rs = client.deletePurl(url);
			String test = rs.getResponseTxt();

			if (expectSuccess) {
				// This test expects to succeed.
				// Textual response, so use assertEquals().
				success = assertEquals(control, test);
			} else {
				// This test expects to fail.
				control = "No such resource: " + purlName;
				// Textual response, so use assertEquals().
				success = assertEquals(control, test);
			}

		} catch (Exception e) {
			log.error("Failed to resolve URL: " + url, e);
		}
		return success;
	}

	public boolean createDomain(String path, String name, String maintainers,
			String writers, boolean isPublic) {

		String url = "http://" + host + ":" + port + "/admin/domain" + path;
		try {
			Map<String, String> formParameters = new HashMap<String, String>();
			formParameters.put("name", name);
			formParameters.put("maintainers", maintainers);
			formParameters.put("writers", writers);
			formParameters.put("public", Boolean.toString(isPublic));

			PurlResponseWithStatus rs = client
					.createDomain(url, formParameters);
			if (rs.getStatus().isSuccess()) {
				return true;
			}
		} catch (Exception e) {
			log.error("Failed to resolve URL: ", e);
		}
		return false;

	}

	public PurlResponseWithStatus modifyDomain(String domain, String name,
			String maintainers, String writers, String isPublic)
			throws Exception {
		String url = "http://" + host + ":" + port + "/admin/domain" + domain;

		Map<String, String> formParameters = new HashMap<String, String>();
		formParameters.put("name", name);
		formParameters.put("maintainers", maintainers);
		formParameters.put("writers", writers);
		formParameters.put("public", isPublic);

		return client.modifyDomain(url, formParameters);
	}

	protected PurlResponseWithStatus createGroup(String group,
			String maintainers, String members, String comments)
			throws Exception {

		String url = "http://" + host + ":" + port + "/admin/group/" + group;

		Map<String, String> formParameters = new HashMap<String, String>();
		formParameters.put("name", group);
		formParameters.put("maintainers", maintainers);
		formParameters.put("members", members);
		formParameters.put("comments", comments);
		return client.createGroup(url, formParameters);

	}

	public boolean createSimplePurl(String path, String targetPath) {
		try {
			String url = "http://" + host + ":" + port + "/admin/purl" + path;
			String targetUrl= targetUrlPrefix+ targetPath;
			Map<String, String> formParameters = new HashMap<String, String>();
			formParameters.put("type", "302");
			formParameters.put("target", targetUrl);
			formParameters.put("maintainers", maintainers);
			loginUser(userName, password);
			PurlResponseWithStatus rs = client.createPurl(url, formParameters);
			if (rs.getStatus().isSuccess()
					&& rs.getResponseTxt().contains("status='1'")) {
				return true;
			} else {
				log.debug("Purl creation failed. Response obtained: "
						+ rs.getResponseTxt());
			}

		} catch (Exception e) {
			log.error("Failed to resolve URL: ", e);
		}
		return false;

	}

	public boolean createAdvancedPartialPurl(String path, String targetPath) {
		try {
			String url = "http://" + host + ":" + port + "/admin/purl" + path;
			String targetUrl= targetUrlPrefix+ targetPath;
			Map<String, String> formParameters = new HashMap<String, String>();
			formParameters.put("type", "partial");
			formParameters.put("target", targetUrl);
			formParameters.put("maintainers", maintainers);
			log.debug("formParameters="+ formParameters);
			loginUser(userName, password);
			PurlResponseWithStatus rs = client.createPurl(url, formParameters);
			if (rs.getStatus().isSuccess()
					&& rs.getResponseTxt().contains("status=\"1\"")) {
				log.debug("Purl created Successfully. Response text="+ rs.getResponseTxt());
				return true;
			} else {
				System.out.println("Purl creation failed. Response obtained: "
						+ rs.getResponseTxt());
				log.debug("Purl creation failed. Response obtained: "
						+ rs.getResponseTxt());
			}
		} catch (Exception e) {
			log.error("Failed to resolve URL: ", e);
		}
		return false;

	}
	
	public boolean doesPurlExist(String path) {
		boolean exists= false;
		String url1 = "http://" + host + ":" + port + "/purl" + path;
		try {
		    String response= client.searchPurl(url1);
		    if (response.contains("status=\"1\"")) {
				return true;
			}
		} catch (Exception ex) {
			
		}
		return exists;
		
	}

	protected PurlResponseWithStatus createPurl(String path,
			Map<String, String> formParameters) throws Exception {
		String url = "http://" + host + ":" + port + "/admin/purl" + path;
		return client.createPurl(url, formParameters);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMaintainers() {
		return maintainers;
	}

	public void setMaintainers(String maintainers) {
		this.maintainers = maintainers;
	}

	public String getTargetUrlPrefix() {
		return targetUrlPrefix;
	}


	public void setTargetUrlPrefix(String targetUrlPrefix) {
		this.targetUrlPrefix = targetUrlPrefix;
	}
	

	public boolean isConfigured() {
		return configured;
	}

	public void setConfigured(boolean configured) {
		this.configured = configured;
	}

	protected PurlResponseWithStatus modifyPurl(String path,
			Map<String, String> formParameters) throws Exception {
		String url = "http://" + host + ":" + port + "/admin/purl" + path;
		return client.modifyPurl(url, formParameters);
	}

	protected PurlResponseWithStatus modifyGroup(String group, String name,
			String maintainers, String members, String comments)
			throws Exception {
		String url = "http://" + host + ":" + port + "/admin/group/" + group;

		Map<String, String> formParameters = new HashMap<String, String>();
		formParameters.put("name", name);
		formParameters.put("maintainers", maintainers);
		formParameters.put("members", members);
		formParameters.put("comments", comments);

		return client.modifyGroup(url, formParameters);
	}

	public void resolvePurlMetdata(String path) throws Exception {
		String url1 = "http://" + host + ":" + port + "/purl" + path;
		String url2 = "http://" + host + ":" + port + "/admin/purl" + path;
		assertEquals(client.resolvePurl(url1), client.resolvePurl(url2));
	}

	boolean assertEquals(String str1, String str2) {
		return StringUtils.equals(str1, str2);

	}

	public static void main(String[] var) {
		// Test creating a new PURL via an HTTP POST.
		PurlClientManagerImpl bpc = new PurlClientManagerImpl("localhost", "9080", "admin",
				"password", "admin");
		bpc.isLoggedIn("admin");

		boolean success = bpc.createAdvancedPartialPurl("/test/hindu1",
				"http://www.hindu.com/aaa");
		if (success) {
			System.out.println("Success");
		} else {
			System.out.println("failed");
		}
		bpc.isLoggedIn();
		// apc.resolvePurlMetdata("/testdomain/testPURL");

	}

}
