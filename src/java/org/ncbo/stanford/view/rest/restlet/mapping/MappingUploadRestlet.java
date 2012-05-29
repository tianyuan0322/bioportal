package org.ncbo.stanford.view.rest.restlet.mapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.mapping.MappingBean;
import org.ncbo.stanford.bean.mapping.MappingOntologyStatsBean;
import org.ncbo.stanford.bean.mapping.MappingStatsBean;
import org.ncbo.stanford.bean.mapping.upload.UploadedMappingBean;
import org.ncbo.stanford.exception.ConceptNotFoundException;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.MappingMissingException;
import org.ncbo.stanford.manager.mapping.MappingManager;
import org.ncbo.stanford.service.concept.ConceptService;
import org.ncbo.stanford.service.mapping.MappingService;
import org.ncbo.stanford.service.ontology.OntologyService;
import org.ncbo.stanford.sparql.bean.Mapping;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

/**
 * @author g.prakash
 * 
 */
public class MappingUploadRestlet extends AbstractBaseRestlet {
	private static final Log log = LogFactory
			.getLog(MappingUploadRestlet.class);

	private OntologyService ontologyService;
	private MappingManager mappingManager;
	private ConceptService conceptService;
	private MappingService mappingService;
	List<MappingStatsBean> mappingBean = new ArrayList<MappingStatsBean>();

	@Override
	public void postRequest(Request request, Response response) {

		filesForMapping(request, response);

	}

