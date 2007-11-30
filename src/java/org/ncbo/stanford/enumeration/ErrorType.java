package org.ncbo.stanford.enumeration;

/**
 * Enum to store site-wide errors with codes and default messages
 * 
 * @author Michael Dorf
 *
 */
public enum ErrorType {
	
	AUTHENTICATION_REQUIRED(
			"AUTHENTICATION_REQUIRED", 
			"Access to this resource requires authentication"),
	ACCESS_DENIED(
			"ACCESS_DENIED", 
			"You do not possess sufficient privileges to access this resource");
	
    private final String errorCode;
    private final String defErrorMessage;
	
	private ErrorType(String errCode, String defErrMessage) {
		errorCode = errCode;
		defErrorMessage = defErrMessage;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @return the defErrorMessage
	 */
	public String getDefErrorMessage() {
		return defErrorMessage;
	}
}
