package org.ncbo.stanford.manager.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.search.ProtegeSearchFrame;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.search.OntologySearchManager;
import org.ncbo.stanford.util.LoggingUtils;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.util.protege.RemoveOWLOntologiesUtil;
import org.ncbo.stanford.wrapper.LuceneIndexWriterWrapper;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * Implements the OntologySearchManager interface that deals specifically with
 * ontologies handled by Protege backend
 * 
 * @author Michael Dorf
 * 
 */
public class OntologySearchManagerProtegeImpl extends
		AbstractOntologyManagerProtege implements OntologySearchManager {

	private static final Log log = LogFactory
			.getLog(OntologySearchManagerProtegeImpl.class);

	/**
	 * Index a given ontology
	 * 
	 * @param writer
	 * @param ontology
	 * @throws Exception
	 */
	public void indexOntology(LuceneIndexWriterWrapper writer,
			OntologyBean ontologyBean) throws Exception {
		KnowledgeBase kb = getKnowledgeBaseInstance(ontologyBean);
		boolean owlMode = kb instanceof OWLModel;
		Collection<Frame> frames = kb.getFrames();

		if (owlMode) {
			try {
				RemoveOWLOntologiesUtil.removeOWLOntologies((OWLModel) kb,
						frames);
			} catch (Exception e) {
				log
						.error("RemoveOWLOntologiesUtil.removeOWLOntologies threw an exception: "
								+ e.getMessage());
				e.printStackTrace();
			}
		}

		Slot deprecatedSlot = getDeprecatedSlot(kb);
		List<Slot> preferredNameSlots = getPreferredNameSlots(kb, ontologyBean
				.getPreferredNameSlot());
		Slot synonymSlot = getSynonymSlot(kb, ontologyBean.getSynonymSlot());
		Set<Slot> propertySlots = getPropertySlots(kb);

		// Progress logging
		int classCount = frames.size();
		MutableInt progressCount = new MutableInt(1);

		for (Frame frame : frames) {
			// Log progress
			LoggingUtils.logProgress(classCount, progressCount, ontologyBean.getId(), "Search Indexing Progress", log);
			
			// Avoid cache timeout for KB
			kb = getKnowledgeBaseInstance(ontologyBean);
			
			// exclude anonymous and system frames from being indexed
			if (frame.isSystem()
					|| (frame instanceof RDFResource && ((RDFResource) frame)
							.isAnonymous())) {
				continue;
			}

			Byte isObsolete = new Byte(
					isObsolete(deprecatedSlot, frame) ? (byte) 1 : (byte) 0);
			String preferredName = null;
			ProtegeSearchFrame protegeFrame = new ProtegeSearchFrame(
					ontologyBean.getId(), ontologyBean.getOntologyId(),
					ontologyBean.getDisplayLabel(),
					SearchRecordTypeEnum.RECORD_TYPE_PREFERRED_NAME,
					getConceptType(frame), null, isObsolete, frame);

			// add preferred name slot
			for (Slot prefNameSlot : preferredNameSlots) {
				preferredName = addPreferredNameSlotToIndex(writer,
						ontologyBean, kb, protegeFrame, prefNameSlot, owlMode);

				if (!StringHelper.isNullOrNullString(preferredName)) {
					break;
				}
			}

			// add name slot (concept id)
			protegeFrame
					.setRecordType(SearchRecordTypeEnum.RECORD_TYPE_CONCEPT_ID);
			addNameSlotToIndex(writer, ontologyBean, kb, protegeFrame,
					preferredName, owlMode);

			// add synonym slot if exists
			if (synonymSlot != null) {
				protegeFrame
						.setRecordType(SearchRecordTypeEnum.RECORD_TYPE_SYNONYM);
				addSlotToIndex(writer, ontologyBean, kb, protegeFrame,
						synonymSlot, owlMode);
			}

			// add property slots
			protegeFrame
					.setRecordType(SearchRecordTypeEnum.RECORD_TYPE_PROPERTY);

			for (Slot propertySlot : propertySlots) {
				addSlotToIndex(writer, ontologyBean, kb, protegeFrame,
						propertySlot, owlMode);
			}
		}
	}

	/**
	 * Returns an instance of the knowledge base. Provides exception handling
	 * 
	 * @param ontology
	 * @return
	 * @throws Exception
	 */
	private KnowledgeBase getKnowledgeBaseInstance(OntologyBean ontology)
			throws Exception {
		KnowledgeBase kb = null;

		try {
			kb = getKnowledgeBase(ontology);
		} catch (Exception e) {
			Throwable t = e.getCause();
			String className = (t == null) ? "" : t.getClass().getName();

			if (t != null
					&& (className
							.equals("com.mysql.jdbc.exceptions.MySQLSyntaxErrorException") || className
							.equals("com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException"))) {
				throw new Exception("Ontology " + ontology.getDisplayLabel()
						+ " (Id: " + ontology.getId() + ", Ontology Id: "
						+ ontology.getOntologyId()
						+ ") does not exist in Protege back-end");
			} else {
				throw e;
			}
		}

		return kb;
	}

	/**
	 * Adds a single Protege slot to the index
	 * 
	 * @param writer
	 * @param ontologyBean
	 * @param kb
	 * @param nfs
	 * @param protegeFrame
	 * @param slot
	 * @param owlMode
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void addSlotToIndex(LuceneIndexWriterWrapper writer,
			OntologyBean ontologyBean, KnowledgeBase kb,
			ProtegeSearchFrame protegeFrame, Slot slot, boolean owlMode)
			throws IOException {
		Collection values = kb.getOwnSlotValues(protegeFrame.getFrame(), slot);
		List<SearchIndexBean> docs = new ArrayList<SearchIndexBean>(0);

		for (Object value : values) {
			if (!(value instanceof String)) {
				continue;
			}

			SearchIndexBean doc = populateIndexBean(ontologyBean, protegeFrame,
					(String) value, owlMode);
			docs.add(doc);
		}
		writer.addDocuments(docs);
	}

	/**
	 * Adds the preferred name slot to index
	 * 
	 * @param writer
	 * @param kb
	 * @param nfs
	 * @param protegeFrame
	 * @param preferredNameSlot
	 * @param owlMode
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private String addPreferredNameSlotToIndex(LuceneIndexWriterWrapper writer,
			OntologyBean ontologyBean, KnowledgeBase kb,
			ProtegeSearchFrame protegeFrame, Slot preferredNameSlot,
			boolean owlMode) throws IOException {
		String preferredName = null;
		Collection<String> values = new ArrayList<String>();
		List<SearchIndexBean> docs = new ArrayList<SearchIndexBean>(0);

		// add a local name to the index -- critical in cases where the rdf:ID
		// is the only name we have for a resource add a local name to the index
		// so that it comes up as a search result
		// otherwise Lucene treats it as a very bad match since it is only a
		// short substring that matches
		// Also since only 1 preferred name can be involved, the logic is 'add
		// all values, unless its the name slot,
		// then remove all the values and add just the local value'
		// The size is to avoid 'oddball' frames that shouldnt be displayed

		Frame frame = protegeFrame.getFrame();
		values.addAll(kb.getOwnSlotValues(frame, preferredNameSlot));

		if (frame instanceof RDFResource
				&& preferredNameSlot.equals(kb.getNameSlot())
				&& values.size() > 0) {
			values = new ArrayList<String>();
			values.add(((RDFResource) frame).getLocalName());
		}

		for (Object value : values) {
			if (!(value instanceof String)) {
				continue;
			}

			preferredName = (String) value;
			protegeFrame.setPreferredName(preferredName);
			SearchIndexBean doc = populateIndexBean(ontologyBean, protegeFrame,
					(String) value, owlMode);
			docs.add(doc);
			break;
		}
		writer.addDocuments(docs);

		return preferredName;
	}

	/**
	 * Adds the Protege name slot to the index
	 * 
	 * @param writer
	 * @param kb
	 * @param nfs
	 * @param protegeFrame
	 * @param slot
	 * @param owlMode
	 * @throws IOException
	 */
	private void addNameSlotToIndex(LuceneIndexWriterWrapper writer,
			OntologyBean ontologyBean, KnowledgeBase kb,
			ProtegeSearchFrame protegeFrame, String preferredName,
			boolean owlMode) throws IOException {
		Frame frame = protegeFrame.getFrame();
		List<SearchIndexBean> docs = new ArrayList<SearchIndexBean>(0);

		if (frame instanceof RDFResource) {
			String name = ((RDFResource) frame).getLocalName();
			SearchIndexBean doc = populateIndexBean(ontologyBean,
					protegeFrame, name, owlMode);
			docs.add(doc);
			writer.addDocuments(docs);
		}
	}

	/**
	 * Populates the index bean with data for a single record
	 * 
	 * @param ontologyBean
	 * @param luceneProtegeFrame
	 * @param value
	 * @param owlMode
	 */
	private SearchIndexBean populateIndexBean(OntologyBean ontologyBean,
			ProtegeSearchFrame luceneProtegeFrame, String value, boolean owlMode) {
		value = stripLanguageIdentifier(value, owlMode);
		String preferredName = stripLanguageIdentifier(luceneProtegeFrame
				.getPreferredName(), owlMode);
		SearchIndexBean doc = new SearchIndexBean();
		doc.populateInstance(luceneProtegeFrame.getOntologyVersionId(),
				luceneProtegeFrame.getOntologyId(), luceneProtegeFrame
						.getOntologyDisplayLabel(), luceneProtegeFrame
						.getRecordType(), luceneProtegeFrame.getObjectType(),
				getFullId(luceneProtegeFrame.getFrame(), ontologyBean),
				getConceptIdShort(luceneProtegeFrame.getFrame()),
				preferredName, value, null, luceneProtegeFrame.getIsObsolete());

		return doc;
	}

	/**
	 * Removes the language identifier from the value
	 * 
	 * @param value
	 * @param owlMode
	 * @return
	 */
	private String stripLanguageIdentifier(String value, boolean owlMode) {
		if (owlMode && value.startsWith("~#")) {
			value = value.substring(5);
		}

		return value;
	}

	/**
	 * Returns the concept id representation minus the namespace prefix
	 * 
	 * @param frame
	 * @return
	 */
	private String getConceptIdShort(Frame frame) {
		if (frame instanceof RDFResource) {
			RDFResource rdfNode = (RDFResource) frame;
			return NamespaceUtil.getPrefixedName(rdfNode.getOWLModel(), rdfNode
					.getName());
		} else {
			return frame.getName();
		}
	}

	/**
	 * Returns all slots that identify ontology "properties"
	 * 
	 * @param kb
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Set<Slot> getPropertySlots(KnowledgeBase kb) {
		Set<Slot> allSlots = new HashSet<Slot>();
		Set<Slot> propertySlots = new HashSet<Slot>();

		if (kb instanceof OWLModel) {
			OWLModel owl = (OWLModel) kb;
			allSlots.addAll(owl.getOWLAnnotationProperties());
			allSlots.add(owl.getRDFSLabelProperty());
			allSlots.add(owl.getRDFSCommentProperty());
			allSlots.add(owl.getSystemFrames().getNameSlot());
		} else {
			allSlots.addAll(kb.getSlots());
		}

		for (Slot slot : allSlots) {
			ValueType vt = slot.getValueType();

			if (vt.equals(ValueType.ANY) || vt.equals(ValueType.STRING)) {
				propertySlots.add(slot);
			}
		}

		return propertySlots;
	}

	/**
	 * Returns all possible preferred name slots. The caller is responsible for
	 * selecting the most appropriate slot for indexing.
	 * 
	 * @param kb
	 * @param preferredNameSlotName
	 * @return
	 */
	private List<Slot> getPreferredNameSlots(KnowledgeBase kb,
			String preferredNameSlotName) {
		List<Slot> slots = new ArrayList<Slot>();
		Slot slot = null;

		if (!StringHelper.isNullOrNullString(preferredNameSlotName)) {
			slot = kb instanceof OWLModel ? ((OWLModel) kb)
					.getRDFProperty(preferredNameSlotName) : kb
					.getSlot(preferredNameSlotName);

			if (slot != null) {
				slots.add(slot);
			}
		}

		if (kb instanceof OWLModel) {
			slots.add(((OWLModel) kb).getRDFSLabelProperty());
		}

		slots.add(kb.getNameSlot());

		return slots;
	}
}
