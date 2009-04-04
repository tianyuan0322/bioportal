package org.ncbo.stanford.bean.response;

import org.ncbo.stanford.enumeration.ErrorTypeEnum;

/**
 * Error container for RESTful error responses
 * 
 * @author Michael Dorf
 * 
 */
public class ErrorBean extends AbstractResponseBean {

	private ErrorTypeEnum errorType;
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
	 * @param e -
	 *            exception object
	 */
	public ErrorBean(Exception e) {
		super();
		this.errorType = ErrorTypeEnum.RUNTIME_ERROR;
		this.errorCode = this.errorType.getErrorCode();
		setShortMessage(e.getLocalizedMessage());
		setLongMessage(e.getMessage());
	}

	/**
	 * Instantiate using an enum
	 * 
	 * @param errorType
	 */
	public ErrorBean(ErrorTypeEnum errorType) {
		super();
		this.errorType = errorType;
		this.errorCode = errorType.getErrorCode();
		setShortMessage(errorType.getDefErrorMessage());
	}

	@Override
	public boolean isResponseError() {
		return true;
	}

	@Override
	public boolean isResponseSuccess() {
		return false;
	}

	/**
	 * @return the errorType
	 */
	public ErrorTypeEnum getErrorType() {
		return errorType;
	}

	/**
	 * @param errorType
	 *            the errorType to set
	 */
	public void setErrorType(ErrorTypeEnum errorType) {
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
