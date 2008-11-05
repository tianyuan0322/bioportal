package lucene.bean;

import lucene.enumeration.LuceneRecordTypeEnum;

public class AbstractLuceneConceptProperty {
	protected Integer ontologyId;
	protected LuceneRecordTypeEnum propertyType;

	/**
	 * @param ontologyId
	 * @param propetyType
	 */
	public AbstractLuceneConceptProperty(Integer ontologyId,
			LuceneRecordTypeEnum propetyType) {
		super();
		this.ontologyId = ontologyId;
		this.propertyType = propetyType;
	}

	/**
	 * @return the ontologyId
	 */
	public Integer getOntologyId() {
		return ontologyId;
	}

	/**
	 * @return the slotType
	 */
	public LuceneRecordTypeEnum getPropertyType() {
		return propertyType;
	}
}
