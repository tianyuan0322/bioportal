package org.ncbo.stanford.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.util.metadata.MetadataUtils;
import org.ncbo.stanford.util.metadata.OntologyCategoryMetadataUtils;
import org.ncbo.stanford.util.metadata.OntologyGroupMetadataUtils;
import org.ncbo.stanford.util.metadata.OntologyMetadataUtils;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

public abstract class AbstractOntologyMetadataManager extends
		AbstractOntologyManagerProtege {

	protected static final boolean CREATE_IF_MISSING = true;
	protected static final boolean DO_NOT_CREATE_IF_MISSING = false;
	
	private static final Log log = LogFactory
			.getLog(AbstractOntologyMetadataManager.class);
	
	private Map<Integer, Integer> versionIdToOntologyIdMap = new HashMap<Integer, Integer>(0);
	private Map<Integer, Integer> viewVersionIdToViewIdMap = new HashMap<Integer, Integer>(0);
	private Map<Integer, List<Integer>> viewIdToOntologyIdsMap = new HashMap<Integer, List<Integer>>(0);
	
	/**
	 * Returns the virtual ontology id corresponding to the given ontology
	 * version id
	 * 
	 * @param versionId
	 * @return
	 */
	public Integer getOntologyIdByVersionId(Integer versionId) {
		return versionIdToOntologyIdMap.get(versionId);
	}
	
	/**
	 * Returns the virtual view id corresponding to the given view version id
	 * 
	 * @param viewVersionId
	 * @return
	 */
	public Integer getViewIdByViewVersionId(Integer viewVersionId) {
		return viewVersionIdToViewIdMap.get(viewVersionId);
	}
	
	/**
	 * Returns all virtual ontology ids corresponding to the given virtual view
	 * id
	 * 
	 * @param viewId
	 * @return
	 */
	public List<Integer> getOntologyIdsByViewId(Integer viewId) {
		return viewIdToOntologyIdsMap.get(viewId);
	}
	
	/**
	 * Populates three global maps for quick ID translation:
	 * 		versionIdToOntologyIdMap
	 * 		viewVersionIdToViewIdMap
	 * 		viewIdToOntologyIdsMap
	 */
	public void populateIdMaps() throws Exception {
		OWLModel metadata = getMetadataOWLModel();
		Map<Integer, Integer> versionIdToOntologyIdMap = new HashMap<Integer, Integer>(0);
		Map<Integer, Integer> viewVersionIdToViewIdMap = new HashMap<Integer, Integer>(0);
		Map<Integer, List<Integer>> viewIdToOntologyIdsMap = new HashMap<Integer, List<Integer>>(0);
		List<Integer> ontologyIds = OntologyMetadataUtils.getAllVirtualOntologyIDs(metadata, false);
		List<Integer> ontologyViewIds = OntologyMetadataUtils.getAllVirtualViewIDs(metadata);

		for (Integer ontologyId : ontologyIds) {
			OWLIndividual vOntInd = getVirtualOntologyInstance(metadata, ontologyId, DO_NOT_CREATE_IF_MISSING);
			//alternatively, if you decide to move this method in the OntologyMetadataUtils:
			//OWLIndividual vOntInd = OntologyMetadataUtils.getVirtualOntologyWithId(metadata, ontologyId);

			// populate versionIdToOntologyIdMap
			if (vOntInd != null) {
				List<Integer> allOntologyVersionIds;
				
				try {
					allOntologyVersionIds = OntologyMetadataUtils.getAllOntologyVersionIDs(metadata, vOntInd, false);
					
					for (Integer ontologyVersionId : allOntologyVersionIds) {
						versionIdToOntologyIdMap.put(ontologyVersionId, ontologyId);
					}				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		for (Integer viewId : ontologyViewIds) {
			OWLIndividual vViewInd = getVirtualViewInstance(metadata, viewId, DO_NOT_CREATE_IF_MISSING);
			//alternatively, if you decide to move this method in the OntologyMetadataUtils:
			//OWLIndividual vViewInd = OntologyMetadataUtils.getVirtualViewWithId(metadata, viewId);

			if (vViewInd != null) {
				try {
					List<Integer> allViewVersionIDs = OntologyMetadataUtils.getAllOntologyVersionIDs(metadata, vViewInd, false);
					List<Integer> vOntIds = MetadataUtils.getPropertyValueIds(metadata, vViewInd, 
							OntologyMetadataUtils.PROPERTY_VIRTUAL_VIEW_OF);

					// populate viewVersionIdToViewIdMap
					for (Integer viewVersionId : allViewVersionIDs) {
						viewVersionIdToViewIdMap.put(viewVersionId, viewId);
					}
					// populate viewIdToOntologyIdsMap
					viewIdToOntologyIdsMap.put(viewId, vOntIds);					
				} catch (Exception e) {
					e.printStackTrace();
				}			
			}
		}
		
		this.versionIdToOntologyIdMap = versionIdToOntologyIdMap;
		this.viewVersionIdToViewIdMap = viewVersionIdToViewIdMap;
		this.viewIdToOntologyIdsMap = viewIdToOntologyIdsMap;		
	}
	
	protected OWLIndividual getOntologyInstance(OWLModel metadata, int id, boolean createIfMissing) {
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
		OWLNamedClass ontClass = metadata.getOWLNamedClass(OntologyMetadataUtils.CLASS_OMV_ONTOLOGY);
		return ontClass.createOWLIndividual(indName);
	}

	public OWLIndividual getOntologyOrViewInstance(OWLModel metadata, int id) {
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
	
	protected OWLIndividual getVirtualOntologyInstance(OWLModel metadata, int id, boolean createIfMissing) {
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
		if (ontInd == null && createIfMissing) {
			ontInd = createVirtualOntologyInstance(metadata, ontInstName);
			ontInd.setPropertyValue(ontInd.getOWLModel().getOWLProperty(OntologyMetadataUtils.PROPERTY_ID), new Integer(id));
		}
		return ontInd;
	}
	
	private OWLIndividual createVirtualOntologyInstance(OWLModel metadata, String indName) {
		OWLNamedClass ontClass = metadata.getOWLNamedClass(OntologyMetadataUtils.CLASS_VIRTUAL_ONTOLOGY);
		return ontClass.createOWLIndividual(indName);
	}
	
	protected OWLIndividual getVirtualOntologyOrViewInstance(OWLModel metadata, int id) {
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

	
	protected OWLIndividual getOntologyViewInstance(OWLModel metadata, int id, boolean createIfMissing) {
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
		OWLNamedClass ontClass = metadata.getOWLNamedClass(OntologyMetadataUtils.CLASS_ONTOLOGY_VIEW);
		return ontClass.createOWLIndividual(indName);
	}
	
	protected OWLIndividual getVirtualViewInstance(OWLModel metadata, int id, boolean createIfMissing) {
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
		if (ontInd == null && createIfMissing) {
			ontInd = createVirtualViewInstance(metadata, ontInstName);
			ontInd.setPropertyValue(ontInd.getOWLModel().getOWLProperty(OntologyMetadataUtils.PROPERTY_ID), new Integer(id));
		}
		return ontInd;
	}
	
	private OWLIndividual createVirtualViewInstance(OWLModel metadata, String indName) {
		OWLNamedClass ontClass = metadata.getOWLNamedClass(OntologyMetadataUtils.CLASS_VIRTUAL_VIEW);
		return ontClass.createOWLIndividual(indName);
	}
	
	
	protected OWLIndividual getUserInstance(OWLModel metadata, Integer userId) {
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
		OWLNamedClass userClass = metadata.getOWLNamedClass(OntologyMetadataUtils.CLASS_BIOPORTAL_USER);
		return userClass.createOWLIndividual(indName);
	}
	
	
	protected OWLIndividual getOntologyDomainInstance(OWLModel metadata, int id, boolean createIfMissing) {
		String ontDomainInstName = getOntologyDomainIndividualName(id);
		OWLIndividual ontDomainInd = metadata.getOWLIndividual(ontDomainInstName);
		
		if (ontDomainInd == null && createIfMissing) {
			ontDomainInd = createOntologyDomainInstance(metadata, ontDomainInstName);
		}
		
		//alternative lookup
		if (ontDomainInd == null) {
			ontDomainInd = OntologyCategoryMetadataUtils.getOntologyDomainWithId(metadata, id);
			if (ontDomainInd != null) {
				log.warn("OntologyDomain instance for id: " + id + " has been found having non-standard name: " + ontDomainInd);
			}
		}
		
		return ontDomainInd;
	}
	
	private OWLIndividual createOntologyDomainInstance(OWLModel metadata, String indName) {
		OWLNamedClass ontClass = metadata.getOWLNamedClass(OntologyMetadataUtils.CLASS_OMV_ONTOLOGY_DOMAIN);

		return ontClass.createOWLIndividual(indName);
	}


	protected Collection<OWLIndividual> getOntologyDomainInstances(OWLModel metadata,
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

	
	protected OWLIndividual getOntologyGroupInstance(OWLModel metadata, int id, boolean createIfMissing) {
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
		OWLNamedClass ontClass = metadata.getOWLNamedClass(OntologyMetadataUtils.CLASS_ONTOLOGY_GROUP);

		return ontClass.createOWLIndividual(indName);
	}
}
