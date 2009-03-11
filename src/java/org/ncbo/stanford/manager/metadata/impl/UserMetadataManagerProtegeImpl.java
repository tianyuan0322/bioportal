package org.ncbo.stanford.manager.metadata.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.UserBean;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.metadata.UserMetadataManager;

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

	private static final String CLASS_BIOPORTAL_USER = "BioPortalUser";


	public void saveUser(UserBean userBean) throws Exception {
		
	}
	
	private OWLIndividual getUserInstance(OWLModel metadata, int id) {
		String userInstName = getUserIndividualName(id);
		OWLIndividual ontInd = metadata.getOWLIndividual(userInstName);
		if (ontInd == null) {
			ontInd = createUserInstance(metadata, userInstName);
		}
		return ontInd;
	}
	
	private OWLIndividual createUserInstance(OWLModel metadata, String indName) {
		OWLNamedClass ontClass = metadata.getOWLNamedClass(CLASS_BIOPORTAL_USER);
		return ontClass.createOWLIndividual(indName);
	}
	
}
