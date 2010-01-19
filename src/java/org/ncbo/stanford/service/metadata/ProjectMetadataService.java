package org.ncbo.stanford.service.metadata;

import java.util.List;

import org.ncbo.stanford.bean.ProjectBean;

/**
 * @author tony
 *
 */
public interface ProjectMetadataService {

	public void saveProject(ProjectBean pb) throws Exception;
	
	public ProjectBean getProjectForId(Integer id) throws Exception;
	
	public boolean deleteProject(Integer id) throws Exception;
	
	public List<ProjectBean> getAllProjects() throws Exception;
}
