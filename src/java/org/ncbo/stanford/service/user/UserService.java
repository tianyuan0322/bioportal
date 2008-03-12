package org.ncbo.stanford.service.user;

import org.ncbo.stanford.bean.UserBean;

public interface UserService {
	public UserBean getUser(Integer userId);
	public UserBean getUser(String username);
}
