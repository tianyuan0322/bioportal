package lucene.bean;

import lucene.enumeration.LuceneRecordTypeEnum;

import org.apache.lucene.document.Field;

public class LuceneSearchDocument {
	public static final String ONTOLOGY_VERSION_ID_FIELD_LABEL = "ontologyVersionId";
	public static final String ONTOLOGY_ID_FIELD_LABEL = "ontologyId";
	public static final String ONTOLOGY_DISPLAY_LABEL_FIELD_LABEL = "ontologyDisplayLabel";
	public static final String RECORD_TYPE_FIELD_LABEL = "recordType";
	public static final String CONCEPT_ID_FIELD_LABEL = "conceptId";
	public static final String CONCEPT_ID_SHORT_FIELD_LABEL = "conceptIdShort";
	public static final String PREFERRED_NAME_FIELD_LABEL = "preferredName";
	public static final String CONTENTS_FIELD_LABEL = "contents";
	public static final String LITERAL_CONTENTS_FIELD_LABEL = "literalContents";

	private LuceneSearchField ontologyVersionId = new LuceneSearchField(
			ONTOLOGY_VERSION_ID_FIELD_LABEL, Field.Store.YES,
			Field.Index.NOT_ANALYZED);
	private LuceneSearchField ontologyId = new LuceneSearchField(
			ONTOLOGY_ID_FIELD_LABEL, Field.Store.YES, Field.Index.NOT_ANALYZED);
	private LuceneSearchField ontologyDisplayLabel = new LuceneSearchField(
			ONTOLOGY_DISPLAY_LABEL_FIELD_LABEL, Field.Store.YES,
			Field.Index.NOT_ANALYZED);
	private LuceneSearchField recordType = new LuceneSearchField(
			RECORD_TYPE_FIELD_LABEL, Field.Store.YES, Field.Index.NOT_ANALYZED);
	private LuceneSearchField conceptId = new LuceneSearchField(
			CONCEPT_ID_FIELD_LABEL, Field.Store.YES, Field.Index.NOT_ANALYZED);
	private LuceneSearchField conceptIdShort = new LuceneSearchField(
			CONCEPT_ID_SHORT_FIELD_LABEL, Field.Store.YES,
			Field.Index.NOT_ANALYZED);
	private LuceneSearchField preferredName = new LuceneSearchField(
			PREFERRED_NAME_FIELD_LABEL, Field.Store.YES,
			Field.Index.NOT_ANALYZED);
	private LuceneSearchField contents = new LuceneSearchField(
			CONTENTS_FIELD_LABEL, Field.Store.YES, Field.Index.ANALYZED);
	private LuceneSearchField literalContents = new LuceneSearchField(
			LITERAL_CONTENTS_FIELD_LABEL, Field.Store.YES,
			Field.Index.NOT_ANALYZED);

	public LuceneSearchDocument() {
	}

	/**
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param recordType
	 * @param conceptId
	 * @param conceptIdShort
	 * @param preferredName
	 * @param contents
	 * @param literalContents
	 */
	public LuceneSearchDocument(String ontologyVersionId, String ontologyId,
			String ontologyDisplayLabel, LuceneRecordTypeEnum recordType,
			String conceptId, String conceptIdShort, String preferredName,
			String contents, String literalContents) {
		super();
		setOntologyVersionId(ontologyVersionId);
		setOntologyId(ontologyId);
		setOntologyDisplayLabel(ontologyDisplayLabel);
		setRecordType(recordType);
		setConceptId(conceptId);
		setConceptIdShort(conceptIdShort);
		setPreferredName(preferredName);
		setContents(contents);
		setLiteralContents(literalContents);
	}

	/**
	 * @param ontologyVersionId
	 *            the ontologyVersionId to set
	 */
	public void setOntologyVersionId(String ontologyVersionId) {
		this.ontologyVersionId.setContents(ontologyVersionId);
	}

	/**
	 * @param ontologyId
	 *            the ontologyId to set
	 */
	public void setOntologyId(String ontologyId) {
		this.ontologyId.setContents(ontologyId);
	}

	/**
	 * @param ontologyDisplayLabel
	 *            the ontologyVersionId to set
	 */
	public void setOntologyDisplayLabel(String ontologyDisplayLabel) {
		this.ontologyDisplayLabel.setContents(ontologyDisplayLabel);
	}

	/**
	 * @param recordType
	 *            the recordType to set
	 */
	public void setRecordType(LuceneRecordTypeEnum recordType) {
		this.recordType.setContents(recordType.getLabel());
	}

	/**
	 * @param conceptId
	 *            the frameName to set
	 */
	public void setConceptId(String conceptId) {
		this.conceptId.setContents(conceptId);
	}

	/**
	 * @param conceptIdShort
	 *            the conceptIdShort to set
	 */
	public void setConceptIdShort(String conceptIdShort) {
		this.conceptIdShort.setContents(conceptIdShort);
	}

	/**
	 * @param preferredName
	 *            the preferredName to set
	 */
	public void setPreferredName(String preferredName) {
		this.preferredName.setContents(preferredName);
	}

	/**
	 * @param contents
	 *            the contents to set
	 */
	public void setContents(String contents) {
		this.contents.setContents(contents);
	}

	/**
	 * @param literalContents
	 *            the literalContents to set
	 */
	public void setLiteralContents(String literalContents) {
		this.literalContents.setContents((literalContents == null) ? null
				: literalContents.trim().toLowerCase());
	}

	/**
	 * @return the ontologyVersionId
	 */
	public LuceneSearchField getOntologyVersionId() {
		return ontologyVersionId;
	}

	/**
	 * @return the ontologyId
	 */
	public LuceneSearchField getOntologyId() {
		return ontologyId;
	}

	/**
	 * @return the ontologyDisplayLabel
	 */
	public LuceneSearchField getOntologyDisplayLabel() {
		return ontologyDisplayLabel;
	}

	/**
	 * @return the recordType
	 */
	public LuceneSearchField getRecordType() {
		return recordType;
	}

	/**
	 * @return the coneptId
	 */
	public LuceneSearchField getConceptId() {
		return conceptId;
	}

	/**
	 * @return the conceptIdShort
	 */
	public LuceneSearchField getConceptIdShort() {
		return conceptIdShort;
	}

	/**
	 * @return the preferredName
	 */
	public LuceneSearchField getPreferredName() {
		return preferredName;
	}

	/**
	 * @return the contents
	 */
	public LuceneSearchField getContents() {
		return contents;
	}

	/**
	 * @return the literalContents
	 */
	public LuceneSearchField getLiteralContents() {
		return literalContents;
	}
}