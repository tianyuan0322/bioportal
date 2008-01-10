package org.ncbo.stanford.service.ontology;

import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * Exposes services that provide read and write operations on ontologies
 * 
 * @author Michael Dorf
 * @author Nick Griffith
 */
@Transactional
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

	/**
	 * Loads the specified ontology into the BioPortal repository. The minimum
	 * requirement is that the ontology file/uri exists and is in the right
	 * format. If the ontology id already exists, the invocation assumes
	 * overwrite of the existing ontology.
	 * 
	 * @param ontology
	 */
	public void loadOntology(OntologyBean ontology);
}
