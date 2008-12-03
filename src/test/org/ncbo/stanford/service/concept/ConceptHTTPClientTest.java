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
import java.net.URLEncoder;

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

	private final static int TEST_OID2 = 38563;
	private final static String TEST_CID2 = "Bleeding From Tongue";
	private final static String TEST_NAME2 = "Bleeding From Tongue";
	
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
	public void testConceptWithSpaceID() {
		System.out
				.println("ConceptHTTPClientTest: testConceptWithSpaceID().......................BEGIN");

		// Prepare HTTP client connector.
		Client client = new Client(Protocol.HTTP);

		try {
			String encodedCID = URLEncoder.encode(TEST_CID2, "UTF-8");
			
			Response response = client.get(BASE_URL + "concepts/" + TEST_OID2 + "/" + encodedCID);

			String xml = response.getEntity().getText();
			assertNotNull(xml);
			System.out.println(xml);
			assertTrue(xml.contains("<label>" + TEST_NAME2));
		} catch (IOException ioe) {
			System.out.println("ERROR in ConceptHTTPClientTest: testConceptWithSpaceID() ");
			ioe.printStackTrace();
			fail();
		}

		System.out
				.println("ConceptHTTPClientTest: testConceptWithSpaceID().........................DONE");
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
