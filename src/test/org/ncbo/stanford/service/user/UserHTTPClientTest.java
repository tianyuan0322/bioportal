/**
 * 
 */
package org.ncbo.stanford.service.user;

import java.io.IOException;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.data.Response;

/**
 * @author cyoun
 * 
 */
public class UserHTTPClientTest extends AbstractBioPortalTest {

	
	public void testTomcatBaseURL() {
		System.out.println("UserHTTPClientTest: testTomcatBaseURL().......................BEGIN");

		// Prepare HTTP client connector.
		Client client = new Client(Protocol.HTTP);
		
		Response response = client.get("http://localhost:8080");

		try {
			String xml = response.getEntity().getText();

      assertNotNull(xml);

			System.out.println(xml);

      assertTrue(xml.contains("Apache Tomcat"));

		} catch (IOException ioe) {
			System.out
					.println("ERROR in UserHTTPClientTest: testTomcatBaseURL() ");
			ioe.printStackTrace();
      fail();
		}
		
		System.out.println ("UserHTTPClientTest: testTomcatBaseURL().........................DONE");

	}

	public void testHttpClientGETUsers() {

		System.out
				.println("UserHTTPClientTest: testHttpClientGETUsers().......................BEGIN");

		// Prepare HTTP client connector.
		Client client = new Client(Protocol.HTTP);
		
		Response response = client
				.get("http://localhost:8080/bioportal/rest/users");

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
		
		System.out.println ("UserHTTPClientTest: testHttpClientGETUsers().........................DONE");

	}
	
	public void testHttpClientGETUser() {

		System.out
				.println("UserHTTPClientTest: testHttpClientGETUser().......................BEGIN");

		// Prepare HTTP client connector.
		Client client = new Client(Protocol.HTTP);
		
		Response response = client
				.get("http://localhost:8080/bioportal/rest/users/2850");

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
		
		System.out.println ("UserHTTPClientTest: testHttpClientGETUser().........................DONE");

	}	
/*  
    //HttpClient POST not working yet
    
	public void testHttpClientPOST() {

		System.out
				.println("UserHTTPClientTest: testHttpClientPOSTUser().......................BEGIN");
		
		Request request = new Request();
		request.setMethod(Method.POST);
		
		// Target resource.
		request.setResourceRef("http://localhost:8080/bioportal/rest/users");

		Form form = new Form();
		form.add("username", "user111");
		form.add("password", "password111");
		form.add("firstname", "firstname111");
		form.add("lastname", "lastname111");
		form.add("email", "email111@email.com");
		
		request.setEntity(form.getWebRepresentation());
		
		// create representation object
		Representation representation = request.getEntity(); 
		representation.setMediaType(MediaType.TEXT_PLAIN);

		/*
		// Debug output

		//System.out.println("FORM1: " + request.getEntity().getText());
		//System.out.println("FORM2: " + request.getEntityAsForm().getWebRepresentation().getText());
					
		// Prepare HTTP client connector
		Client client = new Client(Protocol.HTTP);
		
		// Make the call
		Response response = client.post("http://localhost:8080/bioportal/rest/users", representation);	
		
		
		try {
			
			String xml = response.getEntity().getText();
			xml = response.getEntity().getText();
			System.out.println(xml);			

		} catch (IOException ioe) {
			System.out
					.println("ERROR in UserHTTPClientTest: testHttpClient() ");
		}
		
		System.out.println ("UserHTTPClientTest: testHttpClientPOSTUser().........................DONE");

	}
	*/

}
