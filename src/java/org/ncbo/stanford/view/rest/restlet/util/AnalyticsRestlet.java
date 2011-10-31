package org.ncbo.stanford.view.rest.restlet.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserDAO;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

public class AnalyticsRestlet extends AbstractBaseRestlet {

	private OntologyService ontologyService;
	private NcboUserDAO ncboUserDAO;

	@Override
	public void anyMethodRequest(Request request, Response response) {
		String analyticsType = (String) request.getAttributes().get("analyticsType");
		
		if (analyticsType.equalsIgnoreCase("ontologies")) {
			generateOntologiesInfo(request, response);
		} else if (analyticsType.equalsIgnoreCase("users")) {
			generateUsersInfo(request, response);
		}
	}

	public void generateOntologiesInfo(Request request, Response response) {
		Map<Integer, String> abbreviations = getAbbreviations();
		StringBuffer analyticsMap = new StringBuffer();

		for (Entry<Integer, String> a : abbreviations.entrySet()) {
			analyticsMap.append(a.getKey() + "," + a.getValue() + "\n");
		}

		RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
				MediaType.TEXT_CSV, analyticsMap.toString());
	}
	
	public void generateUsersInfo(Request request, Response response) {
		List<String[]> userInformation = getUserInformation();
		StringBuffer analyticsMap = new StringBuffer();

		for (String[] u : userInformation) {
			analyticsMap.append(u[0] + "," + u[2] + "\n");
		}

		RequestUtils.setHttpServletResponse(response, Status.SUCCESS_OK,
				MediaType.TEXT_CSV, analyticsMap.toString());
	}

	public Map<Integer, String> getAbbreviations() {
		Map<Integer, String> abbreviations = new HashMap<Integer, String>();
		List<OntologyBean> ontAndViews = null;
		try {
			ontAndViews = ontologyService.findLatestOntologyVersions();
			ontAndViews
					.addAll(ontologyService.findLatestOntologyViewVersions());
			for (OntologyBean o : ontAndViews) {
				List<OntologyBean> versions = ontologyService
						.findAllOntologyOrViewVersionsByVirtualId(
								o.getOntologyId(), false);

				// Important: this must come before we get all versions as we'll
				// use the most revent abbreviation this way
				abbreviations.put(o.getOntologyId(), o.getAbbreviation());

				for (OntologyBean version : versions) {
					abbreviations.put(version.getId(),
							abbreviations.get(version.getOntologyId()));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return abbreviations;
	}

	public List<String[]> getUserInformation() {
		List<String[]> usersInfo = new ArrayList<String[]>();

		@SuppressWarnings("unchecked")
		List<NcboUser> users = ncboUserDAO.findAll();

		for (NcboUser user : users) {
			String[] userInfo = new String[3];

			userInfo[0] = user.getApiKey();
			userInfo[1] = user.getId().toString();
			userInfo[2] = user.getEmail();

			usersInfo.add(userInfo);
		}

		return usersInfo;
	}

	/**
	 * @param ontologyService
	 *            the ontologyService to set
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

	/**
	 * @param ncboUserDAO
	 *            the ncboUserDAO to set
	 */
	public void setNcboUserDAO(NcboUserDAO ncboUserDAO) {
		this.ncboUserDAO = ncboUserDAO;
	}

}
