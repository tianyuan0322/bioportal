package org.ncbo.stanford.service.xml.converters;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.URI;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class URIListConverter extends CollectionConverter implements Converter {

	public URIListConverter(Mapper mapper) {
		super(mapper);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		List<URI> uris = (ArrayList<URI>) source;

		for (URI uri : uris) {
			writer.startNode("fullId");
			writer.setValue(uri.toString());
			writer.endNode();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class type) {
		return (type.equals(ArrayList.class) || type.equals(List.class));
	}
}
