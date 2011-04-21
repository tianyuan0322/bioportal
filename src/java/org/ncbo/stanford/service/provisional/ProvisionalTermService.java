package org.ncbo.stanford.service.provisional;

import java.util.Date;
import java.util.List;

import org.ncbo.stanford.bean.ProvisionalTermBean;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.ProvisionalTermExistsException;
import org.ncbo.stanford.exception.ProvisionalTermMissingException;
import org.ncbo.stanford.util.paginator.impl.Page;
import org.ncbo.stanford.util.sparql.SPARQLFilterGenerator;
import org.openrdf.model.URI;

public interface ProvisionalTermService {

	/**
	 * Get a provisional term by id.
	 * 
	 * @param id
	 * @return
	 * @throws ProvisionalTermMissingException
	 */
	public ClassBean getProvisionalTerm(String id)
			throws ProvisionalTermMissingException;

	/**
	 * To narrow the returned list use the parameters bean (simply add authors,
	 * ontologyIds, etc).
	 * 
	 * @param pageSize
	 * @param pageNum
	 * @param parameters
	 * @return
	 * @throws InvalidInputException
	 */
	public Page<ClassBean> getAllProvisionalTerms(Integer pageSize,
			Integer pageNum, SPARQLFilterGenerator parameters)
			throws InvalidInputException;

	/**
	 * Create a new provisional term.
	 * 
	 * @param ontologyIds
	 * @param label
	 * @param synonyms
	 * @param definition
	 * @param provisionalSubclassOf
	 * @param created
	 * @param updated
	 * @param submittedBy
	 * @param noteId
	 * @param status
	 * @param permanentId
	 * @return
	 * @throws ProvisionalTermExistsException
	 */
	public ClassBean createProvisionalTerm(List<Integer> ontologyIds,
			String label, List<String> synonyms, String definition,
			URI provisionalSubclassOf, Date created, Date updated,
			Integer submittedBy, String noteId, String status, URI permanentId)
			throws ProvisionalTermExistsException;

	/**
	 * Delete a provisional term by id.
	 * 
	 * @param id
	 * @throws ProvisionalTermMissingException
	 */
	public void deleteProvisionalTerm(String id)
			throws ProvisionalTermMissingException;

	/**
	 * Update a provisional term by id using a provisional term object with new
	 * values.
	 * 
	 * @param id
	 * @param term
	 * @return
	 * @throws ProvisionalTermMissingException
	 */
	public ClassBean updateProvisionalTerm(String id, ProvisionalTermBean term)
			throws ProvisionalTermMissingException;

}
