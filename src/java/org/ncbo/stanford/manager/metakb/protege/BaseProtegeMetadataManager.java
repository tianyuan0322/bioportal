package org.ncbo.stanford.manager.metakb.protege;

import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.metakb.protege.DAO.base.AbstractDAO;
import org.ncbo.stanford.manager.metakb.protege.DAO.base.DAOGroup;

import edu.stanford.smi.protegex.owl.model.OWLModel;

public class BaseProtegeMetadataManager extends AbstractOntologyManagerProtege {
	
	private final DAOGroup daoGroup;
	
	public BaseProtegeMetadataManager() {
		DAOGroup.MetadataKbProvider mkp = new DAOGroup.MetadataKbProvider() {
			public OWLModel getMetadataKb() {
				return BaseProtegeMetadataManager.this.getMetadataKb();
			}
		};
		daoGroup = new DAOGroup(mkp);
	}
	
	protected <DAOType extends AbstractDAO<?>> DAOType
		   getDAO(Class<DAOType> daoType) {
		return daoGroup.getDAO(daoType);
	}
	
	// =========================================================================
	// Metadata KB access
	
	protected OWLModel getMetadataKb() {
		try {
			return getMetadataOWLModel();
		} catch (Exception e) {
			throw new BPRuntimeException("Could not initialize metadata Kb", e);
		}
	}

	

}
