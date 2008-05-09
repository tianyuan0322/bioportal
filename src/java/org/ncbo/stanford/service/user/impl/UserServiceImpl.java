package org.ncbo.stanford.service.user.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserDAO;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.exception.DatabaseException;

@Transactional
public class UserServiceImpl implements UserService {

	private CustomNcboUserDAO ncboUserDAO = null;

	public UserBean findUser(Integer userId) throws DatabaseException {
		
		
		// populate userBean from ncbuUser with matched userId
		UserBean userBean = new UserBean();
		try {
			userBean.populateFromEntity(ncboUserDAO.findById(userId));
			
		} catch (Exception e)
        {
            throw new DatabaseException(e);
        }
		return userBean;
	}
	

	
	public UserBean findUser(String username) throws DatabaseException{
		
		// populate userBean from ncbuUser with matched username
		UserBean userBean = new UserBean();
		
		try {
			userBean.populateFromEntity(ncboUserDAO.getUserByUsername(username));
		
		} catch (Exception e)
        {
            throw new DatabaseException(e);
        }
		
		return userBean;
	}
	
	
	/**
	 * @return List<UserBean>
	 */
	public List<UserBean> findUsers() throws DatabaseException{
		
		List<NcboUser> ncboUserList = ncboUserDAO.findAll();
		List<UserBean> userBeanList = new ArrayList<UserBean>();
		UserBean userBean = new UserBean();
		
		// populate the query result to UserBean
		for ( Iterator<NcboUser> iter  = ncboUserList.iterator(); iter.hasNext(); ) {
				
			try {
				userBean.populateFromEntity((NcboUser)iter.next());
				userBeanList.add(userBean);
				
			} catch (Exception e)
	        {
	            throw new DatabaseException(e);
	        }
			
		}
		
		return userBeanList;
	}
	
	
	public void createUser(UserBean userBean) throws DatabaseException{
		
		// populate NcboUser from userBean
		NcboUser ncboUser = new NcboUser();
		
		try {
			userBean.populateToEntity(ncboUser);
			
			ncboUserDAO.save (ncboUser);
			
		} catch (Exception e)
        {
            throw new DatabaseException(e);
        }
		


	}
	
	
	public void updateUser(UserBean userBean) throws DatabaseException{
				
		try {
			// get ncboUser DAO instance using user_id
			NcboUser ncboUser = ncboUserDAO.findById(userBean.getId());

			// populate NcboUser from userBean
			userBean.populateToEntity(ncboUser);

			ncboUserDAO.save(ncboUser);

		} catch (Exception e) {
			throw new DatabaseException(e);
		}
	}
	


	public void deleteUser(UserBean userBean) throws DatabaseException{
		
		try {
			// get ncboUser DAO instance using user_id
			NcboUser ncboUser = ncboUserDAO.findById(userBean.getId());

			ncboUserDAO.delete(ncboUser);


		} catch (Exception e) {
			throw new DatabaseException(e);
		}
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
