package org.ncbo.stanford.service.loader.scheduler.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyLoadQueueDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.domain.generated.NcboLStatus;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.manager.OntologyLoadManager;
import org.ncbo.stanford.service.loader.scheduler.OntologyLoadSchedulerService;

public class OntologyLoadSchedulerServiceImpl implements OntologyLoadSchedulerService {

	private static final Log log = LogFactory.getLog(OntologyLoadSchedulerServiceImpl.class);

	private CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO;
	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	private OntologyLoadManager ontologyLoadManager;
	
	
	public void processOntologyLoad() throws Exception {
		List<NcboOntologyLoadQueue> ontologiesToLoad = 
			ncboOntologyLoadQueueDAO.getOntologiesToLoad();
		
		for (NcboOntologyLoadQueue rec : ontologiesToLoad) {
			NcboOntologyVersion ver = rec.getNcboOntologyVersion();
			String filePath = ver.getFilePath();
			Set<NcboOntologyFile> files = ver.getNcboOntologyFiles();
			NcboOntology ontology = ncboOntologyVersionDAO.findOntologyVersion(ver.getId());
			OntologyBean ontologyBean = new OntologyBean();
			ontologyBean.populateFromEntity(ontology);

			NcboLStatus status = new NcboLStatus();
			status.setId(StatusEnum.STATUS_PARSING.getStatus());
			ver.setNcboLStatus(status);
			rec.setNcboLStatus(status);
			ncboOntologyVersionDAO.save(ver);
			ncboOntologyLoadQueueDAO.save(rec);

			for (NcboOntologyFile file : files) {
				String fullPath = filePath + "/" + file.getFilename();				
				ontologyLoadManager.loadOntology(ontologyBean);
			}

		
		
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
