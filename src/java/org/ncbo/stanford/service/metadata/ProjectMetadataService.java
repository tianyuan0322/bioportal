package org.ncbo.stanford.service.metadata;

import java.util.List;

import org.ncbo.stanford.bean.ProjectBean;

/**
 * A thin service layer that forwards calls to 
 * {@link org.ncbo.stanford.manager.metakb.ProjectMetadataManager}.
 * 
 * @author Tony Loeser
 */
public interface ProjectMetadataService extends BeanCRUDService<ProjectBean> {

	/**
	 * Forward to the corresponding manager method.
	 * 
	 * @see org.ncbo.stanford.manager.metakb.ProjectMetadataManager#getProjectsForUser(Integer)
	 */
	public List<ProjectBean> getProjectsForUser(Integer id) throws Exception;
}
