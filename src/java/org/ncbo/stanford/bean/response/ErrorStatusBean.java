package org.ncbo.stanford.bean.response;

import org.restlet.data.Status;

/**
 * Error container for RESTful error responses
 * 
 * @author cyoun
 * 
 */
public class ErrorStatusBean extends AbstractResponseBean {

	private Status status;
	private int errorCode;

	/**
	 * Default constructor
	 */
	public ErrorStatusBean() {
		super();
	}

	/**
	 * Instantiate using a Status object
	 * 
	 * @param status
	 */
	public ErrorStatusBean(Status status) {
		super();

		this.errorCode = status.getCode();
		setShortMessage(status.getName());
		setLongMessage(status.getDescription());
		setStatus(status);
	}

	@Override
	public boolean isResponseError() {
		return true;
	}

	@Override
	public boolean isResponseSuccess() {
		return false;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String toString() {		
		return "{accessedResource: " + this.getAccessedResource()
			+ ", accessedDate: " + this.getAccessDate()
			+ ", shortMessage: " + this.getShortMessage() 
			+ ", longMessage: " + this.getLongMessage() 
			+ ", errorCode: " + this.getErrorCode() 
			+ ", status: " + this.getStatus() 
			+ "}";
	}
}
