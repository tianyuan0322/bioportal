package org.ncbo.stanford.manager.rdfstore;

import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;

public abstract class AbstractRDFStoreManager implements RDFStoreManager {

	protected Repository repository;
	protected ObjectRepository objectRepository;

	public Repository getRepository() {
		initializeRepository();
		return repository;
	}

	public RepositoryConnection getRepositoryConnection() {
		initializeRepository();

		RepositoryConnection connection = null;
		try {
			connection = repository.getConnection();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

		return connection;
	}

	public ObjectConnection getObjectConnection() {
		initializeRepository();

		// Get an object connection from our existing repository or use existing
		ObjectConnection objectConnection = null;
		try {
			if (objectRepository == null || !objectRepository.isWritable()) {
				ObjectRepositoryFactory objectRepositoryFactory = new ObjectRepositoryFactory();
				objectRepository = objectRepositoryFactory
						.createRepository(repository);
				objectConnection = objectRepository.getConnection();
			} else {
				objectConnection = objectRepository.getConnection();
			}
		} catch (RepositoryConfigException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

		return objectConnection;
	}

	public ValueFactory getValueFactory() {
		initializeRepository();
		return repository.getValueFactory();
	}

	public Boolean isAvailable() {
		Boolean available = false;

		try {
			available = (repository != null && repository.isWritable());
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

		return available;
	}

	public void addTriple(Statement statement, String graph) throws RepositoryException,
			Exception {
		RepositoryConnection con = getRepositoryConnection();
		try {
			con.add(statement, new URIImpl(graph));
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			con.close();
		}
	}

	public void addTriples(List<Statement> statements, String graph)
			throws RepositoryException, Exception {
		RepositoryConnection con = getRepositoryConnection();
		try {
			con.add(statements, new URIImpl(graph));
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			con.close();
		}
	}

	public void deleteTriple(Statement statement) throws RepositoryException,
			Exception {
		RepositoryConnection con = getRepositoryConnection();
		try {
			con.remove(statement);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			con.close();
		}
	}

	public void deleteTriples(List<Statement> statements)
			throws RepositoryException, Exception {
		RepositoryConnection con = getRepositoryConnection();
		try {
			con.remove(statements);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			con.close();
		}
	}

	protected abstract void initializeRepository();

	public abstract void cleanup();

}
