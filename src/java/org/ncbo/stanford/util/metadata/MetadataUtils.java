package org.ncbo.stanford.util.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ncbo.stanford.exception.MetadataException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;

/**
 * Super class of all ...MetadataUtil classes defining some commonly used
 * constants and some general purpose utility methods
 * 
 * @author csnyulas
 * 
 */
public class MetadataUtils {

	private static final Log log = LogFactory.getLog(MetadataUtils.class);

	protected enum AcronymUsagePolicy {
		AcronymAsNamePreferred, AcronymIfNoName, AcronymAsLastResort
	}

	public static final String PREFIX_OMV = "OMV:";
	public static final String PREFIX_METADATA = "metadata:";
	public static final String PREFIX_METRICS = "metrics:";

	public static final String PROPERTY_ID = PREFIX_METADATA + "id";
	public static final String PROPERTY_OMV_NAME = PREFIX_OMV + "name";
	public static final String PROPERTY_OMV_DESCRIPTION = PREFIX_OMV
			+ "description";
	public static final String PROPERTY_OMV_ACRONYM = PREFIX_OMV + "acronym";

	public static final String PROPERTY_RDFS_LABEL = "rdfs:label";
	private static Map map;

	protected static void addPropertyValue(OWLModel owlModel,
			RDFResource owlInd, String propName, Object value)
			throws MetadataException {
		OWLProperty prop = owlModel.getOWLProperty(propName);

		if (prop == null) {
			throw new MetadataException(
					"Metadata ontology does not contain property " + propName);
		}

		if (!owlInd.hasPropertyValue(prop, value)) {
			owlInd.addPropertyValue(prop, value);
		}
	}

	protected static void removePropertyValue(OWLModel owlModel,
			RDFResource owlInd, String propName, Object value)
			throws MetadataException {
		OWLProperty prop = owlModel.getOWLProperty(propName);

		if (prop == null) {
			throw new MetadataException(
					"Metadata ontology does not contain property " + propName);
		}

		owlInd.removePropertyValue(prop, value);
	}

	protected static void setPropertyValue(OWLModel owlModel,
			RDFResource owlInd, String propName, Object value)
			throws MetadataException {
		OWLProperty prop = owlModel.getOWLProperty(propName);

		if (prop == null) {
			throw new MetadataException(
					"Metadata ontology does not contain property " + propName);
		}

		if (value instanceof Collection && ((Collection<?>) value).isEmpty()) {
			value = null;
		}

		if (value instanceof Collection) {
			owlInd.setPropertyValues(prop, (Collection<?>) value);
		} else {
			owlInd.setPropertyValue(prop, value);
		}
	}

	@SuppressWarnings("unchecked")
	protected static <K, V> void setPropertyValuesFromMap(OWLModel owlModel,
			RDFResource owlInd, String propName, Map<K, V> valuesMap) throws MetadataException {
		// Basic error checking
		if (valuesMap == null)
			return;

		// Use comma as default token
		String token = ",";

		OWLProperty prop = owlModel.getOWLProperty(propName);

		if (prop == null) {
			throw new MetadataException(
					"Metadata ontology does not contain property " + propName);
		}

		// Collection to store converted Map
		ArrayList<String> convertedMap = new ArrayList<String>();
		// Break Map into tokenized strings and set the property value
		for (Iterator it = valuesMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<K, V> pair = (Map.Entry<K, V>) it.next();
			String value = StringEscapeUtils
					.escapeCsv(pair.getKey().toString())
					+ "\"" + token + "\""
					+ StringEscapeUtils.escapeCsv(pair.getValue().toString());
			convertedMap.add(value);
		}
		// Add converted map to metadata ontology
		owlInd.setPropertyValues(prop, convertedMap);
	}

	@SuppressWarnings("unchecked")
	protected static <T> T getPropertyValue(OWLModel owlModel,
			RDFResource owlInd, String propName, Class<T> type)
			throws Exception {
		OWLProperty prop = owlModel.getOWLProperty(propName);

		if (prop == null) {
			throw new MetadataException(
					"Metadata ontology does not contain property " + propName);
		}

		Collection<?> propVals = owlInd.getPropertyValues(prop);

		if (propVals == null || propVals.size() == 0) {
			return null;
		} else if (propVals.size() == 1) {
			Object val = propVals.iterator().next();

			if (val instanceof RDFSLiteral) {
				RDFSLiteral lit = (RDFSLiteral) val;

				if (lit.getPlainValue() != null) {
					val = lit.getPlainValue();
				} else {
					// try to extract some non-standard values like date, time,
					// datetime, etc.
					RDFSDatatype datatype = lit.getDatatype();

					if (owlModel.getXSDdate().equals(datatype)
							|| owlModel.getXSDtime().equals(datatype)
							|| owlModel.getXSDdateTime().equals(datatype)) {
						val = convertStringToDate(lit.getString());
					} else if (owlModel.getXSDinteger().equals(datatype)) {
						val = Integer.parseInt(lit.toString());
					}
				}
			}

			return (T) val;
		} else {
			throw new MetadataException(
					"Multiple values attached to individual: "
							+ owlInd.getLocalName() + " for property: "
							+ propName);
		}
	}

