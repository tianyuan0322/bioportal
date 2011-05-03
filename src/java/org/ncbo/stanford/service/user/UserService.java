package org.ncbo.stanford.service.user;

import java.util.List;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.service.session.RESTfulSession;

public interface UserService {
	public UserBean findUser(Integer userId);

	public UserBean findUser(String username);

	public UserBean findUserByEmail(String email);

	public UserBean findUserByApiKey(String apiKey);
	
	public List<UserBean> findUsers();

	public RESTfulSession createUser(UserBean userBean);

	public RESTfulSession updateUser(UserBean userBean);

	public void deleteUser(UserBean userBean);

}
