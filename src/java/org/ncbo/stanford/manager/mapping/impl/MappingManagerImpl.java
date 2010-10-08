/**
 * 
 */
package org.ncbo.stanford.manager.mapping.impl;

import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.com.bytecode.opencsv.CSVReader;

import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.List;

import org.ncbo.stanford.bean.MappingBean;

import org.ncbo.stanford.manager.mapping.MappingManager;

import org.ncbo.stanford.domain.generated.NcboOntologyFileDAO;
import org.ncbo.stanford.domain.generated.NcboUsageLogDAO;
import org.ncbo.stanford.domain.generated.NcboOntologyFile;
import org.ncbo.stanford.domain.generated.NcboUsageLog;
import org.ncbo.stanford.domain.generated.NcboUser;
import org.ncbo.stanford.domain.generated.NcboUserDAO;
import org.ncbo.stanford.service.mapping.MappingService;

import org.openrdf.model.impl.URIImpl;
import org.ncbo.stanford.enumeration.MappingSourceEnum;
import org.ncbo.stanford.exception.InvalidInputException;

/**
 * @author g.prakash
 * 
 */
public class MappingManagerImpl implements MappingManager {
	private static final Log log = LogFactory.getLog(MappingManagerImpl.class);
	private NcboOntologyFileDAO ncboOntologyFileDAO;
	private NcboUsageLogDAO ncboUsageLogDAO;
	private NcboUserDAO ncboUserDAO;

	private MappingService mappingService;

	/**
	 * After parsing the CSVFile the value of CSVFile setted inside the Bean
	 * class
	 * 
	 * @param fileData
	 */
	public void parseCSVFile(String fileData) {
		try {
			MappingBean mappingBean=new MappingBean();
			// Creating CSVReader using a comma for the separator.
			CSVReader reader = new CSVReader(new StringReader(fileData));
			// Creating the ColumnPositionMappingStrategy object of CSVBean
			ColumnPositionMappingStrategy<MappingBean> columnPositionMappingStrategy = new ColumnPositionMappingStrategy<MappingBean>();
			// Setting the CSVBean
			columnPositionMappingStrategy.setType(MappingBean.class);
			// Creating a String array which contains the Field of CSVBean
			String[] columnsForCSVFields = new String[] { "submittedBy",
					"source", "target", "relation", "mappingType",
					"sourceOntologyId", "sourceCreatedInOntologyVersion",
					"targetOntologyId", "targetCreatedInOntologyVersion",
					"created", "mappingSource", "mappingSourceName",
					"mappingSourceSite", "mappingSourceContactInfo",
					"mappingSourceAlgorithm" };

			// the fields to bind in CSVBean
			columnPositionMappingStrategy.setColumnMapping(columnsForCSVFields);
			// Creating a object for CsvToBean of CSVBean type
			CsvToBean<MappingBean> csvToBean = new CsvToBean<MappingBean>();
			// Creating a list of CSVBean type its contains the Populated
			// CSVBean

			List<MappingBean> listForFile = new ArrayList<MappingBean>();

			listForFile = csvToBean
					.parse(columnPositionMappingStrategy, reader);
			int i=0;
			while(i<listForFile.size()){
			mappingBean = listForFile.get(i);
			}
			createMappingForUploadedFile(mappingBean);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method handle the mapping for Uploaded Files
	 * 
	 * @param listOfFile
	 */
	private void createMappingForUploadedFile(MappingBean mappingBean) {
		/**
		NcboOntologyFile ncboOntologyFile = new NcboOntologyFile();
		NcboUsageLog ncboUsageLog = new NcboUsageLog();
		NcboUser ncboUser = new NcboUser();
		// List for NcboUsageLog
		List<NcboUsageLog> listForNcboUsageLog = new ArrayList<NcboUsageLog>();
		List<NcboOntologyFile> listForNcboOntologyFile = new ArrayList<NcboOntologyFile>();
		// MappingBean
		
		try {
			
				// Taking the value which is available in List of NcboUsageLog
				
				// Validate users to make sure they exist in BioPortal
				ncboUser = ncboUserDAO.findById(Integer.parseInt(mappingBean
						.getSubmittedBy()));

				// Validate concepts to make sure they exist in BioPortal
				listForNcboUsageLog = ncboUsageLogDAO
						.findByConceptId(Integer.parseInt(mappingBean.getSource()));
				// Validate virtual ontology ids to make sure they exist in
				// BioPortal
				ncboUsageLog = ncboUsageLogDAO.findById(Integer
						.parseInt(mappingBean.getSourceOntologyId()));
				// Validate version ontology ids to make sure they exist in
				// BioPortal
				listForNcboOntologyFile = ncboOntologyFileDAO
						.findByOntologyVersionId(Integer.parseInt(mappingBean
								.getSourceCreatedInOntologyVersion()));
				if (ncboUser.getId() == null && listForNcboUsageLog.isEmpty()
						&& ncboUsageLog.getId() == null
						&& listForNcboOntologyFile.isEmpty()) {
					String errorMsg = "Enter a valid ontology version id";
					throw new InvalidInputException(errorMsg);
				} else {
					// Converting the source to SourceURI
					String source = mappingBean.getRelation() + "#"
							+ mappingBean.getSource();
					// Converting the target to TargetURI
					String target = mappingBean.getRelation() + "#"
							+ mappingBean.getTarget();
					// Converting the mappingSourceSite to MappingSourceSiteUri
					String mappingSourceSite = mappingBean.getRelation() + "#"
							+ mappingBean.getMappingSourceSite();
					// Calling the createMapping() method
					mappingService
							.createMapping(
									new URIImpl(source),
									new URIImpl(target),
									new URIImpl(mappingBean.getRelation()),
									Integer.parseInt(mappingBean
											.getSourceOntologyId()),
									Integer.parseInt(mappingBean
											.getTargetOntologyId()),
									Integer
											.parseInt(mappingBean
													.getSourceCreatedInOntologyVersion()),
									Integer
											.parseInt(mappingBean
													.getTargetCreatedInOntologyVersion()),
									Integer.parseInt(mappingBean
											.getSubmittedBy()), "comment",
									MappingSourceEnum.APPLICATION, mappingBean
											.getMappingSourceName(),
									mappingBean.getMappingSourceContactInfo(),
									new URIImpl(mappingSourceSite), mappingBean
											.getMappingSourceAlgorithm(),
									mappingBean.getMappingType());

				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		**/
	}

	/**
	 * 
	 * @param ncboOntologyFileDAO
	 */
	public void setNcboOntologyFileDAO(NcboOntologyFileDAO ncboOntologyFileDAO) {
		this.ncboOntologyFileDAO = ncboOntologyFileDAO;
	}

	/**
	 * 
	 * @param ncboUsageLogDAO
	 */
	public void setNcboUsageLogDAO(NcboUsageLogDAO ncboUsageLogDAO) {
		this.ncboUsageLogDAO = ncboUsageLogDAO;
	}

	/**
	 * 
	 * @param mappingService
	 */
	public void setMappingService(MappingService mappingService) {
		this.mappingService = mappingService;
	}

}
