package lucene.bean;

import lucene.enumeration.LuceneRecordTypeEnum;

import org.LexGrid.commonTypes.Property;

public class LuceneLexGridProperty extends AbstractLuceneConceptProperty {
	Property property;

	/**
	 * @param ontologyId
	 * @param propetyType
	 * @param property
	 */
	public LuceneLexGridProperty(Integer ontologyId,
			LuceneRecordTypeEnum propetyType, Property property) {
		super(ontologyId, propetyType);
		this.property = property;
	}

	/**
	 * @return the content of the property
	 */
	public String getPropertyContent() {
		return property.getText().getContent();
	}
}
