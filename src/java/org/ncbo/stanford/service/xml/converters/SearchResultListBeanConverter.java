package org.ncbo.stanford.service.xml.converters;

import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Implements an XStream converter that serializes the SearchResultListBean
 * objects into XML
 * 
 * @author Michael Dorf
 * 
 */
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
		writer.startNode(SearchResultListBean.CLASS_ALIAS);

		for (SearchBean searchBean : results) {
			writeItem(searchBean, context, writer);
		}

		writer.endNode();
		writeOntologyHits(results.getHitsPerOntology(), context, writer);

		writer.startNode(SearchResultListBean.NUM_HITS_TOTAL_FIELD_NAME);
		writer.setValue(Integer.toString(results.getNumHitsTotal()));
		writer.endNode();
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
