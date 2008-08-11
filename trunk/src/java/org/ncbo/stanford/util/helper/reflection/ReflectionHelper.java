package org.ncbo.stanford.util.helper.reflection;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class that wraps some of the most common java reflection functionality
 * 
 * @author Michael Dorf
 * 
 */
@SuppressWarnings("unchecked")
public class ReflectionHelper {

	/**
	 * Holds the cache map
	 */
	private static Map instanceFieldsCache = new HashMap(10);

	/**
	 * Invoke a method dynamically
	 * 
	 * @param object
	 * @param method
	 * @param args
	 * @return
	 */
	public static final Object invokeMethod(Object object, String method,
			ArgumentHolder args) {
		Method meth = null;
		Object result = null;

		try {
			meth = object.getClass().getMethod(method,
					args.getArgumentClasses());
			result = meth.invoke(object, args.getArguments());
		} catch (SecurityException e) {
			throw new RuntimeException(
					"Security exception during invocation of " + method
							+ " of " + object.getClass(), e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Can not find get method " + method
					+ " of " + object.getClass(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Can not invoke method" + method
					+ " of " + object.getClass(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Can not access method" + method
					+ " of " + object.getClass(), e);
		}

		return result;
	}

	/**
	 * To set the property of the object
	 * 
	 * @param object
	 * @param propertyName
	 * @return Object
	 */
	public static final void setProperty(Object object, String propertyName,
			Object value) {
		try {
			Method method = null;

			try {
				method = getPropertyDescriptor(object.getClass(), propertyName)
						.getWriteMethod();
				method.invoke(object, new Object[] { value });
			} catch (IntrospectionException introExp) {
				Field fld = null;

				try {
					fld = object.getClass().getField(propertyName);
				} catch (NoSuchFieldException noSuchFieldExp) {
					throw new RuntimeException(
							"Can not find get method or field of "
									+ propertyName + " of " + object.getClass(),
							noSuchFieldExp);
				}

				fld.set(object, value);
			}
		} catch (InvocationTargetException exc) {
			throw new RuntimeException("Can not invoke " + propertyName
					+ " of " + object.getClass(), exc);
		} catch (IllegalAccessException exc) {
			throw new RuntimeException("Can not access " + propertyName
					+ " of " + object.getClass(), exc);
		}
	}

	/**
	 * To get the read property of the object
	 * 
	 * @param object
	 * @param propertyName
	 * @return Object
	 */
	public static final Object getProperty(Object object, String propertyName) {
		try {
			Method method = null;
			try {
				method = getPropertyDescriptor(object.getClass(), propertyName)
						.getReadMethod();
			} catch (IntrospectionException introExp) {
				Field fld = null;

				try {
					fld = object.getClass().getField(propertyName);
				} catch (NoSuchFieldException noSuchFieldExp) {
					throw new RuntimeException(
							"Can not find get method or field of "
									+ propertyName + " of " + object.getClass(),
							noSuchFieldExp);
				}

				return fld.get(object);
			}

			return method.invoke(object, (Object[]) null);
		} catch (InvocationTargetException exc) {
			throw new RuntimeException("Can not invoke " + propertyName
					+ " of " + object.getClass(), exc);
		} catch (IllegalAccessException exc) {
			throw new RuntimeException("Can not access " + propertyName
					+ " of " + object.getClass(), exc);
		}
	}

	/**
	 * Returns the equals the object o1 and object 2
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean equals(Object o1, Object o2) {
		if ((o1 == null) || (o2 == null)) {
			return o1 == o2;
		}

		if (o1.getClass() != o2.getClass()) {
			return false;
		}

		if ((o1 instanceof String) || (o1 instanceof Number)
				|| (o1 instanceof Boolean) || (o1 instanceof Character)
				|| (o1 instanceof Date)) {
			return o1.equals(o2);
		}

		try {
			Iterator fields = getAllFieldsOf(o1.getClass(), Object.class)
					.iterator();

			while (fields.hasNext()) {
				Object value1 = null;
				Field field = (Field) fields.next();

				if ("_objectClass".equals(field.getName())) {
					continue;
				}

				field.setAccessible(true);
				value1 = field.get(o1);

				if (!equals(value1, field.get(o2))) {
					return false;
				}
			}

			return true;
		} catch (IllegalAccessException iae) {
			throw new RuntimeException(
					"Error while accessing the fields of object for comparision"
							+ iae.getMessage());
		}
	}

	/**
	 * Returns the state of the object in the format - : [className,
	 * attribut1=[], attribut2=...]
	 */
	public static String dump(Object object) {
		return object.toString();
	}

	/**
	 * Returns the state of the object in the format - : [className,
	 * attribut1=[], attribut2=...] and adds to buffer
	 * 
	 * @param buffer
	 * @param object
	 * @return StringBuffer
	 */
	public static StringBuffer dump(StringBuffer buffer, Object object) {
		if (object == null) {
			return buffer;
		} else {
			return buffer.append(object.toString());
		}
	}

	/**
	 * It trims the fields prefix for "-"
	 * 
	 * @param s
	 * @return String
	 */
	public static String trimFieldPrefix(String s) {
		int index = s.indexOf('_');

		if (index < 0) {
			return s;
		} else {
			return s.substring(index + 1);
		}
	}

	/**
	 * Gets the class short name
	 * 
	 * @param class
	 *            c
	 * @return String
	 */
	public static String getClassShortName(Class c) {
		String className = null;
		int index = 0;

		if (c == null) {
			return "ClassShortNameNull";
		}

		className = c.getName();
		index = className.lastIndexOf(".");

		return className.substring(index + 1);
	}

	/**
	 * To get the proertyDescriptor
	 * 
	 * @param objectClass
	 * @param propertyName
	 * @return
	 */
	private static PropertyDescriptor getPropertyDescriptor(Class objectClass,
			String propertyName) throws IntrospectionException {
		PropertyDescriptor descriptor = null;

		try {
			descriptor = new PropertyDescriptor(propertyName, objectClass);
		} catch (IntrospectionException exc) {
			try {
				descriptor = new PropertyDescriptor(propertyName, objectClass,
						"is" + capitalize(propertyName), null);
			} catch (Exception getterExc) {
				descriptor = new PropertyDescriptor(propertyName, objectClass,
						"get" + capitalize(propertyName), null);
			}
		}

		return descriptor;
	}

	/**
	 * To capitalise the first letter of propertyname
	 * 
	 * @param s
	 * @return
	 */
	private static String capitalize(String s) {
		char[] chars = null;

		if (s.length() == 0) {
			return s;
		}

		chars = s.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);

		return new String(chars);
	}

	/**
	 * Returns the list of the attributes of the class realClass and super
	 * classes.
	 * 
	 * @param realClass
	 *            class of the object
	 * @param upperClass
	 *            super class
	 * 
	 * @return list of attributes
	 */
	private static List getAllFieldsOf(Class realClass, Class upperClass) {
		List result = (List) instanceFieldsCache.get(realClass);

		if (result == null) {
			result = recursiveGetAllFieldsOf(realClass, upperClass);
			instanceFieldsCache.put(realClass, result);
		}

		return result;
	}

	/**
	 * It recursively gets all fields of given class
	 * 
	 * @param realClass
	 * @param upperClass
	 * @return
	 */
	private static List recursiveGetAllFieldsOf(Class realClass,
			Class upperClass) {
		List result = null;
		Iterator fields = null;

		if (realClass == upperClass) {
			return new ArrayList(1);
		}

		result = recursiveGetAllFieldsOf(realClass.getSuperclass(), upperClass);
		result.addAll(Arrays.asList(realClass.getDeclaredFields()));
		fields = result.iterator();

		while (fields.hasNext()) {
			Field field = (Field) fields.next();

			if (Modifier.isStatic(field.getModifiers())) {
				fields.remove();
			}
		}

		return result;
	}
}
