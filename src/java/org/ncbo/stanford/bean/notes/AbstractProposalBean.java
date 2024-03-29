package org.ncbo.stanford.bean.notes;

public abstract class AbstractProposalBean {
	protected String reasonForChange;
	private String contactInfo;

	/**
	 * @return the reasonForChange
	 */
	public String getReasonForChange() {
		return reasonForChange;
	}

	/**
	 * @param reasonForChange
	 *            the reasonForChange to set
	 */
	public void setReasonForChange(String reasonForChange) {
		this.reasonForChange = reasonForChange;
	}

	/**
	 * @return the contactInfo
	 */
	public String getContactInfo() {
		return contactInfo;
	}

	/**
	 * @param contactInfo
	 *            the contactInfo to set
	 */
	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	/**
	 * Output proposal information as HTML for use in email.
	 * 
	 * @return
	 */
	public abstract String toHTML();
}
