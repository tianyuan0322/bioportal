package org.ncbo.stanford.view.rest.restlet.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.sparql.SPARQLFilterGenerator;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.restlet.Request;
import org.restlet.Response;

import com.ibm.icu.util.Calendar;

public class AbstractMappingRestlet extends AbstractBaseRestlet {

	/**
	 * Gets a common set of parameters from the request.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	protected SPARQLFilterGenerator getMappingParameters(Request request,
			Response response) {
		HttpServletRequest httpRequest = RequestUtils
				.getHttpServletRequest(request);

		// Mapping submitters
		List<Integer> submittedBy = RequestUtils
				.parseIntegerListParam(httpRequest
						.getParameter(RequestParamConstants.PARAM_SUBMITTERS));

		// Mapping types
		String mappingType = httpRequest
				.getParameter(RequestParamConstants.PARAM_MAPPING_TYPE);

		// Date parameters
		String startDateStr = httpRequest
				.getParameter(RequestParamConstants.PARAM_START_DATE);
		String endDateStr = httpRequest
				.getParameter(RequestParamConstants.PARAM_END_DATE);

		Date startDate = null;
		Date endDate = null;
		if (startDateStr != null) {
			startDate = DatatypeConverter.parseDateTime(startDateStr).getTime();
		}

		if (endDateStr != null) {
			endDate = DatatypeConverter.parseDateTime(endDateStr).getTime();
		} else if (startDateStr != null) {
			endDate = Calendar.getInstance().getTime();
		}

		// Relationship types
		List<String> relationshipTypesStr = RequestUtils
				.parseStringListParam(httpRequest
						.getParameter(RequestParamConstants.PARAM_RELATIONSHIPS));

		List<URI> relationshipTypes = new ArrayList<URI>();
		if (relationshipTypesStr != null) {
			for (String relationshipType : relationshipTypesStr) {
				relationshipTypes.add(new URIImpl(relationshipType));
			}
		}

		// Mapping sources
		List<String> mappingSourcesStr = RequestUtils
				.parseStringListParam(httpRequest
						.getParameter(RequestParamConstants.PARAM_MAPPING_SOURCES));

		List<MappingSourceEnum> mappingSources = new ArrayList<MappingSourceEnum>();
		if (mappingSourcesStr != null) {
			for (String mappingSource : mappingSourcesStr) {
				mappingSources.add(MappingSourceEnum.valueOf(mappingSource));
			}
		}

		// Create parameters bean
		SPARQLFilterGenerator parameters = new SPARQLFilterGenerator();
		if (submittedBy != null && !submittedBy.isEmpty())
			parameters.setSubmittedBy(submittedBy);
		if (mappingType != null)
			parameters.setMappingType(mappingType);
		if (startDate != null && endDate != null) {
			parameters.setStartDate(startDate);
			parameters.setEndDate(endDate);
		}
		if (relationshipTypes != null && !relationshipTypes.isEmpty())
			parameters.setRelationshipTypes(relationshipTypes);
		if (mappingSources != null && !mappingSources.isEmpty())
			parameters.setMappingSource(mappingSources);

		return parameters;
	}

}
