package org.ncbo.stanford.manager.wrapper.impl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
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
import org.LexGrid.concepts.CodedEntry;
import org.LexGrid.concepts.Comment;
import org.LexGrid.concepts.Definition;
import org.LexGrid.concepts.Presentation;
import org.LexGrid.naming.SupportedProperty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.search.SearchResultBean;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.manager.wrapper.AbstractOntologyManagerWrapperLexGrid;
import org.ncbo.stanford.manager.wrapper.OntologyRetrievalManagerWrapper;

/**
 * A implementation of the OntologyRetrievalManagerWrapper for ontologies stored
 * in LexGrid.
 * 
 * 
 * @author Pradip Kanjamala
 * 
 */
public class OntologyRetrievalManagerWrapperLexGridImpl extends
		AbstractOntologyManagerWrapperLexGrid implements OntologyRetrievalManagerWrapper
{

	private static final Log log = LogFactory
			.getLog(OntologyRetrievalManagerWrapperLexGridImpl.class);

	private enum Match_Types {
		SEARCH_STARTS_WITH, SEARCH_ENDS_WITH, SEARCH_CONTAINS, SEARCH_EXACT_MATCH
	};

	private LexBIGService lbs;

	OntologyRetrievalManagerWrapperLexGridImpl() throws Exception
	{
		lbs = LexBIGServiceImpl.defaultInstance();
	}

	public List<String> findProperties(Integer id) throws Exception {
		CodingScheme cs = getCodingScheme(lbs, id);
		ArrayList<String> list = new ArrayList<String>();
		SupportedProperty[] sp = cs.getMappings().getSupportedProperty();
		for (int i = 0; i < sp.length; i++) {
			SupportedProperty prop = sp[i];
			if (prop != null && prop.getLocalId() != null
					&& !prop.getLocalId().equalsIgnoreCase("textualPresentation")) {
				list.add(prop.getLocalId());
			}
		}
		return list;
	}

	/**
	 * Get the root concept for the specified ontology.
	 */
	public ClassBean findRootConcept(NcboOntology ncboOntology) throws Exception {
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
		ResolvedConceptReferenceList rcrl = lbscm.getHierarchyRoots(urnVersionArray[0], csvt,
				hierarchyId);
		// return createClassBeanArray(rcrl);
		return null;
	}

	public ClassBean findConcept(NcboOntology ncboOntology, String conceptID) throws Exception {
		String urnAndVersion = ncboOntology.getCodingScheme();
		String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
		CodingSchemeVersionOrTag csvt = Constructors
				.createCodingSchemeVersionOrTagFromVersion(urnVersionArray[1]);

		// Perform the query ...
		ConceptReferenceList crefs = ConvenienceMethods.createConceptReferenceList(
				new String[] { conceptID }, urnVersionArray[0]);

		ResolvedConceptReferenceList matches = lbs.getCodingSchemeConcepts(urnVersionArray[0], csvt)
				.restrictToStatus(ActiveOption.ALL, null).restrictToCodes(crefs).resolveToList(null,
						null, null, 1);

		// Analyze the result ...
		if (matches.getResolvedConceptReferenceCount() > 0) {
			ResolvedConceptReference ref = (ResolvedConceptReference) matches
					.enumerateResolvedConceptReference().nextElement();

			return createClassBean(ref);

		} else {

			return null;
		}
	}

	public ArrayList<ClassBean> findPathToRoot(NcboOntology ncboOntology, String conceptId)
			throws Exception {
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
		AssociationList associations = lbscm.getHierarchyPathToRoot(urnVersionArray[0], csvt,
				hierarchyId, conceptId, false,
				LexBIGServiceConvenienceMethods.HierarchyPathResolveOption.ALL, null);
		ClassBean conceptClass = findConcept(ncboOntology, conceptId);
		ArrayList<ClassBean> classBeans = createClassBeanArray(associations, conceptClass);

		return classBeans;
	}

	public ArrayList<ClassBean> findParent(NcboOntology ncboOntology, String conceptId)
			throws Exception {
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

		AssociationList associations = lbscm.getHierarchyLevelPrev(urnVersionArray[0], csvt,
				hierarchyId, conceptId, false, null);

		ClassBean conceptClass = findConcept(ncboOntology, conceptId);
		ArrayList<ClassBean> classBeans = createClassBeanArray(associations, conceptClass);

		return classBeans;
	}

	public ArrayList<ClassBean> findChildren(NcboOntology ncboOntology, String conceptId)
			throws Exception {
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

		AssociationList associations = lbscm.getHierarchyLevelNext(urnVersionArray[0], csvt,
				hierarchyId, conceptId, false, null);

		ClassBean conceptClass = findConcept(ncboOntology, conceptId);
		ArrayList<ClassBean> classBeans = createClassBeanArray(associations, conceptClass);

		return classBeans;
	}

	public ArrayList<SearchResultBean> findConceptNameExact(ArrayList<Integer> ontologyIds,
			List<String> query, boolean includeObsolete, int maxToReturn) {
		ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();
		for (Integer ontologyId : ontologyIds) {
			SearchResultBean result = searchNodesForName(ontologyId, query, maxToReturn,
					Match_Types.SEARCH_EXACT_MATCH, false, includeObsolete);
			results.add(result);
		}

		return results;
	}

	public ArrayList<SearchResultBean> findConceptNameStartsWith(ArrayList<Integer> ontologyIds,
			List<String> query, boolean includeObsolete, int maxToReturn) {
		ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();
		for (Integer ontologyId : ontologyIds) {
			SearchResultBean result = searchNodesForName(ontologyId, query, maxToReturn,
					Match_Types.SEARCH_STARTS_WITH, false, includeObsolete);
			results.add(result);
		}

		return results;
	}

	public ArrayList<SearchResultBean> findConceptNameContains(ArrayList<Integer> ontologyIds,
			List<String> query, boolean includeObsolete, int maxToReturn) {
		ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();
		for (Integer ontologyId : ontologyIds) {
			SearchResultBean result = searchNodesForName(ontologyId, query, maxToReturn,
					Match_Types.SEARCH_CONTAINS, false, includeObsolete);
			results.add(result);
		}

		return results;
	}

	public ArrayList<SearchResultBean> findConceptPropertyExact(ArrayList<Integer> ontologyIds,
			List<String> query, String properties[], boolean includeObsolete, int maxToReturn) {
		ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();
		for (Integer ontologyId : ontologyIds) {
			SearchResultBean result = searchNodesForProperties(ontologyId, query, properties, false,
					includeObsolete, maxToReturn, Match_Types.SEARCH_EXACT_MATCH);
			results.add(result);
		}

		return results;
	}

	public ArrayList<SearchResultBean> findConceptPropertyStartsWith(ArrayList<Integer> ontologyIds,
			List<String> query, String properties[], boolean includeObsolete, int maxToReturn) {
		ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();
		for (Integer ontologyId : ontologyIds) {
			SearchResultBean result = searchNodesForProperties(ontologyId, query, properties, false,
					includeObsolete, maxToReturn, Match_Types.SEARCH_STARTS_WITH);
			results.add(result);
		}

		return results;
	}

	public ArrayList<SearchResultBean> findConceptPropertyContains(ArrayList<Integer> ontologyIds,
			List<String> query, String properties[], boolean includeObsolete, int maxToReturn) {
		ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();
		for (Integer ontologyId : ontologyIds) {
			SearchResultBean result = searchNodesForProperties(ontologyId, query, properties, false,
					includeObsolete, maxToReturn, Match_Types.SEARCH_CONTAINS);
			results.add(result);
		}

		return results;
	}

	private String replacePeriod(String s) {
		String temp = "";
		if (s.indexOf(".") < 0)
			return s;
		while (s.indexOf(".") >= 0) {
			temp = temp + s.substring(0, s.indexOf(".")) + "\\.";
			s = s.substring(s.indexOf(".") + 1);
		}
		temp += s;
		return temp;
	}

	private SearchResultBean searchNodesForProperties(Integer ontologyId, List<String> names,
			String[] properties, boolean soundsLike, boolean includeObsolete, int maxToReturn,
			Match_Types algorithm) {
		try {

			String urnAndVersion = getLexGridUrnAndVersion(ontologyId);
			String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
			CodedNodeSet nodes = lbs.getCodingSchemeConcepts(urnVersionArray[0], Constructors
					.createCodingSchemeVersionOrTagFromVersion(urnVersionArray[1]));

			String name = null;
			Iterator<String> namesIte = names.iterator();
			String[] propList = properties;
			String matchAlgorithm = "RegExp";
			if (soundsLike)
				matchAlgorithm = MatchAlgorithms.DoubleMetaphoneLuceneQuery.name();

			while (namesIte.hasNext()) {
				name = (String) namesIte.next();
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
							.createLocalNameList(propList), null, name, matchAlgorithm, null);
				}

			}
			if (includeObsolete) {
				nodes = nodes.restrictToStatus(ActiveOption.ALL, null);
			} else {
				nodes = nodes.restrictToStatus(ActiveOption.ACTIVE_ONLY, null);
			}

			SortOptionList sortCriteria = Constructors.createSortOptionList(new String[] {
					"matchToQuery", "code" });
			ResolvedConceptReferencesIterator matchIterator = nodes.resolve(sortCriteria, null, null);

			ResolvedConceptReferenceList lst = matchIterator.next(maxToReturn);
			SearchResultBean srb = new SearchResultBean();
			srb.setOntologyId(ontologyId);
			srb.setPropertyValueSearchResult(createClassBeanArray(lst));
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());

		}
		return null;

	}

	private SearchResultBean searchNodesForName(Integer ontologyId, List<String> names,
			int maxToReturn, Match_Types algorithm, boolean soundsLike, boolean includeObsolete) {
		try {
			String urnAndVersion = getLexGridUrnAndVersion(ontologyId);
			String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
			CodedNodeSet nodes = lbs.getCodingSchemeConcepts(urnVersionArray[0], Constructors
					.createCodingSchemeVersionOrTagFromVersion(urnVersionArray[1]));
			String name = "";
			Iterator<String> namesIte = names.iterator();

			String matchAlgorithm = "RegExp";

			if (soundsLike)
				matchAlgorithm = MatchAlgorithms.DoubleMetaphoneLuceneQuery.name();

			while (namesIte.hasNext()) {
				name = (String) namesIte.next();
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
						SearchDesignationOption.PREFERRED_ONLY, matchAlgorithm, null);

			}

			// Sort by search engine recommendation & code ...
			SortOptionList sortCriteria = Constructors.createSortOptionList(new String[] {
					"matchToQuery", "code" });
			// Analyze the result ...
			ResolvedConceptReferencesIterator matchIterator = nodes.resolve(sortCriteria, null, null);
			ResolvedConceptReferenceList lst = matchIterator.next(maxToReturn);
			SearchResultBean srb = new SearchResultBean();
			srb.setOntologyId(ontologyId);
			srb.setNameSearchResult(createClassBeanArray(lst));
			return srb;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());

		}
		return null;

	}

	private ClassBean createClassBean(ResolvedConceptReference rcr) {
		ClassBean bean = null;
		CodedEntry entry = rcr.getReferencedEntry();

		if (entry != null
				&& (entry.getIsAnonymous() == null || (entry.getIsAnonymous() != null && !entry
						.getIsAnonymous().booleanValue()))) {
			bean = new ClassBean();
			bean.setId(rcr.getConceptCode());
			if (rcr.getEntityDescription().getContent() != null) {
				bean.setLabel(rcr.getEntityDescription().getContent());
			} else {
				bean.setLabel(getPreferredPresentation(entry));
			}
			bean.addRelation("definition", getDefinition(entry));
			addCodedEntryPropertyValue(entry, bean);

		}
		return bean;
	}

	private ArrayList<ClassBean> createClassBeanArray(ResolvedConceptReferenceList list) {
		ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();
		Enumeration<ResolvedConceptReference> refEnum = list.enumerateResolvedConceptReference();
		ResolvedConceptReference ref = null;
		while (refEnum.hasMoreElements()) {
			ref = (ResolvedConceptReference) refEnum.nextElement();
			ClassBean bean = createClassBean(ref);
			if (bean != null)
				classBeans.add(bean);

		}
		return classBeans;

	}

	private static String getDefinition(CodedEntry entry) {
		// Presentation[] presentation = entry.getPresentation();
		Definition p = null;
		int count = entry.getDefinitionCount();
		for (int i = 0; i < count; i++) {
			p = entry.getDefinition(i);
			// if(p.getIsPreferred().booleanValue())
			return p.getText().getContent();
		}
		return "";

	}

	private static String getPreferredPresentation(CodedEntry entry) {
		Presentation[] presentations = entry.getPresentation();
		for (int i = 0; i < presentations.length; i++) {
			if (presentations[i].getIsPreferred().booleanValue())

				return presentations[i].getText().getContent();
		}
		return "";

	}

	@SuppressWarnings("unchecked")
	private static void addCodedEntryPropertyValue(CodedEntry entry, ClassBean bean) {
		// Presentation[] presentation = entry.getPresentation();
		HashMap<Object, Object> map = bean.getRelations();
		Presentation p = null;
		int count = entry.getPresentationCount();
		List<Object> list = null;

		for (int i = 0; i < count; i++) {
			p = entry.getPresentation(i);

			if (!p.getIsPreferred().booleanValue()) {
				if (p.getDegreeOfFidelity() != null && !p.getDegreeOfFidelity().equals("")) {
					list = (List) map.get(p.getDegreeOfFidelity() + " SYNONYM");

					if (list == null) {
						list = new ArrayList<Object>();
					}

					if (p.getText() != null) {
						list.add(p.getText().getContent());
						map.put(p.getDegreeOfFidelity() + " SYNONYM", list);
					}
				} else {

					if (p.getRepresentationalForm() != null) {
						list = (List) map.get(p.getRepresentationalForm());
						if (list == null) {
							list = new ArrayList<Object>();
						}
						if (p.getText() != null) {
							list.add(p.getText().getContent());
							map.put(p.getRepresentationalForm(), list);

						}
					} else {

						list = (List) map.get("SYNONYM");
						if (list == null) {
							list = new ArrayList<Object>();
						}
						if (p.getText() != null) {
							list.add(p.getText().getContent());
							map.put("SYNONYM", list);

						}
					}

				}
			}
		}

		// handle comment
		Comment c = null;
		count = entry.getCommentCount();
		for (int i = 0; i < count; i++) {
			c = entry.getComment(i);
			list = (List) map.get("Comment");
			if (list == null) {
				list = new ArrayList<Object>();
			}
			if (c.getText() != null) {
				list.add(c.getText().getContent());
				map.put("Comment", list);
			}

		}
		// handle definiton as now definiton is bundled into property
		Definition d = null;
		count = entry.getDefinitionCount();
		for (int i = 0; i < count; i++) {
			d = entry.getDefinition(i);
			list = (List) map.get("Definition");
			if (list == null) {
				list = new ArrayList<Object>();
			}
			if (d.getText() != null) {
				list.add(d.getText().getContent());
				map.put("Definition", list);
			}
		}

		org.LexGrid.commonTypes.Property prop = null;
		count = entry.getConceptPropertyCount();
		for (int i = 0; i < count; i++) {
			prop = entry.getConceptProperty(i);
			if (!(prop instanceof Presentation) && !(prop instanceof Comment)) {

				list = (List) map.get(prop.getProperty());
				if (list == null) {
					list = new ArrayList<Object>();
				}
				prop = entry.getConceptProperty(i);
				if (prop.getText() != null) {
					list.add(prop.getText().getContent());
					map.put(prop.getProperty(), list);
				}
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
			// AssociatedConceptList concepts =
			// association.getAssociatedConcepts();
			// for (int i = 0; i < concepts.getAssociatedConceptCount(); i++) {
			// AssociatedConcept concept= concepts.getAssociatedConcept(i);
			// if (concept !=null) {
			// ClassBean classBean = createClassBean(concept);
			// classBeans.add(classBean);
			// }
			// }

		}
		classBeans.add(current_classBean);
		return classBeans;

	}

	private void createClassBeanArray(Association association, ClassBean current_classBean) {
		AssociatedConceptList concepts = association.getAssociatedConcepts();
		ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();
		for (int i = 0; i < concepts.getAssociatedConceptCount(); i++) {
			AssociatedConcept concept = concepts.getAssociatedConcept(i);
			if (concept != null) {
				ClassBean classBean = createClassBean(concept);
				classBeans.add(classBean);
				// Find and recurse printing for next batch ...
				AssociationList nextLevel = concept.getSourceOf();
				if (nextLevel != null && nextLevel.getAssociationCount() != 0)
					for (int j = 0; j < nextLevel.getAssociationCount(); j++)
						createClassBeanArray(nextLevel.getAssociation(j), classBean);

				// Find and recurse printing for previous batch ...
				AssociationList prevLevel = concept.getTargetOf();
				if (prevLevel != null && prevLevel.getAssociationCount() != 0)
					for (int j = 0; j < prevLevel.getAssociationCount(); j++)
						createClassBeanArray(prevLevel.getAssociation(j), classBean);
			}

		}
		current_classBean.addRelation(association.getDirectionalName(), classBeans);

	}

}
