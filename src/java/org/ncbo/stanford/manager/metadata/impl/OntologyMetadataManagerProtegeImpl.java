package org.ncbo.stanford.manager.metadata.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyViewBean;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
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
public class OntologyMetadataManagerProtegeImpl extends
		AbstractOntologyManagerProtege implements OntologyMetadataManager {

	private static final Log log = LogFactory
			.getLog(OntologyMetadataManagerProtegeImpl.class);

	private static final String CLASS_ONTOLOGY = OntologyMetadataUtils.CLASS_OMV_ONTOLOGY;
	private static final String CLASS_ONTOLOGY_VIEW = OntologyMetadataUtils.CLASS_ONTOLOGY_VIEW;
	private static final String CLASS_VIRTUAL_ONTOLOGY = OntologyMetadataUtils.CLASS_VIRTUAL_ONTOLOGY;
	//private static final String CLASS_VIRTUAL_VIEW = OntologyMetadataUtils.CLASS_VIRTUAL_VIEW;
	private static final String CLASS_USER = OntologyMetadataUtils.CLASS_BIOPORTAL_USER;
	//private static final String CLASS_ONTOLOGY_DOMAIN = OntologyMetadataUtils.CLASS_OMV_ONTOLOGY_DOMAIN;
	
	private static final String QUERY_MAX_ONTOLOGY_ID = "Query-Max-Ontology-ID";
	private static final String QUERY_MAX_VIRTUAL_ONTOLOGY_ID = "Query-Max-VirtualOntology-ID";
	
	private static final boolean CREATE_IF_MISSING = true;
	private static final boolean DO_NOT_CREATE_IF_MISSING = false;
	
	private static final boolean ONLY_ACTIVE_VERSIONS = true;
	private static final boolean ALL_VERSIONS = (! ONLY_ACTIVE_VERSIONS);
	
	public void saveOntology(OntologyBean ob) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual ontVerInd = getOntologyInstance(metadata, ob.getId(), CREATE_IF_MISSING);

		saveOrUpdate(metadata, ontVerInd, ob);
	}

	public void updateOntology(OntologyBean ob) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual ontVerInd = getOntologyInstance(metadata, ob.getId(), DO_NOT_CREATE_IF_MISSING);
		
		if (ontVerInd == null) {
			throw new MetadataException(
				"Metadata for ontology " + ob.getId() + " could not be updated because it could not be found!");
		}
		
		saveOrUpdate(metadata, ontVerInd, ob);
	}
	
	/**
	 * Method containing the common behavior for save and update ontology bean
	 *  
	 * @param metadata the Metadata OWL ontology
	 * @param ontVerInd 
	 * @param ob
	 * @throws Exception
	 */
	private void saveOrUpdate(OWLModel metadata, OWLIndividual ontVerInd, OntologyBean ob) throws Exception {
		OWLIndividual vOntInd = getVirtualOntologyInstance(metadata, ob.getOntologyId());
		OWLIndividual userInd = getUserInstance(metadata, ob.getUserId());
		Collection<OWLIndividual> domainInd = getOntologyDomainInstances(metadata, ob.getCategoryIds());
		Collection<OWLIndividual> viewInd = getOntologyViewInstances(metadata, ob.getHasViews());

		OntologyMetadataUtils.ensureOntologyBeanDoesNotInvalidateOntologyInstance(ontVerInd, ob, vOntInd);
		
		OntologyMetadataUtils.fillInOntologyInstancePropertiesFromBean(ontVerInd, ob, vOntInd, userInd, domainInd, viewInd);
//		OntologyMetadataUtils.setLatestVersion(vOntInd, ontVerInd); //use this if we will reintroduce the "metadata:currentVersion" property
	}
	
	public void updateOntologyMetrics(OntologyBean ob, OntologyMetricsBean mb) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual ontVerInd = getOntologyInstance(metadata, ob.getId(), DO_NOT_CREATE_IF_MISSING);
		
		if (ontVerInd == null) {
			throw new MetadataException(
					"Metadata for ontology " + ob.getId() + " could not be updated with metrics because it could not be found!");
		}
		
		if (ob.getId() != mb.getId()) {
			throw new MetadataException(
					"Trying to attach ontology metrics information from OntologyMetricsBean with id " + mb.getId() + 
					" to ontology version with id " + ob.getId() + ". Invalid operation!");
		}
		
		OntologyMetadataUtils.fillInOntologyInstancePropertiesFromBean(ontVerInd, mb);
	}
	
	public void deleteOntology(OntologyBean ob) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual ontVerInd = getOntologyInstance(metadata, ob.getId(), DO_NOT_CREATE_IF_MISSING);
		
		if (ontVerInd == null) {
			throw new MetadataException(
				"Metadata for ontology " + ob.getId() + " could not be deleted because it could not be found!");
		}

		ontVerInd.delete();

		// delete the virtual record if all version records have been deleted
