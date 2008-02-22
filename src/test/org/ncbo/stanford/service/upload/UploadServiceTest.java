/**
 * 
 */
package org.ncbo.stanford.service.upload;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.loader.processor.OntologyLoadProcessorService;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * @author nickgriffith
 * 
 */
public class UploadServiceTest extends
		AbstractDependencyInjectionSpringContextTests {

	protected String[] getConfigLocations() {
		return new String[] { "classpath:applicationContext-datasources.xml",
				"classpath:applicationContext-services.xml",
				"classpath:applicationContext-rest.xml",
				"classpath:applicationContext-security.xml" };
	}

	public void testUploadOntology() throws Exception {

		OntologyLoadProcessorService service = (OntologyLoadProcessorService) applicationContext
				.getBean("ontologyLoadProcessorService",
						OntologyLoadProcessorService.class);
		
		File inputFile = new File("/Users/nickgriffith/Documents/GWT.txt");
		DiskFileItemFactory factory = new DiskFileItemFactory(
		        DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, inputFile);
		
		
		ServletFileUpload upload = new ServletFileUpload(factory);
		
//		factory.createItem("file1", contentType, false, fileName)
		
		
		
		
//		inputFile = new File("/Users/nickgriffith/Documents/GWT.txt");
		
		FileInputStream inputStream = new FileInputStream(inputFile);

		
		
//		service.processOntologyLoad(ontologyFile, createTestBean());
//		
//		
//		uploadOntology(inputStream, createTestBean());

	}

	private OntologyBean createTestBean() {
		OntologyBean bean = new OntologyBean();
		bean.setContactEmail("email@email.com");
		bean.setContactName("Name");
		bean.setDateCreated(new Date());
		bean.setDateReleased(new Date());
		bean.setDisplayLabel("Label");
		bean.setFormat("OBO");
		bean.setIsCurrent(new Byte("1"));
		bean.setVersionNumber("5");
		bean.setIsRemote(new Byte("0"));
		bean.setIsReviewed(new Byte("1"));
		bean.setIsFoundry(new Byte("0"));

		return bean;
	}

}
