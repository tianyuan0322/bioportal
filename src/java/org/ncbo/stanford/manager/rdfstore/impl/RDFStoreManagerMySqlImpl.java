package org.ncbo.stanford.manager.rdfstore.impl;

import org.ncbo.stanford.manager.rdfstore.AbstractRDFStoreManager;
import org.ncbo.stanford.manager.rdfstore.RDFStoreManager;
import org.ncbo.stanford.util.MessageUtils;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.SailException;
import org.openrdf.sail.rdbms.mysql.MySqlStore;

public class RDFStoreManagerMySqlImpl extends AbstractRDFStoreManager implements
		RDFStoreManager {

	private static MySqlStore mySqlStore;

	@Override
	public void initializeRepository() {
		try {
			if (!isAvailable()) {
				mySqlStore = new MySqlStore();
				mySqlStore.setServerName(MessageUtils
						.getMessage("rdf.mysql.store.server.name"));
				mySqlStore.setPortNumber(Integer.parseInt(MessageUtils
						.getMessage("rdf.mysql.store.port.number")));
				mySqlStore.setDatabaseName(MessageUtils
						.getMessage("rdf.mysql.store.database.name"));
				mySqlStore.setUser(MessageUtils
						.getMessage("rdf.mysql.store.user"));
				mySqlStore.setPassword(MessageUtils
						.getMessage("rdf.mysql.store.password"));

				repository = new SailRepository(mySqlStore);

				repository.initialize();
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup() {
		if (repository != null) {
			try {
				mySqlStore.shutDown();
				mySqlStore = null;

				repository.shutDown();
				repository = null;
			} catch (SailException e) {
				e.printStackTrace();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
	}

}
