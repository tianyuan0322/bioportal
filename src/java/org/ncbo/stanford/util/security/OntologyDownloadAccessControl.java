package org.ncbo.stanford.util.security;

import java.util.Collections;
import java.util.List;

import org.ncbo.stanford.bean.OntologyBean;
import org.ncbo.stanford.util.RequestUtils;

public class OntologyDownloadAccessControl {
	
	private List<Integer> notDownloadableOntologies = null;
	
	public OntologyDownloadAccessControl(String notDownloadableOntologies) {
		this.notDownloadableOntologies = Collections.unmodifiableList(
				RequestUtils.parseIntegerListParam(notDownloadableOntologies));
	}
	
	public boolean isDownloadable(OntologyBean ont) {
		return !this.notDownloadableOntologies.contains(ont.getOntologyId());
	}
}
