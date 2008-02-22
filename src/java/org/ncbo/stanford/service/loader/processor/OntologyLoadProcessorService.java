package org.ncbo.stanford.service.loader.processor;

import java.io.IOException;

import org.apache.commons.fileupload.FileItem;
import org.ncbo.stanford.bean.OntologyBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface OntologyLoadProcessorService {

	public void processOntologyLoad(FileItem ontologyFile,
			OntologyBean ontologyBean) throws IOException;
}
