package org.ncbo.stanford.bean.response;

import java.util.Calendar;
import java.util.Date;

/**
 * Abstract class to define a generic RESTful Response
 * 
 * @author Michael Dorf
 * 
 */
public abstract class AbstractResponseBean {

	private String accessedResource;
	private Date accessDate = Calendar.getInstance().getTime();
	private String shortMessage;
	private String longMessage;

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
	 * @return the accessDate
	 */
	public Date getAccessDate() {
		return accessDate;
	}

	/**
	 * @param accessDate
	 *            the accessDate to set
	 */
	public void setAccessDate(Date accessDate) {
		this.accessDate = accessDate;
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
}
