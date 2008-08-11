package org.ncbo.stanford.bean.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Success response container for RESTful responses
 * 
 * @author Michael Dorf
 * 
 */
public class SuccessBean extends AbstractResponseBean {

	private String sessionId;
	private List<Object> data = new ArrayList<Object>();

	/**
	 * Default constructor
	 */
	public SuccessBean() {
		super();
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId
	 *            the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	/**
	 * @return the data
	 */
	public List<Object> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(List<Object> data) {
		this.data = data;
	}
}
