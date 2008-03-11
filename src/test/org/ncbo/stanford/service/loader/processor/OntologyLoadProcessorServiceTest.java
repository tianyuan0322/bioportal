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

		File inputFile = new File("/apps/bmir.apps/bioportal/files/test.obo");
		FileHandler ontologyFile = new PhysicalDirectoryFileHandler(inputFile);

		// DiskFileItemFactory factory = new DiskFileItemFactory(
		// DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, inputFile);
		// ServletFileUpload upload = new ServletFileUpload(factory);
		// FileItem ontologyFile = factory.createItem("file1", null, false,
		// "test.obo");

		// DiskFileItem fileItem = new DiskFileItem("file1", null, true,
		// "test.obo", 22, new File("/hello"));

		// inputFile = new File("/Users/nickgriffith/Documents/GWT.txt");

		// FileInputStream inputStream = new FileInputStream(inputFile);

		service.processOntologyLoad(ontologyFile, createTestBean());
		//		
		//		
		//		uploadOntology(inputStream, createTestBean());

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
		bean.setDisplayLabel("Testing Loading Ontology");
		bean.setFormat("OBO");
		bean.setIsFoundry(new Byte("0"));

		return bean;
	}

}
