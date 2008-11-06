package lucene.manager.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lucene.bean.LuceneProtegeFrame;
import lucene.bean.LuceneSearchDocument;
import lucene.enumeration.LuceneRecordTypeEnum;
import lucene.manager.LuceneSearchManager;
import lucene.wrapper.IndexWriterWrapper;

import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.DefaultKnowledgeBase;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.SimpleFrameStore;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;

public class LuceneSearchManagerProtegeImpl implements LuceneSearchManager {

	private final String protegeTablePrefix = "tbl_";
	private final String protegeTableSuffix = "";

	// TODO: Throwaway code ===============================================

	private String protegeJdbcUrl;
	private String protegeJdbcDriver;
	private String protegeJdbcUsername;
	private String protegeJdbcPassword;

	/**
	 * @param protegeJdbcUrl
	 * @param protegeJdbcDriver
	 * @param protegeJdbcUsername
	 * @param protegeJdbcPassword
	 */
	public LuceneSearchManagerProtegeImpl(String protegeJdbcUrl,
			String protegeJdbcDriver, String protegeJdbcUsername,
			String protegeJdbcPassword) {
		super();
		this.protegeJdbcUrl = protegeJdbcUrl;
		this.protegeJdbcDriver = protegeJdbcDriver;
		this.protegeJdbcUsername = protegeJdbcUsername;
		this.protegeJdbcPassword = protegeJdbcPassword;
	}

	// TODO: END, Throwaway code ==========================================

	@SuppressWarnings("unchecked")
	public void indexOntology(IndexWriterWrapper writer, ResultSet rs)
			throws Exception {
		String displayLabel = rs.getString("display_label");
		Integer ontologyId = rs.getInt("ontology_id");
		System.out.println("Adding ontology to index: " + displayLabel
				+ " (Id: " + rs.getInt("id") + ", Ontology Id: " + ontologyId
				+ ", Format: " + rs.getString("format") + ")");
		long start = System.currentTimeMillis();

		KnowledgeBase kb = createKnowledgeBaseInstance(rs);
		boolean owlMode = kb instanceof OWLModel;
		List<LuceneProtegeFrame> searchableSlots = getSearchableSlots(kb,
				ontologyId, rs.getString("synonym_slot"), rs
						.getString("preferred_name_slot"));

		FrameStore fs = ((DefaultKnowledgeBase) kb).getTerminalFrameStore();
		NarrowFrameStore nfs = ((SimpleFrameStore) fs).getHelper();

		Set<Frame> frames;
		Collection values;

		synchronized (kb) {
			frames = nfs.getFrames();
		}

		LuceneSearchDocument doc = new LuceneSearchDocument();

		for (Frame frame : frames) {
			String preferredName = null;
			boolean isFirst = true;

			for (LuceneProtegeFrame luceneSlot : searchableSlots) {
				synchronized (kb) {
					values = nfs.getValues(frame, (Slot) luceneSlot.getFrame(),
							null, false);
				}

				for (Object value : values) {
					if (!(value instanceof String)) {
						continue;
					}

					// the first slot is always the preferred name slot
					if (isFirst) {
						preferredName = (String) value;
						isFirst = false;
					}

					LuceneProtegeFrame luceneProtegeFrame = new LuceneProtegeFrame(
							frame, luceneSlot.getOntologyId(), preferredName,
							luceneSlot.getPropertyType());
					setLuceneSearchDocument(doc, nfs, luceneProtegeFrame,
							(String) value, owlMode);
					writer.addDocument(doc);
				}
			}
		}

		kb.dispose();
		kb = null;

		long stop = System.currentTimeMillis(); // stop timing
		System.out.println("Finished indexing ontology: " + displayLabel
				+ " in " + (double) (stop - start) / 1000 / 60 + " minutes.");
	}

	private List<LuceneProtegeFrame> getSearchableSlots(KnowledgeBase kb,
			Integer ontologyId, String synonymSlotName,
			String preferredNameSlotName) {
		List<LuceneProtegeFrame> searchableSlots = new ArrayList<LuceneProtegeFrame>();

		// add preferred name slot
		// we always assume that the preferred name slot
		// is the first element on the list
		searchableSlots.add(new LuceneProtegeFrame(getPreferredNameSlot(kb,
				preferredNameSlotName), ontologyId, null,
				LuceneRecordTypeEnum.RECORD_TYPE_PREFERRED_NAME));

		// add synonym slot if exists
		Slot synonymSlot = getSynonymSlot(kb, synonymSlotName);

		if (synonymSlot != null) {
			searchableSlots.add(new LuceneProtegeFrame(synonymSlot, ontologyId,
					null, LuceneRecordTypeEnum.RECORD_TYPE_SYNONYM));
		}

		// add property slots
		Set<Slot> propertySlots = getPropertySlots(kb);

		for (Slot propertySlot : propertySlots) {
			searchableSlots
					.add(new LuceneProtegeFrame(propertySlot, ontologyId, null,
							LuceneRecordTypeEnum.RECORD_TYPE_PROPERTY));
		}

		return searchableSlots;
	}

