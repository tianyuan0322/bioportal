package org.ncbo.stanford.service.ontology.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.CategoryBean;
import org.ncbo.stanford.bean.GroupBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.OntologyMetricsBean;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.manager.metadata.OntologyCategoryMetadataManager;
import org.ncbo.stanford.manager.metadata.OntologyGroupMetadataManager;
import org.ncbo.stanford.service.ontology.AbstractOntologyService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.CompressionUtils;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.ontologyfile.pathhandler.AbstractFilePathHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OntologyServiceMetadataImpl extends AbstractOntologyService
		implements OntologyService {

	private static final Log log = LogFactory
			.getLog(OntologyServiceMetadataImpl.class);

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
	public void createOntologyOrView(OntologyBean ontologyBean,
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

		Integer newVersionId;
		if (ontologyBean.isView()) {
			newVersionId = ontologyMetadataManager
					.getNextAvailableOntologyViewVersionId();
		} else {
			newVersionId = ontologyMetadataManager
					.getNextAvailableOntologyVersionId();
		}
		ontologyBean.setId(newVersionId);

		// if remote, do not continue to upload(i.e. ontologyFile and
		// ontologyQueue)
		if (ontologyBean.isRemote()) {
			ontologyMetadataManager.saveOntologyOrView(ontologyBean);
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

		ontologyMetadataManager.saveOntologyOrView(ontologyBean);

		// 5. <ontologyQueue> - populate and save
		ontologyBean.populateToLoadQueueEntity(loadQueue, newVersionId);
		ncboOntologyLoadQueueDAO.save(loadQueue);
	}

	/**
	 * Delete/Deprecate several ontologies
	 * 
	 * @param ontologyVersionIds
	 * @param removeMetadata
	 * @param removeOntologyFiles
	 * @return
	 */
	@Transactional(propagation = Propagation.NEVER)
	public void deleteOntologiesOrViews(List<Integer> ontologyVersionIds,
			boolean removeMetadata, boolean removeOntologyFiles) {
		errorOntologies.clear();
		List<Integer> errorVersionIdList = new ArrayList<Integer>(
				ontologyVersionIds);

		for (Integer ontologyVersionId : ontologyVersionIds) {
			try {
				OntologyBean ontologyBean = findOntologyOrView(ontologyVersionId);

				if (ontologyBean == null) {
					continue;
				}

				errorVersionIdList.remove(ontologyVersionId);

				deleteOntologyOrView(ontologyBean, removeMetadata,
						removeOntologyFiles, true);
			} catch (Exception e) {
				addErrorOntology(errorOntologies, ontologyVersionId.toString(),
						null, e.getMessage());
				e.printStackTrace();
				log.error(e);
			}
		}

		for (Integer errorVersionId : errorVersionIdList) {
			String error = addErrorOntology(errorOntologies, errorVersionId
					.toString(), null, ONTOLOGY_VERSION_DOES_NOT_EXIST_ERROR);
			log.error(error);
		}
	}

	/**
	 * Delete/Deprecate a single ontology
	 * 
	 * @param ontologyVersionId
	 * @param removeMetadata
	 * @param removeOntologyFiles
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteOntologyOrView(Integer ontologyVersionId,
			boolean removeMetadata, boolean removeOntologyFiles)
			throws Exception {
		errorOntologies.clear();
		OntologyBean ontologyBean = findOntologyOrView(ontologyVersionId);

		if (ontologyBean == null) {
			String error = addErrorOntology(errorOntologies, ontologyVersionId
					.toString(), null, ONTOLOGY_VERSION_DOES_NOT_EXIST_ERROR);
			log.error(error);
			return;
		}

		deleteOntologyOrView(ontologyBean, removeMetadata, removeOntologyFiles,
				true);
	}

	/**
	 * Delete/Deprecate a single ontology
	 * 
	 * @param ontologyVersionId
	 * @param removeMetadata
	 * @param removeOntologyFiles
	 * @param dummyFlag
	 *            - used just to alter the signature
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void deleteOntologyOrView(OntologyBean ontologyBean,
			boolean removeMetadata, boolean removeOntologyFiles,
			boolean dummyFlag) throws Exception {
		Integer ontologyVersionId = ontologyBean.getId();
		Integer ontologyId = ontologyBean.getOntologyId();
		OntologyBean latestOntologyBean = findLatestActiveOntologyOrViewVersion(ontologyId);

		log
				.info("Deleting ontology: "
						+ getOntologyDisplay(ontologyVersionId.toString(),
								ontologyBean) + ", Remove Metadata: "
						+ removeMetadata + ", Remove Files: "
						+ removeOntologyFiles);

		// 1. Remove ontology from the backend
		if (!ontologyBean.isRemote()) {
			try {
				getLoadManager(ontologyBean).cleanup(ontologyBean);
			} catch (LBParameterException e) {
				String error = addErrorOntology(errorOntologies,
						ontologyVersionId.toString(), ontologyBean, e
								.getMessage());
				log.error(error);
			}

			if (removeOntologyFiles) {
				deleteOntologyFile(ontologyBean);

				// 3. Remove ontologyFile records from DB
				List<NcboOntologyFile> ontologyFileSet = ncboOntologyFileDAO
						.findByOntologyVersionId(ontologyVersionId);

				for (NcboOntologyFile ontologyFile : ontologyFileSet) {
					ncboOntologyFileDAO.delete(ontologyFile);
				}
			}

			// 4. Remove ontologyQueue record from DB
			NcboOntologyLoadQueue ontologyLoadQueue = ncboOntologyLoadQueueDAO
					.findByOntologyVersionId(ontologyVersionId);

			if (ontologyLoadQueue != null) {
				ncboOntologyLoadQueueDAO.delete(ontologyLoadQueue);
			}
		}

		// 2. Remove or "deprecate" ontology metadata
		ontologyMetadataManager.deleteOntologyOrView(ontologyBean,
				removeMetadata);

		// 5. Reindex the latest version of ontology but only if the version we
		// are removing IS the latest. This operation removes
		// this version from the index. Do not backup or optimize the index (it
		// will be backed up and optimized on the next ontology indexing
		// operation).
		if (latestOntologyBean != null
				&& ontologyVersionId.equals(latestOntologyBean.getId())) {
			indexService.indexOntology(ontologyId, false, false);
		}
	}

	public List<CategoryBean> findAllCategories() throws Exception {
		return ontologyCategoryMetadataManager.findAllCategories();
	}

	public List<GroupBean> findAllGroups() throws Exception {
		return ontologyGroupMetadataManager.findAllGroups();
	}

	public List<OntologyBean> findAllOntologyOrViewVersionsByVirtualId(
			Integer ontologyId, boolean excludeDeprecated) throws Exception {
		return ontologyMetadataManager.findAllOntologyOrViewVersionsById(
				ontologyId, excludeDeprecated);
	}

	public List<Integer> findCategoryIdsByOBOFoundryNames(
			String[] oboFoundryNames) throws Exception {
		List<Integer> categoryIds = new ArrayList<Integer>(1);
		List<CategoryBean> categories = ontologyCategoryMetadataManager
				.findCategoriesByOBOFoundryNames(oboFoundryNames);

		for (CategoryBean cat : categories) {
			categoryIds.add(cat.getId());
		}

		return categoryIds;
	}

	public List<OntologyBean> findLatestActiveOntologyVersions()
			throws Exception {
		return ontologyMetadataManager.findLatestActiveOntologyVersions();
	}

	public OntologyBean findLatestActiveOntologyOrViewVersion(Integer ontologyId)
			throws Exception {
		return ontologyMetadataManager
				.findLatestActiveOntologyOrViewVersionById(ontologyId);
	}

	public OntologyBean findLatestOntologyOrViewVersion(Integer ontologyId)
			throws Exception {
		return ontologyMetadataManager
				.findLatestOntologyOrViewVersionById(ontologyId);
	}

	public OntologyBean findLatestOntologyVersionByOboFoundryId(
			String oboFoundryId) throws Exception {
		return ontologyMetadataManager
				.findLatestOntologyVersionByOboFoundryId(oboFoundryId);
	}

	/**
	 * Returns a single record for each ontology in the system. If more than one
	 * version of ontology exists, return the latest version.
	 * 
	 * @return list of Ontology beans
	 */
	public List<OntologyBean> findLatestOntologyVersions() throws Exception {
		return ontologyMetadataManager.findLatestOntologyVersions();
	}

	/**
	 * Returns the list of the latest versions of auto-pulled ontologies.
	 * 
	 * @return the list of ontology beans corresponding to the latest version of
	 *         each ontology
	 */
	public List<OntologyBean> findLatestAutoPulledOntologyVersions()
			throws Exception {
		return ontologyMetadataManager.findLatestAutoPulledOntologyVersions();
	}

	/**
	 * Returns a single ontology version record
	 * 
	 * @param ontologyVersionId
	 * @return
	 * @throws Exception
	 */
	public OntologyBean findOntologyOrView(Integer ontologyVersionId)
			throws Exception {
		return ontologyMetadataManager
				.findOntologyOrViewVersionById(ontologyVersionId);
	}

	public File getOntologyFile(OntologyBean ontologyBean) throws Exception {
		List<String> fileNames = ontologyBean.getFilenames();
		File file = null;

		for (String filename : fileNames) {
			if (CompressionUtils.isCompressed(filename)) {
				file = new File(AbstractFilePathHandler.getOntologyFilePath(
						ontologyBean, filename));
				break;
			}
		}

		if (file == null && !fileNames.isEmpty()) {
			String fileName = (String) ontologyBean.getFilenames().toArray()[0];
			file = new File(AbstractFilePathHandler.getOntologyFilePath(
					ontologyBean, fileName));
		}
		

		if (file == null) {
			String errorMsg = "Missing ontology file to download";
			log.error(errorMsg);
			throw new FileNotFoundException(errorMsg);
		}
		
		return file;
	}
	
	/**
	 * This method find the rdfFile.
	 * If rdf file is available then they return the File otherwise send the errors.
	 * 
	 */
	
	public File findRdfFileForOntology(OntologyBean ontologyBean)
			throws Exception {
		//File name According to OntologyId
		String filename = ontologyBean.getOntologyId() + ".rdf";
		File file = null;
		
		if (CompressionUtils.isCompressed(filename)) {
			file = new File(AbstractFilePathHandler.getRdfFilePath(
					ontologyBean, filename));

		}

		if (file == null && !filename.isEmpty()) {
			//Taking the Rdf File
			file = new File(AbstractFilePathHandler.getRdfFilePath(
					ontologyBean, filename));
		}

		if (file == null) {
			String errorMsg = "do NOT try to generate it";
			log.error(errorMsg);
			throw new FileNotFoundException(errorMsg);
		}

		return file;
	}

	public List<OntologyBean> searchOntologyMetadata(String query,
			boolean includeViews) throws Exception {
		// TODO check if we want separate metadata search for
		// ontologies and view
		return ontologyMetadataManager.searchOntologyMetadata(query,
				includeViews);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateOntologyOrView(OntologyBean ontologyBean)
			throws Exception {
		ontologyMetadataManager.updateOntologyOrView(ontologyBean);
	}

	public OntologyMetricsBean getOntologyMetrics(OntologyBean ontologyBean)
			throws Exception {
		return metricsService.getOntologyMetrics(ontologyBean);
	}

	// ******************** view specific methods ********************

	public List<OntologyBean> findLatestActiveOntologyViewVersions()
			throws Exception {
		return ontologyMetadataManager.findLatestActiveOntologyViewVersions();
	}

	// public OntologyBean findLatestOntologyViewVersionByOboFoundryId(
	// String oboFoundryId) {
	// // TODO see if we need this method. If yes, add it also to the
	// OntologyService interface
	// return null;
	// }

	/**
	 * Returns a single record for each ontology view in the system. If more
	 * than one version of view exists, return the latest version.
	 * 
	 * @return list of Ontology view beans
	 */
	public List<OntologyBean> findLatestOntologyViewVersions() throws Exception {
		return ontologyMetadataManager.findLatestOntologyViewVersions();
	}

	/**
	 * Programmatically reloads the metadata ontology stored in the memory
	 */
	public void reloadMetadataOntology() throws Exception {
		ontologyMetadataManager.reloadMetadataOWLModel();
	}

	// Utility methods

	/**
	 * Finds existing or creates new NcboOntology record
	 * 
	 * @param OntologyBean
	 *            ontologyBean
	 * @throws Exception
	 */
	private void populateInternalVersionNumber(OntologyBean ontologyBean)
			throws Exception {
		Integer ontologyId = ontologyBean.getOntologyId();
		Integer internalVersionNumber = null;

		if (ontologyId == null) {
			if (ontologyBean.isView()) {
				ontologyBean.setOntologyId(ontologyMetadataManager
						.getNextAvailableVirtualViewId());
			} else {
				ontologyBean.setOntologyId(ontologyMetadataManager
						.getNextAvailableOntologyId());
			}

			internalVersionNumber = Integer
					.parseInt(MessageUtils
							.getMessage("config.db.ontology.internalVersionNumberStart"));
		} else {
			OntologyBean ob = findLatestOntologyOrViewVersion(ontologyId);

			if (ob == null) {
				internalVersionNumber = Integer
						.parseInt(MessageUtils
								.getMessage("config.db.ontology.internalVersionNumberStart"));
			} else {
				internalVersionNumber = ob.getInternalVersionNumber() + 1;
			}
		}

		ontologyBean.setInternalVersionNumber(internalVersionNumber);
	}

	/**
	 * @param ontologyCategoryMetadataManager
	 *            the ontologyCategoryMetadataManager to set
	 */
	public void setOntologyCategoryMetadataManager(
			OntologyCategoryMetadataManager ontologyCategoryMetadataManager) {
		this.ontologyCategoryMetadataManager = ontologyCategoryMetadataManager;
	}

	/**
	 * @param ontologyGroupMetadataManager
	 *            the ontologyGroupMetadataManager to set
	 */
	public void setOntologyGroupMetadataManager(
			OntologyGroupMetadataManager ontologyGroupMetadataManager) {
		this.ontologyGroupMetadataManager = ontologyGroupMetadataManager;
	}
}
