package org.ncbo.stanford.bean.response;

/**
 * Success response container for RESTful responses
 * 
 * @author Michael Dorf
 * 
 */
public class SuccessBean extends AbstractResponseBean {

	private String sessionId;
	private Object data;

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
	public Object getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}
}
