/**
 * 
 */
package org.ncbo.stanford.service.ontology;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl.CompressedFileHandlerFactory;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.impl.PhysicalDirectoryFilePathHandlerImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author nickgriffith
 * 
 */

public class OntologyServiceTest extends AbstractBioPortalTest {
	
	@Autowired
	OntologyService ontologyService;
	
/* * /
	@Test
	public void testPerformanceFindOntologyOrView() {
		
		System.out
				.println("OntologyServiceTest: testPerformanceFindOntologyOrView()..........................BEGIN");

		try {
			for (int i = 0; i < 5000; i++) {
				Thread.currentThread().sleep(100);

				getOntologyService().findOntologyOrView(new Integer(3905));

			}
		} catch (Exception e) {
			System.out.println("Exception Occured : "
					+ e.getMessage().substring(0, 200));
		}

		System.out
				.println("OntologyServiceTest: testPerformanceFindOntologyOrView()...........................DONE");
		
	}
/* * /

	@Test
	public void testfindLatestOntologyVersions() {

		System.out.println ("OntologyServiceTest: testfindLatestOntologyVersions().......................BEGIN");
		
		List<OntologyBean> ontologies = ontologyService.findLatestOntologyVersions();

		for (OntologyBean ontology : ontologies) {

			System.out.println(ontology);
		}
		
		System.out.println ("OntologyServiceTest: testfindLatestOntologyVersions().........................DONE");

	}

	@Test
	public void testfindLatestActiveOntologyVersions() {
		
		System.out.println ("OntologyServiceTest: testfindLatestActiveOntologyVersions().......................BEGIN");
		
		List<OntologyBean> ontologies = ontologyService.findLatestActiveOntologyVersions();
		
		for (OntologyBean ontology : ontologies) {
			
			System.out.println(ontology);
		}
		
		System.out.println ("OntologyServiceTest: testfindLatestActiveOntologyVersions().........................DONE");
		
	}
	
/*	*/
	@Test
	public void testfindAllOntologyOrViewVersions() throws Exception {

		System.out.println ("OntologyServiceTest: testfindAllOntologyOrViewVersions().......................BEGIN");
		
		List<OntologyBean> ontologies = getOntologyService().findAllOntologyOrViewVersionsByVirtualId(new Integer(6000), false);

		for (OntologyBean ontology : ontologies) {

			System.out.println(ontology.toString());
		}
		
		System.out.println ("OntologyServiceTest: testfindAllOntologyOrViewVersions().........................DONE");

	}

	
	//@Test
	public void testSearchOntologyView(){	
		List<OntologyBean> ontologies = getOntologyService().searchOntologyMetadata("Liver", false);
		
		for (OntologyBean ontology : ontologies) {
			
			System.out.println(ontology.toString());
		}		
	}
	
	//@Test
	public void testSearchOntologyViewMetadata(){	
		List<OntologyBean> ontologies = getOntologyService().searchOntologyMetadata("Mouse", true);

		for (OntologyBean ontology : ontologies) {

			System.out.println(ontology.toString());
		}		
	}
	
