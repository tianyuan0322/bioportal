package org.ncbo.stanford.service.user;

import org.ncbo.stanford.bean.UserBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserService {
	public UserBean getUser(Integer userId);
	public UserBean getUser(String username);
}
