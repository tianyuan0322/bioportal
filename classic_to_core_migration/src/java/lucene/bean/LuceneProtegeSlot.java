package lucene.bean;

import lucene.enumeration.LuceneRecordTypeEnum;
import edu.stanford.smi.protege.model.Slot;

public class LuceneProtegeSlot extends AbstractLuceneConceptProperty {
	private Slot slot;

	/**
	 * @param slot
	 * @param ontologyId
	 * @param slotType
	 */
	public LuceneProtegeSlot(Slot slot, Integer ontologyId,
			LuceneRecordTypeEnum slotType) {
		super(ontologyId, slotType);
		this.slot = slot;
	}

	/**
	 * @return the slot
	 */
	public Slot getSlot() {
		return slot;
	}
}
