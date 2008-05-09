package org.ncbo.stanford.service.user;

import java.util.List;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.exception.DatabaseException;

public interface UserService {
	//public UserBean findUser(Integer userId);
	public UserBean findUser(Integer userId) throws DatabaseException;
	public UserBean findUser(String username) throws DatabaseException;
	public List<UserBean> findUsers() throws DatabaseException;
	public void createUser(UserBean userBean) throws DatabaseException;
	public void updateUser(UserBean userBean) throws DatabaseException;
	public void deleteUser(UserBean userBean) throws DatabaseException;
	
}
