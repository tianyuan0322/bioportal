package org.ncbo.stanford.service.ontology;

import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;

/**
 * Exposes services that provide read and write operations on ontologies
 * 
 * @author Michael Dorf
 * @author Nick Griffith
 */
public interface OntologyService {

	/**
	 * Returns a single record for each ontology in the system. If more than one
	 * version of ontology exists, return the latest version.
	 * 
	 * @return list of Ontology beans
	 */
	public List<OntologyBean> findLatestOntologyVersions();

	public OntologyBean findOntology(int id, String version);

	public List<OntologyBean> findOntologyVersions(int id);

	public List<String> findProperties(int id);

	public void createOntology(OntologyBean ontology);
}
