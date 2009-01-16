/**
 * 
 */
package org.ncbo.stanford.service.user;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.data.Response;

/**
 * @author cyoun
 * 
 */
public class UserHTTPClientTest extends AbstractBioPortalTest {

	@Test
	public void testTomcatBaseURL() {
		System.out
				.println("UserHTTPClientTest: testTomcatBaseURL().......................BEGIN");

		// Prepare HTTP client connector.
		Client client = new Client(Protocol.HTTP);

		Response response = client.get("http://localhost:8080");

		try {
			String xml = response.getEntity().getText();
			assertNotNull(xml);
//			System.out.println(xml);
			assertTrue(xml.contains("Apache Tomcat"));
		} catch (IOException ioe) {
			System.out
					.println("ERROR in UserHTTPClientTest: testTomcatBaseURL() ");
			ioe.printStackTrace();
			fail();
		}

		System.out
				.println("UserHTTPClientTest: testTomcatBaseURL().........................DONE");
	}

	@Test
	public void testHttpClientGETUsers() {
		System.out
				.println("UserHTTPClientTest: testHttpClientGETUsers().......................BEGIN");

		// Prepare HTTP client connector.
		Client client = new Client(Protocol.HTTP);
		Response response = client
				.get("http://localhost:8080/bioportal/users");

		try {
			String xml = response.getEntity().getText();
			assertNotNull(xml);
			System.out.println(xml);
			assertTrue(xml.contains("<success>"));
		} catch (IOException ioe) {
			System.out
					.println("ERROR in UserHTTPClientTest: testHttpClientGETUsers() ");
			ioe.printStackTrace();
			fail();
		}

		System.out
				.println("UserHTTPClientTest: testHttpClientGETUsers().........................DONE");
	}

	@Test
	public void testHttpClientGETUser() {
		System.out
				.println("UserHTTPClientTest: testHttpClientGETUser().......................BEGIN");

		// Prepare HTTP client connector.
		Client client = new Client(Protocol.HTTP);
		Response response = client
				.get("http://localhost:8080/bioportal/users/2850");

		try {
			String xml = response.getEntity().getText();
			assertNotNull(xml);
			System.out.println(xml);
			assertTrue(xml.contains("<accessedResource>"));
		} catch (IOException ioe) {
			System.out
					.println("ERROR in UserHTTPClientTest: testHttpClient() ");
			ioe.printStackTrace();
			fail();
		}

		System.out
				.println("UserHTTPClientTest: testHttpClientGETUser().........................DONE");
	}
}
