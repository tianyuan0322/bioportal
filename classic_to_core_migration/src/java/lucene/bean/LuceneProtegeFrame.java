package lucene.bean;

import lucene.enumeration.LuceneRecordTypeEnum;
import edu.stanford.smi.protege.model.Frame;

public class LuceneProtegeFrame extends LuceneSearchBean {
	private Frame frame;

	/**
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param recordType
	 * @param preferredName
	 * @param frame
	 */
	public LuceneProtegeFrame(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, LuceneRecordTypeEnum recordType,
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
