package org.ncbo.stanford.manager.wrapper.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ConceptBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.loader.impl.OntologyLoaderProtegeImpl;
import org.ncbo.stanford.manager.wrapper.OntologyAndConceptManagerWrapper;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSNamedClass;

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
public class OntologyAndConceptManagerWrapperProtegeImpl extends
		OntologyAndConceptManagerWrapper {

	// Hack for development testing
	private final static String TEST_OWL_URI = "test/sample_data/pizza.owl";

	private static final Log log = LogFactory
			.getLog(OntologyAndConceptManagerWrapperProtegeImpl.class);

	private String protegeJdbcUrl;
	private String protegeJdbcDriver;
	private String protegeJdbcUsername;
	private String protegeJdbcPassword;

	private OntologyLoaderProtegeImpl protegeLoader = null;

	/**
	 * Default Constructor
	 */
	public OntologyAndConceptManagerWrapperProtegeImpl() {
		this.protegeLoader = new OntologyLoaderProtegeImpl();
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

	public OntologyBean findOntology(Integer id, String version) {
		return null;
	}

	public List<OntologyBean> findOntologyVersions(Integer id) {
		return new ArrayList();
	}

	public List<String> findProperties(Integer id) {
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
	public ConceptBean getRootConcept(Integer ontologyId) {
		OWLModel owlModel = this.getOWLModel(ontologyId);

		// Get all root nodes associated with this ontology. Then iterate
		// through the collection, returning the first one.
		OWLNamedClass oThing = owlModel.getOWLThingClass();

		if (log.isDebugEnabled())
			log.debug("Searching for root node for ontology id: " + ontologyId);
		/*
		 * OWLNamedClass owlClass = null; for (Iterator it =
		 * oThing.getNamedSubclasses().iterator(); it.hasNext(); ) { owlClass =
		 * (OWLNamedClass)it.next();
		 * 
		 * if (log.isDebugEnabled()) { log.debug("Found root node: " +
		 * owlClass); }
		 * 
		 * break; }
		 */
		if (oThing != null)
			return createConceptBean(oThing, ontologyId);
		else
			return null;
	}

	public ConceptBean findConcept(String conceptId, Integer ontologyId) {
		OWLModel owlModel = this.getOWLModel(ontologyId);

		String conceptName = owlModel.getResourceNameForURI(conceptId);

		if (log.isDebugEnabled()) {
			log.debug("Getting concept id: " + conceptId);
		}

		OWLNamedClass owlClass = owlModel.getOWLNamedClass(conceptName);

		if (owlClass != null)
			return createConceptBean(owlClass, ontologyId);
		else
			return null;
	}

	public ArrayList<ConceptBean> findPathToRoot(String id, Integer ontologyId) {
		/**
		 * OWLModel om = (OWLModel) fileProject.getKnowledgeBase();
		 * 
		 * for (Iterator it = country.getSuperclasses(true).iterator(); it
		 * .hasNext();) { System.out.println("parent> " + it.next()); }
		 */
		return new ArrayList();
	}

	public ConceptBean findParent(String id, Integer ontologyId) {
		return new ConceptBean();
	}

	public ArrayList<ConceptBean> findChildren(String id, Integer ontologyId) {
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
	 * @return the protegeJdbcUrl
	 */
	public String getProtegeJdbcUrl() {
		return protegeJdbcUrl;
	}

	/**
	 * @param protegeJdbcUrl
	 *            the protegeJdbcUrl to set
	 */
	public void setProtegeJdbcUrl(String protegeJdbcUrl) {
		this.protegeJdbcUrl = protegeJdbcUrl;
	}

	/**
	 * @return the protegeJdbcDriver
	 */
	public String getProtegeJdbcDriver() {
		return protegeJdbcDriver;
	}

	/**
	 * @param protegeJdbcDriver
	 *            the protegeJdbcDriver to set
	 */
	public void setProtegeJdbcDriver(String protegeJdbcDriver) {
		this.protegeJdbcDriver = protegeJdbcDriver;
	}

	/**
	 * @return the protegeJdbcUsername
	 */
	public String getProtegeJdbcUsername() {
		return protegeJdbcUsername;
	}

	/**
	 * @param protegeJdbcUsername
	 *            the protegeJdbcUsername to set
	 */
	public void setProtegeJdbcUsername(String protegeJdbcUsername) {
		this.protegeJdbcUsername = protegeJdbcUsername;
	}

	/**
	 * @return the protegeJdbcPassword
	 */
	public String getProtegeJdbcPassword() {
		return protegeJdbcPassword;
	}

	/**
	 * @param protegeJdbcPassword
	 *            the protegeJdbcPassword to set
	 */
	public void setProtegeJdbcPassword(String protegeJdbcPassword) {
		this.protegeJdbcPassword = protegeJdbcPassword;
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
	private OWLModel getOWLModel(int ontologyId) {
		OWLDatabaseKnowledgeBaseFactory factory = new OWLDatabaseKnowledgeBaseFactory();
		List errors = new ArrayList();

		Project prj = Project.createNewProject(factory, errors);
		OWLDatabaseKnowledgeBaseFactory.setSources(prj.getSources(),
				protegeJdbcDriver, protegeJdbcUrl, 
				
				
				
				OntologyLoaderProtegeImpl
						.getTableName(ontologyId), 
						
						
						
						
						
						protegeJdbcUsername,
				protegeJdbcPassword);
		prj.createDomainKnowledgeBase(factory, errors, true);

		OWLModel owlModel = (OWLModel) prj.getKnowledgeBase();

		return owlModel;
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Creates a ConceptBean from a Protege concept class.
	 */
	private static ConceptBean createConceptBean(OWLNamedClass pConcept,
			int ontologyID) {

		// Populate the target concept
		ConceptBean concept = createSimpleConceptBean(pConcept, ontologyID);

		// Copy sub class concepts
		ArrayList<ConceptBean> children = new ArrayList();

		for (Iterator it = pConcept.getNamedSubclasses().iterator(); it
				.hasNext();) {

			// TODO: There must be a more efficient way to filter out all
			// OWLNamedClasses
			DefaultRDFSNamedClass pChild = (DefaultRDFSNamedClass) it.next();

			if (pChild instanceof OWLNamedClass) {

				if (!pChild.isSystem()) {
					children.add(createSimpleConceptBean(
							(OWLNamedClass) pChild, ontologyID));
				}
			}
		}
		concept.setChildren(children);

		// Copy super class concepts;
		// TODO: BioPortal makes a funky assumption that a concept has only one
		// parent; May want to consider this more.
		ArrayList<ConceptBean> parents = new ArrayList();
		for (Iterator it = pConcept.getNamedSuperclasses().iterator(); it
				.hasNext();) {
			OWLNamedClass pParent = (OWLNamedClass) it.next();
			parents.add(createSimpleConceptBean(pParent, ontologyID));
		}
		concept.setParents(parents);

		return concept;
	}

	/**
	 * Only copies the protege class into the protege class. Subclasses and
	 * superclasses are ignored.
	 */
	private static ConceptBean createSimpleConceptBean(OWLNamedClass pConcept,
			int ontologyID) {
		// System.out.println("OWLNamedClass: " + pConcept);
		// System.out.println("icon name: " + pConcept.getIconName());
		// System.out.println("browser text: " + pConcept.getBrowserText());
		// System.out.println("browser text: " + pConcept.getNamespace());
		// System.out.println("browser text: " + pConcept.getNamespacePrefix());

		ConceptBean concept = new ConceptBean();
		concept.setId(pConcept.getName());
		concept.setDisplayLabel(pConcept.getName());
		concept.setOntologyId(ontologyID);

		return concept;
	}

	/**
	 * Creates an OntologyBean from a Protege ontology class.
	 */
	private static OntologyBean createOntologyBean(OWLModel owlModel,
			int ontologyID) {
		OWLOntology pOntology = owlModel.getDefaultOWLOntology();

		OntologyBean ontologyBean = new OntologyBean();
		ontologyBean.setId(Integer.valueOf(ontologyID));
		ontologyBean.setUrn(pOntology.getURI());
		ontologyBean.setDisplayLabel(pOntology.getName());
		ontologyBean.setInternalVersionNumber(Integer.valueOf(1));

		return new OntologyBean();
	}
}
