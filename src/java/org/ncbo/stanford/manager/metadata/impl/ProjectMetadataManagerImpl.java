package org.ncbo.stanford.manager.metadata.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.ncbo.stanford.bean.ProjectBean;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.manager.AbstractOntologyMetadataManager;
import org.ncbo.stanford.manager.metadata.ProjectMetadataManager;
import org.ncbo.stanford.util.metadata.ProjectMetadataUtils;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

public class ProjectMetadataManagerImpl extends AbstractOntologyMetadataManager
		implements ProjectMetadataManager {

	static String PROJECT_CLASS_NAME = "metadata:Project";
	static String PROJECT_INST_STEM = "http://protege.stanford.edu/ontologies/metadata/BioPortalMetadataKnowledgeBase.owl#Project_";
	
	@Override
	public void saveProject(ProjectBean projectBean) throws Exception {
		// If the ProjectBean id is not set, then this is a new object and needs to be 
		// created in the KB.  Otherwise, it should correspond to an existing kb object.
		Integer id = projectBean.getId();
		OWLIndividual projectInd = null;
		
		if (id == null) {
			// Create the fresh instance in the KB
			OWLModel metadataOntology = getMetadataOWLModel(); // throws Exception
			OWLNamedClass projectClass = metadataOntology.getOWLNamedClass(PROJECT_CLASS_NAME); // returns null on error!!
			projectInd = projectClass.createOWLIndividual(null);
			// Set the bean id to match the new instance name
			projectBean.setId(convertNameToId(projectInd.getName()));
		} else {
			// Retrieve the existing instance from the KB
			projectInd = getProjectIndividual(id);
		}
		
		// Copy the property values into the bean
		ProjectMetadataUtils.fillInProjectPropertiesFromBean(projectInd, projectBean);
	}


	@Override
	public ProjectBean getProject(Integer id) throws Exception {
		OWLIndividual projectInd = getProjectIndividual(id);

		// Build the corresponding bean and return it
		ProjectBean pb = new ProjectBean();
		pb.setId(convertNameToId(projectInd.getName()));
		ProjectMetadataUtils.fillInBeanPropertiesFromProject(projectInd, pb);
		return pb;
	}
	
	@Override
	public void deleteProject(Integer id) throws Exception {
		OWLIndividual projectInd = getProjectIndividual(id);
		projectInd.delete();
	}
	
	@Override
	public List<ProjectBean> getAllProjects() throws Exception {
		OWLModel metadataOntology = getMetadataOWLModel(); // throws Exception
		OWLNamedClass projectClass = metadataOntology.getOWLNamedClass(PROJECT_CLASS_NAME); // returns null on error!!
		
		Collection<?> allInstances = projectClass.getInstances(false);
		
		List<ProjectBean> result = new ArrayList<ProjectBean>();
		for (Iterator<?> allInstIt = allInstances.iterator(); allInstIt.hasNext(); ) {
			OWLIndividual projectInd = (OWLIndividual)allInstIt.next();
			if (!projectInd.isBeingDeleted() && !projectInd.isDeleted()) {
				ProjectBean pb = new ProjectBean();
				pb.setId(convertNameToId(projectInd.getName()));
				ProjectMetadataUtils.fillInBeanPropertiesFromProject(projectInd, pb);
				result.add(pb);
			}
		}
		
		return result;
	}

	// ============================================================
	// Helpers

	// Retrieve a project from the knowledge base
	private OWLIndividual getProjectIndividual(Integer id) throws Exception {
		OWLModel metadataOntology = getMetadataOWLModel(); // throws Exception
		String name = convertIdToName(id);
		OWLIndividual projectInd = metadataOntology.getOWLIndividual(name);
		if (projectInd == null || projectInd.isDeleted() || projectInd.isBeingDeleted()) {
			throw new MetadataObjectNotFoundException("Could not find project id="+id+" in metadata knowledge base");
		}
		return projectInd;
	}
	
	// Extract the ID from the KB instance's name
	protected Integer convertNameToId(String name) {
		int numStart = name.lastIndexOf('_') + 1;
		return new Integer(name.substring(numStart));
	}
	
	// Construct a KB instance name from the ID
	protected String convertIdToName(Integer id) {
		return PROJECT_INST_STEM+id;
	}
}
