package org.ncbo.stanford.enumeration;

/**
 * An Enum to store site-wide errors with codes and default messages
 * 
 * @author Michael Dorf
 * 
 */
public enum ErrorTypeEnum {

	AUTHENTICATION_REQUIRED("AUTHENTICATION_REQUIRED",
			"Access to this resource requires authentication"), INVALID_CREDENTIALS(
			"INVALID_CREDENTIALS", "The credentials you supplied are not valid"), ACCESS_DENIED(
			"ACCESS_DENIED",
			"You do not possess sufficient privileges to access this resource"), RUNTIME_ERROR(
			"RUNTIME_ERROR", "A runtime error has occurred"), INVALID_FILE(
			"INVALID_FILE", "The uploaded file is invalid");

	private final String errorCode;
	private final String defErrorMessage;

	private ErrorTypeEnum(String errCode, String defErrMessage) {
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
