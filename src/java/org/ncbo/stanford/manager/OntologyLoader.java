package org.ncbo.stanford.manager;

import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * An interface to load ontologies into the BioPortal repository.  Since there may be many implementations, the
 * service layer should never directly invoke any implementations of this interface (e.g. LexGrid and Protege).
 * 
 * @author Benjamin Dai
 * 
 */
public interface OntologyLoader {

	/**
	 * Loads the specified ontology into the BioPortal repository.
	 * 
	 * @param ontologyFile	the file containing the ontlogy to  be loaded.
	 * 
	 * @exception	FileNotFoundException	the ontology file to be loaded was not found.
	 * @exception	Exception catch all for all other ontlogy file load errors.
	 */
	public void loadOntlogy(File ontologyFile) throws FileNotFoundException, Exception;
}
