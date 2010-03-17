package org.ncbo.stanford.view.rest.restlet.extractor;

import java.io.File;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.ncbo.stanford.exception.ConceptNotFoundException;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.FileRepresentation;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.ncbo.stanford.exception.InvalidInputException;

public class ViewExtractionRestlet extends AbstractBaseRestlet {

	private static transient Logger log = Logger
			.getLogger(ViewExtractionRestlet.class);

	private ConceptService conceptService;
	private String tempDir;

	// Limits the total number of traversed concepts
	private int traversedConceptLimit;

	@Override
	public void getRequest(Request request, Response response) {
		viewExtractor(request, response);

	}

	public void viewExtractor(Request request, Response response) {
		try {
			NcboProperties ncboProperties;
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			
			// get conceptId and versionId from request
			String conceptId = getConceptId(request);
			String versionId = (String) request.getAttributes().get(
					MessageUtils.getMessage("entity.ontologyversionid"));

			HttpServletRequest httpRequest = RequestUtils
					.getHttpServletRequest(request);

			String delay = httpRequest
					.getParameter(RequestParamConstants.PARAM_DELAY);
			String relations = httpRequest
					.getParameter(RequestParamConstants.PARAM_FILTERRELATIONS);

			String ontologyName = httpRequest
					.getParameter(RequestParamConstants.PARAM_ONTOLOGYNAME);
			String existingOntology = httpRequest
					.getParameter(RequestParamConstants.PARAM_EXISTONTOLOGY);
			String logCount = httpRequest
					.getParameter(RequestParamConstants.PARAM_LOGCOUNT);
			String saveCount = httpRequest
					.getParameter(RequestParamConstants.PARAM_SAVECOUNT);

			// set all request parameter to props obj
			Properties properties = new Properties();
			if (delay != null)
				properties.setProperty("bioportal.delay.ms", delay);
			if (relations != null)
				properties.setProperty("bioportal.filter.relations", relations);
			properties.setProperty("target.ontology.name", ontologyName);
			if (existingOntology != null)
				properties.setProperty("target.append.existing.ontology",
						existingOntology);
			if (logCount != null)
				properties.setProperty("log.count", logCount);
			if (saveCount != null)
				properties.setProperty("save.count", saveCount);

			// Make sure saveCount is below appropriate limits
			Integer checkCount = RequestUtils.parseIntegerParam(saveCount);
			if (checkCount != null && checkCount > traversedConceptLimit) {
				throw new InvalidInputException(MessageUtils.getMessage(
						"msg.error.overConceptLimit").replaceAll(
						"%CONCEPT_LIMIT%",
						Integer.toString(traversedConceptLimit)));
			}

			ncboProperties = new NcboProperties();
			ncboProperties.setProps(properties);

			Integer ontologyVersionId = Integer.parseInt(versionId);

			OWLOntology ontology;
			ontology = manager.createOntology(IRI.create(ncboProperties
					.getOwlOntologyName()));
			ncboProperties.getFilteredOutProperties();
			OntologyExtractor extractor = new OntologyExtractor(
					ontologyVersionId, ncboProperties, manager, ncboProperties
							.getBioportalOntologyId(), null);
			extractor.setConceptService(conceptService);

			// perform extraction
			extractor.extract(ontology, conceptId);
			if (extractor.getConceptFound() == false) {
				throw new ConceptNotFoundException(MessageUtils
						.getMessage("msg.error.conceptNotFound"));
			}

			String filename = ontologyName.toString();
			if (!filename.contains(".owl")) {
				filename = filename + ".owl";
			}

			File output = new File(tempDir + File.separator + filename);

			// synchronized (this) {
			// save ontology into local system
			manager.saveOntology(ontology, output.toURI());

			FileRepresentation fileRepresentation = new FileRepresentation(
					output, MediaType.APPLICATION_ALL,
					ApplicationConstants.TIMETOLIVE);
			response.setEntity(fileRepresentation);
			RequestUtils.getHttpServletResponse(response).setHeader(
					"Content-Disposition",
					"attachment; filename=\"" + filename + "\";");
			// sleep
			// Thread.sleep(ApplicationConstants.TIMETOLIVE * 1000);
			// delete tmp files
			deletetempDirs(filename);
			// }

		} catch (ConceptNotFoundException cnfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, cnfe.getMessage());
		} catch (InvalidInputException iie) {
			response
			.setStatus(Status.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE, iie.getMessage());
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			log.error(e);
		}
	}

	private void deletetempDirs(String filename) {

		File directory = new File(tempDir);
		if (directory.exists()) {

			File[] files = directory.listFiles();
			for (File file : files) {
				if (!file.getName().equals(filename)) {
					// delete exiting file
					file.delete();
				}
			}
		} else {
			directory.mkdirs();
		}
	}

	public ConceptService getConceptService() {
		return conceptService;
	}

	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	public String getTempDir() {
		return tempDir;
	}

	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

	/**
	 * @return the traversedConceptLimit
	 */
	public int getTraversedConceptLimit() {
		return traversedConceptLimit;
	}

	/**
	 * @param traversedConceptLimit the traversedConceptLimit to set
	 */
	public void setTraversedConceptLimit(int traversedConceptLimit) {
		this.traversedConceptLimit = traversedConceptLimit;
	}

}
