package org.ncbo.stanford.service.loader.processor.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyCategoryDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyFileDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyLoadQueueDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyMetadataDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboSeqOntologyIdDAO;
import org.ncbo.stanford.domain.generated.NcboLCategory;
import org.ncbo.stanford.domain.generated.NcboLStatus;
import org.ncbo.stanford.domain.generated.NcboOntologyCategory;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboOntologyLoadQueue;
import org.ncbo.stanford.domain.generated.NcboOntologyMetadata;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.enumeration.StatusEnum;
import org.ncbo.stanford.service.loader.processor.OntologyLoadProcessorService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.ontologyfile.pathhandler.FilePathHandler;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class to handle ontology uploads. Adds/updates all appropriate records in the
 * BioPortal database, copies ontology file(s) to the appropriate location and
 * populates the load queue for later parsing by the load scheduler
 * 
 * @author Michael Dorf
 * 
 */
@Transactional
public class OntologyLoadProcessorServiceImpl implements
		OntologyLoadProcessorService {

	private static final Log log = LogFactory
			.getLog(OntologyLoadProcessorServiceImpl.class);

	private CustomNcboOntologyMetadataDAO ncboOntologyMetadataDAO;
	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	private CustomNcboOntologyCategoryDAO ncboOntologyCategoryDAO;
	private CustomNcboOntologyFileDAO ncboOntologyFileDAO;
	private CustomNcboOntologyLoadQueueDAO ncboOntologyLoadQueueDAO;
	private CustomNcboSeqOntologyIdDAO ncboSeqOntologyIdDAO;
	private String ontologyFilePath;

	/**
	 * Extract an ontology from a file and populate all the necessary db tables
	 * 
	 * @param filePathHandler
	 * @param ontologyBean
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = IOException.class)
	public NcboOntologyVersion processOntologyLoad(
			FilePathHandler filePathHandler, OntologyBean ontologyBean)
			throws Exception {
		NcboOntologyVersion ontologyVersion = new NcboOntologyVersion();
		Integer ontologyId = ontologyBean.getOntologyId();

		if (ontologyId == null || ontologyId < 1) {
			ontologyVersion.setOntologyId(ncboSeqOntologyIdDAO
					.generateNewOntologyId());
		} else {
			ontologyVersion.setOntologyId(ontologyId);
		}

		Integer parentId = ontologyBean.getParentId();

		if (parentId != null) {
			NcboOntologyVersion parentOntology = new NcboOntologyVersion();
			parentOntology.setId(parentId);
			ontologyVersion.setNcboOntologyVersion(parentOntology);
		}

		NcboUser ncboUser = new NcboUser();
		ncboUser.setId(ontologyBean.getUserId());
		ontologyVersion.setNcboUser(ncboUser);

		if (ontologyBean.getInternalVersionNumber() == null) {
			ontologyVersion.setInternalVersionNumber(Integer.parseInt(MessageUtils.getMessage("internalVersionNumberStart")));
		} else {
			ontologyVersion.setInternalVersionNumber(ontologyBean
					.getInternalVersionNumber() + 1);
		}

		ontologyVersion.setVersionNumber(ontologyBean.getVersionNumber());
		ontologyVersion.setIsCurrent(ontologyBean.getIsCurrent());
		ontologyVersion.setIsRemote(ontologyBean.getIsRemote());
		ontologyVersion.setIsReviewed(ontologyBean.getIsReviewed());
		ontologyVersion.setDateCreated(ontologyBean.getDateCreated());
		ontologyVersion.setDateReleased(ontologyBean.getDateReleased());

		ontologyVersion.setFilePath(filePathHandler
				.getOntologyDirPath(ontologyBean));

		NcboLStatus status = new NcboLStatus();
		status.setId(StatusEnum.STATUS_WAITING.getStatus());
		ontologyVersion.setNcboLStatus(status);
		NcboOntologyVersion newOntologyVersion = ncboOntologyVersionDAO
				.saveOntologyVersion(ontologyVersion);

		List<String> relevantFiles = filePathHandler.processOntologyFileUpload(
				ontologyFilePath, ontologyBean);

		for (String filename : relevantFiles) {
			NcboOntologyFile ontologyFileRec = new NcboOntologyFile();
			ontologyFileRec.setFilename(filename);
			ontologyFileRec.setNcboOntologyVersion(ontologyVersion);
			ncboOntologyFileDAO.save(ontologyFileRec);
			newOntologyVersion.getNcboOntologyFiles().add(ontologyFileRec);
		}

		NcboOntologyMetadata metadata = new NcboOntologyMetadata();
		metadata.setContactEmail(ontologyBean.getContactEmail());
		metadata.setContactName(ontologyBean.getContactName());
		metadata.setDisplayLabel(ontologyBean.getDisplayLabel());
		metadata.setDocumentation(ontologyBean.getDocumentation());
		metadata.setFormat(ontologyBean.getFormat());
		metadata.setHomepage(ontologyBean.getHomepage());
		metadata.setIsFoundry(ontologyBean.getIsFoundry());
		metadata.setPublication(ontologyBean.getPublication());
		metadata.setUrn(ontologyBean.getUrn());
		metadata.setNcboOntologyVersion(newOntologyVersion);

		newOntologyVersion.getNcboOntologyMetadatas().add(metadata);
		ncboOntologyMetadataDAO.save(metadata);

		for (Integer categoryId : ontologyBean.getCategoryIds()) {
			NcboOntologyCategory ontologyCategory = new NcboOntologyCategory();
			ontologyCategory.setNcboOntologyVersion(newOntologyVersion);

			NcboLCategory cat = new NcboLCategory();
			cat.setId(categoryId);
			ontologyCategory.setNcboLCategory(cat);
			ncboOntologyCategoryDAO.save(ontologyCategory);
			newOntologyVersion.getNcboOntologyCategories()
					.add(ontologyCategory);
		}

		NcboOntologyLoadQueue loadQueue = new NcboOntologyLoadQueue();
		loadQueue.setNcboOntologyVersion(newOntologyVersion);
		loadQueue.setNcboLStatus(status);
		loadQueue.setDateCreated(Calendar.getInstance().getTime());

		ncboOntologyLoadQueueDAO.save(loadQueue);
		newOntologyVersion.getNcboOntologyLoadQueues().add(loadQueue);

		return newOntologyVersion;
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
	 * @return the ontologyFilePath
	 */
	public String getOntologyFilePath() {
		return ontologyFilePath;
	}

	/**
	 * @param ontologyFilePath
	 *            the ontologyFilePath to set
	 */
	public void setOntologyFilePath(String ontologyFilePath) {
		this.ontologyFilePath = ontologyFilePath;
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
}
