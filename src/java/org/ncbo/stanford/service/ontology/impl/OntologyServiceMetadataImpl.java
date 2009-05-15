package org.ncbo.stanford.service.ontology.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.CategoryBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyLoadQueueDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;

public class OntologyServiceMetadataImpl implements OntologyService {

	private static final Log log = LogFactory.getLog(OntologyServiceMetadataImpl.class);

	private OntologyMetadataManager ontologyMetadataManager;
	private CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO;
	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	
	public void cleanupOntologyCategory(OntologyBean ontologyBean) {
		// This method was created in the original implementation where
		// metadata was stored in databases. 
		// The function of this method was to remove old table entries 
		// representing relations between ontology versions and categories.
		//
		// Since Protege takes care of the consistency of relationships
		// automatically, this method in this class should have nothing to do. 
	}

	public void createOntology(OntologyBean ontologyBean,
			FilePathHandler filePathHander) throws Exception {
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
//		ArrayList<NcboOntologyFile> ontologyFileList = new ArrayList<NcboOntologyFile>();
//		ArrayList<NcboOntologyCategory> ontologyCategoryList = new ArrayList<NcboOntologyCategory>();
		NcboOntologyLoadQueue loadQueue = new NcboOntologyLoadQueue();

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
		ontologyBean.setId(ontologyMetadataManager.getNextAvailableOntologyVersionId());
		
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
			ontologyMetadataManager.saveOntology(ontologyBean);
			return;
		}

		// upload the fileItem
		List<String> fileNames = uploadOntologyFile(ontologyBean,
				filePathHander);
		ontologyBean.setFilenames(fileNames);

//		// 4. <ontologyFile> - populate and save
//		ontologyBean.populateToFileEntity(ontologyFileList, newOntologyVersion);
//		for (NcboOntologyFile ontologyFile : ontologyFileList) {
//			ncboOntologyFileDAO.save(ontologyFile);
//		}
		
		ontologyMetadataManager.saveOntology(ontologyBean);
/*
		// 5. <ontologyQueue> - populate and save
		ontologyBean.populateToLoadQueueEntity(loadQueue, newOntologyVersion);
		//ontologyBean.populateToLoadQueueEntity2(loadQueue);
		ncboOntologyLoadQueueDAO.save(loadQueue);
*/		
	}

	public void deleteOntologies(List<Integer> ontologyVersionIds)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void deleteOntology(Integer ontologyVersionId) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public List<CategoryBean> findAllCategories() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<OntologyBean> findAllOntologyVersions(Integer ontologyId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<OntologyBean> findAllOntologyVersionsByOntologyId(
			Integer ontologyId) {
		try {
			return ontologyMetadataManager.findAllOntologyVersionsById(ontologyId);
		} catch (Exception e) {
			//TODO see if this is the way we want to deal with exceptions
			e.printStackTrace();
			return null;
		}
	}

	public List<Integer> findCategoryIdsByOBOFoundryNames(
			String[] oboFoundryNames) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<OntologyBean> findLatestActiveOntologyVersions() {
		return ontologyMetadataManager.findLatestActiveOntologyVersions();
	}

	public OntologyBean findLatestOntologyVersion(Integer ontologyId) {
		return ontologyMetadataManager.findLatestOntologyVersionById(ontologyId);
	}

	public OntologyBean findLatestOntologyVersionByOboFoundryId(
			String oboFoundryId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a single record for each ontology in the system. If more than one
	 * version of ontology exists, return the latest version.
	 * 
	 * @return list of Ontology beans
	 */
	public List<OntologyBean> findLatestOntologyVersions() {
		return ontologyMetadataManager.findLatestOntologyVersions();
	}

	/**
	 * Returns a single ontology version record
	 * 
	 * @param ontologyVersionId
	 * @return
	 */
	public OntologyBean findOntology(Integer ontologyVersionId) {
		return ontologyMetadataManager.findOntologyById(ontologyVersionId);
	}

	public List<String> findProperties(Integer ontologyVersionId) {
		// TODO Auto-generated method stub
		return null;
	}

	public File getOntologyFile(OntologyBean ontologyBean) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<OntologyBean> searchOntologyMetadata(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateOntology(OntologyBean ontologyBean) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the ontologyMetadataManager
	 */
	public OntologyMetadataManager getOntologyMetadataManager() {
		return ontologyMetadataManager;
	}

	/**
	 * @param ontologyMetadataManager the ontologyMetadataManager to set
	 */
	public void setOntologyMetadataManager(
			OntologyMetadataManager ontologyMetadataManager) {
		this.ontologyMetadataManager = ontologyMetadataManager;
	}

	
	//utility method

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

	/**
	 * Finds existing or creates new NcboOntology record
	 * 
	 * @param OntologyBean
	 *            ontologyBean
	 */
	private void findOrCreateNcboOntologyRecord(OntologyBean ontologyBean) {
		Integer ontologyId = ontologyBean.getOntologyId();
//		NcboOntology ont = null;

		if (ontologyId == null) {
			//ont = new NcboOntology();
			ontologyBean.setOntologyId(
					ontologyMetadataManager.getNextAvailableOntologyId());
			ontologyBean
					.setInternalVersionNumber(Integer
							.parseInt(MessageUtils
									.getMessage("config.db.ontology.internalVersionNumberStart")));
		} else {
			//ont = ncboOntologyDAO.findById(ontologyId);
			Integer lastInternalVersion = findLatestOntologyVersion(ontologyId)
					.getInternalVersionNumber();
			ontologyBean.setInternalVersionNumber(lastInternalVersion + 1);
		}

//		ontologyBean.populateToOntologyEntity(ont);
//		NcboOntology ontNew = ncboOntologyDAO.saveOntology(ont);
//		ontologyBean.setOntologyId(ontNew.getId());
	}
	

	private List<String> uploadOntologyFile(OntologyBean ontologyBean,
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


}
