package org.ncbo.stanford.manager.metakb.protege;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.ProjectBean;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;

/**
 * Basic test for storing ProjectBean in the metadata kb.
 * 
 * @author Tony Loeser
 */
public class ProjectMetadataManagerImplTest extends AbstractBioPortalTest {

	@Autowired
	ProjectMetadataManagerImpl projectMan;
	
	@Test
	public void testCrudOperations() throws Exception {
		
		// == CREATE ==
		
		ProjectBean pb = projectMan.createObject();
		Integer id = pb.getId();

		pb.setName("name_1");
		pb.setDescription("description_1");
		pb.setHomePage("homepage_1");
		pb.setInstitution("institution_1");
		pb.setPeople("people_1");
		pb.setUserId(new Integer(12));
		
		
		Assert.assertNotNull("Problem setting dateCreated", pb.getDateCreated());
		Assert.assertNotNull("Problem setting dateModified", pb.getDateModified());
		
		// == UPDATE == 
		
		pb.setName("name_2");
		projectMan.updateObject(pb);
		
		// == RETRIEVE ==
		
		ProjectBean pb_2 = projectMan.retrieveObject(id);
		Assert.assertEquals("Retrieved properties didn't match", pb_2.getDescription(), pb.getDescription());
		Assert.assertEquals("Retrieved properties didn't match", pb_2.getUserId(), pb.getUserId());
		Assert.assertEquals("Update seems not to have worked", pb_2.getName(), pb.getName());
		
		// testing dates is difficult because of differing precision between java and kb
		Assert.assertEquals("dateCreated retrieval issue",
							convertDateToSeconds(pb_2.getDateCreated()),
							convertDateToSeconds(pb.getDateCreated()));
		Assert.assertNotSame("dateModified didn't seem to update",
							 convertDateToSeconds(pb_2.getDateModified()),
							 convertDateToSeconds(pb_2.getDateCreated()));
		
		// == DELETE ==
		
		projectMan.deleteObject(id);
		try {
			projectMan.retrieveObject(id);
			Assert.fail("Attempt to retrieve deleted project should throw exception");
		} catch (MetadataObjectNotFoundException e) {
			// OK
		} catch (Exception e) {
			Assert.fail("Incorrect exception on get of non-existent project: "+e);
		}
	}
	
	public long convertDateToSeconds(Date date) {
		return date.getTime() / 1000;
	}
	
	@Test
	public void testGetForUser() throws Exception {
		List<ProjectBean> results = projectMan.getProjectsForUser(new Integer(12));
		System.out.println("Got a few: "+results.size());
	}

	@Test
	public void testPropertyStuff() throws Exception {
		OWLModel metaKb = projectMan.getMetadataKb();
		String pName = "http://protege.stanford.edu/ontologies/ChAO/changes.rdfs#oldName";
		RDFProperty rProp = metaKb.getRDFProperty(pName);
		System.out.println("RDFProperty type: "+rProp.getClass().getName());
		OWLProperty oProp = metaKb.getOWLProperty(pName);
		System.out.println("OWLProperty type: "+oProp.getClass().getName());
	}
	
//	@Test
//	public void cleanUpInstances() throws Exception {
//		// delete all instances in the KB. CAREFUL
//		Collection<ProjectBean> projects = projectMan.retrieveAllObjects();
//		for (Iterator<ProjectBean> pIt = projects.iterator(); pIt.hasNext(); ) {
//			ProjectBean pBean = pIt.next();
//			projectMan.deleteObject(pBean.getId());
//			System.out.println("Deleted instance id="+pBean.getId());
//		}
//	}
}
