package lucene.bean;

import lucene.enumeration.LuceneRecordTypeEnum;

public class AbstractLuceneConceptProperty {
	protected Integer ontologyId;
	protected String preferredName;
	protected LuceneRecordTypeEnum propertyType;

	/**
	 * @param ontologyId
	 * @param propetyType
	 */
	public AbstractLuceneConceptProperty(Integer ontologyId, String preferredName,
			LuceneRecordTypeEnum propetyType) {
		super();
		this.ontologyId = ontologyId;
		this.preferredName = preferredName;
		this.propertyType = propetyType;
	}

	/**
	 * @return the ontologyId
	 */
	public Integer getOntologyId() {
		return ontologyId;
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
