package org.ncbo.stanford.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

/**
 * Bean that contains properties used as a part of the metrics calculation for
 * Protege ontologies.
 * 
 * @author Paul Alexander
 */
public class OntologyMetricsProtegeCalculationBean {
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

	public OntologyMetricsProtegeCalculationBean() {
		setOwlQueue(new LinkedList<OwlQueueItem>());
		setQueue(new LinkedList<QueueItem>());
		setSeenClasses(new HashMap<String, Integer>());
		setOneSubClses(new ArrayList<String>());
		setXSubClses(new HashMap<String, Integer>());
		setNoDocClses(new ArrayList<String>());
		setConceptAuthors(new HashMap<String, ArrayList<String>>());
		setNoAuthorConcepts(new ArrayList<String>());
		setXAnnotConcepts(new ArrayList<String>());
		setNoDocClsesTotal(0);
		setNoAuthorConceptsTotal(0);
		setXAnnotConceptsTotal(0);
		setXSubClsesTotal(0);
		setOneSubClsesTotal(0);
	}
	
	/**
	 * @return the kb
	 */
	public KnowledgeBase getKb() {
		return kb;
	}

	/**
	 * @param kb
	 *            the kb to set
	 */
	public void setKb(KnowledgeBase kb) {
		this.kb = kb;
	}

	/**
	 * @return the mb
	 */
	public OntologyMetricsBean getMb() {
		return mb;
	}

	/**
	 * @param mb
	 *            the mb to set
	 */
	public void setMb(OntologyMetricsBean mb) {
		this.mb = mb;
	}

	/**
	 * @return the maxDepth
	 */
	public int getMaxDepth() {
		return maxDepth;
	}

	/**
	 * @param maxDepth
	 *            the maxDepth to set
	 */
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	/**
	 * @return the maxSiblings
	 */
	public int getMaxSiblings() {
		return maxSiblings;
	}

	/**
	 * @param maxSiblings
	 *            the maxSiblings to set
	 */
	public void setMaxSiblings(int maxSiblings) {
		this.maxSiblings = maxSiblings;
	}

	/**
	 * @return the conceptAuthors
	 */
	public HashMap<String, ArrayList<String>> getConceptAuthors() {
		return conceptAuthors;
	}

	/**
	 * @param conceptAuthors
	 *            the conceptAuthors to set
	 */
	public void setConceptAuthors(
			HashMap<String, ArrayList<String>> conceptAuthors) {
		this.conceptAuthors = conceptAuthors;
	}

	/**
	 * @return the noAuthorConcepts
	 */
	public ArrayList<String> getNoAuthorConcepts() {
		return noAuthorConcepts;
	}

	/**
	 * @param noAuthorConcepts
	 *            the noAuthorConcepts to set
	 */
	public void setNoAuthorConcepts(ArrayList<String> noAuthorConcepts) {
		this.noAuthorConcepts = noAuthorConcepts;
	}

	/**
	 * @return the xAnnotConcepts
	 */
	public ArrayList<String> getXAnnotConcepts() {
		return xAnnotConcepts;
	}

	/**
	 * @param annotConcepts
	 *            the xAnnotConcepts to set
	 */
	public void setXAnnotConcepts(ArrayList<String> annotConcepts) {
		xAnnotConcepts = annotConcepts;
	}

	/**
	 * @return the documentationProperty
	 */
	public String getDocumentationProperty() {
		return documentationProperty;
	}

	/**
	 * @param documentationProperty
	 *            the documentationProperty to set
	 */
	public void setDocumentationProperty(String documentationProperty) {
		this.documentationProperty = documentationProperty;
	}

	/**
	 * @return the authorProperty
	 */
	public String getAuthorProperty() {
		return authorProperty;
	}

	/**
	 * @param authorProperty
	 *            the authorProperty to set
	 */
	public void setAuthorProperty(String authorProperty) {
		this.authorProperty = authorProperty;
	}

	/**
	 * @return the annotationProperty
	 */
	public String getAnnotationProperty() {
		return annotationProperty;
	}

	/**
	 * @param annotationProperty
	 *            the annotationProperty to set
	 */
	public void setAnnotationProperty(String annotationProperty) {
		this.annotationProperty = annotationProperty;
	}

	/**
	 * @return the propertyWithUniqueValue
	 */
	public String getPropertyWithUniqueValue() {
		return propertyWithUniqueValue;
	}

	/**
	 * @param propertyWithUniqueValue
	 *            the propertyWithUniqueValue to set
	 */
	public void setPropertyWithUniqueValue(String propertyWithUniqueValue) {
		this.propertyWithUniqueValue = propertyWithUniqueValue;
	}

	/**
	 * @return the preferredMaximumSubclassLimit
	 */
	public int getPreferredMaximumSubclassLimit() {
		return preferredMaximumSubclassLimit;
	}

	/**
	 * @param preferredMaximumSubclassLimit
	 *            the preferredMaximumSubclassLimit to set
	 */
	public void setPreferredMaximumSubclassLimit(
			int preferredMaximumSubclassLimit) {
		this.preferredMaximumSubclassLimit = preferredMaximumSubclassLimit;
	}

	/**
	 * @return the oneSubClses
	 */
	public ArrayList<String> getOneSubClses() {
		return oneSubClses;
	}

	/**
	 * @param oneSubClses
	 *            the oneSubClses to set
	 */
	public void setOneSubClses(ArrayList<String> oneSubClses) {
		this.oneSubClses = oneSubClses;
	}

