package org.ncbo.stanford.sparql.bean;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.annotation.IRI;
import org.ncbo.stanford.manager.rdfstore.impl.RDFStoreManagerVirtuosoImpl;
import org.openrdf.model.Statement;
import org.springframework.beans.factory.annotation.Autowired;

public class SPARQLBeanTest extends AbstractBioPortalTest {

	@Autowired
	RDFStoreManagerVirtuosoImpl rdfStore;

	public abstract class TestBeanLevel2 extends AbstractSPARQLBean {
		private static final long serialVersionUID = 4023019904375170543L;
		@IRI("http://test.com/test_level_2")
		protected String test_level_2 = "testing";
		
		TestBeanLevel2() {
		}
	}
	
	public class TestBeanLevel3 extends TestBeanLevel2 {
		private static final long serialVersionUID = -375829657531331911L;
		@IRI("http://test.com/test_level_3")
		protected String test_level_3 = "testing";
		
		TestBeanLevel3() {
		}
	}
	
	public class TestBeanDifferentPrefix extends AbstractSPARQLBean {
		private static final long serialVersionUID = -7711073500394067998L;
		static final String PREFIX = "http://test.com/prefix#";
		static final String RDF_TYPE = "http://test.com/prefix#TestBean";
		
		TestBeanDifferentPrefix() {
			super(PREFIX, RDF_TYPE);
		}
	}
	
	@Test
	public void testToStatementsWithInheritance() {
		TestBeanLevel3 l3 = new TestBeanLevel3();
		
		ArrayList<Statement> statements = l3.toStatements(rdfStore.getValueFactory());
		
		System.out.println(statements);
		
		// Look for expected triples
		assertTrue(statements.size() == 3);
		assertTrue(statements.toString().contains("http://test.com/test_level_3"));
		assertTrue(statements.toString().contains("http://test.com/test_level_2"));
		assertTrue(statements.toString().contains("http://purl.bioontology.org/bioportal#SPARQL_Bean"));
	}
	
	@Test
	public void testDifferentPrefix() {
		TestBeanDifferentPrefix dp = new TestBeanDifferentPrefix();

		ArrayList<Statement> statements = dp.toStatements(rdfStore.getValueFactory());
		
		System.out.println(statements);
		
		// Look for expected triples
		assertTrue(statements.size() == 1);
		assertTrue(statements.toString().contains("http://test.com/prefix#TestBean"));
		assertTrue(statements.toString().contains("http://test.com/prefix#"));
	}
	
	
}
