/**
 * 
 */
package org.ncbo.stanford.service.upload.impl;

import java.io.File;
import java.util.Date;

import org.apache.commons.fileupload.FileItem;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyFileDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyMetadataDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboOntologyVersionDAO;
import org.ncbo.stanford.domain.custom.dao.CustomNcboSeqOntologyIdDAO;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboOntologyMetadata;
import org.ncbo.stanford.domain.generated.NcboOntologyVersion;
import org.ncbo.stanford.service.upload.UploadService;

/**
 * @author Nick Griffith
 *
 */
public class UploadServiceImpl implements UploadService{
	
	
	private CustomNcboOntologyVersionDAO ncboOntologyVersionDAO;
	private CustomNcboOntologyMetadataDAO ncboOntologyMetadataDAO;
	private CustomNcboOntologyFileDAO ncboOntologyFileDAO;
	private CustomNcboSeqOntologyIdDAO ncboSeqOntologyIdDAO;
	
	private String ontologyFilePath;
	
	/**
	 * Takes a file and creates the database records for the newly uploaded ontology.
	 * Must provide a populated OntologyBean for the metadata.
	 * 
	 */
	
	public void uploadOntology(FileItem file, OntologyBean ontology) throws Exception{
		
		
		NcboOntologyVersion versionInfo = new NcboOntologyVersion();
		versionInfo.setDateCreated(new Date());
		versionInfo.setDateReleased(ontology.getDateReleased());
		
		if(ontology.getId()==null || ontology.getId()<1){			
			versionInfo.setOntologyId(ncboSeqOntologyIdDAO.generateNewOntologyId());
		}else{
			versionInfo.setOntologyId(ontology.getId());
		}
		versionInfo.setFilePath("/"+versionInfo.getOntologyId()+"/"+versionInfo.getInternalVersionNumber());
		ncboOntologyVersionDAO.save(versionInfo);
		
		File outputFile = new File(ontologyFilePath+versionInfo.getFilePath());
		file.write(outputFile);
		
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
		ontologyFile.setFilename(file.getName());
		ontologyFile.setNcboOntologyVersion(versionInfo);
		ncboOntologyFileDAO.save(ontologyFile);
		
	}
	
	public void parseOntology(File file){
		
	}

	/**
	 * @return the ontologyFilePath
	 */
	public String getOntologyFilePath() {
		return ontologyFilePath;
	}

	/**
	 * @param ontologyFilePath the ontologyFilePath to set
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
	 * @param ncboOntologyVersionDAO the ncboOntologyVersionDAO to set
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
	 * @param ncboOntologyMetadataDAO the ncboOntologyMetadataDAO to set
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
	 * @param ncboOntologyFileDAO the ncboOntologyFileDAO to set
	 */
	public void setNcboOntologyFileDAO(CustomNcboOntologyFileDAO ncboOntologyFileDAO) {
		this.ncboOntologyFileDAO = ncboOntologyFileDAO;
	}

	/**
	 * @return the ncboSeqOntologyIdDAO
	 */
	public CustomNcboSeqOntologyIdDAO getNcboSeqOntologyIdDAO() {
		return ncboSeqOntologyIdDAO;
	}

	/**
	 * @param ncboSeqOntologyIdDAO the ncboSeqOntologyIdDAO to set
	 */
	public void setNcboSeqOntologyIdDAO(
			CustomNcboSeqOntologyIdDAO ncboSeqOntologyIdDAO) {
		this.ncboSeqOntologyIdDAO = ncboSeqOntologyIdDAO;
	}
}

