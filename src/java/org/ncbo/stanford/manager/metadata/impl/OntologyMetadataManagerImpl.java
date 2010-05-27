package org.ncbo.stanford.manager.metadata.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.exception.MetadataException;
import org.ncbo.stanford.manager.AbstractOntologyMetadataManager;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.metadata.MetadataUtils;
import org.ncbo.stanford.util.metadata.OntologyMetadataUtils;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
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
public class OntologyMetadataManagerImpl extends
		AbstractOntologyMetadataManager implements OntologyMetadataManager {

	private static final Log log = LogFactory
			.getLog(OntologyMetadataManagerImpl.class);

	private static final String CLASS_ONTOLOGY = OntologyMetadataUtils.CLASS_OMV_ONTOLOGY;
	private static final String CLASS_VIRTUAL_ONTOLOGY = OntologyMetadataUtils.CLASS_VIRTUAL_ONTOLOGY;
	private static final String QUERY_MAX_ONTOLOGY_ID = "Query-Max-Ontology-ID";
	private static final String QUERY_MAX_VIRTUAL_ONTOLOGY_ID = "Query-Max-VirtualOntology-ID";

	private static final boolean CREATE_IF_MISSING = true;
	private static final boolean DO_NOT_CREATE_IF_MISSING = false;
	private static final boolean ONLY_ACTIVE_VERSIONS = true;
	private static final boolean ALL_VERSIONS = (!ONLY_ACTIVE_VERSIONS);
	private static final boolean INCLUDE_SUBCLASSES = true;
	private static final boolean DO_NOT_INCLUDE_SUBCLASSES = (!INCLUDE_SUBCLASSES);

	private static final int SQWRL_EXCEPTION_ERROR_CODE = -1;
	private static final int SWRL_PARSE_EXCEPTION_ERROR_CODE = -2;
	private static final int SWRL_FACTORY_EXCEPTION_ERROR_CODE = -3;

	public void saveOntologyOrView(OntologyBean ob) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual ontOrViewVerInd;

		if (ob.isView()) {
			ontOrViewVerInd = getOntologyViewInstance(metadata, ob.getId(),
					CREATE_IF_MISSING);
		} else {
			ontOrViewVerInd = getOntologyInstance(metadata, ob.getId(),
					CREATE_IF_MISSING);
		}

		saveOrUpdate(metadata, ontOrViewVerInd, ob);
	}

	public void updateOntologyOrView(OntologyBean ob) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual ontOrViewVerInd;

		if (ob.isView()) {
			ontOrViewVerInd = getOntologyViewInstance(metadata, ob.getId(),
					DO_NOT_CREATE_IF_MISSING);
		} else {
			ontOrViewVerInd = getOntologyInstance(metadata, ob.getId(),
					DO_NOT_CREATE_IF_MISSING);
		}

		if (ontOrViewVerInd == null) {
			throw new MetadataException("Metadata for ontology "
					+ (ob.isView() ? "view" : "") + ob.getId()
					+ " could not be updated because it could not be found!");
		}

		saveOrUpdate(metadata, ontOrViewVerInd, ob);
	}

	/**
	 * Method containing the common behavior for save and update ontology bean
	 * 
	 * @param metadata
	 *            the Metadata OWL ontology
	 * @param ontVerInd
	 * @param ob
	 * @throws Exception
	 */
	private void saveOrUpdate(OWLModel metadata, OWLIndividual ontVerInd,
			OntologyBean ob) throws Exception {
		OWLIndividual vOntInd;

		if (ob.isView()) {
			vOntInd = getVirtualViewInstance(metadata, ob.getOntologyId(),
					CREATE_IF_MISSING);
		} else {
			vOntInd = getVirtualOntologyInstance(metadata, ob.getOntologyId(),
					CREATE_IF_MISSING);
		}

		OWLIndividual userInd = getUserInstance(metadata, ob.getUserId());
		Collection<OWLIndividual> domainInd = getOntologyDomainInstances(
				metadata, ob.getCategoryIds());
		Collection<OWLIndividual> viewInd = getOntologyViewInstances(metadata,
				ob.getHasViews());
		Collection<OWLIndividual> srcOntInd = null;

		if (OntologyMetadataUtils.isOntologyViewIndividual(ontVerInd)) {
			srcOntInd = getOntologyInstances(metadata, ob
					.getViewOnOntologyVersionId());
		}

		OntologyMetadataUtils
				.ensureOntologyBeanDoesNotInvalidateOntologyInstance(ontVerInd,
						ob, vOntInd);
		OntologyMetadataUtils.fillInOntologyInstancePropertiesFromBean(
				ontVerInd, ob, vOntInd, userInd, domainInd, viewInd, srcOntInd);
		// OntologyMetadataUtils.setLatestVersion(vOntInd, ontVerInd); //use
		// this if we will reintroduce the "metadata:currentVersion" property
	}

	/**
	 * Deletes or "deprecates" the ontology metadata specified by the
	 * ontologyBean representing an ontology or view version.
	 * 
	 * @param ontologyBean
	 * @param removeMetadata
	 * @throws Exception
	 */
	public void deleteOntologyOrView(OntologyBean ob, boolean removeMetadata)
			throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual ontVerInd = getOntologyOrViewInstance(metadata, ob
				.getId());

		if (ontVerInd == null) {
			throw new MetadataException(
					"Metadata for ontology "
							+ (ob.isView() ? "view" : "")
							+ ob.getId()
							+ " could not be deleted/deprecated because it could not be found!");
		}

		if (removeMetadata) {
			ontVerInd.delete();
		} else {
			ob.setStatusId(StatusEnum.STATUS_DEPRECATED.getStatus());
			saveOrUpdate(metadata, ontVerInd, ob);
		}

		// delete the virtual record if all version records have been deleted
		// if (findAllOntologyVersionsById(ob.getOntologyId()).isEmpty()) {
		// OWLIndividual vOntInd = getVirtualOntologyInstance(metadata,
		// ob.getOntologyId());
		// vOntInd.delete();
		// }
	}

	public OntologyBean findOntologyOrViewVersionById(
			Integer ontologyOrViewVersionId) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		// should we have a SQWRL solution for this too?

		// OWLProperty prop =
		// metadata.getOWLProperty(OntologyMetadataUtils.PROPERTY_OMV_NAME);
		// Collection results = metadata.getFramesWithValue(prop, null, false,
		// ontologyVersionId);
		// for (Object frame : results) {
		// if (frame instanceof OWLIndividual) {
		// OWLIndividual cls = (OWLIndividual) frame;
		// if (cls.hasRDFType(metadata.getRDFSNamedClass(CLASS_ONTOLOGY))) {
		// OntologyBean ob = new OntologyBean();
		// try {
		// OntologyMetadataUtils.fillInOntologyBeanFromInstance(ob, cls);
		// return ob;
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// }

		OWLIndividual ontInd = getOntologyOrViewInstance(metadata,
				ontologyOrViewVersionId);

		if (ontInd == null) {
			return null;
		}

		boolean isView = OntologyMetadataUtils.isOntologyViewIndividual(ontInd);
		OntologyBean ob = new OntologyBean(isView);
		OntologyMetadataUtils.fillInOntologyBeanFromInstance(ob, ontInd);

		return ob;
	}

	// private OntologyBean findLatestOntologyVersionById(Integer ontologyId) {
	// //WARNING: Any modification to this method should be replicated
	// // in the findLatestActiveOntologyVersionById() method
	// OWLModel metadata = getMetadataOWLModel();
	//		
	// OWLIndividual vOntInd = getVirtualOntologyInstance(metadata, ontologyId);
	// OntologyBean ob = new OntologyBean(false);
	// try {
	// OWLIndividual ontInd = OntologyMetadataUtils.getLatestVersion(vOntInd,
	// ALL_VERSIONS);
	//			
	// OntologyMetadataUtils.fillInOntologyBeanFromInstance(ob, ontInd);
	// return ob;
	// } catch (Exception e) {
	// return null;
	// }
	// }

	private OntologyBean findLatestActiveOntologyVersionById(Integer ontologyId)
			throws Exception {
		// WARNING: Any modification to this method should be replicated
		// in the findLatestOntologyVersionById() method
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual vOntInd = getVirtualOntologyInstance(metadata,
				ontologyId, DO_NOT_CREATE_IF_MISSING);
		OntologyBean ob = new OntologyBean(false);
		OWLIndividual ontInd = OntologyMetadataUtils.getLatestVersion(vOntInd,
				ONLY_ACTIVE_VERSIONS);

		try {
			OntologyMetadataUtils.fillInOntologyBeanFromInstance(ob, ontInd);
		} catch (MetadataException e) {
			return null;
		}

		return ob;
	}

	public OntologyBean findLatestOntologyOrViewVersionById(
			Integer ontologyOrViewId) throws Exception {
		// WARNING: Any modification to this method should be replicated
		// in the findLatestActiveOntologyOrOntologyViewVersionById() method
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual vOntInd = getVirtualOntologyOrViewInstance(metadata,
				ontologyOrViewId);

		if (vOntInd == null) {
			return null;
		}

		boolean isView = OntologyMetadataUtils.isVirtualViewIndividual(vOntInd);
		OntologyBean ob = new OntologyBean(isView);
		OWLIndividual ontInd = OntologyMetadataUtils.getLatestVersion(vOntInd,
				ALL_VERSIONS);

		try {
			OntologyMetadataUtils.fillInOntologyBeanFromInstance(ob, ontInd);
		} catch (MetadataException e) {
			return null;
		}

		return ob;
	}

	public OntologyBean findLatestActiveOntologyOrViewVersionById(
			Integer ontologyOrViewId) throws Exception {
		// WARNING: Any modification to this method should be replicated
		// in the findLatestOntologyOrOntologyViewVersionById() method
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual vOntInd = getVirtualOntologyOrViewInstance(metadata,
				ontologyOrViewId);

		if (vOntInd == null) {
			return null;
		}

		boolean isView = OntologyMetadataUtils.isVirtualViewIndividual(vOntInd);
		OntologyBean ob = new OntologyBean(isView);
		OWLIndividual ontInd = OntologyMetadataUtils.getLatestVersion(vOntInd,
				ONLY_ACTIVE_VERSIONS);

		try {
			OntologyMetadataUtils.fillInOntologyBeanFromInstance(ob, ontInd);
		} catch (MetadataException e) {
			return null;
		}

		return ob;
	}

	public List<OntologyBean> findAllOntologyOrViewVersionsById(
			Integer ontologyOrViewId, boolean excludeDeprecated)
			throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		List<OntologyBean> res = new ArrayList<OntologyBean>();
		OWLIndividual vOntInd = getVirtualOntologyOrViewInstance(metadata,
				ontologyOrViewId);

		if (vOntInd != null) {
			List<Integer> ontologyVersionIds = OntologyMetadataUtils
					.getAllOntologyVersionIDs(metadata, vOntInd,
							excludeDeprecated);

			for (Integer id : ontologyVersionIds) {
				try {
					OntologyBean ob = findOntologyOrViewVersionById(id);

					if (ob != null) {
						res.add(ob);
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error("Error while getting ontology version: " + id, e);
				}
			}
		}

		return res;
	}

	public List<OntologyBean> findLatestOntologyVersions() throws Exception {
		// WARNING: Any modification to this method should be replicated
		// in the findLatestActiveOntologyVersions() method
		OWLModel metadata = getMetadataOWLModel();
		List<Integer> ontologyIds = OntologyMetadataUtils
				.getAllVirtualOntologyIDs(metadata, DO_NOT_INCLUDE_SUBCLASSES);
		List<OntologyBean> res = new ArrayList<OntologyBean>();

		for (Integer ontologyId : ontologyIds) {
			try {
				OntologyBean ob = findLatestOntologyOrViewVersionById(ontologyId);

				if (ob != null) {
					res.add(ob);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Error while getting latest version for ontology "
						+ ontologyId, e);
			}
		}

		return res;
	}

	public List<OntologyBean> findLatestAutoPulledOntologyVersions()
			throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		List<Integer> ontologyIds = OntologyMetadataUtils
				.getAllVirtualOntologyIDs(metadata, DO_NOT_INCLUDE_SUBCLASSES);
		List<OntologyBean> res = new ArrayList<OntologyBean>();

		for (Integer ontologyId : ontologyIds) {
			try {
				OntologyBean ob = findLatestOntologyOrViewVersionById(ontologyId);

				if (ob != null) {
					Byte isManual = ob.getIsManual();

					if (!StringHelper.isNullOrNullString(ob.getOboFoundryId())
							&& isManual != null
							&& isManual.byteValue() == ApplicationConstants.FALSE) {
						res.add(ob);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Error while getting latest version for ontology "
						+ ontologyId, e);
			}
		}

		return res;
	}

	public List<OntologyBean> findLatestActiveOntologyVersions()
			throws Exception {
		// WARNING: Any modification to this method should be replicated
		// in the findLatestOntologyVersions() method
		OWLModel metadata = getMetadataOWLModel();
		List<Integer> ontologyIds = OntologyMetadataUtils
				.getAllVirtualOntologyIDs(metadata, DO_NOT_INCLUDE_SUBCLASSES);

		return findLatestActiveOntologyVersions(ontologyIds);
	}

	public List<OntologyBean> findLatestActiveOntologyOrOntologyViewVersions()
			throws Exception {
		// WARNING: Any modification to this method should be replicated
		// in the findLatestOntologyVersions() method
		OWLModel metadata = getMetadataOWLModel();
		List<Integer> ontologyIds = OntologyMetadataUtils
				.getAllVirtualOntologyIDs(metadata, INCLUDE_SUBCLASSES);
		ontologyIds
				.addAll(OntologyMetadataUtils.getAllVirtualViewIDs(metadata));

		return findLatestActiveOntologyOrOntologyViewVersions(ontologyIds);
	}

	private List<OntologyBean> findLatestActiveOntologyVersions(
			List<Integer> ontologyIds) {
		List<OntologyBean> res = new ArrayList<OntologyBean>();

		for (Integer ontologyId : ontologyIds) {
			try {
				OntologyBean ob = findLatestActiveOntologyVersionById(ontologyId);

				if (ob != null) {
					res.add(ob);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Error while getting latest version for ontology: "
						+ ontologyId, e);
			}
		}

		return res;
	}

	public List<OntologyBean> findLatestActiveOntologyOrOntologyViewVersions(
			List<Integer> ontologyOrViewIds) throws Exception {
		List<OntologyBean> res = new ArrayList<OntologyBean>();

		for (Integer ontologyOrViewId : ontologyOrViewIds) {
			try {
				OntologyBean ob = findLatestActiveOntologyOrViewVersionById(ontologyOrViewId);

				if (ob != null) {
					res.add(ob);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error(
						"Error while getting latest version for ontology/view: "
								+ ontologyOrViewId, e);
			}
		}

		return res;
	}

	public int getNextAvailableOntologyId() throws Exception {
		// return getNextAvailableIdWithSQWRL(QUERY_MAX_VIRTUAL_ONTOLOGY_ID,
		// CLASS_VIRTUAL_ONTOLOGY);
		return OntologyMetadataUtils
				.getNextAvailableVirtualOntologyId(getMetadataOWLModel());
	}

	public int getNextAvailableOntologyVersionId() throws Exception {
		// return getNextAvailableIdWithSQWRL(QUERY_MAX_ONTOLOGY_ID,
		// CLASS_ONTOLOGY);
		return OntologyMetadataUtils
				.getNextAvailableOntologyVersionId(getMetadataOWLModel());
	}

	private int getNextAvailableIdWithSQWRL(String queryName, String className)
			throws Exception {
		// TODO check this
		try {
			SQWRLQueryEngine queryEngine = getMetadataSQWRLEngine();
			SWRLFactory swrlFactory = getMetadataSWRLFactory();

			if (swrlFactory.hasImp(queryName)) {
				SWRLImp imp = swrlFactory.getImp(queryName);
				imp.deleteImp();
			}

			swrlFactory.createImp(queryName, className + "(?o) "
					+ SWRLParser.AND_CHAR + " " + MetadataUtils.PROPERTY_ID
					+ "(?o, ?id) " + SWRLParser.IMP_CHAR + " sqwrl:max(?id)");
			queryEngine.runSQWRLQueries();
			SQWRLResult result = queryEngine.getSQWRLResult(queryName);

			if (result == null) {
				return 0;
			}

			DatatypeValue value = result.getDatatypeValue(0);

			return value.getInt() + 1;
		} catch (SQWRLException e) {
			e.printStackTrace();
			return SQWRL_EXCEPTION_ERROR_CODE;
		} catch (SWRLParseException e) {
			e.printStackTrace();
			return SWRL_PARSE_EXCEPTION_ERROR_CODE;
		} catch (SWRLFactoryException e) {
			e.printStackTrace();
			return SWRL_FACTORY_EXCEPTION_ERROR_CODE;
		}
	}

	// ***************** view specific methods ************************

	public OntologyBean findLatestActiveOntologyViewVersionById(Integer viewId)
			throws Exception {
		// WARNING: Any modification to this method should be replicated
		// in the findLatestOntologyViewVersionById() method
		OWLModel metadata = getMetadataOWLModel();
		OWLIndividual vOntInd = getVirtualViewInstance(metadata, viewId,
				DO_NOT_CREATE_IF_MISSING);
		OntologyBean ob = new OntologyBean(true);

		OWLIndividual ontInd = OntologyMetadataUtils.getLatestVersion(vOntInd,
				ONLY_ACTIVE_VERSIONS);

		try {
			OntologyMetadataUtils.fillInOntologyBeanFromInstance(ob, ontInd);
		} catch (MetadataException e) {
			return null;
		}

		return ob;
	}

	public List<OntologyBean> findLatestOntologyViewVersions() throws Exception {
		// WARNING: Any modification to this method should be replicated
		// in the findLatestActiveOntologyViewVersions() method
		OWLModel metadata = getMetadataOWLModel();
		List<Integer> viewIds = OntologyMetadataUtils
				.getAllVirtualViewIDs(metadata);
		List<OntologyBean> res = new ArrayList<OntologyBean>();

		for (Integer viewId : viewIds) {
			try {
				OntologyBean ob = findLatestOntologyOrViewVersionById(viewId);

				if (ob != null) {
					res.add(ob);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error(
						"Error while getting latest version for ontology view "
								+ viewId, e);
			}
		}

		return res;
	}

	public List<OntologyBean> findLatestActiveOntologyViewVersions()
			throws Exception {
		// WARNING: Any modification to this method should be replicated
		// in the findLatestOntologyViewVersions() method
		OWLModel metadata = getMetadataOWLModel();
		List<Integer> viewIds = OntologyMetadataUtils
				.getAllVirtualViewIDs(metadata);
		List<OntologyBean> res = new ArrayList<OntologyBean>();

		for (Integer viewId : viewIds) {
			try {
				OntologyBean ob = findLatestActiveOntologyViewVersionById(viewId);

				if (ob != null) {
					res.add(ob);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Error while getting latest version for view "
						+ viewId, e);
			}
		}

		return res;
	}

	public int getNextAvailableVirtualViewId() throws Exception {
		// return getNextAvailableIdWithSQWRL(QUERY_MAX_VIRTUAL_VIEW_ID,
		// CLASS_VIRTUAL_ONTOLOGY);
		return OntologyMetadataUtils
				.getNextAvailableVirtualViewId(getMetadataOWLModel());
	}

	public int getNextAvailableOntologyViewVersionId() throws Exception {
		// return getNextAvailableIdWithSQWRL(QUERY_MAX_ONTOLOGY_VIEW_ID,
		// CLASS_ONTOLOGY);
		return OntologyMetadataUtils
				.getNextAvailableOntologyViewVersionId(getMetadataOWLModel());
	}

	public OntologyBean findLatestOntologyVersionByOboFoundryId(
			String oboFoundryId) throws Exception {
		return OntologyMetadataUtils.findLatestOntologyVersionByOboFoundryId(
				getMetadataOWLModel(), oboFoundryId, ALL_VERSIONS);
	}

	// *****************************************

	public List<OntologyBean> searchOntologyMetadata(String query,
			boolean includeViews) throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		List<OWLIndividual> matchingIndividuals = OntologyMetadataUtils
				.searchOntologyMetadata(metadata, query, includeViews);
		List<OntologyBean> res = new ArrayList<OntologyBean>();

		for (OWLIndividual ontInd : matchingIndividuals) {
			OntologyBean ob = new OntologyBean(false);

			try {
				OntologyMetadataUtils
						.fillInOntologyBeanFromInstance(ob, ontInd);
				res.add(ob);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
			}
		}

		return res;
	}

	public List<OntologyBean> searchOntologyViewMetadata(String query)
			throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		List<OWLIndividual> matchingIndividuals = OntologyMetadataUtils
				.searchOntologyViewMetadata(metadata, query);
		List<OntologyBean> res = new ArrayList<OntologyBean>();

		for (OWLIndividual ontInd : matchingIndividuals) {
			OntologyBean ob = new OntologyBean(true);

			try {
				OntologyMetadataUtils
						.fillInOntologyBeanFromInstance(ob, ontInd);
				res.add(ob);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
			}
		}

		return res;
	}

	// ------------- private helper methods ---------------//

	private Collection<OWLIndividual> getOntologyViewInstances(
			OWLModel metadata, List<Integer> ontViewVerIds) {
		HashSet<OWLIndividual> res = new HashSet<OWLIndividual>();

		for (Integer ontViewVerId : ontViewVerIds) {
			OWLIndividual ontViewInd = getOntologyViewInstance(metadata,
					ontViewVerId, DO_NOT_CREATE_IF_MISSING);

			if (ontViewInd != null) {
				res.add(ontViewInd);
			} else {
				log
						.error("No metadata:OntologyView individual found for view ID: "
								+ ontViewVerId);
			}
		}

		return res;
	}

	private Collection<OWLIndividual> getOntologyInstances(OWLModel metadata,
			List<Integer> ontVerIds) {
		HashSet<OWLIndividual> res = new HashSet<OWLIndividual>();

		for (Integer ontVerId : ontVerIds) {
			OWLIndividual ontInd = getOntologyInstance(metadata, ontVerId,
					DO_NOT_CREATE_IF_MISSING);

			if (ontInd != null) {
				res.add(ontInd);
			} else {
				ontInd = getOntologyViewInstance(metadata, ontVerId,
						DO_NOT_CREATE_IF_MISSING);

				if (ontInd != null) {
					res.add(ontInd);
				} else {
					log
							.error("No OMV:Ontology individual found for ontology version ID: "
									+ ontVerId);
				}
			}
		}

		return res;
	}
}