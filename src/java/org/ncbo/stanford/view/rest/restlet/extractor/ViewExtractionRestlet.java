package org.ncbo.stanford.view.rest.restlet.extractor;
import java.io.File;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class ViewExtractionRestlet extends AbstractBaseRestlet{
	
    private static transient Logger log = Logger.getLogger(ViewExtractionRestlet.class);
    
    private NcboProperties ncboProperties;
    private ConceptService conceptService;

    @Override
    public void getRequest(Request request,Response response){
    	viewExtractor(request,response);
    	
    }
	public void viewExtractor(Request request,Response response) {
		String message="";
	    try {
	        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	        String conceptId = getConceptId(request);
	        String versionId = (String) request.getAttributes().get(
					MessageUtils.getMessage("entity.ontologyversionid"));
	        
	        Integer ontologyVersionId=Integer.parseInt(versionId);
	        File output = new File(ncboProperties.getOntologyFileLocation());
	        OWLOntology ontology;
	        if (ncboProperties.getAppendOntologyFile() && output.exists()) {
	            log.info("Loading ontology");
	            ontology = manager.loadOntologyFromPhysicalURI(output.toURI());
	        }
	        else {
	            ontology = manager.createOntology(IRI.create(ncboProperties.getOwlOntologyName()));
	        }
	        ncboProperties.getFilteredOutProperties();
	        OntologyExtractor extractor = new OntologyExtractor(ontologyVersionId,ncboProperties,manager, ncboProperties.getBioportalOntologyId(), output);
	        extractor.setConceptService(conceptService);
	        log.info("Started ontology extraction on " + new Date());
	        extractor.extract(ontology, conceptId);
	        log.info("Finished ontology extraction on " + new Date());
	        log.info("Saving ontology");
	        manager.saveOntology(ontology, output.toURI());	        
	        log.info("Done on " + new Date());
	        message="Ontology extraction finished sucessfuly";
	    }
	    catch (Throwable t) {
	        log.log(Level.ERROR, t.getMessage(),t);
	        message="Ontology extraction failed";
	    }
	    finally{
	    	xmlSerializationService.generateXMLResponse(request, response,message);
	    }
	}
	public NcboProperties getNcboProperties() {
		return ncboProperties;
	}
	public void setNcboProperties(NcboProperties ncboProperties) {
		this.ncboProperties = ncboProperties;
	}
	public ConceptService getConceptService() {
		return conceptService;
	}
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	
}
