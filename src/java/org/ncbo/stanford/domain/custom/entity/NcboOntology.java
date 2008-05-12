package org.ncbo.stanford.domain.custom.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ncbo.stanford.domain.generated.VNcboOntology;

public class NcboOntology extends VNcboOntology {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5602873061909462865L;
	private List<String> filenames = new ArrayList<String>(0);

	
	/**
	 * @param arg0
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean addFilename(String filename) {
		return filenames.add(filename);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addFilenames(Collection<? extends String> c) {
		return filenames.addAll(c);
	}

	/**
	 * @return the filenames
	 */
	public List<String> getFilenames() {
		return filenames;
	}

	/**
	 * @param filenames the filenames to set
	 */
	public void setFilenames(List<String> filenames) {
		this.filenames = filenames;
	}
}