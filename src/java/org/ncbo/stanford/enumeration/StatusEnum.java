package org.ncbo.stanford.enumeration;

import org.ncbo.stanford.util.MessageUtils;

/**
 * An Enum to store site-wide status codes (corresponds to ncbo_l_status table
 * values)
 * 
 * @author Michael Dorf
 * 
 */
public enum StatusEnum {

	STATUS_WAITING(new Integer(MessageUtils.getMessage("ncbo.status.waiting"))), STATUS_READY(
			new Integer(MessageUtils.getMessage("ncbo.status.ready"))), STATUS_PARSING(
			new Integer(MessageUtils.getMessage("ncbo.status.parsing"))), STATUS_ERROR(
			new Integer(MessageUtils.getMessage("ncbo.status.error"))), STATUS_NOTAPPLICABLE(
			new Integer(MessageUtils.getMessage("ncbo.status.notapplicable")));

	private final Integer status;

	private StatusEnum(Integer sts) {
		status = sts;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}
}
