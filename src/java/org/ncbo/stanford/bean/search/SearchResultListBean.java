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
	private int numHitsTotal = 0;

	public static final String NUM_HITS_TOTAL_FIELD_NAME = "numHitsTotal";
	public static final String CLASS_ALIAS = "searchResultList";

	/**
	 * @param c
	 */
	public SearchResultListBean(Collection<SearchBean> c, final int numHitsTotal) {
		super(c);
		this.numHitsTotal = numHitsTotal;
	}

	/**
	 * @param initialCapacity
	 */
	public SearchResultListBean(int initialCapacity, final int numHitsTotal) {
		super(initialCapacity);
		this.numHitsTotal = numHitsTotal;
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
		SearchResultListBean results;

		if (super.size() >= toIndex) {
			results = new SearchResultListBean(super
					.subList(fromIndex, toIndex), numHitsTotal);
		} else {
			results = new SearchResultListBean(0, numHitsTotal);
		}
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

	/**
	 * @return the numHitsTotal
	 */
	public int getNumHitsTotal() {
		return numHitsTotal;
	}
}
