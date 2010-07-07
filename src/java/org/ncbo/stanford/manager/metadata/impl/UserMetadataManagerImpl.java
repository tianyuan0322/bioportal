package org.ncbo.stanford.manager.metadata.impl;

import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.manager.metadata.UserMetadataManager;

import org.ncbo.stanford.manager.AbstractOntologyMetadataManager;

import org.ncbo.stanford.util.metadata.UserMetadataUtils;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class UserMetadataManagerImpl extends AbstractOntologyMetadataManager
		implements UserMetadataManager {

	@SuppressWarnings("unchecked")
	public void saveUser(UserBean userBean) throws Exception {
		// This will creates the metadata for user
		OWLModel metadata = getMetadataOWLModel();
		// This will bind the metadata and user according to their id
		OWLIndividual ontDomainInd = getUserInstance(metadata, userBean.getId());
		// This will set the user account inside metadata
		UserMetadataUtils.fillInOntologyInstancePropertiesFromBean(
				ontDomainInd, userBean);
	}

	/**
	 * This method is not used anywhere in this application just i am overriding
	 * this method inside this class
	 */
	public UserBean findUserById(Integer userId) {
		return null;
	}

	@Deprecated
	public void saveUserRole(Integer roleId, String name, String description)
			throws Exception {

	}

	@Deprecated
	public String findUserRoleNameById(Integer roleId) {
		return null;
	}
}