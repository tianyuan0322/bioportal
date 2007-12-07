/**
 * 
 */
package org.ncbo.stanford.service.upload;

import java.io.File;

import org.ncbo.stanford.bean.OntologyBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author nickgriffith
 *
 */
@Transactional
public interface UploadService {
	
	void uploadOntology(File file, OntologyBean ontology);
	
	void parseOntology(File file);
	
}
