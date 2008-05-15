package org.ncbo.stanford.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.AssociatedConceptList;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.ActiveOption;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.ConvenienceMethods;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.Utility.LBConstants.MatchAlgorithms;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.commonTypes.PropertyQualifier;
import org.LexGrid.commonTypes.Source;
import org.LexGrid.concepts.CodedEntry;
import org.LexGrid.concepts.Comment;
import org.LexGrid.concepts.ConceptProperty;
import org.LexGrid.concepts.Definition;
import org.LexGrid.concepts.Presentation;
import org.LexGrid.naming.SupportedProperty;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.concept.PropertyBean;
import org.ncbo.stanford.bean.search.SearchResultBean;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.manager.AbstractOntologyManagerLexGrid;
import org.ncbo.stanford.manager.OntologyRetrievalManager;

/**
 * A implementation of the OntologyRetrievalManager for ontologies stored
 * in LexGrid.
 * 
 * 
 * @author Pradip Kanjamala
 * 
 */
public class OntologyRetrievalManagerLexGridImpl extends
		AbstractOntologyManagerLexGrid implements
		OntologyRetrievalManager {

	private static final Log log = LogFactory
			.getLog(OntologyRetrievalManagerLexGridImpl.class);

	private enum Match_Types {
		SEARCH_STARTS_WITH, SEARCH_ENDS_WITH, SEARCH_CONTAINS, SEARCH_EXACT_MATCH
	};

	private LexBIGService lbs;

	OntologyRetrievalManagerLexGridImpl() throws Exception {
		lbs = LexBIGServiceImpl.defaultInstance();
	}

	public List<String> findProperties(NcboOntology ncboOntology)
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
	public ClassBean findRootConcept(NcboOntology ncboOntology)
			throws Exception {
		LexBIGServiceConvenienceMethods lbscm = (LexBIGServiceConvenienceMethods) lbs
				.getGenericExtension("LexBIGServiceConvenienceMethods");

		String urnAndVersion = ncboOntology.getCodingScheme();
		String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
		// Iterate through all hierarchies ...
		CodingSchemeVersionOrTag csvt = Constructors
				.createCodingSchemeVersionOrTagFromVersion(urnVersionArray[1]);
		String[] hierarchyIDs = lbscm.getHierarchyIDs(urnVersionArray[0], csvt);
		String hierarchyId = (hierarchyIDs.length > 0) ? hierarchyIDs[0] : null;

		for (String hierarchy : hierarchyIDs) {
			if (hierarchy.equalsIgnoreCase("IS_A"))
				hierarchyId = hierarchy;
		}

		ResolvedConceptReferenceList rcrl = lbscm.getHierarchyRoots(
				urnVersionArray[0], csvt, hierarchyId);

		return createThingClassBean(rcrl);
	}

	public ClassBean findConcept(NcboOntology ncboOntology, String conceptID)
			throws Exception {
		String urnAndVersion = ncboOntology.getCodingScheme();
		String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
		CodingSchemeVersionOrTag csvt = Constructors
				.createCodingSchemeVersionOrTagFromVersion(urnVersionArray[1]);

		// Perform the query ...
		ConceptReferenceList crefs = ConvenienceMethods
				.createConceptReferenceList(new String[] { conceptID },
						urnVersionArray[0]);
		ResolvedConceptReferenceList matches = lbs.getCodingSchemeConcepts(
				urnVersionArray[0], csvt).restrictToStatus(ActiveOption.ALL,
				null).restrictToCodes(crefs).resolveToList(null, null, null, 1);
		// Analyze the result ...
		if (matches.getResolvedConceptReferenceCount() > 0) {
			ResolvedConceptReference ref = (ResolvedConceptReference) matches
					.enumerateResolvedConceptReference().nextElement();
			return createClassBean(ref);
		}

		return null;
	}

	public List<ClassBean> findPathToRoot(NcboOntology ncboOntology,
			String conceptId) throws Exception {
		String urnAndVersion = ncboOntology.getCodingScheme();
		String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
		CodingSchemeVersionOrTag csvt = Constructors
				.createCodingSchemeVersionOrTagFromVersion(urnVersionArray[1]);
		LexBIGServiceConvenienceMethods lbscm = (LexBIGServiceConvenienceMethods) lbs
				.getGenericExtension("LexBIGServiceConvenienceMethods");

		String[] hierarchyIDs = lbscm.getHierarchyIDs(urnVersionArray[0], csvt);
		String hierarchyId = (hierarchyIDs.length > 0) ? hierarchyIDs[0] : null;
		for (String hierarchy : hierarchyIDs) {
			if (hierarchy.equalsIgnoreCase("IS_A"))
				hierarchyId = hierarchy;
		}
		AssociationList associations = lbscm.getHierarchyPathToRoot(
				urnVersionArray[0], csvt, hierarchyId, conceptId, true,
				LexBIGServiceConvenienceMethods.HierarchyPathResolveOption.ALL,
				null);
		ClassBean conceptClass = findConcept(ncboOntology, conceptId);
		ArrayList<ClassBean> classBeans = createClassBeanArray(associations,
				conceptClass);

		return classBeans;
	}

	public List<ClassBean> findParent(NcboOntology ncboOntology,
			String conceptId) throws Exception {
		String urnAndVersion = ncboOntology.getCodingScheme();
		String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
		CodingSchemeVersionOrTag csvt = Constructors
				.createCodingSchemeVersionOrTagFromVersion(urnVersionArray[1]);
		LexBIGServiceConvenienceMethods lbscm = (LexBIGServiceConvenienceMethods) lbs
				.getGenericExtension("LexBIGServiceConvenienceMethods");

		String[] hierarchyIDs = lbscm.getHierarchyIDs(urnVersionArray[0], csvt);
		String hierarchyId = (hierarchyIDs.length > 0) ? hierarchyIDs[0] : null;
		for (String hierarchy : hierarchyIDs) {
			if (hierarchy.equalsIgnoreCase("IS_A"))
				hierarchyId = hierarchy;
		}

		AssociationList associations = lbscm.getHierarchyLevelPrev(
				urnVersionArray[0], csvt, hierarchyId, conceptId, true, null);
		ClassBean conceptClass = findConcept(ncboOntology, conceptId);
		ArrayList<ClassBean> classBeans = createClassBeanArray(associations,
				conceptClass);

		return classBeans;
	}

	public List<ClassBean> findChildren(NcboOntology ncboOntology,
			String conceptId) throws Exception {
		String urnAndVersion = ncboOntology.getCodingScheme();
		String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
		CodingSchemeVersionOrTag csvt = Constructors
				.createCodingSchemeVersionOrTagFromVersion(urnVersionArray[1]);
		LexBIGServiceConvenienceMethods lbscm = (LexBIGServiceConvenienceMethods) lbs
				.getGenericExtension("LexBIGServiceConvenienceMethods");

		String[] hierarchyIDs = lbscm.getHierarchyIDs(urnVersionArray[0], csvt);
		String hierarchyId = (hierarchyIDs.length > 0) ? hierarchyIDs[0] : null;

		for (String hierarchy : hierarchyIDs) {
			if (hierarchy.equalsIgnoreCase("IS_A"))
				hierarchyId = hierarchy;
		}

		AssociationList associations = lbscm.getHierarchyLevelNext(
				urnVersionArray[0], csvt, hierarchyId, conceptId, true, null);
		ClassBean conceptClass = findConcept(ncboOntology, conceptId);
		ArrayList<ClassBean> classBeans = createClassBeanArray(associations,
				conceptClass);

		return classBeans;
	}

	public ArrayList<SearchResultBean> findConceptNameExact(
			List<NcboOntology> ontologyVersions, String query,
			boolean includeObsolete, int maxToReturn) {
		ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();

		for (NcboOntology ontologyVersion : ontologyVersions) {
			SearchResultBean result = searchNodesForName(ontologyVersion,
					query, maxToReturn, Match_Types.SEARCH_EXACT_MATCH, false,
					includeObsolete);
			results.add(result);
		}

		return results;
	}

	public List<SearchResultBean> findConceptNameStartsWith(
			List<NcboOntology> ontologyVersions, String query,
			boolean includeObsolete, int maxToReturn) {
		ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();

		for (NcboOntology ontologyVersion : ontologyVersions) {
			SearchResultBean result = searchNodesForName(ontologyVersion,
					query, maxToReturn, Match_Types.SEARCH_STARTS_WITH, false,
					includeObsolete);
			results.add(result);
		}

		return results;
	}

	public List<SearchResultBean> findConceptNameContains(
			List<NcboOntology> ontologyVersions, String query,
			boolean includeObsolete, int maxToReturn) {
		ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();

		for (NcboOntology ontologyVersion : ontologyVersions) {
			SearchResultBean result = searchNodesForName(ontologyVersion,
					query, maxToReturn, Match_Types.SEARCH_CONTAINS, false,
					includeObsolete);
			results.add(result);
		}

		return results;
	}

	public List<SearchResultBean> findConceptPropertyExact(
			List<NcboOntology> ontologyVersions, String query,
			String properties[], boolean includeObsolete, int maxToReturn) {
		ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();

		for (NcboOntology ontologyVersion : ontologyVersions) {
			SearchResultBean result = searchNodesForProperties(ontologyVersion,
					query, properties, false, includeObsolete, maxToReturn,
					Match_Types.SEARCH_EXACT_MATCH);
			results.add(result);
		}

		return results;
	}

	public List<SearchResultBean> findConceptPropertyStartsWith(
			List<NcboOntology> ontologyVersions, String query,
			String properties[], boolean includeObsolete, int maxToReturn) {
		ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();

		for (NcboOntology ontologyVersion : ontologyVersions) {
			SearchResultBean result = searchNodesForProperties(ontologyVersion,
					query, properties, false, includeObsolete, maxToReturn,
					Match_Types.SEARCH_STARTS_WITH);
			results.add(result);
		}

		return results;
	}

	public List<SearchResultBean> findConceptPropertyContains(
			List<NcboOntology> ontologyVersions, String query,
			String properties[], boolean includeObsolete, int maxToReturn) {
		ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();

		for (NcboOntology ontologyVersion : ontologyVersions) {
			SearchResultBean result = searchNodesForProperties(ontologyVersion,
					query, properties, false, includeObsolete, maxToReturn,
					Match_Types.SEARCH_CONTAINS);
			results.add(result);
		}

		return results;
	}

	private String replacePeriod(String s) {
		String temp = "";

		if (s.indexOf(".") < 0) {
			return s;
		}

		while (s.indexOf(".") >= 0) {
			temp = temp + s.substring(0, s.indexOf(".")) + "\\.";
			s = s.substring(s.indexOf(".") + 1);
		}

		temp += s;

		return temp;
	}

	private SearchResultBean searchNodesForProperties(
			NcboOntology ncboOntology, String name, String[] properties,
			boolean soundsLike, boolean includeObsolete, int maxToReturn,
			Match_Types algorithm) {
		try {
			String urnAndVersion = ncboOntology.getCodingScheme();
			String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
			CodedNodeSet nodes = lbs
					.getCodingSchemeConcepts(
							urnVersionArray[0],
							Constructors
									.createCodingSchemeVersionOrTagFromVersion(urnVersionArray[1]));

			String[] propList = properties;
			String matchAlgorithm = "RegExp";

			if (soundsLike)
				matchAlgorithm = MatchAlgorithms.DoubleMetaphoneLuceneQuery
						.name();
			name = name.toLowerCase();

			if (!soundsLike) {
				name = replacePeriod(name);
				switch (algorithm) {
				case SEARCH_STARTS_WITH:
					name = name + ".*";
					break;
				case SEARCH_ENDS_WITH:
					name = ".*" + name;
					break;
				case SEARCH_CONTAINS:
					name = ".*" + name + ".*";
					break;
				case SEARCH_EXACT_MATCH:
					break;
				}

				matchAlgorithm = "RegExp";
			}

			if (properties != null && properties.length > 0) {
				nodes = nodes.restrictToMatchingProperties(Constructors
						.createLocalNameList(propList), null, name,
						matchAlgorithm, null);
			}

			if (includeObsolete) {
				nodes = nodes.restrictToStatus(ActiveOption.ALL, null);
			} else {
				nodes = nodes.restrictToStatus(ActiveOption.ACTIVE_ONLY, null);
			}

			SortOptionList sortCriteria = Constructors
					.createSortOptionList(new String[] { "matchToQuery", "code" });
			ResolvedConceptReferencesIterator matchIterator = nodes.resolve(
					sortCriteria, null, null);

			ResolvedConceptReferenceList lst = matchIterator.next(maxToReturn);
			SearchResultBean srb = new SearchResultBean();
			srb.setOntologyVersionId(ncboOntology.getId());
			srb.setProperties(createClassBeanArray(lst));

			return srb;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

		return null;

	}

	private SearchResultBean searchNodesForName(NcboOntology ncboOntology,
			String name, int maxToReturn, Match_Types algorithm,
			boolean soundsLike, boolean includeObsolete) {
		try {
			String urnAndVersion = ncboOntology.getCodingScheme();
			String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
			CodedNodeSet nodes = lbs
					.getCodingSchemeConcepts(
							urnVersionArray[0],
							Constructors
									.createCodingSchemeVersionOrTagFromVersion(urnVersionArray[1]));

			String matchAlgorithm = "RegExp";

			if (soundsLike)
				matchAlgorithm = MatchAlgorithms.DoubleMetaphoneLuceneQuery
						.name();

			name = name.toLowerCase();

			if (!soundsLike) {
				name = replacePeriod(name);
				switch (algorithm) {
				case SEARCH_STARTS_WITH:
					name = name + ".*";
					break;
				case SEARCH_ENDS_WITH:
					name = ".*" + name;
					break;
				case SEARCH_CONTAINS:
					name = ".*" + name + ".*";
					break;
				case SEARCH_EXACT_MATCH:
					break;
				}

				matchAlgorithm = "RegExp";
			}

			nodes = nodes.restrictToMatchingDesignations(name,
					SearchDesignationOption.PREFERRED_ONLY, matchAlgorithm,
					null);

			// Sort by search engine recommendation & code ...
			SortOptionList sortCriteria = Constructors
					.createSortOptionList(new String[] { "matchToQuery", "code" });
			// Analyze the result ...
			ResolvedConceptReferencesIterator matchIterator = nodes.resolve(
					sortCriteria, null, null);
			ResolvedConceptReferenceList lst = matchIterator.next(maxToReturn);
			SearchResultBean srb = new SearchResultBean();
			srb.setOntologyVersionId(ncboOntology.getId());
			srb.setNames(createClassBeanArray(lst));

			return srb;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

		return null;
	}

	private PropertyBean createPropertyBean(Source source) {
		PropertyBean bean = new PropertyBean();
		bean.setId("source");
		bean.setLabel(source.getContent());
		HashMap<Object, Object> map = bean.getRelations();
		addStringToHashMap(map, "Role", source.getRole());
		addStringToHashMap(map, "SubRef", source.getSubRef());

		return bean;
	}

	private PropertyBean createPropertyBean(PropertyQualifier qualifier) {
		PropertyBean bean = new PropertyBean();
		bean.setId(qualifier.getPropertyQualifierId());
		bean.setLabel(qualifier.getContent());
		HashMap<Object, Object> map = bean.getRelations();
		addArrayToHashMap(map, "AnyObject", qualifier.getAnyObject());

		return bean;
	}

	private PropertyBean createPropertyBean(Property prop) {
		PropertyBean bean = new PropertyBean();
		bean.setId(prop.getProperty());
		bean.setLabel(prop.getText().getContent());
		HashMap<Object, Object> map = bean.getRelations();
		addStringToHashMap(map, "Language", prop.getLanguage());
		addStringToHashMap(map, "PresentationFormat", prop
				.getPresentationFormat());
		addArrayToHashMap(map, "UsageContext", prop.getUsageContext());
		addArrayToHashMap(map, "PropertyQualifier", prop.getPropertyQualifier());
		addArrayToHashMap(map, "Source", prop.getSource());

		if (prop instanceof Presentation) {
			Presentation presentation = (Presentation) prop;
			addStringToHashMap(map, "DegreeOfFidelity", presentation
					.getDegreeOfFidelity());
			addStringToHashMap(map, "IsPreferred", presentation
					.getIsPreferred().toString());
			addStringToHashMap(map, "RepresentationalForm", presentation
					.getRepresentationalForm());
		}

		if (prop instanceof Definition) {
			Definition definition = (Definition) prop;
			addStringToHashMap(map, "IsPreferred", definition.getIsPreferred()
					.toString());
		}

		return bean;
	}

	private ClassBean createClassBean(ResolvedConceptReference rcr) {
		ClassBean bean = new ClassBean();

		bean.setId(rcr.getConceptCode());
		bean.setLabel(rcr.getEntityDescription().getContent());
		CodedEntry entry = rcr.getReferencedEntry();

		if (entry == null) {
			// bean.setLight(true);
		} else if (entry.getIsAnonymous() == null
				|| (entry.getIsAnonymous() != null && !entry.getIsAnonymous()
						.booleanValue())) {
			addCodedEntryPropertyValue(entry, bean);

			if (StringUtils.isBlank(bean.getLabel())) {
				bean.setLabel(getPreferredPresentation(entry));
			}
		}

		return bean;
	}

	private ClassBean createThingClassBean(ResolvedConceptReferenceList list) {
		ClassBean classBean = new ClassBean();
		classBean.setId("THING");
		classBean.setLabel("THING");
		ArrayList<ClassBean> classBeans = createClassBeanArray(list);
		classBean.addRelation("hasSubType", classBeans);

		return classBean;
	}

	private ArrayList<ClassBean> createClassBeanArray(
			ResolvedConceptReferenceList list) {
		ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();
		Enumeration<ResolvedConceptReference> refEnum = list
				.enumerateResolvedConceptReference();
		ResolvedConceptReference ref = null;

		while (refEnum.hasMoreElements()) {
			ref = (ResolvedConceptReference) refEnum.nextElement();
			ClassBean bean = createClassBean(ref);
			classBeans.add(bean);
		}

		return classBeans;
	}

	private String getDefinition(CodedEntry entry) {
		Definition d = null;
		int count = entry.getDefinitionCount();
		for (int i = 0; i < count; i++) {
			d = entry.getDefinition(i);
			if (d.getIsPreferred().booleanValue())
				return d.getText().getContent();
		}
		return "";
	}

	private String getPreferredPresentation(CodedEntry entry) {
		Presentation[] presentations = entry.getPresentation();
		for (int i = 0; i < presentations.length; i++) {
			if (presentations[i].getIsPreferred().booleanValue())

				return presentations[i].getText().getContent();
		}
		return "";
	}

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

	private static void addStringToHashMap(HashMap<Object, Object> map,
			String key, String value) {
		if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
			map.put(key, value);
		}
	}

	private void addArrayToHashMap(HashMap<Object, Object> map, String key,
			Property[] properties) {
		if (StringUtils.isNotBlank(key) && properties != null
				&& properties.length > 0) {
			ArrayList<PropertyBean> beans = new ArrayList<PropertyBean>();
			for (int i = 0; i < properties.length; i++) {
				PropertyBean bean = createPropertyBean(properties[i]);
				beans.add(bean);
			}
			map.put(key, beans);
		}
	}

	private void addArrayToHashMap(HashMap<Object, Object> map, String key,
			Source[] sources) {
		if (StringUtils.isNotBlank(key) && sources != null
				&& sources.length > 0) {
			ArrayList<PropertyBean> beans = new ArrayList<PropertyBean>();
			for (int i = 0; i < sources.length; i++) {
				PropertyBean bean = createPropertyBean(sources[i]);
				beans.add(bean);
			}
			map.put(key, beans);
		}
	}

	private void addArrayToHashMap(HashMap<Object, Object> map, String key,
			PropertyQualifier[] qualifiers) {
		if (StringUtils.isNotBlank(key) && qualifiers != null
				&& qualifiers.length > 0) {
			ArrayList<PropertyBean> beans = new ArrayList<PropertyBean>();
			for (int i = 0; i < qualifiers.length; i++) {
				PropertyBean bean = createPropertyBean(qualifiers[i]);
				beans.add(bean);
			}
			map.put(key, beans);
		}
	}

	private void addArrayToHashMap(HashMap<Object, Object> map, String key,
			Object[] values) {
		if (StringUtils.isNotBlank(key) && values != null && values.length > 0) {
			map.put(key, Arrays.asList(values));
		}
	}

	/**
	 * Populate the ClassBean's map with the CodedEntry's properties
	 * 
	 * @param entry
	 * @param bean
	 */
	private void addCodedEntryPropertyValue(CodedEntry entry, ClassBean bean) {
		HashMap<Object, Object> map = bean.getRelations();
		addArrayToHashMap(map, "Presentation", entry.getPresentation());
		addArrayToHashMap(map, "Definition", entry.getDefinition());
		addArrayToHashMap(map, "Instruction", entry.getInstruction());
		addArrayToHashMap(map, "Comment", entry.getComment());
		addArrayToHashMap(map, "ConceptProperty", entry.getConceptProperty());
	}

	private void addCodedEntryPropertyValueOld(CodedEntry entry, ClassBean bean) {
		// Presentation[] presentation = entry.getPresentation();
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
			String key = prop.getProperty();
			if (StringUtils.isNotBlank(key)) {
				addStringToHashMapsArrayList(map, key, prop.getText()
						.getContent());
			}
		}

	}

	private ArrayList<ClassBean> createClassBeanArray(AssociationList list,
			ClassBean current_classBean) {
		ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();
		Enumeration<Association> assocEnum = list.enumerateAssociation();
		Association association = null;
		while (assocEnum.hasMoreElements()) {
			association = (Association) assocEnum.nextElement();
			createClassBeanArray(association, current_classBean);
		}
		classBeans.add(current_classBean);
		return classBeans;
	}

	private void createClassBeanArray(Association association,
			ClassBean current_classBean) {
		AssociatedConceptList assocConceptList = association
				.getAssociatedConcepts();
		ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();
		for (int i = 0; i < assocConceptList.getAssociatedConceptCount(); i++) {
			AssociatedConcept assocConcept = assocConceptList
					.getAssociatedConcept(i);
			if (assocConcept != null) {
				ClassBean classBean = createClassBean(assocConcept);
				// Find and recurse printing for next batch ...
				AssociationList nextLevel = assocConcept.getSourceOf();
				if (nextLevel != null && nextLevel.getAssociationCount() != 0)
					for (int j = 0; j < nextLevel.getAssociationCount(); j++)
						createClassBeanArray(nextLevel.getAssociation(j),
								classBean);

				// Find and recurse printing for previous batch ...
				AssociationList prevLevel = assocConcept.getTargetOf();
				if (prevLevel != null && prevLevel.getAssociationCount() != 0)
					for (int j = 0; j < prevLevel.getAssociationCount(); j++)
						createClassBeanArray(prevLevel.getAssociation(j),
								classBean);
			}
		}
		current_classBean.addRelation(association.getDirectionalName(),
				classBeans);
	}

}
