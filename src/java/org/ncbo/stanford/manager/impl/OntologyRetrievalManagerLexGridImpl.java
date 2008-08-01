package org.ncbo.stanford.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.AssociatedConceptList;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.ActiveOption;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.ConvenienceMethods;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.Utility.LBConstants.MatchAlgorithms;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.commonTypes.PropertyQualifier;
import org.LexGrid.commonTypes.Source;
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
import org.ncbo.stanford.bean.concept.PropertyBean;
import org.ncbo.stanford.bean.search.SearchResultBean;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.manager.AbstractOntologyManagerLexGrid;
import org.ncbo.stanford.manager.OntologyRetrievalManager;
import org.ncbo.stanford.util.constants.ApplicationConstants;

/**
 * A implementation of the OntologyRetrievalManager for ontologies stored in
 * LexGrid.
 * 
 * 
 * @author Pradip Kanjamala
 * 
 */

public class OntologyRetrievalManagerLexGridImpl extends AbstractOntologyManagerLexGrid implements
        OntologyRetrievalManager {

    private static final Log log = LogFactory.getLog(OntologyRetrievalManagerLexGridImpl.class);

    private enum Match_Types {
        SEARCH_STARTS_WITH, SEARCH_ENDS_WITH, SEARCH_CONTAINS, SEARCH_EXACT_MATCH
    };

    private LexBIGService lbs;
    LexBIGServiceConvenienceMethods lbscm;

    public OntologyRetrievalManagerLexGridImpl() throws Exception {
        lbs = LexBIGServiceImpl.defaultInstance();
        lbscm = (LexBIGServiceConvenienceMethods) lbs.getGenericExtension("LexBIGServiceConvenienceMethods");
    }

    public List<String> findProperties(VNcboOntology ncboOntology) throws Exception {
        String urnAndVersion = ncboOntology.getCodingScheme();
        String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
        CodingScheme cs = getCodingScheme(lbs, urnVersionArray[0], urnVersionArray[1]);
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
    public ClassBean findRootConcept(VNcboOntology ncboOntology) throws Exception {
        String schemeName = getLexGridCodingSchemeName(ncboOntology);
        CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);
        // Iterate through all hierarchies ...
        String[] hierarchyIDs = lbscm.getHierarchyIDs(schemeName, csvt);
        String hierarchyId = (hierarchyIDs.length > 0) ? hierarchyIDs[0] : null;

        for (String hierarchy : hierarchyIDs) {
            if (hierarchy.equalsIgnoreCase("IS_A"))
                hierarchyId = hierarchy;
        }
        ResolvedConceptReferenceList rcrl = lbscm.getHierarchyRoots(schemeName, csvt, hierarchyId);
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
    public ClassBean findConceptWithoutRelations(VNcboOntology ncboOntology, String conceptId) throws Exception {
        String schemeName = getLexGridCodingSchemeName(ncboOntology);
        CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);
        // Perform the query ...
        ConceptReferenceList crefs = ConvenienceMethods.createConceptReferenceList(new String[] { conceptId },
                schemeName);
        ResolvedConceptReferenceList matches = lbs.getCodingSchemeConcepts(schemeName, csvt).restrictToStatus(
                ActiveOption.ALL, null).restrictToCodes(crefs).resolveToList(null, null, null, 1);
        // Analyze the result ...
        if (matches.getResolvedConceptReferenceCount() > 0) {
            ResolvedConceptReference ref = (ResolvedConceptReference) matches.enumerateResolvedConceptReference()
                    .nextElement();
            return createClassBean(ref);
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
    public ClassBean findConcept(VNcboOntology ncboOntology, String conceptId) throws Exception {
        String schemeName = getLexGridCodingSchemeName(ncboOntology);
        CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);
        ResolvedConceptReferenceList matches = lbs.getNodeGraph(schemeName, csvt, null).resolveAsList(
                ConvenienceMethods.createConceptReference(conceptId, schemeName), true, true, 0, 1, null, null, null,
                -1);
        // Analyze the result ...
        if (matches.getResolvedConceptReferenceCount() > 0) {
            ResolvedConceptReference ref = (ResolvedConceptReference) matches.enumerateResolvedConceptReference()
                    .nextElement();
            ClassBean classBean = createClassBean(ref);
            // Add the children
            AssociationList childList = getHierarchyLevelNext(schemeName, csvt, conceptId);
            addAssociationListInfoToClassBean(childList, classBean, ApplicationConstants.SUB_CLASS, false);
            // Add the parents
            AssociationList paerntList = getHierarchyLevelPrev(schemeName, csvt, conceptId);
            addAssociationListInfoToClassBean(paerntList, classBean, ApplicationConstants.SUPER_CLASS, false);
            return classBean;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ncbo.stanford.manager.OntologyRetrievalManager#findPathToRoot(org.ncbo.stanford.domain.custom.entity.VNcboOntology,
     *      java.lang.String, boolean)
     */
    public ClassBean findPathToRoot(VNcboOntology ncboOntology, String conceptId, boolean light) throws Exception {
        String schemeName = getLexGridCodingSchemeName(ncboOntology);
        CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);
        String[] hierarchyIDs = lbscm.getHierarchyIDs(schemeName, csvt);
        String hierarchyId = (hierarchyIDs.length > 0) ? hierarchyIDs[0] : null;
        for (String hierarchy : hierarchyIDs) {
            if (hierarchy.equalsIgnoreCase("IS_A"))
                hierarchyId = hierarchy;
        }
        AssociationList pathToRoot = lbscm.getHierarchyPathToRoot(schemeName, csvt, hierarchyId, conceptId, false,
                LexBIGServiceConvenienceMethods.HierarchyPathResolveOption.ALL, null);

        // Recursively reverse the chain ...

        AssociationList pathFromRoot = new AssociationList();
        HashMap<String, EntityDescription> codeToEntityDescriptionMap = new HashMap<String, EntityDescription>();
        for (int i = pathToRoot.getAssociationCount() - 1; i >= 0; i--) {
            reverseAssoc(ncboOntology, pathToRoot.getAssociation(i), pathFromRoot, codeToEntityDescriptionMap);
        }

        ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();
        boolean includeChildren = !light;
        for (int i = 0; i < pathFromRoot.getAssociationCount(); i++) {
            Association association = pathFromRoot.getAssociation(i);
            ConceptReference cr = association.getAssociationReference();
            ClassBean classBean = createClassBeanWithChildCount(ncboOntology, cr);
            classBeans.add(classBean);
            addAssociationListInfoToClassBean(pathFromRoot, classBean, ApplicationConstants.SUB_CLASS, includeChildren);

        }
        return createThingClassBean(classBeans);
    }



    /**
     * 
     * @param ncboOntology
     * @param conceptId
     * @param light
     * @return
     * @throws Exception
     */
    public ClassBean findPathFromRoot(VNcboOntology ncboOntology, String conceptId, boolean light) throws Exception {
        String schemeName = getLexGridCodingSchemeName(ncboOntology);
        CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);

        String[] hierarchyIDs = lbscm.getHierarchyIDs(schemeName, csvt);
        String hierarchyId = (hierarchyIDs.length > 0) ? hierarchyIDs[0] : null;
        for (String hierarchy : hierarchyIDs) {
            if (hierarchy.equalsIgnoreCase("IS_A"))
                hierarchyId = hierarchy;
        }
        AssociationList associations = lbscm.getHierarchyPathToRoot(schemeName, csvt, hierarchyId, conceptId, false,
                LexBIGServiceConvenienceMethods.HierarchyPathResolveOption.ALL, null);
        ClassBean conceptClass = findConceptWithoutRelations(ncboOntology, conceptId);
        boolean includeChildren = !light;
        addAssociationListInfoToClassBean(associations, conceptClass, ApplicationConstants.SUPER_CLASS, includeChildren);
        return conceptClass;

    }

    /**
     * 
     * @param ncboOntology
     * @param conceptId
     * @return
     * @throws Exception
     */
    public List<ClassBean> findParent(VNcboOntology ncboOntology, String conceptId) throws Exception {
        String schemeName = getLexGridCodingSchemeName(ncboOntology);
        CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);
        AssociationList associations = getHierarchyLevelPrev(schemeName, csvt, conceptId);
        ClassBean conceptClass = findConceptWithoutRelations(ncboOntology, conceptId);
        ArrayList<ClassBean> classBeans = createClassBeanArray(associations, conceptClass,
                ApplicationConstants.SUPER_CLASS, false);
        return classBeans;
    }

    /**
     * 
     * @param ncboOntology
     * @param conceptId
     * @return
     * @throws Exception
     */
    public List<ClassBean> findChildren(VNcboOntology ncboOntology, String conceptId) throws Exception {
        String schemeName = getLexGridCodingSchemeName(ncboOntology);
        CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);
        AssociationList associations = getHierarchyLevelNext(schemeName, csvt, conceptId);
        ClassBean conceptClass = findConceptWithoutRelations(ncboOntology, conceptId);
        ArrayList<ClassBean> classBeans = createClassBeanArray(associations, conceptClass,
                ApplicationConstants.SUB_CLASS, false);
        return classBeans;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ncbo.stanford.manager.OntologyRetrievalManager#findConceptNameExact(java.util.List,
     *      java.lang.String, boolean, int)
     */
    public ArrayList<SearchResultBean> findConceptNameExact(List<VNcboOntology> ontologyVersions, String query,
            boolean includeObsolete, int maxToReturn) {
        ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();

        for (VNcboOntology ontologyVersion : ontologyVersions) {
            SearchResultBean result = searchNodesForName(ontologyVersion, query, maxToReturn,
                    Match_Types.SEARCH_EXACT_MATCH, false, includeObsolete);
            results.add(result);
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ncbo.stanford.manager.OntologyRetrievalManager#findConceptNameStartsWith(java.util.List,
     *      java.lang.String, boolean, int)
     */
    public List<SearchResultBean> findConceptNameStartsWith(List<VNcboOntology> ontologyVersions, String query,
            boolean includeObsolete, int maxToReturn) {
        ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();

        for (VNcboOntology ontologyVersion : ontologyVersions) {
            SearchResultBean result = searchNodesForName(ontologyVersion, query, maxToReturn,
                    Match_Types.SEARCH_STARTS_WITH, false, includeObsolete);
            results.add(result);
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ncbo.stanford.manager.OntologyRetrievalManager#findConceptNameContains(java.util.List,
     *      java.lang.String, boolean, int)
     */
    public List<SearchResultBean> findConceptNameContains(List<VNcboOntology> ontologyVersions, String query,
            boolean includeObsolete, int maxToReturn) {
        ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();

        for (VNcboOntology ontologyVersion : ontologyVersions) {
            SearchResultBean result = searchNodesForName(ontologyVersion, query, maxToReturn,
                    Match_Types.SEARCH_CONTAINS, false, includeObsolete);
            results.add(result);
        }
        return results;
    }

    /**
     * Search through the concept properties that are in the property names
     * array for a property value that exactly matches the query string. All the
     * ontologies in the NcboOntologies list are searched. If the property names
     * array is null, we search through all the properties.
     * 
     * @param ontologyVersions
     * @param query
     * @param property_names
     * @param includeObsolete
     * @param maxToReturn
     * @return
     */
    public List<SearchResultBean> findConceptPropertyExact(List<VNcboOntology> ontologyVersions, String query,
            String property_names[], boolean includeObsolete, int maxToReturn) {
        ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();

        for (VNcboOntology ontologyVersion : ontologyVersions) {
            SearchResultBean result = searchNodesForProperties(ontologyVersion, query, property_names, false,
                    includeObsolete, maxToReturn, Match_Types.SEARCH_EXACT_MATCH);
            results.add(result);
        }

        return results;
    }

    /**
     * Search through the concept properties that are in the property names
     * array for a property value that starts with the query string. All the
     * ontologies in the NcboOntologies list are searched. If the property names
     * array is null, we search through all the properties.
     * 
     * @param ontologyVersions
     * @param query
     * @param property_names
     * @param includeObsolete
     * @param maxToReturn
     * @return
     */
    public List<SearchResultBean> findConceptPropertyStartsWith(List<VNcboOntology> ontologyVersions, String query,
            String property_names[], boolean includeObsolete, int maxToReturn) {
        ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();

        for (VNcboOntology ontologyVersion : ontologyVersions) {
            SearchResultBean result = searchNodesForProperties(ontologyVersion, query, property_names, false,
                    includeObsolete, maxToReturn, Match_Types.SEARCH_STARTS_WITH);
            results.add(result);
        }
        return results;
    }

    /**
     * Search through the concept properties that are in the property names
     * array for a property value that contains the query string. All the
     * ontologies in the NcboOntologies list are searched. If the property names
     * array is null, we search through all the properties.
     * 
     * @param ontologyVersions
     * @param query
     * @param property_names
     * @param includeObsolete
     * @param maxToReturn
     * @return
     */
    public List<SearchResultBean> findConceptPropertyContains(List<VNcboOntology> ontologyVersions, String query,
            boolean includeObsolete, int maxToReturn) {
        ArrayList<SearchResultBean> results = new ArrayList<SearchResultBean>();

        for (VNcboOntology ontologyVersion : ontologyVersions) {
            SearchResultBean result = searchNodesForProperties(ontologyVersion, query, null, false, includeObsolete,
                    maxToReturn, Match_Types.SEARCH_CONTAINS);
            results.add(result);
        }

        return results;
    }

    private EntityDescription getEntityDescription(VNcboOntology ncboOntology, String conceptId) {
        String schemeName = getLexGridCodingSchemeName(ncboOntology);
        CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);
        try {
            // Perform the query ...
            ConceptReferenceList crefs = ConvenienceMethods.createConceptReferenceList(new String[] { conceptId },
                    schemeName);
            CodedNodeSet nodes = lbs.getCodingSchemeConcepts(schemeName, csvt).restrictToCodes(crefs);
            ResolvedConceptReferenceList matches = nodes.resolveToList(null, null, null, null, false, -1);
            ResolvedConceptReference rcr = matches.getResolvedConceptReference()[0];
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
    protected AssociationList reverseAssoc(VNcboOntology ncboOntology, Association assoc, AssociationList addTo,
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
            acFromRef.setEntityDescription(codeToEntityDescriptionMap.get(acRef.getConceptCode()));
        // Otherwise retrieve on demand ...
        else {
            EntityDescription ed = getEntityDescription(ncboOntology, acRef.getConceptCode());
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
                    rAssoc.setDirectionalName(lbscm.isForwardName(schemeName, csvt, dirName) ? lbscm
                            .getAssociationReverseName(assoc.getAssociationName(), schemeName, csvt) : lbscm
                            .getAssociationForwardName(assoc.getAssociationName(), schemeName, csvt));
                } catch (LBException e) {
                }

            // Save code desc for future reference when setting up
            // concept references in recursive calls ...
            codeToEntityDescriptionMap.put(ac.getConceptCode(), ac.getEntityDescription());

            AssociationList sourceOf = ac.getSourceOf();
            if (sourceOf != null)
                for (Association sourceAssoc : sourceOf.getAssociation()) {
                    AssociationList pos = reverseAssoc(ncboOntology, sourceAssoc, addTo, codeToEntityDescriptionMap);
                    pos.addAssociation(rAssoc);
                }
            else
                addTo.addAssociation(rAssoc);
        }
        return acSources;
    }    
    
    /**
     * 
     * @param s
     * @return
     */
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

    /**
     * This is the common method that imnplements searching of properties of
     * concepts.
     * 
     * @param ncboOntology
     * @param search_string
     * @param property_names
     * @param soundsLike
     * @param includeObsolete
     * @param maxToReturn
     * @param algorithm
     * @return
     */
    private SearchResultBean searchNodesForProperties(VNcboOntology ncboOntology, String search_string,
            String[] property_names, boolean soundsLike, boolean includeObsolete, int maxToReturn, Match_Types algorithm) {
        try {
            String urnAndVersion = ncboOntology.getCodingScheme();
            String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
            CodedNodeSet nodes = lbs.getCodingSchemeConcepts(urnVersionArray[0], Constructors
                    .createCodingSchemeVersionOrTagFromVersion(urnVersionArray[1]));

            String matchAlgorithm = "RegExp";
            if (soundsLike)
                matchAlgorithm = MatchAlgorithms.DoubleMetaphoneLuceneQuery.name();
            search_string = search_string.toLowerCase();
            if (!soundsLike) {
                search_string = replacePeriod(search_string);
                switch (algorithm) {
                case SEARCH_STARTS_WITH:
                    search_string = search_string + ".*";
                    break;
                case SEARCH_ENDS_WITH:
                    search_string = ".*" + search_string;
                    break;
                case SEARCH_CONTAINS:
                    search_string = ".*" + search_string + ".*";
                    break;
                case SEARCH_EXACT_MATCH:
                    break;
                }

                matchAlgorithm = "RegExp";
            }

            if (property_names != null && property_names.length > 0) {
                nodes = nodes.restrictToMatchingProperties(Constructors.createLocalNameList(property_names), null,
                        search_string, matchAlgorithm, null);
            } else {
                // We'll search all the properties
                PropertyType[] propTypes = { PropertyType.COMMENT, PropertyType.DEFINITION, PropertyType.INSTRUCTION,
                        PropertyType.PRESENTATION, PropertyType.GENERIC };
                nodes = nodes.restrictToMatchingProperties(null, propTypes, search_string, matchAlgorithm, null);
            }

            if (includeObsolete) {
                nodes = nodes.restrictToStatus(ActiveOption.ALL, null);
            } else {
                nodes = nodes.restrictToStatus(ActiveOption.ACTIVE_ONLY, null);
            }

            SortOptionList sortCriteria = Constructors.createSortOptionList(new String[] { "matchToQuery", "code" });
            ResolvedConceptReferencesIterator matchIterator = nodes.resolve(sortCriteria, null, null, null, false);
            ResolvedConceptReferenceList lst = matchIterator.next(maxToReturn);
            SearchResultBean srb = new SearchResultBean();
            srb.setOntologyVersionId(ncboOntology.getId());
            srb.setProperties(createClassBeanArray(lst, false));

            return srb;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * This is the common method that imnplements searching the designations
     * (Presentations) of concepts.
     * 
     * @param ncboOntology
     * @param search_string
     * @param maxToReturn
     * @param algorithm
     * @param soundsLike
     * @param includeObsolete
     * @return
     */
    private SearchResultBean searchNodesForName(VNcboOntology ncboOntology, String search_string, int maxToReturn,
            Match_Types algorithm, boolean soundsLike, boolean includeObsolete) {
        try {
            String urnAndVersion = ncboOntology.getCodingScheme();
            String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
            CodedNodeSet nodes = lbs.getCodingSchemeConcepts(urnVersionArray[0], Constructors
                    .createCodingSchemeVersionOrTagFromVersion(urnVersionArray[1]));
            String matchAlgorithm = "RegExp";
            if (soundsLike) {
                matchAlgorithm = MatchAlgorithms.DoubleMetaphoneLuceneQuery.name();
            }
            search_string = search_string.toLowerCase();
            if (!soundsLike) {
                search_string = replacePeriod(search_string);
                switch (algorithm) {
                case SEARCH_STARTS_WITH:
                    search_string = search_string + ".*";
                    break;
                case SEARCH_ENDS_WITH:
                    search_string = ".*" + search_string;
                    break;
                case SEARCH_CONTAINS:
                    search_string = ".*" + search_string + ".*";
                    break;
                case SEARCH_EXACT_MATCH:
                    break;
                }

                matchAlgorithm = "RegExp";
            }

            nodes = nodes.restrictToMatchingDesignations(search_string, SearchDesignationOption.PREFERRED_ONLY,
                    matchAlgorithm, null);

            // Sort by search engine recommendation & code ...
            SortOptionList sortCriteria = Constructors.createSortOptionList(new String[] { "matchToQuery", "code" });
            // Analyze the result ...
            ResolvedConceptReferencesIterator matchIterator = nodes.resolve(sortCriteria, null, null, null, false);
            ResolvedConceptReferenceList lst = matchIterator.next(maxToReturn);
            SearchResultBean srb = new SearchResultBean();
            srb.setOntologyVersionId(ncboOntology.getId());
            srb.setNames(createClassBeanArray(lst, false));
            return srb;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 
     * @param schemeName
     * @param csvt
     * @param conceptId
     * @return
     * @throws Exception
     */
    private AssociationList getHierarchyLevelPrev(String schemeName, CodingSchemeVersionOrTag csvt, String conceptId)
            throws Exception {

        String[] hierarchyIDs = lbscm.getHierarchyIDs(schemeName, csvt);
        String hierarchyId = (hierarchyIDs.length > 0) ? hierarchyIDs[0] : null;
        for (String hierarchy : hierarchyIDs) {
            if (hierarchy.equalsIgnoreCase("IS_A")) {
                hierarchyId = hierarchy;
            }
        }
        AssociationList associations = lbscm.getHierarchyLevelPrev(schemeName, csvt, hierarchyId, conceptId, false,
                null);
        return associations;
    }

    /**
     * 
     * @param schemeName
     * @param csvt
     * @param conceptId
     * @return
     * @throws Exception
     */
    private AssociationList getHierarchyLevelNext(String schemeName, CodingSchemeVersionOrTag csvt, String conceptId)
            throws Exception {
        String[] hierarchyIDs = lbscm.getHierarchyIDs(schemeName, csvt);
        String hierarchyId = (hierarchyIDs.length > 0) ? hierarchyIDs[0] : null;

        for (String hierarchy : hierarchyIDs) {
            if (hierarchy.equalsIgnoreCase("IS_A")) {
                hierarchyId = hierarchy;
            }
        }
        AssociationList associations = lbscm.getHierarchyLevelNext(schemeName, csvt, hierarchyId, conceptId, false,
                null);
        return associations;
    }

    /**
     * 
     * @param source
     * @return
     */
    private PropertyBean createPropertyBean(Source source) {
        PropertyBean bean = new PropertyBean();
        bean.setId("source");
        bean.setLabel(source.getContent());
        HashMap<Object, Object> map = bean.getRelations();
        addStringToHashMap(map, "Role", source.getRole());
        addStringToHashMap(map, "SubRef", source.getSubRef());
        return bean;
    }

    /**
     * @param qualifier
     * @return
     */
    private PropertyBean createPropertyBean(PropertyQualifier qualifier) {
        PropertyBean bean = new PropertyBean();
        bean.setId(qualifier.getPropertyQualifierId());
        bean.setLabel(qualifier.getContent());
        HashMap<Object, Object> map = bean.getRelations();
        addArrayToHashMap(map, "AnyObject", qualifier.getAnyObject());
        return bean;
    }

    /**
     * 
     * @param prop
     * @return
     */
    private PropertyBean createPropertyBean(Property prop) {
        PropertyBean bean = new PropertyBean();
        bean.setId(prop.getPropertyName());
        bean.setLabel(prop.getText().getContent());
        HashMap<Object, Object> map = bean.getRelations();
        addStringToHashMap(map, "Language", prop.getLanguage());
        addStringToHashMap(map, "Format", prop.getFormat());
        addArrayToHashMap(map, "UsageContext", prop.getUsageContext());
        addArrayToHashMap(map, "PropertyQualifier", prop.getPropertyQualifier());
        addArrayToHashMap(map, "Source", prop.getSource());

        if (prop instanceof Presentation) {
            Presentation presentation = (Presentation) prop;
            addStringToHashMap(map, "DegreeOfFidelity", presentation.getDegreeOfFidelity());
            addStringToHashMap(map, "IsPreferred", presentation.getIsPreferred().toString());
            addStringToHashMap(map, "RepresentationalForm", presentation.getRepresentationalForm());
        }

        if (prop instanceof Definition) {
            Definition definition = (Definition) prop;
            addStringToHashMap(map, "IsPreferred", definition.getIsPreferred().toString());
        }
        return bean;
    }

    /**
     * 
     * @param ncboOntology
     * @param cr
     * @return
     */
    private ClassBean createClassBeanWithChildCount(VNcboOntology ncboOntology, ConceptReference cr) {
        CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ncboOntology);
        ResolvedConceptReference rcr = new ResolvedConceptReference();
        rcr.setCodingScheme(cr.getCodingScheme());
        rcr.setConceptCode(cr.getConceptCode());
        EntityDescription ed = getEntityDescription(ncboOntology, cr.getConceptCode());
        rcr.setEntityDescription(ed);
        return createClassBeanWithChildCount(rcr);
    }

    /**
     * 
     * @param rcr
     * @return
     */
    private ClassBean createClassBeanWithChildCount(ResolvedConceptReference rcr) {
        ClassBean bean = createClassBean(rcr);
        // Add the children
        String schemeName = rcr.getCodingScheme();
        String version = rcr.getCodingSchemeVersion();
        String conceptId = rcr.getConceptCode();
        CodingSchemeVersionOrTag csvt = Constructors.createCodingSchemeVersionOrTagFromVersion(version);
        try {
            AssociationList childList = getHierarchyLevelNext(schemeName, csvt, conceptId);
            bean.addRelation(ApplicationConstants.CHILD_COUNT, getChildCount(childList));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bean;
    }

    
    /**
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
                || (entry.getIsAnonymous() != null && !entry.getIsAnonymous().booleanValue())) {
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
    private ClassBean createThingClassBeanWithCount(ResolvedConceptReferenceList list) {
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
        classBean.addRelation(ApplicationConstants.CHILD_COUNT, classBeans.size());
        return classBean;
    }

    
    /**
     * 
     * @param list
     * @param includeCount
     * @return
     */
    private ArrayList<ClassBean> createClassBeanArray(ResolvedConceptReferenceList list, boolean includeCount) {
        ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();
        Enumeration<ResolvedConceptReference> refEnum = list.enumerateResolvedConceptReference();
        ResolvedConceptReference ref = null;

        while (refEnum.hasMoreElements()) {
            ref = (ResolvedConceptReference) refEnum.nextElement();
            ClassBean bean;
            if (includeCount) {
                bean = createClassBeanWithChildCount(ref);
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
    private String getDefinition(Concept entry) {
        Definition d = null;
        int count = entry.getDefinitionCount();
        for (int i = 0; i < count; i++) {
            d = entry.getDefinition(i);
            if (d.getIsPreferred().booleanValue())
                return d.getText().getContent();
        }
        return "";
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
    private static void addStringToHashMapsArrayList(HashMap<Object, Object> map, String key, String value) {
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
     * @param map
     * @param key
     * @param value
     */
    private static void addStringToHashMap(HashMap<Object, Object> map, String key, String value) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            map.put(key, value);
        }
    }

    /**
     * 
     * @param map
     * @param key
     * @param properties
     */
    private void addArrayToHashMap(HashMap<Object, Object> map, String key, Property[] properties) {
        if (StringUtils.isNotBlank(key) && properties != null && properties.length > 0) {
            ArrayList<PropertyBean> beans = new ArrayList<PropertyBean>();
            for (int i = 0; i < properties.length; i++) {
                PropertyBean bean = createPropertyBean(properties[i]);
                beans.add(bean);
            }
            map.put(key, beans);
        }
    }

    
    /**
     * 
     * @param map
     * @param key
     * @param sources
     */
    private void addArrayToHashMap(HashMap<Object, Object> map, String key, Source[] sources) {
        if (StringUtils.isNotBlank(key) && sources != null && sources.length > 0) {
            ArrayList<PropertyBean> beans = new ArrayList<PropertyBean>();
            for (int i = 0; i < sources.length; i++) {
                PropertyBean bean = createPropertyBean(sources[i]);
                beans.add(bean);
            }
            map.put(key, beans);
        }
    }

    /**
     * 
     * @param map
     * @param key
     * @param qualifiers
     */
    private void addArrayToHashMap(HashMap<Object, Object> map, String key, PropertyQualifier[] qualifiers) {
        if (StringUtils.isNotBlank(key) && qualifiers != null && qualifiers.length > 0) {
            ArrayList<PropertyBean> beans = new ArrayList<PropertyBean>();
            for (int i = 0; i < qualifiers.length; i++) {
                PropertyBean bean = createPropertyBean(qualifiers[i]);
                beans.add(bean);
            }
            map.put(key, beans);
        }
    }

    /**
     * 
     * @param map
     * @param key
     * @param values
     */
    private void addArrayToHashMap(HashMap<Object, Object> map, String key, Object[] values) {
        if (StringUtils.isNotBlank(key) && values != null && values.length > 0) {
            map.put(key, Arrays.asList(values));
        }
    }

    /**
     * Populate the ClassBean's map with the Concept's properties
     * 
     * @param entry
     * @param bean
     */
    private void addConceptPropertyValue(Concept entry, ClassBean bean) {
        HashMap<Object, Object> map = bean.getRelations();
        addArrayToHashMap(map, "Presentation", entry.getPresentation());
        addArrayToHashMap(map, "Definition", entry.getDefinition());
        addArrayToHashMap(map, "Instruction", entry.getInstruction());
        addArrayToHashMap(map, "Comment", entry.getComment());
        addArrayToHashMap(map, "ConceptProperty", entry.getConceptProperty());
    }

    /**
     * 
     * @param list
     * @param current_classBean
     * @param hierarchy_relationName
     * @param includeChildren
     * @return
     */
    private ArrayList<ClassBean> createClassBeanArray(AssociationList list, ClassBean current_classBean,
            String hierarchy_relationName, boolean includeChildren) {
        ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();
        addAssociationListInfoToClassBean(list, current_classBean, hierarchy_relationName, includeChildren);
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
    private void addAssociationListInfoToClassBean(AssociationList list, ClassBean current_classBean,
            String hierarchy_relationName, boolean includeChildren) {
        if (list == null || current_classBean == null) {
            return;
        }
        Enumeration<Association> assocEnum = list.enumerateAssociation();
        Association association = null;
        while (assocEnum.hasMoreElements()) {
            association = (Association) assocEnum.nextElement();
            addAssociationInfoToClassBean(association, current_classBean, hierarchy_relationName, includeChildren);
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
            AssociatedConceptList assocConceptList = association.getAssociatedConcepts();
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
    private void addAssociationInfoToClassBean(Association association, ClassBean current_classBean,
            String hierarchy_relationName, boolean includeChildren) {
        AssociatedConceptList assocConceptList = association.getAssociatedConcepts();
        ArrayList<ClassBean> classBeans = new ArrayList<ClassBean>();
        for (int i = 0; i < assocConceptList.getAssociatedConceptCount(); i++) {
            AssociatedConcept assocConcept = assocConceptList.getAssociatedConcept(i);
            if (assocConcept != null) {
                ClassBean classBean = createClassBeanWithChildCount(assocConcept);
                classBeans.add(classBean);
                if (includeChildren) {
                    // Add the children
                    String schemeName = assocConcept.getCodingScheme();
                    String version = assocConcept.getCodingSchemeVersion();
                    String conceptId = assocConcept.getConceptCode();
                    CodingSchemeVersionOrTag csvt = Constructors.createCodingSchemeVersionOrTagFromVersion(version);
                    try {
                        AssociationList childList = getHierarchyLevelNext(schemeName, csvt, conceptId);
                        addAssociationListInfoToClassBean(childList, classBean, ApplicationConstants.SUB_CLASS, false);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                // Find and recurse printing for next batch ...
                AssociationList nextLevel = assocConcept.getSourceOf();
                if (nextLevel != null && nextLevel.getAssociationCount() != 0)
                    for (int j = 0; j < nextLevel.getAssociationCount(); j++) {                      
                        addAssociationInfoToClassBean(nextLevel.getAssociation(j), classBean, hierarchy_relationName,
                                includeChildren);
                    }

                // Find and recurse printing for previous batch ...
                AssociationList prevLevel = assocConcept.getTargetOf();
                if (prevLevel != null && prevLevel.getAssociationCount() != 0)
                    for (int j = 0; j < prevLevel.getAssociationCount(); j++) {                       
                        addAssociationInfoToClassBean(prevLevel.getAssociation(j), classBean, hierarchy_relationName,
                                includeChildren);
                    }
            }
        }

        String dirName = association.getDirectionalName();
        if (StringUtils.isBlank(dirName)) {
            dirName = "[R]" + association.getAssociationName();
        }
        current_classBean.addRelation(dirName, classBeans);
        addHierarchyRelationName(current_classBean, classBeans, hierarchy_relationName);
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
    private void addHierarchyRelationName(ClassBean bean, ArrayList<ClassBean> beanlist, String hierarchy_name) {
        // If no hierarchy_name is provided, we assume that special BioPortal
        // relations do not have to be setup.
        if (StringUtils.isBlank(hierarchy_name)) {
            return;
        }
        Object value = bean.getRelations().get(hierarchy_name);
        if (value != null && value instanceof ArrayList) {
            // Ensure we do not add duplicates
            Set<ClassBean> set = new HashSet<ClassBean>();
            List<ClassBean> list = (List<ClassBean>) value;
            set.addAll(list);
            set.addAll((List<ClassBean>) beanlist);

            // avoid overhead of clear if not needed
            if (set.size() < list.size()) {
                list.clear();
                list.addAll(set);
            }
            bean.addRelation(hierarchy_name, list);
            if (ApplicationConstants.SUB_CLASS.equalsIgnoreCase(hierarchy_name)) {
                bean.addRelation(ApplicationConstants.CHILD_COUNT, list.size());
            }

        } else {
            bean.addRelation(hierarchy_name, beanlist);
            if (ApplicationConstants.SUB_CLASS.equalsIgnoreCase(hierarchy_name)) {
                bean.addRelation(ApplicationConstants.CHILD_COUNT, beanlist.size());
            }
        }
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
                    addStringToHashMapsArrayList(map, key, p.getText().getContent());
                } else if (StringUtils.isNotBlank(p.getRepresentationalForm())) {
                    addStringToHashMapsArrayList(map, p.getRepresentationalForm(), p.getText().getContent());
                } else {
                    addStringToHashMapsArrayList(map, "SYNONYM", p.getText().getContent());
                }
            }
        }

        // handle comment
        Comment c = null;
        count = entry.getCommentCount();
        for (int i = 0; i < count; i++) {
            c = entry.getComment(i);
            addStringToHashMapsArrayList(map, "Comment", c.getText().getContent());
        }

        // handle definitions
        Definition d = null;
        count = entry.getDefinitionCount();
        for (int i = 0; i < count; i++) {
            d = entry.getDefinition(i);
            addStringToHashMapsArrayList(map, "Definition", d.getText().getContent());
        }

        // handle concept properties
        ConceptProperty prop = null;
        count = entry.getConceptPropertyCount();
        for (int i = 0; i < count; i++) {
            prop = entry.getConceptProperty(i);
            String key = prop.getPropertyName();
            if (StringUtils.isNotBlank(key)) {
                addStringToHashMapsArrayList(map, key, prop.getText().getContent());
            }
        }

    }

}
