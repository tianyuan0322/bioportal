/**
 * 
 */
package org.ncbo.stanford.domain.custom.dao;

import java.util.List;

import org.hibernate.criterion.Expression;

import org.hibernate.criterion.Restrictions;

import org.hibernate.Criteria;

import org.ncbo.stanford.domain.generated.NcboUserSubscriptions;
import org.ncbo.stanford.domain.generated.NcboUserSubscriptionsDAO;
import org.ncbo.stanford.enumeration.NotificationTypeEnum;
import org.ncbo.stanford.util.constants.ApplicationConstants;

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

	@Override
	public List findByOntologyId(Object ontologyId) {
		// add custom code that checks for ontology id = 99, which is a dummy id
		// to indicate that the user is subscribed to all ontologies

		return findByProperty(ONTOLOGY_ID, ontologyId);
	}

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

		Criteria criteria = getSession().createCriteria(
				NcboUserSubscriptions.class).createAlias(
				"ncboLNotificationType", "nt").add(
				Expression.or(Expression.and(Restrictions.eq("ontologyId",
						ontologyId), Expression.eq("nt.type", notificationType
						.toString())), Restrictions.eq("ontologyId",
						ApplicationConstants.ONTOLOGY_ID)));

		List result = criteria.list();
		return result;

	}

}
