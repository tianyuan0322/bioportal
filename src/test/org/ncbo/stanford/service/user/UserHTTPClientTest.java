/**
 * 
 */
package org.ncbo.stanford.service.user;

import java.io.IOException;

import org.restlet.Client;
//import org.restlet.data.MediaType;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
//import org.restlet.resource.Variant;


import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.UserBean;

/**
 * @author cyoun
 * 
 */
public class UserHTTPClientTest extends AbstractBioPortalTest {

	public void testHttpClientGETUsers() {

		System.out
				.println("UserHTTPClientTest: testHttpClientGETUsers().......................BEGIN");

		// Prepare HTTP client connector.
		Client client = new Client(Protocol.HTTP);
		
		Response response = client
				.get("http://localhost:8080/bioportal/rest/users");

		try {
			String xml = response.getEntity().getText();
			System.out.println(xml);

		} catch (IOException ioe) {
			System.out
					.println("ERROR in UserHTTPClientTest: testHttpClientGETUsers() ");
			ioe.printStackTrace();
		}
		
		System.out.println ("UserHTTPClientTest: testHttpClientGETUsers().........................DONE");

	}
	
	public void testHttpClientGETUser() {

		System.out
				.println("UserHTTPClientTest: testHttpClientGETUser().......................BEGIN");

		// Prepare HTTP client connector.
		Client client = new Client(Protocol.HTTP);
		
		Response response = client
				.get("http://localhost:8080/bioportal/rest/user/2850");

		try {
			String xml = response.getEntity().getText();
			System.out.println(xml);

		} catch (IOException ioe) {
			System.out
					.println("ERROR in UserHTTPClientTest: testHttpClient() ");
			ioe.printStackTrace();
		}
		
		System.out.println ("UserHTTPClientTest: testHttpClientGETUser().........................DONE");

	}	

	
	public void testHttpClientPOST() {

		System.out
				.println("UserHTTPClientTest: testHttpClientPOSTUser().......................BEGIN");
		
		Request request = new Request();
		request.setMethod(Method.POST);
		
		// Target resource.
		request.setResourceRef("http://localhost:8080/bioportal/rest/users");

		Form form = new Form();
		// Action: create
		
		form.add("username", "user111");
		form.add("password", "password111");
		form.add("firstname", "firstname111");
		form.add("lastname", "lastname111");
		form.add("email", "email111@email.com");

		// Action: Update
		request.setMethod(Method.PUT);
		form.add("username", "user111");
		form.add("password", "password111");
		form.add("firstname", "firstname111");
		form.add("lastname", "lastname111");
		form.add("email", "email111@email.com");

		request.setEntity(form.getWebRepresentation());
		
		// create representation object
		Representation representation = request.getEntity(); 
		
		// Prepare HTTP client connector
		Client client = new Client(Protocol.HTTP);
		
		// Make the call
		//Response response = client.put("http://localhost:8080/bioportal/rest/users", request.getEntity());
		Response response = client.post("http://localhost:8080/bioportal/rest/users", representation);	
		
		try {
			
			String xml = response.getEntity().getText();
			xml = response.getEntity().getText();
			System.out.println(xml);			

		} catch (IOException ioe) {
			System.out
					.println("ERROR in UserHTTPClientTest: testHttpClient() ");
		}

		System.out
				.println("UserServiceTest: testfindUsers().........................DONE");
		
		System.out.println ("UserHTTPClientTest: testHttpClientPOSTUser().........................DONE");

	}
	


	/*
	 * public void post(Representation representation) {
	 * insertNewResource(representation);
	 * getResponse().redirectSeeOther("http://www.domain.com/new_resource_location");
	 * getResponse().setStatus(Status.SUCCESS_CREATED); // restlet 1.1
	 * //response.setStatus(Status.SUCCESS_CREATED);
	 * //response.setLocationRef(yourCreatedURIString); }
	 */

}