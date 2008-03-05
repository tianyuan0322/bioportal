package org.ncbo.stanford.manager.wrapper.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.manager.wrapper.AbstractOntologyManagerWrapperProtege;
import org.ncbo.stanford.manager.wrapper.OntologyRetrievalManagerWrapper;
import org.ncbo.stanford.util.constants.ApplicationConstants;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLOntology;

/**
 * A default implementation to OntologyRetrievalManagerWrapper interface
 * designed to provide an abstraction layer to ontology and concept retrieval
 * operations. The service layer will consume this interface instead of directly
 * calling a specific implementation (i.e. LexGrid, Protege etc.). Do not use
 * this class directly in upper layers.
 * 
 * 
 * @author Michael Dorf
 */
public class OntologyRetrievalManagerWrapperProtegeImpl extends
		AbstractOntologyManagerWrapperProtege implements
		OntologyRetrievalManagerWrapper {

	// Hack for development testing
	private final static String TEST_OWL_URI = "test/sample_data/pizza.owl.pprj";

	private static final Log log = LogFactory
			.getLog(OntologyRetrievalManagerWrapperProtegeImpl.class);

	/**
	 * Default Constructor
	 */
	public OntologyRetrievalManagerWrapperProtegeImpl() {
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

	//
	// Concept methods
	//

	/**
	 * Get the root concept for the specified ontology.
	 */
	public ClassBean getRootConcept(Integer ontologyId) {
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
		if (oThing != null) {
			return createClassBean(oThing,true);
		}

		return null;
	}

	public ClassBean findConcept(String conceptId, Integer ontologyId) {
		OWLModel owlModel = this.getOWLModel(ontologyId);

		String conceptName = owlModel.getResourceNameForURI(conceptId);

		if (log.isDebugEnabled()) {
			log.debug("Getting concept id: " + conceptId);
		}

		OWLNamedClass owlClass = owlModel.getOWLNamedClass(conceptName);

		if (owlClass != null) {
			return createClassBean(owlClass,true);
		}

		return null;
	}

	public ArrayList<ClassBean> findPathToRoot(String id, Integer ontologyId) {
		/**
		 * OWLModel om = (OWLModel) fileProject.getKnowledgeBase();
		 * 
		 * for (Iterator it = country.getSuperclasses(true).iterator(); it
		 * .hasNext();) { System.out.println("parent> " + it.next()); }
		 */
		return new ArrayList();
	}

	public ClassBean findParent(String id, Integer ontologyId) {
		return new ClassBean();
	}

	public ArrayList<ClassBean> findChildren(String id, Integer ontologyId) {
		return new ArrayList();
	}

	public ArrayList<ClassBean> findConceptNameExact(String query,
			ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ClassBean> findConceptNameStartsWith(String query,
			ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ClassBean> findConceptNameContains(String query,
			ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ClassBean> findConceptPropertyExact(String property,
			String query, ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ClassBean> findConceptPropertyStartsWith(String property,
			String query, ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	public ArrayList<ClassBean> findConceptPropertyContains(String property,
			String query, ArrayList<Integer> ontologyIds) {
		return new ArrayList();
	}

	//
	// Non interface methods
	//

	//
	// Private methods
	//

	/**
	 * Gets the Protege ontology associated with the specified ontology id.
	 */
	private OWLOntology getOntology(int ontologyId) {
		OWLModel owlModel = getOWLModel(ontologyId);
		return owlModel.getDefaultOWLOntology();
	}

	/**
	 * Gets the Protege ontology associated with the specified ontology id.
	 */
	private OWLModel getOWLModel(int ontologyId) {
		OWLDatabaseKnowledgeBaseFactory factory = new OWLDatabaseKnowledgeBaseFactory();
		List errors = new ArrayList();

	/*	Project prj = Project.createNewProject(factory, errors);
		OWLDatabaseKnowledgeBaseFactory.setSources(prj.getSources(),
				protegeJdbcDriver, protegeJdbcUrl, getTableName(ontologyId),
				protegeJdbcUsername, protegeJdbcPassword);
		prj.createDomainKnowledgeBase(factory, errors, true);
	*/
		
		Project prj = Project.loadProjectFromFile(TEST_OWL_URI, new ArrayList());
		
		OWLModel owlModel = (OWLModel) prj.getKnowledgeBase();

		return owlModel;
	}

	// /**
	// * Creates a ConceptBean from a Protege concept class.
	// */
	// private static ConceptBean createConceptBean(OWLNamedClass pConcept,
	// int ontologyId) {
	//
	// // Populate the target concept
	// ConceptBean concept = createSimpleConceptBean(pConcept, ontologyId);
	//
	// // Copy sub class concepts
	// ArrayList<ConceptBean> children = new ArrayList();
	//
	// for (Iterator it = pConcept.getNamedSubclasses().iterator(); it
	// .hasNext();) {
	//
	// // TODO: There must be a more efficient way to filter out all
	// // OWLNamedClasses
	// DefaultRDFSNamedClass pChild = (DefaultRDFSNamedClass) it.next();
	//
	// if (pChild instanceof OWLNamedClass) {
	//
	// if (!pChild.isSystem()) {
	// children.add(createSimpleConceptBean(
	// (OWLNamedClass) pChild, ontologyId));
	// }
	// }
	// }
	// concept.setChildren(children);
	//
	// // Copy super class concepts;
	// // TODO: BioPortal makes a funky assumption that a concept has only one
	// // parent; May want to consider this more.
	// ArrayList<ConceptBean> parents = new ArrayList();
	// for (Iterator it = pConcept.getNamedSuperclasses().iterator(); it
	// .hasNext();) {
	// OWLNamedClass pParent = (OWLNamedClass) it.next();
	// parents.add(createSimpleConceptBean(pParent, ontologyId));
	// }
	// concept.setParents(parents);
	//
	// return concept;
	// }
	//

	
	private Collection<ClassBean> convertClasses(Collection<Cls> clsbeans,boolean recursive){
		
		Collection<ClassBean> beans = new ArrayList<ClassBean>();
		
		for (Cls cls : clsbeans) {
			beans.add(createClassBean(cls,recursive));
		}
		return beans;
		
	}
	
	private ClassBean createClassBean(Cls pConcept,boolean recursive) {
		ClassBean classBean = new ClassBean();
		classBean.setId(pConcept.getName());
		classBean.setLabel(pConcept.getName());

		// add properties
		Collection<Slot> slots = pConcept.getOwnSlots();
		classBean.addRelations(convertProperties(slots));

		
		if(recursive){
			// add subclasses
			// if OWLNamedClass, then use getNamedSubclasses/Superclasses,
			// else use getDirectSubclasses/Superclasses (cast to Collection<Cls>)
			Collection<Cls> subclasses;
			Collection<Cls> superclasses;
	
			if (pConcept instanceof OWLNamedClass) {
				subclasses = ((OWLNamedClass) pConcept).getNamedSubclasses(false);
			} else {
				subclasses = pConcept.getDirectSubclasses();
			}
			
			classBean.addRelation(ApplicationConstants.SUB_CLASS, convertClasses(subclasses,false));
	
			// add superclasses
			if (pConcept instanceof OWLNamedClass) {
				superclasses = ((OWLNamedClass) pConcept)
						.getNamedSuperclasses(false);
			} else {
				superclasses = pConcept.getDirectSuperclasses();
			}
	
			classBean.addRelation(ApplicationConstants.SUPER_CLASS, convertClasses(superclasses,false));

		}
		// add RDF type
		if (pConcept instanceof OWLNamedClass) {
			classBean.addRelation(ApplicationConstants.RDF_TYPE,
					convertClasses(((OWLNamedClass) pConcept).getRDFTypes(),false));
		}

		return classBean;
	}

	
	/**
	 * Converts collection of slots into a string representation of values
	 * 
	 * @param slots
	 * @return
	 */
	private HashMap<String, List<String>> convertProperties(
			Collection<Slot> slots) {
		HashMap<String, List<String>> bpProps = new HashMap<String, List<String>>();
		ArrayList<String> bpPropVals = new ArrayList<String>();

		// add properties
		for (Slot obj : slots) {
			Collection<Object> vals = obj.getOwnSlotValues(obj);

			for (Object val : vals) {
				bpPropVals.add(val.toString());
			}

			bpProps.put(obj.getName(), bpPropVals);
		}

		return bpProps;
	}

	/**
	 * Creates an OntologyBean from a Protege ontology class.
	 */
	private static OntologyBean createOntologyBean(OWLModel owlModel,
			int ontologyId) {
		OWLOntology pOntology = owlModel.getDefaultOWLOntology();

		OntologyBean ontologyBean = new OntologyBean();
		ontologyBean.setId(Integer.valueOf(ontologyId));
		ontologyBean.setUrn(pOntology.getURI());
		ontologyBean.setDisplayLabel(pOntology.getName());
		ontologyBean.setInternalVersionNumber(Integer.valueOf(1));

		return new OntologyBean();
	}
}
