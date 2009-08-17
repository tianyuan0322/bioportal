package org.ncbo.stanford.manager.metadata;

import java.util.List;

import org.ncbo.stanford.bean.GroupBean;

/**
 * An interface for all API specific user metadata managers to conform to. This allows
 * for a seamless Spring injection.
 * 
 * @author Csongor Nyulas
 * 
 */
public interface OntologyGroupMetadataManager {

	/**
	 * Saves the user information specified by the groupBean.
	 * 
	 * @param groupBean
	 * @throws Exception
	 */
	public void saveOntologyGroup(GroupBean groupBean) throws Exception;

	
	/**
	 * Retrieves the groupBean representing an ontology group for a specific group id.
	 * 
	 * @param groupId
	 */
	public GroupBean findGroupById(Integer groupId);
	
	/**
	 * Returns the list of groupBeans, one for each group in the metadata.
	 *  
	 * @return the list of group beans
	 * @throws Exception 
	 */
	public List<GroupBean> findAllGroups();
}
