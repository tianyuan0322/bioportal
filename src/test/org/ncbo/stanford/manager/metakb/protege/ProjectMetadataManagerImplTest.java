package org.ncbo.stanford.manager.metakb.protege;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.ProjectBean;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Basic test for storing ProjectBean to the metadata kb.
 * 
 * @author <a href="mailto:tony@loeser.name">Tony Loeser</a>
 */
public class ProjectMetadataManagerImplTest extends AbstractBioPortalTest {

	@Autowired
	ProjectMetadataManagerImpl projectMan;
	
	@Test
	public void testCrudOperations() throws Exception {
		
		// == CREATE ==
		
		ProjectBean pb = projectMan.createInstance();
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
		projectMan.updateInstance(pb);
		
		// == RETRIEVE ==
		
		ProjectBean pb_2 = projectMan.retrieveInstance(id);
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
		
		projectMan.deleteInstance(id);
		try {
			projectMan.retrieveInstance(id);
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
	public void cleanUpInstances() throws Exception {
		Collection<ProjectBean> projects = projectMan.getAllProjects();
		for (Iterator<ProjectBean> pIt = projects.iterator(); pIt.hasNext(); ) {
			ProjectBean pBean = pIt.next();
			projectMan.deleteInstance(pBean.getId());
			System.out.println("Deleted instance id="+pBean.getId());
		}
		
	}
}
