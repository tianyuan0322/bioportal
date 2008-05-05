package org.ncbo.stanford.service.user.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserDAO;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.service.user.UserService;


@Transactional
public class UserServiceImpl implements UserService {

	private CustomNcboUserDAO ncboUserDAO = null;

	public UserBean findUser(Integer userId) {
		
		// populate userBean from ncbuUser with matched userId
		UserBean userBean = new UserBean();
		userBean.populateFromEntity(ncboUserDAO.findById(userId));
			
		return userBean;
	}
	

	
	public UserBean findUser(String username) {
		
		// populate userBean from ncbuUser with matched username
		UserBean userBean = new UserBean();
		userBean.populateFromEntity(ncboUserDAO.getUserByUsername(username));
		
		return userBean;
	}
	
	
	/**
	 * @return List<UserBean>
	 */
	public List<UserBean> findUsers() {
		
		List<NcboUser> ncboUserList = ncboUserDAO.findAll();
		List<UserBean> userBeanList = new ArrayList<UserBean>();
		UserBean userBean = new UserBean();
		
		// populate the query result to UserBean
		for ( Iterator<NcboUser> iter  = ncboUserList.iterator(); iter.hasNext(); ) {
						
			userBean.populateFromEntity((NcboUser)iter.next());
			userBeanList.add(userBean);
			
		}
		
		return userBeanList;
	}
	
	
	public void createUser(UserBean userBean) {
		
		// populate NcboUser from userBean
		NcboUser ncboUser = new NcboUser();
		userBean.populateToEntity(ncboUser);
		
		ncboUserDAO.save (ncboUser);

	}
	
	
	public void updateUser(UserBean userBean) {
				
		// get ncboUser DAO instance using user_id
		NcboUser ncboUser = ncboUserDAO.findById(userBean.getId());
		
		// populate NcboUser from userBean
		userBean.populateToEntity(ncboUser);
				
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

	

}
