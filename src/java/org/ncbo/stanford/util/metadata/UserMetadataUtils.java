package org.ncbo.stanford.util.metadata;

import org.ncbo.stanford.bean.UserBean;
import org.springframework.util.StringUtils;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;

import edu.stanford.smi.protegex.owl.model.RDFSLiteral;

public class UserMetadataUtils extends MetadataUtils {

	public static final String PROPERTY_USER_ROLES = PREFIX_METADATA
			+ "hasRoles";
	public static final String PROPERTY_IS_MANUAL = PREFIX_METADATA
			+ "isManual";
	public static final String PROPERTY_UPLOAD_DATE = PREFIX_METADATA
			+ "timestampCreation";
	public static final String PROPERTY_HAS_FIRST_NAME = PREFIX_OMV
			+ "firstName";
	public static final String PROPERTY_HAS_LAST_NAME = PREFIX_OMV
			+ "lastName";
	public static final String PROPERTY_HAS_EMAIL = PREFIX_OMV
			+ "eMail";
	public static final String PROPERTY_HAS_PHONE = PREFIX_OMV
			+ "phoneNumber";
	public static final String PROPERTY_USERNAME = PREFIX_METADATA + "username";

	// This Method is set the UserInstance inside Metadata
	public static void fillInOntologyInstancePropertiesFromBean(
			OWLIndividual userInd, UserBean userBean) throws Exception {
		if (userInd == null || userBean == null) {
			throw new Exception(
					"The method fillInUserInstancePropertiesFromBean can't take null arguments."
							+ "Please make sure that both arguments are properly initialized.");
		}

		OWLModel owlModel = userInd.getOWLModel();

		setPropertyValue(owlModel, userInd, PROPERTY_USERNAME, userBean
				.getUsername());

		setPropertyValue(owlModel, userInd, PROPERTY_HAS_EMAIL,
				userBean.getEmail());
		setPropertyValue(owlModel, userInd, PROPERTY_HAS_FIRST_NAME, userBean.getFirstname());

		setPropertyValue(owlModel, userInd, PROPERTY_HAS_LAST_NAME, userBean.getLastname());

		if (userBean.getPhone() != null && userBean.getPhone().trim().length() > 0)
			setPropertyValue(owlModel, userInd, PROPERTY_HAS_PHONE, userBean.getPhone());
		
		RDFSLiteral litDateCreated = createXsdDateTimePropertyValue(owlModel,
				userBean.getDateCreated());
		setPropertyValue(owlModel, userInd, PROPERTY_UPLOAD_DATE,
				litDateCreated);
		setPropertyValue(owlModel, userInd, PROPERTY_USER_ROLES, userBean
				.getRoles());

	}

}
