package org.ncbo.stanford.domain.custom.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.domain.custom.dao.mapping.CustomNcboMappingCountsDAO;
import org.ncbo.stanford.domain.custom.dao.mapping.CustomNcboMappingDAO;
import org.ncbo.stanford.domain.custom.entity.Mapping;
import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.MappingExistsException;
import org.ncbo.stanford.exception.MappingMissingException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Test RDF store connectivity.
 */
public class CustomNcboMappingDAOTest extends AbstractBioPortalTest {

	private static URI mappingId;

	@Autowired
	CustomNcboMappingDAO mappingDAO;
	CustomNcboMappingCountsDAO mappingCountsDAO;

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateMapping() {
		Mapping mapping;
		try {
			mapping = mappingDAO
					.createMapping(
							new ArrayList<URI>(
									Collections.singleton(new URIImpl(
											"http://purl.bioontology.org/ontology/ATMO/ATM_00000"))),
							new ArrayList<URI>(
									Collections.singleton(new URIImpl(
											"http://purl.org/obo/owl/UBERON#UBERON_0001062"))),
							new URIImpl(
									"http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#owl:sameAs"),
							1099, 1404, 44203, 44301, 99, null, "Test comment",
							MappingSourceEnum.APPLICATION.toString(),
							"Test Source Name",
							"Paul Alexander (palexander@stanford.edu)",
							new URIImpl("http://test.com"),
							"Unknown algorithm", "Automatic");

			mappingId = mapping.getId();

			Mapping retrievedMapping = mappingDAO.getMapping(mappingId);

			assertEquals(retrievedMapping.getId(), mapping.getId());
		} catch (MappingExistsException e) {
			e.printStackTrace();
		} catch (MappingMissingException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRetrieveMapping() {
		Mapping mapping;
		try {
			mapping = mappingDAO.getMapping(mappingId);
			assertTrue(mapping != null);
			assertTrue(mapping.getId().toString().equalsIgnoreCase(
					mappingId.toString()));
		} catch (MappingMissingException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRetrieveMappingsForOntology() {
		List<Mapping> mappings;
		try {
			mappings = mappingDAO.getMappingsForOntology(1032, 50000, 0, null);
			assertTrue(mappings != null && mappings.size() > 0);

			mappings = mappingDAO.getMappingsFromOntology(1032, 50000, 0, null);
			assertTrue(mappings != null && mappings.size() == 0);

			mappings = mappingDAO.getMappingsToOntology(1032, 50000, 0, null);
			assertTrue(mappings != null && mappings.size() > 0);
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRetrieveMappingsCountForOntology()
			throws InvalidInputException {
		Integer count = mappingCountsDAO
				.getCountMappingsForOntology(1032, null);
		System.out.println("All mappings count for ontology 1032: " + count);
		assertTrue(count instanceof Integer && count >= 0);

		count = mappingCountsDAO.getCountMappingsFromOntology(1032, null);
		System.out.println("Mappings from count for ontology 1032: " + count);
		assertTrue(count instanceof Integer && count >= 0);

		count = mappingCountsDAO.getCountMappingsToOntology(1032, null);
		System.out.println("Mappings to count for ontology 1032: " + count);
		assertTrue(count instanceof Integer && count >= 0);
	}

	@Test
	public void testUpdateMapping() {
		Integer sourceOntologyId = 1000;
		Integer targetOntologyId = 1001;
		Integer sourceOntologyVersion = 10000;
		Integer targetOntologyVersion = 10001;
		String comment = "New test comment";

		Mapping mapping;
		try {
			mapping = mappingDAO.updateMapping(mappingId, null, null, null,
					sourceOntologyId, targetOntologyId, sourceOntologyVersion,
					targetOntologyVersion, null, null, comment, null, null,
					null, null, null, null);

			assertEquals(sourceOntologyId, mapping.getSourceOntologyId());
			assertEquals(targetOntologyId, mapping.getTargetOntologyId());
			assertEquals(sourceOntologyVersion, mapping
					.getCreatedInSourceOntologyVersion());
			assertEquals(targetOntologyVersion, mapping
					.getCreatedInTargetOntologyVersion());
			assertEquals(comment, mapping.getComment());
		} catch (MappingMissingException e) {
			e.printStackTrace();
		}
	}

	@Test(expected = MappingMissingException.class)
	public void deleteMapping() throws MappingMissingException {
		mappingDAO.deleteMapping(mappingId);
		mappingDAO.getMapping(mappingId);
	}
}
