package org.ncbo.stanford.bean.search;

import java.util.HashMap;
import java.util.List;

import org.ncbo.stanford.bean.concept.ClassBean;

public class SearchResultBean {
	public enum SearchCategory {
		SEARCH_DEFINITION, SEARCH_NAME_CODE, SEARCH_PROPERTY_VALUE, SEARCH_METADATA
	};

	HashMap<SearchCategory, List<ClassBean>> map= new HashMap<SearchCategory, List<ClassBean>>();
	Integer ontologyId;

	public List<ClassBean> getSearchResultBean(SearchCategory category) {
		switch (category) {
		case SEARCH_DEFINITION:
			return getDefinitionSearchResult();
		case SEARCH_NAME_CODE:
			return getNameSearchResult();
		case SEARCH_PROPERTY_VALUE:
			return getPropertyValueSearchResult();
		case SEARCH_METADATA:
			return getMetadataSearchResult();
		default:
			return null;
		}
	}

	public List<ClassBean> getDefinitionSearchResult() {
		return map.get(SearchCategory.SEARCH_DEFINITION);
	}

	public List<ClassBean> getNameSearchResult() {
		return map.get(SearchCategory.SEARCH_NAME_CODE);
	}

	public List<ClassBean> getPropertyValueSearchResult() {
		return map.get(SearchCategory.SEARCH_PROPERTY_VALUE);
	}

	public List<ClassBean> getMetadataSearchResult() {
		return map.get(SearchCategory.SEARCH_METADATA);
	}

	public void setDefinitionSearchResult(List<ClassBean> beans) {
		map.put(SearchCategory.SEARCH_DEFINITION, beans);
	}

	public void setNameSearchResult(List<ClassBean> beans) {
		map.put(SearchCategory.SEARCH_NAME_CODE, beans);
	}

	public void setPropertyValueSearchResult(List<ClassBean> beans) {
		map.put(SearchCategory.SEARCH_PROPERTY_VALUE, beans);
	}

	public void setMetadataSearchResult(List<ClassBean> beans) {
		map.put(SearchCategory.SEARCH_METADATA, beans);
	}

	public Integer getOntologyId() {
		return ontologyId;
	}

	public void setOntologyId(Integer ontologyId) {
		this.ontologyId = ontologyId;
	}

}
