/**
 * 
 */
package org.ncbo.stanford.service.upload.impl;

import java.io.File;
import java.util.Date;

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
	
	/**
	 * Takes a file and creates the database records for the newly uploaded ontology.
	 * Must provide a populated OntologyBean for the metadata.
	 * 
	 */
	
	public void uploadOntology(File file, OntologyBean ontology){
		
		
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

}
