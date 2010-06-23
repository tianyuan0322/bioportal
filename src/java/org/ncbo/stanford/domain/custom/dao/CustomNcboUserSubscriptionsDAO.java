/**
 * 
 */
package org.ncbo.stanford.domain.custom.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.ncbo.stanford.domain.generated.NcboLNotificationType;
import org.ncbo.stanford.domain.generated.NcboUserSubscriptions;
import org.ncbo.stanford.domain.generated.NcboUserSubscriptionsDAO;
import org.ncbo.stanford.enumeration.NotificationTypeEnum;

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

	@Override
	public List findByOntologyId(Object ontologyId) {
		// add custom code that checks for ontology id = 99, which is a dummy id
		// to indicate that the user is subscribed to all ontologies

		return findByProperty(ONTOLOGY_ID, ontologyId);
	}

	/**
	 * 
	 * @param ontologyId
	 * @param notificationType
	 * @return
	 */
	public List findByOntologyIdAndNotificationType(String ontologyId,
			NotificationTypeEnum notificationType) {

		DetachedCriteria notificationTypeCriteria = DetachedCriteria.forClass(
				NcboLNotificationType.class).setProjection(
				Property.forName("id")).add(
				Property.forName("type").eq(notificationType.toString()));

		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(
				NcboUserSubscriptions.class).add(
				Property.forName("ontologyId").eq(ontologyId)).add(
				Property.forName("notificationType").eq(
						notificationTypeCriteria));
		List result = getHibernateTemplate().findByCriteria(detachedCriteria);
		return result;
	}

}
