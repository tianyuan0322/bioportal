package org.ncbo.stanford.bean.response;

import org.ncbo.stanford.enumeration.ErrorType;

/**
 * Error container for RESTful error responses
 * 
 * @author Michael Dorf
 * 
 */
public class ErrorBean extends AbstractResponseBean {

	private ErrorType errorType;
	private String errorCode;

	/**
	 * Default constructor
	 */
	public ErrorBean() {
		super();
	}

	/**
	 * Instantiate using an exception object
	 * 
	 * @param e - exception object
	 */
	public ErrorBean(Exception e) {
		super();
		this.errorType = ErrorType.RUNTIME_ERROR;
		this.errorCode = this.errorType.getErrorCode();
		setShortMessage(e.getLocalizedMessage());
		setLongMessage(e.getMessage());
	}
	
	/**
	 * Instantiate using an enum
	 * 
	 * @param errorType
	 */
	public ErrorBean(ErrorType errorType) {
		super();
		this.errorType = errorType;
		this.errorCode = errorType.getErrorCode();
		setShortMessage(errorType.getDefErrorMessage());
	}

	/**
	 * @return the errorType
	 */
	public ErrorType getErrorType() {
		return errorType;
	}

	/**
	 * @param errorType
	 *            the errorType to set
	 */
	public void setErrorType(ErrorType errorType) {
		this.errorType = errorType;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
