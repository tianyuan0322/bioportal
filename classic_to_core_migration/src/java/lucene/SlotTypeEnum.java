package lucene;

public enum SlotTypeEnum {

	SLOT_TYPE_SYNONYM("synonym"), SLOT_TYPE_PREFERRED_NAME("preferred_name");

	private final String label;

	private SlotTypeEnum(String lbl) {
		label = lbl;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
}
