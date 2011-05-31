package org.ncbo.stanford.manager.metrics.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.bean.OntologyMetricsProtegeCalculationBean;
import org.ncbo.stanford.bean.OntologyMetricsProtegeCalculationBean.OwlQueueItem;
import org.ncbo.stanford.bean.OntologyMetricsProtegeCalculationBean.QueueItem;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.metrics.OntologyMetricsManager;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.FrameCounts;
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
		OntologyMetricsProtegeCalculationBean calcBean = new OntologyMetricsProtegeCalculationBean();

		calcBean.setMb(new OntologyMetricsBean());

		calcBean.setKb(getKnowledgeBase(ontologyBean));
		calcBean.setDocumentationProperty(ontologyBean.getDocumentationSlot());
		calcBean.setAuthorProperty(ontologyBean.getAuthorSlot());
		calcBean.setPropertyWithUniqueValue(ontologyBean
				.getSlotWithUniqueValue());
		calcBean
				.setPreferredMaximumSubclassLimit((ontologyBean
						.getPreferredMaximumSubclassLimit() == null ? GOOD_DESIGN_SUBCLASS_LIMIT
						: ontologyBean.getPreferredMaximumSubclassLimit()));

		populateOntologyMetrics(calcBean);

		calcBean.getMb().setId(ontologyBean.getId());
		return calcBean.getMb();
	}

	/**
	 * Determines if the ontology is OWL or Frame, calls appropriate analysis,
	 * and handles common data.
	 */
	private void populateOntologyMetrics(
			OntologyMetricsProtegeCalculationBean calcBean) {
		// Initialize to default value, will be overwritten during traversal
		calcBean.setMaxDepth(1);

		if (calcBean.getKb() instanceof OWLModel) {
			owlBasicAnalysis(calcBean);
		} else {
			basicAnalysis(calcBean);
		}

		fillInCommonFields(calcBean);
	}

	/**
	 * Calculates overall metrics and manages the queue of classes for
	 * processing for OWL ontologies.
	 */
	private void owlBasicAnalysis(OntologyMetricsProtegeCalculationBean calcBean) {
		// do metrics calculations
		ModelMetrics met = new ModelMetrics((OWLModel) calcBean.getKb());

		log.debug("Starting ModelMetrics calculation");
		met.calculateMetrics();

		// Add first item to queue and trigger processing
		OwlQueueItem owlItem = calcBean.new OwlQueueItem(((OWLModel) calcBean
				.getKb()).getOWLThingClass(), 1);
		calcBean.getOwlQueue().addLast(owlItem);
		while (!calcBean.getOwlQueue().isEmpty()) {
			if (calcBean.getSeenClasses().size() % 1000 == 0)
				log.debug("Processing class "
						+ calcBean.getSeenClasses().size());
			OwlQueueItem currCls = calcBean.getOwlQueue().remove();
			owlClsIterate(calcBean, currCls.nextCls, currCls.newDepth);
		}

		// count classes
		int clsCount = met.getNamedClassCount();
		calcBean.getMb().setNumberOfClasses(clsCount);
		// count instances
		int instanceCount = met.getOwlIndividualCount();
		calcBean.getMb().setNumberOfIndividuals(instanceCount);
		// count properties
		int propertyCount = met.getPropertyCount();
		calcBean.getMb().setNumberOfProperties(propertyCount);
		// calculate maximum depth
		calcBean.getMb().setMaximumDepth(calcBean.getMaxDepth());
		// calculate average siblings
		int avgSib = met.getMeanSiblings();
		calcBean.getMb().setAverageNumberOfSiblings(avgSib);
		// calculate max siblings
		calcBean.setMaxSiblings(met.getMaxSiblings());
		calcBean.getMb().setMaximumNumberOfSiblings(calcBean.getMaxSiblings());

		postProcessLists(calcBean);

		// fill out author/concept relationships table
		Collections.sort(calcBean.getNoAuthorConcepts());
		calcBean.getMb().setClassesWithNoAuthor(calcBean.getNoAuthorConcepts());
		Collections.sort(calcBean.getXAnnotConcepts());
		calcBean.getMb().setClassesWithMoreThanOnePropertyValue(
				calcBean.getXAnnotConcepts());
	}

	/**
	 * Calculates overall metrics and manages the queue of classes for
	 * processing for Frames ontologies.
	 */
	private void basicAnalysis(OntologyMetricsProtegeCalculationBean calcBean) {
		ArrayList<Integer> sibCounts = new ArrayList<Integer>();
		Slot docSlot = getDefinitionSlot(calcBean.getKb(), calcBean
				.getDocumentationProperty());

		// Add first item to queue and trigger processing
		QueueItem item = calcBean.new QueueItem(calcBean.getKb().getRootCls(),
				1, docSlot, sibCounts);
		calcBean.setQueue(new LinkedList<QueueItem>());
		calcBean.getQueue().addLast(item);
		while (!calcBean.getQueue().isEmpty()) {
			QueueItem currCls = calcBean.getQueue().remove();
			clsIterate(calcBean, currCls.nextCls, currCls.newDepth,
					currCls.docSlot, currCls.sibCounts);
		}

		FrameCounts frameCounts = calcBean.getKb().getFrameCounts();
		// count classes
		int clsCount = frameCounts.getDirectClsCount();
		calcBean.getMb().setNumberOfClasses(clsCount);
		// count instances
		int instanceCount = calcBean.getKb().getSimpleInstanceCount();
		calcBean.getMb().setNumberOfIndividuals(instanceCount);
		// count properties
		int propertyCount = calcBean.getKb().getSlotCount();
		calcBean.getMb().setNumberOfProperties(propertyCount);
		// calculate maximum depth
		calcBean.getMb().setMaximumDepth(calcBean.getMaxDepth());

		postProcessLists(calcBean);

		// calculate average siblings
		int sum = 0;
		for (int i = 0; i < sibCounts.size(); i++) {
			sum += sibCounts.get(i);
		}
		int avgSiblings = (sum == 0) ? 0 : sum / sibCounts.size();
		calcBean.getMb().setAverageNumberOfSiblings(avgSiblings);
		// calculate max siblings
		calcBean.getMb().setMaximumNumberOfSiblings(calcBean.getMaxSiblings());

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
	private void owlClsIterate(OntologyMetricsProtegeCalculationBean calcBean,
			RDFSNamedClass currCls, Integer currDepth) {
		if (currDepth > calcBean.getMaxDepth()) {
			calcBean.setMaxDepth(currDepth);
		}
		// Important: getNamedSubclasses(false) ignores transitive relationships
		Collection<RDFSNamedClass> subclasses = currCls
				.getNamedSubclasses(false);

		// Get property objects for defined metadata slots
		OWLModel om = (OWLModel) calcBean.getKb();
		RDFProperty documentation = om.getRDFProperty(calcBean.getDocumentationProperty());
		RDFProperty author = om.getRDFProperty(calcBean.getAuthorProperty());
		RDFProperty annotation = om.getRDFProperty(calcBean.getAnnotationProperty());

		matchTags(calcBean, currCls, documentation, author, annotation);
		Iterator<RDFSNamedClass> it = subclasses.iterator();

		String currClsName = currCls.getPrefixedName();

		int count = 0;
		while (it.hasNext()) {
			RDFSNamedClass nextCls = it.next();
			if (!nextCls.isSystem() && !nextCls.isIncluded()) {
				count++;

				String nextFullName = nextCls.getName();

				if (!it.hasNext()) {
					if (count == 1
							&& !calcBean.getOneSubClses().contains(currClsName))
						if (calcBean.getOneSubClsesTotal() < CLASS_LIST_LIMIT) {
							calcBean.getOneSubClses().add(
									currCls.getBrowserText());
							calcBean.setOneSubClsesTotal(calcBean
									.getOneSubClsesTotal() + 1);
						} else {
							calcBean.setOneSubClsesTotal(calcBean
									.getOneSubClsesTotal() + 1);
						}
				}

				if (count > calcBean.getPreferredMaximumSubclassLimit()
						&& !currClsName.equals("owl:Thing")
						&& !calcBean.getXSubClses().containsKey(currClsName)) {
					if (calcBean.getXSubClsesTotal() < CLASS_LIST_LIMIT) {
						calcBean.getXSubClses().put(currCls.getBrowserText(),
								count);
						calcBean
								.setXSubClsesTotal(calcBean.getXSubClsesTotal() + 1);
					} else {
						calcBean
								.setXSubClsesTotal(calcBean.getXSubClsesTotal() + 1);
					}
				}

				if (!calcBean.getSeenClasses().containsKey(nextFullName)) {
					calcBean.getSeenClasses().put(nextFullName, count);
					// Create new data object and add it to queue
					Integer newDepth = currDepth + 1;
					OwlQueueItem owlItem = calcBean.new OwlQueueItem(nextCls,
							newDepth);
					calcBean.getOwlQueue().addLast(owlItem);
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
	private void clsIterate(OntologyMetricsProtegeCalculationBean calcBean,
			Cls currCls, int currDepth, Slot docSlot,
			ArrayList<Integer> sibCounts) {

		if (currDepth > calcBean.getMaxDepth()) {
			calcBean.setMaxDepth(currDepth);
		}

		Collection<Cls> subclasses = currCls.getDirectSubclasses();

		if (docSlot != null) {
			Collection doc = currCls.getOwnSlotValues(docSlot);
			if (doc.isEmpty() && !currCls.getName().equals(":THING")) {
				String identifier = currCls.getBrowserText() + "("
						+ currCls.getName() + ")";
				if (!calcBean.getNoDocClses().contains(identifier)) {
					if (calcBean.getNoDocClsesTotal() < CLASS_LIST_LIMIT) {
						calcBean.getNoDocClses().add(identifier);
						calcBean.setNoDocClsesTotal(calcBean
								.getNoDocClsesTotal() + 1);
					} else {
						calcBean.setNoDocClsesTotal(calcBean
								.getNoDocClsesTotal() + 1);
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
				if (count > calcBean.getMaxSiblings()) {
					calcBean.setMaxSiblings(count);
				}
				if (!it.hasNext()) {
					for (int i = 0; i < count; i++) {
						sibCounts.add(new Integer(count));
					}
					if (count == 1
							&& !calcBean.getOneSubClses().contains(
									currCls.getBrowserText())) {
						if (calcBean.getOneSubClsesTotal() < CLASS_LIST_LIMIT) {
							calcBean.getOneSubClses().add(
									currCls.getBrowserText());
							calcBean.setOneSubClsesTotal(calcBean
									.getOneSubClsesTotal() + 1);
						} else {
							calcBean.setOneSubClsesTotal(calcBean
									.getOneSubClsesTotal() + 1);
						}
					}
					try {
						if (count > calcBean.getPreferredMaximumSubclassLimit()
								&& !currCls.getName().equals(":THING")
								&& !calcBean.getXSubClses().containsKey(
										currCls.getBrowserText())) {
							if (calcBean.getXSubClsesTotal() < CLASS_LIST_LIMIT) {
								calcBean.getXSubClses().put(
										currCls.getBrowserText(), count);
								calcBean.setXSubClsesTotal(calcBean
										.getXSubClsesTotal() + 1);
							} else {
								calcBean.setXSubClsesTotal(calcBean
										.getXSubClsesTotal() + 1);
							}
						}
					} catch (NumberFormatException ignored) {
					}
				}

				if (!calcBean.getSeenClasses().containsKey(nextFullName)) {
					calcBean.getSeenClasses().put(nextFullName, count);
					// Queue next class
					Integer newDepth = currDepth + 1;
					QueueItem item = calcBean.new QueueItem(nextCls, newDepth,
							docSlot, sibCounts);
					calcBean.getQueue().addLast(item);
				}
			}
		}
	}

	/**
	 * Fills in common fields for both OWL and Frames ontologies.
	 */
	private void fillInCommonFields(
			OntologyMetricsProtegeCalculationBean calcBean) {
		Collections.sort(calcBean.getOneSubClses());
		calcBean.getMb().setClassesWithOneSubclass(calcBean.getOneSubClses());

		calcBean.getMb().setClassesWithMoreThanXSubclasses(
				calcBean.getXSubClses());

		Collections.sort(calcBean.getNoDocClses());
		calcBean.getMb()
				.setClassesWithNoDocumentation(calcBean.getNoDocClses());
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
	private void matchTags(OntologyMetricsProtegeCalculationBean calcBean,
			RDFSNamedClass cls, RDFProperty docTag, RDFProperty authorTag, RDFProperty annotTag) {
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
			if (docTag != null && prop.equals(docTag)) {
				docTagFound = true;
			}

			// Matching the author tag, want to add author/concept to HashMap
			if (authorTag != null && prop.equals(authorTag)
					&& !cls.getName().equals("owl:Thing")) {
				authorTagFound = true;
				addClsToConceptAuthorRelationship(calcBean, cls, prop);
			}

			if (annotTag != null && prop.equals(annotTag)) {
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
				if (!calcBean.getNoDocClses().contains(identifier)) {
					if (calcBean.getNoDocClsesTotal() < CLASS_LIST_LIMIT) {
						calcBean.getNoDocClses().add(identifier);
						calcBean.setNoDocClsesTotal(calcBean
								.getNoDocClsesTotal() + 1);
					} else {
						calcBean.setNoDocClsesTotal(calcBean
								.getNoDocClsesTotal() + 1);
					}
				}
			}

			// We found a missing author tag
			if (!authorTagFound && !cls.getName().equals("owl:Thing")) {
				String identifier = cls.getPrefixedName() + "(" + cls.getName()
						+ ")";
				if (!calcBean.getNoAuthorConcepts().contains(identifier)) {
					if (calcBean.getNoAuthorConceptsTotal() < CLASS_LIST_LIMIT) {
						calcBean.getNoAuthorConcepts().add(identifier);
						calcBean.setNoAuthorConceptsTotal(calcBean
								.getNoAuthorConceptsTotal() + 1);
					} else {
						calcBean.setNoAuthorConceptsTotal(calcBean
								.getNoAuthorConceptsTotal() + 1);
					}
				}
			}

			if (annotTagDoubleMatched && !cls.getName().equals("owl:Thing")) {
				String identifier = cls.getBrowserText() + "(" + cls.getName()
						+ ")";
				if (!calcBean.getXAnnotConcepts().contains(identifier)) {
					if (calcBean.getXAnnotConceptsTotal() < CLASS_LIST_LIMIT) {
						calcBean.getXAnnotConcepts().add(identifier);
						calcBean.setXAnnotConceptsTotal(calcBean
								.getXAnnotConceptsTotal() + 1);
					} else {
						calcBean.setXAnnotConceptsTotal(calcBean
								.getXAnnotConceptsTotal() + 1);
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
	private void addClsToConceptAuthorRelationship(
			OntologyMetricsProtegeCalculationBean calcBean, RDFSNamedClass cls,
			RDFProperty prop) {
		Collection props = cls.getPropertyValues(prop, false);
		Iterator it = props.iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			// If author already in map, add to his/her list of concepts
			if (calcBean.getConceptAuthors().containsKey(key)) {
				ArrayList<String> values = calcBean.getConceptAuthors()
						.get(key);
				if (!values.contains(cls.getBrowserText()))
					values.add(cls.getBrowserText());
				calcBean.getConceptAuthors().put(key, values);
			} else {
				// Otherwise, create a new ArrayList for this author
				ArrayList<String> values = new ArrayList<String>();
				values.add(cls.getBrowserText());
				calcBean.getConceptAuthors().put(key, values);
			}
		}
	}

	private void postProcessLists(OntologyMetricsProtegeCalculationBean calcBean) {
		final String ALL_TRIGGERED = "alltriggered";
		final String LIMIT_PASSED = "limitpassed:";

		// Check to see if every seen class has no property
		// If so, we're going to put a special message in the xml
		// Then, if the classes have more than CLASS_LIST_SIZE items in the list
		// Then clear the list and show how many were missing props
		if (calcBean.getNoDocClsesTotal() >= calcBean.getSeenClasses().size()) {
			calcBean.getNoDocClses().clear();
			calcBean.getNoDocClses().add(ALL_TRIGGERED);
		} else if (calcBean.getNoDocClsesTotal() >= CLASS_LIST_LIMIT) {
			calcBean.getNoDocClses().clear();
			calcBean.getNoDocClses().add(
					LIMIT_PASSED + calcBean.getNoDocClsesTotal());
		}

		if (calcBean.getNoAuthorConceptsTotal() >= calcBean.getSeenClasses()
				.size()) {
			calcBean.getNoAuthorConcepts().clear();
			calcBean.getNoAuthorConcepts().add(ALL_TRIGGERED);
		} else if (calcBean.getNoAuthorConceptsTotal() >= CLASS_LIST_LIMIT) {
			calcBean.getNoAuthorConcepts().clear();
			calcBean.getNoAuthorConcepts().add(
					LIMIT_PASSED + calcBean.getNoAuthorConceptsTotal());
		}

		if (calcBean.getOneSubClsesTotal() >= calcBean.getSeenClasses().size()) {
			calcBean.getOneSubClses().clear();
			calcBean.getOneSubClses().add(ALL_TRIGGERED);
		} else if (calcBean.getOneSubClsesTotal() >= CLASS_LIST_LIMIT) {
			calcBean.getOneSubClses().clear();
			calcBean.getOneSubClses().add(
					LIMIT_PASSED + calcBean.getOneSubClsesTotal());
		}

		if (calcBean.getXSubClsesTotal() >= calcBean.getSeenClasses().size()) {
			calcBean.getXSubClses().clear();
			calcBean.getXSubClses().put(ALL_TRIGGERED, 0);
		} else if (calcBean.getXSubClsesTotal() >= CLASS_LIST_LIMIT) {
			calcBean.getXSubClses().clear();
			calcBean.getXSubClses().put(LIMIT_PASSED,
					calcBean.getXSubClsesTotal());
		}

	}

}
