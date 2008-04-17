package org.ncbo.stanford.service.user.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
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
	 * @return List<UserBean>
	 * 			added by cyoun
	 */
	public List<UserBean> findUsers() {
		
		List<NcboUser> ncboUserList = ncboUserDAO.findAll();
		List<UserBean> userBeanList = new ArrayList<UserBean>();
		
		// populate the query result to UserBean
		for ( Iterator<NcboUser> iter  = ncboUserList.iterator(); iter.hasNext(); ) {
			userBeanList.add(populateUser( (NcboUser)iter.next()));
		}
		
		return userBeanList;
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
