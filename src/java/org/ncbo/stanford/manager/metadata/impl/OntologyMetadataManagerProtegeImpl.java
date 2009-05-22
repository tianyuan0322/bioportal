package org.ncbo.stanford.manager.metadata.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
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

	private static final String CLASS_ONTOLOGY = MetadataUtils.PREFIX_OMV + "Ontology";
	//private static final String CLASS_ONTOLOGY_VIEW = MetadataUtils.PREFIX_OMV + "OntologyView";
	private static final String CLASS_VIRTUAL_ONTOLOGY = MetadataUtils.PREFIX_METADATA + "VirtualOntology";
	//private static final String CLASS_VIRTUAL_VIEW = MetadataUtils.PREFIX_METADATA + "VirtualView";
	private static final String CLASS_USER = MetadataUtils.PREFIX_METADATA + "BioPortalUser";
	//private static final String CLASS_ONTOLOGY_DOMAIN = MetadataUtils.PREFIX_OMV + "OntologyDomain";
	
	private static final String QUERY_MAX_ONTOLOGY_ID = "Query-Max-Ontology-ID";
	private static final String QUERY_MAX_VIRTUAL_ONTOLOGY_ID = "Query-Max-VirtualOntology-ID";
	
	private static final boolean CREATE_IF_MISSING = true;
	private static final boolean DO_NOT_CREATE_IF_MISSING = false;
	
	private static final boolean ONLY_ACTIVE_VERSIONS = true;
	private static final boolean ALL_VERSIONS = (! ONLY_ACTIVE_VERSIONS);
	
	public void saveOntology(OntologyBean ob) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual ontVerInd = getOntologyInstance(metadata, ob.getId(), CREATE_IF_MISSING);

		OWLIndividual vOntInd = getVirtualOntologyInstance(metadata, ob.getOntologyId());
		OWLIndividual userInd = getUserInstance(metadata, ob.getUserId());
		Collection<OWLIndividual> domainInd = getOntologyDomainInstances(metadata, ob.getCategoryIds());

		OntologyMetadataUtils.ensureOntologyBeanDoesNotInvalidateOntologyInstance(ontVerInd, ob, vOntInd);
		
		OntologyMetadataUtils.fillInOntologyInstancePropertiesFromBean(ontVerInd, ob, vOntInd, userInd, domainInd);
//		OntologyMetadataUtils.setLatestVersion(vOntInd, ontVerInd); //use this if we will reintroduce the "metadata:currentVersion" property
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

	public List<OntologyBean> findOntologyVersions(
			List<Integer> ontologyVersionIds) {
		List<OntologyBean> res = new ArrayList<OntologyBean>();
		for (Integer ontologyVersionId : ontologyVersionIds) {
			res.add(findOntologyById(ontologyVersionId));
		}
		return res;
	}
	
//	public OntologyBean findVirtualOntologyById(Integer ontologyId) {
//		return findLatestActiveOntologyVersionById(ontologyId);
//	}	
//
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

	public List<OntologyBean> findAllOntologyVersionsById(Integer ontologyId) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		
		OWLIndividual vOntInd = getVirtualOntologyInstance(metadata, ontologyId);
		List<Integer> ontologyIds = OntologyMetadataUtils.getAllOntologyVersionIDs(metadata, vOntInd);
		List<OntologyBean> res = new ArrayList<OntologyBean>();
		
		for (Integer id : ontologyIds) {
			res.add(findOntologyById(id));
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

	public List<OntologyBean> findLatestActiveOntologyVersions(
			List<Integer> ontologyIds) {
		List<OntologyBean> res = new ArrayList<OntologyBean>();
		
		for (Integer ontologyId : ontologyIds) {
			res.add(findLatestActiveOntologyVersionById(ontologyId));
		}
		
		return res;
	}
	
	public int getNextAvailableOntologyId() {
		//return getNextAvailableIdWithSQWRL(QUERY_MAX_VIRTUAL_ONTOLOGY_ID, CLASS_VIRTUAL_ONTOLOGY);
		return OntologyMetadataUtils.getNextAvailableOntologyId(getMetadataOWLModel());
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
	
	//------------- private helper methods ---------------//
	
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
