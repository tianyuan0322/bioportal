package org.ncbo.stanford.bean.search;

import java.util.Collection;
import java.util.Map;

import org.ncbo.stanford.util.paginator.Paginatable;
import org.ncbo.stanford.util.paginator.impl.PaginatableList;

/**
 * Class that aggregates all search results
 * 
 * @author Michael Dorf
 *
 */
public class SearchResultListBean extends PaginatableList<SearchBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1067248571020887697L;
	private Map<String, OntologyHitBean> hitsPerOntology = new OntologyHitMap();

	/**
	 * 
	 */
	public SearchResultListBean() {
		super();
	}

	/**
	 * @param c
	 */
	public SearchResultListBean(Collection<SearchBean> c) {
		super(c);
	}

	/**
	 * @param initialCapacity
	 */
	public SearchResultListBean(int initialCapacity) {
		super(initialCapacity);
	}

	public void addOntologyHit(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel) {
		String key = getKey(ontologyDisplayLabel);

		if (hitsPerOntology.containsKey(key)) {
			hitsPerOntology.get(key).addHit();
		} else {
			hitsPerOntology.put(key, new OntologyHitBean(ontologyVersionId,
					ontologyId, ontologyDisplayLabel, 1));
		}
	}

	public Integer getOntologyHits(String ontologyDisplayLabel) {
		String key = getKey(ontologyDisplayLabel);

		return hitsPerOntology.containsKey(key) ? hitsPerOntology.get(key)
				.getNumHits() : 0;
	}

	public Collection<OntologyHitBean> getAllHits() {
		return hitsPerOntology.values();
	}

	public Paginatable<SearchBean> sublist(int fromIndex, int toIndex) {
		SearchResultListBean results = new SearchResultListBean(super.subList(
				fromIndex, toIndex));
		results.setHitsPerOntology(hitsPerOntology);

		return results;
	}

	/**
	 * @param hitsPerOntology
	 *            the hitsPerOntology to set
	 */
	private void setHitsPerOntology(Map<String, OntologyHitBean> hitsPerOntology) {
		this.hitsPerOntology = hitsPerOntology;
	}

	private String getKey(String ontologyDisplayLabel) {
		return ontologyDisplayLabel.toLowerCase();
	}

	/**
	 * @return the hitsPerOntology
	 */
	public Map<String, OntologyHitBean> getHitsPerOntology() {
		return hitsPerOntology;
	}
}
