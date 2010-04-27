package org.ncbo.stanford.manager.metakb;

import java.util.List;

import org.ncbo.stanford.bean.ProjectBean;

/**
 * Store for Project objects in the Metadata KB.
 * 
 * @author tony
 */
public interface ProjectMetadataManager extends SimpleObjectManager<ProjectBean> {
	
	public List<ProjectBean> getProjectsForUser(Integer userId) throws Exception;
	
	public List<ProjectBean> getAllProjects() throws Exception;
}
