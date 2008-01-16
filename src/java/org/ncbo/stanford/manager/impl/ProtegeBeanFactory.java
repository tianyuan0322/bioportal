/**
 * 
 */
package org.ncbo.stanford.manager.impl;

import org.ncbo.stanford.manager.BioPortalBeanFactory;
import org.ncbo.stanford.bean.*;

import antlr.collections.List;

import edu.stanford.smi.protegex.owl.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSNamedClass;

/**
 * An abstract class that implements standard methods used for creating NCBO
 * beans (e.g. OntologyBean and ConceptBean) from external repositories (e.g.
 * Protege and LexGrid).
 * 
 * TODO: Consider if this pattern is the best way to decouple the domain of
 * BioPortal beans instances (e.g. ontology bean) from the domain of source
 * repository beans (i.e. Protege and LexGrid).
 * 
 * @author bdai1
 * 
 */
public class ProtegeBeanFactory extends BioPortalBeanFactory {

	/**
	 * Creates an OntologyBean from a Protege ontology class.
	 */
	public static OntologyBean createOntologyBean(OWLModel owlModel, int ontologyID) {
		OWLOntology pOntology = owlModel.getDefaultOWLOntology();
	
		OntologyBean ontologyBean = new OntologyBean();
		ontologyBean.setId(Integer.valueOf(ontologyID));
		ontologyBean.setUrn(pOntology.getURI());
		ontologyBean.setDisplayLabel(pOntology.getName());
		ontologyBean.setInternalVersionNumber(Integer.valueOf(1));

		return new OntologyBean();
	}

	/**
	 * Creates a ConceptBean from a Protege concept class.
	 */
	public static ConceptBean createConceptBean(OWLNamedClass pConcept, int ontologyID) {
		
		
		// Populate the target concept
		ConceptBean concept = createSimpleConceptBean(pConcept, ontologyID);
		
		// Copy sub class concepts
		ArrayList<ConceptBean> children = new ArrayList();
	
		for (Iterator it = pConcept.getNamedSubclasses().iterator(); it
				.hasNext();) {

			// TODO: There must be a more efficient way to filter out all OWLNamedClasses
			DefaultRDFSNamedClass pChild = (DefaultRDFSNamedClass)it.next();

			if (pChild instanceof OWLNamedClass) {
				
				if (!pChild.isSystem()) {
					children.add(createSimpleConceptBean((OWLNamedClass)pChild, ontologyID));
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
			parents.add(createSimpleConceptBean(pParent,ontologyID));
		}
		concept.setParents(parents);
		
		return concept;
	}

	/**
	 * Creates a OntologyMetadata from a Protege meta data class.
	 */
	public static OntologyMetadata createOntologyMetadata() {
		return new OntologyMetadata();
	}

	/**
	 * Creates a collection of ConceptBean from a collection of Protege classes.
	 */
	public static ArrayList<ConceptBean> createConceptBeans(Collection protegeConcepts) {
		return new ArrayList();
	}

	//
	// Private methods
	//
	/**
	 * Only copies the protege class into the protege class. Subclasses and
	 * superclasses are ignored.
	 */
	private static ConceptBean createSimpleConceptBean(OWLNamedClass pConcept,
			int ontologyID) {
//		System.out.println("OWLNamedClass: " + pConcept);
//		System.out.println("icon name: " + pConcept.getIconName());
//		System.out.println("browser  text: " + pConcept.getBrowserText());
//		System.out.println("browser  text: " + pConcept.getNamespace());
//		System.out.println("browser  text: " + pConcept.getNamespacePrefix());
		
		ConceptBean concept = new ConceptBean();
		concept.setId(pConcept.getURI());  // URI is the unique indentifier
		concept.setDisplayLabel(pConcept.getName());
		concept.setOntologyId(ontologyID);

		return concept;
	}
}
