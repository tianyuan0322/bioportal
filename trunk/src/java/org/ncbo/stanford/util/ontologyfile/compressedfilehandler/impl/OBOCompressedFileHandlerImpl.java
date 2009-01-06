/**
 * 
 */
package org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.CompressionUtils;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.AbstractCompressedFileHandler;

/**
 * @author Michael Dorf
 * 
 */
public class OBOCompressedFileHandlerImpl extends AbstractCompressedFileHandler {

	protected OBOCompressedFileHandlerImpl() {
	}

	public List<String> handle(File outputFile, OntologyBean ontologyBean)
			throws FileNotFoundException, IOException {
		String filePath = outputFile.getPath();
		String filename = outputFile.getName();
		List<String> relevantFiles = super.handle(outputFile, ontologyBean);

		if (CompressionUtils.isCompressed(filename)) {
			String joinFile = createCompositeFile(filePath, filename);
			relevantFiles.add(joinFile);
		}

		return relevantFiles;
	}

	private String createCompositeFile(String compressedFilePath,
			String compressedFilename) throws FileNotFoundException,
			IOException {
		BufferedWriter joinOut = null;

		File del = new File(getCompositeFilePath(compressedFilePath,
				compressedFilename));
		del.delete();

		for (String filename : uncompressedFilenames) {
			byte data[] = new byte[ApplicationConstants.BUFFER_SIZE];
			String str = CompressionUtils.getOnlyFileName(filename);

			joinOut = new BufferedWriter(new FileWriter(getCompositeFilePath(
					compressedFilePath, compressedFilename), true));
			FileInputStream fin = new FileInputStream(compressedFilePath
					+ File.separator + str);

			while (fin.read(data) != -1) {
				joinOut.write((new String(data)).trim());
				data = new byte[ApplicationConstants.BUFFER_SIZE];
			}

			joinOut.newLine();
			joinOut.newLine();
			joinOut.flush();
			joinOut.close();
		}

		return getCompositeFilename(compressedFilename);
	}
}
