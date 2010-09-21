package org.ncbo.stanford.manager.impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.manager.rdfstore.impl.RDFStoreManagerMySqlImpl;
import org.ncbo.stanford.manager.rdfstore.impl.RDFStoreManagerVirtuosoImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test RDF store connectivity.
 */
public class RDFStoreManagerTest extends AbstractBioPortalTest {

	@Autowired
	RDFStoreManagerMySqlImpl mysql;
	@Autowired
	RDFStoreManagerVirtuosoImpl virtuoso;

	@Test
	public void testMySql() {
		String testName = "MySQL";
		
		Repository repo = mysql.getRepository();
		try {
			assertTrue(repo.isWritable());
		} catch (RepositoryException e) {
			System.err.println(testName + ": Problem getting repository");
			e.printStackTrace();
		}
		
		RepositoryConnection con1 = mysql.getRepositoryConnection();
		try {
			assertTrue(con1.isOpen());
		} catch (RepositoryException e) {
			System.err.println(testName + ": Problem getting repository connection");
			e.printStackTrace();
		}
		
		ObjectConnection con2 = mysql.getObjectConnection();
		try {
			assertTrue(con2.isOpen());
		} catch (RepositoryException e) {
			System.err.println(testName + ": Problem getting object connection");
			e.printStackTrace();
		}
		
		mysql.cleanup();
		assertFalse(mysql.isAvailable());
	}

	@Test
	public void testVirtuoso() {
		String testName = "Virtuoso";
		
		Repository repo = virtuoso.getRepository();
		try {
			assertTrue(repo.isWritable());
		} catch (RepositoryException e) {
			System.err.println(testName + ": Problem getting repository");
			e.printStackTrace();
		}
		
		RepositoryConnection con1 = virtuoso.getRepositoryConnection();
		try {
			assertTrue(con1.isOpen());
		} catch (RepositoryException e) {
			System.err.println(testName + ": Problem getting repository connection");
			e.printStackTrace();
		}
		
		ObjectConnection con2 = virtuoso.getObjectConnection();
		try {
			assertTrue(con2.isOpen());
		} catch (RepositoryException e) {
			System.err.println(testName + ": Problem getting object connection");
			e.printStackTrace();
		}

		virtuoso.cleanup();
		assertFalse(virtuoso.isAvailable());
	}
	
	@After
	public void cleanupTests() {
		mysql.cleanup();
		virtuoso.cleanup();
	}

}
