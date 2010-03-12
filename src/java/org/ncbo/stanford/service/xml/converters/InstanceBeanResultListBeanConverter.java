/**
 * 
 */
package org.ncbo.stanford.service.xml.converters;

import org.ncbo.stanford.bean.concept.InstanceBean;
import org.ncbo.stanford.bean.concept.InstanceBeanResultListBean;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * @author s.reddy
 *
 */
public class InstanceBeanResultListBeanConverter extends CollectionConverter {

	public InstanceBeanResultListBeanConverter(Mapper mapper) {
		super(mapper);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class type) {
		return type.equals(InstanceBeanResultListBean.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		InstanceBeanResultListBean results = (InstanceBeanResultListBean) source;
		writer.startNode("list");

		for (InstanceBean instanceBean : results) {
			writeItem(instanceBean, context, writer);
		}
		writer.endNode();
	}
}

