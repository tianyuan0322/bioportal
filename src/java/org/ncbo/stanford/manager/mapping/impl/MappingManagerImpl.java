/**
 * 
 */
package org.ncbo.stanford.manager.mapping.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.mapping.MappingBean;
import org.ncbo.stanford.bean.mapping.upload.UploadedMappingBean;
import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.manager.mapping.MappingManager;
import org.ncbo.stanford.service.mapping.MappingService;
import org.ncbo.stanford.sparql.bean.Mapping;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

/**
 * @author g.prakash
 * 
 */
public class MappingManagerImpl implements MappingManager {
	private static final Log log = LogFactory.getLog(MappingManagerImpl.class);

	private MappingService mappingService;

	/**
	 * After parsing the CSVFile the value of CSVFile setted inside the Bean
	 * class
	 * 
	 * @param fileData
	 */
	public List<UploadedMappingBean> parseCSVFile(String fileData) {

		// List<MappingBean> listForFile = new ArrayList<MappingBean>();
		List<UploadedMappingBean> listForFile = new ArrayList<UploadedMappingBean>();

		try {

			// Creating CSVReader using a comma for the separator.
			CSVReader reader = new CSVReader(new StringReader(fileData));
			// Creating the ColumnPositionMappingStrategy object of CSVBean
			ColumnPositionMappingStrategy<UploadedMappingBean> columnPositionMappingStrategy = new ColumnPositionMappingStrategy<UploadedMappingBean>();
			// Setting the CSVBean
			// columnPositionMappingStrategy.setType(MappingBean.class);
			columnPositionMappingStrategy.setType(UploadedMappingBean.class);
			// Creating a String array which contains the Field of CSVBean

			String[] columnsForCSVFields = new String[] { "submittedBy",
					"source", "target", "relation", "mappingType",
					"sourceOntologyId", "sourceCreatedInOntologyVersion",
					"targetOntologyId", "targetCreatedInOntologyVersion",
					"created", "mappingSource", "mappingSourceName",
					"mappingSourceSite", "mappingSourceContactInfo",
					"mappingSourceAlgorithm", "comment" };

			// the fields to bind in CSVBean
			columnPositionMappingStrategy.setColumnMapping(columnsForCSVFields);
			// Creating a object for CsvToBean of CSVBean type
			// CsvToBean<MappingBean> csvToBean = new CsvToBean<MappingBean>();
			CsvToBean<UploadedMappingBean> csvToBean = new CsvToBean<UploadedMappingBean>();
			// Creating a list of CSVBean type its contains the Populated
			// CSVBean

			// populateMappingBean(myEntries);
			listForFile = csvToBean
					.parse(columnPositionMappingStrategy, reader);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listForFile;

	}

	/**
	 * This method handle the mapping for Uploaded Files
	 * 
	 * @param listOfFile
	 */
	public MappingBean createMappingForUploadedFile(
			UploadedMappingBean uploadedMappingBean) {
		MappingBean mappingBeanForMapping = new MappingBean();
		
		ArrayList<URI> sourceURIs = new ArrayList<URI>();
		String source = uploadedMappingBean.getRelation() + "#"
				+ uploadedMappingBean.getSource();
		URI sourceURI = new URIImpl(source);
		sourceURIs.add(sourceURI);
		
		ArrayList<URI> targetURIs = new ArrayList<URI>();
		String target = uploadedMappingBean.getRelation() + "#"
				+ uploadedMappingBean.getTarget();
		URI targetURI = new URIImpl(target);
		targetURIs.add(targetURI);
		
		String relation = uploadedMappingBean.getRelation();
		URI relationURI = new URIImpl(relation);
		
		// SourceOntologyId
		URI sourceOntologyId = Mapping.ontologyURIFromOntologyID(
				Integer.parseInt(uploadedMappingBean.getSourceOntologyId()));

		// TargetOntologyId
		URI targetOntologyId = Mapping.ontologyURIFromOntologyID(
				Integer.parseInt(uploadedMappingBean.getTargetOntologyId()));

		// createdInSourceOntologyVersion
		URI createdInSourceOntologyVersion = Mapping.ontologyURIFromOntologyID(
				Integer.parseInt(uploadedMappingBean.getSourceCreatedInOntologyVersion()));

		// createdInTargetOntologyVersion
		URI createdInTargetOntologyVersion = Mapping.ontologyURIFromOntologyID(
				Integer.parseInt(uploadedMappingBean.getTargetCreatedInOntologyVersion())); 

		// submittedBy
		Integer submittedBy = Integer.parseInt(uploadedMappingBean
				.getSubmittedBy());

		String enumType = uploadedMappingBean.getMappingSource();
		MappingSourceEnum sourceEnum = null;
		if (enumType.equals("organization")) {
			sourceEnum = MappingSourceEnum.ORGANIZATION;
		} else {
			sourceEnum = MappingSourceEnum.APPLICATION;
		}
		String mappingSourceSite = uploadedMappingBean.getRelation() + "#"
				+ uploadedMappingBean.getMappingSourceSite();
		URI mappingSourceSiteURI = new URIImpl(mappingSourceSite);

		try {
			// Calling the method for mapping
			mappingBeanForMapping = mappingService.createMapping(
					sourceURIs, targetURIs, relationURI, sourceOntologyId,
					targetOntologyId, createdInSourceOntologyVersion,
					createdInTargetOntologyVersion, submittedBy,
					null, uploadedMappingBean.getComment(), sourceEnum,
					uploadedMappingBean.getMappingSourceName(),
					uploadedMappingBean.getMappingSourceContactInfo(),
					mappingSourceSiteURI, uploadedMappingBean
							.getMappingSourceAlgorithm(), uploadedMappingBean
							.getMappingType());

		} catch (Exception e) {
			log.error(e);
		}
		return mappingBeanForMapping;
	}

	/**
	 * 
	 * @param mappingService
	 */
	public void setMappingService(MappingService mappingService) {
		this.mappingService = mappingService;
	}

}
