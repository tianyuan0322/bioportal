package org.ncbo.stanford.bean;

import java.util.Calendar;
import java.util.Date;

import org.ncbo.stanford.enumeration.ErrorType;

/**
 * Error container for RESTful error responses
 * 
 * @author Michael Dorf
 * 
 */
public class ErrorBean {

	private ErrorType errorType;
	private String errorCode;
	private String shortMessage;
	private String longMessage;
	private String accessedResource;
	private Date errorDate = Calendar.getInstance().getTime();

	/**
	 * Default constructor
	 */
	public ErrorBean() {
	}

	/**
	 * Instantiate using an enum
	 * 
	 * @param errorType
	 */
	public ErrorBean(ErrorType errorType) {
		this.errorType = errorType;
		this.errorCode = errorType.getErrorCode();
		this.shortMessage = errorType.getDefErrorMessage();
		this.longMessage = this.shortMessage;
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

	/**
	 * @return the shortMessage
	 */
	public String getShortMessage() {
		return shortMessage;
	}

	/**
	 * @param shortMessage
	 *            the shortMessage to set
	 */
	public void setShortMessage(String shortMessage) {
		this.shortMessage = shortMessage;
	}

	/**
	 * @return the longMessage
	 */
	public String getLongMessage() {
		return longMessage;
	}

	/**
	 * @param longMessage
	 *            the longMessage to set
	 */
	public void setLongMessage(String longMessage) {
		this.longMessage = longMessage;
	}

	/**
	 * @return the accessedResource
	 */
	public String getAccessedResource() {
		return accessedResource;
	}

	/**
	 * @param accessedResource
	 *            the accessedResource to set
	 */
	public void setAccessedResource(String accessedResource) {
		this.accessedResource = accessedResource;
	}

	/**
	 * @return the errorDate
	 */
	public Date getErrorDate() {
		return errorDate;
	}

	/**
	 * @param errorDate
	 *            the errorDate to set
	 */
	public void setErrorDate(Date errorDate) {
		this.errorDate = errorDate;
	}
}
