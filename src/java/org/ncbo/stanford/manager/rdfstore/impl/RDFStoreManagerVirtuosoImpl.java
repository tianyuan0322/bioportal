package org.ncbo.stanford.manager.rdfstore.impl;

import org.ncbo.stanford.manager.rdfstore.AbstractRDFStoreManager;
import org.ncbo.stanford.manager.rdfstore.RDFStoreManager;
import org.ncbo.stanford.util.MessageUtils;
import org.openrdf.repository.RepositoryException;

import virtuoso.sesame2.driver.VirtuosoRepository;

public class RDFStoreManagerVirtuosoImpl extends AbstractRDFStoreManager
		implements RDFStoreManager {

	@Override
	public void initializeRepository() {
		try {
			if (!isAvailable()) {
				repository = new VirtuosoRepository(MessageUtils
						.getMessage("rdf.virtuoso.connection.string"),
						MessageUtils.getMessage("rdf.virtuoso.user"),
						MessageUtils.getMessage("rdf.virtuoso.password"));
				repository.initialize();
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup() {
		if (repository != null) {
			try {
				repository.shutDown();
				repository = null;
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
