package org.ncbo.stanford.manager.metadata.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.util.metadata.OntologyMetadataUtils;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;


/**
 * Provides the functionality to deal with ontology metadata
 * 
 * @author Csongor Nyulas
 * 
 */
public class OntologyMetadataManagerProtegeImpl extends
		AbstractOntologyManagerProtege implements OntologyMetadataManager {

	private static final Log log = LogFactory
			.getLog(OntologyMetadataManagerProtegeImpl.class);

	private static final String CLASS_ONTOLOGY = "OMV:Ontology";
	//private static final String CLASS_ONTOLOGY_VIEW = "OMV:OntologyView";
	private static final String CLASS_VIRTUAL_ONTOLOGY = "metadata:VirtualOntology";
	//private static final String CLASS_VIRTUAL_VIEW = "metadata:VirtualView";
	private static final String CLASS_USER = "metadata:BioPortalUser";
	//private static final String CLASS_ONTOLOGY_DOMAIN = "OMV:OntologyDomain";
	
	public static final String INTERNAL_VERSION_NUMBER = "internalVersionNumber";
	public static final String VERSION_NUMBER = "versionNumber";
	public static final String VERSION_STATUS = "versionStatus";
	public static final String FILE_PATH = "filePath";
	public static final String IS_REMOTE = "isRemote";
	public static final String IS_REVIEWED = "isReviewed";

	public static final String OBO_FOUNDRY_ID = "oboFoundryId";
	public static final String IS_MANUAL = "isManual";

	private static final boolean CREATE_IF_MISSING = true;
	private static final boolean DO_NOT_CREATE_IF_MISSING = false;
	
	public void saveOntology(OntologyBean ob) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual ontVerInd = getOntologyInstance(metadata, ob.getId(), CREATE_IF_MISSING);

		OWLIndividual vOntInd = getVirtualOntologyInstance(metadata, ob.getOntologyId());
		OWLIndividual userInd = getUserInstance(metadata, ob.getUserId());
		Collection<OWLIndividual> domainInd = getOntologyDomainInstances(metadata, ob.getCategoryIds());

		OntologyMetadataUtils.fillInOntologyInstancePropertiesFromBean(ontVerInd, ob, vOntInd, userInd, domainInd);
		OntologyMetadataUtils.setLatestVersion(vOntInd, ontVerInd);
	}

//	private void saveVirtualOntology(OntologyBean ob) throws Exception {
//		TODO continue here
//		OWLModel metadata = getMetadataOWLModel();
//		OWLIndividual ontInd = getVirtualOntologyInstance(metadata, ob.getOntologyId());
//	}
	
	public OntologyBean findOntologyById(Integer ontologyVersionid) {
		OWLModel metadata = getMetadataOWLModel();
//		OWLProperty prop = metadata.getOWLProperty(OntologyMetadataUtils.PROPERTY_OMV_NAME);
//		Collection results = metadata.getFramesWithValue(prop, null, false, ontologyVersionid);
//		for (Object frame : results) {
//			if (frame instanceof OWLIndividual) {
//				OWLIndividual cls = (OWLIndividual) frame;
//				if (cls.hasRDFType(metadata.getRDFSNamedClass(CLASS_ONTOLOGY))) {
//					OntologyBean ob = new OntologyBean();
//					try {
//						OntologyMetadataUtils.fillInOntologyBeanFromInstance(ob, cls);
//						return ob;
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
		
		OWLIndividual ontInd = getOntologyInstance(metadata, ontologyVersionid, DO_NOT_CREATE_IF_MISSING);
		
		OntologyBean ob = new OntologyBean();
		try {
			OntologyMetadataUtils.fillInOntologyBeanFromInstance(ob, ontInd);
			return ob;
		} catch (Exception e) {
			return null;
		}
	}
	

