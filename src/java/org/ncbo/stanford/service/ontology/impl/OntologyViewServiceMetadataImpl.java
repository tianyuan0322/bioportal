package org.ncbo.stanford.service.ontology.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyViewBean;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.manager.metadata.OntologyViewMetadataManager;
import org.ncbo.stanford.service.ontology.AbstractOntologyService;
import org.ncbo.stanford.service.ontology.OntologyViewService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OntologyViewServiceMetadataImpl extends AbstractOntologyService implements OntologyViewService {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(OntologyViewServiceMetadataImpl.class);

	private OntologyViewMetadataManager ontologyViewMetadataManager;
	
	public void cleanupOntologyViewCategory(OntologyViewBean ontologyViewBean) {
		// This method was created in the original implementation where
		// metadata was stored in databases. 
		// The function of this method was to remove old table entries 
		// representing relations between ontology versions and categories.
		//
		// Since Protege takes care of the consistency of relationships
		// automatically, this method in this class should have nothing to do. 
	}

	@Transactional(rollbackFor = Exception.class)
	public void createOntologyView(OntologyViewBean ontologyBean,
			FilePathHandler filePathHander) throws Exception {
		ArrayList<NcboOntologyFile> ontologyFileList = new ArrayList<NcboOntologyFile>();
		NcboOntologyLoadQueue loadQueue = new NcboOntologyLoadQueue();

		// assign new Ontology Id for new instance
		// and assign internal version ID
		populateInternalVersionNumber(ontologyBean);

		// set filepath in the bean
		if (!ontologyBean.isRemote()) {
			ontologyBean.setFilePath(ontologyBean.getOntologyDirPath());
		}

		// executing actions previously in
		// ontologyBean.populateToVersionEntity(ontologyVersion);
		ontologyBean.updateIfNecessary();

		Integer newVersionId = ontologyViewMetadataManager
				.getNextAvailableOntologyViewVersionId();
		ontologyBean.setId(newVersionId);

		// if remote, do not continue to upload(i.e. ontologyFile and
		// ontologyQueue)
		if (ontologyBean.isRemote()) {
			ontologyViewMetadataManager.saveOntologyView(ontologyBean);
			return;
		}

		// upload the fileItem
		List<String> fileNames = uploadOntologyFile(ontologyBean,
				filePathHander);
		ontologyBean.setFilenames(fileNames);

		// 4. <ontologyFile> - populate and save
		ontologyBean.populateToFileEntity(ontologyFileList);

		for (NcboOntologyFile ontologyFile : ontologyFileList) {
			ncboOntologyFileDAO.save(ontologyFile);
		}

		ontologyViewMetadataManager.saveOntologyView(ontologyBean);

		// 5. <ontologyQueue> - populate and save
		ontologyBean.populateToLoadQueueEntity(loadQueue, newVersionId);
		ncboOntologyLoadQueueDAO.save(loadQueue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ncbo.stanford.service.ontology.OntologyViewService#deleteOntologyViews(java.util.List)
	 */
	public void deleteOntologyViews(List<Integer> ontologyViewVersionIds)
			throws Exception {
		for (Integer ontologyViewVersionId : ontologyViewVersionIds) {
			deleteOntologyView(ontologyViewVersionId);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public void deleteOntologyView(Integer ontologyViewVersionId) throws Exception {
		OntologyViewBean ontologyBean = findOntologyView(ontologyViewVersionId);

		if (ontologyBean == null) {
			return;
		}

		// 1. Remove ontology from the backend
		if (!ontologyBean.isRemote()) {
			getLoadManager(ontologyBean).cleanup(ontologyBean);
			deleteOntologyFile(ontologyBean);
		}
		
		// 2. Remove ontology view metadata
		ontologyViewMetadataManager.deleteOntologyView(ontologyBean);
		
		// 3. Remove ontologyFile records from DB
		List<NcboOntologyFile> ontologyFileSet = ncboOntologyFileDAO.findByOntologyVersionId(ontologyViewVersionId);

		for (NcboOntologyFile ontologyFile : ontologyFileSet) {
			ncboOntologyFileDAO.delete(ontologyFile);
		}

		// 4. Remove ontologyQueue record from DB
		NcboOntologyLoadQueue ontologyLoadQueue = ncboOntologyLoadQueueDAO.findByOntologyVersionId(ontologyViewVersionId);

		if (ontologyLoadQueue != null) {
			ncboOntologyLoadQueueDAO.delete(ontologyLoadQueue);
		}
		
		// 5. Reindex the latest version of ontology (this operation removes
		// this version from the index). Do not backup or optimize the index (it
		// will be backed up and optimized on the next ontology indexing
		// operation).
		indexService.indexOntology(ontologyBean.getOntologyId(), false, false);
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
//		// TODO see if we need this method. If yes, add it also to the OntologyViewService interface
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

	public List<OntologyViewBean> searchOntologyViewMetadata(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateOntologyView(OntologyViewBean ontologyViewBean) throws Exception {
		ontologyViewMetadataManager.updateOntologyView(ontologyViewBean);
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
	 * Finds existing or creates new NcboOntology record
	 * 
	 * @param OntologyBean
	 *            ontologyBean
	 */
	private void populateInternalVersionNumber(OntologyViewBean ontologyBean) {
		Integer ontologyId = ontologyBean.getOntologyId();

		if (ontologyId == null) {
			ontologyBean.setOntologyId(
					ontologyViewMetadataManager.getNextAvailableVirtualViewId());
			ontologyBean
					.setInternalVersionNumber(Integer
							.parseInt(MessageUtils
									.getMessage("config.db.ontology.internalVersionNumberStart")));
		} else {
			Integer lastInternalVersion = findLatestOntologyViewVersion(ontologyId)
					.getInternalVersionNumber();
			ontologyBean.setInternalVersionNumber(lastInternalVersion + 1);
		}
	}
	
}
