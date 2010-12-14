package org.ncbo.stanford.service.xml.converters;

import org.ncbo.stanford.bean.mapping.MappingResultListBean;
import org.ncbo.stanford.bean.mapping.OneToOneMappingBean;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Implements an XStream converter that serializes the MappingResultListBean
 * objects into XML
 * 
 */
public class MappingResultListBeanConverter extends CollectionConverter {

	public MappingResultListBeanConverter(Mapper mapper) {
		super(mapper);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class type) {
		return type.equals(MappingResultListBean.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		MappingResultListBean mappings = (MappingResultListBean) source;
		writer.startNode("mappings");

		for (OneToOneMappingBean mapping : mappings) {
			writeItem(mapping, context, writer);
		}

		writer.endNode();
	}

}
