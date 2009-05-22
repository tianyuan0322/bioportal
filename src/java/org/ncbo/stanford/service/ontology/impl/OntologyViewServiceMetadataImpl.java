package org.ncbo.stanford.service.ontology.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyViewBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyFileDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyLoadQueueDAO;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.manager.metadata.OntologyViewMetadataManager;
import org.ncbo.stanford.service.ontology.OntologyViewService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OntologyViewServiceMetadataImpl implements OntologyViewService {

	private static final Log log = LogFactory.getLog(OntologyViewServiceMetadataImpl.class);

	private OntologyViewMetadataManager ontologyViewMetadataManager;
	private CustomNcboOntologyFileDAO ncboOntologyFileDAO;
	private CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO;
	
	public void cleanupOntologyViewCategory(OntologyViewBean ontologyViewBean) {
		// This method was created in the original implementation where
		// metadata was stored in databases. 
		// The function of this method was to remove old table entries 
		// representing relations between ontology versions and categories.
		//
		// Since Protege takes care of the consistency of relationships
		// automatically, this method in this class should have nothing to do. 
	}

	public void createOntologyView(OntologyViewBean ontologyBean,
			FilePathHandler filePathHander) throws Exception {
		ArrayList<NcboOntologyFile> ontologyFileList = new ArrayList<NcboOntologyFile>();
		NcboOntologyLoadQueue loadQueue = new NcboOntologyLoadQueue();

		// assign new Ontology Id for new instance
		// and assign internal version ID
		findOrCreateNcboOntologyRecord(ontologyBean);

		// set filepath in the bean
		if (!ontologyBean.isRemote()) {
			ontologyBean.setFilePath(ontologyBean.getOntologyDirPath());
		}
		
		//executing actions previously in ontologyBean.populateToVersionEntity(ontologyVersion);
		ontologyBean.updateIfNecessary();
		
/*
//		// create new instances
		NcboOntologyVersion ontologyVersion = new NcboOntologyVersion();
//		NcboOntologyVersionMetadata ontologyMetadata = new NcboOntologyVersionMetadata();
//		ArrayList<NcboOntologyCategory> ontologyCategoryList = new ArrayList<NcboOntologyCategory>();

//		// 1. <ontologyVersion> - populate and save : get the new instance with
		// OntologyVersionId populated
		//TODO revise this method
		//HACK TO break the chain of unnecessary entries in all the tables referenced by foreign keys
		int ontologyId = ontologyBean.getOntologyId();
		ontologyBean.setOntologyId(1001);
		ontologyBean.populateToVersionEntity(ontologyVersion);
		ontologyBean.setOntologyId(ontologyId);
		NcboOntologyVersion newOntologyVersion = ncboOntologyVersionDAO
				.saveOntologyVersion(ontologyVersion);
		ontologyBean.setId(newOntologyVersion.getId());
*/		
		ontologyBean.setId(ontologyViewMetadataManager.getNextAvailableOntologyViewVersionId());
		
//		// 2. <ontologyMetadata> - populate and save
//		ontologyBean.populateToMetadataEntity(ontologyMetadata,
//				newOntologyVersion);
//		ncboOntologyVersionMetadataDAO.save(ontologyMetadata);

//		// 3. <ontologyCategory> - populate and save
//		ontologyBean.populateToCategoryEntity(ontologyCategoryList,
//				newOntologyVersion);
//		for (NcboOntologyCategory ontologyCategory : ontologyCategoryList) {
//			ncboOntologyCategoryDAO.save(ontologyCategory);
//		}

		
		// if remote, do not continue to upload(i.e. ontologyFile and
		// ontologyQueue )
		if (ontologyBean.isRemote()) {
			ontologyViewMetadataManager.saveOntologyView(ontologyBean);
			return;
		}

		// upload the fileItem
		List<String> fileNames = uploadOntologyFile(ontologyBean,
				filePathHander);
		ontologyBean.setFilenames(fileNames);

//		// 4. <ontologyFile> - populate and save
		ontologyBean.populateToFileEntity(ontologyFileList);
		
		for (NcboOntologyFile ontologyFile : ontologyFileList) {
			ncboOntologyFileDAO.save(ontologyFile);
		}
		
		ontologyViewMetadataManager.saveOntologyView(ontologyBean);
/*
		// 5. <ontologyQueue> - populate and save
		ontologyBean.populateToLoadQueueEntity(loadQueue, newOntologyVersion);
		//ontologyBean.populateToLoadQueueEntity2(loadQueue);
		ncboOntologyLoadQueueDAO.save(loadQueue);
*/		
	}

	public void deleteOntologyViews(List<Integer> ontologyViewVersionIds)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void deleteOntologyView(Integer ontologyViewVersionId) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public List<OntologyViewBean> findAllOntologyViewVersions(Integer viewId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<OntologyViewBean> findAllOntologyViewVersionsByVirtualViewId(
			Integer viewId) {
		try {
			return ontologyViewMetadataManager.findAllOntologyViewVersionsById(viewId);
		} catch (Exception e) {
			//TODO see if this is the way we want to deal with exceptions
			e.printStackTrace();
			return null;
		}
	}

	public List<OntologyViewBean> findLatestActiveOntologyViewVersions() {
		return ontologyViewMetadataManager.findLatestActiveOntologyViewVersions();
	}

	public OntologyViewBean findLatestOntologyViewVersion(Integer viewId) {
		return ontologyViewMetadataManager.findLatestOntologyViewVersionById(viewId);
	}

//	public OntologyViewBean findLatestOntologyViewVersionByOboFoundryId(
//			String oboFoundryId) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	/**
	 * Returns a single record for each ontology view in the system. If more than one
	 * version of view exists, return the latest version.
	 * 
	 * @return list of Ontology view beans
	 */
	public List<OntologyViewBean> findLatestOntologyViewVersions() {
		return ontologyViewMetadataManager.findLatestOntologyViewVersions();
	}

	/**
	 * Returns a single ontology view version record
	 * 
	 * @param ontologyVersionId
	 * @return
	 */
	public OntologyViewBean findOntologyView(Integer ontologyViewVersionId) {
		return ontologyViewMetadataManager.findOntologyViewById(ontologyViewVersionId);
	}

	public List<String> findProperties(Integer ontologyViewVersionId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<OntologyViewBean> searchOntologyViewMetadata(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateOntologyView(OntologyViewBean ontologyViewBean) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the ontologyViewMetadataManager
	 */
	public OntologyViewMetadataManager getOntologyViewMetadataManager() {
		return ontologyViewMetadataManager;
	}

	/**
	 * @param ontologyViewMetadataManager the ontologyViewMetadataManager to set
	 */
	public void setOntologyViewMetadataManager(
			OntologyViewMetadataManager ontologyViewMetadataManager) {
		this.ontologyViewMetadataManager = ontologyViewMetadataManager;
	}

	
	//utility method

	/**
	 * @param ncboOntologyLoadQueueDAO the ncboOntologyLoadQueueDAO to set
	 */
	public void setNcboOntologyLoadQueueDAO(
			CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO) {
		this.ncboOntologyLoadQueueDAO = ncboOntologyLoadQueueDAO;
	}

	/**
	 * Finds existing or creates new NcboOntology record
	 * 
	 * @param OntologyBean
	 *            ontologyBean
	 */
	private void findOrCreateNcboOntologyRecord(OntologyViewBean ontologyBean) {
		Integer ontologyId = ontologyBean.getOntologyId();
//		NcboOntology ont = null;

		if (ontologyId == null) {
			//ont = new NcboOntology();
			ontologyBean.setOntologyId(
					ontologyViewMetadataManager.getNextAvailableVirtualViewId());
			ontologyBean
					.setInternalVersionNumber(Integer
							.parseInt(MessageUtils
									.getMessage("config.db.ontology.internalVersionNumberStart")));
		} else {
			//ont = ncboOntologyDAO.findById(ontologyId);
			Integer lastInternalVersion = findLatestOntologyViewVersion(ontologyId)
					.getInternalVersionNumber();
			ontologyBean.setInternalVersionNumber(lastInternalVersion + 1);
		}

//		ontologyBean.populateToOntologyEntity(ont);
//		NcboOntology ontNew = ncboOntologyDAO.saveOntology(ont);
//		ontologyBean.setOntologyId(ontNew.getId());
	}
	

	private List<String> uploadOntologyFile(OntologyViewBean ontologyBean,
			FilePathHandler filePathHandler) throws Exception {

		List<String> fileNames = new ArrayList<String>(1);

		if (filePathHandler != null) {
			try {
				fileNames = filePathHandler
						.processOntologyFileUpload(ontologyBean);
			} catch (Exception e) {
				// log to error
				log
						.error("Error in OntologyService:uploadOntologyFile()! - remote file (fileItem) "
								+ e.getMessage());
				e.printStackTrace();
				throw e;
			}
		}
		return fileNames;
	}

	/**
	 * @param ncboOntologyFileDAO the ncboOntologyFileDAO to set
	 */
	public void setNcboOntologyFileDAO(CustomNcboOntologyFileDAO ncboOntologyFileDAO) {
		this.ncboOntologyFileDAO = ncboOntologyFileDAO;
	}
}
