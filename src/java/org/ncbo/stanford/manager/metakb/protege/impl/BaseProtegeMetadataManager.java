package org.ncbo.stanford.manager.metakb.protege.impl;

import org.ncbo.stanford.exception.BPRuntimeException;
import org.ncbo.stanford.manager.AbstractOntologyManagerProtege;
import org.ncbo.stanford.manager.metakb.protege.DAL.DALayer;

import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * XXX Document this!
 * Base class for all managers that work with metadata stuff.
 * 
 * @author Tony Loeser
 *
 */
public class BaseProtegeMetadataManager extends AbstractOntologyManagerProtege {
	
	// Seems like there is only one choice here.  But should it be injected
	// by Spring, in case...?
	private final DALayer daLayer;
	
	/**
	 * Simple constructor.
	 */
	public BaseProtegeMetadataManager() {
		DALayer.MetadataKbProvider mkp = new DALayer.MetadataKbProvider() {
			public OWLModel getMetadataKb() {
				return BaseProtegeMetadataManager.this.getMetadataKb();
			}
		};
		daLayer = new DALayer(mkp);
	}
	
	// =========================================================================
	// Simple accessors
	
	protected DALayer getDALayer() {
		return daLayer;
	}

	protected OWLModel getMetadataKb() {
		try {
			return getMetadataOWLModel();
		} catch (Exception e) {
			throw new BPRuntimeException("Could not initialize metadata Kb", e);
		}
	}

}
