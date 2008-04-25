package org.ncbo.stanford.service.user.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserDAO;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.service.user.UserService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserServiceImpl implements UserService {

	private CustomNcboUserDAO ncboUserDAO = null;

	public UserBean findUser(Integer userId) {
		return populateUser(ncboUserDAO.findById(userId));
	}
	

	public UserBean findUser(String username) {
		return populateUser(ncboUserDAO.getUserByUsername(username));
	}
	
	/**
	 * @return List<UserBean>
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
	
	
	public void createUser(UserBean userBean) {
				
		ncboUserDAO.save(populateUser(userBean));
	}
	
	
	public void updateUser(UserBean userBean) {
				
		// get ncboUser DAO instance using user_id
		NcboUser ncboUser = ncboUserDAO.findById(userBean.getId());
		
		// update the DAO instance
		populateNcboUser(userBean, ncboUser);
				
		ncboUserDAO.save(ncboUser);
	}
	


	public void deleteUser(UserBean userBean) {
		
		// get ncboUser DAO instance using user_id
		NcboUser ncboUser = ncboUserDAO.findById(userBean.getId());
		
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
	 * populate NcboUser instance from UserBean instance
	 * source: UserBean, destination: NcboUser
	 * 
	 * @param UserBean, NcboUser
	 */
	public NcboUser populateNcboUser(UserBean userBean, NcboUser ncboUser) {

		if (userBean != null) {
			
			ncboUser.setId(userBean.getId());
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
	
	
	/**
	 * Convert NcboUser to UserBean
	 * 
	 * @param NcboUser
	 */
	public UserBean populateUser(NcboUser ncboUser) {
		UserBean userBean = null;

		if (ncboUser != null) {
			userBean = new UserBean();
			userBean.setId(ncboUser.getId());
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
			ncboUser.setId(userBean.getId());
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
