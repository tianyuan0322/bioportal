package org.ncbo.stanford.service.concept;

/**
 * Service that provides RDF N3 representations of ontologies.
 * 
 * @author <a href="mailto:tony@loeser.name">Tony Loeser</a>
 */
public interface DumpRDFService {

	/**
	 * Return an RDF N3 document representing the ontology.  Only certain information
	 * is included; classes, with their names, synonyms, and subclass relationships.
	 */
	public String generateRDFDump(Integer ontologyVersionId) throws Exception;
}
