package org.ncbo.stanford.manager.impl;

import java.io.File;

import junit.framework.TestCase;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.manager.wrapper.impl.OntologyLoadManagerWrapperLexGridImpl;
import org.ncbo.stanford.util.constants.ApplicationConstants;

/**
 * Tests loading ontologies into LexGrid using the OntologyLoadManagerWrapperLexGridImpl
 * 
 * @author Pradip Kanjamala
 */
public class OntologyLoaderLexGridImplTest extends TestCase {


	// Test ontology URIs
	private final static String TEST_OWL_PATHNAME = "test/sample_data/pizza.owl";
	private final static String TEST_OWL_URN_VERSION = "http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl#|version 1.3";
	private final static String TEST_OBO_PATHNAME = "test/sample_data/cell.obo";
	private final static String TEST_OBO_URN_VERSION = "urn:lsid:bioontology.org:cell|UNASSIGNED";
	private final static String TEST_LEXGRID_XML_PATHNAME = "test/sample_data/Automobiles.xml";
	private final static String TEST_LEXGRID_XML_URN_VERSION = "urn:oid:11.11.0.1|1.0";
	private final static String TEST_UMLS_PATHNAME = "test/sample_data/sampleUMLS-AIR";
	private final static String TEST_UMLS_URN_VERSION = "urn:oid:2.16.840.1.113883.6.110|1993.bvt";

	OntologyLoadManagerWrapperLexGridImpl loadManagerLexGrid= new OntologyLoadManagerWrapperLexGridImpl();
   
 
   public void testLoadObo() throws Exception
   {
       OntologyBean ontology_bean = new OntologyBean();
       ontology_bean.setFormat(ApplicationConstants.FORMAT_OBO);
       ontology_bean.setUrn(TEST_OBO_URN_VERSION);
       loadManagerLexGrid.loadOntology(new File(TEST_OBO_PATHNAME).toURI(), ontology_bean);
       assertTrue(ontology_bean.getUrn()!= null);
   }    
   
 
   
   public void testLoadGenericOwl() throws  Exception
   {
      OntologyBean ontology_bean = new OntologyBean();
      ontology_bean.setFormat(ApplicationConstants.FORMAT_OWL_DL);
      ontology_bean.setUrn(TEST_OWL_URN_VERSION);
      loadManagerLexGrid.loadOntology(new File(TEST_OWL_PATHNAME).toURI(), ontology_bean);
      assertTrue(ontology_bean.getUrn()!= null);

   }
   
 
   public void testLoadLexGridXML() throws  Exception
   {
      OntologyBean ontology_bean = new OntologyBean();
      ontology_bean.setFormat(ApplicationConstants.FORMAT_LEXGRID_XML);
      ontology_bean.setUrn(TEST_LEXGRID_XML_URN_VERSION);
      loadManagerLexGrid.loadOntology(new File(TEST_LEXGRID_XML_PATHNAME).toURI(), ontology_bean);
      assertTrue(ontology_bean.getUrn()!= null);

   }
   
   public void testLoadUMLS() throws Exception
   {
         OntologyBean ontology_bean = new OntologyBean();
         ontology_bean.setFormat(ApplicationConstants.FORMAT_UMLS_RRF);
         ontology_bean.setUrn(TEST_UMLS_URN_VERSION);
         loadManagerLexGrid.setTargetTerminologies("AIR");
         loadManagerLexGrid.loadOntology(new File(TEST_UMLS_PATHNAME).toURI(), ontology_bean);
         assertTrue(ontology_bean.getUrn()!= null);

   }


}
