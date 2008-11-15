package lucene.bean;

import lucene.enumeration.LuceneRecordTypeEnum;

public class AbstractLuceneConceptProperty {
	protected Integer ontologyVersionId;
	protected Integer ontologyId;
	protected String ontologyDisplayLabel;
	protected String preferredName;
	protected LuceneRecordTypeEnum propertyType;

	/**
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param preferredName
	 * @param propetyType
	 */
	public AbstractLuceneConceptProperty(Integer ontologyVersionId, Integer ontologyId, String ontologyDisplayLabel, String preferredName,
			LuceneRecordTypeEnum propetyType) {
		super();
		this.ontologyVersionId = ontologyVersionId;
		this.ontologyId = ontologyId;
		this.ontologyDisplayLabel = ontologyDisplayLabel;
		this.preferredName = preferredName;
		this.propertyType = propetyType;
	}

	/**
	 * @return the ontologyVersionId
	 */
	public Integer getOntologyVersionId() {
		return ontologyVersionId;
	}

	/**
	 * @return the ontologyId
	 */
	public Integer getOntologyId() {
		return ontologyId;
	}

	/**
	 * @return the ontologyDisplayLabel
	 */
	public String getOntologyDisplayLabel() {
		return ontologyDisplayLabel;
	}

	/**
	 * @return the preferredName
	 */
	public String getPreferredName() {
		return preferredName;
	}

	/**
	 * @return the slotType
	 */
	public LuceneRecordTypeEnum getPropertyType() {
		return propertyType;
	}

	/**
	 * @param ontologyId the ontologyId to set
	 */
	public void setOntologyId(Integer ontologyId) {
		this.ontologyId = ontologyId;
	}

	/**
	 * @param preferredName the preferredName to set
	 */
	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
	}

	/**
	 * @param propertyType the propertyType to set
	 */
	public void setPropertyType(LuceneRecordTypeEnum propertyType) {
		this.propertyType = propertyType;
	}
}
