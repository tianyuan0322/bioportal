/**
 * 
 */
package org.ncbo.stanford.view.rest.restlet.subscriptions;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ncbo.stanford.service.subscriptions.SubscriptionsService;

import org.ncbo.stanford.util.MessageUtils;
import org.ncbo.stanford.util.RequestUtils;

import org.ncbo.stanford.util.helper.BeanHelper;

import org.ncbo.stanford.view.rest.restlet.AbstractBaseRestlet;

import org.ncbo.stanford.bean.SubscriptionsBean;
import org.ncbo.stanford.domain.generated.NcboLNotificationType;

import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * @author g.prakash
 * 
 */
public class SubscriptionsOntologyRestlet extends AbstractBaseRestlet {
	private SubscriptionsService subscriptionsService;

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void getRequest(Request request, Response response) {
		// Handle the calls for List Of Subscriptions
		listOfSubscriptionsForOntologyId(request, response);
	}

	/**
	 * Handle POST calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void postRequest(Request request, Response response) {
		// Handle the Calls for create Subscriptions
		createSubscriptionsForOntologyId(request, response);
	}

	/**
	 * Handle DELETE calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void deleteRequest(Request request, Response response) {
		// Handle the calls for delete Subscriptions
		deleteSubscriptionsForOntoogyId(request, response);
	}

	/**
	 * Handle UPDATE calls here
	 * 
	 * @param request
	 * @param response
	 */
	
	@Override
	public void putRequest(Request request, Response response) {
		// Handle the calls for update Subscriptions
		updateSubscriptionsForOntologyId(request, response);
	}

	/**
	 * Return to the response a listing of Subscriptions. This Method will be
	 * called when the request is coming for List of Subscriptions
	 * 
	 * @param response
	 */
	private void listOfSubscriptionsForOntologyId(Request request,
			Response response) {
		List<SubscriptionsBean> subscriptionsList = null;

		String ontologyId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyid"));
		try {
			// List of Subscriptions according to ontologyId
			subscriptionsList = subscriptionsService
					.listOfSubscriptionsForOntologyId(ontologyId);

		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());

		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					subscriptionsList);

		}
	}

	/**
	 * Return to the response creating subscriptions . This Method will be
	 * called when the request is coming for createSubscriptionsForOntologyId
	 * 
	 * @param request
	 *            response
	 */
	private void createSubscriptionsForOntologyId(Request request,
			Response response) {
		SubscriptionsBean subscriptionsBean = BeanHelper
				.populateSubscriptionsBeanFromRequest(request);
		// create the subscriptions
		try {
			subscriptionsService.createSubscriptions(subscriptionsBean);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();

		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					subscriptionsBean);
		}
	}

	/**
	 * Delete a specified SubscriptionsBean to the response. This Method will be
	 * called when the request is coming for deleteSubscriptionsForOntoogyId
	 * 
	 * @param request
	 * @param resp
	 */
	private void deleteSubscriptionsForOntoogyId(Request request,
			Response response) {
		SubscriptionsBean subscriptionsBean = BeanHelper
				.populateSubscriptionsBeanFromRequest(request);
		// create the subscriptions
		try {
			subscriptionsService.removeSubscriptions(subscriptionsBean);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();

		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					subscriptionsBean);
		}

	}

	/**
	 * Returns a specified SubscriptionsBean and set the response status if
	 * there is an error. This Method will be called when the request is coming
	 * for findSubscriptionsBean
	 * 
	 * @param request
	 * @param response
	 */
	private SubscriptionsBean findSubscriptionsBean(Request request,
			Response response) {
		SubscriptionsBean subscriptionsBean = null;
		String userId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.userid"));
		String ontologyId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.ontologyid"));
		String ncboLNotificationType = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.notificationType"));
		NcboLNotificationType notificationType = new NcboLNotificationType();
		notificationType.setType(ncboLNotificationType);
		try {
			Integer integerId = Integer.parseInt(userId);

			subscriptionsBean.setUserId(integerId);
			subscriptionsBean.setOntologyId(ontologyId);
			//subscriptionsBean.setNcboLNotificationType(notificationType);
			// subscriptionsBean = subscriptionsService
			// .findSubscriptions(ontologyId);
		} catch (Exception e) {
			// set the response if there is any any Error
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());

		}

		return subscriptionsBean;
	}

	/**
	 * Update a specified SubscriptionsBean to the response. This Method will be
	 * called when the request is coming for updateSubscriptionsForOntologyId
	 * 
	 * @param request
	 * @param resp
	 */
	
	private void updateSubscriptionsForOntologyId(Request request,
			Response response) {
		SubscriptionsBean subscriptionsBean = BeanHelper
				.populateSubscriptionsBeanFromRequest(request);
		try {
			subscriptionsService.updateSubscriptions(subscriptionsBean);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			e.printStackTrace();

		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					subscriptionsBean);
		}
	}

	/**
	 * @param subscriptionsService
	 */
	public void setSubscriptionsService(
			SubscriptionsService subscriptionsService) {
		this.subscriptionsService = subscriptionsService;
	}
}
