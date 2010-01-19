package org.ncbo.stanford.service.metadata.impl;

import java.util.List;

import org.ncbo.stanford.bean.ProjectBean;
import org.ncbo.stanford.manager.metadata.ProjectMetadataManager;
import org.ncbo.stanford.service.metadata.ProjectMetadataService;

public class ProjectMetadataServiceImpl implements ProjectMetadataService {
	
	ProjectMetadataManager projectMetadataManager;

	@Override
	public void saveProject(ProjectBean pb) throws Exception {
		projectMetadataManager.saveProject(pb);
	}

	@Override
	public ProjectBean getProjectForId(Integer id) throws Exception {
		return projectMetadataManager.getProject(id);
	}

	@Override
	public boolean deleteProject(Integer id) throws Exception {
		projectMetadataManager.deleteProject(id);
		return true;
	}
	
	@Override
	public List<ProjectBean> getAllProjects() throws Exception {
		return projectMetadataManager.getAllProjects();
	}


	/**
	 * Standard setter method.
	 */
	public void setProjectMetadataManager(
			ProjectMetadataManager projectMetadataManager) {
		this.projectMetadataManager = projectMetadataManager;
	}

}
