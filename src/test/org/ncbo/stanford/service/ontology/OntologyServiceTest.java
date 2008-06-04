/**
 * 
 */
package org.ncbo.stanford.service.ontology;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;

/**
 * @author nickgriffith
 * 
 */

public class OntologyServiceTest extends AbstractBioPortalTest {

	/*
	public void testfindLatestOntologyVersions() {

		System.out.println ("OntologyServiceTest: testfindLatestOntologyVersions().......................BEGIN");
		
		List<OntologyBean> ontologies = getOntologyService().findLatestOntologyVersions();

		for (OntologyBean ontology : ontologies) {

			System.out.println(ontology.toString());
		}
		
		System.out.println ("OntologyServiceTest: testfindLatestOntologyVersions().........................DONE");

	}
*/
	
	
	public void testSearchOntologyMetadata(){	
		List<OntologyBean> ontologies = getOntologyService().searchOntologyMetadata("Mouse");

		for (OntologyBean ontology : ontologies) {

			System.out.println(ontology.toString());
		}		
	}
	
	public void testFindOntology() {
		
		System.out.println ("OntologyServiceTest: testFindOntology()..........................BEGIN");
		
		OntologyBean ontologyBean = getOntologyService().findOntology(new Integer(3905));
		
		if (ontologyBean != null) {
			
			System.out.println (".....ContactEmail: " +  ontologyBean.getContactEmail());
			System.out.println (".....ContactName: " +  ontologyBean.getContactName());
		}
		
		System.out.println ("OntologyServiceTest: testFindOntology()...........................DONE");
		
	}
		
	/*
	public void testCreateOntology() {
		
		System.out.println ("OntologyServiceTest: testCreateOntology()........................BEGIN");
			
		OntologyBean ontologyBean = createOntolgyBean();
		
		getOntologyService().createOntology(ontologyBean);
		
		System.out.println ("OntologyServiceTest: testCreateOntology().........................DONE");
	}
	*/
	
	
	/*
	public void testCleanupOntologyCategory() {
		
		System.out.println ("OntologyServiceTest: testCleanupOntologyCategory()........................BEGIN");
		
		OntologyBean ontologyBean = getOntologyService().findOntology(new Integer(34280));
		
		if (ontologyBean != null) {

			getOntologyService().cleanupOntologyCategory(ontologyBean,
					ontologyBean.getCategoryIds());

			System.out
					.println("OntologyServiceTest: testCleanupOntologyCategory().........................DONE");
		}
	
	}
	*/
	
	
/*
	public void testUpdateOntology() {
		
		System.out.println ("OntologyServiceTest: testUpdateOntology().......................BEGIN");
		
		OntologyBean ontologyBean = getOntologyService().findOntology(new Integer(3905));
	
		// test updating some properties
		if (ontologyBean != null) {
			
			System.out.println (".....Updating Ontology Bean - Version number to 2.222");
			ontologyBean.setVersionNumber("2.222");
			
			System.out.println (".....Updating Ontology Bean - Metadata ");
			ontologyBean.setContactEmail("testemail_111@test.edu");
			ontologyBean.setContactName("NewFirstname NewLastname");
			System.out.println (".....ContactEmail: " +  ontologyBean.getContactEmail());
			System.out.println (".....ContactName: " +  ontologyBean.getContactName());
			
			getOntologyService().updateOntology(ontologyBean);
		}
		else {
			System.out.println (".....No matching record found!");
		}
		
		System.out.println ("OntologyServiceTest: testUpdateOntology()........................DONE");
		
	}
*/	

	
	
	public void testDeleteOntology() {
		
		System.out.println ("OntologyServiceTest: testDeleteOntology()........................BEGIN");
		
		OntologyBean ontologyBean = getOntologyService().findOntology(new Integer(34321));
				
		if (ontologyBean != null) {
			
			System.out.println (".....Deleting Ontology where internal ID = 34321");
			
			getOntologyService().deleteOntology(ontologyBean);
		}
		else {
			System.out.println (".....No matching record found!");
		}
		
		System.out.println ("OntologyServiceTest: testDeleteOntology().........................DONE");
		
	}
	
	

	
	private OntologyBean createOntolgyBean() {
		
		OntologyBean bean = new OntologyBean();
		
		bean.setUserId(12564);
		bean.setVersionNumber("1.0");
		bean.setStatusId(new Integer(1));
		bean.setVersionStatus("waiting");
		bean.setIsCurrent(new Byte("1"));
		bean.setIsRemote(new Byte("0"));
		bean.setIsReviewed(new Byte("1"));
		bean.setDateCreated(new Date());
		bean.setDateReleased(new Date());
		bean.setContactEmail("test111@email.com");
		bean.setContactName("TesterFirst TesterLastName");
		bean.setDisplayLabel("Pizza Ontology");
		bean.setFormat("OWL-DL");
		bean.setIsFoundry(new Byte("0"));
				
		ArrayList<Integer> categoryIds = new ArrayList<Integer>();
		categoryIds.add(2807);
		categoryIds.add(2821);
		bean.setCategoryIds(categoryIds);
		
		// set inputFilePath
		//String inputFileStr = "/Users/nickgriffith/projects/smi.apps/BioPortalCore/test/sample_data/pizza.owl";
		
		String inputFileStr = "/dev/cyoun/workspace/bioportal_resources/uploads/3000/0/pizza.owl";
		bean.setFilePath(inputFileStr);
		
//		ArrayList<String> fileNames = new ArrayList<String>();
//		fileNames.add("pizza.owl");
//		//fileNames.add("testFileName2.OWL");
//		bean.setFilenames(fileNames);
				
		/*
		bean.setPublication(ncboOntology.getPublication());
		bean.setUrn(ncboOntology.getUrn());
		bean.setCodingScheme(ncboOntology.getCodingScheme());
		*/
		
		
		return bean;
	}
	
	
	private OntologyService getOntologyService() {
		
		OntologyService service = (OntologyService) applicationContext.getBean(
				"ontologyService", OntologyService.class);
	
		return service;
	}

}
