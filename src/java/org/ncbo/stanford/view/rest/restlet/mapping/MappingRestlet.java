package org.ncbo.stanford.view.rest.restlet.mapping;

import java.util.Iterator;
import java.util.List;

import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.service.mapping.upload.MappingUploadService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;

import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.restlet.data.Request;
import org.restlet.data.Response;

import org.restlet.data.Status;

/**
 * @author g.prakash
 * 
 */
public class MappingRestlet extends AbstractBaseRestlet {
	private static final Log log = LogFactory.getLog(MappingRestlet.class);
	private MappingUploadService mappingUploadService;

	/**
	 * 
	 * @param mappingUploadService
	 */
	public void setMappingUploadService(
			MappingUploadService mappingUploadService) {
		this.mappingUploadService = mappingUploadService;
	}

	@Override
	public void postRequest(Request request, Response response) {

		filesForMapping(request, response);

	}

	/**
	 * Return to the response uploading files
	 * 
	 * @param request
	 *            response
	 */
	private void filesForMapping(Request request, Response response) {
		HttpServletRequest httpServletRequest = RequestUtils
				.getHttpServletRequest(request);
		String fileName = " ";
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
					if (fileName.endsWith(ApplicationConstants.CSVFile)) {
						// Calling the fileUpload() method by passing list of
						// file Items
						mappingUploadService
								.csvFileForMapping(listForUploadFileItems);
					} else {
						throw new InvalidInputException(MessageUtils
								.getMessage("msg.error.mappingFile"));
					}
				}
			}

		} catch (Exception e) {
			response.setStatus(Status.CLIENT_ERROR_NOT_ACCEPTABLE, e
					.getMessage());
			log.error(e);
		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					fileName);

		}
	}

}
