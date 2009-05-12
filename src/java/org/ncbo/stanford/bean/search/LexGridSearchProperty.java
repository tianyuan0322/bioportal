package org.ncbo.stanford.bean.search;


import org.LexGrid.commonTypes.Property;
import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;

/**
 * Class to contain LexGrid specific attributes needed for indexing
 * 
 * @author Michael Dorf
 * 
 */
public class LexGridSearchProperty extends SearchBean {
	Property property;

	/**
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param recordType
	 * @param preferredName
	 * @param property
	 */
	public LexGridSearchProperty(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, SearchRecordTypeEnum recordType,
			String preferredName, Property property) {
		super(ontologyVersionId, ontologyId, ontologyDisplayLabel, recordType,
				preferredName);
		this.property = property;
	}

	/**
	 * @return the content of the property
	 */
	public String getPropertyContent() {
		return property.getValue().getContent();
	}
}