	/**
	 * Return's the response after mapping
	 * 
	 * @param request
	 *            response
	 * @throws FileUploadException
	 */
	private void filesForMapping(Request request, Response response) {
		HttpServletRequest httpServletRequest = RequestUtils
				.getHttpServletRequest(request);
		String fileName;
		MappingBean oneToOneMappingBean = null;

		UploadedMappingBean uploadedMappingBean = null;
		OntologyBean ontologyBean = null;

		ClassBean concept = null;
		Integer importedMappingCount = 0;
		Integer count = 0;
		List<Integer> lineOfError = new ArrayList<Integer>();
		// List for UploadedMappingBean
		List<UploadedMappingBean> listForMappingBean = new ArrayList<UploadedMappingBean>();
		List<MappingOntologyStatsBean> mappingStatusBean = null;

		MappingStatsBean statsBean = new MappingStatsBean();
		try {
			// Condition for the HTTP request is encoded in multipart format
			if (ServletFileUpload.isMultipartContent(httpServletRequest)) {
				// Parsing the form data contained in the HTTP request.
				ServletFileUpload servletFileUpload = new ServletFileUpload(
						new DiskFileItemFactory());
				// Creating a list for containing a list of file items
				List listForUploadFileItems = servletFileUpload
						.parseRequest(httpServletRequest);
				Iterator iteratorForFile = listForUploadFileItems.iterator();
				// Using the Iterator for iterating a file inside List
				while (iteratorForFile.hasNext()) {
					// Passing the File inside FileItem
					FileItem fileItem = (FileItem) iteratorForFile.next();
					fileName = fileItem.getName();
					// Taking the InputStream
					InputStream inputStream = fileItem.getInputStream();
					// Creating a ByteArrayOutputStream Object
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					// Taking the Size of FileItems
					int sizeOfFile = (int) fileItem.getSize();
					// Creating a Array of Byte
					byte buffer[] = new byte[sizeOfFile];
					// Initialising a variable
					int bytesRead = 0;
					// read the input
					while ((bytesRead = inputStream.read(buffer, 0, sizeOfFile)) != -1) {
						byteArrayOutputStream.write(buffer, 0, bytesRead);
					}
					// assign it to string
					String data = new String(byteArrayOutputStream
							.toByteArray());
					// Condition For File type it's csv or rdf
					if (fileName.endsWith(ApplicationConstants.CSVFile)) {
						// Parsing the CSV file and stores inside List
						listForMappingBean = mappingManager.parseCSVFile(data);
					}

					// Iterating the UploadedMappingBean from the list
					for (int i = 1; i < listForMappingBean.size(); i++) {
						uploadedMappingBean = listForMappingBean.get(i);
						// OntologyIdInt from the UploadedOntologyBean
						Integer ontologyIdInt = Integer
								.parseInt(uploadedMappingBean
										.getSourceOntologyId());
						// OntologyVersionIdInt from the UploadedOntologyBean
						Integer ontologyVersionIdInt = Integer
								.parseInt(uploadedMappingBean
										.getSourceCreatedInOntologyVersion());
						// ConceptId from the UploadedOntologyBean
						String conceptId = uploadedMappingBean.getSource();
						// Condition for validating ontologies using
						// ontologyIdInt
						if (ontologyIdInt != null) {
							ontologyBean = ontologyService
									.findLatestOntologyOrViewVersion(ontologyIdInt);
						}
						// Condition for validating ontologies using
						// ontologyVersionIdInt
						else if (ontologyVersionIdInt != null) {
							ontologyBean = ontologyService
									.findOntologyOrView(ontologyVersionIdInt);
						}
						// Condition for validating the existence of a concept
						if (conceptId != null)
							concept = conceptService.findConcept(ontologyBean
									.getId(), conceptId, null, false, false,
									false);
						// Condition for OntologyBean for null Values
						if (ontologyBean == null) {
							throw new InvalidInputException(
									MessageUtils
											.getMessage("msg.error.ontologyversionidinvalid"));
						}
						// Condition for ConceptBean for null values
						if (concept == null) {
							throw new ConceptNotFoundException(MessageUtils
									.getMessage("msg.error.conceptNotFound"));
						}
						// Calling the createMappingForUploadFile
						oneToOneMappingBean = mappingManager
								.createMappingForUploadedFile(uploadedMappingBean);

					}

					// Try to get the List for MappingStatusBean after Mapping
					mappingStatusBean = mappingService
							.getOntologiesMappingCount();
					// Iterating the List of MappingStatusBean
					for (MappingOntologyStatsBean mappingStatsBean : mappingStatusBean) {
						importedMappingCount = mappingStatsBean
								.getTargetMappings();
						statsBean.setImportedMappingCount(importedMappingCount);

					}
					// Iterating the List of Uploaded MappingBean
					for (int i = 1; i < listForMappingBean.size(); i++) {
						// Try to get the UploadedMappingBean from the List
						uploadedMappingBean = listForMappingBean.get(i);
						// Try to get the MappingStatusBean according to the
						// TargetOntologyId from the CSV file
						mappingStatusBean = mappingService
								.getOntologyMappingCount(Mapping.ontologyURIFromOntologyID(
										Integer
										.parseInt(uploadedMappingBean
												.getTargetOntologyId())));

						// Checking the condition for null values for
						// MappingStatusBean
						if (mappingStatusBean == null) {
							// Finding the index of MappingStatusBean inside the
							// List
							count = listForMappingBean
									.indexOf(mappingStatusBean);
							// Adding this index value inside the List of
							// lineOfError
							lineOfError.add(count);
							statsBean.setErrorMappingCount(count);
							// set the line of error inside the MappingStatsBean
							statsBean.setErrorLineNumber(lineOfError);

						} else {
							statsBean.setErrorMappingCount(count);
						}

					}
					mappingBean.add(statsBean);
				}
			}
		} catch (MappingMissingException mappingMissingException) {
			mappingMissingException.getMessage();
		} catch (FileUploadException fileUploadException) {
			fileUploadException.getMessage();
		} catch (InputMismatchException inputOutputException) {
			inputOutputException.getMessage();
		} catch (IOException ioException) {
			ioException.getMessage();
		} catch (InvalidInputException e) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		} catch (ConceptNotFoundException cnfe) {
			response
					.setStatus(Status.CLIENT_ERROR_NOT_FOUND, cnfe.getMessage());

		} catch (Exception e) {
			xmlSerializationService.generateXMLResponse(request, response,
					mappingBean);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response,
					statsBean);
		}

	}

	/**
	 * 
	 * @param mappingService
	 */
	public void setMappingService(MappingService mappingService) {
		this.mappingService = mappingService;
	}

	/**
	 * 
	 * @param conceptService
	 */
	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	/**
	 * 
	 * @param mappingManager
	 */
	public void setMappingManager(MappingManager mappingManager) {
		this.mappingManager = mappingManager;
	}

	/**
	 * 
	 * @param ontologyService
	 */
	public void setOntologyService(OntologyService ontologyService) {
		this.ontologyService = ontologyService;
	}

}
