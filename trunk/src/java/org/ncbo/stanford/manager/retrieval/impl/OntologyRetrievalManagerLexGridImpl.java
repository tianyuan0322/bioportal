package org.ncbo.stanford.manager.retrieval.impl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.LexGrid.LexBIG.DataModel.Collections.AssociatedConceptList;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.Impl.dataAccess.WriteLockManager;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.ActiveOption;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.ConvenienceMethods;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.concepts.Comment;
import org.LexGrid.concepts.Concept;
import org.LexGrid.concepts.ConceptProperty;
import org.LexGrid.concepts.Definition;
import org.LexGrid.concepts.Presentation;
import org.LexGrid.naming.SupportedProperty;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.manager.AbstractOntologyManagerLexGrid;
import org.ncbo.stanford.manager.retrieval.OntologyRetrievalManager;
import org.ncbo.stanford.util.constants.ApplicationConstants;

/**
 * A implementation of the OntologyRetrievalManager for ontologies stored in
 * LexGrid.
 * 
 * 
 * @author Pradip Kanjamala
 * 
 */

public class OntologyRetrievalManagerLexGridImpl extends
		AbstractOntologyManagerLexGrid implements OntologyRetrievalManager {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(OntologyRetrievalManagerLexGridImpl.class);
	private LexBIGService lbs;
	LexBIGServiceConvenienceMethods lbscm;

	public OntologyRetrievalManagerLexGridImpl() throws Exception {
		lbs = LexBIGServiceImpl.defaultInstance();
		lbscm = (LexBIGServiceConvenienceMethods) lbs
				.getGenericExtension("LexBIGServiceConvenienceMethods");
	}

	public List<String> findProperties(VNcboOntology ncboOntology)
			throws Exception {
		String urnAndVersion = ncboOntology.getCodingScheme();
		String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
		CodingScheme cs = getCodingScheme(lbs, urnVersionArray[0],
				urnVersionArray[1]);
		ArrayList<String> list = new ArrayList<String>();
		SupportedProperty[] sp = cs.getMappings().getSupportedProperty();

		for (int i = 0; i < sp.length; i++) {
			SupportedProperty prop = sp[i];

			if (prop != null && StringUtils.isNotBlank(prop.getLocalId())) {
				list.add(prop.getLocalId());
			}
		}
		return list;
	}

	/**
	 * Get the root concept for the specified ontology.
	 */
	public ClassBean findRootConcept(VNcboOntology ncboOntology)
			throws Exception {
		String schemeName = getLexGridCodingSchemeName(ncboOntology);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);
		ResolvedConceptReferenceList rcrl = getHierarchyRootConcepts(
				schemeName, csvt);
		return createThingClassBeanWithCount(rcrl);
	}

	/**
	 * Find just the concept without the relations. Makes use of the
	 * CodedNodeSet of LexBIG to implement
	 * 
	 * @param ncboOntology
	 * @param conceptId
	 * @return
	 * @throws Exception
	 */
	public ClassBean findConceptIncludeChildCountNoRelations(
			VNcboOntology ncboOntology, String conceptId) throws Exception {
		String schemeName = getLexGridCodingSchemeName(ncboOntology);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);
		// Perform the query ...
		ConceptReferenceList crefs = ConvenienceMethods
				.createConceptReferenceList(new String[] { conceptId },
						schemeName);
		ResolvedConceptReferenceList matches = lbs.getCodingSchemeConcepts(
				schemeName, csvt).restrictToStatus(ActiveOption.ALL, null)
				.restrictToCodes(crefs).resolveToList(null, null, null, 1);
		// Analyze the result ...
		if (matches.getResolvedConceptReferenceCount() > 0) {
			ResolvedConceptReference rcr = (ResolvedConceptReference) matches
					.enumerateResolvedConceptReference().nextElement();
			return createClassBeanWithChildCount(rcr, false);
		}
		return null;
	}

	/**
	 * Find just the concept with all the relations. Makes use of the
	 * CodedNodeGraph of LexBIG to implement
	 * 
	 * @param ncboOntology
	 * @param conceptId
	 * @return
	 * @throws Exception
	 */
	public ClassBean findConcept(VNcboOntology ncboOntology, String conceptId)
			throws Exception {
		String schemeName = getLexGridCodingSchemeName(ncboOntology);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);
		ResolvedConceptReferenceList matches = lbs.getNodeGraph(schemeName,
				csvt, null).resolveAsList(
				ConvenienceMethods
						.createConceptReference(conceptId, schemeName), true,
				true, 0, 1, null, null, null, -1);
		// Analyze the result ...
		if (matches.getResolvedConceptReferenceCount() > 0) {
			ResolvedConceptReference ref = (ResolvedConceptReference) matches
					.enumerateResolvedConceptReference().nextElement();
			// Add the children
			ClassBean classBean = createClassBeanWithChildCount(ref, true);

			// Add the parents
			AssociationList parentList = getHierarchyLevelPrev(schemeName,
					csvt, conceptId);
			addAssociationListInfoToClassBean(parentList, classBean,
					ApplicationConstants.SUPER_CLASS, false);
			return classBean;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ncbo.stanford.manager.OntologyRetrievalManager#findPathFromRoot(org.ncbo.stanford.domain.custom.entity.VNcboOntology,
	 *      java.lang.String, boolean)
	 */
	public ClassBean findPathFromRoot(VNcboOntology ncboOntology,
			String conceptId, boolean light) throws Exception {
		String schemeName = getLexGridCodingSchemeName(ncboOntology);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);
		String[] hierarchyIDs = lbscm.getHierarchyIDs(schemeName, csvt);
		String hierarchyId = (hierarchyIDs.length > 0) ? hierarchyIDs[0] : null;
		for (String hierarchy : hierarchyIDs) {
			if (hierarchy.equalsIgnoreCase("IS_A"))
				hierarchyId = hierarchy;
		}
		AssociationList pathToRoot = lbscm.getHierarchyPathToRoot(schemeName,
				csvt, hierarchyId, conceptId, false,
				LexBIGServiceConvenienceMethods.HierarchyPathResolveOption.ONE,
				null);

		// Recursively reverse the chain ...
		AssociationList pathFromRoot = new AssociationList();
		HashMap<String, EntityDescription> codeToEntityDescriptionMap = new HashMap<String, EntityDescription>();
		for (int i = pathToRoot.getAssociationCount() - 1; i >= 0; i--) {
			reverseAssoc(ncboOntology, pathToRoot.getAssociation(i),
					pathFromRoot, codeToEntityDescriptionMap);
		}

		ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();
		boolean includeChildren = !light;
		for (int i = 0; i < pathFromRoot.getAssociationCount(); i++) {
			Association association = pathFromRoot.getAssociation(i);
			ConceptReference cr = association.getAssociationReference();
			ClassBean classBean = createClassBeanWithChildCount(ncboOntology,
					cr, includeChildren);

			classBeans.add(classBean);
			addAssociationListInfoToClassBean(pathFromRoot, classBean,
					ApplicationConstants.SUB_CLASS, includeChildren);

		}

		// The classBeans list could be empty either because there is no path
		// from root to the conceptId or the conceptId is the root itself.
		if (classBeans.isEmpty()) {
			// Check if the conceptId is the root itself. If so, we want to add
			// the root concept to the classBeans list
			ResolvedConceptReferenceList hier_rcrl = getHierarchyRootConcepts(
					schemeName, csvt);
			Enumeration<ResolvedConceptReference> refEnum = hier_rcrl
					.enumerateResolvedConceptReference();
			ResolvedConceptReference ref = null;

			while (refEnum.hasMoreElements()) {
				ref = (ResolvedConceptReference) refEnum.nextElement();
				ClassBean bean;

				if (ref.getConceptCode().equals(conceptId)) {
					// We need a lightweight classBean....
					ResolvedConceptReference rcr = getLightResolvedConceptReference(
							ncboOntology, conceptId);
					bean = createClassBeanWithChildCount(rcr, false);
					classBeans.add(bean);
				}
			}
		}

		ClassBean thingBean = createThingClassBean(classBeans);
		if (includeChildren) {
			ResolvedConceptReferenceList rcrl = getHierarchyRootConcepts(
					schemeName, csvt);
			ArrayList<ClassBean> rootConceptList = createClassBeanArray(rcrl,
					true);
			ArrayList<ClassBean> mergedConceptList = mergeListsEliminatingDuplicates(
					rootConceptList, classBeans);
			thingBean = createThingClassBean(mergedConceptList);
		}

		ClassBean simpleSubclassThingBean = createSimpleSubClassOnlyClassBean(thingBean);
		return simpleSubclassThingBean;
	}

	/**
	 * 
	 * @param ncboOntology
	 * @param conceptId
	 * @param light
	 * @return
	 * @throws Exception
	 */
	public ClassBean findPathToRoot(VNcboOntology ncboOntology,
			String conceptId, boolean light) throws Exception {
		String schemeName = getLexGridCodingSchemeName(ncboOntology);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);

		String[] hierarchyIDs = lbscm.getHierarchyIDs(schemeName, csvt);
		String hierarchyId = (hierarchyIDs.length > 0) ? hierarchyIDs[0] : null;
		for (String hierarchy : hierarchyIDs) {
			if (hierarchy.equalsIgnoreCase("IS_A")) {
				hierarchyId = hierarchy;
			}
		}
		AssociationList associations = lbscm.getHierarchyPathToRoot(schemeName,
				csvt, hierarchyId, conceptId, false,
				LexBIGServiceConvenienceMethods.HierarchyPathResolveOption.ALL,
				null);
		ClassBean conceptClass = findConceptIncludeChildCountNoRelations(
				ncboOntology, conceptId);
		boolean includeChildren = !light;
		addAssociationListInfoToClassBean(associations, conceptClass,
				ApplicationConstants.SUPER_CLASS, includeChildren);
		return conceptClass;
	}

	/**
	 * 
	 * @param ncboOntology
	 * @param conceptId
	 * @return
	 * @throws Exception
	 */
	public List<ClassBean> findParent(VNcboOntology ncboOntology,
			String conceptId) throws Exception {
		String schemeName = getLexGridCodingSchemeName(ncboOntology);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);
		AssociationList associations = getHierarchyLevelPrev(schemeName, csvt,
				conceptId);
		ClassBean conceptClass = findConceptIncludeChildCountNoRelations(
				ncboOntology, conceptId);
		ArrayList<ClassBean> classBeans = createClassBeanArray(associations,
				conceptClass, ApplicationConstants.SUPER_CLASS, false);

		return classBeans;
	}

	/**
	 * 
	 * @param ncboOntology
	 * @param conceptId
	 * @return
	 * @throws Exception
	 */
	public List<ClassBean> findChildren(VNcboOntology ncboOntology,
			String conceptId) throws Exception {
		String schemeName = getLexGridCodingSchemeName(ncboOntology);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);
		AssociationList associations = getHierarchyLevelNext(schemeName, csvt,
				conceptId);
		ClassBean conceptClass = findConceptIncludeChildCountNoRelations(
				ncboOntology, conceptId);
		ArrayList<ClassBean> classBeans = createClassBeanArray(associations,
				conceptClass, ApplicationConstants.SUB_CLASS, false);

		return classBeans;
	}

	public boolean refresh() throws Exception {
		WriteLockManager.instance().checkForRegistryUpdates();
		return true;
	}

	public int findConceptCount(List<VNcboOntology> ontologyVersions)
			throws Exception {
		int count = 0;

		for (VNcboOntology ontologyVersion : ontologyVersions) {
			String schemeName = getLexGridCodingSchemeName(ontologyVersion);
			CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyVersion);
			CodingScheme codingScheme = lbs.resolveCodingScheme(schemeName,
					csvt);
			count += codingScheme.getApproxNumConcepts();
		}
		return count;
	}

	/**
	 * Return a bare bone resolvedConceptReference with none of the details of
	 * the concept filled in.
	 * 
	 * @param ncboOntology
	 * @param conceptId
	 * @return
	 * @throws Exception
	 */
	private ResolvedConceptReference getLightResolvedConceptReference(
			VNcboOntology ncboOntology, String conceptId) throws Exception {
		String schemeName = getLexGridCodingSchemeName(ncboOntology);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);

		// Perform the query ...
		ConceptReferenceList crefs = ConvenienceMethods
				.createConceptReferenceList(new String[] { conceptId },
						schemeName);
		CodedNodeSet nodes = lbs.getCodingSchemeConcepts(schemeName, csvt)
				.restrictToCodes(crefs);
		ResolvedConceptReferenceList matches = nodes.resolveToList(null, null,
				null, null, false, -1);
		ResolvedConceptReference rcr = null;
		if (matches.getResolvedConceptReferenceCount() > 0) {
			rcr = matches.getResolvedConceptReference()[0];
		}
		return rcr;

	}

	/**
	 * Return the entityDescription of the conceptId
	 * 
	 * @param ncboOntology
	 * @param conceptId
	 * @return
	 */
	private EntityDescription getEntityDescription(VNcboOntology ncboOntology,
			String conceptId) {
		try {
			ResolvedConceptReference rcr = getLightResolvedConceptReference(
					ncboOntology, conceptId);
			return rcr.getEntityDescription();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Recursive call to reverse order of the given association, adding results
	 * to the given list.
	 * 
	 * @param ncboOntology
	 * @param assoc
	 * @param addTo
	 * @param codeToEntityDescriptionMap
	 * @return Position in the given association list for recursive adds.
	 */
	protected AssociationList reverseAssoc(VNcboOntology ncboOntology,
			Association assoc, AssociationList addTo,
			Map<String, EntityDescription> codeToEntityDescriptionMap) {
		String schemeName = getLexGridCodingSchemeName(ncboOntology);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);

		ConceptReference acRef = assoc.getAssociationReference();
		AssociatedConcept acFromRef = new AssociatedConcept();
		acFromRef.setCodingScheme(acRef.getCodingScheme());
		acFromRef.setConceptCode(acRef.getConceptCode());
		AssociationList acSources = new AssociationList();
		acFromRef.setIsNavigable(Boolean.TRUE);
		acFromRef.setSourceOf(acSources);

		// Use cached description if available (should be cached for all but
		// original root) ...
		if (codeToEntityDescriptionMap.containsKey(acRef.getConceptCode()))
			acFromRef.setEntityDescription(codeToEntityDescriptionMap.get(acRef
					.getConceptCode()));
		// Otherwise retrieve on demand ...
		else {
			EntityDescription ed = getEntityDescription(ncboOntology, acRef
					.getConceptCode());
			acFromRef.setEntityDescription(ed);
		}

		AssociatedConceptList acl = assoc.getAssociatedConcepts();
		for (AssociatedConcept ac : acl.getAssociatedConcept()) {
			// Create reverse association (same non-directional name)
			Association rAssoc = new Association();
			rAssoc.setAssociationName(assoc.getAssociationName());
			// On reverse, old associated concept is new reference point.
			ConceptReference ref = new ConceptReference();
			ref.setCodingScheme(ac.getCodingScheme());
			ref.setConceptCode(ac.getConceptCode());
			rAssoc.setAssociationReference(ref);
			// And old reference is new associated concept.
			AssociatedConceptList rAcl = new AssociatedConceptList();
			rAcl.addAssociatedConcept(acFromRef);
			rAssoc.setAssociatedConcepts(rAcl);
			// Set reverse directional name, if available.
			String dirName = assoc.getDirectionalName();
			if (dirName != null)
				try {
					rAssoc.setDirectionalName(lbscm.isForwardName(schemeName,
							csvt, dirName) ? lbscm.getAssociationReverseName(
							assoc.getAssociationName(), schemeName, csvt)
							: lbscm.getAssociationForwardName(assoc
									.getAssociationName(), schemeName, csvt));
				} catch (LBException e) {
				}

			// Save code desc for future reference when setting up
			// concept references in recursive calls ...
			codeToEntityDescriptionMap.put(ac.getConceptCode(), ac
					.getEntityDescription());

			AssociationList sourceOf = ac.getSourceOf();
			if (sourceOf != null)
				for (Association sourceAssoc : sourceOf.getAssociation()) {
					AssociationList pos = reverseAssoc(ncboOntology,
							sourceAssoc, addTo, codeToEntityDescriptionMap);
					pos.addAssociation(rAssoc);
				}
			else
				addTo.addAssociation(rAssoc);
		}
		return acSources;
	}

	/**
	 * A helper method that returns the ResolvedConceptReferenceList of root
	 * concepts.
	 * 
	 * @param schemeName
	 * @param csvt
	 * @return
	 * @throws Exception
	 */
	private ResolvedConceptReferenceList getHierarchyRootConcepts(
			String schemeName, CodingSchemeVersionOrTag csvt) throws Exception {
		// Iterate through all hierarchies ...
		String[] hierarchyIDs = lbscm.getHierarchyIDs(schemeName, csvt);
		String hierarchyId = (hierarchyIDs.length > 0) ? hierarchyIDs[0] : null;

		for (String hierarchy : hierarchyIDs) {
			if (hierarchy.equalsIgnoreCase("IS_A"))
				hierarchyId = hierarchy;
		}
		ResolvedConceptReferenceList rcrl = lbscm.getHierarchyRoots(schemeName,
				csvt, hierarchyId);
		return rcrl;
	}

	/**
	 * A helper method that returns the next level of the hierarchy of a
	 * conceptId
	 * 
	 * @param schemeName
	 * @param csvt
	 * @param conceptId
	 * @return
	 * @throws Exception
	 */
	private AssociationList getHierarchyLevelPrev(String schemeName,
			CodingSchemeVersionOrTag csvt, String conceptId) throws Exception {
		String[] hierarchyIDs = lbscm.getHierarchyIDs(schemeName, csvt);
		String hierarchyId = (hierarchyIDs.length > 0) ? hierarchyIDs[0] : null;
		for (String hierarchy : hierarchyIDs) {
			if (hierarchy.equalsIgnoreCase("IS_A")) {
				hierarchyId = hierarchy;
			}
		}
		AssociationList associations = lbscm.getHierarchyLevelPrev(schemeName,
				csvt, hierarchyId, conceptId, false, null);
		return associations;
	}

	/**
	 * A helper method that returns the previous level of the hierarchy of a
	 * conceptId
	 * 
	 * @param schemeName
	 * @param csvt
	 * @param conceptId
	 * @return
	 * @throws Exception
	 */
	private AssociationList getHierarchyLevelNext(String schemeName,
			CodingSchemeVersionOrTag csvt, String conceptId) throws Exception {
		String[] hierarchyIDs = lbscm.getHierarchyIDs(schemeName, csvt);
		String hierarchyId = (hierarchyIDs.length > 0) ? hierarchyIDs[0] : null;

		for (String hierarchy : hierarchyIDs) {
			if (hierarchy.equalsIgnoreCase("IS_A")) {
				hierarchyId = hierarchy;
			}
		}
		AssociationList associations = lbscm.getHierarchyLevelNext(schemeName,
				csvt, hierarchyId, conceptId, false, null);
		return associations;
	}

	/**
	 * 
	 * @param ncboOntology
	 * @param cr
	 * @return
	 */
	private ClassBean createClassBeanWithChildCount(VNcboOntology ncboOntology,
			ConceptReference cr, boolean includeChildren) {
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);
		ResolvedConceptReference rcr = new ResolvedConceptReference();
		rcr.setCodingScheme(cr.getCodingScheme());
		rcr.setConceptCode(cr.getConceptCode());
		rcr.setCodingSchemeVersion(csvt.getVersion());
		EntityDescription ed = getEntityDescription(ncboOntology, cr
				.getConceptCode());
		rcr.setEntityDescription(ed);
		return createClassBeanWithChildCount(rcr, includeChildren);
	}

	/**
	 * 
	 * @param rcr
	 * @return
	 */
	private ClassBean createClassBeanWithChildCount(
			ResolvedConceptReference rcr, boolean includeChildren) {
		ClassBean bean = createClassBean(rcr);
		// Add the children
		String schemeName = rcr.getCodingScheme();
		String version = rcr.getCodingSchemeVersion();
		String conceptId = rcr.getConceptCode();
		CodingSchemeVersionOrTag csvt = Constructors
				.createCodingSchemeVersionOrTagFromVersion(version);
		try {
			AssociationList childList = getHierarchyLevelNext(schemeName, csvt,
					conceptId);
			bean.addRelation(ApplicationConstants.CHILD_COUNT,
					getChildCount(childList));
			if (includeChildren) {
				addAssociationListInfoToClassBean(childList, bean,
						ApplicationConstants.SUB_CLASS, false);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bean;
	}

	/**
	 * This function creates a ClassBean from a resolvedConceptReference(rcr).
	 * The association information of the resolvedConceptReference is also added
	 * to the classbean
	 * 
	 * @param rcr
	 * @return
	 */
	private ClassBean createClassBean(ResolvedConceptReference rcr) {
		ClassBean bean = new ClassBean();
		bean.setId(rcr.getConceptCode());
		if (rcr.getEntityDescription() != null) {
			bean.setLabel(rcr.getEntityDescription().getContent());
		}
		Concept entry = rcr.getReferencedEntry();
		if (entry == null) {
			// bean.setLight(true);
		} else if (entry.getIsAnonymous() == null
				|| (entry.getIsAnonymous() != null && !entry.getIsAnonymous()
						.booleanValue())) {
			addConceptPropertyValueOld(entry, bean);

			if (StringUtils.isBlank(bean.getLabel())) {
				bean.setLabel(getPreferredPresentation(entry));
			}
		}
		addAssociationListInfoToClassBean(rcr.getSourceOf(), bean, null, false);
		addAssociationListInfoToClassBean(rcr.getTargetOf(), bean, null, false);
		return bean;
	}

	/**
	 * 
	 * @param list
	 * @return
	 */
	private ClassBean createThingClassBeanWithCount(
			ResolvedConceptReferenceList list) {
		ArrayList<ClassBean> classBeans = createClassBeanArray(list, true);
		return createThingClassBean(classBeans);
	}

	/**
	 * 
	 * @param classBeans
	 * @return
	 */
	private ClassBean createThingClassBean(ArrayList<ClassBean> classBeans) {
		ClassBean classBean = new ClassBean();
		classBean.setId("THING");
		classBean.setLabel("THING");
		classBean.addRelation(ApplicationConstants.SUB_CLASS, classBeans);
		classBean.addRelation(ApplicationConstants.CHILD_COUNT, classBeans
				.size());
		return classBean;
	}

	/**
	 * 
	 * @param list
	 * @param includeCount
	 * @return
	 */
	private ArrayList<ClassBean> createClassBeanArray(
			ResolvedConceptReferenceList list, boolean includeCount) {
		ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();
		Enumeration<ResolvedConceptReference> refEnum = list
				.enumerateResolvedConceptReference();
		ResolvedConceptReference ref = null;

		while (refEnum.hasMoreElements()) {
			ref = (ResolvedConceptReference) refEnum.nextElement();
			ClassBean bean;
			if (includeCount) {
				bean = createClassBeanWithChildCount(ref, false);
			} else {
				bean = createClassBean(ref);
			}
			classBeans.add(bean);
		}

		return classBeans;
	}

	/**
	 * 
	 * @param entry
	 * @return
	 */
	private String getPreferredPresentation(Concept entry) {
		Presentation[] presentations = entry.getPresentation();
		for (int i = 0; i < presentations.length; i++) {
			if (presentations[i].getIsPreferred().booleanValue())

				return presentations[i].getText().getContent();
		}
		return "";
	}

	/**
	 * 
	 * @param map
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	private static void addStringToHashMapsArrayList(
			HashMap<Object, Object> map, String key, String value) {
		List list = (List) map.get(key);
		if (list == null) {
			list = new ArrayList<Object>();
			map.put(key, list);
		}
		if (StringUtils.isNotBlank(value) && !list.contains(value)) {
			list.add(value);
		}
	}

	/**
	 * 
	 * @param list
	 * @param current_classBean
	 * @param hierarchy_relationName
	 * @param includeChildren
	 * @return
	 */
	private ArrayList<ClassBean> createClassBeanArray(AssociationList list,
			ClassBean current_classBean, String hierarchy_relationName,
			boolean includeChildren) {
		ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();
		addAssociationListInfoToClassBean(list, current_classBean,
				hierarchy_relationName, includeChildren);
		classBeans.add(current_classBean);
		return classBeans;
	}

	/**
	 * 
	 * @param list
	 * @param current_classBean
	 * @param hierarchy_relationName
	 * @param includeChildren
	 */
	private void addAssociationListInfoToClassBean(AssociationList list,
			ClassBean current_classBean, String hierarchy_relationName,
			boolean includeChildren) {
		if (list == null || current_classBean == null) {
			return;
		}
		Enumeration<Association> assocEnum = list.enumerateAssociation();
		Association association = null;
		while (assocEnum.hasMoreElements()) {
			association = (Association) assocEnum.nextElement();
			addAssociationInfoToClassBean(association, current_classBean,
					hierarchy_relationName, includeChildren);
		}

	}

	/**
	 * 
	 * @param list
	 * @return
	 */
	int getChildCount(AssociationList list) {
		int count = 0;
		if (list == null)
			return count;
		Enumeration<Association> assocEnum = list.enumerateAssociation();
		Association association = null;
		while (assocEnum.hasMoreElements()) {
			association = (Association) assocEnum.nextElement();
			AssociatedConceptList assocConceptList = association
					.getAssociatedConcepts();
			count += assocConceptList.getAssociatedConceptCount();
		}
		return count;

	}

	/**
	 * This function adds the association information to the current class bean.
	 * If a hierarchy_relation name is specified, then the association
	 * information is also added using the hierarchy_relationName. The hierarchy
	 * relationName is used to create subClass and superClass relations needed
	 * by BioPortal. For hierarchy realtionNames called subClass, we also add
	 * the count of children of the concept.
	 * 
	 * @param association
	 * @param current_classBean
	 * @param hierarchy_relationName
	 * @param includeChildren
	 */
	private void addAssociationInfoToClassBean(Association association,
			ClassBean current_classBean, String hierarchy_relationName,
			boolean includeChildren) {
		AssociatedConceptList assocConceptList = association
				.getAssociatedConcepts();
		ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();
		for (int i = 0; i < assocConceptList.getAssociatedConceptCount(); i++) {
			AssociatedConcept assocConcept = assocConceptList
					.getAssociatedConcept(i);
			if (assocConcept != null) {
				// We do not want to include children for the target concept
				if (includeChildren
						&& ApplicationConstants.SUB_CLASS
								.equals(hierarchy_relationName)
						&& (assocConcept.getSourceOf() == null || (assocConcept
								.getSourceOf() != null && assocConcept
								.getSourceOf().getAssociationCount() == 0))) {
					includeChildren = false;
				}
				ClassBean classBean = createClassBeanWithChildCount(
						assocConcept, includeChildren);
				classBeans.add(classBean);

				// Find and recurse printing for next batch ...
				AssociationList nextLevel = assocConcept.getSourceOf();
				if (nextLevel != null && nextLevel.getAssociationCount() != 0)
					for (int j = 0; j < nextLevel.getAssociationCount(); j++) {
						addAssociationInfoToClassBean(nextLevel
								.getAssociation(j), classBean,
								hierarchy_relationName, includeChildren);
					}

				// Find and recurse printing for previous batch ...
				AssociationList prevLevel = assocConcept.getTargetOf();
				if (prevLevel != null && prevLevel.getAssociationCount() != 0)
					for (int j = 0; j < prevLevel.getAssociationCount(); j++) {
						addAssociationInfoToClassBean(prevLevel
								.getAssociation(j), classBean,
								hierarchy_relationName, includeChildren);
					}
			}
		}

		String dirName = association.getDirectionalName();
		if (StringUtils.isBlank(dirName)) {
			dirName = "[R]" + association.getAssociationName();
		}
		current_classBean.addRelation(dirName, classBeans);
		addHierarchyRelationName(current_classBean, classBeans,
				hierarchy_relationName);
	}

	/**
	 * This function is written to satisfy the BioPortal need to be able to add
	 * subClass and superClass hierarchy names as well as a count of the
	 * children of a concept. A concept may have sets of children defined in the
	 * different associations, so we need to aggregate them together and deal
	 * with duplicates.
	 * 
	 * @param bean
	 * @param beanlist
	 * @param hierarchy_name
	 */
	@SuppressWarnings("unchecked")
	private void addHierarchyRelationName(ClassBean bean,
			ArrayList<ClassBean> beanlist, String hierarchy_name) {
		// If no hierarchy_name is provided, we assume that special BioPortal
		// relations do not have to be setup.
		if (StringUtils.isBlank(hierarchy_name)) {
			return;
		}

		Object value = bean.getRelations().get(hierarchy_name);

		if (value != null && value instanceof ArrayList) {
			// Ensure we do not add duplicates. We want to also replace the the
			// hierarchy_name class beans with the new beans from the list of
			// they match.
			// The reason for this is because the beanlist classbeans have
			// hierarchy information in them.
			// Note: the equals method of the AbstactClassBean has been
			// customized to same that if if the id and name matches
			// irrespective of the relations content,
			// the 2 beans are equal.
			// Set<ClassBean> set = new HashSet<ClassBean>();
			// List<ClassBean> list = (List<ClassBean>) value;
			//            
			// set.addAll(list);
			// set.removeAll((List<ClassBean>) beanlist);
			// set.addAll((List<ClassBean>) beanlist);
			// list.clear();
			// list.addAll(set);

			List<ClassBean> list = mergeListsEliminatingDuplicates(
					(ArrayList<ClassBean>) value, beanlist);
			bean.addRelation(hierarchy_name, list);

		} else {
			bean.addRelation(hierarchy_name, beanlist);
		}
	}

	/**
	 * This function is used to ensure there are no duplicates when the lists
	 * are merged. The entries in the addOverrideList takes precedence and gets
	 * added when there is a conflict. ClassBeans are considered equal if they
	 * have the same id and label.
	 * 
	 * @param baseList
	 * @param addOverideList
	 * @return
	 */
	private ArrayList<ClassBean> mergeListsEliminatingDuplicates(
			ArrayList<ClassBean> baseList, ArrayList<ClassBean> addOverideList) {
		ArrayList<ClassBean> srcList = new ArrayList<ClassBean>();
		srcList.addAll(baseList);
		// First remove elements that are in the override list
		for (ClassBean overideClass : addOverideList) {
			for (Iterator<ClassBean> it = srcList.iterator(); it.hasNext();) {
				ClassBean srcClass = it.next();
				// The id and labels match
				if (overideClass.getId() != null
						&& overideClass.getId().equals(srcClass.getId())
						&& overideClass.getLabel() != null
						&& overideClass.getLabel().equals(srcClass.getLabel())) {
					it.remove();
				}
			}
		}
		srcList.addAll(addOverideList);
		return srcList;
	}

	/**
	 * 
	 * @param entry
	 * @param bean
	 */
	private void addConceptPropertyValueOld(Concept entry, ClassBean bean) {
		HashMap<Object, Object> map = bean.getRelations();
		Presentation p = null;
		int count = entry.getPresentationCount();

		for (int i = 0; i < count; i++) {
			p = entry.getPresentation(i);
			if (!p.getIsPreferred().booleanValue()) {
				if (StringUtils.isNotBlank(p.getDegreeOfFidelity())) {
					String key = p.getDegreeOfFidelity() + " SYNONYM";
					addStringToHashMapsArrayList(map, key, p.getText()
							.getContent());
				} else if (StringUtils.isNotBlank(p.getRepresentationalForm())) {
					addStringToHashMapsArrayList(map, p
							.getRepresentationalForm(), p.getText()
							.getContent());
				} else {
					addStringToHashMapsArrayList(map, "SYNONYM", p.getText()
							.getContent());
				}
			}
		}

		// handle comment
		Comment c = null;
		count = entry.getCommentCount();
		for (int i = 0; i < count; i++) {
			c = entry.getComment(i);
			addStringToHashMapsArrayList(map, "Comment", c.getText()
					.getContent());
		}

		// handle definitions
		Definition d = null;
		count = entry.getDefinitionCount();
		for (int i = 0; i < count; i++) {
			d = entry.getDefinition(i);
			addStringToHashMapsArrayList(map, "Definition", d.getText()
					.getContent());
		}

		// handle concept properties
		ConceptProperty prop = null;
		count = entry.getConceptPropertyCount();
		for (int i = 0; i < count; i++) {
			prop = entry.getConceptProperty(i);
			String key = prop.getPropertyName();
			if (StringUtils.isNotBlank(key)) {
				addStringToHashMapsArrayList(map, key, prop.getText()
						.getContent());
			}
		}

	}

	@SuppressWarnings("unchecked")
	private ClassBean createSimpleSubClassOnlyClassBean(ClassBean classBean) {
		ClassBean cb = new ClassBean();
		cb.setId(classBean.getId());
		cb.setLabel(classBean.getLabel());
		Integer childCount = (Integer) classBean.getRelations().get(
				ApplicationConstants.CHILD_COUNT);
		if (childCount != null) {
			cb.addRelation(ApplicationConstants.CHILD_COUNT, childCount);
		}

		if (classBean.getRelations()
				.containsKey(ApplicationConstants.SUB_CLASS)) {
			Object subclass_obj = classBean.getRelations().get(
					ApplicationConstants.SUB_CLASS);
			if (subclass_obj != null && subclass_obj instanceof List) {
				List<ClassBean> subclasses = (List<ClassBean>) subclass_obj;
				List<ClassBean> newSubClasses = new ArrayList<ClassBean>();
				for (ClassBean subclass : subclasses) {
					ClassBean newSubClass = createSimpleSubClassOnlyClassBean(subclass);
					newSubClasses.add(newSubClass);
				}
				cb.addRelation(ApplicationConstants.SUB_CLASS, newSubClasses);
			} else if (subclass_obj != null
					&& subclass_obj instanceof ClassBean) {
				ClassBean subclass = (ClassBean) subclass_obj;
				ClassBean newSubClass = createSimpleSubClassOnlyClassBean(subclass);
				cb.addRelation(ApplicationConstants.SUB_CLASS, newSubClass);
			}
		}

		return cb;
	}

}
