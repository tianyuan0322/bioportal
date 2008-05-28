package org.ncbo.stanford.service.user;

import java.util.List;

import org.ncbo.stanford.bean.UserBean;

public interface UserService {
	public UserBean findUser(Integer userId);
	public UserBean findUser(String username);
	public List<UserBean> findUsers();
	public void createUser(UserBean userBean);
	public void updateUser(UserBean userBean);
	public void deleteUser(UserBean userBean);
	
}
