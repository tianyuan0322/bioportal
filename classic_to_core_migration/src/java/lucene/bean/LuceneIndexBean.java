package lucene.bean;

import lucene.enumeration.LuceneRecordTypeEnum;

import org.apache.lucene.document.Field;

public class LuceneIndexBean extends LuceneSearchBean {
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

	/**
	 * @param ontologyVersionId
	 *            the ontologyVersionId to set
	 */
	public void setOntologyVersionId(Integer ontologyVersionId) {
		super.setOntologyVersionId(ontologyVersionId);
		this.ontologyVersionId.setContents((ontologyVersionId == null) ? null
				: ontologyVersionId.toString());
	}

	/**
	 * @param ontologyId
	 *            the ontologyId to set
	 */
	public void setOntologyId(Integer ontologyId) {
		super.setOntologyId(ontologyId);
		this.ontologyId.setContents((ontologyId == null) ? null : ontologyId
				.toString());
	}

	/**
	 * @param ontologyDisplayLabel
	 *            the ontologyVersionId to set
	 */
	public void setOntologyDisplayLabel(String ontologyDisplayLabel) {
		super.setOntologyDisplayLabel(ontologyDisplayLabel);
		this.ontologyDisplayLabel.setContents(ontologyDisplayLabel);
	}

	/**
	 * @param recordType
	 *            the recordType to set
	 */
	public void setRecordType(LuceneRecordTypeEnum recordType) {
		super.setRecordType(recordType);
		this.recordType.setContents((recordType == null) ? null : recordType
				.getLabel());
	}

	/**
	 * @param conceptId
	 *            the frameName to set
	 */
	public void setConceptId(String conceptId) {
		super.setConceptId(conceptId);
		this.conceptId.setContents(conceptId);
	}

	/**
	 * @param conceptIdShort
	 *            the conceptIdShort to set
	 */
	public void setConceptIdShort(String conceptIdShort) {
		super.setConceptIdShort(conceptIdShort);
		this.conceptIdShort.setContents(conceptIdShort);
	}

	/**
	 * @param preferredName
	 *            the preferredName to set
	 */
	public void setPreferredName(String preferredName) {
		super.setPreferredName(preferredName);
		this.preferredName.setContents(preferredName);
	}

	/**
	 * @param contents
	 *            the contents to set
	 */
	public void setContents(String contents) {
		super.setContents(contents);
		this.contents.setContents(contents);
	}

	/**
	 * @param literalContents
	 *            the literalContents to set
	 */
	public void setLiteralContents(String literalContents) {
		super.setLiteralContents(literalContents);
		this.literalContents.setContents((literalContents == null) ? null
				: literalContents.trim().toLowerCase());
	}

	/**
	 * @return the ontologyVersionId
	 */
	public LuceneSearchField getOntologyVersionIdField() {
		return ontologyVersionId;
	}

	/**
	 * @return the ontologyId
	 */
	public LuceneSearchField getOntologyIdField() {
		return ontologyId;
	}

	/**
	 * @return the ontologyDisplayLabel
	 */
	public LuceneSearchField getOntologyDisplayLabelField() {
		return ontologyDisplayLabel;
	}

	/**
	 * @return the recordType
	 */
	public LuceneSearchField getRecordTypeField() {
		return recordType;
	}

	/**
	 * @return the coneptId
	 */
	public LuceneSearchField getConceptIdField() {
		return conceptId;
	}

	/**
	 * @return the conceptIdShort
	 */
	public LuceneSearchField getConceptIdShortField() {
		return conceptIdShort;
	}

	/**
	 * @return the preferredName
	 */
	public LuceneSearchField getPreferredNameField() {
		return preferredName;
	}

	/**
	 * @return the contents
	 */
	public LuceneSearchField getContentsField() {
		return contents;
	}

	/**
	 * @return the literalContents
	 */
	public LuceneSearchField getLiteralContentsField() {
		return literalContents;
	}
}
