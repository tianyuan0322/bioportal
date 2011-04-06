/**
 * 
 */
package org.ncbo.stanford.domain.custom.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.ncbo.stanford.domain.generated.NcboLNotificationType;
import org.ncbo.stanford.domain.generated.NcboUserSubscriptions;
import org.ncbo.stanford.domain.generated.NcboUserSubscriptionsDAO;
import org.ncbo.stanford.enumeration.NotificationTypeEnum;
import org.ncbo.stanford.util.constants.ApplicationConstants;

/**
 * @author mdorf
 * 
 */
public class CustomNcboUserSubscriptionsDAO extends NcboUserSubscriptionsDAO {

	/**
	 * 
	 */
	public CustomNcboUserSubscriptionsDAO() {
		// TODO Auto-generated constructor stub
	}
/**
	@Override
	public List findByOntologyId(Object ontologyId) {
		// add custom code that checks for ontology id = 99, which is a dummy id
		// to indicate that the user is subscribed to all ontologies

		return findByProperty(ONTOLOGY_ID, ontologyId);
	}
**/
	/**
	 * This Method collect the information of userId, according to
	 * ontologyId,notificationType and By Default Dummay OntologyId(99)
	 * 
	 * @param ontologyId
	 * @param notificationType
	 * @return
	 */
	public List findByOntologyIdAndNotificationType(String ontologyId,
			NotificationTypeEnum notificationType) {
		List<String> ontologyList = new ArrayList<String>(2);
		ontologyList.add(ontologyId);
		ontologyList.add(ApplicationConstants.ONTOLOGY_ID);
		List<String> typeEnum=new ArrayList<String>();
		typeEnum.add(notificationType.toString());
		typeEnum.add(NotificationTypeEnum.ALL_NOTIFICATION.toString());
		Criteria criteria = getSession().createCriteria(
				NcboUserSubscriptions.class).createAlias(
				"ncboLNotificationType", "ncboLNotificationType")
				.add(Restrictions.in("ontologyId", ontologyList))
				.add(Restrictions.in("ncboLNotificationType.type", typeEnum));

		return criteria.list();
	}
	/**
	 * 
	 * @param ontologuyId
	 * @param userId
	 * @return
	 */
	public List findByUserIdAndOntologyId(String ontologyId, Integer userId,
			NcboLNotificationType notificationTypes) {
		Criteria criteria = getSession()
				.createCriteria(NcboUserSubscriptions.class); 
		criteria.add(Restrictions.eq("ontologyId", ontologyId));
		criteria.add(Restrictions.eq("userId", userId));
		criteria.add(Restrictions.eq("ncboLNotificationType", notificationTypes));

		return criteria.list();
	}
	/**
	 * Method For Finding the userId,notificationType according to List of OntologyId
	 * 
	 *  @param ontologyIds
	 *  @param userId
	 *  @param notificationType
	 *  @return
	 */
	public List findByListOfOntologyIds(List<String> ontologyIds,Integer userIds,NcboLNotificationType notificationType){
		List<String> typeEnum=new ArrayList<String>();
		typeEnum.add(notificationType.toString());
		typeEnum.add(NotificationTypeEnum.ALL_NOTIFICATION.toString());
		Criteria criteria = getSession().createCriteria(
				NcboUserSubscriptions.class).createAlias(
				"ncboLNotificationType", "ncboLNotificationType")
				.add(Restrictions.in("ontologyId", ontologyIds))
				.add(Restrictions.in("ncboLNotificationType.type", typeEnum))
				.add(Restrictions.eq("userId", userIds));
		
		return criteria.list();
	}
}
