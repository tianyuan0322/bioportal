package org.ncbo.stanford.service.concept;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.springframework.beans.factory.annotation.Autowired;

/*
 * Test concept service requests.
 * 
 * @author bdai1
 */
public class ConceptServiceTest extends AbstractBioPortalTest {

	private final static int TEST_ONT_ID = 3905;
	private final static String TEST_CONCEPT_ID = "http://www.w3.org/2002/07/owl#Class";
	private final static String TEST_CONCEPT_NAME = "EnumerationClass";
	
	private final static int AMINO_OID = 4519;
	private final static String AMINO_CID1 = "SpecificAminoAcid";
	private final static String AMINO_NAME = "SpecificAminoAcid";
	
	private final static int NCIT_OID = 13578;
	

	
	private final static int TEST_OID2 = 38563;
	private final static String TEST_CID2 = "Bleeding From Tongue";
	
	private final static int LONG_OID = 28837;
	private final static String LONG_CID = "http://www.owl-ontologies.com/GeographicalRegion.owl#Geographical_Regions";
	
	@Autowired
	ConceptService conceptService;

	@Test
	public void testFindRoot() throws Exception {
		ClassBean root = conceptService.findRootConcept(TEST_ONT_ID, 100, false);
		
		ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) root
				.getRelations().get(ApplicationConstants.SUB_CLASS);
		
		System.out.println("Size:" + subclasses.size());
		for (ClassBean subclass : subclasses) {
			System.out.println(subclass.getLabel()
					+ " || "
					+ subclass.getId()
					+ " || "
					+ subclass.getRelations()
							.get(ApplicationConstants.RDF_TYPE));
		}
	}
	
	@Test
	public void testFindAminoRoot() throws Exception {
		ClassBean root = conceptService.findRootConcept(AMINO_OID, 100, false);
		
		assertEquals("owl:Thing",root.getId());
	}
	
	@Test
	public void testFindNCITRoot() throws Exception {
		ClassBean root = conceptService.findRootConcept(NCIT_OID, 100, false);
		
		assertEquals("owl:Thing",root.getId());
	}
	
	@Test
	public void testFindAminoConcept() throws Exception {
		ClassBean concept = conceptService.findConcept(AMINO_OID, AMINO_CID1, 100, false, false, false);
	
		assertEquals(AMINO_NAME, concept.getLabel());
/*		ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) root
				.getRelations().get(ApplicationConstants.SUB_CLASS);
		
		System.out.println("Size:" + subclasses.size());
		for (ClassBean subclass : subclasses) {
			System.out.println(subclass.getLabel()
					+ " || "
					+ subclass.getId()
					+ " || "
					+ subclass.getRelations()
							.get(ApplicationConstants.RDF_TYPE));
		}*/
	}
	

	
	@Test
	public void testFindConceptsWithSpaces() throws Exception {
		ClassBean concept = conceptService.findConcept(TEST_OID2, TEST_CID2, 100, false, false, false);
		assertEquals(TEST_CID2, concept.getId());

		concept = conceptService.findConcept(38563, "Questionnaire Forms", 100, false, false, false);
		assertEquals("Questionnaire Forms", concept.getId());
		
		
	 concept = conceptService.findConcept(38563, "Symptom-Specific Treatment", 100, false, false, false);
		assertEquals("Symptom-Specific Treatment", concept.getId());
	}
	

	@Test
	public void testLongCIDWithInvalid() throws Exception {
		
		ClassBean concept = conceptService.findConcept(LONG_OID, LONG_CID, 100, false, false, false);
	
		assertEquals(LONG_CID, concept.getId());
		
		ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) concept
		.getRelations().get(ApplicationConstants.SUB_CLASS);

System.out.println("Size:" + subclasses.size());
for (ClassBean subclass : subclasses) {
	System.out.println(subclass.getLabel()
			+ " || "
			+ subclass.getId()
			+ " || "
			+ subclass.getRelations()
					.get(ApplicationConstants.RDF_TYPE));
}
	}

	@Test
	public void testHTTPCIDs() throws Exception {
		
		ClassBean concept = conceptService.findConcept(14174, "http://www.ifomis.org/bfo/1.1/span#Occurrent", 100, false, false, false);
		assertEquals("http://www.ifomis.org/bfo/1.1/span#Occurrent", concept.getId());
		
		System.out.println("Occurant: " + concept.toString());
		

	//	concept = conceptService.findConcept(14174, "obi:OBI_69");
	//	assertEquals("obi:OBI_69", concept.getId());

		concept = conceptService.findConcept(14174, "http://www.ifomis.org/bfo/1.1/snap#Continuant", 100, false, false, false);
		assertEquals("http://www.ifomis.org/bfo/1.1/snap#Continuant", concept.getId());

		System.out.println("Continuant: " + concept.toString());
/*
		 concept = conceptService.findConcept(32939, "http://www.owl-ontologies.com/nullUA45");
		assertEquals("http://www.owl-ontologies.com/nullUA45", concept.getId());
	*/
	}
	
	@Test
	public void testAbnormalCIDs() throws Exception {
	
		try {
			System.out.println("Starting testAbnormalCIDs()...");
			ClassBean concept = conceptService.findConcept(38657, "@_A2318", 100, false, false, false);
//			System.out.println("Abnormal @_2318: " + concept.toString());
			assertEquals("@_A2318", concept.getId());
			
			 concept = conceptService.findConcept(32939, "@_A115449", 100, false, false, false);
//			System.out.println("Abnormal: " + concept.toString());
			assertEquals("@_A115449", concept.getId());

			 concept = conceptService.findConcept(32939, "@_A114928", 100, false, false, false);
//			System.out.println("Abnormal: " + concept.toString());
			assertEquals("@_A114928", concept.getId());

			 concept = conceptService.findConcept(32939, "@_A116138", 100, false, false, false);
//			System.out.println("Abnormal: " + concept.toString());
			assertEquals("@_A116138", concept.getId());

			 concept = conceptService.findConcept(35377, "@_A116934", 100, false, false, false);
//			System.out.println("Abnormal: " + concept.toString());
			assertEquals("@_A116934", concept.getId());

			 concept = conceptService.findConcept(32947, "region1:Europe", 100, false, false, false);
//				System.out.println("Abnormal: " + concept.toString());
				assertEquals("region1:Europe", concept.getId());
		}
		catch (Exception exc) {
			exc.printStackTrace();
			
			fail("Unable to get the abnormal CIDs: " + exc.getMessage());
		}
	
	}

	/*
	@Test
	public void testFindConcept() throws Exception {
		ClassBean conceptBean = conceptService.findConcept(TEST_ONT_ID,
				TEST_CONCEPT_NAME);

		System.out.println("Subclasses");
		ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) conceptBean
				.getRelations().get(ApplicationConstants.SUB_CLASS);
		System.out.println("Size:" + subclasses.size());
		for (ClassBean subclass : subclasses) {
			System.out.println(subclass.getLabel()
					+ " || "
					+ subclass.getId()
					+ " || "
					+ subclass.getRelations()
							.get(ApplicationConstants.RDF_TYPE));
		}

		String id = subclasses.get(0).getId();

		conceptService.findConcept(TEST_ONT_ID, id);
	}
*/
}
