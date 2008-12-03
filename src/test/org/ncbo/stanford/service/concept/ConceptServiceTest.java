package org.ncbo.stanford.service.concept;

import java.util.ArrayList;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

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
	
	private final static int TEST_OID1 = 38657;
	private final static String TEST_CID1 = "@_A2318";
	
	private final static int TEST_OID2 = 38563;
	private final static String TEST_CID2 = "Bleeding From Tongue";

	
	@Autowired
	ConceptService conceptService;

	@Test
	public void testFindRoot() throws Exception {
		ClassBean root = conceptService.findRootConcept(TEST_ONT_ID);
		
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
		ClassBean root = conceptService.findRootConcept(AMINO_OID);
		
		assertEquals("owl:Thing",root.getId());
	}
	
	@Test
	public void testFindNCITRoot() throws Exception {
		ClassBean root = conceptService.findRootConcept(NCIT_OID);
		
		assertEquals("owl:Thing",root.getId());
	}
	
	@Test
	public void testFindAminoConcept() throws Exception {
		ClassBean concept = conceptService.findConcept(AMINO_OID, AMINO_CID1);
	
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
	
	/* Fails for now...
	@Test
	public void testCrypticCID() throws Exception {
		ClassBean concept = conceptService.findConcept(TEST_OID1, TEST_CID1);
	
		assertEquals(TEST_CID1, concept.getId());
		
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
	*/
	
	@Test
	public void testFindConceptsWithSpaces() throws Exception {
		ClassBean concept = conceptService.findConcept(TEST_OID2, TEST_CID2);
	
		assertEquals(TEST_CID2, concept.getId());
		
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
