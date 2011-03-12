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
	private Map<Integer, OntologyHitBean> hitsPerOntology = new OntologyHitMap();

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

	public void addEmptyOntologyHit(Integer ontologyId,
			OntologyHitBean ontologyHit) {
		ontologyHit.setNumHits(0);
		hitsPerOntology.put(ontologyId, ontologyHit);
	}

	public void addOntologyHit(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayLabel) {
		if (hitsPerOntology.containsKey(ontologyId)) {
			hitsPerOntology.get(ontologyId).addHit();
		} else {
			hitsPerOntology.put(ontologyId, new OntologyHitBean(
					ontologyVersionId, ontologyId, ontologyDisplayLabel, 1));
		}
	}

	public Integer getOntologyHits(Integer ontologyId) {
		return hitsPerOntology.containsKey(ontologyId) ? hitsPerOntology.get(
				ontologyId).getNumHits() : 0;
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
	private void setHitsPerOntology(
			Map<Integer, OntologyHitBean> hitsPerOntology) {
		this.hitsPerOntology = hitsPerOntology;
	}

	/**
	 * @return the hitsPerOntology
	 */
	public Map<Integer, OntologyHitBean> getHitsPerOntology() {
		return hitsPerOntology;
	}
}
