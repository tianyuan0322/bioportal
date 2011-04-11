/**
 * 
 */
package org.ncbo.stanford.service.xml.converters;

import java.util.Map.Entry;

import org.ncbo.stanford.bean.acl.UserAcl;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Presents User ACL in a readable XML format
 * 
 * @author Michael Dorf
 * 
 */
public class UserAclConverter extends CollectionConverter {

	public UserAclConverter(Mapper mapper) {
		super(mapper);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class type) {
		return type.equals(UserAcl.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		UserAcl map = (UserAcl) source;

		for (Object obj : map.entrySet()) {
			Entry entry = (Entry) obj;
			writer.startNode("userEntry");
			writer.startNode("userId");
			writer.setValue(entry.getKey().toString());
			writer.endNode();
			writer.startNode("isOwner");
			writer.setValue(entry.getValue().toString());
			writer.endNode();
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		throw new UnsupportedOperationException();
	}
}
