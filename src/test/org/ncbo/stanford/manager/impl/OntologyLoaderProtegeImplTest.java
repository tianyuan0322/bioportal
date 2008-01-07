package org.ncbo.stanford.manager.impl;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import edu.stanford.smi.protegex.owl.database.CreateOWLDatabaseFromFileProjectPlugin;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protege.storage.database.*;

import java.net.*;
import java.util.ArrayList;
import junit.framework.TestCase;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.util.URIUtilities;

/**
 * Tests loading ontologies into Protege using the OntologyLoaderProtegeImpl
 * 
 * @author Benjamin Dai
 */
public class OntologyLoaderProtegeImplTest extends TestCase {
	
    private final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private final static String DB_URL = "jdbc:mysql://localhost/protege";
    
    private final static String TABLE_STREAM = "pizza_table_stream";
    private final static String TABLE_NONSTREAM = "pizza_table_nonstream";

    private final static String PROTEGE_USER = "protege_user";
    private final static String PROTEGE_PASSWORD = "protege_user$123";

    private final static String SOURCE_OWL_URI = "file:///c:/pizza.owl";
    private final static String ERRORS_URI = "file:///c:/pizza_errors.txt";
    
    /**
     * Streaming load for large OWL ontologies.  Note that this is very slow.
     */
	public void testBasicPizzaStreamingLoad(){
			try {
			
				// start import
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
	
	// Fast load
	public void testBasicPizzaNonStreamingLoad(){
		try {
		
		     System.out.println("In Convert to Database Project");
		     OWLModel fileModel = ProtegeOWL.createJenaOWLModelFromURI(SOURCE_OWL_URI);
		     
		     List errors = new ArrayList();
		        Project fileProject = fileModel.getProject();
		        OWLDatabaseKnowledgeBaseFactory factory = new OWLDatabaseKnowledgeBaseFactory();
		        PropertyList sources = PropertyList.create(fileProject.getInternalProjectKnowledgeBase());
		     
		        DatabaseKnowledgeBaseFactory.setSources(sources, JDBC_DRIVER, DB_URL, TABLE_NONSTREAM, PROTEGE_USER, PROTEGE_PASSWORD);
		        factory.saveKnowledgeBase(fileModel, sources, errors);

		        // next lines of code are not needed - just testing the import
		        OWLModel om = (OWLModel) fileProject.getKnowledgeBase();

		        OWLNamedClass country = om.getOWLNamedClass("Country");
		        for (Object o : country.getInstances(true)) {
		            System.out.println("" + o + " is an instance of Country");
		        }
 
		        // Optional error dump of load
		        displayErrors(errors); //optional
		        if (!errors.isEmpty()) {
		        	fail("There were errors: " + errors.size());
		        	return;
		        }

		        Project dbProject = Project.createNewProject(factory, errors);
		        DatabaseKnowledgeBaseFactory.setSources(dbProject.getSources(), JDBC_DRIVER, DB_URL, TABLE_NONSTREAM, PROTEGE_USER, PROTEGE_PASSWORD);

		        dbProject.createDomainKnowledgeBase(factory, errors, true);
		        dbProject.setProjectURI(URIUtilities.createURI(ERRORS_URI));
		        dbProject.save(errors);

		        displayErrors(errors);  //optional
		       // return (OWLModel) dbProject.getKnowledgeBase();
		        

	    }
		catch (URISyntaxException exc) {
			fail("Invalid ontology file URI: " + SOURCE_OWL_URI);
		}
		catch (Exception exc) {
			exc.printStackTrace();
			fail("General exception: " + exc.getMessage());
		}
	}
	
	//
	// Private methods
	//
	/**
	 * Outputs text representations of the specified List.
	 */
	private void displayErrors(List list) {
		int i = 0;
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			System.out.println("ERROR: " + ++i + ") " + it.next());
		}
	}
	
	
}
