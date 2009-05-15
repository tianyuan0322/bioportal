package org.ncbo.stanford.manager.metadata;

import org.ncbo.stanford.bean.CategoryBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.UserBean;

/**
 * An interface for all API specific user metadata managers to conform to. This allows
 * for a seamless Spring injection.
 * 
 * @author Csongor Nyulas
 * 
 */
public interface UserMetadataManager {

	/**
	 * Saves the user information specified by the userBean.
	 * 
	 * @param userBean
	 * @throws Exception
	 */
	public void saveUser(UserBean userBean) throws Exception;

	
	/**
	 * Retrieves the userBean representing a user for a specific user id.
	 * 
	 * @param userId
	 */
	public UserBean findUserById(Integer userId);

	//These methods have been declared only for testing purposes, until 
	//there will be a final decision about whether user and user roles
	//management will or will not be also done through ontological metadata.
	//If yes, it will be probably necessary to create a UserRoleBean class, 
	//and which should be used throughout the project.
	@Deprecated
	public void saveUserRole(Integer roleId, String name, String description) throws Exception;
	@Deprecated
	public String findUserRoleNameById(Integer roleId);

}
