package org.ncbo.stanford.manager.metrics.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.metrics.OntologyMetricsManager;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.FrameCounts;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.util.ModelMetrics;

/**
 * This class handles Protege-based ontology extraction and retrieval.
 * 
 * @author Paul Alexander
 * @author Csongor Nyulas
 */
public class OntologyMetricsManagerProtegeImpl extends
		AbstractOntologyManagerProtege implements OntologyMetricsManager {

	// Used in calculations
	private KnowledgeBase kb;
	private OntologyMetricsBean mb;
	private int maxDepth, maxSiblings;
	private HashMap<String, ArrayList<String>> conceptAuthors;
	private ArrayList<String> noAuthorConcepts, xAnnotConcepts;
	private String documentationProperty;
	private String authorProperty;
	private String annotationProperty;
	private String propertyWithUniqueValue;
	private int preferredMaximumSubclassLimit;

	// Counters for tracking classes in various metrics categories
	private Integer noDocClsesTotal = 0;
	private Integer noAuthorConceptsTotal = 0;
	private Integer xAnnotConceptsTotal = 0;
	private Integer xSubClsesTotal = 0;
	private Integer oneSubClsesTotal = 0;
	// The number of classes to store for the above metrics categories.
	// Classes found after this number will be counted but their names
	// will not be added to the list.
	private final Integer CLASS_LIST_LIMIT = 201;

	public OntologyMetricsBean extractOntologyMetrics(OntologyBean ontologyBean)
			throws Exception {
		mb = new OntologyMetricsBean();

		kb = getKnowledgeBase(ontologyBean);
		documentationProperty = ontologyBean.getDocumentationSlot();
		authorProperty = ontologyBean.getAuthorSlot();
		propertyWithUniqueValue = ontologyBean.getSlotWithUniqueValue();
		preferredMaximumSubclassLimit = (ontologyBean
				.getPreferredMaximumSubclassLimit() == null ? GOOD_DESIGN_SUBCLASS_LIMIT
				: ontologyBean.getPreferredMaximumSubclassLimit());

		populateOntologyMetrics();

		mb.setId(ontologyBean.getId());
		return mb;
	}

	private void populateOntologyMetrics() {
		ArrayList<String> oneSubClses = new ArrayList<String>();
		HashMap<String, Integer> xSubClses = new HashMap<String, Integer>();
		ArrayList<String> noDocClses = new ArrayList<String>();

		// Initialize to default value, will be overwritten during traversal
		maxDepth = 1;

		if (kb instanceof OWLModel) {
			owlBasicAnalysis(oneSubClses, xSubClses, noDocClses);
		} else {
			basicAnalysis(oneSubClses, xSubClses, noDocClses);
		}

		fillInCommonFields(oneSubClses, xSubClses, noDocClses);
	}

	private void owlBasicAnalysis(ArrayList<String> oneSubClses,
			HashMap<String, Integer> xSubClses, ArrayList<String> noDocClses) {
		conceptAuthors = new HashMap<String, ArrayList<String>>();
		noAuthorConcepts = new ArrayList<String>();
		xAnnotConcepts = new ArrayList<String>();

		// do metrics calculations
		ModelMetrics met = new ModelMetrics((OWLModel) kb);
		met.calculateMetrics();
		// do iteration calculations
		owlClsIterate(((OWLModel) kb).getOWLThingClass(), 1, oneSubClses,
				xSubClses, noDocClses);
		// count classes
		int clsCount = met.getNamedClassCount();
		mb.setNumberOfClasses(clsCount);
		// count instances
		int instanceCount = met.getOwlIndividualCount();
		mb.setNumberOfIndividuals(instanceCount);
		// count properties
		int propertyCount = met.getPropertyCount();
		mb.setNumberOfProperties(propertyCount);
		// calculate maximum depth
		mb.setMaximumDepth(maxDepth);
		// calculate average siblings
		int avgSib = met.getMeanSiblings();
		mb.setAverageNumberOfSiblings(avgSib);
		// calculate max siblings
		maxSiblings = met.getMaxSiblings();
		mb.setMaximumNumberOfSiblings(maxSiblings);

		// fill out author/concept relationships table
		Collections.sort(noAuthorConcepts);
		mb.setClassesWithNoAuthor(noAuthorConcepts);
		Collections.sort(xAnnotConcepts);
		mb.setClassesWithMoreThanOnePropertyValue(xAnnotConcepts);
	}

	private void basicAnalysis(ArrayList<String> oneSubClses,
			HashMap<String, Integer> xSubClses, ArrayList<String> noDocClses) {
		ArrayList<Integer> sibCounts = new ArrayList<Integer>();
		Slot docSlot = kb.getSlot(documentationProperty);
		clsIterate(kb.getRootCls(), 1, oneSubClses, xSubClses, noDocClses,
				docSlot, sibCounts);
		FrameCounts frameCounts = kb.getFrameCounts();
		// count classes
		int clsCount = frameCounts.getDirectClsCount();
		mb.setNumberOfClasses(clsCount);
		// count instances
		int instanceCount = kb.getSimpleInstanceCount();
		mb.setNumberOfIndividuals(instanceCount);
		// count properties
		int propertyCount = kb.getSlotCount();
		mb.setNumberOfProperties(propertyCount);
		// calculate maximum depth
		mb.setMaximumDepth(maxDepth);
		// calculate average siblings
		int sum = 0;
		for (int i = 0; i < sibCounts.size(); i++) {
			sum += sibCounts.get(i);
		}
		int avgSiblings = (sum == 0) ? 0 : sum / sibCounts.size();
		mb.setAverageNumberOfSiblings(avgSiblings);
		// calculate max siblings
		mb.setMaximumNumberOfSiblings(maxSiblings);
	}

	private void fillInCommonFields(ArrayList<String> oneSubClses,
			HashMap<String, Integer> xSubClses, ArrayList<String> noDocClses) {
		// fill in classesWithOneSubclass, classesWithMoreThanXSubclasses and
		// classesWithNoDocumentation
		Collections.sort(oneSubClses);
		mb.setClassesWithOneSubclass(oneSubClses);
		mb.setClassesWithMoreThanXSubclasses(xSubClses);
		Collections.sort(noDocClses);
		mb.setClassesWithNoDocumentation(noDocClses);
	}

	/*
	 * Recursively iterates through the tree of classes. Calculates the metrics
	 * for maxDepth and maxSiblings, and counts the number of subclasses for
	 * each class, adding those with only one subclass to an arraylist and those
	 * with too many subclasses to another arraylist.
	 */
	@SuppressWarnings("unchecked")
	private void clsIterate(Cls currCls, int currDepth,
			ArrayList<String> oneSubClses, HashMap<String, Integer> xSubClses,
			ArrayList<String> noDocClses, Slot docSlot,
			ArrayList<Integer> sibCounts) {

		if (currDepth > maxDepth)
			maxDepth = currDepth;
		Collection<Cls> subclasses = currCls.getDirectSubclasses();

		if (docSlot != null) {
			Collection doc = currCls.getOwnSlotValues(docSlot);
			if (doc.isEmpty() && !currCls.getName().equals(":THING")) {
				String identifier = currCls.getBrowserText() + "("
						+ currCls.getName() + ")";
				if (!noDocClses.contains(identifier)) {
					if (noDocClsesTotal < CLASS_LIST_LIMIT) {
						noDocClses.add(identifier);
						noDocClsesTotal++;
					} else {
						noDocClsesTotal++;
					}
				}
			}
		}

		Iterator<Cls> it = subclasses.iterator();
		int count = 0;
		while (it.hasNext()) {
			Cls nextCls = it.next();
			if (!nextCls.isSystem() && !nextCls.isIncluded()) {
				count++;
				if (count > maxSiblings)
					maxSiblings = count;
				if (!it.hasNext()) {
					for (int i = 0; i < count; i++) {
						sibCounts.add(new Integer(count));
					}
					if (count == 1
							&& !oneSubClses.contains(currCls.getBrowserText())) {
						if (oneSubClsesTotal < CLASS_LIST_LIMIT) {
							oneSubClses.add(currCls.getBrowserText());
							oneSubClsesTotal++;
						} else {
							oneSubClsesTotal++;
						}
					}
					try {
						if (count > preferredMaximumSubclassLimit
								&& !currCls.getName().equals(":THING")
								&& !xSubClses.containsKey(currCls
										.getBrowserText())) {
							if (xSubClsesTotal < CLASS_LIST_LIMIT) {
								xSubClses.put(currCls.getBrowserText(), count);
								xSubClsesTotal++;
							} else {
								xSubClsesTotal++;
							}
						}
					} catch (NumberFormatException ignored) {
					}
				}
				clsIterate(nextCls, currDepth + 1, oneSubClses, xSubClses,
						noDocClses, docSlot, sibCounts);
			}
		}
	}

	/*
	 * Recursively iterates through the tree of classes for owl knowledgebases.
	 * Calculates the metrics for maxDepth, matches author and documentation
	 * tags, and counts the number of subclasses for each class, adding those
	 * with only one subclass to an arraylist and those with too many subclasses
	 * to another arraylist.
	 */
	private void owlClsIterate(RDFSNamedClass currCls, int currDepth,
			ArrayList<String> oneSubClses, HashMap<String, Integer> xSubClses,
			ArrayList<String> noDocClses) {
		// System.out.println(currCls.getName() + " " + currDepth);
		if (currDepth > maxDepth)
			maxDepth = currDepth;
		Collection<RDFSNamedClass> subclasses = currCls.getNamedSubclasses();
		String docTag = "";
		String authorTag = "";
		String annotTag = "";

		docTag = documentationProperty;
		authorTag = authorProperty;
		annotTag = annotationProperty;
		matchTags(currCls, docTag, authorTag, annotTag, noDocClses);
		Iterator<RDFSNamedClass> it = subclasses.iterator();
		int count = 0;
		while (it.hasNext()) {
			RDFSNamedClass nextCls = it.next();
			if (!nextCls.isSystem() && !nextCls.isIncluded()) {
				count++;
				if (!it.hasNext()) {
					if (count == 1
							&& !oneSubClses.contains(currCls.getBrowserText()))
						oneSubClses.add(currCls.getBrowserText());
				}
				if (count > preferredMaximumSubclassLimit
						&& !currCls.getName().equals("owl:Thing")
						&& !xSubClses.containsKey(currCls.getBrowserText()))
					xSubClses.put(currCls.getBrowserText(), count);
				owlClsIterate(nextCls, currDepth + 1, oneSubClses, xSubClses,
						noDocClses);
			}
		}
	}

	/*
	 * Called during an iteration of owl classes to match the user specified
	 * documentation and author tags. The documentation tag is used to identify
	 * classes with no documentation, while the author tag is used to identify
	 * concepts with a specific author. For each class, tags are matched by
	 * iterating through the rdf properties of the class and seeing if any of
	 * those properties match the specified tags.
	 */
	private void matchTags(RDFSNamedClass cls, String docTag, String authorTag,
			String annotTag, ArrayList<String> noDocClses) {
		boolean docTagFound = false;
		boolean authorTagFound = false;
		boolean annotTagDoubleMatched = false;
		int annotTagMatches = 0;
		Collection<RDFProperty> properties = cls.getRDFProperties();
		Iterator<RDFProperty> it = properties.iterator();
		// iterate through RDF properties
		while (it.hasNext()) {
			RDFProperty prop = it.next();

			// Matching the documentation tag
			if (docTag != null && prop.getBrowserText().equals(docTag)) {
				docTagFound = true;
			}
			
			// Matching the author tag, want to add author/concept to HashMap
			if (authorTag != null && prop.getBrowserText().equals(authorTag)
					&& !cls.getName().equals("owl:Thing")) {
				authorTagFound = true;
				addClsToConceptAuthorRelationship(cls, prop);
			}
			
			if (annotTag != null && prop.getBrowserText().equals(annotTag)) {
				Collection props = cls.getPropertyValues(prop, false);
				Iterator it2 = props.iterator();
				while (it2.hasNext()) {
					it2.next();
					annotTagMatches++;
					if (annotTagMatches >= 2)
						annotTagDoubleMatched = true;
				}
			}
			annotTagMatches = 0;
		}
		
		if (!cls.isSystem() && !cls.isIncluded()) {
			// We found a missing documentation tag
			if (!docTagFound && !cls.getName().equals("owl:Thing")) {
				String identifier = cls.getBrowserText() + "(" + cls.getName()
						+ ")";
				if (!noDocClses.contains(identifier)) {
					if (noDocClsesTotal < CLASS_LIST_LIMIT) {
						noDocClses.add(identifier);
						noDocClsesTotal++;
					} else {
						noDocClsesTotal++;
					}
				}
			}
			
			// We found a missing author tag
			if (!authorTagFound && !cls.getName().equals("owl:Thing")) {
				String identifier = cls.getBrowserText() + "(" + cls.getName()
						+ ")";
				if (!noAuthorConcepts.contains(identifier)) {
					if (noAuthorConceptsTotal < CLASS_LIST_LIMIT) {
						noAuthorConcepts.add(identifier);
						noAuthorConceptsTotal++;
					} else {
						noAuthorConceptsTotal++;
					}
				}
			}
			
			if (annotTagDoubleMatched && !cls.getName().equals("owl:Thing")) {
				String identifier = cls.getBrowserText() + "(" + cls.getName()
						+ ")";
				if (!xAnnotConcepts.contains(identifier)) {
					if (xAnnotConceptsTotal < CLASS_LIST_LIMIT) {
						xAnnotConcepts.add(identifier);
						xAnnotConceptsTotal++;
					} else {
						xAnnotConceptsTotal++;
					}
				}
			}
		}
	}

	private void addClsToConceptAuthorRelationship(RDFSNamedClass cls,
			RDFProperty prop) {
		Collection props = cls.getPropertyValues(prop, false);
		Iterator it = props.iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			// If author already in map, add to his/her list of concepts
			if (conceptAuthors.containsKey(key)) {
				ArrayList<String> values = conceptAuthors.get(key);
				if (!values.contains(cls.getBrowserText()))
					values.add(cls.getBrowserText());
				conceptAuthors.put(key, values);
			} else {
				// Otherwise, create a new ArrayList for this author
				ArrayList<String> values = new ArrayList();
				values.add(cls.getBrowserText());
				conceptAuthors.put(key, values);
			}
		}
	}

}
