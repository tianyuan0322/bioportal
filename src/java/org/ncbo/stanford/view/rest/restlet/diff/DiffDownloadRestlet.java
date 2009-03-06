package org.ncbo.stanford.view.rest.restlet.diff;

import java.io.*;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.ncbo.stanford.service.diff.*;
import org.ncbo.stanford.util.*;
import org.ncbo.stanford.view.rest.restlet.*;
import org.ncbo.stanford.view.rest.restlet.ontology.*;
import org.ncbo.stanford.view.util.constants.*;
import org.restlet.data.*;
import org.restlet.resource.*;

/**
 * @author Natasha Noy
 */
public class DiffDownloadRestlet extends AbstractBaseRestlet {
	private DiffService diffService;
	private static final Log log = LogFactory
			.getLog(OntologyDownloadRestlet.class);

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	protected void getRequest(Request request, Response response) {
		downloadDiff(request, response);
	}
	
	/**
	 * Return the file with the diff between ontology verisons, if one exists.
	 * Tries both possible orders for the version
	 * 
	 * @param request
	 * @param response
	 */
	
	private void downloadDiff(Request request, Response response) {
		String ontologyVersionId1 = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid1"));
		String ontologyVersionId2 = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyversionid2"));
		
		try {
			Integer intId1 = Integer.parseInt(ontologyVersionId1);
			Integer intId2 = Integer.parseInt(ontologyVersionId2);
			
			HttpServletRequest httpRequest = RequestUtils.getHttpServletRequest(request);
			String format = (String) httpRequest.getParameter(RequestParamConstants.PARAM_FORMAT);
			
			File file = diffService.getDiffFileForOntologyVersions(intId1, intId2, format);
			

			FileRepresentation fileRepresentation = new FileRepresentation(
					file, MediaType.APPLICATION_ALL, 60);
			response.setEntity(fileRepresentation);

			String fileName = file.getName();
			
			RequestUtils.getHttpServletResponse(response)
			.setHeader(
					"Content-Disposition",
					"attachment; filename=\"" + fileName
							+ "\";");
			
		} catch (NumberFormatException nfe) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, nfe
					.getMessage());
			nfe.printStackTrace();
			log.error(nfe);
		} catch (FileNotFoundException fnfe) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, 
							"No diff file found");
			fnfe.printStackTrace();
			log.error(fnfe);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		}
		
	}


	public void setDiffService(DiffService diffService) {
		this.diffService = diffService;
	}


}
