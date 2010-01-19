package org.ncbo.stanford.util.metadata;

import org.ncbo.stanford.bean.ProjectBean;
import org.ncbo.stanford.exception.MetadataException;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * XXX Utilities related to storing projects in the metadata ontology.
 *
 * @author <a href="mailto:tony@loeser.name">Tony Loeser</a>
 */
public class ProjectMetadataUtils extends MetadataUtils {

	public static final String CLASS_NAME_PROJECT = "Project";
	public static final String CLASS_URI_PROJECT = PREFIX_METADATA + CLASS_NAME_PROJECT;
	
	public static final String PROP_URI_NAME = PREFIX_OMV + "name";
	public static final String PROP_URI_FROM_INSTITUTION = PREFIX_METADATA + "institution";
	public static final String PROP_URI_PEOPLE = PREFIX_METADATA + "people";
	public static final String PROP_URI_HAS_HOME_PAGE = PREFIX_METADATA + "hasHomePage";
	public static final String PROP_URI_DESCRIPTION = PREFIX_OMV + "description";
	public static final String PROP_URI_USER_ID = PREFIX_METADATA + "userId";
	
	public static void fillInProjectPropertiesFromBean(OWLIndividual projectInd, ProjectBean projectBean) 
			throws MetadataException {
		// TODO: Might want to check that neither argument is null
		OWLModel owlModel = projectInd.getOWLModel();
		
		setPropertyValue(owlModel, projectInd, PROP_URI_NAME, projectBean.getName()); // throws MetadataException
		setPropertyValue(owlModel, projectInd, PROP_URI_FROM_INSTITUTION, projectBean.getInstitution()); // throws MetadataException
		setPropertyValue(owlModel, projectInd, PROP_URI_PEOPLE, projectBean.getPeople()); // throws MetadataException
		setPropertyValue(owlModel, projectInd, PROP_URI_HAS_HOME_PAGE, projectBean.getHomePage()); // throws MetadataException
		setPropertyValue(owlModel, projectInd, PROP_URI_DESCRIPTION, projectBean.getDescription()); // throws MetadataException
		setPropertyValue(owlModel, projectInd, PROP_URI_USER_ID, projectBean.getUserId()); // throws MetadataException
	}
	
	public static void fillInBeanPropertiesFromProject(OWLIndividual projectInd, ProjectBean projectBean) 
			throws Exception {
		// TODO: Might want to check that neither argument is null
		OWLModel owlModel = projectInd.getOWLModel();
		projectBean.setName(getPropertyValue(owlModel, projectInd, PROP_URI_NAME, String.class)); // throws Exception
		projectBean.setInstitution(getPropertyValue(owlModel, projectInd, PROP_URI_FROM_INSTITUTION, String.class)); // throws Exception
		projectBean.setPeople(getPropertyValue(owlModel, projectInd, PROP_URI_PEOPLE, String.class)); // throws Exception
		projectBean.setHomePage(getPropertyValue(owlModel, projectInd, PROP_URI_HAS_HOME_PAGE, String.class)); // throws Exception
		projectBean.setDescription(getPropertyValue(owlModel, projectInd, PROP_URI_DESCRIPTION, String.class)); // throws Exception
		projectBean.setUserId(getPropertyValue(owlModel, projectInd, PROP_URI_USER_ID, Integer.class)); // throws Exception
	}

}
