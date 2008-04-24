package org.ncbo.stanford.service.user.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

	public void saveUser(NcboUser ncboUser) {
		
		ncboUserDAO.save(ncboUser);
	}


	public void deleteUser(NcboUser ncboUser) {
		
		ncboUserDAO.delete(ncboUser);
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
	 * Convert NcboUser to UserBean
	 * 
	 * @param NcboUser
	 */
	public UserBean populateUser(NcboUser ncboUser) {
		UserBean userBean = null;

		if (ncboUser != null) {
			userBean = new UserBean();
			userBean.setUsername(ncboUser.getUsername());
			userBean.setPassword(ncboUser.getPassword());
			userBean.setEmail(ncboUser.getEmail());
			userBean.setFirstname(ncboUser.getFirstname());
			userBean.setLastname(ncboUser.getLastname());
			userBean.setPhone(ncboUser.getPhone());
			userBean.setDateCreated(ncboUser.getDateCreated());
		}

		return userBean;
	}
	
	/**
	 * Convert UserBean to NcboUser
	 * 
	 * @param UserBean
	 */
	public NcboUser populateUser(UserBean userBean) {
		NcboUser ncboUser = null;

		if (userBean != null) {
			ncboUser = new NcboUser();
			ncboUser.setUsername(userBean.getUsername());
			ncboUser.setPassword(userBean.getPassword());
			ncboUser.setEmail(userBean.getEmail());
			ncboUser.setFirstname(userBean.getFirstname());
			ncboUser.setLastname(userBean.getLastname());
			ncboUser.setPhone(userBean.getPhone());
			ncboUser.setDateCreated(userBean.getDateCreated());
		}

		return ncboUser;
	}
}
