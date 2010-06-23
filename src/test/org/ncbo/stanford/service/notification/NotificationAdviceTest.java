package org.ncbo.stanford.service.notification;

/**
 * @author g.prakash
 *
 */
import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserSubscriptionsDAO;

import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificationAdviceTest extends AbstractBioPortalTest {

	@Autowired
	private OntologyMetadataManager ontologyMetadataManager;

	@Autowired
	private CustomNcboUserSubscriptionsDAO dao;

	@Test
	public void testNotificationService() {

		try {
			ontologyMetadataManager.updateOntologyOrView(createOntolgyBean());
			// dao.findByOntologyIdAndNotificationType("2970",
			// NotificationTypeEnum.CREATE_NOTE_NOTIFICATION);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private OntologyBean createOntolgyBean() {
		OntologyBean bean = new OntologyBean(false);
		// NPO Ontology
		bean.setId(2970);

		bean.setOntologyId(1083);
		return bean;
	}
}