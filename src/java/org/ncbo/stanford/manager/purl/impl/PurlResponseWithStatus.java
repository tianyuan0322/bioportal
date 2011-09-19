package org.ncbo.stanford.manager.purl.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.Response;
import org.restlet.data.Status;

public class PurlResponseWithStatus {
	private Status status;
	private String responseTxt;
	private static final Log log = LogFactory
			.getLog(PurlResponseWithStatus.class);

	public PurlResponseWithStatus(Response response) {
		try {
			status = response.getStatus();
			if (response.getEntity() != null) {
				responseTxt = response.getEntity().getText();
			} else {
				responseTxt = response.toString();
			}

		} catch (Exception ex) {
			log.error("Error creating PurlResponseWithStatus bean: " + ex);
			String errorMessage = ex.toString() + "Cause=" + ex.getCause();
			responseTxt = "Failed. Error message= " + errorMessage;
			status = new Status(Status.SERVER_ERROR_INTERNAL, errorMessage);

		}
	}

	public PurlResponseWithStatus(String responseTxt, Status status) {
		this.status = status;
		this.responseTxt = responseTxt;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getResponseTxt() {
		return responseTxt;
	}

	public void setResponseTxt(String responseTxt) {
		this.responseTxt = responseTxt;
	}

}
