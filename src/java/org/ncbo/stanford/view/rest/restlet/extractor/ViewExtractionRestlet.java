package org.ncbo.stanford.view.rest.restlet.extractor;

import java.io.File;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.FileRepresentation;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class ViewExtractionRestlet extends AbstractBaseRestlet {

	private static transient Logger log = Logger
			.getLogger(ViewExtractionRestlet.class);

	private ConceptService conceptService;
	private String tempfile;

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
			String ontologyFilename = ontology.getOntologyID().toString();

			String filename = ontologyFilename.substring(ontologyFilename
					.lastIndexOf("/") + 1, ontologyFilename.length() - 1);
			if (!filename.contains(".owl")) {
				filename = filename + ".owl";
			}

			File output = new File(tempfile + File.separator + filename);

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
			deleteTempfiles(filename);
			// }

		} catch (Throwable t) {
			log.log(Level.ERROR, t.getMessage(), t);
		}
	}

	private void deleteTempfiles(String filename) {

		File directory = new File(tempfile);
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

	public String getTempfile() {
		return tempfile;
	}

	public void setTempfile(String tempfile) {
		this.tempfile = tempfile;
	}

}
