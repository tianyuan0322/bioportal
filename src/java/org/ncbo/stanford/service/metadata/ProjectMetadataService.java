package org.ncbo.stanford.service.metadata;

import java.util.Collection;

import org.ncbo.stanford.bean.metadata.ProjectBean;

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
	public Collection<ProjectBean> getProjectsForUser(Integer id) throws Exception;
}
