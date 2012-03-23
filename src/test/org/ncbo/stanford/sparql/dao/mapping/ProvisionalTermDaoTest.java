package org.ncbo.stanford.sparql.dao.mapping;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.ProvisionalTermMissingException;
import org.ncbo.stanford.sparql.bean.ProvisionalTerm;
import org.ncbo.stanford.sparql.dao.provisional.ProvisionalTermDAO;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;

public class ProvisionalTermDaoTest extends AbstractBioPortalTest {

	private static URI termId;
	private static ProvisionalTerm newTerm;

	@Autowired
	ProvisionalTermDAO provisionalTermDAO;

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

		ProvisionalTerm term = null;
		try {
			term = provisionalTermDAO.createProvisionalTerm(newTerm);
			termId = term.getId();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			assertNotNull(term);
		}
		
	}

	@Test
	public void testGetProvisionalTerm() throws ProvisionalTermMissingException {
		ProvisionalTerm term = provisionalTermDAO.getProvisionalTerm(termId);
		assertNotNull(term);
	}

	@Test
	public void testGetAllTerms() throws InvalidInputException {
		List<ProvisionalTerm> terms = provisionalTermDAO.getProvisionalTerms(1000, 0, null, null);
		assertNotNull(terms);
	}

	@Test
	public void testUpdateTerm() throws Exception {
		ProvisionalTerm termChanges = new ProvisionalTerm();

		termChanges.setLabel("New label");
		termChanges.setStatus("New status");

		provisionalTermDAO.updateProvisionalTerm(termId, null, "New label", null,
				"New definition", null, null, null, null, null, "New status",
				null);

		ProvisionalTerm updatedTerm = provisionalTermDAO.getProvisionalTerm(termId);

		assertTrue(updatedTerm.getLabel().equalsIgnoreCase("New label"));
		assertTrue(updatedTerm.getDefinition().equalsIgnoreCase(
				"New definition"));
		assertTrue(updatedTerm.getStatus().equalsIgnoreCase("New status"));
	}

	@Test(expected = ProvisionalTermMissingException.class)
	public void testDeleteProvisionalTerm()
			throws Exception {
		provisionalTermDAO.deleteProvisionalTerm(termId);
		provisionalTermDAO.getProvisionalTerm(termId);
	}

}
