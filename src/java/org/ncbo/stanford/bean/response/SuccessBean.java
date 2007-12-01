package org.ncbo.stanford.bean.response;

public class SuccessBean extends AbstractResponseBean {

	private String sessionId;

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
