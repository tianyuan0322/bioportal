package org.ncbo.stanford.service.xml.converters;

import org.ncbo.stanford.bean.search.OntologyHitBean;
import org.ncbo.stanford.bean.search.OntologyHitMap;
import org.ncbo.stanford.util.MessageUtils;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.TreeMapConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Implements an XStream converter that serializes the OntologyHitMap objects
 * into XML
 * 
 * @author Michael Dorf
 * 
 */
public class OntologyHitMapConverter extends TreeMapConverter {

	public OntologyHitMapConverter(Mapper mapper) {
		super(mapper);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class type) {
		return type.equals(OntologyHitMap.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		OntologyHitMap ontologyHitMap = (OntologyHitMap) source;
		writer.startNode(MessageUtils.getMessage("entity.ontologyhitlist"));

		for (OntologyHitBean hit : ontologyHitMap.values()) {
			writeItem(hit, context, writer);
		}

		writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		throw new UnsupportedOperationException();
	}
}
