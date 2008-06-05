package org.ncbo.stanford.service.ontology.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyCategoryDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyFileDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyLoadQueueDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyMetadataDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboSeqOntologyIdDAO;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.domain.generated.NcboLCategory;
import org.ncbo.stanford.domain.generated.NcboOntologyCategory;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.domain.generated.NcboOntologyMetadata;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl.CompressedFileHandlerFactory;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.ncbo.stanford.util.ontologyfile.pathhandler.impl.CommonsFileUploadFilePathHandlerImpl;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OntologyServiceImpl implements OntologyService {

	private static final Log log = LogFactory.getLog(OntologyServiceImpl.class);

	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	private CustomNcboOntologyFileDAO ncboOntologyFileDAO;
	private CustomNcboOntologyMetadataDAO ncboOntologyMetadataDAO;
	private CustomNcboOntologyCategoryDAO ncboOntologyCategoryDAO;
	private CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO;
	private CustomNcboSeqOntologyIdDAO ncboSeqOntologyIdDAO;

	/**
	 * Returns a single record for each ontology in the system. If more than one
	 * version of ontology exists, return the latest version.
	 * 
	 * @return list of Ontology beans
	 */
	public List<OntologyBean> findLatestOntologyVersions() {
		ArrayList<OntologyBean> ontBeanList = new ArrayList<OntologyBean>(1);
		List<NcboOntology> ontEntityList = ncboOntologyVersionDAO
				.findLatestOntologyVersions();

		for (NcboOntology ncboOntology : ontEntityList) {
			OntologyBean ontologyBean = new OntologyBean();
			ontologyBean.populateFromEntity(ncboOntology);
			ontBeanList.add(ontologyBean);
		}

		return ontBeanList;
	}

	public List<OntologyBean> searchOntologyMetadata(String query) {
		List<OntologyBean> ontBeanList = new ArrayList<OntologyBean>();
		List<NcboOntology> ontEntityList = ncboOntologyVersionDAO
				.searchOntologyMetadata(query);

		for (NcboOntology ncboOntology : ontEntityList) {
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

		OntologyBean ontologyBean = new OntologyBean();
		ontologyBean.populateFromEntity(ncboOntologyVersionDAO
				.findOntologyVersion(ontologyVersionId));

		return ontologyBean;

	}

	/**
	 * Finds the latest version of a given ontology
	 * 
	 * @param ontologyId
	 * @return
	 */
	public OntologyBean findLatestOntologyVersion(Integer ontologyId) {
		return null;
	}

	/**
	 * Find all versions of the given ontology in the system
	 * 
	 * @param ontologyId
	 * @return
	 */
	public List<OntologyBean> findAllOntologyVersions(Integer ontologyId) {
		return new ArrayList();
	}

	/**
	 * Find ontology properties, such as "definition", "synonyms", "cui"...
	 * 
	 * @param ontologyVersionId
	 * @return
	 */
	public List<String> findProperties(Integer ontologyVersionId) {
		return new ArrayList();
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
	public void createOntology(OntologyBean ontologyBean) throws Exception {

		// assign new Ontology Id for new instance
		generateNextOntologyId(ontologyBean);

		// assign internal version ID
		generateInternalVersionNumber(ontologyBean);

		// create new instances
		NcboOntologyVersion ontologyVersion = new NcboOntologyVersion();
		NcboOntologyMetadata ontologyMetadata = new NcboOntologyMetadata();
		ArrayList<NcboOntologyFile> ontologyFileList = new ArrayList<NcboOntologyFile>();
		ArrayList<NcboOntologyCategory> ontologyCategoryList = new ArrayList<NcboOntologyCategory>();
		NcboOntologyLoadQueue loadQueue = new NcboOntologyLoadQueue();

		// 1. <ontologyVersion> - populate and save : get the new instance with
		// OntologyVersionId populated
		ontologyBean.populateToVersionEntity(ontologyVersion);
		NcboOntologyVersion newOntologyVersion = ncboOntologyVersionDAO
				.saveOntologyVersion(ontologyVersion);

		// 2. <ontologyMetadata> - populate and save
		ontologyBean.populateToMetadataEntity(ontologyMetadata,
				newOntologyVersion);
		ncboOntologyMetadataDAO.save(ontologyMetadata);

		// get the targetFilePath and upload the fileItem
		ontologyBean.setFilePath(ontologyBean.getOntologyDirPath());
		List<String> fileNames = uploadOntologyFileItem(ontologyBean);

		ontologyBean.setFilenames(fileNames);

		// 3. <ontologyCategory> - populate and save
		ontologyBean.populateToCategoryEntity(ontologyCategoryList,
				newOntologyVersion);
		for (NcboOntologyCategory ontologyCategory : ontologyCategoryList) {
			ncboOntologyCategoryDAO.save(ontologyCategory);
		}

		// 4. <ontologyFile> - populate and save
		ontologyBean.populateToFileEntity(ontologyFileList, newOntologyVersion);
		for (NcboOntologyFile ontologyFile : ontologyFileList) {
			ncboOntologyFileDAO.save(ontologyFile);
		}

		// 5. <ontologyQueue> - populate and save
		ontologyBean.populateToLoadQueueEntity(loadQueue, newOntologyVersion);

		ncboOntologyLoadQueueDAO.save(loadQueue);

		// TODO - verify this - do we need this?
		// newOntologyVersion.getNcboOntologyLoadQueues().add(loadQueue);

	}

	/**
	 * Update Ontology. Execute the business logic with the OntologyBean first,
	 * then populate OntologyVersion object
	 * 
	 * @param ontologyBean
	 * @return
	 */
	public void updateOntology(OntologyBean ontologyBean) {

		// get the NcboOntologyVersion instance using OntologyVersionId
		NcboOntologyVersion ontologyVersion = ncboOntologyVersionDAO
				.findById(ontologyBean.getId());

		// get NcboOntologyMetadata instance from ontologyVersion
		// since it is one-to-one, there is only one ontologyMetadata record per
		// ontologyVersion record
		NcboOntologyMetadata ontologyMetadata = (NcboOntologyMetadata) ontologyVersion
				.getNcboOntologyMetadatas().toArray()[0];

		if (ontologyVersion == null || ontologyMetadata == null)
			return;

		// 1. <ontologyVersion> - populate and save
		ontologyBean.populateToVersionEntity(ontologyVersion);
		ncboOntologyVersionDAO.save(ontologyVersion);

		// 2. <ontologyMetadata> - populate and save
		ontologyBean
				.populateToMetadataEntity(ontologyMetadata, ontologyVersion);
		ncboOntologyMetadataDAO.save(ontologyMetadata);

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
	public void cleanupOntologyCategory(OntologyBean ontologyBean) {

		// get the NcboOntologyVersion instance using OntologyVersionId
		NcboOntologyVersion ontologyVersion = ncboOntologyVersionDAO
				.findById(ontologyBean.getId());
		
		Set<NcboOntologyCategory> categories = ontologyVersion.getNcboOntologyCategories();
		for (NcboOntologyCategory ontologyCategory : categories) {
			ncboOntologyCategoryDAO.delete(ontologyCategory);
		}
	}

	public void deleteOntology(OntologyBean ontologyBean) {

		// 1. <ontologyVersion>
		NcboOntologyVersion ontologyVersion = ncboOntologyVersionDAO
				.findById(ontologyBean.getId());

		if (ontologyVersion == null) {
			return;
		}

		// 2. <ontologyMetadata>		
		Set<NcboOntologyMetadata> ontologyMetadataSet = ontologyVersion.getNcboOntologyMetadatas();
		for (NcboOntologyMetadata ontologyMetadata : ontologyMetadataSet) {			
			ncboOntologyMetadataDAO.delete(ontologyMetadata);
		}

		// 3. <ontologyCategory>
		Set<NcboOntologyCategory> ontologyCategorySet = ontologyVersion.getNcboOntologyCategories();
		for (NcboOntologyCategory ontologyCategory : ontologyCategorySet) {			
			ncboOntologyCategoryDAO.delete(ontologyCategory);
		}

		// 4. <ontologyFile>		
		Set<NcboOntologyFile> ontologyFileSet = ontologyVersion.getNcboOntologyFiles();
		for (NcboOntologyFile ontologyFile : ontologyFileSet) {			
			ncboOntologyFileDAO.delete(ontologyFile);
		}

		// 5. <ontologyQueue>
		Set<NcboOntologyLoadQueue> ontologyLoadQueueSet = ontologyVersion.getNcboOntologyLoadQueues();
		for (NcboOntologyLoadQueue ontologyLoadQueue : ontologyLoadQueueSet) {			
			ncboOntologyLoadQueueDAO.delete(ontologyLoadQueue);
		}
		
		// now all the dependency is removed and ontologyVersion can be deleted
		ncboOntologyVersionDAO.delete(ontologyVersion);

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
	 * @return the ncboOntologyMetadataDAO
	 */
	public CustomNcboOntologyMetadataDAO getNcboOntologyMetadataDAO() {
		return ncboOntologyMetadataDAO;
	}

	/**
	 * @param ncboOntologyMetadataDAO
	 *            the ncboOntologyMetadataDAO to set
	 */
	public void setNcboOntologyMetadataDAO(
			CustomNcboOntologyMetadataDAO ncboOntologyMetadataDAO) {
		this.ncboOntologyMetadataDAO = ncboOntologyMetadataDAO;
	}

	/**
	 * @return the ncboSeqOntologyIdDAO
	 */
	public CustomNcboSeqOntologyIdDAO getNcboSeqOntologyIdDAO() {
		return ncboSeqOntologyIdDAO;
	}

	/**
	 * @param ncboSeqOntologyIdDAO
	 *            the ncboSeqOntologyIdDAO to set
	 */
	public void setNcboSeqOntologyIdDAO(
			CustomNcboSeqOntologyIdDAO ncboSeqOntologyIdDAO) {
		this.ncboSeqOntologyIdDAO = ncboSeqOntologyIdDAO;
	}

	/**
	 * Get next Ontology Id from Id Sequence DAO and assign it to ontologyBean.
	 * No effect if Ontology Id already exist.
	 * 
	 * @param OntologyBean
	 *            ontologyBean
	 */
	private void generateNextOntologyId(OntologyBean ontologyBean) {

		Integer ontologyId = ontologyBean.getOntologyId();
		if (ontologyId == null || ontologyId < 1) {
			ontologyBean.setOntologyId(ncboSeqOntologyIdDAO
					.generateNewOntologyId());
		}
	}

	/**
	 * Check internal version ID on ontologyBean. If null - assign 0. If already
	 * exist - increment by 1
	 * 
	 * @param OntologyBean
	 *            ontologyBean
	 */
	private void generateInternalVersionNumber(OntologyBean ontologyBean) {

		if (ontologyBean.getInternalVersionNumber() == null) {
			ontologyBean
					.setInternalVersionNumber(Integer
							.parseInt(MessageUtils
									.getMessage("config.db.ontology.internalVersionNumberStart")));
		} else {
			ontologyBean.setInternalVersionNumber(ontologyBean
					.getInternalVersionNumber() + 1);
		}
	}

	private List<String> uploadOntologyFileItem(OntologyBean ontologyBean)
			throws Exception {

		FileItem fileItem = ontologyBean.getFileItem();
		List<String> fileNames = new ArrayList<String>(1);

		if (fileItem != null) {

			FilePathHandler filePathHandler = new CommonsFileUploadFilePathHandlerImpl(
					CompressedFileHandlerFactory.createFileHandler(ontologyBean
							.getFormat()), fileItem);

			try {
				fileNames = filePathHandler
						.processOntologyFileUpload(ontologyBean);
			} catch (Exception e) {
				// log to error

				log.error("Error in OntologyService:uploadOntologyFileItem()!"
						+ e.getMessage());
				e.printStackTrace();
				throw e;
			}
		}

		return fileNames;
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

}
