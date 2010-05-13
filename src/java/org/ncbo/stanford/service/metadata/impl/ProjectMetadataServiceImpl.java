package org.ncbo.stanford.service.metadata.impl;

import java.util.List;

import org.ncbo.stanford.bean.ProjectBean;
import org.ncbo.stanford.manager.metakb.ProjectMetadataManager;
import org.ncbo.stanford.manager.metakb.SimpleObjectManager;
import org.ncbo.stanford.service.metadata.ProjectMetadataService;

/**
 * Implementation of {@link ProjectMetadataService}.
 *
 * @author Tony Loeser
 */
public class ProjectMetadataServiceImpl extends BeanCRUDServiceImpl<ProjectBean>
		implements ProjectMetadataService {
	
	ProjectMetadataManager projectMetadataManager;
	
	// Implements BeanCRUDServiceImpl
	protected SimpleObjectManager<ProjectBean> getObjectManager() {
		return projectMetadataManager;
	}

	// Implements interface
	public List<ProjectBean> getProjectsForUser(Integer id) throws Exception {
		return projectMetadataManager.getProjectsForUser(id);
	}

	// Standard setter
	public void setProjectMetadataManager(ProjectMetadataManager projectMetadataManager) {
		this.projectMetadataManager = projectMetadataManager;
	}	
}
