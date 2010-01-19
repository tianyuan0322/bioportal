package org.ncbo.stanford.util.metadata;

import org.ncbo.stanford.bean.ReviewBean;
import org.ncbo.stanford.exception.MetadataException;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * XXX Utilities related to various annotation classes.
 *
 * @author <a href="mailto:tony@loeser.name">Tony Loeser</a>
 */
public class OntologyAnnotationUtils extends MetadataUtils {

	public static final String CLASS_NAME_REVIEW = "Review";
	public static final String CLASS_ID_REVIEW = PREFIX_METADATA + CLASS_NAME_REVIEW;
	public static final String PROPERTY_BODY = "metadata:" + "body";
	
	public static void fillInReviewPropertiesFromBean(OWLIndividual reviewInd, ReviewBean rb) throws MetadataException {
		// TODO: Might want to check that neither argument is null
		OWLModel owlModel = reviewInd.getOWLModel();
		setPropertyValue(owlModel, reviewInd, PROPERTY_BODY, rb.getBody()); // throws MetadataException
	}

	public static void fillInReviewBeanFromInstance(ReviewBean rb, OWLIndividual reviewInd) throws Exception {
		// TODO: Might want to check that neither argument is null
		OWLModel owlModel = reviewInd.getOWLModel();
		rb.setBody(getPropertyValue(owlModel, reviewInd, PROPERTY_BODY, String.class)); // throws Exception
	}

}
