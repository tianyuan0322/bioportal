/**
 * 
 */
package org.ncbo.stanford.service.upload.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import org.apache.commons.fileupload.FileItem;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyFileDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyMetadataDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboSeqOntologyIdDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboUserDAO;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboOntologyMetadata;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.manager.OntologyAndConceptManager;
import org.ncbo.stanford.service.upload.UploadService;

import com.ibm.icu.util.VersionInfo;

/**
 * @author Nick Griffith
 * 
 */
public class UploadServiceImpl implements UploadService {

	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	private CustomNcboOntologyMetadataDAO ncboOntologyMetadataDAO;
	private CustomNcboOntologyFileDAO ncboOntologyFileDAO;
	private CustomNcboSeqOntologyIdDAO ncboSeqOntologyIdDAO;
	private OntologyAndConceptManager ontologyAndConceptManager;
	private CustomNcboUserDAO ncboUserDAO;

	private String ontologyFilePath;

	/**
	 * Takes a file and creates the database records for the newly uploaded
	 * ontology. Must provide a populated OntologyBean for the metadata.
	 * 
	 */

	public void uploadOntology(InputStream inputStream, OntologyBean ontology)
			throws Exception {

		if(ontology.getId()!=null){
			ontology = ontologyAndConceptManager.findOntology(ontology.getId());
		}

		NcboOntologyVersion versionInfo = new NcboOntologyVersion();
		versionInfo.setDateCreated(ontology.getDateCreated());
		versionInfo.setDateReleased(ontology.getDateReleased());
		versionInfo.setNcboUser(ncboUserDAO.getUserByUsername("admin"));
		versionInfo.setVersionNumber(ontology.getVersionNumber());
		versionInfo.setIsCurrent(ontology.getIsCurrent());
		versionInfo.setIsRemote(ontology.getIsRemote());
		versionInfo.setIsReviewed(ontology.getIsReviewed());
		
		
		
		if (ontology.getInternalVersionNumber() == null) {
			versionInfo.setInternalVersionNumber(0);
		} else {
			versionInfo.setInternalVersionNumber(ontology
					.getInternalVersionNumber() + 1);
		}

		
		if (ontology.getId() == null || ontology.getId() < 1) {
			versionInfo.setOntologyId(ncboSeqOntologyIdDAO
					.generateNewOntologyId());
		} else {
			versionInfo.setOntologyId(ontology.getId());
		}
		
		versionInfo.setFilePath("/" + versionInfo.getOntologyId() + "/"
				+ versionInfo.getInternalVersionNumber());
		ncboOntologyVersionDAO.save(versionInfo);
		
		
		File outputFile = new File(ontologyFilePath + versionInfo.getFilePath()+"/file.txt");
		outputFile.mkdirs();
		outputFile.createNewFile();
		
		
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		
		int c;
        while ((c = inputStream.read()) != -1) 
        {
        	outputStream.write(c);
        }

        inputStream.close();
        outputStream.close();

		NcboOntologyMetadata metadata = new NcboOntologyMetadata();
		metadata.setContactEmail(ontology.getContactEmail());
		metadata.setContactName(ontology.getContactName());
		metadata.setDisplayLabel(ontology.getDisplayLabel());
		metadata.setDocumentation(ontology.getDocumentation());
		metadata.setFormat(ontology.getFormat());
		metadata.setHomepage(ontology.getHomepage());
		metadata.setIsFoundry(ontology.getIsFoundry());
		metadata.setPublication(ontology.getPublication());
		metadata.setUrn(ontology.getUrn());
		metadata.setNcboOntologyVersion(versionInfo);
		
		ncboOntologyMetadataDAO.save(metadata);

		NcboOntologyFile ontologyFile = new NcboOntologyFile();
		ontologyFile.setFilename(outputFile.getName());
		ontologyFile.setNcboOntologyVersion(versionInfo);
		ncboOntologyFileDAO.save(ontologyFile);

	}

	public void parseOntology(File file) {

	}

	
	
	
	
	
	
	/**
	 * @return the ncboUserDAO
	 */
	public CustomNcboUserDAO getNcboUserDAO() {
		return ncboUserDAO;
	}

	/**
	 * @param ncboUserDAO the ncboUserDAO to set
	 */
	public void setNcboUserDAO(CustomNcboUserDAO ncboUserDAO) {
		this.ncboUserDAO = ncboUserDAO;
	}

	/**
	 * @return the ontologyAndConceptManager
	 */
	public OntologyAndConceptManager getOntologyAndConceptManager() {
		return ontologyAndConceptManager;
	}

	/**
	 * @param ontologyAndConceptManager
	 *            the ontologyAndConceptManager to set
	 */
	public void setOntologyAndConceptManager(
			OntologyAndConceptManager ontologyAndConceptManager) {
		this.ontologyAndConceptManager = ontologyAndConceptManager;
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
