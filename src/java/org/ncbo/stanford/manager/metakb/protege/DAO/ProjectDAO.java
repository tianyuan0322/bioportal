package org.ncbo.stanford.manager.metakb.protege.DAO;

import org.ncbo.stanford.bean.ProjectBean;
import org.ncbo.stanford.manager.metakb.protege.DAO.base.AbstractDAO;

import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * Data access object for storing ProjectBean objects to the Metadata KB.
 * 
 * @author Tony Loeser
 */
public class ProjectDAO extends AbstractDAO<ProjectBean> {
	
	private static final String OWL_CLASS_NAME = PREFIX_METADATA + "Project";

	public ProjectDAO(OWLModel metadataKb) {
		super(OWL_CLASS_NAME, metadataKb);
	}
	
	protected ProjectBean newBean(Integer id) {
		return new ProjectBean(id);
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
		addDatatypePropertyMap("name", String.class, false, PROP_URI_NAME);
		addDatatypePropertyMap("institution", String.class, false, PROP_URI_FROM_INSTITUTION);
		addDatatypePropertyMap("people", String.class, false, PROP_URI_PEOPLE);
		addDatatypePropertyMap("homePage", String.class, false, PROP_URI_HAS_HOME_PAGE);
		addDatatypePropertyMap("description", String.class, false, PROP_URI_DESCRIPTION);
		addDatatypePropertyMap("userId", Integer.class, false, PROP_URI_USER_ID);
	}
}

