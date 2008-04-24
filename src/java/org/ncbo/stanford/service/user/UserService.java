package org.ncbo.stanford.service.user;

import java.util.List;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.domain.generated.NcboUser;

public interface UserService {
	public UserBean getUser(Integer userId);
	public UserBean getUser(String username);
	public List<UserBean> findUsers();
	public void saveUser(NcboUser ncboUser);
	public void deleteUser(NcboUser ncboUser);
	public NcboUser populateUser(UserBean userBean);
	public UserBean populateUser(NcboUser ncboUser);
	
}
