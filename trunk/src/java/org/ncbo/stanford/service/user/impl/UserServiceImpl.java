package org.ncbo.stanford.service.user.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboLRoleDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserRoleDAO;
import org.ncbo.stanford.domain.generated.NcboLRole;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserRole;
import org.ncbo.stanford.service.encryption.EncryptionService;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.util.helper.StringHelper;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserServiceImpl implements UserService {

	private CustomNcboUserDAO ncboUserDAO = null;
	private CustomNcboLRoleDAO ncboLRoleDAO = null;
	private CustomNcboUserRoleDAO ncboUserRoleDAO = null;
	private EncryptionService encryptionService = null;

	public UserBean findUser(Integer userId) {
		UserBean userBean = null;
		NcboUser user = ncboUserDAO.findById(userId);

		if (user != null) {
			// populate userBean from ncbuUser with matched userId
			userBean = new UserBean();
			userBean.populateFromEntity(user);
		}

		return userBean;
	}

	public UserBean findUser(String username) {
		UserBean userBean = null;
		NcboUser user = ncboUserDAO.getUserByUsername(username);

		if (user != null) {
			// populate userBean from ncbuUser with matched username
			userBean = new UserBean();
			userBean.populateFromEntity(user);
		}

		return userBean;
	}

	public UserBean findUserByEmail(String email) {
		UserBean userBean = null;
		List userList = ncboUserDAO.findByEmail(email);
		if (userList.size() > 0) {
			NcboUser user = (NcboUser) userList.toArray()[0];

			if (user != null) {
				// populate userBean from ncbuUser with matched username
				userBean = new UserBean();
				userBean.populateFromEntity(user);
			}
		}

		return userBean;
	}

	/**
	 * @return List<UserBean>
	 */
	@SuppressWarnings("unchecked")
	public List<UserBean> findUsers() {
		List<NcboUser> ncboUserList = ncboUserDAO.findAll();
		List<UserBean> userBeanList = new ArrayList<UserBean>();

		// populate the query result to UserBean
		for (NcboUser ncboUser : ncboUserList) {
			UserBean userBean = new UserBean();
			userBean.populateFromEntity(ncboUser);
			userBeanList.add(userBean);
		}

		return userBeanList;
	}

	public void createUser(UserBean userBean) {
		// populate NcboUser from userBean
		NcboUser ncboUser = new NcboUser();
		userBean.populateToEntity(ncboUser);

		String encodedPassword = encryptionService.encodePassword(userBean
				.getPassword());
		ncboUser.setPassword(encodedPassword);
		NcboUser newNcboUser = ncboUserDAO.saveUser(ncboUser);
		userBean.setId(newNcboUser.getId());

		if (userBean.getRoles().size() <= 0) {
			userBean.generateDefaultRole();
		}

		for (String roleName : userBean.getRoles()) {
			NcboUserRole ncboUserRole = new NcboUserRole();
			NcboLRole ncboLRole = ncboLRoleDAO.findRoleByName(roleName);
			ncboUserRole.setNcboUser(newNcboUser);
			ncboUserRole.setNcboLRole(ncboLRole);
			ncboUserRoleDAO.save(ncboUserRole);
		}
	}

	public void updateUser(UserBean userBean) {
		// get ncboUser DAO instance using user_id
		NcboUser ncboUser = ncboUserDAO.findById(userBean.getId());

		// populate NcboUser from userBean
		userBean.populateToEntity(ncboUser);
		String password = userBean.getPassword();

		if (!StringHelper.isNullOrNullString(password)) {
			String encodedPassword = encryptionService.encodePassword(password);
			ncboUser.setPassword(encodedPassword);
		}

		ncboUserDAO.save(ncboUser);
	}

	@SuppressWarnings("unchecked")
	public void deleteUser(UserBean userBean) {
		// get ncboUser DAO instance using user_id
		NcboUser ncboUser = ncboUserDAO.findById(userBean.getId());
		Set<NcboUserRole> userRoles = ncboUser.getNcboUserRoles();

		for (NcboUserRole userRole : userRoles) {
			ncboUserRoleDAO.delete(userRole);
		}

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
	 * @return the encryptionService
	 */
	public EncryptionService getEncryptionService() {
		return encryptionService;
	}

	/**
	 * @param encryptionService
	 *            the encryptionService to set
	 */
	public void setEncryptionService(EncryptionService encryptionService) {
		this.encryptionService = encryptionService;
	}

	/**
	 * @return the ncboLRoleDAO
	 */
	public CustomNcboLRoleDAO getNcboLRoleDAO() {
		return ncboLRoleDAO;
	}

	/**
	 * @param ncboLRoleDAO
	 *            the ncboLRoleDAO to set
	 */
	public void setNcboLRoleDAO(CustomNcboLRoleDAO ncboLRoleDAO) {
		this.ncboLRoleDAO = ncboLRoleDAO;
	}

	/**
	 * @return the ncboUserRoleDAO
	 */
	public CustomNcboUserRoleDAO getNcboUserRoleDAO() {
		return ncboUserRoleDAO;
	}

	/**
	 * @param ncboUserRoleDAO
	 *            the ncboUserRoleDAO to set
	 */
	public void setNcboUserRoleDAO(CustomNcboUserRoleDAO ncboUserRoleDAO) {
		this.ncboUserRoleDAO = ncboUserRoleDAO;
	}
}
