package org.ncbo.stanford.manager.metadata.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.GroupBean;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.metadata.OntologyGroupMetadataManager;
import org.ncbo.stanford.util.metadata.OntologyGroupMetadataUtils;
import org.ncbo.stanford.util.metadata.OntologyMetadataUtils;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

/**
 * Provides the functionality to deal with ontology group metadata
 * 
 * @author Csongor Nyulas
 * 
 */
public class OntologyGroupMetadataManagerProtegeImpl extends
		AbstractOntologyManagerProtege implements OntologyGroupMetadataManager {

	private static final Log log = LogFactory
			.getLog(OntologyGroupMetadataManagerProtegeImpl.class);

	private static final String CLASS_ONTOLOGY_GROUP = OntologyGroupMetadataUtils.CLASS_ONTOLOGY_GROUP;

	private static final boolean CREATE_IF_MISSING = true;
	private static final boolean DO_NOT_CREATE_IF_MISSING = false;


	public void saveOntologyGroup(GroupBean gb) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual ontGroupInd = getOntologyGroupInstance(metadata, gb.getId(), CREATE_IF_MISSING);

		OntologyGroupMetadataUtils.fillInGroupInstancePropertiesFromBean(ontGroupInd, gb);
	}

	public GroupBean findGroupById(Integer groupId) {
		OWLModel metadata = getMetadataOWLModel();

		OWLIndividual ontGroupInd = getOntologyGroupInstance(metadata, groupId, DO_NOT_CREATE_IF_MISSING);
		
		GroupBean gb = new GroupBean();
		try {
			OntologyGroupMetadataUtils.fillInGroupBeanFromInstance(gb, ontGroupInd);
			return gb;
		} catch (Exception e) {
			return null;
		}
	}

	public List<GroupBean> findAllGroups() {
		OWLModel metadata = getMetadataOWLModel();
		
		List<Integer> ontologyIds = OntologyMetadataUtils.getAllGroupIDs(metadata);
		List<GroupBean> res = new ArrayList<GroupBean>();
		
		for (Integer id : ontologyIds) {
			res.add(findGroupById(id));
		}
		
		return res;
	}
	
	private OWLIndividual getOntologyGroupInstance(OWLModel metadata, int id, boolean createIfMissing) {
		String ontGroupInstName = getOntologyGroupIndividualName(id);
		OWLIndividual ontGroupInd = metadata.getOWLIndividual(ontGroupInstName);
		
		if (ontGroupInd == null && createIfMissing) {
			ontGroupInd = createOntologyGroupInstance(metadata, ontGroupInstName);
		}
		
		//alternative lookup
		if (ontGroupInd == null) {
			ontGroupInd = OntologyGroupMetadataUtils.getOntologyGroupWithId(metadata, id);
			if (ontGroupInd != null) {
				log.warn("OntologyGroup instance for id: " + id + " has been found having non-standard name: " + ontGroupInd);
			}
		}

		return ontGroupInd;
	}
	
	private OWLIndividual createOntologyGroupInstance(OWLModel metadata, String indName) {
		OWLNamedClass ontClass = metadata.getOWLNamedClass(CLASS_ONTOLOGY_GROUP);

		return ontClass.createOWLIndividual(indName);
	}
	
}
