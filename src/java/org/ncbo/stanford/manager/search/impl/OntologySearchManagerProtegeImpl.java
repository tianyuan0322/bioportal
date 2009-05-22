package org.ncbo.stanford.manager.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.search.ProtegeSearchFrame;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.search.OntologySearchManager;
import org.ncbo.stanford.util.helper.StringHelper;
import org.ncbo.stanford.wrapper.LuceneIndexWriterWrapper;

import edu.stanford.smi.protege.model.DefaultKnowledgeBase;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.SimpleFrameStore;
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

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(OntologySearchManagerProtegeImpl.class);

	/**
	 * Index a given ontology
	 * 
	 * @param writer
	 * @param ontology
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void indexOntology(LuceneIndexWriterWrapper writer,
			OntologyBean ontology) throws Exception {
		KnowledgeBase kb = getKnowledgeBaseInstance(ontology);
		boolean owlMode = kb instanceof OWLModel;
		FrameStore fs = ((DefaultKnowledgeBase) kb).getTerminalFrameStore();
		NarrowFrameStore nfs = ((SimpleFrameStore) fs).getHelper();
		Set<Frame> frames;

		synchronized (kb) {
			frames = nfs.getFrames();
		}

		SearchIndexBean doc = new SearchIndexBean();

		for (Frame frame : frames) {
			// exclude anonymous classes from being indexed
			if (frame instanceof RDFResource
					&& ((RDFResource) frame).isAnonymous()) {
				continue;
			}

			// add preferred name slot
			String preferredName = null;
			List<Slot> preferredNameSlots = getPreferredNameSlots(kb, ontology
					.getPreferredNameSlot());
			ProtegeSearchFrame protegeFrame = new ProtegeSearchFrame(ontology
					.getId(), ontology.getOntologyId(), ontology
					.getDisplayLabel(),
					SearchRecordTypeEnum.RECORD_TYPE_PREFERRED_NAME, null,
					frame);

			for (Slot prefNameSlot : preferredNameSlots) {
				preferredName = addPreferredNameSlotToIndex(writer, doc, kb,
						nfs, protegeFrame, prefNameSlot, owlMode);

				if (!StringHelper.isNullOrNullString(preferredName)) {
					break;
				}
			}

			// add synonym slot if exists
			Slot synonymSlot = getSynonymSlot(kb, ontology.getSynonymSlot());

			if (synonymSlot != null) {
				protegeFrame
						.setRecordType(SearchRecordTypeEnum.RECORD_TYPE_SYNONYM);
				addSlotToIndex(writer, doc, kb, nfs, protegeFrame, synonymSlot,
						owlMode);
			}

			// add property slots
			Set<Slot> propertySlots = getPropertySlots(kb);
			protegeFrame
					.setRecordType(SearchRecordTypeEnum.RECORD_TYPE_PROPERTY);

			for (Slot propertySlot : propertySlots) {
				addSlotToIndex(writer, doc, kb, nfs, protegeFrame,
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
	 * @param doc
	 * @param kb
	 * @param nfs
	 * @param protegeFrame
	 * @param slot
	 * @param owlMode
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void addSlotToIndex(LuceneIndexWriterWrapper writer,
			SearchIndexBean doc, KnowledgeBase kb, NarrowFrameStore nfs,
			ProtegeSearchFrame protegeFrame, Slot slot, boolean owlMode)
			throws IOException {
		Collection values;

		synchronized (kb) {
			values = nfs.getValues(protegeFrame.getFrame(), slot, null, false);
		}

		for (Object value : values) {
			if (!(value instanceof String)) {
				continue;
			}

			populateIndexBean(doc, nfs, protegeFrame, (String) value, owlMode);
			writer.addDocument(doc);
		}
	}

	/**
	 * Adds the preferred name slot to index
	 * 
	 * @param writer
	 * @param doc
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
			SearchIndexBean doc, KnowledgeBase kb, NarrowFrameStore nfs,
			ProtegeSearchFrame protegeFrame, Slot preferredNameSlot,
			boolean owlMode) throws IOException {
		String preferredName = null;
		Collection values = new ArrayList();

		// add a local name to the index -- critical in cases where the rdf:ID
		// is the only name we have for a resource add a local name to the index
		// so that it comes up as a search result
		// otherwise Lucene treats it as a very bad match since it is only a
		// short substring that matches
		// Also since only 1 preferred name can be involved, the logic is 'add
		// all values, unless its the name slot,
		// then remove all the values and add just the local value'
		// The size is to avoid 'oddball' frames that shouldnt be displayed
		synchronized (kb) {
			Frame frame = protegeFrame.getFrame();
			values.addAll(nfs.getValues(frame, preferredNameSlot, null, false));

			if (frame instanceof RDFResource
					&& preferredNameSlot.equals(kb.getNameSlot())
					&& values.size() > 0) {
				values = new ArrayList();
				values.add(((RDFResource) frame).getLocalName());
			}
		}

		for (Object value : values) {
			if (!(value instanceof String)) {
				continue;
			}
			preferredName = (String) value;
			protegeFrame.setPreferredName(preferredName);
			populateIndexBean(doc, nfs, protegeFrame, (String) value, owlMode);
			writer.addDocument(doc);

			break;
		}

		return preferredName;
	}

	/**
	 * Populates the index bean with data for a single record
	 * 
	 * @param doc
	 * @param nfs
	 * @param luceneProtegeFrame
	 * @param value
	 * @param owlMode
	 */
	private void populateIndexBean(SearchIndexBean doc, NarrowFrameStore nfs,
			ProtegeSearchFrame luceneProtegeFrame, String value, boolean owlMode) {
		value = stripLanguageIdentifier(value, owlMode);
		String preferredName = stripLanguageIdentifier(luceneProtegeFrame
				.getPreferredName(), owlMode);

		doc.populateInstance(luceneProtegeFrame.getOntologyVersionId(),
				luceneProtegeFrame.getOntologyId(), luceneProtegeFrame
						.getOntologyDisplayLabel(), luceneProtegeFrame
						.getRecordType(), getFrameName(nfs, luceneProtegeFrame
						.getFrame()), getConceptIdShort(luceneProtegeFrame
						.getFrame()), preferredName, value);
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
	 * Returns the name of a given frame
	 * 
	 * @param nfs
	 * @param frame
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getFrameName(NarrowFrameStore nfs, Frame frame) {
		Collection values = nfs.getValues(frame, (Slot) nfs
				.getFrame(Model.SlotID.NAME), null, false);

		if (values == null || values.isEmpty()) {
			return null;
		}

		return (String) values.iterator().next();
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
