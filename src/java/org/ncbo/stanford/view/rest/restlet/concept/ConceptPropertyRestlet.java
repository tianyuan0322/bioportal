/**
 * 
 */
package org.ncbo.stanford.view.rest.restlet.concept;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.enumeration.PropertyTypeEnum;
import org.ncbo.stanford.service.cuiconcept.ConceptPropertyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

/**
 * @author s.reddy
 * 
 */

public class ConceptPropertyRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory
			.getLog(ConceptPropertyRestlet.class);

	private ConceptPropertyService conceptPropertyService;

	public void getRequest(Request request, Response response) {
		findCUIOntologies(request, response);
	}

	private void findCUIOntologies(Request request, Response response) {

		String propertyType = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.propertyType"));

		List<ClassBean> resultClassbeans = null;
		try {
			if (propertyType.equals(PropertyTypeEnum.CUI.getType())) {
				HttpServletRequest httpRequest = RequestUtils
						.getHttpServletRequest(request);

				String val = httpRequest
						.getParameter(RequestParamConstants.PARAM_PROPERTY_VALUE);

				List<Integer> ontologyIds = getOntologyIds(httpRequest);

				resultClassbeans = conceptPropertyService
						.findConceptsByProperty(PropertyTypeEnum.CUI, val,
								ontologyIds);
			}
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);

		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					resultClassbeans);
		}
	}

	public ConceptPropertyService getConceptPropertyService() {
		return conceptPropertyService;
	}

	public void setConceptPropertyService(
			ConceptPropertyService conceptPropertyService) {
		this.conceptPropertyService = conceptPropertyService;
	}

}
