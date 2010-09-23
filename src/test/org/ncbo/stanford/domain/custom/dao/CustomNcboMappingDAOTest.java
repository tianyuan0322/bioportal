package org.ncbo.stanford.domain.custom.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.domain.custom.dao.CustomNcboMappingDAO;
import org.ncbo.stanford.domain.custom.entity.mapping.OneToOneMapping;
import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.exception.MappingExistsException;
import org.ncbo.stanford.exceptions.MappingMissingException;
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
		OneToOneMapping mapping;
		try {
			mapping = mappingDAO
					.createMapping(
							new URIImpl(
									"http://purl.bioontology.org/ontology/ATMO/ATM_00000"),
							new URIImpl(
									"http://purl.org/obo/owl/UBERON#UBERON_0001062"),
							new URIImpl(
									"http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#owl:sameAs"),
							1099, 1404, 44203, 44301, 99, "Test comment",
							MappingSourceEnum.APPLICATION.toString(),
							"Paul Alexander (palexander@stanford.edu)",
							new URIImpl("http://test.com"),
							"Unknown algorithm", "Automatic");

			mappingId = mapping.getId();

			OneToOneMapping retrievedMapping = mappingDAO.getMapping(mappingId);

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
		OneToOneMapping mapping;
		try {
			mapping = mappingDAO.getMapping(mappingId);
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

		OneToOneMapping mapping;
		try {
			mapping = mappingDAO.updateMapping(mappingId, null, null, null,
					sourceOntologyId, targetOntologyId, sourceOntologyVersion,
					targetOntologyVersion, null, comment, null, null, null,
					null, null);

			assertEquals(sourceOntologyId, mapping.getSourceOntologyId());
			assertEquals(targetOntologyId, mapping.getTargetOntologyId());
			assertEquals(sourceOntologyVersion, mapping
					.getCreatedInSourceOntologyVersion());
			assertEquals(targetOntologyVersion, mapping
					.getCreatedInTargetOntologyVersion());
			assertEquals(comment, mapping.getComment());
		} catch (MappingMissingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test(expected = MappingMissingException.class)
	public void deleteMapping() throws MappingMissingException {
		mappingDAO.deleteMapping(mappingId);
		mappingDAO.getMapping(mappingId);
	}
}
