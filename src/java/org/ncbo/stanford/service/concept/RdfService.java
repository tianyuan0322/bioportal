package org.ncbo.stanford.service.concept;

import java.io.File;
import java.util.List;

import org.ncbo.stanford.service.ontology.OntologyService;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Service that provides RDF/XML representations of an entire ontology or a list
 * of concepts from a certain ontology.
 * 
 * @author plependu
 * 
 */
public interface RdfService {

	/**
	 * Generate the RDF/XML representation for all concepts for all ontologies.
	 * 
	 * Output: the files are stored to disk.
	 * 
	 * @param manager
	 * @throws Exception
	 */
	public void generateRdf(OWLOntologyManager manager, String dir, OntologyService ontologyService) throws Exception;
	

	/**
	 * Generate an RDF/XML representation of all concepts for a single ontology.
	 *  
	 * @param manager
	 * @param ontology
	 * @param ontologyVersionId
	 * @return manager
	 * @return ontology - the ontology is returned
	 * @throws Exception
	 */
	public OWLOntology generateRdf(OWLOntologyManager manager, String dir, Integer ontologyVersionId, boolean isVirtual) throws Exception;

	/**
	 * Generate the RDF/XML representation for a specific concept from a given
	 * ontology.
	 * 
	 * 
	 * @param manager
	 * @param ontology - the ontology is returned
	 * @param ontologyVersionId
	 * @param conceptId
	 * @return RDF representation of the concept from the ontology
	 * @throws Exception
	 */
	public OWLOntology generateRdf(OWLOntologyManager manager, String dir, Integer ontologyVersionId, boolean isVirtual, String conceptId)
			throws Exception;

	/**
	 * Given a list (singleton, many, or NULL) of concepts, generate the RDF for
	 * a given ontology version.
	 * 
	 * @param manager
	 * @param dir
	 * @param ontologyVersionId
	 * @param isVirtual
	 * @param conceptIds
	 * @return
	 * @throws Exception
	 */
	public OWLOntology generateRdf(OWLOntologyManager manager, String dir,
			Integer ontologyVersionId, boolean isVirtual,
			List<String> conceptIds) throws Exception;

}
