package org.ncbo.stanford.view.rest.restlet.concept;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;

public class ConceptChildrenVirtualRestlet extends AbstractBaseRestlet {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory
			.getLog(ConceptChildrenVirtualRestlet.class);

	private ConceptService conceptService;

	/**
	 * @param conceptService
	 *            the conceptService to set
	 */
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}
}