	@Test
	public void testFindOntologyOrView() throws Exception {
		
		System.out.println ("OntologyServiceTest: testFindOntologyOrView()..........................BEGIN");
		
		OntologyBean ontologyBean = getOntologyService().findOntologyOrView(new Integer(50000));
		
		System.out.println(ontologyBean);
		
		if (ontologyBean != null) {
			
			System.out.println (".....ContactEmail: " +  ontologyBean.getContactEmail());
			System.out.println (".....ContactName: " +  ontologyBean.getContactName());
		}
		
		System.out.println ("OntologyServiceTest: testFindOntologyOrView()...........................DONE");
		
	}
		
/**/
/* */
	@Test
	public void testCreateOntologyView() throws Exception {
		
		System.out.println ("OntologyServiceTest: testCreateOntologyView()........................BEGIN");
			
		OntologyBean ontologyBean = createOntolgyViewBean();
		
		getOntologyService().createOntologyOrView(ontologyBean, OntologyServiceTest.getFilePathHandler(ontologyBean));
		
		System.out.println ("OntologyServiceTest: testCreateOntologyView().........................DONE");
	}
/**/

/* * /
	public void testCreateManualOntology() throws Exception {
		
		System.out.println ("OntologyServiceTest: testCreateManualOntology()........................BEGIN");
			
		OntologyBean ontologyBean = createOntolgyBeanManual();
		
		getOntologyService().createOntologyOrView(ontologyBean, OntologyServiceTest.getFilePathHandler(ontologyBean));
		
		System.out.println ("OntologyServiceTest: testCreateManualOntology().........................DONE");
	}
	
	
	public void testCreateRemoteOntology() throws Exception {
		
		System.out.println ("OntologyServiceTest: testCreateRemoteOntology()........................BEGIN");
			
		OntologyBean ontologyBean = createOntolgyBeanRemote();
		
		getOntologyService().createOntologyOrView(ontologyBean, null);
		
		System.out.println ("OntologyServiceTest: testCreateRemoteOntology().........................DONE");
	}
/**/
	

/* * /
	public void testCleanupOntologyCategory() throws Exception {
		
		System.out.println ("OntologyServiceTest: testCleanupOntologyCategory()........................BEGIN");
		
		OntologyBean ontologyBean = getOntologyService().findOntologyOrView(new Integer(34280));
		
		if (ontologyBean != null) {

			getOntologyService().cleanupOntologyCategory(ontologyBean);

			System.out
					.println("OntologyServiceTest: testCleanupOntologyCategory().........................DONE");
		}
	
	}
	
	
	public void testUpdateOntology() throws Exception {
		
		System.out.println ("OntologyServiceTest: testUpdateOntology().......................BEGIN");
		
		OntologyBean ontologyBean = getOntologyService().findOntologyOrView(new Integer(3905));
	
		// test updating some properties
		if (ontologyBean != null) {
			
			System.out.println (".....Updating Ontology Bean - Version number to 2.222");
			ontologyBean.setVersionNumber("2.222");
			
			System.out.println (".....Updating Ontology Bean - Metadata ");
			ontologyBean.setContactEmail("testemail_111@test.edu");
			ontologyBean.setContactName("NewFirstname NewLastname");
			System.out.println (".....ContactEmail: " +  ontologyBean.getContactEmail());
			System.out.println (".....ContactName: " +  ontologyBean.getContactName());
			
			getOntologyService().updateOntologyOrView(ontologyBean);
		}
		else {
			System.out.println (".....No matching record found!");
		}
		
		System.out.println ("OntologyServiceTest: testUpdateOntology()........................DONE");
		
	}
	

	
	
	public void testDeleteOntology() throws Exception {
		
		System.out.println ("OntologyServiceTest: testDeleteOntology()........................BEGIN");
		
		System.out.println (".....Deleting Ontology where internal ID = 34321");
		
		getOntologyService().deleteOntologyOrView(new Integer(34321));
		
		System.out.println ("OntologyServiceTest: testDeleteOntology().........................DONE");
		
	}
	
/**/
	
	
	private OntologyService getOntologyService() {
			return ontologyService;
		}
	
	
	private OntologyBean createOntolgyBean() {
		
		OntologyBean bean = new OntologyBean(false);
		
		bean.setUserId(2850);
		bean.setIsManual(new Byte("0"));
		
		bean.setVersionNumber("1.0");
		//default status Id should be populated upon creation
		//bean.setStatusId(new Integer(1));
		bean.setVersionStatus("pre-production");
		bean.setIsRemote(new Byte("0"));
		bean.setIsReviewed(new Byte("1"));
		bean.setDateCreated(new Date());
		bean.setDateReleased(new Date());
		bean.setContactEmail("test111@email.com");
		bean.setContactName("TesterFirst TesterLastName");
		bean.setDisplayLabel("FMA");
		bean.setFormat("OWL-DL");
		bean.setIsFoundry(new Byte("0"));
		
		ArrayList<Integer> categoryIds = new ArrayList<Integer>();
		categoryIds.add(2810);
		categoryIds.add(2814);
		bean.setCategoryIds(categoryIds);
		
		ArrayList<Integer> groupIds = new ArrayList<Integer>();
		groupIds.add(6001);
		bean.setGroupIds(groupIds);
		
		// set inputFilePath
		//String inputFileStr = "/dev/cyoun/workspace/bioportal_resources/uploads/3000/0/pizza.owl";
		String inputFileStr = "C:\\Program Files\\Protege_3.4\\examples\\pizza\\pizza.owl";
		bean.setFilePath(inputFileStr);
		
		return bean;
	}
	
