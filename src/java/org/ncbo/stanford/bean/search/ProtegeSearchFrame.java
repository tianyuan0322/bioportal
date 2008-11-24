package org.ncbo.stanford.bean.search;

import org.ncbo.stanford.enumeration.SearchRecordTypeEnum;

import edu.stanford.smi.protege.model.Frame;

public class ProtegeSearchFrame extends SearchBean {
	private Frame frame;

	/**
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param recordType
	 * @param preferredName
	 * @param frame
	 */
	public ProtegeSearchFrame(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, SearchRecordTypeEnum recordType,
			String preferredName, Frame frame) {
		super(ontologyVersionId, ontologyId, ontologyDisplayLabel, recordType,
				preferredName);
		this.frame = frame;
	}

	/**
	 * @return the frame
	 */
	public Frame getFrame() {
		return frame;
	}
}
