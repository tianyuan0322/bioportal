/**
 * 
 */
package org.ncbo.stanford.manager.mapping;

import java.util.List;

import org.ncbo.stanford.bean.mapping.MappingBean;
import org.ncbo.stanford.bean.mapping.upload.UploadedMappingBean;

/**
 * @author g.prakash
 * 
 */
public interface MappingManager {
	// Method for parsing the CSV file
	public List<UploadedMappingBean> parseCSVFile(String fileData);

	// Method for Invoking the method of MappingService
	public MappingBean createMappingForUploadedFile(
			UploadedMappingBean uploadedMappingBean);
}
