package org.ncbo.stanford.service.subscriptions;

import java.util.List;

import org.ncbo.stanford.bean.SubscriptionsBean;

import org.ncbo.stanford.domain.generated.NcboUserSubscriptions;

public interface SubscriptionsService {
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public List<SubscriptionsBean> listOfSubscriptions(Integer userId);

	/**
	 * 
	 * @param subscriptionsBean
	 */
	public void createSubscriptions(SubscriptionsBean subscriptionsBean);

	/**
	 * 
	 * @param subscriptionsBean
	 */
	public void updateSubscriptions(SubscriptionsBean subscriptionsBean);

	/**
	 * 
	 * @param subscriptionsBean
	 */
	public void removeSubscriptions(SubscriptionsBean subscriptionsBean);

	/**
	 * 
	 * @param userId
	 * @return
	 */
	public SubscriptionsBean findSubscriptionsForUserId(Integer userId);
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public List<SubscriptionsBean> listOfSubscriptionsForOntologyId(String ontologyId);
	/**
	 * 
	 * @param ontologyId
	 * @return
	 */
	
}
