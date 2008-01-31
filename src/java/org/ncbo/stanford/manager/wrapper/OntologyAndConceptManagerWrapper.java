package org.ncbo.stanford.manager.wrapper;

import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.exception.InvalidMethodCallException;
import org.ncbo.stanford.manager.OntologyAndConceptManager;

/**
 * An interface designed to provide an abstraction layer to ontology and concept
 * operations. The service layer will consume this interface instead of directly
 * calling a specific implementation (i.e. LexGrid, Protege etc.). This
 * interface should be used by internal services and should not be exposed to
 * upper layers.
 * 
 * @author Michael Dorf
 * 
 */
public abstract class OntologyAndConceptManagerWrapper implements OntologyAndConceptManager {

	
	protected CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	
	
	/**
	 * Placeholder. Should not be implemented or called here
	 * 
	 * @return
	 */
	public List<OntologyBean> findLatestOntologyVersions() {
		throw new InvalidMethodCallException();
	}

	/**
	 * @return the ncboOntologyVersionDAO
	 */
	public CustomNcboOntologyVersionDAO getNcboOntologyVersionDAO() {
		return ncboOntologyVersionDAO;
	}

	/**
	 * @param ncboOntologyVersionDAO the ncboOntologyVersionDAO to set
	 */
	public void setNcboOntologyVersionDAO(
			CustomNcboOntologyVersionDAO ncboOntologyVersionDAO) {
		this.ncboOntologyVersionDAO = ncboOntologyVersionDAO;
	}	
}
