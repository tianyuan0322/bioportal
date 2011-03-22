/**
 * 
 */
package org.ncbo.stanford.util.ontologyfile.compressedfilehandler.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.exception.InvalidCompressedFileHandlerException;
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
	private static final String FORMAT_LEXGRID_XML = "LEXGRID_XML";
	private static final String FORMAT_UMLS_RELA = "UMLS_RELA";

	public static CompressedFileHandler createFileHandler(String format)
			throws InstantiationException, IllegalAccessException,
			InvalidCompressedFileHandlerException {
		CompressedFileHandler compressedFileHandler = null;
		String handlerName = null;

		try {
			handlerName = CompressedFileHandlerFactory.class.getPackage()
					.getName()
					+ "."
					+ getHandlerPrefix(format)
					+ CompressedFileHandler.class.getSimpleName() + "Impl";

			compressedFileHandler = (CompressedFileHandler) (Class
					.forName(handlerName).newInstance());
		} catch (ClassNotFoundException e) {
			log.error(e);
			throw new InvalidCompressedFileHandlerException(
					"Invalid compressed file handler: " + handlerName);
		}

		return compressedFileHandler;
	}

	private static String getHandlerPrefix(String format) {
		String prefix = format;

		if (format.equalsIgnoreCase(ApplicationConstants.FORMAT_OWL_DL)
				|| format
						.equalsIgnoreCase(ApplicationConstants.FORMAT_OWL_FULL)
				|| format
						.equalsIgnoreCase(ApplicationConstants.FORMAT_OWL_LITE)) {
			prefix = ApplicationConstants.FORMAT_OWL;
		} else if (format
				.equalsIgnoreCase(ApplicationConstants.FORMAT_LEXGRID_XML)) {
			prefix = FORMAT_LEXGRID_XML;
		} else if (format
				.equalsIgnoreCase(ApplicationConstants.FORMAT_UMLS_RELA)) {
			prefix = FORMAT_UMLS_RELA;
		}

		return prefix;
	}
}
