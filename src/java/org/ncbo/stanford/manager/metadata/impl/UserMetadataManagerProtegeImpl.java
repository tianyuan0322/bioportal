package org.ncbo.stanford.manager.metadata.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.metadata.UserMetadataManager;
import org.ncbo.stanford.util.metadata.MetadataUtils;
import org.ncbo.stanford.util.metadata.UserMetadataUtils;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

/**
 * Provides the functionality to deal with user metadata
 * 
 * @author Csongor Nyulas
 * 
 */
public class UserMetadataManagerProtegeImpl extends
		AbstractOntologyManagerProtege implements UserMetadataManager {

	private static final Log log = LogFactory
			.getLog(UserMetadataManagerProtegeImpl.class);

	private static final String CLASS_BIOPORTAL_USER = MetadataUtils.PREFIX_METADATA + "BioPortalUser";
	private static final String CLASS_BIOPORTAL_USER_ROLE = MetadataUtils.PREFIX_METADATA + "BioPortalUserRole";

	private static final boolean CREATE_IF_MISSING = true;
	private static final boolean DO_NOT_CREATE_IF_MISSING = false;
	

	public void saveUser(UserBean ub) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual userInd = getUserInstance(metadata, ub.getId(), CREATE_IF_MISSING);

		//useful only if roles are identified by id (List<Integer> instead of List<String>)
		//TODO correct this depending of which solution we choose to implement
		//Collection<OWLIndividual> rolesInd = getUserRoleInstancesByNames(metadata, ub.getRoles());
		Collection<OWLIndividual> rolesInd = null;

		UserMetadataUtils.fillInUserInstancePropertiesFromBean(userInd, ub, rolesInd);
	}

	public UserBean findUserById(Integer userId) {
		OWLModel metadata = getMetadataOWLModel();

		OWLIndividual userInd = getUserInstance(metadata, userId, DO_NOT_CREATE_IF_MISSING);
		
		UserBean ub = new UserBean();
		try {
			UserMetadataUtils.fillInUserBeanFromInstance(ub, userInd);
			return ub;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Deprecated
	public void saveUserRole(Integer id, String name, String description) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual userRoleInd = getUserRoleInstance(metadata, id, CREATE_IF_MISSING);
		
		UserMetadataUtils.fillInUserRoleInstancePropertiesWithValues(userRoleInd, id, name, description);
	}

	@Deprecated
	public String findUserRoleNameById(Integer roleId) {
		OWLModel metadata = getMetadataOWLModel();
		
		OWLIndividual userRoleInd = getUserRoleInstance(metadata, roleId, DO_NOT_CREATE_IF_MISSING);
		
		try {
			return UserMetadataUtils.getUserRoleName(userRoleInd);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	private OWLIndividual getUserInstance(OWLModel metadata, int id, boolean createIfMissing) {
		String userInstName = getUserIndividualName(id);
		OWLIndividual userInd = metadata.getOWLIndividual(userInstName);
		if (userInd == null && createIfMissing) {
			userInd = createUserInstance(metadata, userInstName);
		}
		return userInd;
	}
	
	private OWLIndividual createUserInstance(OWLModel metadata, String indName) {
		OWLNamedClass userClass = metadata.getOWLNamedClass(CLASS_BIOPORTAL_USER);
		return userClass.createOWLIndividual(indName);
	}
	

	private Collection<OWLIndividual> getUserRoleInstances(OWLModel metadata,
			List<Integer> roleIds) {
		HashSet<OWLIndividual> res = new HashSet<OWLIndividual>();
		for (Integer roleId : roleIds) {
			OWLIndividual userRoleInd = getUserRoleInstance(metadata, roleId);
			if (userRoleInd != null) {
				res.add(userRoleInd);
			}
			else {
				//TODO what to do?
				//throw Exception?
				log.error("No metadata:BioPortalUserRole individual found for user role ID: " + roleId);
			}
		}
		return res;
	}
	
	private OWLIndividual getUserRoleInstance(OWLModel metadata, int id, boolean createIfMissing) {
		String userRoleInstName = getUserRoleIndividualName(id);
		OWLIndividual userRoleInd = metadata.getOWLIndividual(userRoleInstName);
		if (userRoleInd == null && createIfMissing) {
			userRoleInd = createUserRoleInstance(metadata, userRoleInstName);
		}
		return userRoleInd;
	}
	
	private OWLIndividual getUserRoleInstance(OWLModel metadata, Integer id) {
		String userRoleInstName = getUserRoleIndividualName(id);
		OWLIndividual userRoleInd = metadata.getOWLIndividual(userRoleInstName);
		return userRoleInd;
	}
	
	private OWLIndividual createUserRoleInstance(OWLModel metadata, String indName) {
		OWLNamedClass userRoleClass = metadata.getOWLNamedClass(CLASS_BIOPORTAL_USER_ROLE);
		return userRoleClass.createOWLIndividual(indName);
	}

}
