package org.ncbo.stanford.service.user.impl;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserDAO;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.service.user.UserService;

public class UserServiceImpl implements UserService {

	private CustomNcboUserDAO ncboUserDAO = null;

	public UserBean getUser(Integer userId) {
		return populateUser(ncboUserDAO.findById(userId));
	}

	public UserBean getUser(String username) {
		return populateUser(ncboUserDAO.getUserByUsername(username));
	}

	/**
	 * @return the ncboUserDAO
	 */
	public CustomNcboUserDAO getNcboUserDAO() {
		return ncboUserDAO;
	}

	/**
	 * @param ncboUserDAO
	 *            the ncboUserDAO to set
	 */
	public void setNcboUserDAO(CustomNcboUserDAO ncboUserDAO) {
		this.ncboUserDAO = ncboUserDAO;
	}

	/**
	 * Populate the return user bean
	 * 
	 * @param sourceUser
	 */
	private UserBean populateUser(NcboUser sourceUser) {
		UserBean targetUser = null;

		if (sourceUser != null) {
			targetUser = new UserBean();
			targetUser.setUsername(sourceUser.getUsername());
			targetUser.setEmail(sourceUser.getEmail());
			targetUser.setFirstname(sourceUser.getFirstname());
			targetUser.setLastname(sourceUser.getLastname());
			targetUser.setPhone(sourceUser.getPhone());
			targetUser.setDateCreated(sourceUser.getDateCreated());
		}

		return targetUser;
	}
}