	@SuppressWarnings("unchecked")
	protected static <T> List<T> getPropertyValues(OWLModel owlModel,
			RDFResource owlInd, String propName, Class<T> type)
			throws MetadataException {
		OWLProperty prop = owlModel.getOWLProperty(propName);

		if (prop == null) {
			throw new MetadataException(
					"Metadata ontology does not contain property " + propName);
		}

		Collection<?> propVals = owlInd.getPropertyValues(prop);
		List<T> res = new ArrayList<T>();

		if (propVals != null) {
			for (Object propVal : propVals) {
				res.add((T) propVal);
			}
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	protected static <K, V> Map<K, V> getPropertyValuesAsMap(OWLModel owlModel,
			RDFResource owlInd, String propName, Class<K> keyType,
			Class<V> valueType) throws MetadataException {
		OWLProperty prop = owlModel.getOWLProperty(propName);

		if (prop == null) {
			throw new MetadataException(
					"Metadata ontology does not contain property " + propName);
		}

		Collection<String> propVals = owlInd.getPropertyValues(prop);
		Map<K, V> res = new HashMap<K, V>();

		if (propVals != null) {
			for (String propVal : propVals) {
				// Detokenize the HashMap key/value pair
				String[] values = propVal.split("\",\"");
				// Unescape the text
				for (int i = 0; i < values.length; i++) {
					values[i] = StringEscapeUtils.unescapeCsv(values[i]);
				}
				// Cast the strings to their expected type and put into
				// the returned HashMap
				res.put((K) values[0], (V) values[1]);
			}
		}
		return res;
	}

	protected static List<Integer> getPropertyValueIds(OWLModel owlModel,
			RDFResource owlInd, String propName) throws Exception {
		Collection<?> propVals = getPropertyValues(owlModel, owlInd, propName,
				Object.class);
		List<Integer> idList = new ArrayList<Integer>();

		if (propVals != null) {
			for (Object propVal : propVals) {
				if (propVal instanceof RDFResource) {
					RDFResource ind = (RDFResource) propVal;
					idList.add((Integer) getPropertyValue(owlModel, ind,
							PROPERTY_ID, Integer.class));
				} else {
					log
							.error("UNEXPECTED ERROR: Non-RDFResource value attached to "
									+ owlInd + " through property " + propName);
				}
			}
		}

		return idList;
	}

	/**
	 * this method is marked deprecated because to alert the engineers that they
	 * probably want to use the
	 * {@link #createXsdDateTimePropertyValue(OWLModel, Date)} method, because
	 * currently there is no case where this method should be used. In case
	 * there will be use cases for this method, drop the "deprecation" flag.
	 * 
	 * @param owlModel
	 * @param date
	 * @return
	 */
	@Deprecated
	protected static RDFSLiteral createXsdDatePropertyValue(OWLModel owlModel,
			Date date) {
		String value = XMLSchemaDatatypes.getDateString(date);
		RDFSDatatype datatype = owlModel
				.getRDFSDatatypeByURI(XSDDatatype.XSDdate.getURI());
		return owlModel.createRDFSLiteral(value, datatype);

	}

	protected static RDFSLiteral createXsdDateTimePropertyValue(
			OWLModel owlModel, Date date) {
		String str = XMLSchemaDatatypes.getDateTimeString(date);
		RDFSDatatype datatype = owlModel
				.getRDFSDatatypeByURI(XSDDatatype.XSDdateTime.getURI());
		return owlModel.createRDFSLiteral(str, datatype);
	}

	protected static Date convertStringToDate(String dateString) {
		if (dateString != null) {
			return XMLSchemaDatatypes.getDate(dateString);
		}

		return null;
	}

	protected static String convertDateToDateString(Date date) {
		return XMLSchemaDatatypes.getDateString(date);
	}

	protected static String convertDateToDateTimeString(Date date) {
		return XMLSchemaDatatypes.getDateTimeString(date);
	}

	protected static Integer getId(OWLModel owlModel, RDFResource owlInd)
			throws Exception {
		Collection<Integer> propVals = getPropertyValues(owlModel, owlInd,
				PROPERTY_ID, Integer.class);
		if (propVals == null || propVals.isEmpty()) {
			return null;
		} else {
			if (propVals.size() == 1) {
				return propVals.iterator().next();
			} else {
				// TODO what to do?
				// throw Exception?
				log.error("Multiple IDs specified for individual: " + owlInd);
				// return first element
				return propVals.iterator().next();
			}
		}
	}

	protected static Collection<RDFResource> getRDFResourcesWithId(
			OWLModel owlModel, Integer id) {

		Collection<RDFResource> res = new ArrayList<RDFResource>();
		Collection<?> matchingResources = owlModel
				.getRDFResourcesWithPropertyValue(owlModel
						.getOWLProperty(PROPERTY_ID), id);

		if (matchingResources == null) {
			res = null;
		} else {
			for (Object resource : matchingResources) {
				if (resource instanceof RDFResource) {
					res.add((RDFResource) resource);
				} else {
					log.error("Matching resource for id: " + id
							+ " is not an RDFResource: " + resource);
				}
			}
		}
		if (res.isEmpty()) {
			res = null;
		}
		return res;
	}

	protected static Collection<OWLIndividual> getOWLIndividualsWithId(
			OWLModel owlModel, Integer id) {
		Collection<?> matchingResources = owlModel
				.getRDFResourcesWithPropertyValue(owlModel
						.getOWLProperty(PROPERTY_ID), id);
		return filterMatchingResourcesForOWLIndividuals(matchingResources);
	}

	private static Collection<OWLIndividual> getMatchingOWLIndividuals(
			OWLModel owlModel, OWLProperty property, String query) {
		Collection<?> matchingResources = owlModel.getMatchingResources(
				property, query, -1);
		return filterMatchingResourcesForOWLIndividuals(matchingResources);
	}

	private static Collection<OWLIndividual> filterMatchingResourcesForOWLIndividuals(
			Collection<?> matchingResources) {
		Collection<OWLIndividual> res = new ArrayList<OWLIndividual>();
		if (matchingResources == null) {
			res = null;
		} else {
			for (Object resource : matchingResources) {
				if (resource instanceof OWLIndividual) {
					res.add((OWLIndividual) resource);
				}
			}
		}
		if (res.isEmpty()) {
			res = null;
		}
		return res;
	}

	protected static OWLIndividual getIndividualWithId(OWLModel metadata,
			String class_name, Integer id, boolean transitive) {
		OWLIndividual res = null;
		OWLNamedClass type = metadata.getOWLNamedClass(class_name);
		if (type != null) {
			Collection<OWLIndividual> individualsWithId = getOWLIndividualsWithId(
					metadata, id);
			if (individualsWithId != null) {
				for (OWLIndividual ind : individualsWithId) {
					if (ind.hasRDFType(type, transitive)) {
						if (res == null) {
							res = ind;
						} else {
							log
									.warn("Multiple individuals match class: "
											+ type + " with id: " + id
											+ " transitive: " + transitive
											+ ": " + ind);
						}
					}
				}
			}
		} else {
			log
					.error("Invalid class name specified for the method getIndividualWithId:"
							+ class_name);
		}
		return res;
	}

	protected static List<OWLIndividual> getIndividualsWithMatchingProperty(
			OWLModel metadata, String class_name, String property_name,
			String value, boolean transitive) {
		List<OWLIndividual> res = null;
		OWLNamedClass type = metadata.getOWLNamedClass(class_name);
		OWLProperty prop = metadata.getOWLProperty(property_name);

		if (type != null && prop != null) {
			res = new ArrayList<OWLIndividual>();
			Collection<OWLIndividual> individualsWithId = getMatchingOWLIndividuals(
					metadata, prop, value);
			if (individualsWithId != null) {
				for (OWLIndividual ind : individualsWithId) {
					if (ind.hasRDFType(type, transitive)) {
						res.add(ind);
					}
				}
			}
		} else {
			if (type == null) {
				log
						.error("Invalid class name specified in the method getIndividualWithId:"
								+ class_name);
			}
			if (prop == null) {
				log
						.error("Invalid property name specified in the method getIndividualWithId:"
								+ property_name);
			}
		}
		return res;
	}

	protected static <T> T getFirstElement(List<T> list) {
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return list.iterator().next();
		}
	}

	protected static int getNextAvailableIdForClass(OWLClass cls,
			Integer startId) throws Exception {
		OWLModel owlModel = cls.getOWLModel();
		Collection<?> instances = cls.getInstances(false);
		int max = 0;

		for (Iterator<?> iterator = instances.iterator(); iterator.hasNext();) {
			Object inst = iterator.next();

			if (inst instanceof RDFResource) {
				Integer id = getId(owlModel, (RDFResource) inst);

				if (id != null && id.intValue() > max) {
					max = id.intValue();
				} else if (id == null && max == 0) {
					// this is an error
					max = -1;
				}
			}
		}

		// check for 0, which means that there was no instance of the class
		// found
		if (max == 0 && startId != null) {
			max = startId.intValue();
		}

		return max + 1;
	}
}
