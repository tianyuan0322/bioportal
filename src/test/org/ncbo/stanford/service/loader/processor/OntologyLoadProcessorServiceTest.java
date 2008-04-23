package org.ncbo.stanford.service.loader.processor;

import java.io.File;
import java.util.Date;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.filehandler.FileHandler;
import org.ncbo.stanford.util.filehandler.impl.PhysicalDirectoryFileHandler;

/**
 * @author Michael Dorf
 * 
 */
public class OntologyLoadProcessorServiceTest extends AbstractBioPortalTest {

	public void testUploadOntology() throws Exception {
		OntologyLoadProcessorService service = (OntologyLoadProcessorService) applicationContext
				.getBean("ontologyLoadProcessorService",
						OntologyLoadProcessorService.class);

		File inputFile = new File("/apps/bmir.apps/bioportal_resources/files/pizza.owl");
		FileHandler ontologyFile = new PhysicalDirectoryFileHandler(inputFile);
		service.processOntologyLoad(ontologyFile, createTestBean());
	}

	private OntologyBean createTestBean() {
		OntologyBean bean = new OntologyBean();
		bean.setUserId(12564);
		bean.setVersionNumber("5");
		bean.setIsCurrent(new Byte("1"));
		bean.setIsRemote(new Byte("0"));
		bean.setIsReviewed(new Byte("1"));
		bean.setDateCreated(new Date());
		bean.setDateReleased(new Date());
		bean.setContactEmail("email@email.com");
		bean.setContactName("Name");
		bean.setDisplayLabel("Pizza Ontology");
		bean.setFormat("OWL-DL");
		bean.setIsFoundry(new Byte("0"));

		return bean;
	}
}
