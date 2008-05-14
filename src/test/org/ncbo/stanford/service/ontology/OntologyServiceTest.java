/**
 * 
 */
package org.ncbo.stanford.service.ontology;

import java.util.Date;
import java.util.List;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.ontology.OntologyService;

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
		
		OntologyBean ontologyBean = getOntologyService().findOntology(4519);
		
		if (ontologyBean != null) {
			System.out.println ("...getContactEmail is " +  ontologyBean.getContactEmail());
			System.out.println ("...getContactName is " +  ontologyBean.getContactName());
		}
		
		System.out.println ("UserServiceTest: testFindUser()...........................DONE");
		
	}
	
	public void testUploadOntologies() {

	}
		
/*
	public void testCreateOntology() {
		
		System.out.println ("OntologyServiceTest: testCreateOntology()........................BEGIN");
			
		OntologyBean ontologyBean = createOntolgyBean();
		getOntologyService().createOntology(ontologyBean);
		
		System.out.println ("OntologyServiceTest: testCreateOntology().........................DONE");
	}
		

	public void testUpdateUser() {
		
		System.out.println ("UserServiceTest: testUpdateUsers().......................BEGIN");
		
		UserBean userBean = getUserService().findUser("myusername");
				
		if (userBean != null) {
			
			System.out.println (".....Updating phone number to 111-222-3333");
			
			userBean.setPhone("333-222-3333");
			getUserService().updateUser(userBean);
		}
		else {
			System.out.println (".....No matching record!");
		}
		
		System.out.println ("UserServiceTest: testUpdateUsers()........................DONE");
		
	}
	
	
	
	public void testDeleteUser() {
		
		System.out.println ("UserServiceTest: testDeleteUser()........................BEGIN");
		
		// "getUser" by username does not work if duplicate is allowed in DB.
		UserBean userBean = getUserService().findUser("myusername");
		
		if ( userBean != null ) {

			getUserService().deleteUser(userBean);
			
		} else {
			System.out.println (".....No matching record!");
		}
		
		System.out.println ("UserServiceTest: testDeleteUser().........................DONE");
		
	}
	*/
	
	private OntologyBean createOntolgyBean() {
		
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
	
	
	private OntologyService getOntologyService() {
		
		OntologyService service = (OntologyService) applicationContext.getBean(
				"ontologyService", OntologyService.class);
	
		return service;
	}

}
