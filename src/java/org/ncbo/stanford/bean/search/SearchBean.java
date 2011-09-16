package org.ncbo.stanford.bean.search;

import org.ncbo.stanford.enumeration.ConceptTypeEnum;
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
	private ConceptTypeEnum objectType;
	private String conceptId;
	private String conceptIdShort;
	private String preferredName;
	private String contents;
	private String definition;
	private Byte isObsolete;

	public SearchBean() {
	}

	/**
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 */
	public SearchBean(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel) {
		populateInstance(ontologyVersionId, ontologyId, ontologyDisplayLabel,
				null, null, null, null, null, null, null, null);
	}

	/**
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param recordType
	 * @param objectType
	 * @param conceptId
	 * @param conceptIdShort
	 * @param preferredName
	 * @param contents
	 */
	public SearchBean(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, SearchRecordTypeEnum recordType,
			ConceptTypeEnum objectType, String conceptId,
			String conceptIdShort, String preferredName, String contents,
			String definition, Byte isObsolete) {
		populateInstance(ontologyVersionId, ontologyId, ontologyDisplayLabel,
				recordType, objectType, conceptId, conceptIdShort,
				preferredName, contents, definition, isObsolete);
	}

	/**
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param recordType
	 * @param objectType
	 * @param preferredName
	 * @param isObsolete
	 */
	public SearchBean(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, SearchRecordTypeEnum recordType,
			ConceptTypeEnum objectType, String preferredName, Byte isObsolete) {
		populateInstance(ontologyVersionId, ontologyId, ontologyDisplayLabel,
				recordType, objectType, null, null, preferredName, null, null,
				isObsolete);
	}

	/**
	 * Populates this instance with data. This is the "official" function to use
	 * to populate the bean. Do not create any custom population methods!!!
	 * 
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param recordType
	 * @param objectType
	 * @param conceptId
	 * @param conceptIdShort
	 * @param preferredName
	 * @param contents
	 * @param definition
	 * @param isObsolete
	 */
	public void populateInstance(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, SearchRecordTypeEnum recordType,
			ConceptTypeEnum objectType, String conceptId,
			String conceptIdShort, String preferredName, String contents,
			String definition, Byte isObsolete) {
		setOntologyVersionId(ontologyVersionId);
		setOntologyId(ontologyId);
		setOntologyDisplayLabel(ontologyDisplayLabel);
		setRecordType(recordType);
		setObjectType(objectType);
		setConceptId(conceptId);
		setConceptIdShort(conceptIdShort);
		setPreferredName(preferredName);
		setContents(contents);
		setDefinition(definition);
		setIsObsolete(isObsolete);
	}

	public String toString() {
		return "VersionId: " + ontologyVersionId + " | OntologyId: "
				+ ontologyId + " | ConceptId: " + conceptId + " | Contents: "
				+ contents + " | RecordType: " + recordType.getLabel() + "\n"
				+ " | ObjectType: "
				+ ((objectType != null) ? objectType.getLabel() : "")
				+ " PreferedName: " + preferredName + " | ConceptIdShort: "
				+ conceptIdShort + " | OntologyDisplayLabel: "
				+ ontologyDisplayLabel + " | Definition: " + definition
				+ " | Obsolete?: " + isObsolete + "\n";
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
	 * @param objectType
	 *            the objectType to set
	 */
	public void setObjectType(ConceptTypeEnum objectType) {
		this.objectType = objectType;
	}

	/**
	 * @return the objectType
	 */
	public ConceptTypeEnum getObjectType() {
		return objectType;
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
	 * @return the definition
	 */
	public String getDefinition() {
		return definition;
	}

	/**
	 * @param definition
	 *            the definition to set
	 */
	public void setDefinition(String definition) {
		this.definition = definition;
	}

	/**
	 * @return the isObsolete
	 */
	public Boolean isObsolete() {
		return (isObsolete != null && isObsolete.byteValue() > 0);
	}

	/**
	 * @return the isObsolete
	 */
	public Byte getIsObsolete() {
		return isObsolete;
	}

	/**
	 * @param isObsolete
	 *            the isObsolete to set
	 */
	public void setIsObsolete(Byte isObsolete) {
		this.isObsolete = isObsolete;
	}
}
