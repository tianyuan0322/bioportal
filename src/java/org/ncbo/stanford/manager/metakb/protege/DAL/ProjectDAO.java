package org.ncbo.stanford.manager.metakb.protege.DAL;

import org.ncbo.stanford.bean.metadata.ProjectBean;
import org.ncbo.stanford.manager.metakb.protege.base.AbstractDALayer;
import org.ncbo.stanford.manager.metakb.protege.base.DAO.TimestampedDAO;
import org.ncbo.stanford.manager.metakb.protege.base.prop.DatatypePropertyMap;

/**
 * Data access object for storing {@link ProjectBean} objects to the Metadata KB.
 * 
 * @author Tony Loeser
 */
public class ProjectDAO extends TimestampedDAO<ProjectBean> {
	
	private static final String OWL_CLASS_NAME = PREFIX_METADATA + "Project";
	
	public ProjectDAO(AbstractDALayer daLayer) {
		super(daLayer, OWL_CLASS_NAME);
	}

	
	// ============================================================
	// Properties

	public static final String PROP_URI_NAME = PREFIX_OMV + "name";
	public static final String PROP_URI_FROM_INSTITUTION = PREFIX_METADATA + "institution";
	public static final String PROP_URI_PEOPLE = PREFIX_METADATA + "people";
	public static final String PROP_URI_HAS_HOME_PAGE = PREFIX_METADATA + "hasHomePage";
	public static final String PROP_URI_DESCRIPTION = PREFIX_OMV + "description";
	public static final String PROP_URI_USER_ID = PREFIX_METADATA + "userId";
	
	// Override
	protected void initializePropertyMaps() {
		addMap(new DatatypePropertyMap("name", String.class, SINGLE_VALUE, PROP_URI_NAME));
		addMap(new DatatypePropertyMap("institution", String.class, SINGLE_VALUE, PROP_URI_FROM_INSTITUTION));
		addMap(new DatatypePropertyMap("people", String.class, SINGLE_VALUE, PROP_URI_PEOPLE));
		addMap(new DatatypePropertyMap("homePage", String.class, SINGLE_VALUE, PROP_URI_HAS_HOME_PAGE));
		addMap(new DatatypePropertyMap("description", String.class, SINGLE_VALUE, PROP_URI_DESCRIPTION));
		addMap(new DatatypePropertyMap("userId", Integer.class, SINGLE_VALUE, PROP_URI_USER_ID));
	}
}

