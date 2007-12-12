/**
 * 
 */
package org.ncbo.stanford.service.upload;

import java.io.File;

import org.apache.commons.fileupload.FileItem;
import org.ncbo.stanford.bean.OntologyBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author nickgriffith
 *
 */
@Transactional
public interface UploadService {
	
	void uploadOntology(FileItem file, OntologyBean ontology) throws Exception;
	
	void parseOntology(File file);
	
}
