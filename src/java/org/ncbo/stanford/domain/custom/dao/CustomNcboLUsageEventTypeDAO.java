/**
 * 
 */
package org.ncbo.stanford.domain.custom.dao;

import java.util.List;

import org.ncbo.stanford.domain.generated.NcboLUsageEventType;
import org.ncbo.stanford.domain.generated.NcboLUsageEventTypeDAO;

/**
 * @author Michael Dorf
 * 
 */
public class CustomNcboLUsageEventTypeDAO extends NcboLUsageEventTypeDAO {

	/**
	 * 
	 */
	public CustomNcboLUsageEventTypeDAO() {
		super();
	}

	/**
	 * Finds a event type using its name
	 * 
	 * @param eventName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public NcboLUsageEventType findEventTypeByName(String eventName) {
		NcboLUsageEventType eventType = null;
		List<NcboLUsageEventType> eventTypes = findByEventName(eventName);

		if (eventTypes.size() > 0) {
			eventType = eventTypes.get(0);
		}

		return eventType;
	}
}
