package org.ncbo.stanford.service.ontology;

import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;

public interface OntologyService {
	List<OntologyBean> findLatestOntologyVersions();
}



