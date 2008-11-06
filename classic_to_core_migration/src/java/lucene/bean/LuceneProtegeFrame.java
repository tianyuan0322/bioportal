package lucene.bean;

import lucene.enumeration.LuceneRecordTypeEnum;
import edu.stanford.smi.protege.model.Frame;

public class LuceneProtegeFrame extends AbstractLuceneConceptProperty {
	private Frame frame;

	/**
	 * @param frame
	 * @param ontologyId
	 * @param preferredName
	 * @param frameType
	 */
	public LuceneProtegeFrame(Frame frame, Integer ontologyId,
			String preferredName, LuceneRecordTypeEnum frameType) {
		super(ontologyId, preferredName, frameType);
		this.frame = frame;
	}

	/**
	 * @return the frame
	 */
	public Frame getFrame() {
		return frame;
	}
}
