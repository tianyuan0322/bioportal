/**
 * 
 */
package org.ncbo.stanford.service.xml.converters;

import java.util.ArrayList;

import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.concept.InstanceTypesList;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * @author s.reddy
 * 
 */
public class ClassBeanListConverter extends CollectionConverter {

	public ClassBeanListConverter(Mapper mapper) {
		super(mapper);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class type) {
		return type.equals(InstanceTypesList.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		InstanceTypesList ontologyHitMap = (InstanceTypesList) source;
		writer.startNode("list");
		ArrayList<ClassBean> collection = ontologyHitMap.getInstanceTypes();

		for (ClassBean hit : collection) {
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
