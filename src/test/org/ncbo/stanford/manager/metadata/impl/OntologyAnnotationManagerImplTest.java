package org.ncbo.stanford.manager.metadata.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.ReviewBean;
import org.ncbo.stanford.manager.metadata.OntologyAnnotationManager;
import org.springframework.beans.factory.annotation.Autowired;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;


public class OntologyAnnotationManagerImplTest extends AbstractBioPortalTest {
	
	@Autowired
	OntologyAnnotationManager annMan;
	
	static String REVIEW_CLASS_NAME = "metadata:Review";
	
	
	@Test
	public void testLearnClassNamingConventions() throws Exception {
		OWLModel metadataOntology = ((OntologyAnnotationManagerImpl)annMan).getMetadataOWLModel();
		Iterator classIt = metadataOntology.listOWLNamedClasses();
		for ( ; classIt.hasNext(); ) {
			OWLNamedClass theClass = (OWLNamedClass)classIt.next();
			System.out.println(theClass.getName());
		}
	}
	
	@Test
	public void testRetrieveClasses() throws Exception {
		OWLModel metadataOntology = ((OntologyAnnotationManagerImpl)annMan).getMetadataOWLModel();
		OWLNamedClass reviewClass = metadataOntology.getOWLNamedClass(REVIEW_CLASS_NAME);
		if (reviewClass == null) {
			System.out.println("Could not retrieve class.");
		} else {
			System.out.println("Here it is: "+reviewClass.getName());
		}
	}
	
	@Test
	public void testCreateAndDeleteBasics() throws Exception {
		OWLModel metadataOntology = ((OntologyAnnotationManagerImpl)annMan).getMetadataOWLModel();
		System.out.println("Retrieving class");
		OWLNamedClass ontClass = metadataOntology.getOWLNamedClass(REVIEW_CLASS_NAME);
		System.out.println("The name was: "+ontClass.getName());
		System.out.println("Creating new instance");
		OWLIndividual reviewInd = ontClass.createOWLIndividual(null);
		String name = reviewInd.getName();
		System.out.println("The name was: "+name);
		Integer id = ((OntologyAnnotationManagerImpl)annMan).convertNameToId(name);
		System.out.println("The id was: "+id);
		String nspcInstName = REVIEW_CLASS_NAME + "_" + id;
		OWLIndividual retrInd1 = metadataOntology.getOWLIndividual(nspcInstName);
		System.out.println("Retrieve using "+nspcInstName+": "+retrInd1);
		String fullInstName = ontClass.getName() + "_" + id;
		OWLIndividual retrInd2 = metadataOntology.getOWLIndividual(fullInstName);
		System.out.println("Retrieve using "+fullInstName+": "+retrInd2);
		String bareInstname = "Review_" + id;
		OWLIndividual retrInd3 = metadataOntology.getOWLIndividual(fullInstName);
		System.out.println("Retrieve using "+bareInstname+": "+retrInd3);
		String fullInstname = name;
		OWLIndividual retrInd4 = metadataOntology.getOWLIndividual(fullInstname);
		System.out.println("Retrieve using "+fullInstname+": "+retrInd4);
		String anotherName = ":Review_" + id;
		OWLIndividual retrInd5 = metadataOntology.getOWLIndividual(anotherName);
		System.out.println("Retrieve using "+anotherName+": "+retrInd5);
		System.out.println("Deleting instance");
		reviewInd.delete();
//		System.out.println("Here are the remaining instances:");
//		for (Iterator instIt = ontClass.getInstances(false).iterator(); instIt.hasNext(); ) {
//			OWLIndividual nextInd = (OWLIndividual)instIt.next();
//			System.out.println("     "+nextInd.getName());
//			System.out.println("       Being deleted? "+nextInd.isBeingDeleted());
//			System.out.println("       Is deleted? "+nextInd.isDeleted());
//		}
	}
	
	@Test
	public void testCleanUpInstances() throws Exception {
		OWLModel metadataOntology = ((OntologyAnnotationManagerImpl)annMan).getMetadataOWLModel();
		OWLNamedClass ontClass = metadataOntology.getOWLNamedClass(REVIEW_CLASS_NAME);
		Collection instances = ontClass.getInstances(false);
		for (Iterator instIt = instances.iterator(); instIt.hasNext(); ) {
			OWLIndividual nextInd = (OWLIndividual)instIt.next();
			System.out.println("About to delete individual: "+nextInd.getName());
			nextInd.delete();
		}
	}
	
	@Test
	public void testBasicPropertyActions() throws Exception {
		OWLModel metadataOntology = ((OntologyAnnotationManagerImpl)annMan).getMetadataOWLModel();
		//OWLNamedClass reviewClass = metadataOntology.getOWLNamedClass(REVIEW_CLASS_NAME);
		Object prop = metadataOntology.getOWLProperty("metadata:body");
		System.out.println("Here's the property: "+prop.getClass().getName());
//		for (Iterator propIt = metadataOntology.listRDFProperties(); propIt.hasNext(); ) {
//			RDFProperty theProp = (RDFProperty)propIt.next();
//			System.out.println(theProp.getName());
//		}
	}
	
	@Test
	public void testCreateAndDeleteReview() throws Exception {
		ReviewBean rb = new ReviewBean();
		rb.setBody("This is the body of my first review");
		annMan.saveNewReviewBean(rb);
		System.out.println("Saved new review with ID: "+rb.getId());
		annMan.deleteReview(rb.getId());
		System.out.println("Deleted it!");
	}
}
