/**
 * 
 */
package org.ncbo.stanford.service.upload;

import java.io.File;

/**
 * @author nickgriffith
 *
 */
public interface UploadService {
	
	void uploadOntology(File file);
	
	void parseOntology(File file);
	
}
