package org.ncbo.stanford.util;


public class ConfigUtils {
	
	private static String ontologyFilePath;

	public static String getOntologyFileRootPath() {
		return ontologyFilePath;
	}

	/**
	 * @return the ontologyFilePath
	 */
	public static String getOntologyFilePath() {
		return ontologyFilePath;
	}

	/**
	 * @param ontologyFilePath the ontologyFilePath to set
	 */
	public void setOntologyFilePath(String ontologyFilePath) {
		ConfigUtils.ontologyFilePath = ontologyFilePath;
	}
}
