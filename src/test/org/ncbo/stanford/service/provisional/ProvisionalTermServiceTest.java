package org.ncbo.stanford.service.provisional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.ProvisionalTermBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.ProvisionalTermMissingException;
import org.ncbo.stanford.sparql.bean.ProvisionalTerm;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;

public class ProvisionalTermServiceTest extends AbstractBioPortalTest {

	private static String termId;
	private static ProvisionalTerm newTerm;

	@Autowired
	ProvisionalTermService provisionalTermService;

	@Test
	public void testCreateProvisionalTerm()
			throws ProvisionalTermMissingException {
		newTerm = new ProvisionalTerm();

		newTerm.addOntologyIds(1104);
		newTerm.addSynonyms("Test Synonym");
		newTerm.setCreated(new Date());
		newTerm.setDefinition("Test definition");
		newTerm.setLabel("Test label");
		newTerm.setNoteId("Test note id");
		newTerm.setPermanentId(new URIImpl("http://test.com/permId"));
		newTerm.setStatus("Test status");
		newTerm.setSubmittedBy(442211);
		newTerm.setUpdated(new Date());

		ClassBean term = null;
		try {
			term = provisionalTermService
					.createProvisionalTerm(newTerm.getOntologyIds(), newTerm
							.getLabel(), newTerm.getSynonyms(), newTerm
							.getDefinition(), newTerm
							.getProvisionalSubclassOf(), newTerm.getCreated(),
							newTerm.getUpdated(), newTerm.getSubmittedBy(),
							newTerm.getNoteId(), newTerm.getStatus(), newTerm
									.getPermanentId());

			termId = term.getId();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			assertNotNull(term);
		}

	}

	@Test
	public void testGetProvisionalTerm() throws ProvisionalTermMissingException {
		ClassBean term = provisionalTermService
				.getProvisionalTerm(termId);
		assertNotNull(term);
	}

	@Test
	public void testGetAllTerms() throws InvalidInputException {
		Page<ClassBean> terms = provisionalTermService
				.getAllProvisionalTerms(1000, 0, null);
		assertNotNull(terms);
	}

	@Test
	public void testUpdateTerm() throws Exception {
		ProvisionalTermBean termChanges = new ProvisionalTermBean();

		termChanges.setLabel("New label");
		termChanges.setDefinition("New definition");

		provisionalTermService.updateProvisionalTerm(termId, termChanges);

		ClassBean updatedTerm = provisionalTermService
				.getProvisionalTerm(termId);

		assertTrue(updatedTerm.getLabel().equalsIgnoreCase("New label"));
		assertTrue(updatedTerm.getDefinitions().contains(
				"New definition"));
	}

	@Test(expected = ProvisionalTermMissingException.class)
	public void testDeleteProvisionalTerm()
			throws Exception {
		provisionalTermService.deleteProvisionalTerm(termId);
		provisionalTermService.getProvisionalTerm(termId);
	}

}
