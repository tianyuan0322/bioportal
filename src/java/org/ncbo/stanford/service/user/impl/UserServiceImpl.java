package org.ncbo.stanford.service.user.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboLRoleDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyAclDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserOntologyLicenseDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserRoleDAO;
import org.ncbo.stanford.domain.generated.NcboLRole;
import org.ncbo.stanford.domain.generated.NcboOntologyAcl;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserOntologyLicense;
import org.ncbo.stanford.domain.generated.NcboUserRole;
import org.ncbo.stanford.exception.AuthenticationException;
import org.ncbo.stanford.manager.metadata.UserMetadataManager;
import org.ncbo.stanford.service.encryption.EncryptionService;
import org.ncbo.stanford.service.session.RESTfulSession;
import org.ncbo.stanford.service.user.UserService;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.security.authentication.AuthenticationService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserServiceImpl implements UserService {
	private static final Log log = LogFactory.getLog(UserServiceImpl.class);

	private CustomNcboUserDAO ncboUserDAO = null;
	private CustomNcboLRoleDAO ncboLRoleDAO = null;
	private CustomNcboUserRoleDAO ncboUserRoleDAO = null;
	private CustomNcboOntologyAclDAO ncboOntologyAclDAO = null;
	private CustomNcboUserOntologyLicenseDAO ncboUserOntologyLicenseDAO = null;
	private EncryptionService encryptionService = null;
	private AuthenticationService authenticationService = null;

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

	public RESTfulSession createUser(UserBean userBean) {
		RESTfulSession session = null;
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

		saveUserOntologyAcl(userBean, newNcboUser);
		saveOntologyLicenses(userBean, newNcboUser);

		// This code is adding for creating a new user account in the metadata
		// ontology
		try {
			userMetadataManager.saveUser(userBean);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			session = authenticationService.authenticate(uuid);
		} catch (AuthenticationException e) {
			// this should never happen in this case
			log.error("A really bizarre error occurred. "
					+ "The created user failed to authenticate, "
					+ " which can only mean the user is not in the database. "
					+ "See stack trace below for details.");
			e.printStackTrace();
		}

		return session;
	}

	public RESTfulSession updateUser(UserBean userBean) {
		// get ncboUser DAO instance using user_id
		NcboUser ncboUser = ncboUserDAO.findById(userBean.getId());
		String userApiKey = ncboUser.getApiKey();
		RESTfulSession session = null;

		// populate NcboUser from userBean
		userBean.populateToEntity(ncboUser);
		String password = userBean.getPassword();

		if (!StringHelper.isNullOrNullString(password)) {
			String encodedPassword = encryptionService.encodePassword(password);
			ncboUser.setPassword(encodedPassword);
		}

		ncboUserDAO.save(ncboUser);
		saveUserOntologyAcl(userBean, ncboUser);
		saveOntologyLicenses(userBean, ncboUser);

		// This code is adding for creating a new user account in the metadata
		// ontology
		try {
			// Update userBean with info from entity
			userBean.populateFromEntity(ncboUser);

			userMetadataManager.saveUser(userBean);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			session = authenticationService.authenticate(userApiKey);
		} catch (AuthenticationException e) {
			// this should never happen in this case
			log.error("A really bizarre error occurred. "
					+ "The updated user failed to authenticate, "
					+ " which can only mean the user is not in the database. "
					+ "See stack trace below for details.");
			e.printStackTrace();
		}

		return session;
	}

	public void deleteUser(UserBean userBean) {
		// get ncboUser DAO instance using user_id
		NcboUser ncboUser = ncboUserDAO.findById(userBean.getId());
		String userApiKey = ncboUser.getApiKey();

		// delete all user roles
		deleteAllUserRoles(ncboUser);

		// delete the entire ACL for this user
		deleteFullUserOntologyAcl(ncboUser);

		// delete all ontology licenses for this user
		deleteAllUserOntologyLicenses(ncboUser);

		ncboUserDAO.delete(ncboUser);
		authenticationService.logout(userApiKey);
	}

	@SuppressWarnings("unchecked")
	private void saveUserOntologyAcl(UserBean userBean, NcboUser ncboUser) {
		Set<NcboOntologyAcl> existingAcl = ncboUser.getNcboOntologyAcls();
		Map<Integer, NcboOntologyAcl> ontologyIds = new HashMap<Integer, NcboOntologyAcl>(
				0);

		for (NcboOntologyAcl aclEntry : existingAcl) {
			ontologyIds.put(aclEntry.getOntologyId(), aclEntry);
		}

		for (Map.Entry<Integer, Boolean> entry : userBean.getOntologyAcl()
				.entrySet()) {
			Integer ontologyId = entry.getKey();
			NcboOntologyAcl aclEntry = null;

			if (ontologyIds.containsKey(ontologyId)) {
				// ACL entry already exists, need to update
				aclEntry = ontologyIds.get(ontologyId);
			} else {
				// a new ACL entry, need to insert
				aclEntry = new NcboOntologyAcl();
				aclEntry.setOntologyId(ontologyId);
				aclEntry.setNcboUser(ncboUser);
			}

			aclEntry.setIsOwner(entry.getValue());
			ncboOntologyAclDAO.save(aclEntry);
		}
	}

	private void addOntologyToAcl(NcboUser ncboUser, Integer ontologyId,
			Boolean isOwner) {
		if (ncboUser != null && ontologyId != null && ontologyId > 0) {
			NcboOntologyAcl aclEntry = ncboOntologyAclDAO
					.findByUserIdAndOntologyId(ncboUser.getId(), ontologyId);

			if (aclEntry == null) {
				aclEntry = new NcboOntologyAcl();
				aclEntry.setNcboUser(ncboUser);
				aclEntry.setOntologyId(ontologyId);
			}

			aclEntry.setIsOwner(isOwner);
			ncboOntologyAclDAO.save(aclEntry);
		}
	}

	@SuppressWarnings("unchecked")
	private void deleteFullUserOntologyAcl(NcboUser ncboUser) {
		Set<NcboOntologyAcl> acls = ncboUser.getNcboOntologyAcls();

		for (NcboOntologyAcl acl : acls) {
			ncboOntologyAclDAO.delete(acl);
		}
	}

	@SuppressWarnings("unchecked")
	private void saveOntologyLicenses(UserBean userBean, NcboUser ncboUser) {
		Set<NcboUserOntologyLicense> existingLicenses = ncboUser
				.getNcboUserOntologyLicenses();
		Map<Integer, NcboUserOntologyLicense> ontologyIds = new HashMap<Integer, NcboUserOntologyLicense>(
				0);

		for (NcboUserOntologyLicense lic : existingLicenses) {
			ontologyIds.put(lic.getOntologyId(), lic);
		}

		for (Map.Entry<Integer, String> entry : userBean.getOntologyLicenses()
				.entrySet()) {
			Integer ontologyId = entry.getKey();
			NcboUserOntologyLicense lic = null;

			if (ontologyIds.containsKey(ontologyId)) {
				// license already exists, need to update
				lic = ontologyIds.get(ontologyId);
			} else {
				// a new license, need to insert
				lic = new NcboUserOntologyLicense();
				lic.setOntologyId(ontologyId);
				lic.setNcboUser(ncboUser);
			}

			lic.setLicenseText(entry.getValue());
			ncboUserOntologyLicenseDAO.save(lic);

			// add ACL records for this combo of user/ontology
			addOntologyToAcl(ncboUser, ontologyId, false);
		}
	}

	@SuppressWarnings("unchecked")
	private void deleteAllUserOntologyLicenses(NcboUser ncboUser) {
		Set<NcboUserOntologyLicense> licenses = ncboUser
				.getNcboUserOntologyLicenses();

		for (NcboUserOntologyLicense license : licenses) {
			ncboUserOntologyLicenseDAO.delete(license);
		}
	}

	@SuppressWarnings("unchecked")
	private void deleteAllUserRoles(NcboUser ncboUser) {
		Set<NcboUserRole> userRoles = ncboUser.getNcboUserRoles();

		for (NcboUserRole userRole : userRoles) {
			ncboUserRoleDAO.delete(userRole);
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
	 * @param authenticationService
	 *            the authenticationService to set
	 */
	public void setAuthenticationService(
			AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
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

	/**
	 * @param ncboUserOntologyLicenseDAO
	 *            the ncboUserOntologyLicenseDAO to set
	 */
	public void setNcboUserOntologyLicenseDAO(
			CustomNcboUserOntologyLicenseDAO ncboUserOntologyLicenseDAO) {
		this.ncboUserOntologyLicenseDAO = ncboUserOntologyLicenseDAO;
	}
}
