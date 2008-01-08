package org.ncbo.stanford.manager;

import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import org.ncbo.stanford.bean.OntologyBean;
/**
 * An interface to load ontologies into the BioPortal repository.  Since there may be many implementations, the
 * service layer should never directly invoke any implementations of this interface (e.g. LexGrid and Protege).
 * 
 * @author Benjamin Dai
 * 
 */
public interface OntologyLoader {

	/**
	 * Loads the specified ontology into the BioPortal repository.  If the ontology is missing is source file and identifier, an exception 
	 * will be thrown.
	 * 
	 * @param ontology	the ontology to load.
	 * 
	 * @exception	FileNotFoundException	the ontology file to be loaded was not found.
	 * @exception	Exception catch all for all other ontlogy file load errors.
	 */
	public void loadOntology(OntologyBean ontology) throws FileNotFoundException, Exception;

	/**
	 * Loads the specified ontology into the BioPortal repository.  
	 * 
	 * @param ontologyID	the ontology id for the specified ontology file.
	 * @param ontologyFile	the file containing the ontlogy to  be loaded.
	 * 
	 * @exception	FileNotFoundException	the ontology file to be loaded was not found.
	 * @exception	Exception catch all for all other ontlogy file load errors.
	 */
	public void loadOntology(Integer ontologyID, File ontologyFile) throws FileNotFoundException, Exception;
}
