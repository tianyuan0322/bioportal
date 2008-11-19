package lucene.bean;

import lucene.enumeration.LuceneRecordTypeEnum;

import org.LexGrid.commonTypes.Property;

public class LuceneLexGridProperty extends LuceneSearchBean {
	Property property;

	/**
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param recordType
	 * @param preferredName
	 * @param property
	 */
	public LuceneLexGridProperty(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, LuceneRecordTypeEnum recordType,
			String preferredName, Property property) {
		super(ontologyVersionId, ontologyId, ontologyDisplayLabel, recordType,
				preferredName);
		this.property = property;
	}

	/**
	 * @return the content of the property
	 */
	public String getPropertyContent() {
		return property.getText().getContent();
	}
}
