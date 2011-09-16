package org.ncbo.stanford.bean.search;

import org.LexGrid.commonTypes.Property;
import org.ncbo.stanford.enumeration.ConceptTypeEnum;
import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;

/**
 * Class to contain LexGrid specific attributes needed for indexing
 * 
 * @author Michael Dorf
 * 
 */
public class LexGridSearchProperty extends SearchBean {
	Property property;
	// setting all LexGrid concept types to "Class".
	// See GForge [#2740] Ability to search only classes, instances, or
	// properties
	private static final ConceptTypeEnum LEXGRID_OBJECT_TYPE = ConceptTypeEnum.CONCEPT_TYPE_CLASS;

	/**
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param recordType
	 * @param preferredName
	 * @param isObsolete
	 * @param property
	 */
	public LexGridSearchProperty(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, SearchRecordTypeEnum recordType,
			String preferredName, Byte isObsolete, Property property) {
		super(ontologyVersionId, ontologyId, ontologyDisplayLabel, recordType,
				LEXGRID_OBJECT_TYPE, preferredName, isObsolete);
		this.property = property;
	}

	/**
	 * @return the content of the property
	 */
	public String getPropertyContent() {
		return property.getValue().getContent();
	}
}
