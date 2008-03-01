package org.ncbo.stanford.manager.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.OntologyLoadManager;
import org.ncbo.stanford.manager.wrapper.OntologyLoadManagerWrapper;

public class OntologyLoadManagerImpl implements OntologyLoadManager {

	private static final Log log = LogFactory
			.getLog(OntologyLoadManagerImpl.class);

	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>();
	private Map<String, OntologyLoadManagerWrapper> ontologyLoadHandlerMap = new HashMap<String, OntologyLoadManagerWrapper>();

	/**
	 * Loads the specified ontology into the BioPortal repository. The minimum
	 * requirement is that the ontology file/uri exists and is in the right
	 * format. If the ontology id already exists, the invocation assumes
	 * overwrite of the existing ontology.
	 * 
	 * @param ontologyBean
	 *            bean
	 * @throws URISyntaxException
	 */
	public void loadOntology(OntologyBean ontologyBean) throws Exception {
		String formatHandler = ontologyFormatHandlerMap.get(ontologyBean
				.getFormat());
		OntologyLoadManagerWrapper loadManagerWrapper = ontologyLoadHandlerMap
				.get(formatHandler);
		List<String> filenames = ontologyBean.getFilenames();

		for (String filename : filenames) {
			loadManagerWrapper.loadOntology(new URI(ontologyBean.getFilePath()
					+ "/" + filename), ontologyBean);
		}
	}

	/**
	 * @return the ontologyLoadHandlerMap
	 */
	public Map<String, OntologyLoadManagerWrapper> getOntologyLoadHandlerMap() {
		return ontologyLoadHandlerMap;
	}

	/**
	 * @param ontologyLoadHandlerMap
	 *            the ontologyLoadHandlerMap to set
	 */
	public void setOntologyLoadHandlerMap(
			Map<String, OntologyLoadManagerWrapper> ontologyLoadHandlerMap) {
		this.ontologyLoadHandlerMap = ontologyLoadHandlerMap;
	}

	/**
	 * @return the ontologyFormatHandlerMap
	 */
	public Map<String, String> getOntologyFormatHandlerMap() {
		return ontologyFormatHandlerMap;
	}

	/**
	 * @param ontologyFormatHandlerMap
	 *            the ontologyFormatHandlerMap to set
	 */
	public void setOntologyFormatHandlerMap(
			Map<String, String> ontologyFormatHandlerMap) {
		this.ontologyFormatHandlerMap = ontologyFormatHandlerMap;
	}
}
