package org.ncbo.stanford.enumeration;

/**
 * An Enum to store site-wide status codes (corresponds to ncbo_l_status table
 * values)
 * 
 * @author Michael Dorf
 * 
 */
public enum StatusEnum {
	STATUS_WAITING(1), STATUS_READY(2), STATUS_PARSING(3), STATUS_ERROR(4);
	
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
