package org.ncbo.stanford.bean.search;

import java.util.Collection;
import java.util.HashMap;

import org.ncbo.stanford.util.paginator.Paginatable;
import org.ncbo.stanford.util.paginator.impl.PaginatableList;

public class SearchResultListBean extends PaginatableList<SearchBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1067248571020887697L;
	private HashMap<Integer, Integer> hitsPerOntology = new HashMap<Integer, Integer>(
			0);

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

	public void addOntologyHit(Integer ontologyId) {
		hitsPerOntology.put(ontologyId,
				hitsPerOntology.containsKey(ontologyId) ? hitsPerOntology
						.get(ontologyId) + 1 : 1);
	}

	public Integer getOntologyHits(Integer ontologyId) {
		return hitsPerOntology.containsKey(ontologyId) ? hitsPerOntology
				.get(ontologyId) : 0;
	}

	public HashMap<Integer, Integer> getAllHits() {
		return hitsPerOntology;
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
	public void setHitsPerOntology(HashMap<Integer, Integer> hitsPerOntology) {
		this.hitsPerOntology = hitsPerOntology;
	}
}
