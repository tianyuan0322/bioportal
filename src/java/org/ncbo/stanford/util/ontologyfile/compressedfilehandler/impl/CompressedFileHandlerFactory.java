/**
 * 
 */
package org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.util.constants.ApplicationConstants;
import org.ncbo.stanford.util.ontologyfile.compressedfilehandler.CompressedFileHandler;

/**
 * Factory for creating compressed file handlers based on the ontology format
 * 
 * @author Michael Dorf
 * 
 */
public class CompressedFileHandlerFactory {

	private static final Log log = LogFactory
			.getLog(CompressedFileHandlerFactory.class);
	private static final String FORMAT_OWL = "OWL";
	private static final String FORMAT_LEXGRID_XML = "LEXGRID_XML";

	public static CompressedFileHandler createFileHandler(String format) {
		CompressedFileHandler compressedFileHandler = null;

		try {
			compressedFileHandler = (CompressedFileHandler) (Class
					.forName(CompressedFileHandlerFactory.class.getPackage()
							.getName()
							+ "."
							+ getHandlerPrefix(format)
							+ CompressedFileHandler.class.getSimpleName()
							+ "Impl").newInstance());
		} catch (InstantiationException e) {
			log.error(e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			log.error(e);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error(e);
			e.printStackTrace();
		}

		return compressedFileHandler;
	}

	private static String getHandlerPrefix(String format) {
		String prefix = format;

		if (format.equalsIgnoreCase(ApplicationConstants.FORMAT_OWL_DL)
				|| format
						.equalsIgnoreCase(ApplicationConstants.FORMAT_OWL_FULL)) {
			prefix = FORMAT_OWL;
		} else if (format
				.equalsIgnoreCase(ApplicationConstants.FORMAT_LEXGRID_XML)) {
			prefix = FORMAT_LEXGRID_XML;
		}

		return prefix;
	}
}
