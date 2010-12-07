package org.ncbo.stanford.domain.custom.dao.mapping;

import java.util.List;

import org.ncbo.stanford.domain.custom.entity.mapping.OneToOneMapping;
import org.ncbo.stanford.exception.InvalidInputException;

public class CustomNcboMappingStatsDAO extends AbstractNcboMappingDAO {

	public List<OneToOneMapping> getRecentMappings(Integer limit)
			throws InvalidInputException {
		String orderBy = "?date";

		return getMappings(limit, 0, null, orderBy, null);
	}

}
