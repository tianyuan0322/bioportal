package org.ncbo.stanford.manager.rdfstore;

import org.openrdf.model.ValueFactory;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return available;
	}

	protected abstract void initializeRepository();

	public abstract void cleanup();

}
