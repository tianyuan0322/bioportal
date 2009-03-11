package org.ncbo.stanford.manager.metadata;

import org.ncbo.stanford.bean.CategoryBean;
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

}
