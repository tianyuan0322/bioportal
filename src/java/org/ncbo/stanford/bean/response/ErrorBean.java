package org.ncbo.stanford.bean.response;

import org.restlet.data.Status;

/**
 * Error container for RESTful error responses
 * 
 * @author Michael Dorf
 * 
 */
public class ErrorBean extends AbstractResponseBean {

	private int errorCode;

	/**
	 * Instantiate using a Status object
	 * 
	 * @param status
	 */
	public ErrorBean(Status status) {
		super(status);
		setErrorCode(status.getCode());
		setShortMessage(status.getName());
		setLongMessage(status.getDescription());
	}

	@Override
	public boolean isResponseError() {
		return true;
	}

	@Override
	public boolean isResponseSuccess() {
		return false;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String toString() {
		return super.toString() + " {errorCode: " + this.getErrorCode() + "}";
	}
}
