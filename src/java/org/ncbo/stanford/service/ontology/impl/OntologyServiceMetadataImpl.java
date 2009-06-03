package org.ncbo.stanford.service.ontology.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.CategoryBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyFileDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyLoadQueueDAO;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.exception.InvalidOntologyFormatException;
import org.ncbo.stanford.manager.load.OntologyLoadManager;
import org.ncbo.stanford.manager.metadata.OntologyMetadataManager;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.service.search.IndexSearchService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.ontologyfile.pathhandler.AbstractFilePathHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OntologyServiceMetadataImpl implements OntologyService {

	private static final Log log = LogFactory
			.getLog(OntologyServiceMetadataImpl.class);

	private OntologyMetadataManager ontologyMetadataManager;
	private CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO;
	private CustomNcboOntologyFileDAO ncboOntologyFileDAO;
	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>();
	private Map<String, OntologyLoadManager> ontologyLoadHandlerMap = new HashMap<String, OntologyLoadManager>();
	private IndexSearchService indexService;

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
		ArrayList<NcboOntologyFile> ontologyFileList = new ArrayList<NcboOntologyFile>();
		NcboOntologyLoadQueue loadQueue = new NcboOntologyLoadQueue();

		// assign new Ontology Id for new instance
		// and assign internal version ID
		findOrCreateNcboOntologyRecord(ontologyBean);

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

	public void deleteOntologies(List<Integer> ontologyVersionIds)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
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
		//TODO: remove ontology metadata
		
		
		
		
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
		return ontologyMetadataManager.findLatestActiveOntologyVersions();
	}

	public OntologyBean findLatestActiveOntologyVersion(Integer ontologyId) {
		// TODO Auto-generated method stub
		return null;
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
	 * @param ontologyMetadataManager
	 *            the ontologyMetadataManager to set
	 */
	public void setOntologyMetadataManager(
			OntologyMetadataManager ontologyMetadataManager) {
		this.ontologyMetadataManager = ontologyMetadataManager;
	}

	// Utility methods

	private boolean deleteOntologyFile(OntologyBean ontologyBean) {
		String dirPath = AbstractFilePathHandler
				.getFullOntologyDirPath(ontologyBean);

		return AbstractFilePathHandler.deleteDirectory(new File(dirPath));
	}

	/**
	 * Finds existing or creates new NcboOntology record
	 * 
	 * @param OntologyBean
	 *            ontologyBean
	 */
	private void findOrCreateNcboOntologyRecord(OntologyBean ontologyBean) {
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

	private OntologyLoadManager getLoadManager(OntologyBean ontologyBean)
			throws Exception {
		String formatHandler = ontologyFormatHandlerMap.get(ontologyBean
				.getFormat());
		OntologyLoadManager loadManager = ontologyLoadHandlerMap
				.get(formatHandler);

		if (loadManager == null) {
			log.error("Cannot find formatHandler for "
					+ ontologyBean.getFormat());
			throw new InvalidOntologyFormatException(
					"Cannot find formatHandler for " + ontologyBean.getFormat());
		}

		return loadManager;
	}

	/**
	 * @param indexService
	 *            the indexService to set
	 */
	public void setIndexService(IndexSearchService indexService) {
		this.indexService = indexService;
	}

	/**
	 * @param ncboOntologyFileDAO
	 *            the ncboOntologyFileDAO to set
	 */
	public void setNcboOntologyFileDAO(
			CustomNcboOntologyFileDAO ncboOntologyFileDAO) {
		this.ncboOntologyFileDAO = ncboOntologyFileDAO;
	}

	/**
	 * @param ncboOntologyLoadQueueDAO
	 *            the ncboOntologyLoadQueueDAO to set
	 */
	public void setNcboOntologyLoadQueueDAO(
			CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO) {
		this.ncboOntologyLoadQueueDAO = ncboOntologyLoadQueueDAO;
	}

	/**
	 * @param ontologyFormatHandlerMap
	 *            the ontologyFormatHandlerMap to set
	 */
	public void setOntologyFormatHandlerMap(
			Map<String, String> ontologyFormatHandlerMap) {
		this.ontologyFormatHandlerMap = ontologyFormatHandlerMap;
	}

	/**
	 * @param ontologyLoadHandlerMap
	 *            the ontologyLoadHandlerMap to set
	 */
	public void setOntologyLoadHandlerMap(
			Map<String, OntologyLoadManager> ontologyLoadHandlerMap) {
		this.ontologyLoadHandlerMap = ontologyLoadHandlerMap;
	}
}
