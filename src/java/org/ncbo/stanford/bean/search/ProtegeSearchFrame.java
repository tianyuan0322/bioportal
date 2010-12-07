package org.ncbo.stanford.bean.search;

import org.ncbo.stanford.enumeration.ConceptTypeEnum;
import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;

import edu.stanford.smi.protege.model.Frame;

/**
 * Class to contain Protege specific attributes needed for indexing
 * 
 * @author Michael Dorf
 * 
 */
public class ProtegeSearchFrame extends SearchBean {
	private Frame frame;

	/**
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param recordType
	 * @param objectType
	 * @param preferredName
	 * @param frame
	 */
	public ProtegeSearchFrame(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, SearchRecordTypeEnum recordType,
			ConceptTypeEnum objectType, String preferredName, Frame frame) {
		super(ontologyVersionId, ontologyId, ontologyDisplayLabel, recordType,
				objectType, preferredName);
		this.frame = frame;
	}

	/**
	 * @return the frame
	 */
	public Frame getFrame() {
		return frame;
	}
}
