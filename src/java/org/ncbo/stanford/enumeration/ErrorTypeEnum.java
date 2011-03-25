package org.ncbo.stanford.enumeration;

/**
 * An Enum to store site-wide errors with codes and default messages
 * 
 * @author Michael Dorf
 * 
 */
public enum ErrorTypeEnum {

	AUTHENTICATION_REQUIRED("AUTHENTICATION_REQUIRED", "Access to this resource requires authentication."), 
	INVALID_SESSION("INVALID_SESSION", "Your session information is invalid. Please check your API key and re-run your request."), 
	APIKEY_REQUIRED("APIKEY_REQUIRED", "A valid API key is required to call REST services. Please see the developer documentation for more detail."),
	INVALID_APIKEY("INVALID_APIKEY", "The API key you supplied is invalid."),
	INVALID_CREDENTIALS("INVALID_CREDENTIALS", "The credentials you supplied are not valid."), 
	ACCESS_DENIED("ACCESS_DENIED", "You don't have sufficient privileges to access this resource."), 
	RUNTIME_ERROR("RUNTIME_ERROR", "A runtime error has occurred."), 
	INVALID_FILE("INVALID_FILE", "The uploaded file is invalid."), 
	INVALID_OPENID("INVALID_OPENID", "OpenId not exist in Bioportal database.");

	private final String errorCode;
	private String errorMessage;

	private ErrorTypeEnum(String errCode, String errMessage) {
		errorCode = errCode;
		errorMessage = errMessage;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String msg) {
		errorMessage = msg;
	}
}