	private void setLuceneSearchDocument(LuceneSearchDocument doc,
			NarrowFrameStore nfs, LuceneProtegeFrame luceneProtegeFrame,
			String value, boolean owlMode) {
		value = stripLanguageIdentifier(value, owlMode);
		String preferredName = stripLanguageIdentifier(luceneProtegeFrame
				.getPreferredName(), owlMode);

		doc.setOntologyId(luceneProtegeFrame.getOntologyId().toString());
		doc.setRecordType(luceneProtegeFrame.getPropertyType());
		doc.setConceptId(getFrameName(nfs, luceneProtegeFrame.getFrame()));
		doc.setConceptIdShort(getConceptIdShort(luceneProtegeFrame.getFrame()));
		doc.setPreferredName(preferredName);
		doc.setContents(value);
		doc.setLiteralContents(value);
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

	/**
	 * Gets the Protege ontology associated with the specified ontology id.
	 * 
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	private KnowledgeBase createKnowledgeBaseInstance(ResultSet rs)
			throws SQLException {
		DatabaseKnowledgeBaseFactory factory = null;

		if (rs.getString("format").contains(ApplicationConstants.FORMAT_OWL)) {
			factory = new OWLDatabaseKnowledgeBaseFactory();
		} else {
			factory = new DatabaseKnowledgeBaseFactory();
		}

		List errors = new ArrayList();
		Project prj = Project.createBuildProject(factory, errors);
		DatabaseKnowledgeBaseFactory.setSources(prj.getSources(),
				protegeJdbcDriver, protegeJdbcUrl,
				getTableName(rs.getInt("id")), protegeJdbcUsername,
				protegeJdbcPassword);
		prj.createDomainKnowledgeBase(factory, errors, true);
		KnowledgeBase kb = prj.getKnowledgeBase();

		setBrowserSlotByPreferredNameSlot(kb, getPreferredNameSlot(kb, rs
				.getString("preferred_name_slot")));

		return kb;
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
			allSlots.add(kb.getSystemFrames().getNameSlot());
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

	private void setBrowserSlotByPreferredNameSlot(KnowledgeBase kb,
			Slot preferredNameSlot) {
		Set<Cls> types = new HashSet<Cls>();

		if (kb instanceof OWLModel) {
			OWLModel owlModel = (OWLModel) kb;
			types.add(owlModel.getRDFSNamedClassClass());
			types.add(owlModel.getOWLNamedClassClass());

			types.add(owlModel.getRDFPropertyClass());
			types.add(owlModel.getOWLObjectPropertyClass());
			types.add(owlModel.getOWLDatatypePropertyClass());

			types.add(owlModel.getRootCls());
		} else {
			types.add(kb.getRootClsMetaCls());
			types.add(kb.getRootSlotMetaCls());
			types.add(kb.getRootCls());
		}

		for (Cls cls : types) {
			cls.setDirectBrowserSlot(preferredNameSlot);
		}
	}

	private Slot getSynonymSlot(KnowledgeBase kb, String synonymSlot) {
		if (!StringHelper.isNullOrNullString(synonymSlot)) {
			return (kb instanceof OWLModel ? ((OWLModel) kb)
					.getRDFProperty(synonymSlot) : kb.getSlot(synonymSlot));
		}

		return null;
	}

	private Slot getPreferredNameSlot(KnowledgeBase kb,
			String preferredNameSlotName) {
		Slot slot = null;

		if (!StringHelper.isNullOrNullString(preferredNameSlotName)) {
			slot = kb instanceof OWLModel ? ((OWLModel) kb)
					.getRDFProperty(preferredNameSlotName) : kb
					.getSlot(preferredNameSlotName);
		}

		if (slot == null) {
			slot = kb instanceof OWLModel ? ((OWLModel) kb)
					.getRDFSLabelProperty() : kb.getNameSlot();
		}

		return slot;
	}

	/**
	 * Gets the table name associated with an protege ontology id.
	 */
	private String getTableName(Integer ontologyVersionId) {
		return protegeTablePrefix + ontologyVersionId + protegeTableSuffix;
	}
}
