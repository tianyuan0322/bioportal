package org.ncbo.stanford.service.xml;

import org.ncbo.stanford.enumeration.ErrorTypeEnum;
import org.restlet.data.Status;

/**
 * An interface that allows predefined structure of XML responses
 * 
 * @author Michael Dorf
 * 
 */
public interface XMLSerializationService {

	/**
	 * Generate an XML representation of a specific error
	 * 
	 * @param errorType
	 * @return
	 */
	public String getErrorAsXML(ErrorTypeEnum errorType, String accessedResource);

	/**
	 * Generate an XML representation of a successfully processed request. This
	 * should only be used when no other XML response is expected (i.e.
	 * authentication).
	 * 
	 * @param sessionId
	 * @param accessedResource
	 * @return
	 */
	public String getSuccessAsXML(String sessionId, String accessedResource);

	/**
	 * Generate an XML representation of a successfully processed request. This
	 * should only be used when no other XML response is expected (i.e.
	 * authentication).
	 * 
	 * @param sessionId
	 * @param accessedResource
	 * @param data
	 * @return
	 */
	public String getSuccessAsXML(String sessionId, String accessedResource,
			Object data);
	
	
	/**
	 * Generate Error XML from status object.
	 * Note that ErrorStatusBean is used instead of ErrorBean. 
	 * Status object is passed around instead of in-house ErrorTypeEnum object
	 * 
	 * cyoun
	 * 
	 * @param status
	 * @param accessedResource
	 * @return
	 */
	public String getErrorAsXML (Status status, String accessedResource);
}