//		if (findAllOntologyVersionsById(ob.getOntologyId()).isEmpty()) {
//			OWLIndividual vOntInd = getVirtualOntologyInstance(metadata, ob.getOntologyId());
//			vOntInd.delete();
//		}
	}

	public OntologyBean findOntologyById(Integer ontologyVersionId) {
		OWLModel metadata = getMetadataOWLModel();
//		OWLProperty prop = metadata.getOWLProperty(OntologyMetadataUtils.PROPERTY_OMV_NAME);
//		Collection results = metadata.getFramesWithValue(prop, null, false, ontologyVersionId);
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
		
		OWLIndividual ontInd = getOntologyInstance(metadata, ontologyVersionId, DO_NOT_CREATE_IF_MISSING);
		
		OntologyBean ob = new OntologyBean();
		try {
			OntologyMetadataUtils.fillInOntologyBeanFromInstance(ob, ontInd);
			return ob;
		} catch (Exception e) {
			return null;
		}
	}

	public OntologyBean findOntologyOrOntologyViewById(Integer ontologyOrViewVersionId) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
//		should we have a SQWRL solution for this too?
		
		OWLIndividual ontInd = getOntologyOrViewInstance(metadata, ontologyOrViewVersionId);
		
		if (ontInd == null) {
			return null;
		}
		OWLNamedClass ontClass = metadata.getOWLNamedClass(CLASS_ONTOLOGY);
		
		if (ontInd.hasRDFType(ontClass)) {
			OntologyBean ob = new OntologyBean();
			OntologyMetadataUtils.fillInOntologyBeanFromInstance(ob, ontInd);
			
			return ob;
		} else {
			OntologyViewBean ob = new OntologyViewBean();
			OntologyMetadataUtils.fillInOntologyViewBeanFromInstance(ob, ontInd);
			
			return ob;
		}
	}
	
	public OntologyBean findLatestOntologyVersionById(Integer ontologyId) {
		//WARNING: Any modification to this method should be replicated 
		//         in the findLatestActiveOntologyVersionById() method
		OWLModel metadata = getMetadataOWLModel();
		
		OWLIndividual vOntInd = getVirtualOntologyInstance(metadata, ontologyId);
		OntologyBean ob = new OntologyBean();
		try {
			OWLIndividual ontInd = OntologyMetadataUtils.getLatestVersion(vOntInd, ALL_VERSIONS);
			
			OntologyMetadataUtils.fillInOntologyBeanFromInstance(ob, ontInd);
			return ob;
		} catch (Exception e) {
			return null;
		}
	}
	
	public OntologyBean findLatestActiveOntologyVersionById(Integer ontologyId) {
		//WARNING: Any modification to this method should be replicated 
		//         in the findLatestOntologyVersionById() method
		OWLModel metadata = getMetadataOWLModel();
		
		OWLIndividual vOntInd = getVirtualOntologyInstance(metadata, ontologyId);
		OntologyBean ob = new OntologyBean();
		
		try {
			OWLIndividual ontInd = OntologyMetadataUtils.getLatestVersion(vOntInd, ONLY_ACTIVE_VERSIONS);			
			OntologyMetadataUtils.fillInOntologyBeanFromInstance(ob, ontInd);
			
			return ob;
		} catch (Exception e) {
			return null;
		}
	}

	public OntologyBean findLatestActiveOntologyOrOntologyViewVersionById(Integer ontologyOrViewId) throws Exception {
		//WARNING: Any modification to this method should be replicated 
		//         in the findLatestOntologyOrOntologyViewVersionById() method
		OWLModel metadata = getMetadataOWLModel();

		
		OWLIndividual vOntInd = getVirtualOntologyOrViewInstance(metadata, ontologyOrViewId);
		
		if (vOntInd == null) {
			return null;
		}
		OWLNamedClass vOntClass = metadata.getOWLNamedClass(CLASS_VIRTUAL_ONTOLOGY);
		
		if (vOntInd.hasRDFType(vOntClass)) {
			OntologyBean ob = new OntologyBean();
			OWLIndividual ontInd = OntologyMetadataUtils.getLatestVersion(vOntInd, ONLY_ACTIVE_VERSIONS);	
			try {
				OntologyMetadataUtils.fillInOntologyBeanFromInstance(ob, ontInd);
			}
			catch (MetadataException e) {
				return null;
			}
			
			return ob;
		}
		else {
			OntologyViewBean ob = new OntologyViewBean();
			OWLIndividual ontInd = OntologyMetadataUtils.getLatestVersion(vOntInd, ONLY_ACTIVE_VERSIONS);
			try {
				OntologyMetadataUtils.fillInOntologyViewBeanFromInstance(ob, ontInd);
			}
			catch (MetadataException e) {
				return null;
			}

			return ob;
		}
	}
	
	public List<OntologyBean> findAllOntologyVersionsById(Integer ontologyId) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		
		List<OntologyBean> res = new ArrayList<OntologyBean>();
		
		OWLIndividual vOntInd = getVirtualOntologyInstance(metadata, ontologyId);
		if (vOntInd != null) {
			List<Integer> ontologyIds = OntologyMetadataUtils.getAllOntologyVersionIDs(metadata, vOntInd);
			
			for (Integer id : ontologyIds) {
				res.add(findOntologyById(id));
			}
		}
		
		return res;
	}	
	
	public List<OntologyBean> findAllOntologyOrOntologyViewVersionsById(Integer ontologyOrViewId) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		
		List<OntologyBean> res = new ArrayList<OntologyBean>();

		OWLIndividual vOntInd = getVirtualOntologyOrViewInstance(metadata, ontologyOrViewId);
		if (vOntInd != null) {
			List<Integer> ontologyIds = OntologyMetadataUtils.getAllOntologyVersionIDs(metadata, vOntInd);
			
			for (Integer id : ontologyIds) {
				res.add(findOntologyOrOntologyViewById(id));
			}
		}
		
		return res;
	}	

	public List<OntologyBean> findLatestOntologyVersions() {
		//WARNING: Any modification to this method should be replicated 
		//         in the findLatestActiveOntologyVersions() method
		OWLModel metadata = getMetadataOWLModel();
		
		List<Integer> ontologyIds = OntologyMetadataUtils.getAllVirtualOntologyIDs(metadata);
		
		List<OntologyBean> res = new ArrayList<OntologyBean>();
		
		for (Integer ontologyId : ontologyIds) {
			res.add(findLatestOntologyVersionById(ontologyId));
		}
		
		return res;
	}
	
	public List<OntologyBean> findLatestActiveOntologyVersions() {
		//WARNING: Any modification to this method should be replicated 
		//         in the findLatestOntologyVersions() method
		OWLModel metadata = getMetadataOWLModel();
		
		List<Integer> ontologyIds = OntologyMetadataUtils.getAllVirtualOntologyIDs(metadata);
		
		return findLatestActiveOntologyVersions(ontologyIds);
	}
	
	public List<OntologyBean> findLatestActiveOntologyOrOntologyViewVersions() throws Exception {
		//WARNING: Any modification to this method should be replicated 
		//         in the findLatestOntologyVersions() method
		OWLModel metadata = getMetadataOWLModel();
		
		List<Integer> ontologyIds = OntologyMetadataUtils.getAllVirtualOntologyIDs(metadata);
		ontologyIds.addAll(OntologyMetadataUtils.getAllVirtualViewIDs(metadata));
		
		return findLatestActiveOntologyOrOntologyViewVersions(ontologyIds); 
	}

	public List<OntologyBean> findLatestActiveOntologyVersions(
			List<Integer> ontologyIds) {
		List<OntologyBean> res = new ArrayList<OntologyBean>();
		OntologyBean ob;
		
		for (Integer ontologyId : ontologyIds) {
			if ((ob = findLatestActiveOntologyVersionById(ontologyId)) != null) {			
				res.add(ob);
			}
		}
		
		return res;
	}
	
	public List<OntologyBean> findLatestActiveOntologyOrOntologyViewVersions(
			List<Integer> ontologyOrViewIds) throws Exception {
		List<OntologyBean> res = new ArrayList<OntologyBean>();
		OntologyBean ob;
		
		for (Integer ontologyOrViewId : ontologyOrViewIds) {
			if ((ob = findLatestActiveOntologyOrOntologyViewVersionById(ontologyOrViewId)) != null) {			
				res.add(ob);
			}
		}
		
		return res;
	}
	
	public int getNextAvailableOntologyId() {
		//return getNextAvailableIdWithSQWRL(QUERY_MAX_VIRTUAL_ONTOLOGY_ID, CLASS_VIRTUAL_ONTOLOGY);
		return OntologyMetadataUtils.getNextAvailableVirtualOntologyId(getMetadataOWLModel());
	}

	
	public int getNextAvailableOntologyVersionId() {
		//return getNextAvailableIdWithSQWRL(QUERY_MAX_ONTOLOGY_ID, CLASS_ONTOLOGY);
		return OntologyMetadataUtils.getNextAvailableOntologyVersionId(getMetadataOWLModel());
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

	public List<OntologyBean> searchOntologyMetadata(String query) {
		OWLModel metadata = getMetadataOWLModel();
		List<OWLIndividual> matchingIndividuals = OntologyMetadataUtils.searchOntologyMetadata(metadata, query);
		List<OntologyBean> res = new ArrayList<OntologyBean>();
		for (OWLIndividual ontInd : matchingIndividuals) {
			OntologyBean ob = new OntologyBean();
			try {
				OntologyMetadataUtils.fillInOntologyBeanFromInstance(ob, ontInd);
				res.add(ob);
			} catch (Exception e) {
				return null;
			}
		}
		return res;
	}
	
	//------------- private helper methods ---------------//
	
	private OWLIndividual getOntologyInstance(OWLModel metadata, int id, boolean createIfMissing) {
		String ontInstName = getOntologyIndividualName(id);
		OWLIndividual ontInd = metadata.getOWLIndividual(ontInstName);
		if (ontInd == null && createIfMissing) {
			ontInd = createOntologyInstance(metadata, ontInstName);
		}
		//alternative lookup
		if (ontInd == null) {
			ontInd = OntologyMetadataUtils.getOntologyWithId(metadata, id);
			if (ontInd != null) {
				log.warn("Ontology instance for id: " + id + " has been found having non-standard name: " + ontInd);
			}
		}
		return ontInd;
	}
	
	private OWLIndividual createOntologyInstance(OWLModel metadata, String indName) {
		OWLNamedClass ontClass = metadata.getOWLNamedClass(CLASS_ONTOLOGY);
		return ontClass.createOWLIndividual(indName);
	}

	private OWLIndividual getOntologyOrViewInstance(OWLModel metadata, int id) {
		String ontInstName = getOntologyIndividualName(id);
		OWLIndividual ontInd = metadata.getOWLIndividual(ontInstName);
		if (ontInd == null) {
			String viewInstName = getOntologyViewIndividualName(id);
			ontInd = metadata.getOWLIndividual(viewInstName);
		}
		//alternative lookup
		if (ontInd == null) {
			ontInd = OntologyMetadataUtils.getOntologyOrViewWithId(metadata, id);
			if (ontInd != null) {
				log.warn("Ontology or view instance for id: " + id + " has been found having non-standard name: " + ontInd);
			}
		}
		return ontInd;
	}
	
	private OWLIndividual getVirtualOntologyInstance(OWLModel metadata, int id) {
		String ontInstName = getVirtualOntologyIndividualName(id);
		OWLIndividual ontInd = metadata.getOWLIndividual(ontInstName);
		//alternative lookup
		if (ontInd == null) {
			ontInd = OntologyMetadataUtils.getVirtualOntologyWithId(metadata, id);
			if (ontInd != null) {
				log.warn("Virtual ontology instance for id: " + id + " has been found having non-standard name: " + ontInd);
			}
		}
		//create if could not be found
		if (ontInd == null) {
			ontInd = createVirtualOntologyInstance(metadata, ontInstName);
			ontInd.setPropertyValue(ontInd.getOWLModel().getOWLProperty(OntologyMetadataUtils.PROPERTY_ID), new Integer(id));
		}
		return ontInd;
	}
	
	private OWLIndividual getVirtualOntologyOrViewInstance(OWLModel metadata, int id) {//TODO
		String ontInstName = getVirtualOntologyIndividualName(id);
		OWLIndividual ontInd = metadata.getOWLIndividual(ontInstName);
		if (ontInd == null) {
			String viewInstName = getVirtualViewIndividualName(id);
			ontInd = metadata.getOWLIndividual(viewInstName);
		}
		//alternative lookup
		if (ontInd == null) {
			ontInd = OntologyMetadataUtils.getVirtualOntologyOrViewWithId(metadata, id);
			if (ontInd != null) {
				log.warn("Virtual ontology or view instance for id: " + id + " has been found having non-standard name: " + ontInd);
			}
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