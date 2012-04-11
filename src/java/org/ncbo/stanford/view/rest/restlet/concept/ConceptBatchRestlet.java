package org.ncbo.stanford.view.rest.restlet.concept;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.concept.ConceptOntologyPairBean;
import org.ncbo.stanford.exception.ConceptNotFoundException;
import org.ncbo.stanford.exception.OntologyNotFoundException;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class ConceptBatchRestlet extends AbstractBaseRestlet {

	private static final Log log = LogFactory.getLog(ConceptBatchRestlet.class);
	private ConceptService conceptService;
	private OntologyService ontologyService;

	/**
	 * Handle GET calls here
	 */
	@Override
	public void getRequest(Request request, Response response) {
		findConcepts(request, response);
	}

	/**
	 * Return to the response an individual ontology
	 * 
	 * @param request
	 * @param response
	 */
	private void findConcepts(Request request, Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		String conceptOntologyPairs = (String) httpRequest
				.getParameter("concepts");
		String maxNumChildren = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_MAXNUMCHILDREN);
		String light = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_LIGHT);
		String noRelations = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_NORELATIONS);
		String withProperties = (String) httpRequest
				.getParameter(RequestParamConstants.PARAM_CLASS_PROPERTIES);

		Integer maxNumChildrenInt = RequestUtils
				.parseIntegerParam(maxNumChildren);
		if (maxNumChildrenInt == null) {
			maxNumChildrenInt = Integer.MAX_VALUE;
		}

		Boolean lightBool = RequestUtils.parseBooleanParam(light);
		Boolean noRelationsBool = RequestUtils.parseBooleanParam(noRelations);
		Boolean withPropertiesBool = RequestUtils
				.parseBooleanParam(withProperties);

		List<ConceptOntologyPairBean> concepts = new ArrayList<ConceptOntologyPairBean>();

		try {
			List<ConceptOntologyPairBean> conceptOntologyPairList = RequestUtils
					.parseConceptOntologyPairs(conceptOntologyPairs);

			for (ConceptOntologyPairBean conceptOntologyPair : conceptOntologyPairList) {
				OntologyBean ont = ontologyService
						.findLatestActiveOntologyOrViewVersion(conceptOntologyPair
								.getOntologyId());

				if (ont == null) {
					throw new OntologyNotFoundException();
				}

				ClassBean concept = conceptService.findConcept(ont.getId(),
						conceptOntologyPair.getConceptId(), maxNumChildrenInt,
						lightBool, noRelationsBool, withPropertiesBool);

				if (concept == null) {
					throw new ConceptNotFoundException(
							MessageUtils
									.getMessage("msg.error.conceptNotFound"));
				}
				
				conceptOntologyPair.setConcept(concept);
				conceptOntologyPair.setOntologyName(ont.getDisplayLabel());
				concepts.add(conceptOntologyPair);
			}
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					concepts);
		}
	}

	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}
}
