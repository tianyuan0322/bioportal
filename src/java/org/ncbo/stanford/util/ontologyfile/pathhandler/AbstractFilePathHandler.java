package org.ncbo.stanford.util.ontologyfile.pathhandler;

import java.io.File;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.CompressedFileHandler;

/**
 * An abstract class to contain common functionality for all file handlers
 * 
 * @author Michael Dorf
 * 
 */
public abstract class AbstractFilePathHandler implements FilePathHandler {

	protected CompressedFileHandler compressedFileHandler;

	protected AbstractFilePathHandler(
			CompressedFileHandler compressedFileHandler) {
		this.compressedFileHandler = compressedFileHandler;
	}

	public static String getOntologyFilePath(OntologyBean ontologyBean,
			String filename) {
		return getFullOntologyDirPath(ontologyBean) + "/" + filename;
	}

	public static String getFullOntologyDirPath(OntologyBean ontologyBean) {
		return MessageUtils.getMessage("bioportal.ontology.filepath")
				+ ontologyBean.getOntologyDirPath();
	}

	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		
		return path.delete();
	}
}
