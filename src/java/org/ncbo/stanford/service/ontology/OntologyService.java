package org.ncbo.stanford.service.ontology;

import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;

public interface OntologyService {
	
	List<OntologyBean> findLatestOntologyVersions();
	
	OntologyBean findOntology(int id,String version);
	
	List<OntologyBean> findOntologyVersions(int id);
	
	List<String> findProperties(int id);
	
	void createOntology(OntologyBean ontology);
}



