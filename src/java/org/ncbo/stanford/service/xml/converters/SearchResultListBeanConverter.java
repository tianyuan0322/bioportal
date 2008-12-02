package org.ncbo.stanford.service.xml.converters;

import org.ncbo.stanford.bean.search.SearchResultListBean;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class SearchResultListBeanConverter extends CollectionConverter {

	public SearchResultListBeanConverter(Mapper mapper) {
		super(mapper);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class type) {
		return type.equals(SearchResultListBean.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {

		SearchResultListBean results = (SearchResultListBean) source;

		super.marshal(source, writer, context);

		writer.startNode("hitsPerOntology");
		writeItem(results.getHitsPerOntology(), context, writer);
		writer.endNode();

	}

}
