package org.ncbo.stanford.util.security.ui;

import javax.servlet.http.HttpServletRequest;

import org.acegisecurity.ui.WebAuthenticationDetails;

public class ApplicationAuthenticationDetails extends WebAuthenticationDetails {

	private static final long serialVersionUID = 1L;
	
	private String applicationId;

	/**
	 * @param request
	 * @param applicationId
	 */
	public ApplicationAuthenticationDetails(HttpServletRequest request, String applicationId) {
		super(request);
		this.applicationId = applicationId;
	}
	
    protected ApplicationAuthenticationDetails() {
        throw new IllegalArgumentException("Cannot use default constructor");
    }

    /**
	 * @return the applicationId
	 */
	public String getApplicationId() {
		return applicationId;
	}
	
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString() + "; ");
        sb.append("ApplicationId: " + this.getApplicationId());

        return sb.toString();
    }	
}
