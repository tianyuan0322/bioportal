/**
 * 
 */
package org.ncbo.stanford.util.ontologyfile.compressedfilehandler;

import java.io.File;

import org.ncbo.stanford.util.constants.ApplicationConstants;


/**
 * @author Michael Dorf
 *
 */
public abstract class AbstractCompressedFileHandler implements CompressedFileHandler {

	public String getCompositeFilename(String compressedFilename) {
		return compressedFilename + ApplicationConstants.COMPOSITE_FILENAME;
	}

	public String getCompositeFilePath(String compressedFilePath,
			String compressedFilename) {
		return compressedFilePath + File.separator
				+ getCompositeFilename(compressedFilename);
	}
}
