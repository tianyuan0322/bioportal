package org.ncbo.stanford.manager.metakb;

import java.util.List;

import org.ncbo.stanford.bean.metadata.ProjectBean;
import org.ncbo.stanford.exception.MetadataException;

/**
 * Store for Project objects in the Metadata KB.
 * 
 * @author Tony Loeser
 */
public interface ProjectMetadataManager extends SimpleObjectManager<ProjectBean> {
	
	/**
	 * Return a list of all projects owned by the indicated user.
	 *
	 * @throws MetadataException when there is a problem creating the project java beans
	 */
	public List<ProjectBean> getProjectsForUser(Integer userId) throws MetadataException;
}
