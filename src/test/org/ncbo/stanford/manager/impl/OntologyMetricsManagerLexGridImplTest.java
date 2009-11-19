package org.ncbo.stanford.manager.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.manager.metrics.impl.OntologyMetricsManagerLexGridImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Pradip Kanjamala
 */
public class OntologyMetricsManagerLexGridImplTest extends
		AbstractBioPortalTest {

	// Test ontology display labels
    //TEST_OWL_DISPLAY_LABEL = "pizza.owl";
    //TEST_OBO_CELL_DISPLAY_LABEL = "cell";
    //TEST_OBO_CELL_OLD_DISPLAY_LABEL = "cell_old";
    //TEST_OBO_DICTYOSTELIUM_DISPLAY_LABEL = "DICTYOSTELIUM_ANATOMY";
    //TEST_OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL = "INFECTIOUS_DISEASE";
    //TEST_LEXGRID_DISPLAY_LABEL = "Automobiles.xml";
    //TEST_UMLS_DISPLAY_LABEL = "AIR";
	
	@Autowired
	OntologyMetricsManagerLexGridImpl metricsManager;

	@Test
	public void testGetOntologyMetricsBean() throws Exception {
		System.out.println("testGetOntologyMetricsBean()");

		
		OntologyBean ncboOntology = metricsManager
				.getLatestNcboOntology(OntologyRetrievalManagerLexGridImplTest.TEST_OBO_DICTYOSTELIUM_DISPLAY_LABEL);
		OntologyMetricsBean omb = metricsManager.extractOntologyMetrics(ncboOntology);
		System.out.println("OBO_DICTYOSTELIUM_ANATOMY");
		System.out.println(omb.toString());

        ncboOntology = metricsManager
                .getLatestNcboOntology(OntologyRetrievalManagerLexGridImplTest.TEST_OBO_CELL_DISPLAY_LABEL);
        omb = metricsManager.extractOntologyMetrics(ncboOntology);
        System.out.println("OBO_CELL");
        System.out.println(omb.toString());
        
        ncboOntology = metricsManager
                .getLatestNcboOntology(OntologyRetrievalManagerLexGridImplTest.TEST_OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL);
        omb = metricsManager.extractOntologyMetrics(ncboOntology);
        System.out.println("OBO_INFECTIOUS_DISEASE");
        System.out.println(omb.toString());
        
        ncboOntology = metricsManager
                .getLatestNcboOntology(OntologyRetrievalManagerLexGridImplTest.TEST_OWL_DISPLAY_LABEL);
        omb = metricsManager.extractOntologyMetrics(ncboOntology);
        System.out.println("OWL_PIZZA");
        System.out.println(omb.toString());
	        
		assertTrue(omb != null);
	}

}
