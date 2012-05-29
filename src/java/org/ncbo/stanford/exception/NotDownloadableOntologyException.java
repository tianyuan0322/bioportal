package org.ncbo.stanford.exception;

public class NotDownloadableOntologyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static final String DEFAULT_MESSAGE = "Requested Ontology is not avalaible " +
			"for download due to licensing restrictrions.";
	
	public NotDownloadableOntologyException(String msg) {
		super(msg);
	}
	
	public NotDownloadableOntologyException() {
		super(DEFAULT_MESSAGE);
	}
	
}
