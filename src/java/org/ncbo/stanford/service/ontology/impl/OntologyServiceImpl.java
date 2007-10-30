package org.ncbo.stanford.service.ontology.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.service.ontology.OntologyService;

public class OntologyServiceImpl implements OntologyService {

	private static final Log log = LogFactory.getLog(OntologyServiceImpl.class);
	
	public List<OntologyBean> getAllOntologies() {
		log.debug("getting all ontologies");
		
		ArrayList<OntologyBean> ontList = new ArrayList<OntologyBean>(1);
		
		OntologyBean ontBean = new OntologyBean();
		ontBean.setDisplayLabel("NCI Thesaurus");
		
		ontList.add(ontBean);
		
		return ontList;
	}

}
