/**
 * 
 */
package org.ncbo.stanford.service.mapping.upload.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.ncbo.stanford.service.mapping.upload.MappingUploadService;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.manager.mapping.MappingManager;

/**
 * @author g.prakash
 * 
 */
public class MappingUploadServiceImpl implements MappingUploadService {
	private static final Log log = LogFactory
			.getLog(MappingUploadServiceImpl.class);
	private MappingManager mappingManager;

	/**
	 * 
	 * @param mappingManager
	 */
	public void setMappingManager(MappingManager mappingManager) {
		this.mappingManager = mappingManager;
	}

	/**
	 * 
	 * @param fileItems
	 */
	public void csvFileForMapping(List listForUploadedFileItems) {
		FileItem fileItem = null;
		try {

			Iterator it = listForUploadedFileItems.iterator();
			while (it.hasNext()) {
				// Passing the File inside FileItem
				fileItem = (FileItem) it.next();
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
				String data = new String(byteArrayOutputStream.toByteArray());
				// Calling this method for Parsing
				mappingManager.parseCSVFile(data);
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