	/**
	 * @return the xSubClses
	 */
	public HashMap<String, Integer> getXSubClses() {
		return xSubClses;
	}

	/**
	 * @param subClses
	 *            the xSubClses to set
	 */
	public void setXSubClses(HashMap<String, Integer> subClses) {
		xSubClses = subClses;
	}

	/**
	 * @return the noDocClses
	 */
	public ArrayList<String> getNoDocClses() {
		return noDocClses;
	}

	/**
	 * @param noDocClses
	 *            the noDocClses to set
	 */
	public void setNoDocClses(ArrayList<String> noDocClses) {
		this.noDocClses = noDocClses;
	}

	/**
	 * @return the owlQueue
	 */
	public LinkedList<OwlQueueItem> getOwlQueue() {
		return owlQueue;
	}

	/**
	 * @param owlQueue
	 *            the owlQueue to set
	 */
	public void setOwlQueue(LinkedList<OwlQueueItem> owlQueue) {
		this.owlQueue = owlQueue;
	}

	/**
	 * @return the queue
	 */
	public LinkedList<QueueItem> getQueue() {
		return queue;
	}

	/**
	 * @param queue
	 *            the queue to set
	 */
	public void setQueue(LinkedList<QueueItem> queue) {
		this.queue = queue;
	}

	/**
	 * @return the seenClasses
	 */
	public HashMap<String, Integer> getSeenClasses() {
		return seenClasses;
	}

	/**
	 * @param seenClasses
	 *            the seenClasses to set
	 */
	public void setSeenClasses(HashMap<String, Integer> seenClasses) {
		this.seenClasses = seenClasses;
	}

	/**
	 * @return the noDocClsesTotal
	 */
	public Integer getNoDocClsesTotal() {
		return noDocClsesTotal;
	}

	/**
	 * @param noDocClsesTotal
	 *            the noDocClsesTotal to set
	 */
	public void setNoDocClsesTotal(Integer noDocClsesTotal) {
		this.noDocClsesTotal = noDocClsesTotal;
	}

	/**
	 * @return the noAuthorConceptsTotal
	 */
	public Integer getNoAuthorConceptsTotal() {
		return noAuthorConceptsTotal;
	}

	/**
	 * @param noAuthorConceptsTotal
	 *            the noAuthorConceptsTotal to set
	 */
	public void setNoAuthorConceptsTotal(Integer noAuthorConceptsTotal) {
		this.noAuthorConceptsTotal = noAuthorConceptsTotal;
	}

	/**
	 * @return the xAnnotConceptsTotal
	 */
	public Integer getXAnnotConceptsTotal() {
		return xAnnotConceptsTotal;
	}

	/**
	 * @param annotConceptsTotal
	 *            the xAnnotConceptsTotal to set
	 */
	public void setXAnnotConceptsTotal(Integer annotConceptsTotal) {
		xAnnotConceptsTotal = annotConceptsTotal;
	}

	/**
	 * @return the xSubClsesTotal
	 */
	public Integer getXSubClsesTotal() {
		return xSubClsesTotal;
	}

	/**
	 * @param subClsesTotal
	 *            the xSubClsesTotal to set
	 */
	public void setXSubClsesTotal(Integer subClsesTotal) {
		xSubClsesTotal = subClsesTotal;
	}

	/**
	 * @return the oneSubClsesTotal
	 */
	public Integer getOneSubClsesTotal() {
		return oneSubClsesTotal;
	}

	/**
	 * @param oneSubClsesTotal
	 *            the oneSubClsesTotal to set
	 */
	public void setOneSubClsesTotal(Integer oneSubClsesTotal) {
		this.oneSubClsesTotal = oneSubClsesTotal;
	}

	public String toString() {
		return "maxDepth " + maxDepth + "\n" + "maxSiblings " + maxSiblings
				+ "\n" + "conceptAuthors size " + conceptAuthors.size() + "\n"
				+ "noAuthorConcepts size " + noAuthorConcepts.size() + "\n"
				+ "xAnnotConcepts size " + xAnnotConcepts.size() + "\n"
				+ "documentationProperty " + documentationProperty + "\n"
				+ "authorProperty " + authorProperty + "\n"
				+ "annotationProperty " + annotationProperty + "\n"
				+ "propertyWithUniqueValue " + propertyWithUniqueValue + "\n"
				+ "preferredMaximumSubclassLimit "
				+ preferredMaximumSubclassLimit + "\n" + "oneSubClses size "
				+ oneSubClses.size() + "\n" + "xSubClses size "
				+ xSubClses.size() + "\n" + "noDocClses size " + noDocClses
				+ "\n" + "noDocClsesTotal " + noDocClsesTotal + "\n"
				+ "noAuthorConceptsTotal " + noAuthorConceptsTotal + "\n"
				+ "xAnnotConceptsTotal " + xAnnotConceptsTotal + "\n"
				+ "xSubClsesTotal " + xSubClsesTotal + "\n"
				+ "oneSubClsesTotal " + oneSubClsesTotal + "\n";
	}
	
	/**
	 * Simple data class for storing queue items.
	 */
	public class QueueItem {
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
	public class OwlQueueItem {
		public RDFSNamedClass nextCls;
		public Integer newDepth;

		public OwlQueueItem(RDFSNamedClass nextCls, Integer newDepth) {
			this.nextCls = nextCls;
			this.newDepth = newDepth;
		}
	}

}
