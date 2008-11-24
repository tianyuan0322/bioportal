package org.ncbo.stanford.service.ontology.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.CategoryBean;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboLCategoryDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyCategoryDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyFileDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyLoadQueueDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionMetadataDAO;
import org.ncbo.stanford.domain.custom.entity.VNcboOntology;
import org.ncbo.stanford.domain.generated.NcboLCategory;
import org.ncbo.stanford.domain.generated.NcboOntology;
import org.ncbo.stanford.domain.generated.NcboOntologyCategory;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.domain.generated.NcboOntologyVersionMetadata;
import org.ncbo.stanford.exception.InvalidOntologyFormatException;
import org.ncbo.stanford.manager.load.OntologyLoadManager;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.ontologyfile.pathhandler.AbstractFilePathHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OntologyServiceImpl implements OntologyService {

	private static final Log log = LogFactory.getLog(OntologyServiceImpl.class);

	private CustomNcboOntologyDAO ncboOntologyDAO;
	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	private CustomNcboOntologyFileDAO ncboOntologyFileDAO;
	private CustomNcboOntologyVersionMetadataDAO ncboOntologyVersionMetadataDAO;
	private CustomNcboOntologyCategoryDAO ncboOntologyCategoryDAO;
	private CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO;
	private CustomNcboLCategoryDAO ncboLCategoryDAO;
	private Map<String, String> ontologyFormatHandlerMap = new HashMap<String, String>();
	private Map<String, OntologyLoadManager> ontologyLoadHandlerMap = new HashMap<String, OntologyLoadManager>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ncbo.stanford.service.ontology.OntologyService#findAllCategoryIds(java.lang.String[])
	 */
	@SuppressWarnings("unchecked")
	public List<CategoryBean> findAllCategories() {
		ArrayList<CategoryBean> categoryBeanList = new ArrayList<CategoryBean>(
				1);
		List<NcboLCategory> categoryEntityList = ncboLCategoryDAO.findAll();

		for (NcboLCategory ncboCategory : categoryEntityList) {
			CategoryBean categoryBean = new CategoryBean();
			categoryBean.populateFromEntity(ncboCategory);
			categoryBeanList.add(categoryBean);
		}

		return categoryBeanList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ncbo.stanford.service.ontology.OntologyService#findCategoryIdsByOBOFoundryNames(java.lang.String[])
	 */
	public List<Integer> findCategoryIdsByOBOFoundryNames(
			String[] oboFoundryNames) {
		List<Integer> categoryIds = new ArrayList<Integer>(1);
		List<NcboLCategory> categories = ncboLCategoryDAO
				.findCategoriesByOBOFoundryNames(oboFoundryNames);

		for (NcboLCategory cat : categories) {
			categoryIds.add(cat.getId());
		}

		return categoryIds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ncbo.stanford.service.ontology.OntologyService#findLatestOntologyVersionByOboFoundryId(java.lang.String)
	 */
	public OntologyBean findLatestOntologyVersionByOboFoundryId(
			String oboFoundryId) {
		OntologyBean ontologyBean = null;
		VNcboOntology ncboOntology = ncboOntologyVersionDAO
				.findLatestOntologyVersionByOboFoundryId(oboFoundryId);

		if (ncboOntology != null) {
			ontologyBean = new OntologyBean();
			ontologyBean.populateFromEntity(ncboOntology);
		}

		return ontologyBean;
	}

	/**
	 * Returns a single record for each ontology in the system. If more than one
	 * version of ontology exists, return the latest version.
	 * 
	 * @return list of Ontology beans
	 */
	public List<OntologyBean> findLatestOntologyVersions() {
		ArrayList<OntologyBean> ontBeanList = new ArrayList<OntologyBean>(1);
		List<VNcboOntology> ontEntityList = ncboOntologyVersionDAO
				.findLatestOntologyVersions();

		for (VNcboOntology ncboOntology : ontEntityList) {
			OntologyBean ontologyBean = new OntologyBean();
			ontologyBean.populateFromEntity(ncboOntology);
			ontBeanList.add(ontologyBean);
		}

		return ontBeanList;
	}

	/**
	 * Returns a single record for each ontology which is active in the system.
	 * If more than one version of ontology exists, return the latest and active
	 * version. "active" meaning parse status is "ready".
	 * 
	 * @return list of Ontology beans
	 */
	public List<OntologyBean> findLatestActiveOntologyVersions() {
		ArrayList<OntologyBean> ontBeanList = new ArrayList<OntologyBean>(1);
		List<VNcboOntology> ontEntityList = ncboOntologyVersionDAO
				.findLatestActiveOntologyVersions();

		for (VNcboOntology ncboOntology : ontEntityList) {
			OntologyBean ontologyBean = new OntologyBean();
			ontologyBean.populateFromEntity(ncboOntology);
			ontBeanList.add(ontologyBean);
		}

		return ontBeanList;
	}

	/**
	 * Returns all versions for given ontology. Two steps : 1. Get list of
	 * OntologyVersions to get the list of ontologyVersionId. 2. Get list of
	 * NcboOntology entity from the list of ontVersionId.
	 * 
	 * @return list of Ontology beans
	 */
	@SuppressWarnings("unchecked")
	public List<OntologyBean> findAllOntologyVersionsByOntologyId(
			Integer ontologyId) {
		ArrayList<OntologyBean> ontBeanList = new ArrayList<OntologyBean>(1);

		// 1. get version IDs
		NcboOntology ont = ncboOntologyDAO.findById(ontologyId);
		Set<NcboOntologyVersion> ontVersionList = ont.getNcboOntologyVersions();
		List<Integer> versionIds = new ArrayList<Integer>(1);

		for (NcboOntologyVersion ncboOntologyVersion : ontVersionList) {
			versionIds.add(new Integer(ncboOntologyVersion.getId()));
		}

		// check if the list is empty before executing next query
		if (ontVersionList.size() == 0) {
			return ontBeanList;
		}

		// 2. get NcboOntology entities to get OntologyBeans
		List<VNcboOntology> ontEntityList = ncboOntologyVersionDAO
				.findOntologyVersions(versionIds);

		for (VNcboOntology ncboOntology : ontEntityList) {
			OntologyBean ontologyBean = new OntologyBean();
			ontologyBean.populateFromEntity(ncboOntology);
			ontBeanList.add(ontologyBean);
		}

		return ontBeanList;
	}

	public List<OntologyBean> searchOntologyMetadata(String query) {
		List<OntologyBean> ontBeanList = new ArrayList<OntologyBean>();
		List<VNcboOntology> ontEntityList = ncboOntologyVersionDAO
				.searchOntologyMetadata(query);

		for (VNcboOntology ncboOntology : ontEntityList) {
			OntologyBean ontologyBean = new OntologyBean();
			ontologyBean.populateFromEntity(ncboOntology);
			ontBeanList.add(ontologyBean);
		}

		return ontBeanList;
	}

	/**
	 * Returns a single ontology version record
	 * 
	 * @param ontologyVersionId
	 * @return
	 */
	public OntologyBean findOntology(Integer ontologyVersionId) {
		OntologyBean ontologyBean = null;
		VNcboOntology ncboOntology = ncboOntologyVersionDAO
				.findOntologyVersion(ontologyVersionId);

		if (ncboOntology != null) {
			ontologyBean = new OntologyBean();
			ontologyBean.populateFromEntity(ncboOntology);
		}

		return ontologyBean;
	}

	/**
	 * Finds the latest version of a given ontology
	 * 
	 * @param ontologyId
	 * @return
	 */
	public OntologyBean findLatestOntologyVersion(Integer ontologyId) {
		OntologyBean ontologyBean = null;
		VNcboOntology ncboOntology = ncboOntologyVersionDAO
				.findLatestOntologyVersion(ontologyId);
		if (ncboOntology != null) {
			ontologyBean = new OntologyBean();
			ontologyBean.populateFromEntity(ncboOntology);
		}
		return ontologyBean;
	}

	/**
	 * Find all versions of the given ontology in the system
	 * 
	 * @param ontologyId
	 * @return
	 */
	public List<OntologyBean> findAllOntologyVersions(Integer ontologyId) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Find ontology properties, such as "definition", "synonyms", "cui"...
	 * 
	 * @param ontologyVersionId
	 * @return
	 */
	public List<String> findProperties(Integer ontologyVersionId) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Create Ontology. The instances of related entities such as
	 * NcboOntologyVersion, NcboOntologyMetadata, NcboOntologyFile,
	 * NcboOntologyCategory, NcboOntologyLoadQueue are populated first then
	 * saved.
	 * 
	 * @param ontologyBean
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public void createOntology(OntologyBean ontologyBean,
			FilePathHandler filePathHander) throws Exception {
		// assign new Ontology Id for new instance
		// and assign internal version ID
		findOrCreateNcboOntologyRecord(ontologyBean);

		// set filepath in the bean
		if (!ontologyBean.isRemote()) {
			ontologyBean.setFilePath(ontologyBean.getOntologyDirPath());
		}

		// create new instances
		NcboOntologyVersion ontologyVersion = new NcboOntologyVersion();
		NcboOntologyVersionMetadata ontologyMetadata = new NcboOntologyVersionMetadata();
		ArrayList<NcboOntologyFile> ontologyFileList = new ArrayList<NcboOntologyFile>();
		ArrayList<NcboOntologyCategory> ontologyCategoryList = new ArrayList<NcboOntologyCategory>();
		NcboOntologyLoadQueue loadQueue = new NcboOntologyLoadQueue();

		// 1. <ontologyVersion> - populate and save : get the new instance with
		// OntologyVersionId populated
		ontologyBean.populateToVersionEntity(ontologyVersion);
		NcboOntologyVersion newOntologyVersion = ncboOntologyVersionDAO
				.saveOntologyVersion(ontologyVersion);
		ontologyBean.setId(newOntologyVersion.getId());

		// 2. <ontologyMetadata> - populate and save
		ontologyBean.populateToMetadataEntity(ontologyMetadata,
				newOntologyVersion);
		ncboOntologyVersionMetadataDAO.save(ontologyMetadata);

		// 3. <ontologyCategory> - populate and save
		ontologyBean.populateToCategoryEntity(ontologyCategoryList,
				newOntologyVersion);
		for (NcboOntologyCategory ontologyCategory : ontologyCategoryList) {
			ncboOntologyCategoryDAO.save(ontologyCategory);
		}

		// if remote, do not continue to upload(i.e. ontologyFile and
		// ontologyQueue )
		if (ontologyBean.isRemote()) {
			return;
		}

		// upload the fileItem
		List<String> fileNames = uploadOntologyFile(ontologyBean,
				filePathHander);
		ontologyBean.setFilenames(fileNames);

		// 4. <ontologyFile> - populate and save
		ontologyBean.populateToFileEntity(ontologyFileList, newOntologyVersion);
		for (NcboOntologyFile ontologyFile : ontologyFileList) {
			ncboOntologyFileDAO.save(ontologyFile);
		}

		// 5. <ontologyQueue> - populate and save
		ontologyBean.populateToLoadQueueEntity(loadQueue, newOntologyVersion);
		ncboOntologyLoadQueueDAO.save(loadQueue);
	}

	/**
	 * Update Ontology. Execute the business logic with the OntologyBean first,
	 * then populate OntologyVersion object
	 * 
	 * @param ontologyBean
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateOntology(OntologyBean ontologyBean) {
		// get the NcboOntologyVersion instance using OntologyVersionId
		NcboOntologyVersion ontologyVersion = ncboOntologyVersionDAO
				.findById(ontologyBean.getId());

		// get NcboOntologyMetadata instance from ontologyVersion
		// since it is one-to-one, there is only one ontologyMetadata record per
		// ontologyVersion record
		NcboOntologyVersionMetadata ontologyMetadata = (NcboOntologyVersionMetadata) ontologyVersion
				.getNcboOntologyVersionMetadatas().toArray()[0];

		if (ontologyVersion == null || ontologyMetadata == null)
			return;

		// 1. <ontologyVersion> - populate and save
		ontologyBean.populateToVersionEntity(ontologyVersion);
		ncboOntologyVersionDAO.save(ontologyVersion);

		// 2. <ontologyMetadata> - populate and save
		ontologyBean
				.populateToMetadataEntity(ontologyMetadata, ontologyVersion);
		ncboOntologyVersionMetadataDAO.save(ontologyMetadata);

		// 3. <ontologyCategory> - populate and save
		// remove all corresponding categories associated with given
		// ontologyVersionId
		// and add new categories
		ArrayList<NcboOntologyCategory> ontologyCategoryList = new ArrayList<NcboOntologyCategory>();
		ontologyBean.populateToCategoryEntity(ontologyCategoryList,
				ontologyVersion);

		for (NcboOntologyCategory ontologyCategory : ontologyCategoryList) {
			ncboOntologyCategoryDAO.save(ontologyCategory);
		}
	}

	/**
	 * Update Ontology Category. Execute the business logic with the
	 * OntologyBean first, then populate OntologyVersion object
	 * 
	 * @param ontologyBean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public void cleanupOntologyCategory(OntologyBean ontologyBean) {
		// get the NcboOntologyVersion instance using OntologyVersionId
		NcboOntologyVersion ontologyVersion = ncboOntologyVersionDAO
				.findById(ontologyBean.getId());

		Set<NcboOntologyCategory> categories = ontologyVersion
				.getNcboOntologyCategories();

		for (NcboOntologyCategory ontologyCategory : categories) {
			ncboOntologyCategoryDAO.delete(ontologyCategory);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ncbo.stanford.service.ontology.OntologyService#deleteOntology(org.ncbo.stanford.bean.OntologyBean)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public void deleteOntology(OntologyBean ontologyBean) throws Exception {
		// 1. Remove ontology from the backend
		if (!ontologyBean.isRemote()) {
			getLoadManager(ontologyBean).cleanup(ontologyBean);
			deleteOntologyFile(ontologyBean);
		}

		// 2. <ontologyVersion>
		NcboOntologyVersion ontologyVersion = ncboOntologyVersionDAO
				.findById(ontologyBean.getId());

		if (ontologyVersion == null) {
			return;
		}

		// 3. <ontologyMetadata>
		Set<NcboOntologyVersionMetadata> ontologyMetadataSet = ontologyVersion
				.getNcboOntologyVersionMetadatas();
		for (NcboOntologyVersionMetadata ontologyMetadata : ontologyMetadataSet) {
			ncboOntologyVersionMetadataDAO.delete(ontologyMetadata);
		}

		// 4. <ontologyCategory>
		Set<NcboOntologyCategory> ontologyCategorySet = ontologyVersion
				.getNcboOntologyCategories();
		for (NcboOntologyCategory ontologyCategory : ontologyCategorySet) {
			ncboOntologyCategoryDAO.delete(ontologyCategory);
		}

		// 5. <ontologyFile>
		Set<NcboOntologyFile> ontologyFileSet = ontologyVersion
				.getNcboOntologyFiles();
		for (NcboOntologyFile ontologyFile : ontologyFileSet) {
			ncboOntologyFileDAO.delete(ontologyFile);
		}

		// 6. <ontologyQueue>
		Set<NcboOntologyLoadQueue> ontologyLoadQueueSet = ontologyVersion
				.getNcboOntologyLoadQueues();
		for (NcboOntologyLoadQueue ontologyLoadQueue : ontologyLoadQueueSet) {
			ncboOntologyLoadQueueDAO.delete(ontologyLoadQueue);
		}

		// 7. Now that all dependencies have been removed, delete
		// ontologyVersion
		ncboOntologyVersionDAO.delete(ontologyVersion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ncbo.stanford.service.ontology.OntologyService#getOntologyFile(org.ncbo.stanford.bean.OntologyBean)
	 */
	public File getOntologyFile(OntologyBean ontologyBean) throws Exception {
		List<String> fileNames = ontologyBean.getFilenames();
		File file = null;

		if (fileNames.size() > 0) {
			String fileName = (String) ontologyBean.getFilenames().toArray()[0];
			file = new File(AbstractFilePathHandler.getOntologyFilePath(
					ontologyBean, fileName));
		}

		if (file == null) {
			log.error("Missing ontology file to download.");
			throw new FileNotFoundException("Missing ontology file to load");
		}

		return file;
	}

	private boolean deleteOntologyFile(OntologyBean ontologyBean) {
		String dirPath = AbstractFilePathHandler
				.getFullOntologyDirPath(ontologyBean);

		return AbstractFilePathHandler.deleteDirectory(new File(dirPath));
	}

	/**
	 * @return the ncboOntologyVersionDAO
	 */
	public CustomNcboOntologyVersionDAO getNcboOntologyVersionDAO() {
		return ncboOntologyVersionDAO;
	}

	/**
	 * @param ncboOntologyVersionDAO
	 *            the ncboOntologyVersionDAO to set
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
		NcboOntology ont = null;

		if (ontologyId == null) {
			ont = new NcboOntology();
			ontologyBean
					.setInternalVersionNumber(Integer
							.parseInt(MessageUtils
									.getMessage("config.db.ontology.internalVersionNumberStart")));
		} else {
			ont = ncboOntologyDAO.findById(ontologyId);
			Integer lastInternalVersion = findLatestOntologyVersion(ontologyId)
					.getInternalVersionNumber();
			ontologyBean.setInternalVersionNumber(lastInternalVersion + 1);
		}

		ontologyBean.populateToOntologyEntity(ont);
		NcboOntology ontNew = ncboOntologyDAO.saveOntology(ont);
		ontologyBean.setOntologyId(ontNew.getId());
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
	 * @return the ncboOntologyCategoryDAO
	 */
	public CustomNcboOntologyCategoryDAO getNcboOntologyCategoryDAO() {
		return ncboOntologyCategoryDAO;
	}

	/**
	 * @param ncboOntologyCategoryDAO
	 *            the ncboOntologyCategoryDAO to set
	 */
	public void setNcboOntologyCategoryDAO(
			CustomNcboOntologyCategoryDAO ncboOntologyCategoryDAO) {
		this.ncboOntologyCategoryDAO = ncboOntologyCategoryDAO;
	}

	/**
	 * @return the ncboOntologyFileDAO
	 */
	public CustomNcboOntologyFileDAO getNcboOntologyFileDAO() {
		return ncboOntologyFileDAO;
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
	 * @return the ncboOntologyLoadQueueDAO
	 */
	public CustomNcboOntologyLoadQueueDAO getNcboOntologyLoadQueueDAO() {
		return ncboOntologyLoadQueueDAO;
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
	 * @return the ncboLCategoryDAO
	 */
	public CustomNcboLCategoryDAO getNcboLCategoryDAO() {
		return ncboLCategoryDAO;
	}

	/**
	 * @param ncboLCategoryDAO
	 *            the ncboLCategoryDAO to set
	 */
	public void setNcboLCategoryDAO(CustomNcboLCategoryDAO ncboLCategoryDAO) {
		this.ncboLCategoryDAO = ncboLCategoryDAO;
	}

	/**
	 * @return the ncboOntologyDAO
	 */
	public CustomNcboOntologyDAO getNcboOntologyDAO() {
		return ncboOntologyDAO;
	}

	/**
	 * @param ncboOntologyDAO
	 *            the ncboOntologyDAO to set
	 */
	public void setNcboOntologyDAO(CustomNcboOntologyDAO ncboOntologyDAO) {
		this.ncboOntologyDAO = ncboOntologyDAO;
	}

	/**
	 * @return the ncboOntologyVersionMetadataDAO
	 */
	public CustomNcboOntologyVersionMetadataDAO getNcboOntologyVersionMetadataDAO() {
		return ncboOntologyVersionMetadataDAO;
	}

	/**
	 * @param ncboOntologyVersionMetadataDAO
	 *            the ncboOntologyVersionMetadataDAO to set
	 */
	public void setNcboOntologyVersionMetadataDAO(
			CustomNcboOntologyVersionMetadataDAO ncboOntologyVersionMetadataDAO) {
		this.ncboOntologyVersionMetadataDAO = ncboOntologyVersionMetadataDAO;
	}

	/**
	 * @return the ontologyFormatHandlerMap
	 */
	public Map<String, String> getOntologyFormatHandlerMap() {
		return ontologyFormatHandlerMap;
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
	 * @return the ontologyLoadHandlerMap
	 */
	public Map<String, OntologyLoadManager> getOntologyLoadHandlerMap() {
		return ontologyLoadHandlerMap;
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
