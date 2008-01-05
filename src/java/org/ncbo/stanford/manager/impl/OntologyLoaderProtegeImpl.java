package org.ncbo.stanford.manager.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.manager.OntologyLoader;
import org.ncbo.stanford.service.ontology.impl.OntologyServiceImpl;

import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * OntologyLoaderProtegeImpl is a class that loads ontologies into a Protege
 * repository. It is an implementation of the OntologyLoader interface. Thus,
 * this class should never be directly invoked by the BioPortal service layer.
 * 
 * @author Benjamin Dai
 * 
 */
public class OntologyLoaderProtegeImpl implements OntologyLoader {

	private static final Log log = LogFactory
			.getLog(OntologyLoaderProtegeImpl.class);

	private final static int BIG_PROTEGE_FILE = 100000; // Threshold indicating
														// a big ontology file.
														// This should be in a
														// configuration file.
	private final static String SPACE = " ";

	/**
	 * Loads the specified ontology into the BioPortal repository.
	 * 
	 * @param ontologyFile
	 *            the file containing the ontlogy to be loaded.
	 * 
	 * @exception FileNotFoundException
	 *                the ontology file to be loaded was not found.
	 * @exception Exception
	 *                catch all for all other ontlogy file load errors.
	 */
	public void loadOntlogy(File ontologyFile) throws FileNotFoundException, Exception {
		
		if (ontologyFile == null) {
			log.error("Missing ontlogy file to load.");
			throw(new FileNotFoundException("Missing ontology file to load"));
		}
		
		// If the ontology file is big, use the slower/more scalable database
		// driven Protege load approach.
		if (ontologyFile.length() > BIG_PROTEGE_FILE) {
			if (log.isDebugEnabled())
				log.debug("Loading big ontology file: " + ontologyFile.getAbsoluteFile()
												+ SPACE + ontologyFile.getName());
/*			
			CreateOWLDatabaseFromFileProjectPlugin creator = new CreateOWLDatabaseFromFileProjectPlugin();
	        creator.setKnowledgeBaseFactory(new
	OWLDatabaseKnowledgeBaseFactory());
	        creator.setDriver(driver);
	        creator.setURL(url);
	        creator.setTable(table);
	        creator.setUsername(user);
	        creator.setPassword(password);
	        creator.setOntologyFileURI(new URI(uri));
	        creator.setUseExistingSources(true);

	        Project p = creator.createProject();
*/
		}
		else { 
			// If the ontology file is not too big, use the faster in-memory
			// Protege load approach.

		}
	}
}
