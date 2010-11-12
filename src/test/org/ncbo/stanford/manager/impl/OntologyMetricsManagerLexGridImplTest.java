package org.ncbo.stanford.manager.impl;

import static org.junit.Assert.assertTrue;

import java.util.Date;

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

	@Autowired
	OntologyMetricsManagerLexGridImpl metricsManager_;
	OntologyMetricsBean metricsBean_;
	OntologyBean ontologyBean_;
	Date startDate_;
	Date endDate_;

	@Test
	public void testExtractDictyosteliumAnatomyMetricsBean() throws Exception {
		System.out.println("OBO_DICTYOSTELIUM_ANATOMY");
		startDate_ = new Date();

		ontologyBean_ = metricsManager_
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.OBO_DICTYOSTELIUM_DISPLAY_LABEL);
		metricsBean_ = metricsManager_.extractOntologyMetrics(ontologyBean_);

		endDate_ = new Date();
		System.out.println(metricsBean_.toString());
		System.out.println(endDate_.getTime() - startDate_.getTime() + " ms");

		assertTrue(metricsBean_ != null);
	}

	@Test
	public void testExtractCellMetricsBean() throws Exception {
		System.out.println("OBO_CELL");
		startDate_ = new Date();

		ontologyBean_ = metricsManager_
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.OBO_CELL_DISPLAY_LABEL);
		metricsBean_ = metricsManager_.extractOntologyMetrics(ontologyBean_);

		endDate_ = new Date();
		System.out.println(metricsBean_.toString());
		System.out.println(endDate_.getTime() - startDate_.getTime() + " ms");

		assertTrue(metricsBean_ != null);
	}

	@Test
	public void testExtractInfectiousDiseaseMetricsBean() throws Exception {
		System.out.println("OBO_INFECTIOUS_DISEASE");
		startDate_ = new Date();

		ontologyBean_ = metricsManager_
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.OBO_INFECTIOUS_DISEASE_DISPLAY_LABEL);
		metricsBean_ = metricsManager_.extractOntologyMetrics(ontologyBean_);

		endDate_ = new Date();
		System.out.println(metricsBean_.toString());
		System.out.println(endDate_.getTime() - startDate_.getTime() + " ms");

		assertTrue(metricsBean_ != null);
	}

	@Test
	public void testExtractPizzaMetricsBean() throws Exception {
		System.out.println("OWL_PIZZA");
		startDate_ = new Date();

		ontologyBean_ = metricsManager_
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.OWL_PIZZA_DISPLAY_LABEL);
		metricsBean_ = metricsManager_.extractOntologyMetrics(ontologyBean_);

		endDate_ = new Date();
		System.out.println(metricsBean_.toString());
		System.out.println(endDate_.getTime() - startDate_.getTime() + " ms");

		assertTrue(metricsBean_ != null);
	}

	@Test
	public void testExtractCPTMetricsBeanNoHierarchy() throws Exception {
		System.out.println("testExtractCPTMetricsBeanNoHierarchy");
		startDate_ = new Date();

		ontologyBean_ = metricsManager_
				.getLatestOntologyBean(OntologyLoaderLexGridImplTest.UMLS_NOHIERACHY_DISPLAY_LABEL);
		metricsBean_ = metricsManager_.extractOntologyMetrics(ontologyBean_);

		endDate_ = new Date();
		System.out.println(metricsBean_.toString());
		System.out.println(endDate_.getTime() - startDate_.getTime() + " ms");

		assertTrue(metricsBean_ != null);
	}	
}