	private OntologyBean createOntolgyViewBean() {
		
		OntologyBean bean = new OntologyBean(true);
		
		bean.setUserId(2850);
		bean.setIsManual(new Byte("0"));
		
		bean.setVersionNumber("1.0");
		//default status Id should be populated upon creation
		//bean.setStatusId(new Integer(1));
		bean.setVersionStatus("pre-production");
		bean.setIsRemote(new Byte("0"));
		bean.setIsReviewed(new Byte("1"));
		bean.setDateCreated(new Date());
		bean.setDateReleased(new Date());
		bean.setContactEmail("test111@email.com");
		bean.setContactName("TesterFirst TesterLastName");
		bean.setDisplayLabel("Liver");
		bean.setFormat("OWL-DL");
		bean.setIsFoundry(new Byte("0"));
				
		ArrayList<Integer> categoryIds = new ArrayList<Integer>();
		categoryIds.add(2810);
		categoryIds.add(2814);
		bean.setCategoryIds(categoryIds);
		
		ArrayList<Integer> groupIds = new ArrayList<Integer>();
		groupIds.add(6001);
		bean.setGroupIds(groupIds);
		
		// set inputFilePath
		//String inputFileStr = "/dev/cyoun/workspace/bioportal_resources/uploads/3000/0/pizza.owl";
		String inputFileStr = "C:\\Program Files\\Protege_3.4\\examples\\pizza\\pizza.owl";
		bean.setFilePath(inputFileStr);
				
		bean.setViewDefinition("SELECT smthg FROM TEST");
		bean.setViewDefinitionLanguage("SPARQL");
		bean.setViewGenerationEngine("Jena/ARQ");
		
		ArrayList<Integer> ontVerIds = new ArrayList<Integer>();
		ontVerIds.add(4513);
		bean.setViewOnOntologyVersionId(ontVerIds);
		return bean;
	}

	
	private OntologyBean createOntolgyBeanManual() {	
		OntologyBean bean = createOntolgyBean();

		bean.setIsFoundry(new Byte("1"));
						
		return bean;
	}	
	
	private OntologyBean createOntolgyBeanRemote() {	
		OntologyBean bean = createOntolgyBean();

		bean.setIsRemote(new Byte("1"));
		bean.setFilePath(null);
				
		return bean;
	}
	
	public static FilePathHandler getFilePathHandler(OntologyBean ontologyBean) throws Exception {
		
		File inputFile = new File(ontologyBean.getFilePath());
		System.out.println("Testcase: getFilePathHandler() - inputfilepath = "
				+ ontologyBean.getFilePath());
		
		if (!inputFile.exists()) {
			System.out.println("Error! InputFile Not Found. Could not create filePathHanlder for input file.");
			throw new Exception("Error! InputFile Not Found. Could not create filePathHanlder for input file.");
		}

		FilePathHandler filePathHandler = new PhysicalDirectoryFilePathHandlerImpl(
				CompressedFileHandlerFactory.createFileHandler(ontologyBean
						.getFormat()), inputFile);
		
		return filePathHandler;

	}

}
