package org.ncbo.stanford.service.ontology.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ncbo.stanford.bean.CategoryBean;
import org.ncbo.stanford.bean.GroupBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.manager.metadata.OntologyCategoryMetadataManager;
import org.ncbo.stanford.manager.metadata.OntologyGroupMetadataManager;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.service.ontology.AbstractOntologyService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OntologyServiceMetadataImpl extends AbstractOntologyService implements OntologyService {

	private OntologyMetadataManager ontologyMetadataManager;
	private OntologyCategoryMetadataManager ontologyCategoryMetadataManager;
	private OntologyGroupMetadataManager ontologyGroupMetadataManager;

	public void cleanupOntologyCategory(OntologyBean ontologyBean) {
		// This method was created in the original implementation where
		// metadata was stored in databases.
		// The function of this method was to remove old table entries
		// representing relations between ontology versions and categories.
		//
		// Since Protege takes care of the consistency of relationships
		// automatically, this method in this class should have nothing to do.
	}

	@Transactional(rollbackFor = Exception.class)
	public void createOntology(OntologyBean ontologyBean,
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

		Integer newVersionId = ontologyMetadataManager
				.getNextAvailableOntologyVersionId();
		ontologyBean.setId(newVersionId);

		// if remote, do not continue to upload(i.e. ontologyFile and
		// ontologyQueue)
		if (ontologyBean.isRemote()) {
			ontologyMetadataManager.saveOntology(ontologyBean);
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

		ontologyMetadataManager.saveOntology(ontologyBean);

		// 5. <ontologyQueue> - populate and save
		ontologyBean.populateToLoadQueueEntity(loadQueue, newVersionId);
		ncboOntologyLoadQueueDAO.save(loadQueue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ncbo.stanford.service.ontology.OntologyService#deleteOntologies(java.util.List)
	 */
	public void deleteOntologies(List<Integer> ontologyVersionIds)
			throws Exception {
		for (Integer ontologyVersionId : ontologyVersionIds) {
			deleteOntology(ontologyVersionId);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public void deleteOntology(Integer ontologyVersionId) throws Exception {
		OntologyBean ontologyBean = findOntology(ontologyVersionId);

		if (ontologyBean == null) {
			return;
		}

		// 1. Remove ontology from the backend
		if (!ontologyBean.isRemote()) {
			getLoadManager(ontologyBean).cleanup(ontologyBean);
			deleteOntologyFile(ontologyBean);
		}
		
		// 2. Remove ontology metadata
		ontologyMetadataManager.deleteOntology(ontologyBean);
		
		// 3. Remove ontologyFile records from DB
		List<NcboOntologyFile> ontologyFileSet = ncboOntologyFileDAO.findByOntologyVersionId(ontologyVersionId);

		for (NcboOntologyFile ontologyFile : ontologyFileSet) {
			ncboOntologyFileDAO.delete(ontologyFile);
		}

		// 4. Remove ontologyQueue record from DB
		NcboOntologyLoadQueue ontologyLoadQueue = ncboOntologyLoadQueueDAO.findByOntologyVersionId(ontologyVersionId);

		if (ontologyLoadQueue != null) {
			ncboOntologyLoadQueueDAO.delete(ontologyLoadQueue);
		}
		
		// 5. Reindex the latest version of ontology (this operation removes
		// this version from the index). Do not backup or optimize the index (it
		// will be backed up and optimized on the next ontology indexing
		// operation).
		indexService.indexOntology(ontologyBean.getOntologyId(), false, false);
	}

	public List<CategoryBean> findAllCategories() {
		return ontologyCategoryMetadataManager.findAllCategories();
	}

	public List<GroupBean> findAllGroups() {
		return ontologyGroupMetadataManager.findAllGroups();
	}
	
	public List<OntologyBean> findAllOntologyVersionsByOntologyId(
			Integer ontologyId) {
		try {
			return ontologyMetadataManager
					.findAllOntologyVersionsById(ontologyId);
		} catch (Exception e) {
			// TODO see if this is the way we want to deal with exceptions
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
		return ontologyMetadataManager
				.findLatestActiveOntologyVersions();
	}

	public OntologyBean findLatestActiveOntologyVersion(Integer ontologyId) {
		return ontologyMetadataManager
				.findLatestActiveOntologyVersionById(ontologyId);
	}

	public OntologyBean findLatestOntologyVersion(Integer ontologyId) {
		return ontologyMetadataManager
				.findLatestOntologyVersionById(ontologyId);
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

	public File getOntologyFile(OntologyBean ontologyBean) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<OntologyBean> searchOntologyMetadata(String query) {
		// TODO check if we want separate metadata search for
		// ontologies and view
		return ontologyMetadataManager.searchOntologyMetadata(query);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateOntology(OntologyBean ontologyBean) throws Exception {
		ontologyMetadataManager.updateOntology(ontologyBean);
	}

	// Utility methods

	/**
	 * Finds existing or creates new NcboOntology record
	 * 
	 * @param OntologyBean
	 *            ontologyBean
	 */
	private void populateInternalVersionNumber(OntologyBean ontologyBean) {
		Integer ontologyId = ontologyBean.getOntologyId();

		if (ontologyId == null) {
			ontologyBean.setOntologyId(ontologyMetadataManager
					.getNextAvailableOntologyId());
			ontologyBean
					.setInternalVersionNumber(Integer
							.parseInt(MessageUtils
									.getMessage("config.db.ontology.internalVersionNumberStart")));
		} else {
			Integer lastInternalVersion = findLatestOntologyVersion(ontologyId)
					.getInternalVersionNumber();
			ontologyBean.setInternalVersionNumber(lastInternalVersion + 1);
		}
	}

	/**
	 * @param ontologyMetadataManager
	 *            the ontologyMetadataManager to set
	 */
	public void setOntologyMetadataManager(
			OntologyMetadataManager ontologyMetadataManager) {
		this.ontologyMetadataManager = ontologyMetadataManager;
	}

	/**
	 * @param ontologyCategoryMetadataManager
	 *            the ontologyCategoryMetadataManager to set
	 */
	public void setOntologyCategoryMetadataManager(
			OntologyCategoryMetadataManager ontologyCategoryMetadataManager) {
		this.ontologyCategoryMetadataManager = ontologyCategoryMetadataManager;
	}
}
