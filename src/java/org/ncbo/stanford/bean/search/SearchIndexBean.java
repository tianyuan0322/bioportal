package org.ncbo.stanford.bean.search;

import org.apache.lucene.document.Field;
import org.ncbo.stanford.enumeration.ConceptTypeEnum;
import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;

/**
 * A bean that contains information required for indexing a single search
 * record. This class overrides the SearchBean class with additional parameters
 * for each field, such as labels and Lucene indexing attributes.
 * 
 * @author Michael Dorf
 * 
 */
public class SearchIndexBean extends SearchBean {
	public static final String ONTOLOGY_VERSION_ID_FIELD_LABEL = "ontologyVersionId";
	public static final String ONTOLOGY_ID_FIELD_LABEL = "ontologyId";
	public static final String ONTOLOGY_DISPLAY_LABEL_FIELD_LABEL = "ontologyDisplayLabel";
	public static final String RECORD_TYPE_FIELD_LABEL = "recordType";
	public static final String OBJECT_TYPE_FIELD_LABEL = "objectType";
	public static final String CONCEPT_ID_FIELD_LABEL = "conceptId";
	public static final String CONCEPT_ID_SHORT_FIELD_LABEL = "conceptIdShort";
	public static final String PREFERRED_NAME_FIELD_LABEL = "preferredName";
	public static final String PREFERRED_NAME_LC_FIELD_LABEL = "preferredNameLC";
	public static final String CONTENTS_FIELD_LABEL = "contents";
	public static final String LITERAL_CONTENTS_FIELD_LABEL = "literalContents";

	private SearchField ontologyVersionId = new SearchField(
			ONTOLOGY_VERSION_ID_FIELD_LABEL, Field.Store.YES,
			Field.Index.NOT_ANALYZED);
	private SearchField ontologyId = new SearchField(ONTOLOGY_ID_FIELD_LABEL,
			Field.Store.YES, Field.Index.NOT_ANALYZED);
	private SearchField ontologyDisplayLabel = new SearchField(
			ONTOLOGY_DISPLAY_LABEL_FIELD_LABEL, Field.Store.YES,
			Field.Index.NOT_ANALYZED);
	private SearchField recordType = new SearchField(RECORD_TYPE_FIELD_LABEL,
			Field.Store.YES, Field.Index.NOT_ANALYZED);
	private SearchField objectType = new SearchField(OBJECT_TYPE_FIELD_LABEL,
			Field.Store.YES, Field.Index.NOT_ANALYZED);
	private SearchField conceptId = new SearchField(CONCEPT_ID_FIELD_LABEL,
			Field.Store.YES, Field.Index.NOT_ANALYZED);
	private SearchField conceptIdShort = new SearchField(
			CONCEPT_ID_SHORT_FIELD_LABEL, Field.Store.YES,
			Field.Index.NOT_ANALYZED);
	private SearchField preferredName = new SearchField(
			PREFERRED_NAME_FIELD_LABEL, Field.Store.YES,
			Field.Index.NOT_ANALYZED);
	private SearchField preferredNameLC = new SearchField(
			PREFERRED_NAME_LC_FIELD_LABEL, Field.Store.NO,
			Field.Index.NOT_ANALYZED);
	private SearchField contents = new SearchField(CONTENTS_FIELD_LABEL,
			Field.Store.YES, Field.Index.ANALYZED);
	private SearchField literalContents = new SearchField(
			LITERAL_CONTENTS_FIELD_LABEL, Field.Store.NO,
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
	public void setRecordType(SearchRecordTypeEnum recordType) {
		super.setRecordType(recordType);
		this.recordType.setContents((recordType == null) ? null : recordType
				.getLabel());
	}

	/**
	 * @param objectType
	 *            the objectType to set
	 */
	public void setObjectType(ConceptTypeEnum objectType) {
		super.setObjectType(objectType);
		this.objectType.setContents((objectType == null) ? null : objectType
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
		this.preferredNameLC.setContents((preferredName == null) ? null
				: preferredName.trim().toLowerCase());
	}

	/**
	 * @param contents
	 *            the contents to set
	 */
	public void setContents(String contents) {
		super.setContents(contents);
		this.contents.setContents(contents);
		this.literalContents.setContents((contents == null) ? null : contents
				.trim().toLowerCase());
	}

	/**
	 * @return the ontologyVersionId
	 */
	public SearchField getOntologyVersionIdField() {
		return ontologyVersionId;
	}

	/**
	 * @return the ontologyId
	 */
	public SearchField getOntologyIdField() {
		return ontologyId;
	}

	/**
	 * @return the ontologyDisplayLabel
	 */
	public SearchField getOntologyDisplayLabelField() {
		return ontologyDisplayLabel;
	}

	/**
	 * @return the recordType
	 */
	public SearchField getRecordTypeField() {
		return recordType;
	}

	/**
	 * @return the objectType
	 */
	public SearchField getObjectTypeField() {
		return objectType;
	}

	/**
	 * @return the coneptId
	 */
	public SearchField getConceptIdField() {
		return conceptId;
	}

	/**
	 * @return the conceptIdShort
	 */
	public SearchField getConceptIdShortField() {
		return conceptIdShort;
	}

	/**
	 * @return the preferredName
	 */
	public SearchField getPreferredNameField() {
		return preferredName;
	}

	/**
	 * @return the preferredNameLC
	 */
	public SearchField getPreferredNameLCField() {
		return preferredNameLC;
	}

	/**
	 * @return the contents
	 */
	public SearchField getContentsField() {
		return contents;
	}

	/**
	 * @return the literalContents
	 */
	public SearchField getLiteralContentsField() {
		return literalContents;
	}
}
