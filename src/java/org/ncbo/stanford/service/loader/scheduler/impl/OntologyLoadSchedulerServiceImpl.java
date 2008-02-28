package org.ncbo.stanford.service.loader.scheduler.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyLoadQueueDAO;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.manager.OntologyLoadManager;
import org.ncbo.stanford.service.loader.scheduler.OntologyLoadSchedulerService;

public class OntologyLoadSchedulerServiceImpl implements OntologyLoadSchedulerService {

	private static final Log log = LogFactory.getLog(OntologyLoadSchedulerServiceImpl.class);

	private CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO;
	private OntologyLoadManager ontologyLoadManager;
	
	
	public void processOntologyLoad() {
		List<NcboOntologyLoadQueue> ontologiesToLoad = 
			ncboOntologyLoadQueueDAO.getOntologiesToLoad();
		
		for (NcboOntologyLoadQueue rec : ontologiesToLoad) {
			NcboOntologyVersion ver = rec.getNcboOntologyVersion();
			
			
			
		}

	}
	
	/**
	 * @return the ncboOntologyLoadQueueDAO
	 */
	public CustomNcboOntologyLoadQueueDAO getNcboOntologyLoadQueueDAO() {
		return ncboOntologyLoadQueueDAO;
	}

	/**
	 * @param ncboOntologyLoadQueueDAO the ncboOntologyLoadQueueDAO to set
	 */
	public void setNcboOntologyLoadQueueDAO(
			CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO) {
		this.ncboOntologyLoadQueueDAO = ncboOntologyLoadQueueDAO;
	}

	/**
	 * @return the ontologyLoadManager
	 */
	public OntologyLoadManager getOntologyLoadManager() {
		return ontologyLoadManager;
	}

	/**
	 * @param ontologyLoadManager the ontologyLoadManager to set
	 */
	public void setOntologyLoadManager(OntologyLoadManager ontologyLoadManager) {
		this.ontologyLoadManager = ontologyLoadManager;
	}
}
