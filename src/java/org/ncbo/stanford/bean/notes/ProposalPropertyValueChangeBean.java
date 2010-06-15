package org.ncbo.stanford.bean.notes;


public class ProposalPropertyValueChangeBean extends AbstractProposalBean {
	private String newValue;
	private String oldValue;
	private String propertyId;

	/**
	 * @return the newValue
	 */
	public String getNewValue() {
		return newValue;
	}

	/**
	 * @param newValue
	 *            the newValue to set
	 */
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	/**
	 * @return the oldValue
	 */
	public String getOldValue() {
		return oldValue;
	}

	/**
	 * @param oldValue
	 *            the oldValue to set
	 */
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	/**
	 * @return the propertyId
	 */
	public String getPropertyId() {
		return propertyId;
	}

	/**
	 * @param propertyId
	 *            the propertyId to set
	 */
	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}
	
	public String toHTML() {
		return "<p>Property ID: " + this.propertyId + "</p>" + 
			"<p>New Value: " + this.newValue + "</p>" +
			"<p>Old Value: " + this.oldValue + "</p>" +
			"<p>Reason for Change: " + this.reasonForChange + "</p>";
	}

}
