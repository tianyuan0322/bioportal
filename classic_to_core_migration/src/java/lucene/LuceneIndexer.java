package lucene;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import lucene.bean.LuceneProtegeSlot;
import lucene.enumeration.LuceneRecordTypeEnum;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.helper.StringHelper;

import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;

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
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class LuceneIndexer {

	public static final String PROPERTY_FILENAME = "build.properties";
	private static Properties properties = new Properties();
	private static final String protegeTablePrefix = "tbl_";
	private static final String protegeTableSuffix = "";
	private static final Analyzer analyzer = new StandardAnalyzer();

	private static final String ONTOLOGY_ID_FIELD = "ontologyId";
	private static final String SLOT_TYPE_FIELD = "slotType";
	private static final String FRAME_NAME_FIELD = "frameName";
	private static final String SLOT_NAME_FIELD = "slotName";
	private static final String CONTENTS_FIELD = "contents";
	private static final String LITERAL_CONTENTS_FIELD = "literalContents";

	public static void main(String[] args) {
		try {
			// contents:"blood" && (ontologyId:1116 || ontologyId:1053)

			index();

			executeQuery("blood");

			Collection<Integer> ontologyIds = new ArrayList<Integer>();
			ontologyIds.add(1053);
			ontologyIds.add(1089);
			executeQuery("blood", ontologyIds);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static {
		try {
			properties.load(new FileInputStream(PROPERTY_FILENAME));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void executeQuery(String expr) throws IOException {
		executeQuery(expr, null);
	}

	public static void executeQuery(String expr, Collection<Integer> ontologyIds)
			throws IOException {
		Searcher searcher = null;
		Collection<Frame> results = new LinkedHashSet<Frame>();

		Query q = generateLuceneQuery(ontologyIds, expr);

		searcher = new IndexSearcher(getIndexPath());
		Hits hits = searcher.search(q);

		System.out.println("Query:" + q);
		System.out.println("Hits:" + hits.length());
	}

	@SuppressWarnings("unchecked")
	public static void index() throws Exception {
		Connection connBioPortal = connectBioPortal();
		ResultSet rs = findAllProtegeOntologies(connBioPortal);
		IndexWriter writer = openWriter(true);

		while (rs.next()) {
			try {
				String displayLabel = rs.getString("display_label");

				System.out.println("Indexing ontology: " + displayLabel
						+ " (Id: " + rs.getInt("id") + ", Ontology Id: "
						+ rs.getInt("ontology_id") + ", Format: "
						+ rs.getString("format") + ")");

				long start = System.currentTimeMillis();

				KnowledgeBase kb = createKnowledgeBaseInstance(rs);
				boolean owlMode = kb instanceof OWLModel;
				Set<LuceneProtegeSlot> searchableSlots = getSearchableSlots(kb,
						rs.getInt("ontology_id"), rs.getString("synonym_slot"),
						rs.getString("preferred_name_slot"));

				FrameStore fs = ((DefaultKnowledgeBase) kb)
						.getTerminalFrameStore();
				NarrowFrameStore nfs = ((SimpleFrameStore) fs).getHelper();

				Set<Frame> frames;
				Collection values;

				synchronized (kb) {
					frames = nfs.getFrames();
				}

				for (Frame frame : frames) {
					for (LuceneProtegeSlot luceneSlot : searchableSlots) {
						synchronized (kb) {
							values = nfs.getValues(frame, luceneSlot.getSlot(),
									null, false);
						}

						for (Object value : values) {
							if (!(value instanceof String)) {
								continue;
							}

							addDocument(writer, nfs, frame, luceneSlot,
									(String) value, owlMode);
						}

						// values.clear();
						// values = null;
					}
				}

				kb.dispose();
				kb = null;

				long stop = System.currentTimeMillis(); // stop timing
				System.out.println("Finished indexing ontology: "
						+ displayLabel + " in " + (double) (stop - start)
						/ 1000 + " seconds.");

			} catch (RuntimeException e) {
				Throwable t = e.getCause();

				if (!(t instanceof MySQLSyntaxErrorException)) {
					throw new Exception(t);
				} else {
					System.out.println("Ontology: "
							+ rs.getString("display_label") + " (Id: "
							+ rs.getInt("id") + ", Ontology Id: "
							+ rs.getInt("ontology_id")
							+ ") does not exist in Protege");
				}
			}
		}

		writer.optimize();
		forceWriterClose(writer);
	}

	private static Query generateLuceneQuery(Collection<Integer> ontologyIds,
			String expr) throws IOException {
		BooleanQuery query = new BooleanQuery();
		QueryParser parser = new QueryParser(CONTENTS_FIELD, analyzer);
		parser.setAllowLeadingWildcard(true);

		try {
			query.add(parser.parse(expr), BooleanClause.Occur.MUST);
		} catch (ParseException e) {
			IOException ioe = new IOException(e.getMessage());
			ioe.initCause(e);
			throw ioe;
		}

		if (ontologyIds != null && !ontologyIds.isEmpty()) {
			BooleanQuery ontologyIdQuery = new BooleanQuery();

			for (Integer ontologyId : ontologyIds) {
				Term term = new Term(ONTOLOGY_ID_FIELD, ontologyId.toString());
				ontologyIdQuery.add(new TermQuery(term),
						BooleanClause.Occur.SHOULD);
			}

			query.add(ontologyIdQuery, BooleanClause.Occur.MUST);
		}

		return query;
	}

	private static void forceWriterClose(IndexWriter writer) {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private static void addDocument(IndexWriter writer, NarrowFrameStore nfs,
			Frame frame, LuceneProtegeSlot luceneSlot, String value,
			boolean owlMode) throws IOException {
		if (owlMode && value.startsWith("~#")) {
			value = value.substring(5);
		}

		Document doc = new Document();

		doc.add(new Field(ONTOLOGY_ID_FIELD, luceneSlot.getOntologyId()
				.toString(), Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(SLOT_TYPE_FIELD, luceneSlot.getSlotType().getLabel(),
				Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(FRAME_NAME_FIELD, getFrameName(nfs, frame),
				Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(SLOT_NAME_FIELD, getFrameName(nfs, luceneSlot
				.getSlot()), Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(CONTENTS_FIELD, value, Field.Store.YES,
				Field.Index.TOKENIZED));
		doc.add(new Field(LITERAL_CONTENTS_FIELD, value, Field.Store.YES,
				Field.Index.UN_TOKENIZED));

		writer.addDocument(doc);
	}

	@SuppressWarnings("unchecked")
	private static String getFrameName(NarrowFrameStore nfs, Frame frame) {
		Collection values = nfs.getValues(frame, (Slot) nfs
				.getFrame(Model.SlotID.NAME), null, false);

		if (values == null || values.isEmpty()) {
			return null;
		}

		return (String) values.iterator().next();
	}

	private static Set<LuceneProtegeSlot> getSearchableSlots(KnowledgeBase kb,
			Integer ontologyId, String synonymSlotName,
			String preferredNameSlotName) {
		Set<LuceneProtegeSlot> searchableSlots = new HashSet<LuceneProtegeSlot>();

		// add synonym slot if exists
		Slot synonymSlot = getSynonymSlot(kb, synonymSlotName);

		if (synonymSlot != null) {
			searchableSlots.add(new LuceneProtegeSlot(synonymSlot, ontologyId,
					LuceneRecordTypeEnum.RECORD_TYPE_SYNONYM));
		}

		// add preferred name slot
		searchableSlots.add(new LuceneProtegeSlot(getPreferredNameSlot(kb,
				preferredNameSlotName), ontologyId,
				LuceneRecordTypeEnum.RECORD_TYPE_PREFERRED_NAME));

		// add property slots
		Set<Slot> propertySlots = getPropertySlots(kb);

		for (Slot propertySlot : propertySlots) {
			searchableSlots.add(new LuceneProtegeSlot(propertySlot, ontologyId,
					LuceneRecordTypeEnum.RECORD_TYPE_PROPERTY));
		}

		return searchableSlots;
	}

	@SuppressWarnings("unchecked")
	private static Set<Slot> getPropertySlots(KnowledgeBase kb) {
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

	/**
	 * Gets the Protege ontology associated with the specified ontology id.
	 * 
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	private static KnowledgeBase createKnowledgeBaseInstance(ResultSet rs)
			throws SQLException {
		DatabaseKnowledgeBaseFactory factory = null;

		if (rs.getString("format").contains(ApplicationConstants.FORMAT_OWL)) {
			factory = new OWLDatabaseKnowledgeBaseFactory();
		} else {
			factory = new DatabaseKnowledgeBaseFactory();
		}

		List errors = new ArrayList();
		Project prj = Project.createBuildProject(factory, errors);
		DatabaseKnowledgeBaseFactory.setSources(prj.getSources(), properties
				.getProperty("protege.jdbc.driver"), properties
				.getProperty("protege.jdbc.url"),
				getTableName(rs.getInt("id")), properties
						.getProperty("protege.jdbc.username"), properties
						.getProperty("protege.jdbc.password"));
		prj.createDomainKnowledgeBase(factory, errors, true);
		KnowledgeBase kb = prj.getKnowledgeBase();

		setBrowserSlotByPreferredNameSlot(kb, getPreferredNameSlot(kb, rs
				.getString("preferred_name_slot")));

		return kb;
	}

	private static void setBrowserSlotByPreferredNameSlot(KnowledgeBase kb,
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

	private static Slot getSynonymSlot(KnowledgeBase kb, String synonymSlot) {
		if (!StringHelper.isNullOrNullString(synonymSlot)) {
			return (kb instanceof OWLModel ? ((OWLModel) kb)
					.getRDFProperty(synonymSlot) : kb.getSlot(synonymSlot));
		}

		return null;
	}

	private static Slot getPreferredNameSlot(KnowledgeBase kb,
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
	private static String getTableName(Integer ontologyVersionId) {
		return protegeTablePrefix + ontologyVersionId + protegeTableSuffix;
	}

	private static IndexWriter openWriter(boolean create) throws IOException {
		return new IndexWriter(getIndexPath(), analyzer, create);
	}

	private static ResultSet findAllProtegeOntologies(Connection conn)
			throws SQLException {
		String sqlSelect = "SELECT ont.* "
				+ "FROM "
				+ "	v_ncbo_ontology ont "
				+ "    INNER JOIN ( "
				+ "		SELECT ontology_id, MAX(internal_version_number) AS internal_version_number "
				+ "		FROM "
				+ "			ncbo_ontology_version "
				+ "		WHERE status_id = ? "
				+ "		GROUP BY ontology_id "
				+ "	) a ON ont.ontology_id = a.ontology_id AND ont.internal_version_number = a.internal_version_number "
				+ "WHERE "
				+ "	upper(ont.format) in ('PROTEGE', 'OWL-FULL', 'OWL-DL', 'OWL-LITE') "

				// + " and id = 13578 "
				+ "AND id = 29684 "

				+ "ORDER BY " + "ont.display_label" + " LIMIT 10";

		PreparedStatement stmt = conn.prepareStatement(sqlSelect);
		stmt.setInt(1, 3);

		return stmt.executeQuery();
	}

	private static String getIndexPath() {
		return properties.getProperty("bioportal.resource.path") + "/lucene";
	}

	/**
	 * Connect to BioPortal Protege db
	 * 
	 * @return Connection
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static Connection connectBioPortalProtege() throws SQLException,
			ClassNotFoundException {
		return connect("protege.jdbc.url", "protege.jdbc.driver",
				"protege.jdbc.username", "protege.jdbc.password");
	}

	/**
	 * Connect to BioPortal db
	 * 
	 * @return Connection
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static Connection connectBioPortal() throws SQLException,
			ClassNotFoundException {
		return connect("bioportal.jdbc.url", "bioportal.jdbc.driver",
				"bioportal.jdbc.username", "bioportal.jdbc.password");
	}

	/**
	 * Connect to a db
	 * 
	 * @return Connection
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static Connection connect(String url, String driver,
			String username, String password) throws SQLException,
			ClassNotFoundException {
		String jdbcUrl = properties.getProperty(url);
		String jdbcDriver = properties.getProperty(driver);
		String jdbcUsername = properties.getProperty(username);
		String jdbcPassword = properties.getProperty(password);

		// Load the JDBC driver
		Class.forName(jdbcDriver);
		return DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
	}
}
