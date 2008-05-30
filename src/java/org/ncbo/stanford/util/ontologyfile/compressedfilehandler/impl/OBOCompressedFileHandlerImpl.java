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
import java.util.ArrayList;
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

		if (filename.endsWith("zip") || filename.endsWith("jar")
				|| filename.endsWith("tar")) {
			CompressionUtils compressionUtils = new CompressionUtils();
			List<String> allFiles = new ArrayList<String>(1);

			if (filename.endsWith("zip")) {
				allFiles = compressionUtils.unZip(filePath, filename);
			} else if (filename.endsWith("jar")) {
				allFiles = compressionUtils.unJar(outputFile.getPath(),
						filename);
			} else if (filename.endsWith("tar")) {
				allFiles = compressionUtils.unTar(outputFile.getPath(),
						filename);
			}

			String joinFile = createCompositeFile(filePath, filename, allFiles);
			relevantFiles.add(joinFile);
		}

		return relevantFiles;
	}

	public String createCompositeFile(String compressedFilePath,
			String compressedFilename, List<String> allFiles)
			throws FileNotFoundException, IOException {
		BufferedWriter joinOut = null;

		File del = new File(getCompositeFilePath(compressedFilePath,
				compressedFilename));
		del.delete();

		for (String filename : allFiles) {
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
