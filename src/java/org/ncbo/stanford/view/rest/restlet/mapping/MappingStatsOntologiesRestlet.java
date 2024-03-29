package org.ncbo.stanford.view.rest.restlet.mapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.mapping.MappingOntologyPairStatsBean;
import org.ncbo.stanford.bean.mapping.MappingOntologyStatsBean;
import org.ncbo.stanford.service.mapping.MappingService;
import org.ncbo.stanford.sparql.bean.Mapping;
import org.ncbo.stanford.util.RequestUtils;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

import com.mysql.jdbc.StringUtils;

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
		String ontologyIdStr = (String) request.getAttributes().get(
				"ontologyids");

		if (StringUtils.isNullOrEmpty(ontologyIdStr)) {
			listMappings(request, response);
		} else {
			listMappingsForOntologySet(request, response, ontologyIdStr);
		}
	}

	private void listMappingsForOntologySet(Request request, Response response,
			String ontologyIdsStr) {
		List<Integer> ontologyIds = RequestUtils
				.parseIntegerListParam(ontologyIdsStr);
		
		Set<Integer> ontologyIdSet = new HashSet<Integer>(ontologyIds);

		Set<MappingOntologyPairStatsBean> ontologyCounts = new HashSet<MappingOntologyPairStatsBean>();
		try {
			for (Integer ontologyId : ontologyIds) {
				List<MappingOntologyStatsBean> stats = mappingService
						.getOntologyMappingCount(Mapping.ontologyURIFromOntologyID(ontologyId));

				for (MappingOntologyStatsBean stat : stats) {
					if (ontologyIdSet.contains(stat.getOntologyId())) {
						MappingOntologyPairStatsBean pairStats = new MappingOntologyPairStatsBean();
						pairStats.setSourceOntologyId(ontologyId);
						pairStats.setTargetOntologyId(stat.getOntologyId());
						pairStats.setMappingCount(stat.getSourceMappings());
						ontologyCounts.add(pairStats);
					}
				}

			}
		} catch (Exception e) {
			log.debug(e);
			e.printStackTrace();
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					ontologyCounts);
		}
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
