package org.ncbo.stanford.manager.rdfstore;

import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;

public interface RDFStoreManager {

	/**
	 * Get a repository object. Should not be used except inside an RDF DAO.
	 * 
	 * @return
	 */
	public Repository getRepository();

	/**
	 * Get a connection to a repository. Allows for CRUD operations querying the
	 * underlying store.
	 * 
	 * @return
	 */
	public RepositoryConnection getRepositoryConnection();

	/**
	 * Get an object-based connection to the repository. Acts like a normal
	 * repository connection but also allows you to use AliBaba-based objects
	 * when doing CRUD operations.
	 * 
	 * @return
	 */
	public ObjectConnection getObjectConnection();

	/**
	 * Get a value factory for the repository. Allows for creation of
	 * appropriately typed values (XSD, etc).
	 * 
	 * @return
	 */
	public ValueFactory getValueFactory();

	public void addTriple(Statement statement, String graph) throws RepositoryException,
			Exception;

	public void addTriples(List<Statement> statements, String graph)
			throws RepositoryException, Exception;

	public void deleteTriple(Statement statement) throws RepositoryException,
			Exception;

	public void deleteTriples(List<Statement> statements)
			throws RepositoryException, Exception;

	/**
	 * Cleanup the repository and its stores.
	 */
	public void cleanup();

}
