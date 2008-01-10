package org.ncbo.stanford.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.ConceptBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.manager.OntologyAndConceptManager;
import org.ncbo.stanford.service.ontology.impl.OntologyServiceImpl;

/**
 * A default implementation to OntologyAndConceptManager interface designed to
 * provide an abstraction layer to ontology and concept operations. The service
 * layer will consume this interface instead of directly calling a specific
 * implementation (i.e. LexGrid, Protege etc.). Do not use this class directly
 * in upper layers.
 * 
 * NOTE(bdai):  It appears that all ontology and concept methods should be included int this
 * class. However, it may be worth it to refactor into two classes if it becomes
 * too big.
 *  
 * @author Michael Dorf
 * 
 */
public class OntologyAndConceptManagerImpl implements OntologyAndConceptManager {
	private static final Log log = LogFactory.getLog(OntologyAndConceptManagerImpl.class);

	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	
	// TODO:  This could be dynamically set by Spring (if desired).
	private OntologyLoaderProtegeImpl	protegeLoader = null;
//	private OntologyLoaderLexGridImpl	lexGridLoader = null;

	/**
	 * Default Constructor
	 */
	public OntologyAndConceptManagerImpl() {
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
	 * Returns the specified ontology
	 * @param id
	 * @return
	 */
	
	public OntologyBean findOntology(Integer id){
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
		} 
		catch (Exception exc) {
			// This is where one would set the code to indicate a failed ontology load...
			
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
	public ConceptBean findConcept(String id, int ontologyId) {
		return new ConceptBean();
	}

	public ArrayList<ConceptBean> findPathToRoot(String id, int ontologyId) {
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
}
