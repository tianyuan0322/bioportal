package org.ncbo.stanford.manager.metadata.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.bean.OntologyViewBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.metadata.OntologyViewMetadataManager;
import org.ncbo.stanford.util.metadata.MetadataUtils;
import org.ncbo.stanford.util.metadata.OntologyMetadataUtils;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.DatatypeValue;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLQueryEngine;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLResult;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.SQWRLException;


/**
 * Provides the functionality to deal with ontology metadata
 * 
 * @author Csongor Nyulas
 * 
 */
public class OntologyViewMetadataManagerProtegeImpl extends
		AbstractOntologyManagerProtege implements OntologyViewMetadataManager {

	private static final Log log = LogFactory
			.getLog(OntologyViewMetadataManagerProtegeImpl.class);

	//private static final String CLASS_ONTOLOGY = OntologyMetadataUtils.CLASS_OMV_ONTOLOGY;
	private static final String CLASS_ONTOLOGY_VIEW = OntologyMetadataUtils.CLASS_ONTOLOGY_VIEW;
	//private static final String CLASS_VIRTUAL_ONTOLOGY = OntologyMetadataUtils.CLASS_VIRTUAL_ONTOLOGY;
	private static final String CLASS_VIRTUAL_VIEW = OntologyMetadataUtils.CLASS_VIRTUAL_VIEW;
	private static final String CLASS_USER = OntologyMetadataUtils.CLASS_BIOPORTAL_USER;
	//private static final String CLASS_ONTOLOGY_DOMAIN = OntologyMetadataUtils.CLASS_OMV_ONTOLOGY_DOMAIN;
	
	private static final String QUERY_MAX_ONTOLOGY_VIEW_ID = "Query-Max-OntologyView-ID";
	private static final String QUERY_MAX_VIRTUAL_VIEW_ID = "Query-Max-VirtualView-ID";
	
	private static final boolean CREATE_IF_MISSING = true;
	private static final boolean DO_NOT_CREATE_IF_MISSING = false;
	
	private static final boolean ONLY_ACTIVE_VERSIONS = true;
	private static final boolean ALL_VERSIONS = (! ONLY_ACTIVE_VERSIONS);
	
	public void saveOntologyView(OntologyViewBean ob) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual ontVerInd = getOntologyViewInstance(metadata, ob.getId(), CREATE_IF_MISSING);

		saveOrUpdate(metadata, ontVerInd, ob);
	}

	public void updateOntologyView(OntologyViewBean ob) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual ontVerInd = getOntologyViewInstance(metadata, ob.getId(), DO_NOT_CREATE_IF_MISSING);

		if (ontVerInd == null) {
			throw new MetadataException(
				"Metadata for ontology view " + ob.getId() + " could not be updated because it could not be found!");
		}
		
		saveOrUpdate(metadata, ontVerInd, ob);
	}
	
	/**
	 * Method containing the common behavior for save and update ontology bean
	 *  
	 * @param metadata the Metadata OWL ontology
	 * @param ontVerInd 
	 * @param ob
	 * @throws MetadataException
	 */
	private void saveOrUpdate(OWLModel metadata, OWLIndividual ontVerInd, OntologyViewBean ob) throws Exception {
		OWLIndividual vViewInd = getVirtualViewInstance(metadata, ob.getOntologyId());
		OWLIndividual userInd = getUserInstance(metadata, ob.getUserId());
		Collection<OWLIndividual> domainInd = getOntologyDomainInstances(metadata, ob.getCategoryIds());
		Collection<OWLIndividual> viewInd = getOntologyViewInstances(metadata, ob.getHasViews());
		Collection<OWLIndividual> srcOntInd = getOntologyInstances(metadata, ob.getViewOnOntologyVersionId());

		OntologyMetadataUtils.ensureOntologyViewBeanDoesNotInvalidateOntologyViewInstance(ontVerInd, ob, vViewInd);
		
		OntologyMetadataUtils.fillInOntologyViewInstancePropertiesFromBean(ontVerInd, ob, vViewInd, userInd, domainInd, viewInd, srcOntInd);
//		OntologyMetadataUtils.setLatestVersion(vOntInd, ontVerInd); //use this if we will reintroduce the "metadata:currentVersion" property
	}

	public void updateOntologyViewMetrics(OntologyViewBean ob, OntologyMetricsBean mb) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual ontVerInd = getOntologyViewInstance(metadata, ob.getId(), DO_NOT_CREATE_IF_MISSING);

		if (ontVerInd == null) {
			throw new MetadataException(
				"Metadata for ontology view " + ob.getId() + " could not be updated because it could not be found!");
		}
		
		if (ob.getId() != mb.getId()) {
			throw new MetadataException(
					"Trying to attach ontology metrics information from OntologyMetricsBean with id " + mb.getId() + 
					"to ontology view version with id " + ob.getId() + ". Invalid operation!");
		}
		
		OntologyMetadataUtils.fillInOntologyInstancePropertiesFromBean(ontVerInd, mb);
	}
	
	public void deleteOntologyView(OntologyViewBean ob) throws MetadataException {
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual ontVerInd = getOntologyViewInstance(metadata, ob.getId(), DO_NOT_CREATE_IF_MISSING);
		
		if (ontVerInd == null) {
			throw new MetadataException(
				"Metadata for ontology view " + ob.getId() + " could not be deleted because it could not be found!");
		}

		ontVerInd.delete();

		// delete the virtual record if all version records have been deleted
//		if (findAllOntologyViewVersionsById(ob.getOntologyId()).isEmpty()) {
//			OWLIndividual vViewInd = getVirtualViewInstance(metadata, ob.getOntologyId());
//			vViewInd.delete();
//		}
	}

	public OntologyViewBean findOntologyViewById(Integer ontologyViewVersionId) {
		OWLModel metadata = getMetadataOWLModel();
//		OWLProperty prop = metadata.getOWLProperty(OntologyMetadataUtils.PROPERTY_OMV_NAME);
//		Collection results = metadata.getFramesWithValue(prop, null, false, ontologyViewVersionId);
//		for (Object frame : results) {
//			if (frame instanceof OWLIndividual) {
//				OWLIndividual cls = (OWLIndividual) frame;
//				if (cls.hasRDFType(metadata.getRDFSNamedClass(CLASS_ONTOLOGY_VIEW))) {
//					OntologyViewBean ob = new OntologyViewBean();
//					try {
//						OntologyMetadataUtils.fillInOntologyViewBeanFromInstance(ob, cls);
//						return ob;
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
		
		OWLIndividual ontInd = getOntologyViewInstance(metadata, ontologyViewVersionId, DO_NOT_CREATE_IF_MISSING);
		
		OntologyViewBean ob = new OntologyViewBean();
		try {
			OntologyMetadataUtils.fillInOntologyViewBeanFromInstance(ob, ontInd);
			return ob;
		} catch (Exception e) {
			return null;
		}
	}
	
	public OntologyViewBean findLatestOntologyViewVersionById(Integer viewId) {
		//WARNING: Any modification to this method should be replicated 
		//         in the findLatestActiveOntologyViewVersionById() method
		OWLModel metadata = getMetadataOWLModel();
		
		OWLIndividual vOntInd = getVirtualViewInstance(metadata, viewId);
		OntologyViewBean ob = new OntologyViewBean();
		try {
			OWLIndividual ontInd = OntologyMetadataUtils.getLatestVersion(vOntInd, ALL_VERSIONS);
			
			OntologyMetadataUtils.fillInOntologyViewBeanFromInstance(ob, ontInd);
			return ob;
		} catch (Exception e) {
			return null;
		}
	}
	
	public OntologyViewBean findLatestActiveOntologyViewVersionById(Integer viewId) {
		//WARNING: Any modification to this method should be replicated 
		//         in the findLatestOntologyViewVersionById() method
		OWLModel metadata = getMetadataOWLModel();
		
		OWLIndividual vOntInd = getVirtualViewInstance(metadata, viewId);
		OntologyViewBean ob = new OntologyViewBean();
		try {
			OWLIndividual ontInd = OntologyMetadataUtils.getLatestVersion(vOntInd, ONLY_ACTIVE_VERSIONS);
			
			OntologyMetadataUtils.fillInOntologyViewBeanFromInstance(ob, ontInd);
			return ob;
		} catch (Exception e) {
			return null;
		}
	}

	public List<OntologyViewBean> findAllOntologyViewVersionsById(Integer viewId) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		
		OWLIndividual vOntInd = getVirtualViewInstance(metadata, viewId);
		List<Integer> ontologyIds = OntologyMetadataUtils.getAllOntologyVersionIDs(metadata, vOntInd);
		List<OntologyViewBean> res = new ArrayList<OntologyViewBean>();
		for (Integer id : ontologyIds) {
			res.add(findOntologyViewById(id));
		}
		return res;
	}	

	public List<OntologyViewBean> findLatestOntologyViewVersions() {
		//WARNING: Any modification to this method should be replicated 
		//         in the findLatestActiveOntologyViewVersions() method
		OWLModel metadata = getMetadataOWLModel();
		
		List<Integer> ontologyIds = OntologyMetadataUtils.getAllVirtualViewIDs(metadata);
		
		List<OntologyViewBean> res = new ArrayList<OntologyViewBean>();
		for (Integer ontologyId : ontologyIds) {
			res.add(findLatestOntologyViewVersionById(ontologyId));
		}
		return res;
	}
	
	public List<OntologyViewBean> findLatestActiveOntologyViewVersions() {
		//WARNING: Any modification to this method should be replicated 
		//         in the findLatestOntologyViewVersions() method
		OWLModel metadata = getMetadataOWLModel();
		
		List<Integer> ontologyIds = OntologyMetadataUtils.getAllVirtualViewIDs(metadata);
		
		List<OntologyViewBean> res = new ArrayList<OntologyViewBean>();
		for (Integer ontologyId : ontologyIds) {
			res.add(findLatestActiveOntologyViewVersionById(ontologyId));
		}
		return res;
	}

	
	public int getNextAvailableVirtualViewId() {
		//return getNextAvailableIdWithSQWRL(QUERY_MAX_VIRTUAL_VIEW_ID, CLASS_VIRTUAL_ONTOLOGY);
		return OntologyMetadataUtils.getNextAvailableVirtualViewId(getMetadataOWLModel());
	}

	
	public int getNextAvailableOntologyViewVersionId() {
		//return getNextAvailableIdWithSQWRL(QUERY_MAX_ONTOLOGY_VIEW_ID, CLASS_ONTOLOGY);
		return OntologyMetadataUtils.getNextAvailableOntologyViewVersionId(getMetadataOWLModel());
	}
	
	private int getNextAvailableIdWithSQWRL(String queryName, String className) {
		//TODO check this
		try {
			SQWRLQueryEngine queryEngine = getMetadataSQWRLEngine();
			SWRLFactory swrlFactory = getMetadataSWRLFactory();
			if ( swrlFactory.hasImp(queryName) ) {
				SWRLImp imp = swrlFactory.getImp(queryName);
				imp.deleteImp();
			}
			swrlFactory.createImp(queryName, className + "(?o) " + SWRLParser.AND_CHAR + " " + MetadataUtils.PROPERTY_ID + "(?o, ?id) " + SWRLParser.IMP_CHAR + " sqwrl:max(?id)");
			queryEngine.runSQWRLQueries();
			SQWRLResult result = queryEngine.getSQWRLResult(queryName);
			if (result == null) {
				return 0;
			}
			DatatypeValue value = result.getDatatypeValue(0);
			return value.getInt() + 1;
			
		} catch (SQWRLException e) {
			e.printStackTrace();
			return -1;
		} catch (SWRLParseException e) {
			e.printStackTrace();
			return -2;
		} catch (SWRLFactoryException e) {
			e.printStackTrace();
			return -3;
		}
	}

	public List<OntologyViewBean> searchOntologyViewMetadata(String query) {
		OWLModel metadata = getMetadataOWLModel();
		List<OWLIndividual> matchingIndividuals = OntologyMetadataUtils.searchOntologyViewMetadata(metadata, query);
		List<OntologyViewBean> res = new ArrayList<OntologyViewBean>();
		for (OWLIndividual ontInd : matchingIndividuals) {
			OntologyViewBean ob = new OntologyViewBean();
			try {
				OntologyMetadataUtils.fillInOntologyViewBeanFromInstance(ob, ontInd);
				res.add(ob);
			} catch (Exception e) {
				return null;
			}
		}
		return res;
	}
	
	//------------- private helper methods ---------------//
	
	private OWLIndividual getOntologyViewInstance(OWLModel metadata, int id, boolean createIfMissing) {
		String ontInstName = getOntologyViewIndividualName(id);
		OWLIndividual ontInd = metadata.getOWLIndividual(ontInstName);
		if (ontInd == null && createIfMissing) {
			ontInd = createOntologyViewInstance(metadata, ontInstName);
		}
		//alternative lookup
		if (ontInd == null) {
			ontInd = OntologyMetadataUtils.getOntologyViewWithId(metadata, id);
			if (ontInd != null) {
				log.warn("Ontology view instance for id: " + id + " has been found having non-standard name: " + ontInd);
			}
		}
		return ontInd;
	}
	
	private OWLIndividual createOntologyViewInstance(OWLModel metadata, String indName) {
		OWLNamedClass ontClass = metadata.getOWLNamedClass(CLASS_ONTOLOGY_VIEW);
		return ontClass.createOWLIndividual(indName);
	}
	
	private OWLIndividual getVirtualViewInstance(OWLModel metadata, int id) {
		String ontInstName = getVirtualViewIndividualName(id);
		OWLIndividual ontInd = metadata.getOWLIndividual(ontInstName);
		//alternative lookup
		if (ontInd == null) {
			ontInd = OntologyMetadataUtils.getVirtualViewWithId(metadata, id);
			if (ontInd != null) {
				log.warn("Virtual view instance for id: " + id + " has been found having non-standard name: " + ontInd);
			}
		}
		//create if could not be found
		if (ontInd == null) {
			ontInd = createVirtualOntologyInstance(metadata, ontInstName);
			ontInd.setPropertyValue(ontInd.getOWLModel().getOWLProperty(OntologyMetadataUtils.PROPERTY_ID), new Integer(id));
		}
		return ontInd;
	}
	
	private OWLIndividual createVirtualOntologyInstance(OWLModel metadata, String indName) {
		OWLNamedClass ontClass = metadata.getOWLNamedClass(CLASS_VIRTUAL_VIEW);
		return ontClass.createOWLIndividual(indName);
	}
	
	private OWLIndividual getUserInstance(OWLModel metadata, Integer userId) {
		OWLIndividual userInd = null;
		
		if (userId != null) {
			String ontInstName = getUserIndividualName(userId);
			userInd = metadata.getOWLIndividual(ontInstName);
			
			if (userInd == null) {
				userInd = createUserInstance(metadata, ontInstName);
				userInd.setPropertyValue(userInd.getOWLModel().getOWLProperty(OntologyMetadataUtils.PROPERTY_ID), new Integer(userId));
			}
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

	
	private Collection<OWLIndividual> getOntologyInstances(OWLModel metadata,
			List<Integer> ontVerIds) {
		HashSet<OWLIndividual> res = new HashSet<OWLIndividual>();
		for (Integer ontVerId : ontVerIds) {
			OWLIndividual ontInd = getOntologyInstance(metadata, ontVerId);
			if (ontInd != null) {
				res.add(ontInd);
			}
			else {
				ontInd = getOntologyViewInstance(metadata, ontVerId, DO_NOT_CREATE_IF_MISSING);
				if (ontInd != null) {
					res.add(ontInd);
				}
				else {
					//TODO what to do?
					//throw Exception?
					log.error("No OMV:Ontology individual found for ontology version ID: " + ontVerId);
				}
			}
		}
		return res;
	}
	
	
	private OWLIndividual getOntologyInstance(OWLModel metadata, int id) {
		String ontInstName = getOntologyIndividualName(id);
		OWLIndividual ontInd = metadata.getOWLIndividual(ontInstName);
		return ontInd;
	}

	
	private Collection<OWLIndividual> getOntologyViewInstances(OWLModel metadata,
			List<Integer> ontViewVerIds) {
		HashSet<OWLIndividual> res = new HashSet<OWLIndividual>();
		for (Integer ontViewVerId : ontViewVerIds) {
			OWLIndividual ontViewInd = getOntologyViewInstance(metadata, ontViewVerId, DO_NOT_CREATE_IF_MISSING);
			if (ontViewInd != null) {
				res.add(ontViewInd);
			}
			else {
				//TODO what to do?
				//throw Exception?
				log.error("No metadata:OntologyView individual found for view ID: " + ontViewVerId);
			}
		}
		return res;
	}
	
}
