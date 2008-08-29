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

	}

	/**
	 * Instantiate using an exception object
	 * 
	 * @param e -
	 *            exception object
	 */
	public ErrorStatusBean(Exception e) {
		super();

		this.errorCode = status.getCode();
		setShortMessage(e.getLocalizedMessage());
		setLongMessage(e.getMessage());
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

}
