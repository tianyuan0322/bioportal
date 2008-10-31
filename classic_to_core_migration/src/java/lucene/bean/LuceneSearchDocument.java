package lucene.bean;

import lucene.enumeration.LuceneRecordTypeEnum;

import org.apache.lucene.document.Field;

public class LuceneSearchDocument {
	public static final String ONTOLOGY_ID_FIELD_LABEL = "ontologyId";
	public static final String RECORD_TYPE_FIELD_LABEL = "recordType";
	public static final String FRAME_NAME_FIELD_LABEL = "frameName";
	public static final String CONTENTS_FIELD_LABEL = "contents";
	public static final String LITERAL_CONTENTS_FIELD_LABEL = "literalContents";

	private LuceneSearchField ontologyId = new LuceneSearchField(
			ONTOLOGY_ID_FIELD_LABEL, Field.Store.YES, Field.Index.UN_TOKENIZED);
	private LuceneSearchField recordType = new LuceneSearchField(
			RECORD_TYPE_FIELD_LABEL, Field.Store.YES, Field.Index.UN_TOKENIZED);
	private LuceneSearchField frameName = new LuceneSearchField(
			FRAME_NAME_FIELD_LABEL, Field.Store.YES, Field.Index.UN_TOKENIZED);
	private LuceneSearchField contents = new LuceneSearchField(
			CONTENTS_FIELD_LABEL, Field.Store.YES, Field.Index.TOKENIZED);
	private LuceneSearchField literalContents = new LuceneSearchField(
			LITERAL_CONTENTS_FIELD_LABEL, Field.Store.YES,
			Field.Index.UN_TOKENIZED);

	public LuceneSearchDocument() {
	}

	/**
	 * @param ontologyId
	 * @param recordType
	 * @param frameName
	 * @param contents
	 * @param literalContents
	 */
	public LuceneSearchDocument(String ontologyId,
			LuceneRecordTypeEnum recordType, String frameName, String contents,
			String literalContents) {
		super();
		setOntologyId(ontologyId);
		setRecordType(recordType);
		setFrameName(frameName);
		setContents(contents);
		setLiteralContents(literalContents);
	}

	/**
	 * @param ontologyId
	 *            the ontologyId to set
	 */
	public void setOntologyId(String ontologyId) {
		this.ontologyId.setContents(ontologyId);
	}

	/**
	 * @param recordType
	 *            the recordType to set
	 */
	public void setRecordType(LuceneRecordTypeEnum recordType) {
		this.recordType.setContents(recordType.getLabel());
	}

	/**
	 * @param frameName
	 *            the frameName to set
	 */
	public void setFrameName(String frameName) {
		this.frameName.setContents(frameName);
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
		this.literalContents.setContents(literalContents);
	}

	/**
	 * @return the ontologyId
	 */
	public LuceneSearchField getOntologyId() {
		return ontologyId;
	}

	/**
	 * @return the recordType
	 */
	public LuceneSearchField getRecordType() {
		return recordType;
	}

	/**
	 * @return the frameName
	 */
	public LuceneSearchField getFrameName() {
		return frameName;
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