/**
 * 
 */
package org.ncbo.stanford.domain.custom.dao;

import org.ncbo.stanford.domain.generated.NcboSeqOntologyIdDAO;

/**
 * @author Michael Dorf
 *
 */
public class CustomNcboSeqOntologyIdDAO extends NcboSeqOntologyIdDAO {

	/**
	 * 
	 */
	public CustomNcboSeqOntologyIdDAO() {
		super();
	}

	
	/**
	 * Generates a new ontology id (the same for all versions of a unique ontology)
	 * 
	 * @return new ontology id
	 */
	public Integer generateNewOntologyId() {		
		int updatedEntities = getSession().createSQLQuery("update ncbo_seq_ontology_id set id = last_insert_id(id + 1)").executeUpdate();

		if (updatedEntities > 0) {
			return (Integer) getSession().createSQLQuery("select last_insert_id()").uniqueResult();
		}
		
		return null;
	}

}
