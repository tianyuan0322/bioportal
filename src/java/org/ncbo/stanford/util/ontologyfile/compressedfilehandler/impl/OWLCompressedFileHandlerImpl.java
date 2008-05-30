/**
 * 
 */
package org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.CompressionUtils;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.AbstractCompressedFileHandler;

/**
 * @author Michael Dorf
 * 
 */
public class OWLCompressedFileHandlerImpl extends AbstractCompressedFileHandler {

	protected OWLCompressedFileHandlerImpl() {
	}

	public List<String> handle(File outputFile, OntologyBean ontologyBean)
			throws FileNotFoundException, IOException {
		String filePath = outputFile.getPath();
		String filename = outputFile.getName();
		List<String> relevantFiles = super.handle(outputFile, ontologyBean);

//		if (filename.endsWith("zip") || filename.endsWith("jar")
//				|| filename.endsWith("tar")) {
//			CompressionUtils compressionUtils = new CompressionUtils();
//			List<String> allFiles = new ArrayList<String>(1);
//
//			if (filename.endsWith("zip")) {
//				allFiles = compressionUtils.unZip(filePath, filename);
//			} else if (filename.endsWith("jar")) {
//				allFiles = compressionUtils.unJar(outputFile.getPath(),
//						filename);
//			} else if (filename.endsWith("tar")) {
//				allFiles = compressionUtils.unTar(outputFile.getPath(),
//						filename);
//			}
//
//			String joinFile = createCompositeFile(filePath, filename, allFiles);
//			relevantFiles.add(joinFile);
//		}

		return relevantFiles;
	}

}
