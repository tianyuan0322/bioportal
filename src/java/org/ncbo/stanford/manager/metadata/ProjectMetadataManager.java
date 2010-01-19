package org.ncbo.stanford.manager.metadata;

import java.util.List;

import org.ncbo.stanford.bean.ProjectBean;

/**
 * Organizes access to project descriptions stored in the metadata ontology.
 * <p>
 * <strong>NOTE: These APIs are experimental, and will change in the near future.</strong>
 *
 * @author <a href="mailto:tony@loeser.name">Tony Loeser</a>
 */
public interface ProjectMetadataManager {
	
	public void saveProject(ProjectBean pb) throws Exception;
	
	public void deleteProject(Integer id) throws Exception;
	
	public ProjectBean getProject(Integer id) throws Exception;
	
	public List<ProjectBean> getAllProjects() throws Exception;
	
//	public void getProjectsForOntology(OntologyBean ob) throws Exception;

}
