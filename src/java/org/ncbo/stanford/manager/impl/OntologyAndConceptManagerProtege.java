package org.ncbo.stanford.manager.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ConceptBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.manager.OntologyAndConceptManager;
import org.ncbo.stanford.service.ontology.impl.OntologyServiceImpl;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

/**
 * A default implementation to OntologyAndConceptManager interface designed to
 * provide an abstraction layer to ontology and concept operations. The service
 * layer will consume this interface instead of directly calling a specific
 * implementation (i.e. LexGrid, Protege etc.). Do not use this class directly
 * in upper layers.
 * 
 * NOTE(bdai): This class with all the ontology and concept methods seems to be
 * a bit bloated. Will refactor if it becomes a problem.
 * 
 * TODO: This class is currently Protege specific. Refactoring required so that
 * it can support both LexGrid and Protege.
 * 
 * @author Benjamin Dai
 * 
 */
public class OntologyAndConceptManagerProtege implements OntologyAndConceptManager {

	// Hack for development testing
	private final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private final static String DB_URL = "jdbc:mysql://localhost/protege";

	private final static String PROTEGE_USER = "protege_user";
	private final static String PROTEGE_PASSWORD = "protege_user$123";

	private final static String TEST_OWL_URI = "test/sample_data/pizza.owl";

	private static final Log log = LogFactory
			.getLog(OntologyAndConceptManagerProtege.class);

	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;

	private OntologyLoaderProtegeImpl protegeLoader = null;

	/**
	 * Default Constructor
	 */
	public OntologyAndConceptManagerProtege() {
		this.protegeLoader = new OntologyLoaderProtegeImpl();
	}

	/**
	 * Returns a single record for each ontology in the system. If more than one
	 * version of ontology exists, return the latest version.
	 * 
	 * @return
	 */
	public List<OntologyBean> findLatestOntologyVersions() {
		ArrayList<OntologyBean> ontBeanList = new ArrayList<OntologyBean>(1);
		List<NcboOntology> ontEntityList = ncboOntologyVersionDAO
				.findLatestOntologyVersions();

		for (NcboOntology ncboOntology : ontEntityList) {
			OntologyBean ontologyBean = new OntologyBean();
			ontologyBean.populateFromEntity(ncboOntology);
			ontBeanList.add(ontologyBean);
		}

		return ontBeanList;
	}

	/**
	 * Returns the specified ontology.
	 * 
	 * @param id
	 * @return
	 */

	public OntologyBean findOntology(Integer id) {
		return null;
	}

	public OntologyBean findOntology(int id, String version) {
		return null;
	}

	public List<OntologyBean> findOntologyVersions(int id) {
		return new ArrayList();
	}

	public List<String> findProperties(int id) {
		return new ArrayList();
	}

	/**
	 * Loads the specified ontology into the BioPortal repository.
	 */
	public void loadOntology(OntologyBean ontology) {
		try {
			protegeLoader.loadOntology(ontology);
		} catch (Exception exc) {
			// This is where one would set the code to indicate a failed
			// ontology load...
			// TODO: These ontologies loads are long-running transactions. Thus,
			// this action should in some way be executed asynchronously.
			// TODO: The load action should be queued so that multiple loads do
			// not slam the server.
			// TODO: There needs to be a better mechanism to handle exceptions
			// during ontology loads (e.g. this method could throw an exception
			log.error("Unable load specified ontology: "
					+ ontology.getDisplayLabel());
			exc.printStackTrace();
		}
	}

	//
	// Concept methods
	//

	/**
	 * Get the root concept for the specified ontology.
	 */
	public ConceptBean getRootConcept(int ontologyId) {
		OWLModel owlModel = this.getOWLModel(ontologyId);
		
		// Get all root nodes associated with this ontology. Then iterate
		// through the collection, returning the first one.
		OWLNamedClass oThing = owlModel.getOWLThingClass();

		if (log.isDebugEnabled())
			log.debug("Searching for root node for ontology id: " + ontologyId);
/*	
		OWLNamedClass owlClass = null;
		for (Iterator it = oThing.getNamedSubclasses().iterator(); it.hasNext(); ) {
			owlClass = (OWLNamedClass)it.next();
			
			if (log.isDebugEnabled()) {
				log.debug("Found root node: " + owlClass);
			}
			
			break;
		}
	*/	
		if (oThing != null) 
			return ProtegeBeanFactory.createConceptBean(oThing,ontologyId);
		else 
			return null;
	}

	

	public ConceptBean findConcept(String conceptID, int ontologyId) {
		OWLModel owlModel = this.getOWLModel(ontologyId);

		String conceptName = owlModel.getResourceNameForURI(conceptID);
		
		if (log.isDebugEnabled()) {
			log.debug("Getting concept id: " + conceptID);
		}


		OWLNamedClass owlClass = owlModel.getOWLNamedClass(conceptName);
		
		
		if (owlClass != null) 
			return ProtegeBeanFactory.createConceptBean(owlClass, ontologyId);
		else 
			return null;
	}

	public ArrayList<ConceptBean> findPathToRoot(String id, int ontologyId) {
		/**
		 * OWLModel om = (OWLModel) fileProject.getKnowledgeBase();
		 * 
		 * for (Iterator it = country.getSuperclasses(true).iterator(); it
		 * .hasNext();) { System.out.println("parent> " + it.next()); }
		 */
		return new ArrayList();
	}

	public ConceptBean findParent(String id, int ontologyId) {
		return new ConceptBean();
	}

	public ArrayList<ConceptBean> findChildren(String id, int ontologyId) {
		return new ArrayList();
	}

	public ArrayList<ConceptBean> findConceptNameExact(String query,
			ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ConceptBean> findConceptNameStartsWith(String query,
			ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ConceptBean> findConceptNameContains(String query,
			ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ConceptBean> findConceptPropertyExact(String property,
			String query, ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ConceptBean> findConceptPropertyStartsWith(
			String property, String query, ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ConceptBean> findConceptPropertyContains(String property,
			String query, ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	//
	// Non interface methods
	//
	/**
	 * @return the ncboOntologyVersionDAO
	 */
	public CustomNcboOntologyVersionDAO getNcboOntologyVersionDAO() {
		return ncboOntologyVersionDAO;
	}

	/**
	 * @param ncboOntologyVersionDAO
	 *            the ncboOntologyVersionDAO to set
	 */
	public void setNcboOntologyVersionDAO(
			CustomNcboOntologyVersionDAO ncboOntologyVersionDAO) {
		this.ncboOntologyVersionDAO = ncboOntologyVersionDAO;
	}

	//
	// Private methods
	//

	/**
	 * Gets the Protege ontology associated with the specified ontology id.
	 */
	private OWLOntology getOntology(int ontologyID) {
		OWLModel owlModel = getOWLModel(ontologyID);
		return owlModel.getDefaultOWLOntology();
	}
	
	/**
	 * Gets the Protege ontology associated with the specified ontology id.
	 */
	private OWLModel getOWLModel(int ontologyID) {
		OWLDatabaseKnowledgeBaseFactory factory = new OWLDatabaseKnowledgeBaseFactory();         
		List errors = new ArrayList();

		Project prj = Project.createNewProject(factory, errors);
		OWLDatabaseKnowledgeBaseFactory.setSources(prj.getSources(), JDBC_DRIVER, DB_URL,
				OntologyLoaderProtegeImpl.getTableName(ontologyID), PROTEGE_USER, PROTEGE_PASSWORD);
		prj.createDomainKnowledgeBase(factory, errors, true);

		OWLModel owlModel = (OWLModel) prj.getKnowledgeBase();
	
		return owlModel;
	}
}
