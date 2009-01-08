package org.ncbo.stanford.service.concept;

import static org.junit.Assert.assertEquals;
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

	private final static String 	BASE_URL = "http://localhost:8080/bioportal/";
	
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
			// Test CID with spaces
			String encodedCID = URLEncoder.encode(TEST_CID2, "UTF-8");
			Response response = client.get(BASE_URL + "concepts/" + TEST_OID2 + "/" + encodedCID);

			String xml = response.getEntity().getText();
			assertNotNull(xml);
//			System.out.println(xml);
			assertTrue(xml.contains("<label>" + TEST_NAME2));
			

			// Test CID with spaces
			encodedCID = URLEncoder.encode("Questionnaire Forms", "UTF-8");
			response = client.get(BASE_URL + "concepts/" + 38563 + "/" + encodedCID);

			xml = response.getEntity().getText();
			assertNotNull(xml);
//			System.out.println(xml);
			assertTrue(xml.contains("<label>" + "Questionnaire Forms"));
	
			// Test CID with spaces
			encodedCID = URLEncoder.encode("Symptom-Specific Treatment", "UTF-8");
			response = client.get(BASE_URL + "concepts/" + 38563 + "/" + encodedCID);

			xml = response.getEntity().getText();
			assertNotNull(xml);
	//		System.out.println(xml);
			assertTrue(xml.contains("<label>" + "Symptom-Specific Treatment"));
			
		} catch (IOException ioe) {
			System.out.println("ERROR in ConceptHTTPClientTest: testConceptWithSpaceID() ");
			ioe.printStackTrace();
			fail();
		}

		System.out
				.println("ConceptHTTPClientTest: testConceptWithSpaceID().........................DONE");
	}
	
	@Test
	public void testAbnormalCIDs() {
		System.out
				.println("ConceptHTTPClientTest: testAbnormalCIDs().......................BEGIN");

		// Prepare HTTP client connector.
		Client client = new Client(Protocol.HTTP);
		

		try {
			// Test CID with spaces
			String encodedCID = URLEncoder.encode("@_A116138", "UTF-8");
			Response response = client.get(BASE_URL + "concepts/" + 32939 + "/" + encodedCID);

			String xml = response.getEntity().getText();
			assertNotNull(xml);
	//		System.out.println(xml);
			assertTrue(xml.contains("<label>" + "UMLS_ICD9CM_2006_AUI:A8359098"));
			

			// Test CID with spaces
			encodedCID = URLEncoder.encode("@_A116934", "UTF-8");
			response = client.get(BASE_URL + "concepts/" + 35377 + "/" + encodedCID);

			xml = response.getEntity().getText();
			assertNotNull(xml);
//			System.out.println(xml);
			assertTrue(xml.contains("<label>" + "@_A116934"));
	
				
		} catch (IOException ioe) {
			System.out.println("ERROR in ConceptHTTPClientTest: testAbnormalCIDs() ");
			ioe.printStackTrace();
			fail();
		}

		System.out
				.println("ConceptHTTPClientTest: testAbnormalCIDs().........................DONE");
	}
	/*
	// This test currently fails
	@Test
	public void testHTTPCIDs() {
		System.out
				.println("ConceptHTTPClientTest: testHTTPCIDs().......................BEGIN");

			
		// Prepare HTTP client connector.
		Client client = new Client(Protocol.HTTP);
		
		try {
			// Test HTTP CID w
			String encodedCID = URLEncoder.encode( "http://www.ifomis.org/bfo/1.1/snap#Continuant", "UTF-8");
			
			String urlRequest = BASE_URL + "concepts/" + 14174 + "/" + encodedCID;
			
			System.out.println("Test URL: " + urlRequest);

			Response response = client.get(urlRequest);
			
			String xml = response.getEntity().getText();
			assertNotNull(xml);
		System.out.println("output: " + xml);
		
//			assertTrue(xml.contains("<id>" +  "http://www.ifomis.org/bfo/1.1/snap#Continuant"));
	

			// Test HTTP CID
			encodedCID = URLEncoder.encode("http://www.owl-ontologies.com/nullUA45", "UTF-8");
			response = client.get(BASE_URL + "concepts/" + 32939 + "/" + encodedCID);

			xml = response.getEntity().getText();
			assertNotNull(xml);
			System.out.println(xml);
	//		assertTrue(xml.contains("<id>" + "http://www.owl-ontologies.com/nullUA45"));
	
			// Test HTTP CID
			encodedCID = URLEncoder.encode("http://purl.org/nbirn/birnlex/ontology/BIRNLex-OBI-proxy.owl#birnlex_11013", "UTF-8");
			response = client.get(BASE_URL + "concepts/" + 28096 + "/" + encodedCID);

			xml = response.getEntity().getText();
			assertNotNull(xml);
			System.out.println("BIRN: " + xml);
	//		assertTrue(xml.contains("<id>" + "http://www.owl-ontologies.com/nullUA45"));
			
				
		} catch (IOException ioe) {
			System.out.println("ERROR in ConceptHTTPClientTest: testHTTPCIDs() ");
			ioe.printStackTrace();
			fail();
		}

		System.out
				.println("ConceptHTTPClientTest: testHTTPCIDs().........................DONE");
	}
	*/
	/*
	
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
	*/
}
