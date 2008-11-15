package lucene.bean;

import lucene.enumeration.LuceneRecordTypeEnum;

import org.LexGrid.commonTypes.Property;

public class LuceneLexGridProperty extends AbstractLuceneConceptProperty {
	Property property;

	/**
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param preferredName
	 * @param propetyType
	 * @param property
	 */
	public LuceneLexGridProperty(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, String preferredName,
			LuceneRecordTypeEnum propetyType, Property property) {
		super(ontologyVersionId, ontologyId, ontologyDisplayLabel,
				preferredName, propetyType);
		this.property = property;
	}

	/**
	 * @return the content of the property
	 */
	public String getPropertyContent() {
		return property.getText().getContent();
	}
}
