package org.ncbo.stanford.bean.search;

public class OntologyHitBean extends SearchBean {
	private Integer numHits = null;

	public OntologyHitBean(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayName) {
		this(ontologyVersionId, ontologyId, ontologyDisplayName, 0);
	}

	public OntologyHitBean(Integer ontologyVersionId, Integer ontologyId,
			String ontologyDisplayName, Integer numHits) {
		super(ontologyVersionId, ontologyId, ontologyDisplayName);
		this.numHits = numHits;
	}

	public void addHit() {
		numHits++;
	}

	/**
	 * @return the numHits
	 */
	public Integer getNumHits() {
		return numHits;
	}

	/**
	 * @param numHits
	 *            the numHits to set
	 */
	public void setNumHits(Integer numHits) {
		this.numHits = numHits;
	}
}
