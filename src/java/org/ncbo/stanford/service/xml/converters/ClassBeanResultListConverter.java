/**
 * 
 */
package org.ncbo.stanford.service.xml.converters;

import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.search.ClassBeanResultList;
import org.ncbo.stanford.util.MessageUtils;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * @author s.reddy
 * 
 */
public class ClassBeanResultListConverter extends CollectionConverter {

	public ClassBeanResultListConverter(Mapper mapper) {
		super(mapper);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class type) {
		return type.equals(ClassBeanResultList.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		ClassBeanResultList results = (ClassBeanResultList) source;
		writer.startNode(MessageUtils.getMessage("entity.classbeanresultlist"));

		for (ClassBean classBean : results) {
			writeItem(classBean, context, writer);
		}

		writer.endNode();
	}
}
