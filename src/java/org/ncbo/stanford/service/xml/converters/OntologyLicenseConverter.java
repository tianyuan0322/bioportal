/**
 * 
 */
package org.ncbo.stanford.service.xml.converters;

import java.util.Map.Entry;

import org.ncbo.stanford.bean.user.OntologyLicense;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Presents User Ontology Licenses in a readable XML format
 * 
 * @author Michael Dorf
 * 
 */
public class OntologyLicenseConverter extends CollectionConverter {

	public OntologyLicenseConverter(Mapper mapper) {
		super(mapper);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class type) {
		return type.equals(OntologyLicense.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		OntologyLicense map = (OntologyLicense) source;

		for (Object obj : map.entrySet()) {
			Entry entry = (Entry) obj;
			writer.startNode("licenseEntry");
			writer.startNode("ontologyId");
			writer.setValue(entry.getKey().toString());
			writer.endNode();
			writer.startNode("licenseText");
			writer.setValue(entry.getValue().toString());
			writer.endNode();
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		throw new UnsupportedOperationException();
	}
}
