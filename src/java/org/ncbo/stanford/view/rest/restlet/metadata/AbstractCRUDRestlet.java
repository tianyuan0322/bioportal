package org.ncbo.stanford.view.rest.restlet.metadata;

import org.ncbo.stanford.bean.AbstractIdBean;
import org.ncbo.stanford.exception.InvalidInputException;
import org.ncbo.stanford.exception.MetadataObjectNotFoundException;
import org.ncbo.stanford.service.metadata.BeanCRUDService;
import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;
import org.ncbo.stanford.view.util.constants.RequestParamConstants;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

// Note that the id part of the URL spec needs to be labeled "id".
public abstract class AbstractCRUDRestlet<BeanType extends AbstractIdBean>
		extends AbstractBaseRestlet {
	
	// Implement to provide basic logging
	protected abstract void logError(Exception e);
	
	// Implement to provide access to object persistence service with basic CRUD capability
	protected abstract BeanCRUDService<BeanType> getBeanCRUDService();
	
	// Implement to copy the parameters from the request into the bean
	protected abstract void populateBeanFromRequest(BeanType bean, Request request);
	

	// =========================================================================
	// POST: Create a new object

	// Implement AbstractBaseRestlet
	public void postRequest(Request request, Response response) {
		createObject(request, response);
	}
	
	private void createObject(Request request, Response response) {
		BeanType newBean = null;
		try {
			newBean = getBeanCRUDService().createObject();
			
			// Verify that the "id" in the URL says "new"
			String idString = extractIdString(request);
			if (!(idString.equalsIgnoreCase("new"))) {
				throw new IllegalArgumentException("Expected id=\"new\" for POST create-project request");
			}
			
			// Fill in non-automatic data from the POST parameters, and update in store
			populateBeanFromRequest(newBean, request);
			getBeanCRUDService().updateObject(newBean);

		} catch (IllegalArgumentException iae) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, iae.getMessage());
			iae.printStackTrace();
			logError(iae);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			logError(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response, newBean);
		}
	}

	
	// =========================================================================
	// GET: Retrieve and return the object.
	
	// Implement AbstractBaseRestlet
	public void getRequest(Request request, Response response) {
		retrieveObject(request, response);
	}
	
	private void retrieveObject(Request request, Response response) {
		BeanType bean = null;
		try {
			Integer id = extractId(request);
			bean = getBeanCRUDService().retrieveObject(id);
		} catch (InvalidInputException iie) {
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, iie.getMessage());
			iie.printStackTrace();
			logError(iie);
		} catch (MetadataObjectNotFoundException monfe) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, monfe.getMessage());
			monfe.printStackTrace();
			logError(monfe);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			logError(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response, bean);
		}
	}

	
	// =========================================================================
	// DELETE: Delete the project.
	
	// Implement AbstractBaseRestlet
	public void deleteRequest(Request request, Response response) {
		deleteObject(request, response);
	}
	
	private void deleteObject(Request request, Response response) {
		try {
			Integer id = extractId(request);
			getBeanCRUDService().deleteObject(id);
		} catch (MetadataObjectNotFoundException monfe) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, monfe.getMessage());
			monfe.printStackTrace();
			logError(monfe);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			logError(e);
		} finally {
			xmlSerializationService.generateStatusXMLResponse(request, response);
		}
	}

	
	// =========================================================================
	// PUT: Update the project.
	
	// Implement AbstractBaseRestlet
	public void putRequest(Request request, Response response) {
		updateObject(request, response);
	}

	private void updateObject(Request request, Response response) {
		BeanType bean = null;
		try {
			Integer id = extractId(request);
			bean = getBeanCRUDService().retrieveObject(id);
			populateBeanFromRequest(bean, request);
			getBeanCRUDService().updateObject(bean);
		} catch (MetadataObjectNotFoundException monfe) {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND, monfe.getMessage());
			monfe.printStackTrace();
			logError(monfe);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();
			logError(e);
		} finally {
			xmlSerializationService.generateXMLResponse(request, response, bean);
		}
	}

	
	// =========================================================================
	// Helpers
	
	private String extractIdString(Request request) {
		// Get the string from the URL that is in the ID position
		String idString = (String)request.getAttributes().get(RequestParamConstants.PARAM_META_ID);
		return idString;
	}
	
	private Integer extractId(Request request) throws InvalidInputException {
		// Get the Project ID from the URL.
		String intString = (String)request.getAttributes().get(RequestParamConstants.PARAM_META_ID);
		Integer id = null;
		try {
			id = new Integer(intString);
		} catch (NumberFormatException e) {
			throw new InvalidInputException("Bad integer format in id: "+intString, e);
		}
		return id;
	}

}
