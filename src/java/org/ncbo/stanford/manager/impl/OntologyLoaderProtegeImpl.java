package org.ncbo.stanford.manager.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.manager.OntologyLoader;
import org.ncbo.stanford.service.ontology.impl.OntologyServiceImpl;

import edu.stanford.smi.protegex.owl.database.CreateOWLDatabaseFromFileProjectPlugin;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protege.storage.database.*;

import java.net.*;
import java.util.ArrayList;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protege.util.PropertyList;
//import edu.stanford.smi.protege.util.URIUtilities;
//import edu.stanford.smi.protegex.owl.model.ProtegeCls;

import java.util.ArrayList;
import java.util.Iterator;
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

	private final static int BIG_PROTEGE_FILE = 500000; // Threshold in bytes indicating
														// a big ontology file.
														// This should be in a
														// configuration file.
	private final static String SPACE = " ";
	private final static String TABLE_SUFFIX = "_table";
	
	// Sample data for development
    private final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private final static String DB_URL = "jdbc:mysql://localhost/protege";
    
    private final static String PROTEGE_USER = "protege_user";
    private final static String PROTEGE_PASSWORD = "protege_user$123";

 //   private final static String SOURCE_OWL_URI = "file:///c:/pizza.owl";
 //   private final static String ERRORS_URI = "file:///c:/pizza_errors.txt";

	
	/**
	 * Loads the specified ontology into the BioPortal repository.  If the specified ontology 
	 * identifier already exists, overwrite the ontology with the new ontology file.
	 *
 	 * @param ontologyID	the ontology id for the specified ontology file.
	 * @param ontologyFile
	 *            the file containing the ontlogy to be loaded.
	 * 
	 * @exception FileNotFoundException
	 *                the ontology file to be loaded was not found.
	 * @exception Exception
	 *                catch all for all other ontlogy file load errors.
	 */
	public void loadOntlogy(int ontologyID, File ontologyFile) throws FileNotFoundException, Exception {
		
		if (ontologyFile == null) {
			log.error("Missing ontlogy file to load.");
			throw(new FileNotFoundException("Missing ontology file to load"));
		}
		
		// Setup ontology URI and table name
		URI ontologyURI = ontologyFile.toURI();
		String tableName = Integer.toString(ontologyID) + TABLE_SUFFIX;
		
		// If the ontology file is small, use the fast non-streaming Protege load code.
		if (ontologyFile.length() < BIG_PROTEGE_FILE) {
			if (log.isDebugEnabled())  {
				log.debug("Loading small ontology file: " + ontologyFile.getAbsoluteFile());
				log.debug("File name: " + ontologyFile.getName());
				log.debug("URI: " + ontologyURI.toString());
			}
			
			OWLModel fileModel = ProtegeOWL.createJenaOWLModelFromURI(ontologyURI.toString());
		     
		    List errors = new ArrayList();
		    Project fileProject = fileModel.getProject();
		        OWLDatabaseKnowledgeBaseFactory factory = new OWLDatabaseKnowledgeBaseFactory();
		        PropertyList sources = PropertyList.create(fileProject.getInternalProjectKnowledgeBase());
		     
		        DatabaseKnowledgeBaseFactory.setSources(sources, JDBC_DRIVER, DB_URL, tableName, PROTEGE_USER, PROTEGE_PASSWORD);
		        factory.saveKnowledgeBase(fileModel, sources, errors);

		        // next lines of code are not needed - just testing the import
		        OWLModel om = (OWLModel) fileProject.getKnowledgeBase();

		        OWLNamedClass country = om.getOWLNamedClass("Country");
		        for (Object o : country.getInstances(true)) {
		        	System.out.println("" + o + " is an instance of Country");
		        }
		        
		    //    System.out.println("Parents of Country");
		      //  for (Iterator it = country.getSuperclasses(true).iterator(); it.hasNext(); ) {
		        //	System.out.println("parent> " + it.next());		        	
		        //}
		}
		else {
			// If the ontology file is small, use the fast non-streaming Protege load code.
			
			if (log.isDebugEnabled())
				log.debug("Loading big ontology file: " + ontologyFile.getAbsoluteFile()
												+ SPACE + ontologyFile.getName());
			
			
		         
/*		
			try {
			    CreateOWLDatabaseFromFileProjectPlugin creator = new CreateOWLDatabaseFromFileProjectPlugin();
		        creator.setKnowledgeBaseFactory(new	OWLDatabaseKnowledgeBaseFactory());
		        creator.setDriver(JDBC_DRIVER);
		        creator.setURL(DB_URL);
		        creator.setTable(TABLE_STREAM);
		        creator.setUsername(PROTEGE_USER);
		        creator.setPassword(PROTEGE_PASSWORD);
		        
		        URI myURI = new URI(SOURCE_OWL_URI);
		        System.out.println("path: " + myURI.getPath());
		        creator.setOntologyFileURI(myURI);
		        
		        creator.setUseExistingSources(true);

		        Project p = creator.createProject();
		       // end of import

		     //next lines of code are not needed - just testing the import
		        OWLModel om = (OWLModel) p.getKnowledgeBase();

		        OWLNamedClass country = om.getOWLNamedClass("Country");
		        for (Object o : country.getInstances(true)) {
		            System.out.println("" + o + " is an instance of Country");
		        }
		    }
			catch (URISyntaxException exc) {
				fail("Invalid ontology file URI: " + SOURCE_OWL_URI);
			}
		}
		*/
		}
	}
}
