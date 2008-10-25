package lucene;

import edu.stanford.smi.protege.model.Slot;

public class LuceneSlot {
	Slot slot;
	Integer ontologyId;
	SlotTypeEnum slotType;

	/**
	 * @param slot
	 * @param ontologyId
	 * @param slotType
	 */
	public LuceneSlot(Slot slot, Integer ontologyId, SlotTypeEnum slotType) {
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
	public SlotTypeEnum getSlotType() {
		return slotType;
	}
}
