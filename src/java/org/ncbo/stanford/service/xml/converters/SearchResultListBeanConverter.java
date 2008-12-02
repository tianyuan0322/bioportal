package org.ncbo.stanford.service.xml.converters;

import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.ncbo.stanford.util.MessageUtils;

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
		writer.startNode(MessageUtils.getMessage("entity.searchresultlist"));

		for (SearchBean searchBean : results) {
			writeItem(searchBean, context, writer);
		}

		writer.endNode();
		writeOntologyHits(results.getHitsPerOntology(), context, writer);
	}

	protected void writeOntologyHits(Object item, MarshallingContext context,
			HierarchicalStreamWriter writer) {
		if (item == null) {
			String name = mapper().serializedClass(null);
			writer.startNode(name);
			writer.endNode();
		} else {
			context.convertAnother(item);
		}
	}
}
