package org.ncbo.stanford.service.ontology.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyCategoryDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyFileDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyMetadataDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboSeqOntologyIdDAO;
import org.ncbo.stanford.domain.custom.entity.NcboOntology;
import org.ncbo.stanford.domain.generated.NcboLCategory;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboOntologyCategory;
import org.ncbo.stanford.domain.generated.NcboOntologyMetadata;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.util.filehandler.FileHandler;
import org.ncbo.stanford.util.filehandler.impl.PhysicalDirectoryFileHandler;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OntologyServiceImpl implements OntologyService {

	private static final Log log = LogFactory.getLog(OntologyServiceImpl.class);

	private CustomNcboOntologyCategoryDAO ncboOntologyCategoryDAO;
	private CustomNcboOntologyFileDAO ncboOntologyFileDAO;
	private CustomNcboOntologyMetadataDAO ncboOntologyMetadataDAO;
	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
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

	/**
	 * Returns a single ontology version record
	 * 
	 * @param ontologyVersionId
	 * @return
	 */
	public OntologyBean findOntology(Integer ontologyVersionId) {

		NcboOntology ontology = ncboOntologyVersionDAO
				.findOntologyVersion(ontologyVersionId);
		OntologyBean ontologyBean = new OntologyBean();
		ontologyBean.populateFromEntity(ontology);

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

	public void createOntology(OntologyBean ontologyBean) {

		// TODO
		/*
		 * move this to processorService later
		 * 
		 * e.g. processorService.create()
		 */
		// assign new Ontology Id for new instance
		generateNextOntologyId(ontologyBean);

		// assign internal version ID
		generateInternalVersionNumber(ontologyBean);

		// obsolete - moved inside uploadOntologyFile()
		// assign output file path
		// generateOutputFilePath(ontologyBean);

		// upload ontology file
		uploadOntologyFile(ontologyBean);

		// create new instances
		NcboOntologyVersion ontologyVersion = new NcboOntologyVersion();
		NcboOntologyMetadata ontologyMetadata = new NcboOntologyMetadata();
		ArrayList<NcboOntologyFile> ontologyFileList = new ArrayList<NcboOntologyFile>();
		ArrayList<NcboOntologyCategory> ontologyCategoryList = new ArrayList<NcboOntologyCategory>();

		// 1. <ontologyVersion> - populate and save : get the new instance with
		// OntologyVersionId populated
		ontologyBean.populateToVersionEntity(ontologyVersion);
		NcboOntologyVersion newOntologyVersion = ncboOntologyVersionDAO
				.saveOntologyVersion(ontologyVersion);

		// 2. ontologyMetadata - populate and save
		ontologyBean.populateToMetadataEntity(ontologyMetadata,
				newOntologyVersion);
		ncboOntologyMetadataDAO.save(ontologyMetadata);

		// 3. <ontologyFile> - populate and save
		ontologyBean.populateToFileEntity(ontologyFileList, newOntologyVersion);
		for (NcboOntologyFile ontologyFile : ontologyFileList) {
			ncboOntologyFileDAO.save(ontologyFile);
		}

		// 4. <ontologyCategory> - populate and save
		ontologyBean.populateToCategoryEntity(ontologyCategoryList,
				newOntologyVersion);
		for (NcboOntologyCategory ontologyCategory : ontologyCategoryList) {
			ncboOntologyCategoryDAO.save(ontologyCategory);
		}

	}

	/**
	 * Update ontology. Execute the business logic with the OntologyBean first,
	 * then populate OntologyVersion object
	 * 
	 * @param ontologyBean
	 * @return
	 */
	public void updateOntology(OntologyBean ontologyBean) {

		// upload ontology file
		uploadOntologyFile(ontologyBean);

		// get the NcboOntologyVersion instance using OntologyVersionId
		NcboOntologyVersion ontologyVersion = ncboOntologyVersionDAO
				.findById(ontologyBean.getId());

		// get NcboOntologyMetadata instance from ontologyVersion
		// since it is one-to-one, there is only one ontologyMetadata record per
		// ontologyVersion record
		NcboOntologyMetadata ontologyMetadata = (NcboOntologyMetadata) ontologyVersion
				.getNcboOntologyMetadatas().toArray()[0];

		ArrayList<NcboOntologyFile> ontologyFileList = new ArrayList<NcboOntologyFile>();
		ArrayList<NcboOntologyCategory> ontologyCategoryList = new ArrayList<NcboOntologyCategory>();

		if (ontologyVersion == null || ontologyMetadata == null)
			return;

		// 1. <ontologyVersion> - populate and save
		ontologyBean.populateToVersionEntity(ontologyVersion);
		ncboOntologyVersionDAO.save(ontologyVersion);

		// 2. <ontologyMetadata> - populate and save
		ontologyBean
				.populateToMetadataEntity(ontologyMetadata, ontologyVersion);
		ncboOntologyMetadataDAO.save(ontologyMetadata);

		// 3. <ontologyFile> - populate and save
		ontologyBean.populateToFileEntity(ontologyFileList, ontologyVersion);
		for (NcboOntologyFile ontologyFile : ontologyFileList) {
			ncboOntologyFileDAO.save(ontologyFile);
		}

		// 4. <ontologyCategory> - populate and save
		ontologyBean.populateToCategoryEntity(ontologyCategoryList,
				ontologyVersion);
		for (NcboOntologyCategory ontologyCategory : ontologyCategoryList) {
			ncboOntologyCategoryDAO.save(ontologyCategory);
		}

	}

	public void deleteOntology(OntologyBean ontologyBean) {
		// TODO
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
			ontologyBean.setInternalVersionNumber(0);
		} else {
			ontologyBean.setInternalVersionNumber(ontologyBean
					.getInternalVersionNumber() + 1);
		}
	}

	/**
	 * Generate output file path from ontologyBean. Since the output file path
	 * is determined from ontologyBean Id and version number, make sure these
	 * values are present when you invoke this method.
	 * 
	 * @param OntologyBean
	 *            ontologyBean
	 */
	/*
	 * private void generateOutputFilePath(OntologyBean ontologyBean) {
	 *  // set target location for input file to be parsed File inputFile = new
	 * File(ontologyBean.getFilePath()); FileHandler ontologyFile = new
	 * PhysicalDirectoryFileHandler(inputFile);
	 * ontologyBean.setFilePath(ontologyFile .getOntologyDirPath(ontologyBean)); }
	 */

	/**
	 * Generate output file path from ontologyBean. Since the output file path
	 * is determined from ontologyBean Id and version number, make sure these
	 * values are present when you invoke this method.
	 * 
	 * @param OntologyBean
	 *            ontologyBean
	 */
	private void uploadOntologyFile(OntologyBean ontologyBean) {

		String filePathStr = ontologyBean.getFilePath();

		if (filePathStr == null)
			return;

		// if (filePathStr != null) {
		// create a fileHandler instance
		File inputFile = new File(ontologyBean.getFilePath());
		FileHandler ontologyFile = new PhysicalDirectoryFileHandler(inputFile);

		// TODO - test the copy process and uncomment this out

		// upload the file
		try {
			ontologyFile.processOntologyFileUpload(ontologyBean.getFilePath(),
					ontologyBean);
			System.out.println("ontologyBean.getFilePath() = "
					+ ontologyBean.getFilePath());

		} catch (Exception e) {

			// log to error
			System.out
					.println("Error in OntologyService:loadOntologyFile()!!!");
			e.printStackTrace();
		}

		// now update the file path
		ontologyBean.setFilePath(ontologyFile.getOntologyDirPath(ontologyBean));

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

}
