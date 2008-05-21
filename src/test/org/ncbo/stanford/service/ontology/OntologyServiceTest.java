/**
 * 
 */
package org.ncbo.stanford.service.ontology;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.generated.NcboLStatus;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.filehandler.FileHandler;
import org.ncbo.stanford.util.filehandler.impl.PhysicalDirectoryFileHandler;

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
	
	public void testFindOntology() {
		
		System.out.println ("OntologyServiceTest: testFindOntology()..........................BEGIN");
		
		OntologyBean ontologyBean = getOntologyService().findOntology(new Integer(3905));
		
		if (ontologyBean != null) {
			
			System.out.println (".....ContactEmail: " +  ontologyBean.getContactEmail());
			System.out.println (".....ContactName: " +  ontologyBean.getContactName());
		}
		
		System.out.println ("UserServiceTest: testFindUser()...........................DONE");
		
	}
	
	public void testUpdateOntologies() {
		


	}
		

	public void testCreateOntology() {
		
		System.out.println ("OntologyServiceTest: testCreateOntology()........................BEGIN");
			
		OntologyBean ontologyBean = createOntolgyBean();
		
			
		// set inputFilePath
//		String inputFileStr="/Users/nickgriffith/projects/smi.apps/BioPortalCore/test/sample_data/pizza.owl";
//		ontologyBean.setFilePath(inputFileStr);	
		
		getOntologyService().createOntology(ontologyBean);
		
		System.out.println ("OntologyServiceTest: testCreateOntology().........................DONE");
	}
		
/*
	public void testUpdateOntology() {
		
		System.out.println ("UserServiceTest: testUpdateUsers().......................BEGIN");
		
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
		
		System.out.println ("UserServiceTest: testUpdateUsers()........................DONE");
		
	}
*/	
	
/*	
	public void testDeleteUser() {
		
		System.out.println ("UserServiceTest: testDeleteUser()........................BEGIN");
		
		OntologyBean ontologyBean = getOntologyService().findOntology(new Integer(3905));
				
		if (ontologyBean != null) {
			
			System.out.println (".....Updating Ontology contact email to testemail_111@test.edu");
			
			ontologyBean.setContactEmail("testemail_111@test.edu")
			
			//getOntologyService().deleteOntology(ontologyBean);
		}
		else {
			System.out.println (".....No matching record found!");
		}
		
		System.out.println ("UserServiceTest: testDeleteUser().........................DONE");
		
	}
	*/
	
	private OntologyBean createOntolgyBean() {
		
		OntologyBean bean = new OntologyBean();
		
		bean.setUserId(12564);
		bean.setVersionNumber("5");
		bean.setInternalVersionNumber(new Integer(1));
		bean.setStatusId(new Integer(1));
		//bean.setVersionStatus("waiting");
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
		
		/*
		this.setId(ncboOntology.getId());
		this.setOntologyId(ncboOntology.getOntologyId());
		this.setParentId(ncboOntology.getParentId());
		this.setUserId(ncboOntology.getUserId());
		this.setInternalVersionNumber(ncboOntology.getInternalVersionNumber());
		this.setVersionNumber(ncboOntology.getVersionNumber());
		this.setVersionStatus(ncboOntology.getVersionStatus());
		this.setFilePath(ncboOntology.getFilePath());
		this.setIsCurrent(ncboOntology.getIsCurrent());
		this.setIsRemote(ncboOntology.getIsRemote());
		this.setIsReviewed(ncboOntology.getIsReviewed());
		this.setStatusId(ncboOntology.getStatusId());
		this.setDateCreated(ncboOntology.getDateCreated());
		this.setDateReleased(ncboOntology.getDateReleased());
		this.setDisplayLabel(ncboOntology.getDisplayLabel());
		this.setFormat(ncboOntology.getFormat());
		this.setContactName(ncboOntology.getContactName());
		this.setContactEmail(ncboOntology.getContactEmail());
		this.setHomepage(ncboOntology.getHomepage());
		this.setDocumentation(ncboOntology.getDocumentation());
		this.setPublication(ncboOntology.getPublication());
		this.setUrn(ncboOntology.getUrn());
		this.setCodingScheme(ncboOntology.getCodingScheme());
		this.setIsFoundry(ncboOntology.getIsFoundry());
		*/
		
		
		return bean;
	}
	
	
	private OntologyService getOntologyService() {
		
		OntologyService service = (OntologyService) applicationContext.getBean(
				"ontologyService", OntologyService.class);
	
		return service;
	}

}
