package org.ncbo.stanford.manager.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.search.ProtegeSearchFrame;
import org.ncbo.stanford.bean.search.SearchIndexBean;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
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

public class OntologySearchManagerProtegeImpl extends
		AbstractOntologyManagerProtege implements OntologySearchManager {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(OntologySearchManagerProtegeImpl.class);

	@SuppressWarnings("unchecked")
	public void indexOntology(LuceneIndexWriterWrapper writer,
			VNcboOntology ontology) throws Exception {
		KnowledgeBase kb = getKnowledgeBase(ontology);
		boolean owlMode = kb instanceof OWLModel;
		FrameStore fs = ((DefaultKnowledgeBase) kb).getTerminalFrameStore();
		NarrowFrameStore nfs = ((SimpleFrameStore) fs).getHelper();
		Set<Frame> frames;

		synchronized (kb) {
			frames = nfs.getFrames();
		}

		SearchIndexBean doc = new SearchIndexBean();

		for (Frame frame : frames) {
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

	@SuppressWarnings("unchecked")
	private String addPreferredNameSlotToIndex(LuceneIndexWriterWrapper writer,
			SearchIndexBean doc, KnowledgeBase kb, NarrowFrameStore nfs,
			ProtegeSearchFrame protegeFrame, Slot preferredNameSlot,
			boolean owlMode) throws IOException {
		String preferredName = null;
		Collection values;

		synchronized (kb) {
			values = nfs.getValues(protegeFrame.getFrame(), preferredNameSlot,
					null, false);
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
						.getFrame()), preferredName, value, value);
	}

	private String stripLanguageIdentifier(String value, boolean owlMode) {
		if (owlMode && value.startsWith("~#")) {
			value = value.substring(5);
		}

		return value;
	}

	@SuppressWarnings("unchecked")
	private String getFrameName(NarrowFrameStore nfs, Frame frame) {
		Collection values = nfs.getValues(frame, (Slot) nfs
				.getFrame(Model.SlotID.NAME), null, false);

		if (values == null || values.isEmpty()) {
			return null;
		}

		return (String) values.iterator().next();
	}

	private String getConceptIdShort(Frame frame) {
		if (frame instanceof RDFResource) {
			RDFResource rdfNode = (RDFResource) frame;
			return NamespaceUtil.getPrefixedName(rdfNode.getOWLModel(), rdfNode
					.getName());
		} else {
			return frame.getName();
		}
	}

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