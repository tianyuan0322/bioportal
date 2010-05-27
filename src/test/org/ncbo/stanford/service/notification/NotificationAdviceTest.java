package org.ncbo.stanford.service.notification;

/**
 * @author g.prakash
 *
 */
import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificationAdviceTest extends AbstractBioPortalTest {

	@Autowired
	private OntologyMetadataManager ontologyMetadataManager;

	@Test
	public void testNotificationService() {

		try {
			ontologyMetadataManager.updateOntologyOrView(createOntolgyBean());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private OntologyBean createOntolgyBean() {
		OntologyBean bean = new OntologyBean(false);
		// NPO Ontology
		bean.setId(42785);
		bean.setOntologyId(1083);
		return bean;
	}
}