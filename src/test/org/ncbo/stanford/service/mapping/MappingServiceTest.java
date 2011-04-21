package org.ncbo.stanford.service.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.mapping.MappingBean;
import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.MappingExistsException;
import org.ncbo.stanford.exception.MappingMissingException;
import org.ncbo.stanford.service.mapping.impl.MappingServiceImpl;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.util.sparql.SPARQLFilterGenerator;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Test mappings service functionality. Requires a working RDF store.
 * 
 * @author palexander
 */
public class MappingServiceTest extends AbstractBioPortalTest {

	private static URI mappingId;

	@Autowired
	MappingServiceImpl mappingService;
	OntologyService ontService;

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateMapping() {
		MappingBean mapping;
		try {
			mapping = mappingService
					.createMapping(
							new ArrayList<URI>(
									Collections
											.singleton(new URIImpl(
													"http://purl.bioontology.org/ontology/ATMO/ATM_00000"))),
							new ArrayList<URI>(
									Collections
											.singleton(new URIImpl(
													"http://purl.org/obo/owl/UBERON#UBERON_0001062"))),
							new URIImpl(
									"http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#owl:sameAs"),
							1099, 1404, 44203, 44301, 99, null, "Test comment",
							MappingSourceEnum.APPLICATION, "Test Source Name",
							"Paul Alexander (palexander@stanford.edu)",
							new URIImpl("http://test.com"),
							"Unknown algorithm", "Automatic");

			mappingId = mapping.getId();

			MappingBean retrievedMapping = mappingService.getMapping(mappingId);

			assertEquals(retrievedMapping.getId(), mapping.getId());
		} catch (MappingExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MappingMissingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testRetrieveMapping() {
		MappingBean mapping;
		try {
			mapping = mappingService.getMapping(mappingId);

			assertTrue(mapping != null);
			assertTrue(mapping.getId().toString().equalsIgnoreCase(
					mappingId.toString()));
		} catch (MappingMissingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testUpdateMapping() {
		Integer sourceOntologyId = 1000;
		Integer targetOntologyId = 1001;
		Integer sourceOntologyVersion = 10000;
		Integer targetOntologyVersion = 10001;
		String comment = "New test comment";

		MappingBean mapping = new MappingBean();

		mapping.setSourceOntologyId(sourceOntologyId);
		mapping.setTargetOntologyId(targetOntologyId);
		mapping.setCreatedInSourceOntologyVersion(sourceOntologyVersion);
		mapping.setCreatedInTargetOntologyVersion(targetOntologyVersion);
		mapping.setComment(comment);

		MappingBean updatedMapping;
		try {
			updatedMapping = mappingService.updateMapping(mappingId, mapping);

			assertEquals(sourceOntologyId, updatedMapping.getSourceOntologyId());
			assertEquals(targetOntologyId, updatedMapping.getTargetOntologyId());
			assertEquals(sourceOntologyVersion, updatedMapping
					.getCreatedInSourceOntologyVersion());
			assertEquals(targetOntologyVersion, updatedMapping
					.getCreatedInTargetOntologyVersion());
			assertEquals(comment, updatedMapping.getComment());
		} catch (MappingMissingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetMappingsFromOntology() throws Exception {
		// Mock bean
		OntologyBean ont = new OntologyBean(false);
		ont.setOntologyId(1006);

		int pageSize = 10000;
		int pageNum = 1;

		Page<MappingBean> mappings = mappingService.getMappingsFromOntology(
				ont, pageSize, pageNum, null);

		assertTrue(mappings != null);
	}

	@Test
	public void testGetMappingsBetweenOntologies() throws InvalidInputException {
		// Mock source
		OntologyBean sourceOnt = new OntologyBean(false);
		sourceOnt.setOntologyId(1032);

		// Mock target
		OntologyBean targetOnt = new OntologyBean(false);
		targetOnt.setOntologyId(1009);

		int pageSize = 10000;
		int pageNum = 1;
		boolean unidirectional = false;

		Page<MappingBean> mappings = mappingService
				.getMappingsBetweenOntologies(sourceOnt, targetOnt, pageSize,
						pageNum, unidirectional, null);

		assertTrue(mappings != null);
	}

	@Test
	public void testGetMappingsForOntology() throws InvalidInputException {
		// Mock ontology
		OntologyBean ont = new OntologyBean(false);
		ont.setOntologyId(1009);

		int pageSize = 10000;
		int pageNum = 1;

		Page<MappingBean> mappings = mappingService.getMappingsForOntology(ont,
				pageSize, pageNum, null);

		assertTrue(mappings != null);
	}

	@Test
	public void testGetMappingsToOntology() throws InvalidInputException {
		// Mock ontology
		OntologyBean ont = new OntologyBean(false);
		ont.setOntologyId(1009);

		int pageSize = 10000;
		int pageNum = 1;

		Page<MappingBean> mappings = mappingService.getMappingsToOntology(ont,
				pageSize, pageNum, null);

		assertTrue(mappings != null);
	}

	@Test
	public void testGetMappingsForConcept() throws InvalidInputException {
		// Mock ontology
		OntologyBean ont = new OntologyBean(false);
		ont.setOntologyId(1009);

		// Mock concept
		ClassBean concept = new ClassBean();
		concept.setFullId("http://purl.org/obo/owl/DOID#DOID_0000000");

		int pageSize = 10000;
		int pageNum = 1;

		Page<MappingBean> mappings = mappingService.getMappingsForConcept(ont,
				concept, pageSize, pageNum, null);

		assertTrue(mappings != null);
	}

	@Test
	public void testGetMappingsFromConcept() throws InvalidInputException {
		// Mock ontology
		OntologyBean ont = new OntologyBean(false);
		ont.setOntologyId(1009);

		// Mock concept
		ClassBean concept = new ClassBean();
		concept.setFullId("http://purl.org/obo/owl/DOID#DOID_0000000");

		int pageSize = 10000;
		int pageNum = 1;

		Page<MappingBean> mappings = mappingService.getMappingsFromConcept(ont,
				concept, pageSize, pageNum, null);

		assertTrue(mappings != null);
	}

	@Test
	public void testGetMappingsToConcept() throws InvalidInputException {
		// Mock ontology
		OntologyBean ont = new OntologyBean(false);
		ont.setOntologyId(1032);

		// Mock concept
		ClassBean concept = new ClassBean();
		concept
				.setFullId("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Gallbladder_Disorder");

		int pageSize = 10000;
		int pageNum = 1;

		Page<MappingBean> mappings = mappingService.getMappingsToConcept(ont,
				concept, pageSize, pageNum, null);

		assertTrue(mappings != null);
	}

	@Test
	public void testGetMappingsBetweenConcepts() throws InvalidInputException {
		// Mock source
		OntologyBean sourceOnt = new OntologyBean(false);
		sourceOnt.setOntologyId(1032);

		// Mock target
		OntologyBean targetOnt = new OntologyBean(false);
		targetOnt.setOntologyId(1009);

		// Mock concepts
		ClassBean sourceConcept = new ClassBean();
		sourceConcept
				.setFullId("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#Gallbladder_Disorder");
		ClassBean targetConcept = new ClassBean();
		targetConcept.setFullId("http://purl.org/obo/owl/DOID#DOID_0000000");

		int pageSize = 10000;
		int pageNum = 1;
		boolean unidirectional = true;

		Page<MappingBean> mappings = mappingService.getMappingsBetweenConcepts(
				sourceOnt, targetOnt, sourceConcept, targetConcept,
				unidirectional, pageSize, pageNum, null);

		assertTrue(mappings != null);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetMappingsByParameters() throws InvalidInputException {
		// Mock ontology
		OntologyBean ont = new OntologyBean(false);
		ont.setOntologyId(1009);

		// Check submitted by
		ArrayList<Integer> submittedBy = new ArrayList<Integer>();
		submittedBy.add(11111);

		SPARQLFilterGenerator parameters = new SPARQLFilterGenerator();
		parameters.setSubmittedBy(submittedBy);

		Page<MappingBean> mappings = mappingService.getMappingsForParameters(
				50000, 1, parameters);

		assertTrue(mappings != null);

		// Check mapping type
		String mappingType = "Automatic";

		parameters = new SPARQLFilterGenerator();
		parameters.setMappingType(mappingType);

		mappings = mappingService
				.getMappingsForParameters(50000, 1, parameters);

		assertTrue(mappings != null);

		// Check mapping dates
		Date startDate = new Date(108, 4, 2, 15, 17, 38);
		Date endDate = new Date(108, 4, 2, 16, 18, 38);

		parameters = new SPARQLFilterGenerator();
		parameters.setStartDate(startDate);
		parameters.setEndDate(endDate);

		mappings = mappingService
				.getMappingsForParameters(50000, 1, parameters);

		assertTrue(mappings != null);

		// Check relationship types
		List<URI> relationshipTypes = new ArrayList<URI>();
		relationshipTypes.add(new URIImpl(
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#closeMatch"));

		parameters = new SPARQLFilterGenerator();
		parameters.setRelationshipTypes(relationshipTypes);

		mappings = mappingService
				.getMappingsForParameters(50000, 1, parameters);

		assertTrue(mappings != null);

		// Check mapping sources
		List<MappingSourceEnum> mappingSources = new ArrayList<MappingSourceEnum>();
		mappingSources.add(MappingSourceEnum.APPLICATION);

		parameters = new SPARQLFilterGenerator();
		parameters.setMappingSource(mappingSources);

		mappings = mappingService
				.getMappingsForParameters(50000, 1, parameters);

		assertTrue(mappings != null);

		// Check multiple parameters
		parameters = new SPARQLFilterGenerator();
		parameters.setSubmittedBy(submittedBy);
		parameters.setStartDate(startDate);
		parameters.setEndDate(endDate);

		mappings = mappingService
				.getMappingsForParameters(50000, 1, parameters);

		assertTrue(mappings != null);
	}

	@Test(expected = MappingMissingException.class)
	public void deleteMapping() throws MappingMissingException {
		mappingService.deleteMapping(mappingId);
		mappingService.getMapping(mappingId);
	}

}
