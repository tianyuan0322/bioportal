package org.ncbo.stanford.service.loader.remote;

/**
 * A utility that runs as a scheduled process, connecting to OBO Sourceforge CVS
 * system and pulling all new and updated ontologies into BioPortal
 * 
 * @author Michael Dorf
 */
public interface OBOCVSPullService {

	/**
	 * Performs the pull of ontologies from OBO Sourceforge CVS
	 */
	public void doOntologyPull();
}
