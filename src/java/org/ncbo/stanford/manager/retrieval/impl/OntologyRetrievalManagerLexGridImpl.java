package org.ncbo.stanford.manager.retrieval.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.Impl.dataAccess.WriteLockManager;
import org.LexGrid.LexBIG.Impl.helpers.CountConceptReference;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.ActiveOption;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.ConvenienceMethods;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.concepts.Comment;
import org.LexGrid.concepts.Concept;
import org.LexGrid.concepts.Definition;
import org.LexGrid.concepts.Presentation;
import org.LexGrid.naming.SupportedHierarchy;
import org.LexGrid.naming.SupportedProperty;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.concept.ClassBeanResultListBean;
import org.ncbo.stanford.bean.concept.InstanceBean;
import org.ncbo.stanford.enumeration.ConceptTypeEnum;
import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.exception.OntologyVersionNotFoundException;
import org.ncbo.stanford.manager.AbstractOntologyManagerLexGrid;
import org.ncbo.stanford.manager.retrieval.OntologyRetrievalManager;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.paginator.Paginator;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.util.paginator.impl.PaginatorImpl;

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

	private static final Log log = LogFactory
			.getLog(OntologyRetrievalManagerLexGridImpl.class);

	private Integer allConceptsMaxPageSize;

	private static final String ROOT_CLASS_ID = "THING";

	// mdorf: hack for now to remove certain MSH relations
	List<String> relationsToFilter = new ArrayList<String>(Arrays.asList("QB",
			"QA", "NH", "SIB", "AQ"));

	public OntologyRetrievalManagerLexGridImpl() throws Exception {
		lbs = LexBIGServiceImpl.defaultInstance();
		lbscm = (LexBIGServiceConvenienceMethods) lbs
				.getGenericExtension("LexBIGServiceConvenienceMethods");
	}

	public List<String> findProperties(OntologyBean ontologyBean)
			throws Exception {
		String urnAndVersion = ontologyBean.getCodingScheme();
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
	public ClassBean findRootConcept(OntologyBean ontologyBean, boolean light)
			throws Exception {
		String schemeName = getLexGridCodingSchemeName(ontologyBean);

		if (StringUtils.isBlank(schemeName)) {
			log
					.warn("Can not process request when the codingSchemeURI is blank");
			return null;
		}

		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyBean);
		ResolvedConceptReferenceList rcrl = getHierarchyRootConcepts(
				schemeName, csvt, light);

		return createThingClassBeanWithCount(ontologyBean, rcrl);
	}

	/**
	 * Find concept based on the parameters passed. Makes use of the
	 * CodedNodeGraph of LexBIG to implement
	 * 
	 * @param ontologyBean
	 * @param conceptId
	 * @param light
	 * @param noRelations
	 * @return
	 * @throws Exception
	 */
	public ClassBean findConcept(OntologyBean ontologyBean, String conceptId,
			boolean light, boolean noRelations) throws Exception {
		ClassBean concept = null;

		if (noRelations) {
			concept = findConceptNoRelations(ontologyBean, conceptId);
		} else if (light) {
			concept = findConceptLight(ontologyBean, conceptId);
		} else {
			concept = findConcept(ontologyBean, conceptId);
		}

		return concept;
	}

	private ClassBean findConceptNoRelations(OntologyBean ontologyBean,
			String conceptId) throws Exception {
		ClassBean classBean = null;
		String schemeName = getLexGridCodingSchemeName(ontologyBean);

		if (StringUtils.isBlank(schemeName)) {
			log
					.warn("Can not process request when the codingSchemeURI is blank");
		} else if (StringUtils.isBlank(conceptId)) {
			log.warn("Can not process request when the conceptId is blank");
		} else {
			CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyBean);
			// Perform the query ...
			ConceptReferenceList crefs = ConvenienceMethods
					.createConceptReferenceList(new String[] { conceptId },
							schemeName);
			ResolvedConceptReferenceList matches = lbs.getCodingSchemeConcepts(
					schemeName, csvt).restrictToCodes(crefs).resolveToList(
					null, null, null, 1);

			// Analyze the result ...
			if (matches.getResolvedConceptReferenceCount() > 0) {
				ResolvedConceptReference ref = (ResolvedConceptReference) matches
						.enumerateResolvedConceptReference().nextElement();
				classBean = createClassBean(ontologyBean, ref, false);
			}
		}

		return classBean;
	}

	/**
	 * Find just the concept with only the subClass relation populated. Makes
	 * use of the CodedNodeSet and the hierarchy API of LexBIG to implement
	 * 
	 * @param ontologyBean
	 * @param conceptId
	 * @return
	 * @throws Exception
	 */
	public ClassBean findConceptLight(OntologyBean ontologyBean,
			String conceptId) throws Exception {
		ClassBean classBean = null;
		String schemeName = getLexGridCodingSchemeName(ontologyBean);

		if (StringUtils.isBlank(schemeName)) {
			log
					.warn("Can not process request when the codingSchemeURI is blank");
		} else if (StringUtils.isBlank(conceptId)) {
			log.warn("Can not process request when the conceptId is blank");
		} else {
			CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyBean);
			// Perform the query ...
			ConceptReferenceList crefs = ConvenienceMethods
					.createConceptReferenceList(new String[] { conceptId },
							schemeName);
			ResolvedConceptReferenceList matches = lbs.getCodingSchemeConcepts(
					schemeName, csvt).restrictToCodes(crefs).resolveToList(
					null, null, null, 1);

			// Analyze the result ...
			if (matches.getResolvedConceptReferenceCount() > 0) {
				ResolvedConceptReference rcr = (ResolvedConceptReference) matches
						.enumerateResolvedConceptReference().nextElement();
				classBean = createClassBeanWithChildCount(ontologyBean, rcr,
						true);
				classBean = createSimpleSubClassOnlyClassBean(classBean, false);
			}
		}

		return classBean;
	}

	// TODO: need to implement
	public InstanceBean findInstanceById(OntologyBean ontologyBean,
			String instanceId) throws Exception {
		return null;
	}

	public Page<InstanceBean> findInstancesByConceptId(
			OntologyBean ontologyBean, String instanceId, Integer pageSize,
			Integer pageNum) throws Exception {
		return null;
	}

	/**
	 * Find just the concept with all the relations. Makes use of the
	 * CodedNodeGraph of LexBIG to implement
	 * 
	 * @param ontologyBean
	 * @param conceptId
	 * @return
	 * @throws Exception
	 */
	private ClassBean findConcept(OntologyBean ontologyBean, String conceptId)
			throws Exception {

		ClassBean classBean = null;
		String schemeName = getLexGridCodingSchemeName(ontologyBean);

		if (StringUtils.isBlank(schemeName)) {
			log
					.warn("Can not process request when the codingSchemeURI is blank");
		} else if (StringUtils.isBlank(conceptId)) {
			log.warn("Can not process request when the conceptId is blank");
		} else {
			CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyBean);
			CodedNodeGraph cng = lbs.getNodeGraph(schemeName, csvt, null);
			addFilterRestrictionToCNG(cng, schemeName, csvt);
			ResolvedConceptReferenceList matches = cng
					.resolveAsList(ConvenienceMethods.createConceptReference(
							conceptId, schemeName), true, true, 0, 1, null,
							null, null, -1);

			// Analyze the result ...
			if (matches.getResolvedConceptReferenceCount() > 0) {
				ResolvedConceptReference ref = (ResolvedConceptReference) matches
						.enumerateResolvedConceptReference().nextElement();
				classBean = createClassBean(ontologyBean, ref, true);
				addSubClassRelationAndCountToClassBean(schemeName, csvt,
						classBean);
				addSuperClassRelationToClassBean(schemeName, csvt, classBean);
				// If this is a UMLS ontology, Natasha wanted the hierarchy
				// relations
				// removed. The subClass relation would hold the same info.
				if (ontologyBean.getFormat().equalsIgnoreCase(
						ApplicationConstants.FORMAT_UMLS_RRF)) {
					List<String> umlsFilterList = getListOfSubClassDirectionalName(
							schemeName, csvt);
					for (String relationToFilter : umlsFilterList) {
						classBean.removeRelation(relationToFilter);
					}
				}

			}
		}

		return classBean;
	}

	private void addFilterRestrictionToCNG(CodedNodeGraph cng,
			String schemeName, CodingSchemeVersionOrTag csvt) throws Exception {
		String assocNames[] = lbscm
				.getAssociationForwardNames(schemeName, csvt);
		List<String> assocNameList = new ArrayList<String>(Arrays
				.asList(assocNames));
		if (assocNameList.removeAll(relationsToFilter)) {
			String[] assocRestrictions = (String[]) assocNameList
					.toArray(new String[assocNameList.size()]);
			cng.restrictToAssociations(Constructors
					.createNameAndValueList(assocRestrictions), null);
		}
	}

	/**
	 * Find just the concept without the relations. Makes use of the
	 * CodedNodeSet of LexBIG to implement
	 * 
	 * @param ontologyBean
	 * @param conceptId
	 * @return
	 * @throws Exception
	 */
	private ClassBean findConceptIncludeChildCountNoRelations(
			OntologyBean ontologyBean, String conceptId) throws Exception {
		String schemeName = getLexGridCodingSchemeName(ontologyBean);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyBean);
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
			return createClassBeanWithChildCount(ontologyBean, rcr, false);
		}

		return null;
	}

	/**
	 * Find all concepts for a given ontology. The pageNum and pageSize
	 * parameters allow paginating over concepts for better performance
	 * 
	 * @param ontologyBean
	 * @param conceptId
	 * @param pageSize
	 *            - the number of results to return
	 * @param pageNum
	 *            - the results offset
	 * @return
	 * @throws Exception
	 */
	public Page<ClassBean> findAllConcepts(OntologyBean ontologyBean,
			Integer pageSize, Integer pageNum) throws Exception {
		String schemeName = getLexGridCodingSchemeName(ontologyBean);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyBean);

		try {
			ResolvedConceptReferencesIterator iterator = lbs
					.getCodingSchemeConcepts(schemeName, csvt).resolve(null,
							null, null, null, false);
			ClassBeanResultListBean pageConcepts = new ClassBeanResultListBean(
					0);
			int totalResults = iterator.numberRemaining();

			if (pageSize == null || pageSize <= 0) {
				pageSize = (totalResults <= allConceptsMaxPageSize) ? totalResults
						: allConceptsMaxPageSize;
			} else if (pageSize > allConceptsMaxPageSize) {
				pageSize = allConceptsMaxPageSize;
			}

			Paginator<ClassBean> p = new PaginatorImpl<ClassBean>(pageConcepts,
					pageSize, totalResults);

			if (pageNum == null || pageNum <= 1) {
				pageNum = 1;
			} else {
				int numPages = p.getNumPages();
				pageNum = (pageNum > numPages) ? numPages : pageNum;
			}

			int fromIndex = pageNum * pageSize - pageSize;
			int toIndex = (fromIndex + pageSize > totalResults) ? totalResults
					: fromIndex + pageSize;

			ResolvedConceptReferenceList rcrl = iterator
					.get(fromIndex, toIndex);

			for (Iterator<ResolvedConceptReference> itr = rcrl
					.iterateResolvedConceptReference(); itr.hasNext();) {
				ResolvedConceptReference rcr = itr.next();
				pageConcepts
						.add(findConcept(ontologyBean, rcr.getConceptCode()));
			}

			return p.getCurrentPage(pageNum);
		} catch (LBParameterException ex) {
			return null;
		}
	}

	public Iterator<ClassBean> listAllClasses(final OntologyBean ob)
			throws Exception {
		// Convert ontology (BP) to coding scheme (LexBIG)
		final String schemeName = getLexGridCodingSchemeName(ob);
		final CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ob);

		if (StringUtils.isBlank(schemeName) || (csvt == null)) {
			throw new OntologyVersionNotFoundException(
					"Could not resolve ontology in LexBIG service: " + ob);
		}

		// Fetch all concepts -- delay conversion to ClassBean
		CodedNodeSet cns = lbs.getCodingSchemeConcepts(schemeName, csvt);
		final ResolvedConceptReferencesIterator rcrIt = cns.resolve(null, null,
				null, null, false);

		return new Iterator<ClassBean>() {
			public boolean hasNext() {
				try {
					return rcrIt.hasNext();
				} catch (LBResourceUnavailableException e) {
					throw new BPRuntimeException(
							"Problem encountered in LexBIG Service", e);
				}
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

			public ClassBean next() {
				try {
					// Get a basic version of the LexBIG concept
					ResolvedConceptReference rcr = rcrIt.next(); // throws
					// LBException
					// Get a fleshed out ClassBean
					// This seems like an unfortunate hack -- I already hit the
					// db to get the RCR object,
					// now I'm going back and fetching it all over again. But I
					// haven't yet figured
					// out how to get the full set of relations into the
					// ClassBean from the RCR I
					// have here. Perhaps 'getCodingSchemeConcepts' doesn't
					// return a graph, and one
					// needs a graph in order to resolve the relations? --TL
					return findConcept(ob, rcr.getConceptCode());
				} catch (Exception e) {
					// Note neither subtype of LBException that could get us
					// here is specific enough
					// to justify throwing a NoSuchElementException.
					throw new BPRuntimeException(
							"Problem encountered in LexBIG Service", e);
				}
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ncbo.stanford.manager.OntologyRetrievalManager#findPathFromRoot(org
	 * .ncbo.stanford.domain.custom.entity.VNcboOntology, java.lang.String,
	 * boolean)
	 */
	public ClassBean findPathFromRoot(OntologyBean ontologyBean,
			String conceptId, boolean light) throws Exception {
		String schemeName = getLexGridCodingSchemeName(ontologyBean);

		if (StringUtils.isBlank(schemeName)) {
			log.warn("Can not proceed when the codingSchemeURI is blank");
			return null;
		}

		if (StringUtils.isBlank(conceptId)) {
			log.warn("Can not process request when the conceptId is blank");
			return null;
		}

		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyBean);
		String hierarchyId = getDefaultHierarchyId(schemeName, csvt);
		AssociationList pathToRoot = lbscm.getHierarchyPathToRoot(schemeName,
				csvt, hierarchyId, conceptId, false,
				LexBIGServiceConvenienceMethods.HierarchyPathResolveOption.ONE,
				null);

		// Recursively reverse the chain ...
		AssociationList pathFromRoot = new AssociationList();
		HashMap<String, EntityDescription> codeToEntityDescriptionMap = new HashMap<String, EntityDescription>();

		for (int i = pathToRoot.getAssociationCount() - 1; i >= 0; i--) {
			reverseAssoc(ontologyBean, pathToRoot.getAssociation(i),
					pathFromRoot, codeToEntityDescriptionMap);
		}
		// log.debug(ObjectToString.toString(pathFromRoot));
		ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();
		boolean includeChildren = !light;

		for (int i = 0; i < pathFromRoot.getAssociationCount(); i++) {
			Association association = pathFromRoot.getAssociation(i);
			ConceptReference cr = association.getAssociationReference();
			ClassBean classBean = createClassBeanWithChildCount(ontologyBean,
					cr, includeChildren);

			classBeans.add(classBean);
			addAssociationListInfoToClassBean(ontologyBean, pathFromRoot,
					classBean, ApplicationConstants.SUB_CLASS, includeChildren);

		}

		// The classBeans list could be empty either because there is no path
		// from root to the conceptId or the conceptId is the root itself.
		if (classBeans.isEmpty()) {
			// Check if the conceptId is the root itself. If so, we want to add
			// the root concept to the classBeans list
			ResolvedConceptReferenceList hier_rcrl = getHierarchyRootConcepts(
					schemeName, csvt, false);
			Enumeration<ResolvedConceptReference> refEnum = hier_rcrl
					.enumerateResolvedConceptReference();
			ResolvedConceptReference ref = null;

			while (refEnum.hasMoreElements()) {
				ref = (ResolvedConceptReference) refEnum.nextElement();
				ClassBean bean;

				if (ref.getConceptCode().equals(conceptId)) {
					// We need a lightweight classBean....
					ResolvedConceptReference rcr = getLightResolvedConceptReference(
							ontologyBean, conceptId);
					bean = createClassBeanWithChildCount(ontologyBean, rcr,
							false);
					classBeans.add(bean);
				}
			}
		}

		ClassBean thingBean = createThingClassBean(classBeans);

		if (includeChildren) {
			ResolvedConceptReferenceList rcrl = getHierarchyRootConcepts(
					schemeName, csvt, false);
			ArrayList<ClassBean> rootConceptList = createClassBeanArray(
					ontologyBean, rcrl, true);
			ArrayList<ClassBean> mergedConceptList = mergeListsEliminatingDuplicates(
					rootConceptList, classBeans);
			thingBean = createThingClassBean(mergedConceptList);
		}

		ClassBean simpleSubclassThingBean = createSimpleSubClassOnlyClassBean(
				thingBean, includeChildren);

		return simpleSubclassThingBean;
	}

	/**
	 * 
	 * @param ontologyBean
	 * @param conceptId
	 * @param light
	 * @return
	 * @throws Exception
	 */
	public ClassBean findPathToRoot(OntologyBean ontologyBean,
			String conceptId, boolean light) throws Exception {
		String schemeName = getLexGridCodingSchemeName(ontologyBean);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyBean);

		String hierarchyId = getDefaultHierarchyId(schemeName, csvt);
		AssociationList associations = lbscm.getHierarchyPathToRoot(schemeName,
				csvt, hierarchyId, conceptId, false,
				LexBIGServiceConvenienceMethods.HierarchyPathResolveOption.ALL,
				null);
		ClassBean conceptClass = findConceptIncludeChildCountNoRelations(
				ontologyBean, conceptId);
		boolean includeChildren = !light;
		addAssociationListInfoToClassBean(ontologyBean, associations,
				conceptClass, ApplicationConstants.SUPER_CLASS, includeChildren);

		return conceptClass;
	}

	/**
	 * 
	 * @param ontologyBean
	 * @param conceptId
	 * @return
	 * @throws Exception
	 */
	public List<ClassBean> findParent(OntologyBean ontologyBean,
			String conceptId) throws Exception {
		String schemeName = getLexGridCodingSchemeName(ontologyBean);

		if (StringUtils.isBlank(schemeName)) {
			log
					.warn("Can not process request when the codingSchemeURI is blank");
			return null;
		}

		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyBean);
		AssociationList associations = getHierarchyLevelPrev(schemeName, csvt,
				conceptId);
		ClassBean conceptClass = findConceptIncludeChildCountNoRelations(
				ontologyBean, conceptId);
		ArrayList<ClassBean> classBeans = createClassBeanArray(ontologyBean,
				associations, conceptClass, ApplicationConstants.SUPER_CLASS,
				false);

		return classBeans;
	}

	/**
	 * 
	 * @param ontologyBean
	 * @param conceptId
	 * @return
	 * @throws Exception
	 */
	public List<ClassBean> findChildren(OntologyBean ontologyBean,
			String conceptId) throws Exception {
		String schemeName = getLexGridCodingSchemeName(ontologyBean);

		if (StringUtils.isBlank(schemeName)) {
			log
					.warn("Can not process request when the codingSchemeURI is blank");
			return null;
		}

		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyBean);
		AssociationList associations = getHierarchyLevelNext(schemeName, csvt,
				conceptId);
		ClassBean conceptClass = findConceptIncludeChildCountNoRelations(
				ontologyBean, conceptId);
		ArrayList<ClassBean> classBeans = createClassBeanArray(ontologyBean,
				associations, conceptClass, ApplicationConstants.SUB_CLASS,
				false);

		return classBeans;
	}

	public boolean hasParent(OntologyBean ontologyBean, String childConceptId,
			String parentConceptId) throws Exception {
		boolean isRelated = false;
		String schemeName = getLexGridCodingSchemeName(ontologyBean);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyBean);
		String hierarchyId = getDefaultHierarchyId(schemeName, csvt);
		SupportedHierarchy[] supHiers = lbscm.getSupportedHierarchies(
				schemeName, csvt, hierarchyId);
		SupportedHierarchy sh = supHiers[0];

		for (String associationName : sh.getAssociationNames()) {
			// We need to be careful about the direction flag
			CodedNodeGraph cng = lbs.getNodeGraph(schemeName, csvt, null);
			boolean related = false;

			if (sh.isIsForwardNavigable()) {
				related = cng.areCodesRelated(ConvenienceMethods
						.createNameAndValue(associationName, schemeName),
						ConvenienceMethods.createConceptReference(
								parentConceptId, schemeName),
						ConvenienceMethods.createConceptReference(
								childConceptId, schemeName), false);
			} else {
				related = cng.areCodesRelated(ConvenienceMethods
						.createNameAndValue(associationName, schemeName),
						ConvenienceMethods.createConceptReference(
								childConceptId, schemeName), ConvenienceMethods
								.createConceptReference(parentConceptId,
										schemeName), false);
			}

			if (related) {
				return related;
			}
		}

		return isRelated;
	}

	public boolean refresh() throws Exception {
		WriteLockManager.instance().checkForRegistryUpdates();
		return true;
	}

	public int findConceptCount(List<OntologyBean> ontologyVersions)
			throws Exception {
		int count = 0;

		for (OntologyBean ontologyVersion : ontologyVersions) {
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
	 * @param ontologyBean
	 * @param conceptId
	 * @return
	 * @throws Exception
	 */
	private ResolvedConceptReference getLightResolvedConceptReference(
			OntologyBean ontologyBean, String conceptId) throws Exception {
		String schemeName = getLexGridCodingSchemeName(ontologyBean);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyBean);

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
	 * Return resolvedConceptReference without the associations
	 * 
	 * @param AssociatedConcept
	 * @return
	 * @throws Exception
	 */

	private ResolvedConceptReference getResolvedConceptReferenceWithoutRelations(
			AssociatedConcept ac) {
		ResolvedConceptReference rcr = new ResolvedConceptReference();
		rcr.setReferencedEntry(ac.getReferencedEntry());
		rcr.setCode(ac.getCode());
		rcr.setCodeNamespace(ac.getCodeNamespace());
		rcr.setCodingSchemeName(ac.getCodingSchemeName());
		rcr.setCodingSchemeURI(ac.getCodingSchemeURI());
		rcr.setCodingSchemeVersion(ac.getCodingSchemeVersion());
		rcr.setConceptCode(ac.getConceptCode());
		rcr.setEntity(ac.getEntity());
		rcr.setEntityDescription(ac.getEntityDescription());

		return rcr;
	}

	/**
	 * Return the entityDescription of the conceptId
	 * 
	 * @param ontologyBean
	 * @param conceptId
	 * @return
	 */
	private EntityDescription getEntityDescription(OntologyBean ontologyBean,
			String conceptId) {
		try {
			ResolvedConceptReference rcr = getLightResolvedConceptReference(
					ontologyBean, conceptId);

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
	 * @param ontologyBean
	 * @param assoc
	 * @param addTo
	 * @param codeToEntityDescriptionMap
	 * @return Position in the given association list for recursive adds.
	 */
	protected AssociationList reverseAssoc(OntologyBean ontologyBean,
			Association assoc, AssociationList addTo,
			Map<String, EntityDescription> codeToEntityDescriptionMap) {
		String schemeName = getLexGridCodingSchemeName(ontologyBean);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyBean);

		ConceptReference acRef = assoc.getAssociationReference();
		AssociatedConcept acFromRef = new AssociatedConcept();
		acFromRef.setCodingSchemeName(acRef.getCodingSchemeName());
		acFromRef.setCodeNamespace(acRef.getCodeNamespace());
		acFromRef.setConceptCode(acRef.getConceptCode());
		acFromRef.setCodingSchemeVersion(csvt.getVersion());
		AssociationList acSources = new AssociationList();
		acFromRef.setIsNavigable(Boolean.TRUE);
		acFromRef.setSourceOf(acSources);

		// Use cached description if available (should be cached for all but
		// original root) ...
		if (codeToEntityDescriptionMap.containsKey(acRef.getConceptCode())) {
			acFromRef.setEntityDescription(codeToEntityDescriptionMap.get(acRef
					.getConceptCode()));
			// Otherwise retrieve on demand ...
		} else {
			EntityDescription ed = getEntityDescription(ontologyBean, acRef
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
			ref.setCodingSchemeName(ac.getCodingSchemeName());
			ref.setConceptCode(ac.getConceptCode());
			rAssoc.setAssociationReference(ref);
			// And old reference is new associated concept.
			AssociatedConceptList rAcl = new AssociatedConceptList();
			rAcl.addAssociatedConcept(acFromRef);
			rAssoc.setAssociatedConcepts(rAcl);
			// Set reverse directional name, if available.
			String dirName = assoc.getDirectionalName();

			if (dirName != null) {
				try {
					rAssoc.setDirectionalName(lbscm.isForwardName(schemeName,
							csvt, dirName) ? lbscm.getAssociationReverseName(
							assoc.getAssociationName(), schemeName, csvt)
							: lbscm.getAssociationForwardName(assoc
									.getAssociationName(), schemeName, csvt));
				} catch (LBException e) {
				}
			}

			// Save code desc for future reference when setting up
			// concept references in recursive calls ...
			codeToEntityDescriptionMap.put(ac.getConceptCode(), ac
					.getEntityDescription());

			AssociationList sourceOf = ac.getSourceOf();

			if (sourceOf != null) {
				for (Association sourceAssoc : sourceOf.getAssociation()) {
					AssociationList pos = reverseAssoc(ontologyBean,
							sourceAssoc, addTo, codeToEntityDescriptionMap);
					pos.addAssociation(rAssoc);
				}
			} else {
				addTo.addAssociation(rAssoc);
			}
		}

		return acSources;
	}

	/**
	 * 
	 * @param ontologyBean
	 * @param cr
	 * @return
	 */
	private ClassBean createClassBeanWithChildCount(OntologyBean ontologyBean,
			ConceptReference cr, boolean includeChildren) {

		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyBean);
		ResolvedConceptReference rcr = new ResolvedConceptReference();
		rcr.setCodingSchemeName(cr.getCodingSchemeName());
		rcr.setConceptCode(cr.getConceptCode());
		rcr.setCodingSchemeVersion(csvt.getVersion());
		EntityDescription ed = getEntityDescription(ontologyBean, cr
				.getConceptCode());
		rcr.setEntityDescription(ed);

		try {
			rcr = getLightResolvedConceptReference(ontologyBean, cr
					.getConceptCode());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return createClassBeanWithChildCount(ontologyBean, rcr, includeChildren);
	}

	/**
	 * 
	 * @param rcr
	 * @return
	 */
	private ClassBean createClassBeanWithChildCount(OntologyBean ontologyBean,
			ResolvedConceptReference rcr, boolean includeChildren) {
		ClassBean bean = createClassBean(ontologyBean, rcr, true);
		// log.debug("createClassBeanWithChildCount for conceptCode "+
		// rcr.getConceptCode());
		// Add the children
		String schemeName = rcr.getCodingSchemeName();
		String version = rcr.getCodingSchemeVersion();
		String conceptId = rcr.getConceptCode();
		CodingSchemeVersionOrTag csvt = Constructors
				.createCodingSchemeVersionOrTagFromVersion(version);
		try {
			if (includeChildren) {
				AssociationList childList = getHierarchyLevelNext(schemeName,
						csvt, conceptId);
				bean.addRelation(ApplicationConstants.CHILD_COUNT,
						getChildCount(childList));

				addAssociationListInfoToClassBean(ontologyBean, childList,
						bean, ApplicationConstants.SUB_CLASS, false);
			} else {
				String hierarchyId = getDefaultHierarchyId(schemeName, csvt);
				int count = lbscm.getHierarchyLevelNextCount(schemeName, csvt,
						hierarchyId, rcr);
				bean.addRelation(ApplicationConstants.CHILD_COUNT, count);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// log.debug("createClassBeanWithChildCount returned ");
		return bean;
	}

	/**
	 * 
	 * @param rcr
	 * @return
	 */
	private ClassBean createClassBeanWithChildCount(OntologyBean ontologyBean,
			ResolvedConceptReference rcr, HashMap<String, Integer> countMap,
			boolean includeChildren) {
		ClassBean bean = createClassBean(ontologyBean, rcr, true);
		// log.debug("createClassBeanWithChildCount for conceptCode "+
		// rcr.getConceptCode());
		// Add the children
		String schemeName = rcr.getCodingSchemeName();
		String version = rcr.getCodingSchemeVersion();
		String conceptId = rcr.getConceptCode();
		CodingSchemeVersionOrTag csvt = Constructors
				.createCodingSchemeVersionOrTagFromVersion(version);
		try {
			if (includeChildren) {
				AssociationList childList = getHierarchyLevelNext(schemeName,
						csvt, conceptId);
				bean.addRelation(ApplicationConstants.CHILD_COUNT,
						getChildCount(childList));

				addAssociationListInfoToClassBean(ontologyBean, childList,
						bean, ApplicationConstants.SUB_CLASS, false);
			} else {
				// Check for the count in the hashmap first
				String key = getKey(rcr);
				Integer countInt = countMap.get(key);
				int count = 0;

				if (countInt == null) {
					// Did not find the count in the hashmap....One case where
					// this happens is
					// when the namespace of the concept reference is null.
					// log.debug("Count not found in map for key=" + key);
					String hierarchyId = getDefaultHierarchyId(schemeName, csvt);
					count = lbscm.getHierarchyLevelNextCount(schemeName, csvt,
							hierarchyId, rcr);
				} else {
					count = countInt;
				}

				bean.addRelation(ApplicationConstants.CHILD_COUNT, count);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// log.debug("createClassBeanWithChildCount returned ");
		return bean;
	}

	private HashMap<String, Integer> getHashMapWithChildCount(
			ResolvedConceptReferenceList rcrl) {
		HashMap<String, Integer> countMap = new HashMap<String, Integer>();

		if (rcrl == null || rcrl.getResolvedConceptReferenceCount() == 0) {
			return countMap;
		}
		// long startTime = System.currentTimeMillis();
		try {
			ResolvedConceptReference[] rcrs = rcrl
					.getResolvedConceptReference();
			ResolvedConceptReference rcr = rcrs[0];
			String schemeName = rcr.getCodingSchemeName();
			String version = rcr.getCodingSchemeVersion();
			CodingSchemeVersionOrTag csvt = Constructors
					.createCodingSchemeVersionOrTagFromVersion(version);
			String hierarchyId = getDefaultHierarchyId(schemeName, csvt);

			ConceptReferenceList crl = new ConceptReferenceList();
			crl.setConceptReference(rcrs);
			ConceptReferenceList countList = lbscm.getHierarchyLevelNextCount(
					schemeName, csvt, hierarchyId, crl);

			for (ConceptReference cr : countList.getConceptReference()) {
				CountConceptReference ccr = (CountConceptReference) cr;
				countMap.put(getKey(cr), ccr.getChildCount());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// log.debug("Time to getHashMapWithChildCount=" +
		// (System.currentTimeMillis() - startTime));
		return countMap;
	}

	private String getKey(ConceptReference cr) {
		String key = cr.getCodeNamespace() + "[:]" + cr.getConceptCode();
		return key;
	}

	/**
	 * This function creates a ClassBean from a resolvedConceptReference(rcr).
	 * The association information of the resolvedConceptReference is also added
	 * to the classbean
	 * 
	 * @param rcr
	 * @return
	 */
	private ClassBean createClassBean(OntologyBean ontologyBean,
			ResolvedConceptReference rcr, boolean addRelations) {
		String fullId = getFullId(ontologyBean, rcr.getConceptCode());
		ClassBean bean = createBaseClassBean(rcr.getConceptCode(), fullId, null);

		if (rcr.getEntityDescription() != null) {
			bean.setLabel(rcr.getEntityDescription().getContent());
		}

		Concept entry = rcr.getReferencedEntry();

		if (entry == null) {
			// bean.setLight(true);
		} else if (entry.getIsAnonymous() == null
				|| (entry.getIsAnonymous() != null && !entry.getIsAnonymous()
						.booleanValue())) {
			HashMap<Object, Object> relationMap = bean.getRelations();
			// handle synonyms
			addSynonyms(relationMap, entry, bean, addRelations);
			// handle definitions
			addDefinitions(entry, bean);
			// handle comments

			if (addRelations) {
				addComments(relationMap, entry);
				// handle concept properties
				addProperties(relationMap, entry);
			}

			if (StringUtils.isBlank(bean.getLabel())) {
				bean.setLabel(getPreferredPresentation(entry));
			}
		}

		if (addRelations) {
			addAssociationListInfoToClassBean(ontologyBean, rcr.getSourceOf(),
					bean, null, false);
			addAssociationListInfoToClassBean(ontologyBean, rcr.getTargetOf(),
					bean, null, false);
		}

		return bean;
	}

	/**
	 * 
	 * @param list
	 * @return
	 */
	private ClassBean createThingClassBeanWithCount(OntologyBean ontologyBean,
			ResolvedConceptReferenceList list) {
		ArrayList<ClassBean> classBeans = createClassBeanArray(ontologyBean,
				list, true);

		return createThingClassBean(classBeans);
	}

	/**
	 * 
	 * @param classBeans
	 * @return
	 */
	private ClassBean createThingClassBean(ArrayList<ClassBean> classBeans) {
		ClassBean classBean = createBaseClassBean(ROOT_CLASS_ID, ROOT_CLASS_ID,
				ROOT_CLASS_ID);
		classBean.addRelation(ApplicationConstants.SUB_CLASS, classBeans);
		classBean.addRelation(ApplicationConstants.CHILD_COUNT, classBeans
				.size());

		return classBean;
	}

	private ClassBean createBaseClassBean(String id, String fullId, String label) {
		ClassBean classBean = new ClassBean();
		classBean.setId(id);
		classBean.setFullId(fullId);
		classBean.setLabel(label);
		classBean.setType(ConceptTypeEnum.CONCEPT_TYPE_CLASS);

		return classBean;
	}

	/**
	 * 
	 * @param list
	 * @param includeCount
	 * @return
	 */
	private ArrayList<ClassBean> createClassBeanArray(
			OntologyBean ontologyBean, ResolvedConceptReferenceList list,
			boolean includeCount) {
		ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();
		Enumeration<ResolvedConceptReference> refEnum = list
				.enumerateResolvedConceptReference();
		ResolvedConceptReference ref = null;

		while (refEnum.hasMoreElements()) {
			ref = (ResolvedConceptReference) refEnum.nextElement();
			ClassBean bean;

			if (includeCount) {
				bean = createClassBeanWithChildCount(ontologyBean, ref, false);
			} else {
				bean = createClassBean(ontologyBean, ref, true);
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
			if (presentations[i].getIsPreferred().booleanValue()) {
				return presentations[i].getValue().getContent();
			}
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
	private ArrayList<ClassBean> createClassBeanArray(
			OntologyBean ontologyBean, AssociationList list,
			ClassBean current_classBean, String hierarchy_relationName,
			boolean includeChildren) {
		ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();
		addAssociationListInfoToClassBean(ontologyBean, list,
				current_classBean, hierarchy_relationName, includeChildren);
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
	private void addAssociationListInfoToClassBean(OntologyBean ontologyBean,
			AssociationList list, ClassBean current_classBean,
			String hierarchy_relationName, boolean includeChildren) {
		if (list == null || current_classBean == null) {
			return;
		}

		for (Association association : list.getAssociation()) {
			addAssociationInfoToClassBean(ontologyBean, association,
					current_classBean, hierarchy_relationName, includeChildren);
		}
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
	private void addAssociationInfoToClassBean(OntologyBean ontologyBean,
			Association association, ClassBean current_classBean,
			String hierarchy_relationName, boolean includeChildren) {
		AssociatedConceptList assocConceptList = association
				.getAssociatedConcepts();
		ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();
		ResolvedConceptReferenceList rcrl = new ResolvedConceptReferenceList();
		rcrl.setResolvedConceptReference(assocConceptList
				.getAssociatedConcept());
		HashMap<String, Integer> countMap = getHashMapWithChildCount(rcrl);

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

				ResolvedConceptReference rcr_without_relations = getResolvedConceptReferenceWithoutRelations(assocConcept);
				ClassBean classBean = createClassBeanWithChildCount(
						ontologyBean, rcr_without_relations, countMap,
						includeChildren);

				// ClassBean classBean = createClassBeanWithChildCount(
				// rcr_without_relations, includeChildren);

				classBeans.add(classBean);

				// Find and recurse printing for next batch ...
				AssociationList nextLevel = assocConcept.getSourceOf();

				if (nextLevel != null && nextLevel.getAssociationCount() != 0) {
					for (int j = 0; j < nextLevel.getAssociationCount(); j++) {
						addAssociationInfoToClassBean(ontologyBean, nextLevel
								.getAssociation(j), classBean,
								hierarchy_relationName, includeChildren);
					}
				}

				// Find and recurse printing for previous batch ...
				AssociationList prevLevel = assocConcept.getTargetOf();
				if (prevLevel != null && prevLevel.getAssociationCount() != 0) {
					for (int j = 0; j < prevLevel.getAssociationCount(); j++) {
						addAssociationInfoToClassBean(ontologyBean, prevLevel
								.getAssociation(j), classBean,
								hierarchy_relationName, includeChildren);
					}
				}
			}
		}

		String dirName = association.getDirectionalName();
		if (StringUtils.isBlank(dirName)) {
			dirName = "[R]" + association.getAssociationName();
		}
		// current_classBean.addRelation(dirName, classBeans);
		mergeClassBeansIntoClassBeanRelationsUsingRelationName(
				current_classBean, classBeans, dirName);
		mergeClassBeansIntoClassBeanRelationsUsingRelationName(
				current_classBean, classBeans, hierarchy_relationName);
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
	 * @param relation_name
	 */
	@SuppressWarnings("unchecked")
	private void mergeClassBeansIntoClassBeanRelationsUsingRelationName(
			ClassBean bean, ArrayList<ClassBean> beanlist, String relation_name) {
		// If no hierarchy_name is provided, we assume that special BioPortal
		// relations do not have to be setup.
		if (StringUtils.isBlank(relation_name)) {
			return;
		}

		Object value = bean.getRelations().get(relation_name);

		if (value != null && value instanceof ArrayList) {
			ArrayList valueList = (ArrayList) value;

			if (!valueList.isEmpty() && valueList.get(0) instanceof ClassBean) {
				List<ClassBean> list = mergeListsEliminatingDuplicates(
						(ArrayList<ClassBean>) value, beanlist);
				bean.addRelation(relation_name, list);
			}
		} else {
			bean.addRelation(relation_name, beanlist);
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

	private void addSynonyms(HashMap<Object, Object> relationMap,
			Concept entry, ClassBean bean, boolean addRelations) {
		int count = entry.getPresentationCount();

		for (int i = 0; i < count; i++) {
			Presentation p = entry.getPresentation(i);

			if (!p.getIsPreferred().booleanValue()) {
				// Add a abstraction for getting all the Synonyms..gforge #1351
				String synVal = p.getValue().getContent();

				// remove duplicates
				List<String> synonyms = bean.getSynonyms();

				if (synonyms == null || !synonyms.contains(synVal)) {
					bean.addSynonym(synVal);
				}

				if (addRelations) {
					if (StringUtils.isNotBlank(p.getDegreeOfFidelity())) {
						String key = p.getDegreeOfFidelity() + " SYNONYM";
						addStringToHashMapsArrayList(relationMap, key, synVal);
					} else if (StringUtils.isNotBlank(p
							.getRepresentationalForm())) {
						String key = "SYNONYM " + p.getRepresentationalForm();
						addStringToHashMapsArrayList(relationMap, key, synVal);
					} else {
						addStringToHashMapsArrayList(relationMap, "SYNONYM",
								synVal);
					}
				}
			}
		}
	}

	private void addComments(HashMap<Object, Object> relationMap, Concept entry) {
		int count = entry.getCommentCount();

		for (int i = 0; i < count; i++) {
			Comment c = entry.getComment(i);
			addStringToHashMapsArrayList(relationMap, "Comment", c.getValue()
					.getContent());
		}
	}

	private void addDefinitions(Concept entry, ClassBean bean) {
		int count = entry.getDefinitionCount();

		for (int i = 0; i < count; i++) {
			Definition d = entry.getDefinition(i);
			bean.addDefinition(d.getValue().getContent());
		}
	}

	private void addProperties(HashMap<Object, Object> relationMap,
			Concept entry) {
		int count = entry.getPropertyCount();

		for (int i = 0; i < count; i++) {
			Property prop = entry.getProperty(i);
			String key = prop.getPropertyName();

			if (StringUtils.isNotBlank(key)) {
				addStringToHashMapsArrayList(relationMap, key, prop.getValue()
						.getContent());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private ClassBean createSimpleSubClassOnlyClassBean(ClassBean classBean,
			boolean includeChildren) {
		ClassBean cb = createBaseClassBean(classBean.getId(), classBean
				.getFullId(), classBean.getLabel());
		cb.setSynonyms(classBean.getSynonyms());
		cb.setDefinitions(classBean.getDefinitions());
		cb.setAuthors(classBean.getAuthors());

		Integer childCount = (Integer) classBean.getRelations().get(
				ApplicationConstants.CHILD_COUNT);
		if (childCount != null) {
			cb.addRelation(ApplicationConstants.CHILD_COUNT, childCount);
		}

		if (includeChildren) {
			for (Object key : classBean.getRelations().keySet()) {
				if (key instanceof String) {
					String keyString = (String) key;

					if (keyString.equals(ApplicationConstants.SUB_CLASS)) {
						continue;
					}

					Object value_obj = classBean.getRelations().get(keyString);
					// We only want to add the association information. So we
					// check for List of classbeans and add only those.
					if (value_obj != null && value_obj instanceof List) {
						List value_list = (List) value_obj;
						List<ClassBean> newClasses = new ArrayList<ClassBean>();

						for (Object element_obj : value_list) {
							if (element_obj instanceof ClassBean) {
								ClassBean classB = (ClassBean) element_obj;
								ClassBean newClassB = createSimpleStrippedDownClassBean(classB);
								newClasses.add(newClassB);
							}
						}

						if (!newClasses.isEmpty()) {
							cb.addRelation(keyString, newClasses);
						}
					} else if (value_obj != null
							&& value_obj instanceof ClassBean) {
						ClassBean classB = (ClassBean) value_obj;
						ClassBean newClassB = createSimpleStrippedDownClassBean(classB);
						cb.addRelation(keyString, newClassB);
					}
				}
			}
		}

		if (classBean.getRelations()
				.containsKey(ApplicationConstants.SUB_CLASS)) {
			Object subclass_obj = classBean.getRelations().get(
					ApplicationConstants.SUB_CLASS);
			if (subclass_obj != null && subclass_obj instanceof List) {
				List<ClassBean> subclasses = (List<ClassBean>) subclass_obj;
				List<ClassBean> newSubClasses = new ArrayList<ClassBean>();
				for (ClassBean subclass : subclasses) {
					ClassBean newSubClass = createSimpleSubClassOnlyClassBean(
							subclass, includeChildren);
					newSubClasses.add(newSubClass);
				}
				cb.addRelation(ApplicationConstants.SUB_CLASS, newSubClasses);
			} else if (subclass_obj != null
					&& subclass_obj instanceof ClassBean) {
				ClassBean subclass = (ClassBean) subclass_obj;
				ClassBean newSubClass = createSimpleSubClassOnlyClassBean(
						subclass, includeChildren);
				cb.addRelation(ApplicationConstants.SUB_CLASS, newSubClass);
			}
		}

		return cb;
	}

	private ClassBean createSimpleStrippedDownClassBean(ClassBean classBean) {
		ClassBean cb = createBaseClassBean(classBean.getId(), classBean
				.getFullId(), classBean.getLabel());
		Integer childCount = (Integer) classBean.getRelations().get(
				ApplicationConstants.CHILD_COUNT);
		if (childCount != null) {
			cb.addRelation(ApplicationConstants.CHILD_COUNT, childCount);
		}

		return cb;
	}

	@SuppressWarnings("unchecked")
	private void addSubClassRelationAndCountToClassBean(String schemeName,
			CodingSchemeVersionOrTag csvt, ClassBean classBean)
			throws Exception {
		List<String> hierarchyDirectionalNames = getListOfSubClassDirectionalName(
				schemeName, csvt);

		for (String directionalName : hierarchyDirectionalNames) {
			Object obj_beans = classBean.getRelations().get(directionalName);
			if (obj_beans != null && obj_beans instanceof ArrayList) {
				ArrayList<ClassBean> beanlist = (ArrayList<ClassBean>) obj_beans;
				mergeClassBeansIntoClassBeanRelationsUsingRelationName(
						classBean, beanlist, ApplicationConstants.SUB_CLASS);
			} else if (obj_beans != null && obj_beans instanceof ClassBean) {
				ArrayList<ClassBean> subclassList = new ArrayList<ClassBean>();
				subclassList.add((ClassBean) obj_beans);
				mergeClassBeansIntoClassBeanRelationsUsingRelationName(
						classBean, subclassList, ApplicationConstants.SUB_CLASS);
			}
		}

		// Add the child count
		Object count_obj = classBean.getRelations().get(
				ApplicationConstants.SUB_CLASS);
		if (count_obj != null && count_obj instanceof ArrayList) {
			ArrayList<ClassBean> beanlist = (ArrayList<ClassBean>) count_obj;
			classBean.addRelation(ApplicationConstants.CHILD_COUNT, beanlist
					.size());
		} else {
			classBean.addRelation(ApplicationConstants.CHILD_COUNT, 0);
		}

	}

	@SuppressWarnings("unchecked")
	private void addSuperClassRelationToClassBean(String schemeName,
			CodingSchemeVersionOrTag csvt, ClassBean classBean)
			throws Exception {
		ArrayList<String> hierarchyDirectionalNames = new ArrayList<String>();
		String hierarchyId = getDefaultHierarchyId(schemeName, csvt);
		SupportedHierarchy[] supHiers = lbscm.getSupportedHierarchies(
				schemeName, csvt, hierarchyId);
		SupportedHierarchy sh = supHiers[0];
		for (String associationName : sh.getAssociationNames()) {
			// We need to be careful about the direction flag
			String dirName = getAssociationDirectionalName(schemeName, csvt,
					associationName, !sh.isIsForwardNavigable());
			hierarchyDirectionalNames.add(dirName);
		}

		for (String directionalName : hierarchyDirectionalNames) {
			Object obj_beans = classBean.getRelations().get(directionalName);
			if (obj_beans != null && obj_beans instanceof ArrayList) {
				ArrayList<ClassBean> beanlist = (ArrayList<ClassBean>) obj_beans;
				mergeClassBeansIntoClassBeanRelationsUsingRelationName(
						classBean, beanlist, ApplicationConstants.SUPER_CLASS);
			} else if (obj_beans != null && obj_beans instanceof ClassBean) {
				ArrayList<ClassBean> subclassList = new ArrayList<ClassBean>();
				subclassList.add((ClassBean) obj_beans);
				mergeClassBeansIntoClassBeanRelationsUsingRelationName(
						classBean, subclassList,
						ApplicationConstants.SUPER_CLASS);
			}
		}

	}

	private String getAssociationDirectionalName(String schemeName,
			CodingSchemeVersionOrTag csvt, String assoc, boolean forward)
			throws Exception {
		String dirName = forward ? lbscm.getAssociationForwardName(assoc,
				schemeName, csvt) : lbscm.getAssociationReverseName(assoc,
				schemeName, csvt);
		return (StringUtils.isNotBlank(dirName) ? dirName : "[R]" + assoc);
	}

	private List<String> getListOfSubClassDirectionalName(String schemeName,
			CodingSchemeVersionOrTag csvt) throws Exception {
		ArrayList<String> hierarchyDirectionalNames = new ArrayList<String>();
		String hierarchyId = getDefaultHierarchyId(schemeName, csvt);
		SupportedHierarchy[] supHiers = lbscm.getSupportedHierarchies(
				schemeName, csvt, hierarchyId);
		SupportedHierarchy sh = supHiers[0];
		for (String associationName : sh.getAssociationNames()) {
			// We need to be careful about the direction flag
			String dirName = getAssociationDirectionalName(schemeName, csvt,
					associationName, sh.isIsForwardNavigable());
			hierarchyDirectionalNames.add(dirName);
		}
		return hierarchyDirectionalNames;
	}

	private String getFullId(OntologyBean ontologyBean, String code) {
		String fullId = code;
		String modCode = code.replace(':', '_');
		if (ontologyBean != null) {
			if (ApplicationConstants.FORMAT_OBO.equalsIgnoreCase(ontologyBean
					.getFormat())) {
				fullId = "http://purl.obolibrary.org/obo/" + modCode;
			}
			if (ApplicationConstants.FORMAT_UMLS_RRF
					.equalsIgnoreCase(ontologyBean.getFormat())) {
				fullId = "http://purl.bioontology.org/ontology/"
						+ ontologyBean.getAbbreviation() + "/" + code;
			}
			if (ApplicationConstants.FORMAT_LEXGRID_XML
					.equalsIgnoreCase(ontologyBean.getFormat())) {
				fullId = "http://purl.bioontology.org/ontology/"
						+ ontologyBean.getAbbreviation() + "/" + modCode;
			}

		}
		return fullId;
	}

	/**
	 * @param allConceptsMaxPageSize
	 *            the allConceptsMaxPageSize to set
	 */
	public void setAllConceptsMaxPageSize(Integer allConceptsMaxPageSize) {
		this.allConceptsMaxPageSize = allConceptsMaxPageSize;
	}
}