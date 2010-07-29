package org.ncbo.stanford.view.rest.restlet.subscriptions;

import java.util.List;
import java.util.ArrayList;

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

public class SubscritpionsRestlet extends AbstractBaseRestlet {

	private SubscriptionsService subscriptionsService;

	/**
	 * Handle GET calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void getRequest(Request request, Response response) {
		// Handle the calls for list of Subscriptions
		listOfSubscriptions(request, response);
	}

	/**
	 * Handle POST calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void postRequest(Request request, Response response) {
		// Handle the calls for create Subscriptions
		createSubscriptions(request, response);
	}

	/**
	 * Handle DELETE calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void deleteRequest(Request request, Response response) {
		// Handle the calls for delete subscriptions
		deleteSubscriptions(request, response);
	}

	/**
	 * Handle UPDATE calls here
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	public void putRequest(Request request, Response response) {
		// Handle the calls for delete subscriptions
		updateSubscriptions(request, response);
	}

	/**
	 * Return to the response a listing of subscriptions. This Method will be
	 * called when the request is coming for List of Subscriptions.
	 * 
	 * @param response
	 */
	private void listOfSubscriptions(Request request, Response response) {
		List<SubscriptionsBean> subscriptionsList = null;

		String userId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.userid"));
		try {
			// List of Subscriptions according to userId
			subscriptionsList = subscriptionsService
					.listOfSubscriptions(RequestUtils.parseIntegerParam(userId));

		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());

		} finally {
			// generate response XML
			xmlSerializationService.generateXMLResponse(request, response,
					subscriptionsList);
		}
	}

	/**
	 * Return to the response creating subscriptions. This Method will be called
	 * when the request is coming for createSubscriptions.
	 * 
	 * @param request
	 *            response
	 */
	private void createSubscriptions(Request request, Response response) {
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
	 * Delete a specified SubscriptionsBean to the response
	 * 
	 * @param request
	 * @param resp
	 */
	private void deleteSubscriptions(Request request, Response response) {
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
	 * there is an error
	 * 
	 * 
	 * @param request
	 * @param response
	 */
	private SubscriptionsBean findSubscriptionsBean(Request request,
			Response response) {
		SubscriptionsBean subscriptionsBean = null;
		String userId = (String) request.getAttributes().get(
				MessageUtils.getMessage("entity.userid"));

		try {
			Integer userSubscriptionId = Integer.parseInt(userId);
			subscriptionsBean = subscriptionsService
					.findSubscriptionsForUserId(userSubscriptionId);
		} catch (Exception e) {
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());

		}

		return subscriptionsBean;
	}

	/**
	 * Update a specified SubscriptionsBean to the response
	 * 
	 * @param request
	 * @param resp
	 */
	private void updateSubscriptions(Request request, Response response) {
		
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