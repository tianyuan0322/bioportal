package org.ncbo.stanford.domain.custom.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.domain.custom.dao.CustomNcboMappingDAO;
import org.ncbo.stanford.domain.custom.entity.mapping.OneToOneMapping;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test RDF store connectivity.
 */
public class CustomNcboMappingDAOTest extends AbstractBioPortalTest {

	private static URI mappingId;

	@Autowired
	CustomNcboMappingDAO mappingDAO;

	@Test
	public void testCreateMapping() {
		System.out.println(System.getProperty("java.class.path"));
		
		OneToOneMapping mapping = mappingDAO
				.createMapping(
						new URIImpl(
								"http://purl.bioontology.org/ontology/ATMO/ATM_00000"),
						new URIImpl(
								"http://purl.org/obo/owl/UBERON#UBERON_0001062"),
						new URIImpl(
								"http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#owl:sameAs"),
						1099, 1404, 44203, 44301, 99, "Test comment", null,
						"Automatic");

		mappingId = mapping.getId();

		OneToOneMapping retrievedMapping = mappingDAO.getMapping(mappingId);

		assertEquals(retrievedMapping.getId(), mapping.getId());
	}

	@Test
	public void testRetrieveMapping() {
		OneToOneMapping mapping = mappingDAO.getMapping(mappingId);

		assertTrue(mapping != null);
		assertTrue(mapping.getId().toString().equalsIgnoreCase(mappingId.toString()));
	}

	@Test
	public void testUpdateMapping() {
		Integer sourceOntologyId = 1000;
		Integer targetOntologyId = 1001;
		Integer sourceOntologyVersion = 10000;
		Integer targetOntologyVersion = 10001;
		String comment = "New test comment";

		OneToOneMapping mapping = mappingDAO.updateMapping(mappingId, null,
				null, null, sourceOntologyId, targetOntologyId,
				sourceOntologyVersion, targetOntologyVersion, null, comment,
				null, null);

		assertEquals(sourceOntologyId, mapping.getSourceOntologyId());
		assertEquals(targetOntologyId, mapping.getTargetOntologyId());
		assertEquals(sourceOntologyVersion, mapping
				.getCreatedInSourceOntologyVersion());
		assertEquals(targetOntologyVersion, mapping
				.getCreatedInTargetOntologyVersion());
		assertEquals(comment, mapping.getComment());
	}

	@Test
	public void deleteMapping() {
		mappingDAO.deleteMapping(mappingId);
		assertNull(mappingDAO.getMapping(mappingId));
	}
}
