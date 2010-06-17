package org.ncbo.stanford.manager.metakb.protege.DAO;

import org.ncbo.stanford.bean.ProjectBean;
import org.ncbo.stanford.manager.metakb.protege.DAO.base.AbstractDAO;
import org.ncbo.stanford.manager.metakb.protege.DAO.base.PropertyMapSet;

/**
 * Data access object for storing {@link ProjectBean} objects to the Metadata KB.
 * 
 * @author Tony Loeser
 */
public class ProjectDAO extends AbstractDAO<ProjectBean> {
	
	private static final String OWL_CLASS_NAME = PREFIX_METADATA + "Project";

	@Override
	protected String getQualifiedClassName() {
		return OWL_CLASS_NAME;
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
	protected void initializePropertyMaps(PropertyMapSet maps) {
		maps.addDatatypePropertyMap("name", String.class, SINGLE_VALUE, PROP_URI_NAME);
		maps.addDatatypePropertyMap("institution", String.class, SINGLE_VALUE, PROP_URI_FROM_INSTITUTION);
		maps.addDatatypePropertyMap("people", String.class, SINGLE_VALUE, PROP_URI_PEOPLE);
		maps.addDatatypePropertyMap("homePage", String.class, SINGLE_VALUE, PROP_URI_HAS_HOME_PAGE);
		maps.addDatatypePropertyMap("description", String.class, SINGLE_VALUE, PROP_URI_DESCRIPTION);
		maps.addDatatypePropertyMap("userId", Integer.class, SINGLE_VALUE, PROP_URI_USER_ID);
	}
}

