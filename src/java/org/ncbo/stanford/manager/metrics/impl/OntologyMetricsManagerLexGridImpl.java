package org.ncbo.stanford.manager.metrics.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.naming.SupportedProperty;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.manager.AbstractOntologyManagerLexGrid;
import org.ncbo.stanford.manager.metrics.OntologyMetricsManager;

/**
 * A implementation of the OntologyMetricsManager for ontologies stored in
 * LexGrid.
 * 
 * 
 * @author Pradip Kanjamala
 * 
 */

public class OntologyMetricsManagerLexGridImpl extends
		AbstractOntologyManagerLexGrid implements OntologyMetricsManager {

	private int maxSubClasses_;
	private int maxDepth;

	private static final Log log = LogFactory
			.getLog(OntologyMetricsManagerLexGridImpl.class);

	public OntologyMetricsManagerLexGridImpl() throws Exception {
		lbs = LexBIGServiceImpl.defaultInstance();
		lbscm = (LexBIGServiceConvenienceMethods) lbs
				.getGenericExtension("LexBIGServiceConvenienceMethods");
	}

	public OntologyMetricsBean extractOntologyMetrics(OntologyBean ontologyBean)
			throws Exception {
		maxSubClasses_ = OntologyMetricsManager.GOOD_DESIGN_SUBCLASS_LIMIT;

		String schemeName = getLexGridCodingSchemeName(ontologyBean);
		if (StringUtils.isBlank(schemeName)) {
			log
					.warn("Can not process request when the codingSchemeURI is blank");
			return null;
		}
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyBean);
		OntologyMetricsBean omb = new OntologyMetricsBean();
		ResolvedConceptReferencesIterator iterator = lbs
				.getCodingSchemeConcepts(schemeName, csvt).resolve(null, null,
						null, null, false);

		omb.setNumberOfProperties(findNumberOfProperties(ontologyBean));

		ArrayList<String> noDescConcepts = new ArrayList<String>();
		ArrayList<String> oneSubConcepts = new ArrayList<String>();
		HashMap<String, Integer> manySubConcepts = new HashMap<String, Integer>();
		String description = "";
		int numberOfSiblings = 0;
		int maxNumberOfSiblings = 0;
		int totalNumberSiblings = 0;
		int totalNumberParents = 0;
		while (iterator.hasNext()) {
			ResolvedConceptReference ref = iterator.next();			
			AssociationList al = getHierarchyLevelNext(schemeName, csvt, ref.getCode());
			numberOfSiblings= getChildCount(al);
			totalNumberSiblings +=  numberOfSiblings;
			if (numberOfSiblings > maxNumberOfSiblings) {
				maxNumberOfSiblings = numberOfSiblings;
			}
			if (numberOfSiblings == 1) {
				oneSubConcepts.add(ref.getCode());
			}
			if (numberOfSiblings > maxSubClasses_) {
				manySubConcepts.put(ref.getCode(), numberOfSiblings);
			}
			totalNumberParents++;

			description = ref.getEntityDescription().getContent();
			if (description == null || description.equals("")) {
				noDescConcepts.add(ref.getCode());
			}
		}

		computeTopologicalMaxDepth(ontologyBean);
		omb.setId(ontologyBean.getId());
		omb.setNumberOfClasses(totalNumberParents);
		omb.setClassesWithNoDocumentation(noDescConcepts);
		omb.setMaximumNumberOfSiblings(maxNumberOfSiblings);
		int avgNumOfSiblings= (int)Math.round((float)totalNumberSiblings/ (float)totalNumberParents);
		omb.setAverageNumberOfSiblings(avgNumOfSiblings);
		omb.setClassesWithOneSubclass(oneSubConcepts);
		omb.setClassesWithMoreThanXSubclasses(manySubConcepts);
		omb.setMaximumDepth(maxDepth);
		
		// not done
		omb.setNumberOfAxioms(0);
		omb.setNumberOfIndividuals(0);		
		omb.setClassesWithNoAuthor(new ArrayList<String>());
		omb.setClassesWithMoreThanOnePropertyValue(new ArrayList<String>());

		return omb;
	}

	public int findNumberOfProperties(OntologyBean ontologyBean)
			throws Exception {
		String urnAndVersion = ontologyBean.getCodingScheme();
		String urnVersionArray[] = splitUrnAndVersion(urnAndVersion);
		CodingScheme cs = getCodingScheme(lbs, urnVersionArray[0],
				urnVersionArray[1]);
		SupportedProperty[] sp = cs.getMappings().getSupportedProperty();

		return sp.length;
	}

	private void computeTopologicalMaxDepth(OntologyBean ontologyBean) throws Exception {
		maxDepth = 1;
		String schemeName = getLexGridCodingSchemeName(ontologyBean);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyBean);
		ResolvedConceptReferenceList rcrl = getHierarchyRootConcepts(
				schemeName, csvt, true);
		List<String> topologicalList= new ArrayList<String>();
		Set<String> visitedSet= new HashSet<String>();
		for (ResolvedConceptReference rcr : rcrl.getResolvedConceptReference()) {
			topologicalSort(schemeName, csvt, rcr, visitedSet,  topologicalList);

		}
		
		//Reverse this list to get actual topological list
		Collections.reverse(topologicalList);
		maxDepth = dagLongestPath(schemeName, csvt, topologicalList);
	}	
	
	
	//Algorithm from: http://en.wikipedia.org/wiki/Topological_sorting
	//The topological list returned is in reverse order, so we would need to reverse this
	private void topologicalSort(String schemeName,
			CodingSchemeVersionOrTag csvt, ConceptReference cr,
			Set<String> visitedSet, List<String> topologicalList)
			throws Exception {
		String conceptId = cr.getCode();
		if (visitedSet.contains(conceptId))
			return;
		visitedSet.add(conceptId);
		AssociationList al = getHierarchyLevelNext(schemeName, csvt, conceptId);
		for (Association association : al.getAssociation()) {
			for (AssociatedConcept ac : association.getAssociatedConcepts()
					.getAssociatedConcept()) {
				if (ac != null) {
					topologicalSort(schemeName, csvt, ac, visitedSet,
							topologicalList);

				}
			}
		}
		topologicalList.add(conceptId);
	}

	
	//Algorithm from: http://en.wikipedia.org/wiki/Longest_path_problem
	int dagLongestPath(String schemeName, CodingSchemeVersionOrTag csvt,
			List<String> topologicalOrder) throws Exception {
		HashMap<String, Integer> lengthMap = new HashMap<String, Integer>();
		for (String conceptId : topologicalOrder) {
			lengthMap.put(conceptId, 0);
		}
		for (String sourceConceptId : topologicalOrder) {
			AssociationList al = getHierarchyLevelNext(schemeName, csvt,
					sourceConceptId);
			for (Association association : al.getAssociation()) {
				for (AssociatedConcept ac : association.getAssociatedConcepts()
						.getAssociatedConcept()) {
					if (ac != null) {
						String targetCode= ac.getCode();
						if (lengthMap.get(targetCode) <= lengthMap.get(sourceConceptId) + 1) {
							lengthMap.put(targetCode, lengthMap.get(sourceConceptId) + 1);
						}
					}
	
				}
			}
		}
		return Collections.max(lengthMap.values()).intValue();
	}

	private void depricatedGetMaxDepth(String schemeName, CodingSchemeVersionOrTag csvt,
			ConceptReference cr, int currentDepth, Set<String> visitedSet)
			throws Exception {
		String conceptId = cr.getCode();
		if (visitedSet.contains(conceptId))
			return;
		//Set<String> newVisitedSet = ((Set<String>)((HashSet<String>)visitedSet).clone());
	    
		//Set<String> newVisitedSet= new HashSet<String>(visitedSet);
		Set<String> newVisitedSet = visitedSet;
		newVisitedSet.add(conceptId);
	
		if (currentDepth > maxDepth) {
			maxDepth = currentDepth;
		}
	
		AssociationList al = getHierarchyLevelNext(schemeName, csvt, conceptId);
		for (Association association : al.getAssociation()) {
			for (AssociatedConcept ac : association.getAssociatedConcepts()
					.getAssociatedConcept()) {
				if (ac != null) {
					depricatedGetMaxDepth(schemeName, csvt, ac, currentDepth + 1,
							newVisitedSet);
	
				}
			}
		}
	
	}

	private void deprecatedComputeMaxDepth(OntologyBean ontologyBean) throws Exception {
		maxDepth = 1;
		String schemeName = getLexGridCodingSchemeName(ontologyBean);
		CodingSchemeVersionOrTag csvt = getLexGridCodingSchemeVersion(ontologyBean);
		ResolvedConceptReferenceList rcrl = getHierarchyRootConcepts(
				schemeName, csvt, true);
		for (ResolvedConceptReference rcr : rcrl.getResolvedConceptReference()) {
			HashSet<String> visitedSet = new HashSet<String>();
			depricatedGetMaxDepth(schemeName, csvt, rcr, 1, visitedSet);
	
		}
	
	}

}
