package org.ncbo.stanford.service.xml;

import org.ncbo.stanford.enumeration.ErrorType;

public interface XMLSerializationService {

	/**
	 * Generate an XML representation of a specific error
	 * 
	 * @param errorType
	 * @return
	 */
	public String getErrorAsXML(ErrorType errorType, String accessedResource);

	/**
	 * Generate an XML representation of a successfully processed request. This
	 * should only be used when no other XML response is expected (i.e.
	 * authentication).
	 * 
	 * @param errorType
	 * @return
	 */
	public String getSuccessAsXML(String sessionId, String accessedResource);
}
