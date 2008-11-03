package lucene.bean;

import lucene.enumeration.LuceneRecordTypeEnum;
import edu.stanford.smi.protege.model.Slot;

public class LuceneProtegeSlot {
	Slot slot;
	Integer ontologyId;
	LuceneRecordTypeEnum slotType;

	/**
	 * @param slot
	 * @param ontologyId
	 * @param slotType
	 */
	public LuceneProtegeSlot(Slot slot, Integer ontologyId,
			LuceneRecordTypeEnum slotType) {
		super();
		this.slot = slot;
		this.ontologyId = ontologyId;
		this.slotType = slotType;
	}

	/**
	 * @return the slot
	 */
	public Slot getSlot() {
		return slot;
	}

	/**
	 * @return the ontologyId
	 */
	public Integer getOntologyId() {
		return ontologyId;
	}

	/**
	 * @return the slotType
	 */
	public LuceneRecordTypeEnum getSlotType() {
		return slotType;
	}
}
