package org.ncbo.stanford.manager.metrics.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private ArrayList<String> oneSubClses;
	private HashMap<String, Integer> xSubClses;
	private ArrayList<String> noDocClses;

	private LinkedList<OwlQueueItem> owlQueue;
	private LinkedList<QueueItem> queue;
	private HashMap<String, Integer> seenClasses;

	// Counters for tracking classes in various metrics categories
	private Integer noDocClsesTotal;
	private Integer noAuthorConceptsTotal;
	private Integer xAnnotConceptsTotal;
	private Integer xSubClsesTotal;
	private Integer oneSubClsesTotal;
	// The number of classes to store for the above metrics categories.
	// Classes found after this number will be counted but their names
	// will not be added to the list.
	private final Integer CLASS_LIST_LIMIT = 201;

	private static final Log log = LogFactory
			.getLog(OntologyMetricsManagerProtegeImpl.class);

	/**
	 * Interface method, dispatches the processing.
	 */
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

	/**
	 * Determines if the ontology is OWL or Frame, calls appropriate analysis,
	 * and handles common data.
	 */
	private void populateOntologyMetrics() {
		oneSubClses = new ArrayList<String>();
		xSubClses = new HashMap<String, Integer>();
		noDocClses = new ArrayList<String>();

		// Initialize to default value, will be overwritten during traversal
		maxDepth = 1;

		if (kb instanceof OWLModel) {
			owlBasicAnalysis();
		} else {
			basicAnalysis();
		}

		fillInCommonFields();
	}

	/**
	 * Calculates overall metrics and manages the queue of classes for
	 * processing for OWL ontologies.
	 */
	private void owlBasicAnalysis() {
		resetDefaultValues();

		// do metrics calculations
		ModelMetrics met = new ModelMetrics((OWLModel) kb);
		met.calculateMetrics();
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

		// Add first item to queue and trigger processing
		OwlQueueItem owlItem = new OwlQueueItem(((OWLModel) kb)
				.getOWLThingClass(), 1);
		owlQueue.addLast(owlItem);
		while (!owlQueue.isEmpty()) {
			if (seenClasses.size() % 1000 == 0)
				log.debug("Processing class " + seenClasses.size());
			OwlQueueItem currCls = owlQueue.remove();
			owlClsIterate(currCls.nextCls, currCls.newDepth);
		}

		postProcessLists();

		// fill out author/concept relationships table
		Collections.sort(noAuthorConcepts);
		mb.setClassesWithNoAuthor(noAuthorConcepts);
		Collections.sort(xAnnotConcepts);
		mb.setClassesWithMoreThanOnePropertyValue(xAnnotConcepts);
	}

	/**
	 * Calculates overall metrics and manages the queue of classes for
	 * processing for Frames ontologies.
	 */
	private void basicAnalysis() {
		ArrayList<Integer> sibCounts = new ArrayList<Integer>();
		Slot docSlot = getDefinitionSlot(kb, documentationProperty);

		resetDefaultValues();

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

		// Add first item to queue and trigger processing
		QueueItem item = new QueueItem(kb.getRootCls(), 1, docSlot, sibCounts);
		queue.addLast(item);
		while (!queue.isEmpty()) {
			QueueItem currCls = queue.remove();
			clsIterate(currCls.nextCls, currCls.newDepth, currCls.docSlot,
					currCls.sibCounts);
		}

		postProcessLists();

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

	/*
	 * Recursively iterates through the tree of classes for owl knowledgebases.
	 * Calculates the metrics for maxDepth, matches author and documentation
	 * tags, and counts the number of subclasses for each class, adding those
	 * with only one subclass to an arraylist and those with too many subclasses
	 * to another arraylist.
	 * 
	 * TODO: There may be some opportunities for optimization in the
	 * getBrowserText lookups.
	 */
	@SuppressWarnings("unchecked")
	private void owlClsIterate(RDFSNamedClass currCls, Integer currDepth) {
		if (currDepth > maxDepth)
			maxDepth = currDepth;
		// Important: getNamedSubclasses(false) ignores transitive relationships
		Collection<RDFSNamedClass> subclasses = currCls
				.getNamedSubclasses(false);
		String docTag = "";
		String authorTag = "";
		String annotTag = "";

		docTag = documentationProperty;
		authorTag = authorProperty;
		annotTag = annotationProperty;
		matchTags(currCls, docTag, authorTag, annotTag);
		Iterator<RDFSNamedClass> it = subclasses.iterator();

		String currClsName = currCls.getPrefixedName();
		String currClsLabel = currCls.getBrowserText();

		int count = 0;
		while (it.hasNext()) {
			RDFSNamedClass nextCls = it.next();
			if (!nextCls.isSystem() && !nextCls.isIncluded()) {
				count++;

				String nextFullName = nextCls.getName();

				if (!it.hasNext()) {
					if (count == 1 && !oneSubClses.contains(currClsName))
						oneSubClses.add(currClsLabel);
				}

				if (count > preferredMaximumSubclassLimit
						&& !currClsName.equals("owl:Thing")
						&& !xSubClses.containsKey(currClsName)) {
					xSubClses.put(currClsLabel, count);
				}

				if (!seenClasses.containsKey(nextFullName)) {
					seenClasses.put(nextFullName, count);
					// Create new data object and add it to queue
					Integer newDepth = currDepth + 1;
					OwlQueueItem owlItem = new OwlQueueItem(nextCls, newDepth);
					owlQueue.addLast(owlItem);
				}
			}
		}
	}

	/*
	 * Recursively iterates through the tree of classes. Calculates the metrics
	 * for maxDepth and maxSiblings, and counts the number of subclasses for
	 * each class, adding those with only one subclass to an arraylist and those
	 * with too many subclasses to another arraylist.
	 * 
	 * TODO: There may be some opportunities for optimization in the
	 * getBrowserText lookups.
	 */
	@SuppressWarnings("unchecked")
	private void clsIterate(Cls currCls, int currDepth, Slot docSlot,
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
			String nextFullName = nextCls.getName();
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

				if (!seenClasses.containsKey(nextFullName)) {
					seenClasses.put(nextFullName, count);
					// Queue next class
					Integer newDepth = currDepth + 1;
					QueueItem item = new QueueItem(nextCls, newDepth, docSlot,
							sibCounts);
					queue.addLast(item);
				}
			}
		}
	}

	/**
	 * Fills in common fields for both OWL and Frames ontologies.
	 */
	private void fillInCommonFields() {
		Collections.sort(oneSubClses);
		mb.setClassesWithOneSubclass(oneSubClses);

		mb.setClassesWithMoreThanXSubclasses(xSubClses);

		Collections.sort(noDocClses);
		mb.setClassesWithNoDocumentation(noDocClses);
	}

	/*
	 * Called during an iteration of owl classes to match the user specified
	 * documentation and author tags. The documentation tag is used to identify
	 * classes with no documentation, while the author tag is used to identify
	 * concepts with a specific author. For each class, tags are matched by
	 * iterating through the rdf properties of the class and seeing if any of
	 * those properties match the specified tags.
	 */
	@SuppressWarnings("unchecked")
	private void matchTags(RDFSNamedClass cls, String docTag, String authorTag,
			String annotTag) {
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
				String identifier = cls.getPrefixedName() + "(" + cls.getName()
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
				String identifier = cls.getPrefixedName() + "(" + cls.getName()
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

	/**
	 * 
	 * @param cls
	 * @param prop
	 */
	@SuppressWarnings("unchecked")
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
				ArrayList<String> values = new ArrayList<String>();
				values.add(cls.getBrowserText());
				conceptAuthors.put(key, values);
			}
		}
	}

	private void postProcessLists() {
		// Check to see if every seen class has no property
		// If so, we're going to put a special message in the xml
		// Then, if the classes have more than CLASS_LIST_SIZE items in the list
		// Then clear the list and show how many were missing props
		if (noDocClsesTotal >= seenClasses.size()) {
			noDocClses.clear();
			noDocClses.add("alldocmissing");
		} else if (noDocClsesTotal >= CLASS_LIST_LIMIT) {
			noDocClses.clear();
			noDocClses.add("docmissing:" + noDocClsesTotal);
		}

		if (noAuthorConceptsTotal >= seenClasses.size()) {
			noAuthorConcepts.clear();
			noAuthorConcepts.add("allauthormissing");
		} else if (noAuthorConceptsTotal >= CLASS_LIST_LIMIT) {
			noAuthorConcepts.clear();
			noAuthorConcepts.add("authormissing:" + noAuthorConceptsTotal);
		}

		if (oneSubClsesTotal >= seenClasses.size()) {
			oneSubClses.clear();
			oneSubClses.add("allhaveonesubclass");
		} else if (oneSubClsesTotal >= CLASS_LIST_LIMIT) {
			oneSubClses.clear();
			oneSubClses.add("haveonesubclass:" + oneSubClsesTotal);
		}

		if (xSubClsesTotal >= seenClasses.size()) {
			xSubClses.clear();
			xSubClses.put("allhavemorethanxsubclasses", 0);
		} else if (xSubClsesTotal >= CLASS_LIST_LIMIT) {
			xSubClses.clear();
			xSubClses.put("allhavemorethanxsubclasses", xSubClsesTotal);
		}

	}

	/**
	 * Resets all common class variables to default values.
	 */
	private void resetDefaultValues() {
		conceptAuthors = new HashMap<String, ArrayList<String>>();
		noAuthorConcepts = new ArrayList<String>();
		xAnnotConcepts = new ArrayList<String>();
		owlQueue = new LinkedList<OwlQueueItem>();
		queue = new LinkedList<QueueItem>();
		seenClasses = new HashMap<String, Integer>();
		noDocClsesTotal = 0;
		noAuthorConceptsTotal = 0;
		xAnnotConceptsTotal = 0;
		xSubClsesTotal = 0;
		oneSubClsesTotal = 0;
	}

	/**
	 * Simple data class for storing queue items.
	 */
	private class QueueItem {
		public Cls nextCls;
		public Integer newDepth;
		public Slot docSlot;
		public ArrayList<Integer> sibCounts;

		public QueueItem(Cls nextCls, Integer newDepth, Slot docSlot,
				ArrayList<Integer> sibCounts) {
			this.nextCls = nextCls;
			this.newDepth = newDepth;
			this.docSlot = docSlot;
			this.sibCounts = sibCounts;
		}
	}

	/**
	 * Simple data class for storing OWL queue items.
	 */
	private class OwlQueueItem {
		public RDFSNamedClass nextCls;
		public Integer newDepth;

		public OwlQueueItem(RDFSNamedClass nextCls, Integer newDepth) {
			this.nextCls = nextCls;
			this.newDepth = newDepth;
		}
	}

}
