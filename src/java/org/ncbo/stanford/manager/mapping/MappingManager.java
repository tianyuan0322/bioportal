/**
 * 
 */
package org.ncbo.stanford.manager.mapping;

import org.apache.commons.fileupload.FileItem;

/**
 * @author g.prakash
 * 
 */
public interface MappingManager {
	// Method for uploading the CSV file
	public void parseCSVFile(String fileData)throws Exception;
}
