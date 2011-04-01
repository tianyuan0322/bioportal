package org.ncbo.stanford.service.user.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboLRoleDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyAclDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserRoleDAO;
import org.ncbo.stanford.domain.generated.NcboLRole;
import org.ncbo.stanford.domain.generated.NcboOntologyAcl;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserRole;
import org.ncbo.stanford.manager.metadata.UserMetadataManager;
import org.ncbo.stanford.service.encryption.EncryptionService;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.util.helper.StringHelper;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserServiceImpl implements UserService {

	private CustomNcboUserDAO ncboUserDAO = null;
	private CustomNcboLRoleDAO ncboLRoleDAO = null;
	private CustomNcboUserRoleDAO ncboUserRoleDAO = null;
	private CustomNcboOntologyAclDAO ncboOntologyAclDAO = null;
	private EncryptionService encryptionService = null;
	private UserMetadataManager userMetadataManager = null;

	public void setUserMetadataManager(UserMetadataManager userMetadataManager) {
		this.userMetadataManager = userMetadataManager;
	}

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

	@SuppressWarnings("unchecked")
	public UserBean findUserByEmail(String email) {
		UserBean userBean = null;
		List<NcboUser> userList = ncboUserDAO.findByEmail(email);

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

	@SuppressWarnings("unchecked")
	public UserBean findUserByApiKey(String apiKey) {
		UserBean userBean = null;
		List<NcboUser> userList = ncboUserDAO.findByApiKey(apiKey);

		if (userList.size() > 0) {
			NcboUser user = (NcboUser) userList.toArray()[0];

			if (user != null) {
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
		String uuid = UUID.randomUUID().toString();
		ncboUser.setApiKey(uuid);
		userBean.setApiKey(uuid);

		NcboUser newNcboUser = ncboUserDAO.saveUser(ncboUser);
		userBean.setId(newNcboUser.getId());
		userBean.setDateCreated(newNcboUser.getDateCreated());

		if (userBean.getRoles().size() <= 0) {
			userBean.generateDefaultRole();
		}

		for (String role : userBean.getRoles()) {
			NcboUserRole ncboUserRole = new NcboUserRole();
			NcboLRole ncboLRole = ncboLRoleDAO.findRoleByName(role);
			ncboUserRole.setNcboUser(newNcboUser);
			ncboUserRole.setNcboLRole(ncboLRole);
			ncboUserRoleDAO.save(ncboUserRole);
		}

		saveOntologyAcl(userBean, newNcboUser);

		// This code is adding for creating a new user account in the metadata
		// ontology
		try {
			userMetadataManager.saveUser(userBean);
		} catch (Exception e) {
			e.printStackTrace();
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
		saveOntologyAcl(userBean, ncboUser);

		// This code is adding for creating a new user account in the metadata
		// ontology
		try {
			userMetadataManager.saveUser(userBean);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void deleteUser(UserBean userBean) {
		// get ncboUser DAO instance using user_id
		NcboUser ncboUser = ncboUserDAO.findById(userBean.getId());
		Set<NcboUserRole> userRoles = ncboUser.getNcboUserRoles();

		for (NcboUserRole userRole : userRoles) {
			ncboUserRoleDAO.delete(userRole);
		}

		Set<NcboOntologyAcl> acls = ncboUser.getNcboOntologyAcls();

		for (NcboOntologyAcl acl : acls) {
			ncboOntologyAclDAO.delete(acl);
		}

		ncboUserDAO.delete(ncboUser);
	}

	@SuppressWarnings("unchecked")
	private void saveOntologyAcl(UserBean userBean, NcboUser ncboUser) {
		Set<NcboOntologyAcl> acls = ncboUser.getNcboOntologyAcls();

		for (NcboOntologyAcl acl : acls) {
			ncboOntologyAclDAO.delete(acl);
		}

		for (Map.Entry<Integer, Boolean> entry : userBean.getOntologyAcl()
				.entrySet()) {
			NcboOntologyAcl acl = new NcboOntologyAcl();
			acl.setNcboUser(ncboUser);
			acl.setOntologyId(entry.getKey());
			acl.setIsOwner(entry.getValue());
			ncboOntologyAclDAO.save(acl);
		}
	}

	/**
	 * @param ncboUserDAO
	 *            the ncboUserDAO to set
	 */
	public void setNcboUserDAO(CustomNcboUserDAO ncboUserDAO) {
		this.ncboUserDAO = ncboUserDAO;
	}

	/**
	 * @param encryptionService
	 *            the encryptionService to set
	 */
	public void setEncryptionService(EncryptionService encryptionService) {
		this.encryptionService = encryptionService;
	}

	/**
	 * @param ncboLRoleDAO
	 *            the ncboLRoleDAO to set
	 */
	public void setNcboLRoleDAO(CustomNcboLRoleDAO ncboLRoleDAO) {
		this.ncboLRoleDAO = ncboLRoleDAO;
	}

	/**
	 * @param ncboUserRoleDAO
	 *            the ncboUserRoleDAO to set
	 */
	public void setNcboUserRoleDAO(CustomNcboUserRoleDAO ncboUserRoleDAO) {
		this.ncboUserRoleDAO = ncboUserRoleDAO;
	}

	/**
	 * @param ncboOntologyAclDAO
	 *            the ncboOntologyAclDAO to set
	 */
	public void setNcboOntologyAclDAO(
			CustomNcboOntologyAclDAO ncboOntologyAclDAO) {
		this.ncboOntologyAclDAO = ncboOntologyAclDAO;
	}
}