//	public List findOntologyByProperty(String propertyName, Object value) {
//		log.debug("finding NcboOntologyVersion instance with property: "
//				+ propertyName + ", value: " + value);
//		try {
//			String queryString = "from NcboOntologyVersion as model where model."
//					+ propertyName + "= ?";
//			return getHibernateTemplate().find(queryString, value);
//		} catch (RuntimeException re) {
//			log.error("find by property name failed", re);
//			throw re;
//		}
//	}
//
//	public List findOntologyByInternalVersionNumber(Object internalVersionNumber) {
//		return findOntologyByProperty(INTERNAL_VERSION_NUMBER, internalVersionNumber);
//	}
//
//	public List findOntologyByVersionNumber(Object versionNumber) {
//		return findOntologyByProperty(VERSION_NUMBER, versionNumber);
//	}
//
//	public List findOntologyByVersionStatus(Object versionStatus) {
//		return findOntologyByProperty(VERSION_STATUS, versionStatus);
//	}
//
//	public List findOntologyByFilePath(Object filePath) {
//		return findOntologyByProperty(FILE_PATH, filePath);
//	}
//
//	public List findOntologyByIsRemote(Object isRemote) {
//		return findOntologyByProperty(IS_REMOTE, isRemote);
//	}
//
//	public List findOntologyByIsReviewed(Object isReviewed) {
//		return findOntologyByProperty(IS_REVIEWED, isReviewed);
//	}
//
//	public List findAllOntologies() {
//		log.debug("finding all NcboOntologyVersion instances");
//		try {
//			String queryString = "from NcboOntologyVersion";
//			return getHibernateTemplate().find(queryString);
//		} catch (RuntimeException re) {
//			log.error("find all failed", re);
//			throw re;
//		}
//	}
//
//	public NcboOntology findVirtualOntologyById(java.lang.Integer id) {
//		log.debug("getting NcboOntology instance with id: " + id);
//		try {
//			NcboOntology instance = (NcboOntology) getHibernateTemplate().get(
//					"org.ncbo.stanford.domain.generated.NcboOntology", id);
//			return instance;
//		} catch (RuntimeException re) {
//			log.error("get failed", re);
//			throw re;
//		}
//	}
//
//	public List findVirtualOntologyByExample(NcboOntology instance) {
//		log.debug("finding NcboOntology instance by example");
//		try {
//			List results = getHibernateTemplate().findVirtualOntologyByExample(instance);
//			log.debug("find by example successful, result size: "
//					+ results.size());
//			return results;
//		} catch (RuntimeException re) {
//			log.error("find by example failed", re);
//			throw re;
//		}
//	}
//
//	public List findVirtualOntologyByProperty(String propertyName, Object value) {
//		log.debug("finding NcboOntology instance with property: "
//				+ propertyName + ", value: " + value);
//		try {
//			String queryString = "from NcboOntology as model where model."
//					+ propertyName + "= ?";
//			return getHibernateTemplate().find(queryString, value);
//		} catch (RuntimeException re) {
//			log.error("find by property name failed", re);
//			throw re;
//		}
//	}
//
//	public List findVirtualOntologyByOboFoundryId(Object oboFoundryId) {
//		return findVirtualOntologyByProperty(OBO_FOUNDRY_ID, oboFoundryId);
//	}
//
//	public List findVirtualOntologyByIsManual(Object isManual) {
//		return findVirtualOntologyByProperty(IS_MANUAL, isManual);
//	}
//
//	public List findAllVirtualOntologies() {
//		log.debug("finding all NcboOntology instances");
//		try {
//			String queryString = "from NcboOntology";
//			return getHibernateTemplate().find(queryString);
//		} catch (RuntimeException re) {
//			log.error("find all failed", re);
//			throw re;
//		}
//	}

	
	private OWLIndividual getOntologyInstance(OWLModel metadata, int id, boolean createIfMissing) {
		String ontInstName = getOntologyIndividualName(id);
		OWLIndividual ontInd = metadata.getOWLIndividual(ontInstName);
		if (ontInd == null && createIfMissing) {
			ontInd = createOntologyInstance(metadata, ontInstName);
		}
		return ontInd;
	}
	
	private OWLIndividual createOntologyInstance(OWLModel metadata, String indName) {
		OWLNamedClass ontClass = metadata.getOWLNamedClass(CLASS_ONTOLOGY);
		return ontClass.createOWLIndividual(indName);
	}
	
	private OWLIndividual getVirtualOntologyInstance(OWLModel metadata, int id) {
		String ontInstName = getVirtualOntologyIndividualName(id);
		OWLIndividual ontInd = metadata.getOWLIndividual(ontInstName);
		if (ontInd == null) {
			ontInd = createVirtualOntologyInstance(metadata, ontInstName);
			ontInd.setPropertyValue(ontInd.getOWLModel().getOWLProperty(OntologyMetadataUtils.PROPERTY_ID), new Integer(id));
		}
		return ontInd;
	}
	
	private OWLIndividual createVirtualOntologyInstance(OWLModel metadata, String indName) {
		OWLNamedClass ontClass = metadata.getOWLNamedClass(CLASS_VIRTUAL_ONTOLOGY);
		return ontClass.createOWLIndividual(indName);
	}
	
	private OWLIndividual getUserInstance(OWLModel metadata, int id) {
		String ontInstName = getUserIndividualName(id);
		OWLIndividual userInd = metadata.getOWLIndividual(ontInstName);
		if (userInd == null) {
			userInd = createUserInstance(metadata, ontInstName);
			userInd.setPropertyValue(userInd.getOWLModel().getOWLProperty(OntologyMetadataUtils.PROPERTY_ID), new Integer(id));
		}
		return userInd;
	}
	
	private OWLIndividual createUserInstance(OWLModel metadata, String indName) {
		OWLNamedClass userClass = metadata.getOWLNamedClass(CLASS_USER);
		return userClass.createOWLIndividual(indName);
	}


	private Collection<OWLIndividual> getOntologyDomainInstances(OWLModel metadata,
			List<Integer> categoryIds) {
		HashSet<OWLIndividual> res = new HashSet<OWLIndividual>();
		for (Integer categoryId : categoryIds) {
			OWLIndividual ontDomainInd = getOntologyDomainInstance(metadata, categoryId);
			if (ontDomainInd != null) {
				res.add(ontDomainInd);
			}
			else {
				//TODO what to do?
				//throw Exception?
				log.error("No OMV:OntologyDomain individual found for category ID: " + categoryId);
			}
		}
		return res;
	}
	
	private OWLIndividual getOntologyDomainInstance(OWLModel metadata, Integer id) {
		String ontDomainInstName = getOntologyDomainIndividualName(id);
		OWLIndividual ontDomainInd = metadata.getOWLIndividual(ontDomainInstName);
		return ontDomainInd;
	}

}
