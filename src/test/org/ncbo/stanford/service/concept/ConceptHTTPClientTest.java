package org.ncbo.stanford.service.concept;

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
 * Tests concept rest calls.
 * 
 * @author bdai1
 *
 */

public class ConceptHTTPClientTest extends AbstractBioPortalTest {

	private final static String 	BASE_URL = "http://localhost:8080/bioportal/rest/";
	
	private final static int AMINO_OID = 4519;
	private final static String AMINO_CID1 = "SpecificAminoAcid";
	private final static String AMINO_NAME = "SpecificAminoAcid";
	
	private final static int NCIT_OID = 13578;

	@Test
	public void testAminoConcept() {
		System.out
				.println("ConceptHTTPClientTest: testAminoConcept().......................BEGIN");

		// Prepare HTTP client connector.
		Client client = new Client(Protocol.HTTP);

		Response response = client.get(BASE_URL + "concepts/" + AMINO_OID + "/" + AMINO_CID1);

		try {
			String xml = response.getEntity().getText();
			assertNotNull(xml);
//			System.out.println(xml);
			assertTrue(xml.contains("<label>" + AMINO_NAME));
		} catch (IOException ioe) {
			System.out.println("ERROR in ConceptHTTPClientTest: testTomcatBaseURL() ");
			ioe.printStackTrace();
			fail();
		}

		System.out
				.println("ConceptHTTPClientTest: testAminoConcept().........................DONE");
	}
	

	@Test
	public void testAminoRoot() {
		System.out
				.println("ConceptHTTPClientTest: testAminoRoot().......................BEGIN");

		// Prepare HTTP client connector.
		Client client = new Client(Protocol.HTTP);

		Response response = client.get(BASE_URL + "concepts/" + AMINO_OID + "/root");

		try {
			String xml = response.getEntity().getText();
			assertNotNull(xml);
//			System.out.println(xml);
			assertTrue(xml.contains("<id>owl:Thing</id>"));
		} catch (IOException ioe) {
			System.out.println("ERROR in ConceptHTTPClientTest: testAminoRoot() ");
			ioe.printStackTrace();
			fail();
		}

		System.out
				.println("ConceptHTTPClientTest: testAminoRoot().........................DONE");
	}
	
	@Test
	public void testNCITRoot() {
		System.out
				.println("ConceptHTTPClientTest: testNCITRoot().......................BEGIN");

		// Prepare HTTP client connector.
		Client client = new Client(Protocol.HTTP);

		Response response = client.get(BASE_URL + "concepts/" + NCIT_OID + "/root");

		try {
			String xml = response.getEntity().getText();
			assertNotNull(xml);
//			System.out.println(xml);
			assertTrue(xml.contains("<id>owl:Thing</id>"));
		} catch (IOException ioe) {
			System.out.println("ERROR in ConceptHTTPClientTest: testNCITRoot() ");
			ioe.printStackTrace();
			fail();
		}

		System.out
				.println("ConceptHTTPClientTest: testNCITRoot().........................DONE");
	}
}
