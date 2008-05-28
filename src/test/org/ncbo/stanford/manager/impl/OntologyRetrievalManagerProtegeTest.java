package org.ncbo.stanford.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.search.SearchResultBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.manager.OntologyRetrievalManager;
import org.ncbo.stanford.util.constants.ApplicationConstants;

/**
 * Tests loading ontologies into Protege using the OntologyLoaderProtegeImpl
 * 
 * @author Benjamin Dai
 */
public class OntologyRetrievalManagerProtegeTest extends AbstractBioPortalTest {
	private final static int TEST_ONT_ID = 15831;
	private final static String TEST_CONCEPT_ID = "http://www.w3.org/2002/07/owl#Class";
	private final static String TEST_CONCEPT_NAME = "http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl#Pizza";

	
	
	public void testSearch() throws Exception{
		OntologyRetrievalManager ocMgr = (OntologyRetrievalManager) applicationContext
		.getBean("ontologyRetrievalManagerProtege",
				OntologyRetrievalManager.class);
		
		 CustomNcboOntologyVersionDAO ncboOntologyVersionDAO=(CustomNcboOntologyVersionDAO) applicationContext
			.getBean("NcboOntologyVersionDAO",
					CustomNcboOntologyVersionDAO.class);
		
		 NcboOntology version = ncboOntologyVersionDAO.findOntologyVersion(TEST_ONT_ID);
		 ArrayList<NcboOntology> versions = new ArrayList<NcboOntology>();
		 versions.add(version);
		 
		 List<SearchResultBean> results = ocMgr.findConceptNameContains(versions,"*Pizza*",true,100);
	System.out.println("Size:" + results.size());

	for (SearchResultBean result : results) {
		for(ClassBean item : result.getNames()){
			System.out.println(item.getId() + " : "+item.getLabel());
		}
	}
		
	}
	
	public void testPathToRoot() throws Exception{
		OntologyRetrievalManager ocMgr = (OntologyRetrievalManager) applicationContext
				.getBean("ontologyRetrievalManagerProtege",
						OntologyRetrievalManager.class);

 CustomNcboOntologyVersionDAO ncboOntologyVersionDAO=(CustomNcboOntologyVersionDAO) applicationContext
	.getBean("NcboOntologyVersionDAO",
			CustomNcboOntologyVersionDAO.class);

NcboOntology version = ncboOntologyVersionDAO.findOntologyVersion(TEST_ONT_ID);
ClassBean conceptBean = ocMgr.findPathToRoot( version,"SpicyPizza");

System.out.println("Path");

// outputConcept(conceptBean);

System.out.println("Subclasses");
ClassBean subclass = (ClassBean) conceptBean.getRelations().get(ApplicationConstants.SUB_CLASS);

while (subclass!=null){
		System.out.println(subclass.getLabel() + " : "+subclass.getId());
		subclass = (ClassBean) subclass.getRelations().get(ApplicationConstants.SUB_CLASS);
}
	}
	
	
	public void testGetRootNode() throws Exception {
		OntologyRetrievalManager ocMgr = (OntologyRetrievalManager) applicationContext
				.getBean("ontologyRetrievalManagerProtege",
						OntologyRetrievalManager.class);

		 CustomNcboOntologyVersionDAO ncboOntologyVersionDAO=(CustomNcboOntologyVersionDAO) applicationContext
			.getBean("NcboOntologyVersionDAO",
					CustomNcboOntologyVersionDAO.class);
		
		NcboOntology version = ncboOntologyVersionDAO.findOntologyVersion(TEST_ONT_ID);
		ClassBean conceptBean = ocMgr.findRootConcept(version);

		System.out.println("ROOT");

		// outputConcept(conceptBean);

		System.out.println("Subclasses");
		ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) conceptBean
				.getRelations().get(ApplicationConstants.SUB_CLASS);
		System.out.println("Size:" + subclasses.size());

		for (ClassBean subclass : subclasses) {
			System.out.println(subclass.getLabel() + " " + subclass.getId());
		}
	}

	public void PizzaConcept() throws Exception {
		System.out.println("Starting testGetConcept");
		OntologyRetrievalManager ocMgr = (OntologyRetrievalManager) applicationContext
				.getBean("ontologyRetrievalManagerProtege",
						OntologyRetrievalManager.class);
		ClassBean conceptBean = null ; //ocMgr.findConcept(TEST_ONT_ID, TEST_CONCEPT_NAME);

		outputConcept(conceptBean);

		System.out.println("Subclasses");
		ArrayList<ClassBean> subclasses = (ArrayList<ClassBean>) conceptBean
				.getRelations().get(ApplicationConstants.SUB_CLASS);
		System.out.println("Size:" + subclasses.size());
		for (ClassBean subclass : subclasses) {
			System.out.println(subclass.getLabel() + " " + subclass.getId());
		}

	}

	public void CheeseyVegetablePizzaConcept() throws Exception {
		System.out.println("Starting cheesyvegetablepizza concept");
		OntologyRetrievalManager ocMgr = (OntologyRetrievalManager) applicationContext
				.getBean("ontologyRetrievalManagerProtege",
						OntologyRetrievalManager.class);
		ClassBean classBean = null; //ocMgr.findConcept(TEST_ONT_ID, "CheeseyVegetableTopping");

		outputConcept(classBean);

	}

	//
	// Private methods
	//
	/**
	 * Output concept content for debugging. Potential helper method to be added
	 * to ConceptBean itself or in the helper package.
	 */
	private void outputConcept(ClassBean classBean) {
		System.out.println(classBean);
	}
}
