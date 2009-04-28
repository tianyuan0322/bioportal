package org.ncbo.stanford.bean.search;

import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;

/**
 * A base search bean that define a single search result entity
 * 
 * @author Michael Dorf
 * 
 */
public class SearchBean {

	private Integer ontologyVersionId;
	private Integer ontologyId;
	private String ontologyDisplayLabel;
	private SearchRecordTypeEnum recordType;
	private String conceptId;
	private String conceptIdShort;
	private String preferredName;
	private String contents;
	private String literalContents;

	public SearchBean() {
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
	public SearchBean(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel) {
		populateInstance(ontologyVersionId, ontologyId, ontologyDisplayLabel,
				null, null, null, null, null, null);
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
	public SearchBean(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, SearchRecordTypeEnum recordType,
			String conceptId, String conceptIdShort, String preferredName,
			String contents, String literalContents) {
		populateInstance(ontologyVersionId, ontologyId, ontologyDisplayLabel,
				recordType, conceptId, conceptIdShort, preferredName, contents,
				literalContents);
	}

	/**
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param recordType
	 * @param preferredName
	 */
	public SearchBean(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, SearchRecordTypeEnum recordType,
			String preferredName) {
		populateInstance(ontologyVersionId, ontologyId, ontologyDisplayLabel,
				recordType, null, null, preferredName, null, null);
	}

	/**
	 * Populates this instance with data. This is the "official" function to use
	 * to populate the bean. Do not create any custom population methods!!!
	 * 
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
	public void populateInstance(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, SearchRecordTypeEnum recordType,
			String conceptId, String conceptIdShort, String preferredName,
			String contents, String literalContents) {
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

	public String toString() {
		return "VersionId: " + ontologyVersionId + " | OntologyId: "
				+ ontologyId + " | ConceptId: " + conceptId + " | Contents: "
				+ contents + " | LiteralContents: " + literalContents
				+ " | RecordType: " + recordType.getLabel() + "\n"
				+ " PreferedName: " + preferredName + " | ConceptIdShort: "
				+ conceptIdShort + " | OntologyDisplayLabel: "
				+ ontologyDisplayLabel + "\n";
	}

	/**
	 * @return the ontologyVersionId
	 */
	public Integer getOntologyVersionId() {
		return ontologyVersionId;
	}

	/**
	 * @param ontologyVersionId
	 *            the ontologyVersionId to set
	 */
	public void setOntologyVersionId(Integer ontologyVersionId) {
		this.ontologyVersionId = ontologyVersionId;
	}

	/**
	 * @return the ontologyId
	 */
	public Integer getOntologyId() {
		return ontologyId;
	}

	/**
	 * @param ontologyId
	 *            the ontologyId to set
	 */
	public void setOntologyId(Integer ontologyId) {
		this.ontologyId = ontologyId;
	}

	/**
	 * @return the ontologyDisplayLabel
	 */
	public String getOntologyDisplayLabel() {
		return ontologyDisplayLabel;
	}

	/**
	 * @param ontologyDisplayLabel
	 *            the ontologyDisplayLabel to set
	 */
	public void setOntologyDisplayLabel(String ontologyDisplayLabel) {
		this.ontologyDisplayLabel = ontologyDisplayLabel;
	}

	/**
	 * @return the recordType
	 */
	public SearchRecordTypeEnum getRecordType() {
		return recordType;
	}

	/**
	 * @param recordType
	 *            the recordType to set
	 */
	public void setRecordType(SearchRecordTypeEnum recordType) {
		this.recordType = recordType;
	}

	/**
	 * @return the conceptId
	 */
	public String getConceptId() {
		return conceptId;
	}

	/**
	 * @param conceptId
	 *            the conceptId to set
	 */
	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}

	/**
	 * @return the conceptIdShort
	 */
	public String getConceptIdShort() {
		return conceptIdShort;
	}

	/**
	 * @param conceptIdShort
	 *            the conceptIdShort to set
	 */
	public void setConceptIdShort(String conceptIdShort) {
		this.conceptIdShort = conceptIdShort;
	}

	/**
	 * @return the preferredName
	 */
	public String getPreferredName() {
		return preferredName;
	}

	/**
	 * @param preferredName
	 *            the preferredName to set
	 */
	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
	}

	/**
	 * @return the contents
	 */
	public String getContents() {
		return contents;
	}

	/**
	 * @param contents
	 *            the contents to set
	 */
	public void setContents(String contents) {
		this.contents = contents;
	}

	/**
	 * @return the literalContents
	 */
	public String getLiteralContents() {
		return literalContents;
	}

	/**
	 * @param literalContents
	 *            the literalContents to set
	 */
	public void setLiteralContents(String literalContents) {
		this.literalContents = literalContents;
	}
}
