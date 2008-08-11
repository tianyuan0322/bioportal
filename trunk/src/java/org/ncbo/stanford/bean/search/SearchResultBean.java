package org.ncbo.stanford.bean.search;

import java.util.ArrayList;
import java.util.List;

import org.ncbo.stanford.bean.concept.ClassBean;

public class SearchResultBean {
	private Integer ontologyVersionId;

	private List<ClassBean> definitions = new ArrayList<ClassBean>();
	private List<ClassBean> names = new ArrayList<ClassBean>();
	private List<ClassBean> properties = new ArrayList<ClassBean>();
	private List<String> metadata = new ArrayList<String>();

	public List<ClassBean> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<ClassBean> definitions) {
		this.definitions = definitions;
	}

	public List<ClassBean> getNames() {
		return names;
	}

	public void setNames(List<ClassBean> names) {
		this.names = names;
	}

	public List<ClassBean> getProperties() {
		return properties;
	}

	public void setProperties(List<ClassBean> properties) {
		this.properties = properties;
	}

	public List<String> getMetadata() {
		return metadata;
	}

	public void setMetadata(List<String> metadata) {
		this.metadata = metadata;
	}

	public Integer getOntologyVersionId() {
		return ontologyVersionId;
	}

	public void setOntologyVersionId(Integer ontologyVersionId) {
		this.ontologyVersionId = ontologyVersionId;
	}

}
