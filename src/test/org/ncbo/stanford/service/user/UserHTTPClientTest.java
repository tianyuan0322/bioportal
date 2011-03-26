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
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

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
		ClientResource resource = new ClientResource("http://localhost:8080");
		resource.setProtocol(Protocol.HTTP);
		Representation rep = resource.get();
		
		try {
			String xml = rep.getText();
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
		ClientResource resource = new ClientResource("http://localhost:8080/bioportal/users");
		resource.setProtocol(Protocol.HTTP);
		Representation rep = resource.get();

		try {
			String xml = rep.getText();
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
		ClientResource resource = new ClientResource("http://localhost:8080/bioportal/users/2850");
		resource.setProtocol(Protocol.HTTP);
		Representation rep = resource.get();

		try {
			String xml = rep.getText();
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
