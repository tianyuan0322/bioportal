package lucene.bean;

import lucene.enumeration.LuceneRecordTypeEnum;
import edu.stanford.smi.protege.model.Frame;

public class LuceneProtegeFrame extends AbstractLuceneConceptProperty {
	private Frame frame;

	/**
	 * @param ontologyVersionId
	 * @param ontologyId
	 * @param ontologyDisplayLabel
	 * @param preferredName
	 * @param frameType
	 * @param frame
	 */
	public LuceneProtegeFrame(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel, String preferredName,
			LuceneRecordTypeEnum frameType, Frame frame) {
		super(ontologyVersionId, ontologyId, ontologyDisplayLabel,
				preferredName, frameType);
		this.frame = frame;
	}

	/**
	 * @return the frame
	 */
	public Frame getFrame() {
		return frame;
	}
}
