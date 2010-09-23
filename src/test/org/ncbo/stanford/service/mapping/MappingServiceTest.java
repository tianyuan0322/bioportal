package org.ncbo.stanford.service.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.mapping.OneToOneMappingBean;
import org.ncbo.stanford.domain.custom.entity.mapping.OneToOneMapping;
import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.exception.MappingExistsException;
import org.ncbo.stanford.exceptions.MappingMissingException;
import org.ncbo.stanford.service.mapping.impl.MappingServiceImpl;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test mappings service functionality. Requires a working RDF store.
 * 
 * @author palexander
 */
public class MappingServiceTest extends AbstractBioPortalTest {

	private static URI mappingId;

	@Autowired
	MappingServiceImpl mappingService;

	@Test
	public void testCreateMapping() {
		OneToOneMapping mapping;
		try {
			mapping = mappingService
					.createMapping(
							new URIImpl(
							"http://purl.bioontology.org/ontology/ATMO/ATM_00000"),
					new URIImpl(
							"http://purl.org/obo/owl/UBERON#UBERON_0001062"),
					new URIImpl(
							"http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#owl:sameAs"),
					1099, 1404, 44203, 44301, 99, "Test comment",
					MappingSourceEnum.APPLICATION,
					"Paul Alexander (palexander@stanford.edu)",
					new URIImpl("http://test.com"),
					"Unknown algorithm", "Automatic");

			mappingId = mapping.getId();

			OneToOneMappingBean retrievedMapping = mappingService
					.getMapping(mappingId);

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
		OneToOneMappingBean mapping;
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

		OneToOneMappingBean mapping = new OneToOneMappingBean();

		mapping.setSourceOntologyId(sourceOntologyId);
		mapping.setTargetOntologyId(targetOntologyId);
		mapping.setCreatedInSourceOntologyVersion(sourceOntologyVersion);
		mapping.setCreatedInTargetOntologyVersion(targetOntologyVersion);
		mapping.setComment(comment);

		OneToOneMappingBean updatedMapping;
		try {
			updatedMapping = mappingService.updateMapping(
					mappingId, mapping);

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

	@Test(expected=MappingMissingException.class)
	public void deleteMapping() throws MappingMissingException {
		mappingService.deleteMapping(mappingId);
		mappingService.getMapping(mappingId);
	}

}
