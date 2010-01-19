package org.ncbo.stanford.manager.metadata.impl;

import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.ProjectBean;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.manager.metadata.ProjectMetadataManager;
import org.springframework.beans.factory.annotation.Autowired;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

public class ProjectMetadataManagerImplTest extends AbstractBioPortalTest {
	
	@Autowired
	ProjectMetadataManager projMan;

	@Test
	public void testCreateAndDelete() throws Exception {
		ProjectBean pb = new ProjectBean();
		pb.setName("name_1");
		pb.setDescription("description_1");
		pb.setHomePage("homepage_1");
		pb.setInstitution("institution_1");
		pb.setPeople("people_1");
		pb.setUserId(new Integer(12));
		
		projMan.saveProject(pb);
		Integer id = pb.getId();
		
		ProjectBean pb_1 = projMan.getProject(id);
		Assert.assertEquals("Retrieved properties didn't match", pb_1.getDescription(), pb.getDescription());
		Assert.assertEquals("Retrieved properties didn't match", pb_1.getHomePage(), pb.getHomePage());
		Assert.assertEquals("Retrieved properties didn't match", pb_1.getInstitution(), pb.getInstitution());
		Assert.assertEquals("Retrieved properties didn't match", pb_1.getUserId(), pb.getUserId());
		
		projMan.deleteProject(id);
		try {
			ProjectBean pb_2 = projMan.getProject(id);
			Assert.fail("Attempt to retrieve deleted project should throw exception");
		} catch (MetadataObjectNotFoundException monfe) {
			// OK
		} catch (Exception e) {
			Assert.fail("Incorrect exception on get of non-existent project: "+e);
		}
	}
	
	@Test
	public void testListProjectInstances() throws Exception {
		ProjectMetadataManagerImpl pmImpl = (ProjectMetadataManagerImpl)projMan;
		OWLModel metadataOntology = pmImpl.getMetadataOWLModel();
		OWLNamedClass reviewClass = metadataOntology.getOWLNamedClass(pmImpl.PROJECT_CLASS_NAME);
		for (Iterator instIt = reviewClass.getInstances(false).iterator(); instIt.hasNext(); ) {
			OWLIndividual nextInd = (OWLIndividual)instIt.next();
			Integer id = pmImpl.convertNameToId(nextInd.getName());
			System.out.print("Project id="+id);
			if (nextInd.isDeleted()) {
				System.out.println(" (deleted)");
			} else {
				System.out.println();
			}
		}
	}
}
