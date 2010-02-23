/**
 * 
 */
package org.ncbo.stanford.service.xml.converters;

import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.concept.ClassBeanResultListBean;
import org.ncbo.stanford.util.MessageUtils;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * @author s.reddy
 * 
 */
public class ClassBeanResultListBeanConverter extends CollectionConverter {

	public ClassBeanResultListBeanConverter(Mapper mapper) {
		super(mapper);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class type) {
		return type.equals(ClassBeanResultListBean.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		ClassBeanResultListBean results = (ClassBeanResultListBean) source;
		writer.startNode(MessageUtils.getMessage("entity.classbeanresultlist"));

		for (ClassBean classBean : results) {
			writeItem(classBean, context, writer);
		}

		writer.endNode();
	}
}
