package org.ncbo.stanford.service.user;

import java.util.List;

import org.ncbo.stanford.bean.UserBean;

public interface UserService {
	public UserBean getUser(Integer userId);
	public UserBean getUser(String username);
	public List<UserBean> findUsers();
}
