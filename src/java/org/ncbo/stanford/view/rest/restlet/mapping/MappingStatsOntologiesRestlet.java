package org.ncbo.stanford.view.rest.restlet.mapping;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.mapping.MappingOntologyStatsBean;
import org.ncbo.stanford.service.mapping.MappingService;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class MappingStatsOntologiesRestlet extends AbstractMappingRestlet {

	private static final Log log = LogFactory
			.getLog(MappingStatsOntologiesRestlet.class);

	private MappingService mappingService;

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	public void getRequest(Request request, Response response) {
		listMappings(request, response);
	}

	private void listMappings(Request request, Response response) {
		List<MappingOntologyStatsBean> ontologyCounts = null;
		try {
			ontologyCounts = mappingService.getOntologiesMappingCount();
		} catch (Exception e) {
			log.debug(e);
			e.printStackTrace();
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					ontologyCounts);
		}
	}

	/**
	 * @param mappingService
	 *            the mappingService to set
	 */
	public void setMappingService(MappingService mappingService) {
		this.mappingService = mappingService;
	}

}
