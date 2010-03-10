/**
 * 
 */
package org.ncbo.stanford.service.xmlparsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.ncbo.stanford.AbstractBioPortalTest;
import org.ncbo.stanford.bean.concept.ClassBean;
import org.ncbo.stanford.bean.concept.InstanceBean;
import org.ncbo.stanford.bean.concept.InstanceTypesList;
import org.ncbo.stanford.bean.concept.PropertyBean;
import org.ncbo.stanford.service.xml.XMLSerializationService;
import org.springframework.beans.factory.annotation.Autowired;

import com.thoughtworks.xstream.XStream;

/**
 * @author s.reddy
 * 
 */
public class InstanceBeanTest extends AbstractBioPortalTest {

	@Autowired
	XMLSerializationService xmlSerializationService;

	@Test
	public void testParse() {
		XStream xmlSerializer = xmlSerializationService.getXmlSerializer();

		InstanceBean instanceBean = new InstanceBean();
		Map<Object, Object> m = new HashMap<Object, Object>();
		PropertyBean classBean = new PropertyBean();

		m.put(classBean, "value1");
		m.put("key2", "value2");
		m.put("key3", "value3");

		instanceBean.setFullId("fullId1");
		instanceBean.setId("id");
		instanceBean.addDefinition("def");

		InstanceBean instanceBean2 = new InstanceBean();
		instanceBean2.setFullId("fullId1");
		instanceBean2.setId("id");
		instanceBean2.addDefinition("def");
		m.put("key", instanceBean2);

		instanceBean.addRelations(m);
		ArrayList<ClassBean> instanceTypes = new ArrayList<ClassBean>();
		ClassBean bean = new ClassBean();
		bean.setFullId("fullId");
		bean.setId("Id");

		ClassBean bean2 = new ClassBean();
		bean2.setFullId("fullId");
		bean2.setId("Id");
		instanceTypes.add(bean);
		instanceTypes.add(bean2);

		InstanceTypesList InstanceTypesList = new InstanceTypesList();
		InstanceTypesList.setInstanceTypes(instanceTypes);
		instanceBean.setInstanceType(InstanceTypesList);

		System.out.println(xmlSerializer.toXML(instanceBean));
	}
}
