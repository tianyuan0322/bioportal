/**
 * 
 */
package org.ncbo.stanford.service.xml.converters;

import java.util.Map.Entry;

import org.ncbo.stanford.bean.acl.OntologyAcl;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Presents Ontology ACL in a readable XML format
 * 
 * @author Michael Dorf
 * 
 */
public class OntologyAclConverter extends CollectionConverter {

	public OntologyAclConverter(Mapper mapper) {
		super(mapper);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class type) {
		return type.equals(OntologyAcl.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		OntologyAcl map = (OntologyAcl) source;

		for (Object obj : map.entrySet()) {
			Entry entry = (Entry) obj;
			writer.startNode("entry");
			writer.startNode("ontologyId");
			writer.setValue(entry.getKey().toString());
			writer.endNode();
			writer.startNode("isOwner");
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
