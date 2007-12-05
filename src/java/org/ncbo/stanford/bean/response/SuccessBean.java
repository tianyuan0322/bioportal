package org.ncbo.stanford.bean.response;

/**
 * Success response container for RESTful responses
 * 
 * @author Michael Dorf
 * 
 */
public class SuccessBean extends AbstractResponseBean {

	private String sessionId;

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
}
